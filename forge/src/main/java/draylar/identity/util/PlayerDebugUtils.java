package draylar.identity.util;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerDebugUtils {

    public static void logPlayerDebug(PlayerEntity player, String side) {
        var bb = player.getBoundingBox();
        var dim = player.getDimensions(player.getPose());

        System.out.println("[" + side.toUpperCase() + "] Pose: " + player.getPose());
        System.out.println("[" + side.toUpperCase() + "] Sneaking: " + player.isSneaking());
        System.out.println("[" + side.toUpperCase() + "] Dimensions: " + dim.width + " x " + dim.height);
        System.out.println("[" + side.toUpperCase() + "] BoundingBox: " + bb.minX + ", " + bb.minY + ", " + bb.minZ + " -> " + bb.maxX + ", " + bb.maxY + ", " + bb.maxZ);
        System.out.println("[" + side.toUpperCase() + "] Position: " + player.getX() + ", " + player.getY() + ", " + player.getZ());
        System.out.println("------------------------------------");
    }
}