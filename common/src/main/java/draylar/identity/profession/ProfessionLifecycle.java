package draylar.identity.profession;

import draylar.identity.api.PlayerIdentity;
import draylar.identity.impl.PlayerDataProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

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
            if (tag == null) continue;

            String prof = tag.getString("ProfessionId");
            if (prof == null || prof.isEmpty()) continue; // unemployed

            String dim = tag.getString("WorkstationDim");
            long posLong = tag.contains("WorkstationPos") ? tag.getLong("WorkstationPos") : Long.MIN_VALUE;
            if (dim == null || dim.isEmpty() || posLong == Long.MIN_VALUE) {
                // Missing binding info; mark unemployed
                tag.remove("ProfessionId");
                tag.remove("WorkstationDim");
                tag.remove("WorkstationPos");
                player.sendMessage(Text.literal(prof + " lost due to block destruction"), false);
                continue;
            }

            ServerWorld world = player.getServer().getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier(dim)));
            if (world == null) {
                clearAndNotify(player, tag, prof);
                continue;
            }

            BlockPos pos = BlockPos.fromLong(posLong);
            if (world.isAir(pos)) {
                clearAndNotify(player, tag, prof);
                continue;
            }

            // Optional: verify POI still exists
            if (net.minecraft.world.poi.PointOfInterestTypes.getTypeForState(world.getBlockState(pos)).isEmpty()) {
                clearAndNotify(player, tag, prof);
            }
        }
    }

    private static void clearAndNotify(ServerPlayerEntity player, NbtCompound tag, String prof) {
        tag.remove("ProfessionId");
        tag.remove("WorkstationDim");
        tag.remove("WorkstationPos");
        player.sendMessage(Text.literal(prof + " lost due to block destruction"), false);
        // If currently morphed as this saved villager, force refresh on client
        PlayerIdentity.sync(player);
    }
}

