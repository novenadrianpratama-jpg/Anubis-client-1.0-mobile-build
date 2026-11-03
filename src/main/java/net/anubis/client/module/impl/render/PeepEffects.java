/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 */
package net.anubis.client.module.impl.render;

import net.anubis.client.module.impl.render.PeepSky;
import net.anubis.client.module.impl.render.PeepSong;
import net.anubis.client.module.impl.render.SkyImage;
import net.anubis.client.module.impl.render.SkyPaint;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;

@Environment(value=EnvType.CLIENT)
final class PeepEffects {
    private static final float SPOT_BASE = -7.0f;
    private static final float SPOT_LENGTH = 95.0f;
    private static final float SPOT_WIDTH = 30.0f;
    private static final float SPOT_SIDE = 0.085f;
    private static final int BLUE = 9074943;
    private static final int MAGENTA = 16736192;
    private static final int LILAC = 13995263;
    private static final int COOL = 8029951;
    static final int LIGHTNING_LANES = 2;
    static final int HEARTS = 20;
    private static final int METEOR_LANES = 15;
    private static final int RAIN_DROPS = 330;
    private static final int BEAMS = 8;
    private static final int SPARKS = 135;
    private static final int PETALS = 38;
    private static final int WISPS = 21;
    private static final int MOTES = 55;
    private static final int GLITTER = 84;
    private static final int SPOT_DUST = 44;
    private static final int SPIRITS = 12;
    private static final int FEATHERS = 22;
    private static final int METEOR_TRIES = 4;
    private static final int METEOR_SLOTS = 0;
    private static final int PETAL_SLOTS = 15;
    private static final int HEART_SLOTS = 53;
    private static final int WISP_SLOTS = 73;
    private static final int MOTE_SLOTS = 94;
    private static final int GLITTER_SLOTS = 149;
    private static final int SPIRIT_SLOTS = 233;
    private static final int FEATHER_SLOTS = 245;
    private static final float IN_VIEW = 0.8f;
    private static final float VIEW_SPREAD = 0.375f;
    private static final float CRY_ALONE_RAIN = 2.2f;
    private static final float RAIN_SLANT = 0.06f;
    private static final int BEAM_SAMPLES = 8;
    private static final float[] SCRATCH = new float[8];
    private static final float[] OTHER = new float[8];
    private static final float[] ARC = new float[9];
    private static final float[] AT = new float[2];
    private static final float METEOR_GAP = 5.0f;

    private PeepEffects() {
    }

    static void skyLight(PeepSky s, SkyPaint p, float cover, float time) {
        float sun;
        float bright = s.w(PeepSong.THE_BRIGHTSIDE) * cover;
        if (bright > 0.003f) {
            PeepEffects.sunrise(s, p, bright, time);
        }
        if ((sun = s.w(PeepSong.SUNLIGHT_ON_YOUR_SKIN) * cover) > 0.003f) {
            PeepEffects.sunlight(s, p, sun, time);
        }
    }

    private static void sunrise(PeepSky s, SkyPaint p, float w, float time) {
        float age = s.age(PeepSong.THE_BRIGHTSIDE);
        float rise = PeepSky.smootherstep(class_3532.method_15363((float)(age / 14.0f), (float)0.0f, (float)1.0f));
        float height = class_3532.method_16439((float)rise, (float)0.28f, (float)0.7f) + 0.02f * class_3532.method_15374((double)(time * 0.25f));
        float horizon = 0.72f;
        float middle = horizon - 0.16f;
        int pink = PeepSky.tint(0.36f * w, 16743088);
        int none = PeepSky.tint(0.0f, 16743088);
        for (int i = 0; i < 64; ++i) {
            float u0 = (float)i / 64.0f;
            float u1 = (float)(i + 1) / 64.0f;
            float underSun = PeepSky.smooth(0.06f, 0.0f, Math.abs(PeepSky.wrapDelta(u0 + 0.0078125f)));
            int gold = PeepSky.tint(0.28f * w * (1.0f - 0.5f * underSun), 16761963);
            p.gradient(true, u0, u1, horizon - height, middle, none, pink);
            p.gradient(true, u0, u1, middle, horizon + 0.04f, pink, gold);
        }
        float up = class_3532.method_16439((float)PeepSky.smootherstep(class_3532.method_15363((float)(age / 18.0f), (float)0.0f, (float)1.0f)), (float)-3.0f, (float)9.0f);
        SkyImage beam = s.art[7];
        for (int k = 0; k < 3; ++k) {
            float tilt = (float)(k - 1) * 0.55f + 0.08f * class_3532.method_15374((double)(time * 0.12f + (float)k * 2.0f));
            float shine = 0.085f * (0.8f + 0.2f * class_3532.method_15374((double)(time * 0.3f + (float)k * 1.9f))) * (1.0f - 0.3f * s.hazeAt(PeepEffects.y(up)));
            PeepEffects.beam(p, beam, 0.0f, up - 1.0f, tilt, k == 0 ? 36.0f : (k == 2 ? 44.0f : 40.0f), k == 0 ? 12.0f : (k == 2 ? 16.0f : 14.0f), 16763024, shine * w);
        }
        float sunY = PeepEffects.y(up);
        float aim = PeepEffects.clear(s, 0.0f, up);
        p.glow(0.0f, sunY, PeepEffects.units(34.0f), 16752250, 0.23f * w);
        p.glow(0.0f, sunY, PeepEffects.units(13.0f), 16763024, 0.2f * w * (0.4f + 0.6f * aim));
        p.glow(0.0f, sunY, PeepEffects.units(3.6f), 16760468, 0.2f * w * (0.1f + 0.9f * aim) * PeepEffects.core(s, sunY));
    }

    private static void sunlight(PeepSky s, SkyPaint p, float w, float time) {
        float sourceU = s.stageU + 0.05f;
        float sourceUp = 64.0f;
        p.glow(sourceU, PeepEffects.y(50.0f), PeepEffects.units(64.0f), 16736150, 0.13f * w);
        p.glow(sourceU, PeepEffects.y(40.0f), PeepEffects.units(70.0f), 16756848, 0.07f * w);
        p.glow(sourceU, PeepEffects.y(sourceUp - 8.0f), PeepEffects.units(34.0f), 16752762, 0.2f * w);
        p.glow(sourceU, PeepEffects.y(sourceUp), PeepEffects.units(14.0f), 16762976, 0.32f * w);
        p.glow(sourceU, PeepEffects.y(sourceUp), PeepEffects.units(5.0f), 16765562, 0.3f * w * PeepEffects.core(s, PeepEffects.y(sourceUp)));
        SkyImage cone = s.art[14];
        for (int k = 0; k < 5; ++k) {
            float tilt = (float)Math.PI + ((float)k - 2.0f) * 0.3f + 0.07f * class_3532.method_15374((double)(time * 0.17f + (float)k * 1.3f));
            float length = 62.0f + 8.0f * PeepSky.hash01(k * 3 + 1);
            float width = 15.0f + 6.0f * PeepSky.hash01(k * 3 + 2);
            float breathe = 0.8f + 0.2f * class_3532.method_15374((double)(time * 0.31f + (float)k * 1.7f));
            float shine = breathe * PeepEffects.beamClear(s, sourceU, sourceUp + 2.0f, tilt, length, width) * w;
            PeepEffects.beam(p, cone, sourceU, sourceUp + 2.0f, tilt, length, width, 16749704, 0.17f * shine);
            PeepEffects.beam(p, cone, sourceU, sourceUp + 2.0f, tilt, length * 0.45f, width * 0.7f, 14721070, 0.62f * shine, false);
            PeepEffects.beam(p, cone, sourceU, sourceUp + 2.0f, tilt, length * 0.45f, width * 0.6f, 16761418, 0.5f * shine);
        }
    }

