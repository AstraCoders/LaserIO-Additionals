package com.lucasmellof.laserio_aditionals.common;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public interface ILaserNodeCardHost {
    <T extends LaserNodeCardExtension> T laserioAdditionals$getCardIntegration(Class<T> type);

    static <T extends LaserNodeCardExtension> T get(Object self, Class<T> type) {
        if (self instanceof ILaserNodeCardHost host) {
            return host.laserioAdditionals$getCardIntegration(type);
        }
        return null;
    }
}
