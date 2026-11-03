/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1792
 */
package net.anubis.client.easyplace.inventory;

import net.anubis.client.easyplace.inventory.InventoryPickResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1792;

@Environment(value=EnvType.CLIENT)
public interface InventoryPickSource {
    public InventoryPickResult prepare(class_1792 var1, long var2);

    public void reset();
}

