/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11414
 *  net.minecraft.class_11419
 *  net.minecraft.class_11424
 *  net.minecraft.class_11426
 *  net.minecraft.class_11430
 *  net.minecraft.class_11432
 *  net.minecraft.class_11434
 *  net.minecraft.class_11435
 *  net.minecraft.class_11437
 *  net.minecraft.class_11438
 *  net.minecraft.class_11444
 *  net.minecraft.class_11473
 *  net.minecraft.class_11519
 *  net.minecraft.class_11521$class_11522
 *  net.minecraft.class_11722
 *  net.minecraft.class_1799
 *  net.minecraft.class_2481
 *  net.minecraft.class_2520
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_5251
 */
package net.anubis.client.module.impl.donutmacros;

import com.example.client.mixin.AccessorOrderDialogScreen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.anubis.client.module.impl.donutmacros.ServerText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11414;
import net.minecraft.class_11419;
import net.minecraft.class_11424;
import net.minecraft.class_11426;
import net.minecraft.class_11430;
import net.minecraft.class_11432;
import net.minecraft.class_11434;
import net.minecraft.class_11435;
import net.minecraft.class_11437;
import net.minecraft.class_11438;
import net.minecraft.class_11444;
import net.minecraft.class_11473;
import net.minecraft.class_11519;
import net.minecraft.class_11521;
import net.minecraft.class_11722;
import net.minecraft.class_1799;
import net.minecraft.class_2481;
import net.minecraft.class_2520;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_5251;

@Environment(value=EnvType.CLIENT)
final class Dialogs {
    private Dialogs() {
    }

    static View current(class_310 mc) {
        class_437 class_4372 = mc.field_1755;
        if (!(class_4372 instanceof class_11473)) {
            return null;
        }
        class_11473 screen = (class_11473)class_4372;
        class_11419 dialog = ((AccessorOrderDialogScreen)screen).anubis$dialog();
        if (dialog == null) {
            return null;
        }
        ArrayList raw = new ArrayList();
        if (dialog instanceof class_11426) {
            class_11426 multi = (class_11426)dialog;
            raw.addAll(multi.comp_4317());
        }
        if (dialog instanceof class_11430) {
            class_11430 simple = (class_11430)dialog;
            raw.addAll(simple.method_71698());
        }
        if (dialog instanceof class_11414) {
            class_11414 list = (class_11414)dialog;
            list.comp_4395().ifPresent(raw::add);
        }
        ArrayList<Button> buttons = new ArrayList<Button>(raw.size());
        for (class_11519 button : raw) {
            class_2561 label = button.comp_4390().comp_4298();
            String text = Dialogs.visibleText(label);
            buttons.add(new Button(button, ServerText.normalize(text), ServerText.key(text), label));
        }
        ArrayList<String> body = new ArrayList<String>();
        ArrayList<class_1799> items = new ArrayList<class_1799>();
        for (class_11432 part : dialog.comp_4305().comp_4304()) {
            if (part instanceof class_11435) {
                class_11435 message2 = (class_11435)part;
                body.add(ServerText.normalize(Dialogs.visibleText(message2.comp_4330())));
            }
            if (!(part instanceof class_11434)) continue;
            class_11434 item = (class_11434)part;
            items.add(item.comp_4324());
            item.comp_4325().ifPresent(message -> body.add(ServerText.normalize(Dialogs.visibleText(message.comp_4330()))));
        }
        ArrayList<String> textInputs = new ArrayList<String>();
        for (class_11424 input : dialog.comp_4305().comp_4394()) {
            if (!(input.comp_4313() instanceof class_11444)) continue;
            textInputs.add(input.comp_4312());
        }
        return new View(dialog, ServerText.normalize(Dialogs.visibleText(dialog.comp_4305().comp_4301())), buttons, body, textInputs, items);
    }

    static String visibleText(class_2561 component) {
        StringBuilder out = new StringBuilder();
        Dialogs.appendVisible(component, out);
        return out.toString();
    }

