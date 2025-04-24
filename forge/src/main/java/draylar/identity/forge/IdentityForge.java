package draylar.identity.forge;

import dev.architectury.platform.Platform;
import draylar.identity.Identity;
import draylar.identity.forge.config.ConfigLoader;
import draylar.identity.forge.config.IdentityForgeConfig;
//import draylar.identity.forge.mixin.accessor.ForgeLivingEntityAccessorCompat;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod("identity")
public class IdentityForge {

    public static final IdentityForgeConfig CONFIG = ConfigLoader.read();

    public IdentityForge() {
        new Identity().initialize();
//        if (ModList.get().isLoaded("bjornlib")) {
//            ForgeLivingEntityCompatProvider.init(); // qui utilise Bjorn
//        }
        if(Platform.getEnv().isClient()) {
            new IdentityForgeClient();
        }
    }
}
