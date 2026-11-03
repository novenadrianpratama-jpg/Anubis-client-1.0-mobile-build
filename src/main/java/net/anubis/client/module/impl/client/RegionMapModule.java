/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.client;

import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class RegionMapModule
extends ClientModule {
    private final NumberSetting squareSize = this.addSetting("square-size", new NumberSetting("Cell Size", 6.0f, 3.0f, 16.0f).step(0.5f));
    private final NumberSetting playerScale = this.addSetting("player-scale", new NumberSetting("Player Scale", 1.0f, 0.5f, 3.0f).step(0.1f));
    private final NumberSetting textScale = this.addSetting("text-scale", new NumberSetting("Text Scale", 1.0f, 0.1f, 2.0f).step(0.1f));
    private final NumberSetting opacity = this.addSetting("opacity", new NumberSetting("Opacity", 1.0f, 0.1f, 1.0f).step(0.05f));
    private final BooleanSetting shadow = this.addSetting("shadow", new BooleanSetting("Shadow", true));
    private final BooleanSetting showRegionIds = this.addSetting("show-region-ids", new BooleanSetting("Show Goliath Numbers", true));
    private final BooleanSetting showLegend = this.addSetting("show-legend", new BooleanSetting("Show Legend", true));
    private final BooleanSetting showCoordinates = this.addSetting("show-coordinates", new BooleanSetting("Show Coordinates", true));
    private final ColorSetting playerIndicatorColor = this.addSetting(new ColorSetting("Player Indicator", -52686));

    public RegionMapModule() {
        super("region-map", "Region Map", "DonutSMP goliath map with directional indicator.", ModuleCategory.CLIENT);
    }

    public float squareSize() {
        return this.squareSize.get();
    }

    public float playerScale() {
        return this.playerScale.get();
    }

    public float textScale() {
        return this.textScale.get();
    }

    public float opacity() {
        return this.opacity.get();
    }

    public boolean shadow() {
        return this.shadow.get();
    }

    public boolean showRegionIds() {
        return this.showRegionIds.get();
    }

    public boolean showLegend() {
        return this.showLegend.get();
    }

    public boolean showCoordinates() {
        return this.showCoordinates.get();
    }

    public ColorSetting playerIndicatorColor() {
        return this.playerIndicatorColor;
    }
}

