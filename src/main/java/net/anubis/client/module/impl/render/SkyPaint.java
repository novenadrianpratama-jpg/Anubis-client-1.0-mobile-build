/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.render;

import net.anubis.client.module.impl.render.SkyImage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
interface SkyPaint {
    public static final float HORIZON = 0.72f;
    public static final float RADIANS_PER_UNIT = 1.0471976f;

    public SkyImage upload(String var1, int var2, int var3, int[] var4);

    public SkyImage upload(String var1, int var2, int var3, int[][] var4);

    public void gradient(boolean var1, float var2, float var3, float var4, float var5, int var6, int var7);

    public void band(SkyImage var1, float var2, float var3, float var4, float var5, int var6, boolean var7, int var8);

    public void sprite(SkyImage var1, float var2, float var3, float var4, float var5, float var6, int var7, boolean var8);

    public void glow(float var1, float var2, float var3, int var4, float var5);
}

