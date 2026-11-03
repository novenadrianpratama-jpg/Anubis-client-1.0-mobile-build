/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.gui;

import net.anubis.client.config.Surface;
import net.anubis.client.config.ThemeStore;
import net.anubis.client.gui.AnubisTheme;
import net.anubis.client.gui.ThemeTint;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class GuiPalette {
    private static int accent = -624588;
    private static int accentHover = -486564;
    private static int panelBg = -349691361;
    private static int cardBg = -149022441;
    private static int tabBarBg = -149022441;
    private static float rounding = 10.0f;
    private static float animationSpeed = 1.0f;

    private GuiPalette() {
    }

    public static void refresh() {
        ThemeStore theme = ThemeStore.get();
        accent = GuiPalette.abgr(ThemeTint.accent(theme.accent.argbWithAlpha(255)));
        accentHover = AnubisTheme.lerpColor(accent, -1, 0.2f);
        panelBg = GuiPalette.fill(theme.panels);
        cardBg = GuiPalette.fill(theme.cards);
        tabBarBg = GuiPalette.fill(theme.tabBar);
        rounding = theme.rounding.get();
        animationSpeed = theme.animationSpeed.get();
    }

    public static int accent() {
        return accent;
    }

    public static int accentHover() {
        return accentHover;
    }

    public static int panelBg() {
        return panelBg;
    }

    public static int cardBg() {
        return cardBg;
    }

    public static int tabBarBg() {
        return tabBarBg;
    }

    public static int fill(Surface surface) {
        return GuiPalette.abgr(ThemeTint.surface(surface.color.argbWithAlpha(Math.round(surface.opacity.get() * 255.0f))));
    }

    public static float rounding() {
        return rounding;
    }

    public static float animationSpeed() {
        return animationSpeed;
    }

    public static int abgr(int argb) {
        return argb & 0xFF00FF00 | argb >> 16 & 0xFF | (argb & 0xFF) << 16;
    }
}

