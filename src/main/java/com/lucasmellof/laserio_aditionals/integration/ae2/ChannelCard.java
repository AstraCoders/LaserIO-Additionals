package com.lucasmellof.laserio_aditionals.integration.ae2;

import com.direwolf20.laserio.client.blockentityrenders.LaserNodeBERender;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.util.MiscTools;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
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
public final class ChannelCard extends BaseCard {
    public ChannelCard() {
        CARDTYPE = CardType.MISSING;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.openMenu(new SimpleMenuProvider(
                    (id, inventory, ignored) -> new ChannelCardMenu(id, inventory, player, stack),
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
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }
        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("laserio.tooltip.item.show_settings").withStyle(ChatFormatting.GRAY));
            return;
        }

        int channel = BaseCard.getChannel(stack);
        MutableComponent line = MiscTools.tooltipMaker(
                "laserio.tooltip.item.card.channel", ChatFormatting.GRAY.getColor());
        line.append(MiscTools.tooltipMaker(String.valueOf(channel), LaserNodeBERender.colors[channel].getRGB()));
        tooltip.add(line);
    }
}
