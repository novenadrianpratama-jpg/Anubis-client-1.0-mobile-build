/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12125
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.combat;

import net.anubis.client.event.EventHandler;
import net.anubis.client.event.EventPriority;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.pvp.InputControl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12125;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class WTapRecodeModule
extends ClientModule {
    private static final String W_TAP = "W Tap";
    private static final String S_TAP = "S Tap";
    private final ModeSetting mode = this.addSetting("mode", new ModeSetting("Mode", "W Tap", "W Tap", "S Tap"));
    private final NumberSetting hold = this.addSetting("hold", new NumberSetting("Hold", 1.0f, 1.0f, 3.0f).step(1.0f));
    private boolean sprintHit;
    private int taps;
    private int tapTick;

    public WTapRecodeModule() {
        super("w-tap-recode", W_TAP, "Resets your sprint right after a sprint hit so the next hit knocks back too.", ModuleCategory.COMBAT);
    }

    @Override
    protected void onDisable() {
        this.taps = 0;
    }

    public void beforeAttack(class_1657 player, class_1297 target) {
        class_746 self = class_310.method_1551().field_1724;
        this.sprintHit = player == self && self.method_5624() && MeleeAttacks.charge((class_1657)self) > 0.9f && target instanceof class_1309 && self.field_3913.field_54155.comp_3159();
    }

    public void afterAttack() {
        if (this.sprintHit) {
            this.start(class_310.method_1551().field_1724);
        }
    }

    public void afterStab() {
        class_746 player = class_310.method_1551().field_1724;
        if (!player.method_5624() || !player.field_3913.field_54155.comp_3159()) {
            return;
        }
        if (MeleeAttacks.stabTarget(player, entity -> class_12125.method_75244((class_1297)player, (class_1297)entity)) != null) {
            this.start(player);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onTick(TickEvent event) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            this.taps = 0;
            return;
        }
        if (this.taps > 0 && player.field_6012 != this.tapTick) {
            this.apply();
        }
    }

    private void start(class_746 player) {
        this.taps = this.hold.getInt();
        this.tapTick = player.field_6012;
        this.apply();
    }

    private void apply() {
        InputControl.releaseForward(this.mode.is(S_TAP));
        --this.taps;
    }
}

