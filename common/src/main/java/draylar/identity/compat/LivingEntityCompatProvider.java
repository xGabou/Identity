// src/common/java/draylar/identity/compat/LivingEntityCompatProvider.java
package draylar.identity.compat;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

/**
 * Provides compatibility for LivingEntity accessor and invoker methods,
 * delegating to either the BjornLib mixin or the built-in Mixin accessor.
 */
public class LivingEntityCompatProvider {

//    private static AccessorImpl impl;
//
//    /**
//     * Sets the implementation (must be called from Forge initializer).
//     */
//    public static void set(AccessorImpl implementation) {
//        impl = implementation;
//    }
//
//    public static boolean isJumping(LivingEntity entity) {
//        return impl != null && impl.isJumping(entity);
//    }
//
//    public static float getSwimAmount(LivingEntity entity) {
//        return impl != null ? impl.getSwimAmount(entity) : 0f;
//    }
//
//    public static float getSwimAmountO(LivingEntity entity) {
//        return impl != null ? impl.getSwimAmountO(entity) : 0f;
//    }
//
//    public static void setSwimAmount(LivingEntity entity, float value) {
//        if (impl != null) impl.setSwimAmount(entity, value);
//    }
//
//    public static void setSwimAmountO(LivingEntity entity, float value) {
//        if (impl != null) impl.setSwimAmountO(entity, value);
//    }
//
//    public static float callGetActiveEyeHeight(LivingEntity entity, EntityPose pose, EntityDimensions dim) {
//        return impl.callGetActiveEyeHeight(entity, pose, dim);
//    }
//
//    public static void callTickActiveItemStack(LivingEntity entity) {
//        if (impl != null) impl.callTickActiveItemStack(entity);
//    }
//
//    public static SoundEvent callGetHurtSound(LivingEntity entity, DamageSource source) {
//        return impl != null
//                ? impl.callGetHurtSound(entity, source)
//                : SoundEvents.ENTITY_PLAYER_HURT;
//    }
//
//    public static SoundEvent callGetDeathSound(LivingEntity entity) {
//        return impl != null
//                ? impl.callGetDeathSound(entity)
//                : SoundEvents.ENTITY_PLAYER_DEATH;
//    }
//
//    public static void callPlayBlockFallSound(LivingEntity entity) {
//        if (impl != null) impl.callPlayBlockFallSound(entity);
//    }
//
//    public static int callComputeFallDamage(LivingEntity entity, float distance, float multiplier) {
//        return impl != null
//                ? impl.callComputeFallDamage(entity, distance, multiplier)
//                : entity.calculateFallDamage(distance, multiplier);
//    }
//
//    public static float callGetSoundVolume(LivingEntity entity) {
//        return impl != null ? impl.callGetSoundVolume(entity) : 1.0f;
//    }
//
//    public static float callGetSoundPitch(LivingEntity entity) {
//        return impl != null ? impl.callGetSoundPitch(entity) : entity.getVoicePitch();
//    }
//
//    public static void callSetLivingFlag(LivingEntity entity, int mask, boolean value) {
//        if (impl != null) impl.callSetLivingFlag(entity, mask, value);
//    }
//
//    public interface AccessorImpl {
//        boolean isJumping(LivingEntity entity);
//        float getSwimAmount(LivingEntity entity);
//        float getSwimAmountO(LivingEntity entity);
//        void setSwimAmount(LivingEntity entity, float value);
//        void setSwimAmountO(LivingEntity entity, float value);
//        float callGetActiveEyeHeight(LivingEntity entity, EntityPose pose, EntityDimensions dim);
//        void callTickActiveItemStack(LivingEntity entity);
//        SoundEvent callGetHurtSound(LivingEntity entity, DamageSource source);
//        SoundEvent callGetDeathSound(LivingEntity entity);
//        void callPlayBlockFallSound(LivingEntity entity);
//        int callComputeFallDamage(LivingEntity entity, float distance, float multiplier);
//        float callGetSoundVolume(LivingEntity entity);
//        float callGetSoundPitch(LivingEntity entity);
//        void callSetLivingFlag(LivingEntity entity, int mask, boolean value);
//    }
}