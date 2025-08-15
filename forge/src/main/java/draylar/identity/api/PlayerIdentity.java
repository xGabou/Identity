package draylar.identity.api;

import draylar.identity.api.variant.IdentityType;
import draylar.identity.impl.PlayerDataProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class PlayerIdentity {
    private PlayerIdentity() {
    }

    public static LivingEntity getIdentity(Player player) {
        return ((PlayerDataProvider) player).getIdentity();
    }

    public static IdentityType<?> getIdentityType(Player player) {
        return ((PlayerDataProvider) player).getIdentityType();
    }

    public static boolean updateIdentity(ServerPlayer player, IdentityType<?> type, LivingEntity entity) {
        return ((PlayerDataProvider) player).updateIdentity(type, entity);
    }

    public static void sync(ServerPlayer player) {
        // TODO: networking sync will be implemented later
    }
}

