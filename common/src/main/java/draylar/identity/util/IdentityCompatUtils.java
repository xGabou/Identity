package draylar.identity.util;
import dev.architectury.platform.Platform;
import draylar.identity.Identity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class IdentityCompatUtils {

    private static final Set<Identifier> INCOMPATIBLE_TYPES = new HashSet<>();

    public static boolean isBlacklistedEntityType(EntityType<?> type) {
        Identifier id = Registries.ENTITY_TYPE.getId(type);

        if (INCOMPATIBLE_TYPES.contains(id)) {
            return true;
        }

        // Blacklist le dragon de Dragon Mounts
        return id.getNamespace().equals("dragonmounts") && id.getPath().equals("dragon");
    }

    public static void markIncompatibleEntityType(EntityType<?> type) {
        Identifier id = Registries.ENTITY_TYPE.getId(type);
        INCOMPATIBLE_TYPES.add(id);
        Identity.LOGGER.warn("Marked incompatible identity {}", id);
    }
    public static boolean isAlexsMobsLoaded() {
//        for(var d:Platform.getMods())
//        {
//            Identity.LOGGER.info(d.getName()+" "+d.getModId());
//        }
        return Platform.isModLoaded("alexsmobs");
    }
    public static boolean isNaturalistLoaded() {
        return Platform.isModLoaded("naturalist");
    }
}