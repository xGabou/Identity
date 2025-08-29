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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;

public class VillagerProfessionPackets {

    public static void openScreen(ServerPlayerEntity player, Identifier professionId, net.minecraft.util.math.BlockPos pos, Identifier worldId) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeIdentifier(professionId);
        packet.writeBlockPos(pos);
        packet.writeIdentifier(worldId);
        NetworkManager.sendToPlayer(player, NetworkHandler.OPEN_PROFESSION_SCREEN, packet);
    }

    public static void sendSetProfession(Identifier professionId, String name, boolean reset, net.minecraft.util.math.BlockPos pos, Identifier worldId) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeIdentifier(professionId);
        packet.writeString(name);
        packet.writeBoolean(reset);
        packet.writeBlockPos(pos);
        packet.writeIdentifier(worldId);
        NetworkManager.sendToServer(NetworkHandler.SET_PROFESSION, packet);
    }

    public static void registerServerHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkHandler.SET_PROFESSION, (buf, context) -> {
            Identifier professionId = buf.readIdentifier();
            String name = buf.readString();
            boolean reset = buf.readBoolean();
            net.minecraft.util.math.BlockPos pos = buf.readBlockPos();
            Identifier worldId = buf.readIdentifier();
            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();

            context.getPlayer().getServer().execute(() -> {
                if (reset) {
                    ((PlayerDataProvider) player).removeVillagerIdentity(name);
                    PlayerIdentity.updateIdentity(player, null, null);
                    player.sendMessage(Text.literal("Identity: removed villager '" + name + "'"), false);
                } else {
                    // Enforce: only unemployed can take a profession
                    if (((PlayerDataProvider) player).getVillagerIdentities().containsKey(name)) {
                        net.minecraft.nbt.NbtCompound existing = ((PlayerDataProvider) player).getVillagerIdentities().get(name);
                        String existingProf = existing.getString("ProfessionId");
                        if (existingProf != null && !existingProf.isEmpty()) {
                            player.sendMessage(Text.literal("Identity: already employed as " + existingProf + ". Use Reset first."), false);
                            return;
                        }
                    }
                    // Optional: prevent duplicates across names
                    boolean exists = ((PlayerDataProvider) player).getVillagerIdentities().values().stream().anyMatch(c -> c.getString("ProfessionId").equals(professionId.toString()));
                    if (exists) {
                        player.sendMessage(Text.translatable("identity.profession.duplicate"), false);
                        return;
                    }
                    if (PlayerIdentity.getIdentity(player) instanceof VillagerEntity villager) {
                        // Validate workstation matches profession and still exists
                        net.minecraft.server.world.ServerWorld world = player.getServer().getWorld(net.minecraft.registry.RegistryKey.of(net.minecraft.registry.RegistryKeys.WORLD, worldId));
                        if (world == null || !world.getRegistryKey().equals(player.getWorld().getRegistryKey())) {
                            player.sendMessage(Text.literal("Identity: invalid workstation world"), false);
                            return;
                        }
                        java.util.Optional<net.minecraft.registry.entry.RegistryEntry<net.minecraft.world.poi.PointOfInterestType>> poi = net.minecraft.world.poi.PointOfInterestTypes.getTypeForState(world.getBlockState(pos));
                        if (poi.isEmpty()) {
                            player.sendMessage(Text.literal("Identity: no workstation at target"), false);
                            return;
                        }
                        NbtCompound tag = new NbtCompound();
                        villager.writeNbt(tag);
                        tag.putString("ProfessionId", professionId.toString());
                        tag.putString("WorkstationDim", worldId.toString());
                        tag.putLong("WorkstationPos", pos.asLong());
                        ((PlayerDataProvider) player).setVillagerIdentity(name, tag);
                        PlayerIdentity.updateIdentity(player, null, null);
                        player.sendMessage(Text.literal("Identity: saved villager '" + name + "' as " + professionId), false);
                        // Emit happy villager particles at player
                        world.spawnParticles(net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1.0, player.getZ(), 10, 0.5, 0.5, 0.5, 0.0);
                    }
                }
            });
        });
    }

    public static void registerClientHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.OPEN_PROFESSION_SCREEN, (buf, context) -> {
            Identifier professionId = buf.readIdentifier();
            net.minecraft.util.math.BlockPos pos = buf.readBlockPos();
            Identifier worldId = buf.readIdentifier();
            ClientNetworking.runOrQueue(context, player -> MinecraftClient.getInstance().setScreen(new VillagerProfessionScreen(professionId, pos, worldId)));
        });
    }
}
