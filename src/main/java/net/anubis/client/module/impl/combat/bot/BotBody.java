/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3481
 *  net.minecraft.class_3532
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.combat.bot;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import net.anubis.client.easyplace.builder.Aim;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.anubis.client.util.pvp.Rotation;
import net.anubis.client.util.pvp.RotationUtils;
import net.anubis.client.util.pvp.TargetingUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3481;
import net.minecraft.class_3532;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
final class BotBody {
    private static final double KEEP_EXTRA = 8.0;
    private static final float FAR_OFF = 40.0f;
    private static final int FLIP_COOLDOWN = 8;
    private static final int DROP_SCAN = 16;
    private class_1657 target;
    private boolean grounded;
    private int side = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
    private int sideUntil;
    private int flippedAt = -100;
    private double sideCarry;
    private int tick;

    BotBody() {
    }

    class_1657 target() {
        return this.target;
    }

    int tick() {
        return this.tick;
    }

    void forget() {
        this.target = null;
    }

    void hold(class_1657 who) {
        this.target = who;
    }

    void stayGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    void release(class_310 mc) {
        mc.field_1690.field_1894.method_23481(false);
        mc.field_1690.field_1881.method_23481(false);
        mc.field_1690.field_1913.method_23481(false);
        mc.field_1690.field_1849.method_23481(false);
        mc.field_1690.field_1903.method_23481(false);
        mc.field_1690.field_1867.method_23481(false);
        mc.field_1690.field_1904.method_23481(false);
    }

    class_1657 pick(class_310 mc, class_746 self, double range) {
        return this.pick(mc, self, range, range + 8.0);
    }

    class_1657 pick(class_310 mc, class_746 self, double range, double keep) {
        ++this.tick;
        if (this.tick >= this.sideUntil) {
            this.side = -this.side;
            this.sideUntil = this.tick + 10 + ThreadLocalRandom.current().nextInt(13);
        }
        if (this.target != null && (!this.target.method_5805() || this.target.method_31481() || this.target.method_73183() != self.method_73183() || (double)this.target.method_5739((class_1297)self) > keep || !MeleeAttacks.isEnemy(self, (class_1297)this.target))) {
            this.target = null;
        }
        if (this.target != null) {
            return this.target;
        }
        class_1657 best = null;
        for (class_1657 other : mc.field_1687.method_18456()) {
            double distance;
            if (other == self || !other.method_5805() || other.method_7325() || !MeleeAttacks.isEnemy(self, (class_1297)other) || !((distance = (double)other.method_5739((class_1297)self)) <= range) || best != null && !(distance < (double)best.method_5739((class_1297)self))) continue;
            best = other;
        }
        this.target = best;
        return best;
    }

    void flipSide() {
        if (this.tick - this.flippedAt < 8) {
            return;
        }
        this.flippedAt = this.tick;
        this.side = -this.side;
        this.sideUntil = this.tick + 10 + ThreadLocalRandom.current().nextInt(13);
    }

    void face(class_310 mc, class_746 self, class_1657 who, float speed) {
        class_243 eye = self.method_33571();
        class_243 drift = who.method_73189().method_1023(who.field_6014, who.field_6036, who.field_5969);
        class_238 box = who.method_5829().method_997(drift);
        class_238 core = box.method_35580(box.method_17939() * 0.2, box.method_17940() * 0.15, box.method_17941() * 0.2);
        Rotation look = RotationUtils.fromTo(eye, TargetingUtils.closestPoint(eye, core));
        float off = Math.abs(class_3532.method_15393((float)(look.yaw() - self.method_36454())));
        Aim.turn(mc, look.yaw(), look.pitch(), (off > 40.0f ? 27.0f : 22.0f) * speed);
    }

    void drive(class_310 mc, class_746 self, int forward, double sideways, boolean sprint, boolean jump) {
        class_243 way;
        float yaw = self.method_36454();
        class_243 ahead = class_243.method_1030((float)0.0f, (float)yaw);
        if (forward > 0 && BotBody.safeStep(self, ahead)) {
            mc.field_1690.field_1894.method_23481(true);
            mc.field_1690.field_1867.method_23481(sprint);
            if (self.field_5976 && self.method_24828()) {
                jump = true;
            }
        } else if (forward < 0 && BotBody.safeStep(self, ahead.method_1021(-1.0))) {
            mc.field_1690.field_1881.method_23481(true);
        }
        if (jump && !this.grounded && self.method_24828() && !self.method_5799()) {
            mc.field_1690.field_1903.method_23481(true);
        }
        this.sideCarry += Math.max(0.0, Math.min(1.0, sideways));
        if (this.sideCarry < 1.0) {
            return;
        }
        this.sideCarry -= 1.0;
        class_243 left = class_243.method_1030((float)0.0f, (float)(yaw - 90.0f));
        class_243 class_2432 = way = this.side > 0 ? left : left.method_1021(-1.0);
        if (!BotBody.safeStep(self, way)) {
            this.side = -this.side;
            if (!BotBody.safeStep(self, way = way.method_1021(-1.0))) {
                return;
            }
        }
        (this.side > 0 ? mc.field_1690.field_1913 : mc.field_1690.field_1849).method_23481(true);
    }

