/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.gui.mainmenu;

import imgui.ImDrawList;
import imgui.ImFont;
import net.anubis.client.gui.EgyptDraw;
import net.anubis.client.gui.mainmenu.DuskEmblem;
import net.anubis.client.gui.mainmenu.MenuFrame;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
final class DuskPill {
    private static final float BAND = 0.2f;
    private static final float STRIP = 4.0f;

    private DuskPill() {
    }

    static void frame(MenuFrame c, float x, float y, float w, float h, float hover, float pass, float a) {
        ImDrawList dl = c.bg();
        float s = c.scale();
        float r = h * 0.5f;
        if (hover > 0.01f) {
            float glow = hover * DuskPill.glow(c) * a;
            DuskPill.stroke(dl, x, y, w, h, r, EgyptDraw.col(-871878, 0.1f * glow), 7.0f * s);
            DuskPill.stroke(dl, x, y, w, h, r, EgyptDraw.col(-871878, 0.18f * glow), 3.5f * s);
        }
        DuskPill.stroke(dl, x, y, w, h, r, EgyptDraw.col(-16777216, 0.2f * a), 3.0f * s);
        DuskPill.stroke(dl, x, y, w, h, r, EgyptDraw.col(EgyptDraw.mix(-871878, -11654, hover), (0.7f + 0.3f * hover) * a), Math.max(1.0f, s));
        if (pass >= 0.0f) {
            DuskPill.light(dl, x, y, w, h, r, s, pass, a);
        }
    }

    static void halo(MenuFrame c, ImFont font, float x, float y, float track, float hover, String label) {
        if (hover <= 0.01f) {
            return;
        }
        float s = c.scale();
        float amount = hover * DuskPill.glow(c);
        for (int ring = 1; ring <= 2; ++ring) {
            for (int i = 0; i < 8; ++i) {
                double angle = (double)i * Math.PI / 4.0;
                float d = (float)ring * 1.8f * s;
                c.spacedText(c.bg(), font, x + (float)Math.cos(angle) * d, y + (float)Math.sin(angle) * d, track, -871878, (ring == 1 ? 0.1f : 0.06f) * amount, label);
            }
        }
    }

    private static float glow(MenuFrame c) {
        return 0.7f + 0.3f * DuskEmblem.breath(c.time());
    }

    private static void light(ImDrawList dl, float x, float y, float w, float h, float r, float s, float pass, float a) {
        float half = w * 0.2f;
        float pad = 5.0f * s;
        float step = 4.0f * s;
        float centre = x - half + pass * (w + half * 2.0f);
        float start = centre - half;
        int strips = (int)Math.ceil(half * 2.0f / step);
        for (int i = 0; i < strips; ++i) {
            float x0 = Math.max((float)Math.round(start + (float)i * step), x - pad);
            float x1 = Math.min((float)Math.round(start + (float)(i + 1) * step), x + w + pad);
            float k = DuskEmblem.smooth(1.0f - Math.abs(start + ((float)i + 0.5f) * step - centre) / half);
            if (k < 0.02f || x1 <= x0) continue;
            dl.pushClipRect(x0, y - pad, x1, y + h + pad, true);
            DuskPill.stroke(dl, x, y, w, h, r, EgyptDraw.col(-871878, 0.26f * k * a), 8.0f * s);
            DuskPill.stroke(dl, x, y, w, h, r, EgyptDraw.col(-11654, 0.42f * k * a), 4.5f * s);
            DuskPill.stroke(dl, x, y, w, h, r, EgyptDraw.col(-3633, 0.95f * k * a), Math.max(2.0f, 2.0f * s));
            dl.popClipRect();
        }
    }

    private static void stroke(ImDrawList dl, float x, float y, float w, float h, float r, int color, float thickness) {
        dl.addRect(x, y, x + w, y + h, color, r, 0, thickness);
    }
}

