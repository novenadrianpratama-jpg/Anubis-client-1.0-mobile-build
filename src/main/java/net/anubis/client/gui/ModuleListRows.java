/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRevision;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
final class ModuleListRows {
    private static final float SNAP = 0.005f;
    private static final Comparator<Row> WIDEST_FIRST = Comparator.comparingDouble(row -> -row.nameW).thenComparing(row -> row.module.name());
    private static final Comparator<Row> WIDEST_LAST = WIDEST_FIRST.reversed();
    private final List<Row> rows = new ArrayList<Row>();
    private final List<Row> view = Collections.unmodifiableList(this.rows);
    private List<ClientModule> seenEnabled;
    private long seenRevision = Long.MIN_VALUE;
    private boolean seenEditMode;
    private boolean unsorted;
    private boolean sortedBottomFirst;

    ModuleListRows() {
    }

    void update(List<ClientModule> enabled, boolean editMode, float dt) {
        long revision = ModuleRevision.current();
        if (enabled != this.seenEnabled || revision != this.seenRevision || editMode != this.seenEditMode) {
            this.seenEnabled = enabled;
            this.seenRevision = revision;
            this.seenEditMode = editMode;
            this.rows.removeIf(row -> !ModuleListRows.listed(row.module, editMode) || row.module.isEnabled() && !enabled.contains(row.module));
            for (ClientModule module : enabled) {
                if (!ModuleListRows.listed(module, editMode) || this.hasRow(module)) continue;
                this.rows.add(new Row(module));
                this.unsorted = true;
            }
        }
        float k = 1.0f - (float)Math.exp(-12.0f * dt);
        for (int i = this.rows.size() - 1; i >= 0; --i) {
            float target;
            Row row2 = this.rows.get(i);
            float f = target = row2.module.isEnabled() ? 1.0f : 0.0f;
            if (row2.anim == target) continue;
            float next = row2.anim + (target - row2.anim) * k;
            if (Math.abs(next - target) < 0.005f) {
                next = target;
            }
            row2.anim = next;
            if (next != 0.0f) continue;
            this.rows.remove(i);
        }
    }

    <F> List<Row> ordered(F font, int px, TextWidth<F> width, boolean bottomFirst) {
        for (int i = 0; i < this.rows.size(); ++i) {
            Row row = this.rows.get(i);
            if (row.measuredPx == px && row.measuredFont == font) continue;
            row.nameW = width.of(font, px, row.module.name());
            row.measuredFont = font;
            row.measuredPx = px;
            this.unsorted = true;
        }
        if (this.unsorted || bottomFirst != this.sortedBottomFirst) {
            this.rows.sort(bottomFirst ? WIDEST_LAST : WIDEST_FIRST);
            this.unsorted = false;
            this.sortedBottomFirst = bottomFirst;
        }
        return this.view;
    }

    private static boolean listed(ClientModule module, boolean editMode) {
        return module.category() != ModuleCategory.CLIENT && (editMode || module.showInHud() == null || module.showInHud().get());
    }

    private boolean hasRow(ClientModule module) {
        for (int i = 0; i < this.rows.size(); ++i) {
            if (this.rows.get((int)i).module != module) continue;
            return true;
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    static final class Row {
        private final ClientModule module;
        private float anim;
        private float nameW;
        private Object measuredFont;
        private int measuredPx = -1;

        private Row(ClientModule module) {
            this.module = module;
        }

        ClientModule module() {
            return this.module;
        }

        float anim() {
            return this.anim;
        }

        float nameW() {
            return this.nameW;
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface TextWidth<F> {
        public float of(F var1, int var2, String var3);
    }
}

