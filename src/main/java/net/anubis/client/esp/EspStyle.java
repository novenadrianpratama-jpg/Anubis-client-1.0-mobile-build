/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.esp;

import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.module.setting.Setting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class EspStyle {
    public static final String OUTLINE = "Outline";
    public static final String BOXES = "Boxes";
    public final ModeSetting shape;
    public final NumberSetting boxSize;
    public final NumberSetting fillAlpha;
    public final NumberSetting lineWidth;
    public final BooleanSetting glow;
    public final BooleanSetting fade;
    public final BooleanSetting reveal;
    public final NumberSetting range;

    public EspStyle(Registrar r, String defaultShape, float defaultFillAlpha, float defaultRange) {
        this.shape = r.add("shape", new ModeSetting("Shape", defaultShape, OUTLINE, BOXES));
        this.boxSize = r.add("box-size", new NumberSetting("Box Size", 1.0f, 0.1f, 2.0f).step(0.1f));
        this.fillAlpha = r.add("fill-alpha", new NumberSetting("Fill Alpha", defaultFillAlpha, 0.0f, 1.0f).step(0.05f));
        this.lineWidth = r.add("line-width", new NumberSetting("Line Width", 1.5f, 0.5f, 5.0f).step(0.5f));
        this.glow = r.add("glow", new BooleanSetting("Glow", true));
        this.fade = r.add("distance-fade", new BooleanSetting("Distance Fade", true));
        this.reveal = r.add("reveal", new BooleanSetting("Reveal Animation", true));
        this.range = r.add("range", new NumberSetting("Range", defaultRange, 16.0f, 2048.0f).step(1.0f));
        this.boxSize.visibleWhen(() -> this.shape.is(BOXES));
    }

    public boolean outline() {
        return this.shape.is(OUTLINE);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Registrar {
        public <S extends Setting> S add(String var1, S var2);
    }
}

