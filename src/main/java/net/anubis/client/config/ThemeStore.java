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
 *  net.minecraft.class_310
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
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.CopyOption;
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
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.module.setting.Setting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_310;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class ThemeStore {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final ThemeStore INSTANCE = new ThemeStore();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int CONFIG_VERSION = 4;
    private static final String THEME_KEY = "anubisTheme";
    private static final String DEFAULT_BANNER = "donutbets-banner.png";
    private static final String BANNER_RESOURCE = "/assets/anubis/textures/gui/donutbets_banner.png";
    public static final List<Preset> PRESETS = List.of(new Preset("Default", -13338378, -14736856, -15263458), new Preset("Midnight", -7643914, -15001050, -15461603), new Preset("Rose", -770210, -14410720, -15002344), new Preset("Mint", -15681151, -15064288, -15459560), new Preset("Amber", -680437, -14409445, -15001324), new Preset("Mono", -1710101, -14671840, -15263977));
    public final ColorSetting accent = new ColorSetting("Accent", -13338378);
    public final NumberSetting rounding = new NumberSetting("Corner radius", 16.0f, 0.0f, 16.0f).step(1.0f);
    public final NumberSetting backgroundDim = new NumberSetting("Background dim", 0.55f, 0.0f, 0.8f).step(0.05f);
    public final BooleanSetting menuBlur = new BooleanSetting("Menu blur", true);
    public final BooleanSetting moduleDividers = new BooleanSetting("Module dividers", true);
    public final NumberSetting uiScale = new NumberSetting("UI scale", 1.0f, 0.75f, 1.5f).step(0.05f);
    public final ModeSetting font;
    public final NumberSetting animationSpeed;
    public final BooleanSetting animateLines;
    public final BooleanSetting customMainMenu;
    public final BooleanSetting communityPopup;
    public final Surface panels;
    public final Surface cards;
    public final Surface tabBar;
    private final List<Surface> surfaces;
    private final List<Setting> shared;
    private final List<Setting> settings;
    private final JsonObject defaults;
    private boolean loaded;
    private boolean readOnly;

    private ThemeStore() {
        this.font = new ModeSetting("Font", UiFont.GEIST.label, UiFont.labels());
        this.animationSpeed = new NumberSetting("Animation speed", 1.3f, 0.0f, 2.0f).step(0.1f);
        this.animateLines = new BooleanSetting("Animate patterns", true);
        this.customMainMenu = new BooleanSetting("Custom menus", true);
        this.communityPopup = new BooleanSetting("Community popup", true);
        this.panels = new Surface("panels", "Panels", false, -14736856, 1.0f, 0.45f, "Flow");
        this.cards = new Surface("cards", "Cards", true, -15263458, 1.0f, 0.85f);
        this.tabBar = new Surface("tabbar", "Tab bar", false, -15263458, 1.0f, 0.5f, "Flow");
        this.surfaces = List.of(this.panels, this.cards, this.tabBar);
        ArrayList<BooleanSetting> all = new ArrayList<BooleanSetting>(List.of(this.accent, this.rounding, this.backgroundDim, this.menuBlur, this.moduleDividers, this.uiScale, this.font, this.animationSpeed, this.animateLines));
        for (Surface surface : this.surfaces) {
            all.addAll(surface.settings());
        }
        this.shared = List.copyOf(all);
        all.add(this.customMainMenu);
        all.add(this.communityPopup);
        this.settings = List.copyOf(all);
        ThemeStore.useBanner(this.cards);
        this.defaults = ThemeStore.snapshot(this.settings, true);
        this.customMainMenu.onChange(() -> {
            class_310 mc = class_310.method_1551();
            if (mc != null && mc.method_22683() != null) {
                mc.method_24288();
            }
        });
    }

    public static ThemeStore get() {
        INSTANCE.ensureLoaded();
        return INSTANCE;
    }

    public List<Surface> surfaces() {
        return this.surfaces;
    }

    public void applyPreset(Preset preset) {
        this.accent.setArgb(preset.accent());
        this.panels.color.setArgb(preset.panel());
        this.cards.color.setArgb(preset.card());
        this.tabBar.color.setArgb(preset.card());
    }

    public boolean matchesPreset(Preset preset) {
        return this.accent.rgb() == (preset.accent() & 0xFFFFFF) && this.panels.color.rgb() == (preset.panel() & 0xFFFFFF) && this.cards.color.rgb() == (preset.card() & 0xFFFFFF);
    }

    public void resetToDefaults() {
        ThemeStore.apply(this.shared, this.defaults, true);
        this.installBanner();
    }

    public static Path backgroundsDir() {
        return FabricLoader.getInstance().getConfigDir().resolve("anubis").resolve("backgrounds");
    }

    public String exportString() {
        JsonObject root = new JsonObject();
        root.add(THEME_KEY, (JsonElement)ThemeStore.snapshot(this.shared, false));
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
            ThemeStore.apply(this.shared, values.getAsJsonObject(), false);
            return true;
        }
        catch (RuntimeException e) {
            return false;
        }
    }

    public JsonObject profileSnapshot() {
        return ThemeStore.snapshot(this.shared, true);
    }

    public void applyProfile(JsonObject values) {
        for (Setting setting : this.shared) {
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
        for (Setting setting : this.shared) {
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
        this.readFile();
        this.installBanner();
    }

    private void readFile() {
        Path path = this.configPath();
        if (!Files.isRegularFile(path, new LinkOption[0])) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(path);){
            int version;
            JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            int n = version = root.has("version") ? root.get("version").getAsInt() : 0;
            if (version > 4) {
                this.readOnly = true;
                LOGGER.warn("[Anubis] Theme version {} is newer than supported {}; skipping load", (Object)version, (Object)4);
                return;
            }
            if (root.has("values") && root.get("values").isJsonObject()) {
                ThemeStore.apply(this.settings, root.getAsJsonObject("values"), true);
            }
            if (version < 3) {
                this.adoptNewDefaults();
            }
            if (version < 4) {
                this.adoptBanner();
            }
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to load theme; preserving the file", (Throwable)e);
            this.readOnly = true;
        }
    }

    private void adoptNewDefaults() {
        if (this.panels.background.is("None")) {
            this.panels.background.set("Flow");
        }
        if (Math.abs(this.panels.opacity.get() - 0.92f) < 0.001f) {
            this.panels.opacity.set(1.0f);
        }
        for (Surface surface : List.of(this.cards, this.tabBar)) {
            if (!(Math.abs(surface.opacity.get() - 0.97f) < 0.001f)) continue;
            surface.opacity.set(1.0f);
        }
    }

    private void adoptBanner() {
        if (this.cards.background.is("None") && this.cards.image.get().isBlank()) {
            ThemeStore.useBanner(this.cards);
        }
    }

    private static void useBanner(Surface surface) {
        surface.background.set("Image");
        surface.image.set(DEFAULT_BANNER);
        surface.coverage.set("Header");
        surface.headerHeight.set(157.0f);
        surface.imageOpacity.set(1.0f);
        surface.imageScale.set(1.1f);
        surface.imageX.set(0.6f);
        surface.imageY.set(0.55f);
    }

    private void installBanner() {
        if (!DEFAULT_BANNER.equals(this.cards.image.get())) {
            return;
        }
        Path target = ThemeStore.backgroundsDir().resolve(DEFAULT_BANNER);
        if (Files.isRegularFile(target, new LinkOption[0])) {
            return;
        }
        try (InputStream in = ThemeStore.class.getResourceAsStream(BANNER_RESOURCE);){
            if (in == null) {
                return;
            }
            Files.createDirectories(target.getParent(), new FileAttribute[0]);
            Files.copy(in, target, new CopyOption[0]);
        }
        catch (IOException e) {
            LOGGER.warn("[Anubis] Could not install the default card banner", (Throwable)e);
        }
    }

    public synchronized void save() {
        if (this.readOnly || !this.loaded) {
            return;
        }
        JsonObject root = new JsonObject();
        root.addProperty("version", (Number)4);
        root.add("values", (JsonElement)ThemeStore.snapshot(this.settings, true));
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
            LOGGER.warn("[Anubis] Failed to save theme", (Throwable)e);
        }
    }

    private static JsonObject snapshot(List<Setting> from, boolean withPrivate) {
        JsonObject values = new JsonObject();
        for (Setting setting : from) {
            if (!withPrivate && setting.isPrivate()) continue;
            values.add(setting.id(), setting.serialize());
        }
        return values;
    }

    private static void apply(List<Setting> from, JsonObject values, boolean withPrivate) {
        for (Setting setting : from) {
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
        return FabricLoader.getInstance().getConfigDir().resolve("anubis").resolve("theme.json");
    }

    @Environment(value=EnvType.CLIENT)
    public record Preset(String name, int accent, int panel, int card) {
    }
}

