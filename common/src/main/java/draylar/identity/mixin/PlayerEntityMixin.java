package draylar.identity.mixin;

import draylar.identity.Identity;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.SafeTagManager;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.api.variant.IdentityType;
import draylar.identity.compat.LivingEntityCompatAccessor;
import draylar.identity.mixin.accessor.*;
import draylar.identity.registry.IdentityEntityTags;
import draylar.identity.util.AttributeSync;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {

    @Shadow public abstract boolean isSpectator();
    @Shadow public abstract EntityDimensions getDimensions(EntityPose pose);
    @Shadow public abstract boolean isSwimming();

    private PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void identity$loadForcedIdentity(CallbackInfo ci) {
        if((Object) this instanceof ServerPlayerEntity serverPlayerEntity) {
            @Nullable LivingEntity active = PlayerIdentity.getIdentity(serverPlayerEntity);
            if(active == null) {
                @Nullable String forced = IdentityConfig.getInstance().getForcedIdentity();
                if(forced != null) {
                    EntityType foundType = Registries.ENTITY_TYPE.get(new Identifier(forced));
                    if(foundType != null) {
                        PlayerIdentity.updateIdentity(serverPlayerEntity, new IdentityType<LivingEntity>(
                                foundType
                        ), (LivingEntity) foundType.create(getWorld()));
                    }
                }
            }
        }
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);

        if (identity != null) {
            // Récupère les dimensions dynamiques de l'identité, selon la pose actuelle du joueur
            cir.setReturnValue(identity.getDimensions(pose));
        }
    }

    /**
     * When a player turns into an Aquatic identity, they lose breath outside water.
     *
     * @param ci mixin callback info
     */
    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void tickAquaticBreathingOutsideWater(CallbackInfo ci) {
        LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);

        if(identity != null) {
            if(Identity.identity$isAquatic(identity)) {
                int air = this.getAir();

                // copy of WaterCreatureEntity#tickWaterBreathingAir
                if(this.isAlive() && !this.isInsideWaterOrBubbleColumn()) {
                    int i = EnchantmentHelper.getRespiration((LivingEntity) (Object) this);

                    // If the player has respiration, 50% chance to not consume air
                    if(i > 0) {
                        if(random.nextInt(i + 1) <= 0) {
                            this.setAir(air - 1);
                        }
                    }

                    // No respiration, decrease air as normal
                    else {
                        this.setAir(air - 1);
                    }

                    // Air has ran out, start drowning
                    if(this.getAir() == -20) {
                        this.setAir(0);
                        this.damage(getDamageSources().drown(), 2.0F);
                    }
                } else {
                    this.setAir(300);
                }
            }
        }
    }

    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    private void identity_getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;

        // cursed
        try {
            LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);

            if(identity != null) {
                cir.setReturnValue(((LivingEntityCompatAccessor) identity).callGetActiveEyeHeight(getPose(), getDimensions(getPose())));
            }
        } catch (Exception ignored) {

        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getEyeHeight(EntityPose pose) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);

        if(identity != null) {
            return identity.getEyeHeight(pose);
        } else {
            return this.getEyeHeight(pose, this.getDimensions(pose));
        }
    }

    @Inject(
            method = "getHurtSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);

        if(IdentityConfig.getInstance().useIdentitySounds() && identity != null) {
            cir.setReturnValue(((LivingEntityCompatAccessor) identity).callGetHurtSound(source));
        }
    }


    // todo: separate mixin for ambient sounds
    private int identity_ambientSoundChance = 0;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void tickAmbientSounds(CallbackInfo ci) {
        LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);

        if(!getWorld().isClient && IdentityConfig.getInstance().playAmbientSounds() && identity instanceof MobEntity) {
            MobEntity mobIdentity = (MobEntity) identity;

            if(this.isAlive() && this.random.nextInt(1000) < this.identity_ambientSoundChance++) {
                // reset sound delay
                this.identity_ambientSoundChance = -mobIdentity.getMinAmbientSoundDelay();

                // play ambient sound
                SoundEvent sound = ((MobEntityAccessor) mobIdentity).callGetAmbientSound();
                if(sound != null) {
                    float volume = ((LivingEntityCompatAccessor) mobIdentity).callGetSoundVolume();
                    float pitch = ((LivingEntityCompatAccessor) mobIdentity).callGetSoundPitch();

                    // By default, players can not hear their own ambient noises.
                    // This is because ambient noises can be very annoying.
                    if(IdentityConfig.getInstance().hearSelfAmbient()) {
                        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch);
                    } else {
                        this.getWorld().playSound((PlayerEntity) (Object) this, this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch);
                    }
                }
            }
        }
    }

    @Inject(
            method = "getDeathSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getDeathSound(CallbackInfoReturnable<SoundEvent> cir) {
        LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);

        if(IdentityConfig.getInstance().useIdentitySounds() && identity != null) {
            cir.setReturnValue(((LivingEntityCompatAccessor) identity).callGetDeathSound());
        }
    }

    @Inject(
            method = "getFallSounds",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getFallSounds(CallbackInfoReturnable<LivingEntity.FallSounds> cir) {
        LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);

        if(IdentityConfig.getInstance().useIdentitySounds() && identity != null) {
            cir.setReturnValue(identity.getFallSounds());
        }
    }

    @Inject(method = "attack", at = @At("HEAD"))
    protected void identity_tryAttack(Entity target, CallbackInfo ci) {
        LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);

        if(identity instanceof IronGolemEntity golem) {
            ((IronGolemEntityAccessor) golem).setAttackTicksLeft(10);
        }

        if(identity instanceof WardenEntity warden) {
            warden.attackingAnimationState.start(age);
        }

        if(identity instanceof RavagerEntity ravager) {
            ((RavagerEntityAccessor) ravager).setAttackTick(10);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickGolemAttackTicks(CallbackInfo ci) {
        LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);

        if(identity instanceof IronGolemEntity golem) {
            IronGolemEntityAccessor accessor = (IronGolemEntityAccessor) golem;
            if(accessor.getAttackTicksLeft() > 0) {
                accessor.setAttackTicksLeft(accessor.getAttackTicksLeft() - 1);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickRavagerAttackTicks(CallbackInfo ci) {
        LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);

        if(identity instanceof RavagerEntity ravager) {
            RavagerEntityAccessor accessor = (RavagerEntityAccessor) ravager;
            if(accessor.getAttackTick() > 0) {
                accessor.setAttackTick(accessor.getAttackTick() - 1);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickWardenSneakingAnimation(CallbackInfo ci) {
        LivingEntity identity = PlayerIdentity.getIdentity((PlayerEntity) (Object) this);

        if(identity instanceof WardenEntity warden) {
            if(isSneaking()) {
                if(!warden.sniffingAnimationState.isRunning()) {
                    warden.sniffingAnimationState.start(age);
                }
            } else {
                warden.sniffingAnimationState.stop();
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickFire(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        LivingEntity identity = PlayerIdentity.getIdentity(player);

        if (!player.getWorld().isClient && !player.isCreative() && !player.isSpectator()) {
            if (identity != null) {
                EntityType<?> type = identity.getType();

                if (type.isIn(IdentityEntityTags.BURNS_IN_DAYLIGHT) || SafeTagManager.isCustomBurnsInDaylight(type)) {
                    boolean bl = this.isInDaylight();

                    if (bl) {
                        if (player.getWorld().isRaining()) return;

                        ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
                        if (!helmet.isEmpty() && helmet.isDamageable()) {
                            helmet.setDamage(helmet.getDamage() + player.getRandom().nextInt(2));
                            if (helmet.getDamage() >= helmet.getMaxDamage()) {
                                player.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                                player.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                            }

                            bl = false;
                        }

                        if (bl) {
                            player.setOnFireFor(8);
                        }
                    }
                }
            }
        }
    }


    @Unique
    private boolean isInDaylight() {
        if(getWorld().isDay() && !getWorld().isClient) {
            float brightnessAtEyes = getBrightnessAtEyes();
            BlockPos daylightTestPosition = new BlockPos((int) getX(), (int) Math.round(getY()), (int) getZ());

            // move test position up one block for boats
            if(getVehicle() instanceof BoatEntity) {
                daylightTestPosition = daylightTestPosition.up();
            }

            return brightnessAtEyes > 0.5F && random.nextFloat() * 30.0F < (brightnessAtEyes - 0.4F) * 2.0F && getWorld().isSkyVisible(daylightTestPosition);
        }

        return false;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickTemperature(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        LivingEntity identity = PlayerIdentity.getIdentity(player);

        if(!player.isCreative() && !player.isSpectator()) {
            // check if the player is identity
            if(identity != null) {
                EntityType<?> type = identity.getType();

                // damage player if they are an identity that gets hurt by high temps (eg. snow golem in nether)
                if(type.isIn(IdentityEntityTags.HURT_BY_HIGH_TEMPERATURE) || SafeTagManager.isCustomHurtByHeat(type)) {
                    Biome biome = getWorld().getBiome(getBlockPos()).value();
                    if (!biome.isCold(getBlockPos())) {
                        player.damage(getDamageSources().onFire(), 1.0f);
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickIdentity(CallbackInfo ci) {
        if(!getWorld().isClient) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            // assign basic data to entity from player on server; most data transferring occurs on client
            if(identity != null) {
                identity.setPos(player.getX(), player.getY(), player.getZ());
                identity.setHeadYaw(player.getHeadYaw());
                identity.setJumping(((LivingEntityCompatAccessor) player).isJumping());
                identity.setSprinting(player.isSprinting());
                identity.setStuckArrowCount(player.getStuckArrowCount());
                identity.setInvulnerable(true);
                identity.setNoGravity(true);
                identity.setSneaking(player.isSneaking());
                identity.setSwimming(player.isSwimming());
                identity.setCurrentHand(player.getActiveHand());
                identity.setPose(player.getPose());

                if(identity instanceof TameableEntity) {
                    ((TameableEntity) identity).setInSittingPose(player.isSneaking());
                    ((TameableEntity) identity).setSitting(player.isSneaking());
                }

                ((EntityAccessor) identity).identity_callSetFlag(7, player.isFallFlying());

                ((LivingEntityCompatAccessor) identity).callTickActiveItemStack();
                PlayerIdentity.sync((ServerPlayerEntity) player); // safe cast - context is server world
                AttributeSync.syncMaxHealth((ServerPlayerEntity) player);

            }
        }
    }
}
