/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.service.humanize;

import java.util.concurrent.ThreadLocalRandom;
import net.anubis.client.service.TickJitter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class HumanInventoryTiming {
    private HumanInventoryTiming() {
    }

    public static int openGuiDelayTicks() {
        return TickJitter.apply(4) + ThreadLocalRandom.current().nextInt(0, 3);
    }

    public static int closeGuiDelayTicks() {
        return TickJitter.apply(2) + ThreadLocalRandom.current().nextInt(0, 2);
    }
}

