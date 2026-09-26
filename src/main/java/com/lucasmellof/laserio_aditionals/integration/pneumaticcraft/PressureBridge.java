package com.lucasmellof.laserio_aditionals.integration.pneumaticcraft;

import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.direwolf20.laserio.common.containers.LaserNodeContainer;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.lucasmellof.laserio_aditionals.common.ILaserNodeCardHost;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerMachine;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public final class PressureBridge {
    private PressureBridge() {}

    public static List<IAirHandlerMachine> findRemoteHandlers(
            LaserNodeBE sourceNode, Set<GlobalPos> networkNodes, IAirHandlerMachine sourceHandler) {
        Set<IAirHandlerMachine> handlers = new LinkedHashSet<>();
        if (!(sourceNode.getLevel() instanceof ServerLevel sourceLevel)) {
            return List.of();
        }
        MinecraftServer server = sourceLevel.getServer();
        for (PressureEndpoint source : endpointsFor(sourceNode, sourceLevel)) {
            if (source.mode != BaseCard.TransferMode.EXTRACT) {
                continue;
            }
            for (GlobalPos position : networkNodes) {
                ServerLevel targetLevel = server.getLevel(position.dimension());
                if (targetLevel == null
                        || !(targetLevel.getBlockEntity(position.pos()) instanceof LaserNodeBE targetNode)) {
                    continue;
                }
                for (PressureEndpoint destination : endpointsFor(targetNode, targetLevel)) {
                    if (destination.mode != BaseCard.TransferMode.INSERT || destination.channel != source.channel) {
                        continue;
                    }
                    IAirHandlerMachine targetHandler = destination.additions.getPressureHandler();
                    if (targetHandler == null || targetHandler == sourceHandler) {
                        continue;
                    }
                    handlers.add(targetHandler);
                }
            }
        }
        return List.copyOf(handlers);
    }

    private static List<PressureEndpoint> endpointsFor(LaserNodeBE node, ServerLevel level) {
        List<PressureEndpoint> endpoints = new ArrayList<>();
        if (!(node instanceof ILaserNodeCardHost host)) {
            return endpoints;
        }
        PneumaticCraftNodeAdditions additions =
                host.laserioAdditionals$getCardIntegration(PneumaticCraftNodeAdditions.class);
        for (Direction side : Direction.values()) {
            for (int slot = 0; slot < LaserNodeContainer.CARDSLOTS; slot++) {
                ItemStack stack = node.nodeSideCaches[side.ordinal()].itemHandler.getStackInSlot(slot);
                if (stack.getItem() instanceof PressureCard) {
                    endpoints.add(new PressureEndpoint(
                            node,
                            additions,
                            level,
                            side,
                            BaseCard.getChannel(stack),
                            BaseCard.getNamedTransferMode(stack)));
                }
            }
        }
        return endpoints;
    }

    private record PressureEndpoint(
            LaserNodeBE node,
            PneumaticCraftNodeAdditions additions,
            ServerLevel level,
            Direction side,
            byte channel,
            BaseCard.TransferMode mode) {}
}
