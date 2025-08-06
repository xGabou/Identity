package draylar.identity.forge.ability.impl;

import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class BearAbility extends IdentityAbility<LivingEntity> {
    @Override
    public void onUse(PlayerEntity player, LivingEntity identity, World world) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 200, 1));
    }

    @Override
    public Item getIcon() {
        return Items.HONEYCOMB;
    }
}
