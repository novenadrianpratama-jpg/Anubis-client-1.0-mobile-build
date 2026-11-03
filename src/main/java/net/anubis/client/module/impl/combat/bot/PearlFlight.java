/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_2374
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_2680
 *  net.minecraft.class_3481
 *  net.minecraft.class_3486
 *  net.minecraft.class_3532
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.combat.bot;

import java.util.List;
import net.anubis.client.util.pvp.Rotation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3481;
import net.minecraft.class_3486;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
final class PearlFlight {
    private static final double SPEED = 1.5;
    private static final double GRAVITY = 0.03;
    private static final double DRAG = 0.99;
    private static final double WATER_DRAG = 0.8;
    private static final int MAX_TICKS = 100;
    private static final int MAX_DROP = 3;
    private static final double EDGE_MARGIN = 0.8;

    private PearlFlight() {
    }

    static Landing land(class_746 self, float yaw, float pitch) {
        class_243 start = self.method_33571().method_1023(0.0, 0.1, 0.0);
        class_243 motion = self.method_60478();
        class_243 velocity = class_243.method_1030((float)pitch, (float)yaw).method_1021(1.5).method_1031(motion.field_1352, self.method_24828() ? 0.0 : motion.field_1351, motion.field_1350);
        return PearlFlight.fly(self.method_73183(), (class_1297)self, start, velocity);
    }

    static Landing fly(class_1937 level, class_1297 ignored, class_243 from, class_243 velocity) {
        class_243 at = from;
        class_243 v = velocity;
        for (int tick = 1; tick <= 100; ++tick) {
            class_2338 cell = class_2338.method_49638((class_2374)at);
            if (!level.method_8398().method_12123(cell.method_10263() >> 4, cell.method_10260() >> 4) || at.field_1351 < (double)level.method_31607()) {
                return null;
            }
            boolean water = level.method_8316(cell).method_15767(class_3486.field_15517);
            v = new class_243(v.field_1352, v.field_1351 - 0.03, v.field_1350).method_1021(water ? 0.8 : 0.99);
            class_243 next = at.method_1019(v);
            class_3965 hit = level.method_17742(new class_3959(at, next, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, ignored));
            if (hit.method_17783() != class_239.class_240.field_1333) {
                class_243 landed = hit.method_17784();
                class_2338 floor = PearlFlight.floorUnder(level, landed);
                return floor == null ? null : new Landing(landed, floor, tick);
            }
            at = next;
        }
        return null;
    }

    /*
     * WARNING - void declaration
     */
    static Rotation escape(class_746 self, List<? extends class_1297> enemies, double minAway) {
        class_243 middle = class_243.field_1353;
        for (class_1297 class_12972 : enemies) {
            middle = middle.method_1019(class_12972.method_73189());
        }
        middle = middle.method_1021(1.0 / (double)Math.max(1, enemies.size()));
        float away = (float)Math.toDegrees(Math.atan2(-(self.method_23317() - middle.field_1352), self.method_23321() - middle.field_1350));
        Object var6_7 = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (int k = -4; k <= 4; ++k) {
            float yaw = class_3532.method_15393((float)(away + (float)k * 22.5f));
            for (float pitch = -45.0f; pitch <= 5.0f; pitch += 7.0f) {
                double score;
                double nearest;
                Landing landing = PearlFlight.land(self, yaw, pitch);
                if (landing == null || !PearlFlight.safe(self.method_73183(), landing) || (nearest = PearlFlight.nearest(enemies, landing.at())) < minAway || !((score = nearest - 0.5 * (landing.at().field_1351 - (double)landing.floor().method_10264() - 1.0) - 0.05 * (double)landing.ticks()) > bestScore)) continue;
                bestScore = score;
                Rotation rotation = new Rotation(yaw, pitch);
            }
        }
        return var6_8;
    }

    static Rotation toward(class_746 self, class_243 spot, double near, double far) {
        class_243 eye = self.method_33571();
        double straight = Math.hypot(spot.field_1352 - eye.field_1352, spot.field_1350 - eye.field_1350);
        float yaw0 = (float)Math.toDegrees(Math.atan2(-(spot.field_1352 - eye.field_1352), spot.field_1350 - eye.field_1350));
        double middle = (near + far) / 2.0;
        for (boolean lob : new boolean[]{false, true}) {
            double[] dArray;
            if (lob) {
                double[] dArray2 = new double[3];
                dArray2[0] = 0.0;
                dArray2[1] = middle;
                dArray = dArray2;
                dArray2[2] = -middle;
            } else {
                double[] dArray3 = new double[2];
                dArray3[0] = middle;
                dArray = dArray3;
                dArray3[1] = -middle;
            }
            for (double past : dArray) {
                double wanted = straight + past;
                if (wanted < 1.0) continue;
                for (float off : new float[]{0.0f, -1.5f, 1.5f, -3.0f, 3.0f}) {
                    Rotation found = PearlFlight.pitchFor(self, eye, class_3532.method_15393((float)(yaw0 + off)), wanted, lob);
                    if (found == null || !PearlFlight.reaches(self, found.yaw(), found.pitch(), spot, lob ? 0.0 : near, far)) continue;
                    return found;
                }
            }
        }
        return null;
    }

