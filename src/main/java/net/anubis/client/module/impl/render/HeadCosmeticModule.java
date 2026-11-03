/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.render;

import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import java.lang.ref.WeakReference;
import net.anubis.client.config.FriendStore;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.misc.FreecamModule;
import net.anubis.client.module.impl.render.PharaohMesh;
import net.anubis.client.module.impl.render.character.CharacterModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_638;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
abstract class HeadCosmeticModule
extends ClientModule {
    static volatile HeadCosmeticModule worn;
    static final String COLOR_THEME = "Theme";
    static final String COLOR_MANUAL = "Manual";
    private static final long EPOCH;
    private static final long LOOK_REFRESH_NANOS = 4000000L;
    private static final long FRIEND_REFRESH_NANOS = 1000000000L;
    private static final float GLINT_CYCLE_SECONDS = 3.6f;
    private static final float GLINT_STRENGTH = 0.6f;
    private PharaohMesh.Look look;
    private long lookAt;
    private final Int2BooleanOpenHashMap friendCache = new Int2BooleanOpenHashMap();
    private WeakReference<class_638> friendLevel = new WeakReference<Object>(null);
    private long friendsCheckedAt;

    HeadCosmeticModule(String id, String name, String description) {
        super(id, name, description, ModuleCategory.VISUALS);
    }

    abstract PharaohMesh headMesh(boolean var1);

    abstract PharaohMesh bodyMesh(boolean var1);

    abstract float scale();

    abstract int[] palette();

    abstract boolean glows();

    abstract boolean glints();

    abstract boolean onFriends();

    @Override
    protected void onEnable() {
        CharacterModule character = ModuleRegistry.get(CharacterModule.class);
        if (character != null) {
            character.setEnabled(false);
        }
        this.reset();
        worn = this;
    }

    @Override
    protected void onDisable() {
        if (worn == this) {
            worn = null;
        }
        this.reset();
    }

    void reset() {
        this.look = null;
        this.friendCache.clear();
        this.friendLevel = new WeakReference<Object>(null);
    }

    final boolean wears(class_10055 state) {
        class_310 mc = class_310.method_1551();
        class_746 self = mc.field_1724;
        if (self == null || mc.field_1687 == null) {
            return false;
        }
        if (state.field_53528 == self.method_5628()) {
            return !mc.field_1690.method_31044().method_31034() || mc.field_1755 != null || FreecamModule.active != null;
        }
        return this.onFriends() && this.isFriend(mc, state.field_53528);
    }

    /*
     * Unable to fully structure code
     */
    private boolean isFriend(class_310 mc, int id) {
        now = System.nanoTime();
        if (this.friendLevel.get() != mc.field_1687) {
            this.friendLevel = new WeakReference<class_638>(mc.field_1687);
            this.friendCache.clear();
        }
        if (now - this.friendsCheckedAt > 1000000000L) {
            this.friendCache.clear();
            this.friendsCheckedAt = now;
        }
        if (this.friendCache.containsKey(id)) {
            return this.friendCache.get(id);
        }
        entity = mc.field_1687.method_8469(id);
        if (!(entity instanceof class_1657)) {
            v0 = false;
        } else {
            player = (class_1657)entity;
            v0 = FriendStore.getInstance().isFriend(player.method_7334().name());
        }
        friend = v0;
        this.friendCache.put(id, friend);
        return friend;
    }

    final PharaohMesh.Look look() {
        long now = System.nanoTime();
        PharaohMesh.Look current = this.look;
        if (current != null && now - this.lookAt < 4000000L) {
            return current;
        }
        float seconds = (float)(now - EPOCH) / 1.0E9f;
        float phase = seconds % 3.6f / 3.6f;
        float sweep = (phase * 2.0f - 1.0f) * (float)Math.PI * 1.25f;
        float envelope = class_3532.method_15374((double)(phase * (float)Math.PI));
        float gain = this.glints() ? 0.6f * envelope * envelope : 0.0f;
        float pulse = 0.9f + 0.1f * class_3532.method_15374((double)(seconds * 2.2f));
        this.look = current = new PharaohMesh.Look(this.palette(), this.glows(), class_3532.method_15362((double)sweep), class_3532.method_15374((double)sweep), gain, pulse);
        this.lookAt = now;
        return current;
    }

    static int metal(int accent) {
        return HeadCosmeticModule.shade(accent, 0.55f, 0.96f);
    }

    static int gem(int accent) {
        return HeadCosmeticModule.shade(accent, 1.15f, 1.0f);
    }

    static int shade(int argb, float saturation, float value) {
        int r = argb >> 16 & 0xFF;
        int g = argb >> 8 & 0xFF;
        int b = argb & 0xFF;
        int max = Math.max(r, Math.max(g, b));
        int chroma = max - Math.min(r, Math.min(g, b));
        float hue = 0.0f;
        if (chroma > 0) {
            hue = max == r ? (float)(g - b) / (float)chroma : (max == g ? 2.0f + (float)(b - r) / (float)chroma : 4.0f + (float)(r - g) / (float)chroma);
            if ((hue /= 6.0f) < 0.0f) {
                hue += 1.0f;
            }
        }
        float s = max == 0 ? 0.0f : (float)chroma / (float)max;
        return class_3532.method_60599((float)hue, (float)Math.min(1.0f, s * saturation), (float)Math.min(1.0f, value), (int)255);
    }

    static {
        EPOCH = System.nanoTime();
    }
}

