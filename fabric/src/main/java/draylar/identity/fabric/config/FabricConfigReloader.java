package draylar.identity.fabric.config;

import draylar.identity.api.platform.ConfigReloader;
import draylar.identity.api.platform.IdentityPlatform;
import draylar.identity.fabric.IdentityFabric;
import draylar.omegaconfig.OmegaConfig;

public class FabricConfigReloader implements ConfigReloader {
    @Override
    public void reloadConfig() {
        // Use the Forge-compatible config loader (shared class)
        IdentityFabric.CONFIG = OmegaConfig.register(IdentityFabricConfig.class);
        IdentityPlatform.setConfig(IdentityFabric.CONFIG);


        System.out.println("[Identity] Fabric config reloaded.");
    }
}
