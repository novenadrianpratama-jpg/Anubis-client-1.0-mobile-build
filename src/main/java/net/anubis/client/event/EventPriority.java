/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum EventPriority {
    LOWEST(-100),
    LOW(-50),
    NORMAL(0),
    HIGH(50),
    HIGHEST(100);

    public final int value;

    private EventPriority(int value) {
        this.value = value;
    }

    public static EventPriority fromInt(int v) {
        if (v >= 75) {
            return HIGHEST;
        }
        if (v >= 25) {
            return HIGH;
        }
        if (v >= -25) {
            return NORMAL;
        }
        if (v >= -75) {
            return LOW;
        }
        return LOWEST;
    }
}

