/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2708
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  net.minecraft.class_8042
 */
package net.anubis.client.module.impl.movement;

import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.movement.speed.SpeedMode;
import net.anubis.client.module.impl.movement.speed.StrafeSpeedMode;
import net.anubis.client.module.impl.movement.speed.VanillaSpeedMode;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2708;
import net.minecraft.class_310;
import net.minecraft.class_746;
import net.minecraft.class_8042;

@Environment(value=EnvType.CLIENT)
public final class SpeedModule
extends ClientModule {
    public static volatile SpeedModule active;
    public static volatile float timerMultiplier;
    private final ModeSetting mode = this.addSetting("mode", new ModeSetting("Mode", "Vanilla", "Strafe", "Vanilla"));
    private final NumberSetting vanillaSpeed = this.addSetting("vanilla-speed", new NumberSetting("Vanilla Speed", 5.6f, 0.0f, 20.0f).step(0.1f));
    private final NumberSetting strafeSpeed = this.addSetting("strafe-speed", new NumberSetting("Strafe Speed", 1.6f, 0.0f, 3.0f).step(0.1f));
    private final BooleanSetting speedLimit = this.addSetting("speed-limit", new BooleanSetting("Speed Limit", false));
    private final NumberSetting timer = this.addSetting("timer", new NumberSetting("Timer", 1.0f, 0.01f, 10.0f).step(0.01f));
    private final BooleanSetting inLiquids = this.addSetting("in-liquids", new BooleanSetting("In Liquids", false));
    private final BooleanSetting whenSneaking = this.addSetting("when-sneaking", new BooleanSetting("When Sneaking", false));
    private final BooleanSetting onlyOnGround = this.addSetting("only-on-ground", new BooleanSetting("Only On Ground", false));
    private SpeedMode currentMode;
    private volatile boolean rubberband;

    public SpeedModule() {
        super("speed", "Speed", "Modifies your movement speed when moving on the ground.", ModuleCategory.MOVEMENT);
        this.vanillaSpeed.visibleWhen(() -> this.mode.is("Vanilla"));
        this.strafeSpeed.visibleWhen(() -> this.mode.is("Strafe"));
        this.speedLimit.visibleWhen(() -> this.mode.is("Strafe"));
        this.onlyOnGround.visibleWhen(() -> this.mode.is("Vanilla"));
        this.mode.onChange(() -> {
            this.currentMode = this.createMode();
        });
        this.currentMode = this.createMode();
    }

    @Override
    protected void onEnable() {
        this.currentMode = this.createMode();
        this.rubberband = false;
        active = this;
        this.currentMode.onActivate();
        this.on(TickEvent.TYPE, Stage.PRE, this::onPreTick);
        this.on(PacketReceiveNettyEvent.TYPE, this::onPacketReceive);
    }

    @Override
    protected void onDisable() {
        active = null;
        timerMultiplier = 1.0f;
        this.currentMode.onDeactivate();
    }

    private SpeedMode createMode() {
        return this.mode.is("Strafe") ? new StrafeSpeedMode(this) : new VanillaSpeedMode(this);
    }

    private void onPreTick(TickEvent event) {
        class_746 player;
        if (this.rubberband) {
            this.rubberband = false;
            this.currentMode.onRubberband();
        }
        if ((player = class_310.method_1551().field_1724) == null) {
            timerMultiplier = 1.0f;
            return;
        }
        if (this.stopSpeed(player)) {
            return;
        }
        this.currentMode.onTick(player);
    }

    private void onPacketReceive(PacketReceiveNettyEvent event) {
        if (event.packet instanceof class_2708) {
            this.rubberband = true;
        } else {
            class_2596<?> class_25962 = event.packet;
            if (class_25962 instanceof class_8042) {
                class_8042 bundle = (class_8042)class_25962;
                for (class_2596 packet : bundle.method_48324()) {
                    if (!(packet instanceof class_2708)) continue;
                    this.rubberband = true;
                    break;
                }
            }
        }
    }

    public class_243 onSelfMove(class_746 player, class_243 movement) {
        if (player != class_310.method_1551().field_1724 || this.stopSpeed(player)) {
            return movement;
        }
        float timerValue = this.timer.get();
        timerMultiplier = timerValue != 1.0f && SpeedModule.isMoving(player) ? timerValue : 1.0f;
        class_243 result = this.currentMode.onMove(player, movement);
        if (result != movement && movement == player.method_18798()) {
            player.method_18799(result);
        }
        return result;
    }

    private boolean stopSpeed(class_746 player) {
        if (player.method_6128() || player.method_6101() || player.method_5854() != null) {
            return true;
        }
        if (!this.whenSneaking.get() && player.method_5715()) {
            return true;
        }
        if (this.onlyOnGround.get() && !player.method_24828() && this.mode.is("Vanilla")) {
            return true;
        }
        return !this.inLiquids.get() && (player.method_5799() || player.method_5771());
    }

    public static boolean isMoving(class_746 player) {
        return player.field_6250 != 0.0f || player.field_6212 != 0.0f;
    }

    public double vanillaSpeed() {
        return this.vanillaSpeed.get();
    }

    public double strafeSpeed() {
        return this.strafeSpeed.get();
    }

    public boolean speedLimit() {
        return this.speedLimit.get();
    }

    static {
        timerMultiplier = 1.0f;
    }
}

