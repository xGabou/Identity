package draylar.identity.forge.ability.impl;

import com.github.alexthe666.alexsmobs.entity.EntityGuster;
import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import static draylar.identity.util.AbilityUtils.raycastEntities;

public class GusterAbility extends IdentityAbility<EntityGuster> {

    @Override
    public void onUse(PlayerEntity player, EntityGuster identity, World world) {
        if (!world.isClient) {
            EntityHitResult entityHitResult = raycastEntities(player, 8.0D);
            if (entityHitResult != null && entityHitResult.getEntity() != null) {
                entityHitResult.getEntity().addVelocity(
                    player.getRotationVector().x * 2.0D,
                    0.5D,
                    player.getRotationVector().z * 2.0D
                );
            }
        }
    }

    @Override
    public Item getIcon() {
        return Items.FEATHER;
    }
}