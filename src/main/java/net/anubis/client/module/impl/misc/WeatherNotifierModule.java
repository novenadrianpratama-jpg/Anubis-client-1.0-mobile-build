/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_368
 *  net.minecraft.class_3928
 *  net.minecraft.class_638
 */
package net.anubis.client.module.impl.misc;

import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.gui.ChallengeToast;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.BooleanSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_368;
import net.minecraft.class_3928;
import net.minecraft.class_638;

@Environment(value=EnvType.CLIENT)
public final class WeatherNotifierModule
extends ClientModule {
    private static final int STABLE_TICKS = 20;
    private final BooleanSetting sound = this.addSetting("sound", new BooleanSetting("Sound", true));
    private class_638 level;
    private boolean raining;
    private int changedTicks;

    public WeatherNotifierModule() {
        super("weather-notifier", "Weather Notifier", "Shows an advancement-style toast when it starts raining.", ModuleCategory.MISC);
    }

    @Override
    protected void onEnable() {
        this.level = null;
        this.raining = false;
        this.changedTicks = 0;
        this.on(TickEvent.TYPE, Stage.POST, this::onTick);
    }

    @Override
    protected void onDisable() {
        this.level = null;
    }

    private void onTick(TickEvent event) {
        class_310 mc = class_310.method_1551();
        class_638 current = mc.field_1687;
        if (current != this.level) {
            this.level = current;
            this.raining = false;
            this.changedTicks = 0;
        }
        if (current == null || mc.field_1755 instanceof class_3928) {
            this.changedTicks = 0;
            return;
        }
        if (current.method_8419() == this.raining) {
            this.changedTicks = 0;
            return;
        }
        if (++this.changedTicks < 20) {
            return;
        }
        this.raining = !this.raining;
        this.changedTicks = 0;
        if (this.raining) {
            mc.method_1566().method_1999((class_368)new WeatherToast(this.sound.get()));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class WeatherToast
    extends ChallengeToast {
        private static final class_2561 TITLE = class_2561.method_43470((String)"Weather Changed!");
        private static final class_2561 MESSAGE = class_2561.method_43470((String)"It started raining.");

        WeatherToast(boolean sound) {
            super(new class_1799((class_1935)class_1802.field_8547), TITLE, MESSAGE, sound);
        }
    }
}

