package draylar.identity.network.impl;

import dev.architectury.networking.NetworkManager;
import draylar.identity.api.PlayerFavorites;
import draylar.identity.api.variant.IdentityType;
import draylar.identity.impl.PlayerDataProvider;
import draylar.identity.network.ClientNetworking;
import draylar.identity.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import draylar.identity.network.impl.Payload.*;

import java.util.Set;

public class FavoritePackets {

    public static void sendFavoriteRequest(IdentityType<?> type, boolean favorite) {
        Identifier entityId = Registries.ENTITY_TYPE.getId(type.getEntityType());
        NetworkManager.sendToServer(new FavoriteUpdatePayload(entityId, type.getVariantData(), favorite));
    }

    public static void registerFavoriteRequestHandler() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                FavoriteUpdatePayload.ID,
                FavoriteUpdatePayload.CODEC,
                (payload, context) -> {
                    EntityType<?> entityType = Registries.ENTITY_TYPE.get(payload.entityTypeId());
                    int variant = payload.variant();
                    boolean favorite = payload.favorite();
                    ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();

                    context.queue(() -> {
                        @Nullable IdentityType<?> type = IdentityType.from(entityType, variant);
                        if (type != null) {
                            if (favorite) {
                                PlayerFavorites.favorite(player, type);
                            } else {
                                PlayerFavorites.unfavorite(player, type);
                            }
                        }
                    });
                }
        );
    }

    public static void sendFavoriteSync(ServerPlayerEntity player) {
        Set<IdentityType<?>> favorites = ((PlayerDataProvider) player).getFavorites();
        NbtCompound tag = new NbtCompound();
        NbtList idList = new NbtList();
        favorites.forEach(type -> idList.add(type.writeCompound()));
        tag.put("FavoriteIdentities", idList);

        NetworkManager.sendToPlayer(player, new FavoriteSyncPayload(tag));
    }

    public static void registerFavoriteSyncHandler() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                FavoriteSyncPayload.ID,
                FavoriteSyncPayload.CODEC,
                (payload, context) -> {
                    NbtCompound tag = payload.data();
                    ClientNetworking.runOrQueue(context, player -> {
                        PlayerDataProvider data = (PlayerDataProvider) player;
                        data.getFavorites().clear();
                        NbtList idList = tag.getList("FavoriteIdentities", NbtElement.COMPOUND_TYPE);
                        idList.forEach(compound -> data.getFavorites().add(IdentityType.from((NbtCompound) compound)));
                    });
                }
        );
    }
}
