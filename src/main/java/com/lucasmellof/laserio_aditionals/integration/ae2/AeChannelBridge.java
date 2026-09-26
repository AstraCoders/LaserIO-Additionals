package com.lucasmellof.laserio_aditionals.integration.ae2;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AECableType;
import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.direwolf20.laserio.common.containers.LaserNodeContainer;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.lucasmellof.laserio_aditionals.common.ILaserNodeCardHost;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public class AeChannelBridge {
    private static final Map<BridgePair, IGridConnection> CONNECTIONS = new HashMap<>();
    private static final IGridNodeListener<Ae2NodeAdditions> NODE_LISTENER =
            (host, gridNode) -> host.getLaserNode().setChanged();

    public static void sync(Ae2NodeAdditions host, Map<Direction, IManagedGridNode> nodes, Set<GlobalPos> networkNodes) {
        LaserNodeBE laserNode = host.getLaserNode();
        if (!(laserNode.getLevel() instanceof ServerLevel level)) {
            return;
        }

        syncLocalNodes(host, nodes, level, laserNode.getBlockPos());
        List<BridgeEndpoint> localEndpoints = endpointsFor(host, nodes);
        Set<BridgePair> expected = new HashSet<>();
        MinecraftServer server = level.getServer();

        for (BridgeEndpoint source : localEndpoints) {
            for (GlobalPos position : networkNodes) {
                ServerLevel targetLevel = server.getLevel(position.dimension());
                if (targetLevel == null || !(targetLevel.getBlockEntity(position.pos()) instanceof LaserNodeBE)) {
                    continue;
                }
                Ae2NodeAdditions target = ILaserNodeCardHost.get(targetLevel, Ae2NodeAdditions.class);
                for (BridgeEndpoint destination : endpointsFor(target, target.getNodes())) {
                    if (source.node == destination.node || source.channel != destination.channel) {
                        continue;
                    }
                    BridgePair pair = BridgePair.of(source, destination);
                    expected.add(pair);
                    connect(pair, source, destination);
                }
            }
        }

        Set<IGridNode> localNodes = new HashSet<>();
        for (BridgeEndpoint endpoint : localEndpoints) {
            localNodes.add(endpoint.node);
        }
        CONNECTIONS.entrySet().removeIf(entry -> {
            BridgePair pair = entry.getKey();
            if (!localNodes.contains(pair.first.node) && !localNodes.contains(pair.second.node)) {
                return false;
            }
            if (expected.contains(pair)) {
                return false;
            }
            entry.getValue().destroy();
            return true;
        });
    }

    public static void destroy(Map<Direction, IManagedGridNode> nodes) {
        for (IManagedGridNode managedNode : nodes.values()) {
            IGridNode gridNode = managedNode.getNode();
            if (gridNode != null) {
                removeConnectionsFor(gridNode);
            }
            managedNode.destroy();
        }
        nodes.clear();
    }

    public static @Nullable IGridNode getGridNode(Map<Direction, IManagedGridNode> nodes, Direction direction) {
        if (nodes == null) {
            return null;
        }
        IManagedGridNode node = nodes.get(direction);
        return node == null ? null : node.getNode();
    }

    public static AECableType getCableType(Map<Direction, IManagedGridNode> nodes, Direction direction) {
        return AECableType.DENSE_SMART;
    }

    private static void syncLocalNodes(Ae2NodeAdditions host, Map<Direction, IManagedGridNode> nodes,
            ServerLevel level, BlockPos pos) {
        for (Direction side : Direction.values()) {
            if (!hasChannelCard(host.getLaserNode(), side)) {
                IManagedGridNode removed = nodes.remove(side);
                if (removed != null) {
                    IGridNode gridNode = removed.getNode();
                    if (gridNode != null) {
                        removeConnectionsFor(gridNode);
                    }
                    removed.destroy();
                }
                continue;
            }
            if (!nodes.containsKey(side)) {
                IManagedGridNode managedNode = GridHelper.createManagedNode(host, NODE_LISTENER)
                        .setInWorldNode(true)
                        .setExposedOnSides(EnumSet.of(side))
                        .setFlags(GridFlags.DENSE_CAPACITY, GridFlags.PREFERRED)
                        .setVisualRepresentation(Ae2IntegrationItems.CHANNEL_CARD.get());
                managedNode.create(level, pos);
                nodes.put(side, managedNode);
            }
        }
    }

    private static boolean hasChannelCard(LaserNodeBE node, Direction side) {
        for (int slot = 0; slot < LaserNodeContainer.CARDSLOTS; slot++) {
            if (node.nodeSideCaches[side.ordinal()].itemHandler.getStackInSlot(slot).getItem() instanceof ChannelCard) {
                return true;
            }
        }
        return false;
    }

    private static List<BridgeEndpoint> endpointsFor(Ae2NodeAdditions host, Map<Direction, IManagedGridNode> nodes) {
        List<BridgeEndpoint> endpoints = new ArrayList<>();
        LaserNodeBE laserNode = host.getLaserNode();
        for (Map.Entry<Direction, IManagedGridNode> entry : nodes.entrySet()) {
            IGridNode gridNode = entry.getValue().getNode();
            int channel = findChannel(laserNode, entry.getKey());
            if (gridNode != null && channel >= 0) {
                endpoints.add(new BridgeEndpoint(gridNode, entry.getKey(), channel));
            }
        }
        return endpoints;
    }

    private static int findChannel(LaserNodeBE node, Direction side) {
        for (int slot = 0; slot < LaserNodeContainer.CARDSLOTS; slot++) {
            ItemStack card = node.nodeSideCaches[side.ordinal()].itemHandler.getStackInSlot(slot);
            if (card.getItem() instanceof ChannelCard) {
                return BaseCard.getChannel(card);
            }
        }
        return -1;
    }

    private static void connect(BridgePair pair, BridgeEndpoint source, BridgeEndpoint destination) {
        if (!CONNECTIONS.containsKey(pair)) {
            CONNECTIONS.put(pair, GridHelper.createConnection(source.node, destination.node));
        }
    }

    private static void removeConnectionsFor(IGridNode gridNode) {
        CONNECTIONS.entrySet().removeIf(entry -> {
            if (entry.getKey().first.node != gridNode && entry.getKey().second.node != gridNode) {
                return false;
            }
            entry.getValue().destroy();
            return true;
        });
    }

    private record BridgeEndpoint(IGridNode node, Direction side, int channel) {
    }

    private record BridgePair(BridgeEndpoint first, BridgeEndpoint second) {
        static BridgePair of(BridgeEndpoint left, BridgeEndpoint right) {
            return System.identityHashCode(left.node) <= System.identityHashCode(right.node)
                    ? new BridgePair(left, right)
                    : new BridgePair(right, left);
        }
    }
}
