/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1661
 *  net.minecraft.class_1747
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3481
 *  net.minecraft.class_3489
 *  net.minecraft.class_746
 *  net.minecraft.class_8923
 */
package net.anubis.client.easyplace.builder;

import net.anubis.client.easyplace.builder.Hands;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1661;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3481;
import net.minecraft.class_3489;
import net.minecraft.class_746;
import net.minecraft.class_8923;

@Environment(value=EnvType.CLIENT)
final class Scaffold {
    private Scaffold() {
    }

    static boolean item(class_1799 stack) {
        return Scaffold.glass(stack) || stack.method_31573(class_3489.field_15558);
    }

    static boolean glass(class_1799 stack) {
        class_1747 item;
        class_1792 class_17922 = stack.method_7909();
        return class_17922 instanceof class_1747 && (item = (class_1747)class_17922).method_7711() instanceof class_8923;
    }

    static boolean block(class_2680 state) {
        return state.method_26164(class_3481.field_15503) || state.method_26204() instanceof class_8923;
    }

    static int count(class_746 player) {
        int count = 0;
        class_1661 inventory = player.method_31548();
        for (int i = 0; i < 36; ++i) {
            if (!Scaffold.item(inventory.method_5438(i))) continue;
            count += inventory.method_5438(i).method_7947();
        }
        if (Scaffold.item(player.method_6079())) {
            count += player.method_6079().method_7947();
        }
        return count;
    }

    static boolean has(class_746 player) {
        return Hands.has(player, Scaffold::item);
    }

    static boolean hold(class_310 mc, class_746 player) {
        if (Hands.has(player, stack -> stack.method_31574(class_1802.field_8280))) {
            return Hands.hold(mc, player, stack -> stack.method_31574(class_1802.field_8280));
        }
        if (Hands.has(player, stack -> stack.method_31573(class_3489.field_15558))) {
            return Hands.hold(mc, player, stack -> stack.method_31573(class_3489.field_15558));
        }
        return Hands.hold(mc, player, Scaffold::glass);
    }
}

