/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 *  net.minecraft.class_9848
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.render;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import javax.imageio.ImageIO;
import net.anubis.client.module.impl.render.PeepArt;
import net.anubis.client.module.impl.render.PeepEffects;
import net.anubis.client.module.impl.render.PeepSong;
import net.anubis.client.module.impl.render.PeepWorld;
import net.anubis.client.module.impl.render.SkyImage;
import net.anubis.client.module.impl.render.SkyMips;
import net.anubis.client.module.impl.render.SkyPaint;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;
import net.minecraft.class_9848;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
final class PeepSky {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    static final int PINK = 16732080;
    static final int HOT = 16766704;
    static final int DEEP = 12720235;
    private static final int BLUR = 9;
    private static final int SEGMENTS = 64;
    private static final int ROWS = 14;
    private static final int TATTOO_PAD = 26;
    private static final int TATTOO_MAX = 320;
    private static final float COVER_SHARE = 0.7f;
    private static final float TATTOO_ART = 0.8f;
    static final float SPIN_PER_SECOND = 0.02f;
    private static final String TATTOOS = "/assets/anubis/textures/peep/";
    private static final String[] KINDS = new String[]{"star", "anarchy", "cake", "heart", "wave", "rose", "mom", "horseshoe", "hellboy", "rip"};
    static final int STAR = 0;
    static final int RIP = 9;
    private static final float[] ELEVATION = new float[]{41.0f, 18.0f, 34.0f, 46.0f, 22.0f, 30.0f, 38.0f, 49.0f, 23.0f, 36.0f};
    private static final float[] SIZE = new float[]{6.0f, 10.0f, 8.0f, 6.0f, 7.0f, 9.0f, 5.0f, 6.0f, 6.0f, 12.0f};
    private static final int STARS = 140;
    private static final int BOKEH = 12;
    private static final float CROSSFADE_SECONDS = 2.0f;
    private static final float STAGE_FOLLOW = 0.5f;
    private static final float FALL_PER_SECOND = 0.014285714f;
    private static final float FALL_LOW = 12.0f;
    private static final float FALL_HIGH = 64.0f;
    private static final float STAR_LOW = -1.5f;
    private static final float STAR_HIGH = 86.0f;
    private static final float LYRIC_EASE = 3.0f;
    private static final float PARTICLE_KEEP_OUT = 1.0f;
    private static final float TEXT_GAP = 3.0f;
    private static final float TEXT_GAP_BELOW = 1.0f;
    private static final float TEXT_FADE = 5.0f;
    private static final float TEXT_FADE_BELOW = 1.5f;
    private static final float COLUMN_HALF = 14.0f;
    private static final float AIM_NEAR = 6.0f;
    private static final float AIM_FAR = 15.0f;
    private static final float HORIZON_HOLD = 0.04f;
    private static final float HORIZON_DEEP = 0.36f;
    private static final float HORIZON_FADE = 0.6f;
    private static final float HORIZON_DEEP_SHARE = 0.78f;
    private static final float BEAT_RISE = 0.14f;
    private static final int GOLD = 16769658;
    private static final int GHOST = 14208255;
    private static final float LIT_GLOW = 0.6f;
    private volatile Built built;
    private volatile int generation;
    private boolean building;
    private SkyImage[] tattoos;
    private SkyImage[] rims;
    private SkyImage[] golds;
    private SkyImage[] veils;
    private int[] kinds;
    SkyImage[] art;
    private final float[] weight = new float[PeepSong.ALL.length];
    private final float[] shown = new float[PeepSong.ALL.length];
    private final float[] songAge = new float[PeepSong.ALL.length];
    private PeepSong song;
    float time;
    float alpha;
    float viewU;
    float aimU;
    float viewUp;
    float stageU = Float.NaN;
    float pulse;
    int horizonRgb;
    float horizonShare;
    private float fall;
    private float fallFade;
    private boolean fresh = true;
    static final int ANCHORS = 512;
    private static final int GHOST_SLOTS = 512 - KINDS.length;
    private static final float HAUNT_SPEED = 0.42f;
    private static final float HAUNT_PHASE = 3.5f;
    private static final float[] HAUNT_AZIMUTH = new float[]{-40.0f, -40.0f, -56.0f, -56.0f, -33.0f, -33.0f, 33.0f, 33.0f, 40.0f, 40.0f, 56.0f};
    private static final boolean[] HAUNT_HIGH = new boolean[]{false, false, true, true, true, true, true, true, false, false, true};
    private final float[] anchorU = new float[512];
    private final int[] anchorRun = new int[512];
    private final byte[] pick = new byte[512];
    private final int[] pickRun = new int[512];
    private boolean lyricsShown;
    private float lyricU;
    private float titleU;
    private float lyricUp;
    private float lineHalfWide;
    private float lineHalfTall;
    private float titleUp;
    private float titleHalfWide;
    private float titleHalfTall;
    private float wantLineWide;
    private float wantTitleWide;
    private final Look look;
    private static final int MAX_SHOWN = KINDS.length * 2;
    final float[] tu;
    final float[] ty;
    final float[] tw;
    final float[] th;
    final float[] tGlow;
    final float[] tLit;
    private final float[] tSpin;
    private final float[] tAlpha;
    private final float[] tGhost;
    private final int[] tTex;
    int shownTattoos;
    private static final Palette BASE = new Palette(16732080, 12720235, 1836056, 525072, 3934768, 16766704, 16732080, 0.42f, 1.0f, 1.0f);
    private static final Palette[] PALETTES = new Palette[PeepSong.ALL.length];

    PeepSky() {
        Arrays.fill(this.anchorRun, Integer.MIN_VALUE);
        Arrays.fill(this.pickRun, Integer.MIN_VALUE);
        this.lyricUp = 28.0f;
        this.look = new Look();
        this.tu = new float[MAX_SHOWN];
        this.ty = new float[MAX_SHOWN];
        this.tw = new float[MAX_SHOWN];
        this.th = new float[MAX_SHOWN];
        this.tGlow = new float[MAX_SHOWN];
        this.tLit = new float[MAX_SHOWN];
        this.tSpin = new float[MAX_SHOWN];
        this.tAlpha = new float[MAX_SHOWN];
        this.tGhost = new float[MAX_SHOWN];
        this.tTex = new int[MAX_SHOWN];
    }

