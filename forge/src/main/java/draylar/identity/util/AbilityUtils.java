
package draylar.identity.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.util.List;
import java.util.Random;

public class AbilityUtils {

    public static EntityHitResult raycastEntities(PlayerEntity player, double maxDistance) {
        Vec3d eyePosition = player.getEyePos();
        Vec3d viewVector = player.getRotationVec(1.0F);
        Vec3d targetPosition = eyePosition.add(viewVector.multiply(maxDistance));

        HitResult blockHit = player.getWorld().raycast(new RaycastContext(eyePosition, targetPosition, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
        double blockDistance = blockHit.getType() == HitResult.Type.BLOCK ? eyePosition.distanceTo(blockHit.getPos()) : maxDistance;

        EntityHitResult closestEntityHit = null;
        double closestDistance = blockDistance;

        Box searchBox = player.getBoundingBox().stretch(viewVector.multiply(maxDistance)).expand(1.0D);
        List<Entity> entities = player.getWorld().getOtherEntities(player, searchBox, (entity) -> entity.isAttackable() && entity instanceof LivingEntity);

        for (Entity entity : entities) {
            Box entityBox = entity.getBoundingBox().expand(entity.getTargetingMargin());
            Vec3d intersection = entityBox.raycast(eyePosition, targetPosition).orElse(null);

            if (intersection != null) {
                double entityDistance = eyePosition.distanceTo(intersection);
                if (entityDistance < closestDistance) {
                    closestEntityHit = new EntityHitResult(entity, intersection);
                    closestDistance = entityDistance;
                }
            }
        }

        return closestEntityHit;
    }
    public static List<LivingEntity> raycastNearbyEntities(PlayerEntity player, double maxDistance) {
        Vec3d eyePosition = player.getEyePos();
        Vec3d viewVector = player.getRotationVec(1.0F);
        Box box = player.getBoundingBox().stretch(viewVector.multiply(maxDistance)).expand(1.5D);

        return player.getWorld().getEntitiesByClass(LivingEntity.class, box,
                entity -> entity != player && entity.isAttackable());
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

    public static void dashForward(PlayerEntity player, double distance) {
        Vec3d look = player.getRotationVec(1.0F);
        player.setVelocity(look.x * distance, player.getVelocity().y, look.z * distance);
        player.velocityModified = true;
    }


    public static void dropRandomItemFromInventory(PlayerEntity player) {
        if (!player.getInventory().isEmpty()) {
            Random random = new Random();
            int slot = random.nextInt(player.getInventory().size() - 1);
            if (!player.getInventory().getStack(slot).isEmpty()) {
                player.dropItem(player.getInventory().getStack(slot).getItem(), 1);
                player.getInventory().removeStack(slot, 1);
            }
        }
    }

    public static void healNearbyPlayers(PlayerEntity player, float radius, float healAmount) {
        World world = player.getWorld();
        for (LivingEntity livingEntity : world.getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(radius), entity -> entity instanceof PlayerEntity)) {
            livingEntity.heal(healAmount);
        }
    }
    public static void randomMorphNearby(PlayerEntity player) {
        // (Placeholder) would select a nearby entity and morph into it
        System.out.println("Morphing into a nearby entity...");
    }

    public static void constrictNearby(PlayerEntity player, float radius) {
        World world = player.getWorld();
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(radius), entity -> entity != player);

        for (LivingEntity living : entities) {
            living.setVelocity(Vec3d.ZERO);
            living.velocityModified = true;
            living.setNoDrag(true); // Optional: reduces inertia
        }
    }

    public static void dashUpward(PlayerEntity player, double power) {
        Vec3d current = player.getVelocity();
        player.setVelocity(current.x, power, current.z);
        player.velocityModified = true;
    }


    public static void waterDash(PlayerEntity player, double power) {
        if (player.isTouchingWater()) {
            Vec3d look = player.getRotationVec(1.0F);
            Vec3d current = player.getVelocity();
            Vec3d added = new Vec3d(look.x * power, look.y * 0.5D, look.z * power);

            player.setVelocity(current.add(added));
            player.velocityModified = true;
        }
    }


    public static void shortTeleportForward(PlayerEntity player, double distance) {
        Vec3d look = player.getRotationVec(1.0F);
        Vec3d target = player.getPos().add(look.multiply(distance));
        player.requestTeleport(target.x, target.y, target.z);
    }


    public static void pullEntityTowardPlayer(PlayerEntity player, LivingEntity target, double strength) {
        Vec3d direction = player.getPos().subtract(target.getPos()).normalize();
        target.setVelocity(direction.x * strength, 0.2D, direction.z * strength);
        target.velocityModified = true;
    }

    public static void poisonNearbyEnemies(PlayerEntity player, float radius, int durationTicks, int amplifier) {
        World world = player.getWorld();
        for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(radius), e -> e != player)) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, durationTicks, amplifier));
        }
    }
    /**
     * Finds the first JukeboxBlockEntity near the origin using a 3.46-block Euclidean radius,
     * matching the behavior used in Alex's Mobs.
     *
     * @param world  The world to search in
     * @param origin The central position (e.g., player or mob)
     * @return A nearby JukeboxBlockEntity or null if none are found
     */
    public static BlockPos findNearbyJukebox(World world, BlockPos origin) {
        double radius = 3.46;
        int blockRadius = (int) Math.ceil(radius); // search cube of size 4

        int originChunkX = origin.getX() >> 4;
        int originChunkZ = origin.getZ() >> 4;
        int chunkRadius = (blockRadius >> 4) + 1;

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                Chunk chunk = world.getChunk(originChunkX + dx, originChunkZ + dz);

                if (chunk instanceof WorldChunk worldChunk) {
                    for (BlockEntity be : worldChunk.getBlockEntities().values()) {
                        if (be instanceof JukeboxBlockEntity jukebox) {
                            if (jukebox.getPos().isWithinDistance(origin, radius)) {
                                return jukebox.getPos();
                            }
                        }
                    }
                }
            }
        }

        return null; // None found
    }

}
