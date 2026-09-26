package com.lucasmellof.laserio_aditionals.integration.pneumaticcraft;

import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.direwolf20.laserio.common.containers.LaserNodeContainer;
import com.direwolf20.laserio.util.CardRender;
import com.lucasmellof.laserio_aditionals.common.LaserNodeCardExtension;
import me.desht.pneumaticcraft.api.pressure.PressureTier;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerMachine;
import me.desht.pneumaticcraft.common.capabilities.MachineAirHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public final class PneumaticCraftNodeAdditions implements LaserNodeCardExtension {
    private static final String PRESSURE_NBT_KEY = "laserioAdditionalsPressure";
    private static final float[] PRESSURE_GRAY = {0.62f, 0.62f, 0.66f};

    private final LaserNodeBE node;
    private final Set<GlobalPos> networkNodes = new HashSet<>();
    private final Set<Direction> pressureFaces = EnumSet.noneOf(Direction.class);
    private MachineAirHandler pressureHandler;
    private int lastPressureAir = Integer.MIN_VALUE;

    public PneumaticCraftNodeAdditions(LaserNodeBE node) {
        this.node = node;
    }

    @Override
    public void setNetworkNodes(Set<GlobalPos> nodes) {
        networkNodes.clear();
        networkNodes.addAll(nodes);
    }

    @Override
    public void save(CompoundTag tag) {
        tag.put(PRESSURE_NBT_KEY, pressureHandler().serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains(PRESSURE_NBT_KEY, Tag.TAG_COMPOUND)) {
            pressureHandler().deserializeNBT(tag.getCompound(PRESSURE_NBT_KEY));
            lastPressureAir = Integer.MIN_VALUE;
        }
    }

    public IAirHandlerMachine getPressureHandler() {
        return pressureHandler();
    }

    public boolean hasPressureCard(Direction side) {
        for (int slot = 0; slot < LaserNodeContainer.CARDSLOTS; slot++) {
            if (node.nodeSideCaches[side.ordinal()].itemHandler.getStackInSlot(slot).getItem() instanceof PressureCard) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick() {
        Set<Direction> activeFaces = EnumSet.noneOf(Direction.class);
        for (Direction side : Direction.values()) {
            if (hasPressureCard(side)) {
                activeFaces.add(side);
            }
        }
        MachineAirHandler handler = pressureHandler();
        if (!activeFaces.equals(pressureFaces)) {
            pressureFaces.clear();
            pressureFaces.addAll(activeFaces);
            handler.setConnectableFaces(activeFaces);
            if (node.getLevel() != null) {
                node.getLevel().invalidateCapabilities(node.getBlockPos());
            }
        }
        int before = handler.getAir();
        if (lastPressureAir != before) {
            node.setChanged();
        }
        handler.tick(node);
        if (before != handler.getAir()) {
            node.setChanged();
        }
        lastPressureAir = handler.getAir();
    }

    public List<IAirHandlerMachine> addConnectedPneumatics(List<IAirHandlerMachine> handlers) {
        handlers.addAll(PressureBridge.findRemoteHandlers(node, networkNodes, pressureHandler()));
        return handlers;
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
                if (!(card.getItem() instanceof PressureCard)) {
                    continue;
                }
                CardRender render = new CardRender(side, slot, card, node.getBlockPos(), level, true);
                render.r = PRESSURE_GRAY[0];
                render.g = PRESSURE_GRAY[1];
                render.b = PRESSURE_GRAY[2];
                render.floatcolors = PRESSURE_GRAY.clone();
                node.cardRenders.add(render);
            }
        }
    }

    private MachineAirHandler pressureHandler() {
        if (pressureHandler == null) {
            pressureHandler = new MachineAirHandler(PressureTier.TIER_TWO, 4_000);
        }
        return pressureHandler;
    }
}
