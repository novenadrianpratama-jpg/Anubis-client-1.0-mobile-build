/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module;

import net.anubis.client.module.ClientModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class CombatMacroLock {
    private static Object holder;

    private CombatMacroLock() {
    }

    public static boolean tryAcquire(Object owner) {
        ClientModule module;
        Object object = holder;
        if (object instanceof ClientModule && !(module = (ClientModule)object).isEnabled()) {
            holder = null;
        }
        if (holder == null || holder == owner) {
            holder = owner;
            return true;
        }
        return false;
    }

    public static void take(Object owner) {
        holder = owner;
    }

    public static void release(Object owner) {
        if (holder == owner) {
            holder = null;
        }
    }

    public static boolean heldByOther(Object owner) {
        ClientModule module;
        Object object = holder;
        if (object instanceof ClientModule && !(module = (ClientModule)object).isEnabled()) {
            holder = null;
        }
        return holder != null && holder != owner;
    }
}

