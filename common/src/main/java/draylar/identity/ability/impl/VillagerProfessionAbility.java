package draylar.identity.ability.impl;

import draylar.identity.ability.IdentityAbility;
import draylar.identity.network.impl.VillagerProfessionPackets;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import net.minecraft.village.VillagerProfession;

import java.util.Optional;

public class VillagerProfessionAbility extends IdentityAbility<VillagerEntity> {

    @Override
    public void onUse(PlayerEntity player, VillagerEntity identity, World world) {
        if (world.isClient) {
            return;
        }

        HitResult result = player.raycast(5.0, 0.0F, false);
        if (result.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockResult = (BlockHitResult) result;
        Optional<RegistryEntry<PointOfInterestType>> poi = PointOfInterestTypes.getTypeForState(world.getBlockState(blockResult.getBlockPos()));
        if (poi.isPresent()) {
            for (RegistryEntry<VillagerProfession> profession : Registries.VILLAGER_PROFESSION.getEntries()) {
                if (profession.value().acquirableJobSite().test(poi.get())) {
                    VillagerProfessionPackets.openScreen((ServerPlayerEntity) player, profession.getKey().get().getValue());
                    break;
                }
            }
        }
    }

    @Override
    public Item getIcon() {
        return Items.EMERALD;
    }
}
