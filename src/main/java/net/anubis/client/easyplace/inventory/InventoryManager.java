/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1792
 *  net.minecraft.class_310
 */
package net.anubis.client.easyplace.inventory;

import net.anubis.client.easyplace.inventory.InventoryPickResult;
import net.anubis.client.easyplace.inventory.InventoryPickSourceMode;
import net.anubis.client.easyplace.inventory.ServerRangeInventorySource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1792;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class InventoryManager {
    private final class_310 mc;
    private final ServerRangeInventorySource serverRange;

    public InventoryManager(class_310 mc) {
        this.mc = mc;
        this.serverRange = new ServerRangeInventorySource(mc);
    }

    public InventoryPickResult prepare(InventoryPickSourceMode source, class_1792 item, long tick, boolean driving) {
        return switch (source) {
            default -> throw new MatchException(null, null);
            case InventoryPickSourceMode.SERVER_RANGE -> this.serverRange.prepare(item, tick, driving);
            case InventoryPickSourceMode.HOTBAR_ONLY -> this.hotbarOnly(item);
        };
    }

    private InventoryPickResult hotbarOnly(class_1792 item) {
        if (this.mc.field_1724 == null) {
            return InventoryPickResult.missing(item);
        }
        for (int slot = 0; slot < 9; ++slot) {
            if (this.mc.field_1724.method_31548().method_5438(slot).method_7909() != item) continue;
            return InventoryPickResult.ready(item, null, slot);
        }
        return InventoryPickResult.missing(item);
    }

    public void reset() {
        this.serverRange.reset();
    }
}

