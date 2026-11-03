/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.textures.TextureFormat
 *  imgui.ImDrawList
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10444
 *  net.minecraft.class_1047
 *  net.minecraft.class_1058
 *  net.minecraft.class_10725
 *  net.minecraft.class_10868
 *  net.minecraft.class_11515
 *  net.minecraft.class_1747
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_4538
 *  net.minecraft.class_4696
 *  net.minecraft.class_811
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.gui;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import imgui.ImDrawList;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.ArrayList;
import java.util.List;
import net.anubis.client.gui.AnubisWidgets;
import net.anubis.client.gui.ItemIcons;
import net.anubis.client.imgui.ImGuiManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10444;
import net.minecraft.class_1047;
import net.minecraft.class_1058;
import net.minecraft.class_10725;
import net.minecraft.class_10868;
import net.minecraft.class_11515;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_4538;
import net.minecraft.class_4696;
import net.minecraft.class_811;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
final class BlockIcons {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final float SPRITE_TEXELS = 16.0f;
    private static final int PAGE_SIDE = 1024;
    private static final int RENDERS_PER_FRAME = 48;
    private static final int MAX_SHEETS = 4;
    private static final int MAX_LOSSES = 3;
    private static final int WATER_RGB = 4159204;
    private static final Int2ObjectOpenHashMap<Sheet> SHEETS = new Int2ObjectOpenHashMap();
    private static final ReferenceOpenHashSet<GpuTexture> CRISP = new ReferenceOpenHashSet();
    private static GpuTexture atlas;
    private static final class_10444 ITEM;
    private static int budgetFrame;
    private static int rendersLeft;
    private static boolean itemsFailed;
    private static int auditedFrame;
    private static int losses;

    private BlockIcons() {
    }

    static void validate() {
        GpuTexture current;
        try {
            current = class_310.method_1551().method_72703().method_73025(class_10725.field_56382).method_68004();
        }
        catch (RuntimeException notLoaded) {
            current = null;
        }
        if (current != atlas) {
            atlas = current;
            BlockIcons.clearSheets();
            return;
        }
        int frame = ImGuiManager.getInstance().frameCount();
        if (frame - auditedFrame < 30) {
            return;
        }
        auditedFrame = frame;
        if (BlockIcons.intact()) {
            return;
        }
        BlockIcons.clearSheets();
        if (++losses < 3) {
            LOGGER.warn("Block icon pages were lost; drawing them again");
        } else {
            itemsFailed = true;
            LOGGER.warn("Block icon pages keep getting lost; using block textures instead");
        }
    }

    private static boolean intact() {
        for (Sheet sheet : SHEETS.values()) {
            if (sheet.depth != null && !ItemIcons.alive(sheet.depth)) {
                return false;
            }
            for (GpuTexture page : sheet.pages) {
                if (ItemIcons.alive(page)) continue;
                return false;
            }
        }
        return true;
    }

    static void forget() {
        BlockIcons.clearSheets();
    }

    static float size(float target) {
        return (float)Math.max(1, (int)(target / 16.0f)) * 16.0f;
    }

    static void draw(ImDrawList dl, class_2248 block, float x, float y, float size) {
        int px = Math.round(size);
        Sheet sheet = BlockIcons.sheet(px);
        Icon icon = (Icon)sheet.icons.get((Object)block);
        if (icon == null) {
            icon = BlockIcons.resolve(sheet, block);
            if (icon == null) {
                return;
            }
            sheet.icons.put((Object)block, (Object)icon);
        }
        float x0 = Math.round(x);
        float y0 = Math.round(y);
        if (icon.texture != 0) {
            dl.addImage((long)icon.texture, x0, y0, x0 + (float)px, y0 + (float)px, icon.u0, icon.v0, icon.u1, icon.v1, AnubisWidgets.c(icon.tint));
        } else {
            float r = (float)px * 0.2f;
            dl.addRectFilled(x0, y0, x0 + (float)px, y0 + (float)px, AnubisWidgets.c(-12240588), r);
            dl.addRect(x0, y0, x0 + (float)px, y0 + (float)px, AnubisWidgets.c(0x1FFFFFFF), r, 0, 1.0f);
        }
    }

    private static Sheet sheet(int px) {
        Sheet sheet = (Sheet)SHEETS.get(px);
        if (sheet == null) {
            if (SHEETS.size() >= 4) {
                BlockIcons.clearSheets();
            }
            sheet = new Sheet(px);
            SHEETS.put(px, (Object)sheet);
        }
        return sheet;
    }

    private static void clearSheets() {
        for (Sheet sheet : SHEETS.values()) {
            sheet.close();
        }
        SHEETS.clear();
        CRISP.clear();
        ItemIcons.forgetVerdict();
    }

    private static Icon resolve(Sheet sheet, class_2248 block) {
        class_1792 item = block.method_8389();
        class_2248 shown = block;
        if (item == class_1802.field_8162 && (item = BlockIcons.standIn(block)) instanceof class_1747) {
            class_1747 placed = (class_1747)item;
            shown = placed.method_7711();
        }
        if (item == class_1802.field_8162 || itemsFailed || sheet.cell > 256 || !ItemIcons.works()) {
            return BlockIcons.sprite(block);
        }
        if (!BlockIcons.takeRender()) {
            return null;
        }
        class_310 mc = class_310.method_1551();
        try {
            mc.method_65386().method_65598(ITEM, new class_1799((class_1935)item), class_811.field_4317, (class_1937)mc.field_1687, null, 0);
        }
        catch (RuntimeException brokenModel) {
            ITEM.method_65605();
            return BlockIcons.sprite(block);
        }
        if (ITEM.method_65606()) {
            return BlockIcons.sprite(block);
        }
        Icon icon = new Icon();
        boolean seeThrough = class_4696.method_23679((class_2680)shown.method_9564()) == class_11515.field_60926;
        try {
            BlockIcons.renderItem(sheet, icon, seeThrough);
            return icon;
        }
        catch (RuntimeException e) {
            itemsFailed = true;
            LOGGER.warn("Block item icons failed, using block textures instead", (Throwable)e);
            return BlockIcons.sprite(block);
        }
    }

