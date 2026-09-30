package com.lucasmellof.laserio_aditionals.integration.common;

import com.direwolf20.laserio.common.containers.CardEnergyContainer;
import com.lucasmellof.laserio_aditionals.integration.common.ChannelCardMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 30/09/2026
 */
public final class ChannelCardMenu extends CardEnergyContainer {
    public ChannelCardMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, inventory.player, ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer));
        sourceContainer = BlockPos.STREAM_CODEC.decode(buffer);
        direction = buffer.readByte();
    }

    public ChannelCardMenu(int id, Inventory inventory, Player player, ItemStack card) {
        super(ChannelCardMenuType.CHANNEL_CARD_MENU.get(), id);
        playerEntity = player;
        playerInventory = new InvWrapper(inventory);
        cardItem = card;
        layoutPlayerInventorySlots(8, 84);
    }

    public ChannelCardMenu(int id, Inventory inventory, Player player, BlockPos sourcePos, ItemStack card, byte side) {
        this(id, inventory, player, card);
        sourceContainer = sourcePos;
        direction = side;
    }
}
