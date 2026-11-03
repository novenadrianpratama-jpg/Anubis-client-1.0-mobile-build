/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1068
 *  net.minecraft.class_2561
 *  net.minecraft.class_2703
 *  net.minecraft.class_2703$class_5893
 *  net.minecraft.class_310
 *  net.minecraft.class_320
 *  net.minecraft.class_5348
 *  net.minecraft.class_634
 *  net.minecraft.class_640
 *  net.minecraft.class_8685
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.misc;

import com.mojang.authlib.GameProfile;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.misc.SkinChangeModule;
import net.anubis.client.module.impl.misc.nameprotect.AliasPolicy;
import net.anubis.client.module.impl.misc.nameprotect.NameMatcher;
import net.anubis.client.module.impl.misc.nameprotect.ProtectedText;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.StringSetting;
import net.anubis.client.util.NameFilter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1068;
import net.minecraft.class_2561;
import net.minecraft.class_2703;
import net.minecraft.class_310;
import net.minecraft.class_320;
import net.minecraft.class_5348;
import net.minecraft.class_634;
import net.minecraft.class_640;
import net.minecraft.class_8685;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class NameProtectModule
extends ClientModule {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final String OTHERS_KEEP = "Keep";
    private static final String OTHERS_SAME = "Same Label";
    private static final String OTHERS_NUMBERED = "Numbered";
    private static final String DEFAULT_SELF_ALIAS = "You";
    private static final int DEPARTED_MEMORY = 256;
    private static final int SEEN_MEMORY = 8192;
    private static final int MIN_OTHER_NAME_LENGTH = 3;
    private static final int NPC_UUID_VERSION = 2;
    private static final int CHAT_REFRESH_INTERVAL_TICKS = 20;
    private static final int REFUSAL_NOTICE_DELAY_TICKS = 30;
    private static final int RECENT_CHAT_LINES = 100;
    private static final int DISPLAY_CACHE_SLOTS = 64;
    private static volatile NameMatcher active;
    private static volatile NameProtectModule running;
    private static volatile int generation;
    private static volatile boolean failureLogged;
    private static final Object[] cacheInputs;
    private static final Object[] cacheOutputs;
    private static NameMatcher cacheMatcher;
    private static final String[] recentChat;
    private static int recentChatNext;
    private final StringSetting selfAlias = this.addSetting("self-alias", new StringSetting("Your Alias", "You"));
    private final StringSetting extraNames = this.addSetting("extra-names", new StringSetting("Extra Names", ""));
    private final ModeSetting others = this.addSetting("others", new ModeSetting("Others", "Keep", "Keep", "Same Label", "Numbered"));
    private final ModeSetting othersLabel = this.addSetting("others-label", new ModeSetting("Others Label", "Player", "Player", "Anonymous", "Unknown"));
    private final LinkedHashMap<String, String> knownOthers = new LinkedHashMap();
    private final Map<String, Integer> numbers = new HashMap<String, Integer>();
    private int nextNumber = 1;
    private final LinkedHashSet<String> seenSkeletons = new LinkedHashSet();
    private WeakReference<class_634> trackedConnection = new WeakReference<Object>(null);
    private volatile boolean dirty = true;
    private volatile boolean settingsEdited;
    private int builtListSize = -1;
    private String builtProfileName;
    private NameMatcher matcher = NameMatcher.EMPTY;
    private String refusedAlias;
    private AliasPolicy.Refusal refusalReason;
    private String noticedAlias;
    private int refusalNoticeDelay;
    private boolean chatRefreshPending;
    private int chatRefreshCooldown;

    public NameProtectModule() {
        super("name-protect", "Name Protect", "Shows aliases instead of player names on your screen.", ModuleCategory.MISC);
        this.usesNotifications();
        this.othersLabel.visibleWhen(() -> !this.others.is(OTHERS_KEEP));
        this.extraNames.privateValue();
        this.selfAlias.onChange(this::markEdited);
        this.extraNames.onChange(this::markEdited);
        this.others.onChange(this::markEdited);
        this.othersLabel.onChange(this::markEdited);
    }

    public static class_2561 protect(class_2561 text) {
        class_2561 shown;
        Object object;
        NameMatcher current = active;
        if (current == null || text == null) {
            return text;
        }
        boolean cache = NameProtectModule.onRenderThread();
        if (cache && (object = NameProtectModule.cached(current, text)) instanceof class_2561) {
            class_2561 hit = (class_2561)object;
            return hit;
        }
        try {
            shown = ProtectedText.rewrite(current, text);
        }
        catch (RuntimeException failure) {
            NameProtectModule.reportFailure(failure);
            return text;
        }
        if (cache) {
            NameProtectModule.remember(text, shown);
        }
        return shown;
    }

    public static boolean hidesName(String name) {
        NameMatcher current = active;
        return current != null && name != null && current.aliasAt(name, 0, name.length()) != null;
    }

    public static class_8685 hiddenHead(UUID profileId, class_8685 real) {
        class_8685 worn;
        SkinChangeModule skins = ModuleRegistry.get(SkinChangeModule.class);
        if (skins != null && skins.isEnabled() && (worn = skins.skinFor(profileId, real)) != real) {
            return worn;
        }
        return class_1068.method_4648((UUID)profileId);
    }

    public static class_5348 protectText(class_5348 text) {
        class_5348 shown;
        Object object;
        NameMatcher current = active;
        if (current == null || text == null) {
            return text;
        }
        boolean cache = NameProtectModule.onRenderThread();
        if (cache && (object = NameProtectModule.cached(current, text)) instanceof class_5348) {
            class_5348 hit = (class_5348)object;
            return hit;
        }
        try {
            shown = ProtectedText.rewrite(current, text);
        }
        catch (RuntimeException failure) {
            NameProtectModule.reportFailure(failure);
            return text;
        }
        if (cache) {
            NameProtectModule.remember(text, shown);
        }
        return shown;
    }

    public static class_5348 protectChatLine(class_5348 text) {
        NameMatcher current = active;
        if (current == null || text == null) {
            return text;
        }
        try {
            if (NameProtectModule.onRenderThread()) {
                NameProtectModule.recentChat[NameProtectModule.recentChatNext] = text.getString();
                recentChatNext = (recentChatNext + 1) % 100;
            }
            return ProtectedText.rewrite(current, text);
        }
        catch (RuntimeException failure) {
            NameProtectModule.reportFailure(failure);
            return text;
        }
    }

    public static int generation() {
        return generation;
    }

    public static void onPlayerInfoApplied(class_2703 packet) {
        NameProtectModule module = running;
        if (module == null || packet == null) {
            return;
        }
        EnumSet actions = packet.method_46327();
        if (!actions.contains(class_2703.class_5893.field_29136) && !actions.contains(class_2703.class_5893.field_40700)) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (!mc.method_18854()) {
            return;
        }
        module.syncConnection(mc);
        module.rebuild();
    }

    private static String filterName(String name) {
        NameMatcher current = active;
        return current != null ? current.replace(name) : name;
    }

    private static void reportFailure(RuntimeException failure) {
        if (failureLogged) {
            return;
        }
        failureLogged = true;
        LOGGER.error("Name Protect could not rewrite a text; showing it unchanged", (Throwable)failure);
    }

    private static boolean onRenderThread() {
        class_310 mc = class_310.method_1551();
        return mc != null && mc.method_18854();
    }

    private static Object cached(NameMatcher current, Object input) {
        if (cacheMatcher != current) {
            NameProtectModule.clearDisplayCache();
            cacheMatcher = current;
            return null;
        }
        int slot = System.identityHashCode(input) & 0x3F;
        return cacheInputs[slot] == input ? cacheOutputs[slot] : null;
    }

    private static void remember(Object input, Object output) {
        int slot = System.identityHashCode(input) & 0x3F;
        NameProtectModule.cacheInputs[slot] = input;
        NameProtectModule.cacheOutputs[slot] = output;
    }

    private static void clearDisplayCache() {
        Arrays.fill(cacheInputs, null);
        Arrays.fill(cacheOutputs, null);
        cacheMatcher = null;
    }

    @Override
    protected void onEnable() {
        running = this;
        class_310 mc = class_310.method_1551();
        this.syncConnection(mc);
        this.settingsEdited = false;
        this.rebuild();
        NameFilter.set(NameProtectModule::filterName);
        this.chatRefreshPending = true;
        this.chatRefreshCooldown = 0;
        this.on(TickEvent.TYPE, Stage.PRE, this::onTick);
    }

    @Override
    protected void onDisable() {
        running = null;
        active = null;
        ++generation;
        this.matcher = NameMatcher.EMPTY;
        NameFilter.set(null);
        this.chatRefreshPending = false;
        this.dirty = true;
        this.refusedAlias = null;
        this.noticedAlias = null;
        class_310 mc = class_310.method_1551();
        if (mc == null) {
            return;
        }
        mc.execute(() -> {
            NameProtectModule.clearDisplayCache();
            Arrays.fill(recentChat, null);
            if (mc.field_1705 != null) {
                mc.field_1705.method_1743().method_1817();
            }
        });
    }

    private void markEdited() {
        this.settingsEdited = true;
        this.dirty = true;
    }

    private void onTick(TickEvent event) {
        class_310 mc = class_310.method_1551();
        this.syncConnection(mc);
        if (NameProtectModule.listSize(mc.method_1562()) != this.builtListSize) {
            this.dirty = true;
        }
        if (!Objects.equals(NameProtectModule.profileName(mc), this.builtProfileName)) {
            this.dirty = true;
        }
        if (this.dirty) {
            this.rebuild();
        }
        this.announceRefusal(mc);
        if (this.chatRefreshCooldown > 0) {
            --this.chatRefreshCooldown;
        }
        if (this.chatRefreshPending && this.chatRefreshCooldown == 0 && !NameProtectModule.chatOpen(mc) && mc.field_1705 != null) {
            this.chatRefreshPending = false;
            this.chatRefreshCooldown = 20;
            mc.field_1705.method_1743().method_1817();
        }
    }

    private void syncConnection(class_310 mc) {
        class_634 connection;
        class_634 class_6342 = connection = mc != null ? mc.method_1562() : null;
        if (connection == this.trackedConnection.get()) {
            return;
        }
        this.trackedConnection = new WeakReference<class_634>(connection);
        this.knownOthers.clear();
        this.numbers.clear();
        this.nextNumber = 1;
        this.builtListSize = -1;
        this.dirty = true;
    }

    private void rebuild() {
        this.dirty = false;
        boolean edited = this.settingsEdited;
        this.settingsEdited = false;
        class_310 mc = class_310.method_1551();
        class_634 connection = mc != null ? mc.method_1562() : null;
        class_320 user = mc != null ? mc.method_1548() : null;
        String accountName = NameMatcher.nameCore(user != null ? user.method_1676() : null);
        this.builtProfileName = NameProtectModule.profileName(mc);
        String profile = NameMatcher.nameCore(this.builtProfileName);
        this.builtListSize = NameProtectModule.listSize(connection);
        ArrayList<String> extras = new ArrayList<String>();
        for (String raw : this.extraNames.get().split("[,;\\s]+")) {
            String extra = NameMatcher.nameCore(raw);
            if (extra == null) continue;
            extras.add(extra);
        }
        ArrayList<String> ownNames = new ArrayList<String>(extras.size() + 2);
        if (accountName != null) {
            ownNames.add(accountName);
        }
        if (profile != null) {
            ownNames.add(profile);
        }
        ownNames.addAll(extras);
        this.rememberOnline(connection, accountName, profile);
        String ownAlias = this.resolveOwnAlias(extras);
        LinkedHashMap<String, String> table = new LinkedHashMap<String, String>();
        for (String own : ownNames) {
            NameProtectModule.put(table, own, ownAlias);
        }
        if (!this.others.is(OTHERS_KEEP)) {
            this.trackOthers(connection, ownNames);
            String label = this.othersLabel.get();
            boolean numbered = this.others.is(OTHERS_NUMBERED);
            for (Map.Entry<String, String> known : this.knownOthers.entrySet()) {
                NameProtectModule.put(table, known.getValue(), (String)(numbered ? label + " " + String.valueOf(this.numbers.get(known.getKey())) : label));
            }
        }
        this.publish(new NameMatcher(table), edited);
    }

    private void publish(NameMatcher next, boolean edited) {
        NameMatcher previous = this.matcher;
        this.matcher = next;
        active = next;
        if (next.sameMapping(previous)) {
            return;
        }
        ++generation;
        if (edited || NameProtectModule.recentChatMentions(next.newNamesSince(previous))) {
            this.chatRefreshPending = true;
        }
    }

    private static boolean recentChatMentions(NameMatcher fresh) {
        if (fresh.isEmpty()) {
            return false;
        }
        for (String line : recentChat) {
            if (!fresh.mentionsAny(line)) continue;
            return true;
        }
        return false;
    }

    private String resolveOwnAlias(List<String> extras) {
        String alias = AliasPolicy.sanitize(this.selfAlias.get());
        if (alias.isEmpty() || alias.equalsIgnoreCase(DEFAULT_SELF_ALIAS)) {
            this.refusedAlias = null;
            return alias.isEmpty() ? DEFAULT_SELF_ALIAS : alias;
        }
        HashSet<String> extraSkeletons = new HashSet<String>();
        for (String extra : extras) {
            extraSkeletons.add(AliasPolicy.skeleton(extra));
        }
        AliasPolicy.Refusal refusal = AliasPolicy.check(alias, skeleton -> this.seenSkeletons.contains(skeleton) || extraSkeletons.contains(skeleton));
        if (refusal == null) {
            this.refusedAlias = null;
            return alias;
        }
        if (!alias.equals(this.refusedAlias)) {
            this.refusedAlias = alias;
            this.refusalNoticeDelay = 30;
        }
        this.refusalReason = refusal;
        return DEFAULT_SELF_ALIAS;
    }

    private void announceRefusal(class_310 mc) {
        if (this.refusedAlias == null) {
            this.noticedAlias = null;
            return;
        }
        if (this.refusedAlias.equals(this.noticedAlias)) {
            return;
        }
        if (this.refusalNoticeDelay > 0) {
            --this.refusalNoticeDelay;
            return;
        }
        if (mc.field_1724 == null) {
            return;
        }
        this.noticedAlias = this.refusedAlias;
        ModuleChat.send((ClientModule)this, ModuleChat.body().text("Alias ").name(this.refusedAlias).text(" ").danger("not used").text(": " + this.refusalReason.reason() + ", showing ").name(DEFAULT_SELF_ALIAS).text(" instead"));
    }

    private void rememberOnline(class_634 connection, String accountName, String profile) {
        if (connection == null) {
            return;
        }
        for (class_640 info : connection.method_2880()) {
            GameProfile gameProfile = info.method_2966();
            String name = NameMatcher.nameCore(gameProfile != null ? gameProfile.name() : null);
            if (name == null || name.equalsIgnoreCase(accountName) || name.equalsIgnoreCase(profile)) continue;
            String skeleton = AliasPolicy.skeleton(name);
            this.seenSkeletons.remove(skeleton);
            this.seenSkeletons.add(skeleton);
        }
        Iterator oldest = this.seenSkeletons.iterator();
        while (this.seenSkeletons.size() > 8192 && oldest.hasNext()) {
            oldest.next();
            oldest.remove();
        }
    }

    private void trackOthers(class_634 connection, List<String> own) {
        if (connection == null) {
            return;
        }
        int online = 0;
        for (class_640 info : connection.method_45732()) {
            String name;
            GameProfile profile = info.method_2966();
            if (profile == null || profile.id() != null && profile.id().version() == 2 || !NameProtectModule.isLikelyPlayer(name = NameMatcher.nameCore(profile.name())) || NameProtectModule.isOwn(name, own)) continue;
            String key = name.toLowerCase(Locale.ROOT);
            this.knownOthers.remove(key);
            this.knownOthers.put(key, name);
            this.numbers.computeIfAbsent(key, k -> this.nextNumber++);
            ++online;
        }
        Iterator<String> oldest = this.knownOthers.keySet().iterator();
        while (this.knownOthers.size() > online + 256 && oldest.hasNext()) {
            this.numbers.remove(oldest.next());
            oldest.remove();
        }
    }

    private static void put(Map<String, String> table, String rawName, String alias) {
        String name = NameMatcher.nameCore(rawName);
        if (name != null && !alias.equalsIgnoreCase(name)) {
            table.putIfAbsent(name, alias);
        }
    }

    private static boolean isLikelyPlayer(String name) {
        if (name == null || name.length() < 3) {
            return false;
        }
        for (int i = 0; i < name.length(); ++i) {
            if (!Character.isLetter(name.charAt(i))) continue;
            return true;
        }
        return false;
    }

    private static boolean isOwn(String name, List<String> own) {
        for (String ownName : own) {
            if (!ownName.equalsIgnoreCase(name)) continue;
            return true;
        }
        return false;
    }

    private static String profileName(class_310 mc) {
        return mc != null && mc.field_1724 != null ? mc.field_1724.method_7334().name() : null;
    }

    private static int listSize(class_634 connection) {
        return connection != null ? connection.method_45732().size() : -1;
    }

    private static boolean chatOpen(class_310 mc) {
        return mc.field_1705 != null && mc.field_1705.method_1743().method_1819();
    }

    static {
        cacheInputs = new Object[64];
        cacheOutputs = new Object[64];
        recentChat = new String[100];
    }
}

