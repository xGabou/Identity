package draylar.identity.network.impl;

import dev.architectury.networking.NetworkManager;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.impl.PlayerDataProvider;
import draylar.identity.network.ClientNetworking;
import draylar.identity.network.NetworkHandler;
import draylar.identity.screen.VillagerProfessionScreen;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;

public class VillagerProfessionPackets {

    public static void openScreen(ServerPlayerEntity player, Identifier professionId, net.minecraft.util.math.BlockPos pos, Identifier worldId, String existingName, String existingProfessionId) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeIdentifier(professionId);
        packet.writeBlockPos(pos);
        packet.writeIdentifier(worldId);
        packet.writeBoolean(existingName != null);
        if (existingName != null) {
            packet.writeString(existingName);
            packet.writeString(existingProfessionId == null ? "" : existingProfessionId);
        }
        NetworkManager.sendToPlayer(player, NetworkHandler.OPEN_PROFESSION_SCREEN, packet);
    }

    public static void sendSetProfession(Identifier professionId, String name, boolean reset, net.minecraft.util.math.BlockPos pos, Identifier worldId, String originalName) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeIdentifier(professionId);
        packet.writeString(name);
        packet.writeBoolean(reset);
        packet.writeBlockPos(pos);
        packet.writeIdentifier(worldId);
        packet.writeBoolean(originalName != null);
        if (originalName != null) {
            packet.writeString(originalName);
        }
        NetworkManager.sendToServer(NetworkHandler.SET_PROFESSION, packet);
    }

    public static void registerServerHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkHandler.SET_PROFESSION, (buf, context) -> {
            Identifier professionId = buf.readIdentifier();
            String name = buf.readString();
            boolean reset = buf.readBoolean();
            net.minecraft.util.math.BlockPos pos = buf.readBlockPos();
            Identifier worldId = buf.readIdentifier();
            boolean hasOriginal = buf.readBoolean();
            String originalName = hasOriginal ? buf.readString() : null;
            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();

            context.getPlayer().getServer().execute(() -> {
                handleServerRequest(player, professionId, name, reset, pos, worldId, originalName);
            });
        });
    }

    public static void registerClientHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.OPEN_PROFESSION_SCREEN, (buf, context) -> {
            Identifier professionId = buf.readIdentifier();
            net.minecraft.util.math.BlockPos pos = buf.readBlockPos();
            Identifier worldId = buf.readIdentifier();
            String existingName = null;
            String existingProfession = null;
            if (buf.readBoolean()) {
                existingName = buf.readString();
                String prof = buf.readString();
                existingProfession = prof.isEmpty() ? null : prof;
            }
            String finalExistingName = existingName;
            String finalExistingProfession = existingProfession;
            ClientNetworking.runOrQueue(context, player -> MinecraftClient.getInstance().setScreen(new VillagerProfessionScreen(professionId, pos, worldId, finalExistingName, finalExistingProfession)));
        });
    }

    private static void handleServerRequest(ServerPlayerEntity player, Identifier professionId, String rawName, boolean reset, net.minecraft.util.math.BlockPos pos, Identifier worldId, String originalName) {
        PlayerDataProvider data = (PlayerDataProvider) player;
        java.util.Map<String, NbtCompound> map = data.getVillagerIdentities();
        long workstationPos = pos.asLong();
        String trimmedName = rawName.trim();

        String existingKey = null;
        if (originalName != null && map.containsKey(originalName) && matchesWorkstation(map.get(originalName), worldId, workstationPos)) {
            existingKey = originalName;
        }
        if (existingKey == null) {
            for (java.util.Map.Entry<String, NbtCompound> entry : map.entrySet()) {
                if (matchesWorkstation(entry.getValue(), worldId, workstationPos)) {
                    existingKey = entry.getKey();
                    break;
                }
            }
        }

        if (reset) {
            if (existingKey != null) {
                data.removeVillagerIdentity(existingKey);
                player.sendMessage(Text.translatable("identity.profession.removed", existingKey), false);
                PlayerIdentity.sync(player);
            } else {
                player.sendMessage(Text.translatable("identity.profession.none"), false);
            }
            return;
        }

        if (trimmedName.isEmpty()) {
            player.sendMessage(Text.translatable("identity.profession.require_name"), false);
            return;
        }

        if (map.containsKey(trimmedName) && (existingKey == null || !existingKey.equals(trimmedName))) {
            player.sendMessage(Text.translatable("identity.profession.name_conflict", trimmedName), false);
            return;
        }

        net.minecraft.server.world.ServerWorld world = player.getServer().getWorld(net.minecraft.registry.RegistryKey.of(net.minecraft.registry.RegistryKeys.WORLD, worldId));
        if (world == null || !world.getRegistryKey().equals(player.getWorld().getRegistryKey())) {
            player.sendMessage(Text.translatable("identity.profession.invalid_world"), false);
            return;
        }

        if (net.minecraft.world.poi.PointOfInterestTypes.getTypeForState(world.getBlockState(pos)).isEmpty()) {
            player.sendMessage(Text.translatable("identity.profession.invalid_workstation"), false);
            return;
        }

        if (!(PlayerIdentity.getIdentity(player) instanceof VillagerEntity villager)) {
            player.sendMessage(Text.translatable("identity.profession.missing_identity"), false);
            return;
        }

        NbtCompound tag = new NbtCompound();
        VillagerProfession profession =
                Registries.VILLAGER_PROFESSION.getOrEmpty(professionId)
                        .orElse(VillagerProfession.NONE);

        villager.setVillagerData(new VillagerData(villager.getVillagerData().getType(), profession,villager.getVillagerData().getLevel()));
        villager.writeNbt(tag);
        tag.putString("ProfessionId", professionId.toString());
        tag.putString("WorkstationDim", worldId.toString());
        tag.putLong("WorkstationPos", workstationPos);
        tag.putString("IdentityName", trimmedName);


        data.setVillagerIdentity(trimmedName, tag);
        if (existingKey != null && !existingKey.equals(trimmedName)) {
            data.removeVillagerIdentity(existingKey);
        }

        String activeKey = data.getActiveVillagerKey();
        if (existingKey != null && existingKey.equals(activeKey)) {
            data.setActiveVillagerKey(trimmedName);
        } else if (existingKey == null) {
            data.setActiveVillagerKey(trimmedName);
        }
        Text professionText = Text.literal(professionId.toString());
        player.sendMessage(Text.translatable(existingKey != null ? "identity.profession.updated" : "identity.profession.saved", trimmedName, professionText), false);
        world.spawnParticles(net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1.0, player.getZ(), 10, 0.5, 0.5, 0.5, 0.0);
        PlayerIdentity.sync(player);
    }

    private static boolean matchesWorkstation(NbtCompound tag, Identifier worldId, long workstationPos) {
        if (tag == null) {
            return false;
        }
        String dim = tag.getString("WorkstationDim");
        long storedPos = tag.contains("WorkstationPos") ? tag.getLong("WorkstationPos") : Long.MIN_VALUE;
        return !dim.isEmpty() && storedPos != Long.MIN_VALUE && worldId.toString().equals(dim) && storedPos == workstationPos;
    }
}
