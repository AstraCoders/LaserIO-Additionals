package com.lucasmellof.laserio_aditionals.integration.pneumaticcraft;

import com.direwolf20.laserio.common.items.cards.BaseCard;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public final class PressureCard extends BaseCard {
    public PressureCard() {
        CARDTYPE = CardType.MISSING;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                BaseCard.nextChannel(stack);
                player.displayClientMessage(Component.translatable(
                        "item.laserio_aditionals.pressure_card.selected_channel", BaseCard.getChannel(stack)), true);
            } else {
                byte mode = (byte) (BaseCard.getTransferMode(stack) == BaseCard.TransferMode.EXTRACT.ordinal() ? 0 : 1);
                BaseCard.setTransferMode(stack, mode);
                player.displayClientMessage(Component.translatable(
                        "item.laserio_aditionals.pressure_card.selected_mode",
                        BaseCard.getNamedTransferMode(stack).name()), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
