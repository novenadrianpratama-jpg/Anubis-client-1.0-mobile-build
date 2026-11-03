/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.event.impl;

import net.anubis.client.event.CancellableEvent;
import net.anubis.client.event.CancellableEventType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class MouseInputEvent
extends CancellableEvent {
    public static final CancellableEventType<MouseInputEvent> TYPE = new CancellableEventType("MouseInputEvent");
    public long window;
    public int button;
    public int action;
    public int mods;
    private static final MouseInputEvent INSTANCE = new MouseInputEvent();

    private MouseInputEvent() {
    }

    public static boolean dispatch(long window, int button, int action, int mods) {
        if (!TYPE.hasPreListeners()) {
            return false;
        }
        MouseInputEvent.INSTANCE.window = window;
        MouseInputEvent.INSTANCE.button = button;
        MouseInputEvent.INSTANCE.action = action;
        MouseInputEvent.INSTANCE.mods = mods;
        INSTANCE.resetCancellation();
        TYPE.dispatch(INSTANCE);
        return INSTANCE.isCancelled();
    }
}

