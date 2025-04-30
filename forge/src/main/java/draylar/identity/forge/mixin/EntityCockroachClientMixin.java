package draylar.identity.forge.mixin;

import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import draylar.identity.forge.compat.accessor.ForceDanceAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityCockroach.class)
public class EntityCockroachClientMixin {

    @Inject(method = "handleStatus", at = @At("HEAD"))
    private void identity$handleDanceStatus(byte status, CallbackInfo ci) {
        if (status == 67) {
            EntityCockroach self = (EntityCockroach) (Object) this;

            if (self instanceof ForceDanceAccessor accessor) {
                accessor.identity$forceDance(200); // ✅ directly sets client copy
            }

            self.setNearbySongPlaying(self.getBlockPos(), true);
        }
    }

}

