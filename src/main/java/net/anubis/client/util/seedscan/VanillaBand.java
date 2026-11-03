/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1923
 *  net.minecraft.class_2338
 */
package net.anubis.client.util.seedscan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.anubis.client.util.seedscan.ChunkDecoration;
import net.anubis.client.util.seedscan.DecorationOrder;
import net.anubis.client.util.seedscan.TerrainChunk;
import net.anubis.client.util.seedscan.VBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1923;
import net.minecraft.class_2338;

@Environment(value=EnvType.CLIENT)
public final class VanillaBand {
    public static final int MIN_Y = -16;
    public static final int MAX_Y = 63;
    public static final int LAYERS = 80;
    private static final int MAX_CHUNKS = 4;
    private static final int MAX_POSSIBLE = 10;
    private static final int[][][] PERMUTATIONS = VanillaBand.permutations(4);
    public final int chunkX;
    public final int chunkZ;
    private final byte[] cells;

    private VanillaBand(int chunkX, int chunkZ, byte[] cells) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.cells = cells;
    }

    private static int index(int x, int y, int z) {
        return y - -16 << 8 | (z & 0xF) << 4 | x & 0xF;
    }

    public static boolean inBand(int y) {
        return y >= -16 && y <= 63;
    }

    public byte certain(int x, int y, int z) {
        return (byte)(this.cells[VanillaBand.index(x, y, z)] & 0xF);
    }

    public int families(int x, int y, int z) {
        return this.cells[VanillaBand.index(x, y, z)] >> 4 & 0xF;
    }

    public static VanillaBand of(TerrainChunk terrain, ChunkDecoration[] around, DecorationOrder order) {
        byte[] cells = new byte[20480];
        ArrayList<short[]> sequences = new ArrayList<short[]>(9);
        long[] writers = new long[9];
        int x0 = terrain.chunkX << 4;
        int z0 = terrain.chunkZ << 4;
        for (int y = -16; y <= 63; ++y) {
            for (int z = z0; z < z0 + 16; ++z) {
                for (int x = x0; x < x0 + 16; ++x) {
                    byte model = terrain.kind(x, y, z);
                    sequences.clear();
                    long key = class_2338.method_10064((int)x, (int)y, (int)z);
                    for (ChunkDecoration decoration : around) {
                        short[] sequence;
                        if (decoration == null || (sequence = VanillaBand.sequence(decoration, key, x, y, z)) == null) continue;
                        writers[sequences.size()] = class_1923.method_8331((int)decoration.chunkX, (int)decoration.chunkZ);
                        sequences.add(sequence);
                    }
                    cells[VanillaBand.index((int)x, (int)y, (int)z)] = VanillaBand.play(model, sequences, writers, order);
                }
            }
        }
        return new VanillaBand(terrain.chunkX, terrain.chunkZ, cells);
    }

    private static short[] sequence(ChunkDecoration decoration, long key, int x, int y, int z) {
        int index = decoration.find(key);
        int count = index >= 0 ? decoration.writeCount(index) : 0;
        ArrayList<ChunkDecoration.Fog> fogs = null;
        for (ChunkDecoration.Fog fog : decoration.fogs()) {
            if (!fog.contains(x, y, z)) continue;
            if (fogs == null) {
                fogs = new ArrayList<ChunkDecoration.Fog>(2);
            }
            fogs.add(fog);
        }
        if (count == 0 && fogs == null) {
            return null;
        }
        short[] out = new short[count + (fogs == null ? 0 : fogs.size())];
        for (int n = 0; n < count; ++n) {
            out[n] = decoration.write(index, n);
        }
        if (fogs != null) {
            int at = count;
            for (ChunkDecoration.Fog fog : fogs) {
                out[at++] = ChunkDecoration.pack(fog.order(), fog.kind(), true);
            }
            Arrays.sort(out, 0, out.length);
        }
        return out;
    }

    private static byte play(byte model, List<short[]> sequences, long[] writers, DecorationOrder known) {
        int n;
        if (sequences.isEmpty()) {
            return (byte)(VBlock.familyBit(model) << 4 | model);
        }
        int possible = 0;
        Iterator<short[]> iterator = sequences.iterator();
        while (iterator.hasNext()) {
            short[] sArray;
            short[] sArray2 = sArray = iterator.next();
            int n2 = sArray2.length;
            for (int i = 0; i < n2; ++i) {
                int write = sArray2[i];
                if (!ChunkDecoration.possible((short)write)) continue;
                ++possible;
            }
        }
        if (sequences.size() > 4 || possible > 10) {
            int families = VBlock.familyBit(model);
            for (short[] sequence2 : sequences) {
                for (short write : sequence2) {
                    families |= VBlock.familyBit(ChunkDecoration.kind(write));
                }
            }
            return (byte)(families << 4 | 0xF);
        }
        int ends = 0;
        for (int[] order : PERMUTATIONS[sequences.size()]) {
            if (known != null && !VanillaBand.fits(order, writers, known)) continue;
            for (int choice = 0; choice < 1 << possible; ++choice) {
                byte state = model;
                int bit = 0;
                for (int c : order) {
                    for (short write : sequences.get(c)) {
                        boolean taken = true;
                        if (ChunkDecoration.possible(write)) {
                            boolean bl = taken = (choice >> bit++ & 1) != 0;
                        }
                        if (!taken || !VBlock.isBase(state)) continue;
                        state = ChunkDecoration.kind(write);
                    }
                }
                ends |= 1 << state;
            }
        }
        if (ends == 0) {
            return VanillaBand.play(model, sequences, writers, null);
        }
        boolean bl = false;
        for (byte kind = 0; kind < 16; kind = (byte)(kind + 1)) {
            if ((ends >> kind & 1) == 0) continue;
            n |= VBlock.familyBit(kind);
        }
        int certain = Integer.bitCount(ends) == 1 ? (int)Integer.numberOfTrailingZeros(ends) : 15;
        return (byte)(n << 4 | certain);
    }

    private static boolean fits(int[] order, long[] writers, DecorationOrder known) {
        for (int i = 0; i < order.length; ++i) {
            for (int j = i + 1; j < order.length; ++j) {
                if (!known.after(writers[order[i]], writers[order[j]])) continue;
                return false;
            }
        }
        return true;
    }

    private static int[][][] permutations(int max) {
        int[][][] all = new int[max + 1][][];
        for (int n = 0; n <= max; ++n) {
            ArrayList<int[]> list = new ArrayList<int[]>();
            VanillaBand.permute(new int[n], new boolean[n], 0, list);
            all[n] = (int[][])list.toArray((T[])new int[0][]);
        }
        return all;
    }

    private static void permute(int[] current, boolean[] used, int depth, List<int[]> out) {
        if (depth == current.length) {
            out.add((int[])current.clone());
            return;
        }
        for (int i = 0; i < current.length; ++i) {
            if (used[i]) continue;
            used[i] = true;
            current[depth] = i;
            VanillaBand.permute(current, used, depth + 1, out);
            used[i] = false;
        }
    }
}

