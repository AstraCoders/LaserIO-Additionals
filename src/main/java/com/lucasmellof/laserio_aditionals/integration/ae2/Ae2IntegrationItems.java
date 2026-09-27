package com.lucasmellof.laserio_aditionals.integration.ae2;

import com.lucasmellof.laserio_aditionals.LaserioAdditionals;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public class Ae2IntegrationItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, LaserioAdditionals.MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, LaserioAdditionals.MODID);
    public static final DeferredHolder<Item, ChannelCard> CHANNEL_CARD = ITEMS.register("channel_card", ChannelCard::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ChannelCardMenu>> CHANNEL_CARD_MENU = MENUS.register(
            "channel_card", () -> IMenuTypeExtension.create(ChannelCardMenu::new));

    public static void init(IEventBus bus) {
        ITEMS.register(bus);
        MENUS.register(bus);
    }
}
