package draylar.identity.forge.ability.impl;

import com.github.alexthe666.alexsmobs.entity.EntityKomodoDragon;
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

public class KomodoDragonAbility extends IdentityAbility<EntityKomodoDragon> {

    @Override
    public void onUse(PlayerEntity player, EntityKomodoDragon identity, World world) {
        if (!world.isClient) {
            EntityHitResult hit = AbilityUtils.raycastEntities(player, 3.0D);
            if (hit != null && hit.getEntity() instanceof LivingEntity target) {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 1));
            }
        }
    }


    @Override
    public Item getIcon() {
        return Items.SPIDER_EYE;
    }
}