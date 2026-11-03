/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338
 *  net.minecraft.class_3532
 */
package net.anubis.client.util.seedscan;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import net.anubis.client.util.seedscan.OreSpec;
import net.anubis.client.util.seedscan.VBlock;
import net.anubis.client.util.seedscan.WgRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;
import net.minecraft.class_3532;

@Environment(value=EnvType.CLIENT)
final class OreSim {
    static final byte NO = 0;
    static final byte YES = 1;
    static final byte MAYBE = 2;
    static final byte CERTAIN = 1;
    static final byte POSSIBLE = 2;
    static final int MAX_STATES = Integer.getInteger("anubis.oreStates", 1024);
    static final int MAX_WORK = Integer.getInteger("anubis.oreWork", 30000);
    static final int MIN_Y = -64;
    static final int MAX_Y = 319;
    private static final int[][] SIDES = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};

    private OreSim() {
    }

    static Chain run(OreSpec spec, int chunkX, int chunkZ, World world) {
        WgRandom random = new WgRandom();
        long decoration = random.setDecorationSeed(world.seed(), chunkX << 4, chunkZ << 4);
        random.setFeatureSeed(decoration, spec.index(), spec.step());
        Chain chain = new Chain(spec, chunkX, chunkZ);
        int count = spec.sampleCount(random);
        List<Object> states = new ArrayList<WgRandom>();
        states.add(random);
        Long2ByteOpenHashMap written = chain.writes;
        Long2ByteOpenHashMap mine = new Long2ByteOpenHashMap();
        int work = 0;
        for (int a = 0; a < count; ++a) {
            ArrayList<WgRandom> next = new ArrayList<WgRandom>(states.size() + 2);
            Long2ByteOpenHashMap attemptWrites = new Long2ByteOpenHashMap();
            LongOpenHashSet everywhere = null;
            for (WgRandom wgRandom : states) {
                mine.clear();
                OreSim.attempt(spec, chunkX, chunkZ, world, wgRandom, written, mine, next);
                LongOpenHashSet certainHere = new LongOpenHashSet();
                ObjectIterator it = mine.long2ByteEntrySet().fastIterator();
                while (it.hasNext()) {
                    Long2ByteMap.Entry entry = (Long2ByteMap.Entry)it.next();
                    attemptWrites.put(entry.getLongKey(), (byte)2);
                    if (entry.getByteValue() != 1) continue;
                    certainHere.add(entry.getLongKey());
                }
                if (everywhere == null) {
                    everywhere = certainHere;
                    continue;
                }
                everywhere.retainAll((LongCollection)certainHere);
            }
            ObjectIterator it = attemptWrites.long2ByteEntrySet().fastIterator();
            while (it.hasNext()) {
                long l = ((Long2ByteMap.Entry)it.next()).getLongKey();
                if (written.get(l) == 1) continue;
                written.put(l, (byte)2);
            }
            if (everywhere != null) {
                LongIterator longIterator = everywhere.iterator();
                while (longIterator.hasNext()) {
                    long l = (Long)longIterator.next();
                    written.put(l, (byte)1);
                }
            }
            if ((states = OreSim.distinct(next)).size() <= MAX_STATES && (work += states.size()) <= MAX_WORK || a + 1 >= count) continue;
            chain.fogFrom = a + 1;
            break;
        }
        return chain;
    }

    private static void attempt(OreSpec spec, int chunkX, int chunkZ, World world, WgRandom random, Long2ByteOpenHashMap written, Long2ByteOpenHashMap mine, List<WgRandom> next) {
        int x = random.nextInt(16) + (chunkX << 4);
        int z = random.nextInt(16) + (chunkZ << 4);
        int y = spec.sampleY(random);
        if (!world.inBiome(spec, x, y, z)) {
            next.add(random);
            return;
        }
        int size = spec.size();
        float f = random.nextFloat() * (float)Math.PI;
        float g = (float)size / 8.0f;
        int i = class_3532.method_15386((float)(((float)size / 16.0f * 2.0f + 1.0f) / 2.0f));
        double d = (double)x + Math.sin(f) * (double)g;
        double e = (double)x - Math.sin(f) * (double)g;
        double h = (double)z + Math.cos(f) * (double)g;
        double j = (double)z - Math.cos(f) * (double)g;
        double l = y + random.nextInt(3) - 2;
        double m = y + random.nextInt(3) - 2;
        int n = x - class_3532.method_15386((float)g) - i;
        int o = y - 2 - i;
        int p = z - class_3532.method_15386((float)g) - i;
        int q = 2 * (class_3532.method_15386((float)g) + i);
        int r = 2 * (2 + i);
        byte gate = OreSim.gate(world, o, n, p, q);
        if (gate == 0) {
            next.add(random);
            return;
        }
        if (gate == 2) {
            next.add(random.copy());
        }
        OreSim.doPlace(spec, chunkX, chunkZ, world, random, written, mine, gate == 2, d, e, h, j, l, m, n, o, p, q, r, next);
    }

    private static byte gate(World world, int o, int n, int p, int q) {
        boolean maybe = false;
        for (int s = n; s <= n + q; ++s) {
            for (int t = p; t <= p + q; ++t) {
                if (o <= world.gateLow(s, t)) {
                    return 1;
                }
                if (o > world.gateHigh(s, t)) continue;
                maybe = true;
            }
        }
        return maybe ? (byte)2 : 0;
    }

    private static void doPlace(OreSpec spec, int chunkX, int chunkZ, World world, WgRandom random, Long2ByteOpenHashMap written, Long2ByteOpenHashMap mine, boolean onlyIfPassed, double d, double e, double h, double j, double l, double m, int n, int o, int p, int q, int r, List<WgRandom> next) {
        int k;
        double sz;
        double sy;
        double sx;
        int size = spec.size();
        double[] ds = new double[size * 4];
        for (int k2 = 0; k2 < size; ++k2) {
            float t = (float)k2 / (float)size;
            sx = class_3532.method_16436((double)t, (double)d, (double)e);
            sy = class_3532.method_16436((double)t, (double)l, (double)m);
            sz = class_3532.method_16436((double)t, (double)h, (double)j);
            double v = random.nextDouble() * (double)size / 16.0;
            double w = ((double)(class_3532.method_15374((double)((float)Math.PI * t)) + 1.0f) * v + 1.0) / 2.0;
            ds[k2 * 4] = sx;
            ds[k2 * 4 + 1] = sy;
            ds[k2 * 4 + 2] = sz;
            ds[k2 * 4 + 3] = w;
        }
        for (int a = 0; a < size - 1; ++a) {
            if (ds[a * 4 + 3] <= 0.0) continue;
            for (int b = a + 1; b < size; ++b) {
                double sr;
                if (ds[b * 4 + 3] <= 0.0 || !((sr = ds[a * 4 + 3] - ds[b * 4 + 3]) * sr > (sx = ds[a * 4] - ds[b * 4]) * sx + (sy = ds[a * 4 + 1] - ds[b * 4 + 1]) * sy + (sz = ds[a * 4 + 2] - ds[b * 4 + 2]) * sz)) continue;
                if (sr > 0.0) {
                    ds[b * 4 + 3] = -1.0;
                    continue;
                }
                ds[a * 4 + 3] = -1.0;
            }
        }
        boolean draws = spec.drawsOnTerrain();
        float discard = spec.discard();
        boolean baseKind = VBlock.isBase(spec.kind());
        WgRandom afterShape = draws ? random.copy() : null;
        int sure = 0;
        int open = 0;
        boolean diverged = false;
        BitSet bits = new BitSet(q * r * q);
        for (k = 0; k < size; ++k) {
            double s = ds[k * 4 + 3];
            if (s < 0.0) continue;
            double t = ds[k * 4];
            double u = ds[k * 4 + 1];
            double v = ds[k * 4 + 2];
            int x0 = Math.max(class_3532.method_15357((double)(t - s)), n);
            int y0 = Math.max(class_3532.method_15357((double)(u - s)), o);
            int z0 = Math.max(class_3532.method_15357((double)(v - s)), p);
            int x1 = Math.max(class_3532.method_15357((double)(t + s)), x0);
            int y1 = Math.max(class_3532.method_15357((double)(u + s)), y0);
            int z1 = Math.max(class_3532.method_15357((double)(v + s)), z0);
            for (int ae = x0; ae <= x1; ++ae) {
                double af = ((double)ae + 0.5 - t) / s;
                if (af * af >= 1.0) continue;
                for (int ag = y0; ag <= y1; ++ag) {
                    double ah = ((double)ag + 0.5 - u) / s;
                    if (af * af + ah * ah >= 1.0) continue;
                    for (int ai = z0; ai <= z1; ++ai) {
                        int place;
                        long key;
                        byte base;
                        int ak;
                        double aj = ((double)ai + 0.5 - v) / s;
                        if (af * af + ah * ah + aj * aj >= 1.0 || ag < -64 || ag > 319 || bits.get(ak = ae - n + (ag - o) * q + (ai - p) * q * r)) continue;
                        bits.set(ak);
                        if (Math.abs((ae >> 4) - chunkX) > 1 || Math.abs((ai >> 4) - chunkZ) > 1 || (base = OreSim.baseNow(world, written, baseKind, ae, ag, ai, key = class_2338.method_10064((int)ae, (int)ag, (int)ai))) == 0) continue;
                        if (base == 2) {
                            if (draws) {
                                ++open;
                                diverged = true;
                            }
                            mine.put(key, (byte)2);
                            continue;
                        }
                        if (discard <= 0.0f) {
                            place = 1;
                        } else if (discard >= 1.0f) {
                            place = OreSim.clearOfAir(world, written, ae, ag, ai);
                        } else {
                            ++sure;
                            if (!diverged) {
                                place = random.nextFloat() >= discard ? 1 : (int)OreSim.clearOfAir(world, written, ae, ag, ai);
                            } else {
                                int n2 = place = OreSim.clearOfAir(world, written, ae, ag, ai) == 1 ? 1 : 2;
                            }
                        }
                        if (place == 1) {
                            mine.put(key, onlyIfPassed ? (byte)2 : 1);
                            continue;
                        }
                        if (place != 2) continue;
                        mine.put(key, (byte)2);
                    }
                }
            }
        }
        if (!draws) {
            next.add(random);
            return;
        }
        for (k = 0; k <= open; ++k) {
            WgRandom state = k == open ? afterShape : afterShape.copy();
            state.skip(sure + k);
            next.add(state);
        }
    }

    private static byte baseNow(World world, Long2ByteOpenHashMap written, boolean baseKind, int x, int y, int z, long key) {
        byte before = written.get(key);
        if (before == 0 || baseKind) {
            return world.base(x, y, z);
        }
        return before == 1 ? (byte)0 : 2;
    }

    private static byte clearOfAir(World world, Long2ByteOpenHashMap written, int x, int y, int z) {
        int result = 1;
        for (int[] side : SIDES) {
            int nx = x + side[0];
            int ny = y + side[1];
            int nz = z + side[2];
            if (ny < -64 || ny > 319) {
                return 0;
            }
            if (written.get(class_2338.method_10064((int)nx, (int)ny, (int)nz)) == 1) continue;
            byte air = world.air(nx, ny, nz);
            if (air == 1) {
                return 0;
            }
            if (air != 2) continue;
            result = 2;
        }
        return (byte)result;
    }

    private static List<WgRandom> distinct(List<WgRandom> states) {
        if (states.size() <= 1) {
            return states;
        }
        HashMap<Long, List> byHash = new HashMap<Long, List>();
        ArrayList<WgRandom> out = new ArrayList<WgRandom>(states.size());
        for (WgRandom state : states) {
            List same = byHash.computeIfAbsent(state.stateHash(), k -> new ArrayList(1));
            boolean seen = false;
            for (WgRandom other : same) {
                if (!other.sameState(state)) continue;
                seen = true;
                break;
            }
            if (seen) continue;
            same.add(state);
            out.add(state);
        }
        return out;
    }

    @Environment(value=EnvType.CLIENT)
    static interface World {
        public long seed();

        public int gateLow(int var1, int var2);

        public int gateHigh(int var1, int var2);

        public byte base(int var1, int var2, int var3);

        public byte air(int var1, int var2, int var3);

        public boolean inBiome(OreSpec var1, int var2, int var3, int var4);
    }

    @Environment(value=EnvType.CLIENT)
    static final class Chain {
        final OreSpec spec;
        final int chunkX;
        final int chunkZ;
        final Long2ByteOpenHashMap writes = new Long2ByteOpenHashMap();
        int fogFrom = -1;

        Chain(OreSpec spec, int chunkX, int chunkZ) {
            this.spec = spec;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }

        boolean fogged() {
            return this.fogFrom >= 0;
        }

        int[] fogBox() {
            int half = this.spec.halfWidth();
            int reach = this.spec.reach();
            int x0 = this.chunkX << 4;
            int z0 = this.chunkZ << 4;
            return new int[]{Math.max(x0 - half, x0 - 16), Math.max(Math.min(this.spec.yMin(), this.spec.yMax()) - 2 - reach, -64), Math.max(z0 - half, z0 - 16), Math.min(x0 + 15 + half, x0 + 31), Math.min(Math.max(this.spec.yMin(), this.spec.yMax()) + 2 + reach, 319), Math.min(z0 + 15 + half, z0 + 31)};
        }
    }
}

