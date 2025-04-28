package draylar.identity.forge.ability.impl;

import com.github.alexthe666.alexsmobs.entity.EntityDropBear;
import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class DropbearAbility extends IdentityAbility<EntityDropBear> {

    @Override
    public void onUse(PlayerEntity player, EntityDropBear identity, World world) {
        player.addVelocity(0, -1.0D, 0);
    }

    @Override
    public Item getIcon() {
        return Items.LEATHER;
    }
}