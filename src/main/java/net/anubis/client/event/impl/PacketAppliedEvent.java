/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2596
 */
package net.anubis.client.event.impl;

import net.anubis.client.event.Event;
import net.anubis.client.event.EventType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2596;

@Environment(value=EnvType.CLIENT)
public final class PacketAppliedEvent
extends Event {
    public static final EventType<PacketAppliedEvent> TYPE = new EventType("PacketAppliedEvent");
    public class_2596<?> packet;
    private static final PacketAppliedEvent INSTANCE = new PacketAppliedEvent();

    private PacketAppliedEvent() {
    }

    public static void dispatch(class_2596<?> packet) {
        if (!TYPE.hasPreListeners()) {
            return;
        }
        PacketAppliedEvent.INSTANCE.packet = packet;
        TYPE.dispatch(INSTANCE);
        PacketAppliedEvent.INSTANCE.packet = null;
    }
}

