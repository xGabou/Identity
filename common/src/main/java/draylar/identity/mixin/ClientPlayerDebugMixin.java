package draylar.identity.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerDebugMixin {

//    @Inject(method = "tick", at = @At("TAIL"))
//    private void onClientTick(CallbackInfo ci) {
//        PlayerDebugUtils.logPlayerDebug((PlayerEntity)(Object)this, "client");
//    }
}