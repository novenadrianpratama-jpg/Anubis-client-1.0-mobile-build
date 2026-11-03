/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.event;

import java.util.function.Supplier;
import net.anubis.client.event.CancellableEvent;
import net.anubis.client.event.CancellableEventType;
import net.anubis.client.event.PooledEventType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class PooledCancellableEventType<E extends CancellableEvent>
extends PooledEventType<E> {
    public PooledCancellableEventType(Supplier<E> factory, int capacity) {
        super(factory, capacity);
    }

    public PooledCancellableEventType(String name, Supplier<E> factory, int capacity) {
        super(name, factory, capacity);
    }

    @Override
    public void dispatchPre(E event) {
        CancellableEventType.dispatchPreCancellable(this.list, event);
    }

    @Override
    public void dispatchPost(E event) {
        CancellableEventType.dispatchPostGuarded(this.list, event);
    }
}

