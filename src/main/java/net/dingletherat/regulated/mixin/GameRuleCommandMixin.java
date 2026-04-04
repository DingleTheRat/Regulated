package net.dingletherat.regulated.mixin;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.dingletherat.regulated.Regulated;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.world.level.gamerules.GameRule;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(GameRuleCommand.class)
public class GameRuleCommandMixin {
    private static final String COMMAND_NAME = "gamerule";

    @Redirect(method = "register", at = @At(value = "INVOKE",
        target = "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;requires(Ljava/util/function/Predicate;)Lcom/mojang/brigadier/builder/ArgumentBuilder;"))
    private static ArgumentBuilder<CommandSourceStack, ?> allowAllPlayers(
            LiteralArgumentBuilder<CommandSourceStack> instance,
            Predicate<CommandSourceStack> predicate) {
        return instance.requires(source -> true);
    }

    @Inject(method = "setRule", at = @At("HEAD"), cancellable = true)
    private static <T> void executeSet(CommandContext<CommandSourceStack> context, GameRule<T> key, CallbackInfoReturnable<Integer> cir) {
        Regulated.checkUse(context.getSource(), cir, COMMAND_NAME);
    }

    @Inject(method = "queryRule", at = @At("HEAD"), cancellable = true)
    private static <T> void executeSet(CommandSourceStack source, GameRule<T> key, CallbackInfoReturnable<Integer> cir) {
        Regulated.checkUse(source, cir, COMMAND_NAME);
    }
}
