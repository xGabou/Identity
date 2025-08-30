package draylar.identity.fabric;

import draylar.identity.Identity;
import draylar.identity.api.platform.IdentityPlatform;
import draylar.identity.fabric.config.FabricConfigReloader;
import draylar.identity.fabric.config.IdentityFabricConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class IdentityFabric implements ModInitializer {

    public static final int CONFIG_VERSION = 2;
    public static IdentityFabricConfig CONFIG;

    @Override
    public void onInitialize() {
        AutoConfig.register(IdentityFabricConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(IdentityFabricConfig.class).getConfig();
        IdentityPlatform.setConfig(CONFIG);
        IdentityPlatform.setReloader(new FabricConfigReloader());
        new Identity().initialize();
    }
}