    private static void appendVisible(class_2561 component, StringBuilder out) {
        if (!(component.method_10851() instanceof class_11722)) {
            component.method_10851().method_27659(text -> {
                out.append(text);
                return Optional.empty();
            });
        }
        for (class_2561 sibling : component.method_10855()) {
            Dialogs.appendVisible(sibling, out);
        }
    }

    static boolean press(class_310 mc, View view, Button button, Map<String, String> text) {
        class_437 class_4372 = mc.field_1755;
        if (!(class_4372 instanceof class_11473)) {
            return false;
        }
        class_11473 screen = (class_11473)class_4372;
        if (((AccessorOrderDialogScreen)screen).anubis$dialog() != view.dialog()) {
            return false;
        }
        HashMap<String, class_11521.class_11522> values = new HashMap<String, class_11521.class_11522>();
        for (class_11424 input : view.dialog().comp_4305().comp_4394()) {
            class_11438 class_114382 = input.comp_4313();
            if (class_114382 instanceof class_11444) {
                class_11444 textInput = (class_11444)class_114382;
                values.put(input.comp_4312(), class_11521.class_11522.method_72070((String)text.getOrDefault(input.comp_4312(), textInput.comp_4354())));
                continue;
            }
            class_114382 = input.comp_4313();
            if (!(class_114382 instanceof class_11437)) continue;
            class_11437 bool = (class_11437)class_114382;
            values.put(input.comp_4312(), Dialogs.booleanValue(bool));
        }
        Optional event = button.raw().comp_4391().flatMap(action -> action.method_72068(values));
        screen.method_72141(event);
        return true;
    }

    static void close(class_310 mc) {
        class_437 class_4372 = mc.field_1755;
        if (class_4372 instanceof class_11473) {
            class_11473 screen = (class_11473)class_4372;
            screen.method_25419();
        }
    }

    private static class_11521.class_11522 booleanValue(final class_11437 input) {
        final boolean value = input.comp_4333();
        return new class_11521.class_11522(){

            public String method_72069() {
                return value ? input.comp_4334() : input.comp_4335();
            }

            public class_2520 method_72073() {
                return class_2481.method_23234((boolean)value);
            }
        };
    }

    private static boolean isGreen(int rgb) {
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        return g >= 192 && r < 144 && b < 144;
    }

    @Environment(value=EnvType.CLIENT)
    record Button(class_11519 raw, String label, String key, class_2561 component) {
        boolean green() {
            boolean[] found = new boolean[]{false};
            this.component.method_27658((style, text) -> {
                class_5251 color = style.method_10973();
                if (!text.isBlank() && color != null && Dialogs.isGreen(color.method_27716())) {
                    found[0] = true;
                }
                return Optional.empty();
            }, class_2583.field_24360);
            return found[0];
        }
    }

    @Environment(value=EnvType.CLIENT)
    record View(class_11419 dialog, String title, List<Button> buttons, List<String> body, List<String> textInputs, List<class_1799> items) {
        Button find(Predicate<Button> test) {
            for (Button button : this.buttons) {
                if (!test.test(button)) continue;
                return button;
            }
            return null;
        }

        Button labelled(String ... words) {
            String wanted;
            for (String word : words) {
                wanted = ServerText.key(word);
                Button exact = this.find(button -> button.key().equals(wanted));
                if (exact == null) continue;
                return exact;
            }
            for (String word : words) {
                wanted = ServerText.key(word);
                Button partial = this.find(button -> button.key().contains(wanted));
                if (partial == null) continue;
                return partial;
            }
            return null;
        }

        boolean titled(String text) {
            return this.title.contains(text);
        }

        String describe() {
            ArrayList<String> labels = new ArrayList<String>();
            for (Button button : this.buttons) {
                if (!button.label().isBlank()) {
                    labels.add(button.label());
                }
                if (labels.size() < 12) continue;
                break;
            }
            return "'" + this.title + "' buttons: " + (labels.isEmpty() ? "(none)" : String.join((CharSequence)", ", labels));
        }
    }
}

