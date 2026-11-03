/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.opengl.GlStateManager
 *  imgui.ImFont
 *  imgui.ImFontConfig
 *  imgui.ImFontGlyphRangesBuilder
 *  imgui.ImGui
 *  imgui.ImGuiIO
 *  imgui.glfw.ImGuiImplGlfw
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_1041
 *  net.minecraft.class_11875
 *  net.minecraft.class_11876
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWKeyCallback
 *  org.lwjgl.glfw.GLFWKeyCallbackI
 *  org.lwjgl.glfw.GLFWMonitorCallback
 *  org.lwjgl.glfw.GLFWMonitorCallbackI
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL30
 */
package net.anubis.client.imgui;

import com.mojang.blaze3d.opengl.GlStateManager;
import imgui.ImFont;
import imgui.ImFontConfig;
import imgui.ImFontGlyphRangesBuilder;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.AnubisGl3;
import imgui.glfw.ImGuiImplGlfw;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.function.BooleanSupplier;
import net.anubis.client.config.HudTheme;
import net.anubis.client.config.ThemeStore;
import net.anubis.client.mobile.ZalithCompat;
import net.anubis.client.gui.GuiPalette;
import net.anubis.client.gui.ThemeImages;
import net.anubis.client.imgui.UiFont;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1041;
import net.minecraft.class_11875;
import net.minecraft.class_11876;
import net.minecraft.class_310;
import net.minecraft.class_332;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWMonitorCallbackI;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

@Environment(value=EnvType.CLIENT)
public final class ImGuiManager {
    private static final ImGuiManager INSTANCE = new ImGuiManager();
    private static final String MODULE_FONT_RESOURCE = "/assets/anubis/fonts/geist-medium.ttf";
    private static final String MENU_FONT_RESOURCE = "/assets/anubis/fonts/rajdhani-bold.ttf";
    public static final float MENU_FONT_SIZE = 24.0f;
    public static final float MENU_TAG_FONT_SIZE = 15.0f;
    public static final float MENU_BODY_FONT_SIZE = 18.0f;
    public static final float MENU_NAME_FONT_SIZE = 20.0f;
    public static final float MENU_ITEM_FONT_SIZE = 32.0f;
    public static final float HUD_VALUE_FONT_SIZE = 18.0f;
    public static final float HUD_TEXT_FONT_SIZE = 17.0f;
    public static final float HUD_LABEL_FONT_SIZE = 14.0f;
    private static final String EXTENDED_LATIN_GLYPHS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789\u00c7\u011e\u0130\u00d6\u015e\u00dc\u00e7\u011f\u0131\u00f6\u015f\u00fc\u00c2\u00e2\u00ca\u00ea\u00ce\u00ee\u00d4\u00f4\u00db\u00fb\u00c0\u00c1\u00c4\u00c8\u00c9\u00cb\u00cc\u00cd\u00cf\u00d2\u00d3\u00d6\u00d9\u00da\u00dc\u00dd\u00e0\u00e1\u00e4\u00e8\u00e9\u00eb\u00ec\u00ed\u00ef\u00f2\u00f3\u00f6\u00f9\u00fa\u00fc\u00fd\u00ff\u2212\u221e";
    private static final float REFERENCE_HEIGHT = 1080.0f;
    private final ImGuiImplGlfw glfw = new ImGuiImplGlfw(){

        public void keyCallback(long window, int keycode, int scancode, int action, int mods) {
            boolean feed;
            boolean tracked;
            boolean bl = tracked = keycode >= 0 && keycode < ImGuiManager.this.imguiKeysDown.length;
            boolean bl2 = action == 0 ? tracked && ImGuiManager.this.imguiKeysDown[keycode] : (feed = ImGuiManager.this.feedsKeyPress(keycode, mods));
            if (!feed) {
                if (ImGuiManager.this.minecraftKeys != null) {
                    ImGuiManager.this.minecraftKeys.invoke(window, keycode, scancode, action, mods);
                }
                return;
            }
            if (tracked) {
                ImGuiManager.this.imguiKeysDown[keycode] = action != 0;
            }
            super.keyCallback(window, keycode, scancode, action, mods);
        }

        public void monitorCallback(long monitor, int event) {
            super.monitorCallback(monitor, event);
            if (ImGuiManager.this.minecraftMonitors != null) {
                ImGuiManager.this.minecraftMonitors.invoke(monitor, event);
            }
        }

        public void cursorPosCallback(long window, double x, double y) {
            super.cursorPosCallback(window, x, y);
            ImGuiManager.feedScaledCursor(x, y);
        }

        protected void updateMouseData() {
            super.updateMouseData();
            long handle = class_310.method_1551().method_22683().method_4490();
            if (GLFW.glfwGetWindowAttrib((long)handle, (int)131073) == 0) {
                return;
            }
            double[] cx = new double[1];
            double[] cy = new double[1];
            GLFW.glfwGetCursorPos((long)handle, (double[])cx, (double[])cy);
            ImGuiManager.feedScaledCursor(cx[0], cy[0]);
        }
    };
    private final AnubisGl3 gl3 = new AnubisGl3();
    private final boolean[] imguiKeysDown = new boolean[349];
    private GLFWKeyCallback minecraftKeys;
    private GLFWMonitorCallback minecraftMonitors;
    private boolean initialized;
    private boolean idleSkipped;
    private Runnable activeRenderer;
    private Runnable hudRenderer;
    private BooleanSupplier hudHasWork = () -> true;
    private Runnable toastRenderer;
    private BooleanSupplier toastHasWork = () -> false;
    private Runnable overlayRenderer;
    private Runnable streamRenderer;
    private BooleanSupplier streamHasWork = () -> false;
    private BooleanSupplier overlayHasWork = () -> false;
    private Runnable alertRenderer;
    private BooleanSupplier alertHasWork = () -> false;
    private float uiScale = 1.0f;
    private float userScale = 1.0f;
    private UiFont uiFont = UiFont.GEIST;
    private UiFont hudFont = UiFont.GEIST;
    private int frameCount;
    private float frameDelta;
    private ImFont categoryFont;
    private ImFont moduleFont;
    private ImFont titleFont;
    private ImFont smallFont;
    private ImFont hudValueFont;
    private ImFont hudTextFont;
    private ImFont hudLabelFont;
    private ImFont menuFont;
    private ImFont menuTagFont;
    private ImFont menuBodyFont;
    private ImFont menuNameFont;
    private ImFont menuItemFont;
    private short[] glyphRanges;

