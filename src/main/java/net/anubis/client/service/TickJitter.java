/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.service;

import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class TickJitter {
    private static final float VARIANCE = 0.2f;

    private TickJitter() {
    }

    public static int apply(int baseTicks) {
        if (baseTicks <= 0) {
            return baseTicks;
        }
        int span = Math.max(1, Math.round((float)baseTicks * 0.2f));
        int delta = ThreadLocalRandom.current().nextInt(-span, span + 1);
        return Math.max(1, baseTicks + delta);
    }
}

