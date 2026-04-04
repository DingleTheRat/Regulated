package net.dingletherat.regulated.mixin;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.dingletherat.regulated.Regulated;
import net.dingletherat.regulated.data.RegulatedData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.LookAt;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.Permissions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(TeleportCommand.class)
public class TeleportCommandMixin {
    private static final String COMMAND_NAME = "teleport";

    @Redirect(method = "register", at = @At(value = "INVOKE", 
		target = "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;requires(Ljava/util/function/Predicate;)Lcom/mojang/brigadier/builder/ArgumentBuilder;"))
    private static ArgumentBuilder<CommandSourceStack, ?> allowAllPlayers(
            LiteralArgumentBuilder<CommandSourceStack> instance,
            Predicate<CommandSourceStack> predicate) {
        return instance.requires(source -> true);
    }

    @Inject(method = "performTeleport", at = @At("HEAD"), cancellable = true)
    private static void onTeleport(CommandSourceStack source, Entity target, ServerLevel world, double x, double y, double z, Set<Relative> movementFlags, float yaw, float pitch, LookAt facingLocation, CallbackInfo ci) {
        if (RegulatedData.dataFile == null) return;

        if (!RegulatedData.dataFile.ENABLED.contains(COMMAND_NAME)) {
            if (!source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR) || RegulatedData.dataFile.EFFECTS_OPS.contains(COMMAND_NAME)) {
                if (source.getPlayer() != null)
                        source.getPlayer().sendSystemMessage(Component.literal("Command disabled").withStyle(ChatFormatting.RED));
                ci.cancel();
                return;
            }
        }

        if (RegulatedData.dataFile.SPECIAL_CONDITIONS.contains(COMMAND_NAME)) {
            long lastDamage = Regulated.lastDamageTime.getOrDefault(source.getPlayer().getUUID(), 0L);
            long lastDeath = Regulated.lastDeathTime.getOrDefault(source.getPlayer().getUUID(), 0L);
            boolean recentlyDamaged = System.currentTimeMillis() - lastDamage < 10000;
            boolean recentlyDied = System.currentTimeMillis() - lastDeath < 10000;
            
            if (!recentlyDamaged && !recentlyDied) {
                if (!source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR) || RegulatedData.dataFile.EFFECTS_OPS.contains(COMMAND_NAME)) {
                    if (source.getPlayer() != null)
                        source.getPlayer().sendSystemMessage(Component.literal("Special condition not met: must be in combat or recently died").withStyle(ChatFormatting.RED));
                    ci.cancel();
                    return;
                }
            }
        }
    }
}
