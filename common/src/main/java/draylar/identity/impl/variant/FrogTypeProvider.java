package draylar.identity.impl.variant;

import com.google.common.collect.ImmutableMap;
import draylar.identity.api.variant.TypeProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.Map;

public class FrogTypeProvider extends TypeProvider<FrogEntity> {

    private static final Map<Integer, String> PREFIX_BY_ID = ImmutableMap
            .<Integer, String>builder()
            .put(0, "Temperate")
            .put(1, "Warm")
            .put(2, "Cold")
            .build();

    @Override
    public int getVariantData(FrogEntity entity) {
        // Récupère le FrogVariant depuis l'entry
        FrogVariant variant = entity.getVariant().value();
        return Registries.FROG_VARIANT.getRawId(variant);
    }

    @Override
    public FrogEntity create(EntityType<FrogEntity> type, World world, int data) {
        FrogEntity frog = new FrogEntity(type, world);
        // Obtenir le RegistryEntry<FrogVariant> à partir de l'id
        RegistryEntry<FrogVariant> entry = Registries.FROG_VARIANT.getEntry(data)
                .orElseThrow(() -> new IllegalArgumentException("Invalid frog variant id: " + data));
        frog.setVariant(entry);
        return frog;
    }


    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return 2;
    }

    @Override
    public Text modifyText(FrogEntity frog, MutableText text) {
        int variant = getVariantData(frog);
        return Text.literal(PREFIX_BY_ID.containsKey(variant) ? PREFIX_BY_ID.get(variant) + " " : "").append(text);
    }
}
