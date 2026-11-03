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
import java.util.List;
import net.anubis.client.gui.EgyptDraw;
import net.anubis.client.gui.mainmenu.DuskEmblem;
import net.anubis.client.gui.mainmenu.DuskPill;
import net.anubis.client.gui.mainmenu.DuskScene;
import net.anubis.client.gui.mainmenu.MenuAction;
import net.anubis.client.gui.mainmenu.MenuContext;
import net.anubis.client.gui.mainmenu.MenuIcons;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class DuskMenu {
    private static final float SUN_R = 150.0f;
    private static final float HEAD_SIZE = 1.7f;
    private static final float NAME_GAP = 26.0f;
    private static final float ITEMS_GAP = 34.0f;
    private static final float ROW_H = 56.0f;
    private static final float QUIT_GAP = 12.0f;
    private static final float BUTTON_W = 320.0f;
    private static final float BUTTON_H = 46.0f;
    private static final float ITEM_TRACK = 2.0f;
    private static final List<MenuAction> ICONS = List.of(MenuAction.ACCOUNTS, MenuAction.LANGUAGE, MenuAction.ACCESSIBILITY, MenuAction.ANUBIS, MenuAction.MODS, MenuAction.CREDITS);
    private static final List<MenuAction> ICONS_WITHOUT_ACCOUNTS = ICONS.subList(1, ICONS.size());
    private static final float ICON_R = 9.0f;
    private static final float ICON_STEP = 34.0f;
    private static final float ICON_MARGIN = 26.0f;
    private static final int ITEM = -923950;
    private static final float ITEM_ALPHA = 0.85f;
    private static final int ICON = -4676216;

    private DuskMenu() {
    }

    public static Layout layout(float width, float height, float s) {
        float sunR = 150.0f * s;
        float nameH = 20.0f * s;
        float items = (float)MenuAction.MAIN.size() * 56.0f * s + 12.0f * s;
        float column = 2.0f * sunR + 26.0f * s + nameH + 34.0f * s + items;
        float top = Math.max(48.0f * s, DuskScene.horizon(height) - column);
        float sunY = top + sunR;
        float nameY = sunY + sunR + 26.0f * s;
        return new Layout(width * 0.5f, sunY, sunR, nameY, nameY + nameH + 34.0f * s);
    }

    public static void render(MenuContext c) {
        ImDrawList dl = c.bg();
        float sw = c.width();
        float sh = c.height();
        float s = c.scale();
        Layout l = DuskMenu.layout(sw, sh, s);
        DuskScene.draw(dl, sw, sh, s, c.time(), DuskScene.horizon(sh), l.cx(), l.sunY(), l.sunR(), c.lookX(), c.lookY());
        DuskEmblem.draw(c, l.cx(), l.sunY(), l.sunR() * 1.7f, l.nameY());
        float y = l.itemsY();
        for (int i = 0; i < MenuAction.MAIN.size(); ++i) {
            MenuAction action = MenuAction.MAIN.get(i);
            if (action == MenuAction.QUIT) {
                y += 12.0f * s;
            }
            DuskMenu.item(c, action, i, l.cx(), y, c.intro(0.4f + (float)i * 0.05f));
            y += 56.0f * s;
        }
        float a = c.intro(0.7f);
        float iconY = sh - 26.0f * s;
        List<MenuAction> icons = c.accountsAvailable() ? ICONS : ICONS_WITHOUT_ACCOUNTS;
        float iconX = sw - 26.0f * s - (float)(icons.size() - 1) * 34.0f * s;
        for (int i = 0; i < icons.size(); ++i) {
            DuskMenu.icon(c, icons.get(i), iconX + (float)i * 34.0f * s, iconY, a);
        }
    }

    private static void item(MenuContext c, MenuAction action, int index, float cx, float y, float a) {
        ImDrawList dl = c.bg();
        ImFont font = c.itemFont();
        float s = c.scale();
        float w = 320.0f * s;
        float h = 46.0f * s;
        float x = cx - w * 0.5f;
        boolean enabled = c.enabled(action);
        float k = a > 0.5f ? c.control(action, x, y, w, h) : c.hover(action, false);
        DuskPill.frame(c, x, y, w, h, k, DuskEmblem.buttonPass(c, index), a);
        String label = action.label();
        float track = 2.0f * s;
        float ty = y + (h - c.textHeight(font)) * 0.5f + (1.0f - a) * 8.0f * s;
        float tx = cx - c.spacedWidth(font, label, track) * 0.5f;
        int color = enabled ? EgyptDraw.mix(-923950, -11654, k) : -7372954;
        float alpha = (action == MenuAction.QUIT ? 0.75f + 0.25f * k : 0.85f + 0.14999998f * k) * a;
        if (enabled) {
            DuskPill.halo(c, font, tx, ty, track, k * a, label);
        }
        c.spacedText(dl, font, tx + s, ty + 1.5f * s, track, -16777216, 0.55f * a, label);
        c.spacedText(dl, font, tx, ty, track, color, alpha, label);
    }

    private static void icon(MenuContext c, MenuAction action, float cx, float cy, float a) {
        float s = c.scale();
        float r = 9.0f * s;
        float reach = 34.0f * s * 0.5f;
        float k = a > 0.5f ? c.control(action, cx - reach, cy - reach, reach * 2.0f, reach * 2.0f) : c.hover(action, false);
        MenuIcons.draw(c.bg(), action, cx, cy, r, EgyptDraw.col(EgyptDraw.mix(-4676216, -11654, k), a), Math.max(1.2f, 1.5f * s));
        if (k > 0.5f) {
            String name = action == MenuAction.ACCOUNTS ? action.label() + "  \u00b7  " + c.displayName() : action.label();
            c.tooltip(name, Math.min(cx, c.width() - c.textWidth(c.tagFont(), name) * 0.5f - 18.0f * s), cy - reach - c.textHeight(c.tagFont()) - 14.0f * s);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Layout(float cx, float sunY, float sunR, float nameY, float itemsY) {
    }
}

