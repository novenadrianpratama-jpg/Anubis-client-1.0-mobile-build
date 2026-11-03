/*
 * Anubis Client — touch-friendly module GUI for Zalith/Android.
 * Replaces the GLFW/ImGui ClickGui with a Minecraft-native Screen
 * that works entirely through the game's built-in input pipeline.
 */
package net.anubis.client.mobile;

import java.util.ArrayList;
import java.util.List;

import net.anubis.client.config.ModuleConfigStore;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;   // Text
import net.minecraft.class_310;    // MinecraftClient
import net.minecraft.class_332;    // DrawContext
import net.minecraft.class_437;    // Screen

@Environment(value = EnvType.CLIENT)
public final class MobileGuiScreen extends class_437 {

    // ── layout constants ──────────────────────────────────────────────────
    private static final int BG_COLOR        = 0xE0101018;
    private static final int PANEL_COLOR     = 0xE0181820;
    private static final int ACCENT          = 0xFF7B6BFF;
    private static final int TAB_ACTIVE_BG   = 0xFF1E1E2E;
    private static final int TAB_INACTIVE_BG = 0xFF14141E;
    private static final int ROW_ODD         = 0xFF111119;
    private static final int ROW_EVEN        = 0xFF0D0D15;
    private static final int ENABLED_COLOR   = 0xFF7B6BFF;
    private static final int DISABLED_COLOR  = 0xFF444460;
    private static final int TEXT_COLOR      = 0xFFEEEEFF;
    private static final int TEXT_DIM        = 0xFF888899;
    private static final int TAB_H           = 32;
    private static final int ROW_H           = 36;
    private static final int PADDING         = 8;
    private static final int PILL_W          = 44;
    private static final int PILL_H          = 20;

    // ── state ─────────────────────────────────────────────────────────────
    private int selectedCategoryIndex = 0;
    private float scrollY = 0f;
    private float scrollVelocity = 0f;
    private double lastDragY = -1;
    private boolean dragging = false;
    private int guiWidth;
    private int guiHeight;
    private List<ModuleCategory> categories;
    private List<ClientModule> visibleModules;

    public MobileGuiScreen() {
        super(class_2561.method_43470("Anubis Mobile"));
    }

    // ── Screen lifecycle ──────────────────────────────────────────────────
    @Override
    protected void method_25426() { // init()
        super.method_25426();
        guiWidth  = this.field_22790;  // width
        guiHeight = this.field_22791;  // height
        categories = ModuleCategory.values();
        refreshModuleList();
    }

    @Override
    public void method_25432() { // removed()
        super.method_25432();
        ModuleConfigStore.getInstance().saveIfChanged();
    }

    @Override
    public boolean method_25421() { // shouldCloseOnEsc()
        return true;
    }

    // ── Rendering ─────────────────────────────────────────────────────────
    @Override
    public void method_25394(class_332 ctx, int mouseX, int mouseY, float delta) { // render()
        // Apply fling scroll
        if (!dragging && Math.abs(scrollVelocity) > 0.5f) {
            scrollY += scrollVelocity;
            scrollVelocity *= 0.88f;
            clampScroll();
        }

        // Full-screen dim
        fill(ctx, 0, 0, guiWidth, guiHeight, BG_COLOR);

        // Category tab bar
        drawCategoryBar(ctx, mouseX, mouseY);

        // Module list below tab bar
        int listTop = TAB_H;
        drawModuleList(ctx, mouseX, mouseY, listTop);
    }

    // ── Category tabs ─────────────────────────────────────────────────────
    private void drawCategoryBar(class_332 ctx, int mouseX, int mouseY) {
        if (categories == null) return;
        fill(ctx, 0, 0, guiWidth, TAB_H, PANEL_COLOR);
        int tabW = Math.max(80, guiWidth / Math.max(1, categories.size()));
        for (int i = 0; i < categories.size(); i++) {
            int x = i * tabW;
            int bg = (i == selectedCategoryIndex) ? TAB_ACTIVE_BG : TAB_INACTIVE_BG;
            fill(ctx, x, 0, x + tabW, TAB_H, bg);
            if (i == selectedCategoryIndex) {
                fill(ctx, x, TAB_H - 2, x + tabW, TAB_H, ACCENT);
            }
            String label = categories.get(i).displayName();
            if (label.length() > 8) label = label.substring(0, 7) + "…";
            int tx = x + (tabW - textWidth(label)) / 2;
            int ty = (TAB_H - 8) / 2;
            int col = (i == selectedCategoryIndex) ? TEXT_COLOR : TEXT_DIM;
            drawText(ctx, label, tx, ty, col);
        }
    }

