package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(WitherEntity.class)
public abstract class WitherEntityMixin extends HostileEntity {

    private WitherEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(
            method = "mobTick",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/World;getTargets(Ljava/lang/Class;Lnet/minecraft/entity/ai/TargetPredicate;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/math/Box;)Ljava/util/List;"
            )
    )
    private List<LivingEntity> identity$filterTargets(List<LivingEntity> list) {
        list.removeIf(entity -> {
            if (entity instanceof PlayerEntity player) {
                LivingEntity identity = PlayerIdentity.getIdentity(player);
                return identity != null && identity.getType().isIn(EntityTypeTags.UNDEAD);
            }
            return false;
        });
        return list;
    }




}

