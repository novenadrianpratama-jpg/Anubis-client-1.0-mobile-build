/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_3532
 */
package net.anubis.client.util.pvp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;

@Environment(value=EnvType.CLIENT)
public final class TargetingUtils {
    private TargetingUtils() {
    }

    public static class_243 closestPoint(class_243 point, class_238 box) {
        return new class_243(class_3532.method_15350((double)point.field_1352, (double)box.field_1323, (double)box.field_1320), class_3532.method_15350((double)point.field_1351, (double)box.field_1322, (double)box.field_1325), class_3532.method_15350((double)point.field_1350, (double)box.field_1321, (double)box.field_1324));
    }
}

