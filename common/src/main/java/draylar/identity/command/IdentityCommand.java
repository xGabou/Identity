package draylar.identity.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.PlayerUnlocks;
import draylar.identity.api.platform.IdentityConfig;
import draylar.identity.api.variant.IdentityType;
import draylar.identity.screen.widget.EntityWidget;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class IdentityCommand {

    public static void register() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, b) -> {
            LiteralCommandNode<ServerCommandSource> rootNode = CommandManager
                    .literal("identity")
                    .requires(source -> source.hasPermissionLevel(2))
                    .build();

            /*
            Used to give the specified Identity to the specified Player.
             */
            LiteralCommandNode<ServerCommandSource> grantNode = CommandManager
                    .literal("grant")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                            .then(CommandManager.literal("everything")
                                    .executes(context -> {
                                        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                        for (IdentityType<?> type : IdentityType.getAllTypes(player.getWorld())) {
                                            if(!PlayerUnlocks.has(player, type)) {
                                                PlayerUnlocks.unlock(player, type);
                                            }
                                        }

                                        return 1;
                                    })
                            )
                            .then(CommandManager.argument("identity", IdentifierArgumentType.identifier())
                                    .executes(context -> {
                                        grant(
                                                context.getSource().getPlayer(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                IdentifierArgumentType.getIdentifier(context, "identity"),
                                                null
                                        );
                                        return 1;
                                    })
                                    .then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
                                            .executes(context -> {
                                                NbtCompound nbt = NbtCompoundArgumentType.getNbtCompound(context, "nbt");

                                                grant(
                                                        context.getSource().getPlayer(),
                                                        EntityArgumentType.getPlayer(context, "player"),
                                                        IdentifierArgumentType.getIdentifier(context, "identity"),
                                                        nbt
                                                );

                                                return 1;
                                            })
                                    )
                            )
                    )
                    .build();

            LiteralCommandNode<ServerCommandSource> revokeNode = CommandManager
                    .literal("revoke")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                            .then(CommandManager.literal("everything")
                                    .executes(context -> {
                                        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                        for (IdentityType<?> type : IdentityType.getAllTypes(player.getWorld())) {
                                            if(PlayerUnlocks.has(player, type)) {
                                                PlayerUnlocks.revoke(player, type);
                                            }
                                        }

                                        return 1;
                                    })
                            )
                            .then(CommandManager.argument("identity", IdentifierArgumentType.identifier())
                                    .executes(context -> {
                                        revoke(
                                                context.getSource().getPlayer(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                IdentifierArgumentType.getIdentifier(context, "identity"),
                                                null
                                        );
                                        return 1;
                                    })
                                    .then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
                                            .executes(context -> {
                                                NbtCompound nbt = NbtCompoundArgumentType.getNbtCompound(context, "nbt");

                                                revoke(
                                                        context.getSource().getPlayer(),
                                                        EntityArgumentType.getPlayer(context, "player"),
                                                        IdentifierArgumentType.getIdentifier(context, "identity"),
                                                        nbt
                                                );

                                                return 1;
                                            })
                                    )
                            )
                    )
                    .build();

            LiteralCommandNode<ServerCommandSource> equip = CommandManager
                    .literal("equip")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                            .then(CommandManager.argument("identity", IdentifierArgumentType.identifier())
                                    .executes(context -> {
                                        equip(context.getSource().getPlayer(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                IdentifierArgumentType.getIdentifier(context, "identity"),
                                                null);

                                        return 1;
                                    })
                                    .then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
                                            .executes(context -> {
                                                NbtCompound nbt = NbtCompoundArgumentType.getNbtCompound(context, "nbt");

                                                equip(context.getSource().getPlayer(),
                                                        EntityArgumentType.getPlayer(context, "player"),
                                                        IdentifierArgumentType.getIdentifier(context, "identity"),
                                                        nbt);

                                                return 1;
                                            })
                                    )
                            )
                    )
                    .build();

            LiteralCommandNode<ServerCommandSource> unequip = CommandManager
                    .literal("unequip")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                            .executes(context -> {
                                unequip(
                                        context.getSource().getPlayer(),
                                        EntityArgumentType.getPlayer(context, "player")
                                );
                                return 1;
                            })
                    )
                    .build();

            LiteralCommandNode<ServerCommandSource> test = CommandManager
                    .literal("test")
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                            .then(CommandManager.literal("not")
                                     .then(CommandManager.argument("identity", IdentifierArgumentType.identifier())
                                            .executes(context -> {
                                                return testNot(
                                                        context.getSource().getPlayer(),
                                                        EntityArgumentType.getPlayer(context, "player"),
                                                         IdentifierArgumentType.getIdentifier(context, "identity")
                                             );
                                         })
                                     )
                             )
                             .then(CommandManager.argument("identity", IdentifierArgumentType.identifier())
                                     .executes(context -> {
                                         return test(
                                                 context.getSource().getPlayer(),
                                                 EntityArgumentType.getPlayer(context, "player"),
                                                 IdentifierArgumentType.getIdentifier(context, "identity")
                                         );
                                     })
                             )
                     )
                     .build();
            LiteralCommandNode<ServerCommandSource> offsetNode =
                    CommandManager.literal("offset")
                            .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                    .executes(ctx -> {
                                        int v = IntegerArgumentType.getInteger(ctx, "value");
                                        EntityWidget.VERTICAL_OFFSET = v;
                                        ctx.getSource()
                                                .sendFeedback(
                                                        ()-> Text.literal("Entity‑grid Y‑offset set to §e" + v + "§r"),
                                                        false
                                                );
                                        return 1;
                                    })
                            ).build();

            LiteralCommandNode<ServerCommandSource> whitelistNode =
                    CommandManager.literal("whitelist")
                            .then(CommandManager.literal("enable")
                                    .executes(ctx -> {
                                        IdentityConfig.getInstance().setEnableSwaps(false);
                                        if (IdentityConfig.getInstance().logCommands()) {
                                            ctx.getSource().sendFeedback(() -> Text.literal("Enabled identity whitelist"), true);
                                        }
                                        return 1;
                                    }))
                            .then(CommandManager.literal("disable")
                                    .executes(ctx -> {
                                        IdentityConfig.getInstance().setEnableSwaps(true);
                                        if (IdentityConfig.getInstance().logCommands()) {
                                            ctx.getSource().sendFeedback(() -> Text.literal("Disabled identity whitelist"), true);
                                        }
                                        return 1;
                                    }))
                            .then(CommandManager.literal("add")
                                    .then(CommandManager.argument("player", StringArgumentType.string())
                                            .executes(ctx -> {
                                                String name = StringArgumentType.getString(ctx, "player");
                                                IdentityConfig.getInstance().allowedSwappers().add(name);
                                                if (IdentityConfig.getInstance().logCommands()) {
                                                    ctx.getSource().sendFeedback(() -> Text.literal("Added " + name + " to identity whitelist"), true);
                                                }
                                                return 1;
                                            })))
                            .then(CommandManager.literal("remove")
                                    .then(CommandManager.argument("player", StringArgumentType.string())
                                            .executes(ctx -> {
                                                String name = StringArgumentType.getString(ctx, "player");
                                                IdentityConfig.getInstance().allowedSwappers().removeIf(n -> n.equalsIgnoreCase(name));
                                                if (IdentityConfig.getInstance().logCommands()) {
                                                    ctx.getSource().sendFeedback(() -> Text.literal("Removed " + name + " from identity whitelist"), true);
                                                }
                                                return 1;
                                            })))
                            .build();
            LiteralCommandNode<ServerCommandSource> professionNode =
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
                            .build();

            rootNode.addChild(grantNode);
            rootNode.addChild(revokeNode);
            rootNode.addChild(equip);
            rootNode.addChild(unequip);
            rootNode.addChild(test);
            rootNode.addChild(offsetNode);
            rootNode.addChild(whitelistNode);
            rootNode.addChild(professionNode);

            dispatcher.getRoot().addChild(rootNode);
        });
    }

    private static int test(ServerPlayerEntity source, ServerPlayerEntity player, Identifier identity) {
        EntityType<?> type = Registries.ENTITY_TYPE.get(identity);

        if(PlayerIdentity.getIdentity(player) != null && PlayerIdentity.getIdentity(player).getType().equals(type)) {
            if(IdentityConfig.getInstance().logCommands()) {
                source.sendMessage(Text.translatable("identity.test_positive", player.getDisplayName(), Text.translatable(type.getTranslationKey())), true);
            }

            return 1;
        }

        if(IdentityConfig.getInstance().logCommands()) {
            source.sendMessage(Text.translatable("identity.test_failed", player.getDisplayName(), Text.translatable(type.getTranslationKey())), true);
        }

        return 0;
    }

    private static int testNot(ServerPlayerEntity source, ServerPlayerEntity player, Identifier identity) {
        EntityType<?> type = Registries.ENTITY_TYPE.get(identity);

        if(PlayerIdentity.getIdentity(player) != null && !PlayerIdentity.getIdentity(player).getType().equals(type)) {
            if(IdentityConfig.getInstance().logCommands()) {
                source.sendMessage(Text.translatable("identity.test_failed", player.getDisplayName(), Text.translatable(type.getTranslationKey())), true);
            }

            return 1;
        }

        if(IdentityConfig.getInstance().logCommands()) {
            source.sendMessage(Text.translatable("identity.test_positive", player.getDisplayName(), Text.translatable(type.getTranslationKey())), true);
        }

        return 0;
    }

    private static void grant(ServerPlayerEntity source, ServerPlayerEntity player, Identifier id, @Nullable NbtCompound nbt) {
        IdentityType<LivingEntity> type = new IdentityType(Registries.ENTITY_TYPE.get(id));
        Text name = Text.translatable(type.getEntityType().getTranslationKey());

        // If the specified granting NBT is not null, change the IdentityType to reflect potential variants.
        if(nbt != null) {
            NbtCompound copy = nbt.copy();
            copy.putString("id", id.toString());
            ServerWorld serverWorld = source.getServerWorld();
            Entity loaded = EntityType.loadEntityWithPassengers(copy, serverWorld, it -> it);
            if(loaded instanceof LivingEntity living) {
                type = new IdentityType<>(living);
                name = type.createTooltipText(living);
            }
        }

        if(!PlayerUnlocks.has(player, type)) {
            boolean result = PlayerUnlocks.unlock(player, type);

            if(result && IdentityConfig.getInstance().logCommands()) {
                player.sendMessage(Text.translatable("identity.unlock_entity", name), true);
                source.sendMessage(Text.translatable("identity.grant_success", name, player.getDisplayName()), true);
            }
        } else {
            if(IdentityConfig.getInstance().logCommands()) {
                source.sendMessage(Text.translatable("identity.already_has", player.getDisplayName(), name), true);
            }
        }
    }

    private static void revoke(ServerPlayerEntity source, ServerPlayerEntity player, Identifier id, @Nullable NbtCompound nbt) {
        IdentityType<LivingEntity> type = new IdentityType(Registries.ENTITY_TYPE.get(id));
        Text name = Text.translatable(type.getEntityType().getTranslationKey());

        // If the specified granting NBT is not null, change the IdentityType to reflect potential variants.
        if(nbt != null) {
            NbtCompound copy = nbt.copy();
            copy.putString("id", id.toString());
            ServerWorld serverWorld = source.getServerWorld();
            Entity loaded = EntityType.loadEntityWithPassengers(copy, serverWorld, it -> it);
            if(loaded instanceof LivingEntity living) {
                type = new IdentityType<>(living);
                name = type.createTooltipText(living);
            }
        }

        if(PlayerUnlocks.has(player, type)) {
            PlayerUnlocks.revoke(player, type);

            if(IdentityConfig.getInstance().logCommands()) {
                player.sendMessage(Text.translatable("identity.revoke_entity", name), true);
                source.sendMessage(Text.translatable("identity.revoke_success", name, player.getDisplayName()), true);
            }
        } else {
            if(IdentityConfig.getInstance().logCommands()) {
                source.sendMessage(Text.translatable("identity.does_not_have", player.getDisplayName(), name), true);
            }
        }
    }

    private static void equip(ServerPlayerEntity source, ServerPlayerEntity player, Identifier identity, @Nullable NbtCompound nbt) {
        Entity created;

        if(nbt != null) {
            NbtCompound copy = nbt.copy();
            copy.putString("id", identity.toString());
            ServerWorld serverWorld = source.getServerWorld();
            created = EntityType.loadEntityWithPassengers(copy, serverWorld, it -> it);
        } else {
            EntityType<?> entity = Registries.ENTITY_TYPE.get(identity);
            created = entity.create(player.getWorld());
        }

        if(created instanceof LivingEntity living) {
            @Nullable IdentityType<?> defaultType = IdentityType.from(living);

            if(defaultType != null) {
                boolean result = PlayerIdentity.updateIdentity(player, defaultType, (LivingEntity) created);
                if(result && IdentityConfig.getInstance().logCommands()) {
                    source.sendMessage(Text.translatable("identity.equip_success", Text.translatable(created.getType().getTranslationKey()), player.getDisplayName()), true);
                }
            }
        }
    }

    private static void unequip(ServerPlayerEntity source, ServerPlayerEntity player) {
        boolean result = PlayerIdentity.updateIdentity(player, null, null);

        if(result && IdentityConfig.getInstance().logCommands()) {
            source.sendMessage(Text.translatable("identity.unequip_success", player.getDisplayName()), false);
        }
    }
}

