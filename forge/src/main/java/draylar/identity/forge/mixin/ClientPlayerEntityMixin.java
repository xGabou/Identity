package draylar.identity.forge.mixin;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.mojang.authlib.GameProfile;
import draylar.identity.api.PlayerIdentity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Listens for entity‐status bytes 67 (start) and 68 (stop)
 * on the CLIENT’s player, then runs your debugForceDance logic.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    @Unique
    private boolean identity$forceDanceActive = false;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }


    // This is the method that gets called when the server sends a status byte to the client.
    @Inject(method = "handleStatus", at = @At("HEAD"))
    private void identity$onHandleStatus(byte status, CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        if (PlayerIdentity.getIdentity(player) instanceof EntityCockroach cockroach) {
            if (status == 67) {
                identity$forceDanceActive = true;
            } else if (status == 68) {


                identity$forceDanceActive = false;
                cockroach.setMaracas(false);
                cockroach.setNearestMusician(null);


            } else if (status == 69) {
                cockroach.setMaracas(true);
                AlexsMobs.PROXY.onEntityStatus(cockroach, (byte) 67);
                    for(EntityCockroach roach : this.getWorld().getEntitiesByClass(EntityCockroach.class, identity$getMusicianDistance(), EntityPredicates.EXCEPT_SPECTATOR)) {
                        if (!roach.hasMaracas()) {
                            roach.setNearestMusician(this.getUuid());
                        }
                    }


            }
        }
    }
    @Unique
    private Box identity$getMusicianDistance() {
        return this.getBoundingBox().expand((double)10.0F, (double)10.0F, (double)10.0F);
    }

    // Once per tick, if armed, do the dance and consume the flag
    @Inject(method = "tick", at = @At("HEAD"))
    private void identity$tickDance(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        var morph = PlayerIdentity.getIdentity(player);
        if (!(morph instanceof EntityCockroach cockroach)) {
            return;
        }

        boolean dancing = identity$forceDanceActive;

        // If we're neither dancing nor fading out, skip entirely:
        if (!dancing && cockroach.danceProgress == 0) {
            cockroach.setDancing(false);
            cockroach.setNearbySongPlaying(player.getBlockPos(), false);
//            identity$hasMaracas = false;
        } else {
            // 1) flip the flags so vanilla code knows we're dancing:
            cockroach.setDancing(dancing);
            cockroach.setNearbySongPlaying(player.getBlockPos(), dancing);
        }
        if (cockroach.hasMaracas())
            cockroach.setNearestMusician(player.getUuid());
        else {
            cockroach.setNearestMusician(null);
            cockroach.setMaracas(false);
            identity$tellOtherCockroachesToStopDancing(cockroach,player);
        }
        // 2) now *run the real EntityCockroach.tick()* so it plays out
        //    its own dance logic exactly as in Alex's Mobs.
        cockroach.tick();
    }

    @Unique
    private void identity$tellOtherCockroachesToStopDancing(EntityCockroach cockroach,ClientPlayerEntity player) {
        for(EntityCockroach roach : this.getWorld().getEntitiesByClass(EntityCockroach.class, identity$getMusicianDistance(), EntityPredicates.EXCEPT_SPECTATOR)) {
            if (roach != cockroach && roach.hasMaracas() && roach.getNearestMusician().getUuid() == player.getUuid()) {
                roach.setMaracas(false);
                roach.setDancing(false);
                roach.setNearbySongPlaying(player.getBlockPos(), false);
            }
        }
    }
}
