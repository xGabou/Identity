package draylar.identity.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class AbilityUtils {


    public static EntityHitResult raycastEntities(PlayerEntity player, double distance) {
        HitResult result = player.raycast(distance, 0.0F, false);
        return result instanceof EntityHitResult entityHitResult ? entityHitResult : null;
    }
    public static void knockbackNearbyEntities(PlayerEntity player, float radius, double strength) {
        World world = player.getWorld();
        for (LivingEntity livingEntity : world.getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(radius), entity -> entity != player)) {
            double dx = livingEntity.getX() - player.getX();
            double dz = livingEntity.getZ() - player.getZ();
            double distance = Math.max(Math.sqrt(dx * dx + dz * dz), 0.001D);
            livingEntity.addVelocity(dx / distance * strength, 0.1D, dz / distance * strength);
        }
    }
}
