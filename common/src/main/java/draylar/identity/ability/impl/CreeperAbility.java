package draylar.identity.ability.impl;

import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class CreeperAbility extends IdentityAbility<CreeperEntity> {

    @Override
    public void onUse(PlayerEntity player, CreeperEntity identity, World world) {
        float power = identity.shouldRenderOverlay() ? 6.0f : 3.0f;
        world.createExplosion(player, player.getX(), player.getY(), player.getZ(), power, World.ExplosionSourceType.NONE);
    }

    @Override
    public Item getIcon() {
        return Items.TNT;
    }
}
