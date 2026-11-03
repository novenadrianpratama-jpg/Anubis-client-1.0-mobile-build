/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_9848
 */
package net.anubis.client.module.impl.render;

import java.util.Arrays;
import net.anubis.client.module.impl.render.CosmeticAccent;
import net.anubis.client.module.impl.render.HeadCosmeticModule;
import net.anubis.client.module.impl.render.PharaohMesh;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_9848;

@Environment(value=EnvType.CLIENT)
public final class PharaohHeaddressModule
extends HeadCosmeticModule {
    private static final String STYLE_NEMES = "Nemes";
    private static final String STYLE_KHEPRESH = "Khepresh";
    private static final String STYLE_SUN_DISC = "Sun Disc";
    private final ModeSetting style = this.addSetting("style", new ModeSetting("Style", "Nemes", "Nemes", "Khepresh", "Sun Disc"));
    private final NumberSetting stripes = this.addSetting("stripes", new NumberSetting("Stripes", 5.0f, 3.0f, 9.0f).step(1.0f));
    private final BooleanSetting collar = this.addSetting("collar", new BooleanSetting("Broad Collar", true));
    private final NumberSetting size = this.addSetting("size", new NumberSetting("Size", 1.0f, 0.8f, 1.3f).step(0.05f));
    private final ModeSetting color = this.addSetting("color", new ModeSetting("Color", "Theme", "Theme", "Manual"));
    private final ColorSetting gold = this.addSetting("manual-gold", new ColorSetting("Gold Color", -7688204));
    private final ColorSetting inlay = this.addSetting("manual-inlay", new ColorSetting("Inlay Color", -13738055));
    private final ColorSetting gem = this.addSetting("manual-gem", new ColorSetting("Gem Color", -10313473));
    private final BooleanSetting gemGlow = this.addSetting("gem-glow", new BooleanSetting("Gem Glow", true));
    private final BooleanSetting glint = this.addSetting("glint", new BooleanSetting("Glint", true));
    private final BooleanSetting friends = this.addSetting("friends", new BooleanSetting("On Friends", false));
    private final PharaohMesh[] meshes = new PharaohMesh[4];
    private long styleRevision = -1L;
    private long stripesRevision = -1L;
    private long collarRevision = -1L;

    public PharaohHeaddressModule() {
        super("pharaoh-headdress", "Pharaoh Headdress", "Puts a pharaoh headdress on your player model.");
        this.stripes.visibleWhen(() -> this.style.is(STYLE_NEMES));
        this.gold.visibleWhen(() -> this.color.is("Manual"));
        this.inlay.visibleWhen(() -> this.color.is("Manual"));
        this.gem.visibleWhen(() -> this.color.is("Manual"));
    }

    @Override
    void reset() {
        super.reset();
        Arrays.fill(this.meshes, null);
        this.styleRevision = -1L;
    }

    @Override
    PharaohMesh headMesh(boolean helmet) {
        this.refreshMeshes();
        int slot = helmet ? 1 : 0;
        PharaohMesh mesh = this.meshes[slot];
        if (mesh == null) {
            this.meshes[slot] = mesh = PharaohMesh.head(this.styleIndex(), this.stripes.getInt(), helmet);
        }
        return mesh;
    }

    @Override
    PharaohMesh bodyMesh(boolean chest) {
        this.refreshMeshes();
        int slot = chest ? 3 : 2;
        PharaohMesh mesh = this.meshes[slot];
        if (mesh == null) {
            this.meshes[slot] = mesh = PharaohMesh.body(this.styleIndex(), this.stripes.getInt(), chest, this.collar.get());
        }
        return mesh;
    }

    private void refreshMeshes() {
        long s = this.style.revision();
        long n = this.stripes.revision();
        long c = this.collar.revision();
        if (s == this.styleRevision && n == this.stripesRevision && c == this.collarRevision) {
            return;
        }
        this.styleRevision = s;
        this.stripesRevision = n;
        this.collarRevision = c;
        Arrays.fill(this.meshes, null);
    }

    private int styleIndex() {
        if (this.style.is(STYLE_KHEPRESH)) {
            return 1;
        }
        if (this.style.is(STYLE_SUN_DISC)) {
            return 2;
        }
        return 0;
    }

    @Override
    float scale() {
        return this.size.get();
    }

    @Override
    int[] palette() {
        int[] palette = new int[5];
        if (this.color.is("Manual")) {
            int metal;
            palette[0] = metal = this.gold.argb() | 0xFF000000;
            palette[1] = this.inlay.argb() | 0xFF000000;
            palette[2] = this.gem.argb() | 0xFF000000;
            palette[3] = PharaohHeaddressModule.bronze(metal);
        } else {
            int metal;
            int accent = CosmeticAccent.argb();
            palette[0] = metal = PharaohHeaddressModule.metal(accent);
            palette[1] = class_9848.method_61321((int)accent, (float)0.78f);
            palette[2] = PharaohHeaddressModule.gem(accent);
            palette[3] = class_9848.method_61321((int)metal, (float)0.62f);
        }
        return palette;
    }

    @Override
    boolean glows() {
        return this.gemGlow.get();
    }

    @Override
    boolean glints() {
        return this.glint.get();
    }

    @Override
    boolean onFriends() {
        return this.friends.get();
    }

    private static int bronze(int argb) {
        int r = (int)((float)(argb >> 16 & 0xFF) * 0.7f);
        int g = (int)((float)(argb >> 8 & 0xFF) * 0.55f);
        int b = (int)((float)(argb & 0xFF) * 0.4f);
        return 0xFF000000 | r << 16 | g << 8 | b;
    }
}

