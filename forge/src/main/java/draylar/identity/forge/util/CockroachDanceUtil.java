package draylar.identity.forge.util;

import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import draylar.identity.forge.compat.accessor.ForceDanceAccessor;
import draylar.identity.forge.mixin.EntityCockroachMixin;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.server.world.ServerWorld;

public class CockroachDanceUtil {

    public static void forceDance(EntityCockroach cockroach, int durationTicks) {
        ForceDanceAccessor accessor = (ForceDanceAccessor) cockroach;
        TrackedData<Integer> danceData = accessor.identity$getDanceTicksTracker();

        cockroach.getDataTracker().set(danceData, durationTicks);

        if (!cockroach.getWorld().isClient) {
            ((ServerWorld) cockroach.getWorld()).sendEntityStatus(cockroach, (byte) 67);
        }

        cockroach.setDancing(true);
    }

    public static void tickDance(EntityCockroach cockroach) {
        ForceDanceAccessor accessor = (ForceDanceAccessor) cockroach;
        TrackedData<Integer> danceData = accessor.identity$getDanceTicksTracker();

        int ticks = cockroach.getDataTracker().get(danceData);
        if (ticks > 0) {
            cockroach.getDataTracker().set(danceData, ticks - 1);
            cockroach.setDancing(true);
        } else {
            cockroach.setDancing(false);
        }
    }
}

