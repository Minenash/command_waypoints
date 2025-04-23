package com.minenash.command_waypoints;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointStyleAssets;

import java.util.Optional;
import java.util.UUID;

import static com.minenash.command_waypoints.CommandWaypoints.waypoints;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static java.lang.Math.floor;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.arguments.ColorArgument.color;
import static net.minecraft.commands.arguments.ColorArgument.getColor;
import static net.minecraft.commands.arguments.HexColorArgument.getHexColor;
import static net.minecraft.commands.arguments.HexColorArgument.hexColor;
import static net.minecraft.commands.arguments.ResourceLocationArgument.getId;
import static net.minecraft.commands.arguments.ResourceLocationArgument.id;
import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos;
import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.getBlockPos;

public class SubCommands {

    private static final DynamicCommandExceptionType NO_WAYPOINT_EXCEPTION = new DynamicCommandExceptionType(id -> Component.literal("No Waypoint with ID " + id));

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_STATIC_IDS = (ctx, builder) ->
        SharedSuggestionProvider.suggestResource(waypoints.keySet(), builder);

    public static LiteralArgumentBuilder<CommandSourceStack> addSubCommands(LiteralArgumentBuilder<CommandSourceStack> original) {

        return original.then(literal("static")
            .then(literal("add")
                .then(argument("id", id()).executes(SubCommands::addWaypointNoArgs)
                    .then(argument("location", blockPos()).executes(SubCommands::addWaypointPos)
                        .then(literal("color")
                            .then(literal("hex").then(argument("color", hexColor()).executes(SubCommands::addWaypoint)
                                .then(literal("style").then(argument("style", id()).executes(SubCommands::addWaypoint)
                                    .then(literal("range").then(argument("range", integer(0, 60000000)).executes(SubCommands::addWaypoint)))))
                                .then(literal("range").then(argument("range", integer(0, 60000000)).executes(SubCommands::addWaypoint)
                                    .then(literal("style").then(argument("style", id()).executes(SubCommands::addWaypoint)))))
                            ))
                            .then(argument("color", color()).executes(SubCommands::addWaypoint)
                                .then(literal("style").then(argument("style", id()).executes(SubCommands::addWaypoint)
                                    .then(literal("range").then(argument("range", integer(0, 60000000)).executes(SubCommands::addWaypoint)))))
                                .then(literal("range").then(argument("range", integer(0, 60000000)).executes(SubCommands::addWaypoint)
                                    .then(literal("style").then(argument("style", id()).executes(SubCommands::addWaypoint)))))
                            ))
                        .then(literal("style").then(argument("style", id()).executes(SubCommands::addWaypoint)
                            .then(literal("color")
                                .then(literal("hex").then(argument("color", hexColor()).executes(SubCommands::addWaypoint)
                                    .then(literal("range").then(argument("range", integer(0, 60000000)).executes(SubCommands::addWaypoint)))))
                                .then(argument("color", color()).executes(SubCommands::addWaypoint)
                                    .then(literal("range").then(argument("range", integer(0, 60000000)).executes(SubCommands::addWaypoint)))))
                            .then(literal("range").then(argument("range", integer(0, 60000000)).executes(SubCommands::addWaypoint)
                                .then(literal("color")
                                    .then(literal("hex").then(argument("color", hexColor()).executes(SubCommands::addWaypoint)))
                                    .then(argument("color", color()).executes(SubCommands::addWaypoint)))
                            ))))
                        .then(literal("range").then(argument("range", integer(0, 60000000)).executes(SubCommands::addWaypoint)
                            .then(literal("color")
                                .then(literal("hex").then(argument("color", hexColor()).executes(SubCommands::addWaypoint)
                                    .then(literal("style").then(argument("style", id()).executes(SubCommands::addWaypoint)))))
                                .then(argument("color", color()).executes(SubCommands::addWaypoint)
                                    .then(literal("style").then(argument("style", id()).executes(SubCommands::addWaypoint)))))
                            .then(literal("style").then(argument("style", id()).executes(SubCommands::addWaypoint)
                                .then(literal("color")
                                    .then(literal("hex").then(argument("color", hexColor()).executes(SubCommands::addWaypoint)))
                                    .then(argument("color", color()).executes(SubCommands::addWaypoint)))
                            ))
                        ))
                    )))
            .then(literal("modify")
                .then(argument("id", id()).suggests(SUGGEST_STATIC_IDS)
                .then(literal("color")
                    .then(literal("hex").then(argument("hex_color", hexColor()).executes(SubCommands::modifyWayPointHexColor)))
                    .then(argument("color", color()).executes(SubCommands::modifyWayPointColor)))
                .then(literal("range").then(argument("range", integer(0, 60000000)).executes(SubCommands::modifyWayPointRange)))
                .then(literal("location").then(argument("location", blockPos()).executes(SubCommands::modifyWayPointPos)))
                .then(literal("style")
                    .then(literal("reset").executes(SubCommands::modifyWayPointResetStyle))
                    .then(literal("set")
                        .then(argument("style", id()).executes(SubCommands::modifyWayPointStyle))))))
            .then(literal("remove")
                .then(argument("id", id()).suggests(SUGGEST_STATIC_IDS).executes(SubCommands::removeWaypoint))));
    }

