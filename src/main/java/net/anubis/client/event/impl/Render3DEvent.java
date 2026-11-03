/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_4604
 */
package net.anubis.client.event.impl;

import net.anubis.client.event.Event;
import net.anubis.client.event.EventType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4604;

@Environment(value=EnvType.CLIENT)
public final class Render3DEvent
extends Event {
    public static final EventType<Render3DEvent> TYPE = new EventType("Render3DEvent");
    public float partialTick;
    public class_4604 frustum;
    private static final Render3DEvent INSTANCE = new Render3DEvent();
    private static boolean rendering;
    private static long frame;
    private static long lastDurationNanos;

    private Render3DEvent() {
    }

    public static void dispatch(float partialTick) {
        Render3DEvent.dispatch(partialTick, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void dispatch(float partialTick, class_4604 frustum) {
        if (!TYPE.hasListeners()) {
            lastDurationNanos = 0L;
            return;
        }
        if (rendering) {
            throw new IllegalStateException("Render3DEvent cannot be nested");
        }
        rendering = true;
        long start = System.nanoTime();
        Render3DEvent.INSTANCE.partialTick = partialTick;
        Render3DEvent.INSTANCE.frustum = frustum;
        try {
            TYPE.dispatchPre(INSTANCE);
            TYPE.dispatchPost(INSTANCE);
        }
        finally {
            Render3DEvent.INSTANCE.frustum = null;
            lastDurationNanos = System.nanoTime() - start;
            ++frame;
            rendering = false;
        }
    }

    public static long frame() {
        return frame;
    }

    public static long lastDurationNanos() {
        return lastDurationNanos;
    }
}

