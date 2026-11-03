/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.easyplace.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum InventoryPickSourceMode {
    SERVER_RANGE("Server Range"),
    HOTBAR_ONLY("Hotbar Only");

    private final String title;

    private InventoryPickSourceMode(String title) {
        this.title = title;
    }

    public String title() {
        return this.title;
    }

    public static String[] titles() {
        InventoryPickSourceMode[] modes = InventoryPickSourceMode.values();
        String[] titles = new String[modes.length];
        for (int i = 0; i < modes.length; ++i) {
            titles[i] = modes[i].title;
        }
        return titles;
    }

    public static InventoryPickSourceMode of(String title) {
        for (InventoryPickSourceMode mode : InventoryPickSourceMode.values()) {
            if (!mode.title.equals(title)) continue;
            return mode;
        }
        return HOTBAR_ONLY;
    }

    public String toString() {
        return this.title;
    }
}

