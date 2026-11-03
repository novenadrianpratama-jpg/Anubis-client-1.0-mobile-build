/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.resource.v1.ResourceLoader
 *  net.minecraft.class_1044
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3264
 *  net.minecraft.class_3302
 *  net.minecraft.class_4013
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.render.character;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import net.anubis.client.module.impl.render.character.CharacterModule;
import net.anubis.client.module.impl.render.character.CharacterTexture;
import net.anubis.client.module.impl.render.character.WornCharacter;
import net.anubis.client.module.impl.render.character.kit.CharacterBuilder;
import net.anubis.client.module.impl.render.character.kit.CharacterDesign;
import net.anubis.client.module.impl.render.character.kit.CharacterModel;
import net.anubis.client.module.impl.render.character.kit.Detail;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3264;
import net.minecraft.class_3302;
import net.minecraft.class_4013;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
final class CharacterCache {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final int KEEP = 4;
    private static final ForkJoinPool POOL = new ForkJoinPool(Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors() - 2)), pool -> {
        ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
        thread.setName("Anubis Character Builder " + thread.getPoolIndex());
        thread.setDaemon(true);
        thread.setPriority(4);
        return thread;
    }, null, false);
    private static final Map<String, Entry> ENTRIES = new LinkedHashMap<String, Entry>(8, 0.75f, true);
    private static boolean listening;
    private static Entry shown;

    private CharacterCache() {
    }

    static void listen() {
        if (listening) {
            return;
        }
        listening = true;
        ResourceLoader.get((class_3264)class_3264.field_14188).registerReloader(class_2960.method_60655((String)"anubis", (String)"character_textures"), (class_3302)((class_4013)manager -> CharacterCache.releaseTextures()));
    }

    static void prepare(CharacterDesign design, Detail detail) {
        String key = design.id() + "/" + detail.name();
        if (ENTRIES.containsKey(key)) {
            return;
        }
        Entry created = new Entry(design, detail);
        ENTRIES.put(key, created);
        CharacterCache.trim();
        CompletableFuture.runAsync(() -> CharacterCache.build(created), POOL);
    }

    static WornCharacter ready(CharacterDesign design, Detail detail) {
        String key = design.id() + "/" + detail.name();
        Entry entry = ENTRIES.get(key);
        if (entry == null) {
            Entry created = new Entry(design, detail);
            ENTRIES.put(key, created);
            CharacterCache.trim();
            CompletableFuture.runAsync(() -> CharacterCache.build(created), POOL);
            return CharacterCache.previous();
        }
        if (entry.worn != null) {
            shown = entry;
            return entry.worn;
        }
        CharacterModel model = entry.model;
        if (entry.failed) {
            return null;
        }
        if (model == null) {
            return CharacterCache.previous();
        }
        class_310.method_1551().method_1531().method_4616(entry.texture, (class_1044)new CharacterTexture(entry.texture.toString(), model.atlasWidth, model.atlasHeight, model.atlasMips));
        entry.worn = new WornCharacter(model, entry.texture);
        shown = entry;
        return entry.worn;
    }

    private static WornCharacter previous() {
        Entry last = shown;
        return last != null && ENTRIES.containsValue(last) ? last.worn : null;
    }

    private static void build(Entry entry) {
        try {
            CharacterModel model = CharacterBuilder.build(entry.design, entry.detail);
            LOGGER.info("[Anubis] 3D Character '{}' ({}) built: {} triangles in {} ms", new Object[]{entry.design.name(), entry.detail.label, model.triangles(), model.buildMillis});
            entry.model = model;
        }
        catch (Throwable failure) {
            entry.failed = true;
            LOGGER.error("[Anubis] 3D Character '{}' ({}) failed to build", new Object[]{entry.design.name(), entry.detail.label, failure});
            CharacterModule.failed(entry.design, failure);
        }
    }

    private static void trim() {
        Iterator<Entry> it = ENTRIES.values().iterator();
        while (ENTRIES.size() > 4 && it.hasNext()) {
            Entry eldest = it.next();
            CharacterCache.release(eldest);
            it.remove();
        }
    }

    static void releaseTextures() {
        for (Entry entry : ENTRIES.values()) {
            CharacterCache.release(entry);
        }
    }

    static void clear() {
        CharacterCache.releaseTextures();
        ENTRIES.clear();
        shown = null;
    }

    private static void release(Entry entry) {
        if (entry.worn == null) {
            return;
        }
        entry.worn = null;
        class_310.method_1551().method_1531().method_4615(entry.texture);
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Entry {
        final CharacterDesign design;
        final Detail detail;
        final class_2960 texture;
        volatile CharacterModel model;
        volatile boolean failed;
        WornCharacter worn;

        Entry(CharacterDesign design, Detail detail) {
            this.design = design;
            this.detail = detail;
            this.texture = class_2960.method_60655((String)"anubis", (String)("character/" + design.id() + "_" + detail.name().toLowerCase(Locale.ROOT)));
        }
    }
}

