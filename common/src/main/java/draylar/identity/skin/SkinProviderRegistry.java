package draylar.identity.skin;


import dev.architectury.injectables.annotations.ExpectPlatform;

public class SkinProviderRegistry {
    @ExpectPlatform
    public static SkinProvider get() {
        throw new AssertionError("Platform-specific SkinProvider not implemented");
    }
}