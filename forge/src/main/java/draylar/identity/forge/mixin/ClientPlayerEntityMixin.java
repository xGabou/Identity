package draylar.identity.forge.mixin;

import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.mojang.authlib.GameProfile;
import draylar.identity.api.PlayerIdentity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Listens for entity‐status bytes 67 (start) and 68 (stop)
 * on the CLIENT’s player, then runs your debugForceDance logic.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity{

    @Unique private boolean identity$forceDanceActive = false;

    @Unique private boolean identity$prevStand = false;

    @Unique private int identity$laCucarachaTimer = 0;
    @Unique private boolean identity$hasMaracas = false;
    @Unique private UUID identity$nearestMusicianId = null;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }


    // 67 = start dance, 68 = stop dance
    @Inject(method="handleStatus", at=@At("HEAD"))
    private void identity$onHandleStatus(byte status, CallbackInfo ci) {
        if (status == 67) {
            identity$forceDanceActive = true;
        } else if (status == 68) {
            identity$forceDanceActive = false;
            identity$hasMaracas= false;
        } else if (status==69) {
            identity$hasMaracas= true;
        }
    }

    // Once per tick, if armed, do the dance and consume the flag
    @Inject(method="tick", at=@At("HEAD"))
    private void identity$tickDance(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity)(Object)this;
        var morph = PlayerIdentity.getIdentity(player);
        if (!(morph instanceof EntityCockroach cockroach)) {
            return;
        }
//
//        if (!this.identity$hasMaracas()) {
//            Entity musician = this.identity$getNearestMusician();
//            if (musician != null) {
//                if (musician.isAlive() && !(this.distanceTo(musician) > 10.0F) && (!(musician instanceof EntityCockroach) || ((EntityCockroach)musician).hasMaracas())) {
//                    identity$forceDanceActive = true;
//                    cockroach.setNearestMusician(musician.getUuid());
//                } else {
//                    this.identity$setNearestMusician(null);
//                    identity$forceDanceActive = false;
//                    identity$hasMaracas = false;
//                }
//            }
//        }
//
//        if (this.identity$hasMaracas()) {
//            ++this.identity$laCucarachaTimer;
//            if (this.identity$laCucarachaTimer % 20 == 0 && this.random.nextFloat() < 0.3F) {
//                identity$tellOthersImPlayingLaCucaracha();
//
//            }
//
//            identity$forceDanceActive = true;
//            if (!this.isSilent()) {
//                this.getWorld().sendEntityStatus(cockroach, (byte)67);
//
//            }
//        }

        boolean dancing = identity$forceDanceActive;

        // If we're neither dancing nor fading out, skip entirely:
        if (!dancing && cockroach.danceProgress == 0) {
            cockroach.setDancing(false);
            cockroach.setNearbySongPlaying(player.getBlockPos(), false);
//            identity$hasMaracas = false;
        }
        else {
            // 1) flip the flags so vanilla code knows we're dancing:
            cockroach.setDancing(dancing);
            cockroach.setNearbySongPlaying(player.getBlockPos(), dancing);
        }
        if(identity$hasMaracas)
            cockroach.setNearestMusician( player.getUuid());
        else cockroach.setNearestMusician(null);
        // 2) now *run the real EntityCockroach.tick()* so it plays out
        //    its own dance logic exactly as in Alex's Mobs.
        cockroach.tick();



//        // 1) If the server has turned dancing off, fade out:
//        if (!dancing) {
//            if (cockroach.danceProgress > 0.0F) {
//                // still fading
//                cockroach.prevDanceProgress = cockroach.danceProgress;
//                cockroach.danceProgress--;
//
//                // wing-flap during fade
//                if (!cockroach.isOnGround() || player.clientWorld.random.nextInt(200) == 0) {
//                    cockroach.randomWingFlapTick = 5 + player.clientWorld.random.nextInt(15);
//                }
//                if (cockroach.randomWingFlapTick > 0) {
//                    cockroach.randomWingFlapTick--;
//                }
//
//                return; // don’t run the “start” logic
//            }
//
//            // fully faded out -> clear flags once
//            cockroach.setDancing(false);
//            cockroach.setNearbySongPlaying(cockroach.getBlockPos(), false);
//            identity$prevStand = false;
//            return;
//        }
//
//        // 2) Otherwise, dancing==true -> run your “start dance” animation
//
//        // store last progress
//        cockroach.prevDanceProgress = cockroach.danceProgress;
//
//        // ramp up to full dance
//        if (cockroach.danceProgress < 5.0F) {
//            cockroach.danceProgress++;
//        }
//
//        // wing-flap randomness
//        if (!cockroach.isOnGround() || player.clientWorld.random.nextInt(200) == 0) {
//            cockroach.randomWingFlapTick = 5 + player.clientWorld.random.nextInt(15);
//        }
//        if (cockroach.randomWingFlapTick > 0) {
//            cockroach.randomWingFlapTick--;
//        }
//
//        // on toggle, recalc dimensions
//        if (identity$prevStand != dancing) {
//            if (cockroach.hasMaracas()) {
//                identity$tellOthersImPlayingLaCucaracha();
//            }
//            cockroach.calculateDimensions();
//        }
//
//        // mark vanilla flags so vanilla tick() sees them if you call it
//        cockroach.setDancing(true);
//        cockroach.setNearbySongPlaying(cockroach.getBlockPos(), true);
//
//        // remember stand state
//        identity$prevStand = true;
    }

    @Unique
    private boolean identity$hasMaracas() {
        return identity$hasMaracas;
    }

    @Unique
    private void identity$tellOthersImPlayingLaCucaracha() {
        for(EntityCockroach roach : this.getWorld().getEntitiesByClass(EntityCockroach.class, this.identity$getMusicianDistance(), EntityPredicates.EXCEPT_SPECTATOR)) {
            if (!roach.hasMaracas()) {
                roach.setNearestMusician(this.getUuid());
            }
        }

    }
    @Unique
    private Box identity$getMusicianDistance() {
        return this.getBoundingBox().expand((double)10.0F, (double)10.0F, (double)10.0F);
    }

    @Unique
    public Entity identity$getNearestMusician() {
        UUID id = this.identity$nearestMusicianId;
        return id != null && !this.getWorld().isClient ? ((ServerWorld)this.getWorld()).getEntity(id) : null;
    }

    @Unique
    public void identity$setNearestMusician(@Nullable UUID uniqueId) {
        this.identity$nearestMusicianId = uniqueId;
    }

//
//        // === your debugForceDance() inline ===
//        cockroach.setDancing(true);
//        cockroach.setNearbySongPlaying(cockroach.getBlockPos(), true);
//        cockroach.prevDanceProgress = cockroach.danceProgress;
//        cockroach.danceProgress = 5.0F;
//        cockroach.randomWingFlapTick = 10;

}
