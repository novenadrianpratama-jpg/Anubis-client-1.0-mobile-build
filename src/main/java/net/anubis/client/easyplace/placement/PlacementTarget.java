/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_243
 */
package net.anubis.client.easyplace.placement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;

@Environment(value=EnvType.CLIENT)
public record PlacementTarget(class_2338 targetPos, class_2338 neighborPos, class_2350 placeSide, class_2350 clickedFace, int hotbarSlot, float yaw, float pitch, int score, class_243 hitCursor, boolean sneak) {
}

