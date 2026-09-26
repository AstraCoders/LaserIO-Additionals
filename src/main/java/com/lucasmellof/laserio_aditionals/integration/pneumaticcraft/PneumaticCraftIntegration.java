package com.lucasmellof.laserio_aditionals.integration.pneumaticcraft;

import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.direwolf20.laserio.setup.Registration;
import com.lucasmellof.laserio_aditionals.LaserNodeCardRegistry;
import com.lucasmellof.laserio_aditionals.common.ILaserNodeCardHost;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public final class PneumaticCraftIntegration {
    private PneumaticCraftIntegration() {}

    public static void initialize(IEventBus modBus) {
        LaserNodeCardRegistry.register(PneumaticCraftNodeAdditions::new);
        PneumaticCraftIntegrationItems.init(modBus);
        modBus.addListener(PneumaticCraftIntegration::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                PNCCapabilities.AIR_HANDLER_MACHINE,
                Registration.LaserNode_BE.get(),
                (LaserNodeBE node, Direction side) ->
                        side != null && ILaserNodeCardHost.get(node, PneumaticCraftNodeAdditions.class).hasPressureCard(side)
                                ? ILaserNodeCardHost.get(node, PneumaticCraftNodeAdditions.class).getPressureHandler()
                                : null);
    }

}
