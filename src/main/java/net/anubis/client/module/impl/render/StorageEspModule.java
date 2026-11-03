/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1923
 *  net.minecraft.class_1937
 *  net.minecraft.class_2248
 *  net.minecraft.class_2281
 *  net.minecraft.class_2331
 *  net.minecraft.class_2336
 *  net.minecraft.class_2338
 *  net.minecraft.class_2363
 *  net.minecraft.class_2377
 *  net.minecraft.class_2480
 *  net.minecraft.class_2487
 *  net.minecraft.class_2496
 *  net.minecraft.class_2586
 *  net.minecraft.class_2591
 *  net.minecraft.class_2596
 *  net.minecraft.class_2622
 *  net.minecraft.class_2626
 *  net.minecraft.class_2637
 *  net.minecraft.class_2672
 *  net.minecraft.class_2678
 *  net.minecraft.class_2680
 *  net.minecraft.class_2818
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3708
 *  net.minecraft.class_5321
 *  net.minecraft.class_638
 *  net.minecraft.class_7923
 */
package net.anubis.client.module.impl.render;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import net.anubis.client.esp.BlockLayer;
import net.anubis.client.esp.ChunkScanner;
import net.anubis.client.esp.EspStyle;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.PacketAppliedEvent;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2281;
import net.minecraft.class_2331;
import net.minecraft.class_2336;
import net.minecraft.class_2338;
import net.minecraft.class_2363;
import net.minecraft.class_2377;
import net.minecraft.class_2480;
import net.minecraft.class_2487;
import net.minecraft.class_2496;
import net.minecraft.class_2586;
import net.minecraft.class_2591;
import net.minecraft.class_2596;
import net.minecraft.class_2622;
import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2672;
import net.minecraft.class_2678;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3708;
import net.minecraft.class_5321;
import net.minecraft.class_638;
import net.minecraft.class_7923;

