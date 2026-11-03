/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10192
 *  net.minecraft.class_1304
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1703
 *  net.minecraft.class_1713
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_2371
 *  net.minecraft.class_2596
 *  net.minecraft.class_2815
 *  net.minecraft.class_310
 *  net.minecraft.class_636
 *  net.minecraft.class_746
 *  net.minecraft.class_9334
 */
package net.anubis.client.module.impl.movement.elytrafly;

import com.example.client.mixin.AccessorElytraFlyMultiPlayerGameMode;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10192;
import net.minecraft.class_1304;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2371;
import net.minecraft.class_2596;
import net.minecraft.class_2815;
import net.minecraft.class_310;
import net.minecraft.class_636;
import net.minecraft.class_746;
import net.minecraft.class_9334;

@Environment(value=EnvType.CLIENT)
public final class ElytraFlyInventory {
    public static final int OFFHAND = 40;
    private static final int CHEST_ARMOR_ID = 6;
    private static int previousSlot = -1;

    private ElytraFlyInventory() {
    }

    public static int find(Predicate<class_1799> isGood) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return -1;
        }
        return ElytraFlyInventory.find(isGood, 0, player.method_31548().method_5439() - 1);
    }

    public static int find(Predicate<class_1799> isGood, int start, int end) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return -1;
        }
        class_1661 inventory = player.method_31548();
        for (int i = start; i <= end; ++i) {
            if (!isGood.test(inventory.method_5438(i))) continue;
            return i;
        }
        return -1;
    }

    public static int findInHotbar(Predicate<class_1799> isGood) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return -1;
        }
        if (isGood.test(player.method_6079())) {
            return 40;
        }
        if (isGood.test(player.method_6047())) {
            return player.method_31548().method_67532();
        }
        return ElytraFlyInventory.find(isGood, 0, 8);
    }

    public static boolean isHotbar(int index) {
        return index >= 0 && index <= 8;
    }

    public static void moveToHotbar(int fromIndex, int hotbarSlot) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        ElytraFlyInventory.move(player, ElytraFlyInventory.indexToId(player, fromIndex), ElytraFlyInventory.indexToId(player, hotbarSlot));
    }

    public static void moveToChest(int fromIndex) {
        int fromId;
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        ElytraFlyInventory.move(player, fromId, (fromId = ElytraFlyInventory.indexToId(player, fromIndex)) == -1 ? -1 : 6);
    }

    private static void move(class_746 player, int fromId, int toId) {
        class_636 gameMode = class_310.method_1551().field_1761;
        if (gameMode == null || fromId == -1 || toId == -1) {
            return;
        }
        class_1703 menu = player.field_7512;
        boolean hadEmptyCursor = menu.method_34255().method_7960();
        gameMode.method_2906(menu.field_7763, fromId, 0, class_1713.field_7790, (class_1657)player);
        gameMode.method_2906(menu.field_7763, toId, 0, class_1713.field_7790, (class_1657)player);
        if (hadEmptyCursor && !menu.method_34255().method_7960()) {
            gameMode.method_2906(menu.field_7763, fromId, 0, class_1713.field_7790, (class_1657)player);
        }
    }

    private static int indexToId(class_746 player, int index) {
        if (player.field_7512 != player.field_7498) {
            return -1;
        }
        if (index >= 0 && index <= 8) {
            return 36 + index;
        }
        if (index >= 9 && index < 36) {
            return index;
        }
        if (index >= 36 && index <= 39) {
            return 8 - (index - 36);
        }
        if (index == 40) {
            return 45;
        }
        return -1;
    }

    public static boolean swap(int slot, boolean swapBack) {
        if (slot == 40) {
            return true;
        }
        if (slot < 0 || slot > 8) {
            return false;
        }
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1761 == null) {
            return false;
        }
        if (swapBack && previousSlot == -1) {
            previousSlot = player.method_31548().method_67532();
        } else if (!swapBack) {
            previousSlot = -1;
        }
        player.method_31548().method_61496(slot);
        ((AccessorElytraFlyMultiPlayerGameMode)mc.field_1761).anubis$elytraFlySyncSelected();
        return true;
    }

    public static boolean swapBack() {
        if (previousSlot == -1) {
            return false;
        }
        boolean result = ElytraFlyInventory.swap(previousSlot, false);
        previousSlot = -1;
        return result;
    }

    public static void chestSwap() {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        class_1799 current = player.method_6118(class_1304.field_6174);
        class_10192 equippable = (class_10192)current.method_58694(class_9334.field_54196);
        if (current.method_57826(class_9334.field_54197)) {
            ElytraFlyInventory.equipChestplate(player);
        } else if (equippable != null && equippable.comp_3174().method_5927() == class_1304.field_6174.method_5927()) {
            ElytraFlyInventory.equipElytra(player);
        } else if (!ElytraFlyInventory.equipChestplate(player)) {
            ElytraFlyInventory.equipElytra(player);
        }
    }

    private static boolean equipChestplate(class_746 player) {
        class_2371 items = player.method_31548().method_67533();
        int bestSlot = -1;
        for (int i = 0; i < items.size(); ++i) {
            class_1792 item = ((class_1799)items.get(i)).method_7909();
            if (item == class_1802.field_8058) {
                bestSlot = i;
                continue;
            }
            if (item != class_1802.field_22028) continue;
            bestSlot = i;
            break;
        }
        if (bestSlot != -1) {
            ElytraFlyInventory.equip(player, bestSlot);
        }
        return bestSlot != -1;
    }

    private static void equipElytra(class_746 player) {
        class_2371 items = player.method_31548().method_67533();
        for (int i = 0; i < items.size(); ++i) {
            if (!((class_1799)items.get(i)).method_57826(class_9334.field_54197)) continue;
            ElytraFlyInventory.equip(player, i);
            break;
        }
    }

    private static void equip(class_746 player, int slot) {
        if (ElytraFlyInventory.indexToId(player, slot) == -1) {
            return;
        }
        ElytraFlyInventory.moveToChest(slot);
        player.field_3944.method_52787((class_2596)new class_2815(0));
    }
}

