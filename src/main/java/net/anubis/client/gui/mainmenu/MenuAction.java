/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.gui.mainmenu;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum MenuAction {
    SINGLEPLAYER("Singleplayer"),
    MULTIPLAYER("Multiplayer"),
    REALMS("Realms"),
    OPTIONS("Options"),
    QUIT("Quit"),
    LANGUAGE("Language"),
    ACCESSIBILITY("Accessibility"),
    ANUBIS("Anubis Client"),
    MODS("Mods Folder"),
    ACCOUNTS("Accounts"),
    CREDITS("Credits");

    public static final List<MenuAction> MAIN;
    private final String label;

    private MenuAction(String label) {
        this.label = label;
    }

    public String label() {
        return this.label;
    }

    static {
        MAIN = List.of(SINGLEPLAYER, MULTIPLAYER, REALMS, OPTIONS, QUIT);
    }
}

