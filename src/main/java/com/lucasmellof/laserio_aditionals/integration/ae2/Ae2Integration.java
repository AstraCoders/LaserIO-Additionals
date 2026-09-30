package com.lucasmellof.laserio_aditionals.integration.ae2;

import appeng.api.AECapabilities;
import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.direwolf20.laserio.setup.Registration;
import com.lucasmellof.laserio_aditionals.LaserNodeCardRegistry;
import com.lucasmellof.laserio_aditionals.common.ILaserNodeCardHost;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public class Ae2Integration {
    private Ae2Integration() {}

    public static void initialize(IEventBus modBus) {
        LaserNodeCardRegistry.register(Ae2NodeAdditions::new);
        Ae2IntegrationItems.init(modBus);
        modBus.addListener(Ae2Integration::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                Registration.LaserNode_BE.get(),
                (LaserNodeBE node, Void context) -> ILaserNodeCardHost.get(node, Ae2NodeAdditions.class));
    }
}
