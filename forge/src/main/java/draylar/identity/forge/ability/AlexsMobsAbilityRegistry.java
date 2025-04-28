package draylar.identity.forge.ability;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import draylar.identity.ability.IdentityAbility;
import draylar.identity.forge.ability.impl.*;
import draylar.identity.util.IdentityCompatUtils;
//import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class AlexsMobsAbilityRegistry {

    private static final Map<EntityType<?>, IdentityAbility<?>> abilities = new HashMap<>();

    private AlexsMobsAbilityRegistry() {}

    public static void init() {
        if (!IdentityCompatUtils.isAlexsMobsLoaded()) {
            return;
        }

        register(AMEntityRegistry.GRIZZLY_BEAR, new GrizzlyBearAbility());
        register(AMEntityRegistry.GUSTER, new GusterAbility());
        register(AMEntityRegistry.DROPBEAR, new DropbearAbility());
        register(AMEntityRegistry.SUNBIRD, new SunbirdAbility());
        register(AMEntityRegistry.VOID_WORM, new VoidWormAbility());
        register(AMEntityRegistry.KOMODO_DRAGON, new KomodoDragonAbility());
        register(AMEntityRegistry.SKUNK, new SkunkAbility());
    }

    public static void register(RegistryObject<?> type, IdentityAbility<?> ability) {
        abilities.put((EntityType<?>) type.get(), ability);
    }

    public static IdentityAbility<?> get(EntityType<?> type) {
        return abilities.get(type);
    }

    public static boolean has(EntityType<?> type) {
        return abilities.containsKey(type);
    }
}
