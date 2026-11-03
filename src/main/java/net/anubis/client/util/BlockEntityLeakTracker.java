/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2343
 *  net.minecraft.class_2487
 *  net.minecraft.class_2591
 *  net.minecraft.class_2596
 *  net.minecraft.class_2622
 *  net.minecraft.class_2623
 *  net.minecraft.class_2626
 *  net.minecraft.class_2672
 *  net.minecraft.class_2673
 *  net.minecraft.class_2678
 *  net.minecraft.class_2680
 *  net.minecraft.class_2724
 *  net.minecraft.class_2767
 *  net.minecraft.class_2960
 *  net.minecraft.class_3414
 *  net.minecraft.class_6603
 *  net.minecraft.class_6880
 *  net.minecraft.class_7923
 */
package net.anubis.client.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import net.anubis.client.module.impl.client.PerformanceModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2343;
import net.minecraft.class_2487;
import net.minecraft.class_2591;
import net.minecraft.class_2596;
import net.minecraft.class_2622;
import net.minecraft.class_2623;
import net.minecraft.class_2626;
import net.minecraft.class_2672;
import net.minecraft.class_2673;
import net.minecraft.class_2678;
import net.minecraft.class_2680;
import net.minecraft.class_2724;
import net.minecraft.class_2767;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_6603;
import net.minecraft.class_6880;
import net.minecraft.class_7923;

@Environment(value=EnvType.CLIENT)
public final class BlockEntityLeakTracker {
    private static final double ON_EDGE = 0.07;
    private static final Map<class_2338, Entry> FOUND = new ConcurrentHashMap<class_2338, Entry>();
    private static final ConcurrentLinkedQueue<class_2338> ADDED = new ConcurrentLinkedQueue();
    private static final AtomicInteger GENERATION = new AtomicInteger();
    private static final AtomicIntegerArray COUNTS = new AtomicIntegerArray(Source.values().length);
    private static volatile boolean recordAdditions;

    private BlockEntityLeakTracker() {
    }

    public static Map<class_2338, Entry> view() {
        return FOUND;
    }

    public static boolean contains(class_2338 pos) {
        return FOUND.containsKey(pos);
    }

    public static boolean containsReal(class_2338 pos) {
        Entry entry = FOUND.get(pos);
        return entry != null && entry.real();
    }

    public static int count(Source source) {
        return COUNTS.get(source.ordinal());
    }

    public static void clear() {
        FOUND.clear();
        ADDED.clear();
        for (int i = 0; i < COUNTS.length(); ++i) {
            COUNTS.set(i, 0);
        }
        GENERATION.incrementAndGet();
    }

    public static int generation() {
        return GENERATION.get();
    }

    public static void recordAdditions(boolean on) {
        recordAdditions = on;
        if (!on) {
            ADDED.clear();
        }
    }

    public static class_2338 pollAdded() {
        return ADDED.poll();
    }

    public static void ingest(class_2596<?> packet) {
        if (packet instanceof class_2672) {
            class_2672 chunkPacket = (class_2672)packet;
            class_6603 data = chunkPacket.method_38598();
            try {
                data.method_38587(chunkPacket.method_11523(), chunkPacket.method_11524()).accept((pos, type, tag) -> {
                    if (type != null) {
                        BlockEntityLeakTracker.put(pos, type, tag, Source.CHUNK);
                    }
                });
            }
            catch (Throwable throwable) {}
        } else if (packet instanceof class_2622) {
            class_2622 bePacket = (class_2622)packet;
            BlockEntityLeakTracker.put(bePacket.method_11293(), bePacket.method_11291(), bePacket.method_11290(), Source.DATA);
        } else if (packet instanceof class_2623) {
            class_2623 event = (class_2623)packet;
            if (event.method_11295() instanceof class_2343) {
                BlockEntityLeakTracker.hint(event.method_11298(), event.method_11295(), Source.EVENT);
            }
        } else if (packet instanceof class_2673) {
            class_2673 event = (class_2673)packet;
            class_2248 block = BlockEntityLeakTracker.eventBlock(event.method_11532());
            if (block != null && !event.method_11533()) {
                BlockEntityLeakTracker.hint(event.method_11531(), block, Source.EVENT);
            }
        } else if (packet instanceof class_2767) {
            class_2767 sound = (class_2767)packet;
            class_2248 block = BlockEntityLeakTracker.soundBlock((class_6880<class_3414>)sound.method_11894());
            if (block != null) {
                BlockEntityLeakTracker.hintAt(sound.method_11890(), sound.method_11889(), sound.method_11893(), block);
            }
        } else if (packet instanceof class_2626) {
            class_2626 update = (class_2626)packet;
            BlockEntityLeakTracker.dropRevealedHint(update.method_11309(), update.method_11308());
        } else if (packet instanceof class_2678 || packet instanceof class_2724) {
            BlockEntityLeakTracker.clear();
        }
    }

    public static void hint(class_2338 pos, class_2248 block, Source source) {
        class_2338 key = pos.method_10062();
        Entry old = FOUND.get(key);
        if (old != null && (old.real() || old.block() != class_2246.field_10124 || block == class_2246.field_10124)) {
            return;
        }
        FOUND.put(key, new Entry(block, null, source));
        COUNTS.incrementAndGet(source.ordinal());
        if (recordAdditions) {
            ADDED.add(key);
        }
    }

