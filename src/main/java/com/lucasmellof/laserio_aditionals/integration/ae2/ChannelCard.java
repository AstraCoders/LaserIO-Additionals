package com.lucasmellof.laserio_aditionals.integration.ae2;

import com.direwolf20.laserio.common.items.cards.BaseCard;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public final class ChannelCard extends BaseCard {
    public ChannelCard() {
        CARDTYPE = CardType.MISSING;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }
        if (!level.isClientSide) {
            BaseCard.setChannel(stack, (byte) ((BaseCard.getChannel(stack) + 1) & 15));
            player.displayClientMessage(Component.translatable(
                    "item.laserio_aditionals.channel_card.selected_channel", BaseCard.getChannel(stack)), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.laserio_aditionals.channel_card.linked_channel", BaseCard.getChannel(stack)));
        tooltip.add(Component.translatable("item.laserio_aditionals.channel_card.configuration"));
    }
}