    private static Palette palette(PeepSong song) {
        return switch (song) {
            default -> throw new MatchException(null, null);
            case PeepSong.STAR_SHOPPING -> new Palette(16732080, 12720235, 1574434, 394260, 3017786, 16766704, 16740296, 0.4f, 1.0f, 1.2f);
            case PeepSong.CRYBABY -> new Palette(15751854, 11016294, 1574426, 459791, 3412026, 16766704, 15755960, 0.38f, 0.92f, 0.9f);
            case PeepSong.CRY_ALONE -> new Palette(12073606, 7999568, 0x110310, 262664, 0x220820, 15760056, 12720235, 0.26f, 0.68f, 0.4f);
            case PeepSong.HELLBOY -> new Palette(16727406, 11539004, 1967116, 655878, 4720670, 16752312, 0xFF3355, 0.52f, 1.0f, 0.85f);
            case PeepSong.BEAMER_BOY -> new Palette(14241744, 8002188, 1181220, 328468, 2363456, 15780095, 11554303, 0.34f, 0.9f, 0.9f);
            case PeepSong.SPOTLIGHT -> new Palette(12728968, 7999568, 1442831, 393739, 2492966, 15909598, 12720235, 0.32f, 0.85f, 0.8f);
            case PeepSong.THE_BRIGHTSIDE -> new Palette(16740277, 12728960, 4198974, 2886188, 15764620, 16769224, 16756848, 0.48f, 1.0f, 0.45f);
            case PeepSong.SUNLIGHT_ON_YOUR_SKIN -> new Palette(16751540, 13654656, 5247556, 3017264, 16746144, 16770760, 16760944, 0.4f, 1.0f, 0.6f);
            case PeepSong.LIFE_IS_BEAUTIFUL -> new Palette(16740250, 13117530, 2492440, 1049610, 5904440, 16765660, 16742550, 0.46f, 1.0f, 0.9f);
            case PeepSong.FALLING_DOWN -> new Palette(14711487, 9186424, 1509924, 525846, 2889272, 16046320, 15043792, 0.32f, 0.9f, 1.0f);
            case PeepSong.SAVE_THAT_SHIT -> new Palette(16732080, 13639802, 2229532, 656144, 4721718, 16766704, 16732080, 0.48f, 1.0f, 1.0f);
            case PeepSong.AWFUL_THINGS -> new Palette(9050714, 4852272, 786952, 196868, 2754592, 14701982, 12720235, 0.3f, 0.62f, 0.45f);
            case PeepSong.WITCHBLADES -> new Palette(13127167, 8002216, 1705520, 656408, 4593238, 15255807, 14835936, 0.44f, 0.95f, 0.85f);
            case PeepSong.FOUR_GOLD_CHAINS -> new Palette(0xFF5FAA, 12720235, 2033430, 656392, 4854832, 16770736, 16732080, 0.5f, 1.0f, 1.0f);
            case PeepSong.GHOST_BOY -> new Palette(12352724, 7617154, 2362428, 1378338, 5121634, 14735600, 13671666, 0.42f, 0.85f, 0.7f);
            case PeepSong.GHOST_GIRL -> new Palette(15774948, 12093128, 4991604, 3415640, 14198482, 0xFFF0FA, 16769268, 0.46f, 1.0f, 0.6f);
        };
    }

    void ready(SkyPaint paint, ExecutorService raster) {
        if (this.tattoos != null) {
            return;
        }
        Built made = this.built;
        if (made != null && made.generation() != this.generation) {
            this.built = null;
            made = null;
        }
        if (made == null) {
            if (!this.building) {
                this.building = true;
                int ticket = this.generation;
                raster.submit(() -> {
                    if (ticket != this.generation) {
                        return;
                    }
                    try {
                        Built done = PeepSky.build(ticket);
                        if (ticket == this.generation) {
                            this.built = done;
                        }
                    }
                    catch (Error | RuntimeException e) {
                        LOGGER.warn("[Anubis] Sky easter egg textures couldn't be drawn", e);
                    }
                });
            }
            return;
        }
        SkyImage[] out = new SkyImage[made.tattoos().length];
        SkyImage[] edges = new SkyImage[out.length];
        SkyImage[] gilt = new SkyImage[out.length];
        SkyImage[] ghosts = new SkyImage[out.length];
        SkyImage[] fx = new SkyImage[made.art().length];
        try {
            int i;
            for (i = 0; i < out.length; ++i) {
                out[i] = paint.upload("peep_tattoo_" + i, made.widths()[i], made.heights()[i], made.tattoos()[i]);
                edges[i] = paint.upload("peep_rim_" + i, made.widths()[i], made.heights()[i], made.rims()[i]);
                gilt[i] = paint.upload("peep_gold_" + i, made.widths()[i], made.heights()[i], made.golds()[i]);
                ghosts[i] = paint.upload("peep_veil_" + i, made.widths()[i], made.heights()[i], made.veils()[i]);
            }
            for (i = 0; i < fx.length; ++i) {
                PeepArt.Art piece = made.art()[i];
                fx[i] = paint.upload("peep_fx_" + i, piece.width(), piece.height(), made.artLevels()[i]);
            }
        }
        catch (Error | RuntimeException e) {
            PeepSky.closeAll(out);
            PeepSky.closeAll(edges);
            PeepSky.closeAll(gilt);
            PeepSky.closeAll(ghosts);
            PeepSky.closeAll(fx);
            throw e;
        }
        this.kinds = made.kinds();
        this.rims = edges;
        this.golds = gilt;
        this.veils = ghosts;
        this.art = fx;
        this.tattoos = out;
        this.built = null;
        this.building = false;
    }

    boolean holding() {
        return this.tattoos != null || this.building || this.built != null;
    }

    private static void closeAll(SkyImage[] textures) {
        if (textures == null) {
            return;
        }
        for (SkyImage tex : textures) {
            if (tex == null) continue;
            tex.close();
        }
    }

    void close() {
        PeepSky.closeAll(this.tattoos);
        PeepSky.closeAll(this.rims);
        PeepSky.closeAll(this.golds);
        PeepSky.closeAll(this.veils);
        PeepSky.closeAll(this.art);
        this.tattoos = null;
        this.rims = null;
        this.golds = null;
        this.veils = null;
        this.art = null;
        ++this.generation;
        this.built = null;
        this.building = false;
        this.restart();
    }

    void restart() {
        Arrays.fill(this.weight, 0.0f);
        Arrays.fill(this.shown, 0.0f);
        Arrays.fill(this.songAge, 0.0f);
        this.song = null;
        this.stageU = Float.NaN;
        this.fall = 0.0f;
        this.fallFade = 0.0f;
        this.fresh = true;
        Arrays.fill(this.anchorRun, Integer.MIN_VALUE);
        Arrays.fill(this.pickRun, Integer.MIN_VALUE);
    }

    float anchor(int slot, int run) {
        if (this.anchorRun[slot] != run) {
            this.anchorRun[slot] = run;
            this.anchorU[slot] = Float.isNaN(this.stageU) ? this.viewU : this.stageU;
        }
        return this.anchorU[slot];
    }

    float anchored(int slot, int run) {
        return this.anchorRun[slot] == run ? this.anchorU[slot] : Float.NaN;
    }

    int picked(int slot, int run) {
        return this.pickRun[slot] == run ? this.pick[slot] : -1;
    }

    void pick(int slot, int run, int choice) {
        this.pickRun[slot] = run;
        this.pick[slot] = (byte)choice;
    }

    void lyrics(float lineU, float lineUp, float lineHalfWide, float lineHalfTall, float titleU, float titleUp, float titleHalfWide, float titleHalfTall) {
        this.lyricsShown = !Float.isNaN(lineUp);
        this.lyricU = PeepSky.wrap(lineU);
        this.titleU = PeepSky.wrap(titleU);
        this.lyricUp = this.lyricsShown ? lineUp : 28.0f;
        this.lineHalfTall = lineHalfTall;
        this.titleUp = titleUp;
        this.titleHalfTall = titleHalfTall;
        this.wantLineWide = this.lyricsShown ? lineHalfWide : 0.0f;
        this.wantTitleWide = this.lyricsShown ? titleHalfWide : 0.0f;
    }

