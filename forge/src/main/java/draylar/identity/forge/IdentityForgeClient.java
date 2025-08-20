package draylar.identity.forge;

import draylar.identity.IdentityClient;
import draylar.identity.api.model.forge.EntityUpdaterForge;
import draylar.identity.registry.ModScreens;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class IdentityForgeClient {

    public IdentityForgeClient() {
        new IdentityClient().initialize();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> ModScreens.getConfigScreen(screen)));

    }
    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if(IdentityForge.isAlexsMobsLoaded) {
                EntityUpdaterForge.init();
            }
        });
    }

}
