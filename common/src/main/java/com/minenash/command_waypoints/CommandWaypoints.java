package com.minenash.command_waypoints;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class CommandWaypoints {

    public static Map<Level,Map<ResourceLocation, CommandWaypoint>> waypoints = new HashMap<>();
    public static Consumer<Level> saveWaypoints;

    public static void init(Consumer<Level> saveWaypoints) {
        CommandWaypoints.saveWaypoints = saveWaypoints;
    }
}
