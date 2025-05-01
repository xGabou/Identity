package draylar.identity.api.model.forge;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import draylar.identity.api.model.EntityUpdaters;
import draylar.identity.forge.IdentityForge;
import draylar.identity.impl.NearbySongAccessor;

public class EntityUpdaterForge {

    private EntityUpdaterForge() {}
    public static void init() {
        if (!IdentityForge.isAlexsMobsLoaded) {
            return;
        }
        System.out.println("Registering entity updaters for Alex's Mobs!");
//        EntityUpdaters.register(AMEntityRegistry.COCKROACH.get(), (player, cockroach) -> {
//            if (player.isOnGround() && ((NearbySongAccessor) player).identity_isNearbySongPlaying()) {
//
//                cockroach.setNearbySongPlaying(player.getBlockPos(), true);
//                cockroach.setDancing(true);
//                player.handleStatus((byte) 67);
//                System.out.println("Playing nearby song!");
//
//            } else {
//                cockroach.setNearbySongPlaying(player.getBlockPos(), false);
//                cockroach.setDancing(false);
//                System.out.println("Stopping nearby song!");
//            }
//        });

    }
}
