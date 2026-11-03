/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.util.playerbypass;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum LightSectionStatus {
    NORMAL,
    EMPTY_MASK,
    ZEROED_PRESENT,
    UNIFORM_MAX,
    UNKNOWN;

}

