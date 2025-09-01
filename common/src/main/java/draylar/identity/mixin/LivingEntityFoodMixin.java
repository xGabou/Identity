package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Lazy;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFoodMixin extends Entity {

    @Unique
    private static final Lazy<List<FoodComponent>> WOLVES_IGNORE = new Lazy<>(() -> Arrays.asList(FoodComponents.CHICKEN, FoodComponents.PUFFERFISH, FoodComponents.ROTTEN_FLESH));

    public LivingEntityFoodMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "applyFoodEffects",
            at = @At("HEAD"),
            cancellable = true
    )
    private void removeFleshHungerForWolves(FoodComponent component, CallbackInfo ci) {
        if ((LivingEntity)(Object)this instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);

            // If this player is a Wolf and the food component is one they should ignore, cancel entirely
            if (identity instanceof WolfEntity) {
                if (WOLVES_IGNORE.get().contains(component)) {
                    ci.cancel();
                }
            }
        }
    }

}
