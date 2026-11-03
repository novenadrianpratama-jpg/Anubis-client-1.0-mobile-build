/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_238
 *  net.minecraft.class_239
 *  net.minecraft.class_2390
 *  net.minecraft.class_2394
 *  net.minecraft.class_2398
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3966
 *  net.minecraft.class_5819
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.render;

import net.anubis.client.event.EventPriority;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.PlayerAttackEntityEvent;
import net.anubis.client.gui.ThemeTint;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.render.CosmeticAccent;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_2390;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3966;
import net.minecraft.class_5819;
import net.minecraft.class_638;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class HitParticlesModule
extends ClientModule {
    private static final String TYPE_CRIT = "Crit";
    private static final String TYPE_MAGIC_CRIT = "Magic Crit";
    private static final String TYPE_SPARK = "Spark";
    private static final String TYPE_FLAME = "Flame";
    private static final String TYPE_SOUL_FLAME = "Soul Flame";
    private static final String TYPE_END_ROD = "End Rod";
    private static final String TYPE_TOTEM = "Totem";
    private static final String TYPE_HEART = "Heart";
    private static final String TYPE_DUST = "Dust";
    private static final String ORIGIN_IMPACT = "Impact";
    private static final String ORIGIN_BODY = "Body";
    private static final String TARGETS_LIVING = "Living";
    private static final String TARGETS_PLAYERS = "Players";
    private static final String TARGETS_ALL = "All";
    private static final String COLOR_CUSTOM = "Custom";
    private static final String COLOR_ACCENT = "Accent";
    private static final double LIFT = 0.25;
    private static final int PEEP_DUST = 16743112;
    private final ModeSetting type = this.addSetting("type", new ModeSetting("Type", "Crit", "Crit", "Magic Crit", "Spark", "Flame", "Soul Flame", "End Rod", "Totem", "Heart", "Dust"));
    private final NumberSetting amount = this.addSetting("amount", new NumberSetting("Amount", 10.0f, 1.0f, 40.0f).step(1.0f));
    private final NumberSetting spread = this.addSetting("spread", new NumberSetting("Spread", 1.0f, 0.25f, 3.0f).step(0.25f));
    private final ModeSetting origin = this.addSetting("origin", new ModeSetting("Origin", "Impact", "Impact", "Body"));
    private final BooleanSetting onlyCrits = this.addSetting("only-crits", new BooleanSetting("Only Crits", false));
    private final ModeSetting targets = this.addSetting("targets", new ModeSetting("Targets", "Living", "Living", "Players", "All"));
    private final ModeSetting dustColorMode = this.addSetting("dust-color-mode", new ModeSetting("Color", "Accent", "Custom", "Accent"));
    private final ColorSetting dustColor = this.addSetting("dust-color", new ColorSetting("Custom Color", -657929));
    private final NumberSetting dustSize = this.addSetting("dust-size", new NumberSetting("Dust Size", 1.0f, 0.5f, 2.0f).step(0.1f));
    private final class_5819 random = class_5819.method_43047();

    public HitParticlesModule() {
        super("hit-particles", "Hit Particles", "Spawns particles where your hits land.", ModuleCategory.VISUALS);
        this.dustColorMode.visibleWhen(() -> this.type.is(TYPE_DUST));
        this.dustColor.visibleWhen(() -> this.type.is(TYPE_DUST) && this.dustColorMode.is(COLOR_CUSTOM));
        this.dustSize.visibleWhen(() -> this.type.is(TYPE_DUST));
    }

    @Override
    protected void onEnable() {
        this.on(PlayerAttackEntityEvent.TYPE, Stage.PRE, EventPriority.LOWEST, this::onAttack);
    }

    private void onAttack(PlayerAttackEntityEvent event) {
        class_310 mc = class_310.method_1551();
        if (!mc.method_18854()) {
            return;
        }
        class_746 player = mc.field_1724;
        class_638 level = mc.field_1687;
        class_1297 target = event.target;
        if (player == null || level == null || target == null || target == player) {
            return;
        }
        if (!target.method_5732() || !this.accepts(target)) {
            return;
        }
        if (this.onlyCrits.get() && !HitParticlesModule.isCritical(player, target)) {
            return;
        }
        this.spawnBurst(mc, level, player, target);
    }

    private boolean accepts(class_1297 target) {
        return switch (this.targets.get()) {
            case TARGETS_PLAYERS -> target instanceof class_1657;
            case TARGETS_ALL -> true;
            default -> target instanceof class_1309;
        };
    }

    private static boolean isCritical(class_746 player, class_1297 target) {
        return player.method_7261(0.5f) > 0.9f && player.field_6017 > 0.0 && !player.method_24828() && !player.method_6101() && !player.method_5799() && !player.method_74025() && !player.method_5765() && !player.method_5624() && target instanceof class_1309;
    }

    private void spawnBurst(class_310 mc, class_638 level, class_746 player, class_1297 target) {
        Burst burst = this.burst();
        class_238 box = target.method_5829();
        class_243 center = box.method_1005();
        boolean body = this.origin.is(ORIGIN_BODY);
        class_243 impact = body ? null : HitParticlesModule.impactPoint(mc, player, target, box);
        class_243 fallbackOut = player.method_5720().method_22882();
        double spreadScale = this.spread.get();
        double jitter = burst.jitter() * spreadScale;
        int count = this.amount.getInt();
        float pink = this.type.is(TYPE_DUST) ? 0.0f : ThemeTint.world();
        class_2390 pinkDust = pink > 0.0f ? new class_2390(16743112, 0.9f) : null;
        for (int i = 0; i < count; ++i) {
            class_243 from = body ? this.randomPointIn(box) : impact;
            class_243 out = from.method_1020(center);
            out = out.method_1027() < 1.0E-6 ? fallbackOut : out.method_1029();
            class_243 dir = out.method_1021(0.8).method_1019(this.randomUnit()).method_1031(0.0, 0.25, 0.0).method_1029();
            double speed = burst.speed() * spreadScale * (0.6 + 0.4 * this.random.method_43058());
            class_2394 particle = pinkDust != null && this.random.method_43057() < pink ? pinkDust : burst.particle();
            level.method_8466(particle, true, false, from.field_1352 + this.random.method_43059() * jitter, from.field_1351 + this.random.method_43059() * jitter, from.field_1350 + this.random.method_43059() * jitter, dir.field_1352 * speed, dir.field_1351 * speed, dir.field_1350 * speed);
        }
    }

    private Burst burst() {
        return switch (this.type.get()) {
            case TYPE_MAGIC_CRIT -> new Burst((class_2394)class_2398.field_11208, 0.8, 0.1);
            case TYPE_SPARK -> new Burst((class_2394)class_2398.field_29644, 0.16, 0.1);
            case TYPE_FLAME -> new Burst((class_2394)class_2398.field_11240, 0.035, 0.12);
            case TYPE_SOUL_FLAME -> new Burst((class_2394)class_2398.field_22246, 0.035, 0.12);
            case TYPE_END_ROD -> new Burst((class_2394)class_2398.field_11207, 0.09, 0.1);
            case TYPE_TOTEM -> new Burst((class_2394)class_2398.field_11220, 0.45, 0.1);
            case TYPE_HEART -> new Burst((class_2394)class_2398.field_11201, 0.0, 0.35);
            case TYPE_DUST -> new Burst((class_2394)new class_2390(this.dustRgb(), this.dustSize.get()), 1.0, 0.3);
            default -> new Burst((class_2394)class_2398.field_11205, 0.8, 0.1);
        };
    }

    private int dustRgb() {
        int argb = this.dustColorMode.is(COLOR_ACCENT) ? CosmeticAccent.argb() : ThemeTint.world(this.dustColor.argb());
        return argb & 0xFFFFFF;
    }

    private static class_243 impactPoint(class_310 mc, class_746 player, class_1297 target, class_238 box) {
        class_3966 hit;
        class_239 class_2392 = mc.field_1765;
        if (class_2392 instanceof class_3966 && (hit = (class_3966)class_2392).method_17782() == target) {
            return hit.method_17784();
        }
        class_243 eye = player.method_33571();
        return new class_243(class_3532.method_15350((double)eye.field_1352, (double)box.field_1323, (double)box.field_1320), class_3532.method_15350((double)eye.field_1351, (double)box.field_1322, (double)box.field_1325), class_3532.method_15350((double)eye.field_1350, (double)box.field_1321, (double)box.field_1324));
    }

    private class_243 randomPointIn(class_238 box) {
        return new class_243(box.field_1323 + box.method_17939() * (0.1 + 0.8 * this.random.method_43058()), box.field_1322 + box.method_17940() * (0.1 + 0.8 * this.random.method_43058()), box.field_1321 + box.method_17941() * (0.1 + 0.8 * this.random.method_43058()));
    }

    private class_243 randomUnit() {
        class_243 v = new class_243(this.random.method_43059(), this.random.method_43059(), this.random.method_43059());
        return v.method_1027() < 1.0E-9 ? new class_243(0.0, 1.0, 0.0) : v.method_1029();
    }

    @Environment(value=EnvType.CLIENT)
    private record Burst(class_2394 particle, double speed, double jitter) {
    }
}

