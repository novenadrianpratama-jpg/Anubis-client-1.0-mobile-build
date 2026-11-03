/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.render.character;

import net.anubis.client.module.impl.render.character.WornCharacter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface CharacterHolder {
    public WornCharacter anubis$character();

    public void anubis$setCharacter(WornCharacter var1);

    public float anubis$seat();

    public void anubis$setSeat(float var1);
}