    static void behind(PeepSky s, SkyPaint p, float time) {
        float ghost;
        float witch;
        float awful;
        float spot;
        float beamer;
        float stars;
        float chains;
        float hell;
        float a = s.alpha;
        float alone = s.w(PeepSong.CRY_ALONE) * a;
        if (alone > 0.003f) {
            PeepEffects.lonely(s, p, alone, time);
        }
        if ((hell = s.w(PeepSong.HELLBOY) * a) > 0.003f) {
            float flicker = 0.85f + 0.15f * (0.5f + 0.5f * class_3532.method_15374((double)(time * ((float)Math.PI * 2) * 0.7f)));
            PeepEffects.horizonLine(p, 1.5f, 6.0f, 0xFF3355, 0.17f * flicker * hell);
        }
        if ((chains = s.w(PeepSong.FOUR_GOLD_CHAINS) * a) > 0.003f) {
            PeepEffects.horizonLine(p, 2.0f, 7.0f, 16765562, 0.22f * chains);
        }
        if ((stars = s.w(PeepSong.STAR_SHOPPING) * a) > 0.003f) {
            PeepEffects.shootingStars(s, p, stars, time);
        }
        if ((beamer = s.w(PeepSong.BEAMER_BOY) * a) > 0.003f) {
            PeepEffects.concertBeams(s, p, beamer, time);
        }
        if ((spot = s.w(PeepSong.SPOTLIGHT) * a) > 0.003f) {
            PeepEffects.spotlight(s, p, spot, time);
        }
        if ((awful = s.w(PeepSong.AWFUL_THINGS) * a) > 0.003f) {
            PeepEffects.storm(s, p, awful, time);
        }
        if ((witch = s.w(PeepSong.WITCHBLADES) * a) > 0.003f) {
            PeepEffects.mist(p, s.art[10], 5, time * 0.004f, -3.0f, 18.0f, 10108104, 0.2f * witch, true);
            PeepEffects.mist(p, s.art[10], 4, -time * 0.0025f + 0.3f, 4.0f, 34.0f, 16736200, 0.38f * witch, true);
        }
        if ((ghost = s.w(PeepSong.GHOST_BOY) * a) > 0.003f) {
            PeepEffects.mist(p, s.art[10], 6, time * 0.0015f, -4.0f, 11.0f, 12356338, 0.28f * ghost, false);
            PeepEffects.mist(p, s.art[10], 5, -time * 0.001f + 0.5f, 0.0f, 16.0f, 10516706, 0.1f * ghost, true);
            PeepEffects.mist(p, s.art[10], 5, time * 0.0012f + 0.2f, -2.0f, 9.0f, 14715600, 0.14f * ghost, true);
        }
    }

    private static void lonely(PeepSky s, SkyPaint p, float w, float time) {
        PeepEffects.mist(p, s.art[10], 6, time * 0.0012f, -2.0f, 8.0f, 0x2A0A22, 0.35f * w, false);
    }

    private static void shootingStars(PeepSky s, SkyPaint p, float w, float time) {
        SkyImage streak = s.art[0];
        float[] at = SCRATCH;
        for (int pass = 0; pass < 2; ++pass) {
            for (int lane = 0; lane < 15; ++lane) {
                float length;
                int n;
                boolean hero = lane % 4 == 3;
                float period = 3.2f + 2.6f * PeepSky.hash01(lane * 3 + 1);
                float local = time / period + PeepSky.hash01(lane * 3 + 2);
                float since = (local - (float)(n = class_3532.method_15375((float)local))) * period;
                if (since > (length = (1.1f + 0.6f * PeepSky.hash01(lane, n, 1)) * (hero ? 1.3f : 1.0f))) continue;
                float t = since / length;
                int slot = 0 + lane;
                int path = s.picked(slot, n);
                float anchor = s.anchor(slot, n);
                if (path < 0) {
                    path = 4;
                    for (int k = 0; k < 4 && path == 4; ++k) {
                        if (!PeepEffects.meteorClear(s, lane, n * 4 + k, hero, anchor, length, since, time, at)) continue;
                        path = k;
                    }
                    s.pick(slot, n, path);
                }
                if (path >= 4) continue;
                int run = n * 4 + path;
                PeepEffects.meteor(lane, run, hero, anchor, 1.0f - (float)Math.pow(1.0f - t, 1.7), at);
                float headU = at[0];
                float headUp = at[1];
                float dx = at[2];
                float dy = at[3];
                float travel = at[4];
                float done = at[5];
                float fade = PeepSky.smooth(0.0f, 0.1f, t) * (1.0f - PeepSky.smooth(0.55f, 1.0f, t)) * PeepSky.smooth(3.0f, 10.0f, headUp) * w * PeepEffects.clear(s, headU, headUp) * PeepSky.smooth(0.5f, 1.0f, s.alpha);
                if (fade <= 0.003f) continue;
                if (pass == 0) {
                    PeepEffects.meteor(lane, run, hero, anchor, 1.0f, OTHER);
                    float cap = hero ? 34.0f : (s.lyricBox(OTHER[0], OTHER[1], 6.0f, 6.0f) > 0.05f ? 20.0f : 26.0f);
                    float tail = Math.min(travel * done, cap) * (0.6f + 0.4f * (1.0f - t));
                    float centreUp = headUp - dy * tail * 0.4f;
                    float centreU = headU - PeepEffects.du(dx * tail * 0.4f, centreUp);
                    float angle = (float)Math.atan2(-dy, dx);
                    p.sprite(streak, centreU, PeepEffects.y(centreUp), PeepEffects.units(tail), PeepEffects.units(4.5f), angle, PeepSky.tint(0.42f * fade, 16732080), true);
                    p.sprite(streak, centreU, PeepEffects.y(centreUp), PeepEffects.units(tail), PeepEffects.units(1.2f), angle, PeepSky.tint(0.5f * fade, 16756960), true);
                    continue;
                }
                p.glow(headU, PeepEffects.y(headUp), PeepEffects.units(1.45f), 16756960, 0.38f * fade * PeepEffects.core(s, PeepEffects.y(headUp)));
                p.glow(headU, PeepEffects.y(headUp), PeepEffects.units(7.0f), 16732080, 0.3f * fade);
            }
        }
    }

    private static void meteor(int lane, int run, boolean hero, float anchor, float done, float[] out) {
        float startU = anchor + 0.04f + (PeepSky.hash01(lane, run, 2) - 0.5f) * 0.36f;
        float startUp = 25.0f + 33.0f * PeepSky.hash01(lane, run, 3);
        float slope = (16.0f + 22.0f * PeepSky.hash01(lane, run, 5)) * ((float)Math.PI / 180);
        float travel = hero ? 40.0f + 15.0f * PeepSky.hash01(lane, run, 6) : 22.0f + 16.0f * PeepSky.hash01(lane, run, 6);
        float dx = -class_3532.method_15362((double)slope);
        float dy = -class_3532.method_15374((double)slope);
        out[1] = startUp + dy * travel * done;
        out[0] = startU + PeepEffects.du(dx * travel * done, out[1]);
        out[2] = dx;
        out[3] = dy;
        out[4] = travel;
        out[5] = done;
    }

    private static boolean meteorClear(PeepSky s, int lane, int run, boolean hero, float anchor, float length, float since, float time, float[] scratch) {
        for (int j = 1; j <= 6; ++j) {
            float ahead;
            float t = 0.08f + 0.84f * (float)(j - 1) / 5.0f;
            PeepEffects.meteor(lane, run, hero, anchor, 1.0f - (float)Math.pow(1.0f - t, 1.7), scratch);
            float later = t * length - since;
            if (PeepEffects.crowded(s, lane, time + later, scratch[0], scratch[1])) {
                return false;
            }
            if (s.lyricBox(scratch[0], scratch[1], 2.5f, 2.5f) > 0.02f) {
                return false;
            }
            if (scratch[1] < 3.0f || !(s.clearOfTattoos(scratch[0] - (ahead = 0.02f * later), scratch[1], 1.5f) < 0.9f)) continue;
            return false;
        }
        return true;
    }

