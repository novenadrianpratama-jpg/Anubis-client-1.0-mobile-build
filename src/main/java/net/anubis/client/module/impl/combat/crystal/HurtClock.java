/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1937
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2716
 *  net.minecraft.class_6880
 *  net.minecraft.class_746
 *  net.minecraft.class_8042
 *  net.minecraft.class_8111
 *  net.minecraft.class_8143
 */
package net.anubis.client.module.impl.combat.crystal;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.anubis.client.module.impl.combat.crystal.CrystalDamage;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2716;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import net.minecraft.class_8042;
import net.minecraft.class_8111;
import net.minecraft.class_8143;

@Environment(value=EnvType.CLIENT)
public final class HurtClock {
    private static final int NEVER = -1000;
    private static final int WINDOW = 10;
    private static final int MAX_WAIT = 20;
    private static final int MAX_MARGIN = 2;
    private static final int RELAX_STREAK = 8;
    private static final int REMOVE_GRACE = 3;
    private static final double ANCHOR_MATCH_SQR = 0.25;
    private static final Int2ObjectOpenHashMap<Mark> MARKS = new Int2ObjectOpenHashMap();
    private static class_746 lastPlayer;
    private static class_1937 lastLevel;
    private static class_2596<?> lastPacket;
    private static int now;
    private static int margin;
    private static int streak;

    private HurtClock() {
    }

    public static void note(class_746 player, class_2596<?> packet) {
        if (packet == lastPacket) {
            return;
        }
        lastPacket = packet;
        if (packet instanceof class_8042) {
            class_8042 bundle = (class_8042)packet;
            for (class_2596 inner : bundle.method_48324()) {
                HurtClock.handle(player, inner);
            }
        } else {
            HurtClock.handle(player, packet);
        }
    }

    public static void tick(class_746 player) {
        now = player.field_6012;
        if (player != lastPlayer || player.method_73183() != lastLevel) {
            MARKS.clear();
            margin = 0;
            streak = 0;
            lastPlayer = player;
            lastLevel = player.method_73183();
        }
        ObjectIterator iterator = MARKS.int2ObjectEntrySet().iterator();
        while (iterator.hasNext()) {
            class_1309 target;
            Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)iterator.next();
            class_1297 class_12972 = lastLevel.method_8469(entry.getIntKey());
            if (!(class_12972 instanceof class_1309) || !(target = (class_1309)class_12972).method_5805()) {
                iterator.remove();
                continue;
            }
            Mark mark = (Mark)entry.getValue();
            if (mark.removedAt == -1000 || mark.confirmed || now - mark.removedAt <= 3) continue;
            margin = Math.min(2, margin + 1);
            streak = 0;
            mark.shotTick = -1000;
            mark.removedAt = -1000;
        }
    }

    public static boolean freshAt(class_1309 target, int ahead, int lead) {
        if (target.field_6008 - ahead - lead > 10) {
            return false;
        }
        Mark mark = (Mark)MARKS.get(target.method_5628());
        return mark == null || mark.shotTick == -1000 || now + ahead - mark.shotTick >= 10 + margin;
    }

    public static int opensIn(class_1309 target, int lead) {
        for (int ahead = 0; ahead < 20; ++ahead) {
            if (!HurtClock.freshAt(target, ahead, lead)) continue;
            return ahead;
        }
        return 20;
    }

    public static float effective(CrystalDamage.Victim victim, float raw, int ahead, int lead) {
        class_1309 target = victim.entity();
        if (HurtClock.freshAt(target, ahead, lead)) {
            return victim.reduce(raw);
        }
        Mark mark = (Mark)MARKS.get(target.method_5628());
        float last = mark == null ? Float.MAX_VALUE : mark.lastRaw;
        return raw > last ? victim.reduce(raw - last) : 0.0f;
    }

    public static void shot(class_746 player, class_1309 target, float raw, Object key, int lead) {
        Mark mark = (Mark)MARKS.computeIfAbsent(target.method_5628(), id -> new Mark());
        if (HurtClock.freshAt(target, 0, lead)) {
            mark.shotTick = player.field_6012;
            mark.shotRaw = raw;
            mark.lastRaw = raw;
            mark.shotKey = key;
            mark.confirmed = false;
            mark.removedAt = -1000;
        } else {
            mark.lastRaw = Math.max(mark.lastRaw, raw);
        }
    }

    private static void handle(class_746 player, class_2596<?> packet) {
        if (packet instanceof class_8143) {
            class_8143 damage = (class_8143)packet;
            HurtClock.damaged(player, damage);
        } else if (packet instanceof class_2716) {
            class_2716 remove = (class_2716)packet;
            HurtClock.removed(remove.method_36548());
        }
    }

    private static void damaged(class_746 player, class_8143 packet) {
        class_1657 target;
        class_1297 class_12972 = player.method_73183().method_8469(packet.comp_1267());
        if (!(class_12972 instanceof class_1657) || !MeleeAttacks.isEnemy(player, (class_1297)(target = (class_1657)class_12972))) {
            return;
        }
        Mark mark = (Mark)MARKS.computeIfAbsent(target.method_5628(), id -> new Mark());
        if (mark.shotTick != -1000 && !mark.confirmed && HurtClock.ours(player, mark, packet)) {
            mark.lastRaw = mark.shotRaw;
            mark.confirmed = true;
            if (mark.shotKey instanceof Integer && ++streak >= 8) {
                margin = Math.max(0, margin - 1);
                streak = 0;
            }
            return;
        }
        class_6880 type = packet.comp_1268();
        mark.shotTick = -1000;
        mark.removedAt = -1000;
        mark.lastRaw = type.method_40225(class_8111.field_42320) || type.method_40225(class_8111.field_42360) ? 0.0f : Float.MAX_VALUE;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static boolean ours(class_746 player, Mark mark, class_8143 packet) {
        class_6880 type = packet.comp_1268();
        if (type.method_40225(class_8111.field_42332)) {
            if (packet.comp_1269() != player.method_5628()) return false;
            Object object = mark.shotKey;
            if (!(object instanceof Integer)) return false;
            Integer crystal = (Integer)object;
            if (packet.comp_1270() != crystal.intValue()) return false;
            return true;
        }
        if (!type.method_40225(class_8111.field_42334)) return false;
        Object object = mark.shotKey;
        if (!(object instanceof class_243)) return false;
        class_243 center = (class_243)object;
        if (!packet.comp_1271().filter(at -> at.method_1025(center) < 0.25).isPresent()) return false;
        return true;
    }

    private static void removed(IntList ids) {
        for (Mark mark : MARKS.values()) {
            Integer crystal;
            Object object;
            if (mark.shotTick == -1000 || mark.confirmed || !((object = mark.shotKey) instanceof Integer) || !ids.contains((crystal = (Integer)object).intValue())) continue;
            mark.removedAt = now;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Mark {
        float lastRaw = Float.MAX_VALUE;
        int shotTick = -1000;
        float shotRaw;
        Object shotKey;
        boolean confirmed;
        int removedAt = -1000;

        private Mark() {
        }
    }
}

