package com.lucasmellof.laserio_aditionals;

import com.direwolf20.laserio.setup.Registration;
import com.lucasmellof.laserio_aditionals.integration.ae2.Ae2IntegrationItems;
import com.lucasmellof.laserio_aditionals.integration.pneumaticcraft.PneumaticCraftIntegrationItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 27/09/2026
 */
public final class LaserioAdditionalsCreativeTab {
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LaserioAdditionals.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register(
            "laserio_aditionals", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.laserio_aditionals"))
                    .icon(LaserioAdditionalsCreativeTab::icon)
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .displayItems((parameters, output) -> {
                        if (Ae2IntegrationItems.CHANNEL_CARD.isBound()) {
                            output.accept(Ae2IntegrationItems.CHANNEL_CARD.get());
                        }
                        if (PneumaticCraftIntegrationItems.PRESSURE_CARD.isBound()) {
                            output.accept(PneumaticCraftIntegrationItems.PRESSURE_CARD.get());
                        }
                    })
                    .build());

    private LaserioAdditionalsCreativeTab() {}

    public static void init(IEventBus bus) {
        TABS.register(bus);
    }

    private static ItemStack icon() {
        return new ItemStack(Registration.LaserConnector.get());
    }
}
