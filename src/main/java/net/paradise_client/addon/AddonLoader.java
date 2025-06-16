package net.paradise_client.addon;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.Person;
import net.paradise_client.Constants;

import java.util.ArrayList;
import java.util.List;

public class AddonLoader {
    private static final List<ParadiseAddon> loadedAddons = new ArrayList<>();

    public static void loadAddons() {
        FabricLoader.getInstance().getEntrypointContainers("paradise", ParadiseAddon.class)
                .forEach(container -> {
                    try {
                        ParadiseAddon addon = container.getEntrypoint();
                        addon.name = container.getProvider().getMetadata().getName();
                        addon.authors = container.getProvider().getMetadata().getAuthors()
                                .stream()
                                .map(Person::getName)
                                .toArray(String[]::new);
                        loadedAddons.add(addon);
                        addon.onInitialize();
                        Constants.LOGGER.info("Loaded addon: {}", addon.name);
                    } catch (Throwable e) {
                        Constants.LOGGER.error("Failed to load addon: {}", container.getProvider().getMetadata().getName(), e);
                    }
                });
    }

    public static List<ParadiseAddon> getLoadedAddons() {
        return loadedAddons;
    }
}
