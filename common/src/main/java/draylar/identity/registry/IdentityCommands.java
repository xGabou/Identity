package draylar.identity.registry;

import draylar.identity.command.IdentityCommand;
import draylar.identity.command.VillagerCommand;

public class IdentityCommands {

    public static void init() {
        IdentityCommand.register();
        VillagerCommand.register();
    }

    private IdentityCommands() {

    }
}
