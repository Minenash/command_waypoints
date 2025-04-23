package com.minenash.command_waypoints.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minenash.command_waypoints.SubCommands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.WaypointCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WaypointCommand.class)
public class WaypointCommandMixin {

    @WrapOperation(method = "register", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/commands/Commands;literal(Ljava/lang/String;)Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;"))
    private static LiteralArgumentBuilder<CommandSourceStack> addSubCommands(String string, Operation<LiteralArgumentBuilder<CommandSourceStack>> original) {
        return SubCommands.addSubCommands(original.call(string));
    }


}
