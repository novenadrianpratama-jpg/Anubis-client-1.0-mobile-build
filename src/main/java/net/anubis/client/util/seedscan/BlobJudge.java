/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.bytes.ByteArrayList
 *  it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 *  net.minecraft.class_7923
 */
package net.anubis.client.util.seedscan;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.anubis.client.util.seedscan.VBlock;
import net.anubis.client.util.seedscan.VanillaBand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_7923;

@Environment(value=EnvType.CLIENT)
public final class BlobJudge {
    public static final int JUDGED_MIN_Y = 0;
    public static final int JUDGED_MAX_Y = 30;
    public static final double MIN_MATCH = 0.6;
    public static final int MIN_JUDGED = 8;
    public static final int MAX_FILLED = 3;
    public static final int MAX_CLUSTER = 20000;
    public static final int SHELL_MODEL_CAVE = 16;
    public static final int SHELL_JUDGED = 32;
    public static final int SHELL_STONE = 64;
    private static final Set<class_2248> NATURAL = BlobJudge.set(class_2246.field_27114, class_2246.field_29032, class_2246.field_27159, class_2246.field_27160, class_2246.field_28049, class_2246.field_28048, class_2246.field_28681, class_2246.field_28680, class_2246.field_28685, class_2246.field_10460, class_2246.field_37568, class_2246.field_37569, class_2246.field_37570, class_2246.field_28108, class_2246.field_37571, class_2246.field_10092, class_2246.field_10515, class_2246.field_22423);
    private static final Set<class_2248> FAKES = BlobJudge.set(class_2246.field_10418, class_2246.field_29219, class_2246.field_10212, class_2246.field_29027, class_2246.field_27120, class_2246.field_29221, class_2246.field_10571, class_2246.field_29026, class_2246.field_10080, class_2246.field_29030, class_2246.field_10090, class_2246.field_29028, class_2246.field_10442, class_2246.field_29029, class_2246.field_10013, class_2246.field_29220, class_2246.field_33508, class_2246.field_33509, class_2246.field_10277, class_2246.field_29224, class_2246.field_9989, class_2246.field_10540, class_2246.field_10034, class_2246.field_10443);
    private static final Map<class_2248, Boolean> STRUCTURE = new ConcurrentHashMap<class_2248, Boolean>();

    private BlobJudge() {
    }

    public static List<Cluster> clusters(int chunkX, int chunkZ, Vanilla vanilla) {
        ArrayList<Cluster> clusters = new ArrayList<Cluster>();
        VanillaBand band = vanilla.band(chunkX, chunkZ);
        if (band == null) {
            return clusters;
        }
        Bands bands = new Bands(vanilla);
        for (byte kind : new byte[]{4, 5, 6, 8}) {
            int bit = VBlock.familyBit(kind);
            LongOpenHashSet seen = new LongOpenHashSet();
            for (int y = 0; y <= 30; ++y) {
                for (int z = chunkZ << 4; z < (chunkZ << 4) + 16; ++z) {
                    for (int x = chunkX << 4; x < (chunkX << 4) + 16; ++x) {
                        Cluster cluster;
                        long start;
                        if ((band.families(x, y, z) & bit) == 0 || seen.contains(start = class_2338.method_10064((int)x, (int)y, (int)z)) || class_2338.method_10061((long)(cluster = BlobJudge.cluster(start, kind, bands, seen)).owner()) >> 4 != chunkX || class_2338.method_10083((long)cluster.owner()) >> 4 != chunkZ) continue;
                        clusters.add(cluster);
                    }
                }
            }
        }
        return clusters;
    }

