package draylar.identity.network.impl;

import dev.architectury.networking.NetworkManager;
import draylar.identity.impl.PlayerDataProvider;
import draylar.identity.network.ClientNetworking;
import draylar.identity.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.lang.reflect.Type;
import java.util.Map;

public class VillagerIdentitiesPackets {

    public static void sendSync(ServerPlayerEntity player) {
        PlayerDataProvider data = (PlayerDataProvider) player;
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());

        NbtCompound root = new NbtCompound();
        NbtCompound villagerTag = new NbtCompound();
        for (Map.Entry<String, NbtCompound> entry : data.getVillagerIdentities().entrySet()) {
            villagerTag.put(entry.getKey(), entry.getValue().copy());
        }
        root.put("VillagerIdentities", villagerTag);
        String active = data.getActiveVillagerKey();
        if (active != null && !active.isEmpty()) {
            root.putString("ActiveVillagerKey", active);
        }

        packet.writeNbt(root);
        NetworkManager.sendToPlayer(player, NetworkHandler.VILLAGER_IDENTITIES_SYNC, packet);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClientHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, NetworkHandler.VILLAGER_IDENTITIES_SYNC, (buf, context) -> {
            NbtCompound root = buf.readNbt();

            ClientNetworking.runOrQueue(context, player -> {
                if (root == null) return;
                PlayerDataProvider data = (PlayerDataProvider) player;
                data.getVillagerIdentities().clear();
                NbtCompound villagerTag = root.getCompound("VillagerIdentities");
                for (String key : villagerTag.getKeys()) {
                    data.getVillagerIdentities().put(key, villagerTag.getCompound(key));
                }
                String active = root.contains("ActiveVillagerKey", NbtElement.STRING_TYPE)
                        ? root.getString("ActiveVillagerKey") : null;
                data.setActiveVillagerKey(active == null || active.isEmpty() ? null : active);

                // Refresh identity screen if open
                net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
                if (mc.currentScreen instanceof draylar.identity.screen.IdentityScreen screen) {
                    screen.resize(mc, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
                }
            });
        });
    }
}
