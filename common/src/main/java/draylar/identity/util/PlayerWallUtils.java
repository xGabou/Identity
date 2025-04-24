package draylar.identity.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class PlayerWallUtils {

    public static boolean isFacingWall(PlayerEntity player, double maxDistance) {
        Vec3d direction = player.getRotationVec(1.0F).normalize();
        Vec3d eyePos = player.getEyePos();
        Vec3d target = eyePos.add(direction.multiply(maxDistance));

        BlockHitResult hit = player.getWorld().raycast(new RaycastContext(
                eyePos,
                target,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        ));

        return hit.getType() == HitResult.Type.BLOCK;
    }

    public static void gentlyNudgeTowardWall(PlayerEntity player) {
        Vec3d push = player.getRotationVec(1.0F).normalize().multiply(0.02);
        player.setVelocity(player.getVelocity().add(push));
        player.velocityModified = true;
    }
}

