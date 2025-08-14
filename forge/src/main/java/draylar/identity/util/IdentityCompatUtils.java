package draylar.identity.util;

import dev.architectury.platform.Platform;
import draylar.identity.Identity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class IdentityCompatUtils {

    public static boolean isBlacklistedEntityType(EntityType<?> type) {
        Identifier id = Registries.ENTITY_TYPE.getId(type);

        // DEBUG TEMP
        // Identity.LOGGER.info("Checking identity: " + id);

        // Blacklist le dragon de Dragon Mounts
        return id.getNamespace().equals("dragonmounts") &&
                id.getPath().equals("dragon");
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
