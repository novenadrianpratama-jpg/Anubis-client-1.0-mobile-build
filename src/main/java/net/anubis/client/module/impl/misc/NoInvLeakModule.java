/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Platform
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_1661
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_746
 *  org.joml.Vector2f
 */
package net.anubis.client.module.impl.misc;

import com.example.client.mixin.AccessorCreativeSlotWrapper;
import com.sun.jna.Platform;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.config.ModuleConfigStore;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.misc.noinvleak.FakeLoadout;
import net.anubis.client.module.impl.misc.noinvleak.StreamOverlay;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1661;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_746;
import org.joml.Vector2f;

@Environment(value=EnvType.CLIENT)
public final class NoInvLeakModule
extends ClientModule {
    private static final String STYLE_TOTEM = "Totem";
    private static final String STYLE_RANDOM = "Random";
    private static final String STYLE_EMPTY = "Empty";
    private static final int OFFHAND_SEED = 10;
    private static volatile NoInvLeakModule active;
    private static class_1799 totem;
    private static boolean bareModel;
    private static final Vector2f POINT;
    private final ModeSetting style = this.addSetting("style", new ModeSetting("Style", "Totem", "Totem", "Random", "Empty"));
    private final BooleanSetting fillEmpty = this.addSetting("fill-empty", new BooleanSetting("Fill Empty Slots", true));
    private final BooleanSetting streamOnly = this.addSetting("stream-only", new BooleanSetting("Stream Only", false));
    private final BooleanSetting borderless = this.addSetting("borderless-fullscreen", new BooleanSetting("Borderless Fullscreen", true));
    private final BooleanSetting tooltips = this.addSetting("hide-tooltips", new BooleanSetting("Hide Tooltips", true));
    private final BooleanSetting itemName = this.addSetting("hide-item-name", new BooleanSetting("Hide Item Name", true));
    private final BooleanSetting selectedSlot = this.addSetting("hide-selected-slot", new BooleanSetting("Hide Selected Slot", true));
    private final BooleanSetting hud = this.addSetting("anubis-hud", new BooleanSetting("Anubis HUD", true));
    private final FakeLoadout loadout = new FakeLoadout();

    public NoInvLeakModule() {
        super("no-inv-leak", "No Inv Leak", "Hides your items on screen for screen shares. Stream Only keeps them visible to you alone.", ModuleCategory.MISC);
        this.fillEmpty.visibleWhen(() -> !this.style.is(STYLE_EMPTY));
        this.streamOnly.visibleWhen(Platform::isWindows);
        this.streamOnly.onChange(() -> {
            if (this.streamOnly.get()) {
                StreamOverlay.retry();
            } else {
                StreamOverlay.stop();
            }
        });
        this.borderless.visibleWhen(() -> Platform.isWindows() && this.streamOnly.get());
    }

    public static boolean wantsBorderless() {
        NoInvLeakModule module = ModuleRegistry.get(NoInvLeakModule.class);
        if (module == null || !module.streamOnly.get() || !module.borderless.get()) {
            return false;
        }
        return module.isEnabled() || ModuleConfigStore.getInstance().enablesOnLoad(module);
    }

    @Override
    protected void onEnable() {
        this.loadout.reseed();
        StreamOverlay.retry();
        active = this;
    }

    @Override
    protected void onDisable() {
        active = null;
        StreamOverlay.stop();
    }

    public static class_1799 hotbar(class_1799 stack, int x, int y, int seed) {
        int slot;
        NoInvLeakModule module = active;
        if (module == null || stack == null) {
            return stack;
        }
        int n = seed == 10 ? 40 : (slot = seed >= 1 && seed <= 9 ? seed - 1 : 41);
        if (module.streamOverlaying() && StreamOverlay.recordsHotbar()) {
            StreamOverlay.hotbar(slot, x, y, stack);
            return class_1799.field_8037;
        }
        return module.shown(stack, slot);
    }

    public static class_1799 container(class_1799 stack, class_1735 slot, int x, int y, boolean hovered) {
        NoInvLeakModule module = active;
        if (module == null || stack == null) {
            return stack;
        }
        int index = NoInvLeakModule.inventoryIndex(slot);
        if (module.streamOverlaying()) {
            StreamOverlay.container(index, x, y, stack, hovered);
            return class_1799.field_8037;
        }
        return module.shown(stack, index);
    }

    private static int inventoryIndex(class_1735 slot) {
        class_1735 class_17352;
        if (slot instanceof AccessorCreativeSlotWrapper) {
            AccessorCreativeSlotWrapper wrapper = (AccessorCreativeSlotWrapper)slot;
            class_17352 = wrapper.anubis$target();
        } else {
            class_17352 = slot;
        }
        class_1735 real = class_17352;
        return real.method_34266();
    }

    public static class_1799 dragged(class_1799 stack) {
        NoInvLeakModule module = active;
        if (module == null || stack == null || stack.method_7960()) {
            return stack;
        }
        return module.streamOverlaying() ? class_1799.field_8037 : module.fake(stack, 41);
    }

    public static boolean hidesModelOf(class_1309 entity) {
        return active != null && entity != null && entity == class_310.method_1551().field_1724;
    }

    public static void bareModel(boolean on) {
        bareModel = on;
    }

    public static class_1799 modelItem(class_1309 entity, class_1799 stack) {
        if (!bareModel || stack == null || stack.method_7960() || entity != class_310.method_1551().field_1724) {
            return stack;
        }
        return class_1799.field_8037;
    }

    public static int screenX(class_332 guiGraphics, int localX, int localY, int fallback) {
        if (guiGraphics == null || active == null) {
            return fallback;
        }
        guiGraphics.method_51448().transformPosition((float)localX, (float)localY, POINT);
        return Math.round(NoInvLeakModule.POINT.x);
    }

    public static int screenY(class_332 guiGraphics, int localX, int localY, int fallback) {
        if (guiGraphics == null || active == null) {
            return fallback;
        }
        guiGraphics.method_51448().transformPosition((float)localX, (float)localY, POINT);
        return Math.round(NoInvLeakModule.POINT.y);
    }

    public static class_1799 carried(class_1799 stack, int x, int y) {
        NoInvLeakModule module = active;
        if (module == null || stack == null || stack.method_7960()) {
            return stack;
        }
        if (module.streamOverlaying()) {
            StreamOverlay.carried(stack, x, y);
            return class_1799.field_8037;
        }
        return module.fake(stack, 41);
    }

    public static class_1799 shownInHud(class_1799 stack, class_1304 slot) {
        NoInvLeakModule module = active;
        if (module == null || !module.hud.get() || stack == null || stack.method_7960()) {
            return stack;
        }
        return module.fake(stack, NoInvLeakModule.inventorySlot(slot));
    }

    public static class_1799 fakeFor(int slot) {
        NoInvLeakModule module = active;
        return module == null ? class_1799.field_8037 : module.fake(class_1799.field_8037, slot);
    }

    public static boolean fillsEmpty() {
        NoInvLeakModule module = active;
        return module != null && module.fillEmpty.get() && !module.style.is(STYLE_EMPTY);
    }

    public static boolean overlaying() {
        NoInvLeakModule module = active;
        return module != null && module.streamOnly.get() && StreamOverlay.usable();
    }

    private boolean streamOverlaying() {
        return this.streamOnly.get() && StreamOverlay.usable();
    }

    public static boolean ownSlot(class_1735 slot) {
        return active != null && slot != null && slot.field_7871 instanceof class_1661;
    }

    public static boolean hidesTooltips() {
        NoInvLeakModule module = active;
        return module != null && module.tooltips.get();
    }

    public static boolean hidesItemName() {
        NoInvLeakModule module = active;
        return module != null && module.itemName.get();
    }

    public static boolean hidesSelectedSlot() {
        NoInvLeakModule module = active;
        return module != null && module.selectedSlot.get();
    }

    public static boolean overlayWanted() {
        NoInvLeakModule module = active;
        return module != null && module.streamOnly.get();
    }

    public static void streamNotice(String headline, String detail) {
        NoInvLeakModule module = active;
        if (module != null) {
            ModuleChat.send((ClientModule)module, ModuleChat.body().warn(headline).text(detail));
        }
    }

    public static boolean opaqueCells() {
        NoInvLeakModule module = active;
        return module != null && !module.style.is(STYLE_EMPTY);
    }

    private class_1799 shown(class_1799 stack, int slot) {
        if (stack.method_7960() && !this.fillEmpty.get()) {
            return stack;
        }
        return this.fake(stack, slot);
    }

    private class_1799 fake(class_1799 stack, int slot) {
        if (this.style.is(STYLE_EMPTY)) {
            return class_1799.field_8037;
        }
        if (this.style.is(STYLE_RANDOM)) {
            return this.loadout.get(slot);
        }
        class_1799 stand = totem;
        if (stand == null) {
            totem = stand = new class_1799((class_1935)class_1802.field_8288);
        }
        return stand;
    }

    private static int inventorySlot(class_1304 slot) {
        return switch (slot) {
            case class_1304.field_6169 -> 39;
            case class_1304.field_6174 -> 38;
            case class_1304.field_6172 -> 37;
            case class_1304.field_6166 -> 36;
            case class_1304.field_6171 -> 40;
            case class_1304.field_6173 -> {
                class_746 player = class_310.method_1551().field_1724;
                if (player != null) {
                    yield player.method_31548().method_67532();
                }
                yield 41;
            }
            default -> 41;
        };
    }

    static {
        POINT = new Vector2f();
    }
}

