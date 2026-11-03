/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12179
 *  net.minecraft.class_12180
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 */
package net.anubis.client.module.impl.combat.crystal;

import imgui.ImDrawList;
import java.util.Locale;
import net.anubis.client.gui.WorldLabel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12179;
import net.minecraft.class_12180;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class ShotView {
    private static final long SLIDE_MS = 120L;
    private static final long HOLD_MS = 150L;
    private static final long FADE_MS = 350L;
    private static final float FILL_ALPHA = 0.22f;
    private static final float LINE_WIDTH = 1.5f;
    private static final double LABEL_RISE = 0.25;
    private class_238 from;
    private class_238 to;
    private long movedAt;
    private long shownAt;
    private int color;
    private float damage;
    private float selfDamage;

    public void show(class_238 box, int color, float damage, float selfDamage) {
        long now = System.currentTimeMillis();
        if (!this.showing(now)) {
            this.from = box;
            this.movedAt = now;
        } else if (!box.equals((Object)this.to)) {
            this.from = this.box(now);
            this.movedAt = now;
        }
        this.to = box;
        this.shownAt = now;
        this.color = color;
        this.damage = damage;
        this.selfDamage = selfDamage;
    }

    public void clear() {
        this.to = null;
    }

    public boolean showing() {
        return this.showing(System.currentTimeMillis());
    }

    public void draw() {
        long now = System.currentTimeMillis();
        if (!this.showing(now)) {
            return;
        }
        float alpha = this.alpha(now);
        class_12180.method_75541((class_238)this.box(now), (class_12179)class_12179.method_75537((int)ShotView.fade(this.color, alpha), (float)1.5f, (int)ShotView.fade(this.color, alpha * 0.22f))).method_75533();
    }

    public void label(ImDrawList dl, float screenW, float screenH) {
        long now = System.currentTimeMillis();
        if (!this.showing(now)) {
            return;
        }
        class_238 box = this.box(now);
        class_243 top = new class_243(box.method_1005().field_1352, box.field_1325 + 0.25, box.method_1005().field_1350);
        float[] screen = WorldLabel.project(class_310.method_1551(), top, screenW, screenH);
        if (screen == null) {
            return;
        }
        WorldLabel.drawText(dl, screen[0], screen[1], String.format(Locale.ROOT, "%.1f", Float.valueOf(this.damage)), String.format(Locale.ROOT, "self %.1f", Float.valueOf(this.selfDamage)), this.alpha(now));
    }

    private boolean showing(long now) {
        return this.to != null && this.alpha(now) > 0.0f;
    }

    private float alpha(long now) {
        long idle = now - this.shownAt - 150L;
        return idle <= 0L ? 1.0f : Math.max(0.0f, 1.0f - (float)idle / 350.0f);
    }

    private class_238 box(long now) {
        float t = Math.min(1.0f, (float)(now - this.movedAt) / 120.0f);
        float eased = 1.0f - (1.0f - t) * (1.0f - t) * (1.0f - t);
        return new class_238(this.from.method_61125().method_35590(this.to.method_61125(), (double)eased), this.from.method_61126().method_35590(this.to.method_61126(), (double)eased));
    }

    private static int fade(int argb, float alpha) {
        return Math.round((float)(argb >>> 24) * alpha) << 24 | argb & 0xFFFFFF;
    }
}

