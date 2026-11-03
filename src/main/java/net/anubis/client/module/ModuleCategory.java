/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class ModuleCategory {
    private static final List<ModuleCategory> REGISTERED = new ArrayList<ModuleCategory>();
    private static volatile List<ModuleCategory> values = List.of();
    public static final ModuleCategory COMBAT = ModuleCategory.register("COMBAT", "Combat", Icon.CROSSHAIR);
    public static final ModuleCategory MISC = ModuleCategory.register("MISC", "Misc", Icon.GRID);
    public static final ModuleCategory DONUT = ModuleCategory.register("DONUT", "Donut", Icon.RING);
    public static final ModuleCategory RENDER = ModuleCategory.register("RENDER", "Render", Icon.EYE);
    public static final ModuleCategory CLIENT = ModuleCategory.register("CLIENT", "Anubis Client", Icon.ANKH);
    public static final ModuleCategory MOVEMENT = ModuleCategory.register("MOVEMENT", "Movement", Icon.MOVE);
    public static final ModuleCategory BASE_RAID = ModuleCategory.register("BASE_RAID", "Base Finding", Icon.HOUSE);
    public static final ModuleCategory VISUALS = ModuleCategory.register("VISUALS", "Visuals", Icon.MONITOR);
    public static final ModuleCategory DONUT_MACROS = ModuleCategory.register("DONUT_MACROS", "Macro", Icon.PAPYRUS);
    private final String id;
    private final String displayName;
    private final Icon icon;

    private ModuleCategory(String id, String displayName, Icon icon) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
    }

    public static synchronized ModuleCategory register(String id, String displayName, Icon icon) {
        if (!ModuleCategory.isValidId(id)) {
            throw new IllegalArgumentException("Category id must be upper-case letters, digits and '_': " + id);
        }
        for (ModuleCategory category : REGISTERED) {
            if (!category.id.equals(id)) continue;
            throw new IllegalArgumentException("Duplicate category id: " + id);
        }
        ModuleCategory category = new ModuleCategory(id, Objects.requireNonNull(displayName, "displayName"), Objects.requireNonNull(icon, "icon"));
        REGISTERED.add(category);
        values = List.copyOf(REGISTERED);
        return category;
    }

    private static boolean isValidId(String id) {
        if (id == null || id.isEmpty() || id.charAt(0) < 'A' || id.charAt(0) > 'Z') {
            return false;
        }
        for (int i = 1; i < id.length(); ++i) {
            char c = id.charAt(i);
            if (c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_') continue;
            return false;
        }
        return true;
    }

    public static ModuleCategory register(String id, String displayName) {
        return ModuleCategory.register(id, displayName, Icon.ANKH);
    }

    public static List<ModuleCategory> values() {
        return values;
    }

    public String name() {
        return this.id;
    }

    public String displayName() {
        return this.displayName;
    }

    public Icon icon() {
        return this.icon;
    }

    public String toString() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Icon {
        CROSSHAIR,
        CHEVRON,
        GRID,
        RING,
        EYE,
        MONITOR,
        HOUSE,
        PAPYRUS,
        MOVE,
        ANKH;

    }
}