    private static boolean crowded(PeepSky s, int lane, float when, float u, float up) {
        for (int other = 0; other < 15; ++other) {
            float length;
            int n;
            if (other == lane) continue;
            boolean hero = other % 4 == 3;
            float period = 3.2f + 2.6f * PeepSky.hash01(other * 3 + 1);
            float local = when / period + PeepSky.hash01(other * 3 + 2);
            float since = (local - (float)(n = class_3532.method_15375((float)local))) * period;
            if (since > (length = (1.1f + 0.6f * PeepSky.hash01(other, n, 1)) * (hero ? 1.3f : 1.0f))) continue;
            int path = s.picked(0 + other, n);
            float anchor = s.anchored(0 + other, n);
            if (path < 0 || path >= 4 || Float.isNaN(anchor)) continue;
            PeepEffects.meteor(other, n * 4 + path, hero, anchor, 1.0f - (float)Math.pow(1.0f - since / length, 1.7), OTHER);
            float across = PeepSky.wrapDelta(OTHER[0] - u) * 360.0f * class_3532.method_15362((double)(up * ((float)Math.PI / 180)));
            float along = OTHER[1] - up;
            if (!(across * across + along * along < 25.0f)) continue;
            return true;
        }
        return false;
    }

    private static void concertBeams(PeepSky s, SkyPaint p, float w, float time) {
        float baseU;
        int b;
        SkyImage beam = s.art[7];
        SkyImage cone = s.art[14];
        for (b = 0; b < 8; ++b) {
            baseU = ((float)b + 0.5f) / 8.0f + 0.01f * class_3532.method_15374((double)(time * 0.05f + (float)b));
            for (int j = 0; j < 2; ++j) {
                float swing = class_3532.method_15374((double)(time * 0.28f * (1.0f + 0.12f * (float)b) + (float)b * 1.9f + (float)j * 0.7f));
                float tilt = (j == 0 ? 1.0f : -1.0f) * (0.12f + 0.5f * swing);
                boolean pink = (b + j) % 2 == 0;
                float wide = pink ? 24.0f : 26.0f;
                float shine = (0.78f + 0.22f * class_3532.method_15374((double)(time * 1.1f + (float)b * 2.0f + (float)j))) * w * PeepEffects.beamClear(s, baseU, -6.0f, tilt, 82.0f, wide);
                PeepEffects.beam(p, cone, baseU, -6.0f, tilt, 70.0f, wide * 0.8f, pink ? 16736192 : 8029951, (pink ? 0.21f : 0.24f) * shine);
                PeepEffects.beam(p, beam, baseU, -6.0f, tilt, 82.0f, wide, pink ? 16736192 : 9074943, 0.33f * shine);
                PeepEffects.beam(p, beam, baseU, -6.0f, tilt, 80.0f, 3.0f, pink ? 16769268 : 14866175, 0.2f * shine);
            }
        }
        PeepEffects.horizonLine(p, 1.0f, 7.0f, 9074943, 0.08f * w);
        for (b = 0; b < 8; ++b) {
            baseU = ((float)b + 0.5f) / 8.0f + 0.01f * class_3532.method_15374((double)(time * 0.05f + (float)b));
            float flare = w * PeepEffects.clear(s, baseU, 2.5f);
            p.glow(baseU, PeepEffects.y(2.5f), PeepEffects.units(9.0f), b % 2 == 0 ? 16736192 : 9074943, 0.3f * flare);
            p.glow(baseU, PeepEffects.y(2.5f), PeepEffects.units(3.9f), b % 2 == 0 ? 0xFFE6F6 : 14866175, 0.36f * flare);
        }
    }

    private static void spotlight(PeepSky s, SkyPaint p, float w, float time) {
        SkyImage cone = s.art[14];
        float tilt = PeepEffects.spotTilt(time);
        float baseU = s.stageU + 0.085f;
        float shine = w * (0.3f + 0.7f * PeepEffects.beamClear(s, baseU, -7.0f, tilt, 95.0f, 30.0f));
        PeepEffects.beam(p, cone, baseU, -7.0f, tilt, 95.0f, 54.0f, 16732080, 0.13f * shine);
        PeepEffects.beam(p, cone, baseU, -7.0f, tilt, 95.0f, 30.0f, 15746463, 0.45f * shine);
        PeepEffects.beam(p, cone, baseU, -7.0f, tilt, 52.25f, 13.5f, 16751318, 0.2f * shine);
        PeepEffects.arc(baseU, -7.0f, tilt);
        PeepEffects.onArc(8.5f / Math.max(0.3f, class_3532.method_15362((double)tilt)), 0.0f);
        float sourceU = AT[0];
        float sourceUp = AT[1];
        float source = w * (0.25f + 0.75f * PeepEffects.clear(s, sourceU, sourceUp));
        p.glow(sourceU, PeepEffects.y(sourceUp), PeepEffects.units(13.0f), 16766704, 0.3f * source);
        p.glow(sourceU, PeepEffects.y(sourceUp), PeepEffects.units(5.8f), 16759008, 0.48f * source);
        for (int k = 0; k < 44; ++k) {
            float rise = class_3532.method_22450((float)(PeepSky.hash01(k * 5 + 1) + time * 3.0f / 58.9f));
            float along = 95.0f * (0.08f + 0.62f * rise);
            float spread = PeepEffects.coneHalf(along / 95.0f);
            float side = PeepSky.hash01(k * 5 + 2) - 0.5f;
            float across = side * spread + 0.5f * class_3532.method_15374((double)(time * 0.45f + (float)k * 2.0f));
            PeepEffects.onArc(along, across);
            float u = AT[0];
            float up = AT[1];
            float twinkle = 0.5f + 0.5f * (float)Math.pow(0.5f + 0.5f * class_3532.method_15374((double)(time * (0.7f + 0.5f * PeepSky.hash01(k * 5 + 3)) + (float)k * 1.7f)), 2.0);
            float ends = PeepSky.smooth(0.0f, 0.12f, rise) * (1.0f - PeepSky.smooth(0.8f, 1.0f, rise));
            float axis = (1.0f - 0.6f * Math.min(1.0f, Math.abs(side) * 2.0f)) * (1.0f - PeepSky.smooth(0.3f, 0.5f, Math.abs(side)));
            p.glow(u, PeepEffects.y(up), PeepEffects.units(0.4f + 0.4f * PeepSky.hash01(k * 5 + 4)), 16765164, 0.55f * twinkle * axis * ends * w * PeepEffects.clear(s, u, up));
        }
    }

    static float spotTilt(float time) {
        return -0.05f + 0.4f * class_3532.method_15374((double)(time * 0.2f + 0.5f));
    }

    static float spotlightOn(PeepSky s, float u, float up) {
        PeepEffects.arc(s.stageU + 0.085f, -7.0f, PeepEffects.spotTilt(s.time));
        float along = PeepEffects.alongArc(u, up);
        float across = PeepEffects.acrossArc(u, up);
        if (along <= 0.0f || along >= 95.0f) {
            return 0.0f;
        }
        float t = along / 95.0f;
        float half = PeepEffects.coneHalf(t);
        return (1.0f - PeepSky.smooth(half * 0.55f, half * 1.1f, across)) * (1.0f - PeepSky.smooth(0.75f, 1.0f, t));
    }

    private static float coneHalf(float t) {
        return (0.05f + 0.84999996f * t) * 30.0f * 0.5f;
    }

