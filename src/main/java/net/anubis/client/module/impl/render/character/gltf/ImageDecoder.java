/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.render.character.gltf;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface ImageDecoder {
    public Picture decode(byte[] var1) throws IOException;

    @Environment(value=EnvType.CLIENT)
    public record Picture(int width, int height, int[] argb) {
    }
}

