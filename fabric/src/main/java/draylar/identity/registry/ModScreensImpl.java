package draylar.identity.registry;

import draylar.identity.fabric.config.IdentityFabricConfig;
import draylar.omegaconfig.OmegaConfig;
import net.minecraft.client.gui.screen.Screen;

public class ModScreensImpl {
    public static Screen getConfigScreen(Screen parent) {
        return OmegaConfig.getConfigScreen(IdentityFabricConfig.class, parent);
    }
}

