package draylar.identity.mixin;

import draylar.identity.api.PlayerIdentity;
import draylar.identity.impl.PlayerDataProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.village.TradeOffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {

    @Shadow protected abstract void sayNo();

    @Inject(
            method = "interactMob",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        LivingEntity identity = PlayerIdentity.getIdentity(player);

        if(identity != null && identity.getType() == EntityType.ZOMBIE_VILLAGER) {
            this.sayNo();
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Inject(method = "afterUsing", at = @At("TAIL"))
    private void onTrade(TradeOffer offer, CallbackInfo ci) {
        VillagerEntity villager = (VillagerEntity) (Object) this;
        ServerPlayerEntity owner = null;

        for (ServerPlayerEntity player : villager.getServer().getPlayerManager().getPlayerList()) {
            if (PlayerIdentity.getIdentity(player) == villager) {
                owner = player;
                break;
            }
        }

        if (owner != null) {
            NbtCompound tag = new NbtCompound();
            villager.writeNbt(tag);
            tag.putString("ProfessionId", Registries.VILLAGER_PROFESSION.getId(villager.getVillagerData().getProfession()).toString());
            PlayerDataProvider data = (PlayerDataProvider) owner;
            data.getVillagerIdentities().forEach((name, nbt) -> {
                if (nbt.getString("ProfessionId").equals(tag.getString("ProfessionId"))) {
                    data.setVillagerIdentity(name, tag);
                }
            });
            PlayerIdentity.sync(owner);
        }
    }
}