    private static Rotation pitchFor(class_746 self, class_243 eye, float yaw, double wanted, boolean lob) {
        float shortOf = Float.NaN;
        float from = lob ? -88.0f : 15.0f;
        float to = lob ? -40.0f : -60.0f;
        float step = lob ? 4.0f : -5.0f;
        float pitch = from;
        while (lob ? pitch <= to : pitch >= to) {
            if (!(PearlFlight.carry(self, eye, yaw, pitch) < wanted)) {
                if (Float.isNaN(shortOf)) {
                    return null;
                }
                float low = shortOf;
                float high = pitch;
                for (int i = 0; i < 8; ++i) {
                    float mid = (low + high) / 2.0f;
                    if (PearlFlight.carry(self, eye, yaw, mid) >= wanted) {
                        high = mid;
                        continue;
                    }
                    low = mid;
                }
                return new Rotation(yaw, (low + high) / 2.0f);
            }
            shortOf = pitch;
            pitch += step;
        }
        return null;
    }

    private static double carry(class_746 self, class_243 eye, float yaw, float pitch) {
        Landing landing = PearlFlight.land(self, yaw, pitch);
        return landing == null ? Double.MAX_VALUE : Math.hypot(landing.at().field_1352 - eye.field_1352, landing.at().field_1350 - eye.field_1350);
    }

    static boolean escapes(class_746 self, float yaw, float pitch, List<? extends class_1297> enemies, double minAway) {
        Landing landing = PearlFlight.land(self, yaw, pitch);
        return landing != null && PearlFlight.safe(self.method_73183(), landing) && PearlFlight.nearest(enemies, landing.at()) >= minAway;
    }

    static boolean reaches(class_746 self, float yaw, float pitch, class_243 spot, double near, double far) {
        Landing landing = PearlFlight.land(self, yaw, pitch);
        if (landing == null || !PearlFlight.safe(self.method_73183(), landing)) {
            return false;
        }
        double distance = PearlFlight.horizontal(landing.at(), spot);
        return distance >= near && distance <= far && Math.abs(landing.at().field_1351 - spot.field_1351) <= 3.0;
    }

    static int flightTicks(double distance) {
        if (distance <= 6.0) {
            return 5;
        }
        if (distance <= 10.0) {
            return 5 + (int)Math.round((distance - 6.0) / 2.0);
        }
        if (distance <= 14.0) {
            return 7 + (int)Math.round((distance - 10.0) * 0.75);
        }
        if (distance <= 20.0) {
            return 10 + (int)Math.round((distance - 14.0) * 0.83);
        }
        return 15 + (int)Math.round((distance - 20.0) * 0.8);
    }

    static double nearest(List<? extends class_1297> others, class_243 at) {
        double nearest = Double.MAX_VALUE;
        for (class_1297 class_12972 : others) {
            nearest = Math.min(nearest, class_12972.method_73189().method_1022(at));
        }
        return nearest;
    }

    private static double horizontal(class_243 a, class_243 b) {
        return Math.hypot(a.field_1352 - b.field_1352, a.field_1350 - b.field_1350);
    }

    private static class_2338 floorUnder(class_1937 level, class_243 landed) {
        class_2338 at = class_2338.method_49637((double)landed.field_1352, (double)(landed.field_1351 + 0.05), (double)landed.field_1350);
        for (int down = 0; down <= 4; ++down) {
            class_2338 pos = at.method_10087(down);
            if (level.method_8320(pos).method_26220((class_1922)level, pos).method_1110()) continue;
            return pos;
        }
        return null;
    }

    private static boolean safe(class_1937 level, Landing landing) {
        class_2338 floor = landing.floor();
        if (landing.at().field_1351 - ((double)floor.method_10264() + 1.0) > 3.0) {
            return false;
        }
        for (double[] off : new double[][]{{0.8, 0.0}, {-0.8, 0.0}, {0.0, 0.8}, {0.0, -0.8}}) {
            class_2338 beside = PearlFlight.floorUnder(level, landing.at().method_1031(off[0], 0.0, off[1]));
            if (beside != null && beside.method_10264() == floor.method_10264()) continue;
            return false;
        }
        if (PearlFlight.harmful(level.method_8320(floor)) || level.method_8320(floor).method_27852(class_2246.field_10092)) {
            return false;
        }
        for (int up = 1; up <= 2; ++up) {
            class_2338 pos = floor.method_10086(up);
            class_2680 state = level.method_8320(pos);
            if (!PearlFlight.harmful(state) && state.method_26220((class_1922)level, pos).method_1110() && level.method_8316(pos).method_15769()) continue;
            return false;
        }
        return true;
    }

    private static boolean harmful(class_2680 state) {
        return state.method_27852(class_2246.field_10164) || state.method_26164(class_3481.field_21952) || state.method_27852(class_2246.field_10029) || state.method_27852(class_2246.field_16999) || state.method_27852(class_2246.field_10343) || state.method_27852(class_2246.field_27879) || state.method_27852(class_2246.field_10606);
    }

    @Environment(value=EnvType.CLIENT)
    record Landing(class_243 at, class_2338 floor, int ticks) {
    }
}

