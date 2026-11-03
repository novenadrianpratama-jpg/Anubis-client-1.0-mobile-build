/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  net.minecraft.class_1007
 *  net.minecraft.class_11659
 *  net.minecraft.class_11890
 *  net.minecraft.class_1297
 *  net.minecraft.class_2960
 *  net.minecraft.class_3883
 *  net.minecraft.class_4587
 *  net.minecraft.class_5617$class_5618
 *  net.minecraft.class_583
 *  net.minecraft.class_591
 *  net.minecraft.class_630
 *  net.minecraft.class_922
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.render.character.CharacterLayer;
import net.anubis.client.module.impl.render.character.CharacterModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_1007;
import net.minecraft.class_11659;
import net.minecraft.class_11890;
import net.minecraft.class_1297;
import net.minecraft.class_2960;
import net.minecraft.class_3883;
import net.minecraft.class_4587;
import net.minecraft.class_5617;
import net.minecraft.class_583;
import net.minecraft.class_591;
import net.minecraft.class_630;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1007.class})
public abstract class MixinCharacterAvatarRenderer<T extends class_11890>
extends class_922<T, class_10055, class_591> {
    protected MixinCharacterAvatarRenderer(class_5617.class_5618 context, class_591 model, float shadowRadius) {
        super(context, (class_583)model, shadowRadius);
    }

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void anubis$addCharacterLayer(class_5617.class_5618 context, boolean slim, CallbackInfo ci) {
        this.method_4046(new CharacterLayer((class_3883<class_10055, class_591>)this));
    }

    @Inject(method={"method_62604"}, at={@At(value="TAIL")})
    private void anubis$extractCharacter(class_11890 entity, class_10055 state, float partialTick, CallbackInfo ci) {
        CharacterModule.extract((class_1297)entity, state);
    }

    @Inject(method={"method_23205"}, at={@At(value="HEAD")}, cancellable=true)
    private void anubis$characterFirstPersonHand(class_4587 poseStack, class_11659 collector, int light, class_2960 skin, class_630 arm, boolean sleeve, CallbackInfo ci) {
        if (CharacterLayer.firstPersonHand((class_591)this.method_4038(), poseStack, collector, light, arm)) {
            ci.cancel();
        }
    }
}

