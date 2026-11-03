/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class StableId {
    private StableId() {
    }

    public static String validate(String id) {
        if (!StableId.isValid(id)) {
            throw new IllegalArgumentException("Invalid stable id: " + id);
        }
        return id;
    }

    public static String legacy(String label) {
        String lower = label.toLowerCase(Locale.ROOT);
        StringBuilder id = new StringBuilder(lower.length());
        boolean pendingDash = false;
        for (int i = 0; i < lower.length(); ++i) {
            char c = lower.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= '0' && c <= '9') {
                if (pendingDash && !id.isEmpty()) {
                    id.append('-');
                }
                pendingDash = false;
                id.append(c);
                continue;
            }
            pendingDash = true;
        }
        return StableId.validate(id.toString());
    }

    private static boolean isValid(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        boolean afterSeparator = true;
        for (int i = 0; i < id.length(); ++i) {
            char c = id.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= '0' && c <= '9') {
                afterSeparator = false;
                continue;
            }
            if (c == '.' || c == '_' || c == '-') {
                if (afterSeparator) {
                    return false;
                }
                afterSeparator = true;
                continue;
            }
            return false;
        }
        return !afterSeparator;
    }
}

