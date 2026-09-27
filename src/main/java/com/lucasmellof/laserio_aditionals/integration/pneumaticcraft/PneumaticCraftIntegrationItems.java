package com.lucasmellof.laserio_aditionals.integration.pneumaticcraft;

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
public class PneumaticCraftIntegrationItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, LaserioAdditionals.MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, LaserioAdditionals.MODID);
    public static final DeferredHolder<Item, PressureCard> PRESSURE_CARD = ITEMS.register("pressure_card", PressureCard::new);
    public static final DeferredHolder<MenuType<?>, MenuType<PressureCardMenu>> PRESSURE_CARD_MENU = MENUS.register(
            "pressure_card", () -> IMenuTypeExtension.create(PressureCardMenu::new));

    private PneumaticCraftIntegrationItems() {
    }

    public static void init(IEventBus bus) {
        ITEMS.register(bus);
        MENUS.register(bus);
    }
}
