package draylar.identity.api.platform;

public class IdentityPlatform {
    private static IdentityConfig current;
    private static ConfigReloader configReloader;

    public static IdentityConfig getConfig() {
        return current;
    }

    public static void setConfig(IdentityConfig config) {
        current = config;
    }

    public static ConfigReloader getReloader() {
        return configReloader;
    }

    public static void setReloader(ConfigReloader reloader) {
        configReloader = reloader;
    }
}
