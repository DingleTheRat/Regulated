package net.dingletherat.regulated.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class RegulatedCommands {
   public static void registerRegulatedCommands() {
      CommandRegistrationCallback.EVENT.register(RegulateCommand::register);
   }
}
