package draylar.identity.neoforge.mixin;
import draylar.identity.compat.LivingEntityCompatAccessor;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = LivingEntity.class)
public interface BjornCompatMixin extends LivingEntityCompatAccessor {

}
