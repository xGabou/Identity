package draylar.identity.forge.registry;

import draylar.identity.forge.config.IdentityForgeConfigScreen;
import net.minecraft.client.gui.screen.Screen;

public class ModScreensImpl {
    public static Screen getConfigScreen(Screen parent) {
        return new IdentityForgeConfigScreen(parent);
    }
}

