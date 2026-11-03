/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1937
 *  net.minecraft.class_2596
 *  net.minecraft.class_2663
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_8042
 */
package net.anubis.client.util.combat;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1937;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_8042;

@Environment(value=EnvType.CLIENT)
public final class TotemPops {
    private TotemPops() {
    }

    public static void scan(class_2596<?> packet, Consumer<class_1309> sink) {
        if (packet instanceof class_2663) {
            class_2663 event = (class_2663)packet;
            TotemPops.check(event, sink);
        } else if (packet instanceof class_8042) {
            class_8042 bundle = (class_8042)packet;
            for (class_2596 inner : bundle.method_48324()) {
                if (!(inner instanceof class_2663)) continue;
                class_2663 event = (class_2663)inner;
                TotemPops.check(event, sink);
            }
        }
    }

    private static void check(class_2663 event, Consumer<class_1309> sink) {
        class_1309 living;
        if (event.method_11470() != 35) {
            return;
        }
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return;
        }
        class_1297 entity = event.method_11469((class_1937)level);
        if (entity instanceof class_1309 && !(living = (class_1309)entity).method_31481()) {
            sink.accept(living);
        }
    }
}

