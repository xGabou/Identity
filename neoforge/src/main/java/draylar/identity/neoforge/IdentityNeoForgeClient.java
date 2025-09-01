package draylar.identity.neoforge;

import draylar.identity.IdentityClient;
import draylar.identity.client.NeoForgeClientEvents;
import net.neoforged.neoforge.common.NeoForge;

public class IdentityNeoForgeClient {

    public IdentityNeoForgeClient() {
        new IdentityClient().initialize();
        NeoForge.EVENT_BUS.addListener(NeoForgeClientEvents::onRenderOverlay);
    }
}
