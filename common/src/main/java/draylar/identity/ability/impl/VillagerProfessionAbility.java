package draylar.identity.ability.impl;

import draylar.identity.ability.IdentityAbility;
import draylar.identity.Identity;
import draylar.identity.network.impl.VillagerProfessionPackets;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import net.minecraft.village.VillagerProfession;

import java.util.Optional;
import java.util.function.Predicate;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

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
            RegistryEntry<PointOfInterestType> targetPoi = poi.get();

            boolean found = false;
            Identifier poiId = Registries.POINT_OF_INTEREST_TYPE.getId(targetPoi.value());

            for (VillagerProfession profession : Registries.VILLAGER_PROFESSION) {
                boolean matches = false;

                // 1) Simple ID match: many mappings name POI types after the profession (e.g., minecraft:librarian)
                Identifier profIdDirect = Registries.VILLAGER_PROFESSION.getId(profession);
                if (poiId != null && poiId.equals(profIdDirect)) {
                    matches = true;
                }

                try {
                    Method held = VillagerProfession.class.getMethod("heldWorkstation");
                    Object value = held.invoke(profession);
                    if (!matches && value instanceof RegistryEntry<?> entry) {
                        matches = entry.equals(targetPoi);
                    }
                } catch (NoSuchMethodException e) {
                    try {
                        Method acquirable = VillagerProfession.class.getMethod("acquirableJobSite");
                        Object predicate = acquirable.invoke(profession);
                        if (!matches && predicate instanceof Predicate<?> raw) {
                            @SuppressWarnings("unchecked")
                            Predicate<RegistryEntry<PointOfInterestType>> p = (Predicate<RegistryEntry<PointOfInterestType>>) raw;
                            matches = p.test(targetPoi);
                        }
                    } catch (NoSuchMethodException ignored) {
                        try {
                            Method acquirable = VillagerProfession.class.getMethod("acquirableWorkstation");
                            Object predicate = acquirable.invoke(profession);
                            if (!matches && predicate instanceof Predicate<?> raw) {
                                @SuppressWarnings("unchecked")
                                Predicate<RegistryEntry<PointOfInterestType>> p = (Predicate<RegistryEntry<PointOfInterestType>>) raw;
                                matches = p.test(targetPoi);
                            }
                        } catch (NoSuchMethodException ignoredToo) {
                            // no-op; API mismatch we can't resolve here
                        } catch (IllegalAccessException | InvocationTargetException reflectError) {
                            // ignore and continue
                        }
                    } catch (IllegalAccessException | InvocationTargetException reflectError) {
                        // ignore and continue
                    }
                } catch (IllegalAccessException | InvocationTargetException reflectError) {
                    // ignore and continue
                }

                if (matches) {
                    Identifier profId = Registries.VILLAGER_PROFESSION.getId(profession);
                    // Debug/log + in-game confirmation for verification
                    ((ServerPlayerEntity) player).sendMessage(Text.literal("Identity: matched profession " + profId + " for POI " + poiId), false);
                    Identity.LOGGER.info("[Identity] VillagerProfessionAbility matched profession {} for POI {}", profId, poiId);
                    VillagerProfessionPackets.openScreen((ServerPlayerEntity) player, profId, blockResult.getBlockPos(), player.getWorld().getRegistryKey().getValue());
                    found = true;
                    break;
                }
            }
            if (!found) {
                ((ServerPlayerEntity) player).sendMessage(Text.literal("Identity: no matching profession for POI " + poiId), false);
                Identity.LOGGER.info("[Identity] VillagerProfessionAbility found no matching profession for POI {}", poiId);
            }
        } else {
            ((ServerPlayerEntity) player).sendMessage(Text.literal("Identity: not targeting a workstation block"), false);
        }
    }

    @Override
    public Item getIcon() {
        return Items.EMERALD;
    }
}
