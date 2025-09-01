package draylar.identity.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow
    public abstract M getModel();

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(LivingEntity entity, float f, float g, MatrixStack matrixStack,
                          VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if ((Object) this instanceof BipedEntityRenderer<?, ?> bipedRenderer) {
            EntityModel<?> model = bipedRenderer.getModel();
            if (model instanceof BipedEntityModel<?> bipedModel) {
                bipedModel.sneaking = entity.isInSneakingPose();
            }
        }
    }
}
