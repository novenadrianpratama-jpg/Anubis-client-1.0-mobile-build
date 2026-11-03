/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.GpuTexture
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10868
 *  net.minecraft.class_1297
 *  net.minecraft.class_1299
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_1531
 *  net.minecraft.class_156
 *  net.minecraft.class_1657
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1826
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_742
 *  net.minecraft.class_8685
 *  org.lwjgl.opengl.GL11
 */
package net.anubis.client.gui;

import com.mojang.blaze3d.textures.GpuTexture;
import imgui.ImDrawList;
import imgui.ImFont;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.anubis.client.config.Surface;
import net.anubis.client.gui.EgyptDraw;
import net.anubis.client.gui.HudChrome;
import net.anubis.client.gui.HudGlyphs;
import net.anubis.client.gui.HudLayoutStore;
import net.anubis.client.gui.HudRenderer;
import net.anubis.client.gui.ItemIcons;
import net.anubis.client.imgui.ImGuiManager;
import net.anubis.client.module.impl.client.HudModule;
import net.anubis.client.module.impl.misc.NameProtectModule;
import net.anubis.client.util.combat.CombatTarget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10868;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_156;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1826;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_742;
import net.minecraft.class_8685;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
final class TargetCard {
    private static final long COMBAT_MEMORY_MS = 4000L;
    private static final float LINGER = 0.8f;
    private static final float SHOW_TIME = 0.2f;
    private static final float HIDE_TIME = 0.34f;
    private static final float ENTER_LIFT = 7.0f;
    private static final float HEALTH_RATE = 13.0f;
    private static final float TRAIL_HOLD = 0.4f;
    private static final float TRAIL_RATE = 4.5f;
    private static final float STRIKE_TIME = 0.45f;
    private static final long NAME_REFRESH_MS = 1000L;
    private static final float PAD = 8.0f;
    private static final float PORTRAIT = 40.0f;
    private static final float FACE = 32.0f;
    private static final float CONTENT_H = 46.0f;
    private static final float COLUMN_GAP = 10.0f;
    private static final float COLUMN_W = 178.0f;
    private static final float NAME_ROW = 18.0f;
    private static final float BAR_TOP = 22.0f;
    private static final float BAR_H = 6.0f;
    private static final float GEAR = 16.0f;
    private static final float GEAR_GAP = 3.0f;
    private static final float ANKH_H = 11.0f;
    private static final float ANKH_GAP = 4.0f;
    private static final float CLUSTER_GAP = 8.0f;
    private static final int MAT = 0x59000000;
    private static final int NOTCH = 0x47000000;
    private static final String ELLIPSIS = "...";
    private static final float SKIN_FACE_U = 0.125f;
    private static final float SKIN_HAT_U = 0.625f;
    private static final float SKIN_TOP_V = 0.125f;
    private static final float SKIN_PART = 0.125f;
    private static final class_1304[] GEAR_ORDER = new class_1304[]{class_1304.field_6173, class_1304.field_6171, class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166};
    private class_1309 target;
    private float presence;
    private float lost;
    private float health;
    private float trail;
    private float trailHold;
    private float absorb;
    private float lastHealth;
    private int lastHurt;
    private float strike;
    private float clock;
    private class_1309 namedFor;
    private int nameGeneration = Integer.MIN_VALUE;
    private long namedAt;
    private String rawName = "";
    private String fittedName = "";
    private int fittedWidth = -1;
    private int fittedPx = -1;
    private int distanceTenths = Integer.MIN_VALUE;
    private String distanceText = "";
    private int healthTenths = Integer.MIN_VALUE;
    private String healthText = "";
    private int absorbWhole = Integer.MIN_VALUE;
    private String absorbText = "";
    private final Set<GpuTexture> crispSkins = Collections.newSetFromMap(new WeakHashMap());

    TargetCard() {
    }

    void reset() {
        this.target = null;
        this.namedFor = null;
        this.presence = 0.0f;
        this.lost = 0.0f;
    }

    boolean visible() {
        return this.target != null && this.presence > 0.0f;
    }

    float planWidth(float s) {
        return this.visible() ? (float)Math.round(244.0f * s) : 1.0f;
    }

    float planHeight(float s) {
        return Math.round(62.0f * s);
    }