    void keepDistance(class_310 mc, class_746 self, class_1657 who, double min, double ideal, double max, boolean dance) {
        double distance = Math.hypot(who.method_23317() - self.method_23317(), who.method_23321() - self.method_23321());
        int forward = distance > ideal + 0.25 ? 1 : (distance < min ? -1 : 0);
        this.drive(mc, self, forward, dance && distance <= max + 1.0 ? 1.0 : 0.0, distance > max, false);
    }

    void flee(class_310 mc, class_746 self, class_1657 from) {
        float away = RotationUtils.fromTo(from.method_33571(), self.method_33571()).yaw();
        Aim.turn(mc, away, 5.0f, 27.0f);
        this.drive(mc, self, 1, 0.0, true, false);
    }

    void away(class_310 mc, class_746 self, class_243 point) {
        class_243 way = self.method_73189().method_1020(point).method_18805(1.0, 0.0, 1.0);
        if (way.method_1027() < 1.0E-6) {
            way = class_243.method_1030((float)0.0f, (float)self.method_36454()).method_1021(-1.0);
        }
        this.go(mc, self, way);
    }

    boolean toward(class_310 mc, class_746 self, class_243 point) {
        class_243 way = point.method_1020(self.method_73189()).method_18805(1.0, 0.0, 1.0);
        if (way.method_1027() < 0.09) {
            return true;
        }
        this.go(mc, self, way);
        if (!this.grounded && self.field_5976 && self.method_24828()) {
            mc.field_1690.field_1903.method_23481(true);
        }
        return false;
    }

    void dropToward(class_310 mc, class_746 self, class_243 point) {
        class_243 way = point.method_1020(self.method_73189()).method_18805(1.0, 0.0, 1.0);
        if (way.method_1027() < 0.09 || !BotBody.safeStep(self, way.method_1029(), 16)) {
            return;
        }
        BotBody.keys(mc, self, way);
    }

    private void go(class_310 mc, class_746 self, class_243 way) {
        if (!BotBody.safeStep(self, way.method_1029())) {
            return;
        }
        BotBody.keys(mc, self, way);
    }

    private static void keys(class_310 mc, class_746 self, class_243 way) {
        float wanted = (float)Math.toDegrees(Math.atan2(-way.field_1352, way.field_1350));
        float relative = class_3532.method_15393((float)(wanted - self.method_36454()));
        float off = Math.abs(relative);
        if (off <= 67.5f) {
            mc.field_1690.field_1894.method_23481(true);
        }
        if (off >= 112.5f) {
            mc.field_1690.field_1881.method_23481(true);
        }
        if (relative <= -22.5f && relative >= -157.5f) {
            mc.field_1690.field_1913.method_23481(true);
        }
        if (relative >= 22.5f && relative <= 157.5f) {
            mc.field_1690.field_1849.method_23481(true);
        }
    }

    void backOff(class_310 mc, class_746 self) {
        this.drive(mc, self, -1, 0.0, false, false);
    }

    boolean use(class_310 mc, class_746 self, Predicate<class_1799> want) {
        int slot = BotBody.hotbarSlot(self, want);
        if (slot < 0) {
            return false;
        }
        class_1661 inventory = self.method_31548();
        if (inventory.method_67532() != slot) {
            inventory.method_61496(slot);
            return true;
        }
        mc.field_1690.field_1904.method_23481(true);
        return true;
    }

    static int hotbarSlot(class_746 self, Predicate<class_1799> want) {
        class_1661 inventory = self.method_31548();
        if (want.test(inventory.method_5438(inventory.method_67532()))) {
            return inventory.method_67532();
        }
        for (int i = 0; i < 9; ++i) {
            if (!want.test(inventory.method_5438(i))) continue;
            return i;
        }
        return -1;
    }

    static boolean safeStep(class_746 self, class_243 way) {
        return BotBody.safeStep(self, way, 3);
    }

    private static boolean safeStep(class_746 self, class_243 way, int deepest) {
        class_1937 level = self.method_73183();
        class_2338 feet = class_2338.method_49637((double)(self.method_23317() + way.field_1352 * 0.8), (double)(self.method_23318() + 0.1), (double)(self.method_23321() + way.field_1350 * 0.8));
        for (int down = 0; down <= deepest; ++down) {
            class_2338 pos = feet.method_10087(down);
            class_2680 state = level.method_8320(pos);
            if (state.method_27852(class_2246.field_10164) || state.method_26164(class_3481.field_21952) || state.method_27852(class_2246.field_10029) || state.method_27852(class_2246.field_16999) || state.method_27852(class_2246.field_10343) || down <= 1 && state.method_27852(class_2246.field_10092)) {
                return false;
            }
            if (state.method_26220((class_1922)level, pos).method_1110()) continue;
            return true;
        }
        return false;
    }
}

