package com.lucasmellof.laserio_aditionals.integration.refinedstorage;

import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.util.CardRender;
import com.lucasmellof.laserio_aditionals.LaserNodeCardRegistry;
import com.lucasmellof.laserio_aditionals.common.ILaserNodeCardHost;
import com.lucasmellof.laserio_aditionals.common.LaserNodeCardExtension;
import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.network.ConnectionSink;
import com.refinedmods.refinedstorage.common.api.support.network.ConnectionStrategy;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 30/09/2026
 */
public final class RefinedStorageNodeAdditions implements LaserNodeCardExtension {
    private static final float[] RS_BLUE = {0.22f, 0.72f, 0.92f};

    private final LaserNodeBE node;
    private final Set<GlobalPos> networkNodes = new HashSet<>();
    private NetworkNodeContainerProvider provider = RefinedStorageApi.INSTANCE.createNetworkNodeContainerProvider();
    private InWorldNetworkNodeContainer container;
    private int activeChannel = -1;
    private Direction activeSide;
    private boolean connectionsChanged = true;

    public RefinedStorageNodeAdditions(LaserNodeBE node) {
        this.node = node;
    }

    private static void makeBeamStraight(CardRender render, Direction side) {
        Vector3f start = new Vector3f(0.5f, 0.5f, 0.5f);
        Vector3f end = new Vector3f(0.5f, 0.5f, 0.5f);
        switch (side) {
            case DOWN, UP -> end.set(0.5f, render.diffY, 0.5f);
            case NORTH, SOUTH -> end.set(0.5f, 0.5f, render.diffZ);
            case WEST, EAST -> end.set(render.diffX, 0.5f, 0.5f);
        }
        render.startLaser = start;
        render.endLaser = end;
    }

    @Override
    public void setNetworkNodes(Set<GlobalPos> nodes) {
        if (!networkNodes.equals(nodes)) {
            networkNodes.clear();
            networkNodes.addAll(nodes);
            connectionsChanged = true;
        }
    }

    @Override
    public void tick() {
        if (!(node.getLevel() instanceof ServerLevel level)) {
            return;
        }

        var card = findCard();
        if (card.isEmpty()) {
            if (container != null && container.getNode().getNetwork() == null) {
                return;
            }
            removeContainer(level);
            return;
        }

        int channel = BaseCard.getChannel(card.get().stack());
        Direction side = card.get().side();
        if (container == null) {
            activeChannel = channel;
            activeSide = side;
            container = RefinedStorageApi.INSTANCE
                    .createNetworkNodeContainer(node, new SimpleNetworkNode(0))
                    .name("LaserIO Additionals")
                    .connectionStrategy(new ConnectionStrategy() {
                        @Override
                        public void addOutgoingConnections(ConnectionSink sink) {
                            int currentChannel = getCardChannel();
                            Direction currentSide = getCardSide();
                            if (currentChannel < 0 || currentSide == null) {
                                return;
                            }

                            sink.tryConnectInSameDimension(
                                    node.getBlockPos().relative(currentSide), currentSide.getOpposite());
                            for (GlobalPos targetPos : networkNodes) {
                                if (targetPos.equals(GlobalPos.of(level.dimension(), node.getBlockPos()))) {
                                    continue;
                                }
                                ServerLevel targetLevel = level.getServer().getLevel(targetPos.dimension());
                                if (targetLevel == null
                                        || !(targetLevel.getBlockEntity(targetPos.pos())
                                                instanceof LaserNodeBE target)) {
                                    continue;
                                }
                                RefinedStorageNodeAdditions targetIntegration =
                                        ILaserNodeCardHost.get(target, RefinedStorageNodeAdditions.class);
                                if (targetIntegration != null && targetIntegration.getCardChannel() == currentChannel) {
                                    sink.tryConnect(targetPos, null);
                                }
                            }
                        }

                        @Override
                        public boolean canAcceptIncomingConnection(
                                Direction incomingDirection, BlockState connectingState) {
                            return getCardSide() == incomingDirection;
                        }
                    })
                    .build();
            provider.addContainer(container);
            provider.initialize(level, node::setChanged);
            connectionsChanged = false;
        } else if (channel != activeChannel || side != activeSide || connectionsChanged) {
            activeChannel = channel;
            activeSide = side;
            provider.update(level);
            connectionsChanged = false;
        }
    }

    @Override
    public void destroy() {
        removeContainer(node.getLevel());
    }

    @Override
    public void addCardRenders() {
        Level level = node.getLevel();
        if (level == null || !level.isClientSide) {
            return;
        }
        findCard().ifPresent(card -> {
            CardRender render = new CardRender(card.side(), card.slot(), card.stack(), node.getBlockPos(), level, true);
            render.r = RS_BLUE[0];
            render.g = RS_BLUE[1];
            render.b = RS_BLUE[2];
            render.floatcolors = RS_BLUE.clone();
            makeBeamStraight(render, card.side());
            node.cardRenders.add(render);
        });
    }

    public NetworkNodeContainerProvider getContainerProvider() {
        return provider;
    }

    private int getCardChannel() {
        return findCard().map(card -> (int) BaseCard.getChannel(card.stack())).orElse(-1);
    }

    private Direction getCardSide() {
        return findCard().map(LaserNodeCardRegistry.CardSlot::side).orElse(null);
    }

    private Optional<LaserNodeCardRegistry.CardSlot> findCard() {
        for (Direction side : Direction.values()) {
            var card = LaserNodeCardRegistry.find(node, side, RefinedStorageCard.class);
            if (card.isPresent()) {
                return card;
            }
        }
        return Optional.empty();
    }

    private void removeContainer(Level level) {
        if (container != null) {
            provider.remove(level);
            provider = RefinedStorageApi.INSTANCE.createNetworkNodeContainerProvider();
            container = null;
            activeChannel = -1;
            activeSide = null;
        }
    }
}
