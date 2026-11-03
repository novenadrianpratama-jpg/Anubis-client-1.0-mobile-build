/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12125
 *  net.minecraft.class_1268
 *  net.minecraft.class_1269
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_3965
 *  net.minecraft.class_3966
 *  net.minecraft.class_636
 *  net.minecraft.class_746
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.client.mixin;

import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.combat.WTapRecodeModule;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12125;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_636;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_636.class})
public abstract class MixinMultiPlayerGameMode {
    @Inject(method={"method_2918"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_636;method_2911()V")})
    private void anubis$beforeAttack(class_1657 player, class_1297 entity, CallbackInfo ci) {
        WTapRecodeModule wTap = ModuleRegistry.get(WTapRecodeModule.class);
        if (wTap != null && wTap.isEnabled()) {
            wTap.beforeAttack(player, entity);
        }
    }

    @Inject(method={"method_2918"}, at={@At(value="RETURN")})
    private void anubis$afterAttack(class_1657 player, class_1297 entity, CallbackInfo ci) {
        WTapRecodeModule wTap;
        if (player == class_310.method_1551().field_1724) {
            MeleeAttacks.noteAction(player);
        }
        if ((wTap = ModuleRegistry.get(WTapRecodeModule.class)) != null && wTap.isEnabled()) {
            wTap.afterAttack();
        }
    }

    @Inject(method={"method_75407"}, at={@At(value="RETURN")})
    private void anubis$afterStab(class_12125 weapon, CallbackInfo ci) {
        MeleeAttacks.noteAction((class_1657)class_310.method_1551().field_1724);
        WTapRecodeModule wTap = ModuleRegistry.get(WTapRecodeModule.class);
        if (wTap != null && wTap.isEnabled()) {
            wTap.afterStab();
        }
    }

    @Inject(method={"method_2919"}, at={@At(value="RETURN")})
    private void anubis$afterUseItem(class_1657 player, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
        if (player == class_310.method_1551().field_1724) {
            MeleeAttacks.noteAction(player);
        }
    }

    @Inject(method={"method_2896"}, at={@At(value="RETURN")})
    private void anubis$afterUseItemOn(class_746 player, class_1268 hand, class_3965 hit, CallbackInfoReturnable<class_1269> cir) {
        if (player == class_310.method_1551().field_1724) {
            MeleeAttacks.noteAction((class_1657)player);
        }
    }

    @Inject(method={"method_2905"}, at={@At(value="RETURN")})
    private void anubis$afterInteract(class_1657 player, class_1297 entity, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
        if (player == class_310.method_1551().field_1724) {
            MeleeAttacks.noteAction(player);
        }
    }

    @Inject(method={"method_2917"}, at={@At(value="RETURN")})
    private void anubis$afterInteractAt(class_1657 player, class_1297 entity, class_3966 hit, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
        if (player == class_310.method_1551().field_1724) {
            MeleeAttacks.noteAction(player);
        }
    }

    @Inject(method={"method_2897"}, at={@At(value="RETURN")})
    private void anubis$afterRelease(class_1657 player, CallbackInfo ci) {
        if (player == class_310.method_1551().field_1724) {
            MeleeAttacks.noteAction(player);
        }
    }
}

