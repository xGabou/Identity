package draylar.identity.client;

import draylar.identity.Identity;
import draylar.identity.api.PlayerIdentity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "identity", value = Dist.CLIENT)
public class ForgeClientEvents {

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
