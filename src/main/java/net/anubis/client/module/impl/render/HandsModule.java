/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_4587
 *  net.minecraft.class_7833
 *  org.joml.Quaternionfc
 */
package net.anubis.client.module.impl.render;

import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.ActionSetting;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public final class HandsModule
extends ClientModule {
    public static volatile HandsModule live;
    private static final String MAIN = "Main Hand";
    private static final String OFF = "Offhand";
    private static final float PIVOT_X = 0.56f;
    private static final float PIVOT_Y = -0.52f;
    private static final float PIVOT_Z = -0.72f;
    private static final float[] REST;
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    private static final int PITCH = 3;
    private static final int YAW = 4;
    private static final int ROLL = 5;
    private static final int SCALE = 6;
    private static final float SETTLED = 0.001f;
    private static final float MAX_STEP = 0.1f;
    private static final float SLOWEST_RATE = 3.0f;
    private static final float FASTEST_RATE = 30.0f;
    private final BooleanSetting link = this.addSetting("link-hands", new BooleanSetting("Link Hands", false));
    private final ModeSetting editing = this.addSetting("editing", new ModeSetting("Editing", "Main Hand", "Main Hand", "Offhand"));
    private final NumberSetting[] main = this.axes("main", true);
    private final NumberSetting[] off = this.axes("off", false);
    private final NumberSetting glide = this.addSetting("glide", new NumberSetting("Glide", 0.4f, 0.0f, 1.0f).step(0.05f));
    private final HandState mainState = new HandState();
    private final HandState offState = new HandState();

    public HandsModule() {
        super("hand-pose", "Hand Pose", "Moves, rotates and resizes your first-person hands.", ModuleCategory.VISUALS);
        this.editing.visibleWhen(() -> !this.link.get());
        this.addSetting("reset-hand", new ActionSetting("Reset Hand", this::resetEdited));
    }

    private NumberSetting[] axes(String prefix, boolean mainHand) {
        NumberSetting[] axes;
        for (NumberSetting axis : axes = new NumberSetting[]{this.addSetting(prefix + "-x", new NumberSetting("Offset X", REST[0], -1.0f, 1.0f).step(0.01f)), this.addSetting(prefix + "-y", new NumberSetting("Offset Y", REST[1], -1.0f, 1.0f).step(0.01f)), this.addSetting(prefix + "-z", new NumberSetting("Offset Z", REST[2], -1.0f, 1.0f).step(0.01f)), this.addSetting(prefix + "-pitch", new NumberSetting("Pitch", REST[3], -180.0f, 180.0f).step(1.0f)), this.addSetting(prefix + "-yaw", new NumberSetting("Yaw", REST[4], -180.0f, 180.0f).step(1.0f)), this.addSetting(prefix + "-roll", new NumberSetting("Roll", REST[5], -180.0f, 180.0f).step(1.0f)), this.addSetting(prefix + "-scale", new NumberSetting("Scale", REST[6], 0.3f, 2.0f).step(0.05f))}) {
            axis.visibleWhen(mainHand ? () -> this.link.get() || this.editing.is(MAIN) : () -> !this.link.get() && this.editing.is(OFF));
        }
        return axes;
    }

    @Override
    protected void onEnable() {
        this.mainState.stamp = 0L;
        this.offState.stamp = 0L;
        live = this;
    }

    private void resetEdited() {
        NumberSetting[] axes = this.link.get() || this.editing.is(MAIN) ? this.main : this.off;
        for (int i = 0; i < axes.length; ++i) {
            axes[i].set(REST[i]);
        }
    }

    public static void shape(class_4587 pose, boolean mainHand, int side) {
        HandsModule module = live;
        if (module != null) {
            module.shapeHand(pose, mainHand, side);
        }
    }

    private void shapeHand(class_4587 pose, boolean mainHand, int side) {
        boolean on = this.isEnabled();
        HandState state = mainHand ? this.mainState : this.offState;
        state.step(mainHand || this.link.get() ? this.main : this.off, on, this.follow());
        if (!on && this.mainState.rested && this.offState.rested) {
            live = null;
            return;
        }
        if (state.rested) {
            return;
        }
        float[] v = state.value;
        pose.method_46416((float)side * (0.56f + v[0]), -0.52f + v[1], -0.72f + v[2]);
        pose.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)side * v[4]));
        pose.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(v[3]));
        pose.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)side * v[5]));
        pose.method_22905(v[6], v[6], v[6]);
        pose.method_46416((float)(-side) * 0.56f, 0.52f, 0.72f);
    }

    private float follow() {
        float g = this.glide.get();
        return g <= 0.0f ? -1.0f : 30.0f + -27.0f * g;
    }

    static {
        REST = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    }

    @Environment(value=EnvType.CLIENT)
    private static final class HandState {
        final float[] value = (float[])REST.clone();
        long stamp;
        boolean rested = true;

        private HandState() {
        }

        void step(NumberSetting[] target, boolean on, float rate) {
            long now = System.nanoTime();
            float dt = this.stamp == 0L ? 0.0f : Math.min(0.1f, (float)(now - this.stamp) / 1.0E9f);
            this.stamp = now;
            float k = rate < 0.0f ? 1.0f : 1.0f - (float)Math.exp(-rate * dt);
            boolean settled = true;
            for (int i = 0; i < this.value.length; ++i) {
                float next;
                float goal = on ? target[i].get() : REST[i];
                if (Math.abs(goal - (next = this.value[i] + (goal - this.value[i]) * k)) < 0.001f) {
                    next = goal;
                }
                this.value[i] = next;
                if (next == REST[i]) continue;
                settled = false;
            }
            this.rested = settled;
        }
    }
}

