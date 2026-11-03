/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_156
 *  net.minecraft.class_1657
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.CombatMacroLock;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.combat.CombatTarget;
import net.anubis.client.util.pvp.Rotation;
import net.anubis.client.util.pvp.RotationUtils;
import net.anubis.client.util.pvp.ServerRotation;
import net.anubis.client.util.pvp.TargetingUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_156;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class AimAssistModule
extends ClientModule {
    private static final long LAST_HIT_MS = 1500L;
    private static final double STICK_RANGE = 0.5;
    private static final double STICK_FOV = 1.3;
    private static final long MOUSE_IDLE_MS = 150L;
    private static final double[] STILL = new double[]{0.0, 0.0};
    private static final long MINING_QUIET_MS = 250L;
    private final NumberSetting range = this.addSetting("range", new NumberSetting("Range", 4.0f, 3.0f, 6.0f).step(0.1f));
    private final NumberSetting fov = this.addSetting("fov", new NumberSetting("FOV", 60.0f, 10.0f, 180.0f).step(5.0f));
    private final NumberSetting speed = this.addSetting("speed", new NumberSetting("Speed", 60.0f, 10.0f, 180.0f).step(5.0f));
    private final BooleanSetting weaponsOnly = this.addSetting("weapons-only", new BooleanSetting("Weapons Only", true));
    private final BooleanSetting vertical = this.addSetting("vertical", new BooleanSetting("Vertical", true));
    private class_1657 target;
    private double pace;
    private double pendingYaw;
    private double pendingPitch;
    private long movedAt;
    private double userYawSign;
    private double userPitchSign;
    private long minedAt;

    public AimAssistModule() {
        super("aim-assist", "Aim Assist", "Eases your crosshair onto the nearest enemy player while you move the mouse.", ModuleCategory.COMBAT);
    }

    @Override
    public int settingsVersion() {
        return 1;
    }

    @Override
    public void migrateSettings(int savedVersion) {
        if (savedVersion < 1) {
            this.speed.set(60.0f);
        }
    }

    @Override
    protected void onDisable() {
        this.follow(null);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (mc.field_1761 != null && mc.field_1761.method_2923()) {
            this.minedAt = class_156.method_658();
        }
        if (!MeleeAttacks.canAct(mc) || player.method_6115() || mc.field_1690.field_1904.method_1434() || this.weaponsOnly.get() && !MeleeAttacks.isMeleeWeapon(player.method_6047()) || ServerRotation.pending() || CombatMacroLock.heldByOther(this) || class_156.method_658() - this.minedAt < 250L) {
            this.follow(null);
            return;
        }
        this.follow(this.choose(mc, player));
    }

    public double[] nudge(class_746 player, double userYaw, double userPitch, double seconds) {
        float pitchError;
        Rotation want;
        long now = class_156.method_658();
        if (userYaw != 0.0 || userPitch != 0.0) {
            this.movedAt = now;
        }
        if (userYaw != 0.0) {
            this.userYawSign = Math.signum(userYaw);
        }
        if (userPitch != 0.0) {
            this.userPitchSign = Math.signum(userPitch);
        }
        Rotation rotation = want = this.target != null && now - this.movedAt <= 150L ? this.aim(player, player.method_33571(), AimAssistModule.core(this.target)) : null;
        if (want == null) {
            this.pendingYaw = 0.0;
            this.pendingPitch = 0.0;
            this.userYawSign = 0.0;
            this.userPitchSign = 0.0;
            return STILL;
        }
        double dt = Math.min(seconds, 0.05);
        float yawError = class_3532.method_15393((float)(want.yaw() - player.method_36454()));
        float f = pitchError = this.vertical.get() ? want.pitch() - player.method_36455() : 0.0f;
        if (this.userYawSign * (double)yawError < 0.0) {
            yawError = 0.0f;
        }
        if (this.userPitchSign * (double)pitchError < 0.0) {
            pitchError = 0.0f;
        }
        double cap = (double)this.speed.get() * this.pace * dt;
        double ease = 1.0 - Math.exp(-12.0 * dt);
        double k = RotationUtils.mouseScale(class_310.method_1551(), player);
        double degreesPerCount = (float)k * 0.15f;
        this.pendingYaw += class_3532.method_15350((double)((double)yawError * ease), (double)(-cap), (double)cap) / degreesPerCount;
        this.pendingPitch += class_3532.method_15350((double)((double)pitchError * ease * 0.6), (double)(-cap), (double)cap) / degreesPerCount;
        long yawCounts = (long)this.pendingYaw;
        long pitchCounts = (long)this.pendingPitch;
        this.pendingYaw -= (double)yawCounts;
        this.pendingPitch -= (double)pitchCounts;
        return new double[]{(double)yawCounts * k, (double)pitchCounts * k};
    }

    private void follow(class_1657 next) {
        if (next == this.target) {
            return;
        }
        this.target = next;
        this.pace = ThreadLocalRandom.current().nextDouble(0.85, 1.15);
        this.pendingYaw = 0.0;
        this.pendingPitch = 0.0;
    }

    private class_1657 choose(class_310 mc, class_746 player) {
        class_1657 hit;
        class_243 eye = player.method_33571();
        double halfFov = (double)this.fov.get() / 2.0;
        class_1309 class_13092 = CombatTarget.lastHit(1500L);
        if (class_13092 instanceof class_1657 && this.offAngle(player, eye, hit = (class_1657)class_13092, this.range.get()) <= halfFov) {
            return hit;
        }
        if (this.target != null && this.target.method_73183() == player.method_73183() && this.offAngle(player, eye, this.target, (double)this.range.get() + 0.5) <= halfFov * 1.3) {
            return this.target;
        }
        class_1657 best = null;
        double bestAngle = halfFov;
        for (class_1657 candidate : mc.field_1687.method_18456()) {
            double angle = this.offAngle(player, eye, candidate, this.range.get());
            if (angle > bestAngle) continue;
            best = candidate;
            bestAngle = angle;
        }
        return best;
    }

    private double offAngle(class_746 player, class_243 eye, class_1657 candidate, double reach) {
        if (!MeleeAttacks.isEnemy(player, (class_1297)candidate) || candidate.method_5767() && !AimAssistModule.armourShows(candidate)) {
            return Double.MAX_VALUE;
        }
        if (eye.method_1022(TargetingUtils.closestPoint(eye, candidate.method_5829())) > reach) {
            return Double.MAX_VALUE;
        }
        class_238 core = AimAssistModule.core(candidate);
        if (!MeleeAttacks.lineIsClear(player, eye, core.method_1005())) {
            return Double.MAX_VALUE;
        }
        Rotation want = this.aim(player, eye, core);
        return want == null ? 0.0 : RotationUtils.angleTo(RotationUtils.entityRotation((class_1297)player), want);
    }

    private Rotation aim(class_746 player, class_243 eye, class_238 core) {
        class_243 view = player.method_5828(1.0f);
        if (core.method_992(eye, eye.method_1019(view.method_1021((double)this.range.get() + 1.0))).isPresent()) {
            return null;
        }
        class_243 near = TargetingUtils.closestPoint(eye.method_1019(view.method_1021(eye.method_1022(core.method_1005()))), core);
        return RotationUtils.fromTo(eye, near);
    }

    private static boolean armourShows(class_1657 player) {
        for (class_1304 slot : class_1304.field_54086) {
            if (!slot.method_46643() || player.method_6118(slot).method_7960()) continue;
            return true;
        }
        return false;
    }

    private static class_238 core(class_1657 player) {
        class_238 box = player.method_5829();
        return box.method_35580(box.method_17939() * 0.25, box.method_17940() * 0.1, box.method_17939() * 0.25);
    }
}

