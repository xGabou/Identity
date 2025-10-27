package draylar.identity.profession;

import draylar.identity.api.PlayerIdentity;
import draylar.identity.impl.PlayerDataProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Map;

public final class ProfessionLifecycle {

    private ProfessionLifecycle() {}

    public static void tickValidate(ServerPlayerEntity player, int tickCount) {
        // run every 40 ticks to reduce load
        if ((tickCount % 40) != 0) return;

        Map<String, NbtCompound> map = ((PlayerDataProvider) player).getVillagerIdentities();
        Iterator<Map.Entry<String, NbtCompound>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, NbtCompound> e = it.next();
            NbtCompound tag = e.getValue();
            if (tag == null) {
                continue;
            }

            String prof = tag.getString("ProfessionId");
            if (prof == null || prof.isEmpty()) continue; // unemployed

            String dim = tag.getString("WorkstationDim");
            long posLong = tag.contains("WorkstationPos") ? tag.getLong("WorkstationPos") : Long.MIN_VALUE;
            if (dim == null || dim.isEmpty() || posLong == Long.MIN_VALUE) {
                removeAndNotify(player, it, e.getKey(), prof);
                continue;
            }

            ServerWorld world = player.getServer().getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier(dim)));
            if (world == null) {
                removeAndNotify(player, it, e.getKey(), prof);
                continue;
            }

            BlockPos pos = BlockPos.fromLong(posLong);
            if (world.isAir(pos)) {
                removeAndNotify(player, it, e.getKey(), prof);
                continue;
            }

            // Optional: verify POI still exists
            if (net.minecraft.world.poi.PointOfInterestTypes.getTypeForState(world.getBlockState(pos)).isEmpty()) {
                removeAndNotify(player, it, e.getKey(), prof);
            }
        }
    }

    private static void removeAndNotify(ServerPlayerEntity player, Iterator<Map.Entry<String, NbtCompound>> iterator, String key, String prof) {
        iterator.remove();
        PlayerDataProvider data = (PlayerDataProvider) player;
        if (key.equals(data.getActiveVillagerKey())) {
            data.setActiveVillagerKey(null);
        }
        player.sendMessage(Text.translatable("identity.profession.block_destroyed", key, Text.literal(prof)), false);
        PlayerIdentity.sync(player);
    }
}

