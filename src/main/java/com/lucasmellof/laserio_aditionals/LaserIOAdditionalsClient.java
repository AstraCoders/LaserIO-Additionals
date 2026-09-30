package com.lucasmellof.laserio_aditionals;

import com.lucasmellof.laserio_aditionals.integration.ae2.client.ChannelCardScreen;
import com.lucasmellof.laserio_aditionals.integration.common.ChannelCardMenuType;
import com.lucasmellof.laserio_aditionals.integration.pneumaticcraft.PneumaticCraftIntegrationItems;
import com.lucasmellof.laserio_aditionals.integration.pneumaticcraft.client.PressureCardScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
@EventBusSubscriber(modid = LaserioAdditionals.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class LaserIOAdditionalsClient {
    @SubscribeEvent
    static void onRegisterScreen(RegisterMenuScreensEvent event) {
        if (ModList.get().isLoaded("pneumaticcraft")) {
            event.register(PneumaticCraftIntegrationItems.PRESSURE_CARD_MENU.get(), PressureCardScreen::new);
        }
        event.register(ChannelCardMenuType.CHANNEL_CARD_MENU.get(), ChannelCardScreen::new);
    }
}
