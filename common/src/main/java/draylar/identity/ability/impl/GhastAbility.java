package draylar.identity.ability.impl;

import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GhastAbility extends IdentityAbility<GhastEntity> {

    @Override
    public void onUse(PlayerEntity player, GhastEntity identity, World world) {
        Vec3d look = player.getRotationVec(1.0F); // direction du regard
        SmallFireballEntity fireball = new SmallFireballEntity(
                world,
                player,
                look.multiply(0.5) // optionnel : vitesse (ici réduit pour être proche du vanilla blaze)
        );

        fireball.refreshPositionAndAngles(fireball.getX(), fireball.getY() + 1.75, fireball.getZ(), fireball.getYaw(), fireball.getPitch());
        fireball.updatePosition(fireball.getX(), fireball.getY(), fireball.getZ());
        world.spawnEntity(fireball);
        world.playSoundFromEntity(null, player, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
        world.playSoundFromEntity(null, player, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.FIRE_CHARGE;
    }
}
