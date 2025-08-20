package draylar.identity.fabric.config;

import draylar.identity.api.platform.ConfigReloader;
import draylar.identity.api.platform.IdentityPlatform;
import draylar.identity.fabric.IdentityFabric;
import me.shedaniel.autoconfig.AutoConfig;

public class FabricConfigReloader implements ConfigReloader {
    @Override
    public void reloadConfig() {
        // Use the Forge-compatible config loader (shared class)
        AutoConfig.getConfigHolder(IdentityFabricConfig.class).load();
        IdentityFabric.CONFIG = AutoConfig.getConfigHolder(IdentityFabricConfig.class).getConfig();


        System.out.println("[Identity] Fabric config reloaded.");
    }
}
