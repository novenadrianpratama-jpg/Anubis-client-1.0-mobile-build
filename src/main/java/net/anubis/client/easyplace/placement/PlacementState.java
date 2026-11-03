/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.easyplace.placement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum PlacementState {
    IDLE,
    FIND_TARGET,
    CALCULATE_ROTATION,
    WAIT_ROTATION,
    EXECUTE_PLACE,
    WAIT_ACK;

}

