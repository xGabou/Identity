package draylar.identity.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * Basic demonstration command ported to Forge 1.21.1.
 */
public final class IdentityCommand {
    private IdentityCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("identity")
            .then(Commands.literal("hello")
                .executes(IdentityCommand::runHello))
        );
    }

    private static int runHello(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ctx.getSource().sendSuccess(() -> Component.literal("Hello from Identity!"), false);
        return 1;
    }
}