    public static int addWaypoint(CommandContext<CommandSourceStack> ctx) {
        var location = getBlockPos(ctx, "location");

        var color = getArg(ctx, "color", ChatFormatting.class);
        var hexColor = getArg(ctx, "hex_color", Integer.class);
        var style = getArg(ctx, "style", ResourceLocation.class);
        var range = getArg(ctx, "range", Integer.class);

        return addWaypoint(ctx, location,
            hexColor != null ? hexColor : color != null ? color.getColor() : null,
            range != null ? range : Waypoint.MAX_RANGE,
            style);
    }

    public static <V> V getArg(CommandContext<CommandSourceStack> ctx, String name, Class<V> clazz) {
        try { return ctx.getArgument(name, clazz); }
        catch (Exception ignored) { return null; }
    }


    public static int addWaypointNoArgs(CommandContext<CommandSourceStack> ctx) {
        var p = ctx.getSource().getPosition();
        var pos = new BlockPos((int)floor(p.x), (int)floor(p.y), (int)floor(p.z));

        return addWaypoint(ctx, pos, null, Waypoint.MAX_RANGE, null);
    }

    public static int addWaypointPos(CommandContext<CommandSourceStack> ctx) {
        return addWaypoint(ctx, getBlockPos(ctx, "location"), null, Waypoint.MAX_RANGE, null);
    }

    public static int addWaypoint(CommandContext<CommandSourceStack> ctx, BlockPos pos, Integer color, int range, ResourceLocation style) {
        var manager = ctx.getSource().getLevel().getWaypointManager();
        var id = getId(ctx, "id");

        var point = waypoints.get(id);
        if (point != null)
            manager.untrackWaypoint(point);

        var icon = new Waypoint.Icon();
        if (color != null)
            icon.color = Optional.of(color);
        if (style != null)
            icon.style = ResourceKey.create(WaypointStyleAssets.ROOT_ID, style);

        point = new CommandWaypoint(UUID.randomUUID(), pos, icon, range);
        waypoints.put(id, point);
        manager.trackWaypoint(point);
        return 1;
    }

    public static int modifyWayPointPos(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var point = point(ctx);
        point.pos = getBlockPos(ctx, "location");
        updateWaypoint(ctx, point);
        return 1;
    }
    public static int modifyWayPointColor(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var point = point(ctx);
        point.icon.color = Optional.ofNullable(getColor(ctx, "color").getColor());
        updateWaypoint(ctx, point);
        return 1;
    }
    public static int modifyWayPointHexColor(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var point = point(ctx);
        point.icon.color = Optional.of(getHexColor(ctx, "color"));
        updateWaypoint(ctx, point);
        return 1;
    }
    public static int modifyWayPointRange(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var point = point(ctx);
        point.range = getInteger(ctx, "range");
        updateWaypoint(ctx, point);
        return 1;
    }
    public static int modifyWayPointResetStyle(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var point = point(ctx);
        point.icon.style = WaypointStyleAssets.DEFAULT;
        updateWaypoint(ctx, point);
        return 1;
    }
    public static int modifyWayPointStyle(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var point = point(ctx);
        point.icon.style = ResourceKey.create(WaypointStyleAssets.ROOT_ID, getId(ctx, "style"));
        updateWaypoint(ctx, point);
        return 1;
    }

    private static CommandWaypoint point(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var id = getId(ctx, "id");
        var point = waypoints.get(id);
        if (point == null)
            throw NO_WAYPOINT_EXCEPTION.create(id);
        return point;
    }
    private static void updateWaypoint(CommandContext<CommandSourceStack> ctx, CommandWaypoint waypoint) {
        var manager = ctx.getSource().getLevel().getWaypointManager();
        manager.untrackWaypoint(waypoint);
        manager.trackWaypoint(waypoint);
    }


    public static int removeWaypoint(CommandContext<CommandSourceStack> ctx) {
        var manager = ctx.getSource().getLevel().getWaypointManager();
        var id = getId(ctx, "id");

        var point = waypoints.get(id);
        if (point != null)
            manager.untrackWaypoint(point);

        return 1;
    }
}
