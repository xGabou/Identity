package draylar.identity.mixin;

import com.mojang.authlib.GameProfile;
import draylar.identity.Identity;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.PlayerUnlocks;
import draylar.identity.api.FlightHelper;
import draylar.identity.api.platform.IdentityConfig;
import net.Gabou.gaboulibs.util.PlayerDebugUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Shadow public abstract boolean isCreative();
    @Shadow public abstract boolean isSpectator();
    @Shadow public abstract void sendMessage(Text message, boolean actionBar);

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(
            method = "onDeath",
            at = @At("HEAD")
    )
    private void revokeIdentityOnDeath(DamageSource source, CallbackInfo ci) {
        if(IdentityConfig.getInstance().revokeIdentityOnDeath() && !this.isCreative() && !this.isSpectator()) {
            LivingEntity entity = PlayerIdentity.getIdentity(this);

            // revoke the identity current equipped by the player
            if(entity != null) {
                EntityType<?> type = entity.getType();
                PlayerUnlocks.revoke((ServerPlayerEntity) (Object) this, PlayerIdentity.getIdentityType(this));
                PlayerIdentity.updateIdentity((ServerPlayerEntity) (Object) this, null,null);

                // todo: this option might be server-only given that this method isn't[?] called on the client
                // send revoke message to player if they aren't in creative and the config option is on
                if(IdentityConfig.getInstance().overlayIdentityRevokes()) {
                    sendMessage(
                            Text.translatable(
                                    "identity.revoke_entity",
                                    type.getTranslationKey()
                            ), true
                    );
                }
            }
        }
    }

    @Inject(
            method = "onSpawn()V",
            at = @At("HEAD")
    )
    private void onSpawn(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // Force dimension recalculation (important!)
        this.calculateDimensions();

        // Réactiver le vol si nécessaire
        if (Identity.hasFlyingPermissions(player)) {
            if (!FlightHelper.hasFlight(player)) {
                FlightHelper.grantFlightTo(player);
                getAbilities().setFlySpeed(IdentityConfig.getInstance().flySpeed());
                sendAbilitiesUpdate();
            }
        }
    }

    @Inject(method = "onDeath", at = @At("RETURN"))
    private void identity$handleDeath(DamageSource source, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            // Optional: store anything temporarily if needed
        }
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void identity$restoreAfterRespawn(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        PlayerIdentity.sync((ServerPlayerEntity) (Object) this);
    }
    @Inject(method = "changeGameMode", at  = @At("TAIL"))
    private void identity$onGameModeChange(GameMode newMode, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        if (Identity.hasFlyingPermissions(player)) {
            FlightHelper.grantFlightTo(player);
            getAbilities().setFlySpeed(IdentityConfig.getInstance().flySpeed());
        } else {
            if (!player.isCreative() && !player.isSpectator()) {
                FlightHelper.revokeFlight(player);
                getAbilities().setFlySpeed(0.05f);
            }
        }
        sendAbilitiesUpdate();
    }
//    @Inject(method = "tick", at = @At("TAIL"))
//    private void onServerTick(CallbackInfo ci) {
//        PlayerDebugUtils.logPlayerDebug((PlayerEntity)(Object)this, "server");
//    }

}
