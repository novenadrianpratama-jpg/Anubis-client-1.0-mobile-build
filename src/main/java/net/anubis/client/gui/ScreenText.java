/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_5348
 *  net.minecraft.class_9848
 */
package net.anubis.client.gui;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5348;
import net.minecraft.class_9848;

@Environment(value=EnvType.CLIENT)
public final class ScreenText {
    private static final float IN = 0.4f;
    private static final float OUT = 0.8f;
    private final int rgb;
    private final float stay;
    private class_2561 title;
    private class_2561 subtitle;
    private long start;

    public ScreenText(int rgb, float staySeconds) {
        this.rgb = rgb & 0xFFFFFF;
        this.stay = staySeconds;
    }

    public void show(String line, String below) {
        this.title = class_2561.method_43470((String)line);
        this.subtitle = class_2561.method_43470((String)below);
        this.start = 0L;
    }

    public void render(class_332 g) {
        float t;
        if (this.title == null) {
            return;
        }
        class_310 mc = class_310.method_1551();
        long now = System.nanoTime();
        if (this.start == 0L) {
            if (mc.field_1755 != null || mc.field_1687 == null) {
                return;
            }
            this.start = now;
        }
        if ((t = (float)(now - this.start) / 1.0E9f) >= 0.4f + this.stay + 0.8f) {
            this.title = null;
            return;
        }
        float fade = t < 0.4f ? t / 0.4f : Math.min(1.0f, (0.4f + this.stay + 0.8f - t) / 0.8f);
        int alpha = Math.round(fade * 255.0f);
        if (alpha <= 8) {
            return;
        }
        class_327 font = mc.field_1772;
        int color = class_9848.method_61330((int)alpha, (int)this.rgb);
        g.method_51448().pushMatrix();
        g.method_51448().translate((float)g.method_51421() / 2.0f, (float)g.method_51443() / 2.0f - 30.0f);
        g.method_51448().scale(3.0f, 3.0f);
        int n = -font.method_27525((class_5348)this.title) / 2;
        Objects.requireNonNull(font);
        g.method_51439(font, this.title, n, -9 / 2, color, true);
        g.method_51448().popMatrix();
        g.method_51448().pushMatrix();
        g.method_51448().translate((float)g.method_51421() / 2.0f, (float)g.method_51443() / 2.0f);
        g.method_51448().scale(1.5f, 1.5f);
        int n2 = -font.method_27525((class_5348)this.subtitle) / 2;
        Objects.requireNonNull(font);
        g.method_51439(font, this.subtitle, n2, -9 / 2, color, true);
        g.method_51448().popMatrix();
    }
}

