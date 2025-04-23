package com.minenash.command_waypoints;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundTrackedWaypointPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointTransmitter;

import java.util.Optional;
import java.util.UUID;

public class CommandWaypoint implements WaypointTransmitter {

    private final UUID uuid;
    public BlockPos pos;
    public Waypoint.Icon icon;
    public int range;

    public CommandWaypoint(UUID uuid, BlockPos pos, Waypoint.Icon icon, int range) {
        this.uuid = uuid;
        this.pos = pos;
        this.icon = icon;
        this.range = range;
    }

    @Override
    public boolean isTransmittingWaypoint() {
        return true;
    }

    @Override
    public Optional<Connection> makeWaypointConnectionWith(ServerPlayer receiver) {
        return Optional.of(new BlockPosConnection(receiver));
    }

    @Override
    public Icon waypointIcon() {
        return icon;
    }

    public class BlockPosConnection implements WaypointTransmitter.Connection {

        private final ServerPlayer receiver;

        public BlockPosConnection(ServerPlayer receiver) {
            this.receiver = receiver;
        }

        @Override
        public void connect() {
            this.receiver.connection.send(ClientboundTrackedWaypointPacket.addWaypointPosition(uuid, icon, pos));
        }

        @Override
        public void disconnect() {
            this.receiver.connection.send(ClientboundTrackedWaypointPacket.removeWaypoint(uuid));
        }

        @Override
        public void update() {
            this.receiver.connection.send(ClientboundTrackedWaypointPacket.updateWaypointPosition(uuid, icon, pos));
        }

        @Override
        public boolean isBroken() {
            double d = Math.min(range, receiver.getAttributeValue(Attributes.WAYPOINT_RECEIVE_RANGE));
            return receiver.distanceToSqr(pos.getCenter()) >= d;
        }
    }
}
