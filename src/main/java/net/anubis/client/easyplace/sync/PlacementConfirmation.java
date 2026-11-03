/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 */
package net.anubis.client.easyplace.sync;

import net.anubis.client.easyplace.schematic.SchematicBlockState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;
import net.minecraft.class_2680;

@Environment(value=EnvType.CLIENT)
public record PlacementConfirmation(class_2338 pos, SchematicBlockState expected, long sentTick, long deadlineTick, int retry, int sequence, class_2680 predicted) {
}

