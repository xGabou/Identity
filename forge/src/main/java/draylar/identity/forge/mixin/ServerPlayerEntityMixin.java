package draylar.identity.forge.mixin;

import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.forge.util.CockroachDanceManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void identity$tickCockroachDance(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        LivingEntity identity = PlayerIdentity.getIdentity(player);

        if (identity instanceof EntityCockroach) {
            CockroachDanceManager.tick(player, identity);
        }
    }
}

