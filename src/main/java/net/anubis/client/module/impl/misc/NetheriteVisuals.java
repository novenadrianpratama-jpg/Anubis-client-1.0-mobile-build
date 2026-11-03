/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_2338
 *  net.minecraft.class_2374
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_3532
 *  net.minecraft.class_5819
 */
package net.anubis.client.module.impl.misc;

import imgui.ImDrawList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.anubis.client.gui.WorldLabel;
import net.anubis.client.module.impl.misc.AdvancedFinder;
import net.anubis.client.util.FoglessLines;
import net.anubis.client.util.TracerEngine;
import net.anubis.client.util.glyph.GlyphCanvas;
import net.anubis.client.util.glyph.GlyphShapes;
import net.anubis.client.util.glyph.GlyphSwarm;
import net.anubis.client.util.glyph.GlyphTint;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3532;
import net.minecraft.class_5819;

@Environment(value=EnvType.CLIENT)
final class NetheriteVisuals {
    static final String DOTS = "Dots";
    static final String CUBES = "Cubes";
    static final String PULSES = "Pulses";
    private static final long PULSE_NANOS = 1400000000L;
    private static final float WAVE_SECONDS = 0.9f;
    private static final int MAX_WAVES = 12;
    private static final int GLOW_LIMIT = 32;
    private static final int DEBRIS_TRACERS = 3;
    private static final int LABEL_LIMIT = 8;
    private static final double PROBE_RADIUS = 12.0;
    private static final int DOT_LIMIT = 1800;
    private static final int CUBE_LIMIT = 450;
    private static final double MISSING_RADIUS = 32.0;
    private static final int MISSING_LIMIT = 1600;
    private static final float PROBE_FRESH_TICKS = 14.0f;
    private static final float[] SHELL_INFLATE = new float[]{0.06f, 0.16f, 0.3f};
    private static final float[] SHELL_ALPHA = new float[]{0.16f, 0.08f, 0.04f};
    private final GlyphCanvas canvas = new GlyphCanvas();
    private final GlyphSwarm swarm = new GlyphSwarm(128);
    private final class_5819 random = class_5819.method_43047();
    private final List<Wave> waves = new ArrayList<Wave>();
    private final List<Label> labels = new ArrayList<Label>();
    private long lastFrame;
    private double pixel;

    NetheriteVisuals() {
    }

    void clear() {
        this.swarm.clear();
        this.waves.clear();
        this.labels.clear();
        this.lastFrame = 0L;
    }

    void burst(class_2338 pos, int rgb) {
        int i;
        double x = (double)pos.method_10263() + 0.5;
        double y = (double)pos.method_10264() + 0.5;
        double z = (double)pos.method_10260() + 0.5;
        if (this.waves.size() >= 12) {
            this.waves.removeFirst();
        }
        this.waves.add(new Wave(x, y, z, System.nanoTime()));
        int light = GlyphTint.lift(rgb, 0.55f);
        for (i = 0; i < 2; ++i) {
            this.swarm.spawn((byte)0, x + (double)this.jitter(0.3f), y + 0.6, z + (double)this.jitter(0.3f), this.jitter(0.2f), 1.1f, this.jitter(0.2f), 1.8f, 0.22f, this.jitter(0.3f), this.jitter(0.5f), this.random.method_43057(), light, 128);
        }
        for (i = 0; i < 10; ++i) {
            float angle = this.random.method_43057() * ((float)Math.PI * 2);
            float speed = 2.2f + this.random.method_43057() * 1.6f;
            int color = GlyphTint.mix(rgb, -1, this.random.method_43057() * 0.5f);
            this.swarm.spawn((byte)3, x, y, z, class_3532.method_15362((double)angle) * speed, 1.2f + this.random.method_43057() * 1.5f, class_3532.method_15374((double)angle) * speed, 0.8f, 0.09f, angle, 2.8f, this.random.method_43057(), color, 128);
        }
        class_310.method_1551().method_1483().method_4873((class_1113)class_1109.method_4757((class_3414)class_3417.field_26980, (float)1.3f, (float)0.9f));
    }

