package draylar.identity.client;

import draylar.identity.Identity;
import draylar.identity.api.PlayerIdentity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class NeoForgeClientEvents {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiLayerEvent.Pre event) {
        // Check specifically for the air bubbles overlay
        if (event.getName() == VanillaGuiLayers.AIR_LEVEL) {
            MinecraftClient mc = MinecraftClient.getInstance();
            ClientPlayerEntity player = mc.player;
            if (player == null) return;

            if (player.isSubmergedInWater()
                    && Identity.identity$isAquatic(PlayerIdentity.getIdentity(player))) {
                event.setCanceled(true);
            }
        }
    }
}
