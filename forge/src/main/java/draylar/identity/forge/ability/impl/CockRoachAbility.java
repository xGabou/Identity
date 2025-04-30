package draylar.identity.forge.ability.impl;

import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import draylar.identity.ability.IdentityAbility;
import draylar.identity.forge.compat.accessor.ForceDanceAccessor;
import draylar.identity.forge.network.ForceDancePacket;
import draylar.identity.forge.network.NetworkHandler;

import draylar.identity.forge.util.CockroachDanceManager;
import draylar.identity.forge.util.CockroachDanceUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class CockRoachAbility extends IdentityAbility<EntityCockroach> {
    @Override
    public void onUse(PlayerEntity player, EntityCockroach identity, World world) {
        if (!world.isClient) {
            CockroachDanceManager.forceDance(player, 200);
        }
    }




    @Override
    public Item getIcon() {
        return Items.MUSIC_DISC_CAT;
    }
}