    private static void feedScaledCursor(double x, double y) {
        float sx = ImGuiManager.pixelsPerPointX();
        float sy = ImGuiManager.pixelsPerPointY();
        if (sx == 1.0f && sy == 1.0f) {
            return;
        }
        ImGui.getIO().addMousePosEvent((float)(x * (double)sx), (float)(y * (double)sy));
    }

    private static float pixelsPerPointX() {
        class_1041 window = class_310.method_1551().method_22683();
        return window.method_4480() > 0 ? (float)window.method_4489() / (float)window.method_4480() : 1.0f;
    }

    private static float pixelsPerPointY() {
        class_1041 window = class_310.method_1551().method_22683();
        return window.method_4507() > 0 ? (float)window.method_4506() / (float)window.method_4507() : 1.0f;
    }

    private ImGuiManager() {
    }

    public static ImGuiManager getInstance() {
        return INSTANCE;
    }

    public ImFont categoryFont() {
        return this.categoryFont;
    }

    public ImFont moduleFont() {
        return this.moduleFont;
    }

    public ImFont titleFont() {
        return this.titleFont;
    }

    public ImFont smallFont() {
        return this.smallFont;
    }

    public ImFont hudValueFont() {
        return this.hudValueFont;
    }

    public ImFont hudTextFont() {
        return this.hudTextFont;
    }

    public ImFont hudLabelFont() {
        return this.hudLabelFont;
    }

    public ImFont menuFont() {
        return this.menuFont;
    }

    public ImFont menuTagFont() {
        return this.menuTagFont;
    }

    public ImFont menuBodyFont() {
        return this.menuBodyFont;
    }

    public ImFont menuNameFont() {
        return this.menuNameFont;
    }

    public ImFont menuItemFont() {
        return this.menuItemFont;
    }

    public float uiScale() {
        return this.uiScale;
    }

    public int frameCount() {
        return this.frameCount;
    }

    public float frameDelta() {
        return this.frameDelta;
    }

    public void activate(Runnable renderer) {
        this.activeRenderer = renderer;
    }

    public boolean typing() {
        return this.initialized && ImGui.getIO().getWantTextInput();
    }

