package draylar.identity.neoforge.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.platform.Platform;
import draylar.identity.neoforge.IdentityNeoForge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static IdentityNeoForgeConfig read() {
        Path configFolder = Platform.getConfigFolder();
        Path configFile = Paths.get(configFolder.toString(), "identity.json");

        if (!Files.exists(configFile)) {
            IdentityNeoForgeConfig config = new IdentityNeoForgeConfig();
            writeConfigFile(configFile, config);
            return config;
        } else {
            try {
                IdentityNeoForgeConfig newConfig = GSON.fromJson(Files.readString(configFile), IdentityNeoForgeConfig.class);

                if (newConfig == null) {
                    System.err.println("[Identity] Config file corrupted or empty, regenerating default identity.json.");
                    IdentityNeoForgeConfig config = new IdentityNeoForgeConfig();
                    writeConfigFile(configFile, config);
                    return config;
                }

                // Check config version
                if (newConfig.getConfigVersion() < IdentityNeoForge.CONFIG_VERSION) {
                    System.err.println("[Identity] Outdated config version detected (found: " + newConfig.getConfigVersion() + ", expected: " + IdentityNeoForge.CONFIG_VERSION + "). Regenerating identity.json.");
                    IdentityNeoForgeConfig config = new IdentityNeoForgeConfig();
                    writeConfigFile(configFile, config);
                    return config;
                }

                // Config is fine, just update/save any new fields if missing
                writeConfigFile(configFile, newConfig);
                return newConfig;
            } catch (IOException exception) {
                System.err.println("[Identity] Failed to read config file! Regenerating default identity.json.");
                exception.printStackTrace();

                IdentityNeoForgeConfig config = new IdentityNeoForgeConfig();
                writeConfigFile(configFile, config);
                return config;
            }
        }
    }

    private static void writeConfigFile(Path file, IdentityNeoForgeConfig config) {
        try {
            if (!Files.exists(file)) {
                Files.createFile(file);
            }

            Files.writeString(file, GSON.toJson(config));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
