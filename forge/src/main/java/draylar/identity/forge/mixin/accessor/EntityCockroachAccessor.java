package draylar.identity.forge.mixin.accessor;

import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityCockroach.class)
public interface EntityCockroachAccessor {

    @Accessor(value = "isJukeboxing", remap = false)
    boolean identity$isJukeboxing();

    @Accessor(value = "isJukeboxing", remap = false)
    void identity$setJukeboxing(boolean value);

    @Accessor(value = "jukeboxPosition", remap = false)
    BlockPos identity$getJukeboxPosition();

    @Accessor(value = "jukeboxPosition", remap = false)
    void identity$setJukeboxPosition(BlockPos pos);

    @Accessor(value = "laCucarachaTimer", remap = false)
    int identity$getLaCucarachaTimer();

    @Accessor(value = "laCucarachaTimer", remap = false)
    void identity$setLaCucarachaTimer(int value);

    @Accessor(value = "prevStand", remap = false)
    boolean identity$getPrevStand();

    @Accessor(value = "prevStand", remap = false)
    void identity$setPrevStand(boolean value);

    @Invoker(value = "tellOthersImPlayingLaCucaracha", remap = false)
    void identity$tellOthersImPlayingLaCucaracha();
}
