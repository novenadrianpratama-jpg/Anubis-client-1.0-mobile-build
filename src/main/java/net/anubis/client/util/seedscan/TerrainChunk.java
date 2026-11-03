/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2680
 *  net.minecraft.class_2826
 *  net.minecraft.class_2839
 *  net.minecraft.class_2902$class_2903
 */
package net.anubis.client.util.seedscan;

import net.anubis.client.util.seedscan.VBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2680;
import net.minecraft.class_2826;
import net.minecraft.class_2839;
import net.minecraft.class_2902;

@Environment(value=EnvType.CLIENT)
public final class TerrainChunk {
    public static final int MIN_Y = -64;
    public static final int HEIGHT = 384;
    public static final int SECTIONS = 24;
    public final int chunkX;
    public final int chunkZ;
    private final byte[][] nibbles = new byte[24][];
    private final byte[] uniform = new byte[24];
    private final short[] oceanFloor = new short[256];

    private TerrainChunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    static TerrainChunk of(class_2839 chunk) {
        TerrainChunk terrain = new TerrainChunk(chunk.method_12004().field_9181, chunk.method_12004().field_9180);
        for (int i = 0; i < 24; ++i) {
            class_2826 section = chunk.method_38259(i);
            if (section.method_38292()) {
                terrain.uniform[i] = 0;
                continue;
            }
            byte first = VBlock.of(section.method_12254(0, 0, 0));
            byte[] packed = null;
            for (int index = 0; index < 4096; ++index) {
                class_2680 state = section.method_12254(index & 0xF, index >> 8, index >> 4 & 0xF);
                byte kind = VBlock.of(state);
                if (packed == null) {
                    if (kind == first) continue;
                    packed = new byte[2048];
                    for (int before = 0; before < index; ++before) {
                        TerrainChunk.put(packed, before, first);
                    }
                }
                TerrainChunk.put(packed, index, kind);
            }
            terrain.uniform[i] = first;
            terrain.nibbles[i] = packed;
        }
        for (int z = 0; z < 16; ++z) {
            for (int x = 0; x < 16; ++x) {
                terrain.oceanFloor[x + 16 * z] = (short)(chunk.method_12005(class_2902.class_2903.field_13195, x, z) + 1);
            }
        }
        return terrain;
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

    public int oceanFloor(int x, int z) {
        return this.oceanFloor[(x & 0xF) + 16 * (z & 0xF)];
    }
}