    // ── Module list ───────────────────────────────────────────────────────
    private void drawModuleList(class_332 ctx, int mouseX, int mouseY, int listTop) {
        if (visibleModules == null) return;
        int listH = guiHeight - listTop;
        int totalH = visibleModules.size() * ROW_H;
        // Scissor manually by only drawing rows within [listTop, guiHeight]
        for (int i = 0; i < visibleModules.size(); i++) {
            int rowY = listTop + i * ROW_H - (int) scrollY;
            if (rowY + ROW_H < listTop || rowY > guiHeight) continue;
            ClientModule m = visibleModules.get(i);
            int rowBg = (i % 2 == 0) ? ROW_EVEN : ROW_ODD;
            fill(ctx, 0, rowY, guiWidth, rowY + ROW_H, rowBg);
            // Highlight hovered row
            if (mouseX >= 0 && mouseX < guiWidth &&
                mouseY >= rowY && mouseY < rowY + ROW_H) {
                fill(ctx, 0, rowY, guiWidth, rowY + ROW_H, 0x22FFFFFF);
            }
            // Module name
            int textY = rowY + (ROW_H - 8) / 2;
            drawText(ctx, m.name(), PADDING, textY, m.isEnabled() ? TEXT_COLOR : TEXT_DIM);
            // Toggle pill
            int pillX = guiWidth - PILL_W - PADDING;
            int pillY = rowY + (ROW_H - PILL_H) / 2;
            int pillBg = m.isEnabled() ? ENABLED_COLOR : DISABLED_COLOR;
            fill(ctx, pillX, pillY, pillX + PILL_W, pillY + PILL_H, pillBg);
            String state = m.isEnabled() ? "ON" : "OFF";
            int stateX = pillX + (PILL_W - textWidth(state)) / 2;
            int stateY = pillY + (PILL_H - 8) / 2;
            drawText(ctx, state, stateX, stateY, 0xFFFFFFFF);
        }
        // Scrollbar
        if (totalH > listH) {
            int sbW = 4;
            int sbX = guiWidth - sbW;
            float ratio = (float) listH / totalH;
            int sbH = (int) (listH * ratio);
            float scrollRatio = scrollY / (totalH - listH);
            int sbY = listTop + (int) ((listH - sbH) * scrollRatio);
            fill(ctx, sbX, sbY, sbX + sbW, sbY + sbH, 0x88FFFFFF);
        }
    }

    // ── Input ─────────────────────────────────────────────────────────────
    @Override
    public boolean method_25406(double mouseX, double mouseY, int button) { // mouseClicked()
        int mx = (int) mouseX;
        int my = (int) mouseY;
        // Category tab click
        if (my < TAB_H && categories != null) {
            int tabW = Math.max(80, guiWidth / Math.max(1, categories.size()));
            int idx = mx / tabW;
            if (idx >= 0 && idx < categories.size() && idx != selectedCategoryIndex) {
                selectedCategoryIndex = idx;
                scrollY = 0;
                scrollVelocity = 0;
                refreshModuleList();
                return true;
            }
        }
        // Start drag tracking for scroll
        if (my >= TAB_H) {
            dragging = true;
            scrollVelocity = 0;
            lastDragY = mouseY;
        }
        return super.method_25406(mouseX, mouseY, button);
    }

    @Override
    public boolean method_25410(double mouseX, double mouseY, int button) { // mouseReleased()
        int mx = (int) mouseX;
        int my = (int) mouseY;
        if (dragging && my >= TAB_H && visibleModules != null) {
            // Detect if this was a tap (small drag) vs a scroll (large drag)
            double dragDist = Math.abs(lastDragY - mouseY);
            if (dragDist < 6) {
                // Tap — toggle module
                int rowIndex = (int) ((my - TAB_H + scrollY) / ROW_H);
                if (rowIndex >= 0 && rowIndex < visibleModules.size()) {
                    visibleModules.get(rowIndex).toggle();
                }
            }
        }
        dragging = false;
        lastDragY = -1;
        return super.method_25410(mouseX, mouseY, button);
    }

    @Override
    public boolean method_25413(double mouseX, double mouseY, int button,
                                 double dragX, double dragY) { // mouseDragged()
        if (dragging && mouseY >= TAB_H) {
            float delta = (float) (lastDragY - mouseY);
            scrollY += delta;
            scrollVelocity = delta;
            lastDragY = mouseY;
            clampScroll();
            return true;
        }
        return super.method_25413(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean method_25408(double mouseX, double mouseY, double amount) { // mouseScrolled()
        scrollY -= (float) amount * ROW_H;
        scrollVelocity = 0;
        clampScroll();
        return true;
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private void refreshModuleList() {
        visibleModules = new ArrayList<>();
        if (categories == null || categories.isEmpty()) return;
        ModuleCategory cat = categories.get(selectedCategoryIndex);
        for (ClientModule m : ModuleRegistry.all()) {
            if (m.category() == cat) {
                visibleModules.add(m);
            }
        }
        clampScroll();
    }

    private void clampScroll() {
        if (visibleModules == null) { scrollY = 0; return; }
        int listH   = guiHeight - TAB_H;
        int totalH  = visibleModules.size() * ROW_H;
        float maxScroll = Math.max(0, totalH - listH);
        if (scrollY < 0) { scrollY = 0; scrollVelocity = 0; }
        if (scrollY > maxScroll) { scrollY = maxScroll; scrollVelocity = 0; }
    }

    // ── DrawContext wrappers (avoids direct intermediary calls in hot code) ──
    private static void fill(class_332 ctx, int x1, int y1, int x2, int y2, int color) {
        ctx.method_25292(x1, y1, x2, y2, color);
    }

    private void drawText(class_332 ctx, String text, int x, int y, int color) {
        // class_332.method_25303(TextRenderer, Text, x, y, color, shadow)
        ctx.method_25303(
            class_310.method_1551().field_1719, // textRenderer
            class_2561.method_43470(text),
            x, y, color, false);
    }

    private int textWidth(String text) {
        return class_310.method_1551().field_1719.method_1727(text);
    }
}
