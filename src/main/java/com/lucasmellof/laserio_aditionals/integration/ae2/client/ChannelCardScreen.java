package com.lucasmellof.laserio_aditionals.integration.ae2.client;

import com.direwolf20.laserio.client.screens.widgets.ChannelButton;
import com.direwolf20.laserio.common.LaserIO;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.common.network.data.OpenNodePayload;
import com.direwolf20.laserio.common.network.data.UpdateCardPayload;
import com.direwolf20.laserio.util.MiscTools;
import com.lucasmellof.laserio_aditionals.integration.common.ChannelCardMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.network.PacketDistributor;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public final class ChannelCardScreen extends AbstractContainerScreen<ChannelCardMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(LaserIO.MODID, "textures/gui/energycard.png");

    private final ItemStack card;
    private ChannelButton channelButton;
    private byte channel;

    public ChannelCardScreen(ChannelCardMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        card = menu.cardItem;
    }

    @Override
    protected void init() {
        super.init();
        channel = BaseCard.getChannel(card);
        channelButton = new ChannelButton(leftPos + 5, topPos + 5, 16, 16, channel, button -> {});
        addRenderableWidget(channelButton);
        if (menu.direction != -1) {
            addRenderableWidget(new ExtendedButton(
                    leftPos - 25, topPos + 1, 25, 20, Component.literal("<--"), button -> openNode()));
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (MiscTools.inBounds(
                channelButton.getX(),
                channelButton.getY(),
                channelButton.getWidth(),
                channelButton.getHeight(),
                mouseX,
                mouseY)) {
            graphics.renderTooltip(
                    font,
                    Component.translatable("screen.laserio.channel").append(String.valueOf(channel)),
                    mouseX,
                    mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MiscTools.inBounds(
                channelButton.getX(),
                channelButton.getY(),
                channelButton.getWidth(),
                channelButton.getHeight(),
                mouseX,
                mouseY)) {
            channel = button == 1 ? BaseCard.previousChannel(card) : BaseCard.nextChannel(card);
            channelButton.setChannel(channel);
            channelButton.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        saveSettings();
        super.onClose();
    }

    private void saveSettings() {
        PacketDistributor.sendToServer(new UpdateCardPayload(
                BaseCard.getTransferMode(card),
                channel,
                0,
                BaseCard.getPriority(card),
                BaseCard.getSneaky(card),
                (short) BaseCard.getExtractSpeed(card),
                BaseCard.getExact(card),
                BaseCard.getRegulate(card),
                (byte) BaseCard.getRoundRobin(card),
                0,
                0,
                BaseCard.getRedstoneMode(card),
                BaseCard.getRedstoneChannel(card),
                BaseCard.getAnd(card)));
    }

    private void openNode() {
        saveSettings();
        PacketDistributor.sendToServer(new OpenNodePayload(menu.sourceContainer, menu.direction));
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
