/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.resource.v1.pack.ModPackResources
 *  net.fabricmc.loader.api.FabricLoader
 *  net.fabricmc.loader.api.ModContainer
 *  net.fabricmc.loader.api.metadata.ModDependency
 *  net.fabricmc.loader.api.metadata.ModDependency$Kind
 *  net.fabricmc.loader.api.metadata.ModMetadata
 *  net.minecraft.class_2477
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_315
 *  net.minecraft.class_3262
 *  net.minecraft.class_3298
 *  net.minecraft.class_5348
 *  net.minecraft.class_5481
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.v1.pack.ModPackResources;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_3262;
import net.minecraft.class_3298;
import net.minecraft.class_5348;
import net.minecraft.class_5481;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class TranslationGuard {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final String MOD_ID = "anubisplus";
    private static final ThreadLocal<int[]> DEPTH = ThreadLocal.withInitial(() -> new int[1]);
    private static final ThreadLocal<Load> LOAD = new ThreadLocal();
    private static volatile int activeCount;
    private static volatile Set<String> hidden;
    private static volatile Set<String> ourMods;
    private static volatile Snapshot snapshot;
    private static class_2477 wrapped;
    private static Set<String> guardedKeys;
    private static class_2477 guarded;

    private TranslationGuard() {
    }

    public static boolean active() {
        return activeCount > 0 && DEPTH.get()[0] > 0;
    }

    public static String plain(class_2561 component) {
        TranslationGuard.enter();
        try {
            String string = component.getString();
            return string;
        }
        finally {
            TranslationGuard.exit();
        }
    }

    public static void run(Runnable action) {
        TranslationGuard.enter();
        try {
            action.run();
        }
        finally {
            TranslationGuard.exit();
        }
    }

    public static synchronized class_2477 language(class_2477 real) {
        if (real == null) {
            return null;
        }
        Set<String> keys = TranslationGuard.hidden();
        if (real != wrapped || keys != guardedKeys) {
            wrapped = real;
            guardedKeys = keys;
            guarded = new GuardedLanguage(real, keys);
        }
        return guarded;
    }

    public static class_2561 keybind(String name) {
        class_304 mapping = class_304.method_65807((String)name);
        if (mapping != null && !TranslationGuard.hidden().contains(name)) {
            return mapping.method_16007();
        }
        return class_2561.method_43471((String)name);
    }

    public static void beginLoad() {
        LOAD.set(new Load());
    }

    public static BiConsumer<String, String> tap(class_3298 resource, BiConsumer<String, String> output) {
        Load load = LOAD.get();
        if (load == null) {
            return output;
        }
        load.seen = true;
        if (TranslationGuard.ours(resource)) {
            return (key, value) -> {
                output.accept((String)key, (String)value);
                load.provided.add((String)key);
            };
        }
        return (key, value) -> {
            output.accept((String)key, (String)value);
            load.clean.put((String)key, (String)value);
        };
    }

    public static void endLoad() {
        Load load = LOAD.get();
        LOAD.remove();
        if (load == null || !load.seen) {
            return;
        }
        load.clean.keySet().retainAll(load.provided);
        snapshot = new Snapshot(Map.copyOf(load.clean), Set.copyOf(load.provided));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static void enter() {
        int[] depth = DEPTH.get();
        int n = depth[0];
        depth[0] = n + 1;
        if (n != 0) return;
        Class<TranslationGuard> clazz = TranslationGuard.class;
        synchronized (TranslationGuard.class) {
            ++activeCount;
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static void exit() {
        int[] depth = DEPTH.get();
        if (depth[0] <= 0 || (depth[0] = depth[0] - 1) != 0) return;
        Class<TranslationGuard> clazz = TranslationGuard.class;
        synchronized (TranslationGuard.class) {
            --activeCount;
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    private static boolean ours(class_3298 resource) {
        class_3262 class_32622 = resource.method_45304();
        if (class_32622 instanceof ModPackResources) {
            ModPackResources pack = (ModPackResources)class_32622;
            return TranslationGuard.ours(pack.getFabricModMetadata());
        }
        return TranslationGuard.ourMods().contains(resource.method_14480());
    }

    private static Set<String> ourMods() {
        Set<String> ids = ourMods;
        if (ids != null) {
            return ids;
        }
        HashSet<String> collected = new HashSet<String>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            if (!TranslationGuard.ours(mod.getMetadata())) continue;
            collected.add(mod.getMetadata().getId());
        }
        ourMods = ids = Set.copyOf(collected);
        return ids;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Set<String> hidden() {
        Set<String> keys = hidden;
        if (keys != null) {
            return keys;
        }
        Class<TranslationGuard> clazz = TranslationGuard.class;
        synchronized (TranslationGuard.class) {
            if (hidden != null) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return hidden;
            }
            HashSet<String> collected = new HashSet<String>();
            HashSet<String> namespaces = new HashSet<String>();
            namespaces.add("anubis");
            for (class_304[] mod : FabricLoader.getInstance().getAllMods()) {
                if (!TranslationGuard.ours(mod.getMetadata())) continue;
                TranslationGuard.collect((ModContainer)mod, collected, namespaces);
            }
            class_315 options = class_310.method_1551().field_1690;
            if (options != null) {
                for (class_304 mapping : options.field_1839) {
                    class_2960 category = mapping.method_1423().comp_4879();
                    if (!namespaces.contains(category.method_12836())) continue;
                    collected.add(mapping.method_1431());
                    collected.add("key.category." + category.method_12836() + "." + category.method_12832());
                }
            }
            Set<String> result = Set.copyOf(collected);
            if (options != null) {
                hidden = result;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return result;
        }
    }

    private static boolean ours(ModMetadata metadata) {
        if (MOD_ID.equals(metadata.getId())) {
            return true;
        }
        for (ModDependency dependency : metadata.getDependencies()) {
            if (dependency.getKind() != ModDependency.Kind.DEPENDS || !MOD_ID.equals(dependency.getModId())) continue;
            return true;
        }
        return false;
    }

    private static void collect(ModContainer mod, Set<String> keys, Set<String> namespaces) {
        for (Path root : mod.getRootPaths()) {
            Path assets = root.resolve("assets");
            if (!Files.isDirectory(assets, new LinkOption[0])) continue;
            for (Path space : TranslationGuard.list(assets)) {
                if (!Files.isDirectory(space, new LinkOption[0])) continue;
                namespaces.add(space.getFileName().toString().replace("/", ""));
                Path lang = space.resolve("lang");
                if (!Files.isDirectory(lang, new LinkOption[0])) continue;
                for (Path file : TranslationGuard.list(lang)) {
                    if (!file.getFileName().toString().endsWith(".json")) continue;
                    try {
                        BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
                        try {
                            JsonElement json = JsonParser.parseReader((Reader)reader);
                            if (!json.isJsonObject()) continue;
                            keys.addAll(json.getAsJsonObject().keySet());
                        }
                        finally {
                            if (reader == null) continue;
                            ((Reader)reader).close();
                        }
                    }
                    catch (IOException | RuntimeException e) {
                        LOGGER.warn("Could not read {}", (Object)file, (Object)e);
                    }
                }
            }
        }
    }

    private static List<Path> list(Path directory) {
        List<Path> list;
        block8: {
            Stream<Path> entries = Files.list(directory);
            try {
                list = entries.toList();
                if (entries == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (entries != null) {
                        try {
                            entries.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    LOGGER.warn("Could not list {}", (Object)directory, (Object)e);
                    return List.of();
                }
            }
            entries.close();
        }
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class GuardedLanguage
    extends class_2477 {
        private final class_2477 real;
        private final Set<String> hidden;

        GuardedLanguage(class_2477 real, Set<String> hidden) {
            this.real = real;
            this.hidden = hidden;
        }

        public String method_4679(String key, String fallback) {
            Snapshot current = snapshot;
            if (current != null) {
                return current.provided().contains(key) ? current.clean().getOrDefault(key, fallback) : this.real.method_4679(key, fallback);
            }
            return this.hidden.contains(key) ? fallback : this.real.method_4679(key, fallback);
        }

        public boolean method_4678(String key) {
            Snapshot current = snapshot;
            if (current != null) {
                return current.provided().contains(key) ? current.clean().containsKey(key) : this.real.method_4678(key);
            }
            return !this.hidden.contains(key) && this.real.method_4678(key);
        }

        public boolean method_29428() {
            return this.real.method_29428();
        }

        public class_5481 method_30934(class_5348 text) {
            return this.real.method_30934(text);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Load {
        private final Map<String, String> clean = new HashMap<String, String>();
        private final Set<String> provided = new HashSet<String>();
        private boolean seen;

        private Load() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Snapshot(Map<String, String> clean, Set<String> provided) {
    }
}

