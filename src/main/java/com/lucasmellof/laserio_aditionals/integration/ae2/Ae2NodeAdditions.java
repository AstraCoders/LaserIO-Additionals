package com.lucasmellof.laserio_aditionals.integration.ae2;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AECableType;
import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.direwolf20.laserio.util.CardRender;
import com.lucasmellof.laserio_aditionals.common.LaserNodeCardExtension;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public class Ae2NodeAdditions implements LaserNodeCardExtension, IInWorldGridNodeHost {
    private static final float[] FLUIX_PURPLE = {0.60f, 0.43f, 0.88f};

    private final LaserNodeBE node;
    private final Map<Direction, IManagedGridNode> nodes = new EnumMap<>(Direction.class);
    private final Set<GlobalPos> networkNodes = new HashSet<>();

    public Ae2NodeAdditions(LaserNodeBE node) {
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
        networkNodes.clear();
        networkNodes.addAll(nodes);
    }

    @Override
    public void tick() {
        AeChannelBridge.sync(this, nodes, networkNodes);
    }

    @Override
    public void destroy() {
        AeChannelBridge.destroy(nodes);
    }

    @Override
    public @Nullable IGridNode getGridNode(Direction direction) {
        return AeChannelBridge.getGridNode(nodes, direction);
    }

    @Override
    public AECableType getCableConnectionType(Direction direction) {
        return AeChannelBridge.getCableType(nodes, direction);
    }

    public Map<Direction, IManagedGridNode> getNodes() {
        return nodes;
    }

    LaserNodeBE getLaserNode() {
        return node;
    }

    @Override
    public void addCardRenders() {
        Level level = node.getLevel();
        if (level == null || !level.isClientSide) {
            return;
        }
        for (Direction side : Direction.values()) {
            IItemHandler cards = level.getCapability(Capabilities.ItemHandler.BLOCK, node.getBlockPos(), side);
            if (cards == null) {
                continue;
            }
            for (int slot = 0; slot < cards.getSlots(); slot++) {
                ItemStack card = cards.getStackInSlot(slot);
                if (!(card.getItem() instanceof ChannelCard)) {
                    continue;
                }
                CardRender render = new CardRender(side, slot, card, node.getBlockPos(), level, true);
                render.r = FLUIX_PURPLE[0];
                render.g = FLUIX_PURPLE[1];
                render.b = FLUIX_PURPLE[2];
                render.floatcolors = FLUIX_PURPLE.clone();
                makeBeamStraight(render, side);
                node.cardRenders.add(render);
            }
        }
    }
}
