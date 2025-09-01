package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.event.PlayerJoinCallback;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.impl.DimensionsRefresher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    // Fire the join callback when a player connects; injecting at HEAD is more stable across versions
    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    private void connect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        PlayerJoinCallback.EVENT.invoker().onPlayerJoin(player);
    }

    @Inject(
            method = "respawnPlayer",
            at = @At("RETURN")
    )
    private void onRespawn(ServerPlayerEntity player, boolean alive, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        ServerPlayerEntity newPlayer = cir.getReturnValue();
        LivingEntity identity = PlayerIdentity.getIdentity(newPlayer);

        // refresh entity hitbox dimensions after death
        ((DimensionsRefresher) newPlayer).identity_refreshDimensions();

        // Re-sync max health for identity
        if (identity != null && IdentityConfig.getInstance().scalingHealth()) {
            float prevMax = player.getMaxHealth();
            float ratio = prevMax <= 0 ? 1.0F : newPlayer.getHealth() / prevMax;
            float newMax = Math.min(IdentityConfig.getInstance().maxHealth(), identity.getMaxHealth());
            newPlayer.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(newMax);
            newPlayer.setHealth(Math.max(1.0F, ratio * newMax));

            // ✅ NeoForge 1.21.1: must use send(packet, null)
            newPlayer.networkHandler.send(
                    new EntityAttributesS2CPacket(
                            newPlayer.getId(),
                            newPlayer.getAttributes().getAttributesToSend()
                    ),
                    null
            );
        }
    }

}

