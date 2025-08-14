package draylar.identity.registry;

import draylar.identity.Identity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class IdentityEntityTags {

    public static final TagKey<EntityType<?>> BURNS_IN_DAYLIGHT = register("burns_in_daylight");
    public static final TagKey<EntityType<?>> FLYING = register("flying");
    public static final TagKey<EntityType<?>> SLOW_FALLING = register("slow_falling");
    public static final TagKey<EntityType<?>> WOLF_PREY = register("wolf_prey");
    public static final TagKey<EntityType<?>> FOX_PREY = register("fox_prey");
    public static final TagKey<EntityType<?>> BREATHE_UNDERWATER = register("breathe_underwater");
    public static final TagKey<EntityType<?>> HURT_BY_HIGH_TEMPERATURE = register("hurt_by_high_temperature");
    public static final TagKey<EntityType<?>> RAVAGER_RIDING = register("ravager_riding");
    public static final TagKey<EntityType<?>> PIGLIN_FRIENDLY = register("piglin_friendly");
    public static final TagKey<EntityType<?>> LAVA_WALKING = register("lava_walking");
    public static final TagKey<EntityType<?>> CANT_SWIM = register("cant_swim");
    public static final TagKey<EntityType<?>> UNDROWNABLE = register("undrownable");
    public static final TagKey<EntityType<?>> CUSTOM_FLYING = register("custom_flying");
    public static final TagKey<EntityType<?>> CUSTOM_BREATHE_UNDERWATER = register("custom_breathe_underwater");
    public static final TagKey<EntityType<?>> CUSTOM_FIRE_IMMUNE = register("custom_fire_immune");
    public static final TagKey<EntityType<?>> CUSTOM_SLOW_FALLING = register("custom_slow_falling");
    public static final TagKey<EntityType<?>> CUSTOM_BURNS_IN_DAYLIGHT = register("custom_burns_in_daylight");
    public static final TagKey<EntityType<?>> CUSTOM_CANT_SWIM = register("custom_cant_swim");
    public static final TagKey<EntityType<?>> CUSTOM_HURT_BY_HEAT = register("custom_hurt_by_high_temperature");
    public static final TagKey<EntityType<?>> CUSTOM_LAVA_WALKING = register("custom_lava_walking");
    public static final TagKey<EntityType<?>> CUSTOM_PIGLIN_FRIENDLY = register("custom_piglin_friendly");
    public static final TagKey<EntityType<?>> CUSTOM_RAVAGER_RIDING = register("custom_ravager_riding");
    public static final TagKey<EntityType<?>> CUSTOM_UNDROWNABLE = register("custom_undrownable");
    public static final TagKey<EntityType<?>> CUSTOM_WOLF_PREY = register("custom_wolf_prey");
    public static final TagKey<EntityType<?>> CUSTOM_FOX_PREY = register("custom_fox_prey");



    private IdentityEntityTags() { }

    public static void init() {
        // NO-OP
    }

    private static TagKey<EntityType<?>> register(String id) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, Identity.id(id));
    }
}
