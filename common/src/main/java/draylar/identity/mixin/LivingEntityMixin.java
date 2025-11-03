package draylar.identity.mixin;

import draylar.identity.api.IdentityGranting;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.SafeTagManager;
import draylar.identity.api.variant.IdentityType;
import draylar.identity.impl.NearbySongAccessor;
import draylar.identity.compat.LivingEntityCompatAccessor;
import draylar.identity.registry.IdentityEntityTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static draylar.identity.Identity.identity$isAquatic;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements NearbySongAccessor {

    @Shadow
    protected abstract int getNextAirOnLand(int air);

    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    protected LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "onDeath",
            at = @At("RETURN")
    )
    private void onDeath(DamageSource source, CallbackInfo ci) {
        Entity attacker = source.getAttacker();
        @Nullable IdentityType<?> thisType = IdentityType.from((LivingEntity) (Object) this);

        // check if attacker is a player to grant identity
        if (attacker instanceof PlayerEntity && thisType != null) {
            IdentityGranting.grantByAttack((PlayerEntity) attacker, thisType);
        }
    }

    @Inject(
            method = "getStepHeight",
            at = @At("RETURN"),
            cancellable = true)
    public void modifyStepHeight(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if (identity != null) {
               cir.setReturnValue(identity.getStepHeight());
            }
        }
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void identity$preventAirRegenForAquatic(CallbackInfo ci) {
        if ((Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);
            if (identity$isAquatic(identity) && !player.isSubmergedInWater()) {
                // Prevent air regen here if needed
                player.setAir(Math.min(player.getAir(), player.getMaxAir()));
            }
        }
    }

