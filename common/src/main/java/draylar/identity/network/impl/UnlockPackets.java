package draylar.identity.network.impl;

import dev.architectury.networking.NetworkManager;
import draylar.identity.api.variant.IdentityType;
import draylar.identity.impl.PlayerDataProvider;
import draylar.identity.network.ClientNetworking;
import draylar.identity.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import draylar.identity.network.impl.Payload.*;

public class UnlockPackets {

    private static final String UNLOCK_KEY = "UnlockedIdentities";

    public static void registerClientHandler() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                UnlockSyncPayload.ID,
                UnlockSyncPayload.CODEC,
                (payload, context) -> {
                    NbtCompound nbt = payload.data();
                    if (nbt != null) {
                        NbtList list = nbt.getList(UNLOCK_KEY, NbtElement.COMPOUND_TYPE);

                        ClientNetworking.runOrQueue(context, player -> {
                            PlayerDataProvider data = (PlayerDataProvider) player;
                            data.getUnlocked().clear();
                            list.forEach(idTag -> data.getUnlocked().add(IdentityType.from((NbtCompound) idTag)));
                        });
                    }
                }
        );
    }

    public static void sendSyncPacket(ServerPlayerEntity player) {
        NbtCompound compound = new NbtCompound();
        NbtList idList = new NbtList();
        ((PlayerDataProvider) player).getUnlocked().forEach(type -> idList.add(type.writeCompound()));
        compound.put(UNLOCK_KEY, idList);

        NetworkManager.sendToPlayer(player, new UnlockSyncPayload(compound));
    }
}
