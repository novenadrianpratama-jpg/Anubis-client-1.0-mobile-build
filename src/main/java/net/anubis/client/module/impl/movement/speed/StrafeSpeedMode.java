/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_241
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.movement.speed;

import net.anubis.client.module.impl.movement.SpeedModule;
import net.anubis.client.module.impl.movement.speed.SpeedMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class StrafeSpeedMode
extends SpeedMode {
    private long timer = 0L;

    public StrafeSpeedMode(SpeedModule settings) {
        super(settings);
    }

    @Override
    public class_243 onMove(class_746 player, class_243 movement) {
        boolean moving = SpeedModule.isMoving(player);
        double y = movement.field_1351;
        switch (this.stage) {
            case 0: {
                if (moving) {
                    ++this.stage;
                    this.speed = (double)1.18f * this.getDefaultSpeed(player) - 0.01;
                }
            }
            case 1: {
                if (!moving || !player.method_24828()) break;
                y = this.getHop(player, 0.40123128);
                this.speed *= this.settings.strafeSpeed();
                ++this.stage;
                break;
            }
            case 2: {
                this.speed = this.distance - 0.76 * (this.distance - this.getDefaultSpeed(player));
                ++this.stage;
                break;
            }
            case 3: {
                if (!player.method_73183().method_18026(player.method_5829().method_989(0.0, player.method_18798().field_1351, 0.0)) || player.field_5992 && this.stage > 0) {
                    this.stage = 0;
                }
                this.speed = this.distance - this.distance / 159.0;
            }
        }
        this.speed = Math.max(this.speed, this.getDefaultSpeed(player));
        if (this.settings.speedLimit()) {
            if (System.currentTimeMillis() - this.timer > 2500L) {
                this.timer = System.currentTimeMillis();
            }
            this.speed = Math.min(this.speed, System.currentTimeMillis() - this.timer > 1250L ? 0.44 : 0.43);
        }
        double[] change = StrafeSpeedMode.transformStrafe(player, this.speed);
        return new class_243(change[0], y, change[1]);
    }

    private static double[] transformStrafe(class_746 player, double speed) {
        class_241 input = player.field_3913.method_3128();
        float forward = Math.signum(input.field_1342);
        float side = Math.signum(input.field_1343);
        float yaw = player.method_61415(class_310.method_1551().method_61966().method_60637(true));
        if (forward == 0.0f && side == 0.0f) {
            return new double[2];
        }
        float strafe = 90.0f * side;
        if (forward != 0.0f) {
            strafe *= forward * 0.5f;
        }
        yaw -= strafe;
        if (forward < 0.0f) {
            yaw -= 180.0f;
        }
        double yawRadians = Math.toRadians(yaw);
        return new double[]{-Math.sin(yawRadians) * speed, Math.cos(yawRadians) * speed};
    }

    @Override
    public void onTick(class_746 player) {
        double dx = player.method_23317() - player.field_6014;
        double dz = player.method_23321() - player.field_5969;
        this.distance = Math.sqrt(dx * dx + dz * dz);
    }
}

