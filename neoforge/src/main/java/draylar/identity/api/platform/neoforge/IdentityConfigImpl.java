package draylar.identity.api.platform.neoforge;

import draylar.identity.neoforge.IdentityNeoForge;
import draylar.identity.api.platform.IdentityConfig;

public class IdentityConfigImpl {

    public static IdentityConfig getInstance() {
        return IdentityNeoForge.CONFIG;
    }
}
