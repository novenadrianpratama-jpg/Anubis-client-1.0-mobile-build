/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.anubis.client.config.BundledDefaults;
import net.anubis.client.module.ModuleCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class PanelStateStore {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final PanelStateStore INSTANCE = new PanelStateStore();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int CONFIG_VERSION = 1;
    private final Set<String> collapsed = new LinkedHashSet<String>();
    private final Map<String, float[]> homes = new LinkedHashMap<String, float[]>();
    private boolean loaded;
    private boolean readOnly;

    private PanelStateStore() {
    }

    public static PanelStateStore getInstance() {
        return INSTANCE;
    }

    public synchronized boolean isCollapsed(ModuleCategory category) {
        this.ensureLoaded();
        return this.collapsed.contains(category.name());
    }

    public synchronized void setCollapsed(ModuleCategory category, boolean fold) {
        boolean changed;
        this.ensureLoaded();
        boolean bl = changed = fold ? this.collapsed.add(category.name()) : this.collapsed.remove(category.name());
        if (changed) {
            this.save();
        }
    }

    public synchronized Map<String, float[]> panelHomes() {
        this.ensureLoaded();
        HashMap<String, float[]> copy = new HashMap<String, float[]>();
        this.homes.forEach((id, spot) -> copy.put((String)id, (float[])spot.clone()));
        return copy;
    }

    public synchronized void setPanelHomes(Map<String, float[]> next) {
        this.ensureLoaded();
        this.homes.clear();
        next.forEach((id, spot) -> this.homes.put((String)id, (float[])spot.clone()));
        this.save();
    }

    public synchronized void load() {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        this.collapsed.clear();
        this.homes.clear();
        Path path = this.configPath();
        if (!Files.isRegularFile(path, new LinkOption[0])) {
            this.collapsed.addAll(BundledDefaults.collapsedPanels());
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(path);){
            int version;
            JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            int n = version = root.has("version") ? root.get("version").getAsInt() : 0;
            if (version > 1) {
                this.readOnly = true;
                LOGGER.warn("[Anubis] ClickGUI state version {} is newer than supported {}; skipping load", (Object)version, (Object)1);
                return;
            }
            if (root.has("collapsed") && root.get("collapsed").isJsonArray()) {
                for (JsonElement element : root.getAsJsonArray("collapsed")) {
                    if (!element.isJsonPrimitive()) continue;
                    this.collapsed.add(element.getAsString());
                }
            }
            if (root.has("panels") && root.get("panels").isJsonObject()) {
                for (Map.Entry entry : root.getAsJsonObject("panels").entrySet()) {
                    if (!((JsonElement)entry.getValue()).isJsonArray() || ((JsonElement)entry.getValue()).getAsJsonArray().size() != 2) continue;
                    JsonArray xy = ((JsonElement)entry.getValue()).getAsJsonArray();
                    float x = xy.get(0).getAsFloat();
                    float y = xy.get(1).getAsFloat();
                    if (!Float.isFinite(x) || !Float.isFinite(y)) continue;
                    this.homes.put((String)entry.getKey(), new float[]{x, y});
                }
            }
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to load the ClickGUI state; preserving the file", (Throwable)e);
            this.collapsed.clear();
            this.homes.clear();
            this.readOnly = true;
        }
    }

    public synchronized void save() {
        if (this.readOnly) {
            return;
        }
        JsonObject root = new JsonObject();
        root.addProperty("version", (Number)1);
        JsonArray list = new JsonArray();
        for (String id2 : this.collapsed) {
            list.add(id2);
        }
        root.add("collapsed", (JsonElement)list);
        JsonObject panels = new JsonObject();
        this.homes.forEach((id, spot) -> {
            JsonArray xy = new JsonArray();
            xy.add((Number)Float.valueOf(spot[0]));
            xy.add((Number)Float.valueOf(spot[1]));
            panels.add(id, (JsonElement)xy);
        });
        root.add("panels", (JsonElement)panels);
        Path path = this.configPath();
        try {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            Path tmp = path.resolveSibling(path.getFileName().toString() + ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(tmp, new OpenOption[0]);){
                GSON.toJson((JsonElement)root, (Appendable)writer);
            }
            try {
                Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (IOException atomicFail) {
                Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            LOGGER.warn("[Anubis] Failed to save the ClickGUI state", (Throwable)e);
        }
    }

    private void ensureLoaded() {
        if (!this.loaded) {
            this.load();
        }
    }

    private Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("anubis").resolve("clickgui.json");
    }
}

