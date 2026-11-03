/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1657
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2848
 *  net.minecraft.class_2848$class_2849
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.movement.elytrafly;

import net.anubis.client.event.impl.PlayerMoveEvent;
import net.anubis.client.module.impl.movement.ElytraFlyModule;
import net.anubis.client.module.impl.movement.elytrafly.ElytraFlightModes;
import net.anubis.client.module.impl.movement.elytrafly.ElytraFlyInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2848;
import net.minecraft.class_310;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public abstract class ElytraFlightMode {
    protected final class_310 mc = class_310.method_1551();
    protected final ElytraFlyModule elytraFly;
    protected boolean lastJumpPressed;
    protected boolean incrementJumpTimer;
    public boolean lastForwardPressed;
    protected int jumpTimer;
    public double velX;
    public double velY;
    public double velZ;
    protected double ticksLeft;
    public class_243 forward;
    public class_243 right;
    protected double acceleration;

    protected ElytraFlightMode(ElytraFlyModule elytraFly) {
        this.elytraFly = elytraFly;
    }

    public void onTick() {
        class_1799 chestStack;
        int fireworks;
        class_746 player = this.mc.field_1724;
        if (player == null) {
            return;
        }
        if (this.elytraFly.autoReplenish.get() && (fireworks = ElytraFlyInventory.find(stack -> stack.method_7909() == class_1802.field_8639)) != -1 && !ElytraFlyInventory.isHotbar(fireworks)) {
            ElytraFlyInventory.moveToHotbar(fireworks, this.elytraFly.replenishSlot.getInt() - 1);
        }
        if (this.elytraFly.replace.get() && (chestStack = player.method_6118(class_1304.field_6174)).method_7909() == class_1802.field_8833) {
            int threshold = this.elytraFly.replaceDurability.getInt();
            if (chestStack.method_7936() - chestStack.method_7919() <= threshold) {
                int elytra = ElytraFlyInventory.find(stack -> stack.method_7936() - stack.method_7919() > threshold && stack.method_7909() == class_1802.field_8833);
                ElytraFlyInventory.moveToChest(elytra);
            }
        }
    }

    public void onPreTick() {
    }

    public void onPacketSend(class_2596<?> packet) {
    }

    public void onPositionPacket() {
    }

    public void onPlayerMove() {
    }

    public void onActivate() {
        this.lastJumpPressed = false;
        this.jumpTimer = 0;
        this.ticksLeft = 0.0;
        this.acceleration = 0.0;
    }

    public void onDeactivate() {
    }

    public void autoTakeoff() {
        class_746 player = this.mc.field_1724;
        if (this.incrementJumpTimer) {
            ++this.jumpTimer;
        }
        boolean jumpPressed = this.mc.field_1690.field_1903.method_1434();
        ElytraFlightModes mode = this.elytraFly.flightModeType();
        if (this.elytraFly.autoTakeOff.get() && mode != ElytraFlightModes.Pitch40 && mode != ElytraFlightModes.Bounce || !this.elytraFly.manualTakeoff.get() && mode == ElytraFlightModes.Bounce && jumpPressed) {
            if (!this.lastJumpPressed && !player.method_6128()) {
                this.jumpTimer = 0;
                this.incrementJumpTimer = true;
            }
            if (this.jumpTimer >= 8) {
                this.jumpTimer = 0;
                this.incrementJumpTimer = false;
                player.method_6100(false);
                player.method_5728(true);
                player.method_6043();
                player.field_3944.method_52787((class_2596)new class_2848((class_1297)player, class_2848.class_2849.field_12982));
            }
        }
        this.lastJumpPressed = jumpPressed;
    }

    public void handleAutopilot() {
        class_746 player = this.mc.field_1724;
        if (!player.method_6128()) {
            return;
        }
        if (this.elytraFly.autoPilot.get() && player.method_23318() > (double)this.elytraFly.autoPilotMinimumHeight.get() && this.elytraFly.flightModeType() != ElytraFlightModes.Bounce) {
            this.mc.field_1690.field_1894.method_23481(true);
            this.lastForwardPressed = true;
        }
        if (this.elytraFly.useFireworks.get()) {
            if (this.ticksLeft <= 0.0) {
                this.ticksLeft = this.elytraFly.autoPilotFireworkDelay.get() * 20.0f;
                int slot = ElytraFlyInventory.findInHotbar(stack -> stack.method_7909() == class_1802.field_8639);
                if (slot == -1 || this.mc.field_1761 == null) {
                    return;
                }
                if (slot == 40) {
                    this.mc.field_1761.method_2919((class_1657)player, class_1268.field_5810);
                    player.method_6104(class_1268.field_5810);
                } else {
                    ElytraFlyInventory.swap(slot, true);
                    this.mc.field_1761.method_2919((class_1657)player, class_1268.field_5808);
                    player.method_6104(class_1268.field_5808);
                    ElytraFlyInventory.swapBack();
                }
            }
            this.ticksLeft -= 1.0;
        }
    }

    public void handleHorizontalSpeed(PlayerMoveEvent event) {
        boolean a = false;
        boolean b = false;
        if (this.mc.field_1690.field_1894.method_1434()) {
            this.velX += this.forward.field_1352 * this.getSpeed() * 10.0;
            this.velZ += this.forward.field_1350 * this.getSpeed() * 10.0;
            a = true;
        } else if (this.mc.field_1690.field_1881.method_1434()) {
            this.velX -= this.forward.field_1352 * this.getSpeed() * 10.0;
            this.velZ -= this.forward.field_1350 * this.getSpeed() * 10.0;
            a = true;
        }
        if (this.mc.field_1690.field_1849.method_1434()) {
            this.velX += this.right.field_1352 * this.getSpeed() * 10.0;
            this.velZ += this.right.field_1350 * this.getSpeed() * 10.0;
            b = true;
        } else if (this.mc.field_1690.field_1913.method_1434()) {
            this.velX -= this.right.field_1352 * this.getSpeed() * 10.0;
            this.velZ -= this.right.field_1350 * this.getSpeed() * 10.0;
            b = true;
        }
        if (a && b) {
            double diagonal = 1.0 / Math.sqrt(2.0);
            this.velX *= diagonal;
            this.velZ *= diagonal;
        }
    }

    public void handleVerticalSpeed(PlayerMoveEvent event) {
        if (this.mc.field_1690.field_1903.method_1434()) {
            this.velY += 0.5 * (double)this.elytraFly.verticalSpeed.get();
        } else if (this.mc.field_1690.field_1832.method_1434()) {
            this.velY -= 0.5 * (double)this.elytraFly.verticalSpeed.get();
        }
    }

    public void handleFallMultiplier() {
        if (this.velY < 0.0) {
            this.velY *= (double)this.elytraFly.fallMultiplier.get();
        } else if (this.velY > 0.0) {
            this.velY = 0.0;
        }
    }

    public void handleAcceleration() {
        if (this.elytraFly.acceleration.get()) {
            if (!this.isMoving()) {
                this.acceleration = 0.0;
            }
            this.acceleration = Math.min(this.acceleration + (double)this.elytraFly.accelerationMin.get() + (double)this.elytraFly.accelerationStep.get() * 0.1, (double)this.elytraFly.horizontalSpeed.get());
        } else {
            this.acceleration = 0.0;
        }
    }

    public void zeroAcceleration() {
        this.acceleration = 0.0;
    }

    protected double getSpeed() {
        return this.elytraFly.acceleration.get() ? this.acceleration : (double)this.elytraFly.horizontalSpeed.get();
    }

    private boolean isMoving() {
        class_746 player = this.mc.field_1724;
        return player.field_6250 != 0.0f || player.field_6212 != 0.0f;
    }
}

