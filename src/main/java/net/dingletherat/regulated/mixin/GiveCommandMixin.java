package net.dingletherat.regulated.mixin;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.dingletherat.regulated.Regulated;
import net.dingletherat.regulated.data.RegulatedData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.function.Predicate;

@Mixin(GiveCommand.class)
public class GiveCommandMixin {
    private static final String COMMAND_NAME = "give";

    @Redirect(method = "register", at = @At(value = "INVOKE", 
		target = "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;requires(Ljava/util/function/Predicate;)Lcom/mojang/brigadier/builder/ArgumentBuilder;"))
    private static ArgumentBuilder<CommandSourceStack, ?> allowAllPlayers(
            LiteralArgumentBuilder<CommandSourceStack> instance,
            Predicate<CommandSourceStack> predicate) {
        return instance.requires(source -> true);
    }

    @Inject(method = "giveItem", at = @At("HEAD"), cancellable = true)
    private static void onExecute(CommandSourceStack source, ItemInput item, Collection<ServerPlayer> targets, int count, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
        if (RegulatedData.dataFile == null) return;

        Regulated.checkUse(source, cir, COMMAND_NAME);

        if (!RegulatedData.dataFile.GIVABLES.isEmpty()) {
            String itemId = BuiltInRegistries.ITEM.getKey(item.createItemStack(1).getItem()).toString();
            String itemIdShort = BuiltInRegistries.ITEM.getKey(item.createItemStack(1).getItem()).getPath();
            if (!RegulatedData.dataFile.GIVABLES.contains(itemId) && !RegulatedData.dataFile.GIVABLES.contains(itemIdShort)) {
                if (!source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR) || RegulatedData.dataFile.EFFECTS_OPS.contains(COMMAND_NAME)) {
                    if (source.getPlayer() != null)
                        source.getPlayer().sendSystemMessage(Component.literal("Special condition not met: item not on whitelist").withStyle(ChatFormatting.RED));
                    cir.setReturnValue(0);
                    return;
                }
            }
        }
    }
}
