/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1944
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350
 *  net.minecraft.class_2680
 *  net.minecraft.class_2804
 *  net.minecraft.class_2818
 *  net.minecraft.class_2826
 *  net.minecraft.class_3562
 *  net.minecraft.class_4076
 *  net.minecraft.class_638
 */
package net.anubis.client.util;

import java.util.function.LongConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1944;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2804;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_3562;
import net.minecraft.class_4076;
import net.minecraft.class_638;

@Environment(value=EnvType.CLIENT)
public final class GeodeGlowScan {
    public static final int MIN_Y = -58;
    public static final int MAX_Y = 30;
    private static final int EDGE_LIGHT = 4;
    private static final int LOW_SECTION = -4;
    private static final int HIGH_SECTION = 1;
    private static final int FIRST_CACHED = -5;
    private static final int CACHED = 8;
    private static final class_2350[] SIDES = class_2350.values();
    private final class_2804[] layers = new class_2804[72];
    private final class_2338.class_2339 cursor = new class_2338.class_2339();
    private int chunkX;
    private int chunkZ;

    public int scan(class_638 level, class_2818 chunk, LongConsumer hit) {
        this.chunkX = chunk.method_12004().field_9181;
        this.chunkZ = chunk.method_12004().field_9180;
        if (!this.cacheLight(level)) {
            return 0;
        }
        class_2826[] sections = chunk.method_12006();
        int minX = this.chunkX << 4;
        int minZ = this.chunkZ << 4;
        int count = 0;
        for (int sy = -4; sy <= 1; ++sy) {
            int index = chunk.method_31603(sy);
            if (index < 0 || index >= sections.length || sections[index] == null || !this.nearLight(sy)) continue;
            class_2826 section = sections[index];
            int from = Math.max(-58, sy << 4);
            int to = Math.min(30, (sy << 4) + 15);
            for (int y = from; y <= to; ++y) {
                for (int z = 0; z < 16; ++z) {
                    for (int x = 0; x < 16; ++x) {
                        int wz;
                        int wx;
                        if (!GeodeGlowScan.open(section.method_12254(x, y & 0xF, z)) || this.lightAt(wx = minX + x, y, wz = minZ + z) != 0 || !this.edge(level, wx, y, wz)) continue;
                        ++count;
                        if (hit == null) continue;
                        hit.accept(class_2338.method_10064((int)wx, (int)y, (int)wz));
                    }
                }
            }
        }
        return count;
    }

    private boolean cacheLight(class_638 level) {
        class_3562 light = level.method_22336().method_15562(class_1944.field_9282);
        boolean any = false;
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                for (int s = 0; s < 8; ++s) {
                    class_2804 layer = light.method_15544(class_4076.method_18676((int)(this.chunkX + dx), (int)(-5 + s), (int)(this.chunkZ + dz)));
                    if (layer != null && layer.method_12146()) {
                        layer = null;
                    }
                    this.layers[GeodeGlowScan.slot((int)dx, (int)dz, (int)s)] = layer;
                    any |= layer != null;
                }
            }
        }
        return any;
    }

    private boolean nearLight(int sy) {
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                for (int s = sy - 1; s <= sy + 1; ++s) {
                    if (this.layers[GeodeGlowScan.slot(dx, dz, s - -5)] == null) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean edge(class_638 level, int x, int y, int z) {
        boolean litOpening = false;
        int brightest = 0;
        for (class_2350 side : SIDES) {
            int nz;
            int ny;
            int nx = x + side.method_10148();
            int value = this.lightAt(nx, ny = y + side.method_10164(), nz = z + side.method_10165());
            if (value > 4) {
                return false;
            }
            brightest = Math.max(brightest, value);
            if (value != 4 || litOpening || !GeodeGlowScan.open(level.method_8320((class_2338)this.cursor.method_10103(nx, ny, nz)))) continue;
            litOpening = true;
        }
        return brightest == 4 && litOpening;
    }

    private int lightAt(int x, int y, int z) {
        int s = (y >> 4) - -5;
        if (s < 0 || s >= 8) {
            return 0;
        }
        class_2804 layer = this.layers[GeodeGlowScan.slot((x >> 4) - this.chunkX, (z >> 4) - this.chunkZ, s)];
        return layer == null ? 0 : layer.method_12139(x & 0xF, y & 0xF, z & 0xF);
    }

    private static int slot(int dx, int dz, int s) {
        return ((dx + 1) * 3 + dz + 1) * 8 + s;
    }

    private static boolean open(class_2680 state) {
        return state.method_26215() || state.method_27852(class_2246.field_27161);
    }
}

