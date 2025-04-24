package draylar.identity.compat;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;

public interface LivingEntityCompatAccessor {
    boolean isJumping();

    SoundEvent callGetHurtSound(DamageSource source);

    SoundEvent callGetDeathSound();

    void callPlayBlockFallSound();

    int callComputeFallDamage(float fallDistance, float damageMultiplier);

    float callGetSoundVolume();

    float callGetSoundPitch();

    void callSetLivingFlag(int mask, boolean value);

    float callGetEyeHeight(EntityPose pose, EntityDimensions dimensions);

    int identity$getNextAirOnLand(int air);

    float callGetActiveEyeHeight(EntityPose pose, EntityDimensions dimensions);

    void callTickActiveItemStack();
}