    private static void storm(PeepSky s, SkyPaint p, float w, float time) {
        SkyImage mist = s.art[10];
        PeepEffects.mist(p, mist, 6, time * 0.002f, -2.0f, 24.0f, 1967638, 0.75f * w, false);
        PeepEffects.mist(p, mist, 5, -time * 0.0013f + 0.4f, 10.0f, 34.0f, 0x2A0A20, 0.75f * w, false);
        PeepEffects.mist(p, mist, 6, time * 0.002f + 0.37f, 2.0f, 20.0f, 5902140, 0.18f * w, true);
        PeepEffects.mist(p, mist, 5, -time * 0.0016f + 0.15f, 16.0f, 52.0f, 9050714, 0.15f * w, true);
        float[] bolt = SCRATCH;
        for (int pass = 0; pass < 2; ++pass) {
            for (int lane = 0; lane < 2; ++lane) {
                float flash = PeepEffects.lightning(lane, time, bolt) * w;
                if (flash <= 0.003f) continue;
                float u = s.stageU + bolt[0];
                float tall = bolt[1];
                flash *= PeepEffects.clear(s, u, tall * 0.5f);
                if (pass == 0) {
                    p.glow(u, PeepEffects.y(tall * 0.7f), PeepEffects.units(tall * 2.4f), 9050714, 0.2f * flash);
                    p.glow(u, PeepEffects.y(tall * 0.55f), PeepEffects.units(tall * 1.5f), 12720235, 0.26f * flash);
                    p.glow(u, PeepEffects.y(tall * 0.45f), PeepEffects.units(tall * 0.6f), 16732080, 0.16f * flash);
                    continue;
                }
                p.sprite(s.art[bolt[2] < 0.5f ? 11 : 12], u, PeepEffects.y(tall * 0.5f - 0.5f), PeepEffects.units(tall * 0.5f), PeepEffects.units(tall + 1.0f), bolt[3], PeepSky.tint(0.6f * flash, 0xFFFFFF), true);
            }
        }
    }

    static float lightning(int lane, float time, float[] out) {
        int n;
        float period = 18.0f + 12.0f * PeepSky.hash01(lane * 7 + 1);
        float local = time / period + PeepSky.hash01(lane * 7 + 2);
        float since = (local - (float)(n = class_3532.method_15375((float)local))) * period;
        if (since > 2.6f) {
            return 0.0f;
        }
        out[0] = (PeepSky.hash01(lane, n, 1) < 0.5f ? -1.0f : 1.0f) * (0.07f + 0.14f * PeepSky.hash01(lane, n, 5));
        out[1] = 16.0f + 12.0f * PeepSky.hash01(lane, n, 2);
        out[2] = PeepSky.hash01(lane, n, 3);
        out[3] = (PeepSky.hash01(lane, n, 4) - 0.5f) * 0.25f;
        return Math.min(1.0f, PeepEffects.flicker(since) + 0.55f * PeepEffects.flicker(since - 0.25f) + 0.3f * PeepEffects.flicker(since - 0.65f));
    }

    private static float flicker(float t) {
        if (t < 0.0f) {
            return 0.0f;
        }
        if (t < 0.2f) {
            return PeepSky.smooth(0.0f, 0.2f, t);
        }
        return (float)Math.exp(-(t - 0.2f) / 0.24f);
    }

    static void front(PeepSky s, SkyPaint p, float time) {
        float glitter;
        float motes;
        float spirits;
        float ghosts;
        float sparks;
        float feathers;
        float hearts;
        float petals;
        float a = s.alpha;
        float alone = s.w(PeepSong.CRY_ALONE);
        float rain = (s.w(PeepSong.CRYBABY) + 2.2f * alone) * a;
        if (rain > 0.003f) {
            PeepEffects.rain(s, p, rain, 1.0f + 0.5f * alone, alone, time);
        }
        if ((petals = s.w(PeepSong.LIFE_IS_BEAUTIFUL) * a) > 0.003f) {
            PeepEffects.petals(s, p, petals, time);
        }
        if ((hearts = s.w(PeepSong.SAVE_THAT_SHIT) * a) > 0.003f) {
            PeepEffects.hearts(s, p, hearts, time);
        }
        if ((feathers = s.w(PeepSong.FALLING_DOWN) * a) > 0.003f) {
            PeepEffects.feathers(s, p, feathers, time);
        }
        if ((sparks = s.w(PeepSong.HELLBOY) * a) > 0.003f) {
            PeepEffects.sparks(s, p, sparks, time);
        }
        if ((ghosts = s.w(PeepSong.GHOST_GIRL) * a) > 0.003f) {
            PeepEffects.ghosts(s, p, ghosts, time);
        }
        if ((spirits = s.w(PeepSong.GHOST_BOY) * a) > 0.003f) {
            PeepEffects.spirits(s, p, spirits, time);
        }
        if ((motes = s.w(PeepSong.SUNLIGHT_ON_YOUR_SKIN) * a) > 0.003f) {
            PeepEffects.motes(s, p, motes, time);
        }
        if ((glitter = s.w(PeepSong.FOUR_GOLD_CHAINS) * a) > 0.003f) {
            PeepEffects.glitter(s, p, glitter, time);
        }
    }

    private static void rain(PeepSky s, SkyPaint p, float amount, float boost, float alone, float time) {
        SkyImage drop = s.art[1];
        float share = amount / 2.2f;
        float strength = Math.min(1.0f, amount) * boost;
        int color = PeepSky.mix(16747216, 16736184, 0.5f * alone);
        float sin = class_3532.method_15374((double)0.06f);
        float cos = class_3532.method_15362((double)0.06f);
        float floor = class_3532.method_16439((float)alone, (float)0.28f, (float)0.22f);
        for (int pass = 0; pass < 2; ++pass) {
            int stride = pass == 0 ? 1 : 3;
            for (int k = pass; k < 330; k += stride) {
                float length;
                float keep = class_3532.method_15363((float)((share - PeepSky.hash01(k * 5 + 1)) * 10.0f), (float)0.0f, (float)1.0f);
                if (keep <= 0.0f) continue;
                float period = 2.2f + 1.2f * PeepSky.hash01(k * 5 + 2);
                float local = time / period + PeepSky.hash01(k * 5 + 3);
                int n = class_3532.method_15375((float)local);
                float t = local - (float)n;
                float near = PeepSky.hash01(k * 5 + 4);
                float up = class_3532.method_16439((float)t, (float)78.0f, (float)-2.0f);
                float u = PeepSky.hash01(k, n, 7) + PeepEffects.du(-(78.0f - up) * 0.06f, up);
                float fade = PeepSky.smooth(0.0f, 0.08f, t) * PeepSky.smooth(2.0f, 10.0f, up) * keep * PeepEffects.clear(s, u, up);
                if (fade <= 0.003f) continue;
                fade *= floor + (1.0f - floor) * PeepSky.smooth(0.25f, 1.0f, s.clearOfTattoos(u, up, 0.4f));
                boolean hero = near > 0.85f - 0.08f * alone;
                float f = length = hero ? class_3532.method_16439((float)alone, (float)(3.0f + 3.0f * near), (float)7.5f) : 3.0f + 3.0f * near;
                if (hero) {
                    fade *= 1.0f + 0.3f * alone;
                }
                if (pass == 0) {
                    p.sprite(drop, u, PeepEffects.y(up), PeepEffects.units(0.35f + 0.2f * near), PeepEffects.units(length), 0.06f, PeepSky.tint((0.4f + 0.38f * near) * fade * strength, color), true);
                    continue;
                }
                float headUp = up - 0.35f * length * cos;
                p.glow(u + PeepEffects.du(-0.35f * length * sin, headUp), PeepEffects.y(headUp), PeepEffects.units(0.7f), 16754397, 0.22f * fade * strength);
            }
        }
    }

