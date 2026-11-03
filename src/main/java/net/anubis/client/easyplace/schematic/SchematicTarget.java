/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 */
package net.anubis.client.easyplace.schematic;

import net.anubis.client.easyplace.schematic.SchematicBlockState;
import net.anubis.client.easyplace.schematic.TargetStatus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;
import net.minecraft.class_2680;

@Environment(value=EnvType.CLIENT)
public record SchematicTarget(class_2338 pos, SchematicBlockState expected, class_2680 actual, TargetStatus status, double distanceSquared) {
    public SchematicTarget withActual(class_2680 state, TargetStatus status) {
        return new SchematicTarget(this.pos, this.expected, state, status, this.distanceSquared);
    }
}

