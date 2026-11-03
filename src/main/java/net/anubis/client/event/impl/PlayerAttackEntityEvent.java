/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 */
package net.anubis.client.event.impl;

import net.anubis.client.event.CancellableEvent;
import net.anubis.client.event.CancellableEventType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;

@Environment(value=EnvType.CLIENT)
public final class PlayerAttackEntityEvent
extends CancellableEvent {
    public static final CancellableEventType<PlayerAttackEntityEvent> TYPE = new CancellableEventType("PlayerAttackEntityEvent");
    public class_1297 target;
    private static final PlayerAttackEntityEvent INSTANCE = new PlayerAttackEntityEvent();

    private PlayerAttackEntityEvent() {
    }

    public static boolean dispatch(class_1297 target) {
        if (!TYPE.hasPreListeners()) {
            return false;
        }
        PlayerAttackEntityEvent.INSTANCE.target = target;
        INSTANCE.resetCancellation();
        TYPE.dispatch(INSTANCE);
        boolean cancelled = INSTANCE.isCancelled();
        PlayerAttackEntityEvent.INSTANCE.target = null;
        return cancelled;
    }
}

