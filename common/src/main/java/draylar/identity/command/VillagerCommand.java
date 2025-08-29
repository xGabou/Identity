package draylar.identity.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import draylar.identity.api.PlayerIdentity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class VillagerCommand {

    public static void register() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, selection) -> {
            dispatcher.register(
                    CommandManager.literal("identity_villager")
                            .requires(src -> true)
                            .then(CommandManager.literal("list")
                                    .executes(ctx -> {
                                        ServerPlayerEntity player = ctx.getSource().getPlayer();
                                        Map<String, NbtCompound> map = PlayerIdentity.getVillagerIdentities(player);
                                        if (map.isEmpty()) {
                                            player.sendMessage(Text.literal("You have no saved villager professions."), false);
                                            return 1;
                                        }
                                        player.sendMessage(Text.literal("Saved villager professions:"), false);
                                        map.forEach((name, tag) -> {
                                            String prof = tag.getString("ProfessionId");
                                            player.sendMessage(Text.literal("- " + name + " -> " + prof), false);
                                        });
                                        return 1;
                                    }))
                            .then(CommandManager.literal("show")
                                    .then(CommandManager.argument("name", StringArgumentType.string())
                                            .executes(ctx -> {
                                                ServerPlayerEntity player = ctx.getSource().getPlayer();
                                                String name = StringArgumentType.getString(ctx, "name");
                                                Map<String, NbtCompound> map = PlayerIdentity.getVillagerIdentities(player);
                                                if (!map.containsKey(name)) {
                                                    player.sendMessage(Text.literal("No villager saved under name: " + name), false);
                                                    return 0;
                                                }
                                                NbtCompound tag = map.get(name);
                                                String prof = tag.getString("ProfessionId");
                                                player.sendMessage(Text.literal("Villager '" + name + "' profession: " + prof), false);
                                                return 1;
                                            })))
            );
        });
    }

    private VillagerCommand() {
    }
}

