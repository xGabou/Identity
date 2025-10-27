package draylar.identity.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.platform.IdentityConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

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
                                            String dim = tag.getString("WorkstationDim");
                                            long posLong = tag.contains("WorkstationPos") ? tag.getLong("WorkstationPos") : Long.MIN_VALUE;
                                            net.minecraft.util.math.BlockPos blockPos = posLong == Long.MIN_VALUE ? null : net.minecraft.util.math.BlockPos.fromLong(posLong);
                                            String location = blockPos == null ? "?" : (blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
                                            player.sendMessage(Text.literal("- " + name + " -> " + prof + " @ " + dim + " " + location), false);
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
                                                String dim = tag.getString("WorkstationDim");
                                                long posLong = tag.contains("WorkstationPos") ? tag.getLong("WorkstationPos") : Long.MIN_VALUE;
                                                net.minecraft.util.math.BlockPos blockPos = posLong == Long.MIN_VALUE ? null : net.minecraft.util.math.BlockPos.fromLong(posLong);
                                                String location = blockPos == null ? "?" : (blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
                                                player.sendMessage(Text.literal("Villager '" + name + "' profession: " + prof + " @ " + dim + " " + location), false);
                                                return 1;
                                            })))
                            .then(CommandManager.literal("trade")
                                    .requires(src -> src.hasPermissionLevel(2))
                                    .then(CommandManager.literal("myself")
                                            .executes(ctx -> {
                                                ServerPlayerEntity player = ctx.getSource().getPlayer();
                                                if (!IdentityConfig.getInstance().allowSelfTrading()) {
                                                    player.sendMessage(Text.translatable("identity.profession.trade.self_disabled"), false);
                                                    return 0;
                                                }

                                                LivingEntity identity = PlayerIdentity.getIdentity(player);
                                                if (!(identity instanceof VillagerEntity villager)) {
                                                    player.sendMessage(Text.translatable("identity.profession.trade.require_villager"), false);
                                                    return 0;
                                                }

                                                villager.interactMob(player, Hand.MAIN_HAND);
                                                return 1;
                                            })))
            );
        });
    }

    private VillagerCommand() {
    }
}

