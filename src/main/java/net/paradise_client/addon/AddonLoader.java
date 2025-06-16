package net.paradise_client.addon;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.Person;
import net.paradise_client.Constants;

import java.util.ArrayList;
import java.util.List;

public class AddonLoader {
    private static final List<ParadiseAddon> loadedAddons = new ArrayList<>();

    public static void loadAddons() {
        FabricLoader.getInstance().getAllMods().forEach(mod -> {
            var metadata = mod.getMetadata();
            var customValue = metadata.getCustomValue("paradise");

            if (customValue != null && customValue.getType() == CustomValue.CvType.OBJECT) {
                var obj = customValue.getAsObject();

                var entrypointValue = obj.get("entrypoint");

                if (entrypointValue != null && entrypointValue.getType() == CustomValue.CvType.STRING) {
                    String mainClass = entrypointValue.getAsString();

                    try {
                        Class<?> clazz = Class.forName(mainClass);
                        if (!ParadiseAddon.class.isAssignableFrom(clazz)) return;

                        ParadiseAddon addon = (ParadiseAddon) clazz.getDeclaredConstructor().newInstance();
                        addon.name = metadata.getName();
                        addon.authors = metadata.getAuthors().stream()
                                .map(Person::getName)
                                .toArray(String[]::new);

                        loadedAddons.add(addon);
                        addon.onInitialize();

                        Constants.LOGGER.info("[ParadiseAddon] Loaded addon: {}", addon.name);
                    } catch (Exception e) {
                        Constants.LOGGER.error("Failed to load addon from {}", mod.getMetadata().getId(), e);
                    }
                }
            }
        });
    }

    public static List<ParadiseAddon> getLoadedAddons() {
        return loadedAddons;
    }
}
