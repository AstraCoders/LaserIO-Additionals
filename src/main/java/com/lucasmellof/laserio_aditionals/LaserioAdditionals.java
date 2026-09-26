package com.lucasmellof.laserio_aditionals;

import com.mojang.logging.LogUtils;
import com.lucasmellof.laserio_aditionals.integration.ae2.Ae2Integration;
import com.lucasmellof.laserio_aditionals.integration.pneumaticcraft.PneumaticCraftIntegration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(LaserioAdditionals.MODID)
public final class LaserioAdditionals {
    public static final String MODID = "laserio_aditionals";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LaserioAdditionals(IEventBus modBus, ModContainer container) {
        if (ModList.get().isLoaded("ae2")) {
            Ae2Integration.initialize(modBus);
        }
        if (ModList.get().isLoaded("pneumaticcraft")) {
            PneumaticCraftIntegration.initialize(modBus);
        }
        LOGGER.info("Loading LaserIO Additionals");
    }
}
