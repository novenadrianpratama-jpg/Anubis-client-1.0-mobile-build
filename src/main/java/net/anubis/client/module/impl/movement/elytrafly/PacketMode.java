/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1656
 *  net.minecraft.class_1802
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2828
 *  net.minecraft.class_2828$class_5911
 *  net.minecraft.class_2848
 *  net.minecraft.class_2848$class_2849
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.movement.elytrafly;

import net.anubis.client.module.impl.movement.ElytraFlyModule;
import net.anubis.client.module.impl.movement.elytrafly.ElytraFlightMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1656;
import net.minecraft.class_1802;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_2848;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class PacketMode
extends ElytraFlightMode {
    public PacketMode(ElytraFlyModule elytraFly) {
        super(elytraFly);
    }

    @Override
    public void onDeactivate() {
        class_746 player = this.mc.field_1724;
        if (player == null) {
            return;
        }
        class_1656 abilities = player.method_31549();
        abilities.field_7479 = false;
        if (!abilities.field_7477 && !player.method_7325()) {
            abilities.field_7478 = false;
        }
    }

    @Override
    public void onTick() {
        super.onTick();
        class_746 player = this.mc.field_1724;
        if (player == null) {
            return;
        }
        if (player.method_6118(class_1304.field_6174).method_7909() != class_1802.field_8833 || player.field_6017 <= 0.2 || this.mc.field_1690.field_1832.method_1434()) {
            return;
        }
        player.method_18799(class_243.field_1353);
        player.field_3944.method_52787((class_2596)new class_2848((class_1297)player, class_2848.class_2849.field_12982));
        player.field_3944.method_52787((class_2596)new class_2828.class_5911(true, player.field_5976));
    }

    @Override
    public void onPacketSend(class_2596<?> packet) {
        if (packet instanceof class_2828) {
            class_746 player = this.mc.field_1724;
            player.field_3944.method_52787((class_2596)new class_2848((class_1297)player, class_2848.class_2849.field_12982));
        }
    }

    @Override
    public void onPlayerMove() {
        class_1656 abilities = this.mc.field_1724.method_31549();
        abilities.field_7479 = true;
        abilities.method_7248(this.elytraFly.horizontalSpeed.get() / 20.0f);
    }
}

