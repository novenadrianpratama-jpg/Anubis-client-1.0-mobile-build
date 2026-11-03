/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1792
 */
package net.anubis.client.easyplace.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1792;

@Environment(value=EnvType.CLIENT)
public record InventoryPickResult(Status status, class_1792 item, class_1268 hand, int slot) {
    public static InventoryPickResult ready(class_1792 item, class_1268 hand, int slot) {
        return new InventoryPickResult(Status.READY, item, hand, slot);
    }

    public static InventoryPickResult waiting(class_1792 item, int slot) {
        return new InventoryPickResult(Status.WAITING, item, null, slot);
    }

    public static InventoryPickResult missing(class_1792 item) {
        return new InventoryPickResult(Status.MISSING, item, null, -1);
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Status {
        READY,
        WAITING,
        MISSING;

    }
}

