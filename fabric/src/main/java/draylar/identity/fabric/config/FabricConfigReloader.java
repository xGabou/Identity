package draylar.identity.fabric.config;

import draylar.identity.api.platform.ConfigReloader;
import draylar.identity.api.platform.IdentityPlatform;
import draylar.identity.fabric.IdentityFabric;
import me.shedaniel.autoconfig.AutoConfig;

public class FabricConfigReloader implements ConfigReloader {
    @Override
    public void reloadConfig() {
        AutoConfig.getConfigHolder(IdentityFabricConfig.class).load();
        IdentityFabric.CONFIG = AutoConfig.getConfigHolder(IdentityFabricConfig.class).getConfig();
        IdentityPlatform.setConfig(IdentityFabric.CONFIG);
        System.out.println("[Identity] Fabric config reloaded.");
    }
}
