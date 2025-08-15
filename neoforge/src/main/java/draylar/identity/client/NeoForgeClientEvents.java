package draylar.identity.client;

import draylar.identity.Identity;
import draylar.identity.api.PlayerIdentity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.client.event.RenderGuiOverlayEvent;
import net.neoforged.eventbus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "identity", value = Dist.CLIENT)
public class NeoForgeClientEvents {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        if ("air_level".equals(event.getOverlay().id().getPath())) {
            MinecraftClient mc = MinecraftClient.getInstance();
            PlayerEntity player = mc.player;
            if(player == null) return ;

            if (player.isSubmergedInWater() && Identity.identity$isAquatic(PlayerIdentity.getIdentity(player))) {
                event.setCanceled(true);
            }
        }
    }
}