    void render(class_310 mc, float partialTick, class_243 eye, List<Section> sections, List<class_2338> revealed, AdvancedFinder finder, Options options) {
        long now = System.nanoTime();
        float dt = this.lastFrame == 0L ? 0.0f : Math.min(0.1f, (float)(now - this.lastFrame) / 1.0E9f);
        this.lastFrame = now;
        this.swarm.step(dt);
        this.canvas.begin(mc.field_1773.method_19418());
        this.pixel = FoglessLines.pixelScale(partialTick);
        int accent = GlyphTint.alpha(options.near(), 1.0f);
        int light = GlyphTint.lift(accent, 0.55f);
        float pulse = 0.5f + 0.5f * class_3532.method_15374((double)((float)(now % 1400000000L) / 1.4E9f * ((float)Math.PI * 2)));
        this.labels.clear();
        this.drawRevealed(eye, revealed, options, accent, light, pulse);
        this.drawWaves(now, accent, light);
        if (options.showProbes()) {
            this.drawProbes(eye, finder, options, accent, light, pulse);
        }
        this.drawSections(sections, options, light, pulse);
        this.drawTracers(sections, options);
        this.swarm.draw(this.canvas, 1.0f, true);
        this.canvas.submit(true);
    }

    void overlay(ImDrawList dl, float screenW, float screenH) {
        class_310 mc = class_310.method_1551();
        for (Label label : this.labels) {
            float[] screen = WorldLabel.project(mc, label.pos(), screenW, screenH);
            if (screen == null) continue;
            WorldLabel.drawText(dl, screen[0], screen[1], label.primary(), label.secondary(), 1.0f);
        }
    }

    boolean hasLabels() {
        return !this.labels.isEmpty();
    }

    private static int tint(Options options, double distance) {
        float t = (float)class_3532.method_15350((double)(distance / options.fade()), (double)0.0, (double)1.0);
        return GlyphTint.alpha(GlyphTint.mix(options.near(), options.far(), t), 1.0f);
    }

    private void drawSections(List<Section> sections, Options options, int light, float pulse) {
        for (Section section : sections) {
            if (!(section.progress() >= 0.0f)) continue;
            this.drawScanning(section, light, pulse);
        }
        for (Section section : sections) {
            if (!(section.progress() < 0.0f)) continue;
            this.drawCandidate(section, options);
        }
    }

    private void drawCandidate(Section section, Options options) {
        double far = class_3532.method_15350((double)(section.distance() / options.fade()), (double)0.0, (double)1.0);
        int base = NetheriteVisuals.tint(options, section.distance());
        this.box(section.box(), GlyphTint.alpha(base, (float)(0.95 - 0.3 * far)), (float)(2.4 - 0.8 * far));
        this.faces(section.box(), GlyphTint.alpha(base, (float)(0.07 - 0.03 * far)));
    }

    private void drawScanning(Section section, int light, float pulse) {
        class_238 box = section.box();
        this.box(box, GlyphTint.alpha(light, 0.6f + 0.4f * pulse), 3.2f);
        this.faces(box, GlyphTint.alpha(light, 0.05f));
        double y = box.field_1322 + (box.field_1325 - box.field_1322) * (double)section.progress();
        int plane = GlyphTint.alpha(light, 0.2f);
        this.canvas.quad(box.field_1323, y, box.field_1321, box.field_1320, y, box.field_1321, box.field_1320, y, box.field_1324, box.field_1323, y, box.field_1324, plane);
        this.canvas.quad(box.field_1323, y, box.field_1321, box.field_1323, y, box.field_1324, box.field_1320, y, box.field_1324, box.field_1320, y, box.field_1321, plane);
        int rim = GlyphTint.alpha(light, 0.95f);
        this.line(box.field_1323, y, box.field_1321, box.field_1320, y, box.field_1321, rim, 2.4f);
        this.line(box.field_1320, y, box.field_1321, box.field_1320, y, box.field_1324, rim, 2.4f);
        this.line(box.field_1320, y, box.field_1324, box.field_1323, y, box.field_1324, rim, 2.4f);
        this.line(box.field_1323, y, box.field_1324, box.field_1323, y, box.field_1321, rim, 2.4f);
    }

    private void drawTracers(List<Section> sections, Options options) {
        int drawn = 0;
        for (Section section : sections) {
            if (drawn >= options.tracerCount()) break;
            if (section.distance() < 1.0) continue;
            class_243 center = section.box().method_1005();
            TracerEngine.draw(center, GlyphTint.alpha(NetheriteVisuals.tint(options, section.distance()), 0.85f));
            String pct = section.progress() >= 0.0f ? Math.round(section.progress() * 100.0f) + "%" : null;
            this.labels.add(new Label(center, String.format(Locale.ROOT, "%.0fm", section.distance()), pct));
            ++drawn;
        }
    }

