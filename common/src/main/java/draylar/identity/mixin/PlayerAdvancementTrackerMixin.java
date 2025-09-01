package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.api.FlightHelper;
import draylar.identity.api.platform.IdentityConfig;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {

    @Shadow private ServerPlayerEntity owner;

    // Run after the criterion is granted to avoid brittle INVOKE targets across versions
    @Inject(method = "grantCriterion", at = @At("RETURN"))
    private void refreshFlight(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if(Identity.hasFlyingPermissions(owner)) {
            FlightHelper.grantFlightTo(owner);
            owner.getAbilities().setFlySpeed(IdentityConfig.getInstance().flySpeed());
            owner.sendAbilitiesUpdate();
        }
    }
}
