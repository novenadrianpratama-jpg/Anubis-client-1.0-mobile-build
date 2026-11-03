/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  net.minecraft.class_3532
 *  net.minecraft.class_591
 *  net.minecraft.class_630
 */
package net.anubis.client.module.impl.render.character;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.anubis.client.module.impl.render.character.kit.Bone;
import net.anubis.client.module.impl.render.character.kit.CharacterModel;
import net.anubis.client.module.impl.render.character.kit.Motion;
import net.anubis.client.module.impl.render.character.kit.PartMesh;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_3532;
import net.minecraft.class_591;
import net.minecraft.class_630;

@Environment(value=EnvType.CLIENT)
final class CharacterMotion {
    private static final long FORGET_NANOS = 20000000000L;
    private static final Int2ObjectOpenHashMap<State> STATES = new Int2ObjectOpenHashMap();
    private static final Motion.Input INPUT = new Motion.Input();
    private static final float[] TARGET = new float[3];
    private static long prunedAt;

    private CharacterMotion() {
    }

    static float[] update(class_10055 state, CharacterModel model, class_591 pm, float[] pitch) {
        float dt;
        if (model.motionSlots == 0) {
            return null;
        }
        long now = System.nanoTime();
        CharacterMotion.prune(now);
        State st = (State)STATES.get(state.field_53528);
        if (st == null || st.model != model) {
            st = new State(model);
            st.bodyRot = state.field_53446;
            STATES.put(state.field_53528, (Object)st);
        }
        float f = dt = st.nanos == 0L ? 0.0f : (float)(now - st.nanos) / 1.0E9f;
        if (st.nanos != 0L && dt < 5.0E-4f) {
            return st.angles;
        }
        st.nanos = now;
        float turn = class_3532.method_15393((float)(state.field_53446 - st.bodyRot));
        st.bodyRot = state.field_53446;
        st.turnRate = dt > 0.0f && dt < 0.25f ? (st.turnRate += (turn / dt - st.turnRate) * Math.min(1.0f, dt * 12.0f)) : 0.0f;
        Motion.Input in = INPUT;
        in.time = state.field_53328 / 20.0f;
        in.walkPos = state.field_53450;
        in.walkSpeed = state.field_53451;
        in.turnRate = st.turnRate;
        CharacterMotion.bone(in, Bone.HEAD, pm.field_3398, pitch);
        CharacterMotion.bone(in, Bone.BODY, pm.field_3391, pitch);
        CharacterMotion.bone(in, Bone.RIGHT_ARM, pm.field_3401, pitch);
        CharacterMotion.bone(in, Bone.LEFT_ARM, pm.field_27433, pitch);
        CharacterMotion.bone(in, Bone.RIGHT_LEG, pm.field_3392, pitch);
        CharacterMotion.bone(in, Bone.LEFT_LEG, pm.field_3397, pitch);
        for (PartMesh part : model.parts) {
            if (part.motionSlot < 0 || part.motion == null) continue;
            part.motion.target(in, part.bone, TARGET);
            part.motion.step(st.spring, part.motionSlot * 6, TARGET, dt);
            System.arraycopy(st.spring, part.motionSlot * 6, st.angles, part.motionSlot * 3, 3);
        }
        return st.angles;
    }

    private static void bone(Motion.Input in, Bone bone, class_630 part, float[] pitch) {
        int b = bone.ordinal();
        in.pitch[b] = pitch[b];
        in.yaw[b] = -part.field_3675;
        in.roll[b] = -part.field_3674;
    }

    private static void prune(long now) {
        if (now - prunedAt < 5000000000L) {
            return;
        }
        prunedAt = now;
        STATES.values().removeIf(st -> now - st.nanos > 20000000000L);
    }

    static void clear() {
        STATES.clear();
    }

    @Environment(value=EnvType.CLIENT)
    private static final class State {
        final CharacterModel model;
        final float[] spring;
        final float[] angles;
        float bodyRot;
        float turnRate;
        long nanos;

        State(CharacterModel model) {
            this.model = model;
            this.spring = new float[model.motionSlots * 6];
            this.angles = new float[model.motionSlots * 3];
            for (int i = 0; i < model.motionSlots; ++i) {
                this.spring[i * 6] = Float.NaN;
            }
        }
    }
}

