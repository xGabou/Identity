package draylar.identity.forge;

import draylar.identity.IdentityClient;
import draylar.identity.api.model.forge.EntityUpdaterForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class IdentityForgeClient {

    public IdentityForgeClient() {
        new IdentityClient().initialize();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

    }
    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if(IdentityForge.isAlexsMobsLoaded) {
                EntityUpdaterForge.init();
            }
        });
    }

}
