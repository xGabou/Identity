package draylar.identity.registry;

import net.minecraft.client.gui.screen.Screen;

import java.util.function.Supplier;

public class ModScreens {
    public static Supplier<Screen> configScreen(Screen parent) {
        return () -> new MyConfigScreen(parent);
    }
}
