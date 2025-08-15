package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "die", at = @At("HEAD"))
    private void identity$clearOnDeath(DamageSource source, CallbackInfo ci) {
        PlayerIdentity.updateIdentity((ServerPlayer) (Object) this, null, null);
    }

    @Inject(method = "restoreFrom", at = @At("RETURN"))
    private void identity$syncAfterRespawn(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        PlayerIdentity.sync((ServerPlayer) (Object) this);
    }
}

