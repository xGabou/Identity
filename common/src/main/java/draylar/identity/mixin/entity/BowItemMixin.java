package draylar.identity.mixin.entity;

import draylar.identity.api.PlayerIdentity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(RangedWeaponItem.class)
public abstract class BowItemMixin {
    @ModifyVariable(
            method = "shootAll",
            at = @At("STORE"),
            ordinal = 0 // correspond à la variable ProjectileEntity projectile
    )
    private ProjectileEntity flameArrows(ProjectileEntity arrow,
                                         ServerWorld world,
                                         LivingEntity shooter,
                                         Hand hand,
                                         ItemStack stack,
                                         List<ItemStack> projectiles,
                                         float speed,
                                         float divergence,
                                         boolean critical,
                                         @Nullable LivingEntity target) {
        if (shooter instanceof PlayerEntity player) {
            LivingEntity identity = PlayerIdentity.getIdentity(player);
            if (identity instanceof WitherSkeletonEntity && arrow instanceof PersistentProjectileEntity proj) {
                proj.setOnFireFor(100);
            }
        }
        return arrow;
    }
}

