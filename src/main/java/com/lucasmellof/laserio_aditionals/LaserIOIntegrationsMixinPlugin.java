package com.lucasmellof.laserio_aditionals;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/*
 * @author Lucasmellof, Lucas de Mello Freitas created on 26/09/2026
 */
public final class LaserIOIntegrationsMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(LaserIOIntegrationsMixinPlugin.class);
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!mixinClassName.startsWith("com.lucasmellof.laserio_aditionals.mixin.mods")) {
            return true;
        }
        var mod = mixinClassName.split("\\.")[5];
        LOGGER.info("[LIA MixinPlugin] Checking mixin for mod: {} | Mixin class: {}", mod, mixinClassName);


        if (FMLLoader.getLoadingModList().getModFileById(mod) == null) {
            LOGGER.info("[LIA MixinPlugin] Mod {} not loaded, ignoring mixin: {}", mod, mixinClassName);
            return false;
        }

        LOGGER.info("[LIA MixinPlugin] Mod {} loaded, applying mixin: {}", mod, mixinClassName);
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
