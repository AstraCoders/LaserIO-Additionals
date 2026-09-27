package com.lucasmellof.laserio_aditionals;

import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.direwolf20.laserio.common.containers.LaserNodeContainer;
import com.lucasmellof.laserio_aditionals.common.LaserNodeCardExtension;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public final class LaserNodeCardRegistry {
    private static final List<Function<LaserNodeBE, LaserNodeCardExtension>> FACTORIES = new ArrayList<>();
    private static final List<CardMenuEntry> CARD_MENUS = new ArrayList<>();

    private LaserNodeCardRegistry() {}

    public static void register(Function<LaserNodeBE, LaserNodeCardExtension> factory) {
        FACTORIES.add(factory);
    }

    public static List<LaserNodeCardExtension> create(LaserNodeBE node) {
        return FACTORIES.stream().map(factory -> factory.apply(node)).toList();
    }

    public static void registerMenu(Class<? extends Item> cardType, CardMenuFactory factory) {
        CARD_MENUS.add(new CardMenuEntry(cardType, factory));
    }

    public static boolean openMenu(Player player, int slotNumber) {
        if (!(player.containerMenu instanceof LaserNodeContainer nodeMenu)
                || slotNumber < 0 || slotNumber >= nodeMenu.slots.size()) {
            return false;
        }
        ItemStack stack = nodeMenu.getSlot(slotNumber).getItem();
        CardMenuEntry entry = CARD_MENUS.stream()
                .filter(candidate -> candidate.cardType.isInstance(stack.getItem()))
                .findFirst()
                .orElse(null);
        if (entry == null) {
            return false;
        }

        BlockPos sourcePos = nodeMenu.tile.getBlockPos();
        byte side = nodeMenu.side;
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return stack.getHoverName();
            }

            @Override
            public boolean shouldTriggerClientSideContainerClosingOnOpen() {
                return false;
            }

            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inventory, Player menuPlayer) {
                return entry.factory.create(id, inventory, menuPlayer, sourcePos, stack, side);
            }
        }, buffer -> {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, stack);
            BlockPos.STREAM_CODEC.encode(buffer, sourcePos);
            buffer.writeByte(side);
        });
        return true;
    }

    /** Reads LaserIO's per-side card inventory, excluding its overclocker slot. */
    public static Optional<CardSlot> find(LaserNodeBE node, Direction side, Class<? extends Item> cardType) {
        for (int slot = 0; slot < LaserNodeContainer.CARDSLOTS; slot++) {
            ItemStack stack = node.nodeSideCaches[side.ordinal()].itemHandler.getStackInSlot(slot);
            if (cardType.isInstance(stack.getItem())) {
                return Optional.of(new CardSlot(side, slot, stack));
            }
        }
        return Optional.empty();
    }

    public static void forEach(LaserNodeBE node, Class<? extends Item> cardType, Consumer<CardSlot> action) {
        for (Direction side : Direction.values()) {
            for (int slot = 0; slot < LaserNodeContainer.CARDSLOTS; slot++) {
                ItemStack stack = node.nodeSideCaches[side.ordinal()].itemHandler.getStackInSlot(slot);
                if (cardType.isInstance(stack.getItem())) {
                    action.accept(new CardSlot(side, slot, stack));
                }
            }
        }
    }

    public record CardSlot(Direction side, int slot, ItemStack stack) {}

    public interface CardMenuFactory {
        AbstractContainerMenu create(
                int id, Inventory inventory, Player player, BlockPos sourcePos, ItemStack card, byte side);
    }

    private record CardMenuEntry(Class<? extends Item> cardType, CardMenuFactory factory) {}
}
