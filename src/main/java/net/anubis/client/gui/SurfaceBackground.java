/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImGui
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import imgui.ImGui;
import net.anubis.client.config.Surface;
import net.anubis.client.config.ThemeStore;
import net.anubis.client.gui.AnubisTheme;
import net.anubis.client.gui.AnubisWidgets;
import net.anubis.client.gui.GuiPalette;
import net.anubis.client.gui.HudChrome;
import net.anubis.client.gui.ThemeImages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class SurfaceBackground {
    private SurfaceBackground() {
    }

    public static float bannerHeight(Surface surface) {
        return surface.isBanner() && SurfaceBackground.isVisible(surface) ? AnubisWidgets.s(surface.headerHeight.get()) : 0.0f;
    }

    public static boolean hasImageBanner(Surface surface) {
        return surface.isImage() && SurfaceBackground.bannerHeight(surface) > 0.0f;
    }

    public static void draw(ImDrawList dl, Surface surface, float x, float y, float w, float h, float rounding) {
        SurfaceBackground.draw(dl, surface, x, y, w, h, rounding, 1.0f);
    }

    public static void draw(ImDrawList dl, Surface surface, float x, float y, float w, float h, float rounding, float alpha) {
        float areaH;
        if (alpha <= 0.001f || !SurfaceBackground.isVisible(surface)) {
            return;
        }
        boolean banner = surface.isBanner();
        float f = areaH = banner ? Math.min(h, AnubisWidgets.s(surface.headerHeight.get())) : h;
        if (surface.isImage()) {
            SurfaceBackground.drawImage(dl, surface, x, y, w, areaH, rounding, banner, alpha, surface.imageScale.get());
        } else {
            SurfaceBackground.drawPattern(dl, surface, x, y, w, areaH, rounding, banner, alpha);
        }
    }

    public static void drawDisc(ImDrawList dl, Surface surface, float cx, float cy, float r, float alpha) {
        if (alpha <= 0.001f || !SurfaceBackground.isVisible(surface)) {
            return;
        }
        float x = cx - r;
        float y = cy - r;
        float size = 2.0f * r;
        if (surface.isImage()) {
            SurfaceBackground.drawImage(dl, surface, x, y, size, size, r, false, alpha, Math.max(1.0f, surface.imageScale.get()));
        } else {
            SurfaceBackground.drawPattern(dl, surface, x, y, size, size, r, false, alpha);
        }
    }

    private static boolean isVisible(Surface surface) {
        if (surface.background.is("None")) {
            return false;
        }
        return !surface.isImage() || ThemeImages.get().texture(surface.image.get()) != null;
    }

    private static void drawImage(ImDrawList dl, Surface surface, float x, float y, float w, float h, float rounding, boolean banner, float alpha, float scale) {
        ThemeImages.Texture texture = ThemeImages.get().texture(surface.image.get());
        if (texture == null) {
            return;
        }
        float[] image = surface.isScreenMapped() ? SurfaceBackground.place(texture, surface, 0.0f, 0.0f, ImGui.getIO().getDisplaySizeX(), ImGui.getIO().getDisplaySizeY(), scale) : SurfaceBackground.place(texture, surface, x, y, w, h, scale);
        SurfaceBackground.drawClipped(dl, texture.id(), image, x, y, w, h, AnubisWidgets.c(AnubisWidgets.withAlpha(-1, surface.imageOpacity.get() * alpha)), rounding, banner ? 48 : 240);
        if (banner) {
            int fill = HudChrome.scaleAlpha(GuiPalette.fill(surface), alpha);
            int clear = fill & 0xFFFFFF;
            dl.addRectFilledMultiColor(x, y + h * 0.3f, x + w, y + h + 1.0f, AnubisWidgets.c(clear), AnubisWidgets.c(clear), AnubisWidgets.c(fill), AnubisWidgets.c(fill));
        }
    }

    public static float[] coverUv(int imageW, int imageH, float boxW, float boxH, float focus) {
        float boxAspect;
        float imageAspect = (float)imageW / (float)Math.max(1, imageH);
        if (imageAspect > (boxAspect = boxW / Math.max(1.0f, boxH))) {
            float span = boxAspect / imageAspect;
            float u0 = (1.0f - span) * 0.5f;
            return new float[]{u0, 0.0f, u0 + span, 1.0f};
        }
        float span = imageAspect / boxAspect;
        float v0 = (1.0f - span) * Math.max(0.0f, Math.min(1.0f, focus));
        return new float[]{0.0f, v0, 1.0f, v0 + span};
    }

    public static float[] place(int imageW, int imageH, float frameX, float frameY, float frameW, float frameH, float scale, float fx, float fy) {
        float k = Math.max(frameW / (float)Math.max(1, imageW), frameH / (float)Math.max(1, imageH)) * scale;
        float w = (float)imageW * k;
        float h = (float)imageH * k;
        float x0 = frameX + (frameW - w) * fx;
        float y0 = frameY + (frameH - h) * fy;
        return new float[]{x0, y0, x0 + w, y0 + h};
    }

    private static float[] place(ThemeImages.Texture texture, Surface surface, float x, float y, float w, float h) {
        return SurfaceBackground.place(texture, surface, x, y, w, h, surface.imageScale.get());
    }

    private static float[] place(ThemeImages.Texture texture, Surface surface, float x, float y, float w, float h, float scale) {
        return SurfaceBackground.place(texture.width(), texture.height(), x, y, w, h, scale, surface.imageX.get(), surface.imageY.get());
    }

    private static void drawClipped(ImDrawList dl, int textureId, float[] image, float x, float y, float w, float h, int tint, float rounding, int corners) {
        float x0 = Math.max(x, image[0]);
        float y0 = Math.max(y, image[1]);
        float x1 = Math.min(x + w, image[2]);
        float y1 = Math.min(y + h, image[3]);
        if (x1 <= x0 || y1 <= y0) {
            return;
        }
        float iw = image[2] - image[0];
        float ih = image[3] - image[1];
        float u0 = (x0 - image[0]) / iw;
        float v0 = (y0 - image[1]) / ih;
        float u1 = (x1 - image[0]) / iw;
        float v1 = (y1 - image[1]) / ih;
        boolean left = x0 - x <= rounding;
        boolean top = y0 - y <= rounding;
        boolean right = x + w - x1 <= rounding;
        boolean bottom = y + h - y1 <= rounding;
        int reached = (left && top ? 16 : 0) | (right && top ? 32 : 0) | (left && bottom ? 64 : 0) | (right && bottom ? 128 : 0);
        int rounded = corners & reached;
        if (rounding > 0.0f && rounded != 0) {
            dl.addImageRounded((long)textureId, x0, y0, x1, y1, u0, v0, u1, v1, tint, rounding, rounded);
        } else {
            dl.addImage((long)textureId, x0, y0, x1, y1, u0, v0, u1, v1, tint);
        }
    }

    public static void drawPart(ImDrawList dl, Surface surface, float boxX, float boxY, float boxW, float boxH, float x, float y, float w, float h, float alpha, float rounding, int cornerFlags) {
        if (alpha <= 0.001f || w <= 0.0f || h <= 0.0f || boxW <= 0.0f || boxH <= 0.0f || !SurfaceBackground.isVisible(surface)) {
            return;
        }
        float screenW = ImGui.getIO().getDisplaySizeX();
        float screenH = ImGui.getIO().getDisplaySizeY();
        boolean screen = surface.isScreenMapped();
        if (surface.isImage()) {
            ThemeImages.Texture texture = ThemeImages.get().texture(surface.image.get());
            if (texture == null) {
                return;
            }
            float[] image = screen ? SurfaceBackground.place(texture, surface, 0.0f, 0.0f, screenW, screenH) : SurfaceBackground.place(texture, surface, boxX, boxY, boxW, boxH);
            SurfaceBackground.drawClipped(dl, texture.id(), image, x, y, w, h, AnubisWidgets.c(AnubisWidgets.withAlpha(-1, surface.imageOpacity.get() * alpha)), rounding, cornerFlags);
            return;
        }
        Lines lines = new Lines(dl, x, y, w, h, 0.0f, surface.lineOpacity.get() * alpha, false, screen ? x : x - boxX, screen ? y : y - boxY, screen ? screenW : boxW, false);
        dl.pushClipRect(x, y, x + w, y + h, true);
        SurfaceBackground.drawLines(surface, lines);
        dl.popClipRect();
    }

    private static void drawPattern(ImDrawList dl, Surface surface, float x, float y, float w, float h, float rounding, boolean banner, float alpha) {
        boolean screen = surface.isScreenMapped();
        Lines lines = new Lines(dl, x, y, w, h, rounding, surface.lineOpacity.get() * alpha, banner, screen ? x : 0.0f, screen ? y : 0.0f, screen ? ImGui.getIO().getDisplaySizeX() : w, true);
        SurfaceBackground.drawLines(surface, lines);
    }

    private static void drawLines(Surface surface, Lines lines) {
        ThemeStore theme = ThemeStore.get();
        float time = theme.animateLines.get() ? (float)ImGui.getTime() * GuiPalette.animationSpeed() : 0.0f;
        switch (surface.background.get()) {
            case "Flow": {
                SurfaceBackground.flow(lines, time);
                break;
            }
            case "Topographic": {
                SurfaceBackground.topographic(lines, time);
                break;
            }
            case "Rings": {
                SurfaceBackground.rings(lines, time);
                break;
            }
            case "Grid": {
                SurfaceBackground.grid(lines, time);
                break;
            }
        }
    }

    private static void flow(Lines lines, float time) {
        float step = AnubisWidgets.s(6.0f);
        float gap = AnubisWidgets.s(10.0f);
        float phase = time * 0.25f;
        int first = (int)Math.floor((lines.originY - AnubisWidgets.s(40.0f)) / gap);
        int last = (int)Math.ceil((lines.originY + lines.h + AnubisWidgets.s(40.0f)) / gap);
        for (int i = first; i <= last; ++i) {
            float prevX = 0.0f;
            float prevY = 0.0f;
            for (float lx = 0.0f; lx <= lines.w + step; lx += step) {
                float u = (lines.originX + lx) / AnubisWidgets.s(1.0f);
                float worldY = (float)i * gap + (float)Math.sin(u * 0.018f + (float)i * 0.35f + phase) * AnubisWidgets.s(14.0f) + (float)Math.sin(u * 0.007f - (float)i * 0.2f - phase * 0.6f) * AnubisWidgets.s(10.0f);
                float ly = worldY - lines.originY;
                if (lx > 0.0f) {
                    lines.segment(prevX, prevY, lx, ly);
                }
                prevX = lx;
                prevY = ly;
            }
        }
    }

    private static void rings(Lines lines, float time) {
        float cx = lines.worldW + AnubisWidgets.s(10.0f) - lines.originX;
        float cy = -AnubisWidgets.s(30.0f) - lines.originY;
        float gap = AnubisWidgets.s(14.0f);
        float offset = time * AnubisWidgets.s(4.0f) % gap;
        float nearest = (float)Math.hypot(Math.max(0.0f, Math.max(-cx, cx - lines.w)), Math.max(0.0f, Math.max(-cy, cy - lines.h)));
        float farthest = (float)Math.hypot(Math.max(Math.abs(cx), Math.abs(cx - lines.w)), Math.max(Math.abs(cy), Math.abs(cy - lines.h)));
        float start = AnubisWidgets.s(24.0f) + offset;
        if (nearest > start) {
            start += (float)Math.floor((nearest - start) / gap) * gap;
        }
        for (float r = start; r <= farthest; r += gap) {
            int segments = Math.max(24, (int)(r * 0.35f));
            float prevX = cx + r;
            float prevY = cy;
            for (int i = 1; i <= segments; ++i) {
                double a = Math.PI * 2 * (double)i / (double)segments;
                float px = cx + (float)Math.cos(a) * r;
                float py = cy + (float)Math.sin(a) * r;
                lines.segment(prevX, prevY, px, py);
                prevX = px;
                prevY = py;
            }
        }
    }

    private static void grid(Lines lines, float time) {
        float cell = AnubisWidgets.s(16.0f);
        float step = AnubisWidgets.s(8.0f);
        float drift = time * AnubisWidgets.s(3.0f) % cell;
        float startX = SurfaceBackground.floorMod(drift - lines.originX, cell);
        float startY = SurfaceBackground.floorMod(drift - lines.originY, cell);
        for (float gx = startX; gx <= lines.w; gx += cell) {
            for (float gy = 0.0f; gy < lines.h; gy += step) {
                lines.segment(gx, gy, gx, gy + step);
            }
        }
        for (float gy = startY; gy <= lines.h; gy += cell) {
            for (float gx = 0.0f; gx < lines.w; gx += step) {
                lines.segment(gx, gy, gx + step, gy);
            }
        }
    }

    private static void topographic(Lines lines, float time) {
        float cell = AnubisWidgets.s(6.0f);
        int cols = (int)Math.ceil(lines.w / cell) + 1;
        int rows = (int)Math.ceil(lines.h / cell) + 1;
        float[] field = new float[cols * rows];
        float drift = time * 0.15f;
        for (int j = 0; j < rows; ++j) {
            for (int i = 0; i < cols; ++i) {
                float u = (lines.originX + (float)i * cell) / AnubisWidgets.s(1.0f);
                float v = (lines.originY + (float)j * cell) / AnubisWidgets.s(1.0f);
                field[j * cols + i] = (float)(Math.sin((double)u * 0.021 + Math.sin((double)v * 0.03 + (double)drift) * 1.6) + Math.cos((double)v * 0.028 + Math.sin((double)u * 0.013 - (double)drift) * 2.1) + 0.6 * Math.sin((double)(u + v) * 0.017 + (double)drift));
            }
        }
        for (float level = -2.4f; level <= 2.4f; level += 0.4f) {
            for (int j = 0; j < rows - 1; ++j) {
                for (int i = 0; i < cols - 1; ++i) {
                    SurfaceBackground.contourCell(lines, field, cols, i, j, cell, level);
                }
            }
        }
    }

    private static void contourCell(Lines lines, float[] f, int cols, int i, int j, float cell, float level) {
        float d;
        float cc;
        float b;
        float a = f[j * cols + i];
        int index = (a > level ? 8 : 0) | ((b = f[j * cols + i + 1]) > level ? 4 : 0) | ((cc = f[(j + 1) * cols + i + 1]) > level ? 2 : 0) | ((d = f[(j + 1) * cols + i]) > level ? 1 : 0);
        if (index == 0 || index == 15) {
            return;
        }
        float x = (float)i * cell;
        float y = (float)j * cell;
        float tx = x + cell * SurfaceBackground.t(a, b, level);
        float ty = y;
        float rx = x + cell;
        float ry = y + cell * SurfaceBackground.t(b, cc, level);
        float bx = x + cell * SurfaceBackground.t(d, cc, level);
        float by = y + cell;
        float lx = x;
        float ly = y + cell * SurfaceBackground.t(a, d, level);
        switch (index) {
            case 1: 
            case 14: {
                lines.segment(lx, ly, bx, by);
                break;
            }
            case 2: 
            case 13: {
                lines.segment(bx, by, rx, ry);
                break;
            }
            case 3: 
            case 12: {
                lines.segment(lx, ly, rx, ry);
                break;
            }
            case 4: 
            case 11: {
                lines.segment(tx, ty, rx, ry);
                break;
            }
            case 6: 
            case 9: {
                lines.segment(tx, ty, bx, by);
                break;
            }
            case 7: 
            case 8: {
                lines.segment(lx, ly, tx, ty);
                break;
            }
            case 5: {
                lines.segment(lx, ly, tx, ty);
                lines.segment(bx, by, rx, ry);
                break;
            }
            case 10: {
                lines.segment(tx, ty, rx, ry);
                lines.segment(lx, ly, bx, by);
                break;
            }
        }
    }

    private static float t(float from, float to, float level) {
        float span = to - from;
        return Math.abs(span) < 1.0E-6f ? 0.5f : (level - from) / span;
    }

    private static float floorMod(float value, float mod) {
        float r = value % mod;
        return r < 0.0f ? r + mod : r;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Lines {
        final ImDrawList dl;
        final float x;
        final float y;
        final float w;
        final float h;
        final float edge;
        final float originX;
        final float originY;
        final float worldW;
        final boolean banner;
        final int color;

        Lines(ImDrawList dl, float x, float y, float w, float h, float rounding, float opacity, boolean banner, float originX, float originY, float worldW, boolean softEdges) {
            this.dl = dl;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.banner = banner;
            this.originX = originX;
            this.originY = originY;
            this.worldW = worldW;
            this.edge = softEdges ? Math.max(AnubisWidgets.s(8.0f), rounding) : 0.0f;
            float strength = banner ? 0.24f : 0.16f;
            this.color = AnubisWidgets.withAlpha(AnubisTheme.lerpColor(-1, GuiPalette.accent(), 0.25f), strength * opacity);
        }

        void segment(float ax, float ay, float bx, float by) {
            float mx = (ax + bx) * 0.5f;
            float my = (ay + by) * 0.5f;
            if (my < 0.0f || my > this.h || mx < 0.0f || mx > this.w) {
                return;
            }
            float fade = 1.0f;
            if (this.banner) {
                fade = 1.0f - my / this.h;
                fade *= fade;
            } else if (this.edge > 0.0f) {
                fade *= Math.min(1.0f, Math.min(my, this.h - my) / this.edge);
            }
            if (this.edge > 0.0f) {
                fade *= Math.min(1.0f, Math.min(mx, this.w - mx) / this.edge);
            }
            if (fade <= 0.02f) {
                return;
            }
            this.dl.addLine(this.x + ax, this.y + ay, this.x + bx, this.y + by, AnubisWidgets.c(AnubisWidgets.withAlpha(this.color, fade)), 1.0f);
        }
    }
}

