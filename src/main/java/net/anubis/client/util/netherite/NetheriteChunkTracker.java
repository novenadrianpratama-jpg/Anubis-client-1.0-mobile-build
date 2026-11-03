/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2596
 *  net.minecraft.class_2626
 *  net.minecraft.class_2637
 *  net.minecraft.class_2672
 *  net.minecraft.class_2680
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_5321
 *  net.minecraft.class_638
 *  net.minecraft.class_7134
 *  net.minecraft.class_7924
 */
package net.anubis.client.util.netherite;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.anubis.client.util.netherite.HiddenDebrisScanner;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2596;
import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2672;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5321;
import net.minecraft.class_638;
import net.minecraft.class_7134;
import net.minecraft.class_7924;

@Environment(value=EnvType.CLIENT)
public final class NetheriteChunkTracker {
    static final int MAXIMUM_CANDIDATES = 512;
    private static final int ANCIENT_STATE_ID = class_2248.method_9507((class_2680)class_2246.field_22109.method_9564());
    private static final class_5321<class_1937> DONUT_NETHER = class_5321.method_29179((class_5321)class_7924.field_41223, (class_2960)class_2960.method_60655((String)"worlds", (String)"smp_nether"));
    private static final Set<Candidate> CANDIDATES = new LinkedHashSet<Candidate>();
    private static WeakReference<class_638> lastLevel = new WeakReference<Object>(null);
    private static class_5321<class_1937> lastDimension;
    private static CandidateListener listener;
    private static RevealListener revealListener;

    private NetheriteChunkTracker() {
    }

    public static void onPacketApplied(class_2596<?> packet) {
        class_638 level = class_310.method_1551().field_1687;
        if (level == null || !NetheriteChunkTracker.isNether(level)) {
            return;
        }
        if (packet instanceof class_2672) {
            class_2672 chunk = (class_2672)packet;
            NetheriteChunkTracker.trackLevel(level);
            for (int sectionY : HiddenDebrisScanner.findHiddenSections(chunk.method_38598(), level.method_32890(), level.method_32891(), ANCIENT_STATE_ID)) {
                NetheriteChunkTracker.add(new Candidate(chunk.method_11523(), chunk.method_11524(), sectionY));
            }
        } else if (packet instanceof class_2626) {
            class_2626 update = (class_2626)packet;
            NetheriteChunkTracker.onBlockUpdate(update.method_11309(), update.method_11308());
        } else if (packet instanceof class_2637) {
            class_2637 sectionUpdate = (class_2637)packet;
            sectionUpdate.method_30621(NetheriteChunkTracker::onBlockUpdate);
        }
    }

    private static void onBlockUpdate(class_2338 pos, class_2680 state) {
        if (class_2248.method_9507((class_2680)state) != ANCIENT_STATE_ID) {
            return;
        }
        if (revealListener != null) {
            revealListener.onRevealed(pos.method_10062());
        }
        CANDIDATES.remove(new Candidate(pos.method_10263() >> 4, pos.method_10260() >> 4, pos.method_10264() >> 4));
    }

    private static void add(Candidate candidate) {
        if (CANDIDATES.contains(candidate)) {
            return;
        }
        if (CANDIDATES.size() >= 512) {
            Iterator<Candidate> oldest = CANDIDATES.iterator();
            oldest.next();
            oldest.remove();
        }
        CANDIDATES.add(candidate);
        if (listener != null) {
            listener.onCandidate(candidate);
        }
    }

    private static void trackLevel(class_638 level) {
        class_5321 dimension = level.method_27983();
        if (level == lastLevel.get() && dimension.equals(lastDimension)) {
            return;
        }
        CANDIDATES.clear();
        lastLevel = new WeakReference<class_638>(level);
        lastDimension = dimension;
    }

    public static boolean isNether(class_638 level) {
        class_5321 dimension = level.method_27983();
        return dimension == class_1937.field_25180 || DONUT_NETHER.equals((Object)dimension) || level.method_40134().method_40225(class_7134.field_37667);
    }

    public static List<Candidate> snapshot() {
        if (class_310.method_1551().field_1687 != lastLevel.get()) {
            return List.of();
        }
        return List.copyOf(CANDIDATES);
    }

    public static void setListener(CandidateListener candidateListener) {
        listener = candidateListener;
    }

    public static void setRevealListener(RevealListener debrisListener) {
        revealListener = debrisListener;
    }

    @Environment(value=EnvType.CLIENT)
    public record Candidate(int chunkX, int chunkZ, int sectionY) {
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface RevealListener {
        public void onRevealed(class_2338 var1);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface CandidateListener {
        public void onCandidate(Candidate var1);
    }
}

