/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.ArrayList;
import java.util.List;
import net.anubis.client.config.Surface;
import net.anubis.client.imgui.UiFont;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.module.setting.Setting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class HudTheme {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final HudTheme INSTANCE = new HudTheme();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int CONFIG_VERSION = 1;
    private static final String THEME_KEY = "anubisHudTheme";
    private static final int DEFAULT_COLOR = -15329251;
    private static final float DEFAULT_OPACITY = 0.85f;
    private static final float DEFAULT_IMAGE_OPACITY = 0.4f;
    private static final float ROWS_OPACITY = 0.6f;
    public final NumberSetting rounding = new NumberSetting("Corner radius", 8.0f, 0.0f, 16.0f).step(1.0f);
    public final ModeSetting font;
    public final Surface infoBar;
    public final Surface coordinates;
    public final Surface moduleList;
    public final Surface potions;
    public final Surface regionMap;
    public final Surface antiVanish;
    public final Surface staffList;
    public final Surface spotify;
    public final Surface hotkeys;
    public final Surface radar;
    public final Surface toasts;
    public final Surface targetCard;
    public final Surface armorStatus;
    public final Surface itemCooldowns;
    private final List<Surface> surfaces;
    private final List<Setting> settings;
    private final JsonObject defaults;
    private boolean loaded;
    private boolean readOnly;

    private HudTheme() {
        this.font = new ModeSetting("Font", UiFont.GEIST.label, UiFont.labels());
        this.infoBar = HudTheme.surface("hud_info_bar", "Info Bar");
        this.coordinates = HudTheme.surface("hud_coordinates", "Coordinates", 0.6f);
        this.moduleList = HudTheme.surface("hud_module_list", "Module List", 0.6f);
        this.potions = HudTheme.surface("hud_potions", "Potions", 0.6f);
        this.regionMap = HudTheme.surface("hud_region_map", "Region Map");
        this.antiVanish = HudTheme.surface("hud_anti_vanish", "Anti Vanish", 0.6f);
        this.staffList = HudTheme.surface("hud_staff_list", "Staff List", 0.6f);
        this.spotify = HudTheme.surface("hud_spotify", "Spotify");
        this.hotkeys = HudTheme.surface("hud_hotkeys", "Hotkeys", 0.6f);
        this.radar = HudTheme.surface("hud_radar", "Radar", 0.6f);
        this.toasts = HudTheme.surface("hud_toasts", "Toggle Toasts");
        this.targetCard = HudTheme.surface("hud_target_card", "Target HUD");
        this.armorStatus = HudTheme.surface("hud_armor_status", "Armor HUD", 0.6f);
        this.itemCooldowns = HudTheme.surface("hud_item_cooldowns", "Cooldown", 0.6f);
        this.surfaces = List.of(this.infoBar, this.coordinates, this.moduleList, this.potions, this.regionMap, this.antiVanish, this.staffList, this.spotify, this.hotkeys, this.radar, this.toasts, this.targetCard, this.armorStatus, this.itemCooldowns);
        ArrayList<ModeSetting> all = new ArrayList<ModeSetting>(List.of(this.rounding, this.font));
        for (Surface surface : this.surfaces) {
            all.addAll(surface.settings());
        }
        this.settings = List.copyOf(all);
        this.defaults = this.snapshot(true);
    }

    private static Surface surface(String key, String label) {
        return HudTheme.surface(key, label, 0.85f);
    }

    private static Surface surface(String key, String label, float opacity) {
        return new Surface(key, label, false, -15329251, opacity, 0.4f);
    }

    public static HudTheme get() {
        INSTANCE.ensureLoaded();
        return INSTANCE;
    }

    public List<Surface> surfaces() {
        return this.surfaces;
    }

    public Surface forElementId(String id) {
        if ("info_bar".equals(id)) {
            return this.infoBar;
        }
        if ("coordinates".equals(id)) {
            return this.coordinates;
        }
        if ("module_list".equals(id)) {
            return this.moduleList;
        }
        if ("potion_tracker".equals(id)) {
            return this.potions;
        }
        if ("region_map".equals(id)) {
            return this.regionMap;
        }
        if ("anti_vanish".equals(id)) {
            return this.antiVanish;
        }
        if ("staff_list".equals(id)) {
            return this.staffList;
        }
        if ("spotify".equals(id)) {
            return this.spotify;
        }
        if ("hotkeys".equals(id)) {
            return this.hotkeys;
        }
        if ("radar".equals(id)) {
            return this.radar;
        }
        if ("toggle_toasts".equals(id)) {
            return this.toasts;
        }
        if ("target_card".equals(id)) {
            return this.targetCard;
        }
        if ("armor_status".equals(id)) {
            return this.armorStatus;
        }
        if ("item_cooldowns".equals(id)) {
            return this.itemCooldowns;
        }
        return null;
    }

    public void resetToDefaults() {
        this.apply(this.defaults, true);
    }

    public String exportString() {
        JsonObject root = new JsonObject();
        root.add(THEME_KEY, (JsonElement)this.snapshot(false));
        return root.toString();
    }

    public boolean importString(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        try {
            JsonElement parsed = JsonParser.parseString((String)text.trim());
            if (!parsed.isJsonObject() || !parsed.getAsJsonObject().has(THEME_KEY)) {
                return false;
            }
            JsonElement values = parsed.getAsJsonObject().get(THEME_KEY);
            if (!values.isJsonObject()) {
                return false;
            }
            this.apply(values.getAsJsonObject(), false);
            return true;
        }
        catch (RuntimeException e) {
            return false;
        }
    }

    public JsonObject profileSnapshot() {
        return this.snapshot(true);
    }

    public void applyProfile(JsonObject values) {
        for (Setting setting : this.settings) {
            JsonElement value = values.get(setting.id());
            if (value == null && !setting.isPrivate()) {
                value = this.defaults.get(setting.id());
            }
            if (value == null) continue;
            setting.deserialize(value);
        }
    }

    public JsonObject shareable(JsonObject values) {
        JsonObject kept = new JsonObject();
        for (Setting setting : this.settings) {
            JsonElement value = values.get(setting.id());
            if (value == null || setting.isPrivate() || Setting.sameValue(value, this.defaults.get(setting.id()))) continue;
            kept.add(setting.id(), value.deepCopy());
        }
        return kept;
    }

    public synchronized void load() {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        Path path = this.configPath();
        if (!Files.isRegularFile(path, new LinkOption[0])) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(path);){
            int version;
            JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            int n = version = root.has("version") ? root.get("version").getAsInt() : 0;
            if (version > 1) {
                this.readOnly = true;
                LOGGER.warn("[Anubis] HUD theme version {} is newer than supported {}; skipping load", (Object)version, (Object)1);
                return;
            }
            if (root.has("values") && root.get("values").isJsonObject()) {
                this.apply(root.getAsJsonObject("values"), true);
            }
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to load HUD theme; preserving the file", (Throwable)e);
            this.readOnly = true;
        }
    }

    public synchronized void save() {
        if (this.readOnly || !this.loaded) {
            return;
        }
        JsonObject root = new JsonObject();
        root.addProperty("version", (Number)1);
        root.add("values", (JsonElement)this.snapshot(true));
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
            LOGGER.warn("[Anubis] Failed to save HUD theme", (Throwable)e);
        }
    }

    private JsonObject snapshot(boolean withPrivate) {
        JsonObject values = new JsonObject();
        for (Setting setting : this.settings) {
            if (!withPrivate && setting.isPrivate()) continue;
            values.add(setting.id(), setting.serialize());
        }
        return values;
    }

    private void apply(JsonObject values, boolean withPrivate) {
        for (Setting setting : this.settings) {
            if (!withPrivate && setting.isPrivate() || !values.has(setting.id())) continue;
            setting.deserialize(values.get(setting.id()));
        }
    }

    private void ensureLoaded() {
        if (!this.loaded) {
            this.load();
        }
    }

    private Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("anubis").resolve("hud_theme.json");
    }
}

