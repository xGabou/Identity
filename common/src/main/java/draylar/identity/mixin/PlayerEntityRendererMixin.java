package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.model.ArmRenderingManipulator;
import draylar.identity.api.model.EntityArms;
import draylar.identity.api.model.EntityUpdater;
import draylar.identity.api.model.EntityUpdaters;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.mixin.accessor.EntityAccessor;
import draylar.identity.compat.LivingEntityCompatAccessor;
import draylar.identity.mixin.accessor.LivingEntityRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    @Shadow
    protected static BipedEntityModel.ArmPose getArmPose(AbstractClientPlayerEntity player, Hand hand) {
        return null;
    }

    private PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRenderInject(AbstractClientPlayerEntity player, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        LivingEntity identity = PlayerIdentity.getIdentity(player);

        if (identity != null) {
            // === SYNC player → identity ===
            LimbAnimatorAccessor target = (LimbAnimatorAccessor) identity.limbAnimator;
            LimbAnimatorAccessor source = (LimbAnimatorAccessor) player.limbAnimator;

            target.setPrevSpeed(source.getPrevSpeed());
            target.setSpeed(source.getSpeed());
            target.setPos(source.getPos());

            identity.handSwinging = player.handSwinging;
            identity.handSwingTicks = player.handSwingTicks;
            identity.lastHandSwingProgress = player.lastHandSwingProgress;
            identity.handSwingProgress = player.handSwingProgress;
            identity.bodyYaw = player.bodyYaw;
            identity.prevBodyYaw = player.prevBodyYaw;
            identity.headYaw = player.headYaw;
            identity.prevHeadYaw = player.prevHeadYaw;
            identity.age = player.age;
            identity.preferredHand = player.preferredHand;
            identity.setOnGround(player.isOnGround());
            identity.setVelocity(player.getVelocity());

            ((EntityAccessor) identity).setVehicle(player.getVehicle());
            ((EntityAccessor) identity).setTouchingWater(player.isTouchingWater());

            if (identity instanceof PhantomEntity) {
                identity.setPitch(-player.getPitch());
                identity.prevPitch = -player.prevPitch;
            } else {
                identity.setPitch(player.getPitch());
                identity.prevPitch = player.prevPitch;
            }

            if (IdentityConfig.getInstance().identitiesEquipItems()) {
                identity.equipStack(EquipmentSlot.MAINHAND, player.getEquippedStack(EquipmentSlot.MAINHAND));
                identity.equipStack(EquipmentSlot.OFFHAND, player.getEquippedStack(EquipmentSlot.OFFHAND));
            }

            if (IdentityConfig.getInstance().identitiesEquipArmor()) {
                identity.equipStack(EquipmentSlot.HEAD, player.getEquippedStack(EquipmentSlot.HEAD));
                identity.equipStack(EquipmentSlot.CHEST, player.getEquippedStack(EquipmentSlot.CHEST));
                identity.equipStack(EquipmentSlot.LEGS, player.getEquippedStack(EquipmentSlot.LEGS));
                identity.equipStack(EquipmentSlot.FEET, player.getEquippedStack(EquipmentSlot.FEET));
            }

            if (identity instanceof MobEntity) {
                ((MobEntity) identity).setAttacking(player.isUsingItem());
            }

            identity.setPose(player.getPose());
            identity.setCurrentHand(player.getActiveHand() == null ? Hand.MAIN_HAND : player.getActiveHand());
            ((LivingEntityCompatAccessor) identity).callSetLivingFlag(1, player.isUsingItem());
            identity.getItemUseTime();
            ((LivingEntityCompatAccessor) identity).callTickActiveItemStack();

            EntityUpdater updater = EntityUpdaters.getUpdater((EntityType<? extends LivingEntity>) identity.getType());
            if (updater != null) {
                updater.update(player, identity);
            }

            // === RENDER ===
            @SuppressWarnings("unchecked")
            EntityRenderer<? super LivingEntity> renderer =
                    (EntityRenderer<? super LivingEntity>) MinecraftClient.getInstance()
                            .getEntityRenderDispatcher().getRenderer(identity);


            if (renderer instanceof LivingEntityRenderer<?, ?> livingRenderer) {
                identity_setBipedIdentityModelPose(player, identity, livingRenderer);
            }


            renderer.render(identity, f, g, matrixStack, vertexConsumerProvider, light);



            if (IdentityConfig.getInstance().showPlayerNametag() && (player != MinecraftClient.getInstance().player || IdentityConfig.getInstance().shouldRenderOwnNameTag())) {
                renderLabelIfPresent(player, player.getDisplayName(), matrixStack, vertexConsumerProvider, light);
            }

            // ⛔ Prevent vanilla render from running
            ci.cancel();
        }
    }






    private void identity_setBipedIdentityModelPose(AbstractClientPlayerEntity player, LivingEntity identity, LivingEntityRenderer identityRenderer) {
        if (!(identityRenderer.getModel() instanceof BipedEntityModel<?> identityBipedModel)) {
            return; // Don't crash on non-humanoid models like CodModel
        }

        if (identity.isSpectator()) {
            identityBipedModel.setVisible(false);
            identityBipedModel.head.visible = true;
            identityBipedModel.hat.visible = true;
        } else {
            identityBipedModel.setVisible(true);
            identityBipedModel.hat.visible = player.isPartVisible(PlayerModelPart.HAT);
            identityBipedModel.sneaking = identity.isInSneakingPose();

            BipedEntityModel.ArmPose mainHandPose = getArmPose(player, Hand.MAIN_HAND);
            BipedEntityModel.ArmPose offHandPose = getArmPose(player, Hand.OFF_HAND);

            if (mainHandPose.isTwoHanded()) {
                offHandPose = identity.getOffHandStack().isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
            }

            if (identity.getMainArm() == Arm.RIGHT) {
                identityBipedModel.rightArmPose = mainHandPose;
                identityBipedModel.leftArmPose = offHandPose;
            } else {
                identityBipedModel.rightArmPose = offHandPose;
                identityBipedModel.leftArmPose = mainHandPose;
            }
        }
    }

    @Inject(
            method = "getPositionOffset",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyPositionOffset(AbstractClientPlayerEntity player, float f, CallbackInfoReturnable<Vec3d> cir) {
        LivingEntity identity = PlayerIdentity.getIdentity(player);

        if(identity != null) {
            if(identity instanceof TameableEntity) {
                cir.setReturnValue(super.getPositionOffset(player, f));
            }
        }
    }

    @Inject(
            method = "renderArm",
            at = @At("HEAD"), cancellable = true)
    private void onRenderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        LivingEntity identity = PlayerIdentity.getIdentity(player);

        // sync player data to identity identity
        if(identity != null) {
            EntityRenderer<?> renderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(identity);

            if(renderer instanceof LivingEntityRenderer) {
                LivingEntityRenderer<LivingEntity, ?> rendererCasted = (LivingEntityRenderer<LivingEntity, ?>) renderer;
                EntityModel model = ((LivingEntityRenderer) renderer).getModel();

                // re-assign arm & sleeve models
                arm = null;
                sleeve = null;

                if(model instanceof PlayerEntityModel) {
                    arm = ((PlayerEntityModel) model).rightArm;
                    sleeve = ((PlayerEntityModel) model).rightSleeve;
                } else if(model instanceof BipedEntityModel) {
                    arm = ((BipedEntityModel) model).rightArm;
                    sleeve = null;
                } else {
                    Pair<ModelPart, ArmRenderingManipulator<EntityModel>> pair = EntityArms.get(identity, model);
                    if(pair != null) {
                        arm = pair.getLeft();
                        pair.getRight().run(matrices, model);
                        matrices.translate(0, -.35, .5);
                    }
                }

                // assign model properties
                model.handSwingProgress = 0.0F;
//                model.sneaking = false;
//                model.leaningPitch = 0.0F;
                model.setAngles(identity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

                // render
                if(arm != null) {
                    arm.pitch = 0.0F;
                    arm.render(matrices, vertexConsumers.getBuffer(((LivingEntityRendererAccessor) rendererCasted).callGetRenderLayer(identity, true, false, true)), light, OverlayTexture.DEFAULT_UV);
                }

                if(sleeve != null) {
                    sleeve.pitch = 0.0F;
                    sleeve.render(matrices, vertexConsumers.getBuffer(((LivingEntityRendererAccessor) rendererCasted).callGetRenderLayer(identity, true, false, true)), light, OverlayTexture.DEFAULT_UV);
                }

                ci.cancel();
            }
        }
    }
}