    public void requestCursor(class_332 graphics) {
        class_11875 cursor;
        if (!this.initialized) {
            return;
        }
        switch (ImGui.getMouseCursor()) {
            case 1: {
                class_11875 class_118752 = class_11876.field_62453;
                break;
            }
            case 7: {
                class_11875 class_118752 = class_11876.field_62455;
                break;
            }
            case 3: {
                class_11875 class_118752 = class_11876.field_62456;
                break;
            }
            case 4: {
                class_11875 class_118752 = class_11876.field_62457;
                break;
            }
            case 2: {
                class_11875 class_118752 = class_11876.field_62458;
                break;
            }
            case 8: {
                class_11875 class_118752 = class_11876.field_62459;
                break;
            }
            default: {
                class_11875 class_118752 = cursor = null;
            }
        }
        if (cursor != null) {
            graphics.method_74037(cursor);
        }
    }

    private boolean feedsKeyPress(int keycode, int mods) {
        return this.activeRenderer != null && (keycode != 258 || (mods & 2) == 0);
    }

    public void deactivate() {
        String ini;
        this.activeRenderer = null;
        if (this.initialized && (ini = ImGui.getIO().getIniFilename()) != null) {
            ImGui.saveIniSettingsToDisk((String)ini);
        }
    }

    public void setHudRenderer(Runnable renderer, BooleanSupplier hasWork) {
        this.hudRenderer = renderer;
        this.hudHasWork = hasWork;
    }

    public void setToastRenderer(Runnable renderer, BooleanSupplier hasWork) {
        this.toastRenderer = renderer;
        this.toastHasWork = hasWork;
    }

    public void setStreamRenderer(Runnable renderer, BooleanSupplier hasWork) {
        this.streamRenderer = renderer;
        this.streamHasWork = hasWork;
    }

    public void setOverlayRenderer(Runnable renderer, BooleanSupplier hasWork) {
        this.overlayRenderer = renderer;
        this.overlayHasWork = hasWork;
    }

