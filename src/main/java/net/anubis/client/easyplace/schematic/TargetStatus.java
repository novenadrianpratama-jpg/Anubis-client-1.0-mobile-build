/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.easyplace.schematic;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum TargetStatus {
    CORRECT,
    MISSING,
    ADJUST,
    WRONG,
    NO_ITEM,
    UNSUPPORTED;

}

