/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12180
 *  net.minecraft.class_1299
 *  net.minecraft.class_1923
 *  net.minecraft.class_1952
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_2382
 *  net.minecraft.class_243
 *  net.minecraft.class_2586
 *  net.minecraft.class_2636
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 */
package net.anubis.client.module.impl.render;

import com.example.client.mixin.AccessorSpawnerNametagsBaseSpawner;
import imgui.ImDrawList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.LongPredicate;
import net.anubis.client.esp.ChunkScanner;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.gui.GuiPalette;
import net.anubis.client.gui.WorldLabel;
import net.anubis.client.gui.WorldText;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.HudOverlayProvider;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12180;
import net.minecraft.class_1299;
import net.minecraft.class_1923;
import net.minecraft.class_1952;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_2636;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_638;

@Environment(value=EnvType.CLIENT)
public final class SpawnerNametagsModule
extends ClientModule
implements HudOverlayProvider,
ChunkScanner.Consumer {
    private static final int MAX_LABELS = 48;
    private static final int RING_SEGMENTS = 64;
    private static final float RING_WIDTH = 1.5f;
    private static final float RING_ALPHA_INSIDE = 0.8f;
    private static final float RING_ALPHA_OUTSIDE = 0.4f;
    private static final double LABEL_HEIGHT = 1.2;
    private static final double FADE_SHARE = 0.125;
    private static final String SEPARATOR = "  \u00b7  ";
    private final NumberSetting sRange = this.addSetting("range", new NumberSetting("Range", 64.0f, 8.0f, 256.0f).step(1.0f));
    private final BooleanSetting sRadius = this.addSetting("activation-radius", new BooleanSetting("Activation Radius", true));
    private final LongOpenHashSet spawners = new LongOpenHashSet();

    public SpawnerNametagsModule() {
        super("spawner-nametags", "Spawner Nametags", "Shows each spawner's mob and activation range over the block.", ModuleCategory.RENDER);
    }

    @Override
    protected void onEnable() {
        ChunkScanner.register(this);
    }

    @Override
    protected void onDisable() {
        ChunkScanner.unregister(this);
        this.spawners.clear();
    }

    public static String mobName(class_2636 spawner) {
        class_1952 data = ((AccessorSpawnerNametagsBaseSpawner)spawner.method_11390()).anubis$spawnerNametagsNextSpawnData();
        if (data == null) {
            return null;
        }
        String id = data.comp_64().method_68564("id", "");
        if (id.isEmpty()) {
            return null;
        }
        return class_1299.method_5898((String)id).map(type -> type.method_5897().getString()).orElse(null);
    }

    public static int activationRange(class_2636 spawner) {
        return ((AccessorSpawnerNametagsBaseSpawner)spawner.method_11390()).anubis$spawnerNametagsRequiredPlayerRange();
    }

    @Override
    public boolean wants(class_2680 state) {
        return state.method_27852(class_2246.field_10260);
    }

    @Override
    public void found(long pos, class_2680 state) {
        this.spawners.add(pos);
    }

    @Override
    public void removed(long pos) {
        this.spawners.remove(pos);
    }

    @Override
    public void reset() {
        this.spawners.clear();
    }

    @Override
    public void chunkScanned(long chunkKey, LongPredicate stillThere) {
        LongIterator it = this.spawners.iterator();
        while (it.hasNext()) {
            long pos = it.nextLong();
            if (class_1923.method_8331((int)(class_2338.method_10061((long)pos) >> 4), (int)(class_2338.method_10083((long)pos) >> 4)) != chunkKey || stillThere.test(pos)) continue;
            it.remove();
        }
    }

    private List<Shown> inRange(class_310 mc) {
        ArrayList<Shown> shown = new ArrayList<Shown>();
        if (this.spawners.isEmpty() || mc.field_1687 == null || mc.field_1724 == null) {
            return shown;
        }
        class_638 level = mc.field_1687;
        class_243 player = mc.field_1724.method_73189();
        double range = this.sRange.get();
        LongIterator it = this.spawners.iterator();
        while (it.hasNext()) {
            class_2586 class_25862;
            class_2338 pos = class_2338.method_10092((long)it.nextLong());
            double distanceSq = player.method_1028((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.5, (double)pos.method_10260() + 0.5);
            if (distanceSq > range * range || !((class_25862 = level.method_8321(pos)) instanceof class_2636)) continue;
            class_2636 spawner = (class_2636)class_25862;
            int activation = SpawnerNametagsModule.activationRange(spawner);
            boolean inside = activation < 0 || distanceSq < (double)activation * (double)activation;
            shown.add(new Shown(pos, Math.sqrt(distanceSq), SpawnerNametagsModule.mobName(spawner), activation, inside));
        }
        shown.sort(Comparator.comparingDouble(Shown::distance));
        return shown.size() > 48 ? shown.subList(0, 48) : shown;
    }

    private float fade(double distance) {
        double range = this.sRange.get();
        return (float)Math.max(0.0, Math.min(1.0, (range - distance) / (range * 0.125)));
    }

    @Override
    public boolean isHudOverlayEnabled() {
        return this.isEnabled() && !this.spawners.isEmpty();
    }

    @Override
    public void renderHudOverlay(ImDrawList dl, float screenW, float screenH) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1690.field_1842) {
            return;
        }
        List<Shown> shown = this.inRange(mc);
        int accent = WorldText.hudAccent();
        for (int i = shown.size() - 1; i >= 0; --i) {
            int[] colors;
            String[] runs;
            Shown spawner = shown.get(i);
            float[] screen = SpawnerNametagsModule.labelAnchor(mc, spawner.pos(), screenW, screenH);
            if (screen == null) continue;
            String name = spawner.mob() == null ? "Spawner" : spawner.mob() + " Spawner";
            String distance = Math.round(spawner.distance()) + "m";
            if (this.sRadius.get() && spawner.range() > 0) {
                runs = new String[]{distance, SEPARATOR, spawner.range() + "m range"};
                colors = new int[]{WorldText.LABEL, WorldText.LABEL, spawner.inside() ? accent : WorldText.LABEL};
            } else {
                runs = new String[]{distance};
                colors = new int[]{WorldText.LABEL};
            }
            WorldText.draw(dl, screen[0], screen[1], name, runs, colors, this.fade(spawner.distance()));
        }
    }

    public static float[] labelAnchor(class_310 mc, class_2338 pos, float screenW, float screenH) {
        float[] anchor = WorldLabel.project(mc, new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 1.2, (double)pos.method_10260() + 0.5), screenW, screenH);
        if (anchor == null) {
            return null;
        }
        for (int corner = 0; corner < 4; ++corner) {
            float[] top = WorldLabel.project(mc, new class_243((double)(pos.method_10263() + (corner & 1)), (double)pos.method_10264() + 1.0, (double)(pos.method_10260() + (corner >> 1))), screenW, screenH);
            if (top == null) continue;
            anchor[1] = Math.min(anchor[1], top[1]);
        }
        return anchor;
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if (!this.sRadius.get()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        List<Shown> shown = this.inRange(mc);
        if (shown.isEmpty()) {
            return;
        }
        int accent = GuiPalette.abgr(WorldText.hudAccent()) & 0xFFFFFF;
        for (Shown spawner : shown) {
            float alpha;
            int color;
            if (spawner.range() <= 0 || (color = Math.round((alpha = (spawner.inside() ? 0.8f : 0.4f) * this.fade(spawner.distance())) * 255.0f) << 24 | accent) >>> 24 < 4) continue;
            SpawnerNametagsModule.drawRing(class_243.method_24953((class_2382)spawner.pos()), spawner.range(), color);
        }
    }

    private static void drawRing(class_243 centre, double radius, int color) {
        class_243 previous = null;
        for (int i = 0; i <= 64; ++i) {
            double angle = Math.PI * 2 * (double)i / 64.0;
            class_243 point = centre.method_1031(Math.cos(angle) * radius, 0.0, Math.sin(angle) * radius);
            if (previous != null) {
                class_12180.method_75546((class_243)previous, (class_243)point, (int)color, (float)1.5f).method_75533();
            }
            previous = point;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Shown(class_2338 pos, double distance, String mob, int range, boolean inside) {
    }
}