    void update(float dt, PeepSong next, float level, float viewU, float aimU, float viewUp, float pulse) {
        int i;
        this.song = next;
        boolean hidden = level <= 0.0f || this.fresh;
        this.fresh = false;
        float step = dt / 2.0f;
        float sum = 0.0f;
        for (i = 0; i < this.weight.length; ++i) {
            float target;
            boolean playing = this.song != null && this.song.ordinal() == i;
            float f = target = playing ? 1.0f : 0.0f;
            this.weight[i] = hidden ? target : (this.weight[i] < target ? Math.min(target, this.weight[i] + step) : Math.max(target, this.weight[i] - step));
            this.shown[i] = PeepSky.smootherstep(this.weight[i]);
            sum += this.shown[i];
            this.songAge[i] = playing ? Math.min(this.songAge[i] + dt, 100000.0f) : (this.weight[i] <= 0.0f ? 0.0f : this.songAge[i]);
        }
        if (sum > 1.0f) {
            i = 0;
            while (i < this.shown.length) {
                int n = i++;
                this.shown[n] = this.shown[n] / sum;
            }
        }
        this.viewU = PeepSky.wrap(viewU);
        this.aimU = PeepSky.wrap(aimU);
        this.viewUp = viewUp;
        if (!this.lyricsShown) {
            this.lyricU = this.viewU;
            this.titleU = this.viewU;
        }
        float ease = hidden ? 1.0f : Math.min(1.0f, dt * 3.0f);
        this.lineHalfWide += (this.wantLineWide - this.lineHalfWide) * ease;
        this.titleHalfWide += (this.wantTitleWide - this.titleHalfWide) * ease;
        this.pulse = pulse;
        this.stageU = Float.isNaN(this.stageU) || hidden ? this.viewU : PeepSky.wrap(this.stageU + PeepSky.wrapDelta(this.viewU - this.stageU) * Math.min(1.0f, dt * 0.5f));
        float falling = this.w(PeepSong.FALLING_DOWN);
        if (falling > 0.001f) {
            this.fall = class_3532.method_22450((float)(this.fall + dt * 0.014285714f * falling));
        }
        float f = this.fallFade = this.song == PeepSong.FALLING_DOWN ? Math.min(1.0f, this.fallFade + step) : Math.max(0.0f, this.fallFade - step);
        if (this.fallFade <= 0.0f) {
            this.fall = 0.0f;
        }
    }

    float w(PeepSong song) {
        return this.shown[song.ordinal()];
    }

    float age(PeepSong song) {
        return this.songAge[song.ordinal()];
    }

    void draw(SkyPaint paint, float level, float time) {
        float at;
        float home;
        float fade;
        float y;
        float u;
        int k;
        boolean fx;
        this.time = time;
        float cover = PeepSky.smootherstep(class_3532.method_15363((float)(level / 0.7f), (float)0.0f, (float)1.0f));
        float marks = PeepSky.smootherstep(class_3532.method_15363((float)((level - 0.7f) / 0.3f), (float)0.0f, (float)1.0f));
        if (cover <= 0.0f) {
            this.horizonShare = 0.0f;
            PeepWorld.horizon(this.horizonRgb, 0.0f);
            return;
        }
        this.alpha = marks;
        this.blendLook();
        float zenith = -0.78f;
        float ground = 1.27f;
        float edge = class_3532.method_16439((float)cover, (float)ground, (float)(zenith - 0.05f));
        this.horizon(edge, cover, time);
        for (int row = 0; row < 14; ++row) {
            float y0 = class_3532.method_16439((float)((float)row / 14.0f), (float)edge, (float)ground);
            float y1 = class_3532.method_16439((float)((float)(row + 1) / 14.0f), (float)edge, (float)ground);
            int c0 = this.shade(y0 - edge, y0, 0.72f);
            int c1 = this.shade(y1 - edge, y1, 0.72f);
            for (int i = 0; i < 64; ++i) {
                float u0 = (float)i / 64.0f;
                float u1 = (float)(i + 1) / 64.0f;
                paint.gradient(false, u0, u1, y0, y1, c0, c1);
            }
        }
        float rim = 0.09f;
        int lit = PeepSky.tint(0.85f, this.look.topColor);
        int clear = PeepSky.tint(0.0f, this.look.topColor);
        for (int i = 0; i < 64; ++i) {
            float u0 = (float)i / 64.0f;
            float u1 = (float)(i + 1) / 64.0f;
            paint.gradient(true, u0, u1, edge - rim, edge, clear, lit);
            paint.gradient(true, u0, u1, edge, edge + rim * 2.2f, PeepSky.tint(0.55f, this.look.rimColor), clear);
        }
        boolean bl = fx = this.art != null && marks > 0.0f;
        if (fx) {
            PeepEffects.skyLight(this, paint, cover, time);
        }
        this.horizonGlow(paint, edge, cover, time);
        if (this.pulse > 0.0f && this.lyricsShown) {
            paint.glow(this.lyricU, PeepSky.elevationToY(this.lyricUp + 1.0f), 0.4f, 16732080, 0.18f * this.pulse * marks);
        }
        float drift = this.fall;
        float driftFade = PeepSky.smootherstep(this.fallFade);
        float lonely = 1.0f - 0.5f * this.w(PeepSong.CRY_ALONE);
        for (k = 0; k < 12; ++k) {
            u = class_3532.method_22450((float)((float)k * 0.2718f + time * (0.004f + 0.002f * (float)(k % 3)) * (k % 2 == 0 ? 1.0f : -1.0f)));
            y = class_3532.method_16439((float)class_3532.method_22450((float)((float)k * 0.5713f + 0.21f)), (float)(zenith + 0.45f), (float)0.67f);
            fade = 1.0f;
            if (driftFade > 0.0f) {
                home = (0.72f - y) * 60.0f;
                at = class_3532.method_22450((float)((home - -1.5f) / 87.5f - drift));
                y = PeepSky.elevationToY(class_3532.method_16439((float)driftFade, (float)home, (float)(-1.5f + at * 87.5f)));
                fade = class_3532.method_16439((float)driftFade, (float)1.0f, (float)(PeepSky.smooth(0.0f, 0.08f, at) * (1.0f - PeepSky.smooth(0.7f, 0.85f, at))));
            }
            if (y < edge + 0.05f) continue;
            float breathe = 0.6f + 0.4f * class_3532.method_15374((double)(time * (0.35f + 0.07f * (float)k) + (float)k * 1.3f));
            float radius = 0.07f + 0.05f * (float)(k % 4);
            paint.glow(u, y, radius, k % 3 == 0 ? this.look.deepColor : this.look.topColor, 0.16f * breathe * cover * this.look.light * (fade *= PeepSky.smooth(0.02f, 0.02f + radius, 0.72f - y)) * lonely * (1.0f + 0.8f * this.pulse));
        }
        for (k = 0; k < 140; ++k) {
            u = class_3532.method_22450((float)((float)k * 0.618034f));
            y = class_3532.method_16439((float)class_3532.method_22450((float)((float)k * 0.41421f + 0.13f)), (float)(zenith + 0.08f), (float)0.74f);
            fade = 1.0f;
            if (driftFade > 0.0f) {
                home = (0.72f - y) * 60.0f;
                at = class_3532.method_22450((float)((home - -1.5f) / 87.5f - drift));
                y = PeepSky.elevationToY(class_3532.method_16439((float)driftFade, (float)home, (float)(-1.5f + at * 87.5f)));
                fade = class_3532.method_16439((float)driftFade, (float)1.0f, (float)(PeepSky.smooth(0.0f, 0.03f, at) * (1.0f - PeepSky.smooth(0.9f, 0.99f, at))));
            }
            if (y < edge + 0.01f) continue;
            float twinkle = 0.35f + 0.65f * Math.max(0.0f, class_3532.method_15374((double)(time * (0.9f + (float)(k % 7) * 0.31f) + (float)k * 2.17f)));
            paint.glow(u, y, 0.004f + 0.0035f * (float)(k % 4), k % 5 == 0 ? 16766704 : 0xFFF2FA, Math.min(1.0f, 0.8f * twinkle * this.look.starLight) * cover * fade);
        }
        boolean inked = marks > 0.0f && this.tattoos != null;
        this.shownTattoos = 0;
        if (inked) {
            this.layoutTattoos(PeepSky.smooth(0.35f, 1.0f, marks) * marks, time);
        }
        if (fx) {
            PeepEffects.behind(this, paint, time);
        }
        if (inked) {
            this.drawTattoos(paint, time);
        }
        if (fx) {
            PeepEffects.front(this, paint, time);
        }
    }

