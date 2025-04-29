package draylar.identity;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import draylar.identity.ability.AbilityRegistry;
import draylar.identity.api.IdentityTickHandlers;
import draylar.identity.api.PlayerFavorites;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.PlayerUnlocks;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.network.NetworkHandler;
import draylar.identity.network.ServerNetworking;
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
    }

    public static void registerJoinSyncPacket() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            // Send config sync packet
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            packet.writeBoolean(IdentityConfig.getInstance().enableClientSwapMenu());
            packet.writeBoolean(IdentityConfig.getInstance().showPlayerNametag());
            NetworkManager.sendToPlayer(player, NetworkHandler.CONFIG_SYNC, packet);

            // Sync unlocked Identity
            PlayerUnlocks.sync(player);

            // Sync favorites
            PlayerFavorites.sync(player);
        });
    }

    public static Identifier id(String name) {
        return new Identifier("identity", name);
    }

    public static boolean hasFlyingPermissions(ServerPlayerEntity player) {
        LivingEntity identity = PlayerIdentity.getIdentity(player);
        if(identity == null) {
            return false;
        }

        if(IdentityConfig.getInstance().enableFlight() && isAbleToFly(identity)) {
            List<String> requiredAdvancements = IdentityConfig.getInstance().advancementsRequiredForFlight();

            // requires at least 1 advancement, check if player has them
            if(!requiredAdvancements.isEmpty()) {

                boolean hasPermission = true;
                for (String requiredAdvancement : requiredAdvancements) {
                    Advancement advancement = player.server.getAdvancementLoader().get(new Identifier(requiredAdvancement));
                    AdvancementProgress progress = player.getAdvancementTracker().getProgress(advancement);

                    if(!progress.isDone()) {
                        hasPermission = false;
                    }
                }

                return hasPermission;
            }


            return true;
        }

        return false;
    }

    private static boolean isAbleToFly(LivingEntity identity) {
        if (identity == null) {
            return false;
        }

        EntityType<?> type = identity.getType();
        String idString = EntityType.getId(type).toString();
        IdentityConfig config = IdentityConfig.getInstance();

        // REMOVE > ADD > TAG priority
        if (config.removedFlyingEntities().contains(idString)) {
            return false;
        }

        if (config.extraFlyingEntities().contains(idString)) {
            return true;
        }

        return type.isIn(IdentityEntityTags.FLYING);
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
        return type.isIn(IdentityEntityTags.BREATHE_UNDERWATER);
    }

    public static int getCooldown(EntityType<?> type) {
        String id = Registries.ENTITY_TYPE.getId(type).toString();
        return IdentityConfig.getInstance().getAbilityCooldownMap().getOrDefault(id, 20);
    }
}
