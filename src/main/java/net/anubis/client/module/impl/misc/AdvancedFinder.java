/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1799
 *  net.minecraft.class_1922
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2680
 *  net.minecraft.class_2846
 *  net.minecraft.class_2846$class_2847
 *  net.minecraft.class_2868
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_7202
 *  net.minecraft.class_746
 *  net.minecraft.class_9334
 */
package net.anubis.client.module.impl.misc;

import com.example.client.mixin.AccessorNetheriteClientLevel;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.impl.misc.NetheriteFinderModule;
import net.anubis.client.util.netherite.NetheriteChunkTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_1922;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2680;
import net.minecraft.class_2846;
import net.minecraft.class_2868;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_7202;
import net.minecraft.class_746;
import net.minecraft.class_9334;

@Environment(value=EnvType.CLIENT)
final class AdvancedFinder {
    private static final double REACH = 5.0;
    private static final double PROBES_PER_TICK = 18.0;
    private static final double BURST = 600.0;
    private static final int RECOUNT_INTERVAL_TICKS = 10;
    private static final int MAX_REVEALED = 256;
    private final NetheriteFinderModule module;
    private final Map<NetheriteChunkTracker.Candidate, Scan> scans = new HashMap<NetheriteChunkTracker.Candidate, Scan>();
    private final Set<NetheriteChunkTracker.Candidate> done = new HashSet<NetheriteChunkTracker.Candidate>();
    private final Set<class_2338> revealed = new LinkedHashSet<class_2338>();
    private boolean armed;
    private int ticks;
    private double budget;
    private int recountCooldown;
    private static final int MISSING_REFRESH_TICKS = 10;
    private int handWarning;
    private String lastReason = "";
    private class_638 level;

    AdvancedFinder(NetheriteFinderModule module) {
        this.module = module;
    }

    boolean armed() {
        return this.armed;
    }

    Collection<class_2338> revealed() {
        return this.revealed;
    }

    boolean isDone(NetheriteChunkTracker.Candidate section) {
        return this.done.contains(section);
    }

    float progress(NetheriteChunkTracker.Candidate section) {
        Scan scan;
        Scan scan2 = scan = this.armed ? this.scans.get(section) : null;
        if (scan == null) {
            return -1.0f;
        }
        return AdvancedFinder.fraction(scan);
    }

    private static float fraction(Scan scan) {
        return scan.total == 0 ? 1.0f : Math.min(1.0f, (float)scan.sent / (float)scan.total);
    }

    void forEachProbe(class_243 eye, double radius, int limit, float missingFrom, ProbeVisitor visitor) {
        if (!this.armed) {
            return;
        }
        double radiusSq = radius * radius;
        int emitted = 0;
        for (Scan scan : this.scans.values()) {
            if (AdvancedFinder.fraction(scan) >= missingFrom) continue;
            int baseX = scan.section.chunkX() << 4;
            int baseY = scan.section.sectionY() << 4;
            int baseZ = scan.section.chunkZ() << 4;
            for (int index = 0; index < 4096; ++index) {
                int z;
                double dz;
                int y;
                double dy;
                int x;
                double dx;
                int at = scan.stamp[index];
                if (at == 0 || (dx = (double)(x = baseX + (index & 0xF)) + 0.5 - eye.field_1352) * dx + (dy = (double)(y = baseY + (index >> 8)) + 0.5 - eye.field_1351) * dy + (dz = (double)(z = baseZ + (index >> 4 & 0xF)) + 0.5 - eye.field_1350) * dz > radiusSq) continue;
                visitor.visit(x, y, z, this.ticks - at + 1);
                if (++emitted < limit) continue;
                return;
            }
        }
    }

    void forEachMissing(class_243 eye, double radius, int limit, float missingFrom, MissingVisitor visitor) {
        if (!this.armed) {
            return;
        }
        double radiusSq = radius * radius;
        int emitted = 0;
        for (Scan scan : this.scans.values()) {
            if (AdvancedFinder.fraction(scan) < missingFrom) continue;
            this.refreshMissing(scan);
            int baseX = scan.section.chunkX() << 4;
            int baseY = scan.section.sectionY() << 4;
            int baseZ = scan.section.chunkZ() << 4;
            for (int i = 0; i < scan.missingCount; ++i) {
                int z;
                double dz;
                int y;
                double dy;
                int x;
                double dx;
                int index = scan.missing[i];
                if (scan.probed.get(index) || (dx = (double)(x = baseX + (index & 0xF)) + 0.5 - eye.field_1352) * dx + (dy = (double)(y = baseY + (index >> 8)) + 0.5 - eye.field_1351) * dy + (dz = (double)(z = baseZ + (index >> 4 & 0xF)) + 0.5 - eye.field_1350) * dz > radiusSq) continue;
                visitor.visit(x, y, z);
                if (++emitted < limit) continue;
                return;
            }
        }
    }

