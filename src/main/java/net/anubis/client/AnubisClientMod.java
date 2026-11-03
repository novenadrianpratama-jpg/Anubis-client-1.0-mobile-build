/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.minecraft.class_11908
 *  net.minecraft.class_2960
 *  net.minecraft.class_304
 *  net.minecraft.class_304$class_11900
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_437
 *  org.lwjgl.glfw.GLFW
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client;

import net.anubis.client.addon.AddonManager;
import net.anubis.client.config.HudTheme;
import net.anubis.client.config.ModuleConfigStore;
import net.anubis.client.config.ProfileAutoLoad;
import net.anubis.client.config.ThemeStore;
import net.anubis.client.donutbets.DonutBetsFeed;
import net.anubis.client.esp.ChunkScanner;
import net.anubis.client.event.impl.PacketAppliedEvent;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.event.impl.Render2DEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.gui.BorderlessFullscreen;
import net.anubis.client.gui.HudLayoutStore;
import net.anubis.client.gui.HudRenderer;
import net.anubis.client.gui.ImGuiScreen;
import net.anubis.client.mobile.MobileGuiScreen;
import net.anubis.client.mobile.ZalithCompat;
import net.anubis.client.gui.LoadingScreens;
import net.anubis.client.gui.StaffGhost;
import net.anubis.client.gui.ToggleToasts;
import net.anubis.client.gui.mainmenu.OfficialBuildBanner;
import net.anubis.client.imgui.ImGuiManager;
import net.anubis.client.input.AnubisKeyPolling;
import net.anubis.client.input.ModuleKeybinds;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.misc.noinvleak.StreamOverlay;
import net.anubis.client.security.OfficialBuild;
import net.anubis.client.util.BlockEntityLeakTracker;
import net.anubis.client.util.CompletionChannel;
import net.anubis.client.util.combat.CombatTarget;
import net.anubis.client.util.netherite.NetheriteChunkTracker;
import net.anubis.client.util.playerbypass.PlayerBypassLightTracker;
import net.anubis.client.util.playerbypass.ProtectedChunkStore;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.class_11908;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3675;
import net.minecraft.class_437;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class AnubisClientMod
implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    public static final class_2960 SPAWNER_NOTIFY_SOUND_ID = class_2960.method_60655((String)"anubis", (String)"spawner_notify");
    public static final class_3414 SPAWNER_NOTIFY_SOUND = class_3414.method_47908((class_2960)SPAWNER_NOTIFY_SOUND_ID);
    public static final class_304.class_11900 KEY_CATEGORY = class_304.class_11900.method_74698((class_2960)class_2960.method_60655((String)"anubis", (String)"controls"));
    private static class_304 openGuiKey;
    private final ModuleKeybinds keybinds = new ModuleKeybinds();
    private static final Object LEAK_TRACKER_OWNER;
    private static final Object PLAYER_BYPASS_TRACKER_OWNER;
    private static final Object NETHERITE_TRACKER_OWNER;

    public void onInitializeClient() {
        LOGGER.info("[Anubis] Initialising");
        AddonManager.init();
        ImGuiManager.getInstance().setHudRenderer(HudRenderer.getInstance()::render, HudRenderer.getInstance()::hasWork);
        ImGuiManager.getInstance().setStreamRenderer(StreamOverlay::drawFakes, StreamOverlay::hasFakes);
        ImGuiManager.getInstance().setToastRenderer(ToggleToasts::render, ToggleToasts::hasWork);
        ImGuiManager.getInstance().setOverlayRenderer(LoadingScreens::render, LoadingScreens::hasWork);
        ImGuiManager.getInstance().setAlertRenderer(StaffGhost::render, StaffGhost::hasWork);
        Render2DEvent.TYPE.listen(StaffGhost.class, StaffGhost::renderNote);
        ChunkScanner.init();
        PacketReceiveNettyEvent.TYPE.listen(LEAK_TRACKER_OWNER, event -> BlockEntityLeakTracker.ingest(event.packet));
        CompletionChannel.install();
        DonutBetsFeed.start();
        PacketAppliedEvent.TYPE.listen(PLAYER_BYPASS_TRACKER_OWNER, event -> PlayerBypassLightTracker.onPacketApplied(event.packet));
        TickEvent.TYPE.listen(PLAYER_BYPASS_TRACKER_OWNER, event -> PlayerBypassLightTracker.onTick());
        TickEvent.TYPE.listen(BorderlessFullscreen.class, event -> BorderlessFullscreen.tick());
        CombatTarget.install();
        PacketAppliedEvent.TYPE.listen(NETHERITE_TRACKER_OWNER, event -> NetheriteChunkTracker.onPacketApplied(event.packet));
        ToggleToasts.install();
        ProfileAutoLoad.install();
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            HudLayoutStore.getInstance().save();
            ModuleConfigStore.getInstance().saveAndWait();
            ThemeStore.get().save();
            HudTheme.get().save();
            ProtectedChunkStore.get().flushAndWait();
            StreamOverlay.stop();
        });
        openGuiKey = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.anubis.open_gui", class_3675.class_307.field_1668, 344, KEY_CATEGORY));
        ModuleConfigStore.getInstance().load();
        OfficialBuild.enforceIfPending();
        OfficialBuildBanner.installVanillaTitleScreen();
        ClientTickEvents.END_CLIENT_TICK.register(client -> OfficialBuild.enforceOnFirstTick());
        AddonManager.configLoaded();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ModuleConfigStore.getInstance().applyPendingEnabled();
            while (openGuiKey.method_1436()) {
                if (ZalithCompat.isMobile()) {
                    // On Android open the touch-friendly screen instead of ImGui.
                    if (client.field_1755 instanceof MobileGuiScreen) {
                        client.method_1507(null);
                    } else {
                        client.method_1507((class_437) new MobileGuiScreen());
                    }
                } else {
                    if (client.field_1755 instanceof ImGuiScreen) {
                        client.method_1507(null);
                        continue;
                    }
                    client.method_1507((class_437) new ImGuiScreen());
                }
            }
            this.keybinds.update(ModuleRegistry.all(), AnubisClientMod::isBoundKeyDown, client.field_1755 == null && client.method_1569() && client.field_1724 != null);
            ModuleConfigStore.getInstance().tickAutosave();
        });
    }

    public static boolean isGuiKey(long window, class_11908 event) {
        return openGuiKey != null && window == class_310.method_1551().method_22683().method_4490() && openGuiKey.method_1417(event);
    }

    public static boolean toggleGuiFromKey() {
        class_310 client = class_310.method_1551();
        if (ZalithCompat.isMobile()) {
            if (client.field_1755 instanceof MobileGuiScreen) {
                client.method_1507(null);
            } else {
                client.method_1507((class_437) new MobileGuiScreen());
            }
            return true;
        }
        ImGuiScreen gui;
        if (client.field_1755 == null) {
            client.method_1507((class_437) new ImGuiScreen());
            return true;
        }
        class_437 screen = client.field_1755;
        if (screen instanceof ImGuiScreen && !(gui = (ImGuiScreen) screen).capturesKeys()) {
            client.method_1507(null);
        }
        return false;
    }

    public static void dropGuiClicks() {
        while (openGuiKey.method_1436()) {
        }
    }

    private static boolean isBoundKeyDown(int key) {
        class_310 client = class_310.method_1551();
        if (ZalithCompat.isMobile()) {
            // GLFW mouse button polling unavailable on Android — key-bound
            // modules will not receive mouse-button triggers on mobile.
            if (key >= 1000) return false;
            return AnubisKeyPolling.isKeyDown(client.method_22683(), key);
        }
        try {
            return key >= 1000
                ? GLFW.glfwGetMouseButton((long) client.method_22683().method_4490(), (int)(key - 1000)) == 1
                : AnubisKeyPolling.isKeyDown(client.method_22683(), key);
        } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
            return false;
        }
    }

    static {
        LEAK_TRACKER_OWNER = new Object();
        PLAYER_BYPASS_TRACKER_OWNER = new Object();
        NETHERITE_TRACKER_OWNER = new Object();
    }
}

