package draylar.identity.mixin.player;

import draylar.identity.impl.PlayerDataProvider;
import draylar.identity.mixin.accessor.ClientCommonNetworkHandlerAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayerDataCacheMixin {

    @Unique private PlayerDataProvider dataCache = null;

    // Cache the player's custom data at the start of respawn handling.
    // Using HEAD avoids hardcoding internal call descriptors between versions.
    @Inject(method = "onPlayerRespawn", at = @At("HEAD"))
    private void beforePlayerReset(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = ((ClientCommonNetworkHandlerAccessor)this).getClient();
        dataCache = ((PlayerDataProvider) client.player);
    }

    // Re-apply cached data once the new player has been created.
    // Using RETURN avoids brittle INVOKE targets that change across mappings.
    @Inject(method = "onPlayerRespawn", at = @At("RETURN"))
    private void afterPlayerReset(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = ((ClientCommonNetworkHandlerAccessor)this).getClient();
        if(dataCache != null && client.player != null) {
            ((PlayerDataProvider) client.player).setIdentity(dataCache.getIdentity());
            ((PlayerDataProvider) client.player).setUnlocked(dataCache.getUnlocked());
            ((PlayerDataProvider) client.player).setFavorites(dataCache.getFavorites());
            ((PlayerDataProvider) client.player).setAbilityCooldown(dataCache.getAbilityCooldown());
            ((PlayerDataProvider) client.player).setRemainingHostilityTime(dataCache.getRemainingHostilityTime());
            // Villager identities & active key to minimize UI blips
            ((PlayerDataProvider) client.player).getVillagerIdentities().clear();
            ((PlayerDataProvider) client.player).getVillagerIdentities().putAll(dataCache.getVillagerIdentities());
            ((PlayerDataProvider) client.player).setActiveVillagerKey(dataCache.getActiveVillagerKey());
        }

        dataCache = null;
    }
}
