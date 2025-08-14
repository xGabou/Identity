package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// ClientLivingEntityMixin.java
@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public abstract class ClientLivingEntityMixin {
    @Unique
    private boolean identity$isAquatic(LivingEntity identity) {
        if (identity == null) return false;

        SpawnGroup group = identity.getType().getSpawnGroup();

        return switch (group) {
            case WATER_CREATURE, WATER_AMBIENT, UNDERGROUND_WATER_CREATURE -> true;
            default -> false;
        };
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void identity$handleAquaticMovement(Vec3d movementInput, CallbackInfo ci) {
        if ((Object) this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            if (identity != null && identity$isAquatic(identity)) {
                boolean inWater = player.isTouchingWater();
                boolean inBubbleColumn = player.getWorld().getBlockState(player.getBlockPos()).isOf(Blocks.BUBBLE_COLUMN);

                if (inWater || inBubbleColumn) {
                    double speedMultiplier = identity.getType() == EntityType.DOLPHIN ? 0.4 : 0.25;

                    Vec3d input = movementInput;

                    if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
                        input = input.add(0, -1.0, 0);
                    }
                    if (MinecraftClient.getInstance().options.sneakKey.isPressed()) {
                        input = input.add(0, 1.0, 0);
                    }

                    Vec3d look = player.getRotationVec(1.0F);
                    Vec3d up = new Vec3d(0, 1, 0);
                    Vec3d right = up.crossProduct(look).normalize();
                    Vec3d adjustedUp = right.crossProduct(look).normalize();
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
}

