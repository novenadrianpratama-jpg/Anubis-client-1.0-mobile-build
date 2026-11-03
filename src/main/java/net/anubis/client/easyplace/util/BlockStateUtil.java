/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1792
 *  net.minecraft.class_1802
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 *  net.minecraft.class_2741
 *  net.minecraft.class_2742
 *  net.minecraft.class_2756
 *  net.minecraft.class_2769
 */
package net.anubis.client.easyplace.util;

import net.anubis.client.easyplace.schematic.TargetStatus;
import net.anubis.client.easyplace.util.StateRules;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2742;
import net.minecraft.class_2756;
import net.minecraft.class_2769;

@Environment(value=EnvType.CLIENT)
public final class BlockStateUtil {
    private BlockStateUtil() {
    }

    public static boolean statesMatch(class_2680 expected, class_2680 actual) {
        return StateRules.placed(actual, expected) || StateRules.slabHalf(actual, expected);
    }

    public static TargetStatus classify(class_2680 expected, class_2680 actual) {
        if (StateRules.done(actual, expected)) {
            return TargetStatus.CORRECT;
        }
        if (StateRules.placed(actual, expected)) {
            return TargetStatus.ADJUST;
        }
        if (actual.method_26215() || actual.method_45474() || StateRules.slabHalf(actual, expected)) {
            return TargetStatus.MISSING;
        }
        return TargetStatus.WRONG;
    }

    public static class_1792 requiredItem(class_2680 expected) {
        class_1792 item = StateRules.plant(expected.method_26204()).method_8389();
        return item == class_1802.field_8162 ? null : item;
    }

    public static boolean isPrimaryPlacementState(class_2680 state) {
        if (state.method_28498((class_2769)class_2741.field_12533) && state.method_11654((class_2769)class_2741.field_12533) == class_2756.field_12609) {
            return false;
        }
        return !state.method_28498((class_2769)class_2741.field_12483) || state.method_11654((class_2769)class_2741.field_12483) != class_2742.field_12560;
    }

    public static boolean isSimpleFullBlock(class_2680 state, class_1937 level, class_2338 pos) {
        return state.method_26234((class_1922)level, pos) && state.method_26204().method_8389() != class_1802.field_8162;
    }
}

