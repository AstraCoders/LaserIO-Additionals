package com.lucasmellof.laserio_aditionals.common;

import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.lucasmellof.laserio_aditionals.LaserNodeCardRegistry;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Set;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public final class LaserNodeCardAdditions {
    private final List<LaserNodeCardExtension> extensions;

    public LaserNodeCardAdditions(LaserNodeBE node) {
        extensions = LaserNodeCardRegistry.create(node);
    }

    public void setNetworkNodes(Set<GlobalPos> nodes) {
        extensions.forEach(extension -> extension.setNetworkNodes(nodes));
    }

    public void tick() {
        extensions.forEach(LaserNodeCardExtension::tick);
    }

    public void save(CompoundTag tag) {
        extensions.forEach(extension -> extension.save(tag));
    }

    public void load(CompoundTag tag) {
        extensions.forEach(extension -> extension.load(tag));
    }

    public void addCardRenders() {
        extensions.forEach(LaserNodeCardExtension::addCardRenders);
    }

    public void destroy() {
        extensions.forEach(LaserNodeCardExtension::destroy);
    }

    public <T extends LaserNodeCardExtension> T get(Class<T> type) {
        return extensions.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing LaserIO card integration: " + type.getName()));
    }
}
