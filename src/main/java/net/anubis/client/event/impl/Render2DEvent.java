/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_332
 */
package net.anubis.client.event.impl;

import net.anubis.client.event.Event;
import net.anubis.client.event.EventType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;

@Environment(value=EnvType.CLIENT)
public final class Render2DEvent
extends Event {
    public static final EventType<Render2DEvent> TYPE = new EventType("Render2DEvent");
    public class_332 graphics;
    public float partialTick;
    private static final Render2DEvent INSTANCE = new Render2DEvent();

    private Render2DEvent() {
    }

    public static void dispatch(class_332 graphics, float partialTick) {
        if (!TYPE.hasPreListeners()) {
            return;
        }
        Render2DEvent.INSTANCE.graphics = graphics;
        Render2DEvent.INSTANCE.partialTick = partialTick;
        TYPE.dispatch(INSTANCE);
        Render2DEvent.INSTANCE.graphics = null;
    }
}