    void update(class_310 mc, HudModule hud, boolean active, boolean editMode, float dt) {
        boolean wanted;
        class_1309 next;
        this.clock += dt;
        if (this.target != null && (this.target.method_73183() != mc.field_1687 || !editMode && this.target == mc.field_1724)) {
            this.reset();
        }
        class_1309 class_13092 = next = active ? TargetCard.pick(mc, hud) : null;
        if (next == null && editMode) {
            next = mc.field_1724;
        }
        if (next != null && next != this.target) {
            this.adopt(next);
        }
        this.lost = next != null ? 0.0f : this.lost + dt;
        boolean bl = wanted = this.target != null && (next != null || this.lost < 0.8f);
        this.presence = editMode && wanted ? 1.0f : class_3532.method_15363((float)(this.presence + (wanted ? dt / 0.2f : -dt / 0.34f)), (float)0.0f, (float)1.0f);
        if (this.presence <= 0.0f && !wanted) {
            this.target = null;
            this.namedFor = null;
            return;
        }
        if (this.target != null) {
            this.follow(dt);
        }
    }

    private static class_1309 pick(class_310 mc, HudModule hud) {
        class_1309 entity;
        class_1309 class_13092 = entity = hud.targetOnLook() ? CombatTarget.current(4000L) : CombatTarget.lastHit(4000L);
        if (entity == null || entity == mc.field_1724 || entity instanceof class_1531) {
            return null;
        }
        if (hud.targetPlayersOnly() && !(entity instanceof class_1657)) {
            return null;
        }
        return entity;
    }

    private void adopt(class_1309 next) {
        this.target = next;
        float max = Math.max(1.0f, next.method_6063());
        this.trail = this.lastHealth = class_3532.method_15363((float)next.method_6032(), (float)0.0f, (float)max);
        this.health = this.lastHealth;
        this.absorb = Math.max(0.0f, next.method_6067());
        this.trailHold = 0.0f;
        this.strike = 0.0f;
        this.lastHurt = next.field_6235;
    }

    private void follow(float dt) {
        float max = Math.max(1.0f, this.target.method_6063());
        float hp = class_3532.method_15363((float)this.target.method_6032(), (float)0.0f, (float)max);
        if (hp < this.lastHealth - 0.01f) {
            this.trailHold = 0.4f;
            this.strike = 1.0f;
        }
        if (this.target.field_6235 > this.lastHurt) {
            this.strike = 1.0f;
        }
        this.lastHurt = this.target.field_6235;
        this.lastHealth = hp;
        this.health = HudGlyphs.approach(this.health, hp, 13.0f, dt);
        if (this.trailHold > 0.0f) {
            this.trailHold -= dt;
        } else {
            this.trail = HudGlyphs.approach(this.trail, this.health, 4.5f, dt);
        }
        if (this.trail < this.health) {
            this.trail = this.health;
        }
        this.absorb = HudGlyphs.approach(this.absorb, Math.max(0.0f, this.target.method_6067()), 13.0f, dt);
        this.strike = Math.max(0.0f, this.strike - dt / 0.45f);
    }

