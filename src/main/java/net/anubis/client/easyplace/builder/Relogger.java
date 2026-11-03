/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  net.minecraft.class_412
 *  net.minecraft.class_437
 *  net.minecraft.class_442
 *  net.minecraft.class_500
 *  net.minecraft.class_638
 *  net.minecraft.class_639
 *  net.minecraft.class_642
 */
package net.anubis.client.easyplace.builder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_412;
import net.minecraft.class_437;
import net.minecraft.class_442;
import net.minecraft.class_500;
import net.minecraft.class_638;
import net.minecraft.class_639;
import net.minecraft.class_642;

@Environment(value=EnvType.CLIENT)
final class Relogger {
    private static final int WAIT_TICKS = 60;
    private static final int TRIES = 5;
    private class_642 server;
    private boolean out;
    private int wait;
    private int tries;

    Relogger() {
    }

    boolean active() {
        return this.server != null;
    }

    boolean start(class_310 mc) {
        class_642 data;
        class_642 class_6422 = data = mc.method_1542() ? null : mc.method_1558();
        if (data == null || data.method_52811() || mc.field_1687 == null) {
            return false;
        }
        this.server = data;
        this.out = false;
        this.wait = 60;
        this.tries = 0;
        mc.method_63588(() -> {
            if (this.server != data || mc.field_1687 == null) {
                return;
            }
            mc.method_73360(class_638.field_61021);
            this.out = true;
        });
        return true;
    }

    void tick(class_310 mc) {
        if (this.server == null || !this.out) {
            return;
        }
        if (mc.field_1687 != null) {
            this.server = null;
            return;
        }
        if (mc.field_1755 instanceof class_412 || --this.wait > 0) {
            return;
        }
        if (++this.tries > 5) {
            this.server = null;
            return;
        }
        class_642 data = this.server;
        class_437 parent = mc.field_1755 instanceof class_500 ? mc.field_1755 : new class_500((class_437)new class_442());
        this.wait = 180;
        mc.method_63588(() -> {
            if (mc.field_1687 == null) {
                class_412.method_36877((class_437)parent, (class_310)mc, (class_639)class_639.method_2950((String)data.field_3761), (class_642)data, (boolean)false, null);
            }
        });
    }
}

