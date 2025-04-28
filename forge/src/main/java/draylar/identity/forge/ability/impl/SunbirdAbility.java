package draylar.identity.forge.ability.impl;

import com.github.alexthe666.alexsmobs.entity.EntitySunbird;
import draylar.identity.ability.IdentityAbility;
import draylar.identity.util.AbilityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import static draylar.identity.util.AbilityUtils.raycastEntities;

public class SunbirdAbility extends IdentityAbility<EntitySunbird> {

    @Override
    public void onUse(PlayerEntity player, EntitySunbird identity, World world) {
        if (!world.isClient) {
            EntityHitResult entityHitResult = AbilityUtils.raycastEntities(player, 6.0D);
            if (entityHitResult != null && entityHitResult.getEntity() instanceof LivingEntity target) {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 60, 1));
            }
        }
    }


    @Override
    public Item getIcon() {
        return Items.SUNFLOWER;
    }
}