//    @Inject(method = "getStepHeight", at = @At("HEAD"), remap = false)
//    private void identity$modifyStepHeight(CallbackInfoReturnable<Float> cir) {
//        if ((Object) this instanceof PlayerEntity player) {
//            LivingEntity identity = PlayerIdentity.getIdentity(player);
//
//            if (identity != null) {
//                if (identity.getType().isIn(IdentityEntityTags.STEP_ASSIST) || SafeTagManager.isCustomStepAssist(identity.getType())) {
//                    this.stepHeight = 1.0F;
//                    return;
//                }
//            }
//        }
//
//        this.stepHeight = 0.6F;
//    }







    @Redirect(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry;)Z",
                    ordinal = 0
            )
    )
    private boolean slowFall(LivingEntity livingEntity, RegistryEntry<StatusEffect> effect) {
        if ((Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if (identity != null && !this.isSneaking()) {
                EntityType<?> type = identity.getType();
                if (type.isIn(IdentityEntityTags.SLOW_FALLING) || SafeTagManager.isCustomSlowFalling(type)) {
                    return true;
                }
            }
        }

        return this.hasStatusEffect(StatusEffects.SLOW_FALLING);
    }


//    @Unique
//    private boolean identity$isAquatic(LivingEntity identity) {
//        return identity != null && identity.getType().isIn(IdentityEntityTags.BREATHE_UNDERWATER);
//    }



    @Inject(method = "baseTick", at = @At("HEAD"))
    private void identity$suffocateAquaticIdentities(CallbackInfo ci) {
        if ((Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if (identity$isAquatic(identity)) {
                boolean inWater = player.isTouchingWater();
                boolean inBubbleColumn = player.getWorld().getBlockState(player.getBlockPos()).isOf(Blocks.BUBBLE_COLUMN);

                int air = player.getAir();

                if (!inWater && !inBubbleColumn) {
                    if (player.age % 40 == 0) { // reduce air every 10 ticks (0.5 sec)
                        if (air > 0) {
                            player.setAir(Math.max(air - 15, 0)); // drop by 15 to deplete in ~200 ticks
                        } else {
                            player.setAir(-1); // prevent re-damage spam
                            player.damage(player.getDamageSources().drown(), 2.0F);
                        }
                    }
                } else {
                    if (air < player.getMaxAir()) {
                        player.setAir(player.getMaxAir());
                    }
                }
            }
        }

    }
    @Redirect(
            method = "baseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getNextAirOnLand(I)I"
            )
    )
    private int identity$cancelAirRegenOnLand(LivingEntity instance, int air) {
        if ((Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if (identity$isAquatic(identity)
                    && !player.isTouchingWater()
                    && !player.getWorld().getBlockState(player.getBlockPos()).isOf(Blocks.BUBBLE_COLUMN)) {

                return air;
            }
        }


        return ((LivingEntityCompatAccessor) instance).identity$getNextAirOnLand(air);
    }


    @Inject(
            method = "handleFallDamage",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if (identity != null) {
                boolean takesFallDamage = identity.handleFallDamage(fallDistance, damageMultiplier, damageSource);
                int damageAmount = ((LivingEntityCompatAccessor) identity).callComputeFallDamage(fallDistance, damageMultiplier);

                if (takesFallDamage && damageAmount > 0) {
                    LivingEntity.FallSounds fallSounds = identity.getFallSounds();
                    this.playSound(damageAmount > 4 ? fallSounds.big() : fallSounds.small(), 1.0F, 1.0F);
                    ((LivingEntityCompatAccessor) identity).callPlayBlockFallSound();
                    this.damage(getDamageSources().fall(), (float) damageAmount);
                    cir.setReturnValue(true);
                } else {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(
            method = "hasStatusEffect",
            at = @At("HEAD"),
            cancellable = true
    )
    private void returnHasNightVision(RegistryEntry<StatusEffect> effect, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof PlayerEntity player) {
            if (effect.equals(StatusEffects.NIGHT_VISION)) {
                LivingEntity identity = PlayerIdentity.getIdentity(player);

                // Apply 'Night Vision' status effect to player if they are a Bat
                if (identity instanceof BatEntity) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(
            method = "getStatusEffect",
            at = @At("HEAD"),
            cancellable = true
    )
    private void returnNightVisionInstance(RegistryEntry<StatusEffect> effect, CallbackInfoReturnable<StatusEffectInstance> cir) {
        if ((Object) this instanceof PlayerEntity player) {
            if (effect.equals(StatusEffects.NIGHT_VISION)) {
                LivingEntity identity = PlayerIdentity.getIdentity(player);

                // Apply 'Night Vision' status effect to player if they are a Bat
                if (identity instanceof BatEntity) {
                    cir.setReturnValue(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 100000, 0, false, false));
                }
            }
        }
    }

    @Inject(method = "getBaseDimensions", at = @At("HEAD"), cancellable = true)
    private void identity_getBaseDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if ((Object)this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);
            if (identity != null) {
                // just delegate to disguise
                cir.setReturnValue(identity.getDimensions(pose));
            }
        }
    }


    @Inject(method = "hurtByWater", at = @At("HEAD"), cancellable = true)
    protected void identity_hurtByWater(CallbackInfoReturnable<Boolean> cir) {
        if((LivingEntity) (Object) this instanceof PlayerEntity player) {
            LivingEntity entity = PlayerIdentity.getIdentity(player);

            if (entity != null) {
                cir.setReturnValue(entity.hurtByWater());
            }
        }
    }

    @Inject(method = "canBreatheInWater", at = @At("HEAD"), cancellable = true)
    private void identity_canBreatheInWater(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if (identity != null) {
                if (identity$isAquatic(identity)) {
                    cir.setReturnValue(true);
                } else if (identity.getType().isIn(IdentityEntityTags.UNDROWNABLE) || SafeTagManager.isCustomUndrownable(identity.getType())) {
                    cir.setReturnValue(true);
                } else {
                    cir.setReturnValue(false);
                }
            }
        }
    }




    @Unique
    private boolean nearbySongPlaying = false;

    @Environment(EnvType.CLIENT)
    @Inject(method = "setNearbySongPlaying", at = @At("RETURN"))
    protected void identity_setNearbySongPlaying(BlockPos songPosition, boolean playing, CallbackInfo ci) {
        if((LivingEntity) (Object) this instanceof PlayerEntity player) {
            nearbySongPlaying = playing;
        }
    }

    @Override
    public boolean identity_isNearbySongPlaying() {
        return nearbySongPlaying;
    }

    @Inject(method = "canHaveStatusEffect", at = @At("HEAD"), cancellable = true)
    private void identity_canHaveStatusEffect(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if (identity != null) {
                cir.setReturnValue(identity.canHaveStatusEffect(effect));
            }
        }
    }



    @Inject(method = "canWalkOnFluid", at = @At("HEAD"), cancellable = true)
    protected void identity_canWalkOnFluid(FluidState state, CallbackInfoReturnable<Boolean> cir) {
        if((LivingEntity) (Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if (identity != null && identity.getType().isIn(IdentityEntityTags.LAVA_WALKING) && state.isIn(FluidTags.LAVA)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(
            method = "isClimbing",
            at = @At("HEAD"),
            cancellable = true
    )
    protected void identity_allowSpiderClimbing(CallbackInfoReturnable<Boolean> cir) {
        if((LivingEntity) (Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if (identity instanceof SpiderEntity) {
                cir.setReturnValue(this.horizontalCollision);
            }
        }
    }
}
