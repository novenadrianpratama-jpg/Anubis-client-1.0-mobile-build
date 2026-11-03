/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.render.character.kit;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
final class Mesh {
    final float[] pos;
    final int vertexCount;
    final int[] tris;
    final int triCount;

    Mesh(float[] pos, int vertexCount, int[] tris, int triCount) {
        this.pos = pos;
        this.vertexCount = vertexCount;
        this.tris = tris;
        this.triCount = triCount;
    }
}

