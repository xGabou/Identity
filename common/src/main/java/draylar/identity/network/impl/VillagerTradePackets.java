package draylar.identity.network.impl;

import dev.architectury.networking.NetworkManager;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class VillagerTradePackets {

    public static void sendTradeRequest(UUID target) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeUuid(target);
        NetworkManager.sendToServer(NetworkHandler.START_TRADE, packet);
    }

    public static void registerTradeRequestHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkHandler.START_TRADE, (buf, context) -> {
            UUID targetId = buf.readUuid();
            ServerPlayerEntity requester = (ServerPlayerEntity) context.getPlayer();
            context.getPlayer().getServer().execute(() -> {
                ServerPlayerEntity target = requester.getServer().getPlayerManager().getPlayer(targetId);
                if (target != null) {
                    LivingEntity identity = PlayerIdentity.getIdentity(target);
                    if (identity instanceof VillagerEntity villager) {
                        requester.openHandledScreen(villager);
                    }
                }
            });
        });
    }
}
