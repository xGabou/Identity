package draylar.identity;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import draylar.identity.ability.AbilityRegistry;
import draylar.identity.api.*;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.network.NetworkHandler;
import draylar.identity.network.ServerNetworking;
import draylar.identity.network.impl.Payload;
import draylar.identity.registry.IdentityCommands;
import draylar.identity.registry.IdentityEntityTags;
import draylar.identity.registry.IdentityEventHandlers;
import io.netty.buffer.Unpooled;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Identity {

    public static final String MODID = "identity";
    public static final Logger LOGGER = LoggerFactory.getLogger(Identity.class);

    public void initialize() {
        IdentityEntityTags.init();
        AbilityRegistry.init();
        IdentityEventHandlers.initialize();
        IdentityCommands.init();
        ServerNetworking.initialize();
        ServerNetworking.registerUseAbilityPacketHandler();
        registerJoinSyncPacket();
        IdentityTickHandlers.initialize();
        LifecycleEvent.SERVER_STARTING.register(server -> {
            SafeTagManager.loadAll(server);
        });
    }

    public static void registerJoinSyncPacket() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            // Send config sync packet
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            packet.writeBoolean(IdentityConfig.getInstance().enableClientSwapMenu());
            packet.writeBoolean(IdentityConfig.getInstance().showPlayerNametag());
            NetworkManager.sendToPlayer(player,
                    new Payload.ConfigSyncPayload(
                            IdentityConfig.getInstance().enableClientSwapMenu(),
                            IdentityConfig.getInstance().showPlayerNametag()
                    )
            );

            // Sync unlocked Identity
            PlayerUnlocks.sync(player);

            // Sync favorites
            PlayerFavorites.sync(player);
            draylar.identity.network.impl.VillagerIdentitiesPackets.sendSync(player);

        });
    }

    public static Identifier id(String name) {
        return Identifier.of("identity", name);
    }

    public static boolean hasFlyingPermissions(ServerPlayerEntity player) {
        boolean hasPermission = false;
        LivingEntity identity = PlayerIdentity.getIdentity(player);
        if(identity == null) {
            return hasPermission;
        }
        return IdentityConfig.getInstance().enableFlight() && isAbleToFly(identity);
    }

    private static boolean isAbleToFly(LivingEntity identity) {
        if (identity == null) return false;

        EntityType<?> type = identity.getType();
        Identifier id = EntityType.getId(type);
        String idString = id.toString();

        IdentityConfig config = IdentityConfig.getInstance();

        if (config.removedFlyingEntities().contains(idString)) return false;
        if (config.extraFlyingEntities().contains(idString)) return true;

        // Check both normal and custom flying tags
        return type.isIn(IdentityEntityTags.FLYING) || SafeTagManager.isCustomFlying(type);
    }



    public static boolean identity$isAquatic(LivingEntity identity) {
        if (identity == null) {
            return false;
        }

        EntityType<?> type = identity.getType();
        Identifier id = EntityType.getId(type);
        String idString = id.toString();

        IdentityConfig config =  IdentityConfig.getInstance();

        // REMOVE > ADD > TAG priority
        if (config.removedAquaticEntities().contains(idString)) {
            return false; // Player requested this mob NOT be aquatic
        }

        if (config.extraAquaticEntities().contains(idString)) {
            return true; // Player manually added it
        }

        // Otherwise, fallback to normal tag detection
        return type.isIn(IdentityEntityTags.BREATHE_UNDERWATER)|| SafeTagManager.isCustomBreatheUnderwater(type);
    }

    public static int getCooldown(EntityType<?> type) {
        String id = Registries.ENTITY_TYPE.getId(type).toString();
        return IdentityConfig.getInstance().getAbilityCooldownMap().getOrDefault(id, 20);
    }
}
