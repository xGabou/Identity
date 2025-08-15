package draylar.identity.impl;

import draylar.identity.api.variant.IdentityType;
import net.minecraft.world.entity.LivingEntity;

public interface PlayerDataProvider {
    LivingEntity getIdentity();
    IdentityType<?> getIdentityType();
    boolean updateIdentity(IdentityType<?> type, LivingEntity entity);
}

