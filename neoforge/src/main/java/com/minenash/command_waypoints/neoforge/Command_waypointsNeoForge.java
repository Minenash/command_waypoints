package com.minenash.command_waypoints.neoforge;

import com.minenash.command_waypoints.Command_waypoints;
import net.neoforged.fml.common.Mod;

@Mod(Command_waypoints.MOD_ID)
public final class Command_waypointsNeoForge {
    public Command_waypointsNeoForge() {
        // Run our common setup.
        Command_waypoints.init();
    }
}
