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
 *  net.minecraft.class_1802
 *  net.minecraft.class_2596
 *  net.minecraft.class_2815
 *  net.minecraft.class_310
 *  net.minecraft.class_3489
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.combat.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.anubis.client.easyplace.inventory.InventoryCalm;
import net.anubis.client.module.impl.combat.AutoInvTotemModule;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2596;
import net.minecraft.class_2815;
import net.minecraft.class_310;
import net.minecraft.class_3489;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
final class BotKit {
    private static final int CLICK_GAP = 4;
    private int lastClick = -4;

    BotKit() {
    }

    boolean needed(class_746 player, boolean anchors) {
        return BotKit.next(player, BotKit.wants(anchors)) != null;
    }

    boolean refill(class_310 mc, class_746 player, boolean anchors) {
        if (mc.field_1761 == null || mc.field_1755 != null || player.field_7512 != player.field_7498 || !player.field_7498.method_34255().method_7960() || AutoInvTotemModule.busy() || player.method_6115()) {
            return false;
        }
        int[] move = BotKit.next(player, BotKit.wants(anchors));
        if (move == null) {
            return false;
        }
        int tick = player.field_6012;
        if (!InventoryCalm.settle(mc, player) || tick >= this.lastClick && tick - this.lastClick < 4) {
            return true;
        }
        mc.field_1761.method_2906(player.field_7498.field_7763, move[0], move[1], class_1713.field_7791, (class_1657)player);
        player.field_3944.method_52787((class_2596)new class_2815(player.field_7498.field_7763));
        MeleeAttacks.noteAction((class_1657)player);
        this.lastClick = tick;
        return true;
    }

    private static List<Want> wants(boolean anchors) {
        ArrayList<Want> wants = new ArrayList<Want>();
        wants.add(new Want(stack -> stack.method_31574(class_1802.field_8301), 16, 32));
        wants.add(new Want(stack -> stack.method_31574(class_1802.field_8281), 8, 16));
        wants.add(new Want(MeleeAttacks::isMeleeWeapon, 1, 1));
        wants.add(new Want(stack -> stack.method_31574(class_1802.field_8367) || stack.method_31574(class_1802.field_8463), 1, 1));
        wants.add(new Want(stack -> stack.method_31574(class_1802.field_8634), 1, 1));
        wants.add(new Want(stack -> stack.method_31573(class_3489.field_42614), 1, 1));
        wants.add(new Want(stack -> stack.method_31574(class_1802.field_8287), 1, 1));
        if (anchors) {
            wants.add(new Want(stack -> stack.method_31574(class_1802.field_23141), 2, 4));
            wants.add(new Want(stack -> stack.method_31574(class_1802.field_8801), 2, 4));
        }
        return wants;
    }

    private static int[] next(class_746 player, List<Want> wants) {
        class_1661 inventory = player.method_31548();
        for (Want want : wants) {
            int free;
            int inHotbar = 0;
            int smallest = -1;
            for (int i = 0; i < 9; ++i) {
                class_1799 stack = inventory.method_5438(i);
                if (!want.item().test(stack)) continue;
                inHotbar += stack.method_7947();
                if (smallest >= 0 && stack.method_7947() >= inventory.method_5438(smallest).method_7947()) continue;
                smallest = i;
            }
            if (inHotbar >= want.low()) continue;
            int source = -1;
            for (int i = 9; i < 36; ++i) {
                class_1799 stack = inventory.method_5438(i);
                if (!want.item().test(stack) || source >= 0 && stack.method_7947() <= inventory.method_5438(source).method_7947()) continue;
                source = i;
            }
            if (source < 0) continue;
            int bigger = inventory.method_5438(source).method_7947();
            if (smallest >= 0 && bigger > inHotbar && (inHotbar < want.topUp() || bigger >= inHotbar + 8)) {
                return new int[]{source, smallest};
            }
            if (smallest >= 0 || (free = BotKit.freeSlot(inventory, wants)) < 0) continue;
            return new int[]{source, free};
        }
        return null;
    }

    private static int freeSlot(class_1661 inventory, List<Want> wants) {
        int i;
        for (i = 0; i < 9; ++i) {
            if (!inventory.method_5438(i).method_7960()) continue;
            return i;
        }
        for (i = 8; i >= 0; --i) {
            class_1799 stack = inventory.method_5438(i);
            if (stack.method_31574(class_1802.field_8288)) continue;
            boolean used = false;
            for (Want want : wants) {
                used |= want.item().test(stack);
            }
            if (used) continue;
            return i;
        }
        return -1;
    }

    @Environment(value=EnvType.CLIENT)
    private record Want(Predicate<class_1799> item, int low, int topUp) {
    }
}

