/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_310
 */
package net.anubis.client.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.anubis.client.AnubisClientMod;
import net.anubis.client.imgui.ImGuiManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class HudLayoutStore {
    public static final String INFO_BAR_ID = "info_bar";
    public static final String COORDINATES_ID = "coordinates";
    public static final String POTION_TRACKER_ID = "potion_tracker";
    public static final String MODULE_LIST_ID = "module_list";
    public static final String REGION_MAP_ID = "region_map";
    public static final String ANTI_VANISH_ID = "anti_vanish";
    public static final String STAFF_LIST_ID = "staff_list";
    public static final String SPOTIFY_ID = "spotify";
    public static final String RADAR_ID = "radar";
    public static final String HOTKEYS_ID = "hotkeys";
    public static final String TARGET_CARD_ID = "target_card";
    public static final String ARMOR_STATUS_ID = "armor_status";
    public static final String ITEM_COOLDOWNS_ID = "item_cooldowns";
    private static final HudLayoutStore INSTANCE = new HudLayoutStore();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int CONFIG_VERSION = 11;
    private static final float DEFAULT_MARGIN = 10.0f;
    private static final float DEFAULT_GAP = 6.0f;
    private static final float CENTER_TOLERANCE = 24.0f;
    static final int ANCHOR_LEFT = 0;
    static final int ANCHOR_CENTER = 1;
    static final int ANCHOR_RIGHT = 2;
    private static final String[] KNOWN_IDS = new String[]{"info_bar", "coordinates", "module_list", "potion_tracker", "region_map", "anti_vanish", "staff_list", "spotify", "hotkeys", "radar", "target_card", "armor_status", "item_cooldowns"};
    private final Map<String, HudElementState> elements = new LinkedHashMap<String, HudElementState>();
    private boolean loaded;

    private HudLayoutStore() {
    }

    public static HudLayoutStore getInstance() {
        return INSTANCE;
    }

    synchronized HudElementState element(String id) {
        this.loadIfNeeded();
        HudElementState state = this.elements.get(id);
        if (state == null) {
            state = HudLayoutStore.defaultElement(id);
            this.elements.put(id, state);
        }
        return state;
    }

    synchronized List<HudElementState> orderedElements() {
        this.loadIfNeeded();
        return List.copyOf(this.elements.values());
    }

    synchronized void updateBounds(String id, float width, float height, float screenW, float screenH) {
        HudElementState state = this.element(id);
        float newW = Math.max(1.0f, width);
        float newH = Math.max(1.0f, height);
        if (state.positioned && state.autoPlaced && screenW > 0.0f && screenH > 0.0f && HudLayoutStore.dockMoved(state, screenW, screenH)) {
            state.positioned = false;
        }
        if (!state.positioned || screenW <= 0.0f || screenH <= 0.0f) {
            state.width = newW;
            state.height = newH;
            if (!state.positioned && screenW > 0.0f && screenH > 0.0f && !HudLayoutStore.isCollapsed(state)) {
                this.placeDefault(state, screenW, screenH);
            }
            return;
        }
        if (!state.anchored) {
            this.adoptLoadedPosition(state, screenW, screenH);
        }
        HudLayoutStore.rebaseToScreen(state, screenW, screenH);
        float dw = state.width - newW;
        if (state.anchorX == 2) {
            state.x += dw;
        } else if (state.anchorX == 1) {
            state.x += dw * 0.5f;
        }
        if (state.anchorBottom) {
            state.y += state.height - newH;
        }
        state.width = newW;
        state.height = newH;
        HudLayoutStore.clampToScreen(state, screenW, screenH);
    }

    private void adoptLoadedPosition(HudElementState state, float screenW, float screenH) {
        if (state.refScreenW > 0.0f && state.refScreenH > 0.0f) {
            state.anchored = true;
            return;
        }
        if (INFO_BAR_ID.equals(state.id)) {
            state.x = (screenW - state.width) * 0.5f;
        } else if (state.x > screenW - state.width || state.y > screenH - state.height) {
            this.placeDefault(state, screenW, screenH);
            return;
        }
        HudLayoutStore.computeAnchor(state, screenW, screenH);
    }

    private static void rebaseToScreen(HudElementState state, float screenW, float screenH) {
        float dx = screenW - state.refScreenW;
        float dy = screenH - state.refScreenH;
        if (dx == 0.0f && dy == 0.0f) {
            return;
        }
        if (state.anchorX == 2) {
            state.x += dx;
        } else if (state.anchorX == 1) {
            state.x += dx * 0.5f;
        }
        if (state.anchorBottom) {
            state.y += dy;
        }
        state.refScreenW = screenW;
        state.refScreenH = screenH;
    }

    synchronized void moveTo(String id, float x, float y, float screenW, float screenH) {
        HudElementState state = this.element(id);
        state.x = x;
        state.y = y;
        state.positioned = true;
        state.autoPlaced = false;
        HudLayoutStore.clampToScreen(state, screenW, screenH);
        HudLayoutStore.computeAnchor(state, screenW, screenH);
    }

    private static void computeAnchor(HudElementState state, float screenW, float screenH) {
        float cx = state.x + state.width * 0.5f;
        state.anchorX = Math.abs(cx - screenW * 0.5f) <= 24.0f * HudLayoutStore.uiScale() ? 1 : (cx > screenW * 0.5f ? 2 : 0);
        state.anchorBottom = state.y + state.height * 0.5f > screenH * 0.5f;
        state.refScreenW = screenW;
        state.refScreenH = screenH;
        state.anchored = true;
    }

    private static float uiScale() {
        return ImGuiManager.getInstance().uiScale();
    }

    synchronized void resetToDefaults(float screenW, float screenH) {
        LinkedHashMap<String, Dimensions> currentSizes = new LinkedHashMap<String, Dimensions>();
        for (String id : KNOWN_IDS) {
            currentSizes.put(id, new Dimensions(this.existingWidth(id, HudLayoutStore.fallbackWidth(id)), this.existingHeight(id, 26.0f)));
        }
        this.elements.clear();
        for (String id : KNOWN_IDS) {
            HudElementState state = HudLayoutStore.defaultElement(id);
            Dimensions size = (Dimensions)currentSizes.get(id);
            state.width = size.width();
            state.height = size.height();
            this.elements.put(state.id, state);
        }
        for (HudElementState state : this.elements.values()) {
            if (HudLayoutStore.isCollapsed(state)) continue;
            this.placeDefault(state, screenW, screenH);
            HudLayoutStore.clampToScreen(state, screenW, screenH);
        }
        this.save();
    }

    public synchronized JsonObject profileSnapshot() {
        this.loadIfNeeded();
        return GSON.toJsonTree((Object)this.buildConfigFile()).getAsJsonObject();
    }

    public synchronized boolean applyProfile(JsonObject layout) {
        this.loadIfNeeded();
        ConfigFile config = HudLayoutStore.parse(layout);
        if (config == null) {
            return false;
        }
        this.applyConfig(config);
        this.save();
        return true;
    }

    public static JsonObject shareable(JsonObject layout) {
        ConfigFile config = HudLayoutStore.parse(layout);
        return config == null ? null : GSON.toJsonTree((Object)config).getAsJsonObject();
    }

    private static ConfigFile parse(JsonObject layout) {
        ConfigFile config;
        try {
            config = (ConfigFile)GSON.fromJson((JsonElement)layout, ConfigFile.class);
        }
        catch (RuntimeException e) {
            return null;
        }
        if (config == null || config.elements == null) {
            return null;
        }
        ArrayList<HudElementState> kept = new ArrayList<HudElementState>();
        for (HudElementState state : config.elements) {
            if (state == null || state.id == null || !HudLayoutStore.isKnownId(state.id) || !HudLayoutStore.finite(state)) continue;
            kept.add(state);
        }
        config.elements = kept;
        return config;
    }

    private static boolean finite(HudElementState state) {
        return HudLayoutStore.finite(state.x) && HudLayoutStore.finite(state.y) && HudLayoutStore.finite(state.width) && HudLayoutStore.finite(state.height) && HudLayoutStore.finite(state.scale) && HudLayoutStore.finite(state.refScreenW) && HudLayoutStore.finite(state.refScreenH) && HudLayoutStore.finite(state.placedGuiScale) && HudLayoutStore.finite(state.placedUiScale);
    }

    public synchronized void save() {
        this.loadIfNeeded();
        ConfigFile config = this.buildConfigFile();
        Path path = HudLayoutStore.configPath();
        try {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            Path tmp = path.resolveSibling(path.getFileName().toString() + ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(tmp, new OpenOption[0]);){
                GSON.toJson((Object)config, (Appendable)writer);
            }
            try {
                Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (IOException atomicFail) {
                Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            AnubisClientMod.LOGGER.warn("[Anubis] Failed to save HUD layout config", (Throwable)e);
        }
    }

    private void loadIfNeeded() {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        this.installDefaults();
        Path path = HudLayoutStore.configPath();
        if (!Files.isRegularFile(path, new LinkOption[0])) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(path);){
            ConfigFile config = (ConfigFile)GSON.fromJson((Reader)reader, ConfigFile.class);
            if (config == null || config.elements == null) {
                return;
            }
            this.applyConfig(config);
        }
        catch (IOException | RuntimeException e) {
            AnubisClientMod.LOGGER.warn("[Anubis] Failed to load HUD layout config; using defaults", (Throwable)e);
            this.elements.clear();
            this.installDefaults();
        }
    }

    private ConfigFile buildConfigFile() {
        ConfigFile config = new ConfigFile();
        config.version = 11;
        config.elements = new ArrayList<HudElementState>();
        for (HudElementState state : this.elements.values()) {
            if (!state.positioned) continue;
            config.elements.add(state);
        }
        return config;
    }

    private void applyConfig(ConfigFile config) {
        this.elements.clear();
        this.installDefaults();
        boolean oldLayout = config.version < 11;
        for (HudElementState loadedState : config.elements) {
            if (loadedState == null || loadedState.id == null || loadedState.id.isBlank() || !HudLayoutStore.isKnownId(loadedState.id)) continue;
            HudLayoutStore.normalize(loadedState);
            this.elements.put(loadedState.id, loadedState);
        }
        this.ensureKnownElements();
        if (oldLayout) {
            this.resetPositionsForMigration();
        }
    }

    private void installDefaults() {
        for (String id : KNOWN_IDS) {
            this.elements.put(id, HudLayoutStore.defaultElement(id));
        }
    }

    private void ensureKnownElements() {
        for (String id : KNOWN_IDS) {
            this.elements.computeIfAbsent(id, HudLayoutStore::defaultElement);
        }
    }

    private void resetPositionsForMigration() {
        for (String id : KNOWN_IDS) {
            HudElementState state = this.elements.get(id);
            if (state == null) continue;
            state.positioned = false;
        }
    }

    private static HudElementState defaultElement(String id) {
        HudElementState state = new HudElementState();
        state.id = id;
        state.x = 0.0f;
        state.y = 0.0f;
        state.width = HudLayoutStore.fallbackWidth(id);
        state.height = 26.0f;
        state.visible = true;
        state.scale = 1.0f;
        state.positioned = false;
        return state;
    }

    private static void normalize(HudElementState state) {
        state.scale = HudLayoutStore.finite(state.scale) ? HudLayoutStore.clamp(state.scale, 0.5f, 3.0f) : 1.0f;
        state.width = HudLayoutStore.finite(state.width) && state.width > 0.0f ? state.width : 1.0f;
        state.height = HudLayoutStore.finite(state.height) && state.height > 0.0f ? state.height : 1.0f;
        boolean bl = state.positioned = HudLayoutStore.finite(state.x) && HudLayoutStore.finite(state.y);
        if (state.anchorX < 0 || state.anchorX > 2 || !HudLayoutStore.finite(state.refScreenW) || !HudLayoutStore.finite(state.refScreenH)) {
            state.refScreenW = 0.0f;
            state.refScreenH = 0.0f;
        }
    }

    private void placeDefault(HudElementState state, float screenW, float screenH) {
        float margin = Math.round(10.0f * HudLayoutStore.uiScale());
        float gap = Math.round(6.0f * HudLayoutStore.uiScale());
        float coordsBottom = margin + this.existingHeight(COORDINATES_ID, 28.0f) + gap;
        switch (state.id) {
            case "potion_tracker": {
                state.x = Math.max(margin, screenW - margin - Math.max(1.0f, state.width));
                state.y = Math.max(margin, screenH - margin - Math.max(1.0f, state.height));
                break;
            }
            case "info_bar": {
                state.x = Math.max(margin, (screenW - state.width) * 0.5f);
                state.y = margin;
                break;
            }
            case "module_list": {
                state.x = Math.max(margin, screenW - margin - Math.max(1.0f, state.width));
                state.y = margin;
                break;
            }
            case "region_map": {
                state.x = margin;
                state.y = this.firstFreeY(state, coordsBottom, gap);
                break;
            }
            case "anti_vanish": {
                state.x = Math.max(margin, (screenW - state.width) * 0.5f);
                state.y = margin + this.existingHeight(INFO_BAR_ID, 28.0f) + gap;
                break;
            }
            case "spotify": {
                state.x = margin;
                state.y = this.firstFreeY(state, coordsBottom, gap);
                break;
            }
            case "staff_list": {
                state.x = margin;
                state.y = this.firstFreeY(state, Math.round(screenH * 0.38f), gap);
                break;
            }
            case "hotkeys": {
                state.x = Math.max(margin, screenW - margin - Math.max(1.0f, state.width));
                state.y = this.firstFreeY(state, Math.round(screenH * 0.36f), gap);
                break;
            }
            case "radar": {
                state.x = Math.max(margin, screenW - margin - Math.max(1.0f, state.width));
                state.y = this.firstFreeY(state, Math.round(screenH * 0.58f), gap);
                break;
            }
            case "target_card": {
                state.x = Math.round(screenW * 0.5f + 44.0f * HudLayoutStore.uiScale());
                state.y = this.firstFreeY(state, Math.round(screenH * 0.5f + 30.0f * HudLayoutStore.uiScale()), gap);
                break;
            }
            case "armor_status": {
                state.x = Math.round(screenW * 0.5f + 119.0f * HudLayoutStore.vanillaGuiScale());
                state.y = Math.max(margin, screenH - margin - Math.max(1.0f, state.height));
                break;
            }
            case "item_cooldowns": {
                state.x = Math.max(margin, (float)Math.round((screenW - state.width) * 0.5f));
                state.y = Math.max(margin, screenH - (float)Math.round(76.0f * HudLayoutStore.vanillaGuiScale()) - Math.max(1.0f, state.height));
                break;
            }
            default: {
                state.x = margin;
                state.y = margin;
            }
        }
        state.positioned = true;
        HudLayoutStore.clampToScreen(state, screenW, screenH);
        HudLayoutStore.computeAnchor(state, screenW, screenH);
        state.autoPlaced = HudLayoutStore.followsVanillaHud(state.id);
        if (state.autoPlaced) {
            state.anchorX = ITEM_COOLDOWNS_ID.equals(state.id) ? 1 : 0;
            state.placedGuiScale = HudLayoutStore.vanillaGuiScale();
            state.placedUiScale = HudLayoutStore.uiScale();
        }
    }

    private static boolean followsVanillaHud(String id) {
        return TARGET_CARD_ID.equals(id) || ARMOR_STATUS_ID.equals(id) || ITEM_COOLDOWNS_ID.equals(id);
    }

    private static boolean dockMoved(HudElementState state, float screenW, float screenH) {
        return screenW != state.refScreenW || screenH != state.refScreenH || HudLayoutStore.vanillaGuiScale() != state.placedGuiScale || HudLayoutStore.uiScale() != state.placedUiScale;
    }

    private static float fallbackWidth(String id) {
        return switch (id) {
            case MODULE_LIST_ID -> 170.0f;
            case INFO_BAR_ID -> 290.0f;
            case COORDINATES_ID -> 210.0f;
            case POTION_TRACKER_ID -> 190.0f;
            case REGION_MAP_ID -> 240.0f;
            case ANTI_VANISH_ID -> 180.0f;
            case STAFF_LIST_ID -> 190.0f;
            case SPOTIFY_ID -> 320.0f;
            case HOTKEYS_ID -> 190.0f;
            case RADAR_ID -> 150.0f;
            case TARGET_CARD_ID -> 244.0f;
            case ARMOR_STATUS_ID -> 120.0f;
            case ITEM_COOLDOWNS_ID -> 80.0f;
            default -> 110.0f;
        };
    }

    private static float vanillaGuiScale() {
        return Math.max(1, class_310.method_1551().method_22683().method_4495());
    }

    private float firstFreeY(HudElementState state, float startY, float gap) {
        float y = startY;
        float w = Math.max(1.0f, state.width);
        float h = Math.max(1.0f, state.height);
        for (int pass = 0; pass < this.elements.size(); ++pass) {
            boolean moved = false;
            for (HudElementState other : this.elements.values()) {
                boolean overlaps;
                if (other == state || !other.positioned || HudLayoutStore.isCollapsed(other) || !(overlaps = state.x < other.x + other.width && other.x < state.x + w && y < other.y + other.height && other.y < y + h)) continue;
                y = other.y + other.height + gap;
                moved = true;
            }
            if (!moved) break;
        }
        return y;
    }

    private static boolean isCollapsed(HudElementState state) {
        return state.width <= 1.0f;
    }

    private static void clampToScreen(HudElementState state, float screenW, float screenH) {
        float maxX = Math.max(0.0f, screenW - Math.max(1.0f, state.width));
        float maxY = Math.max(0.0f, screenH - Math.max(1.0f, state.height));
        state.x = HudLayoutStore.clamp(state.x, 0.0f, maxX);
        state.y = HudLayoutStore.clamp(state.y, 0.0f, maxY);
    }

    private float existingWidth(String id, float fallback) {
        HudElementState state = this.elements.get(id);
        return state != null && HudLayoutStore.finite(state.width) && state.width > 0.0f ? state.width : fallback;
    }

    private float existingHeight(String id, float fallback) {
        HudElementState state = this.elements.get(id);
        return state != null && HudLayoutStore.finite(state.height) && state.height > 0.0f ? state.height : fallback;
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("anubis").resolve("hud.json");
    }

    private static boolean isKnownId(String id) {
        for (String k : KNOWN_IDS) {
            if (!k.equals(id)) continue;
            return true;
        }
        return false;
    }

    private static boolean finite(float value) {
        return !Float.isNaN(value) && !Float.isInfinite(value);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    @Environment(value=EnvType.CLIENT)
    static final class HudElementState {
        String id;
        float x;
        float y;
        float width;
        float height;
        boolean visible;
        float scale;
        transient boolean positioned;
        int anchorX;
        boolean anchorBottom;
        float refScreenW;
        float refScreenH;
        transient boolean anchored;
        boolean autoPlaced;
        float placedGuiScale;
        float placedUiScale;

        HudElementState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Dimensions(float width, float height) {
    }

    @Environment(value=EnvType.CLIENT)
    private static final class ConfigFile {
        int version;
        List<HudElementState> elements;

        private ConfigFile() {
        }
    }
}

