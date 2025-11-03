package draylar.identity.mixin.player;

import draylar.identity.api.PlayerUnlocks;
import draylar.identity.impl.PlayerDataProvider;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class RespawnDataCopyMixin {

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void copyIdentityData(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        PlayerDataProvider oldData = ((PlayerDataProvider) oldPlayer);
        PlayerDataProvider newData = ((PlayerDataProvider) this);

        // Transfer data from the old ServerPlayerEntity -> new ServerPlayerEntity
        newData.setAbilityCooldown(oldData.getAbilityCooldown());
        newData.setRemainingHostilityTime(oldData.getRemainingHostilityTime());
        newData.setIdentity(oldData.getIdentity());
        newData.setUnlocked(oldData.getUnlocked());
        newData.setFavorites(oldData.getFavorites());

        // Copy villager identities and active key
        newData.getVillagerIdentities().clear();
        newData.getVillagerIdentities().putAll(oldData.getVillagerIdentities());
        newData.setActiveVillagerKey(oldData.getActiveVillagerKey());

        PlayerUnlocks.sync((ServerPlayerEntity) (Object) this);
        draylar.identity.network.impl.VillagerIdentitiesPackets.sendSync((ServerPlayerEntity) (Object) this);
    }
}