    private void drawProbes(class_243 eye, AdvancedFinder finder, Options options, int accent, int light, float pulse) {
        int missing = GlyphTint.mix(light, -1, 0.5f);
        finder.forEachMissing(eye, 32.0, 1600, options.missingFrom(), (x, y, z) -> this.canvas.sprite(GlyphShapes.DIAMOND, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, 0.11f + 0.05f * pulse, 0.0f, GlyphTint.alpha(missing, 0.5f + 0.45f * pulse)));
        int fresh = GlyphTint.mix(light, -1, 0.6f);
        String style = options.probeStyle();
        int limit = CUBES.equals(style) ? 450 : 1800;
        finder.forEachProbe(eye, 12.0, limit, options.missingFrom(), (x, y, z, age) -> {
            float t = Math.min(1.0f, (float)age / 14.0f);
            int color = GlyphTint.mix(fresh, accent, t);
            double cx = (double)x + 0.5;
            double cy = (double)y + 0.5;
            double cz = (double)z + 0.5;
            if (CUBES.equals(style)) {
                double half = 0.2 - 0.1 * (double)t;
                this.faces(new class_238(cx - half, cy - half, cz - half, cx + half, cy + half, cz + half), GlyphTint.alpha(color, 0.65f - 0.3f * t));
                return;
            }
            this.canvas.sprite(GlyphShapes.DIAMOND, cx, cy, cz, 0.13f - 0.06f * t, 0.0f, GlyphTint.alpha(color, 0.95f - 0.5f * t));
            if (PULSES.equals(style) && t < 1.0f) {
                this.canvas.sprite(GlyphShapes.HALO, cx, cy, cz, 0.15f + 0.55f * t, 0.0f, GlyphTint.alpha(fresh, 0.55f * (1.0f - t) * (1.0f - t)));
            }
        });
    }

    private void drawRevealed(class_243 eye, List<class_2338> revealed, Options options, int accent, int light, float pulse) {
        class_2338 pos2;
        double distance;
        ArrayList<class_2338> sorted = new ArrayList<class_2338>(revealed);
        sorted.sort(Comparator.comparingDouble(pos -> pos.method_19770((class_2374)eye)));
        for (int i = 0; i < sorted.size() && !((distance = Math.sqrt((pos2 = (class_2338)sorted.get(i)).method_19770((class_2374)eye))) > options.range()); ++i) {
            class_238 box = new class_238(pos2);
            if (i < 32) {
                for (int s = 0; s < SHELL_INFLATE.length; ++s) {
                    this.faces(box.method_1014((double)SHELL_INFLATE[s]), GlyphTint.alpha(accent, SHELL_ALPHA[s] * (0.7f + 0.3f * pulse)));
                }
            }
            this.faces(box, GlyphTint.alpha(accent, 0.3f));
            this.box(box, GlyphTint.alpha(light, 0.95f), 2.6f);
            class_243 center = box.method_1005();
            if (i < 3) {
                TracerEngine.draw(center, GlyphTint.alpha(accent, 0.8f));
            }
            if (i >= 8) continue;
            this.labels.add(new Label(center.method_1031(0.0, 0.9, 0.0), String.format(Locale.ROOT, "%.0fm", distance), null));
        }
    }

    private void drawWaves(long now, int accent, int light) {
        Iterator<Wave> iterator = this.waves.iterator();
        while (iterator.hasNext()) {
            Wave wave = iterator.next();
            float t = (float)(now - wave.born()) / 1.0E9f / 0.9f;
            if (t >= 1.0f) {
                iterator.remove();
                continue;
            }
            float ease = 1.0f - (1.0f - t) * (1.0f - t) * (1.0f - t);
            float radius = 0.4f + 2.6f * ease;
            float fade = (1.0f - t) * (1.0f - t);
            this.canvas.ring(wave.x(), wave.y(), wave.z(), radius * 0.86f, radius, 48, GlyphTint.alpha(light, fade * 0.9f));
            this.canvas.ring(wave.x(), wave.y(), wave.z(), radius, radius * 1.25f, 48, GlyphTint.alpha(accent, fade * 0.3f));
        }
    }

