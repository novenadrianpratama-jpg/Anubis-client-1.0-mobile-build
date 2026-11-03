/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.event;

import java.util.function.Consumer;
import net.anubis.client.event.Event;
import net.anubis.client.event.EventPriority;
import net.anubis.client.event.EventType;
import net.anubis.client.event.Stage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record ListenerHandle<E extends Event>(EventType<E> type, Stage stage, EventPriority priority, Object owner, Consumer<E> callback) {
    public void unregister() {
        this.type.unregister(this);
    }
}

