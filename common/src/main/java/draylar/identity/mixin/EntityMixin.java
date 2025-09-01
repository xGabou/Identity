package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.SafeTagManager;
import draylar.identity.impl.DimensionsRefresher;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements DimensionsRefresher {

    @Shadow private EntityDimensions dimensions;

    @Shadow
    public abstract EntityPose getPose();

    @Shadow
    public abstract EntityDimensions getDimensions(EntityPose pose);

    @Shadow
    public abstract Box getBoundingBox();

    @Shadow
    public abstract void setBoundingBox(Box boundingBox);

    @Shadow protected boolean firstUpdate;
    @Shadow public World world;

    @Shadow
    public abstract void move(MovementType type, Vec3d movement);

    @Shadow private float standingEyeHeight;

    @Shadow
    protected abstract float getEyeHeight(EntityPose pose);

    @Inject(
            method = "getWidth",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getWidth(CallbackInfoReturnable<Float> cir) {
        if((Object) this instanceof PlayerEntity player) {
            LivingEntity Identity = PlayerIdentity.getIdentity(player);

            if(Identity != null) {
                cir.setReturnValue(Identity.getWidth());
            }
        }
    }

    @Inject(
            method = "getHeight",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getHeight(CallbackInfoReturnable<Float> cir) {
        if((Object) this instanceof PlayerEntity player) {
            LivingEntity Identity = PlayerIdentity.getIdentity(player);

            if(Identity != null) {
                cir.setReturnValue(Identity.getHeight());
            }
        }
    }

    @Override
    public void identity_refreshDimensions() {
        EntityDimensions currentDimensions = this.dimensions;
        EntityPose entityPose = this.getPose();
        EntityDimensions newDimensions = this.getDimensions(entityPose);

        this.dimensions = newDimensions;
        this.standingEyeHeight = this.getEyeHeight(entityPose);

        Box box = this.getBoundingBox();
        this.setBoundingBox(new Box(box.minX, box.minY, box.minZ, box.minX + (double) newDimensions.width(), box.minY + (double) newDimensions.height(), box.minZ + (double) newDimensions.width()));

        if(!this.firstUpdate) {
            float f = currentDimensions.width() - newDimensions.width();
            this.move(MovementType.SELF, new Vec3d(f, 0.0D, f));
        }
    }

    @Inject(at = @At("HEAD"), method = "getStandingEyeHeight", cancellable = true)
    public void getStandingEyeHeight(CallbackInfoReturnable<Float> cir) {
        if((Entity) (Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if(identity != null) {
                cir.setReturnValue(identity.getStandingEyeHeight());
            }
        }
    }

    @Inject(
            method = "isFireImmune",
            at = @At("HEAD"),
            cancellable = true
    )
    private void isFireImmune(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if (identity != null) {
                EntityType<?> type = identity.getType();
                boolean vanillaFireImmune = type.isFireImmune();
                boolean customFireImmune = SafeTagManager.isCustomFireImmune(type);

                if (vanillaFireImmune || customFireImmune) {
                    cir.setReturnValue(true);
                } else {
                    cir.setReturnValue(false);
                }
            }
        }
    }
    @Inject(method = "onStruckByLightning", at = @At("TAIL"))
    private void identity$onStruckByLightning(ServerWorld world, LightningEntity lightning, CallbackInfo ci) {
        if ((Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);
            if (identity instanceof CreeperEntity creeper) {
                creeper.onStruckByLightning(world, lightning);
            }
        }
    }


}
