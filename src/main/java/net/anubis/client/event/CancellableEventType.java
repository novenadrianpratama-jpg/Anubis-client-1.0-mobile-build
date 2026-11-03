/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.event;

import java.util.function.Consumer;
import net.anubis.client.event.CancellableEvent;
import net.anubis.client.event.EventType;
import net.anubis.client.event.ListenerErrorReporter;
import net.anubis.client.event.ListenerList;
import net.anubis.client.event.Stage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class CancellableEventType<E extends CancellableEvent>
extends EventType<E> {
    public CancellableEventType() {
    }

    public CancellableEventType(String name) {
        super(name);
    }

    @Override
    public void dispatchPre(E event) {
        CancellableEventType.dispatchPreCancellable(this.list, event);
    }

    @Override
    public void dispatchPost(E event) {
        CancellableEventType.dispatchPostGuarded(this.list, event);
    }

    static <E extends CancellableEvent> void dispatchPreCancellable(ListenerList<E> list, E event) {
        Consumer<E>[] cbs = list.preCallbacks();
        for (int i = 0; i < cbs.length; ++i) {
            try {
                cbs[i].accept(event);
            }
            catch (Exception ex) {
                ListenerErrorReporter.report(list.handleOf(Stage.PRE, cbs[i]), event, ex);
            }
            if (event.cancelled) break;
        }
    }

    static <E extends CancellableEvent> void dispatchPostGuarded(ListenerList<E> list, E event) {
        event.postStage = true;
        try {
            list.dispatchPost(event);
        }
        finally {
            event.postStage = false;
        }
    }
}