    private static Cluster cluster(long start, byte kind, Bands vanilla, LongOpenHashSet seen) {
        VanillaBand band;
        long next;
        int bit = VBlock.familyBit(kind);
        LongArrayList cells = new LongArrayList();
        LongOpenHashSet in = new LongOpenHashSet();
        LongArrayFIFOQueue open = new LongArrayFIFOQueue();
        open.enqueue(start);
        in.add(start);
        boolean incomplete = false;
        boolean outOfBand = false;
        boolean judgedLayers = false;
        long owner = start;
        while (!open.isEmpty()) {
            long pos = open.dequeueLong();
            cells.add(pos);
            int cy = class_2338.method_10071((long)pos);
            if (cy >= 0 && cy <= 30) {
                if (!judgedLayers || BlobJudge.before(pos, owner)) {
                    owner = pos;
                }
                judgedLayers = true;
            }
            if (cells.size() > 20000) {
                outOfBand = true;
                break;
            }
            int px = class_2338.method_10061((long)pos);
            int py = class_2338.method_10071((long)pos);
            int pz = class_2338.method_10083((long)pos);
            for (int dx = -1; dx <= 1; ++dx) {
                for (int dy = -1; dy <= 1; ++dy) {
                    for (int dz = -1; dz <= 1; ++dz) {
                        int z;
                        int y;
                        int x;
                        if (dx == 0 && dy == 0 && dz == 0 || in.contains(next = class_2338.method_10064((int)(x = px + dx), (int)(y = py + dy), (int)(z = pz + dz)))) continue;
                        if (!VanillaBand.inBand(y)) {
                            outOfBand = true;
                            continue;
                        }
                        band = vanilla.at(x, z);
                        if (band == null) {
                            incomplete = true;
                            continue;
                        }
                        if ((band.families(x, y, z) & bit) == 0) continue;
                        in.add(next);
                        open.enqueue(next);
                    }
                }
            }
        }
        seen.addAll((LongCollection)in);
        long[] none = new long[]{};
        if (outOfBand) {
            return new Cluster(kind, owner, Status.OUT_OF_BAND, none, none, none, new byte[0]);
        }
        if (incomplete) {
            return new Cluster(kind, owner, Status.INCOMPLETE, none, none, none, new byte[0]);
        }
        LongArrayList judged = new LongArrayList();
        LongOpenHashSet shell = new LongOpenHashSet();
        for (int i = 0; i < cells.size(); ++i) {
            long pos = cells.getLong(i);
            int px = class_2338.method_10061((long)pos);
            int py = class_2338.method_10071((long)pos);
            int pz = class_2338.method_10083((long)pos);
            if (py >= 0 && py <= 30 && vanilla.at(px, pz).certain(px, py, pz) == kind) {
                judged.add(pos);
            }
            for (int dx = -1; dx <= 1; ++dx) {
                for (int dy = -1; dy <= 1; ++dy) {
                    for (int dz = -1; dz <= 1; ++dz) {
                        next = class_2338.method_10064((int)(px + dx), (int)(py + dy), (int)(pz + dz));
                        if (in.contains(next)) continue;
                        shell.add(next);
                    }
                }
            }
        }
        long[] shellCells = shell.toLongArray();
        ByteArrayList flags = new ByteArrayList(shellCells.length);
        for (long pos : shellCells) {
            int x = class_2338.method_10061((long)pos);
            int y = class_2338.method_10071((long)pos);
            int z = class_2338.method_10083((long)pos);
            int flag = 0;
            band = vanilla.at(x, z);
            if (band != null && VanillaBand.inBand(y)) {
                flag = band.families(x, y, z);
                byte certain = band.certain(x, y, z);
                if (VBlock.isOpen(certain)) {
                    flag |= 0x10;
                }
                if (certain == 3) {
                    flag |= 0x40;
                }
                if (y >= 0 && y <= 30) {
                    flag |= 0x20;
                }
            }
            flags.add((byte)flag);
        }
        return new Cluster(kind, owner, null, cells.toLongArray(), judged.toLongArray(), shellCells, flags.toByteArray());
    }

