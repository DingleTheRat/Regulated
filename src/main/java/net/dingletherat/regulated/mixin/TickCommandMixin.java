package net.dingletherat.regulated.mixin;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.dingletherat.regulated.Regulated;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.TickCommand;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(TickCommand.class)
public class TickCommandMixin {
    private static final String COMMAND_NAME = "tick";

    @Redirect(method = "register", at = @At(value = "INVOKE", 
		target = "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;requires(Ljava/util/function/Predicate;)Lcom/mojang/brigadier/builder/ArgumentBuilder;"))
    private static ArgumentBuilder<CommandSourceStack, ?> allowAllPlayers(
            LiteralArgumentBuilder<CommandSourceStack> instance,
            Predicate<CommandSourceStack> predicate) {
        return instance.requires(source -> true);
    }

    @Inject(method = "setTickingRate", at = @At("HEAD"), cancellable = true)
    private static void executeRate(CommandSourceStack source, float rate, CallbackInfoReturnable<Integer> cir) {
        Regulated.checkUse(source, cir, COMMAND_NAME);
    }

    @Inject(method = "tickQuery", at = @At("HEAD"), cancellable = true)
    private static void executeQuery(CommandSourceStack source, CallbackInfoReturnable<Integer> cir) {
        Regulated.checkUse(source, cir, COMMAND_NAME);
    }

    @Inject(method = "sprint", at = @At("HEAD"), cancellable = true)
    private static void executeSprint(CommandSourceStack source, int ticks, CallbackInfoReturnable<Integer> cir) {
        Regulated.checkUse(source, cir, COMMAND_NAME);
    }


    @Inject(method = "setFreeze", at = @At("HEAD"), cancellable = true)
    private static void executeSprint(CommandSourceStack source, boolean forzen, CallbackInfoReturnable<Integer> cir) {
        Regulated.checkUse(source, cir, COMMAND_NAME);
    }

    @Inject(method = "step", at = @At("HEAD"), cancellable = true)
    private static void executeStep(CommandSourceStack source, int steps, CallbackInfoReturnable<Integer> cir) {
        Regulated.checkUse(source, cir, COMMAND_NAME);
    }

   @Inject(method = "stopStepping", at = @At("HEAD"), cancellable = true)
   private static void stopStepping(final CommandSourceStack source, CallbackInfoReturnable<Integer> cir) {
        Regulated.checkUse(source, cir, COMMAND_NAME);
   }


   @Inject(method = "stopSprinting", at = @At("HEAD"), cancellable = true)
   private static void stopSprinting(final CommandSourceStack source, CallbackInfoReturnable<Integer> cir) {
        Regulated.checkUse(source, cir, COMMAND_NAME);
   }
}
