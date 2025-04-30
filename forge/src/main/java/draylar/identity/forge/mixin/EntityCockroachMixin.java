package draylar.identity.forge.mixin;

import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import draylar.identity.forge.mixin.accessor.EntityCockroachAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityCockroach.class)
public abstract class EntityCockroachMixin extends AnimalEntity implements EntityCockroachAccessor {

    public EntityCockroachMixin(EntityType type, World world) {
        super(type, world);
    }

    @Unique
    private int identity$forceDanceTicks = 0;

    @Unique
    private boolean identity$prevStand = false;

    @Unique
    private int identity$laCucarachaTimer = 0;

    /**
     * @author Gabou
     * @reason Makes cockroachdance
     */
    @Overwrite
    public void tick() {
        EntityCockroach self = (EntityCockroach)(Object)this;
        World world = self.getWorld();
        BlockPos pos = self.getBlockPos();

        self.prevDanceProgress = self.danceProgress;
        boolean dance = identity$isJukeboxing() || self.isDancing();

        if (identity$forceDanceTicks > 0) {
            identity$forceDanceTicks--;
            self.setNearbySongPlaying(pos, true);
            dance = true;
        }

        BlockPos jukeboxPos = identity$getJukeboxPosition();
        if (jukeboxPos == null || !jukeboxPos.isWithinDistance(self.getPos(), 3.46) || !world.getBlockState(jukeboxPos).isOf(Blocks.JUKEBOX)) {
            identity$setJukeboxing(false);
            identity$setJukeboxPosition(null);
        }

        if (self.getStandingEyeHeight() > self.getHeight()) self.calculateDimensions();
        if (dance) {
            if (self.danceProgress < 5.0F) self.danceProgress++;
        } else if (self.danceProgress > 0.0F) self.danceProgress--;

        if (!self.isOnGround() || this.random.nextInt(200) == 0)
            self.randomWingFlapTick = 5 + this.random.nextInt(15);
        if (self.randomWingFlapTick > 0) self.randomWingFlapTick--;

        if (identity$getPrevStand() != dance) {
            if (self.hasMaracas()) identity$tellOthersImPlayingLaCucaracha();
            self.calculateDimensions();
        }

        if (!self.hasMaracas()) {
            Entity musician = self.getNearestMusician();
            if (musician != null) {
                if (musician.isAlive() && !(self.distanceTo(musician) > 10.0F) && (!(musician instanceof EntityCockroach) || ((EntityCockroach)musician).hasMaracas())) {
                    self.setDancing(true);
                } else {
                    self.setNearestMusician(null);
                    self.setDancing(false);
                }
            }
        }

        if (self.hasMaracas()) {
            identity$setLaCucarachaTimer(identity$getLaCucarachaTimer() + 1);
            if (identity$getLaCucarachaTimer() % 20 == 0 && this.random.nextFloat() < 0.3F) {
                identity$tellOthersImPlayingLaCucaracha();
            }
            self.setDancing(true);
            if (!self.isSilent()) world.sendEntityStatus(self, (byte)67);
        } else {
            identity$setLaCucarachaTimer(0);
        }

        if (!world.isClient && self.isAlive() && !self.isBaby() && --self.timeUntilNextEgg <= 0) {
            ItemEntity dropped = self.dropItem((ItemConvertible)AMItemRegistry.COCKROACH_OOTHECA.get());
            if (dropped != null) dropped.setToDefaultPickupDelay();
            self.timeUntilNextEgg = this.random.nextInt(24000) + 24000;
        }

        identity$setPrevStand(dance);
    }

    public void identity$startForceDance() {
        identity$forceDanceTicks = 200;
    }

    // Accessor implementations
    @Override
    public boolean identity$getPrevStand() {
        return identity$prevStand;
    }

    @Override
    public void identity$setPrevStand(boolean value) {
        identity$prevStand = value;
    }

    @Override
    public int identity$getLaCucarachaTimer() {
        return identity$laCucarachaTimer;
    }

    @Override
    public void identity$setLaCucarachaTimer(int value) {
        identity$laCucarachaTimer = value;
    }
}
