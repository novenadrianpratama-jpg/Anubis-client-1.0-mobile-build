/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector4f
 */
package net.anubis.client.module.impl.render;

import net.anubis.client.gui.ThemeTint;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector4f;

@Environment(value=EnvType.CLIENT)
public final class PeepWorld {
    private static final float PINK_R = 1.0f;
    private static final float PINK_G = 0.30980393f;
    private static final float PINK_B = 0.6901961f;
    private static final float PINK_LUMA = PeepWorld.luma(1.0f, 0.30980393f, 0.6901961f);
    private static final long HORIZON_STALE_NANOS = 250000000L;
    private static volatile int horizon;
    private static volatile float horizonShare;
    private static volatile long horizonAt;

    private PeepWorld() {
    }

    static void horizon(int rgb, float share) {
        horizon = rgb;
        horizonShare = share;
        horizonAt = System.nanoTime();
    }

    public static void fog(Vector4f color) {
        float share = horizonShare;
        if (share > 0.0f && System.nanoTime() - horizonAt > 250000000L) {
            share = 0.0f;
        }
        PeepWorld.fog(color, horizon, share);
    }

    private static void fog(Vector4f color, int horizon, float share) {
        float level = ThemeTint.world();
        if (level > 0.0f) {
            float scale = PeepWorld.luma(color.x, color.y, color.z) / PINK_LUMA;
            color.x += (Math.min(1.0f, 1.0f * scale) - color.x) * level;
            color.y += (Math.min(1.0f, 0.30980393f * scale) - color.y) * level;
            color.z += (Math.min(1.0f, 0.6901961f * scale) - color.z) * level;
        }
        if (share > 0.0f) {
            color.x += ((float)(horizon >> 16 & 0xFF) / 255.0f - color.x) * share;
            color.y += ((float)(horizon >> 8 & 0xFF) / 255.0f - color.y) * share;
            color.z += ((float)(horizon & 0xFF) / 255.0f - color.z) * share;
        }
    }

    static int fog(int rgb) {
        return PeepWorld.fog(rgb, 0, 0.0f);
    }

    static int fog(int rgb, int horizon, float share) {
        Vector4f color = new Vector4f((float)(rgb >> 16 & 0xFF) / 255.0f, (float)(rgb >> 8 & 0xFF) / 255.0f, (float)(rgb & 0xFF) / 255.0f, 1.0f);
        PeepWorld.fog(color, horizon, share);
        return PeepWorld.channel(color.x) << 16 | PeepWorld.channel(color.y) << 8 | PeepWorld.channel(color.z);
    }

    private static float luma(float r, float g, float b) {
        return 0.2126f * r + 0.7152f * g + 0.0722f * b;
    }

    private static int channel(float value) {
        return Math.max(0, Math.min(255, Math.round(value * 255.0f)));
    }
}

