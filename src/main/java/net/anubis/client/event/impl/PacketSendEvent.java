/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2596
 */
package net.anubis.client.event.impl;

import net.anubis.client.event.CancellableEvent;
import net.anubis.client.event.PooledCancellableEventType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2596;

@Environment(value=EnvType.CLIENT)
public final class PacketSendEvent
extends CancellableEvent {
    public static final PooledCancellableEventType<PacketSendEvent> TYPE = new PooledCancellableEventType<PacketSendEvent>("PacketSendEvent", PacketSendEvent::new, 1024);
    public class_2596<?> packet;

    private PacketSendEvent() {
    }

    public static boolean dispatchSync(class_2596<?> packet) {
        if (!TYPE.hasPreListeners()) {
            return false;
        }
        PacketSendEvent event = (PacketSendEvent)PacketSendEvent.TYPE.pool.acquire();
        event.packet = packet;
        event.resetCancellation();
        try {
            TYPE.dispatch(event);
            boolean bl = event.isCancelled();
            return bl;
        }
        finally {
            event.packet = null;
            PacketSendEvent.TYPE.pool.release(event);
        }
    }
}

