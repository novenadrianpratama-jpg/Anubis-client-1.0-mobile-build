/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.event.impl;

import net.anubis.client.event.Event;
import net.anubis.client.event.EventType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class TickEvent
extends Event {
    public static final EventType<TickEvent> TYPE = new EventType("TickEvent");
    private static final TickEvent INSTANCE = new TickEvent();

    private TickEvent() {
    }

    public static void dispatch() {
        TYPE.dispatch(INSTANCE);
    }

    public static void dispatchPost() {
        TYPE.dispatchPost(INSTANCE);
    }
}

