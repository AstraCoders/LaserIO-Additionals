package com.lucasmellof.laserio_aditionals.integration.pneumaticcraft;

import com.lucasmellof.laserio_aditionals.LaserioAdditionals;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public class PneumaticCraftIntegrationItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, LaserioAdditionals.MODID);
    public static final DeferredHolder<Item, PressureCard> PRESSURE_CARD = ITEMS.register("pressure_card", PressureCard::new);

    private PneumaticCraftIntegrationItems() {
    }

    public static void init(IEventBus bus) {
        ITEMS.register(bus);
    }
}
