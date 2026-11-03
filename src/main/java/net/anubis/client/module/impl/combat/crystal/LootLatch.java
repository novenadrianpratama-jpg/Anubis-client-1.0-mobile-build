/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_156
 *  net.minecraft.class_1657
 *  net.minecraft.class_1937
 *  net.minecraft.class_2596
 *  net.minecraft.class_2663
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.combat.crystal;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.anubis.client.event.impl.PacketAppliedEvent;
import net.anubis.client.module.impl.combat.crystal.Crystals;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_156;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class LootLatch {
    private static final double RANGE_SQR = 100.0;
    private static final int GRACE_TICKS = 10;
    private static final long ACTIVE_MS = 250L;
    private static final Int2IntOpenHashMap DEAD = new Int2IntOpenHashMap();
    private static class_746 self;
    private static int selfTick;
    private static int clock;
    private static boolean reported;
    private static boolean latched;
    private static boolean killed;
    private static long updatedAt;

    private LootLatch() {
    }

    public static void update(class_310 mc, class_746 player) {
        if (player == self && player.field_6012 == selfTick) {
            return;
        }
        self = player;
        selfTick = player.field_6012;
        class_304 use = mc.field_1690.field_1904;
        LootLatch.evaluate(mc.field_1687, player, Crystals.isPhysicallyDown(mc, use), MeleeAttacks.clickPending(use));
    }

    public static boolean latched() {
        return latched;
    }

    public static boolean killedNow() {
        return killed;
    }

    public static boolean active() {
        return latched && class_156.method_658() - updatedAt < 250L;
    }

    private static void evaluate(class_638 level, class_746 player, boolean down, boolean newPress) {
        int now = ++clock;
        updatedAt = class_156.method_658();
        DEAD.int2IntEntrySet().removeIf(entry -> {
            class_1657 other;
            class_1297 patt0$temp;
            return now - entry.getIntValue() > 10 && (!((patt0$temp = level.method_8469(entry.getIntKey())) instanceof class_1657) || !(other = (class_1657)patt0$temp).method_29504());
        });
        boolean fresh = reported;
        reported = false;
        for (class_1657 other : level.method_18456()) {
            if (other == player || !other.method_29504() || DEAD.containsKey(other.method_5628())) continue;
            DEAD.put(other.method_5628(), now);
            if (!(other.method_5858((class_1297)player) <= 100.0)) continue;
            fresh = true;
        }
        killed = fresh;
        if (fresh && down) {
            latched = true;
        } else if (!down || latched && newPress) {
            latched = false;
        }
    }

    private static void onPacket(PacketAppliedEvent event) {
        class_1657 other;
        class_2663 packet;
        class_2596<?> class_25962 = event.packet;
        if (!(class_25962 instanceof class_2663) || (packet = (class_2663)class_25962).method_11470() != 3) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        class_1297 class_12972 = packet.method_11469((class_1937)mc.field_1687);
        if (!(class_12972 instanceof class_1657) || (other = (class_1657)class_12972) == mc.field_1724 || DEAD.containsKey(other.method_5628())) {
            return;
        }
        DEAD.put(other.method_5628(), clock);
        if (other.method_5858((class_1297)mc.field_1724) <= 100.0) {
            reported = true;
        }
    }

    static {
        selfTick = -1;
        PacketAppliedEvent.TYPE.listen(LootLatch.class, LootLatch::onPacket);
    }
}

