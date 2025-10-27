package draylar.identity.forge.config;

import draylar.identity.api.platform.ConfigReloader;
import draylar.identity.api.platform.IdentityPlatform;
import draylar.identity.forge.IdentityForge;

public class ForgeConfigReloader implements ConfigReloader {
    @Override
    public void reloadConfig() {
        IdentityForge.CONFIG = ConfigLoader.read();
        IdentityPlatform.setConfig(IdentityForge.CONFIG);

        System.out.println("[Identity] Forge config reloaded.");
    }

    @Override
    public void saveConfig() {
        ConfigLoader.save(IdentityForge.CONFIG);
        IdentityPlatform.setConfig(IdentityForge.CONFIG);
    }
}