    private void horizon(float edge, float cover, float time) {
        float horizon = 0.72f;
        int sky = this.shade(horizon - edge, horizon, horizon);
        float glow = 0.0f;
        if (Math.max(edge, horizon - 0.42f) < horizon) {
            float breathe = 0.85f + 0.15f * class_3532.method_15374((double)(time * 0.6f));
            glow = class_3532.method_15363((float)(this.look.glowStrength * cover * breathe * (1.0f + 0.45f * this.pulse)), (float)0.0f, (float)1.0f);
        }
        int add = this.look.glowColor;
        int r = Math.min(255, Math.round((float)(sky >> 16 & 0xFF) + (float)(add >> 16 & 0xFF) * glow));
        int g = Math.min(255, Math.round((float)(sky >> 8 & 0xFF) + (float)(add >> 8 & 0xFF) * glow));
        int b = Math.min(255, Math.round((float)(sky & 0xFF) + (float)(add & 0xFF) * glow));
        this.horizonRgb = r << 16 | g << 8 | b;
        this.horizonShare = 0.97f * class_3532.method_15363((float)((horizon - edge) / 0.1f), (float)0.0f, (float)1.0f);
        PeepWorld.horizon(this.horizonRgb, this.horizonShare);
    }

    private void horizonGlow(SkyPaint paint, float edge, float cover, float time) {
        float top = Math.max(edge, 0.30000004f);
        float horizon = 0.72f;
        float hold = 0.76000005f;
        float deep = 1.08f;
        float below = 1.32f;
        if (top >= horizon) {
            return;
        }
        float breathe = 0.85f + 0.15f * class_3532.method_15374((double)(time * 0.6f));
        float strength = this.look.glowStrength * cover * breathe * (1.0f + 0.45f * this.pulse);
        int glow = PeepSky.tint(strength, this.look.glowColor);
        int dim = PeepSky.tint(strength * 0.78f, this.look.glowColor);
        int none = PeepSky.tint(0.0f, this.look.glowColor);
        for (int i = 0; i < 64; ++i) {
            float u0 = (float)i / 64.0f;
            float u1 = (float)(i + 1) / 64.0f;
            paint.gradient(true, u0, u1, top, horizon, none, glow);
            paint.gradient(true, u0, u1, horizon, hold, glow, glow);
            paint.gradient(true, u0, u1, hold, deep, glow, dim);
            paint.gradient(true, u0, u1, deep, below, dim, none);
        }
    }

    float lyricBox(float u, float up, float halfWide, float halfTall) {
        if (!this.lyricsShown) {
            return 0.0f;
        }
        float squeeze = 360.0f * class_3532.method_15362((double)(up * ((float)Math.PI / 180)));
        float line = this.lineHalfWide <= 0.05f ? 0.0f : PeepSky.smooth(0.0f, 3.0f, this.lineHalfWide) * PeepSky.box(Math.abs(PeepSky.wrapDelta(u - this.lyricU)) * squeeze, up - this.lyricUp, this.lineHalfWide + halfWide + 2.5f, this.lineHalfTall + halfTall + 2.0f);
        float title = this.titleHalfWide <= 0.05f ? 0.0f : PeepSky.smooth(0.0f, 3.0f, this.titleHalfWide) * PeepSky.box(Math.abs(PeepSky.wrapDelta(u - this.titleU)) * squeeze, up - this.titleUp, this.titleHalfWide + halfWide + 2.5f, this.titleHalfTall + halfTall + 2.0f);
        return Math.max(line, title);
    }

    float clear(float u, float up) {
        float keep = 1.0f - 1.0f * this.lyricBox(u, up, 0.8f, 0.8f);
        float e1 = up * ((float)Math.PI / 180);
        float e2 = this.viewUp * ((float)Math.PI / 180);
        float cos = class_3532.method_15374((double)e1) * class_3532.method_15374((double)e2) + class_3532.method_15362((double)e1) * class_3532.method_15362((double)e2) * class_3532.method_15362((double)((u - this.aimU) * ((float)Math.PI * 2)));
        if (cos < 0.93f) {
            return keep;
        }
        float degrees = (float)Math.acos(Math.min(1.0f, cos)) * 57.295776f;
        return keep * PeepSky.smooth(9.0f, 20.0f, degrees);
    }

    private float aimClear(float u, float up, float halfWide, float halfTall) {
        float across = Math.abs(PeepSky.wrapDelta(u - this.aimU)) * 360.0f * class_3532.method_15362((double)((up + this.viewUp) * 0.5f * ((float)Math.PI / 180)));
        float gapX = Math.max(0.0f, across - halfWide);
        float gapY = Math.max(0.0f, Math.abs(up - this.viewUp) - halfTall);
        return PeepSky.smooth(6.0f, 15.0f, (float)Math.sqrt(gapX * gapX + gapY * gapY));
    }

    private float keepOut(float u, float up, float halfTall, float halfWide) {
        return (1.0f - this.textRoom(u, up, halfWide, halfTall)) * this.aimClear(u, up, halfWide, halfTall);
    }

