/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1713
 *  net.minecraft.class_1799
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2596
 *  net.minecraft.class_2815
 *  net.minecraft.class_2846
 *  net.minecraft.class_2846$class_2847
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package net.anubis.client.easyplace.builder;

import java.util.function.Predicate;
import net.anubis.client.easyplace.inventory.InventoryCalm;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2596;
import net.minecraft.class_2815;
import net.minecraft.class_2846;
import net.minecraft.class_310;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
final class Hands {
    private static final long SWAP_WAIT_NANOS = 1000000000L;
    private static long swapSentAt = System.nanoTime() - 1000000000L;

    private Hands() {
    }

    static boolean hold(class_310 mc, class_746 player, Predicate<class_1799> want) {
        int i;
        class_1661 inventory = player.method_31548();
        if (want.test(player.method_6047())) {
            return true;
        }
        for (i = 0; i < 9; ++i) {
            if (!want.test(inventory.method_5438(i))) continue;
            inventory.method_61496(i);
            return true;
        }
        if (player.field_7512 != player.field_7498 || mc.field_1761 == null) {
            return false;
        }
        for (i = 9; i < 36; ++i) {
            if (!want.test(inventory.method_5438(i))) continue;
            if (!InventoryCalm.settle(mc, player)) {
                return false;
            }
            mc.field_1761.method_2906(player.field_7498.field_7763, i, inventory.method_67532(), class_1713.field_7791, (class_1657)player);
            Hands.close(mc, player);
            return false;
        }
        return false;
    }

    static boolean has(class_746 player, Predicate<class_1799> want) {
        class_1661 inventory = player.method_31548();
        for (int i = 0; i < 36; ++i) {
            if (!want.test(inventory.method_5438(i))) continue;
            return true;
        }
        return want.test(player.method_6079());
    }

    static boolean toOffhand(class_310 mc, class_746 player, Predicate<class_1799> want) {
        int i;
        if (player.field_7512 != player.field_7498 || mc.field_1761 == null || mc.method_1562() == null || !player.field_7498.method_34255().method_7960()) {
            return false;
        }
        class_1661 inventory = player.method_31548();
        if (want.test(player.method_6047())) {
            long now = System.nanoTime();
            if (now - swapSentAt < 1000000000L) {
                return true;
            }
            swapSentAt = now;
            mc.method_1562().method_52787((class_2596)new class_2846(class_2846.class_2847.field_12969, class_2338.field_10980, class_2350.field_11033));
            return true;
        }
        for (i = 0; i < 9; ++i) {
            if (!want.test(inventory.method_5438(i))) continue;
            inventory.method_61496(i);
            return true;
        }
        for (i = 9; i < 36; ++i) {
            if (!want.test(inventory.method_5438(i))) continue;
            if (!InventoryCalm.settle(mc, player)) {
                return true;
            }
            mc.field_1761.method_2906(player.field_7498.field_7763, i, 40, class_1713.field_7791, (class_1657)player);
            Hands.close(mc, player);
            return true;
        }
        return false;
    }

    private static void close(class_310 mc, class_746 player) {
        if (mc.field_1755 == null) {
            player.field_3944.method_52787((class_2596)new class_2815(player.field_7498.field_7763));
        }
    }
}

