/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.render.character.CharacterHolder;
import net.anubis.client.module.impl.render.character.WornCharacter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_10055.class})
public abstract class MixinCharacterAvatarRenderState
implements CharacterHolder {
    @Unique
    private WornCharacter anubis$character;
    @Unique
    private float anubis$seat;

    @Override
    public WornCharacter anubis$character() {
        return this.anubis$character;
    }

    @Override
    public void anubis$setCharacter(WornCharacter character) {
        this.anubis$character = character;
    }

    @Override
    public float anubis$seat() {
        return this.anubis$seat;
    }

    @Override
    public void anubis$setSeat(float knee) {
        this.anubis$seat = knee;
    }
}

