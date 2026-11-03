/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.util;

import net.anubis.client.module.impl.client.PerformanceModule;
import net.anubis.client.util.TickWorkBudget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class PerformanceBudget {
    private static final TickWorkBudget WORK = new TickWorkBudget(System::nanoTime);
    private static long lastTick;

    private PerformanceBudget() {
    }

    public static void beginTick() {
        lastTick = WORK.usedNanos();
        WORK.reset(PerformanceModule.scanBudgetNanos());
    }

    public static long acquire(long requestedNanos) {
        return PerformanceModule.optimizationsEnabled() ? WORK.acquire(requestedNanos) : System.nanoTime() + Math.max(0L, requestedNanos);
    }

    public static void record(long startedAt) {
        WORK.record(startedAt);
    }

    public static long lastTickNanos() {
        return lastTick;
    }
}

