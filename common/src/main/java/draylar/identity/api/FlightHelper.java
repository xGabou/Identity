package draylar.identity.api;

import net.minecraft.server.network.ServerPlayerEntity;

public class FlightHelper {

    public static void grantFlightTo(ServerPlayerEntity player) {
        player.getAbilities().allowFlying = true;
    }

    public static boolean hasFlight(ServerPlayerEntity player) {
        return player.getAbilities().allowFlying;
    }

    public static void revokeFlight(ServerPlayerEntity player) {
        // Do not interfere with creative or spectator flight
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        player.getAbilities().allowFlying = false;
        player.getAbilities().flying = false;
        player.sendAbilitiesUpdate();
    }
}
