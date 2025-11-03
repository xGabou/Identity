package draylar.identity.network;

import dev.architectury.networking.NetworkManager;
import draylar.identity.IdentityClient;
import draylar.identity.api.ApplicablePacket;
import draylar.identity.impl.DimensionsRefresher;
import draylar.identity.impl.PlayerDataProvider;
import draylar.identity.network.impl.FavoritePackets;
import draylar.identity.network.impl.Payload;
import draylar.identity.network.impl.UnlockPackets;
import draylar.identity.network.impl.VillagerProfessionPackets;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import draylar.identity.network.impl.Payload.*;

import java.util.Optional;
import java.util.UUID;

public class ClientNetworking implements NetworkHandler {

    public static void registerPacketHandlers() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                IdentitySyncPayload.ID,
                IdentitySyncPayload.CODEC,
                ClientNetworking::handleIdentitySyncPacket
        );
        UnlockPackets.registerClientHandler();
        FavoritePackets.registerFavoriteSyncHandler();
        draylar.identity.network.impl.VillagerIdentitiesPackets.registerClientHandler();
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                AbilitySyncPayload.ID,
                AbilitySyncPayload.CODEC,
                ClientNetworking::handleAbilitySyncPacket
        );
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                ConfigSyncPayload.ID,
                ConfigSyncPayload.CODEC,
                ClientNetworking::handleConfigurationSyncPacket
        );
        draylar.identity.network.client.VillagerProfessionClient.registerClientHandler();


    }

    public static void runOrQueue(NetworkManager.PacketContext context, ApplicablePacket packet) {
        if(context.getPlayer() == null) {
            IdentityClient.getSyncPacketQueue().add(packet);
        } else {
            context.queue(() -> packet.apply(context.getPlayer()));
        }
    }

    public static void sendAbilityRequest() {
        NetworkManager.sendToServer(new UseAbilityPayload());

    }

    public static void handleIdentitySyncPacket(IdentitySyncPayload packet, NetworkManager.PacketContext context) {
        final UUID uuid = packet.uuid();
        final String id = packet.id();
        final NbtCompound entityNbt = packet.entityNbt();

        runOrQueue(context, player -> {
            @Nullable PlayerEntity syncTarget = player.getEntityWorld().getPlayerByUuid(uuid);

            if(syncTarget != null) {
                PlayerDataProvider data = (PlayerDataProvider) syncTarget;

                // set identity to null (no identity) if the entity id is "minecraft:empty"
                if(id.equals("minecraft:empty")) {
                    data.setIdentity(null);
                    ((DimensionsRefresher) syncTarget).identity_refreshDimensions();
                    return;
                }

                // If entity type was valid, deserialize entity data from tag/
                if(entityNbt != null) {
                    entityNbt.putString("id", id);
                    Optional<EntityType<?>> type = EntityType.fromNbt(entityNbt);
                    if(type.isPresent()) {
                        LivingEntity identity = data.getIdentity();

                        // ensure entity data exists
                        if(identity == null || !type.get().equals(identity.getType())) {
                            identity = (LivingEntity) type.get().create(syncTarget.getWorld());
                            data.setIdentity(identity);

                            // refresh player dimensions/hitbox on client
                            ((DimensionsRefresher) syncTarget).identity_refreshDimensions();
                            syncTarget.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT)
                                    .setBaseValue(identity.getAttributeValue(EntityAttributes.GENERIC_STEP_HEIGHT));
                            // sync stepping ability
                            syncTarget.setVelocity(syncTarget.getVelocity().multiply(1, 0, 1)); // reset vertical drag if any
                            syncTarget.fallDistance = 0.0F; // avoid weird midair fall damage
                            syncTarget.prevX = syncTarget.getX(); // reset motion interpolation
                            syncTarget.prevY = syncTarget.getY();
                            syncTarget.prevZ = syncTarget.getZ();
                            syncTarget.velocityDirty = true; // force re-sync of movement state
                            ensureSafePosition(syncTarget);
                            syncTarget.calculateDimensions(); // Forces recalculation on client

                        }

                        if(identity != null) {
                            identity.readNbt(entityNbt);
                        }
                    }
                }
            }
        });
    }
    private static void ensureSafePosition(PlayerEntity player) {
        if (player.isInsideWall()) {
            double safeY = player.getY();
            for (int i = 1; i <= 2; i++) {
                if (!player.getWorld().getBlockState(player.getBlockPos().up(i)).isSolidBlock(player.getWorld(), player.getBlockPos().up(i))) {
                    safeY = player.getY() + i;
                    break;
                }
            }
            player.setPosition(player.getX(), safeY, player.getZ());
        }
    }


    public static void handleAbilitySyncPacket(AbilitySyncPayload payload, NetworkManager.PacketContext context) {
        int cooldown = payload.cooldown();
        runOrQueue(context, player -> ((PlayerDataProvider) player).setAbilityCooldown(cooldown));
    }


    public static void handleConfigurationSyncPacket(ConfigSyncPayload payload, NetworkManager.PacketContext context) {
        boolean enableClientSwapMenu = payload.enableClientSwapMenu();
        boolean showPlayerNametag = payload.showPlayerNametag();

        // TODO: re-handle sync packet
        // IdentityConfig.getInstance().setEnableClientSwapMenu(enableClientSwapMenu);
        // IdentityConfig.getInstance().setShowPlayerNametag(showPlayerNametag);
    }


    private ClientNetworking() {
        // NO-OP
    }



}
