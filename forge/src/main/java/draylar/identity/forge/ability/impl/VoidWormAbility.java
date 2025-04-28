package draylar.identity.forge.ability.impl;

import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class VoidWormAbility extends IdentityAbility<EntityVoidWorm> {

    @Override
    public void onUse(PlayerEntity player, EntityVoidWorm identity, World world) {
        Vec3d look = player.getRotationVec(1.0F).multiply(5.0D);
        player.requestTeleport(player.getX() + look.x, player.getY() + look.y, player.getZ() + look.z);
    }

    @Override
    public Item getIcon() {
        return Items.ENDER_PEARL;
    }
}