    private void refreshMissing(Scan scan) {
        if (this.level == null || this.ticks - scan.missingAt < 10) {
            return;
        }
        scan.missingAt = this.ticks;
        int baseX = scan.section.chunkX() << 4;
        int baseY = scan.section.sectionY() << 4;
        int baseZ = scan.section.chunkZ() << 4;
        class_2338.class_2339 cursor = new class_2338.class_2339();
        int count = 0;
        int index = scan.probed.nextClearBit(0);
        while (index < 4096) {
            cursor.method_10103(baseX + (index & 0xF), baseY + (index >> 8), baseZ + (index >> 4 & 0xF));
            if (AdvancedFinder.worthProbing(this.level, (class_2338)cursor)) {
                scan.missing[count++] = index;
            }
            index = scan.probed.nextClearBit(index + 1);
        }
        scan.missingCount = count;
    }

    void arm() {
        this.armed = true;
        this.lastReason = "";
    }

    void disarm(class_310 mc) {
        this.armed = false;
        this.lastReason = "";
    }

    void reset(class_310 mc) {
        this.disarm(mc);
        this.scans.clear();
        this.done.clear();
        this.revealed.clear();
        this.budget = 0.0;
        this.level = null;
    }

    boolean onRevealed(class_2338 pos) {
        NetheriteChunkTracker.Candidate section = new NetheriteChunkTracker.Candidate(pos.method_10263() >> 4, pos.method_10260() >> 4, pos.method_10264() >> 4);
        if (!this.revealed.add(pos)) {
            return false;
        }
        if (this.revealed.size() > 256) {
            Iterator<class_2338> oldest = this.revealed.iterator();
            oldest.next();
            oldest.remove();
        }
        if (this.armed && this.scans.remove(section) != null) {
            this.done.add(section);
            ModuleChat.send((ClientModule)this.module, ModuleChat.body().text("Ancient debris ").good("found").text(" at ").coords(pos.method_10263(), pos.method_10264(), pos.method_10260()));
        }
        return true;
    }

    void maintain(class_310 mc) {
        class_638 world = mc.field_1687;
        if (world == this.level) {
            if (world != null) {
                this.revealed.removeIf(pos -> !world.method_8320(pos).method_27852(class_2246.field_22109));
            }
            return;
        }
        this.scans.clear();
        this.done.clear();
        this.revealed.clear();
        this.level = world;
    }

    void tick(class_310 mc) {
        if (!this.armed) {
            return;
        }
        class_746 player = mc.field_1724;
        class_638 world = mc.field_1687;
        if (player == null || world == null || player.field_3944 == null || mc.field_1761 == null) {
            return;
        }
        ++this.ticks;
        this.budget = Math.min(600.0, this.budget + 18.0);
        if (this.handWarning > 0) {
            --this.handWarning;
        }
        if (!NetheriteChunkTracker.isNether(world)) {
            this.hold("Not in the Nether");
            return;
        }
        this.lastReason = "";
        if (mc.field_1755 != null || player.method_6115() || mc.field_1761.method_2923()) {
            return;
        }
        class_243 eye = player.method_33571();
        this.refreshScans(world, eye);
        if (this.scans.isEmpty() || this.budget < 1.0) {
            return;
        }
        List<Probe> probes = this.collect(world, eye);
        if (probes.isEmpty()) {
            return;
        }
        int selected = player.method_31548().method_67532();
        int slot = AdvancedFinder.probeSlot(player, selected);
        if (slot < 0) {
            if (this.handWarning <= 0) {
                this.handWarning = 200;
                ModuleChat.send((ClientModule)this.module, ModuleChat.body().text("Hotbar needs an empty or non-tool slot"));
            }
            return;
        }
        if (slot != selected) {
            player.field_3944.method_52787((class_2596)new class_2868(slot));
        }
        class_2338 last = null;
        for (Probe probe : probes) {
            if (this.budget < 1.0) break;
            AdvancedFinder.sendAction(player, world, class_2846.class_2847.field_12968, probe.pos());
            probe.scan().probed.set(probe.index());
            ++probe.scan().sent;
            probe.scan().stamp[probe.index()] = this.ticks;
            probe.scan().dirty = true;
            this.budget -= 1.0;
            last = probe.pos();
        }
        if (last != null) {
            AdvancedFinder.sendAction(player, world, class_2846.class_2847.field_12971, last);
        }
        if (slot != selected) {
            player.field_3944.method_52787((class_2596)new class_2868(selected));
        }
    }

