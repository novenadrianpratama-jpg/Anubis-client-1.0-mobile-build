/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.config;

import java.util.List;
import net.anubis.client.module.StableId;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.module.setting.Setting;
import net.anubis.client.module.setting.StringSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class Surface {
    public static final String BG_NONE = "None";
    public static final String BG_IMAGE = "Image";
    public static final String BG_FLOW = "Flow";
    public static final String BG_TOPOGRAPHIC = "Topographic";
    public static final String BG_RINGS = "Rings";
    public static final String BG_GRID = "Grid";
    public static final String MAP_EACH = "Each";
    public static final String MAP_SCREEN = "Screen";
    public static final String COVER_FULL = "Full";
    public static final String COVER_HEADER = "Header";
    public final String label;
    public final boolean card;
    public final ColorSetting color;
    public final NumberSetting opacity;
    public final ModeSetting background;
    public final StringSetting image = new StringSetting("Image", "");
    public final ModeSetting mapping = new ModeSetting("Mapping", "Each", "Each", "Screen");
    public final ModeSetting coverage = new ModeSetting("Coverage", "Header", "Header", "Full");
    public final NumberSetting headerHeight = new NumberSetting("Header height", 120.0f, 70.0f, 220.0f).step(1.0f);
    public final NumberSetting imageOpacity;
    public final NumberSetting imageScale = new NumberSetting("Image scale", 1.0f, 0.5f, 3.0f).step(0.1f);
    public final NumberSetting imageX = new NumberSetting("Image X", 0.5f, 0.0f, 1.0f).step(0.05f);
    public final NumberSetting imageY = new NumberSetting("Image Y", 0.5f, 0.0f, 1.0f).step(0.05f);
    public final NumberSetting lineOpacity = new NumberSetting("Line opacity", 0.5f, 0.1f, 1.0f).step(0.05f);
    private final List<Setting> settings;

    Surface(String key, String label, boolean card, int color, float opacity, float imageOpacity) {
        this(key, label, card, color, opacity, imageOpacity, BG_NONE);
    }

    Surface(String key, String label, boolean card, int color, float opacity, float imageOpacity, String backdrop) {
        this.label = label;
        this.card = card;
        this.color = new ColorSetting("Color", color);
        this.background = new ModeSetting("Background", backdrop, BG_NONE, BG_IMAGE, BG_FLOW, BG_TOPOGRAPHIC, BG_RINGS, BG_GRID);
        this.opacity = new NumberSetting("Opacity", opacity, 0.2f, 1.0f).step(0.01f);
        this.imageOpacity = new NumberSetting("Image opacity", imageOpacity, 0.05f, 1.0f).step(0.05f);
        this.settings = List.of(this.color, this.opacity, this.background, this.image, this.mapping, this.coverage, this.headerHeight, this.imageOpacity, this.imageScale, this.imageX, this.imageY, this.lineOpacity);
        for (Setting setting : this.settings) {
            String name = setting == this.imageY ? "Image focus" : setting.name();
            setting.bindId(key + "." + StableId.legacy(name));
        }
        this.image.visibleWhen(() -> false);
        this.image.privateValue();
        this.mapping.visibleWhen(() -> !card && !this.background.is(BG_NONE));
        this.coverage.visibleWhen(() -> card && !this.background.is(BG_NONE));
        this.headerHeight.visibleWhen(() -> card && !this.background.is(BG_NONE) && this.coverage.is(COVER_HEADER));
        this.imageOpacity.visibleWhen(this::isImage);
        this.imageScale.visibleWhen(this::isImage);
        this.imageX.visibleWhen(this::isImage);
        this.imageY.visibleWhen(this::isImage);
        this.lineOpacity.visibleWhen(this::isPattern);
    }

    public boolean isImage() {
        return this.background.is(BG_IMAGE);
    }

    public boolean isPattern() {
        return !this.background.is(BG_NONE) && !this.background.is(BG_IMAGE);
    }

    public boolean isBanner() {
        return this.card && this.coverage.is(COVER_HEADER);
    }

    public boolean isScreenMapped() {
        return !this.card && this.mapping.is(MAP_SCREEN);
    }

    public List<Setting> settings() {
        return this.settings;
    }
}

