package draylar.identity.api;

import draylar.identity.registry.IdentityEntityTags;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.registry.entry.RegistryEntryList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SafeTagManager {

    private static final Set<Identifier> CUSTOM_FLYING_ENTITIES = new HashSet<>();
    private static final Set<Identifier> CUSTOM_BREATHE_UNDERWATER_ENTITIES = new HashSet<>();
    private static final Set<Identifier> CUSTOM_FIRE_IMMUNE_ENTITIES = new HashSet<>();
    private static final Set<Identifier> CUSTOM_SLOW_FALLING = new HashSet<>();
    private static final Set<Identifier> CUSTOM_BURNS_IN_DAYLIGHT = new HashSet<>();
    private static final Set<Identifier> CUSTOM_CANT_SWIM = new HashSet<>();
    private static final Set<Identifier> CUSTOM_HURT_BY_HEAT = new HashSet<>();
    private static final Set<Identifier> CUSTOM_LAVA_WALKING = new HashSet<>();
    private static final Set<Identifier> CUSTOM_PIGLIN_FRIENDLY = new HashSet<>();
    private static final Set<Identifier> CUSTOM_RAVAGER_RIDING = new HashSet<>();
    private static final Set<Identifier> CUSTOM_UNDROWNABLE = new HashSet<>();
    private static final Set<Identifier> CUSTOM_WOLF_PREY = new HashSet<>();
    private static final Set<Identifier> CUSTOM_FOX_PREY = new HashSet<>();


    public static void loadAll(MinecraftServer server) {
        var entityTypeRegistry = server.getRegistryManager().get(RegistryKeys.ENTITY_TYPE);

        // Load all custom sets
        loadTagSafely(entityTypeRegistry.getEntryList(IdentityEntityTags.CUSTOM_FLYING), CUSTOM_FLYING_ENTITIES, "custom_flying");
        loadTagSafely(entityTypeRegistry.getEntryList(IdentityEntityTags.CUSTOM_BREATHE_UNDERWATER), CUSTOM_BREATHE_UNDERWATER_ENTITIES, "custom_breathe_underwater");
        loadTagSafely(entityTypeRegistry.getEntryList(IdentityEntityTags.CUSTOM_FIRE_IMMUNE), CUSTOM_FIRE_IMMUNE_ENTITIES, "custom_fire_immune");
        loadTagSafely(entityTypeRegistry.getEntryList(IdentityEntityTags.CUSTOM_SLOW_FALLING), CUSTOM_SLOW_FALLING, "custom_slow_falling");
        loadTagSafely(entityTypeRegistry.getEntryList(IdentityEntityTags.CUSTOM_BURNS_IN_DAYLIGHT), CUSTOM_BURNS_IN_DAYLIGHT, "custom_burns_in_daylight");
        loadTagSafely(entityTypeRegistry.getEntryList(IdentityEntityTags.CUSTOM_CANT_SWIM), CUSTOM_CANT_SWIM, "custom_cant_swim");
        loadTagSafely(entityTypeRegistry.getEntryList(IdentityEntityTags.CUSTOM_HURT_BY_HEAT), CUSTOM_HURT_BY_HEAT, "custom_hurt_by_high_temperature");
        loadTagSafely(entityTypeRegistry.getEntryList(IdentityEntityTags.CUSTOM_LAVA_WALKING), CUSTOM_LAVA_WALKING, "custom_lava_walking");
        loadTagSafely(entityTypeRegistry.getEntryList(IdentityEntityTags.CUSTOM_PIGLIN_FRIENDLY), CUSTOM_PIGLIN_FRIENDLY, "custom_piglin_friendly");
        loadTagSafely(entityTypeRegistry.getEntryList(IdentityEntityTags.CUSTOM_RAVAGER_RIDING), CUSTOM_RAVAGER_RIDING, "custom_ravager_riding");
        loadTagSafely(entityTypeRegistry.getEntryList(IdentityEntityTags.CUSTOM_UNDROWNABLE), CUSTOM_UNDROWNABLE, "custom_undrownable");
        loadTagSafely(entityTypeRegistry.getEntryList(IdentityEntityTags.CUSTOM_WOLF_PREY), CUSTOM_WOLF_PREY, "custom_wolf_prey");
        loadTagSafely(entityTypeRegistry.getEntryList(IdentityEntityTags.CUSTOM_FOX_PREY), CUSTOM_FOX_PREY, "custom_fox_prey");

    }

    private static void loadTagSafely(Optional<RegistryEntryList.Named<EntityType<?>>> tagListOpt, Set<Identifier> targetSet, String tagName) {
        targetSet.clear();

        if (tagListOpt.isPresent()) {
            for (RegistryEntry<EntityType<?>> entry : tagListOpt.get()) {
                Identifier id = EntityType.getId(entry.value());
                if (id != null) {
                    targetSet.add(id);
                } else {
                    System.out.println("[Identity] Skipping missing entity in " + tagName);
                }
            }
            System.out.println("[Identity] Loaded " + targetSet.size() + " entries into " + tagName);
        } else {
            System.out.println("[Identity] Warning: Tag not found: " + tagName);
        }
    }

    // --- API for checking if an entity matches ---

    public static boolean isCustomFlying(EntityType<?> type) {
        return CUSTOM_FLYING_ENTITIES.contains(EntityType.getId(type));
    }

    public static boolean isCustomBreatheUnderwater(EntityType<?> type) {
        return CUSTOM_BREATHE_UNDERWATER_ENTITIES.contains(EntityType.getId(type));
    }

    public static boolean isCustomFireImmune(EntityType<?> type) {
        return CUSTOM_FIRE_IMMUNE_ENTITIES.contains(EntityType.getId(type));
    }
    public static boolean isCustomSlowFalling(EntityType<?> type) {
        return CUSTOM_SLOW_FALLING.contains(EntityType.getId(type));
    }

    public static boolean isCustomBurnsInDaylight(EntityType<?> type) {
        return CUSTOM_BURNS_IN_DAYLIGHT.contains(EntityType.getId(type));
    }

    public static boolean isCustomCantSwim(EntityType<?> type) {
        return CUSTOM_CANT_SWIM.contains(EntityType.getId(type));
    }

    public static boolean isCustomHurtByHeat(EntityType<?> type) {
        return CUSTOM_HURT_BY_HEAT.contains(EntityType.getId(type));
    }

    public static boolean isCustomLavaWalking(EntityType<?> type) {
        return CUSTOM_LAVA_WALKING.contains(EntityType.getId(type));
    }

    public static boolean isCustomPiglinFriendly(EntityType<?> type) {
        return CUSTOM_PIGLIN_FRIENDLY.contains(EntityType.getId(type));
    }

    public static boolean isCustomRavagerRiding(EntityType<?> type) {
        return CUSTOM_RAVAGER_RIDING.contains(EntityType.getId(type));
    }

    public static boolean isCustomUndrownable(EntityType<?> type) {
        return CUSTOM_UNDROWNABLE.contains(EntityType.getId(type));
    }

    public static boolean isCustomWolfPrey(EntityType<?> type) {
        return CUSTOM_WOLF_PREY.contains(EntityType.getId(type));
    }

    public static boolean isCustomFoxPrey(EntityType<?> type) {
        return CUSTOM_FOX_PREY.contains(EntityType.getId(type));
    }

}
