/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.event;

import java.util.function.Supplier;
import net.anubis.client.event.Event;
import net.anubis.client.event.EventPool;
import net.anubis.client.event.EventType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class PooledEventType<E extends Event>
extends EventType<E> {
    public final EventPool<E> pool;

    public PooledEventType(Supplier<E> factory, int capacity) {
        this.pool = new EventPool<E>(factory, capacity);
    }

    public PooledEventType(String name, Supplier<E> factory, int capacity) {
        super(name);
        this.pool = new EventPool<E>(factory, capacity);
    }
}

