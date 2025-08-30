package draylar.identity.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.gui.screen.Screen;

public class ModScreens {
    @ExpectPlatform
    public static Screen getConfigScreen(Screen parent) {
        throw new AssertionError();
    }
}

