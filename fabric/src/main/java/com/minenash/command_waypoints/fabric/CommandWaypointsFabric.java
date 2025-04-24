package com.minenash.command_waypoints.fabric;

import com.minenash.command_waypoints.CommandWaypoint;
import com.minenash.command_waypoints.CommandWaypoints;
import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public final class CommandWaypointsFabric implements ModInitializer {

    public static final AttachmentType<Map<ResourceLocation, CommandWaypoint>> WAYPOINT_ATTACHMENT_TYPE = AttachmentRegistry.createPersistent(
        ResourceLocation.tryBuild("command_waypoints", "points"),
        Codec.unboundedMap(ResourceLocation.CODEC, CommandWaypoint.CODEC));

    @Override
    public void onInitialize() {

        ServerWorldEvents.LOAD.register(ResourceLocation.tryBuild("command_waypoints","read_attachments"), (server, level) -> {
            var points = level.getAttachedOrCreate(WAYPOINT_ATTACHMENT_TYPE, HashMap::new);
            for (var point : points.values())
                level.getWaypointManager().trackWaypoint(point);

            CommandWaypoints.waypoints.put(level, new HashMap<>(points));
        });

        CommandWaypoints.init(CommandWaypointsFabric::save);
    }

    public static void save(Level level) {
        level.setAttached(WAYPOINT_ATTACHMENT_TYPE, CommandWaypoints.waypoints.get(level));
    }
}
