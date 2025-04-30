package draylar.identity.forge.mixin;

import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import draylar.identity.forge.compat.accessor.ForceDanceAccessor;
import draylar.identity.forge.mixin.accessor.EntityCockroachAccessor;
import draylar.identity.forge.network.ForceDancePacket;
import draylar.identity.forge.util.CockroachDanceManager;
import draylar.identity.forge.util.CockroachDanceUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityCockroach.class)
public abstract class EntityCockroachMixin extends AnimalEntity implements ForceDanceAccessor {

    @Unique
    private int identity$forceDanceTicks;

    public EntityCockroachMixin(EntityType<? extends AnimalEntity> type, World world) {
        super(type, world);
        identity$forceDanceTicks=0;
    }



    @Override
    public void identity$forceDance(int ticks) {
        this.identity$forceDanceTicks = ticks;
        EntityCockroach self = (EntityCockroach) (Object) this;

        if (!this.getWorld().isClient) {
            // server side → tell the client to start dancing
            this.getWorld().sendEntityStatus(this, (byte) 67);
        } else {
            // client side → set the value directly too
            this.identity$forceDanceTicks = ticks;
        }

        self.setDancing(true);
    }

}
