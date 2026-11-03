/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.event;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Supplier;
import net.anubis.client.event.Event;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class EventPool<E extends Event> {
    private final ArrayBlockingQueue<E> pool;
    private final Supplier<E> factory;

    public EventPool(Supplier<E> factory, int capacity) {
        this.factory = factory;
        this.pool = new ArrayBlockingQueue(capacity);
    }

    public E acquire() {
        Event event = (Event)this.pool.poll();
        if (event == null) {
            event = (Event)this.factory.get();
        }
        return (E)event;
    }

    public void release(E event) {
        this.pool.offer(event);
    }
}