    private float textRoom(float u, float up, float halfWide, float halfTall) {
        float there;
        float across;
        if (!this.lyricsShown) {
            return 0.0f;
        }
        float squeeze = 360.0f * class_3532.method_15362((double)(up * ((float)Math.PI / 180)));
        float room = 0.0f;
        if (this.lineHalfWide > 0.05f) {
            across = Math.abs(PeepSky.wrapDelta(u - this.lyricU)) * squeeze;
            there = PeepSky.smooth(0.0f, 3.0f, this.lineHalfWide);
            room = there * PeepSky.near(across, this.lineHalfWide + halfWide) * PeepSky.nearUp(up - this.lyricUp, this.lineHalfTall + halfTall);
            if (up > this.lyricUp) {
                room = Math.max(room, there * PeepSky.near(across, 14.0f + halfWide));
            }
        }
        if (this.titleHalfWide > 0.05f) {
            across = Math.abs(PeepSky.wrapDelta(u - this.titleU)) * squeeze;
            there = PeepSky.smooth(0.0f, 3.0f, this.titleHalfWide);
            float above = up > this.titleUp ? 1.0f : PeepSky.nearUp(up - this.titleUp, this.titleHalfTall + halfTall);
            room = Math.max(room, there * PeepSky.near(across, this.titleHalfWide + halfWide) * above);
        }
        return room;
    }

    private static float near(float d, float reach) {
        return 1.0f - PeepSky.smooth(reach + 3.0f, reach + 3.0f + 5.0f, Math.abs(d));
    }

    private static float nearUp(float dy, float reach) {
        if (dy >= 0.0f) {
            return PeepSky.near(dy, reach);
        }
        return 1.0f - PeepSky.smooth(reach + 1.0f, reach + 1.0f + 1.5f, -dy);
    }

    private static float box(float x, float y, float halfX, float halfY) {
        return (1.0f - PeepSky.smooth(halfX * 0.85f, halfX, Math.abs(x))) * (1.0f - PeepSky.smooth(halfY * 0.8f, halfY, Math.abs(y)));
    }

    private static float artShare(int pixels) {
        return 1.0f - 52.0f / (float)pixels;
    }

    int kindAt(int k) {
        return this.kinds[this.tTex[k]];
    }

    private void layoutTattoos(float alpha, float time) {
        int n = this.tattoos.length;
        float alone = this.w(PeepSong.CRY_ALONE);
        float ghost = this.w(PeepSong.GHOST_BOY);
        float shimmer = this.w(PeepSong.GHOST_GIRL);
        float spot = this.w(PeepSong.SPOTLIGHT);
        float light = class_3532.method_16439((float)0.5f, (float)1.0f, (float)this.look.light);
        float beat = 1.0f + 0.1f * this.pulse;
        float drift = PeepSky.smootherstep(this.fallFade);
        int count = 0;
        for (int i = 0; i < n; ++i) {
            SkyImage tex = this.tattoos[i];
            int slot = this.kinds[i] % ELEVATION.length;
            float ring = alpha * (1.0f - alone) * light;
            if (ring <= 0.003f) continue;
            float breath = 0.85f + 0.15f * class_3532.method_15374((double)(time * 2.8f + (float)i));
            float aspect = (float)tex.width() / (float)tex.height();
            float u = class_3532.method_22450((float)((float)i / (float)n + time * 0.02f));
            float up = ELEVATION[slot] + 2.2f * class_3532.method_15374((double)(time * 0.8f + (float)i * 1.7f));
            float fade = 1.0f;
            if (drift > 0.0f) {
                float at = class_3532.method_22450((float)((up - 12.0f) / 52.0f - this.fall));
                up = class_3532.method_16439((float)drift, (float)up, (float)(12.0f + at * 52.0f));
                fade = class_3532.method_16439((float)drift, (float)1.0f, (float)(PeepSky.smooth(0.0f, 0.12f, at) * (1.0f - PeepSky.smooth(0.88f, 0.98f, at))));
            }
            if (ghost > 0.0f) {
                float turn = (time * 0.42f + (float)i * 3.5f) / ((float)Math.PI * 2) + 0.25f;
                int run = class_3532.method_15375((float)turn);
                int place = i % HAUNT_AZIMUTH.length;
                float hauntUp = (HAUNT_HIGH[place] ? 41.0f : 12.5f) + 6.0f * PeepSky.hash01(i, run, 1);
                float hauntU = this.anchor(GHOST_SLOTS + i, run) + (HAUNT_AZIMUTH[place] + 8.0f * (PeepSky.hash01(i, run, 2) - 0.5f)) / (360.0f * class_3532.method_15362((double)(hauntUp * ((float)Math.PI / 180))));
                u = PeepSky.wrap(u + PeepSky.wrapDelta(hauntU - u) * ghost);
                up = class_3532.method_16439((float)ghost, (float)up, (float)hauntUp);
            }
            float tall = SIZE[slot] * (0.94f + 0.06f * class_3532.method_15374((double)(time * 2.0f + (float)i))) * (1.0f + 0.15f * ghost);
            float keep = this.keepOut(u, up, tall * 0.5f * PeepSky.artShare(tex.height()), tall * aspect * 0.5f * PeepSky.artShare(tex.width()));
            count = this.place(count, i, u, up, tall / 60.0f, aspect, 0.1f * class_3532.method_15374((double)(time * 0.7f + (float)i * 2.1f)), ring * (fade *= keep * PeepSky.smooth(0.15f, 0.45f, keep)) * breath, time, shimmer, ghost, spot, beat);
        }
        this.shownTattoos = count;
    }

    float clearOfTattoos(float u, float up, float half) {
        float keep = 1.0f;
        float squeeze = 360.0f * class_3532.method_15362((double)(up * ((float)Math.PI / 180)));
        for (int k = 0; k < this.shownTattoos && keep > 0.0f; ++k) {
            if (this.tGlow[k] <= 0.05f) continue;
            float tUp = (0.72f - this.ty[k]) * 1.0471976f * 57.295776f;
            float across = Math.abs(PeepSky.wrapDelta(u - this.tu[k])) * squeeze;
            float halfW = this.tw[k] * 30.0f * 0.8f;
            float halfT = this.th[k] * 30.0f * 0.8f;
            float gapX = Math.max(0.0f, across - halfW - half);
            float gapY = Math.max(0.0f, Math.abs(up - tUp) - halfT - half);
            if (gapX > 1.5f || gapY > 1.5f) continue;
            keep *= 1.0f - Math.min(1.0f, this.tGlow[k]) * (1.0f - PeepSky.smooth(0.0f, 1.5f, (float)Math.sqrt(gapX * gapX + gapY * gapY)));
        }
        return keep;
    }

