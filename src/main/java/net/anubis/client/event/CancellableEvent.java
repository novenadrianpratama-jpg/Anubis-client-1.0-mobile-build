/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.event;

import net.anubis.client.event.Event;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public abstract class CancellableEvent
extends Event {
    boolean cancelled;
    boolean postStage;

    public final void setCancelled(boolean cancelled) {
        if (this.postStage) {
            return;
        }
        this.cancelled = cancelled;
    }

    public final boolean isCancelled() {
        return this.cancelled;
    }

    protected void resetCancellation() {
        this.cancelled = false;
        this.postStage = false;
    }
}

