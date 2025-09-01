package draylar.identity.network.impl;

import dev.architectury.networking.NetworkManager;
import draylar.identity.Identity;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.api.variant.IdentityType;
import draylar.identity.network.ClientNetworking;
import draylar.identity.network.NetworkHandler;
import draylar.identity.util.IdentityCompatUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import draylar.identity.network.impl.Payload.*;

public class SwapPackets {

    public static void registerIdentityRequestPacketHandler() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                SwapRequestPayload.ID,
                SwapRequestPayload.CODEC,
                (payload, context) -> {
                    boolean validType = payload.validType();

                    if (validType && payload.entityTypeId() != null) {
                        EntityType<?> entityType = Registries.ENTITY_TYPE.get(payload.entityTypeId());
                        int variant = payload.variant();

                        context.queue(() -> {
                            if (IdentityConfig.getInstance().enableSwaps()
                                    || context.getPlayer().hasPermissionLevel(3)
                                    || IdentityConfig.getInstance().allowedSwappers().stream()
                                    .anyMatch(p -> p.equalsIgnoreCase(context.getPlayer().getGameProfile().getName()))) {

                                if (entityType.equals(EntityType.PLAYER)) {
                                    PlayerIdentity.updateIdentity((ServerPlayerEntity) context.getPlayer(), null, null);
                                } else {
                                    @Nullable IdentityType<LivingEntity> type = IdentityType.from(entityType, variant);
                                    if (type != null) {
                                        try {
                                            LivingEntity created;
                                            if (entityType.equals(EntityType.VILLAGER) && variant >= 1_000_000) {
                                                int index = variant - 1_000_000;
                                                draylar.identity.impl.PlayerDataProvider data =
                                                        (draylar.identity.impl.PlayerDataProvider) context.getPlayer();
                                                java.util.List<String> keys = new java.util.ArrayList<>(data.getVillagerIdentities().keySet());
                                                java.util.Collections.sort(keys);
                                                if (index >= 0 && index < keys.size()) {
                                                    net.minecraft.nbt.NbtCompound tag = data.getVillagerIdentities().get(keys.get(index));
                                                    net.minecraft.nbt.NbtCompound copy = tag.copy();
                                                    copy.putString("id", Registries.ENTITY_TYPE.getId(EntityType.VILLAGER).toString());
                                                    created = (LivingEntity) EntityType.loadEntityWithPassengers(copy, context.getPlayer().getWorld(), it -> it);
                                                } else {
                                                    created = type.create(context.getPlayer().getWorld());
                                                }
                                            } else {
                                                created = type.create(context.getPlayer().getWorld());
                                            }
                                            PlayerIdentity.updateIdentity((ServerPlayerEntity) context.getPlayer(), type, created);
                                        } catch (Exception e) {
                                            IdentityCompatUtils.markIncompatibleEntityType(entityType);
                                            Identity.LOGGER.warn("Failed to create identity " + entityType.getTranslationKey(), e);
                                        }
                                    }
                                }
                                context.getPlayer().calculateDimensions();
                            }
                        });
                    } else {
                        // Swap back to player
                        context.queue(() -> {
                            if (IdentityConfig.getInstance().enableSwaps()
                                    || context.getPlayer().hasPermissionLevel(3)
                                    || IdentityConfig.getInstance().allowedSwappers().stream()
                                    .anyMatch(p -> p.equalsIgnoreCase(context.getPlayer().getGameProfile().getName()))) {
                                PlayerIdentity.updateIdentity((ServerPlayerEntity) context.getPlayer(), null, null);
                            }
                            context.getPlayer().calculateDimensions();
                        });
                    }
                }
        );
    }

    public static void sendSwapRequest(@Nullable IdentityType<?> type) {
        boolean validType = type != null;
        Identifier entityId = validType ? Registries.ENTITY_TYPE.getId(type.getEntityType()) : null;
        int variant = validType ? type.getVariantData() : 0;

        NetworkManager.sendToServer(new SwapRequestPayload(validType, entityId, variant));
    }
}

