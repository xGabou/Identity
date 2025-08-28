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

    public static void openScreen(ServerPlayerEntity player, Identifier professionId) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeIdentifier(professionId);
        NetworkManager.sendToPlayer(player, NetworkHandler.OPEN_PROFESSION_SCREEN, packet);
    }

    public static void sendSetProfession(Identifier professionId, String name, boolean reset) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeIdentifier(professionId);
        packet.writeString(name);
        packet.writeBoolean(reset);
        NetworkManager.sendToServer(NetworkHandler.SET_PROFESSION, packet);
    }

    public static void registerServerHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkHandler.SET_PROFESSION, (buf, context) -> {
            Identifier professionId = buf.readIdentifier();
            String name = buf.readString();
            boolean reset = buf.readBoolean();
            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();

            context.getPlayer().getServer().execute(() -> {
                if (reset) {
                    ((PlayerDataProvider) player).removeVillagerIdentity(name);
                    PlayerIdentity.updateIdentity(player, null, null);
                } else {
                    boolean exists = ((PlayerDataProvider) player).getVillagerIdentities().values().stream().anyMatch(c -> c.getString("ProfessionId").equals(professionId.toString()));
                    if (exists) {
                        player.sendMessage(Text.translatable("identity.profession.duplicate"), false);
                        return;
                    }
                    if (PlayerIdentity.getIdentity(player) instanceof VillagerEntity villager) {
                        NbtCompound tag = new NbtCompound();
                        villager.writeNbt(tag);
                        tag.putString("ProfessionId", professionId.toString());
                        ((PlayerDataProvider) player).setVillagerIdentity(name, tag);
                        PlayerIdentity.updateIdentity(player, null, null);
                    }
                }
            });
        });
    }

    public static void registerClientHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.OPEN_PROFESSION_SCREEN, (buf, context) -> {
            Identifier professionId = buf.readIdentifier();
            ClientNetworking.runOrQueue(context, player -> MinecraftClient.getInstance().setScreen(new VillagerProfessionScreen(professionId)));
        });
    }
}
