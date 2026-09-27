package com.lucasmellof.laserio_aditionals.integration.pneumaticcraft;

import com.direwolf20.laserio.common.items.cards.CardEnergy;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.util.MiscTools;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
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
public final class PressureCard extends CardEnergy {
    public static final int BASE_RATE = 10_000;
    public static final int RATE_PER_OVERCLOCKER = 2_500;

    public PressureCard() {
        CARDTYPE = CardType.MISSING;
    }

    public static int getRate(ItemStack stack) {
        return Math.min(CardEnergy.getEnergyExtractAmt(stack), getMaxRate(stack));
    }

    public static int setRate(ItemStack stack, int rate) {
        return CardEnergy.setEnergyExtractAmt(stack, Math.clamp(rate, 100, getMaxRate(stack)));
    }

    public static int getMaxRate(ItemStack stack) {
        int overclockers = BaseCard.getInventory(stack).getStackInSlot(0).getCount();
        return BASE_RATE + overclockers * RATE_PER_OVERCLOCKER;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.openMenu(new SimpleMenuProvider(
                    (id, inventory, ignored) -> new PressureCardMenu(id, inventory, player, stack),
                    stack.getHoverName()), buffer -> {
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, stack);
                BlockPos.STREAM_CODEC.encode(buffer, BlockPos.ZERO);
                buffer.writeByte(-1);
            });
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (Screen.hasShiftDown()) {
            MutableComponent line = MiscTools.tooltipMaker(
                    "laserio_aditionals.tooltip.card.rate", ChatFormatting.GRAY.getColor());
            line.append(Component.literal(String.valueOf(getRate(stack))).withStyle(ChatFormatting.AQUA));
            tooltip.add(line);
        }
    }
}
