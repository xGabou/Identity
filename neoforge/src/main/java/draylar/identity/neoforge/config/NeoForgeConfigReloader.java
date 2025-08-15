package draylar.identity.neoforge.config;

import draylar.identity.api.platform.ConfigReloader;
import draylar.identity.api.platform.IdentityPlatform;
import draylar.identity.neoforge.IdentityNeoForge;

public class NeoForgeConfigReloader implements ConfigReloader {
    @Override
    public void reloadConfig() {
        IdentityNeoForge.CONFIG = ConfigLoader.read();
        IdentityPlatform.setConfig(IdentityNeoForge.CONFIG);

        System.out.println("[Identity] NeoForge config reloaded.");
    }
}
