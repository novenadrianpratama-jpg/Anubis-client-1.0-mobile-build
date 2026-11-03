/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1656
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2696
 *  net.minecraft.class_2793
 *  net.minecraft.class_2828
 *  net.minecraft.class_2828$class_2829
 *  net.minecraft.class_2828$class_2830
 *  net.minecraft.class_310
 *  net.minecraft.class_4970$class_4971
 *  net.minecraft.class_634
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.movement;

import com.example.client.mixin.AccessorLocalPlayer;
import com.example.client.mixin.AccessorServerboundMovePlayerPacket;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.event.impl.PacketSendEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.misc.FreecamModule;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1656;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2696;
import net.minecraft.class_2793;
import net.minecraft.class_2828;
import net.minecraft.class_310;
import net.minecraft.class_4970;
import net.minecraft.class_634;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class FlyModule
extends ClientModule {
    public static volatile FlyModule active;
    private static final String ABILITIES = "Abilities";
    private static final String VELOCITY = "Velocity";
    private static final String KICK_NORMAL = "Normal";
    private static final String KICK_PACKET = "Packet";
    private static final String KICK_NONE = "None";
    private static final double ANTI_KICK_DROP = 0.0313;
    private static final float VANILLA_FLYING_SPEED = 0.05f;
    private static final String DEFAULT_MODE = "Abilities";
    private static final float DEFAULT_SPEED = 0.1f;
    private final ModeSetting mode = this.addSetting("fly-mode", new ModeSetting("Mode", "Abilities", "Abilities", "Velocity"));
    private final NumberSetting speed = this.addSetting("fly-speed", new NumberSetting("Speed", 0.1f, 0.0f, 10.0f).step(0.05f));
    private final BooleanSetting verticalSpeedMatch = this.addSetting("vertical-speed-match", new BooleanSetting("Vertical Speed Match", false));
    private final BooleanSetting noSneak = this.addSetting("no-sneak", new BooleanSetting("No Sneak", false));
    private final ModeSetting antiKickMode = this.addSetting("anti-kick-mode", new ModeSetting("Anti Kick Mode", "Packet", "Normal", "Packet", "None"));
    private final NumberSetting delay = this.addSetting("delay", new NumberSetting("Anti Kick Delay", 20.0f, 1.0f, 200.0f).step(1.0f));
    private final NumberSetting offTime = this.addSetting("off-time", new NumberSetting("Anti Kick Off Time", 1.0f, 1.0f, 20.0f).step(1.0f));
    private int delayLeft = 20;
    private int offLeft = 1;
    private boolean flip;
    private float lastYaw;
    private double lastPacketY = Double.MAX_VALUE;
    private class_746 trackedPlayer;

    public FlyModule() {
        super("fly", "Fly", "Lets you fly.", ModuleCategory.MOVEMENT);
        this.noSneak.visibleWhen(() -> this.mode.is(VELOCITY));
        this.mode.onChange(() -> {
            if (!this.isEnabled()) {
                return;
            }
            class_746 player = class_310.method_1551().field_1724;
            if (player != null) {
                FlyModule.abilitiesOff(player);
            }
        });
    }

    @Override
    public boolean persistEnabled() {
        return false;
    }

    @Override
    protected void onEnable() {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            this.requestDisable();
            return;
        }
        this.delayLeft = this.delay.getInt();
        this.offLeft = this.offTime.getInt();
        this.resetPlayerState(player);
        active = this;
        if (this.mode.is("Abilities") && !player.method_7325()) {
            class_1656 abilities = player.method_31549();
            abilities.field_7479 = true;
            if (!abilities.field_7477) {
                abilities.field_7478 = true;
            }
        }
        this.on(TickEvent.TYPE, Stage.PRE, this::onPreTick);
        this.on(TickEvent.TYPE, Stage.POST, this::onPostTick);
        this.on(PacketSendEvent.TYPE, this::onPacketSend);
        this.on(PacketReceiveNettyEvent.TYPE, this::onPacketReceiveNetty);
    }

    @Override
    protected void onDisable() {
        active = null;
        class_746 player = class_310.method_1551().field_1724;
        if (player != null && this.mode.is("Abilities")) {
            FlyModule.abilitiesOff(player);
        }
        this.trackedPlayer = null;
    }

    private void resetPlayerState(class_746 player) {
        this.trackedPlayer = player;
        this.lastYaw = player.method_36454();
        this.lastPacketY = Double.MAX_VALUE;
    }

    private void onPreTick(TickEvent event) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            this.requestDisable();
            return;
        }
        if (player != this.trackedPlayer) {
            this.resetPlayerState(player);
        }
        float currentYaw = player.method_36454();
        if (player.field_6017 >= 3.0 && currentYaw == this.lastYaw && player.method_18798().method_1033() < 0.003) {
            player.method_36456(currentYaw + (float)(this.flip ? 1 : -1));
            this.flip = !this.flip;
        }
        this.lastYaw = currentYaw;
    }

    private void onPostTick(TickEvent event) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        if (player != this.trackedPlayer) {
            this.resetPlayerState(player);
        }
        if (this.delayLeft > 0) {
            --this.delayLeft;
        }
        if (this.offLeft <= 0 && this.delayLeft <= 0) {
            this.delayLeft = this.delay.getInt();
            this.offLeft = this.offTime.getInt();
            if (this.antiKickMode.is(KICK_PACKET)) {
                FlyModule.forcePositionPacket(player);
            }
        } else if (this.delayLeft <= 0) {
            boolean shouldReturn = false;
            if (this.antiKickMode.is(KICK_NORMAL)) {
                if (this.mode.is("Abilities")) {
                    FlyModule.abilitiesOff(player);
                    shouldReturn = true;
                }
            } else if (this.antiKickMode.is(KICK_PACKET) && this.offLeft == this.offTime.getInt()) {
                FlyModule.forcePositionPacket(player);
            }
            --this.offLeft;
            if (shouldReturn) {
                return;
            }
        }
        if (player.method_36454() != this.lastYaw) {
            player.method_36456(this.lastYaw);
        }
        if (this.mode.is(VELOCITY)) {
            if (!player.method_7325()) {
                player.method_31549().field_7479 = false;
            }
            player.method_18800(0.0, 0.0, 0.0);
            class_243 velocity = player.method_18798();
            if (!FlyModule.isFreecamActive()) {
                class_310 mc = class_310.method_1551();
                double vertical = (double)this.speed.get() * (double)(this.verticalSpeedMatch.get() ? 10.0f : 5.0f);
                if (mc.field_1690.field_1903.method_1434()) {
                    velocity = velocity.method_1031(0.0, vertical, 0.0);
                }
                if (mc.field_1690.field_1832.method_1434()) {
                    velocity = velocity.method_1023(0.0, vertical, 0.0);
                }
            }
            player.method_18799(velocity);
            if (this.noSneak.get()) {
                player.method_24830(false);
            }
        } else {
            if (player.method_7325()) {
                return;
            }
            class_1656 abilities = player.method_31549();
            abilities.method_7248(this.speed.get());
            abilities.field_7479 = true;
            if (abilities.field_7477) {
                return;
            }
            abilities.field_7478 = true;
        }
    }

    private void onPacketSend(PacketSendEvent event) {
        class_2828 packet;
        class_310 mc;
        block9: {
            block8: {
                mc = class_310.method_1551();
                if (!mc.method_18854()) {
                    return;
                }
                if (event.packet instanceof class_2793) {
                    this.lastPacketY = Double.MAX_VALUE;
                    return;
                }
                class_2596<?> class_25962 = event.packet;
                if (!(class_25962 instanceof class_2828)) break block8;
                packet = (class_2828)class_25962;
                if (this.antiKickMode.is(KICK_PACKET)) break block9;
            }
            return;
        }
        class_746 player = mc.field_1724;
        class_634 connection = mc.method_1562();
        if (player == null || connection == null) {
            return;
        }
        double currentY = packet.method_12268(Double.MAX_VALUE);
        if (currentY != Double.MAX_VALUE) {
            this.antiKickPacket(player, packet, currentY);
        } else {
            Object fullPacket = packet.method_36172() ? new class_2828.class_2830(player.method_23317(), player.method_23318(), player.method_23321(), packet.method_12271(0.0f), packet.method_12270(0.0f), packet.method_12273(), player.field_5976) : new class_2828.class_2829(player.method_23317(), player.method_23318(), player.method_23321(), packet.method_12273(), player.field_5976);
            event.setCancelled(true);
            this.antiKickPacket(player, (class_2828)fullPacket, player.method_23318());
            connection.method_52787((class_2596)fullPacket);
        }
    }

    private void antiKickPacket(class_746 player, class_2828 packet, double currentY) {
        if (this.delayLeft <= 0 && this.lastPacketY != Double.MAX_VALUE && FlyModule.shouldFlyDown(currentY, this.lastPacketY) && FlyModule.isOnAir(player)) {
            ((AccessorServerboundMovePlayerPacket)packet).anubis$setY(this.lastPacketY - 0.0313);
        } else {
            this.lastPacketY = currentY;
        }
    }

    private void onPacketReceiveNetty(PacketReceiveNettyEvent event) {
        class_2696 packet;
        block3: {
            block2: {
                class_2596<?> class_25962 = event.packet;
                if (!(class_25962 instanceof class_2696)) break block2;
                packet = (class_2696)class_25962;
                if (this.mode.is("Abilities")) break block3;
            }
            return;
        }
        event.setCancelled(true);
        class_310 mc = class_310.method_1551();
        mc.execute(() -> {
            class_746 player = mc.field_1724;
            if (player == null) {
                return;
            }
            class_1656 abilities = player.method_31549();
            abilities.field_7480 = packet.method_11695();
            abilities.field_7477 = packet.method_11696();
            abilities.method_7250(packet.method_11691());
        });
    }

    private static boolean shouldFlyDown(double currentY, double lastY) {
        if (currentY >= lastY) {
            return true;
        }
        return lastY - currentY < 0.0313;
    }

    private static void forcePositionPacket(class_746 player) {
        ((AccessorLocalPlayer)player).anubis$setPositionReminder(20);
    }

    private static boolean isOnAir(class_746 player) {
        class_238 box = player.method_5829().method_1014(0.0625).method_1012(0.0, -0.55, 0.0);
        return player.method_73183().method_29546(box).allMatch(class_4970.class_4971::method_26215);
    }

    private static boolean isFreecamActive() {
        FreecamModule freecam = ModuleRegistry.get(FreecamModule.class);
        return freecam != null && freecam.isEnabled();
    }

    private static void abilitiesOff(class_746 player) {
        if (player.method_7325()) {
            return;
        }
        class_1656 abilities = player.method_31549();
        abilities.field_7479 = false;
        abilities.method_7248(0.05f);
        if (abilities.field_7477) {
            return;
        }
        abilities.field_7478 = false;
    }

    public float getOffGroundSpeed(class_746 player) {
        if (!this.isEnabled() || !this.mode.is(VELOCITY)) {
            return -1.0f;
        }
        return this.speed.get() * (player.method_5624() ? 15.0f : 10.0f);
    }

    public boolean noSneak() {
        return this.isEnabled() && this.mode.is(VELOCITY) && this.noSneak.get();
    }

    public boolean suppressFlightVerticalInput() {
        return this.isEnabled() && FlyModule.isFreecamActive();
    }
}

