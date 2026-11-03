/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBuffer$MappedView
 *  com.mojang.blaze3d.buffers.GpuFence
 *  com.mojang.blaze3d.opengl.GlStateManager
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.textures.TextureFormat
 *  com.sun.jna.Platform
 *  imgui.ImDrawList
 *  imgui.ImGui
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1041
 *  net.minecraft.class_10725
 *  net.minecraft.class_1799
 *  net.minecraft.class_310
 *  net.minecraft.class_408
 *  net.minecraft.class_437
 *  org.lwjgl.opengl.GL11
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.misc.noinvleak;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.sun.jna.Platform;
import imgui.ImDrawList;
import imgui.ImGui;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.anubis.client.gui.BorderlessFullscreen;
import net.anubis.client.gui.ItemIcons;
import net.anubis.client.gui.ItemLook;
import net.anubis.client.module.impl.misc.NoInvLeakModule;
import net.anubis.client.module.impl.misc.noinvleak.Canvas;
import net.anubis.client.module.impl.misc.noinvleak.DigitFont;
import net.anubis.client.module.impl.misc.noinvleak.OverlayArt;
import net.anubis.client.module.impl.misc.noinvleak.OverlayWindow;
import net.anubis.client.module.impl.misc.noinvleak.TextPanels;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1041;
import net.minecraft.class_10725;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_408;
import net.minecraft.class_437;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class StreamOverlay {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final int SLOTS = 41;
    private static final int CURSOR = 41;
    private static final int ENTRIES = 42;
    private static final long SLOT_BITS = 0x1FFFFFFFFFFL;
    private static final int CELLS_PER_SIDE = 7;
    private static final int CACHE_LIMIT = 0x400000;
    private static final long READBACK_TIMEOUT = 1000000000L;
    private static final long BACKGROUND_INTERVAL = 33000000L;
    private static final int FALLBACK_BACKGROUND = -7631989;
    private static final int[] BLANK = new int[0];
    private static final Runnable NOTHING = () -> {};
    private static final class_1799[] stack = new class_1799[42];
    private static final int[] guiX = new int[42];
    private static final int[] guiY = new int[42];
    private static long mask;
    private static int hovered;
    private static boolean selectionSeen;
    private static int selectionX;
    private static int selectionY;
    private static long fakeMask;
    private static int fakeScale;
    private static ImDrawList fakeList;
    private static final DigitFont.Sink FAKE_SINK;
    private static final class_1799[] shownStack;
    private static final int[] shownX;
    private static final int[] shownY;
    private static final int[] shownCount;
    private static final int[] shownBar;
    private static long shownMask;
    private static int shownScale;
    private static boolean shownOpaque;
    private static boolean shownSelection;
    private static int shownSelectionX;
    private static int shownSelectionY;
    private static int shownHover;
    private static int shownWindowX;
    private static int shownWindowY;
    private static GpuTexture shownAtlas;
    private static int boxX;
    private static int boxY;
    private static boolean backgroundValid;
    private static class_1799 cursorStack;
    private static int cursorCount;
    private static int cursorBar;
    private static int cursorScale;
    private static int cursorX;
    private static int cursorY;
    private static final ItemLook look;
    private static final Object2ObjectOpenHashMap<List<Object>, int[]> icons;
    private static final int[][] slotIcon;
    private static final int[] pendingSlot;
    private static final List<Object>[] pendingKey;
    private static int cachedBytes;
    private static final int[][] background;
    private static final int[][] backgroundNext;
    private static final int[] syncRegion;
    private static long backgroundAt;
    private static GpuFence asyncFence;
    private static GpuBuffer asyncBuffer;
    private static long asyncMask;
    private static int asyncScale;
    private static final int[] asyncX;
    private static final int[] asyncY;
    private static final int[] asyncRegion;
    private static final Canvas canvas;
    private static OverlayWindow slotWindow;
    private static OverlayWindow cursorWindow;
    private static GpuTexture scratch;
    private static GpuTextureView scratchView;
    private static GpuTexture scratchDepth;
    private static GpuTextureView scratchDepthView;
    private static GpuBuffer readback;
    private static int iconPx;
    private static volatile boolean unsupported;
    private static volatile long retryAt;
    private static int transientFailures;
    private static boolean toldUnsupported;
    private static boolean toldPaused;
    private static boolean toldFullscreen;
    private static boolean diagnosed;
    private static final long[] BACKOFF;
    private static final long REPEAT_LOG_INTERVAL = 60000000000L;
    private static final Map<String, long[]> reported;

    private StreamOverlay() {
    }

    public static boolean usable() {
        if (!Platform.isWindows() || unsupported) {
            return false;
        }
        long at = retryAt;
        return at == 0L || System.nanoTime() - at >= 0L;
    }

    public static void retry() {
        unsupported = false;
        retryAt = 0L;
        transientFailures = 0;
    }

    public static boolean recordsHotbar() {
        return StreamOverlay.hotbarVisible();
    }

    public static void hotbar(int slot, int x, int y, class_1799 item) {
        if (StreamOverlay.hotbarVisible()) {
            StreamOverlay.record(slot, x, y, item);
        }
    }

    public static void container(int slot, int x, int y, class_1799 item, boolean isHovered) {
        StreamOverlay.record(slot, x, y, item);
        if (isHovered) {
            hovered = slot;
        }
    }

    public static void carried(class_1799 item, int x, int y) {
        StreamOverlay.record(41, x, y, item);
    }

    public static void selection(int x, int y) {
        if (!StreamOverlay.hotbarVisible()) {
            return;
        }
        selectionSeen = true;
        selectionX = x;
        selectionY = y;
    }

    public static void tooltip(class_1799 item, int mouseX, int mouseY) {
        TextPanels.tooltip(item, mouseX, mouseY);
    }

    public static void itemName(class_1799 item, int alpha, int y) {
        TextPanels.itemName(item, alpha, y);
    }

    private static boolean hotbarVisible() {
        class_437 screen = class_310.method_1551().field_1755;
        return screen == null || screen instanceof class_408;
    }

    private static void record(int entry, int x, int y, class_1799 item) {
        if (entry < 0 || entry >= 42 || entry == 41 && item.method_7960()) {
            return;
        }
        StreamOverlay.stack[entry] = item;
        StreamOverlay.guiX[entry] = x;
        StreamOverlay.guiY[entry] = y;
        mask |= 1L << entry;
    }

    public static boolean hasFakes() {
        return fakeMask != 0L;
    }

    public static void drawFakes() {
        long bits = fakeMask;
        int scale = fakeScale;
        if (bits == 0L || scale <= 0) {
            return;
        }
        try {
            fakeList = ImGui.getBackgroundDrawList();
            for (int slot = 0; slot < 41; ++slot) {
                class_1799 fake;
                if ((bits >>> slot & 1L) == 0L || (fake = NoInvLeakModule.fakeFor(slot)).method_7960()) continue;
                int x = guiX[slot] * scale;
                int y = guiY[slot] * scale;
                ItemIcons.draw(fakeList, fake, (float)x, (float)y, 16.0f * (float)scale, 1.0f);
                DigitFont.decorations(FAKE_SINK, fake, x, y, scale);
            }
        }
        catch (LinkageError | RuntimeException e) {
            fakeMask = 0L;
            StreamOverlay.fail(e, "drawing the stream's fake items");
        }
    }

    public static void endFrame() {
        long frame = mask;
        mask = 0L;
        int hover = hovered;
        hovered = -1;
        boolean selection = selectionSeen && NoInvLeakModule.hidesSelectedSlot();
        selectionSeen = false;
        if (!NoInvLeakModule.overlayWanted() || !StreamOverlay.usable()) {
            fakeMask = 0L;
            if (StreamOverlay.started()) {
                StreamOverlay.release();
            }
            TextPanels.frameDone();
            return;
        }
        try {
            class_310 mc = class_310.method_1551();
            class_1041 win = mc.method_22683();
            if (win.method_65966()) {
                fakeMask = 0L;
                StreamOverlay.hideAll();
            } else {
                int scale = win.method_4495();
                int windowX = win.method_4499();
                int windowY = win.method_4477();
                boolean opaque = NoInvLeakModule.opaqueCells();
                frame = StreamOverlay.withoutEmpty(frame, opaque && NoInvLeakModule.fillsEmpty());
                GpuTexture atlas = StreamOverlay.itemAtlas(mc);
                if (atlas != shownAtlas) {
                    OverlayArt.reset();
                    StreamOverlay.resetIcons(16 * scale);
                    shownAtlas = atlas;
                    shownMask = 0L;
                }
                fakeMask = frame & 0x1FFFFFFFFFFL;
                fakeScale = scale;
                StreamOverlay.layers(mc, win, frame, scale, windowX, windowY, opaque, selection, hover);
                TextPanels.update(win, scale, windowX, windowY);
            }
        }
        catch (LinkageError | RuntimeException e) {
            fakeMask = 0L;
            StreamOverlay.fail(e, "drawing the overlay");
        }
        TextPanels.frameDone();
    }

    private static void fail(Throwable e, String phase) {
        String outcome;
        boolean permanent = e instanceof OverlayWindow.Unsupported || e instanceof LinkageError;
        String reason = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        long wait = 0L;
        if (permanent) {
            unsupported = true;
        } else {
            wait = BACKOFF[Math.min(transientFailures++, BACKOFF.length - 1)];
            retryAt = System.nanoTime() + wait;
        }
        String key = phase + "|" + e.getClass().getName() + "|" + reason;
        long now = System.nanoTime();
        long[] seen = reported.get(key);
        String string = outcome = permanent ? "turned off for this session" : "retrying in " + wait / 1000000000L + " s";
        if (seen == null) {
            reported.put(key, new long[]{now, 0L});
            LOGGER.warn("[Anubis] No Inv Leak: Stream Only failed while {} ({}); {}. Windows build {}. Your items are shown as hidden to you as well until it works again.", new Object[]{phase, reason, outcome, OverlayWindow.windowsBuild(), e});
        } else if (now - seen[0] >= 60000000000L) {
            LOGGER.warn("[Anubis] No Inv Leak: Stream Only failed again while {} ({}), {} more time(s) since the last report; {}", new Object[]{phase, reason, seen[1] + 1L, outcome});
            seen[0] = now;
            seen[1] = 0L;
        } else {
            seen[1] = seen[1] + 1L;
        }
        if (permanent && !toldUnsupported) {
            toldUnsupported = true;
            NoInvLeakModule.streamNotice("Stream Only unavailable", ": " + reason + ". Items stay hidden for you too.");
        } else if (!permanent && !toldPaused) {
            toldPaused = true;
            NoInvLeakModule.streamNotice("Stream Only paused", ": " + reason + ". Retrying automatically.");
        }
        try {
            StreamOverlay.release();
        }
        catch (LinkageError | RuntimeException cleanup) {
            LOGGER.debug("[Anubis] No Inv Leak: overlay cleanup failed", cleanup);
        }
    }

    private static void diagnose(class_310 mc, class_1041 win) {
        Object renderer;
        boolean borderless;
        if (diagnosed) {
            return;
        }
        diagnosed = true;
        transientFailures = 0;
        boolean fullscreen = win.method_4498();
        boolean bl = borderless = fullscreen && ((BorderlessFullscreen.Switchable)win).anubis$isBorderless();
        String mode = !fullscreen ? "windowed" : (borderless ? "borderless fullscreen" : "exclusive fullscreen");
        try {
            renderer = GL11.glGetString((int)7936) + " / " + GL11.glGetString((int)7937);
        }
        catch (LinkageError | RuntimeException unknown) {
            renderer = "unknown";
        }
        LOGGER.info("[Anubis] No Inv Leak: Stream Only overlay active (Windows build {}, {}, window {}x{} at {},{}, framebuffer {}x{}, gui scale {}, {}, overlay {})", new Object[]{OverlayWindow.windowsBuild(), mode, win.method_4480(), win.method_4507(), win.method_4499(), win.method_4477(), mc.method_1522().field_1482, mc.method_1522().field_1481, win.method_4495(), renderer, slotWindow != null ? slotWindow.describe() : "pending"});
        if (fullscreen && !borderless && !toldFullscreen) {
            toldFullscreen = true;
            NoInvLeakModule.streamNotice("Stream Only", ": exclusive fullscreen can hide the overlay from you. Turn on Borderless Fullscreen.");
        }
    }

    public static void stop() {
        diagnosed = false;
        if (RenderSystem.isOnRenderThread()) {
            StreamOverlay.release();
        } else {
            class_310.method_1551().execute(StreamOverlay::release);
        }
    }

    private static boolean started() {
        return slotWindow != null || cursorWindow != null || scratch != null || TextPanels.started();
    }

    private static void layers(class_310 mc, class_1041 win, long frame, int scale, int windowX, int windowY, boolean opaque, boolean selection, int hover) {
        boolean slotsShown;
        long slotBits = frame & 0x1FFFFFFFFFFL;
        boolean bl = slotsShown = slotBits != 0L || selection;
        if (!slotsShown && (frame >>> 41 & 1L) == 0L) {
            StreamOverlay.hideSlotsAndCursor();
            return;
        }
        int px = 16 * scale;
        if (px != iconPx || cachedBytes > 0x400000) {
            StreamOverlay.resetIcons(px);
        }
        if (scratch == null) {
            StreamOverlay.allocate(px);
        }
        if (slotWindow == null && slotsShown) {
            slotWindow = new OverlayWindow(win.method_4490());
        }
        if (cursorWindow == null && (frame >>> 41 & 1L) != 0L) {
            cursorWindow = new OverlayWindow(win.method_4490());
        }
        boolean layout = slotBits != shownMask || scale != shownScale || opaque != shownOpaque || selection != shownSelection || selection && (selectionX != shownSelectionX || selectionY != shownSelectionY);
        boolean content = false;
        for (int i = 0; i < 41; ++i) {
            if ((slotBits >>> i & 1L) == 0L) continue;
            if (guiX[i] != shownX[i] || guiY[i] != shownY[i]) {
                layout = true;
                continue;
            }
            if (stack[i] == shownStack[i] && stack[i].method_7947() == shownCount[i] && StreamOverlay.bar(stack[i]) == shownBar[i]) continue;
            content = true;
        }
        if (layout) {
            backgroundValid = false;
            StreamOverlay.dropAsync();
        }
        int pending = StreamOverlay.resolveIcons(frame, px);
        boolean wantBackground = opaque && slotBits != 0L && !backgroundValid;
        boolean backgroundChanged = false;
        if (pending > 0 || wantBackground) {
            StreamOverlay.synchronousRead(slotBits, scale, wantBackground, pending);
            if (wantBackground) {
                backgroundChanged = true;
                shownHover = hover;
            }
        } else if (opaque && slotBits != 0L) {
            backgroundChanged = StreamOverlay.takeAsync(slotBits, scale);
        }
        if (!slotsShown) {
            if (slotWindow != null) {
                slotWindow.hide();
            }
            shownMask = 0L;
        } else if (layout || content || backgroundChanged || pending > 0) {
            StreamOverlay.composeSlots(slotBits, scale, opaque, selection, windowX, windowY);
        } else if (windowX != shownWindowX || windowY != shownWindowY) {
            slotWindow.move(windowX + boxX, windowY + boxY);
            shownWindowX = windowX;
            shownWindowY = windowY;
        }
        if (opaque && slotBits != 0L && asyncFence == null && backgroundValid && (hover != shownHover || StreamOverlay.hotbarVisible() && System.nanoTime() - backgroundAt >= 33000000L)) {
            StreamOverlay.requestAsync(slotBits, scale);
            shownHover = hover;
        }
        StreamOverlay.cursor(frame, scale, windowX, windowY);
    }

    private static void hideSlotsAndCursor() {
        if (slotWindow != null) {
            slotWindow.hide();
        }
        if (cursorWindow != null) {
            cursorWindow.hide();
        }
        shownMask = 0L;
        cursorStack = null;
    }

    private static long withoutEmpty(long frame, boolean keepEmpty) {
        if (keepEmpty) {
            return frame;
        }
        long bits = frame;
        for (int i = 0; i < 41; ++i) {
            if ((bits >>> i & 1L) == 0L || !stack[i].method_7960()) continue;
            bits &= 1L << i ^ 0xFFFFFFFFFFFFFFFFL;
        }
        return bits;
    }

    private static void hideAll() {
        StreamOverlay.hideSlotsAndCursor();
        TextPanels.hide();
    }

    private static int bar(class_1799 item) {
        return item.method_31578() ? item.method_31580() << 5 | item.method_31579() : -1;
    }

    private static int cellX(int cell, int px) {
        return cell % 7 * px;
    }

    private static int cellY(int cell, int px) {
        return cell / 7 * px;
    }

    private static int resolveIcons(long bits, int px) {
        int pending = 0;
        for (int i = 0; i < 42; ++i) {
            if ((bits >>> i & 1L) == 0L) continue;
            class_1799 item = stack[i];
            if (item != shownStack[i]) {
                StreamOverlay.slotIcon[i] = null;
            }
            if (slotIcon[i] != null) continue;
            if (item.method_7960() || !look.resolve(item)) {
                StreamOverlay.slotIcon[i] = BLANK;
                continue;
            }
            int[] icon = (int[])icons.get(StreamOverlay.look.identity);
            if (icon != null) {
                StreamOverlay.slotIcon[i] = icon;
            } else {
                int cell = pending++;
                try {
                    ItemIcons.render(look, scratch, scratchView, scratchDepth, scratchDepthView, StreamOverlay.cellX(cell, px), StreamOverlay.cellY(cell, px), px);
                }
                catch (RuntimeException e) {
                    StreamOverlay.slotIcon[i] = BLANK;
                    look.method_65605();
                    String key = "icon|" + String.valueOf(item.method_7909());
                    if (reported.putIfAbsent(key, new long[]{System.nanoTime(), 0L}) != null) continue;
                    LOGGER.warn("[Anubis] No Inv Leak: Stream Only could not draw the icon for {}; it stays blank", (Object)item.method_7909(), (Object)e);
                    continue;
                }
                StreamOverlay.pendingSlot[cell] = i;
                StreamOverlay.pendingKey[cell] = Arrays.asList(StreamOverlay.look.identity.toArray());
            }
            look.method_65605();
        }
        return pending;
    }

    private static void resetIcons(int px) {
        icons.clear();
        cachedBytes = 0;
        Arrays.fill((Object[])slotIcon, null);
        StreamOverlay.dropAsync();
        backgroundValid = false;
        if (px == iconPx && scratch != null) {
            return;
        }
        StreamOverlay.closeTextures();
        Arrays.fill((Object[])background, null);
        Arrays.fill((Object[])backgroundNext, null);
    }

    private static void allocate(int px) {
        int side = 7 * px;
        GpuDevice device = RenderSystem.getDevice();
        scratch = device.createTexture("Anubis overlay icons", 15, TextureFormat.RGBA8, side, side, 1, 1);
        scratchView = device.createTextureView(scratch);
        scratchDepth = device.createTexture("Anubis overlay icon depth", 8, TextureFormat.DEPTH32, side, side, 1, 1);
        scratchDepthView = device.createTextureView(scratchDepth);
        device.createCommandEncoder().clearColorAndDepthTextures(scratch, 0, scratchDepth, 1.0);
        iconPx = px;
    }

    private static GpuTexture itemAtlas(class_310 mc) {
        try {
            return mc.method_72703().method_73025(class_10725.field_64477).method_68004();
        }
        catch (RuntimeException notReady) {
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void synchronousRead(long slotBits, int scale, boolean wantBackground, int pending) {
        int px = 16 * scale;
        int side = 7 * px;
        long scratchBytes = pending > 0 ? (long)side * (long)side * 4L : 0L;
        boolean framed = wantBackground && StreamOverlay.region(slotBits, scale, syncRegion);
        long frameBytes = framed ? (long)syncRegion[2] * (long)syncRegion[3] * 4L : 0L;
        long needed = framed ? scratchBytes + StreamOverlay.frameTextureBytes() : scratchBytes;
        long used = scratchBytes + frameBytes;
        if (wantBackground) {
            backgroundValid = true;
        }
        if (needed == 0L) {
            if (wantBackground) {
                StreamOverlay.unpackBackground(null, 0, syncRegion, false, slotBits, scale);
            }
            return;
        }
        GpuDevice device = RenderSystem.getDevice();
        if (readback == null || readback.size() < needed) {
            if (readback != null) {
                readback.close();
            }
            readback = device.createBuffer(() -> "Anubis overlay readback", 9, needed);
        }
        CommandEncoder encoder = device.createCommandEncoder();
        StreamOverlay.packDefaults();
        try {
            if (pending > 0) {
                encoder.copyTextureToBuffer(scratch, readback, 0L, NOTHING, 0);
            }
            if (framed) {
                StreamOverlay.copyRegion(encoder, readback, scratchBytes, syncRegion);
            }
        }
        finally {
            StreamOverlay.packReset();
        }
        try (GpuFence copied = encoder.createFence();){
            GL11.glFlush();
            if (!copied.awaitCompletion(1000000000L)) {
                throw new IllegalStateException("overlay read-back timed out");
            }
        }
        try (GpuBuffer.MappedView view = encoder.mapBuffer(readback.slice(0L, used), true, false);){
            ByteBuffer data = view.data();
            for (int c = 0; c < pending; ++c) {
                int[] icon = StreamOverlay.unpackIcon(data, StreamOverlay.cellX(c, px), StreamOverlay.cellY(c, px), side, px);
                icons.put(pendingKey[c], (Object)icon);
                StreamOverlay.pendingKey[c] = null;
                cachedBytes += px * px * 4;
                StreamOverlay.slotIcon[StreamOverlay.pendingSlot[c]] = icon;
            }
            if (wantBackground) {
                StreamOverlay.unpackBackground(data, (int)scratchBytes, syncRegion, framed, slotBits, scale);
                backgroundAt = System.nanoTime();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void requestAsync(long slotBits, int scale) {
        backgroundAt = System.nanoTime();
        if (!StreamOverlay.region(slotBits, scale, asyncRegion)) {
            return;
        }
        long frameBytes = StreamOverlay.frameTextureBytes();
        GpuDevice device = RenderSystem.getDevice();
        if (asyncBuffer == null || asyncBuffer.size() < frameBytes) {
            if (asyncBuffer != null) {
                asyncBuffer.close();
            }
            asyncBuffer = device.createBuffer(() -> "Anubis overlay background", 9, frameBytes);
        }
        CommandEncoder encoder = device.createCommandEncoder();
        StreamOverlay.packDefaults();
        try {
            StreamOverlay.copyRegion(encoder, asyncBuffer, 0L, asyncRegion);
        }
        finally {
            StreamOverlay.packReset();
        }
        asyncFence = encoder.createFence();
        GL11.glFlush();
        asyncMask = slotBits;
        asyncScale = scale;
        for (int i = 0; i < 41; ++i) {
            StreamOverlay.asyncX[i] = guiX[i];
            StreamOverlay.asyncY[i] = guiY[i];
        }
    }

    private static boolean takeAsync(long slotBits, int scale) {
        if (asyncFence == null || !asyncFence.awaitCompletion(0L)) {
            return false;
        }
        boolean usable = asyncMask == slotBits && asyncScale == scale;
        for (int i = 0; usable && i < 41; ++i) {
            if ((slotBits >>> i & 1L) == 0L || asyncX[i] == guiX[i] && asyncY[i] == guiY[i]) continue;
            usable = false;
        }
        boolean changed = false;
        if (usable) {
            long bytes = (long)asyncRegion[2] * (long)asyncRegion[3] * 4L;
            CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
            try (GpuBuffer.MappedView view = encoder.mapBuffer(asyncBuffer.slice(0L, bytes), true, false);){
                changed = StreamOverlay.unpackBackground(view.data(), 0, asyncRegion, true, slotBits, scale);
            }
        }
        StreamOverlay.dropAsync();
        return changed;
    }

    private static void dropAsync() {
        if (asyncFence == null) {
            return;
        }
        asyncFence.close();
        asyncFence = null;
    }

    private static long frameTextureBytes() {
        GpuTexture frame = class_310.method_1551().method_1522().method_30277();
        return (long)frame.getWidth(0) * (long)frame.getHeight(0) * 4L;
    }

    private static void packDefaults() {
        GlStateManager._pixelStore((int)3333, (int)4);
        GlStateManager._pixelStore((int)3331, (int)0);
        GlStateManager._pixelStore((int)3332, (int)0);
    }

    private static void packReset() {
        GlStateManager._pixelStore((int)3330, (int)0);
    }

    private static boolean region(long slotBits, int scale, int[] out) {
        GpuTexture target = class_310.method_1551().method_1522().method_30277();
        int px = 16 * scale;
        int x0 = Integer.MAX_VALUE;
        int y0 = Integer.MAX_VALUE;
        int x1 = Integer.MIN_VALUE;
        int y1 = Integer.MIN_VALUE;
        for (int i = 0; i < 41; ++i) {
            if ((slotBits >>> i & 1L) == 0L) continue;
            x0 = Math.min(x0, guiX[i] * scale);
            y0 = Math.min(y0, guiY[i] * scale);
            x1 = Math.max(x1, guiX[i] * scale + px);
            y1 = Math.max(y1, guiY[i] * scale + px);
        }
        x0 = Math.max(x0, 0);
        y0 = Math.max(y0, 0);
        x1 = Math.min(x1, target.getWidth(0));
        y1 = Math.min(y1, target.getHeight(0));
        if (x1 <= x0 || y1 <= y0) {
            return false;
        }
        out[0] = x0;
        out[1] = y0;
        out[2] = x1 - x0;
        out[3] = y1 - y0;
        return true;
    }

    private static void copyRegion(CommandEncoder encoder, GpuBuffer target, long offset, int[] region) {
        GpuTexture color = class_310.method_1551().method_1522().method_30277();
        encoder.copyTextureToBuffer(color, target, offset, NOTHING, 0, region[0], color.getHeight(0) - region[1] - region[3], region[2], region[3]);
    }

    private static int[] unpackIcon(ByteBuffer data, int cellX, int cellY, int side, int px) {
        int[] out = new int[px * px];
        for (int y = 0; y < px; ++y) {
            int row = ((side - 1 - (cellY + y)) * side + cellX) * 4;
            for (int x = 0; x < px; ++x) {
                int at = row + x * 4;
                int a = data.get(at + 3) & 0xFF;
                out[y * px + x] = a << 24 | Math.min(a, data.get(at) & 0xFF) << 16 | Math.min(a, data.get(at + 1) & 0xFF) << 8 | Math.min(a, data.get(at + 2) & 0xFF);
            }
        }
        return out;
    }

    private static boolean unpackBackground(ByteBuffer data, int offset, int[] region, boolean framed, long slotBits, int scale) {
        int px = 16 * scale;
        int rx = region[0];
        int ry = region[1];
        int rw = region[2];
        int rh = region[3];
        boolean changed = false;
        for (int slot = 0; slot < 41; ++slot) {
            if ((slotBits >>> slot & 1L) == 0L) continue;
            int cx = guiX[slot] * scale - rx;
            int cy = guiY[slot] * scale - ry;
            if (!framed || cx < 0 || cy < 0 || cx + px > rw || cy + px > rh) {
                if (background[slot] != null) {
                    changed = true;
                }
                StreamOverlay.background[slot] = null;
                continue;
            }
            int[] next = backgroundNext[slot];
            if (next == null || next.length != px * px) {
                next = new int[px * px];
            }
            for (int y = 0; y < px; ++y) {
                int row = offset + ((rh - 1 - (cy + y)) * rw + cx) * 4;
                for (int x = 0; x < px; ++x) {
                    int at = row + x * 4;
                    next[y * px + x] = 0xFF000000 | (data.get(at) & 0xFF) << 16 | (data.get(at + 1) & 0xFF) << 8 | data.get(at + 2) & 0xFF;
                }
            }
            int[] current = background[slot];
            if (current == null || !Arrays.equals(current, next)) {
                changed = true;
                StreamOverlay.background[slot] = next;
                StreamOverlay.backgroundNext[slot] = current;
                continue;
            }
            StreamOverlay.backgroundNext[slot] = next;
        }
        return changed;
    }

    private static void composeSlots(long slotBits, int scale, boolean opaque, boolean selection, int windowX, int windowY) {
        int oy;
        int ox;
        int i;
        int px = 16 * scale;
        OverlayArt.Image frame = selection ? OverlayArt.selection() : null;
        boolean drawSelection = frame != null;
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int i2 = 0; i2 < 41; ++i2) {
            if ((slotBits >>> i2 & 1L) == 0L) continue;
            minX = Math.min(minX, guiX[i2]);
            minY = Math.min(minY, guiY[i2]);
            maxX = Math.max(maxX, guiX[i2] + 17);
            maxY = Math.max(maxY, guiY[i2] + 17);
        }
        if (drawSelection) {
            minX = Math.min(minX, selectionX);
            minY = Math.min(minY, selectionY);
            maxX = Math.max(maxX, selectionX + frame.width());
            maxY = Math.max(maxY, selectionY + frame.height());
        }
        if (minX > maxX) {
            slotWindow.hide();
            shownMask = 0L;
            return;
        }
        int bx = minX * scale;
        int by = minY * scale;
        int bw = (maxX - minX) * scale;
        int bh = (maxY - minY) * scale;
        canvas.wrap(slotWindow.pixels(bw, bh), slotWindow.stride(), bw, bh);
        if (opaque) {
            for (i = 0; i < 41; ++i) {
                if ((slotBits >>> i & 1L) == 0L) continue;
                ox = guiX[i] * scale - bx;
                oy = guiY[i] * scale - by;
                int[] behind = background[i];
                if (behind != null) {
                    canvas.copy(behind, px, px, ox, oy);
                    continue;
                }
                canvas.rect(ox, oy, px, px, -7631989);
            }
        }
        if (drawSelection) {
            canvas.blend(frame.pixels(), frame.width(), frame.height(), selectionX * scale - bx, selectionY * scale - by, scale);
        }
        for (i = 0; i < 41; ++i) {
            if ((slotBits >>> i & 1L) == 0L) continue;
            ox = guiX[i] * scale - bx;
            oy = guiY[i] * scale - by;
            int[] icon = slotIcon[i];
            if (icon != null && icon != BLANK) {
                canvas.blend(icon, px, px, ox, oy, 1);
            }
            if (stack[i].method_7960()) continue;
            DigitFont.decorations(canvas, stack[i], ox, oy, scale);
        }
        slotWindow.present(windowX + bx, windowY + by, bw, bh);
        StreamOverlay.diagnose(class_310.method_1551(), class_310.method_1551().method_22683());
        for (i = 0; i < 41; ++i) {
            if ((slotBits >>> i & 1L) == 0L) continue;
            StreamOverlay.shownStack[i] = stack[i];
            StreamOverlay.shownX[i] = guiX[i];
            StreamOverlay.shownY[i] = guiY[i];
            StreamOverlay.shownCount[i] = stack[i].method_7947();
            StreamOverlay.shownBar[i] = StreamOverlay.bar(stack[i]);
        }
        shownMask = slotBits;
        shownScale = scale;
        shownOpaque = opaque;
        shownSelection = selection;
        shownSelectionX = selectionX;
        shownSelectionY = selectionY;
        shownWindowX = windowX;
        shownWindowY = windowY;
        boxX = bx;
        boxY = by;
    }

    private static void cursor(long frame, int scale, int windowX, int windowY) {
        boolean same;
        if ((frame >>> 41 & 1L) == 0L) {
            if (cursorWindow != null) {
                cursorWindow.hide();
            }
            cursorStack = null;
            return;
        }
        class_1799 item = stack[41];
        int px = 16 * scale;
        int screenX = windowX + guiX[41] * scale;
        int screenY = windowY + guiY[41] * scale;
        boolean bl = same = item == cursorStack && item.method_7947() == cursorCount && StreamOverlay.bar(item) == cursorBar && scale == cursorScale;
        if (same) {
            if (screenX != cursorX || screenY != cursorY) {
                cursorWindow.move(screenX, screenY);
            }
        } else {
            int size = px + scale;
            canvas.wrap(cursorWindow.pixels(size, size), cursorWindow.stride(), size, size);
            int[] icon = slotIcon[41];
            if (icon != null && icon != BLANK) {
                canvas.blend(icon, px, px, 0, 0, 1);
            }
            DigitFont.decorations(canvas, item, 0, 0, scale);
            cursorWindow.present(screenX, screenY, size, size);
            cursorStack = item;
            cursorCount = item.method_7947();
            cursorBar = StreamOverlay.bar(item);
            cursorScale = scale;
        }
        cursorX = screenX;
        cursorY = screenY;
        StreamOverlay.shownStack[41] = item;
    }

    private static void closeTextures() {
        if (scratchView != null) {
            scratchView.close();
        }
        if (scratch != null) {
            scratch.close();
        }
        if (scratchDepthView != null) {
            scratchDepthView.close();
        }
        if (scratchDepth != null) {
            scratchDepth.close();
        }
        scratchView = null;
        scratch = null;
        scratchDepthView = null;
        scratchDepth = null;
        iconPx = 0;
    }

    private static void release() {
        StreamOverlay.dropAsync();
        if (slotWindow != null) {
            slotWindow.destroy();
            slotWindow = null;
        }
        if (cursorWindow != null) {
            cursorWindow.destroy();
            cursorWindow = null;
        }
        TextPanels.release();
        StreamOverlay.closeTextures();
        if (readback != null) {
            readback.close();
            readback = null;
        }
        if (asyncBuffer != null) {
            asyncBuffer.close();
            asyncBuffer = null;
        }
        icons.clear();
        cachedBytes = 0;
        Arrays.fill((Object[])slotIcon, null);
        Arrays.fill(pendingKey, null);
        Arrays.fill((Object[])background, null);
        Arrays.fill((Object[])backgroundNext, null);
        Arrays.fill(stack, null);
        Arrays.fill(shownStack, null);
        mask = 0L;
        fakeMask = 0L;
        shownMask = 0L;
        shownAtlas = null;
        shownHover = -1;
        cursorStack = null;
        backgroundValid = false;
    }

    private static List<Object>[] newKeys(int n) {
        return new List[n];
    }

    static {
        hovered = -1;
        FAKE_SINK = (x, y, w, h, argb) -> fakeList.addRectFilled((float)x, (float)y, (float)(x + w), (float)(y + h), argb & 0xFF00FF00 | argb << 16 & 0xFF0000 | argb >>> 16 & 0xFF);
        shownStack = new class_1799[42];
        shownX = new int[42];
        shownY = new int[42];
        shownCount = new int[42];
        shownBar = new int[42];
        shownHover = -1;
        look = new ItemLook();
        icons = new Object2ObjectOpenHashMap();
        slotIcon = new int[42][];
        pendingSlot = new int[49];
        pendingKey = StreamOverlay.newKeys(49);
        background = new int[41][];
        backgroundNext = new int[41][];
        syncRegion = new int[4];
        asyncX = new int[41];
        asyncY = new int[41];
        asyncRegion = new int[4];
        canvas = new Canvas();
        BACKOFF = new long[]{2000000000L, 10000000000L, 60000000000L};
        reported = new HashMap<String, long[]>();
    }
}

