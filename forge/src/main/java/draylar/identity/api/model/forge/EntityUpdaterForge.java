package draylar.identity.api.model.forge;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import draylar.identity.api.model.EntityUpdaters;
import draylar.identity.forge.IdentityForge;
import draylar.identity.forge.network.ForceDancePacket;
import draylar.identity.forge.network.NetworkHandler;
import draylar.identity.impl.NearbySongAccessor;

public class EntityUpdaterForge {

    private EntityUpdaterForge() {}
    public static void init() {
        if (!IdentityForge.isAlexsMobsLoaded) {
            return;
        }
        EntityUpdaters.register(AMEntityRegistry.COCKROACH.get(), (player, cockroach) -> {
            if (player.isOnGround() && ((NearbySongAccessor) player).identity_isNearbySongPlaying()) {

                cockroach.setNearbySongPlaying(player.getBlockPos(), true);
                cockroach.setDancing(true);
                cockroach.handleStatus((byte) 67);

                NetworkHandler.CHANNEL.sendToServer(new ForceDancePacket(cockroach.getBlockPos()));

            } else {
                cockroach.setNearbySongPlaying(player.getBlockPos(), false);
                cockroach.setDancing(false);
            }
        });

    }
}