    private static void petals(PeepSky s, SkyPaint p, float w, float time) {
        for (int pass = 0; pass < 2; ++pass) {
            for (int k = 0; k < 38; ++k) {
                float h = PeepSky.hash01(k * 7 + 1);
                float period = 16.0f + 8.0f * h;
                float local = time / period + PeepSky.hash01(k * 7 + 2);
                int n = class_3532.method_15375((float)local);
                float t = local - (float)n;
                float near = PeepSky.hash01(k * 7 + 3);
                float up = class_3532.method_16439((float)t, (float)76.0f, (float)2.0f);
                float u = PeepEffects.around(s, 15 + k, n, PeepSky.hash01(k, n, 9)) + PeepEffects.du(3.5f * class_3532.method_15374((double)(time * 0.55f * (0.8f + 0.4f * h) + (float)k * 1.3f)), up);
                float tall = 2.2f + 1.6f * near;
                float fade = PeepSky.smooth(0.0f, 0.07f, t) * PeepSky.smooth(3.0f, 11.0f, up) * w * PeepEffects.clearAround(s, u, up) * s.clearOfTattoos(u, up, tall * 0.4f);
                if (fade <= 0.003f) continue;
                if (pass == 0) {
                    p.glow(u, PeepEffects.y(up), PeepEffects.units(tall * 1.1f), 16732080, 0.1f * fade * PeepSky.smooth(6.0f, 13.0f, up));
                    continue;
                }
                float turn = class_3532.method_15362((double)(time * (0.7f + 0.5f * h) + (float)k));
                float flip = 0.42f + 0.58f * Math.abs(turn);
                float spin = h * ((float)Math.PI * 2) + time * (0.22f + 0.3f * near) * (k % 2 == 0 ? 1.0f : -1.0f) + 0.6f * class_3532.method_15374((double)(time * 0.7f + (float)k));
                int shade = PeepSky.mix(turn < 0.0f ? 16765668 : 16770800, 15916262, 0.4f * (1.0f - near));
                shade = PeepSky.mix(shade, 16740264, 1.0f - fade);
                float seen = PeepSky.smooth(0.35f, 0.75f, fade);
                SkyImage petal = s.art[k % 3 == 0 ? 15 : 2];
                float w0 = PeepEffects.units(tall * 0.8f * flip);
                float h0 = PeepEffects.units(tall);
                p.sprite(petal, u, PeepEffects.y(up), w0, h0, spin, PeepSky.tint(0.9f * seen, shade), false);
                p.sprite(petal, u, PeepEffects.y(up), w0, h0, spin, PeepSky.tint(0.1f * seen, 0xFFFFFF), true);
            }
        }
    }

    static void heart(int k, float time, float[] out) {
        float h = PeepSky.hash01(k * 11 + 1);
        float period = 10.0f + 5.0f * h;
        float local = time / period + PeepSky.hash01(k * 11 + 2);
        int n = class_3532.method_15375((float)local);
        float t = local - (float)n;
        boolean cracks = PeepSky.hash01(k, n, 5) < 0.18f;
        out[1] = class_3532.method_16439((float)t, (float)76.0f, (float)3.0f);
        out[0] = PeepEffects.spread(PeepSky.hash01(k, n, 13)) + PeepEffects.du(2.4f * class_3532.method_15374((double)(time * 0.6f + (float)k * 1.7f)), out[1]);
        out[2] = t * period;
        out[3] = cracks ? out[2] - (0.26f + 0.3f * PeepSky.hash01(k, n, 6)) * period : Float.NaN;
        out[4] = (2.8f + 1.8f * PeepSky.hash01(k * 11 + 3)) * (cracks ? 1.35f : 1.0f);
        out[5] = t;
        out[6] = n;
    }

    private static void hearts(PeepSky s, SkyPaint p, float w, float time) {
        int k;
        float[] at = SCRATCH;
        for (int pass = 0; pass < 2; ++pass) {
            SkyImage half = s.art[pass == 0 ? 3 : 4];
            for (k = 0; k < 20; ++k) {
                float keep;
                boolean broken;
                PeepEffects.heart(k, time, at);
                float u = s.anchor(53 + k, (int)at[6]) + at[0];
                float up = at[1];
                float after = at[3];
                float tall = at[4];
                float wide = tall * 128.0f / 120.0f;
                float t = at[5];
                wide /= 1.0f + 0.35f * PeepSky.smooth(15.0f, 40.0f, Math.abs(PeepSky.wrapDelta(u - s.aimU)) * 360.0f * class_3532.method_15362((double)(up * ((float)Math.PI / 180))));
                boolean bl = broken = !Float.isNaN(after) && after >= 0.0f;
                if (!broken && pass == 1) continue;
                float side = broken ? (pass == 0 ? -1.0f : 1.0f) : 0.0f;
                float spin = 0.35f * class_3532.method_15374((double)(time * 0.5f + (float)k));
                float fade = PeepSky.smooth(0.0f, 0.07f, t) * PeepSky.smooth(1.0f, 8.0f, up) * w;
                float open = 0.0f;
                float apart = 0.0f;
                if (!Float.isNaN(after) && after > -0.4f) {
                    if (after < 0.0f) {
                        u += PeepEffects.du(PeepSky.smooth(-0.4f, 0.0f, after) * 0.15f * class_3532.method_15374((double)(time * 37.0f + (float)k)), up);
                    } else {
                        float split = PeepSky.smootherstep(Math.min(1.0f, after / 1.8f));
                        open = 0.55f * split;
                        apart = (0.04f + 0.3f * split) * wide;
                        up -= 2.5f * split * split;
                        fade *= 1.0f - PeepSky.smooth(0.8f, 2.2f, after);
                    }
                }
                if ((fade *= (keep = PeepEffects.clearAround(s, u, up) * s.clearOfTattoos(u, up, tall * 0.45f)) * keep) <= 0.003f) continue;
                float angle = spin + side * open;
                float reach = 0.33f * tall;
                float tipUp = up - reach;
                float across = reach * class_3532.method_15374((double)angle) + side * apart;
                float down = -reach * class_3532.method_15362((double)angle);
                float centreUp = tipUp - down;
                float centreU = u + PeepEffects.du(across, centreUp);
                if (pass == 0) {
                    p.glow(u, PeepEffects.y(up), PeepEffects.units(tall * 0.95f), 16732080, 0.12f * fade);
                }
                p.sprite(broken ? half : s.art[13], centreU, PeepEffects.y(centreUp), PeepEffects.units(wide), PeepEffects.units(tall), angle, PeepSky.tint(fade * PeepSky.smooth(0.12f, 0.45f, fade), k % 3 == 0 ? 16747208 : 16759014), true);
            }
        }
        SkyImage twinkle = s.art[8];
        for (int pass = 0; pass < 2; ++pass) {
            for (k = 0; k < 20; ++k) {
                PeepEffects.heart(k, time, at);
                float after = at[3];
                if (Float.isNaN(after) || after < 0.0f || after > 0.8f) continue;
                float u = s.anchor(53 + k, (int)at[6]) + at[0];
                float glint = PeepSky.smooth(0.0f, 0.12f, after) * (1.0f - PeepSky.smooth(0.2f, 0.8f, after)) * w * PeepEffects.clearAround(s, u, at[1]);
                if (glint <= 0.003f) continue;
                if (pass == 0) {
                    p.glow(u, PeepEffects.y(at[1]), PeepEffects.units(at[4] * 1.2f), 16732080, 0.3f * glint);
                    continue;
                }
                p.sprite(twinkle, u, PeepEffects.y(at[1]), PeepEffects.units(at[4] * 2.6f), PeepEffects.units(at[4] * 2.6f), 0.3f, PeepSky.tint(0.8f * glint, 16766704), true);
            }
        }
    }

    private static void feathers(PeepSky s, SkyPaint p, float w, float time) {
        SkyImage feather = s.art[5];
        for (int pass = 0; pass < 2; ++pass) {
            for (int k = 0; k < 22; ++k) {
                float h = PeepSky.hash01(k * 13 + 1);
                float period = 30.0f + 10.0f * h;
                float local = time / period + PeepSky.hash01(k * 13 + 2);
                int n = class_3532.method_15375((float)local);
                float t = local - (float)n;
                float near = PeepSky.hash01(k * 13 + 3);
                float swing = time * 0.75f * (0.85f + 0.3f * h) + (float)k * 2.0f;
                float up = class_3532.method_16439((float)t, (float)78.0f, (float)4.0f) - 0.8f * class_3532.method_15379((float)class_3532.method_15374((double)swing));
                float u = PeepEffects.around(s, 245 + k, n, PeepSky.hash01(k, n, 17)) + PeepEffects.du(4.5f * class_3532.method_15374((double)swing), up);
                float tall = (4.0f + 2.0f * near) * 1.1f;
                float fade = PeepSky.smooth(0.0f, 0.08f, t) * PeepSky.smooth(2.0f, 10.0f, up) * w * PeepEffects.clearAround(s, u, up) * s.clearOfTattoos(u, up, tall * 0.25f);
                if (fade <= 0.003f) continue;
                if (pass == 0) {
                    p.glow(u, PeepEffects.y(up), PeepEffects.units(tall * 0.5f), 16732080, 0.22f * fade);
                    continue;
                }
                float spin = 0.5f * class_3532.method_15362((double)swing) + (PeepSky.hash01(k * 13 + 4) - 0.5f) * 0.6f;
                int color = PeepSky.mix(16756446, 14696602, 1.0f - fade);
                p.sprite(feather, u, PeepEffects.y(up), PeepEffects.units(tall * 0.28846154f), PeepEffects.units(tall), spin, PeepSky.tint(fade, color), true);
            }
        }
    }

