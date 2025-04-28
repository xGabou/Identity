package draylar.identity.forge.ability.impl;

import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import draylar.identity.ability.IdentityAbility;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import static draylar.identity.util.AbilityUtils.knockbackNearbyEntities;

public class GrizzlyBearAbility extends IdentityAbility<EntityGrizzlyBear> {

    @Override
    public void onUse(PlayerEntity player, EntityGrizzlyBear identity, World world) {
        world.playSoundFromEntity(null, player, SoundEvents.ENTITY_POLAR_BEAR_WARNING, SoundCategory.HOSTILE, 2.0F, 1.0F);
        knockbackNearbyEntities(player, 4.0F, 1.0D);
    }

    @Override
    public Item getIcon() {
        return Items.HONEYCOMB;
    }


}