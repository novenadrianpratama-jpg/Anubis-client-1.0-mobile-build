/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.movement.elytrafly;

import net.anubis.client.module.impl.movement.ElytraFlyModule;
import net.anubis.client.module.impl.movement.elytrafly.ElytraFlightMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class VanillaMode
extends ElytraFlightMode {
    public VanillaMode(ElytraFlyModule elytraFly) {
        super(elytraFly);
    }
}

