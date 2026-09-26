package com.lucasmellof.laserio_aditionals.common;

import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;

import java.util.Set;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public interface LaserNodeCardExtension {
    default void setNetworkNodes(Set<GlobalPos> nodes) {
    }

    default void tick() {
    }

    default void save(CompoundTag tag) {
    }

    default void load(CompoundTag tag) {
    }

    default void addCardRenders() {
    }

    default void destroy() {
    }
}