    private static void sparks(PeepSky s, SkyPaint p, float w, float time) {
        SkyImage streak = s.art[0];
        for (int pass = 0; pass < 2; ++pass) {
            for (int k = 0; k < 135; ++k) {
                float h = PeepSky.hash01(k * 17 + 1);
                float period = 3.5f + 2.5f * h;
                float local = time / period + PeepSky.hash01(k * 17 + 2);
                int n = class_3532.method_15375((float)local);
                float t = local - (float)n;
                float near = PeepSky.hash01(k * 17 + 3);
                float top = 6.0f + 18.0f * PeepSky.hash01(k, n, 4);
                float up = -1.0f + (top + 1.0f) * (1.0f - (float)Math.pow(1.0f - t, 2.2));
                float swayPhase = time * (1.1f + 0.6f * h) + (float)k;
                float u = PeepSky.hash01(k, n, 19) + PeepEffects.du(1.8f * class_3532.method_15374((double)swayPhase) * t, up);
                float flicker = 0.65f + 0.35f * (0.5f + 0.5f * class_3532.method_15374((double)(time * (13.0f + 12.0f * h) + (float)k * 2.3f)));
                float fade = PeepSky.smooth(0.03f, 0.12f, t) * (1.0f - PeepSky.smooth(0.3f, 0.85f, t)) * flicker * w * PeepEffects.clearAround(s, u, up);
                if (fade <= 0.003f) continue;
                float size = 0.4f + 0.5f * near;
                int color = PeepSky.mix(PeepSky.mix(16751206, 16727150, PeepSky.smooth(0.1f, 0.5f, t)), 12720235, PeepSky.smooth(0.5f, 1.0f, t));
                float lean = 1.8f * class_3532.method_15362((double)swayPhase) * (1.1f + 0.6f * h) * t / Math.max(1.0f, top);
                if (pass == 0) {
                    float trail = 3.5f + 2.5f * near;
                    p.sprite(streak, u, PeepEffects.y(up - trail * 0.4f), PeepEffects.units(trail), PeepEffects.units(size * 0.7f), -1.5707964f + lean, PeepSky.tint(0.45f * fade, color), true);
                    continue;
                }
                p.glow(u, PeepEffects.y(up), PeepEffects.units(size * 1.2f), color, 0.58f * fade);
                p.glow(u, PeepEffects.y(up), PeepEffects.units(size * 0.7f), 16763024, 0.4f * fade * (1.0f - PeepSky.smooth(0.15f, 0.45f, t)));
                p.sprite(s.art[1], u, PeepEffects.y(up - size * 0.45f), PeepEffects.units(size * 0.5f), PeepEffects.units(size * 1.8f), (float)Math.PI + lean, PeepSky.tint(0.75f * fade, 16762010), true);
            }
        }
    }

    private static void ghosts(PeepSky s, SkyPaint p, float w, float time) {
        SkyImage wisp = s.art[6];
        for (int pass = 0; pass < 2; ++pass) {
            for (int k = 0; k < 21; ++k) {
                float h = PeepSky.hash01(k * 19 + 1);
                float period = 10.0f + 4.0f * h;
                float local = time / period + PeepSky.hash01(k * 19 + 2);
                int n = class_3532.method_15375((float)local);
                float t = local - (float)n;
                float near = PeepSky.hash01(k * 19 + 3);
                float headUp = class_3532.method_16439((float)((float)Math.pow(t, 0.9)), (float)-4.0f, (float)58.0f);
                float sway = time * 0.45f + (float)k * 1.9f;
                float grow = 0.85f + 0.35f * t;
                float tall = (8.0f + 5.0f * near) * grow;
                float wide = tall * 96.0f / 256.0f * 1.8f;
                float centreUp = headUp - tall * 0.3f;
                float u = PeepEffects.around(s, 73 + k, n, PeepSky.hash01(k, n, 23)) + PeepEffects.du(3.6f * class_3532.method_15374((double)sway), centreUp);
                float fade = PeepSky.smooth(0.0f, 0.12f, t) * (1.0f - PeepSky.smooth(0.55f, 1.0f, t)) * PeepSky.smooth(1.0f, 12.0f, headUp) * w * PeepEffects.clearAround(s, u, headUp) * PeepEffects.clearAround(s, u, centreUp);
                if (fade <= 0.003f) continue;
                if (pass == 0) {
                    p.sprite(wisp, u, PeepEffects.y(centreUp), PeepEffects.units(wide), PeepEffects.units(tall), 0.32f * class_3532.method_15362((double)sway), PeepSky.tint(0.42f * fade, 16750300), true);
                    continue;
                }
                float lean = 0.32f * class_3532.method_15362((double)sway);
                float reach = tall * 0.3f;
                float glowUp = centreUp + reach * class_3532.method_15362((double)lean);
                p.glow(u + PeepEffects.du(reach * class_3532.method_15374((double)lean), glowUp), PeepEffects.y(glowUp), PeepEffects.units(2.0f), 16759020, 0.24f * fade);
            }
        }
    }

    private static void spirits(PeepSky s, SkyPaint p, float w, float time) {
        for (int k = 0; k < 12; ++k) {
            float h = PeepSky.hash01(k * 31 + 1);
            float period = 11.0f + 6.0f * h;
            float local = time / period + PeepSky.hash01(k * 31 + 2);
            int n = class_3532.method_15375((float)local);
            float t = local - (float)n;
            float up = class_3532.method_16439((float)((float)Math.pow(t, 0.85)), (float)1.0f, (float)(26.0f + 10.0f * PeepSky.hash01(k, n, 3)));
            float sway = time * (0.4f + 0.2f * h) + (float)k * 2.3f;
            float u = PeepEffects.around(s, 233 + k, n, PeepSky.hash01(k, n, 2)) + PeepEffects.du(2.2f * class_3532.method_15374((double)sway), up);
            float flicker = 0.8f + 0.2f * class_3532.method_15374((double)(time * (1.3f + 0.6f * h) + (float)k));
            float fade = PeepSky.smooth(0.0f, 0.15f, t) * (1.0f - PeepSky.smooth(0.5f, 1.0f, t)) * flicker * w * PeepEffects.clearAround(s, u, up);
            if (fade <= 0.003f) continue;
            float size = 0.7f + 0.5f * PeepSky.hash01(k * 31 + 4);
            p.glow(u, PeepEffects.y(up), PeepEffects.units(size * 6.0f), 10258144, 0.12f * fade);
            p.glow(u, PeepEffects.y(up), PeepEffects.units(size * 2.0f), 14208255, 0.3f * fade);
            p.glow(u, PeepEffects.y(up), PeepEffects.units(size * 0.7f), 0xF6F2FF, 0.6f * fade * PeepEffects.core(s, PeepEffects.y(up)));
        }
    }