    public static Verdict judge(Cluster cluster, Client client, int minChanges) {
        int i;
        byte kind = cluster.kind();
        long owner = cluster.owner();
        if (cluster.unfinished() != null) {
            return BlobJudge.verdict(cluster.unfinished(), kind, owner);
        }
        boolean structure = false;
        for (long pos : cluster.cells()) {
            class_2680 state = client.state(class_2338.method_10061((long)pos), class_2338.method_10071((long)pos), class_2338.method_10083((long)pos));
            if (state == null) {
                return BlobJudge.verdict(Status.INCOMPLETE, kind, owner);
            }
            if (BlobJudge.open(state)) {
                return BlobJudge.verdict(Status.OPEN, kind, owner);
            }
            structure |= BlobJudge.isStructure(state.method_26204());
        }
        long[] shell = cluster.shell();
        byte[] flags = cluster.shellFlags();
        int filled = 0;
        for (int i2 = 0; i2 < shell.length; ++i2) {
            class_2680 state = client.state(class_2338.method_10061((long)shell[i2]), class_2338.method_10071((long)shell[i2]), class_2338.method_10083((long)shell[i2]));
            if (state == null) {
                return BlobJudge.verdict(Status.INCOMPLETE, kind, owner);
            }
            if (BlobJudge.open(state)) {
                return BlobJudge.verdict(Status.OPEN, kind, owner);
            }
            structure |= BlobJudge.isStructure(state.method_26204());
            if ((flags[i2] & 0x10) == 0) continue;
            ++filled;
        }
        if (structure) {
            return BlobJudge.verdict(Status.STRUCTURE, kind, owner);
        }
        if (filled > 3) {
            return BlobJudge.verdict(Status.TERRAIN, kind, owner);
        }
        class_2248 block = VBlock.familyBlock(kind);
        int judged = 0;
        int matches = 0;
        int fakes = 0;
        LongArrayList inside = new LongArrayList();
        LongArrayList outside = new LongArrayList();
        for (long pos : cluster.judged()) {
            class_2248 shown = client.state(class_2338.method_10061((long)pos), class_2338.method_10071((long)pos), class_2338.method_10083((long)pos)).method_26204();
            if (shown == block) {
                ++judged;
                ++matches;
                continue;
            }
            if (FAKES.contains(shown)) {
                ++fakes;
                continue;
            }
            if (NATURAL.contains(shown)) continue;
            ++judged;
            inside.add(pos);
        }
        for (int i3 = 0; i3 < shell.length; ++i3) {
            int z;
            int y;
            if ((flags[i3] & 0x20) == 0 || (flags[i3] & 0x10) != 0) continue;
            int x = class_2338.method_10061((long)shell[i3]);
            class_2680 state = client.state(x, y = class_2338.method_10071((long)shell[i3]), z = class_2338.method_10083((long)shell[i3]));
            byte shown = VBlock.of(state);
            int shownBit = VBlock.familyBit(shown);
            if (shownBit != 0) {
                if ((flags[i3] & shownBit) != 0 || shown == 8 && BlobJudge.nearOpen(client, x, y, z)) continue;
                outside.add(shell[i3]);
                continue;
            }
            if ((flags[i3] & 0x40) == 0 || shown == 3 || FAKES.contains(state.method_26204()) || NATURAL.contains(state.method_26204()) || BlobJudge.nearOpen(client, x, y, z)) continue;
            outside.add(shell[i3]);
        }
        if (judged + fakes > 0 && fakes * 10 > (judged + fakes) * 3) {
            return BlobJudge.verdict(Status.HIDDEN, kind, owner);
        }
        if (judged < 8) {
            return BlobJudge.verdict(Status.TOO_FEW, kind, owner);
        }
        if ((double)matches < 0.6 * (double)judged) {
            return BlobJudge.verdict(Status.MISPREDICTED, kind, owner);
        }
        long[] changes = new long[inside.size() + outside.size()];
        for (i = 0; i < inside.size(); ++i) {
            changes[i] = inside.getLong(i);
        }
        for (i = 0; i < outside.size(); ++i) {
            changes[inside.size() + i] = outside.getLong(i);
        }
        Status status = changes.length >= Math.max(1, minChanges) ? Status.ALERT : Status.CLEAN;
        return new Verdict(status, kind, owner, changes);
    }

