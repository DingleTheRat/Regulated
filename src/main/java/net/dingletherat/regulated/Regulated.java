package net.dingletherat.regulated;

import net.dingletherat.regulated.command.RegulatedCommands;
import net.dingletherat.regulated.data.RegulatedData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class Regulated implements ModInitializer {
	public static final String MOD_ID = "regulated";
	public static final Map<UUID, Long> lastDamageTime = new HashMap<>();
	public static final Map<UUID, Long> lastDeathTime = new HashMap<>();

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		ServerLifecycleEvents.SERVER_STARTED.register(server -> RegulatedData.loadData());

		RegulatedCommands.registerRegulatedCommands();
	}

    public static void checkUse(CommandSourceStack source, CallbackInfoReturnable<Integer> cir, String commandName) {
        if (RegulatedData.dataFile != null && !RegulatedData.dataFile.ENABLED.contains(commandName)) {
            if (!source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR) || RegulatedData.dataFile.EFFECTS_OPS.contains(commandName)) {
                if (source.getPlayer() != null)
                    source.getPlayer().sendSystemMessage(Component.literal("Command disabled").withStyle(ChatFormatting.RED));
                cir.setReturnValue(0);
            }
        }
    }
}
