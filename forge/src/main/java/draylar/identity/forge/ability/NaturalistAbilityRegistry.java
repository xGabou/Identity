package draylar.identity.forge.ability;

import draylar.identity.ability.AbilityRegistry;
import draylar.identity.forge.ability.impl.BearAbility;
import draylar.identity.util.IdentityCompatUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class NaturalistAbilityRegistry {

    private NaturalistAbilityRegistry() {}

    public static void init() {
        if (!IdentityCompatUtils.isNaturalistLoaded()) {
            return;
        }

        EntityType<?> bear = Registries.ENTITY_TYPE.get(new Identifier("naturalist", "bear"));
        if (bear != null) {
            AbilityRegistry.register(bear, new BearAbility());
        }
    }
}
