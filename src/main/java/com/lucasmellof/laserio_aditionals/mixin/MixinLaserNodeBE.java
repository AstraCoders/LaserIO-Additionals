package com.lucasmellof.laserio_aditionals.mixin;

import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.lucasmellof.laserio_aditionals.common.ILaserNodeCardHost;
import com.lucasmellof.laserio_aditionals.common.LaserNodeCardAdditions;
import com.lucasmellof.laserio_aditionals.common.LaserNodeCardExtension;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
@Mixin(value = LaserNodeBE.class, remap = false)
public abstract class MixinLaserNodeBE implements ILaserNodeCardHost {
    @Unique
    private LaserNodeCardAdditions laserioAdditionals$cards;

    @Inject(method = "setOtherNodesInNetwork", at = @At("TAIL"))
    private void laserioAdditionals$setNetworkNodes(Set<GlobalPos> nodes, CallbackInfo ci) {
        laserio_aditionals$additions().setNetworkNodes(nodes);
    }

    @Inject(method = "tickServer", at = @At("TAIL"))
    private void laserioAdditionals$tick(CallbackInfo ci) {
        laserio_aditionals$additions().tick();
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void laserioAdditionals$save(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        laserio_aditionals$additions().save(tag);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void laserioAdditionals$load(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        laserio_aditionals$additions().load(tag);
    }

    @Inject(method = "populateRenderList", at = @At("TAIL"))
    private void laserioAdditionals$addRenders(CallbackInfo ci) {
        laserio_aditionals$additions().addCardRenders();
    }

    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void laserioAdditionals$remove(CallbackInfo ci) {
        laserio_aditionals$additions().destroy();
    }

    @Override
    public <T extends LaserNodeCardExtension> T laserioAdditionals$getCardIntegration(Class<T> type) {
        return laserio_aditionals$additions().get(type);
    }

    @Unique
    private LaserNodeCardAdditions laserio_aditionals$additions() {
        if (laserioAdditionals$cards == null) {
            laserioAdditionals$cards = new LaserNodeCardAdditions((LaserNodeBE) (Object) this);
        }
        return laserioAdditionals$cards;
    }
}
