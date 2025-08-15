package draylar.identity.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Basic example mixin to demonstrate Forge 1.21.1 Mixin setup.
 */
@Mixin(Player.class)
public class PlayerEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void identity$onTick(CallbackInfo ci) {
        // Future identity behavior will hook here.
    }
}