    private void drawTattoos(SkyPaint paint, float time) {
        int color;
        int k;
        int count = this.shownTattoos;
        float ghost = this.w(PeepSong.GHOST_BOY);
        float shimmer = this.w(PeepSong.GHOST_GIRL);
        float spot = this.w(PeepSong.SPOTLIGHT);
        float gold = this.w(PeepSong.FOUR_GOLD_CHAINS);
        float witch = this.w(PeepSong.WITCHBLADES);
        float beatGlow = 1.0f + 1.3f * this.pulse;
        int glowColor = PeepSky.mix(PeepSky.mix(PeepSky.mix(16732080, 16757575, gold * 0.4f), 14208255, ghost * 0.7f), 16763118, shimmer * 0.3f);
        for (int k2 = 0; k2 < count; ++k2) {
            paint.glow(this.tu[k2], this.ty[k2], Math.max(this.th[k2], this.tw[k2]) * 0.6f, glowColor, 0.22f * this.tGlow[k2] * beatGlow * (1.0f + 0.6f * this.tLit[k2]) * (1.0f + 0.5f * ghost * this.tGhost[k2]));
        }
        int faint = PeepSky.mix(PeepSky.mix(16732080, 16740552, shimmer), 13150463, ghost);
        float brief = Math.min(1.0f, shimmer + ghost);
        float neon = 1.0f - 0.7f * ghost;
        float pale = PeepSky.smooth(0.7f, 1.0f, this.look.light);
        for (k = 0; k < count; ++k) {
            int kind = this.kindAt(k);
            boolean star = kind == 0;
            float filled = this.filled(k, gold, ghost);
            float a = this.tAlpha[k] * (star ? 0.3f : neon * (1.0f - PeepSky.bodyBack(kind) * filled)) * (1.0f - brief * (1.0f - PeepSky.smooth(0.18f, 0.45f, this.tAlpha[k])));
            float white = PeepSky.smooth(0.2f, 0.8f, this.tAlpha[k]) * pale * 0.7f * (1.0f - 0.5f * gold) * (1.0f - filled) * (1.0f - 0.7f * this.tLit[k]) * (1.0f - 0.5f * shimmer);
            int n = color = star ? PeepSky.mix(16732080, 16740296, white) : PeepSky.mix(PeepSky.mix(faint, 16732080, filled), 0xFFFFFF, white);
            if (ghost > 0.0f) {
                color = PeepSky.mix(color, PeepSky.mix(10121471, 14203135, this.tGhost[k]), ghost);
                a *= 1.0f - ghost * (1.0f - this.haunt(k));
            }
            if (gold > 0.0f) {
                a *= 1.0f - 0.35f * gold;
                color = PeepSky.mix(color, 12720235, 0.4f * gold);
            }
            paint.sprite(this.tattoos[this.tTex[k]], this.tu[k], this.ty[k], this.tw[k], this.th[k], this.tSpin[k], PeepSky.tint(a, color), true);
        }
        if (ghost > 0.003f) {
            for (k = 0; k < count; ++k) {
                float a = 0.72f * this.tAlpha[k] * ghost * PeepSky.smooth(0.1f, 0.35f, this.tAlpha[k]) * (1.0f - 0.08f * PeepSky.smooth(0.8f, 1.0f, this.tGhost[k])) * this.haunt(k);
                if (a <= 0.003f) continue;
                int color2 = PeepSky.mix(11697407, 13145855, this.tGhost[k]);
                paint.sprite(this.veils[this.tTex[k]], this.tu[k], this.ty[k], this.tw[k], this.th[k], this.tSpin[k], PeepSky.tint(a, color2), true);
            }
        }
        if (gold > 0.003f) {
            for (k = 0; k < count; ++k) {
                float g = gold * PeepSky.smooth(0.12f, 0.6f, this.tGlow[k]);
                if (g <= 0.003f) continue;
                paint.sprite(this.golds[this.tTex[k]], this.tu[k], this.ty[k], this.tw[k], this.th[k], this.tSpin[k], PeepSky.tint(0.95f * g, 4859392), false);
                paint.sprite(this.golds[this.tTex[k]], this.tu[k], this.ty[k], this.tw[k], this.th[k], this.tSpin[k], PeepSky.tint(0.9f * g, 16769658), true);
            }
        }
        for (k = 0; k < count; ++k) {
            float filled = this.filled(k, gold, ghost);
            if (filled <= 0.003f) continue;
            float a = (this.kindAt(k) == 9 ? 0.92f : 0.8f) * Math.min(1.0f, filled * 1.35f) * this.tGlow[k];
            paint.sprite(this.golds[this.tTex[k]], this.tu[k], this.ty[k], this.tw[k], this.th[k], this.tSpin[k], PeepSky.tint(a, 16749268), true);
        }
        int rimFaint = PeepSky.mix(16732080, 12099839, ghost);
        for (int k3 = 0; k3 < count; ++k3) {
            float s = spot * this.tLit[k3] * 0.35f;
            float h = ghost * this.tGhost[k3] * 0.26f;
            float o = this.kindAt(k3) == 0 ? 0.6f * (1.0f - gold) * (1.0f - ghost) : 0.0f;
            float sum = s + h + o;
            if (sum <= 0.003f) continue;
            color = PeepSky.mix(rimFaint, PeepSky.average(16743115, s, 15916287, h, 16732080, o), PeepSky.smooth(0.2f, 0.8f, this.tGlow[k3]));
            paint.sprite(this.rims[this.tTex[k3]], this.tu[k3], this.ty[k3], this.tw[k3], this.th[k3], this.tSpin[k3], PeepSky.tint(Math.min(1.0f, sum) * this.tGlow[k3], color), true);
        }
        if (witch > 0.0f) {
            PeepEffects.orbits(this, paint, time, witch);
        }
        if (gold > 0.0f) {
            PeepEffects.goldSparkles(this, paint, time, gold);
        }
    }

    private static float bodyBack(int kind) {
        return kind == 9 ? 0.45f : 0.25f;
    }

    private float haunt(int k) {
        boolean high = HAUNT_HIGH[this.tTex[k] % HAUNT_HIGH.length];
        return (high ? 1.0f : 0.8f) * (1.0f - 0.4f * this.hazeAt(this.ty[k]));
    }

    private float filled(int k, float gold, float ghost) {
        float sign = this.kindAt(k) == 9 ? 0.6f : 0.0f;
        return sign * (1.0f - gold) * (1.0f - ghost);
    }

    private int place(int k, int tex, float u, float up, float tall, float aspect, float spin, float alpha, float time, float shimmer, float ghost, float spot, float beat) {
        if (k >= MAX_SHOWN) {
            return k;
        }
        if (shimmer > 0.0f) {
            u += shimmer * 0.35f * (class_3532.method_15374((double)(time * 2.3f + (float)tex * 1.1f)) + 0.5f * class_3532.method_15374((double)(time * 3.7f + (float)tex * 2.9f))) / (360.0f * Math.max(0.2f, class_3532.method_15362((double)(up * ((float)Math.PI / 180)))));
            up += shimmer * 0.3f * (class_3532.method_15374((double)(time * 2.9f + (float)tex * 1.7f)) + 0.5f * class_3532.method_15374((double)(time * 4.3f + (float)tex)));
            alpha *= 1.0f - shimmer * 0.15f * (0.5f + 0.5f * class_3532.method_15374((double)(time * 3.1f + (float)tex)));
        }
        float haunt = 1.0f;
        if (ghost > 0.0f) {
            float wave = 0.5f + 0.5f * class_3532.method_15374((double)(time * 0.42f + (float)tex * 3.5f));
            haunt = PeepSky.smootherstep(PeepSky.smooth(0.38f, 0.68f, wave));
            up += ghost * 1.5f * wave;
        }
        float lit = spot > 0.0f ? spot * PeepEffects.spotlightOn(this, u, up) : 0.0f;
        float grow = beat * (1.0f + 0.06f * lit);
        this.tTex[k] = tex;
        this.tu[k] = PeepSky.wrap(u);
        this.ty[k] = PeepSky.elevationToY(up);
        this.th[k] = tall * grow;
        this.tw[k] = tall * aspect * grow;
        this.tSpin[k] = spin;
        this.tGhost[k] = haunt;
        this.tLit[k] = lit;
        this.tAlpha[k] = Math.min(alpha * class_3532.method_16439((float)ghost, (float)1.0f, (float)(haunt * 0.85f)), 1.0f - 0.3f * ghost);
        this.tGlow[k] = alpha * class_3532.method_16439((float)ghost, (float)1.0f, (float)haunt);
        return k + 1;
    }