    public void setAlertRenderer(Runnable renderer, BooleanSupplier hasWork) {
        this.alertRenderer = renderer;
        this.alertHasWork = hasWork;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void renderIfActive() {
        // On Android/Zalith there is no GLFW window or desktop GL context — skip entirely.
        if (ZalithCompat.isMobile()) return;
        boolean stream;
        boolean hud = this.hudRenderer != null && this.hudHasWork.getAsBoolean();
        boolean toasts = this.toastRenderer != null && this.toastHasWork.getAsBoolean();
        boolean overlay = this.overlayRenderer != null && this.overlayHasWork.getAsBoolean();
        boolean alert = this.alertRenderer != null && this.alertHasWork.getAsBoolean();
        boolean bl = stream = this.streamRenderer != null && this.streamHasWork.getAsBoolean();
        if (!(this.activeRenderer != null || hud || toasts || overlay || alert || stream)) {
            if (this.initialized) {
                ImGui.getIO().clearEventsQueue();
                this.idleSkipped = true;
            }
            return;
        }
        this.initIfNeeded();
        this.rebuildFontsIfNeeded();
        if (this.idleSkipped) {
            ImGuiIO io = ImGui.getIO();
            io.clearInputKeys();
            io.clearInputMouse();
            Arrays.fill(this.imguiKeysDown, false);
            this.idleSkipped = false;
        }
        class_310 mc = class_310.method_1551();
        int w = mc.method_22683().method_4489();
        int h = mc.method_22683().method_4506();
        GL30.glBindFramebuffer((int)36160, (int)0);
        GL11.glViewport((int)0, (int)0, (int)w, (int)h);
        GlStateManager._viewport((int)0, (int)0, (int)w, (int)h);
        this.glfw.newFrame();
        this.frameDelta = Math.min(ImGui.getIO().getDeltaTime(), 0.1f);
        ImGui.getIO().setDeltaTime(this.frameDelta);
        this.updateDisplayMetrics();
        this.gl3.newFrame();
        ThemeImages.get().beginFrame();
        ImGui.newFrame();
        ++this.frameCount;
        boolean ended = false;
        try {
            GuiPalette.refresh();
            if (this.streamRenderer != null) {
                this.streamRenderer.run();
            }
            if (this.hudRenderer != null) {
                this.hudRenderer.run();
            }
            if (this.activeRenderer != null) {
                this.activeRenderer.run();
            }
            if (this.toastRenderer != null) {
                this.toastRenderer.run();
            }
            if (this.alertRenderer != null) {
                this.alertRenderer.run();
            }
            if (this.overlayRenderer != null) {
                this.overlayRenderer.run();
            }
            ImGui.render();
            ended = true;
            this.gl3.renderDrawData(ImGui.getDrawData());
        }
        finally {
            try {
                if (!ended) {
                    ImGui.endFrame();
                }
            }
            finally {
                ThemeImages.get().endFrame();
            }
        }
    }

    public void dispose() {
        if (!this.initialized || ZalithCompat.isMobile()) {
            return;
        }
        this.gl3.shutdown();
        this.glfw.shutdown();
        ImGui.destroyContext();
        this.categoryFont = null;
        this.moduleFont = null;
        this.titleFont = null;
        this.smallFont = null;
        this.hudValueFont = null;
        this.hudTextFont = null;
        this.hudLabelFont = null;
        this.menuFont = null;
        this.menuTagFont = null;
        this.menuBodyFont = null;
        this.menuNameFont = null;
        this.menuItemFont = null;
        this.initialized = false;
    }

    private void initIfNeeded() {
        if (this.initialized) {
            return;
        }
        // Mobile: GLFW and desktop OpenGL unavailable — stay uninitialised.
        if (ZalithCompat.isMobile()) {
            return;
        }
        long windowHandle = class_310.method_1551().method_22683().method_4490();
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.setIniFilename(ImGuiManager.iniPath());
        io.addConfigFlags(1);
        io.addConfigFlags(32);
        ImGui.styleColorsDark();
        this.uiScale = this.computeScale(this.uiScale);
        this.uiFont = ImGuiManager.chosenUiFont();
        this.hudFont = ImGuiManager.chosenHudFont();
        this.loadFonts(io);
        this.minecraftKeys = GLFW.glfwSetKeyCallback((long)windowHandle, null);
        GLFW.glfwSetKeyCallback((long)windowHandle, (GLFWKeyCallbackI)this.minecraftKeys);
        this.minecraftMonitors = GLFW.glfwSetMonitorCallback(null);
        GLFW.glfwSetMonitorCallback((GLFWMonitorCallbackI)this.minecraftMonitors);
        this.glfw.init(windowHandle, true);
        this.gl3.init(null);
        this.initialized = true;
    }

    private void rebuildFontsIfNeeded() {
        float scale = this.computeScale(this.uiScale);
        UiFont ui = ImGuiManager.chosenUiFont();
        UiFont hud = ImGuiManager.chosenHudFont();
        if (scale == this.uiScale && ui == this.uiFont && hud == this.hudFont) {
            return;
        }
        this.uiScale = scale;
        this.uiFont = ui;
        this.hudFont = hud;
        ImGuiIO io = ImGui.getIO();
        io.getFonts().clear();
        this.loadFonts(io);
        this.gl3.destroyFontsTexture();
        this.gl3.createFontsTexture();
    }

    private static UiFont chosenUiFont() {
        return UiFont.of(ThemeStore.get().font.get());
    }

    private static UiFont chosenHudFont() {
        return UiFont.of(HudTheme.get().font.get());
    }

    private float computeScale(float current) {
        int height = class_310.method_1551().method_22683().method_4506();
        if (height < 200) {
            return current;
        }
        if (!this.initialized || !ImGui.getIO().getMouseDown(0)) {
            this.userScale = ThemeStore.get().uiScale.get();
        }
        float raw = Math.max(0.6f, Math.min(3.5f, (float)height / 1080.0f * this.userScale));
        return (float)Math.round(raw * 20.0f) / 20.0f;
    }

    private static String iniPath() {
        try {
            Path dir = FabricLoader.getInstance().getConfigDir().resolve("anubis");
            Files.createDirectories(dir, new FileAttribute[0]);
            return dir.resolve("gui.ini").toString();
        }
        catch (IOException | RuntimeException e) {
            System.err.println("[Anubis] GUI layout will not be saved: " + e.getMessage());
            return null;
        }
    }

    private void updateDisplayMetrics() {
        class_310 client = class_310.method_1551();
        int w = Math.max(1, client.method_22683().method_4489());
        int h = Math.max(1, client.method_22683().method_4506());
        ImGuiIO io = ImGui.getIO();
        io.setDisplaySize((float)w, (float)h);
        io.setDisplayFramebufferScale(1.0f, 1.0f);
    }

    private void loadFonts(ImGuiIO io) {
        if (this.glyphRanges == null) {
            this.glyphRanges = ImGuiManager.buildGlyphRanges(io);
        }
        try {
            io.getFonts().setFreeTypeRenderer(true);
        }
        catch (Throwable t) {
            System.err.println("[Anubis] FreeType unavailable, using stb_truetype: " + t.getMessage());
        }
        if (this.buildFonts(io, this.uiFont, this.hudFont)) {
            return;
        }
        if (this.uiFont != UiFont.GEIST || this.hudFont != UiFont.GEIST) {
            System.err.println("[Anubis] Couldn't build " + this.uiFont.label + " / " + this.hudFont.label + " fonts, using Geist");
            io.getFonts().clear();
            if (this.buildFonts(io, UiFont.GEIST, UiFont.GEIST)) {
                return;
            }
        }
        throw new IllegalStateException("ImGui font atlas could not be built");
    }

    private boolean buildFonts(ImGuiIO io, UiFont ui, UiFont hud) {
        float s = this.uiScale;
        this.moduleFont = this.loadFont(io, ui, false, 16.0f * s);
        this.categoryFont = this.loadFont(io, ui, true, 17.0f * s);
        this.titleFont = this.loadFont(io, ui, true, 24.0f * s);
        this.smallFont = this.loadFont(io, ui, false, 13.0f * s);
        this.hudValueFont = this.loadFont(io, hud, true, 18.0f * s);
        this.hudTextFont = this.loadFont(io, hud, true, 17.0f * s);
        this.hudLabelFont = this.loadFont(io, hud, false, 14.0f * s);
        this.menuFont = ImGuiManager.loadFont(io, MENU_FONT_RESOURCE, 24.0f * s, this.glyphRanges);
        this.menuTagFont = ImGuiManager.loadFont(io, MENU_FONT_RESOURCE, 15.0f * s, this.glyphRanges);
        this.menuBodyFont = ImGuiManager.loadFont(io, MODULE_FONT_RESOURCE, 18.0f * s, this.glyphRanges);
        this.menuNameFont = ImGuiManager.loadFont(io, MENU_FONT_RESOURCE, 20.0f * s, this.glyphRanges);
        this.menuItemFont = ImGuiManager.loadFont(io, MENU_FONT_RESOURCE, 32.0f * s, this.glyphRanges);
        if (this.moduleFont == null && this.categoryFont == null) {
            io.getFonts().addFontDefault();
        } else if (this.moduleFont != null) {
            io.setFontDefault(this.moduleFont);
        }
        return io.getFonts().build();
    }

    private static short[] buildGlyphRanges(ImGuiIO io) {
        ImFontGlyphRangesBuilder builder = new ImFontGlyphRangesBuilder();
        builder.addRanges(io.getFonts().getGlyphRangesDefault());
        builder.addRanges(io.getFonts().getGlyphRangesVietnamese());
        builder.addText(EXTENDED_LATIN_GLYPHS);
        return builder.buildRanges();
    }

    private ImFont loadFont(ImGuiIO io, UiFont family, boolean bold, float sizePixels) {
        float size = family.size(sizePixels);
        float offsetY = family.offsetY(size);
        ImFont font = ImGuiManager.loadFont(io, family.resource(bold), size, this.glyphRanges, family.flags(), offsetY, false);
        if (font == null) {
            return family == UiFont.GEIST ? null : this.loadFont(io, UiFont.GEIST, bold, sizePixels);
        }
        if (family != UiFont.GEIST) {
            ImGuiManager.loadFont(io, UiFont.GEIST.resource(bold), family.fallbackSize(size), this.glyphRanges, UiFont.GEIST.flags(), offsetY, true);
        }
        return font;
    }

    private static ImFont loadFont(ImGuiIO io, String resourcePath, float sizePixels, short[] ranges) {
        return ImGuiManager.loadFont(io, resourcePath, sizePixels, ranges, 8, 0.0f, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive exception aggregation
     */
    private static ImFont loadFont(ImGuiIO io, String resourcePath, float sizePixels, short[] ranges, int flags, float offsetY, boolean merge) {
        try (InputStream in = ImGuiManager.class.getResourceAsStream(resourcePath);){
            ImFont imFont;
            if (in == null) {
                System.err.println("[Anubis] Font resource not found: " + resourcePath);
                ImFont imFont2 = null;
                return imFont2;
            }
            byte[] data = in.readAllBytes();
            ImFontConfig config = new ImFontConfig();
            try {
                config.setOversampleH(3);
                config.setOversampleV(1);
                config.setPixelSnapH(true);
                config.setFontBuilderFlags(flags);
                config.setGlyphOffset(0.0f, offsetY);
                config.setMergeMode(merge);
                imFont = io.getFonts().addFontFromMemoryTTF(data, (float)Math.round(sizePixels), config, ranges);
            }
            catch (Throwable throwable) {
                config.destroy();
                throw throwable;
            }
            config.destroy();
            return imFont;
        }
        catch (IOException | RuntimeException e) {
            System.err.println("[Anubis] Failed to load font " + resourcePath + ": " + e.getMessage());
            return null;
        }
    }
}

