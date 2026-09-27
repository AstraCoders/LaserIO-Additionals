package com.lucasmellof.laserio_aditionals.mixin;

import com.direwolf20.laserio.common.network.data.OpenCardPayload;
import com.direwolf20.laserio.common.network.handler.PacketOpenCard;
import com.lucasmellof.laserio_aditionals.LaserNodeCardRegistry;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
@Mixin(value = PacketOpenCard.class, remap = false)
public abstract class MixinPacketOpenCard {
    @Inject(method = "lambda$handle$5", at = @At("HEAD"), cancellable = true)
    private void laserioAdditionals$openRegisteredCard(
            IPayloadContext context, OpenCardPayload payload, CallbackInfo ci) {
        if (LaserNodeCardRegistry.openMenu(context.player(), payload.slotNumber())) {
            ci.cancel();
        }
    }
}