    void draw(ImDrawList dl, HudLayoutStore.HudElementState state, float x, float y, float alpha, boolean shadow, HudModule hud) {
        if (this.target == null) {
            return;
        }
        float shown = HudGlyphs.easeOutQuint(this.presence);
        float a = alpha * shown;
        if (a <= 0.004f) {
            return;
        }
        class_310 mc = class_310.method_1551();
        float s = HudRenderer.scaleOf(state);
        y = Math.round(y + 7.0f * s * (1.0f - shown));
        float w = state.width;
        float h = state.height;
        Surface surface = HudRenderer.hudSurface(state);
        boolean guard = HudChrome.needsTextGuard(surface);
        HudChrome.card(dl, surface, x, y, w, h, HudRenderer.hudRounding() * s, a, shadow, s);
        ImGuiManager fonts = ImGuiManager.getInstance();
        ImFont textFont = fonts.hudTextFont();
        ImFont labelFont = fonts.hudLabelFont();
        int textPx = HudChrome.px(17.0f, s);
        int labelPx = HudChrome.px(14.0f, s);
        float left = x + (float)Math.round(8.0f * s);
        float top = y + (float)Math.round(8.0f * s);
        float frame = Math.round(40.0f * s);
        this.drawPortrait(dl, mc, left, top + (float)Math.round((46.0f * s - frame) * 0.5f), frame, s, a);
        float colLeft = left + frame + (float)Math.round(10.0f * s);
        float colRight = x + w - (float)Math.round(8.0f * s);
        float max = Math.max(1.0f, this.target.method_6063());
        float fraction = class_3532.method_15363((float)(this.health / max), (float)0.0f, (float)1.0f);
        float textTop = top + (float)Math.floor((18.0f * s - (float)textPx) * 0.5f);
        float labelTop = textTop + HudChrome.baselineOffset(textFont, textPx, labelFont, labelPx);
        this.refreshNumbers(mc);
        float absorbW = this.absorbText.isEmpty() ? 0.0f : HudChrome.width(labelFont, labelPx, this.absorbText);
        float healthW = HudChrome.tabularWidth(textFont, textPx, this.healthText);
        float ankhH = 11.0f * s;
        float clusterW = ankhH * 0.62f + 4.0f * s + healthW + (absorbW > 0.0f ? 4.0f * s + absorbW : 0.0f);
        float cursor = colRight - clusterW;
        HudGlyphs.ankh(dl, cursor + ankhH * 0.31f, textTop + (float)textPx * 0.52f, ankhH, Math.max(1.0f, 1.3f * s), HudGlyphs.ink(-871878, a));
        HudChrome.tabular(dl, textFont, textPx, cursor += ankhH * 0.62f + 4.0f * s, textTop, HudGlyphs.ink(HudGlyphs.vitality(fraction), a), this.healthText, guard);
        if (absorbW > 0.0f) {
            HudChrome.text(dl, labelFont, labelPx, cursor + healthW + 4.0f * s, labelTop, HudGlyphs.ink(-12750874, a), this.absorbText, guard);
        }
        String name = this.fittedName(textFont, textPx, colRight - clusterW - 8.0f * s - colLeft);
        HudChrome.text(dl, textFont, textPx, colLeft, textTop, HudChrome.scaleAlpha(HudChrome.TEXT_NAME, a), name, guard);
        float barTop = top + (float)Math.round(22.0f * s);
        this.drawBar(dl, colLeft, barTop, colRight, Math.max(3.0f, (float)Math.round(6.0f * s)), max, s, a);
        int gearPx = (int)ItemIcons.pixels(16.0f * s);
        float gearTop = top + (float)Math.round(46.0f * s) - (float)gearPx;
        float distanceW = HudChrome.width(labelFont, labelPx, this.distanceText);
        float distanceX = colRight - distanceW;
        HudChrome.text(dl, labelFont, labelPx, distanceX, gearTop + (float)Math.round((float)(gearPx - labelPx) * 0.5f), HudChrome.scaleAlpha(HudChrome.TEXT_LABEL, a), this.distanceText, guard);
        if (hud.targetGear()) {
            this.drawGear(dl, colLeft, gearTop, gearPx, distanceX - 6.0f * s, s, a);
        }
        float rule = Math.max(1.0f, (float)Math.round(s));
        HudGlyphs.register(dl, x + w * 0.12f, x + w * 0.88f, y + h - rule - Math.max(1.0f, (float)Math.round(2.0f * s)), rule, HudGlyphs.ink(-871878, a * 0.35f));
    }

