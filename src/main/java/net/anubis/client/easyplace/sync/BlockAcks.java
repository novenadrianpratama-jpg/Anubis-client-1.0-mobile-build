/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 */
package net.anubis.client.easyplace.sync;

import net.anubis.client.easyplace.mixin.AccessorClientLevel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_638;

@Environment(value=EnvType.CLIENT)
public final class BlockAcks {
    private static class_638 level;
    private static int lastAcked;

    private BlockAcks() {
    }

    public static void acked(int sequence) {
        class_638 now = class_310.method_1551().field_1687;
        if (now != level) {
            level = now;
            lastAcked = -1;
        }
        lastAcked = Math.max(lastAcked, sequence);
    }

    public static int lastSent(class_638 level) {
        return ((AccessorClientLevel)level).easyplace$predictions().method_41942();
    }

    public static boolean answered(class_638 now, int sequence) {
        return now == level && lastAcked >= sequence;
    }

    static {
        lastAcked = -1;
    }
}

