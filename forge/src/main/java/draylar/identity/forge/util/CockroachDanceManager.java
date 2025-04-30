package draylar.identity.forge.util;

import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CockroachDanceManager {

    private static final Map<UUID, Integer> DANCING = new HashMap<>();

    public static void forceDance(PlayerEntity player, int ticks) {
        DANCING.put(player.getUuid(), ticks);
    }

    public static boolean isDancing(PlayerEntity player) {
        return DANCING.getOrDefault(player.getUuid(), 0) > 0;
    }

    public static void tick(PlayerEntity player, LivingEntity identity) {
        UUID uuid = player.getUuid();
        int remaining = DANCING.getOrDefault(uuid, 0);
        if(identity instanceof EntityCockroach cockroach) {

            if (remaining > 0) {
                DANCING.put(uuid, remaining - 1);
                cockroach.setDancing(true);
            } else {
                DANCING.remove(uuid);
                cockroach.setDancing(false);
            }
        }
    }
}