    private void drawPortrait(ImDrawList dl, class_310 mc, float fx, float fy, float size, float s, float a) {
        float r = Math.min(HudRenderer.hudRounding(), 6.0f) * s;
        dl.addRectFilled(fx, fy, fx + size, fy + size, HudChrome.scaleAlpha(0x59000000, a), r);
        float face = Math.max(8.0f, (float)Math.round(32.0f * s / 8.0f) * 8.0f);
        if (face > size - 4.0f) {
            face = Math.max(8.0f, face - 8.0f);
        }
        float ix = Math.round(fx + (size - face) * 0.5f);
        float iy = Math.round(fy + (size - face) * 0.5f);
        boolean drawn = false;
        class_1309 class_13092 = this.target;
        if (class_13092 instanceof class_742) {
            class_742 player = (class_742)class_13092;
            int texture = this.skinTexture(mc, player);
            if (texture != 0) {
                float fr = Math.max(1.0f, (float)Math.round(2.0f * s));
                int tint = HudChrome.scaleAlpha(-1, a);
                dl.addImageRounded((long)texture, ix, iy, ix + face, iy + face, 0.125f, 0.125f, 0.25f, 0.25f, tint, fr, 240);
                dl.addImageRounded((long)texture, ix, iy, ix + face, iy + face, 0.625f, 0.125f, 0.75f, 0.25f, tint, fr, 240);
                drawn = true;
            }
        } else {
            class_1826 egg = class_1826.method_8019((class_1299)this.target.method_5864());
            if (egg != null) {
                float shown = ItemIcons.pixels(face);
                float ex = Math.round(fx + (size - shown) * 0.5f);
                float ey = Math.round(fy + (size - shown) * 0.5f);
                ItemIcons.draw(dl, (class_1792)egg, ex, ey, shown, a);
                drawn = true;
            }
        }
        if (!drawn) {
            HudGlyphs.horusEye(dl, fx + size * 0.5f, fy + size * 0.46f, size * 0.3f, Math.max(1.0f, 1.4f * s), HudGlyphs.ink(-871878, a * 0.85f));
        }
        int edge = HudGlyphs.ink(EgyptDraw.mix(-871878, -2537681, this.strike), a * (0.7f + 0.3f * this.strike));
        dl.addRect(fx, fy, fx + size, fy + size, edge, r, 0, Math.max(1.0f, s));
        float tie = Math.max(1.0f, (float)Math.round(1.5f * s));
        float tieY = fy + size + Math.max(1.0f, (float)Math.round(s));
        dl.addRectFilled(fx + size * 0.22f, tieY, fx + size * 0.78f, tieY + tie, HudGlyphs.ink(-871878, a * 0.8f), tie * 0.5f);
        if (this.strike > 0.0f) {
            float grow = (1.0f - this.strike) * 6.0f * s;
            dl.addRect(fx - grow, fy - grow, fx + size + grow, fy + size + grow, HudGlyphs.ink(-2537681, a * this.strike * 0.7f), r + grow, 0, Math.max(1.0f, 1.5f * s));
        }
    }

    private void drawBar(ImDrawList dl, float l, float t, float r, float bh, float max, float s, float a) {
        float bw = r - l;
        float round = bh * 0.5f;
        float fraction = class_3532.method_15363((float)(this.health / max), (float)0.0f, (float)1.0f);
        float trailFraction = class_3532.method_15363((float)(this.trail / max), (float)0.0f, (float)1.0f);
        dl.addRectFilled(l, t, r, t + bh, HudChrome.scaleAlpha(0x1AFFFFFF, a), round);
        if (trailFraction > fraction + 0.002f) {
            float tr = l + bw * trailFraction;
            dl.addRectFilled(l, t, tr, t + bh, HudGlyphs.ink(-11654, a * 0.55f), Math.min(round, (tr - l) * 0.5f));
        }
        if (fraction > 0.0f) {
            float fr = l + bw * fraction;
            float fill = a;
            if (fraction < 0.25f) {
                fill *= 0.72f + 0.28f * (0.5f + 0.5f * (float)Math.sin(this.clock * 7.5f));
            }
            float corner = Math.min(round, (fr - l) * 0.5f);
            dl.addRectFilled(l, t, fr, t + bh, HudGlyphs.ink(HudGlyphs.vitality(fraction), fill), corner);
            dl.addRectFilled(l, t, fr, t + bh * 0.45f, HudGlyphs.ink(-1, fill * 0.16f), corner, 48);
        }
        float step = max <= 40.0f ? 2.0f : (max <= 200.0f ? 10.0f : max / 20.0f);
        float notch = Math.max(1.0f, (float)Math.round(0.75f * s));
        int notchInk = HudChrome.scaleAlpha(0x47000000, a);
        for (float mark = step; mark < max - 0.01f; mark += step) {
            float nx = Math.round(l + bw * mark / max);
            dl.addRectFilled(nx, t, nx + notch, t + bh, notchInk);
        }
        float absorbFraction = class_3532.method_15363((float)(this.absorb / max), (float)0.0f, (float)1.0f);
        if (absorbFraction > 0.002f) {
            float ar = l + bw * absorbFraction;
            dl.addRectFilled(l, t, ar, t + Math.max(1.5f, bh * 0.45f), HudGlyphs.ink(-12750874, a * 0.95f), Math.min(round, (ar - l) * 0.5f), 48);
        }
    }

