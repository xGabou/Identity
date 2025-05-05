package draylar.identity.forge.mixin;

import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.forge.util.CockroachDanceManager;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    // on every server tick, advance or stop any cockroach‐dance for this player
    @Inject(method="tick", at=@At("HEAD"))
    private void identity$tickCockroachDance(CallbackInfo ci) {
        CockroachDanceManager.tick((ServerPlayerEntity)(Object)this);
    }
    @Inject(method = "onSpawn", at = @At("RETURN"))
    private void identity$onSpawn(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;

        if (PlayerIdentity.getIdentity(player) instanceof EntityCockroach cockroach) {
           cockroach.getWorld().sendEntityStatus(player,(byte) 68);
        }
    }
}