    private void refreshScans(class_638 world, class_243 eye) {
        HashSet<NetheriteChunkTracker.Candidate> live = new HashSet<NetheriteChunkTracker.Candidate>(NetheriteChunkTracker.snapshot());
        this.scans.keySet().retainAll(live);
        for (NetheriteChunkTracker.Candidate candidate : live) {
            if (this.done.contains(candidate) || this.scans.containsKey(candidate) || !(AdvancedFinder.distanceToSectionSq(eye, candidate) <= 25.0)) continue;
            this.scans.put(candidate, new Scan(candidate, AdvancedFinder.countProbeable(world, candidate)));
        }
        if (this.recountCooldown-- > 0) {
            return;
        }
        this.recountCooldown = 10;
        Iterator<Scan> iterator = this.scans.values().iterator();
        while (iterator.hasNext()) {
            Scan scan = iterator.next();
            if (!scan.dirty) continue;
            scan.dirty = false;
            if (AdvancedFinder.hasUnprobed(world, scan)) continue;
            iterator.remove();
            this.done.add(scan.section);
            NetheriteChunkTracker.Candidate s = scan.section;
            ModuleChat.send((ClientModule)this.module, ModuleChat.body().text("Section ").good("clean").text(" at ").coords(s.chunkX() * 16 + 8, s.sectionY() * 16 + 8, s.chunkZ() * 16 + 8));
        }
    }

