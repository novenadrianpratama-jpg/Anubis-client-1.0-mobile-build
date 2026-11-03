/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11659
 *  net.minecraft.class_12249
 *  net.minecraft.class_1309
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1921
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3489
 *  net.minecraft.class_4587
 *  net.minecraft.class_746
 *  net.minecraft.class_7923
 *  net.minecraft.class_811
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.lwjgl.glfw.GLFW
 */
package net.anubis.client.module.impl.render;

import net.anubis.client.event.EventPriority;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.KeyInputEvent;
import net.anubis.client.event.impl.MouseInputEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.input.AnubisKeyPolling;
import net.anubis.client.input.ModuleKeybinds;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.render.FirstPersonArm;
import net.anubis.client.module.impl.render.HandModelModule;
import net.anubis.client.module.impl.render.InspectAnimation;
import net.anubis.client.module.impl.render.KnifeMaterial;
import net.anubis.client.module.impl.render.KnifeMesh;
import net.anubis.client.module.impl.render.KnifeSkin;
import net.anubis.client.module.impl.render.SlashTrail;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.KeySetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_1309;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3489;
import net.minecraft.class_4587;
import net.minecraft.class_746;
import net.minecraft.class_7923;
import net.minecraft.class_811;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public final class WeaponInspectModule
extends ClientModule {
    public static volatile WeaponInspectModule active;
    private static final String SWORDS = "Swords";
    private static final String WEAPONS = "Weapons";
    private static final String ANY_ITEM = "Any Item";
    private static final class_2960 KNIFE_TEXTURE;
    private static final float EASE_OUT_SECONDS = 0.15f;
    private static final int IDLE = 0;
    private static final int INSPECT = 1;
    private static final int DEPLOY = 2;
    private static final float[] REST;
    private static final float SPARKLE_SIZE = 0.07f;
    private final KeySetting key = this.addSetting("key", new KeySetting("Inspect Key", 70));
    private final ModeSetting skin = this.addSetting("skin", new ModeSetting("Knife Skin", "Butterfly", "None", "Butterfly", "Karambit", "Katana"));
    private final ModeSetting style = this.addSetting("style", new ModeSetting("Style", "Classic", "Classic", "Twirl"));
    private final ModeSetting items = this.addSetting("items", new ModeSetting("Items", "Swords", "Swords", "Weapons", "Any Item"));
    private final NumberSetting speed = this.addSetting("speed", new NumberSetting("Speed", 1.0f, 0.5f, 2.0f).step(0.05f));
    private final NumberSetting knifeSize = this.addSetting("knife-size", new NumberSetting("Knife Size", 1.0f, 0.5f, 2.0f).step(0.05f));
    private final BooleanSetting showArm = this.addSetting("show-arm", new BooleanSetting("Show Arm", true));
    private final BooleanSetting deploy = this.addSetting("deploy", new BooleanSetting("Deploy Animation", true));
    private final BooleanSetting slashTrail = this.addSetting("slash-trail", new BooleanSetting("Slash Trail", true));
    private final NumberSetting trailLength = this.addSetting("trail-length", new NumberSetting("Trail Length", 0.14f, 0.05f, 0.35f).step(0.01f));
    private final BooleanSetting sparkles = this.addSetting("sparkles", new BooleanSetting("Blade Sparkle", true));
    private final BooleanSetting sneakSkips = this.addSetting("sneak-skips", new BooleanSetting("Sneak Skips Inspect", true));
    private int mode = 0;
    private InspectAnimation.Clip clip;
    private KnifeSkin clipKnife;
    private long startedAt;
    private float seconds;
    private boolean easingOut;
    private long easeStartedAt;
    private float easeFrom;
    private int slot;
    private class_1792 item;
    private int heldSlot = -1;
    private class_1792 heldItem;
    private final float[] state = new float[16];
    private final float[] trailSample = new float[12];
    private final SlashTrail[] trails = new SlashTrail[2];
    private final Matrix4f base = new Matrix4f();
    private final Matrix4f baseInverse = new Matrix4f();
    private final Matrix3f baseNormal = new Matrix3f();
    private boolean baseKnown;

    public WeaponInspectModule() {
        super("weapon-inspect", "CS Knife", "Knife skins for your sword and CS-style inspects with a key of your choice (F by default); hold the key to keep the knife spinning.", ModuleCategory.VISUALS);
        for (int i = 0; i < this.trails.length; ++i) {
            this.trails[i] = new SlashTrail();
        }
        this.knifeSize.visibleWhen(() -> !this.skin.is("None"));
        this.deploy.visibleWhen(() -> !this.skin.is("None"));
        this.sparkles.visibleWhen(() -> !this.skin.is("None"));
        this.trailLength.visibleWhen(this.slashTrail::get);
    }

    @Override
    protected void onEnable() {
        this.mode = 0;
        this.heldItem = null;
        this.clearTrails();
        active = this;
        this.on(KeyInputEvent.TYPE, Stage.PRE, EventPriority.HIGH, this::onKey);
        this.on(MouseInputEvent.TYPE, Stage.PRE, EventPriority.HIGH, this::onMouse);
        this.on(TickEvent.TYPE, Stage.POST, this::onTick);
    }

    @Override
    protected void onDisable() {
        active = null;
        this.mode = 0;
        this.clearTrails();
    }

    public static void handsBegin(class_4587 pose) {
        WeaponInspectModule module = active;
        if (module == null) {
            return;
        }
        module.base.set((Matrix4fc)pose.method_23760().method_23761());
        module.base.invert(module.baseInverse);
        module.baseNormal.set((Matrix3fc)pose.method_23760().method_23762());
        module.baseKnown = true;
    }

    public static boolean renderItem(class_4587 pose, class_1309 holder, class_1799 stack, class_811 context, class_11659 collector, int light, boolean mainHand) {
        WeaponInspectModule module = active;
        return module != null && module.render(pose, holder, stack, context, collector, light, mainHand);
    }

    private void onKey(KeyInputEvent event) {
        if (event.action != 0 && this.pressed(event.key, event.action == 2)) {
            event.setCancelled(true);
        }
    }

    private void onMouse(MouseInputEvent event) {
        if (event.action == 1 && this.pressed(1000 + event.button, false)) {
            event.setCancelled(true);
        }
    }

    private boolean pressed(int pressed, boolean repeat) {
        int bound = this.key.value();
        if (!ModuleKeybinds.isBindable(bound) || pressed != bound) {
            return false;
        }
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (mc.field_1755 != null || player == null || player.method_7325()) {
            return false;
        }
        if (this.sneakSkips.get() && player.method_5715()) {
            return false;
        }
        class_1799 held = player.method_6047();
        if (!this.inspectable(held)) {
            return false;
        }
        if (!repeat && !player.method_6115()) {
            this.start(1, player.method_31548().method_67532(), held);
        }
        return true;
    }

    private boolean held() {
        int bound = this.key.value();
        class_310 mc = class_310.method_1551();
        if (!ModuleKeybinds.isBindable(bound) || mc.field_1755 != null) {
            return false;
        }
        return bound >= 1000 ? GLFW.glfwGetMouseButton((long)mc.method_22683().method_4490(), (int)(bound - 1000)) == 1 : AnubisKeyPolling.isKeyDown(mc.method_22683(), bound);
    }

    private boolean inspectable(class_1799 stack) {
        if (stack.method_7960()) {
            return false;
        }
        if (this.items.is(ANY_ITEM) || WeaponInspectModule.isSword(stack)) {
            return true;
        }
        return this.items.is(WEAPONS) && (stack.method_31573(class_3489.field_42612) || stack.method_31573(class_3489.field_63257) || stack.method_31574(class_1802.field_49814) || stack.method_31574(class_1802.field_8547));
    }

    private static boolean isSword(class_1799 stack) {
        return !stack.method_7960() && (stack.method_31573(class_3489.field_42611) || class_7923.field_41178.method_10221((Object)stack.method_7909()).method_12832().endsWith("_sword"));
    }

    private static boolean handheld(class_1799 stack) {
        return WeaponInspectModule.isSword(stack) || stack.method_31573(class_3489.field_42612) || stack.method_31573(class_3489.field_42614) || stack.method_31573(class_3489.field_42615) || stack.method_31573(class_3489.field_42613);
    }

    private void start(int next, int selected, class_1799 held) {
        KnifeSkin knife;
        KnifeSkin knifeSkin = knife = WeaponInspectModule.isSword(held) ? KnifeSkin.of(this.skin.get()) : null;
        this.clip = next == 2 ? knife.deploy : (knife != null ? knife.inspect : InspectAnimation.clip(this.style.get()));
        this.clipKnife = knife;
        this.mode = next;
        this.seconds = this.clip.seconds;
        this.easingOut = false;
        this.startedAt = System.nanoTime();
        this.slot = selected;
        this.item = held.method_7909();
    }

    private void easeOut() {
        long now = System.nanoTime();
        this.easeFrom = Math.min(this.progress(now), 1.0f);
        this.easeStartedAt = now;
        this.easingOut = true;
    }

    private void onTick(TickEvent event) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            this.mode = 0;
            this.heldItem = null;
            return;
        }
        class_1799 held = player.method_6047();
        int selected = player.method_31548().method_67532();
        if (selected != this.heldSlot || !held.method_31574(this.heldItem)) {
            this.heldSlot = selected;
            this.heldItem = held.method_7909();
            if (this.deploy.get() && WeaponInspectModule.isSword(held) && KnifeSkin.of(this.skin.get()) != null) {
                this.start(2, selected, held);
            } else if (this.mode != 0 && !this.easingOut) {
                this.easeOut();
            }
            return;
        }
        if (this.mode == 0 || this.easingOut) {
            return;
        }
        if (player.field_6252) {
            this.mode = 0;
        } else if (player.method_6115()) {
            this.easeOut();
        } else if (this.loopedProgress(System.nanoTime()) >= 1.0f) {
            this.mode = 0;
        }
    }

    private float progress(long now) {
        return (float)(now - this.startedAt) / 1.0E9f / (this.seconds / this.speed.get());
    }

    private float loopedProgress(long now) {
        float progress = this.progress(now);
        if (this.mode != 1 || !this.clip.loops() || progress < this.clip.loopExit || !this.held()) {
            return progress;
        }
        float duration = this.seconds / this.speed.get();
        long cycle = (long)((this.clip.loopExit - this.clip.loopEnter) * duration * 1.0E9f);
        if (cycle <= 0L) {
            return progress;
        }
        long over = (long)((progress - this.clip.loopExit) * duration * 1.0E9f);
        this.startedAt += (over / cycle + 1L) * cycle;
        return this.progress(now);
    }

    private float animate(class_1799 stack, KnifeSkin knife, long now) {
        float progress;
        if (this.mode == 0 || !stack.method_31574(this.item)) {
            return 0.0f;
        }
        if (knife != this.clipKnife) {
            this.mode = 0;
            return 0.0f;
        }
        float weight = 1.0f;
        if (this.easingOut) {
            float t = (float)(now - this.easeStartedAt) / 1.0E9f / 0.15f;
            if (t >= 1.0f) {
                this.mode = 0;
                return 0.0f;
            }
            progress = this.easeFrom;
            weight = 1.0f - t * t * (3.0f - 2.0f * t);
        } else {
            progress = this.loopedProgress(now);
            if (progress >= 1.0f) {
                this.mode = 0;
                return 0.0f;
            }
        }
        this.clip.sample(progress, this.state);
        return weight;
    }

    private boolean render(class_4587 pose, class_1309 holder, class_1799 stack, class_811 context, class_11659 collector, int light, boolean mainHand) {
        boolean trailing;
        int side = context == class_811.field_4321 ? -1 : 1;
        boolean sword = WeaponInspectModule.isSword(stack);
        KnifeSkin knife = sword ? KnifeSkin.of(this.skin.get()) : null;
        long now = System.nanoTime();
        float weight = mainHand ? this.animate(stack, knife, now) : 0.0f;
        float[] at = weight > 0.0f ? this.state : REST;
        boolean bl = trailing = mainHand && sword && this.slashTrail.get() && this.baseKnown;
        if (mainHand && !trailing) {
            this.clearTrails();
        }
        if (knife == null) {
            FirstPersonArm.Arm arm;
            if ((this.showArm.get() && WeaponInspectModule.handheld(stack) || HandModelModule.covers(stack, mainHand)) && (arm = FirstPersonArm.arm()) != null) {
                FirstPersonArm.hold(pose, arm, stack, context, holder, collector, light, at[0] * weight, at[1] * weight, at[2] * weight);
            }
            if (weight > 0.0f) {
                InspectAnimation.place(pose, at, weight, side, 0.07f, 0.2f, 0.07f);
            }
            if (trailing) {
                this.swordTrail(pose.method_23760().method_23761(), side);
                this.record(now, 1);
                this.submitGlow(collector, now, KnifeMaterial.of((class_1799)stack).glow, 0.0f, null);
            }
            return false;
        }
        KnifeMaterial material = KnifeMaterial.of(stack);
        FirstPersonArm.Arm arm = this.showArm.get() || HandModelModule.wants(mainHand) ? FirstPersonArm.arm() : null;
        boolean slim = arm != null && arm.slim();
        pose.method_22903();
        if (side < 0) {
            pose.method_22905(-1.0f, 1.0f, 1.0f);
        }
        knife.draw(pose, at, weight, slim, this.knifeSize.get(), WeaponInspectModule.sink(collector, light, side, arm, mainHand && WeaponInspectModule.offHandFree(), material, stack.method_7958()), trailing ? this.trailSample : null);
        pose.method_22909();
        if (trailing) {
            this.record(now, knife.streakCount());
            float flash = this.sparkles.get() && weight > 0.0f ? knife.sparkle(at, weight) : 0.0f;
            this.submitGlow(collector, now, material.glow, flash, knife);
        }
        return true;
    }

    private static KnifeSkin.Sink sink(final class_11659 collector, final int light, final int side, final FirstPersonArm.Arm arm, final boolean otherFree, final KnifeMaterial material, final boolean foil) {
        final class_1921 solid = class_12249.method_75994((class_2960)KNIFE_TEXTURE);
        final int[] palette = material.palette;
        return new KnifeSkin.Sink(){

            @Override
            public void part(class_4587 part, KnifeMesh mesh, KnifeMesh.Shine shine) {
                collector.method_73483(part, solid, (p, out) -> mesh.emit(p, out, palette, light, shine));
                if (foil) {
                    collector.method_73529(1).method_73483(part, class_12249.method_75997(), (p, out) -> mesh.emit(p, out, palette, light, KnifeMesh.Shine.NONE));
                }
            }

            @Override
            public void glow(class_4587 part, KnifeMesh mesh, KnifeMesh.Shine shine) {
                collector.method_73529(1).method_73483(part, class_12249.method_76014((class_2960)KNIFE_TEXTURE), (p, out) -> mesh.emitGlow(p, out, material2.glow, shine));
            }

            @Override
            public void arm(class_4587 hand, boolean other) {
                if (arm != null && (!other || otherFree)) {
                    arm.draw(hand, side < 0 != other, collector, light);
                }
            }
        };
    }

    private static boolean offHandFree() {
        class_746 player = class_310.method_1551().field_1724;
        return player != null && player.method_6079().method_7960();
    }

    private void swordTrail(Matrix4f pose, int side) {
        Matrix4f m = new Matrix4f((Matrix4fc)pose).translate((float)side * 1.13f / 16.0f, 0.2f, 0.070625f).rotate((Quaternionfc)new Quaternionf().rotationXYZ(0.0f, -1.5707964f, 0.43633232f)).scale(0.68f).translate(-0.5f, -0.5f, -0.5f);
        Vector3f p = m.transformPosition(0.3125f, 0.3125f, 0.5f, new Vector3f());
        this.trailSample[0] = p.x;
        this.trailSample[1] = p.y;
        this.trailSample[2] = p.z;
        m.transformPosition(0.96875f, 0.96875f, 0.5f, p);
        this.trailSample[3] = p.x;
        this.trailSample[4] = p.y;
        this.trailSample[5] = p.z;
    }

    private void record(long now, int streaks) {
        Vector3f p = new Vector3f();
        for (int s = 0; s < this.trails.length; ++s) {
            if (s >= streaks) {
                this.trails[s].clear();
                continue;
            }
            for (int i = s * 6; i < s * 6 + 6; i += 3) {
                this.baseInverse.transformPosition(this.trailSample[i], this.trailSample[i + 1], this.trailSample[i + 2], p);
                this.trailSample[i] = p.x;
                this.trailSample[i + 1] = p.y;
                this.trailSample[i + 2] = p.z;
            }
            this.trails[s].add(now, this.trailSample, s * 6);
        }
    }

    private void clearTrails() {
        for (SlashTrail trail : this.trails) {
            trail.clear();
        }
    }

    private void submitGlow(class_11659 collector, long now, int glow, float flash, KnifeSkin knife) {
        long lifetime = (long)(this.trailLength.get() * 1.0E9f);
        int streaks = knife != null ? knife.streakCount() : 1;
        boolean showing = flash > 0.0f;
        for (int s = 0; s < streaks && !showing; ++s) {
            showing = !this.trails[s].isEmpty(now, lifetime);
        }
        if (!showing) {
            return;
        }
        float tipX = this.trailSample[3];
        float tipY = this.trailSample[4];
        float tipZ = this.trailSample[5];
        float turn = (float)(now % 6283185307L) / 1.0E9f * 1.5f;
        class_4587 hand = new class_4587();
        hand.method_23760().method_23761().set((Matrix4fc)this.base);
        hand.method_23760().method_23762().set((Matrix3fc)this.baseNormal);
        collector.method_73529(1).method_73483(hand, class_12249.method_76014((class_2960)KNIFE_TEXTURE), (p, out) -> {
            for (int s = 0; s < streaks; ++s) {
                this.trails[s].emit(p, out, now, lifetime, glow, knife != null ? knife.streakStrength(s) : 1.0f);
            }
            if (flash > 0.0f) {
                SlashTrail.sparkle(p, out, tipX, tipY, tipZ, 0.07f * flash, turn, glow, flash);
            }
        });
    }

    static {
        KNIFE_TEXTURE = class_2960.method_60655((String)"anubis", (String)"textures/misc/knife_base.png");
        REST = new float[16];
    }
}