    private static void motes(PeepSky s, SkyPaint p, float w, float time) {
        float beat = 1.0f + 0.5f * s.pulse;
        for (int k = 0; k < 55; ++k) {
            float h = PeepSky.hash01(k * 23 + 1);
            float period = 9.0f + 6.0f * h;
            float local = time / period + PeepSky.hash01(k * 23 + 2);
            int n = class_3532.method_15375((float)local);
            float t = local - (float)n;
            float up = 4.0f + 56.0f * PeepSky.hash01(k, n, 3) + 3.0f * t + 1.5f * class_3532.method_15374((double)(time * 0.15f + (float)k * 1.3f));
            float u = PeepEffects.around(s, 94 + k, n, PeepSky.hash01(k, n, 2)) + PeepEffects.du(1.2f * class_3532.method_15374((double)(time * 0.11f + (float)k)), up);
            float twinkle = 0.55f + 0.45f * class_3532.method_15374((double)(time * (0.8f + 0.5f * h) + (float)k));
            float fade = twinkle * PeepSky.smooth(0.0f, 0.2f, t) * (1.0f - PeepSky.smooth(0.75f, 1.0f, t)) * w * PeepEffects.clearAround(s, u, up) * beat;
            if (fade <= 0.003f) continue;
            float size = (0.6f + 0.72f * PeepSky.hash01(k * 23 + 4)) * (k % 2 == 0 ? 1.3f : 1.0f);
            int color = k % 2 == 0 ? 16762714 : (k % 4 == 1 ? 16752324 : 16765050);
            p.glow(u, PeepEffects.y(up), PeepEffects.units(size * 4.0f), color, 0.2f * fade);
            p.glow(u, PeepEffects.y(up), PeepEffects.units(size * 1.4f), color, 0.35f * fade);
            p.glow(u, PeepEffects.y(up), PeepEffects.units(size * 0.6f), color, 0.42f * fade * PeepEffects.core(s, PeepEffects.y(up)));
        }
    }

    private static void glitter(PeepSky s, SkyPaint p, float w, float time) {
        SkyImage glint = s.art[16];
        for (int pass = 0; pass < 2; ++pass) {
            for (int k = 0; k < 84; ++k) {
                boolean pink;
                int n;
                float h = PeepSky.hash01(k * 29 + 1);
                float phase = (time * (0.9f + 0.9f * h) + (float)k * 2.7f) / ((float)Math.PI * 2);
                float t = phase - (float)(n = class_3532.method_15375((float)phase));
                if (t >= 0.5f) continue;
                float sparkle = (float)Math.pow(class_3532.method_15374((double)((float)Math.PI * (float)Math.pow(t * 2.0f, 0.6))), 2.0);
                float up = 5.0f + 60.0f * PeepSky.hash01(k, n, 3) + 1.5f * class_3532.method_15374((double)(time * 0.2f + (float)k));
                float u = PeepEffects.around(s, 149 + k, n, PeepSky.hash01(k, n, 2)) + PeepEffects.du(0.6f * class_3532.method_15374((double)(time * 0.1f + (float)k)), up);
                float fade = PeepSky.smooth(0.05f, 0.3f, sparkle) * w * PeepEffects.clearAround(s, u, up);
                if (fade <= 0.003f) continue;
                float size = (1.3f + 1.2f * PeepSky.hash01(k * 29 + 4)) * (0.25f + 0.75f * sparkle);
                boolean bl = pink = k % 4 == 3;
                int color = pink ? 16747213 : (k % 2 == 0 ? 16769952 : 16771504);
                float low = (1.0f - 0.35f * PeepSky.smooth(14.0f, 4.0f, up)) * PeepEffects.core(s, PeepEffects.y(up));
                if (pass == 0) {
                    p.glow(u, PeepEffects.y(up), PeepEffects.units(size * 0.9f), pink ? 16732080 : 16758858, 0.28f * fade * sparkle);
                    continue;
                }
                p.sprite(glint, u, PeepEffects.y(up), PeepEffects.units(size), PeepEffects.units(size), 0.4f * class_3532.method_15374((double)(time * 0.3f + (float)k)), PeepSky.tint((pink ? 0.65f : 0.8f) * fade * low, color), true);
            }
        }
    }

    static void orbits(PeepSky s, SkyPaint p, float time, float w) {
        SkyImage arc = s.art[9];
        float cap = PeepEffects.units(18.0f);
        for (int k = 0; k < s.shownTattoos; ++k) {
            float size = Math.min(cap, (s.tw[k] + s.th[k]) * 0.5f);
            float a = s.tGlow[k] * (0.6f + 0.4f * s.tGlow[k]) * PeepSky.smooth(0.3f, 0.5f, s.tGlow[k]) * w;
            if (a <= 0.003f) continue;
            float inner = time * 0.55f + (float)k * 1.3f;
            float outer = time * 0.33f + (float)k * 2.1f + 2.0f;
            p.sprite(arc, s.tu[k], s.ty[k], size * 1.35f, size * 1.35f, inner, PeepSky.tint(0.7f * a, 13995263), true);
            p.sprite(arc, s.tu[k], s.ty[k], size * 1.6f, size * 1.6f, outer, PeepSky.tint(0.5f * a, 16748512), true);
            PeepEffects.arcHead(s, p, k, size * 1.35f, inner, 0.5f * a);
            PeepEffects.arcHead(s, p, k, size * 1.6f, outer, 0.35f * a);
        }
    }

    private static void arcHead(PeepSky s, SkyPaint p, int k, float size, float spin, float alpha) {
        float radius = size * 0.421875f;
        float up = (0.72f - s.ty[k]) * 1.0471976f * 57.295776f;
        float u = s.tu[k] + PeepEffects.du(radius * class_3532.method_15362((double)spin) * 60.0f, up);
        float headY = s.ty[k] + radius * class_3532.method_15374((double)spin);
        p.glow(u, headY, PeepEffects.units(3.0f), 16732080, 0.3f * alpha);
        p.glow(u, headY, PeepEffects.units(1.8f), 16748512, 0.3f * alpha);
        p.glow(u, headY, PeepEffects.units(0.8f), 16766704, alpha);
    }

    static void goldSparkles(PeepSky s, SkyPaint p, float time, float w) {
        SkyImage twinkle = s.art[8];
        for (int pass = 0; pass < 2; ++pass) {
            for (int k = 0; k < s.shownTattoos; ++k) {
                float a = PeepSky.smooth(0.15f, 0.6f, s.tGlow[k]) * w;
                if (a <= 0.003f) continue;
                for (int j = 0; j < 3; ++j) {
                    float angle = (float)j * 2.1f + (float)k + time * 0.15f;
                    float sparkle = (float)Math.pow(Math.max(0.0f, class_3532.method_15374((double)(time * (1.1f + 0.3f * (float)j) + (float)k * 1.9f + (float)j * 2.2f))), 4.0);
                    if (sparkle <= 0.03f) continue;
                    float u = s.tu[k] + class_3532.method_15362((double)angle) * s.tw[k] * 0.5f * 60.0f / (360.0f * Math.max(0.2f, class_3532.method_15362((double)((0.72f - s.ty[k]) * 1.0471976f))));
                    float y = s.ty[k] + class_3532.method_15374((double)angle) * s.th[k] * 0.55f;
                    float size = Math.min(s.th[k] * 0.6f, PeepEffects.units(7.0f)) * (0.2f + 0.8f * sparkle);
                    float shine = a * PeepSky.smooth(0.03f, 0.25f, sparkle);
                    if (pass == 0) {
                        p.glow(u, y, size * 0.35f, 16758858, 0.25f * shine * sparkle);
                        continue;
                    }
                    p.sprite(twinkle, u, y, size, size, time * 0.4f + (float)j, PeepSky.tint(shine, j == 1 ? 16774354 : 16770720), true);
                }
            }
        }
    }

    static float spread(float h) {
        return h < 0.8f ? (h / 0.8f - 0.5f) * 0.375f : (h - 0.8f) / 0.19999999f;
    }

    private static float around(PeepSky s, int slot, int run, float h) {
        return s.anchor(slot, run) + PeepEffects.spread(h);
    }

