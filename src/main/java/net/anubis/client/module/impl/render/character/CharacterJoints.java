/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10017
 *  net.minecraft.class_10055
 *  net.minecraft.class_12122
 *  net.minecraft.class_1306
 *  net.minecraft.class_3532
 *  net.minecraft.class_572$class_573
 */
package net.anubis.client.module.impl.render.character;

import net.anubis.client.module.impl.render.character.CharacterModule;
import net.anubis.client.module.impl.render.character.kit.Bone;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10017;
import net.minecraft.class_10055;
import net.minecraft.class_12122;
import net.minecraft.class_1306;
import net.minecraft.class_3532;
import net.minecraft.class_572;

@Environment(value=EnvType.CLIENT)
final class CharacterJoints {
    static final float CROUCH_HIP = 0.5f;
    static final float SADDLE_KNEE = 1.35f;
    static final float SEAT_KNEE = 0.3f;
    private static final float CROUCH_KNEE = 0.62f;
    private static final float CROUCH_ELBOW = 0.2f;
    private static final float MAX_KNEE = 1.7f;
    private static final float MAX_ELBOW = 1.6f;

    private CharacterJoints() {
    }

    static float angle(class_10055 state, Bone bone, float swing) {
        float fade;
        float f = fade = state.field_53411 || state.field_53459 ? 0.0f : 1.0f - state.field_53403 * 0.8f;
        if (fade <= 0.0f) {
            return 0.0f;
        }
        if (bone.leg()) {
            return CharacterJoints.knee(state, bone) * fade;
        }
        if (bone.arm()) {
            return -CharacterJoints.elbow(state, bone, swing) * fade;
        }
        return 0.0f;
    }

    private static float knee(class_10055 state, Bone bone) {
        if (state.field_53413) {
            return CharacterModule.seat((class_10017)state);
        }
        float speed = Math.min(1.0f, state.field_53451 / state.field_53405);
        float phase = state.field_53450 * 0.6662f + (bone == Bone.LEFT_LEG ? (float)Math.PI : 0.0f);
        float flex = speed * (0.1f + 0.85f * Math.max(0.0f, class_3532.method_15374((double)phase)));
        if (state.field_53410) {
            flex += 0.62f;
        }
        return Math.min(1.7f, flex);
    }

    private static float elbow(class_10055 state, Bone bone, float swing) {
        class_1306 arm;
        float flex;
        class_572.class_573 other;
        boolean right = bone == Bone.RIGHT_ARM;
        class_572.class_573 pose = right ? state.field_55304 : state.field_55306;
        class_572.class_573 class_5732 = other = right ? state.field_55306 : state.field_55304;
        if (other.method_30156()) {
            return 0.0f;
        }
        float speed = Math.min(1.0f, state.field_53451 / state.field_53405);
        switch (pose) {
            case field_3409: {
                float f = 0.16f + 0.45f * Math.max(0.0f, -swing) + 0.3f * speed;
                break;
            }
            case field_3410: {
                float f = 0.3f + 0.2f * speed;
                break;
            }
            case field_3406: {
                float f = 0.55f;
                break;
            }
            case field_42877: {
                float f = 0.4f;
                break;
            }
            default: {
                float f = flex = 0.0f;
            }
        }
        if (flex <= 0.0f) {
            return 0.0f;
        }
        if (state.field_53410) {
            flex += 0.2f;
        }
        class_1306 class_13062 = arm = right ? class_1306.field_6183 : class_1306.field_6182;
        if (state.field_63604 > 0.0f && state.field_53408 == arm && state.field_63603 == class_12122.field_63399) {
            flex += 0.6f * class_3532.method_15374((double)(state.field_63604 * (float)Math.PI));
        }
        return Math.min(1.6f, flex);
    }
}

