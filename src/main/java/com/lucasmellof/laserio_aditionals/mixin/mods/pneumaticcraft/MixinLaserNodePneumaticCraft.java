package com.lucasmellof.laserio_aditionals.mixin.mods.pneumaticcraft;

import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.lucasmellof.laserio_aditionals.common.ILaserNodeCardHost;
import com.lucasmellof.laserio_aditionals.integration.pneumaticcraft.PneumaticCraftNodeAdditions;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerMachine;
import me.desht.pneumaticcraft.api.tileentity.IAirListener;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
@Mixin(value = LaserNodeBE.class, remap = false)
public abstract class MixinLaserNodePneumaticCraft implements IAirListener {
    @Override
    public List<IAirHandlerMachine> addConnectedPneumatics(List<IAirHandlerMachine> handlers) {
        return ILaserNodeCardHost.get(this, PneumaticCraftNodeAdditions.class).addConnectedPneumatics(handlers);
    }

    @Override
    public int getMaxDispersion(IAirHandlerMachine handler, Direction side) {
        return ILaserNodeCardHost.get(this, PneumaticCraftNodeAdditions.class).getMaxDispersion(handler, side);
    }
}
