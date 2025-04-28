package draylar.identity.forge.ability.impl;

import com.github.alexthe666.alexsmobs.entity.EntitySkunk;
import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class SkunkAbility extends IdentityAbility<EntitySkunk> {

    @Override
    public void onUse(PlayerEntity player, EntitySkunk identity, World world) {
        if (!world.isClient) {
            AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(world, player.getX(), player.getY(), player.getZ());
            cloud.setRadius(3.0F);
            cloud.setDuration(100);
            cloud.addEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 1));
            world.spawnEntity(cloud);
        }
    }

    @Override
    public Item getIcon() {
        return Items.FERMENTED_SPIDER_EYE;
    }
}