/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1937
 *  net.minecraft.class_2199
 *  net.minecraft.class_2248
 *  net.minecraft.class_2269
 *  net.minecraft.class_2304
 *  net.minecraft.class_2309
 *  net.minecraft.class_2312
 *  net.minecraft.class_2323
 *  net.minecraft.class_2338
 *  net.minecraft.class_2349
 *  net.minecraft.class_2401
 *  net.minecraft.class_2428
 *  net.minecraft.class_2533
 *  net.minecraft.class_3709
 *  net.minecraft.class_3908
 */
package net.anubis.client.easyplace.placement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1937;
import net.minecraft.class_2199;
import net.minecraft.class_2248;
import net.minecraft.class_2269;
import net.minecraft.class_2304;
import net.minecraft.class_2309;
import net.minecraft.class_2312;
import net.minecraft.class_2323;
import net.minecraft.class_2338;
import net.minecraft.class_2349;
import net.minecraft.class_2401;
import net.minecraft.class_2428;
import net.minecraft.class_2533;
import net.minecraft.class_3709;
import net.minecraft.class_3908;

@Environment(value=EnvType.CLIENT)
public final class Clicks {
    private Clicks() {
    }

    public static boolean usesClick(class_1937 level, class_2338 pos, boolean builder) {
        if (level.method_8321(pos) instanceof class_3908) {
            return true;
        }
        if (!builder) {
            return false;
        }
        class_2248 block = level.method_8320(pos).method_26204();
        return level.method_8321(pos) != null || block instanceof class_2323 || block instanceof class_2533 || block instanceof class_2349 || block instanceof class_2269 || block instanceof class_2401 || block instanceof class_2312 || block instanceof class_2428 || block instanceof class_2304 || block instanceof class_2199 || block instanceof class_3709 || block instanceof class_2309;
    }
}