    private static Verdict verdict(Status status, byte kind, long owner) {
        return new Verdict(status, kind, owner, new long[0]);
    }

    public static boolean open(class_2680 state) {
        return state.method_26215() || !state.method_26227().method_15769();
    }

    private static boolean nearOpen(Client client, int x, int y, int z) {
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                for (int dz = -1; dz <= 1; ++dz) {
                    class_2680 state = client.state(x + dx, y + dy, z + dz);
                    if (state != null && !BlobJudge.open(state)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean before(long a, long b) {
        if (class_2338.method_10071((long)a) != class_2338.method_10071((long)b)) {
            return class_2338.method_10071((long)a) < class_2338.method_10071((long)b);
        }
        if (class_2338.method_10083((long)a) != class_2338.method_10083((long)b)) {
            return class_2338.method_10083((long)a) < class_2338.method_10083((long)b);
        }
        return class_2338.method_10061((long)a) < class_2338.method_10061((long)b);
    }

    static boolean isStructure(class_2248 block) {
        Boolean known = STRUCTURE.get(block);
        return known != null ? known : STRUCTURE.computeIfAbsent(block, BlobJudge::structureBlock);
    }

    private static boolean structureBlock(class_2248 block) {
        if (block == class_2246.field_10166 || block == class_2246.field_10260 || block == class_2246.field_47336 || block == class_2246.field_48851 || block == class_2246.field_10398 || block == class_2246.field_38420 || block == class_2246.field_10174) {
            return true;
        }
        String path = class_7923.field_41175.method_10221((Object)block).method_12832();
        return path.contains("stone_brick") || path.contains("tuff") && !path.equals("tuff") || path.contains("copper") && !path.contains("ore") && !path.equals("raw_copper_block") || path.startsWith("deepslate_brick") || path.startsWith("deepslate_tile") || path.startsWith("cracked_deepslate") || path.startsWith("polished_deepslate") || path.equals("chiseled_deepslate") || path.startsWith("suspicious_") || path.contains("prismarine") || path.endsWith("sponge") || path.contains("terracotta") || path.startsWith("mud_brick");
    }

    private static Set<class_2248> set(class_2248 ... blocks) {
        Set<class_2248> set = Collections.newSetFromMap(new IdentityHashMap());
        Collections.addAll(set, blocks);
        return set;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Vanilla {
        public VanillaBand band(int var1, int var2);
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Bands
    implements Vanilla {
        private final Vanilla vanilla;
        private int lastX = Integer.MIN_VALUE;
        private int lastZ = Integer.MIN_VALUE;
        private VanillaBand last;

        Bands(Vanilla vanilla) {
            this.vanilla = vanilla;
        }

        VanillaBand at(int x, int z) {
            return this.band(x >> 4, z >> 4);
        }

        @Override
        public VanillaBand band(int chunkX, int chunkZ) {
            if (chunkX != this.lastX || chunkZ != this.lastZ) {
                this.lastX = chunkX;
                this.lastZ = chunkZ;
                this.last = this.vanilla.band(chunkX, chunkZ);
            }
            return this.last;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Cluster(byte kind, long owner, Status unfinished, long[] cells, long[] judged, long[] shell, byte[] shellFlags) {
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Status {
        ALERT,
        CLEAN,
        OPEN,
        STRUCTURE,
        TERRAIN,
        MISPREDICTED,
        HIDDEN,
        TOO_FEW,
        INCOMPLETE,
        OUT_OF_BAND;

    }

    @Environment(value=EnvType.CLIENT)
    public record Verdict(Status status, byte kind, long owner, long[] changes) {
        public boolean alert() {
            return this.status == Status.ALERT;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Client {
        public class_2680 state(int var1, int var2, int var3);
    }
}

