/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.suggestion.Suggestion
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2596
 *  net.minecraft.class_2639
 *  net.minecraft.class_2805
 *  net.minecraft.class_310
 *  net.minecraft.class_634
 */
package net.anubis.client.util;

import com.mojang.brigadier.suggestion.Suggestion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.event.impl.PacketSendEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2596;
import net.minecraft.class_2639;
import net.minecraft.class_2805;
import net.minecraft.class_310;
import net.minecraft.class_634;

@Environment(value=EnvType.CLIENT)
public final class CompletionChannel {
    public static final CompletionChannel INSTANCE = new CompletionChannel();
    private static final int WIRE_ID = 0;
    private static final long FORGET_MS = 15000L;
    private static final long FOREIGN_MS = 4000L;
    private static final Object FOREIGN = new Object();
    private final Object lock = new Object();
    private final LinkedList<Request> line = new LinkedList();
    private final Map<Integer, Request> byKey = new HashMap<Integer, Request>();
    private Object connection;
    private int nextKey = 1;
    private boolean sending;
    private boolean installed;

    private CompletionChannel() {
    }

    public static void install() {
        if (CompletionChannel.INSTANCE.installed) {
            return;
        }
        CompletionChannel.INSTANCE.installed = true;
        PacketSendEvent.TYPE.listen(CompletionChannel.class, event -> INSTANCE.outgoing(event.packet));
        PacketReceiveNettyEvent.TYPE.listen(CompletionChannel.class, event -> {
            if (INSTANCE.ingest(event.packet)) {
                event.setCancelled(true);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int fire(Object owner, String partial, Form form) {
        Request request;
        class_634 net = class_310.method_1551().method_1562();
        if (net == null || owner == null || partial == null) {
            return -1;
        }
        Object object = this.lock;
        synchronized (object) {
            this.follow(net);
            int key = this.nextKey++;
            if (this.nextKey == Integer.MAX_VALUE) {
                this.nextKey = 1;
            }
            request = new Request(key, owner, CompletionChannel.want(partial), System.currentTimeMillis(), form == Form.BOTH ? 2 : 1);
            this.line.addLast(request);
            this.byKey.put(key, request);
        }
        this.sending = true;
        try {
            if (form != Form.BARE) {
                net.method_52787((class_2596)new class_2805(0, "/" + partial));
            }
            if (form != Form.SLASH) {
                net.method_52787((class_2596)new class_2805(0, partial));
            }
        }
        catch (RuntimeException e) {
            Object object2 = this.lock;
            synchronized (object2) {
                this.line.remove(request);
                this.byKey.remove(request.key);
            }
            int n = -1;
            return n;
        }
        finally {
            this.sending = false;
        }
        return request.key;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> poll(int key) {
        Object object = this.lock;
        synchronized (object) {
            Request request = this.byKey.get(key);
            return request == null || request.forgotten ? null : request.names;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void forget(int key) {
        Object object = this.lock;
        synchronized (object) {
            Request request = this.byKey.remove(key);
            if (request == null) {
                return;
            }
            request.forgotten = true;
            if (!request.waiting()) {
                this.line.remove(request);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void prune(long now) {
        Object object = this.lock;
        synchronized (object) {
            this.expire(now);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear(Object owner) {
        Object object = this.lock;
        synchronized (object) {
            Iterator it = this.line.iterator();
            while (it.hasNext()) {
                Request request = (Request)it.next();
                if (request.owner != owner) continue;
                request.forgotten = true;
                this.byKey.remove(request.key);
                if (request.waiting()) continue;
                it.remove();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void outgoing(class_2596<?> packet) {
        if (this.sending || !(packet instanceof class_2805)) {
            return;
        }
        class_2805 request = (class_2805)packet;
        class_634 net = class_310.method_1551().method_1562();
        Object object = this.lock;
        synchronized (object) {
            this.follow(net);
            this.line.addLast(new Request(0, FOREIGN, CompletionChannel.want(request.method_12148()), System.currentTimeMillis(), 1));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean ingest(class_2596<?> packet) {
        if (!(packet instanceof class_2639)) {
            return false;
        }
        class_2639 reply = (class_2639)packet;
        ArrayList<String> names = new ArrayList<String>();
        for (Suggestion suggestion : reply.method_11397().getList()) {
            String text = suggestion.getText();
            if (text == null || text.isBlank()) continue;
            names.add(text.trim());
        }
        String first = names.isEmpty() ? "" : CompletionChannel.core((String)names.get(0));
        String last = names.isEmpty() ? "" : CompletionChannel.core((String)names.get(names.size() - 1));
        Object object = this.lock;
        synchronized (object) {
            Request request2;
            this.expire(System.currentTimeMillis());
            Request target = null;
            for (Request request2 : this.line) {
                if (!request2.waiting() || !names.isEmpty() && (!first.startsWith(request2.want) || !last.startsWith(request2.want))) continue;
                target = request2;
                break;
            }
            if (target == null) {
                return false;
            }
            Iterator it = this.line.iterator();
            while (it.hasNext() && (request2 = (Request)it.next()) != target) {
                if (!request2.waiting()) continue;
                request2.answered = request2.expected;
                if (request2.owner != FOREIGN && !request2.forgotten) continue;
                it.remove();
            }
            ++target.answered;
            if (!names.isEmpty() && (target.names == null || target.names.isEmpty())) {
                target.names = List.copyOf(names);
            } else if (target.names == null && !target.waiting()) {
                target.names = List.of();
            }
            if (!target.waiting() && (target.owner == FOREIGN || target.forgotten)) {
                this.line.remove(target);
            }
            return target.owner != FOREIGN;
        }
    }

    private void follow(class_634 net) {
        if (net == this.connection) {
            return;
        }
        this.connection = net;
        this.line.clear();
        this.byKey.clear();
    }

    private void expire(long now) {
        Iterator it = this.line.iterator();
        while (it.hasNext()) {
            Request request = (Request)it.next();
            long age = now - request.at;
            if (age <= 15000L && (request.owner != FOREIGN || age <= 4000L)) continue;
            it.remove();
            if (request.owner == FOREIGN) continue;
            this.byKey.remove(request.key, request);
        }
    }

    static String want(String partial) {
        int space;
        String text;
        String string = text = partial == null ? "" : partial.trim();
        if (text.startsWith("/")) {
            text = text.substring(1);
        }
        text = (space = text.lastIndexOf(32)) >= 0 ? text.substring(space + 1) : "";
        return CompletionChannel.stripDots(text).toLowerCase(Locale.ROOT);
    }

    public static String core(String raw) {
        String text = raw.replaceAll("\u00a7.", "").trim();
        int space = text.lastIndexOf(32);
        if (space >= 0) {
            text = text.substring(space + 1);
        }
        text = text.replaceAll("^[\"']|[\"']$", "");
        return CompletionChannel.stripDots(text).toLowerCase(Locale.ROOT);
    }

    private static String stripDots(String text) {
        int i;
        for (i = 0; i < text.length() && text.charAt(i) == '.'; ++i) {
        }
        return text.substring(i);
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Request {
        final int key;
        final Object owner;
        final String want;
        final long at;
        final int expected;
        int answered;
        List<String> names;
        boolean forgotten;

        Request(int key, Object owner, String want, long at, int expected) {
            this.key = key;
            this.owner = owner;
            this.want = want;
            this.at = at;
            this.expected = expected;
        }

        boolean waiting() {
            return this.answered < this.expected;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Form {
        BOTH,
        SLASH,
        BARE;

    }
}

