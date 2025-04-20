package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.api.IdentityGranting;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.variant.IdentityType;
import draylar.identity.impl.NearbySongAccessor;
import draylar.identity.mixin.accessor.LivingEntityAccessor;
import draylar.identity.registry.IdentityEntityTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements NearbySongAccessor {

    @Shadow
    protected abstract int getNextAirOnLand(int air);

    @Shadow
    public abstract boolean hasStatusEffect(StatusEffect effect);

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







    @Redirect(
            method = "travel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z", ordinal = 0)
    )
    private boolean slowFall(LivingEntity livingEntity, StatusEffect effect) {
        if ((Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if (identity != null) {
                if (!this.isSneaking() && identity.getType().isIn(IdentityEntityTags.SLOW_FALLING)) {
                    return true;
                }
            }
        }

        return this.hasStatusEffect(StatusEffects.SLOW_FALLING);
    }
    @Unique
    private boolean identity$isAquatic(LivingEntity identity) {
        if (identity == null) return false;

        SpawnGroup group = identity.getType().getSpawnGroup();

        return switch (group) {
            case WATER_CREATURE, WATER_AMBIENT, UNDERGROUND_WATER_CREATURE -> true;
            default -> false;
        };
    }

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


        return ((LivingEntityAccessor) instance).identity$getNextAirOnLand(air);
    }
//    @Inject(method = "travel", at = @At("HEAD"))
//    private void identity$handleAquaticMovement(Vec3d movementInput, CallbackInfo ci) {
//        if ((Object) this instanceof PlayerEntity player) {
//            LivingEntity identity = PlayerIdentity.getIdentity(player);
//
//            if (identity$isAquatic(identity)) {
//                boolean inWater = player.isTouchingWater();
//                boolean inBubbleColumn = player.getWorld().getBlockState(player.getBlockPos()).isOf(Blocks.BUBBLE_COLUMN);
//
//                if (inWater || inBubbleColumn) {
//                    double speedMultiplier = identity.getType() == EntityType.DOLPHIN ? 0.35 : 0.2;
//
//                    Vec3d velocity = player.getVelocity();
//
//                    // Add directional input (WASD) with multiplier
//                    Vec3d horizontal = new Vec3d(movementInput.x, 0, movementInput.z);
//                    Vec3d newVelocity = horizontal.normalize().multiply(speedMultiplier);
//
//                    // Preserve vertical movement (space/sneak already handled by default)
//                    player.setVelocity(newVelocity.x, velocity.y, newVelocity.z);
//                }
//            }
//        }
//    }








