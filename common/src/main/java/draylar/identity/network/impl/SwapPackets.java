package draylar.identity.network.impl;

import dev.architectury.networking.NetworkManager;
import draylar.identity.Identity;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.impl.PlayerDataProvider;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.api.variant.IdentityType;
import draylar.identity.network.ClientNetworking;
import draylar.identity.network.NetworkHandler;
import net.Gabou.gaboulibs.util.CompatUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class SwapPackets {

    public static void registerIdentityRequestPacketHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkHandler.IDENTITY_REQUEST, (buf, context) -> {
            boolean validType = buf.readBoolean();
            if(validType) {
                EntityType<?> entityType = Registries.ENTITY_TYPE.get(buf.readIdentifier());
                int variant = buf.readInt();

                context.getPlayer().getServer().execute(() -> {
                    // Ensure player has permission to switch identities
                    if(IdentityConfig.getInstance().enableSwaps() ||
                        context.getPlayer().hasPermissionLevel(3) ||
                        IdentityConfig.getInstance().allowedSwappers().stream()
                            .anyMatch(p -> p.equalsIgnoreCase(context.getPlayer().getGameProfile().getName()))) {
                        if(CompatUtils.isBlacklistedEntityType(entityType.toString())) {
                            PlayerIdentity.updateIdentity((ServerPlayerEntity) context.getPlayer(), null, null);
                            ((PlayerDataProvider) context.getPlayer()).setActiveVillagerKey(null);
                        } else if(entityType.equals(EntityType.PLAYER)) {
                            PlayerIdentity.updateIdentity((ServerPlayerEntity) context.getPlayer(), null, null);
                            ((PlayerDataProvider) context.getPlayer()).setActiveVillagerKey(null);
                        } else {
                            @Nullable IdentityType<LivingEntity> type = IdentityType.from(entityType, variant);
                            if(type != null) {
                                try {
                                    LivingEntity created;
                                    String selectedVillagerKey = null;
                                    if (entityType.equals(EntityType.VILLAGER) && variant >= 1_000_000) {
                                        int index = variant - 1_000_000;
                                        draylar.identity.impl.PlayerDataProvider data = (draylar.identity.impl.PlayerDataProvider) context.getPlayer();
                                        java.util.List<String> keys = new java.util.ArrayList<>(data.getVillagerIdentities().keySet());
                                        java.util.Collections.sort(keys);
                                        if (index >= 0 && index < keys.size()) {
                                            String key = keys.get(index);
                                            net.minecraft.nbt.NbtCompound tag = data.getVillagerIdentities().get(key);
                                            net.minecraft.nbt.NbtCompound copy = tag.copy();
                                            copy.putString("id", net.minecraft.registry.Registries.ENTITY_TYPE.getId(EntityType.VILLAGER).toString());
                                            created = (LivingEntity) EntityType.loadEntityWithPassengers(copy, context.getPlayer().getWorld(), it -> it);
                                            selectedVillagerKey = key;
                                        } else {
                                            created = type.create(context.getPlayer().getWorld());
                                        }
                                    } else {
                                        created = type.create(context.getPlayer().getWorld());
                                    }
                                    PlayerIdentity.updateIdentity((ServerPlayerEntity) context.getPlayer(), type, created);
                                    ((PlayerDataProvider) context.getPlayer()).setActiveVillagerKey(selectedVillagerKey);
                                } catch (Exception e) {
                                    CompatUtils.markIncompatibleEntityType(entityType.toString());
                                    Identity.LOGGER.warn("Failed to create identity " + entityType.getTranslationKey(), e);
                                }
                            }
                        }

                        // Refresh player dimensions
                        context.getPlayer().calculateDimensions();
                    }
                });
            } else {
                // Swap back to player if server allows it
                context.getPlayer().getServer().execute(() -> {
                    if(IdentityConfig.getInstance().enableSwaps() ||
                        context.getPlayer().hasPermissionLevel(3) ||
                        IdentityConfig.getInstance().allowedSwappers().stream()
                            .anyMatch(p -> p.equalsIgnoreCase(context.getPlayer().getGameProfile().getName()))) {
                        PlayerIdentity.updateIdentity((ServerPlayerEntity) context.getPlayer(), null, null);
                        ((PlayerDataProvider) context.getPlayer()).setActiveVillagerKey(null);
                    }

                    context.getPlayer().calculateDimensions();
                });
            }
        });
    }

    public static void sendSwapRequest(@Nullable IdentityType<?> type) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());

        packet.writeBoolean(type != null);
        if(type != null) {
            packet.writeIdentifier(Registries.ENTITY_TYPE.getId(type.getEntityType()));
            packet.writeInt(type.getVariantData());
        }

        NetworkManager.sendToServer(ClientNetworking.IDENTITY_REQUEST, packet);
    }
}
