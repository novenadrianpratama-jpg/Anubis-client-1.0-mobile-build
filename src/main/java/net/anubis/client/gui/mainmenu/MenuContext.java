/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_156
 *  net.minecraft.class_310
 *  net.minecraft.class_4189
 *  net.minecraft.class_426
 *  net.minecraft.class_429
 *  net.minecraft.class_4325
 *  net.minecraft.class_437
 *  net.minecraft.class_4749
 *  net.minecraft.class_500
 *  net.minecraft.class_526
 */
package net.anubis.client.gui.mainmenu;

import imgui.ImDrawList;
import java.util.function.Supplier;
import net.anubis.client.gui.ImGuiScreen;
import net.anubis.client.gui.mainmenu.MenuAction;
import net.anubis.client.gui.mainmenu.MenuFrame;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_156;
import net.minecraft.class_310;
import net.minecraft.class_4189;
import net.minecraft.class_426;
import net.minecraft.class_429;
import net.minecraft.class_4325;
import net.minecraft.class_437;
import net.minecraft.class_4749;
import net.minecraft.class_500;
import net.minecraft.class_526;

@Environment(value=EnvType.CLIENT)
public final class MenuContext
extends MenuFrame {
    private static final float LOOK_EASE = 2.5f;
    private final class_437 screen;
    private final Runnable openAccounts;
    private final Runnable openCredits;
    private final float[] hover = new float[MenuAction.values().length];
    private float lookX;
    private float lookY;

    public MenuContext(class_437 screen, Runnable openAccounts, Runnable openCredits) {
        this.screen = screen;
        this.openAccounts = openAccounts;
        this.openCredits = openCredits;
    }

    @Override
    public void begin(ImDrawList bg, ImDrawList fg, float width, float height, float scale, float time, float dt, float mouseX, float mouseY, boolean clicked) {
        super.begin(bg, fg, width, height, scale, time, dt, mouseX, mouseY, clicked);
        boolean onScreen = mouseX >= 0.0f && mouseY >= 0.0f && mouseX <= width && mouseY <= height;
        float targetX = onScreen ? mouseX / width * 2.0f - 1.0f : 0.0f;
        float targetY = onScreen ? mouseY / height * 2.0f - 1.0f : 0.0f;
        float k = Math.min(1.0f, dt * 2.5f);
        this.lookX += (targetX - this.lookX) * k;
        this.lookY += (targetY - this.lookY) * k;
    }

    public boolean accountsAvailable() {
        return this.openAccounts != null;
    }

    public float lookX() {
        return this.lookX;
    }

    public float lookY() {
        return this.lookY;
    }

    public boolean enabled(MenuAction action) {
        return action != MenuAction.MULTIPLAYER && action != MenuAction.REALMS || class_310.method_1551().method_29043();
    }

    public float control(MenuAction action, float x, float y, float w, float h) {
        boolean over = this.inside(x, y, w, h);
        if (!this.enabled(action)) {
            if (over) {
                this.tooltip("Multiplayer is disabled", this.mouseX(), y + h + this.px(6.0f));
            }
            return this.hover(action, false);
        }
        if (over && this.clicked()) {
            this.activate(action);
        }
        return this.hover(action, over);
    }

    public float hover(MenuAction action, boolean target) {
        float k = this.hover[action.ordinal()];
        k += ((target ? 1.0f : 0.0f) - k) * Math.min(1.0f, this.dt() * 12.0f);
        this.hover[action.ordinal()] = k;
        return k;
    }

    public void activate(MenuAction action) {
        Supplier<class_437> next;
        if (!this.enabled(action)) {
            return;
        }
        class_310 mc = class_310.method_1551();
        switch (action) {
            default: {
                throw new MatchException(null, null);
            }
            case SINGLEPLAYER: {
                Supplier<class_437> supplier = () -> new class_526(this.screen);
                break;
            }
            case MULTIPLAYER: {
                Supplier<class_437> supplier = () -> mc.field_1690.field_21840 ? new class_500(this.screen) : new class_4749(this.screen);
                break;
            }
            case REALMS: {
                Supplier<class_437> supplier = () -> new class_4325(this.screen);
                break;
            }
            case OPTIONS: {
                Supplier<class_437> supplier = () -> new class_429(this.screen, mc.field_1690);
                break;
            }
            case LANGUAGE: {
                Supplier<class_437> supplier = () -> new class_426(this.screen, mc.field_1690, mc.method_1526());
                break;
            }
            case ACCESSIBILITY: {
                Supplier<class_437> supplier = () -> new class_4189(this.screen, mc.field_1690);
                break;
            }
            case ANUBIS: {
                Supplier<class_437> supplier = ImGuiScreen::new;
                break;
            }
            case QUIT: 
            case MODS: 
            case ACCOUNTS: 
            case CREDITS: {
                Supplier<class_437> supplier = next = null;
            }
        }
        if (action == MenuAction.QUIT) {
            mc.execute(() -> ((class_310)mc).method_1592());
        } else if (action == MenuAction.ACCOUNTS) {
            if (this.openAccounts != null) {
                this.openAccounts.run();
            }
        } else if (action == MenuAction.CREDITS) {
            this.openCredits.run();
        } else if (action == MenuAction.MODS) {
            mc.execute(() -> class_156.method_668().method_60932(FabricLoader.getInstance().getGameDir().resolve("mods")));
        } else if (next != null) {
            mc.method_63588(() -> mc.method_1507((class_437)next.get()));
        }
    }
}

