package draylar.identity.forge;

import dev.architectury.platform.Platform;
import draylar.identity.Identity;
import draylar.identity.api.platform.IdentityPlatform;
import draylar.identity.forge.ability.AlexsMobsAbilityRegistry;
//import draylar.identity.forge.ability.NaturalistAbilityRegistry;
import draylar.identity.forge.ability.NaturalistAbilityRegistry;
import draylar.identity.forge.config.ConfigLoader;
import draylar.identity.forge.config.ForgeConfigReloader;
import draylar.identity.forge.config.IdentityForgeConfig;
import draylar.identity.util.IdentityCompatUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("identity")
public class IdentityForge {

    public static final boolean isAlexsMobsLoaded = IdentityCompatUtils.isAlexsMobsLoaded();
    //public static final boolean isNaturalistLoaded = IdentityCompatUtils.isNaturalistLoaded();
    public static final int CONFIG_VERSION = 5;
    public static IdentityForgeConfig CONFIG;

    public IdentityForge() {
        CONFIG = ConfigLoader.read();
        new Identity().initialize();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        IdentityPlatform.setConfig(CONFIG);
        IdentityPlatform.setReloader(new ForgeConfigReloader());


//        if (ModList.get().isLoaded("bjornlib")) {
//            ForgeLivingEntityCompatProvider.init(); // qui utilise Bjorn
//        }
        if (Platform.getEnv().isClient()) {
            new IdentityForgeClient();
        }
    }


    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AlexsMobsAbilityRegistry.init();
            NaturalistAbilityRegistry.init();
        });
    }



}
