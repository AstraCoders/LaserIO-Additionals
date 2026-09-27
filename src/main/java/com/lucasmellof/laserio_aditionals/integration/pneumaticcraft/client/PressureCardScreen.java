package com.lucasmellof.laserio_aditionals.integration.pneumaticcraft.client;

import com.direwolf20.laserio.client.screens.widgets.ChannelButton;
import com.direwolf20.laserio.client.screens.widgets.NumberButton;
import com.direwolf20.laserio.client.screens.widgets.ToggleButton;
import com.direwolf20.laserio.common.LaserIO;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.common.items.cards.CardEnergy;
import com.direwolf20.laserio.common.network.data.OpenNodePayload;
import com.direwolf20.laserio.common.network.data.UpdateCardPayload;
import com.direwolf20.laserio.util.MiscTools;
import com.lucasmellof.laserio_aditionals.integration.pneumaticcraft.PressureCard;
import com.lucasmellof.laserio_aditionals.integration.pneumaticcraft.PressureCardMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
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
public final class PressureCardScreen extends AbstractContainerScreen<PressureCardMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(LaserIO.MODID, "textures/gui/energycard.png");
    private static final ResourceLocation ITEM_CARD_BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(LaserIO.MODID, "textures/gui/itemcard.png");
    private static final ResourceLocation[] MODE_TEXTURES = {
        ResourceLocation.fromNamespaceAndPath(LaserIO.MODID, "textures/gui/buttons/modeinserter.png"),
        ResourceLocation.fromNamespaceAndPath(LaserIO.MODID, "textures/gui/buttons/modeextractor.png")
    };

    private final ItemStack card;
    private ToggleButton modeButton;
    private ChannelButton channelButton;
    private NumberButton rateButton;
    private byte mode;
    private byte channel;
    private int rate;

    public PressureCardScreen(PressureCardMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        card = menu.cardItem;
    }

    private static boolean inside(Button button, double mouseX, double mouseY) {
        return MiscTools.inBounds(button.getX(), button.getY(), button.getWidth(), button.getHeight(), mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        mode = (byte) Math.min(BaseCard.getTransferMode(card), 1);
        channel = BaseCard.getChannel(card);
        rate = PressureCard.getRate(card);

        modeButton = new ToggleButton(leftPos + 5, topPos + 5, 16, 16, MODE_TEXTURES, mode, button -> {});
        channelButton = new ChannelButton(leftPos + 25, topPos + 5, 16, 16, channel, button -> {});
        rateButton = new NumberButton(leftPos + 125, topPos + 25, 46, 12, rate, button -> {});
        addRenderableWidget(modeButton);
        addRenderableWidget(channelButton);
        addRenderableWidget(rateButton);
        if (menu.direction != -1) {
            addRenderableWidget(new ExtendedButton(
                    leftPos - 25, topPos + 1, 25, 20, Component.literal("<--"), button -> openNode()));
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (inside(modeButton, mouseX, mouseY)) {
            graphics.renderTooltip(
                    font,
                    Component.translatable(mode == 0 ? "screen.laserio.insert" : "screen.laserio.extract"),
                    mouseX,
                    mouseY);
        } else if (inside(channelButton, mouseX, mouseY)) {
            graphics.renderTooltip(
                    font,
                    Component.translatable("screen.laserio.channel").append(String.valueOf(channel)),
                    mouseX,
                    mouseY);
        } else if (inside(rateButton, mouseX, mouseY)) {
            graphics.renderTooltip(
                    font, Component.translatable("gui.laserio_aditionals.pressure_card.rate"), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        graphics.blit(ITEM_CARD_BACKGROUND, leftPos + 152, topPos + 4, 152, 4, 18, 18);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (inside(modeButton, mouseX, mouseY)) {
            mode = (byte) (mode == 0 ? 1 : 0);
            BaseCard.setTransferMode(card, mode);
            modeButton.setTexturePosition(mode);
            modeButton.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        if (inside(channelButton, mouseX, mouseY)) {
            channel = button == 1 ? BaseCard.previousChannel(card) : BaseCard.nextChannel(card);
            channelButton.setChannel(channel);
            channelButton.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        if (inside(rateButton, mouseX, mouseY)) {
            changeRate(button == 1 ? -100 : 100);
            rateButton.setValue(rate);
            rateButton.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void changeRate(int amount) {
        if (Screen.hasShiftDown()) amount *= 10;
        if (Screen.hasControlDown()) amount *= 100;
        rate = Math.clamp(rate + amount, 100, maxRate());
        PressureCard.setRate(card, rate);
    }

    private int maxRate() {
        return PressureCard.BASE_RATE + menu.getSlot(0).getItem().getCount() * PressureCard.RATE_PER_OVERCLOCKER;
    }

    @Override
    public void onClose() {
        saveSettings();
        super.onClose();
    }

    private void saveSettings() {
        PacketDistributor.sendToServer(new UpdateCardPayload(
                mode,
                channel,
                rate,
                BaseCard.getPriority(card),
                BaseCard.getSneaky(card),
                (short) CardEnergy.getExtractSpeed(card),
                BaseCard.getExact(card),
                BaseCard.getRegulate(card),
                (byte) BaseCard.getRoundRobin(card),
                CardEnergy.getExtractLimitPercent(card),
                CardEnergy.getInsertLimitPercent(card),
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
