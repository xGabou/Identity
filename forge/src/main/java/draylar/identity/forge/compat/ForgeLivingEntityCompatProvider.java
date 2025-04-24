// forge/src/main/java/draylar/identity/forge/compat/ForgeLivingEntityCompatProvider.java
package draylar.identity.forge.compat;

import draylar.identity.compat.LivingEntityCompatProvider;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraftforge.fml.ModList;

public class ForgeLivingEntityCompatProvider {

//    public static void init() {
//        if (ModList.get().isLoaded("bjornlib")) {
//            LivingEntityCompatProvider.set(new BjornImpl());
//        } else {
//            LivingEntityCompatProvider.set(new LegacyImpl());
//        }
//    }
//
//    private static class BjornImpl implements LivingEntityCompatProvider.AccessorImpl {
//        private final Class<?> bjornAccessor = com.furiusmax.bjornlib.forge.mixin.LivingEntityAccessor.class;
//
//        @Override
//        public boolean isJumping(LivingEntity entity) {
//            try {
//                return (boolean) bjornAccessor.getMethod("isJumping").invoke(entity);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
//
//        @Override
//        public float getSwimAmount(LivingEntity entity) {
//            try {
//                return (float) bjornAccessor.getMethod("getSwimAmount").invoke(entity);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return 0f;
//            }
//        }
//
//        @Override
//        public float getSwimAmountO(LivingEntity entity) {
//            try {
//                return (float) bjornAccessor.getMethod("getSwimAmountO").invoke(entity);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return 0f;
//            }
//        }
//
//        @Override
//        public void setSwimAmount(LivingEntity entity, float value) {
//            try {
//                bjornAccessor.getMethod("setSwimAmount", float.class).invoke(entity, value);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void setSwimAmountO(LivingEntity entity, float value) {
//            try {
//                bjornAccessor.getMethod("setSwimAmountO", float.class).invoke(entity, value);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public float callGetActiveEyeHeight(LivingEntity entity, EntityPose pose, EntityDimensions dim) {
//            try {
//                return (float) bjornAccessor
//                        .getMethod("callGetActiveEyeHeight", EntityPose.class, EntityDimensions.class)
//                        .invoke(entity, pose, dim);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return entity.getActiveEyeHeight(pose, dim);
//            }
//        }
//
//        @Override
//        public void callTickActiveItemStack(LivingEntity entity) {
//            try {
//                bjornAccessor.getMethod("callUpdatingUsingItem").invoke(entity);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public SoundEvent callGetHurtSound(LivingEntity entity, DamageSource source) {
//            try {
//                return (SoundEvent) bjornAccessor
//                        .getMethod("callGetHurtSound", DamageSource.class)
//                        .invoke(entity, source);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return SoundEvent.EMPTY;
//            }
//        }
//
//        @Override
//        public SoundEvent callGetDeathSound(LivingEntity entity) {
//            try {
//                return (SoundEvent) bjornAccessor.getMethod("callGetDeathSound").invoke(entity);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return SoundEvents.INTENTIONALLY_EMPTY;
//            }
//        }
//
//        @Override
//        public void callPlayBlockFallSound(LivingEntity entity) {
//            try {
//                bjornAccessor.getMethod("callPlayBlockFallSound").invoke(entity);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public int callComputeFallDamage(LivingEntity entity, float distance, float multiplier) {
//            try {
//                return (int) bjornAccessor
//                        .getMethod("callCalculateFallDamage", float.class, float.class)
//                        .invoke(entity, distance, multiplier);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return entity.calculateFallDamage(distance, multiplier);
//            }
//        }
//
//        @Override
//        public float callGetSoundVolume(LivingEntity entity) {
//            try {
//                return (float) bjornAccessor.getMethod("callGetSoundVolume").invoke(entity);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return 1.0f;
//            }
//        }
//
//        @Override
//        public float callGetSoundPitch(LivingEntity entity) {
//            try {
//                return (float) bjornAccessor.getMethod("callGetVoicePitch").invoke(entity);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return entity.getVoicePitch();
//            }
//        }
//
//        @Override
//        public void callSetLivingFlag(LivingEntity entity, int mask, boolean value) {
//            try {
//                bjornAccessor
//                        .getMethod("callSetLivingEntityFlag", int.class, boolean.class)
//                        .invoke(entity, mask, value);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private static class LegacyImpl implements LivingEntityCompatProvider.AccessorImpl {
//        @Override
//        public boolean isJumping(LivingEntity entity) {
//            return entity instanceof draylar.identity.mixin.accessor.LivingEntityAccessor accessor
//                    && accessor.isJumping();
//        }
//
//        @Override
//        public float getSwimAmount(LivingEntity entity) {
//            return ((draylar.identity.mixin.accessor.LivingEntityAccessor)entity).getSwimAmount();
//        }
//
//        @Override
//        public float getSwimAmountO(LivingEntity entity) {
//            return ((draylar.identity.mixin.accessor.LivingEntityAccessor)entity).getSwimAmountO();
//        }
//
//        @Override
//        public void setSwimAmount(LivingEntity entity, float value) {
//            ((draylar.identity.mixin.accessor.LivingEntityAccessor)entity).setSwimAmount(value);
//        }
//
//        @Override
//        public void setSwimAmountO(LivingEntity entity, float value) {
//            ((draylar.identity.mixin.accessor.LivingEntityAccessor)entity).setSwimAmountO(value);
//        }
//
//        @Override
//        public float callGetActiveEyeHeight(LivingEntity entity, EntityPose pose, EntityDimensions dim) {
//            return ((draylar.identity.mixin.accessor.LivingEntityAccessor)entity)
//                    .callGetActiveEyeHeight(pose, dim);
//        }
//
//        @Override
//        public void callTickActiveItemStack(LivingEntity entity) {
//            ((draylar.identity.mixin.accessor.LivingEntityAccessor)entity).callTickActiveItemStack();
//        }
//
//        @Override
//        public SoundEvent callGetHurtSound(LivingEntity entity, DamageSource source) {
//            return ((draylar.identity.mixin.accessor.LivingEntityAccessor)entity)
//                    .callGetHurtSound(source);
//        }
//
//        @Override
//        public SoundEvent callGetDeathSound(LivingEntity entity) {
//            return ((draylar.identity.mixin.accessor.LivingEntityAccessor)entity).callGetDeathSound();
//        }
//
//        @Override
//        public void callPlayBlockFallSound(LivingEntity entity) {
//            ((draylar.identity.mixin.accessor.LivingEntityAccessor)entity).callPlayBlockFallSound();
//        }
//
//        @Override
//        public int callComputeFallDamage(LivingEntity entity, float distance, float multiplier) {
//            return ((draylar.identity.mixin.accessor.LivingEntityAccessor)entity)
//                    .callComputeFallDamage(distance, multiplier);
//        }
//
//        @Override
//        public float callGetSoundVolume(LivingEntity entity) {
//            return ((draylar.identity.mixin.accessor.LivingEntityAccessor)entity)
//                    .callGetSoundVolume();
//        }
//
//        @Override
//        public float callGetSoundPitch(LivingEntity entity) {
//            return ((draylar.identity.mixin.accessor.LivingEntityAccessor)entity)
//                    .callGetSoundPitch();
//        }
//
//        @Override
//        public void callSetLivingFlag(LivingEntity entity, int mask, boolean value) {
//            ((draylar.identity.mixin.accessor.LivingEntityAccessor)entity)
//                    .callSetLivingFlag(mask, value);
//        }
//    }
}