    private void line(double ax, double ay, double az, double bx, double by, double bz, int argb, float width) {
        this.canvas.ribbon(ax, ay, az, bx, by, bz, argb, width, this.pixel);
    }

    private void box(class_238 b, int argb, float width) {
        this.line(b.field_1323, b.field_1322, b.field_1321, b.field_1320, b.field_1322, b.field_1321, argb, width);
        this.line(b.field_1320, b.field_1322, b.field_1321, b.field_1320, b.field_1322, b.field_1324, argb, width);
        this.line(b.field_1320, b.field_1322, b.field_1324, b.field_1323, b.field_1322, b.field_1324, argb, width);
        this.line(b.field_1323, b.field_1322, b.field_1324, b.field_1323, b.field_1322, b.field_1321, argb, width);
        this.line(b.field_1323, b.field_1325, b.field_1321, b.field_1320, b.field_1325, b.field_1321, argb, width);
        this.line(b.field_1320, b.field_1325, b.field_1321, b.field_1320, b.field_1325, b.field_1324, argb, width);
        this.line(b.field_1320, b.field_1325, b.field_1324, b.field_1323, b.field_1325, b.field_1324, argb, width);
        this.line(b.field_1323, b.field_1325, b.field_1324, b.field_1323, b.field_1325, b.field_1321, argb, width);
        this.line(b.field_1323, b.field_1322, b.field_1321, b.field_1323, b.field_1325, b.field_1321, argb, width);
        this.line(b.field_1320, b.field_1322, b.field_1321, b.field_1320, b.field_1325, b.field_1321, argb, width);
        this.line(b.field_1320, b.field_1322, b.field_1324, b.field_1320, b.field_1325, b.field_1324, argb, width);
        this.line(b.field_1323, b.field_1322, b.field_1324, b.field_1323, b.field_1325, b.field_1324, argb, width);
    }

    private void faces(class_238 b, int argb) {
        this.face(b.field_1323, b.field_1322, b.field_1321, b.field_1320, b.field_1322, b.field_1321, b.field_1320, b.field_1322, b.field_1324, b.field_1323, b.field_1322, b.field_1324, argb);
        this.face(b.field_1323, b.field_1325, b.field_1321, b.field_1320, b.field_1325, b.field_1321, b.field_1320, b.field_1325, b.field_1324, b.field_1323, b.field_1325, b.field_1324, argb);
        this.face(b.field_1323, b.field_1322, b.field_1321, b.field_1320, b.field_1322, b.field_1321, b.field_1320, b.field_1325, b.field_1321, b.field_1323, b.field_1325, b.field_1321, argb);
        this.face(b.field_1323, b.field_1322, b.field_1324, b.field_1320, b.field_1322, b.field_1324, b.field_1320, b.field_1325, b.field_1324, b.field_1323, b.field_1325, b.field_1324, argb);
        this.face(b.field_1323, b.field_1322, b.field_1321, b.field_1323, b.field_1322, b.field_1324, b.field_1323, b.field_1325, b.field_1324, b.field_1323, b.field_1325, b.field_1321, argb);
        this.face(b.field_1320, b.field_1322, b.field_1321, b.field_1320, b.field_1322, b.field_1324, b.field_1320, b.field_1325, b.field_1324, b.field_1320, b.field_1325, b.field_1321, argb);
    }

    private void face(double ax, double ay, double az, double bx, double by, double bz, double cx, double cy, double cz, double dx, double dy, double dz, int argb) {
        this.canvas.quad(ax, ay, az, bx, by, bz, cx, cy, cz, dx, dy, dz, argb);
        this.canvas.quad(ax, ay, az, dx, dy, dz, cx, cy, cz, bx, by, bz, argb);
    }

    private float jitter(float amount) {
        return (this.random.method_43057() * 2.0f - 1.0f) * amount;
    }

    @Environment(value=EnvType.CLIENT)
    private record Wave(double x, double y, double z, long born) {
    }

    @Environment(value=EnvType.CLIENT)
    record Options(int near, int far, double fade, double range, String probeStyle, float missingFrom, int tracerCount, boolean showProbes) {
    }

    @Environment(value=EnvType.CLIENT)
    private record Label(class_243 pos, String primary, String secondary) {
    }

    @Environment(value=EnvType.CLIENT)
    record Section(class_238 box, double distance, float progress) {
    }
}

