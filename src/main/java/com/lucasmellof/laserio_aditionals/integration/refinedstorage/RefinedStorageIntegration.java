package com.lucasmellof.laserio_aditionals.integration.refinedstorage;

import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.direwolf20.laserio.setup.Registration;
import com.lucasmellof.laserio_aditionals.LaserNodeCardRegistry;
import com.lucasmellof.laserio_aditionals.common.ILaserNodeCardHost;
import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 30/09/2026
 */
public final class RefinedStorageIntegration {
    private RefinedStorageIntegration() {}

    public static void initialize(IEventBus modBus) {
        LaserNodeCardRegistry.register(RefinedStorageNodeAdditions::new);
        RefinedStorageIntegrationItems.init(modBus);
        modBus.addListener(RefinedStorageIntegration::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
                Registration.LaserNode_BE.get(),
                (LaserNodeBE node, Direction side) -> {
                    RefinedStorageNodeAdditions additions =
                            ILaserNodeCardHost.get(node, RefinedStorageNodeAdditions.class);
                    return additions == null ? null : additions.getContainerProvider();
                });
    }
}