    private void blendLook() {
        float[] rgb = this.look.rgb;
        Arrays.fill(rgb, 0.0f);
        float rest = 1.0f;
        float strength = 0.0f;
        float light = 0.0f;
        float stars = 0.0f;
        for (PeepSong song : PeepSong.ALL) {
            float w = this.shown[song.ordinal()];
            if (w <= 0.0f) continue;
            rest -= w;
            Palette p = PALETTES[song.ordinal()];
            PeepSky.add(rgb, p, w);
            strength += w * p.glowStrength();
            light += w * p.light();
            stars += w * p.starLight();
        }
        rest = Math.max(0.0f, rest);
        PeepSky.add(rgb, BASE, rest);
        this.look.glowStrength = strength + rest * BASE.glowStrength();
        this.look.light = light + rest * BASE.light();
        this.look.starLight = stars + rest * BASE.starLight();
        this.look.topColor = PeepSky.packed(rgb, 0);
        this.look.deepColor = PeepSky.packed(rgb, 1);
        this.look.duskColor = PeepSky.packed(rgb, 2);
        this.look.nightColor = PeepSky.packed(rgb, 3);
        this.look.hazeColor = PeepSky.packed(rgb, 4);
        this.look.hazeLight = class_3532.method_15363((float)((0.6f * PeepSky.luma(this.look.hazeColor) + this.look.glowStrength * PeepSky.luma(PeepSky.packed(rgb, 6))) / 255.0f), (float)0.0f, (float)1.0f);
        this.look.rimColor = PeepSky.packed(rgb, 5);
        this.look.glowColor = PeepSky.packed(rgb, 6);
    }

    private static void add(float[] rgb, Palette p, float w) {
        if (w <= 0.0f) {
            return;
        }
        PeepSky.add(rgb, 0, p.top(), w);
        PeepSky.add(rgb, 1, p.deep(), w);
        PeepSky.add(rgb, 2, p.dusk(), w);
        PeepSky.add(rgb, 3, p.night(), w);
        PeepSky.add(rgb, 4, p.haze(), w);
        PeepSky.add(rgb, 5, p.rim(), w);
        PeepSky.add(rgb, 6, p.glow(), w);
    }

    private static void add(float[] rgb, int c, int color, float w) {
        int n = c * 3;
        rgb[n] = rgb[n] + w * (float)(color >> 16 & 0xFF);
        int n2 = c * 3 + 1;
        rgb[n2] = rgb[n2] + w * (float)(color >> 8 & 0xFF);
        int n3 = c * 3 + 2;
        rgb[n3] = rgb[n3] + w * (float)(color & 0xFF);
    }

    private static int packed(float[] rgb, int c) {
        return class_3532.method_15340((int)Math.round(rgb[c * 3]), (int)0, (int)255) << 16 | class_3532.method_15340((int)Math.round(rgb[c * 3 + 1]), (int)0, (int)255) << 8 | class_3532.method_15340((int)Math.round(rgb[c * 3 + 2]), (int)0, (int)255);
    }

    float hazeAt(float y) {
        return class_3532.method_15363((float)(1.0f - Math.max(0.0f, 0.72f - y) / 0.4f), (float)0.0f, (float)1.0f) * this.look.hazeLight;
    }

    private static float luma(int rgb) {
        return 0.2126f * (float)(rgb >> 16 & 0xFF) + 0.7152f * (float)(rgb >> 8 & 0xFF) + 0.0722f * (float)(rgb & 0xFF);
    }

    private int shade(float depth, float y, float horizon) {
        float d = Math.max(0.0f, depth);
        int col = d < 0.05f ? PeepSky.mix(this.look.topColor, this.look.deepColor, d / 0.05f) : (d < 0.3f ? PeepSky.mix(this.look.deepColor, this.look.duskColor, (d - 0.05f) / 0.25f) : PeepSky.mix(this.look.duskColor, this.look.nightColor, Math.min(1.0f, (d - 0.3f) / 0.5f)));
        float haze = class_3532.method_15363((float)(1.0f - Math.max(0.0f, horizon - y) / 0.4f), (float)0.0f, (float)1.0f);
        col = PeepSky.mix(col, this.look.hazeColor, haze * 0.6f);
        return PeepSky.tint(d < 0.02f ? 0.92f : 0.97f, col);
    }

    static float heartbeat(float seconds) {
        return Math.max(PeepSky.beat(seconds, 0.17f), 0.6f * PeepSky.beat(seconds - 0.3f, 0.42f));
    }

    private static float beat(float t, float decay) {
        if (t < 0.0f) {
            return 0.0f;
        }
        if (t < 0.14f) {
            return PeepSky.smooth(0.0f, 0.14f, t);
        }
        return (float)Math.exp(-(t - 0.14f) / decay);
    }

    private static int average(int a, float wa, int b, float wb, int c, float wc) {
        float sum = Math.max(wa + wb + wc, 1.0E-4f);
        int r = Math.round(((float)(a >> 16 & 0xFF) * wa + (float)(b >> 16 & 0xFF) * wb + (float)(c >> 16 & 0xFF) * wc) / sum);
        int g = Math.round(((float)(a >> 8 & 0xFF) * wa + (float)(b >> 8 & 0xFF) * wb + (float)(c >> 8 & 0xFF) * wc) / sum);
        int bl = Math.round(((float)(a & 0xFF) * wa + (float)(b & 0xFF) * wb + (float)(c & 0xFF) * wc) / sum);
        return class_3532.method_15340((int)r, (int)0, (int)255) << 16 | class_3532.method_15340((int)g, (int)0, (int)255) << 8 | class_3532.method_15340((int)bl, (int)0, (int)255);
    }

    static float elevationToY(float degrees) {
        return 0.72f - degrees * ((float)Math.PI / 180) / 1.0471976f;
    }

    static float hash01(int seed) {
        int h = seed * -1640531535;
        h ^= h >>> 15;
        h *= -2048144777;
        h ^= h >>> 13;
        h *= -1028477379;
        h ^= h >>> 16;
        return (float)(h >>> 8) * 5.9604645E-8f;
    }

    static float hash01(int a, int b, int c) {
        return PeepSky.hash01(a * 73856093 ^ b * 19349663 ^ c * 83492791);
    }

