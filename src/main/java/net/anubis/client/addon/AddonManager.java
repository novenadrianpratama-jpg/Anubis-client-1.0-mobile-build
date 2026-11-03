/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  net.fabricmc.loader.api.entrypoint.EntrypointContainer
 *  net.fabricmc.loader.api.metadata.ModMetadata
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.addon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.anubis.client.addon.AnubisAddon;
import net.anubis.client.config.ModuleConfigStore;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class AddonManager {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    public static final String ENTRYPOINT = "anubis";
    private static final String CLIENT_PACKAGE = "net.anubis.client";
    private static List<AnubisAddon> addons;
    private static boolean configLoaded;

    private AddonManager() {
    }

    public static List<AnubisAddon> addons() {
        List<AnubisAddon> loaded = addons;
        return loaded != null ? loaded : List.of();
    }

    public static void init() {
        if (addons != null) {
            return;
        }
        List<AnubisAddon> loaded = AddonManager.create();
        loaded.removeIf(addon -> !AddonManager.run(addon, "register its categories", AnubisAddon::onRegisterCategories));
        loaded.removeIf(addon -> {
            if (AddonManager.run(addon, "initialise", AnubisAddon::onInitialize)) {
                return false;
            }
            AddonManager.removeModules(addon);
            return true;
        });
        for (AnubisAddon addon2 : loaded) {
            LOGGER.info("[Anubis] Loaded addon {} v{} by {}", new Object[]{addon2.name(), addon2.version(), addon2.authors().isEmpty() ? "unknown" : String.join((CharSequence)", ", addon2.authors())});
        }
        addons = List.copyOf(loaded);
    }

    public static void configLoaded() {
        if (addons == null || configLoaded) {
            return;
        }
        configLoaded = true;
        ArrayList<AnubisAddon> loaded = new ArrayList<AnubisAddon>(addons);
        loaded.removeIf(addon -> {
            if (AddonManager.run(addon, "start", AnubisAddon::onConfigLoaded)) {
                return false;
            }
            AddonManager.removeModules(addon);
            return true;
        });
        addons = List.copyOf(loaded);
    }

    private static List<AnubisAddon> create() {
        List containers;
        ArrayList<AnubisAddon> created = new ArrayList<AnubisAddon>();
        try {
            containers = FabricLoader.getInstance().getEntrypointContainers(ENTRYPOINT, AnubisAddon.class);
        }
        catch (LinkageError | RuntimeException e) {
            LOGGER.error("[Anubis] Could not list addons; starting without them", e);
            return created;
        }
        for (EntrypointContainer container : containers) {
            String pkg;
            AnubisAddon addon;
            ModMetadata metadata = container.getProvider().getMetadata();
            try {
                addon = (AnubisAddon)container.getEntrypoint();
                pkg = addon.getPackage();
            }
            catch (LinkageError | RuntimeException e) {
                LOGGER.error("[Anubis] Addon {} ({}) could not be created and is skipped", new Object[]{metadata.getName(), metadata.getId(), e});
                continue;
            }
            addon.bind(metadata);
            String problem = AddonManager.packageProblem(addon, pkg, created);
            if (problem != null) {
                LOGGER.error("[Anubis] Addon {} is skipped: {}", (Object)addon.name(), (Object)problem);
                continue;
            }
            addon.packageName = pkg;
            created.add(addon);
        }
        return created;
    }

    private static String packageProblem(AnubisAddon addon, String pkg, List<AnubisAddon> created) {
        if (pkg == null || !addon.getClass().getName().startsWith(pkg + ".")) {
            return "getPackage() is \"" + pkg + "\", which doesn't hold its class " + addon.getClass().getName();
        }
        if (AddonManager.overlap(pkg, CLIENT_PACKAGE)) {
            return "its package " + pkg + " overlaps Anubis' own (net.anubis.client)";
        }
        for (AnubisAddon other : created) {
            if (!AddonManager.overlap(pkg, other.packageName)) continue;
            return "its package " + pkg + " overlaps " + other.name() + "'s (" + other.packageName + "); an addon made from the template needs its package renamed";
        }
        return null;
    }

    private static boolean overlap(String a, String b) {
        return (a + ".").startsWith(b + ".") || (b + ".").startsWith(a + ".");
    }

    private static boolean run(AnubisAddon addon, String step, Consumer<AnubisAddon> action) {
        try {
            action.accept(addon);
            return true;
        }
        catch (LinkageError | RuntimeException e) {
            LOGGER.error("[Anubis] Addon {} failed to {} and is skipped{}", new Object[]{addon.name(), step, AddonManager.reportHint(addon), e});
            return false;
        }
    }

    private static String reportHint(AnubisAddon addon) {
        try {
            AnubisAddon.GithubRepo repo = addon.getRepo();
            Object where = repo != null ? repo.url() + "/issues" : addon.getWebsite();
            return where != null ? " - report it at " + (String)where : "";
        }
        catch (LinkageError | RuntimeException e) {
            return "";
        }
    }

    private static void removeModules(AnubisAddon addon) {
        String prefix = addon.packageName + ".";
        for (ClientModule module : ModuleRegistry.all()) {
            if (!module.getClass().getName().startsWith(prefix)) continue;
            ModuleConfigStore.getInstance().release(module);
            if (module.isEnabled()) {
                module.setEnabled(false);
            }
            ModuleRegistry.remove(module);
        }
    }
}

