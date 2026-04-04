package net.dingletherat.regulated.command;

import java.util.List;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dingletherat.regulated.data.RegulatedData;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class RegulateCommand {
    private static final String[] COMMANDS = {"give", "teleport", "tick", "gamerule"};

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection environment) {
        dispatcher.register(
            ((LiteralArgumentBuilder<CommandSourceStack>) Commands.literal("regulate")
                .requires(Commands.hasPermission(Commands.LEVEL_MODERATORS)))

                // /regulate give ...
                .then(Commands.literal("give")
                    .then(Commands.literal("enabled")
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                            .executes(ctx -> executeBool(ctx.getSource(), "give", "enabled",
                                BoolArgumentType.getBool(ctx, "enabled")))))
                    .then(Commands.literal("effects_ops")
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                            .executes(ctx -> executeBool(ctx.getSource(), "give", "effects_ops",
                                BoolArgumentType.getBool(ctx, "enabled")))))
                    .then(Commands.literal("special_condition")
                        .then(Commands.literal("get")
                            .executes(ctx -> executeGivableGet(ctx.getSource())))
                        .then(Commands.literal("add")
                            .then(Commands.argument("item", ItemArgument.item(buildContext))
                                .executes(ctx -> executeGivable(ctx.getSource(), "add",
                                    ItemArgument.getItem(ctx, "item").createItemStack(1)))))
                        .then(Commands.literal("remove")
                            .then(Commands.argument("item", ItemArgument.item(buildContext))
                                .executes(ctx -> executeGivable(ctx.getSource(), "remove",
                                    ItemArgument.getItem(ctx, "item").createItemStack(1)))))))

                // /regulate teleport ...
                .then(Commands.literal("teleport")
                    .then(Commands.literal("enabled")
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                            .executes(ctx -> executeBool(ctx.getSource(), "teleport", "enabled",
                                BoolArgumentType.getBool(ctx, "enabled")))))
                    .then(Commands.literal("effects_ops")
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                            .executes(ctx -> executeBool(ctx.getSource(), "teleport", "effects_ops",
                                BoolArgumentType.getBool(ctx, "enabled")))))
                    .then(Commands.literal("special_condition")
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                            .executes(ctx -> executeBool(ctx.getSource(), "teleport", "special_condition",
                                BoolArgumentType.getBool(ctx, "enabled"))))))

                // /regulate <command> ... (generic via string suggestion)
                .then(Commands.argument("command", StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        for (String cmd : COMMANDS) builder.suggest(cmd);
                        return builder.buildFuture();
                    })
                    .then(Commands.literal("enabled")
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                            .executes(ctx -> executeBool(ctx.getSource(),
                                StringArgumentType.getString(ctx, "command"), "enabled",
                                BoolArgumentType.getBool(ctx, "enabled")))))
                    .then(Commands.literal("effects_ops")
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                            .executes(ctx -> executeBool(ctx.getSource(),
                                StringArgumentType.getString(ctx, "command"), "effects_ops",
                                BoolArgumentType.getBool(ctx, "enabled"))))))
        );
    }

    private static int executeBool(CommandSourceStack source, String command, String regulateType, boolean bool) {
        if (RegulatedData.dataFile == null) return 0;

        List<String> list = switch (regulateType) {
            case "special_condition" -> RegulatedData.dataFile.SPECIAL_CONDITIONS;
            case "enabled" -> RegulatedData.dataFile.ENABLED;
            case "effects_ops" -> RegulatedData.dataFile.EFFECTS_OPS;
            default -> List.of();
        };

        String status = bool ? "enabled" : "disabled";
        String typeName = regulateType.replace("_", " ");
        source.sendSuccess(() -> Component.literal("Set " + typeName + " for /" + command + " to " + status), true);

        if (bool) list.add(command);
        else list.remove(command);

        RegulatedData.saveData();
        return 1;
    }

    private static int executeGivableGet(CommandSourceStack source) {
        if (RegulatedData.dataFile == null) return 0;
        source.sendSuccess(() -> Component.literal("Whitelist: " + RegulatedData.dataFile.GIVABLES), false);
        return 1;
    }

    private static int executeGivable(CommandSourceStack source, String action, ItemStack item) {
        if (RegulatedData.dataFile == null) return 0;

        String key = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();

        switch (action) {
            case "add" -> {
                RegulatedData.dataFile.GIVABLES.add(key);
                source.sendSuccess(() -> Component.literal("Added: " + key), true);
            }
            case "remove" -> {
                RegulatedData.dataFile.GIVABLES.remove(key);
                source.sendSuccess(() -> Component.literal("Removed: " + key), true);
            }
        }

        RegulatedData.saveData();
        return 1;
    }
}
