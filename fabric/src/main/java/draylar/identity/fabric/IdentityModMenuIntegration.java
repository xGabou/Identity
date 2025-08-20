package draylar.identity.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import draylar.identity.fabric.config.IdentityFabricConfig;
import draylar.omegaconfig.OmegaConfig;

public class IdentityModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> OmegaConfig.getConfigScreen(IdentityFabricConfig.class, parent);
    }
}