    private static class_1792 standIn(class_2248 block) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 != null) {
            try {
                class_1799 picked = block.method_9564().method_65171((class_4538)mc.field_1687, class_2338.field_10980, false);
                if (!picked.method_7960()) {
                    return picked.method_7909();
                }
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
        }
        if (block == class_2246.field_10008) {
            return class_1802.field_8249;
        }
        if (block == class_2246.field_10027) {
            return class_1802.field_8449;
        }
        if (block == class_2246.field_10613) {
            return class_1802.field_8634;
        }
        return class_1802.field_8162;
    }

    private static boolean takeRender() {
        int frame = ImGuiManager.getInstance().frameCount();
        if (frame != budgetFrame) {
            budgetFrame = frame;
            rendersLeft = 48;
        }
        if (rendersLeft <= 0) {
            return false;
        }
        --rendersLeft;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void renderItem(Sheet sheet, Icon icon, boolean seeThrough) {
        int cell = sheet.cell;
        int perRow = 1024 / cell;
        if (sheet.pages.isEmpty() || sheet.used >= perRow * perRow) {
            BlockIcons.addPage(sheet);
        }
        int slot = sheet.used++;
        int x = slot % perRow * cell;
        int y = slot / perRow * cell;
        int glY = 1024 - y - cell;
        GpuTexture page = sheet.pages.get(sheet.pages.size() - 1);
        GpuTextureView view = sheet.views.get(sheet.views.size() - 1);
        try {
            ItemIcons.render(ITEM, page, view, sheet.depth, sheet.depthView, x, y, cell);
        }
        finally {
            ITEM.method_65605();
        }
        if (seeThrough) {
            ItemIcons.straighten(page, x, glY, cell);
        }
        icon.texture = ((class_10868)page).method_68427();
        icon.u0 = (float)x / 1024.0f;
        icon.u1 = (float)(x + cell) / 1024.0f;
        icon.v0 = (float)(1024 - y) / 1024.0f;
        icon.v1 = (float)glY / 1024.0f;
    }

    private static void addPage(Sheet sheet) {
        GpuDevice device = RenderSystem.getDevice();
        if (sheet.depth == null) {
            sheet.depth = ItemIcons.create("Anubis block icons depth", 8, TextureFormat.DEPTH32, 1024);
            sheet.depthView = device.createTextureView(sheet.depth);
        }
        GpuTexture page = ItemIcons.create("Anubis block icons " + sheet.cell + "px", 15, TextureFormat.RGBA8, 1024);
        sheet.pages.add(page);
        sheet.views.add(device.createTextureView(page));
        sheet.used = 0;
        device.createCommandEncoder().clearColorAndDepthTextures(page, 0, sheet.depth, 1.0);
    }

    private static Icon sprite(class_2248 block) {
        Icon icon = new Icon();
        class_310 mc = class_310.method_1551();
        class_2680 state = block.method_9564();
        try {
            class_10868 gl;
            class_1058 sprite = mc.method_1554().method_4743().method_3339(state);
            if (class_1047.method_4539().equals((Object)sprite.method_45851().method_45816())) {
                return icon;
            }
            GpuTexture gpuTexture = mc.method_1531().method_4619(sprite.method_45852()).method_68004();
            if (!(gpuTexture instanceof class_10868) || (gl = (class_10868)gpuTexture).isClosed()) {
                return icon;
            }
            if (CRISP.add((Object)gl)) {
                ItemIcons.crisp(gl);
            }
            icon.texture = gl.method_68427();
            icon.u0 = sprite.method_4594();
            icon.v0 = sprite.method_4593();
            icon.u1 = sprite.method_4577();
            icon.v1 = sprite.method_4575();
        }
        catch (RuntimeException unavailable) {
            return icon;
        }
        if (!state.method_27852(class_2246.field_10219)) {
            int rgb = -1;
            try {
                rgb = mc.method_1505().method_1697(state, null, null, 0);
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
            if (rgb == -1 && (state.method_27852(class_2246.field_10382) || state.method_27852(class_2246.field_10422))) {
                rgb = 4159204;
            }
            if (rgb != -1) {
                icon.tint = 0xFF000000 | (rgb & 0xFF) << 16 | rgb & 0xFF00 | rgb >> 16 & 0xFF;
            }
        }
        return icon;
    }

    static {
        ITEM = new class_10444();
        budgetFrame = -1;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Sheet {
        final int cell;
        final Reference2ObjectOpenHashMap<class_2248, Icon> icons = new Reference2ObjectOpenHashMap();
        final List<GpuTexture> pages = new ArrayList<GpuTexture>();
        final List<GpuTextureView> views = new ArrayList<GpuTextureView>();
        GpuTexture depth;
        GpuTextureView depthView;
        int used;

        Sheet(int cell) {
            this.cell = cell;
        }

        void close() {
            for (GpuTextureView view : this.views) {
                view.close();
            }
            for (GpuTexture page : this.pages) {
                ItemIcons.dispose(page);
            }
            if (this.depthView != null) {
                this.depthView.close();
            }
            ItemIcons.dispose(this.depth);
            this.views.clear();
            this.pages.clear();
            this.depth = null;
            this.depthView = null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Icon {
        int texture;
        float u0;
        float v0;
        float u1;
        float v1;
        int tint = -1;

        private Icon() {
        }
    }
}

