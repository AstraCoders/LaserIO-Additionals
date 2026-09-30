package com.lucasmellof.laserio_aditionals.integration.refinedstorage;

import com.lucasmellof.laserio_aditionals.LaserioAdditionals;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 30/09/2026
 */
public final class RefinedStorageIntegrationItems {
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, LaserioAdditionals.MODID);
    public static final DeferredHolder<Item, RefinedStorageCard> REFINED_STORAGE_CARD =
            ITEMS.register("refined_storage_card", RefinedStorageCard::new);

    private RefinedStorageIntegrationItems() {}

    public static void init(IEventBus bus) {
        ITEMS.register(bus);
    }
}
