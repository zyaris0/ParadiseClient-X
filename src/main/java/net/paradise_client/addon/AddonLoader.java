package net.paradise_client.addon;

import net.fabricmc.loader.api.FabricLoader;
import net.paradise_client.Constants;

import java.util.ArrayList;
import java.util.List;

public class AddonLoader {
    private static final List<ParadiseAddon> loadedAddons = new ArrayList<>();

    public static void loadAddons() {
        FabricLoader.getInstance().getEntrypoints("paradise", ParadiseAddon.class)
                .forEach(addon -> {
            loadedAddons.add(addon);
            addon.onInitialize();
            Constants.LOGGER.info("Loaded addon: {}", addon.getClass().getName());
        });
    }

    public static List<ParadiseAddon> getLoadedAddons() {
        return loadedAddons;
    }
}
