package draylar.identity.util;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class AttributeSync {
    public static void syncMaxHealth(ServerPlayerEntity player) {
        if (player == null) return;

        double max = player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
        player.setHealth(Math.min(player.getHealth(), (float) max));

        EntityAttributesS2CPacket packet = new EntityAttributesS2CPacket(
                player.getId(),
                List.of(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH))
        );
        player.networkHandler.send(packet,null);
        ((ServerWorld) player.getWorld()).getChunkManager().sendToNearbyPlayers(player, packet);
    }
}
