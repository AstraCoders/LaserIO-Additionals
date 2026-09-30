package com.lucasmellof.laserio_aditionals.integration.common;

import com.lucasmellof.laserio_aditionals.LaserioAdditionals;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 30/09/2026
 */
public final class ChannelCardMenuType {
    private static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, LaserioAdditionals.MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<ChannelCardMenu>> CHANNEL_CARD_MENU = MENUS.register(
            "channel_card", () -> IMenuTypeExtension.create(ChannelCardMenu::new));

    private ChannelCardMenuType() {}

    public static void init(IEventBus bus) {
        MENUS.register(bus);
    }
}
