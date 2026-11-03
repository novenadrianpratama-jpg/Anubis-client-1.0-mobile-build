/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11908
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 */
package net.anubis.client.gui;

import java.nio.file.Path;
import java.util.List;
import net.anubis.client.config.HudTheme;
import net.anubis.client.config.ModuleConfigStore;
import net.anubis.client.config.ThemeStore;
import net.anubis.client.gui.ClickGuiRenderer;
import net.anubis.client.imgui.ImGuiManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11908;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;

@Environment(value=EnvType.CLIENT)
public final class ImGuiScreen
extends class_437 {
    private final ClickGuiRenderer clickGui = new ClickGuiRenderer();

    public ImGuiScreen() {
        super((class_2561)class_2561.method_43470((String)"ANUBIS"));
    }

    protected void method_25426() {
        super.method_25426();
        ImGuiManager.getInstance().activate(this.clickGui::render);
    }

    public void method_25432() {
        super.method_25432();
        this.clickGui.handleEscape();
        ImGuiManager.getInstance().deactivate();
        ModuleConfigStore.getInstance().saveIfChanged();
        ThemeStore.get().save();
        HudTheme.get().save();
    }

    public void method_25420(class_332 guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (ThemeStore.get().menuBlur.get()) {
            super.method_25420(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    public void method_29638(List<Path> paths) {
        this.clickGui.onFilesDropped(paths);
    }

    public void method_25394(class_332 graphics, int mouseX, int mouseY, float tickDelta) {
        ImGuiManager.getInstance().requestCursor(graphics);
    }

    public boolean method_25404(class_11908 event) {
        if (event.comp_4795() == 256 && this.clickGui.handleEscape()) {
            return true;
        }
        return super.method_25404(event);
    }

    public boolean capturesKeys() {
        return this.clickGui.capturesKeys();
    }

    public boolean method_25421() {
        return false;
    }
}

