/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1542
 *  net.minecraft.class_156
 *  net.minecraft.class_1657
 *  net.minecraft.class_1937
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_5321
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.combat.crystal;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_156;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_5321;
import net.minecraft.class_638;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class LootGuard {
    private static final double HALF_WIDTH = 8.0;
    private static final double HALF_HEIGHT = 4.0;
    private static final double WATCH_RANGE = 16.0;
    private static final double LOADED_RANGE = 32.0;
    private static final long OTHER_MS = 60000L;
    private static final long OWN_MS = 300000L;
    private static final long EMPTY_GRACE_MS = 3000L;
    private static final List<Spot> SPOTS = new ArrayList<Spot>();
    private static final IntSet SEEN = new IntOpenHashSet();
    private static class_5321<class_1937> dimension;
    private static class_746 lastPlayer;
    private static int lastTick;
    private static boolean selfDead;

    private LootGuard() {
    }

    public static void update(class_638 level, class_746 player) {
        if (player == lastPlayer && player.field_6012 == lastTick) {
            return;
        }
        lastPlayer = player;
        lastTick = player.field_6012;
        dimension = level.method_27983();
        long now = class_156.method_658();
        if (player.method_29504()) {
            if (!selfDead) {
                SPOTS.add(new Spot(dimension, player.method_73189(), now, now + 300000L));
            }
            selfDead = true;
        } else {
            selfDead = false;
        }
        SEEN.removeIf(id -> level.method_8469(id) == null);
        for (class_1657 other : level.method_18456()) {
            if (other == player || !other.method_29504() || !SEEN.add(other.method_5628()) || !((double)other.method_5739((class_1297)player) <= 16.0)) continue;
            SPOTS.add(new Spot(dimension, other.method_73189(), now, now + 60000L));
        }
        SPOTS.removeIf(spot -> now > spot.until() || spot.dimension() == dimension && now - spot.since() > 3000L && spot.at().method_1022(player.method_73189()) <= 32.0 && level.method_18467(class_1542.class, spot.box()).isEmpty());
    }

    public static boolean near(class_243 center) {
        for (Spot spot : SPOTS) {
            if (spot.dimension() != dimension || !spot.box().method_1006(center)) continue;
            return true;
        }
        return false;
    }

    static {
        lastTick = -1;
    }

    @Environment(value=EnvType.CLIENT)
    private record Spot(class_5321<class_1937> dimension, class_243 at, long since, long until) {
        class_238 box() {
            return new class_238(this.at.field_1352 - 8.0, this.at.field_1351 - 4.0, this.at.field_1350 - 8.0, this.at.field_1352 + 8.0, this.at.field_1351 + 4.0, this.at.field_1350 + 8.0);
        }
    }
}

