package draylar.identity.mixin;

import draylar.identity.api.variant.IdentityType;
import draylar.identity.impl.PlayerDataProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerEntityMixin implements PlayerDataProvider {
    private LivingEntity identity$current;
    private IdentityType<?> identity$currentType;

    @Override
    public LivingEntity getIdentity() {
        return identity$current;
    }

    @Override
    public IdentityType<?> getIdentityType() {
        return identity$currentType;
    }

    @Override
    public boolean updateIdentity(IdentityType<?> type, LivingEntity entity) {
        identity$current = entity;
        identity$currentType = type;
        return true;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void identity$onTick(CallbackInfo ci) {
        // Future identity behavior will hook here.
    }
}

