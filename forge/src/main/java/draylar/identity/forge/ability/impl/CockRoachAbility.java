package draylar.identity.forge.ability.impl;

import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import draylar.identity.ability.IdentityAbility;
import draylar.identity.forge.compat.accessor.ForceDanceAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class CockRoachAbility extends IdentityAbility<EntityCockroach> {
    @Override
    public void onUse(PlayerEntity player, EntityCockroach identity, World world) {
        if (!world.isClient) {
            if (identity instanceof ForceDanceAccessor accessor) {
                accessor.identity$startForceDance();
            }

        }
    }


    @Override
    public Item getIcon() {
        return Items.MUSIC_DISC_CAT;
    }
}

