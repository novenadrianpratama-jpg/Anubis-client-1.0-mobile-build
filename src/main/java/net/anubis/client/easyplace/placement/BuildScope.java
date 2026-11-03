/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338
 */
package net.anubis.client.easyplace.placement;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;

@Environment(value=EnvType.CLIENT)
public record BuildScope(int maxY, double lowWeight, Predicate<class_2338> skip) {
    public boolean allows(class_2338 pos) {
        return pos.method_10264() <= this.maxY && !this.skip.test(pos);
    }
}

