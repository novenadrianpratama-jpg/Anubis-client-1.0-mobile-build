/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1923
 *  net.minecraft.class_1944
 *  net.minecraft.class_2338
 *  net.minecraft.class_2804
 *  net.minecraft.class_3562
 *  net.minecraft.class_3568
 *  net.minecraft.class_4076
 *  net.minecraft.class_638
 *  net.minecraft.class_6606
 */
package net.anubis.client.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.BitSet;
import java.util.Iterator;
import net.anubis.client.util.LevelUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1923;
import net.minecraft.class_1944;
import net.minecraft.class_2338;
import net.minecraft.class_2804;
import net.minecraft.class_3562;
import net.minecraft.class_3568;
import net.minecraft.class_4076;
import net.minecraft.class_638;
import net.minecraft.class_6606;

@Environment(value=EnvType.CLIENT)
public final class ServerBlockLight {
    public static final int UNKNOWN = -1;
    public static final int TOP_SECTION = 3;
    public static final int TOP_Y = 63;
    private static final byte[] DARK = new byte[0];
    private final Long2ObjectOpenHashMap<Column> columns = new Long2ObjectOpenHashMap();

    public void onChunk(class_638 level, int chunkX, int chunkZ, class_6606 data) {
        this.columns.put(class_1923.method_8331((int)chunkX, (int)chunkZ), (Object)ServerBlockLight.apply(level, null, data));
    }

    public void onLight(class_638 level, int chunkX, int chunkZ, class_6606 data) {
        long key = class_1923.method_8331((int)chunkX, (int)chunkZ);
        this.columns.put(key, (Object)ServerBlockLight.apply(level, (Column)this.columns.get(key), data));
    }

    public void onBlockChanged(class_2338 pos) {
        if (pos.method_10264() > 63) {
            return;
        }
        Column column = (Column)this.columns.get(class_1923.method_8331((int)(pos.method_10263() >> 4), (int)(pos.method_10260() >> 4)));
        if (column == null) {
            return;
        }
        if (column.touched == null) {
            column.touched = new LongOpenHashSet();
        }
        column.touched.add(pos.method_10063());
    }

    public Column column(class_638 level, int chunkX, int chunkZ) {
        long key = class_1923.method_8331((int)chunkX, (int)chunkZ);
        Column column = (Column)this.columns.get(key);
        if (column == null && LevelUtil.isLoaded(level, chunkX, chunkZ)) {
            column = ServerBlockLight.fromClient(level, chunkX, chunkZ);
            this.columns.put(key, (Object)column);
        }
        return column;
    }

    public void forgetUnloaded(class_638 level) {
        this.columns.keySet().removeIf(key -> !LevelUtil.isLoaded(level, class_1923.method_8325((long)key), class_1923.method_8332((long)key)));
    }

    public void clear() {
        this.columns.clear();
    }

    private static Column apply(class_638 level, Column previous, class_6606 data) {
        class_3568 engine = level.method_22336();
        int minSection = engine.method_31929();
        int count = engine.method_31928();
        byte[][] sections = previous != null && previous.minSection == minSection && previous.sections.length == count ? (Object)((byte[][])previous.sections.clone()) : new byte[count][];
        BitSet sent = data.method_38608();
        BitSet cleared = data.method_38609();
        Iterator updates = data.method_38610().iterator();
        for (int i = 0; i < count; ++i) {
            boolean kept;
            boolean bl = kept = minSection + i <= 3;
            if (sent.get(i)) {
                byte[] bytes = updates.hasNext() ? (byte[])updates.next() : null;
                sections[i] = kept && bytes != null && bytes.length == 2048 ? (byte[])bytes.clone() : null;
                continue;
            }
            if (!cleared.get(i)) continue;
            sections[i] = (byte[])(kept ? DARK : null);
        }
        return new Column(minSection, sections);
    }

    private static Column fromClient(class_638 level, int chunkX, int chunkZ) {
        class_3568 engine = level.method_22336();
        class_3562 listener = engine.method_15562(class_1944.field_9282);
        int minSection = engine.method_31929();
        byte[][] sections = new byte[engine.method_31928()][];
        for (int i = 0; i < sections.length && minSection + i <= 3; ++i) {
            class_2804 layer = listener.method_15544(class_4076.method_18676((int)chunkX, (int)(minSection + i), (int)chunkZ));
            if (layer == null) continue;
            sections[i] = layer.method_12146() ? DARK : (byte[])layer.method_12137().clone();
        }
        return new Column(minSection, sections);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Column {
        private final int minSection;
        private final byte[][] sections;
        private LongOpenHashSet touched;

        private Column(int minSection, byte[][] sections) {
            this.minSection = minSection;
            this.sections = sections;
        }

        public int get(int x, int y, int z) {
            int index = (y >> 4) - this.minSection;
            if (index < 0 || index >= this.sections.length) {
                return -1;
            }
            byte[] data = this.sections[index];
            if (data == null) {
                return -1;
            }
            if (this.touched != null && this.touched.contains(class_2338.method_10064((int)x, (int)y, (int)z))) {
                return -1;
            }
            if (data == DARK) {
                return 0;
            }
            int cell = (y & 0xF) << 8 | (z & 0xF) << 4 | x & 0xF;
            return data[cell >> 1] >> ((cell & 1) << 2) & 0xF;
        }
    }
}

