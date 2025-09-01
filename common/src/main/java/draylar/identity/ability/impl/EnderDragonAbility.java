package draylar.identity.ability.impl;

import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EnderDragonAbility extends IdentityAbility<EnderDragonEntity> {

    @Override
    public void onUse(PlayerEntity player, EnderDragonEntity identity, World world) {
        Vec3d look = player.getRotationVec(1.0F);

        DragonFireballEntity dragonFireball = new DragonFireballEntity(
                world,
                player,        // owner
                look.multiply(0.5) // vitesse, vanilla dragon crache lentement
        );
        dragonFireball.setOwner(player);
        world.spawnEntity(dragonFireball);
    }

    @Override
    public Item getIcon() {
        return Items.DRAGON_BREATH;
    }
}
