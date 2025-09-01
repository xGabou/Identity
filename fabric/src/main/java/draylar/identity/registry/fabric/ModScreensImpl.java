package draylar.identity.registry.fabric;

import draylar.identity.fabric.config.IdentityFabricConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

public class ModScreensImpl {
    public static Screen getConfigScreen(Screen parent) {
        return AutoConfig.getConfigScreen(IdentityFabricConfig.class, parent).get();
    }
}

