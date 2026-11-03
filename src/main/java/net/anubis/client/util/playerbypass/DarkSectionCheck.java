/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2806
 *  net.minecraft.class_2818
 *  net.minecraft.class_2826
 *  net.minecraft.class_4970$class_4971
 *  net.minecraft.class_638
 */
package net.anubis.client.util.playerbypass;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2806;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_4970;
import net.minecraft.class_638;

@Environment(value=EnvType.CLIENT)
final class DarkSectionCheck {
    private static final int[][] SIDES = new int[][]{{0, 1, 0}, {0, -1, 0}, {0, 0, -1}, {0, 0, 1}, {1, 0, 0}, {-1, 0, 0}};

    private DarkSectionCheck() {
    }

    static boolean airlessAround(class_638 level, int chunkX, int sectionY, int chunkZ) {
        if (!DarkSectionCheck.airless(level, chunkX, sectionY, chunkZ)) {
            return false;
        }
        for (int[] side : SIDES) {
            if (DarkSectionCheck.airless(level, chunkX + side[0], sectionY + side[1], chunkZ + side[2])) continue;
            return false;
        }
        return true;
    }

    private static boolean airless(class_638 level, int chunkX, int sectionY, int chunkZ) {
        if (sectionY < level.method_32891() || sectionY > level.method_31597()) {
            return true;
        }
        class_2818 chunk = level.method_2935().method_2857(chunkX, chunkZ, class_2806.field_12803, false);
        if (chunk == null) {
            return false;
        }
        class_2826 section = chunk.method_38259(chunk.method_31603(sectionY));
        if (section.method_38292()) {
            return false;
        }
        if (!section.method_12265().method_19526(class_4970.class_4971::method_26215)) {
            return true;
        }
        for (int y = 0; y < 16; ++y) {
            for (int z = 0; z < 16; ++z) {
                for (int x = 0; x < 16; ++x) {
                    if (!section.method_12254(x, y, z).method_26215()) continue;
                    return false;
                }
            }
        }
        return true;
    }
}

