package draylar.identity.neoforge;

import dev.architectury.platform.Platform;
import draylar.identity.Identity;
import draylar.identity.api.platform.IdentityPlatform;
import draylar.identity.neoforge.config.ConfigLoader;
import draylar.identity.neoforge.config.NeoForgeConfigReloader;
import draylar.identity.neoforge.config.IdentityNeoForgeConfig;
import net.neoforged.fml.common.Mod;

@Mod("identity")
public class IdentityNeoForge {

    public static final int CONFIG_VERSION = 5;
    public static IdentityNeoForgeConfig CONFIG;

    public IdentityNeoForge() {
        CONFIG = ConfigLoader.read();
        new Identity().initialize();
        IdentityPlatform.setConfig(CONFIG);
        IdentityPlatform.setReloader(new NeoForgeConfigReloader());


//        if (ModList.get().isLoaded("bjornlib")) {
//            ForgeLivingEntityCompatProvider.init(); // qui utilise Bjorn
//        }
        if (Platform.getEnv().isClient()) {
            new IdentityNeoForgeClient();
        }
    }
}
