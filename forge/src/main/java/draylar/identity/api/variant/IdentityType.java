package draylar.identity.api.variant;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public record IdentityType<T extends LivingEntity>(EntityType<T> type) {
}

