package com.lucasmellof.laserio_aditionals;

import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.lucasmellof.laserio_aditionals.common.LaserNodeCardExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public final class LaserNodeCardRegistry {
    private static final List<Function<LaserNodeBE, LaserNodeCardExtension>> FACTORIES = new ArrayList<>();

    private LaserNodeCardRegistry() {}

    public static void register(Function<LaserNodeBE, LaserNodeCardExtension> factory) {
        FACTORIES.add(factory);
    }

    public static List<LaserNodeCardExtension> create(LaserNodeBE node) {
        return FACTORIES.stream().map(factory -> factory.apply(node)).toList();
    }
}
