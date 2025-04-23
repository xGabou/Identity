package draylar.identity.forge.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class IdentityMixinPlugin implements IMixinConfigPlugin {

    private static Boolean bjornLoaded = null;

    private static boolean isBjornLibLoaded() {
        if (bjornLoaded != null) return bjornLoaded;

        try {
            // Check via class existence instead of ModList to avoid early crash
            Class.forName("com.furiusmax.bjornlib.BjornLib");
            bjornLoaded = true;
        } catch (ClassNotFoundException e) {
            bjornLoaded = false;
        }
        System.out.println("[Identity] BjornLib detected: " + bjornLoaded);
        return bjornLoaded;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

        if (mixinClassName.endsWith("LivingEntityAccessor") && isBjornLibLoaded()) {
            return false;
        }
        if (mixinClassName.endsWith("PlayerEntityRendererMixin") && isBjornLibLoaded()) {
            return false;
        }
        if (mixinClassName.endsWith("BjornCompatMixin") && !isBjornLibLoaded()) {
            return false;
        }

        return true;
    }

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }
}
