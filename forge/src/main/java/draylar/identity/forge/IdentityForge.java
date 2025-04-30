package draylar.identity.forge;

import dev.architectury.platform.Platform;
import draylar.identity.Identity;
import draylar.identity.forge.ability.AlexsMobsAbilityRegistry;
import draylar.identity.forge.config.ConfigLoader;
import draylar.identity.forge.config.IdentityForgeConfig;
//import draylar.identity.forge.mixin.accessor.ForgeLivingEntityAccessorCompat;
import draylar.identity.forge.network.NetworkHandler;
import draylar.identity.util.IdentityCompatUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("identity")
public class IdentityForge {

    public static final boolean isAlexsMobsLoaded = IdentityCompatUtils.isAlexsMobsLoaded();
    public static final int CONFIG_VERSION = 3;
    public static final IdentityForgeConfig CONFIG = ConfigLoader.read();

    public IdentityForge() {
        new Identity().initialize();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

//        if (ModList.get().isLoaded("bjornlib")) {
//            ForgeLivingEntityCompatProvider.init(); // qui utilise Bjorn
//        }
        if(Platform.getEnv().isClient()) {
            new IdentityForgeClient();
        }
    }


    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            NetworkHandler.registerPackets();
            AlexsMobsAbilityRegistry.init();
        });
    }


}