//    @Inject(method = "travel", at = @At("HEAD"))
//    private void identity$handleAquaticMovement(Vec3d movementInput, CallbackInfo ci) {
//        if ((Object) this instanceof PlayerEntity player) {
//            LivingEntity identity = PlayerIdentity.getIdentity(player);
//
//            if (identity$isAquatic(identity)) {
//                boolean inWater = player.isTouchingWater();
//                boolean inBubbleColumn = player.getWorld().getBlockState(player.getBlockPos()).isOf(Blocks.BUBBLE_COLUMN);
//
//                if (inWater || inBubbleColumn) {
//                    double speedMultiplier = identity.getType() == EntityType.DOLPHIN ? 0.35 : 0.2;
//
//                    // Get vertical input (up/down)
//                    double y = 0.0;
//                    if (((LivingEntityAccessor) player).isJumping()) {
//                        y += speedMultiplier;
//                    } else if (player.isSneaking()) {
//                        y -= speedMultiplier;
//                    }
//
//                    // Horizontal movement from movement input
//                    Vec3d horizontalInput = new Vec3d(movementInput.x, 0, movementInput.z);
//                   if(horizontalInput.lengthSquared() > 0.0001)
//                   {
//                       // Apply speed to forward movement only
//                       Vec3d dir = player.getRotationVec(1.0F).normalize();
//                       Vec3d newVel = dir.multiply(speedMultiplier);
//
//                       // Preserve Y velocity (e.g., rising/falling in water)
//                       player.setVelocity(newVel.x, y, newVel.z);
//                   }
//
//
//                }
//            }
//        }
//    }
@Inject(method = "travel", at = @At("HEAD"))
private void identity$handleAquaticMovement(Vec3d movementInput, CallbackInfo ci) {
    if ((Object) this instanceof PlayerEntity player) {
        LivingEntity identity = PlayerIdentity.getIdentity(player);

        if (identity$isAquatic(identity)) {
            boolean inWater = player.isTouchingWater();
            boolean inBubbleColumn = player.getWorld().getBlockState(player.getBlockPos()).isOf(Blocks.BUBBLE_COLUMN);

            if (inWater || inBubbleColumn) {
                double speedMultiplier = identity.getType() == EntityType.DOLPHIN ? 0.35 : 0.2;

                // Get base 3D input (includes vertical movement key)
                Vec3d input = movementInput;

                // Add jump/sneak input to Y
                if (((LivingEntityAccessor) player).isJumping()) {
                    input = input.add(0, 1.0, 0);
                }
                if (player.isSneaking()) {
                    input = input.add(0, -1.0, 0);
                }

                // Apply full 3D rotation based on player's camera
                Vec3d look = player.getRotationVec(1.0F);
                Vec3d up = new Vec3d(0, 1, 0);
                Vec3d right = look.crossProduct(up).normalize();
                Vec3d adjustedUp = right.crossProduct(look).normalize(); // true up vector

                // Combine axes with 3D projection
                Vec3d worldInput =
                        right.multiply(input.x)
                                .add(adjustedUp.multiply(input.y))
                                .add(look.multiply(input.z));

                if (worldInput.lengthSquared() > 0.0001) {
                    player.setVelocity(worldInput.normalize().multiply(speedMultiplier));
                }
            }



        }
    }
}












//    @ModifyVariable(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z", ordinal = 1), ordinal = 0)
//    public LivingEntity applyWaterCreatureSwimSpeedBoost(LivingEntity value) {
//        if ((Object) this instanceof PlayerEntity player) {
//            LivingEntity identity = PlayerIdentity.getIdentity(player);
//
//            // Apply 'Dolphin's Grace' status effect benefits if the player's Identity is a water creature
//            if (identity instanceof WaterCreatureEntity) {
//                return .96f;
//            }
//        }
//
//        return value;
//    }

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
                int damageAmount = ((LivingEntityAccessor) identity).callComputeFallDamage(fallDistance, damageMultiplier);

                if (takesFallDamage && damageAmount > 0) {
                    LivingEntity.FallSounds fallSounds = identity.getFallSounds();
                    this.playSound(damageAmount > 4 ? fallSounds.big() : fallSounds.small(), 1.0F, 1.0F);
                    ((LivingEntityAccessor) identity).callPlayBlockFallSound();
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
    private void returnHasNightVision(StatusEffect effect, CallbackInfoReturnable<Boolean> cir) {
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
    private void returnNightVisionInstance(StatusEffect effect, CallbackInfoReturnable<StatusEffectInstance> cir) {
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

    @Inject(at = @At("HEAD"), method = "getEyeHeight", cancellable = true)
    public void getEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        if((LivingEntity) (Object) this instanceof PlayerEntity player) {

            // this is cursed
            try {
                LivingEntity identity = PlayerIdentity.getIdentity(player);

                if(identity != null) {
                    cir.setReturnValue(((LivingEntityAccessor) identity).callGetEyeHeight(pose, dimensions));
                }
            } catch (Exception ignored) {}
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
                } else if (identity.getType().isIn(IdentityEntityTags.UNDROWNABLE)) {
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

    @Inject(method = "isUndead", at = @At("HEAD"), cancellable = true)
    protected void identity_isUndead(CallbackInfoReturnable<Boolean> cir) {
        if((LivingEntity) (Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if (identity != null) {
                cir.setReturnValue(identity.isUndead());
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
