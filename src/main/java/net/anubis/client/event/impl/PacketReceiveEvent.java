/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2596
 */
package net.anubis.client.event.impl;

import java.util.function.BooleanSupplier;
import net.anubis.client.event.Event;
import net.anubis.client.event.EventBus;
import net.anubis.client.event.PooledEventType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2596;

@Environment(value=EnvType.CLIENT)
public final class PacketReceiveEvent
extends Event
implements Runnable {
    public static final PooledEventType<PacketReceiveEvent> TYPE = new PooledEventType<PacketReceiveEvent>("PacketReceiveEvent", PacketReceiveEvent::new, 1024);
    public class_2596<?> packet;
    private BooleanSupplier connectionActive;

    private PacketReceiveEvent() {
    }

    public static void dispatchAsync(class_2596<?> packet) {
        PacketReceiveEvent.dispatchAsync(packet, null);
    }

    public static void dispatchAsync(class_2596<?> packet, BooleanSupplier connectionActive) {
        if (!TYPE.hasListeners()) {
            return;
        }
        PacketReceiveEvent event = (PacketReceiveEvent)PacketReceiveEvent.TYPE.pool.acquire();
        event.packet = packet;
        event.connectionActive = connectionActive;
        if (!EventBus.INSTANCE.postAsync(event)) {
            event.release();
        }
    }

    @Override
    public void run() {
        try {
            if (this.connectionActive != null && !this.connectionActive.getAsBoolean()) {
                return;
            }
            TYPE.dispatch(this);
            TYPE.dispatchPost(this);
        }
        finally {
            this.release();
        }
    }

    private void release() {
        this.packet = null;
        this.connectionActive = null;
        PacketReceiveEvent.TYPE.pool.release(this);
    }
}

