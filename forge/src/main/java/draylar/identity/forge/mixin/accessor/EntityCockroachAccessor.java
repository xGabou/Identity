package draylar.identity.forge.mixin.accessor;

import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityCockroach.class)
public interface EntityCockroachAccessor {

    @Accessor("isJukeboxing")
    boolean identity$isJukeboxing();

    @Accessor("isJukeboxing")
    void identity$setJukeboxing(boolean value);

    @Accessor("jukeboxPosition")
    BlockPos identity$getJukeboxPosition();

    @Accessor("jukeboxPosition")
    void identity$setJukeboxPosition(BlockPos pos);

    @Accessor("laCucarachaTimer")
    int identity$getCucarachaTimer();

    @Accessor("laCucarachaTimer")
    void identity$setCucarachaTimer(int value);

    @Accessor("prevStand")
    boolean identity$getPrevStand();

    @Accessor("prevStand")
    void identity$setPrevStand(boolean value);

    @Invoker("tellOthersImPlayingLaCucaracha")
    void identity$tellOthersImPlayingLaCucaracha();

    @Accessor("laCucarachaTimer")
    int identity$getLaCucarachaTimer();

    @Accessor("laCucarachaTimer")
    void identity$setLaCucarachaTimer(int value);


    default void identity$incrementLaCucarachaTimer() {
        identity$setLaCucarachaTimer(identity$getLaCucarachaTimer() + 1);
    }

//    @Accessor("this.random")
//    net.minecraft.util.math.random.Random identity$getRandom();
}
