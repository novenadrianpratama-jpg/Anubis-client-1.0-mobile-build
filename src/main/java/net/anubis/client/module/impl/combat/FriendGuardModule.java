/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.combat;

import net.anubis.client.chat.ModuleChat;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class FriendGuardModule
extends ClientModule {
    private static final long HINT_COOLDOWN_MS = 1500L;
    private static volatile FriendGuardModule active;
    private long lastHintAt;

    public FriendGuardModule() {
        super("friend-guard", "Friend Guard", "Stops you from hitting players on your friends list.", ModuleCategory.COMBAT);
        this.usesNotifications();
    }

    @Override
    protected void onEnable() {
        this.lastHintAt = 0L;
        active = this;
    }

    @Override
    protected void onDisable() {
        active = null;
        this.lastHintAt = 0L;
    }

    public static boolean intercepts(class_1657 attacker, class_1297 target) {
        class_1657 friend;
        FriendGuardModule guard = active;
        if (guard == null || !(target instanceof class_1657) || (friend = (class_1657)target) == attacker) {
            return false;
        }
        if (attacker != class_310.method_1551().field_1724 || !MeleeAttacks.isFriend((class_1297)friend)) {
            return false;
        }
        guard.hint(friend);
        return true;
    }

    public static boolean interceptsStab(class_746 player) {
        FriendGuardModule guard = active;
        if (guard == null || player == null) {
            return false;
        }
        class_1297 friend = MeleeAttacks.stabTarget(player, entity -> {
            class_1657 other;
            return entity instanceof class_1657 && MeleeAttacks.isFriend((class_1297)(other = (class_1657)entity));
        });
        if (friend == null) {
            return false;
        }
        guard.hint((class_1657)friend);
        return true;
    }

    private void hint(class_1657 friend) {
        if (!this.notificationsOn()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastHintAt < 1500L) {
            return;
        }
        this.lastHintAt = now;
        ModuleChat.actionBar(this, ModuleChat.body().text("Held back your hit on ").name(friend.method_7334().name()));
    }
}

