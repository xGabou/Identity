package draylar.identity.mixin.player;

import draylar.identity.Identity;
import draylar.identity.api.FlightHelper;
import draylar.identity.api.IdentityTickHandler;
import draylar.identity.api.IdentityTickHandlers;
import draylar.identity.api.PlayerAbilities;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.impl.PlayerDataProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityTickMixin extends LivingEntity {

    private PlayerEntityTickMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @SuppressWarnings({"unchecked", "rawtypes", "ConstantConditions"})
    @Inject(method = "tick", at = @At("HEAD"))
    private void serverTick(CallbackInfo info) {
        // Tick IdentityTickHandlers on the client & server.
        @Nullable LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);
        if(identity != null) {
            @Nullable IdentityTickHandler handler = IdentityTickHandlers.getHandlers().get(identity.getType());
            if(handler != null) {
                handler.tick((PlayerEntity) (Object) this, identity);
            }

//            if(getWorld().isClient) {
//                identity.tick();
//            }
        }

        // Update misc. server-side entity properties for the player.
        if(!getWorld().isClient) {
            PlayerDataProvider data = (PlayerDataProvider) this;
            data.setRemainingHostilityTime(Math.max(0, data.getRemainingHostilityTime() - 1));

            // Update cooldown & Sync
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            PlayerAbilities.setCooldown(player, Math.max(0, data.getAbilityCooldown() - 1));
            PlayerAbilities.sync(player);

            // Sync flight abilities with identity state
            boolean shouldAllowFlight = Identity.hasFlyingPermissions(player);
            if (shouldAllowFlight != player.getAbilities().allowFlying) {
                if (shouldAllowFlight) {
                    FlightHelper.grantFlightTo(player);
                    player.getAbilities().setFlySpeed(IdentityConfig.getInstance().flySpeed());
                } else {
                    FlightHelper.revokeFlight(player);
                    player.getAbilities().setFlySpeed(0.05f);
                }
                player.sendAbilitiesUpdate();
            }

            // Validate villager profession bindings periodically
            draylar.identity.profession.ProfessionLifecycle.tickValidate(player, player.age);
        }
    }
}