    private static void horizonLine(SkyPaint p, float peak, float top, int rgb, float alpha) {
        if (alpha <= 0.003f) {
            return;
        }
        int glow = PeepSky.tint(alpha, rgb);
        int none = PeepSky.tint(0.0f, rgb);
        float high = PeepEffects.y(top);
        float middle = PeepEffects.y(peak);
        float below = PeepEffects.y(-1.0f);
        for (int i = 0; i < 48; ++i) {
            float u0 = (float)i / 48.0f;
            float u1 = (float)(i + 1) / 48.0f;
            p.gradient(true, u0, u1, high, middle, none, glow);
            p.gradient(true, u0, u1, middle, below, glow, none);
        }
    }

    private static void mist(SkyPaint p, SkyImage mist, int repeats, float drift, float lowUp, float highUp, int rgb, float alpha, boolean additive) {
        if (alpha <= 0.003f) {
            return;
        }
        float top = PeepEffects.y(highUp);
        float bottom = PeepEffects.y(lowUp);
        float offset = PeepSky.wrap(drift);
        int color = PeepSky.tint(alpha, rgb);
        for (int i = 0; i < repeats; ++i) {
            float u0 = ((float)i + offset) / (float)repeats;
            p.band(mist, u0, u0 + 1.0f / (float)repeats, top, bottom, color, additive, 24);
        }
    }

    private static void beam(SkyPaint p, SkyImage beam, float baseU, float baseUp, float tilt, float length, float width, int rgb, float alpha) {
        PeepEffects.beam(p, beam, baseU, baseUp, tilt, length, width, rgb, alpha, true);
    }

    private static void beam(SkyPaint p, SkyImage beam, float baseU, float baseUp, float tilt, float length, float width, int rgb, float alpha, boolean additive) {
        if (alpha <= 0.003f) {
            return;
        }
        PeepEffects.arc(baseU, baseUp, tilt);
        float spin = PeepEffects.onArc(length * 0.5f, 0.0f);
        p.sprite(beam, AT[0], PeepEffects.y(AT[1]), PeepEffects.units(width), PeepEffects.units(length), spin, PeepSky.tint(alpha, rgb), additive);
    }

    private static void arc(float baseU, float baseUp, float tilt) {
        float a = baseU * ((float)Math.PI * 2);
        float e = baseUp * ((float)Math.PI / 180);
        float ca = class_3532.method_15362((double)a);
        float sa = class_3532.method_15374((double)a);
        float ce = class_3532.method_15362((double)e);
        float se = class_3532.method_15374((double)e);
        float ct = class_3532.method_15362((double)tilt);
        float st = class_3532.method_15374((double)tilt);
        PeepEffects.ARC[0] = ce * ca;
        PeepEffects.ARC[1] = ce * sa;
        PeepEffects.ARC[2] = se;
        PeepEffects.ARC[3] = -ct * se * ca - st * sa;
        PeepEffects.ARC[4] = -ct * se * sa + st * ca;
        PeepEffects.ARC[5] = ct * ce;
    }

    private static float onArc(float along, float across) {
        float d = along * ((float)Math.PI / 180);
        float cd = class_3532.method_15362((double)d);
        float sd = class_3532.method_15374((double)d);
        float px = cd * ARC[0] + sd * ARC[3];
        float py = cd * ARC[1] + sd * ARC[4];
        float pz = cd * ARC[2] + sd * ARC[5];
        float tx = cd * ARC[3] - sd * ARC[0];
        float ty = cd * ARC[4] - sd * ARC[1];
        float tz = cd * ARC[5] - sd * ARC[2];
        if (across != 0.0f) {
            float c = across * ((float)Math.PI / 180);
            float cc = class_3532.method_15362((double)c);
            float sc = class_3532.method_15374((double)c);
            float nx = ty * pz - tz * py;
            float ny = tz * px - tx * pz;
            float nz = tx * py - ty * px;
            px = cc * px + sc * nx;
            py = cc * py + sc * ny;
            pz = cc * pz + sc * nz;
        }
        PeepEffects.ARC[6] = px;
        PeepEffects.ARC[7] = py;
        PeepEffects.ARC[8] = pz;
        float a = (float)Math.atan2(py, px);
        float e = (float)Math.asin(class_3532.method_15363((float)pz, (float)-1.0f, (float)1.0f));
        PeepEffects.AT[0] = PeepSky.wrap(a / ((float)Math.PI * 2));
        PeepEffects.AT[1] = e * 57.295776f;
        float ca = class_3532.method_15362((double)a);
        float sa = class_3532.method_15374((double)a);
        float se = class_3532.method_15374((double)e);
        float ce = class_3532.method_15362((double)e);
        float up = -tx * se * ca - ty * se * sa + tz * ce;
        float right = -tx * sa + ty * ca;
        return (float)Math.atan2(right, up);
    }

    private static float alongArc(float u, float up) {
        float a = u * ((float)Math.PI * 2);
        float e = up * ((float)Math.PI / 180);
        float ce = class_3532.method_15362((double)e);
        float x = ce * class_3532.method_15362((double)a);
        float y = ce * class_3532.method_15374((double)a);
        float z = class_3532.method_15374((double)e);
        return (float)Math.atan2(x * ARC[3] + y * ARC[4] + z * ARC[5], x * ARC[0] + y * ARC[1] + z * ARC[2]) * 57.295776f;
    }

    private static float acrossArc(float u, float up) {
        float a = u * ((float)Math.PI * 2);
        float e = up * ((float)Math.PI / 180);
        float ce = class_3532.method_15362((double)e);
        float x = ce * class_3532.method_15362((double)a);
        float y = ce * class_3532.method_15374((double)a);
        float z = class_3532.method_15374((double)e);
        float nx = ARC[4] * ARC[2] - ARC[5] * ARC[1];
        float ny = ARC[5] * ARC[0] - ARC[3] * ARC[2];
        float nz = ARC[3] * ARC[1] - ARC[4] * ARC[0];
        return (float)Math.asin(class_3532.method_15363((float)Math.abs(x * nx + y * ny + z * nz), (float)0.0f, (float)1.0f)) * 57.295776f;
    }

    private static float beamClear(PeepSky s, float baseU, float baseUp, float tilt, float length, float width) {
        PeepEffects.arc(baseU, baseUp, tilt);
        float along = class_3532.method_15363((float)PeepEffects.alongArc(s.aimU, s.viewUp), (float)0.0f, (float)length);
        PeepEffects.onArc(along, 0.0f);
        float a = s.aimU * ((float)Math.PI * 2);
        float e = s.viewUp * ((float)Math.PI / 180);
        float ce = class_3532.method_15362((double)e);
        float dot = ce * class_3532.method_15362((double)a) * ARC[6] + ce * class_3532.method_15374((double)a) * ARC[7] + class_3532.method_15374((double)e) * ARC[8];
        float gap = (float)Math.acos(class_3532.method_15363((float)dot, (float)-1.0f, (float)1.0f)) * 57.295776f - width * (0.025f + 0.42f * along / length);
        float text = 0.0f;
        for (int i = 1; i <= 8; ++i) {
            float at = length * (float)i / 9.0f;
            float half = width * (0.025f + 0.42f * at / length) * 0.5f;
            PeepEffects.onArc(at, 0.0f);
            text = Math.max(text, s.lyricBox(AT[0], AT[1], half, half));
        }
        return (0.35f + 0.65f * PeepSky.smooth(3.0f, 12.0f, gap)) * (1.0f - 0.45f * text);
    }

    private static float clear(PeepSky s, float u, float up) {
        return s.clear(u, up);
    }

    private static float core(PeepSky s, float y) {
        return 1.0f - 0.4f * s.hazeAt(y);
    }

    private static float clearAround(PeepSky s, float u, float up) {
        return s.clear(u, up);
    }

    private static float y(float up) {
        return PeepSky.elevationToY(up);
    }

    private static float units(float degrees) {
        return degrees * ((float)Math.PI / 180) / 1.0471976f;
    }

    private static float du(float degrees, float up) {
        return degrees / (360.0f * Math.max(0.2f, class_3532.method_15362((double)(up * ((float)Math.PI / 180)))));
    }
}