    private static void put(class_2338 pos, class_2591<?> type, class_2487 tag, Source source) {
        if (PerformanceModule.optimizationsEnabled() && BlockEntityLeakTracker.containsReal(pos)) {
            return;
        }
        class_2338 key = pos.method_10062();
        class_2487 data = tag == null || tag.method_33133() ? null : tag;
        Entry old = FOUND.get(key);
        if (old == null || !old.real()) {
            FOUND.put(key, new Entry(BlockEntityLeakTracker.blockFromType(type), data, source));
            COUNTS.incrementAndGet(source.ordinal());
            if (recordAdditions) {
                ADDED.add(key);
            }
        } else if (data != null && !data.equals((Object)old.tag())) {
            FOUND.replace(key, old, new Entry(old.block(), data, old.source()));
        }
    }

    private static void dropRevealedHint(class_2338 pos, class_2680 state) {
        Entry old = FOUND.get(pos);
        if (old == null || old.real()) {
            return;
        }
        if (state.method_26215() || state.method_27852(class_2246.field_10340) || state.method_27852(class_2246.field_28888) || state.method_26204() instanceof class_2343) {
            return;
        }
        if (FOUND.remove(pos, old)) {
            GENERATION.incrementAndGet();
        }
    }

    private static void hintAt(double x, double y, double z, class_2248 block) {
        boolean seamZ;
        int bx = (int)Math.floor(x);
        int by = (int)Math.floor(y);
        int bz = (int)Math.floor(z);
        double fx = x - (double)bx;
        double fy = y - (double)by;
        double fz = z - (double)bz;
        if (Math.abs(fy - 0.5) > 0.07) {
            return;
        }
        boolean seamX = fx < 0.07;
        boolean bl = seamZ = fz < 0.07;
        if (!seamX && Math.abs(fx - 0.5) > 0.07 || !seamZ && Math.abs(fz - 0.5) > 0.07 || seamX && seamZ) {
            return;
        }
        BlockEntityLeakTracker.hint(new class_2338(bx, by, bz), block, Source.SOUND);
        if (seamX) {
            BlockEntityLeakTracker.hint(new class_2338(bx - 1, by, bz), block, Source.SOUND);
        }
        if (seamZ) {
            BlockEntityLeakTracker.hint(new class_2338(bx, by, bz - 1), block, Source.SOUND);
        }
    }

    private static class_2248 eventBlock(int type) {
        return switch (type) {
            case 1000, 1001, 1002, 2000 -> class_2246.field_10200;
            case 1049, 1050, 2010 -> class_2246.field_46797;
            case 1035 -> class_2246.field_10333;
            default -> null;
        };
    }

    private static class_2248 soundBlock(class_6880<class_3414> sound) {
        String kind;
        class_2960 id = ((class_3414)sound.comp_349()).comp_3319();
        if (!id.method_12836().equals("minecraft")) {
            return null;
        }
        String path = id.method_12832();
        if (!path.startsWith("block.")) {
            return null;
        }
        String name = path.substring("block.".length());
        int dot = name.indexOf(46);
        if (dot < 0) {
            return null;
        }
        return switch (kind = name.substring(0, dot)) {
            case "chest" -> class_2246.field_10034;
            case "ender_chest" -> class_2246.field_10443;
            case "barrel" -> class_2246.field_16328;
            case "shulker_box" -> class_2246.field_10603;
            case "beacon" -> class_2246.field_10327;
            case "vault" -> class_2246.field_48851;
            case "trial_spawner" -> class_2246.field_47336;
            case "chiseled_bookshelf" -> class_2246.field_40276;
            case "copper_chest" -> (class_2248)class_7923.field_41175.method_63535(class_2960.method_60656((String)"copper_chest"));
            default -> null;
        };
    }

    public static class_2248 blockFromType(class_2591<?> type) {
        class_2248 mapped;
        String path;
        class_2960 id = class_7923.field_41181.method_10221(type);
        if (id == null) {
            return class_2246.field_10124;
        }
        switch (path = id.method_12832()) {
            case "mob_spawner": {
                class_2248 class_22482 = class_2246.field_10260;
                break;
            }
            case "piston": {
                class_2248 class_22482 = class_2246.field_10008;
                break;
            }
            case "sign": {
                class_2248 class_22482 = class_2246.field_10121;
                break;
            }
            case "hanging_sign": {
                class_2248 class_22482 = class_2246.field_40262;
                break;
            }
            case "skull": {
                class_2248 class_22482 = class_2246.field_10481;
                break;
            }
            case "banner": {
                class_2248 class_22482 = class_2246.field_10154;
                break;
            }
            case "bed": {
                class_2248 class_22482 = class_2246.field_10069;
                break;
            }
            case "brushable_block": {
                class_2248 class_22482 = class_2246.field_42728;
                break;
            }
            default: {
                class_2248 class_22482 = mapped = null;
            }
        }
        if (mapped != null) {
            return mapped;
        }
        return class_7923.field_41175.method_10223(id).map(class_6880::comp_349).orElse(class_2246.field_10124);
    }

    @Environment(value=EnvType.CLIENT)
    public record Entry(class_2248 block, class_2487 tag, Source source) {
        public boolean real() {
            return this.source == Source.CHUNK || this.source == Source.DATA;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Source {
        CHUNK,
        DATA,
        EVENT,
        SOUND,
        LIGHT;

    }
}

