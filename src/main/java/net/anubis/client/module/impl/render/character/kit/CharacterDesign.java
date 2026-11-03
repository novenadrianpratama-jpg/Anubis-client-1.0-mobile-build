/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.render.character.kit;

import net.anubis.client.module.impl.render.character.kit.Rig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public abstract class CharacterDesign {
    private final String id;
    private final String name;

    protected CharacterDesign(String id, String name) {
        if (id == null || !id.matches("[a-z0-9_]+")) {
            throw new IllegalArgumentException("character id must match [a-z0-9_]+: " + id);
        }
        this.id = id;
        this.name = name;
    }

    public final String id() {
        return this.id;
    }

    public final String name() {
        return this.name;
    }

    protected abstract void define(Rig var1);

    public final Rig rig() {
        Rig rig = new Rig();
        this.define(rig);
        return rig;
    }
}

