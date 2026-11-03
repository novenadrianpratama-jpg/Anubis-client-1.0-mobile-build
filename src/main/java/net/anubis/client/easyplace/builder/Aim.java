/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1922
 *  net.minecraft.class_2248
 *  net.minecraft.class_2269
 *  net.minecraft.class_2312
 *  net.minecraft.class_2323
 *  net.minecraft.class_2338
 *  net.minecraft.class_2349
 *  net.minecraft.class_2350
 *  net.minecraft.class_2350$class_2351
 *  net.minecraft.class_238
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_2401
 *  net.minecraft.class_2402
 *  net.minecraft.class_2428
 *  net.minecraft.class_243
 *  net.minecraft.class_2533
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3611
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_746
 */
package net.anubis.client.easyplace.builder;

import java.util.Random;
import net.anubis.client.easyplace.mixin.AccessorMouseHandler;
import net.anubis.client.easyplace.util.PlacementUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1922;
import net.minecraft.class_2248;
import net.minecraft.class_2269;
import net.minecraft.class_2312;
import net.minecraft.class_2323;
import net.minecraft.class_2338;
import net.minecraft.class_2349;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_2401;
import net.minecraft.class_2402;
import net.minecraft.class_2428;
import net.minecraft.class_243;
import net.minecraft.class_2533;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3611;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class Aim {
    private static final class_2350[] ORDER = new class_2350[]{class_2350.field_11036, class_2350.field_11043, class_2350.field_11035, class_2350.field_11034, class_2350.field_11039, class_2350.field_11033};
    private static final double[][] FACE_POINTS = new double[][]{{0.5, 0.5}, {0.5, 0.85}, {0.5, 0.15}, {0.15, 0.5}, {0.85, 0.5}};
    private static final float SETTLED = 0.6f;
    private static final float RETARGET = 25.0f;
    private static Motion motion;
    private static final Random HAND;
    public static final float HAND_TURN = 22.0f;

    private Aim() {
    }

    static Shot at(class_310 mc, class_2338 pos) {
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null) {
            return null;
        }
        class_2680 state = mc.field_1687.method_8320(pos);
        class_265 shape = state.method_26218((class_1922)mc.field_1687, pos);
        if (shape.method_1110()) {
            return null;
        }
        class_238 box = shape.method_1107().method_996(pos);
        class_243 eye = player.method_33571();
        double reach = player.method_55754();
        for (class_2350 face : ORDER) {
            float[] rot;
            class_3965 hit;
            class_243 point;
            if (!Aim.faces(box, face, eye) || (point = Aim.faceCenter(box, face)).method_1022(eye) > reach || (hit = Aim.trace(mc, player, (rot = PlacementUtil.calcRotation(eye, point))[0], rot[1], reach)) == null || hit.method_17783() != class_239.class_240.field_1332 || !hit.method_17777().equals((Object)pos) || hit.method_17780() != face) continue;
            return new Shot(hit, rot[0], rot[1]);
        }
        return null;
    }

    static Shot against(class_310 mc, class_2338 cell, class_3611 fluid) {
        return mc.field_1724 == null ? null : Aim.against(mc, cell, fluid, mc.field_1724.method_33571());
    }

    static Shot against(class_310 mc, class_2338 cell, class_3611 fluid, class_243 eye) {
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null) {
            return null;
        }
        double reach = player.method_55754();
        for (class_2350 direction : ORDER) {
            class_238 box;
            class_265 shape;
            class_2402 container;
            class_2248 class_22482;
            class_2338 next = cell.method_10093(direction.method_10153());
            class_2680 state = mc.field_1687.method_8320(next);
            if (state.method_45474() || Aim.clickable(mc, next, state) || fluid != null && (class_22482 = state.method_26204()) instanceof class_2402 && (container = (class_2402)class_22482).method_10310((class_1309)player, (class_1922)mc.field_1687, next, state, fluid) || (shape = state.method_26218((class_1922)mc.field_1687, next)).method_1110() || !Aim.faces(box = shape.method_1107().method_996(next), direction, eye)) continue;
            for (double[] at : FACE_POINTS) {
                float[] rot;
                class_3965 hit;
                class_243 point = Aim.facePoint(box, direction, at[0], at[1]);
                if (point.method_1022(eye) > reach || (hit = PlacementUtil.rayTraceFrom((class_1657)player, eye, (rot = PlacementUtil.calcRotation(eye, point))[0], rot[1], reach)) == null || hit.method_17783() != class_239.class_240.field_1332 || !hit.method_17777().equals((Object)next) || hit.method_17780() != direction) continue;
                return new Shot(hit, rot[0], rot[1]);
            }
        }
        return null;
    }

    private static class_243 facePoint(class_238 box, class_2350 face, double u, double v) {
        double x = box.field_1323 + (box.field_1320 - box.field_1323) * u;
        double z = box.field_1321 + (box.field_1324 - box.field_1321) * (face.method_10166() == class_2350.class_2351.field_11052 ? v : u);
        double y = box.field_1322 + (box.field_1325 - box.field_1322) * v;
        return switch (face) {
            default -> throw new MatchException(null, null);
            case class_2350.field_11036 -> new class_243(x, box.field_1325, z);
            case class_2350.field_11033 -> new class_243(x, box.field_1322, z);
            case class_2350.field_11043 -> new class_243(x, y, box.field_1321);
            case class_2350.field_11035 -> new class_243(x, y, box.field_1324);
            case class_2350.field_11039 -> new class_243(box.field_1323, y, z);
            case class_2350.field_11034 -> new class_243(box.field_1320, y, z);
        };
    }

    static Shot side(class_310 mc, class_2338 block, class_2350 face) {
        return Aim.side(mc, block, face, new double[][]{{0.5, 0.85}, {0.5, 0.5}, {0.5, 0.2}});
    }

    static Shot mine(class_310 mc, class_2338 block, class_2350 face, double u, double v) {
        return Aim.side(mc, block, face, new double[][]{{u, v}, {0.5, 0.5}, {0.5, 0.85}, {0.5, 0.2}});
    }

    private static Shot side(class_310 mc, class_2338 block, class_2350 face, double[][] spots) {
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null) {
            return null;
        }
        class_265 shape = mc.field_1687.method_8320(block).method_26218((class_1922)mc.field_1687, block);
        if (shape.method_1110()) {
            return null;
        }
        class_238 box = shape.method_1107().method_996(block);
        class_243 eye = player.method_33571();
        double reach = player.method_55754();
        if (!Aim.faces(box, face, eye)) {
            return null;
        }
        for (double[] spot : spots) {
            float[] rot;
            class_3965 hit;
            class_243 point = Aim.facePoint(box, face, spot[0], spot[1]);
            if (point.method_1022(eye) > reach || (hit = Aim.trace(mc, player, (rot = PlacementUtil.calcRotation(eye, point))[0], rot[1], reach)) == null || hit.method_17783() != class_239.class_240.field_1332 || !hit.method_17777().equals((Object)block) || hit.method_17780() != face) continue;
            return new Shot(hit, rot[0], rot[1]);
        }
        return null;
    }

    static Shot water(class_310 mc, class_2338 pos) {
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null) {
            return null;
        }
        class_243 eye = player.method_33571();
        double reach = player.method_55754();
        class_243 point = new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.85, (double)pos.method_10260() + 0.5);
        if (eye.field_1351 <= point.field_1351 || point.method_1022(eye) > reach) {
            return null;
        }
        float[] rot = PlacementUtil.calcRotation(eye, point);
        class_3965 hit = Aim.trace(mc, player, rot[0], rot[1], reach, class_3959.class_242.field_1345);
        if (hit == null || hit.method_17783() != class_239.class_240.field_1332 || !hit.method_17777().equals((Object)pos)) {
            return null;
        }
        return new Shot(hit, rot[0], rot[1]);
    }

    static boolean clickable(class_310 mc, class_2338 pos) {
        return Aim.clickable(mc, pos, mc.field_1687.method_8320(pos));
    }

    private static boolean clickable(class_310 mc, class_2338 pos, class_2680 state) {
        class_2248 block = state.method_26204();
        return mc.field_1687.method_8321(pos) != null || block instanceof class_2323 || block instanceof class_2533 || block instanceof class_2349 || block instanceof class_2269 || block instanceof class_2401 || block instanceof class_2312 || block instanceof class_2428;
    }

    static void look(class_310 mc, float yaw, float pitch) {
        class_746 player = mc.field_1724;
        if (player == null) {
            return;
        }
        Aim.clearMouse(mc);
        motion = null;
        float current = player.method_36454();
        float step = Aim.mouseStep(mc);
        player.method_36456(current + Aim.mouse(class_3532.method_15393((float)(yaw - current)), step));
        player.method_36457(class_3532.method_15363((float)(player.method_36455() + Aim.mouse(pitch - player.method_36455(), step)), (float)-90.0f, (float)90.0f));
    }

    static float mouseStep(class_310 mc) {
        double s = (Double)mc.field_1690.method_42495().method_41753() * 0.6 + 0.2;
        return (float)(s * s * s * 8.0 * 0.15);
    }

    private static float mouse(float degrees, float step) {
        return step <= 0.0f ? degrees : (float)Math.round(degrees / step) * step;
    }

    public static float turn(class_310 mc, float yaw, float pitch, float maxStep) {
        float pitchStep;
        float yawStep;
        class_746 player = mc.field_1724;
        if (player == null) {
            return 0.0f;
        }
        Aim.clearMouse(mc);
        float yawNow = player.method_36454();
        float pitchNow = player.method_36455();
        float yawLeft = class_3532.method_15393((float)(yaw - yawNow));
        float pitchLeft = pitch - pitchNow;
        if (Math.abs(yawLeft) < 0.6f && Math.abs(pitchLeft) < 0.6f) {
            motion = null;
            yawStep = yawLeft;
            pitchStep = pitchLeft;
        } else {
            if (motion == null || motion.done() || motion.drifted(yaw, pitch)) {
                motion = Motion.start(yawNow, pitchNow, yaw, pitch, maxStep);
            } else {
                motion.retarget(yaw, pitch);
            }
            float[] want = motion.next();
            yawStep = class_3532.method_15363((float)class_3532.method_15393((float)(want[0] - yawNow)), (float)(-maxStep), (float)maxStep);
            pitchStep = class_3532.method_15363((float)(want[1] - pitchNow), (float)(-maxStep), (float)maxStep);
        }
        float step = Aim.mouseStep(mc);
        player.method_36456(yawNow + Aim.mouse(yawStep, step));
        player.method_36457(class_3532.method_15363((float)(pitchNow + Aim.mouse(pitchStep, step)), (float)-90.0f, (float)90.0f));
        return Math.abs(class_3532.method_15393((float)(yaw - player.method_36454())));
    }

    private static void clearMouse(class_310 mc) {
        if (mc.field_1729 == null) {
            return;
        }
        AccessorMouseHandler mouse = (AccessorMouseHandler)mc.field_1729;
        mouse.easyplace$setAccumulatedDX(0.0);
        mouse.easyplace$setAccumulatedDY(0.0);
    }

    static boolean looking(class_746 player, float yaw, float pitch) {
        return Math.abs(class_3532.method_15393((float)(player.method_36454() - yaw))) < 0.5f && Math.abs(player.method_36455() - pitch) < 0.5f;
    }

    static class_3965 trace(class_310 mc, class_746 player, float yaw, float pitch, double reach) {
        return Aim.trace(mc, player, yaw, pitch, reach, class_3959.class_242.field_1348);
    }

    private static class_3965 trace(class_310 mc, class_746 player, float yaw, float pitch, double reach, class_3959.class_242 fluids) {
        class_243 eye = player.method_33571();
        double yawRad = Math.toRadians(yaw);
        double cosPitch = Math.cos(Math.toRadians(pitch));
        class_243 direction = new class_243(-Math.sin(yawRad) * cosPitch, -Math.sin(Math.toRadians(pitch)), Math.cos(yawRad) * cosPitch);
        return mc.field_1687.method_17742(new class_3959(eye, eye.method_1019(direction.method_1021(reach)), class_3959.class_3960.field_17559, fluids, (class_1297)player));
    }

    private static boolean faces(class_238 box, class_2350 face, class_243 eye) {
        double gap = 0.02;
        return switch (face) {
            default -> throw new MatchException(null, null);
            case class_2350.field_11036 -> {
                if (eye.field_1351 > box.field_1325 + gap) {
                    yield true;
                }
                yield false;
            }
            case class_2350.field_11033 -> {
                if (eye.field_1351 < box.field_1322 - gap) {
                    yield true;
                }
                yield false;
            }
            case class_2350.field_11043 -> {
                if (eye.field_1350 < box.field_1321 - gap) {
                    yield true;
                }
                yield false;
            }
            case class_2350.field_11035 -> {
                if (eye.field_1350 > box.field_1324 + gap) {
                    yield true;
                }
                yield false;
            }
            case class_2350.field_11039 -> {
                if (eye.field_1352 < box.field_1323 - gap) {
                    yield true;
                }
                yield false;
            }
            case class_2350.field_11034 -> eye.field_1352 > box.field_1320 + gap;
        };
    }

    private static class_243 faceCenter(class_238 box, class_2350 face) {
        class_243 center = box.method_1005();
        return switch (face) {
            default -> throw new MatchException(null, null);
            case class_2350.field_11036 -> new class_243(center.field_1352, box.field_1325, center.field_1350);
            case class_2350.field_11033 -> new class_243(center.field_1352, box.field_1322, center.field_1350);
            case class_2350.field_11043 -> new class_243(center.field_1352, center.field_1351, box.field_1321);
            case class_2350.field_11035 -> new class_243(center.field_1352, center.field_1351, box.field_1324);
            case class_2350.field_11039 -> new class_243(box.field_1323, center.field_1351, center.field_1350);
            case class_2350.field_11034 -> new class_243(box.field_1320, center.field_1351, center.field_1350);
        };
    }

    static {
        HAND = new Random();
    }

    @Environment(value=EnvType.CLIENT)
    record Shot(class_3965 hit, float yaw, float pitch) {
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Motion {
        private final float startYaw;
        private final float startPitch;
        private final float bend;
        private final int ticks;
        private float endYaw;
        private float endPitch;
        private float targetYaw;
        private float targetPitch;
        private int elapsed;

        private Motion(float startYaw, float startPitch, float endYaw, float endPitch, float targetYaw, float targetPitch, float bend, int ticks) {
            this.startYaw = startYaw;
            this.startPitch = startPitch;
            this.endYaw = endYaw;
            this.endPitch = endPitch;
            this.targetYaw = targetYaw;
            this.targetPitch = targetPitch;
            this.bend = bend;
            this.ticks = ticks;
        }

        static Motion start(float yaw0, float pitch0, float yaw, float pitch, float maxStep) {
            float dy = class_3532.method_15393((float)(yaw - yaw0));
            float dp = pitch - pitch0;
            double distance = Math.hypot(dy, dp);
            double seconds = (0.07 + 0.055 * Math.log(1.0 + distance / 3.0) / Math.log(2.0)) * Math.exp(HAND.nextGaussian() * 0.22);
            int ticks = (int)Math.max(Math.ceil(distance * 1.9 / (double)maxStep), (double)Math.max(1L, Math.round(seconds * 20.0)));
            double miss = distance > 6.0 ? HAND.nextGaussian() * 0.045 : 0.0;
            double scatter = Math.min(1.2, distance * 0.015);
            float endYaw = (float)((double)yaw0 + (double)dy * (1.0 + miss) + HAND.nextGaussian() * scatter);
            float endPitch = (float)((double)pitch0 + (double)dp * (1.0 + miss) + HAND.nextGaussian() * scatter * 0.7);
            float bend = (float)(HAND.nextGaussian() * 0.06);
            return new Motion(yaw0, pitch0, endYaw, endPitch, yaw0 + dy, pitch, bend, ticks);
        }

        float[] next() {
            ++this.elapsed;
            double t = Math.min(1.0, (double)this.elapsed / (double)this.ticks);
            double s = t * t * t * (10.0 - 15.0 * t + 6.0 * t * t);
            double bulge = Math.sin(Math.PI * t) * (double)this.bend;
            float dy = this.endYaw - this.startYaw;
            float dp = this.endPitch - this.startPitch;
            return new float[]{(float)((double)this.startYaw + (double)dy * s - (double)dp * bulge), (float)((double)this.startPitch + (double)dp * s + (double)dy * bulge)};
        }

        boolean done() {
            return this.elapsed >= this.ticks;
        }

        boolean drifted(float yaw, float pitch) {
            return Math.hypot(class_3532.method_15393((float)(yaw - this.targetYaw)), pitch - this.targetPitch) > 25.0;
        }

        void retarget(float yaw, float pitch) {
            float dy = class_3532.method_15393((float)(yaw - this.targetYaw));
            float dp = pitch - this.targetPitch;
            this.endYaw += dy;
            this.endPitch += dp;
            this.targetYaw += dy;
            this.targetPitch = pitch;
        }
    }
}

