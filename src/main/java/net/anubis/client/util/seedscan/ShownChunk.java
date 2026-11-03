/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2246
 *  net.minecraft.class_2680
 *  net.minecraft.class_2841
 *  net.minecraft.class_7923
 */
package net.anubis.client.util.seedscan;

import java.util.Arrays;
import net.anubis.client.util.seedscan.VBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2246;
import net.minecraft.class_2680;
import net.minecraft.class_2841;
import net.minecraft.class_7923;

@Environment(value=EnvType.CLIENT)
public final class ShownChunk {
    static final int KEEP_TOP = 207;
    public final int chunkX;
    public final int chunkZ;
    private final byte[][] nibbles = new byte[24][];
    private final byte[] uniform = new byte[24];
    private final short[] terrainTop = new short[256];
    private final boolean[] iceTop = new boolean[256];
    private final short[] motionTop = new short[256];

    private ShownChunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        Arrays.fill(this.terrainTop, (short)-64);
        Arrays.fill(this.motionTop, (short)-64);
    }

    public static ShownChunk of(int chunkX, int chunkZ, class_2841<class_2680>[] sections) {
        ShownChunk shown = new ShownChunk(chunkX, chunkZ);
        for (int i = 0; i < 24; ++i) {
            class_2841<class_2680> states;
            class_2841<class_2680> class_28412 = states = i < sections.length ? sections[i] : null;
            if (states == null) {
                shown.uniform[i] = 0;
                continue;
            }
            boolean keep = -64 + (i << 4) <= 207;
            byte first = VBlock.of((class_2680)states.method_12321(0, 0, 0));
            byte[] packed = null;
            for (int index = 0; index < 4096; ++index) {
                int x = index & 0xF;
                int y = index >> 8;
                int z = index >> 4 & 0xF;
                class_2680 state = (class_2680)states.method_12321(x, y, z);
                byte kind = VBlock.of(state);
                int top = -64 + (i << 4) + y + 1;
                int column = x + 16 * z;
                if (kind != 0 && state.method_51366() && top > shown.motionTop[column]) {
                    shown.motionTop[column] = (short)top;
                }
                if (ShownChunk.isTerrain(state, kind) && top > shown.terrainTop[column]) {
                    shown.terrainTop[column] = (short)top;
                    shown.iceTop[column] = ShownChunk.isIce(state);
                }
                if (!keep) continue;
                if (packed == null) {
                    if (kind == first) continue;
                    packed = new byte[2048];
                    for (int before = 0; before < index; ++before) {
                        ShownChunk.put(packed, before, first);
                    }
                }
                ShownChunk.put(packed, index, kind);
            }
            shown.uniform[i] = keep ? first : (byte)15;
            shown.nibbles[i] = packed;
        }
        return shown;
    }

    static boolean isTerrain(class_2680 state, byte kind) {
        if (kind == 0 || kind == 1 || kind == 2) {
            return false;
        }
        if (kind != 12) {
            return true;
        }
        if (!state.method_51366()) {
            return false;
        }
        String path = class_7923.field_41175.method_10221((Object)state.method_26204()).method_12832();
        return path.endsWith("dirt") || path.equals("grass_block") || path.equals("podzol") || path.equals("mycelium") || path.endsWith("sand") || path.endsWith("sandstone") || path.endsWith("terracotta") || path.equals("snow_block") || path.equals("packed_ice") || path.equals("blue_ice") || path.equals("mud") || path.equals("calcite") || path.equals("powder_snow") || path.equals("moss_block") || path.equals("clay") || path.equals("dripstone_block") || path.equals("magma_block") || path.equals("smooth_basalt");
    }

    static boolean isIce(class_2680 state) {
        return state.method_27852(class_2246.field_10225) || state.method_27852(class_2246.field_10384) || state.method_27852(class_2246.field_10491);
    }

    private static void put(byte[] packed, int index, byte kind) {
        int b = index >> 1;
        packed[b] = (index & 1) == 0 ? (byte)(packed[b] & 0xF0 | kind) : (byte)(packed[b] & 0xF | kind << 4);
    }

    public byte kind(int x, int y, int z) {
        if (y < -64 || y >= 320) {
            return 0;
        }
        int section = y - -64 >> 4;
        byte[] packed = this.nibbles[section];
        if (packed == null) {
            return this.uniform[section];
        }
        int index = (y & 0xF) << 8 | (z & 0xF) << 4 | x & 0xF;
        byte b = packed[index >> 1];
        return (byte)((index & 1) == 0 ? b & 0xF : b >> 4 & 0xF);
    }

    public int terrainTop(int x, int z) {
        int column = (x & 0xF) + 16 * (z & 0xF);
        return this.iceTop[column] ? Integer.MIN_VALUE : this.terrainTop[column];
    }

    public int motionTop(int x, int z) {
        return this.motionTop[(x & 0xF) + 16 * (z & 0xF)];
    }
}