@Environment(value=EnvType.CLIENT)
public final class StorageEspModule
extends ClientModule
implements ChunkScanner.Consumer {
    private final EspStyle style = new EspStyle((x$0, x$1) -> this.addSetting(x$0, x$1), "Outline", 0.25f, 256.0f);
    private final BooleanSetting sTracers = this.addSetting("tracers", new BooleanSetting("Tracers", false));
    private final Type chest = new Type("Chest", true, -6530304, "chests", "chest-color", "chest-tracers", "chest-tracer-color");
    private final Type enderChest = new Type("Ender Chest", true, -9109249, "ender-chests", "ender-chest-color", "ender-chest-tracers", "ender-chest-tracer-color");
    private final Type shulker = new Type("Shulker Box", true, -7995234, "shulker-boxes", "shulker-box-color", "shulker-tracers", "shulker-tracer-color");
    private final Type spawner = new Type("Spawner", true, -7700826, "spawners", "spawner-color", "spawner-tracers", "spawner-tracer-color");
    private final Type furnace = new Type("Furnace", false, -10197916, "furnaces", "furnace-color", "furnace-tracers", "furnace-tracer-color");
    private final Type barrel = new Type("Barrel", true, -10204366, "barrels", "barrel-color", "barrel-tracers", "barrel-tracer-color");
    private final Type enchanting = new Type("Enchanting Table", true, -3670016, "enchanting-tables", "enchanting-table-color", "enchanting-tracers", "enchanting-tracer-color");
    private final Type hopper = new Type("Hopper", false, -11513776, "hoppers", "hopper-color", "hopper-tracers", "hopper-tracer-color");
    private final Type[] types = new Type[]{this.chest, this.enderChest, this.shulker, this.spawner, this.furnace, this.barrel, this.enchanting, this.hopper};
    private final BlockLayer layer = new BlockLayer(new BlockLayer.Palette(this){

        @Override
        public int color(Object kind) {
            return ((Type)kind).color.argb();
        }

        @Override
        public int tracerColor(Object kind) {
            Type t = (Type)kind;
            return t.tracers.get() ? t.tracerColor.argb() : 0;
        }
    }, () -> class_310.method_1551().field_1687 == null ? null : class_310.method_1551().field_1687.method_27983());
    private final Map<class_5321<class_1937>, Long2ObjectOpenHashMap<Type>> detections = new HashMap<class_5321<class_1937>, Long2ObjectOpenHashMap<Type>>();
    private class_5321<class_1937> shownDimension;

    public StorageEspModule() {
        super("storage-esp", "Storage ESP Bypass", "Highlights storage blocks and spawners with per-type colours.", ModuleCategory.BASE_RAID);
    }

    private Type typeOf(class_2248 block) {
        if (block instanceof class_2281) {
            return this.chest;
        }
        if (block instanceof class_2336) {
            return this.enderChest;
        }
        if (block instanceof class_2480) {
            return this.shulker;
        }
        if (block instanceof class_2496) {
            return this.spawner;
        }
        if (block instanceof class_2363) {
            return this.furnace;
        }
        if (block instanceof class_3708) {
            return this.barrel;
        }
        if (block instanceof class_2331) {
            return this.enchanting;
        }
        if (block instanceof class_2377) {
            return this.hopper;
        }
        return null;
    }

    private Type typeOf(class_2591<?> type) {
        if (type == class_2591.field_11914 || type == class_2591.field_11891) {
            return this.chest;
        }
        if (type == class_2591.field_11901) {
            return this.enderChest;
        }
        if (type == class_2591.field_11896) {
            return this.shulker;
        }
        if (type == class_2591.field_11889) {
            return this.spawner;
        }
        if (type == class_2591.field_11903 || type == class_2591.field_16415 || type == class_2591.field_16414) {
            return this.furnace;
        }
        if (type == class_2591.field_16411) {
            return this.barrel;
        }
        if (type == class_2591.field_11912) {
            return this.enchanting;
        }
        if (type == class_2591.field_11888) {
            return this.hopper;
        }
        return null;
    }

    private Type typeOf(class_2591<?> type, class_2487 tag) {
        Type known = this.typeOf(type);
        if (known != null || tag == null) {
            return known;
        }
        class_2960 id = tag.method_10558("id").map(class_2960::method_12829).orElse(null);
        return id == null ? null : this.typeOf((class_2591)class_7923.field_41181.method_63535(id));
    }

    private void rescan() {
        Long2ObjectOpenHashMap<Type> found;
        if (!this.isEnabled()) {
            return;
        }
        this.layer.clear();
        class_638 level = class_310.method_1551().field_1687;
        this.shownDimension = level == null ? null : level.method_27983();
        Long2ObjectOpenHashMap<Type> long2ObjectOpenHashMap = found = this.shownDimension == null ? null : this.detections.get(this.shownDimension);
        if (found == null) {
            return;
        }
        for (Long2ObjectMap.Entry entry : found.long2ObjectEntrySet()) {
            if (!((Type)entry.getValue()).enabled.get()) continue;
            this.layer.put(entry.getLongKey(), entry.getValue());
        }
    }

    private boolean anyTracer() {
        if (!this.sTracers.get()) {
            return false;
        }
        for (Type type : this.types) {
            if (!type.enabled.get() || !type.tracers.get()) continue;
            return true;
        }
        return false;
    }

    @Override
    protected void onEnable() {
        this.detections.clear();
        this.layer.clear();
        ChunkScanner.register(this);
        class_638 level = class_310.method_1551().field_1687;
        class_5321 class_53212 = this.shownDimension = level == null ? null : level.method_27983();
        if (level == null || class_310.method_1551().field_1724 == null) {
            return;
        }
        class_1923 center = class_310.method_1551().field_1724.method_31476();
        int radius = class_310.method_1551().field_1690.method_38521() + 1;
        for (int x = center.field_9181 - radius; x <= center.field_9181 + radius; ++x) {
            for (int z = center.field_9180 - radius; z <= center.field_9180 + radius; ++z) {
                class_2818 chunk = level.method_2935().method_21730(x, z);
                if (chunk == null) continue;
                this.scanBlockEntities(chunk);
            }
        }
    }

    @Override
    protected void onDisable() {
        ChunkScanner.unregister(this);
        this.detections.clear();
        this.layer.clear();
        this.shownDimension = null;
    }

    private void found(class_2338 pos, Type type) {
        class_638 level = class_310.method_1551().field_1687;
        if (type == null || level == null) {
            return;
        }
        long key = pos.method_10063();
        Type previous = (Type)this.detections.computeIfAbsent((class_5321<class_1937>)level.method_27983(), k -> new Long2ObjectOpenHashMap()).put(key, (Object)type);
        if (previous == type) {
            return;
        }
        if (previous != null) {
            this.layer.remove(key);
        }
        if (type.enabled.get()) {
            this.layer.put(key, type);
        }
    }

    private void gone(class_2338 pos) {
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return;
        }
        Long2ObjectOpenHashMap<Type> found = this.detections.get(level.method_27983());
        if (found != null && found.remove(pos.method_10063()) != null) {
            this.layer.remove(pos.method_10063());
        }
    }

    @Override
    public boolean wants(class_2680 state) {
        return this.typeOf(state.method_26204()) != null;
    }

    @Override
    public void found(long pos, class_2680 state) {
        this.found(class_2338.method_10092((long)pos), this.typeOf(state.method_26204()));
    }

    @Override
    public void removed(long pos) {
        class_638 level = class_310.method_1551().field_1687;
        if (level != null && level.method_8320(class_2338.method_10092((long)pos)).method_26215()) {
            this.gone(class_2338.method_10092((long)pos));
        }
    }

    @Override
    public void reset() {
        this.detections.clear();
        this.layer.clear();
    }

    private void scanBlockEntities(class_2818 chunk) {
        for (class_2586 blockEntity : chunk.method_12214().values()) {
            this.found(blockEntity.method_11016(), this.typeOf(blockEntity.method_11017()));
        }
    }

    private void blockChanged(class_2338 pos, class_2680 state) {
        Type type = this.typeOf(state.method_26204());
        if (type != null) {
            this.found(pos, type);
        } else if (state.method_26215()) {
            this.gone(pos);
        }
    }

    @EventHandler
    public void onPacketApplied(PacketAppliedEvent event) {
        class_2596<?> class_25962;
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return;
        }
        if (level.method_27983() != this.shownDimension) {
            this.rescan();
        }
        if ((class_25962 = event.packet) instanceof class_2672) {
            class_2672 packet = (class_2672)class_25962;
            packet.method_38598().method_38587(packet.method_11523(), packet.method_11524()).accept((pos, type, tag) -> this.found(pos, this.typeOf(type, tag)));
            class_2818 chunk = level.method_2935().method_21730(packet.method_11523(), packet.method_11524());
            if (chunk != null) {
                this.scanBlockEntities(chunk);
            }
        } else {
            class_25962 = event.packet;
            if (class_25962 instanceof class_2626) {
                class_2626 packet = (class_2626)class_25962;
                this.blockChanged(packet.method_11309(), packet.method_11308());
            } else {
                class_25962 = event.packet;
                if (class_25962 instanceof class_2637) {
                    class_2637 packet = (class_2637)class_25962;
                    packet.method_30621(this::blockChanged);
                } else if (event.packet instanceof class_2678) {
                    this.detections.clear();
                    this.layer.clear();
                }
            }
        }
    }

    @EventHandler
    public void onPacketReceiveNetty(PacketReceiveNettyEvent event) {
        class_2596<?> class_25962 = event.packet;
        if (!(class_25962 instanceof class_2622)) {
            return;
        }
        class_2622 packet = (class_2622)class_25962;
        class_2338 pos = packet.method_11293();
        Type type = this.typeOf(packet.method_11291(), packet.method_11290());
        if (type != null) {
            class_310.method_1551().execute(() -> {
                if (this.isEnabled()) {
                    this.found(pos, type);
                }
            });
        }
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        class_638 level = class_310.method_1551().field_1687;
        if (level != null && level.method_27983() != this.shownDimension) {
            this.rescan();
        }
        this.layer.render(event, this.style);
        if (this.anyTracer()) {
            this.layer.renderTracers(this.style);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private final class Type {
        final BooleanSetting enabled;
        final BooleanSetting tracers;
        final ColorSetting color;
        final ColorSetting tracerColor;

        Type(String label, boolean on, int argb, String idOn, String idColor, String idTracers, String idTracerColor) {
            this.enabled = (BooleanSetting)StorageEspModule.this.addSetting(idOn, new BooleanSetting(label, on));
            this.color = (ColorSetting)StorageEspModule.this.addSetting(idColor, new ColorSetting(label + " Color", argb));
            this.tracers = (BooleanSetting)StorageEspModule.this.addSetting(idTracers, new BooleanSetting(label + " Tracers", false));
            this.tracerColor = (ColorSetting)StorageEspModule.this.addSetting(idTracerColor, new ColorSetting(label + " Tracer Color", argb));
            this.color.visibleWhen(this.enabled::get);
            this.tracers.visibleWhen(() -> StorageEspModule.this.sTracers.get() && this.enabled.get());
            this.tracerColor.visibleWhen(() -> StorageEspModule.this.sTracers.get() && this.enabled.get() && this.tracers.get());
            this.enabled.onChange(StorageEspModule.this::rescan);
        }
    }
}

