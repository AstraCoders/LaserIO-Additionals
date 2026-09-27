package com.lucasmellof.laserio_aditionals.integration.pneumaticcraft;

import com.direwolf20.laserio.common.containers.CardEnergyContainer;
import com.direwolf20.laserio.common.containers.customhandler.CardItemHandler;
import com.direwolf20.laserio.common.containers.customslot.CardOverclockSlot;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public final class PressureCardMenu extends CardEnergyContainer {
    public final CardItemHandler cardInventory;

    public PressureCardMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, inventory.player, ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer));
        sourceContainer = BlockPos.STREAM_CODEC.decode(buffer);
        direction = buffer.readByte();
    }

    public PressureCardMenu(int id, Inventory inventory, Player player, ItemStack card) {
        super(PneumaticCraftIntegrationItems.PRESSURE_CARD_MENU.get(), id);
        playerEntity = player;
        playerInventory = new InvWrapper(inventory);
        cardItem = card;
        cardInventory = BaseCard.getInventory(card);
        addSlot(new CardOverclockSlot(cardInventory, 0, 153, 5));
        layoutPlayerInventorySlots(8, 84);
    }

    public PressureCardMenu(int id, Inventory inventory, Player player, BlockPos sourcePos, ItemStack card, byte side) {
        this(id, inventory, player, card);
        sourceContainer = sourcePos;
        direction = side;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = getSlot(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (ItemStack.isSameItemSameComponents(copy, cardItem)
                || !(index == 0
                        ? moveItemStackTo(stack, 1, slots.size(), true)
                        : moveItemStackTo(stack, 0, 1, false))) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, stack);
        return copy;
    }
}
