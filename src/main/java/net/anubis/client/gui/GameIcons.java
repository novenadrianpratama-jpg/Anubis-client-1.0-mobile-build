/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1291
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_5321
 *  net.minecraft.class_6880
 */
package net.anubis.client.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.anubis.client.gui.ThemeImages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1291;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5321;
import net.minecraft.class_6880;

@Environment(value=EnvType.CLIENT)
final class GameIcons {
    private static final Map<class_2960, ThemeImages.Texture> TEXTURES = new HashMap<class_2960, ThemeImages.Texture>();
    private static final Set<class_2960> MISSING = new HashSet<class_2960>();

    private GameIcons() {
    }

    static ThemeImages.Texture effect(class_6880<class_1291> effect) {
        class_2960 id = effect.method_40230().map(class_5321::method_29177).orElse(null);
        if (id == null) {
            return null;
        }
        return GameIcons.texture(class_2960.method_60655((String)id.method_12836(), (String)("textures/mob_effect/" + id.method_12832() + ".png")));
    }

    static ThemeImages.Texture block(String name) {
        return GameIcons.texture(class_2960.method_60656((String)("textures/block/" + name + ".png")));
    }

    private static ThemeImages.Texture texture(class_2960 path) {
        ThemeImages.Texture texture = TEXTURES.get(path);
        if (texture != null || MISSING.contains(path)) {
            return texture;
        }
        try (InputStream in = class_310.method_1551().method_1478().open(path);){
            texture = ThemeImages.decode(in.readAllBytes(), path.toString(), true);
        }
        catch (IOException | RuntimeException e) {
            texture = null;
        }
        if (texture == null) {
            MISSING.add(path);
        } else {
            TEXTURES.put(path, texture);
        }
        return texture;
    }
}

