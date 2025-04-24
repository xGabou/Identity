package draylar.identity.util;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class IdentityCompatUtils {

    public static boolean isBlacklistedEntityType(EntityType<?> type) {
        Identifier id = Registries.ENTITY_TYPE.getId(type);

        if (id == null) return false;

        // DEBUG TEMP
        // Identity.LOGGER.info("Checking identity: " + id);

        // Blacklist le dragon de Dragon Mounts
        return id.getNamespace().equals("dragonmounts") &&
                id.getPath().equals("dragon");
    }
}
