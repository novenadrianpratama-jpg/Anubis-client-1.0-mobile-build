/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10017
 *  net.minecraft.class_12179
 *  net.minecraft.class_12180
 *  net.minecraft.class_1297
 *  net.minecraft.class_1308
 *  net.minecraft.class_1309
 *  net.minecraft.class_1511
 *  net.minecraft.class_1542
 *  net.minecraft.class_1569
 *  net.minecraft.class_1657
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  net.minecraft.class_4604
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  net.minecraft.class_9848
 *  org.joml.Matrix4f
 */
package net.anubis.client.module.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import net.anubis.client.config.FriendStore;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.render.CosmeticAccent;
import net.anubis.client.module.impl.render.ShaderEspPass;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.ShaderOverlay;
import net.anubis.client.util.combat.CombatTarget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10017;
import net.minecraft.class_12179;
import net.minecraft.class_12180;
import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1511;
import net.minecraft.class_1542;
import net.minecraft.class_1569;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4604;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_9848;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public final class ShaderEspModule
extends ClientModule {
    public static volatile ShaderEspModule active;
    private static final String COLOR_THEME = "Theme";
    private static final String COLOR_MANUAL = "Manual";
    private static final int OPAQUE = -16777216;
    private static final int WHITE = -1;
    private static final float FRIEND_LIFT = 0.45f;
    private static final float TARGET_LIFT = 0.8f;
    private static final float SHEEN_LIFT = 0.6f;
    private static final int LIMIT_CAP = 96;
    private static final long TARGET_MEMORY_MS = 3500L;
    private static final long BREATH_NANOS = 2600000000L;
    private static final long SHEEN_NANOS = 5200000000L;
    private static final double CULL_MARGIN = 0.75;
    private final BooleanSetting players = this.addSetting("players", new BooleanSetting("Players", true));
    private final BooleanSetting passives = this.addSetting("passives", new BooleanSetting("Passives", false));
    private final BooleanSetting items = this.addSetting("items", new BooleanSetting("Items", false));
    private final BooleanSetting crystals = this.addSetting("crystals", new BooleanSetting("Crystals", true));
    private final BooleanSetting markTarget = this.addSetting("mark-target", new BooleanSetting("Mark Target", true));
    private final ModeSetting colorMode = this.addSetting("color", new ModeSetting("Color", "Theme", "Theme", "Manual"));
    private final ColorSetting playerColor = this.addSetting("player-color", new ColorSetting("Player Color", -13338378));
    private final ColorSetting friendColor = this.addSetting("friend-color", new ColorSetting("Friend Color", -7358982));
    private final ColorSetting passiveColor = this.addSetting("passive-color", new ColorSetting("Passive Color", -13061400));
    private final ColorSetting itemColor = this.addSetting("item-color", new ColorSetting("Item Color", -11744808));
    private final ColorSetting crystalColor = this.addSetting("crystal-color", new ColorSetting("Crystal Color", -6128394));
    private final ColorSetting targetColor = this.addSetting("target-color", new ColorSetting("Target Color", -2693891));
    private final NumberSetting stroke = this.addSetting("stroke", new NumberSetting("Stroke", 2.0f, 0.5f, 6.0f).step(0.5f));
    private final NumberSetting aura = this.addSetting("aura", new NumberSetting("Aura", 55.0f, 0.0f, 100.0f).step(5.0f));
    private final NumberSetting auraReach = this.addSetting("aura-reach", new NumberSetting("Aura Reach", 10.0f, 3.0f, 24.0f).step(1.0f));
    private final NumberSetting fill = this.addSetting("fill", new NumberSetting("Fill", 10.0f, 0.0f, 60.0f).step(5.0f));
    private final NumberSetting sheen = this.addSetting("sheen", new NumberSetting("Sheen", 30.0f, 0.0f, 100.0f).step(5.0f));
    private final ColorSetting sheenColor = this.addSetting("sheen-color", new ColorSetting("Sheen Color", -2299649));
    private final BooleanSetting breathe = this.addSetting("breathe", new BooleanSetting("Breathe", false));
    private final NumberSetting distance = this.addSetting("distance", new NumberSetting("Distance", 64.0f, 8.0f, 192.0f).step(4.0f));
    private final NumberSetting limit = this.addSetting("limit", new NumberSetting("Limit", 48.0f, 4.0f, 96.0f).step(1.0f));
    private final class_1297[] picked = new class_1297[96];
    private final int[] tints = new int[96];
    private final double[] spans = new double[96];
    private int count;
    private long pickedFrame = Long.MIN_VALUE;

    public ShaderEspModule() {
        super("glow-esp", "Glow ESP", "Outlines entities through walls with a glow.", ModuleCategory.RENDER);
        this.playerColor.visibleWhen(() -> this.players.get() && this.manual());
        this.friendColor.visibleWhen(() -> this.players.get() && this.manual());
        this.passiveColor.visibleWhen(() -> this.passives.get() && this.manual());
        this.itemColor.visibleWhen(() -> this.items.get() && this.manual());
        this.crystalColor.visibleWhen(() -> this.crystals.get() && this.manual());
        this.targetColor.visibleWhen(() -> this.markTarget.get() && this.manual());
        this.auraReach.visibleWhen(() -> this.aura.get() > 0.0f);
        this.sheenColor.visibleWhen(() -> this.sheen.get() > 0.0f && this.manual());
    }

    @Override
    protected void onEnable() {
        active = this;
        this.on(Render3DEvent.TYPE, this::onRender3D);
    }

    @Override
    protected void onDisable() {
        active = null;
        this.forget();
        if (RenderSystem.isOnRenderThread()) {
            ShaderEspPass.release();
        } else {
            class_310.method_1551().execute(ShaderEspModule::releaseIfOff);
        }
    }

    private static void releaseIfOff() {
        if (active == null) {
            ShaderEspPass.release();
        }
    }

    public static void afterLevel(class_4184 camera, Matrix4f viewRotation, Matrix4f projection) {
        ShaderEspModule module = active;
        if (module != null) {
            module.present(camera, viewRotation, projection);
        }
    }

    public static void extracted(class_1297 entity, class_10017 state) {
        if (active != null) {
            ShaderEspPass.keep(entity, state, Render3DEvent.frame());
        }
    }

    private void onRender3D(Render3DEvent event) {
        this.pick(event.frustum);
        this.pickedFrame = Render3DEvent.frame();
        if (this.count > 0 && ShaderOverlay.shaderPackInUse()) {
            this.sketch(event.partialTick);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void present(class_4184 camera, Matrix4f viewRotation, Matrix4f projection) {
        int drawn = this.count;
        try {
            if (drawn == 0 || this.pickedFrame + 1L != Render3DEvent.frame() || ShaderOverlay.shaderPackInUse()) {
                return;
            }
            if (!ShaderEspPass.paint(camera, viewRotation, projection, this.picked, this.tints, drawn, this.pickedFrame)) {
                return;
            }
            long now = System.nanoTime();
            float scale = Math.max(1.0f, (float)class_310.method_1551().method_1522().field_1481 / 1080.0f);
            float phase = (float)((double)(now % 5200000000L) / 5.2E9);
            ShaderEspPass.compose(this.stroke.get() * scale, this.auraStrength(now), this.auraReach.get() * scale, this.fill.get() / 100.0f, phase, this.sheenTint(), this.sheen.get() / 100.0f);
        }
        finally {
            this.forget();
            ShaderEspPass.dropKept();
        }
    }

    private boolean manual() {
        return this.colorMode.is(COLOR_MANUAL);
    }

    private int sheenTint() {
        return this.manual() ? this.sheenColor.argb() | 0xFF000000 : class_9848.method_61319((float)0.6f, (int)CosmeticAccent.argb(), (int)-1);
    }

    private float auraStrength(long now) {
        float strength = this.aura.get() / 100.0f;
        if (!this.breathe.get()) {
            return strength;
        }
        double cycle = (double)(now % 2600000000L) / 2.6E9;
        float wave = (float)(0.5 - 0.5 * Math.cos(cycle * Math.PI * 2.0));
        wave = wave * wave * (3.0f - 2.0f * wave);
        return strength * (0.45f + 0.55f * wave);
    }

    private void pick(class_4604 frustum) {
        this.forget();
        class_310 mc = class_310.method_1551();
        class_638 world = mc.field_1687;
        class_746 self = mc.field_1724;
        if (world == null || self == null) {
            return;
        }
        class_4184 camera = mc.field_1773.method_19418();
        class_243 eye = camera.method_71156();
        class_1297 viewer = camera.method_19333() ? null : mc.method_1560();
        double reachSq = (double)this.distance.get() * (double)this.distance.get();
        int cap = Math.max(1, Math.min(96, this.limit.getInt()));
        class_1309 mark = this.markTarget.get() ? CombatTarget.current(3500L) : null;
        boolean theme = !this.manual();
        int accent = theme ? CosmeticAccent.argb() : 0;
        for (class_1297 entity : world.method_18112()) {
            double spanSq;
            int tint;
            if (entity == self || entity == viewer || entity.method_31481() || (tint = this.tintOf(entity, mark, theme, accent)) == 0 || (spanSq = entity.method_5707(eye)) > reachSq || frustum != null && !frustum.method_23093(entity.method_5829().method_1014(0.75))) continue;
            this.admit(entity, tint, spanSq, cap);
        }
    }

    private int tintOf(class_1297 entity, class_1309 mark, boolean theme, int accent) {
        ColorSetting group;
        class_1309 living;
        if (entity instanceof class_1309 && (living = (class_1309)entity).method_29504()) {
            return 0;
        }
        boolean friend = false;
        if (entity instanceof class_1657) {
            class_1657 player = (class_1657)entity;
            if (!this.players.get() || player.method_7325()) {
                return 0;
            }
            friend = FriendStore.getInstance().isFriend(player.method_7334().name());
            group = this.playerColor;
        } else if (entity instanceof class_1511) {
            if (!this.crystals.get()) {
                return 0;
            }
            group = this.crystalColor;
        } else if (entity instanceof class_1542) {
            if (!this.items.get()) {
                return 0;
            }
            group = this.itemColor;
        } else {
            if (entity instanceof class_1569) {
                return 0;
            }
            if (entity instanceof class_1308) {
                if (!this.passives.get()) {
                    return 0;
                }
                group = this.passiveColor;
            } else {
                return 0;
            }
        }
        if (mark != null && entity == mark) {
            return theme ? class_9848.method_61319((float)0.8f, (int)accent, (int)-1) : this.targetColor.argb() | 0xFF000000;
        }
        if (friend) {
            return theme ? class_9848.method_61319((float)0.45f, (int)accent, (int)-1) : this.friendColor.argb() | 0xFF000000;
        }
        return theme ? accent : group.argb() | 0xFF000000;
    }

    private void admit(class_1297 entity, int tint, double spanSq, int cap) {
        int slot = this.count;
        if (this.count < cap) {
            ++this.count;
        } else {
            slot = 0;
            for (int i = 1; i < this.count; ++i) {
                if (!(this.spans[i] > this.spans[slot])) continue;
                slot = i;
            }
            if (this.spans[slot] <= spanSq) {
                return;
            }
        }
        this.picked[slot] = entity;
        this.tints[slot] = tint;
        this.spans[slot] = spanSq;
    }

    private void sketch(float partialTick) {
        float width = Math.max(1.0f, this.stroke.get());
        for (int i = 0; i < this.count; ++i) {
            class_1297 entity = this.picked[i];
            class_243 current = entity.method_73189();
            class_243 shown = entity.method_30950(partialTick);
            class_238 box = entity.method_5829().method_989(shown.field_1352 - current.field_1352, shown.field_1351 - current.field_1351, shown.field_1350 - current.field_1350);
            class_12180.method_75541((class_238)box, (class_12179)class_12179.method_75536((int)this.tints[i], (float)width)).method_75533();
        }
    }

    private void forget() {
        Arrays.fill(this.picked, 0, Math.min(this.count, 96), null);
        this.count = 0;
    }
}

