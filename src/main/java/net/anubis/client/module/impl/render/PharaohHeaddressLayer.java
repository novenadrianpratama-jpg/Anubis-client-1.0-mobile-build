/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  net.minecraft.class_11659
 *  net.minecraft.class_12249
 *  net.minecraft.class_1921
 *  net.minecraft.class_2960
 *  net.minecraft.class_3883
 *  net.minecraft.class_3887
 *  net.minecraft.class_4587
 *  net.minecraft.class_591
 *  net.minecraft.class_9334
 */
package net.anubis.client.module.impl.render;

import net.anubis.client.module.impl.render.HeadCosmeticModule;
import net.anubis.client.module.impl.render.PharaohMesh;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_3883;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_591;
import net.minecraft.class_9334;

@Environment(value=EnvType.CLIENT)
public final class PharaohHeaddressLayer
extends class_3887<class_10055, class_591> {
    private static final class_2960 TEXTURE = class_2960.method_60655((String)"anubis", (String)"textures/cosmetic/headdress_base.png");
    private static final float HEAD_CENTRE = -0.25f;

    public PharaohHeaddressLayer(class_3883<class_10055, class_591> parent) {
        super(parent);
    }

    public void submit(class_4587 poseStack, class_11659 collector, int light, class_10055 state, float yRot, float xRot) {
        HeadCosmeticModule module = HeadCosmeticModule.worn;
        if (module == null || state.field_53333 || !module.wears(state)) {
            return;
        }
        boolean helmet = !state.field_55309.method_7960() || !state.field_53467.method_65606() || state.field_55315 != null;
        boolean chestplate = !state.field_53418.method_7960() && !state.field_53418.method_57826(class_9334.field_54197);
        PharaohMesh head = module.headMesh(helmet);
        PharaohMesh body = module.bodyMesh(chestplate);
        PharaohMesh.Look look = module.look();
        float scale = module.scale();
        class_1921 type = class_12249.method_75994((class_2960)TEXTURE);
        class_591 model = (class_591)this.method_17165();
        poseStack.method_22903();
        model.method_63512().method_22703(poseStack);
        poseStack.method_22903();
        model.method_2838().method_22703(poseStack);
        if (scale != 1.0f) {
            poseStack.method_46416(0.0f, -0.25f, 0.0f);
            poseStack.method_22905(scale, scale, scale);
            poseStack.method_46416(0.0f, 0.25f, 0.0f);
        }
        collector.method_73483(poseStack, type, (pose, out) -> head.emit(pose, out, light, look));
        poseStack.method_22909();
        if (!body.isEmpty()) {
            model.field_3391.method_22703(poseStack);
            collector.method_73483(poseStack, type, (pose, out) -> body.emit(pose, out, light, look));
        }
        poseStack.method_22909();
    }
}