    private void drawGear(ImDrawList dl, float gx, float gy, int gearPx, float limit, float s, float a) {
        float step = gearPx + Math.round(3.0f * s);
        for (class_1304 slot : GEAR_ORDER) {
            float left;
            class_1799 stack = this.target.method_6118(slot);
            if (stack.method_7960()) continue;
            if (gx + (float)gearPx > limit) break;
            ItemIcons.draw(dl, stack, gx, gy, (float)gearPx, a);
            if (stack.method_7958()) {
                HudGlyphs.sparkle(dl, gx + (float)gearPx - 1.5f * s, gy + 1.5f * s, 2.6f * s, HudGlyphs.ink(-11654, a * 0.9f));
            }
            if (stack.method_7963() && stack.method_7936() > 0 && (left = 1.0f - (float)stack.method_7919() / (float)stack.method_7936()) < 0.25f) {
                float line = Math.max(1.0f, (float)Math.round(1.5f * s));
                dl.addRectFilled(gx + 1.0f, gy + (float)gearPx + line * 0.5f, gx + (float)gearPx - 1.0f, gy + (float)gearPx + line * 1.5f, HudGlyphs.ink(-2537681, a), line * 0.5f);
            }
            gx += step;
        }
    }

    private void refreshNumbers(class_310 mc) {
        int distance;
        int whole;
        float max = Math.max(1.0f, this.target.method_6063());
        int tenths = Math.round(class_3532.method_15363((float)this.target.method_6032(), (float)0.0f, (float)max) * 10.0f);
        if (tenths != this.healthTenths) {
            this.healthTenths = tenths;
            String string = this.healthText = tenths % 10 == 0 ? Integer.toString(tenths / 10) : tenths / 10 + "." + tenths % 10;
        }
        if ((whole = Math.round(Math.max(0.0f, this.target.method_6067()))) != this.absorbWhole) {
            this.absorbWhole = whole;
            this.absorbText = whole > 0 ? "+" + whole : "";
        }
        int n = distance = mc.field_1724 == null ? 0 : Math.round(mc.field_1724.method_5739((class_1297)this.target) * 10.0f);
        if (distance != this.distanceTenths) {
            this.distanceTenths = distance;
            this.distanceText = distance / 10 + "." + distance % 10 + "m";
        }
    }

    private String fittedName(ImFont font, int fontPx, float maxW) {
        int budget;
        int generation = NameProtectModule.generation();
        long now = class_156.method_658();
        if (this.target != this.namedFor || generation != this.nameGeneration || now - this.namedAt > 1000L) {
            this.namedFor = this.target;
            this.nameGeneration = generation;
            this.namedAt = now;
            String fresh = NameProtectModule.protect(this.target.method_5477()).getString();
            if (!fresh.equals(this.rawName)) {
                this.rawName = fresh;
                this.fittedWidth = -1;
            }
        }
        if ((budget = Math.max(0, (int)maxW)) == this.fittedWidth && fontPx == this.fittedPx) {
            return this.fittedName;
        }
        this.fittedWidth = budget;
        this.fittedPx = fontPx;
        this.fittedName = TargetCard.fit(font, fontPx, this.rawName, budget);
        return this.fittedName;
    }

    private static String fit(ImFont font, int fontPx, String name, float maxW) {
        if (HudChrome.width(font, fontPx, name) <= maxW) {
            return name;
        }
        float dots = HudChrome.width(font, fontPx, ELLIPSIS);
        int lo = 0;
        int hi = name.length();
        while (lo < hi) {
            int mid = lo + hi + 1 >>> 1;
            if (HudChrome.width(font, fontPx, name.substring(0, mid)) + dots <= maxW) {
                lo = mid;
                continue;
            }
            hi = mid - 1;
        }
        return lo == 0 ? "" : name.substring(0, lo).stripTrailing() + ELLIPSIS;
    }

    private int skinTexture(class_310 mc, class_742 player) {
        class_10868 gl;
        GpuTexture texture;
        class_8685 skin = player.method_52814();
        if (NameProtectModule.hidesName(player.method_7334().name())) {
            skin = NameProtectModule.hiddenHead(player.method_5667(), skin);
        }
        class_2960 path = skin.comp_1626().comp_3627();
        try {
            texture = mc.method_1531().method_4619(path).method_68004();
        }
        catch (IllegalStateException notUploaded) {
            return 0;
        }
        if (!(texture instanceof class_10868) || (gl = (class_10868)texture).isClosed()) {
            return 0;
        }
        if (this.crispSkins.add(texture)) {
            int bound = GL11.glGetInteger((int)32873);
            GL11.glBindTexture((int)3553, (int)gl.method_68427());
            GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
            GL11.glBindTexture((int)3553, (int)bound);
        }
        return gl.method_68427();
    }
}

