/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1665
 *  net.minecraft.class_1684
 *  net.minecraft.class_1685
 *  net.minecraft.class_238
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_5819
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.render;

import java.util.function.Consumer;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.PacketReceiveEvent;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.render.CosmeticAccent;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.combat.TotemPops;
import net.anubis.client.util.glyph.GlyphCanvas;
import net.anubis.client.util.glyph.GlyphSwarm;
import net.anubis.client.util.glyph.GlyphTint;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1665;
import net.minecraft.class_1684;
import net.minecraft.class_1685;
import net.minecraft.class_238;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_5819;
import net.minecraft.class_638;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class RelicParticlesModule
extends ClientModule {
    private static final String GLYPH_ANKH = "Ankh";
    private static final String GLYPH_SCARAB = "Scarab";
    private static final String GLYPH_SAND = "Sand";
    private static final String GLYPH_SPARK = "Spark";
    private static final String GLYPH_MIXED = "Mixed";
    private static final int CAPACITY = 12000 / GlyphSwarm.WORST_QUADS / 10 * 10;
    private static final float BASE_SIZE = 0.085f;
    private static final double PROJECTILE_RANGE_SQR = 6400.0;
    private static final double RESTING_SQR = 9.0E-4;
    private static final double STEP_MIN_SPEED = 0.07;
    private static final int PEARL = 0;
    private static final int ARROW = 1;
    private static final int TRIDENT = 2;
    private static final int NONE = -1;
    private static final byte[] MIXED_ORDER = new byte[]{0, 3, 1, 3, 2, 3};
    private final BooleanSetting pops = this.addSetting("pops", new BooleanSetting("Totem Pops", true));
    private final ModeSetting popGlyph = this.addSetting("pop-glyph", new ModeSetting("Pop Glyph", "Mixed", "Mixed", "Ankh", "Scarab", "Sand", "Spark"));
    private final NumberSetting popBurst = this.addSetting("pop-burst", new NumberSetting("Pop Burst", 26.0f, 4.0f, 80.0f).step(1.0f));
    private final BooleanSetting ownPops = this.addSetting("own-pops", new BooleanSetting("Own Pops", true));
    private final BooleanSetting projectiles = this.addSetting("projectiles", new BooleanSetting("Projectiles", true));
    private final BooleanSetting pearls = this.addSetting("pearls", new BooleanSetting("Pearls", true));
    private final BooleanSetting arrows = this.addSetting("arrows", new BooleanSetting("Arrows", true));
    private final BooleanSetting tridents = this.addSetting("tridents", new BooleanSetting("Tridents", true));
    private final ModeSetting trailGlyph = this.addSetting("trail-glyph", new ModeSetting("Trail Glyph", "Spark", "Spark", "Sand", "Ankh", "Scarab", "Mixed"));
    private final NumberSetting trailDensity = this.addSetting("trail-density", new NumberSetting("Trail Density", 2.0f, 1.0f, 6.0f).step(1.0f));
    private final BooleanSetting footsteps = this.addSetting("footsteps", new BooleanSetting("Footsteps", false));
    private final ModeSetting stepGlyph = this.addSetting("step-glyph", new ModeSetting("Step Glyph", "Sand", "Sand", "Spark", "Ankh", "Scarab", "Mixed"));
    private final NumberSetting stepDensity = this.addSetting("step-density", new NumberSetting("Step Density", 2.0f, 1.0f, 6.0f).step(1.0f));
    private final NumberSetting size = this.addSetting("size", new NumberSetting("Size", 1.0f, 0.3f, 2.5f).step(0.05f));
    private final NumberSetting lifetime = this.addSetting("lifetime", new NumberSetting("Lifetime", 1.4f, 0.3f, 4.0f).step(0.1f));
    private final NumberSetting limit = this.addSetting("limit", new NumberSetting("Limit", 240.0f, 40.0f, CAPACITY).step(10.0f));
    private final BooleanSetting halo = this.addSetting("halo", new BooleanSetting("Halo", true));
    private final ModeSetting colorMode = this.addSetting("color-mode", new ModeSetting("Color", "Theme", "Theme", "Manual"));
    private final ColorSetting mainColor = this.addSetting("main-color", new ColorSetting("Main Color", -12548362));
    private final ColorSetting secondColor = this.addSetting("second-color", new ColorSetting("Second Color", -14992226));
    private final GlyphSwarm swarm = new GlyphSwarm(CAPACITY);
    private final GlyphCanvas canvas = new GlyphCanvas();
    private final GlyphTint palette = new GlyphTint();
    private final class_5819 random = class_5819.method_43047();
    private final Consumer<class_1309> popSink = this::burst;
    private class_638 boundLevel;
    private long lastFrame;

    public RelicParticlesModule() {
        super("particles", "Particles", "Egyptian particles for totem pops, projectiles and footsteps.", ModuleCategory.VISUALS);
        this.popGlyph.visibleWhen(this.pops::get);
        this.popBurst.visibleWhen(this.pops::get);
        this.ownPops.visibleWhen(this.pops::get);
        this.pearls.visibleWhen(this.projectiles::get);
        this.arrows.visibleWhen(this.projectiles::get);
        this.tridents.visibleWhen(this.projectiles::get);
        this.trailGlyph.visibleWhen(this.projectiles::get);
        this.trailDensity.visibleWhen(this.projectiles::get);
        this.stepGlyph.visibleWhen(this.footsteps::get);
        this.stepDensity.visibleWhen(this.footsteps::get);
        this.mainColor.visibleWhen(() -> this.colorMode.is("Manual"));
        this.secondColor.visibleWhen(() -> this.colorMode.is("Manual"));
    }

    @Override
    protected void onEnable() {
        this.reset();
        this.on(PacketReceiveEvent.TYPE, this::onPacket);
        this.on(TickEvent.TYPE, Stage.POST, this::onTick);
        this.on(Render3DEvent.TYPE, this::onRender);
    }

    @Override
    protected void onDisable() {
        this.reset();
    }

    private void reset() {
        this.swarm.clear();
        this.boundLevel = null;
        this.lastFrame = 0L;
    }

    private boolean bind(class_638 level) {
        if (level == null) {
            if (this.boundLevel != null) {
                this.reset();
            }
            return false;
        }
        if (level != this.boundLevel) {
            this.swarm.clear();
            this.boundLevel = level;
        }
        return true;
    }

    private void onPacket(PacketReceiveEvent event) {
        if (this.pops.get()) {
            TotemPops.scan(event.packet, this.popSink);
        }
    }

    private void onTick(TickEvent event) {
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (!this.bind(mc.field_1687) || player == null) {
            return;
        }
        this.refreshPalette();
        if (this.projectiles.get()) {
            this.trails(mc.field_1687, player);
        }
        if (this.footsteps.get()) {
            this.steps(player);
        }
    }

    private void onRender(Render3DEvent event) {
        class_310 mc = class_310.method_1551();
        if (!this.bind(mc.field_1687)) {
            return;
        }
        long now = System.nanoTime();
        float dt = this.lastFrame == 0L ? 0.0f : Math.min(0.1f, (float)(now - this.lastFrame) / 1.0E9f);
        this.lastFrame = now;
        if (mc.method_1493()) {
            dt = 0.0f;
        }
        this.swarm.trim(this.particleLimit());
        this.swarm.step(dt);
        if (this.swarm.count() == 0) {
            return;
        }
        this.canvas.begin(mc.field_1773.method_19418());
        this.swarm.draw(this.canvas, 1.0f, this.halo.get());
        this.canvas.submit(false);
    }

    private void burst(class_1309 entity) {
        class_310 mc = class_310.method_1551();
        if (!this.bind(mc.field_1687) || entity.method_73183() != mc.field_1687) {
            return;
        }
        if (entity == mc.field_1724 && !this.ownPops.get()) {
            return;
        }
        this.refreshPalette();
        class_238 box = entity.method_5829();
        int total = this.popBurst.getInt();
        String mode = this.popGlyph.get();
        for (int i = 0; i < total; ++i) {
            byte kind = RelicParticlesModule.pick(mode, i);
            double px = box.field_1323 + box.method_17939() * this.random.method_43058();
            double py = box.field_1322 + box.method_17940() * (0.15 + 0.8 * this.random.method_43058());
            double pz = box.field_1321 + box.method_17941() * this.random.method_43058();
            float heading = this.random.method_43057() * ((float)Math.PI * 2);
            float out = RelicParticlesModule.outward(kind) * (0.45f + this.random.method_43057());
            float up = RelicParticlesModule.upward(kind) * (0.55f + 0.8f * this.random.method_43057());
            this.emit(kind, px, py, pz, class_3532.method_15362((double)heading) * out, up, class_3532.method_15374((double)heading) * out, 1.0f, 1.0f, this.colorFor(kind, -1));
        }
    }

    private void trails(class_638 level, class_746 player) {
        int density = this.trailDensity.getInt();
        String mode = this.trailGlyph.get();
        for (class_1297 entity : level.method_18112()) {
            double dz;
            double dy;
            double dx;
            int role = this.role(entity);
            if (role == -1 || (dx = entity.method_23317() - entity.field_6014) * dx + (dy = entity.method_23318() - entity.field_6036) * dy + (dz = entity.method_23321() - entity.field_5969) * dz < 9.0E-4 || entity.method_5858((class_1297)player) > 6400.0) continue;
            double mid = (double)entity.method_17682() * 0.5;
            for (int i = 0; i < density; ++i) {
                float f = ((float)i + this.random.method_43057()) / (float)density;
                byte kind = RelicParticlesModule.pick(mode, entity.field_6012 * density + i);
                this.emit(kind, entity.field_6014 + dx * (double)f + (double)this.jitter(0.06f), entity.field_6036 + dy * (double)f + mid + (double)this.jitter(0.06f), entity.field_5969 + dz * (double)f + (double)this.jitter(0.06f), this.jitter(0.3f), this.jitter(0.3f) + 0.15f, this.jitter(0.3f), 0.55f, 0.8f, this.colorFor(kind, role));
            }
        }
    }

    private void steps(class_746 player) {
        double dz;
        if (!player.method_24828() || player.method_7325() || player.method_5765() || player.method_52535()) {
            return;
        }
        double dx = player.method_23317() - player.field_6014;
        double speed = Math.sqrt(dx * dx + (dz = player.method_23321() - player.field_5969) * dz);
        if (speed < 0.07) {
            return;
        }
        int count = this.stepDensity.getInt() + (player.method_5624() ? 1 : 0);
        String mode = this.stepGlyph.get();
        for (int i = 0; i < count; ++i) {
            byte kind = RelicParticlesModule.pick(mode, player.field_6012 * count + i);
            double back = 0.2 + 0.3 * this.random.method_43058();
            this.emit(kind, player.method_23317() - dx / speed * back + (double)this.jitter(0.22f), player.method_23318() + 0.04, player.method_23321() - dz / speed * back + (double)this.jitter(0.22f), (float)(-dx * 7.0) + this.jitter(0.5f), 0.8f + this.random.method_43057() * 1.1f, (float)(-dz * 7.0) + this.jitter(0.5f), 0.7f, 0.75f, this.colorFor(kind, -1));
        }
    }

    private void emit(byte kind, double x, double y, double z, float vx, float vy, float vz, float lifeScale, float sizeScale, int rgb) {
        float life = this.lifetime.get() * lifeScale * (0.75f + 0.5f * this.random.method_43057());
        float scale = 0.085f * this.size.get() * sizeScale * RelicParticlesModule.sizeOf(kind) * (0.8f + 0.4f * this.random.method_43057());
        float turn = RelicParticlesModule.spinOf(kind) * (this.random.method_43056() ? 1.0f : -1.0f) * (0.5f + this.random.method_43057());
        float angle = kind == 0 ? this.jitter(0.35f) : this.random.method_43057() * ((float)Math.PI * 2);
        this.swarm.spawn(kind, x, y, z, vx, vy, vz, life, scale, angle, turn, this.random.method_43057(), rgb, this.particleLimit());
    }

    private int particleLimit() {
        return this.limit.getInt();
    }

    private void refreshPalette() {
        this.palette.resolve(this.colorMode.get(), CosmeticAccent.argb(), this.mainColor.argb(), this.secondColor.argb());
    }

    private int role(class_1297 entity) {
        if (entity.method_31481()) {
            return -1;
        }
        if (entity instanceof class_1684) {
            return this.pearls.get() ? 0 : -1;
        }
        if (entity instanceof class_1685) {
            return this.tridents.get() ? 2 : -1;
        }
        if (entity instanceof class_1665) {
            return this.arrows.get() ? 1 : -1;
        }
        return -1;
    }

    private static byte pick(String mode, int index) {
        return switch (mode) {
            case GLYPH_ANKH -> 0;
            case GLYPH_SCARAB -> 1;
            case GLYPH_SAND -> 2;
            case GLYPH_SPARK -> 3;
            default -> MIXED_ORDER[Math.floorMod(index, MIXED_ORDER.length)];
        };
    }

    private int colorFor(byte kind, int role) {
        float r = this.random.method_43057();
        int base = switch (kind) {
            case 0 -> {
                if (r < 0.6f) {
                    yield this.palette.primary();
                }
                yield this.palette.highlight();
            }
            case 1 -> {
                if (r < 0.55f) {
                    yield this.palette.secondary();
                }
                yield this.palette.companion();
            }
            case 2 -> this.dust(r);
            default -> GlyphTint.mix(this.palette.highlight(), -1, r * 0.35f);
        };
        return switch (role) {
            case 0 -> GlyphTint.mix(base, this.palette.companion(), 0.55f);
            case 2 -> GlyphTint.mix(base, this.palette.secondary(), 0.45f);
            default -> base;
        };
    }

    private int dust(float r) {
        int dust = this.palette.dust();
        if (r < 0.4f) {
            return dust;
        }
        return r < 0.75f ? GlyphTint.lift(dust, 0.4f) : GlyphTint.mix(dust, this.palette.secondary(), 0.3f);
    }

    private static float sizeOf(byte kind) {
        return switch (kind) {
            case 0 -> 1.35f;
            case 1 -> 1.15f;
            case 2 -> 0.5f;
            default -> 0.9f;
        };
    }

    private static float spinOf(byte kind) {
        return switch (kind) {
            case 0 -> 0.5f;
            case 1 -> 1.1f;
            case 2 -> 5.0f;
            default -> 2.8f;
        };
    }

    private static float outward(byte kind) {
        return switch (kind) {
            case 0 -> 0.8f;
            case 1 -> 2.1f;
            case 2 -> 1.5f;
            default -> 3.4f;
        };
    }

    private static float upward(byte kind) {
        return switch (kind) {
            case 0 -> 1.1f;
            case 1 -> 1.7f;
            case 2 -> 2.6f;
            default -> 2.4f;
        };
    }

    private float jitter(float amount) {
        return (this.random.method_43057() * 2.0f - 1.0f) * amount;
    }
}

