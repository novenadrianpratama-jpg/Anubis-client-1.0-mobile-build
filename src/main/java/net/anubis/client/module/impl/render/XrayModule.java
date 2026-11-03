/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_1920
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350
 *  net.minecraft.class_2382
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 */
package net.anubis.client.module.impl.render;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.List;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.BlockColorListSetting;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1920;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2382;
import net.minecraft.class_2680;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class XrayModule
extends ClientModule {
    private static final boolean SODIUM = FabricLoader.getInstance().isModLoaded("sodium");
    private static final List<String> ORES = List.of("minecraft:coal_ore", "minecraft:deepslate_coal_ore", "minecraft:iron_ore", "minecraft:deepslate_iron_ore", "minecraft:gold_ore", "minecraft:deepslate_gold_ore", "minecraft:lapis_ore", "minecraft:deepslate_lapis_ore", "minecraft:redstone_ore", "minecraft:deepslate_redstone_ore", "minecraft:diamond_ore", "minecraft:deepslate_diamond_ore", "minecraft:emerald_ore", "minecraft:deepslate_emerald_ore", "minecraft:copper_ore", "minecraft:deepslate_copper_ore", "minecraft:nether_gold_ore", "minecraft:nether_quartz_ore", "minecraft:ancient_debris");
    private final BlockColorListSetting blocks = this.addSetting("blocks", BlockColorListSetting.blockList("Blocks", ORES));
    private final NumberSetting opacity = this.addSetting("opacity", new NumberSetting("Opacity", 10.0f, 0.0f, 100.0f).step(1.0f));
    private final BooleanSetting exposedOnly = this.addSetting("exposed-only", new BooleanSetting("Exposed Only", false));
    public static volatile Snapshot active;
    public static final ThreadLocal<float[]> CURRENT_ALPHA;
    private static final int RELOAD_DELAY_TICKS = 6;
    private int reloadDelay = -1;

    public XrayModule() {
        super("xray", "Xray", "Hide or fade everything but a block whitelist to see ores through terrain.", ModuleCategory.RENDER);
        this.usesNotifications();
        this.blocks.onChange(this::scheduleReload);
        this.opacity.onChange(this::scheduleReload);
        this.exposedOnly.onChange(this::scheduleReload);
    }

    @Override
    protected void onEnable() {
        if (SODIUM) {
            ModuleChat.send((ClientModule)this, ModuleChat.body().text("Sodium detected; terrain Xray ").danger("isn't supported").text(" with it yet"));
            this.requestDisable();
            return;
        }
        this.rebuildSnapshot();
        this.reloadWorld();
    }

    @Override
    protected void onDisable() {
        active = null;
        this.reloadDelay = -1;
        this.reloadWorld();
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (this.reloadDelay < 0 || --this.reloadDelay > 0) {
            return;
        }
        this.reloadDelay = -1;
        this.rebuildSnapshot();
        this.reloadWorld();
    }

    private void scheduleReload() {
        if (!this.isEnabled()) {
            return;
        }
        this.reloadDelay = 6;
    }

    private void reloadWorld() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1769 != null) {
            mc.field_1769.method_3279();
        }
    }

    private void rebuildSnapshot() {
        active = new Snapshot((ReferenceOpenHashSet<class_2248>)new ReferenceOpenHashSet(this.blocks.enabledBlocks()), this.exposedOnly.get(), this.opacity.get() / 100.0f);
    }

    static {
        CURRENT_ALPHA = ThreadLocal.withInitial(() -> new float[]{1.0f});
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Snapshot {
        private static final class_2350[] DIRECTIONS = class_2350.values();
        private static final ThreadLocal<class_2338.class_2339> SCRATCH_POS = ThreadLocal.withInitial(class_2338.class_2339::new);
        private final ReferenceOpenHashSet<class_2248> whitelist;
        private final boolean exposedOnly;
        private final float opacity;

        private Snapshot(ReferenceOpenHashSet<class_2248> whitelist, boolean exposedOnly, float opacity) {
            this.whitelist = whitelist;
            this.exposedOnly = exposedOnly;
            this.opacity = opacity;
        }

        public boolean isWhitelisted(class_2248 block) {
            return this.whitelist.contains((Object)block);
        }

        public float alphaFor(class_1920 region, class_2680 state, class_2338 pos) {
            if (this.whitelist.contains((Object)state.method_26204()) && (!this.exposedOnly || this.isExposed(region, pos))) {
                return 1.0f;
            }
            return this.opacity;
        }

        private boolean isExposed(class_1920 region, class_2338 pos) {
            class_2338.class_2339 scratch = SCRATCH_POS.get();
            for (class_2350 direction : DIRECTIONS) {
                scratch.method_25505((class_2382)pos, direction);
                if (region.method_8320((class_2338)scratch).method_26216()) continue;
                return true;
            }
            return false;
        }
    }
}

