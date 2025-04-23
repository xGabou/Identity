package draylar.identity.compat;

import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.impl.DimensionsRefresher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class BjornLibCompat {

//    public static void syncAttributesAndDimensions(ServerPlayerEntity player) {
//        LivingEntity identity = PlayerIdentity.getIdentity(player);
//
//        if (identity != null && IdentityConfig.getInstance().scalingHealth()) {
//            float maxHealth = Math.min(identity.getMaxHealth(), IdentityConfig.getInstance().maxHealth());
//            float currentRatio = player.getHealth() / player.getMaxHealth();
//            float adjustedHealth = Math.min(maxHealth, currentRatio * maxHealth);
//
//            player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
//            player.setHealth(adjustedHealth);
//        }
//
//        if (player instanceof DimensionsRefresher refresher) {
//            refresher.identity_refreshDimensions();
//        }
//
//        player.networkHandler.sendPacket(
//                new EntityAttributesS2CPacket(player.getId(), player.getAttributes().getAttributesToSend())
//        );
//    }
}
