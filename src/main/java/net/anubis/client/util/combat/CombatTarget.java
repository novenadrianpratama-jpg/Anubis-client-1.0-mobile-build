/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_156
 *  net.minecraft.class_310
 */
package net.anubis.client.util.combat;

import net.anubis.client.event.EventPriority;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.PlayerAttackEntityEvent;
import net.anubis.client.event.impl.TickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_156;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class CombatTarget {
    private static final Object OWNER = new Object();
    private static final long MEMORY_CAP_MS = 30000L;
    private static class_1309 lastHit;
    private static long lastHitAt;

    private CombatTarget() {
    }

    public static void install() {
        PlayerAttackEntityEvent.TYPE.listen(OWNER, Stage.PRE, EventPriority.LOWEST, event -> {
            class_1297 patt0$temp;
            if (event.isCancelled() || !((patt0$temp = event.target) instanceof class_1309)) {
                return;
            }
            class_1309 living = (class_1309)patt0$temp;
            class_310 mc = class_310.method_1551();
            if (!mc.method_18854() || living.method_73183() != mc.field_1687) {
                return;
            }
            lastHit = living;
            lastHitAt = class_156.method_658();
        });
        TickEvent.TYPE.listen(OWNER, event -> CombatTarget.forgetStale());
    }

    private static void forgetStale() {
        class_1309 entity = lastHit;
        if (entity == null) {
            return;
        }
        if (!entity.method_5805() || entity.method_73183() != class_310.method_1551().field_1687 || class_156.method_658() - lastHitAt > 30000L) {
            lastHit = null;
        }
    }

    public static class_1309 lastHit(long timeoutMs) {
        class_1309 entity = lastHit;
        if (entity == null || !entity.method_5805() || entity.method_73183() != class_310.method_1551().field_1687) {
            return null;
        }
        return class_156.method_658() - lastHitAt > timeoutMs ? null : entity;
    }

    public static class_1309 current(long timeoutMs) {
        class_1309 living;
        class_1309 hit = CombatTarget.lastHit(timeoutMs);
        if (hit != null) {
            return hit;
        }
        class_1297 looked = class_310.method_1551().field_1692;
        return looked instanceof class_1309 && (living = (class_1309)looked).method_5805() ? living : null;
    }
}