    static int tint(float alpha, int rgb) {
        return class_9848.method_71346((float)class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f), (int)rgb);
    }

    static int mix(int from, int to, float t) {
        return class_9848.method_61319((float)class_3532.method_15363((float)t, (float)0.0f, (float)1.0f), (int)from, (int)to) & 0xFFFFFF;
    }

    static float smootherstep(float t) {
        return t * t * t * (t * (t * 6.0f - 15.0f) + 10.0f);
    }

    static float smooth(float from, float to, float x) {
        float t = class_3532.method_15363((float)((x - from) / (to - from)), (float)0.0f, (float)1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    static float wrap(float u) {
        return u - (float)Math.floor(u);
    }

    static float wrapDelta(float delta) {
        return delta - (float)Math.round(delta);
    }

    private static Built build(int generation) {
        ArrayList<int[][]> pixels = new ArrayList<int[][]>();
        ArrayList<int[][]> edges = new ArrayList<int[][]>();
        ArrayList<int[][]> gilt = new ArrayList<int[][]>();
        ArrayList<int[][]> ghosts = new ArrayList<int[][]>();
        ArrayList<Integer> widths = new ArrayList<Integer>();
        ArrayList<Integer> heights = new ArrayList<Integer>();
        ArrayList<Integer> kinds = new ArrayList<Integer>();
        for (int kind = 0; kind < KINDS.length; ++kind) {
            BufferedImage art = PeepSky.load(KINDS[kind]);
            if (art == null) continue;
            float scale = Math.min(3.0f, 320.0f / (float)Math.max(art.getWidth(), art.getHeight()));
            int w = Math.round((float)art.getWidth() * scale) + 52;
            int h = Math.round((float)art.getHeight() * scale) + 52;
            BufferedImage canvas = new BufferedImage(w, h, 2);
            Graphics2D g = canvas.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(art, 26, 26, w - 52, h - 52, null);
            g.dispose();
            int[] mask = canvas.getRGB(0, 0, w, h, null, 0, w);
            pixels.add(SkyMips.chain(PeepSky.neon(mask, w, h), w, h));
            edges.add(SkyMips.chain(PeepArt.rim(mask, w, h), w, h));
            gilt.add(SkyMips.chain(PeepArt.gold(mask, w, h), w, h));
            ghosts.add(SkyMips.chain(PeepArt.veil(mask, w, h), w, h));
            widths.add(w);
            heights.add(h);
            kinds.add(kind);
        }
        PeepArt.Art[] art = PeepArt.drawAll();
        int[][][] artLevels = new int[art.length][][];
        for (int i = 0; i < art.length; ++i) {
            artLevels[i] = SkyMips.chain(art[i].argb(), art[i].width(), art[i].height());
        }
        return new Built(generation, (int[][][])pixels.toArray((T[])new int[0][][]), (int[][][])edges.toArray((T[])new int[0][][]), (int[][][])gilt.toArray((T[])new int[0][][]), (int[][][])ghosts.toArray((T[])new int[0][][]), widths.stream().mapToInt(Integer::intValue).toArray(), heights.stream().mapToInt(Integer::intValue).toArray(), kinds.stream().mapToInt(Integer::intValue).toArray(), art, artLevels);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static BufferedImage load(String kind) {
        try (InputStream in = PeepSky.class.getResourceAsStream(TATTOOS + kind + ".png");){
            if (in == null) {
                LOGGER.warn("[Anubis] Sky easter egg tattoo {} is missing", (Object)kind);
                BufferedImage bufferedImage2 = null;
                return bufferedImage2;
            }
            BufferedImage bufferedImage = ImageIO.read(in);
            return bufferedImage;
        }
        catch (IOException e) {
            LOGGER.warn("[Anubis] Sky easter egg tattoo {} couldn't be read", (Object)kind, (Object)e);
            return null;
        }
    }

    private static int[] neon(int[] mask, int w, int h) {
        float[] alpha = new float[w * h];
        for (int i = 0; i < alpha.length; ++i) {
            alpha[i] = (float)(mask[i] >>> 24) / 255.0f;
        }
        float[] halo = PeepSky.blur(alpha, w, h, 9);
        float[] solid = new float[w * h];
        for (int i = 0; i < solid.length; ++i) {
            solid[i] = PeepSky.smooth(0.6f, 0.9f, alpha[i]);
        }
        float[] deep = PeepSky.blur(solid, w, h, 7);
        float[] thin = PeepSky.blur(solid, w, h, 1);
        int[] out = new int[w * h];
        for (int i = 0; i < out.length; ++i) {
            float core = Math.max(alpha[i], Math.min(1.0f, thin[i] * 1.6f) * (1.0f - PeepSky.smooth(0.2f, 0.4f, deep[i])));
            float glow = Math.min(1.0f, halo[i] * 2.6f);
            float hot = PeepSky.smooth(0.7f, 1.0f, core) * (1.0f - 0.75f * PeepSky.smooth(0.45f, 0.8f, deep[i]));
            out[i] = PeepSky.tint(Math.max(core, glow * 0.85f), PeepSky.mix(16732080, 16766704, hot));
        }
        return out;
    }

    static float[] blur(float[] src, int w, int h, int radius) {
        float[] a = (float[])src.clone();
        float[] b = new float[src.length];
        for (int pass = 0; pass < 2; ++pass) {
            float sum;
            for (int y = 0; y < h; ++y) {
                int x;
                sum = 0.0f;
                for (x = -radius; x <= radius; ++x) {
                    sum += a[y * w + class_3532.method_15340((int)x, (int)0, (int)(w - 1))];
                }
                for (x = 0; x < w; ++x) {
                    b[y * w + x] = sum / (float)(radius * 2 + 1);
                    sum += a[y * w + Math.min(w - 1, x + radius + 1)] - a[y * w + Math.max(0, x - radius)];
                }
            }
            for (int x = 0; x < w; ++x) {
                int y;
                sum = 0.0f;
                for (y = -radius; y <= radius; ++y) {
                    sum += b[class_3532.method_15340((int)y, (int)0, (int)(h - 1)) * w + x];
                }
                for (y = 0; y < h; ++y) {
                    a[y * w + x] = sum / (float)(radius * 2 + 1);
                    sum += b[Math.min(h - 1, y + radius + 1) * w + x] - b[Math.max(0, y - radius) * w + x];
                }
            }
        }
        return a;
    }

    static {
        for (PeepSong song : PeepSong.ALL) {
            PeepSky.PALETTES[song.ordinal()] = PeepSky.palette(song);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Look {
        final float[] rgb = new float[21];
        float glowStrength;
        float light;
        float starLight;
        float hazeLight;
        int topColor;
        int deepColor;
        int duskColor;
        int nightColor;
        int hazeColor;
        int rimColor;
        int glowColor;

        private Look() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Palette(int top, int deep, int dusk, int night, int haze, int rim, int glow, float glowStrength, float light, float starLight) {
    }

    @Environment(value=EnvType.CLIENT)
    private record Built(int generation, int[][][] tattoos, int[][][] rims, int[][][] golds, int[][][] veils, int[] widths, int[] heights, int[] kinds, PeepArt.Art[] art, int[][][] artLevels) {
    }
}

