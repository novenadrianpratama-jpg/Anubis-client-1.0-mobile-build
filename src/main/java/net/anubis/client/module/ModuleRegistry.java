/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.anubis.client.module.BuiltinModuleProvider;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleList;
import net.anubis.client.module.ModuleProvider;
import net.anubis.client.module.ModuleRevision;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class ModuleRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final List<ModuleProvider> PROVIDERS = List.of(new BuiltinModuleProvider());
    private static final List<ClientModule> STAGED = new ArrayList<ClientModule>();
    private static final Comparator<ClientModule> ALPHABETICAL = Comparator.comparing(ClientModule::name, String.CASE_INSENSITIVE_ORDER);
    private static volatile ModuleList modules;
    private static final Map<String, ClientModule> byName;
    private static final Map<String, ClientModule> byId;
    private static volatile Reference2ReferenceOpenHashMap<Class<?>, ClientModule> byClass;
    private static volatile Reference2ReferenceOpenHashMap<ModuleCategory, ModuleList> byCategory;
    private static volatile ModuleList enabled;
    private static volatile TypeIndex types;
    private static int revision;

    private ModuleRegistry() {
    }

    public static List<ClientModule> all() {
        ModuleList m = modules;
        if (m == null) {
            ModuleRegistry.freeze();
            m = modules;
        }
        return m;
    }

    public static ClientModule byName(String name) {
        ModuleRegistry.all();
        return byName.get(name);
    }

    public static ClientModule resolve(String key) {
        ModuleRegistry.all();
        ClientModule module = byId.get(key);
        return module != null ? module : byName.get(key);
    }

    public static <T extends ClientModule> T get(Class<T> type) {
        Reference2ReferenceOpenHashMap<Class<?>, ClientModule> classes = byClass;
        if (classes == null) {
            ModuleRegistry.all();
            classes = byClass;
        }
        return (T)((ClientModule)classes.get(type));
    }

    public static List<ClientModule> inCategory(ModuleCategory category) {
        ModuleRegistry.all();
        return ModuleRegistry.category(category);
    }

    public static List<ClientModule> enabled() {
        ModuleRegistry.all();
        return enabled;
    }

    public static <T> List<T> instancesOf(Class<T> type) {
        List<Object> list;
        List<ClientModule> current = ModuleRegistry.all();
        TypeIndex index = types;
        if (index.source() != current) {
            types = index = new TypeIndex(current);
        }
        if ((list = index.lists().get(type)) == null) {
            ArrayList<ClientModule> matches = new ArrayList<ClientModule>();
            for (ClientModule module : current) {
                if (!type.isInstance(module)) continue;
                matches.add(module);
            }
            list = List.copyOf(matches);
            index.lists().put(type, list);
        }
        return list;
    }

    public static int revision() {
        ModuleRegistry.all();
        return revision;
    }

    public static synchronized boolean add(ClientModule module) {
        if (modules == null) {
            for (ClientModule staged : STAGED) {
                if (!staged.id().equals(module.id()) && !staged.name().equals(module.name())) continue;
                return false;
            }
            STAGED.add(module);
            return true;
        }
        if (byId.containsKey(module.id()) || byName.containsKey(module.name())) {
            return false;
        }
        module.captureDefaults();
        byName.put(module.name(), module);
        byId.put(module.id(), module);
        if (!byClass.containsKey(module.getClass())) {
            Reference2ReferenceOpenHashMap classes = byClass.clone();
            classes.put(module.getClass(), (Object)module);
            byClass = classes;
        }
        ModuleRegistry.replaceCategory(module.category(), ModuleRegistry.alphabetical(ModuleRegistry.category(module.category()).with(module)));
        module.registered = true;
        if (module.isEnabled()) {
            enabled = enabled.with(module);
        }
        ModuleRegistry.publish(modules.with(module));
        return true;
    }

    public static synchronized void remove(ClientModule module) {
        ModuleRegistry.all();
        if (byId.get(module.id()) != module) {
            return;
        }
        ModuleList next = modules.without(module);
        byName.remove(module.name(), module);
        byId.remove(module.id(), module);
        if (byClass.get(module.getClass()) == module) {
            Reference2ReferenceOpenHashMap classes = byClass.clone();
            classes.remove(module.getClass());
            for (ClientModule other : next) {
                if (other.getClass() != module.getClass()) continue;
                classes.put(other.getClass(), (Object)other);
                break;
            }
            byClass = classes;
        }
        ModuleRegistry.replaceCategory(module.category(), ModuleRegistry.category(module.category()).without(module));
        module.registered = false;
        enabled = enabled.without(module);
        ModuleRegistry.publish(next);
    }

    static synchronized void switched(ClientModule module) {
        if (!module.registered) {
            return;
        }
        if (module.isEnabled()) {
            if (!enabled.containsIdentity(module)) {
                enabled = enabled.with(module);
            }
        } else {
            enabled = enabled.without(module);
        }
    }

    private static synchronized void freeze() {
        if (modules != null) {
            return;
        }
        ArrayList<ClientModule> collected = new ArrayList<ClientModule>();
        HashSet<String> names = new HashSet<String>();
        HashSet<String> ids = new HashSet<String>();
        for (ModuleProvider moduleProvider : PROVIDERS) {
            int before = collected.size();
            for (ClientModule clientModule : moduleProvider.createModules()) {
                if (ids.contains(clientModule.id())) {
                    throw new IllegalStateException("Duplicate module id: " + clientModule.id());
                }
                if (!names.add(clientModule.name())) {
                    LOGGER.warn("[Anubis] Duplicate module name '{}' from provider '{}' \u2014 skipped", (Object)clientModule.name(), (Object)moduleProvider.id());
                    continue;
                }
                clientModule.captureDefaults();
                collected.add(clientModule);
                ids.add(clientModule.id());
            }
            LOGGER.info("[Anubis] Module provider '{}' contributed {} modules", (Object)moduleProvider.id(), (Object)(collected.size() - before));
        }
        int before = collected.size();
        for (ClientModule module : STAGED) {
            if (ids.contains(module.id()) || !names.add(module.name())) {
                LOGGER.warn("[Anubis] Module '{}' ({}) clashes with a provided module's name or id \u2014 skipped", (Object)module.name(), (Object)module.id());
                continue;
            }
            module.captureDefaults();
            collected.add(module);
            ids.add(module.id());
        }
        if (!STAGED.isEmpty()) {
            LOGGER.info("[Anubis] Addons contributed {} modules", (Object)(collected.size() - before));
        }
        STAGED.clear();
        Reference2ReferenceOpenHashMap reference2ReferenceOpenHashMap = new Reference2ReferenceOpenHashMap();
        LinkedHashMap<ModuleCategory, List> grouped = new LinkedHashMap<ModuleCategory, List>();
        ArrayList<ClientModule> on = new ArrayList<ClientModule>();
        for (ClientModule module : collected) {
            byName.put(module.name(), module);
            byId.put(module.id(), module);
            reference2ReferenceOpenHashMap.putIfAbsent(module.getClass(), (Object)module);
            grouped.computeIfAbsent(module.category(), category -> new ArrayList()).add(module);
            module.registered = true;
            if (!module.isEnabled()) continue;
            on.add(module);
        }
        Reference2ReferenceOpenHashMap reference2ReferenceOpenHashMap2 = new Reference2ReferenceOpenHashMap();
        grouped.forEach((category, list) -> categories.put(category, (Object)ModuleRegistry.alphabetical(list)));
        byClass = reference2ReferenceOpenHashMap;
        byCategory = reference2ReferenceOpenHashMap2;
        enabled = ModuleList.of(on);
        ModuleRegistry.publish(ModuleList.of(collected));
    }

    private static void publish(ModuleList ordered) {
        ++revision;
        ModuleRevision.changed();
        modules = ordered;
    }

    private static ModuleList alphabetical(List<ClientModule> list) {
        ArrayList<ClientModule> sorted = new ArrayList<ClientModule>(list);
        sorted.sort(ALPHABETICAL);
        return ModuleList.of(sorted);
    }

    private static ModuleList category(ModuleCategory category) {
        ModuleList list = (ModuleList)byCategory.get((Object)category);
        return list != null ? list : ModuleList.EMPTY;
    }

    private static void replaceCategory(ModuleCategory category, ModuleList list) {
        Reference2ReferenceOpenHashMap categories = byCategory.clone();
        if (list.isEmpty()) {
            categories.remove((Object)category);
        } else {
            categories.put((Object)category, (Object)list);
        }
        byCategory = categories;
    }

    static {
        byName = new ConcurrentHashMap<String, ClientModule>();
        byId = new ConcurrentHashMap<String, ClientModule>();
        enabled = ModuleList.EMPTY;
        types = new TypeIndex(List.of());
    }

    @Environment(value=EnvType.CLIENT)
    private record TypeIndex(List<ClientModule> source, Map<Class<?>, List<?>> lists) {
        TypeIndex(List<ClientModule> source) {
            this(source, new ConcurrentHashMap());
        }
    }
}

