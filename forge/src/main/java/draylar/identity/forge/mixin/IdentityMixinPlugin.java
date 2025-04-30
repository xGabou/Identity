package draylar.identity.forge.mixin;

import draylar.identity.Identity;
import draylar.identity.forge.IdentityForge;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class IdentityMixinPlugin implements IMixinConfigPlugin {

    private static Boolean bjornLoaded = null;
    private static Boolean alexLoaded = null;

    private boolean isBjornLibLoaded() {
        if (bjornLoaded != null) return bjornLoaded;

        try {
            // Check via class existence instead of ModList to avoid early crash
            Class.forName("com.furiusmax.bjornlib.BjornLib", false, getClass().getClassLoader());
            bjornLoaded = true;
        } catch (ClassNotFoundException e) {
            bjornLoaded = false;
        }
        System.out.println("[Identity] BjornLib detected: " + bjornLoaded);
        return bjornLoaded;
    }

    private boolean isAlexLoaded() {
        if (alexLoaded != null) return alexLoaded;

        try {
            // Safer to check for a known class rather than the base package
            Class.forName("com.github.alexthe666.alexsmobs.AlexsMobs", false, getClass().getClassLoader());
            alexLoaded = true;
        } catch (ClassNotFoundException e) {
            alexLoaded = false;
        }

        System.out.println("[Identity] Alex's Mobs detected: " + alexLoaded);
        return alexLoaded;
    }



    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

        if (mixinClassName.endsWith("LivingEntityAccessor") && isBjornLibLoaded()) {
            return false;
        }
        if(mixinClassName.endsWith("EntityCockroachMixin") && !isAlexLoaded() ) {
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