    private List<Probe> collect(class_638 world, class_243 eye) {
        ArrayList<Probe> coarse = new ArrayList<Probe>();
        ArrayList<Probe> fine = new ArrayList<Probe>();
        int span = (int)Math.ceil(5.0);
        int cx = (int)Math.floor(eye.field_1352);
        int cy = (int)Math.floor(eye.field_1351);
        int cz = (int)Math.floor(eye.field_1350);
        class_2338.class_2339 cursor = new class_2338.class_2339();
        for (Scan scan : this.scans.values()) {
            int baseX = scan.section.chunkX() << 4;
            int baseY = scan.section.sectionY() << 4;
            int baseZ = scan.section.chunkZ() << 4;
            int minX = Math.max(baseX, cx - span);
            int maxX = Math.min(baseX + 15, cx + span);
            int minY = Math.max(baseY, cy - span);
            int maxY = Math.min(baseY + 15, cy + span);
            int minZ = Math.max(baseZ, cz - span);
            int maxZ = Math.min(baseZ + 15, cz + span);
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        int ly = y & 0xF;
                        int lz = z & 0xF;
                        int lx = x & 0xF;
                        int index = ly << 8 | lz << 4 | lx;
                        if (scan.probed.get(index)) continue;
                        cursor.method_10103(x, y, z);
                        double distanceSq = AdvancedFinder.distanceToBlockSq(eye, (class_2338)cursor);
                        if (distanceSq > 25.0) continue;
                        if (!AdvancedFinder.worthProbing(world, (class_2338)cursor)) {
                            scan.probed.set(index);
                            continue;
                        }
                        Probe probe = new Probe(scan, index, cursor.method_10062(), distanceSq);
                        if (lx % 3 == 1 && ly % 3 == 1 && lz % 3 == 1) {
                            coarse.add(probe);
                            continue;
                        }
                        fine.add(probe);
                    }
                }
            }
        }
        coarse.sort(Comparator.comparingDouble(Probe::distanceSq));
        fine.sort(Comparator.comparingDouble(Probe::distanceSq));
        coarse.addAll(fine);
        return coarse;
    }

    private static int countProbeable(class_638 world, NetheriteChunkTracker.Candidate section) {
        int baseX = section.chunkX() << 4;
        int baseY = section.sectionY() << 4;
        int baseZ = section.chunkZ() << 4;
        class_2338.class_2339 cursor = new class_2338.class_2339();
        int count = 0;
        for (int index = 0; index < 4096; ++index) {
            cursor.method_10103(baseX + (index & 0xF), baseY + (index >> 8), baseZ + (index >> 4 & 0xF));
            if (!AdvancedFinder.worthProbing(world, (class_2338)cursor)) continue;
            ++count;
        }
        return count;
    }

    private static boolean hasUnprobed(class_638 world, Scan scan) {
        int baseX = scan.section.chunkX() << 4;
        int baseY = scan.section.sectionY() << 4;
        int baseZ = scan.section.chunkZ() << 4;
        class_2338.class_2339 cursor = new class_2338.class_2339();
        int index = scan.probed.nextClearBit(0);
        while (index < 4096) {
            cursor.method_10103(baseX + (index & 0xF), baseY + (index >> 8), baseZ + (index >> 4 & 0xF));
            if (AdvancedFinder.worthProbing(world, (class_2338)cursor)) {
                return true;
            }
            index = scan.probed.nextClearBit(index + 1);
        }
        return false;
    }

    private static boolean worthProbing(class_638 world, class_2338 pos) {
        class_2680 state = world.method_8320(pos);
        if (state.method_26215() || !state.method_26227().method_15769() || state.method_27852(class_2246.field_22109)) {
            return false;
        }
        return state.method_26216() && state.method_26214((class_1922)world, pos) > 0.0f;
    }

    private void hold(String reason) {
        if (reason.equals(this.lastReason)) {
            return;
        }
        this.lastReason = reason;
        ModuleChat.send((ClientModule)this.module, ModuleChat.body().text(reason));
    }

    private static void sendAction(class_746 player, class_638 world, class_2846.class_2847 action, class_2338 pos) {
        class_7202 handler = ((AccessorNetheriteClientLevel)world).anubis$blockStatePredictionHandler();
        try (class_7202 sequence = handler.method_41937();){
            player.field_3944.method_52787((class_2596)new class_2846(action, pos, class_2350.field_11036, sequence.method_41942()));
        }
    }

    private static double distanceToSectionSq(class_243 from, NetheriteChunkTracker.Candidate section) {
        double minX = (double)section.chunkX() * 16.0;
        double minY = (double)section.sectionY() * 16.0;
        double minZ = (double)section.chunkZ() * 16.0;
        double dx = Math.max(0.0, Math.max(minX - from.field_1352, from.field_1352 - (minX + 16.0)));
        double dy = Math.max(0.0, Math.max(minY - from.field_1351, from.field_1351 - (minY + 16.0)));
        double dz = Math.max(0.0, Math.max(minZ - from.field_1350, from.field_1350 - (minZ + 16.0)));
        return dx * dx + dy * dy + dz * dz;
    }

    private static double distanceToBlockSq(class_243 from, class_2338 pos) {
        double dx = Math.max(0.0, Math.max((double)pos.method_10263() - from.field_1352, from.field_1352 - ((double)pos.method_10263() + 1.0)));
        double dy = Math.max(0.0, Math.max((double)pos.method_10264() - from.field_1351, from.field_1351 - ((double)pos.method_10264() + 1.0)));
        double dz = Math.max(0.0, Math.max((double)pos.method_10260() - from.field_1350, from.field_1350 - ((double)pos.method_10260() + 1.0)));
        return dx * dx + dy * dy + dz * dz;
    }

    private static int probeSlot(class_746 player, int selected) {
        int slot;
        if (!AdvancedFinder.isTool(player.method_31548().method_5438(selected))) {
            return selected;
        }
        for (slot = 0; slot < 9; ++slot) {
            if (!player.method_31548().method_5438(slot).method_7960()) continue;
            return slot;
        }
        for (slot = 0; slot < 9; ++slot) {
            if (AdvancedFinder.isTool(player.method_31548().method_5438(slot))) continue;
            return slot;
        }
        return -1;
    }

    private static boolean isTool(class_1799 stack) {
        return stack.method_57826(class_9334.field_50077);
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Scan {
        final NetheriteChunkTracker.Candidate section;
        final BitSet probed = new BitSet(4096);
        final int[] stamp = new int[4096];
        final int total;
        final int[] missing = new int[4096];
        int missingCount;
        int missingAt = -1000;
        int sent;
        boolean dirty;

        Scan(NetheriteChunkTracker.Candidate section, int total) {
            this.section = section;
            this.total = total;
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface ProbeVisitor {
        public void visit(int var1, int var2, int var3, int var4);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface MissingVisitor {
        public void visit(int var1, int var2, int var3);
    }

    @Environment(value=EnvType.CLIENT)
    private record Probe(Scan scan, int index, class_2338 pos, double distanceSq) {
    }
}

