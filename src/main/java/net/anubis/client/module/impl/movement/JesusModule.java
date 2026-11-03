/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10255
 *  net.minecraft.class_1293
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297
 *  net.minecraft.class_1941
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_241
 *  net.minecraft.class_243
 *  net.minecraft.class_259
 *  net.minecraft.class_2596
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_2741
 *  net.minecraft.class_2769
 *  net.minecraft.class_2828
 *  net.minecraft.class_310
 *  net.minecraft.class_3610
 *  net.minecraft.class_3611
 *  net.minecraft.class_3612
 *  net.minecraft.class_4985
 *  net.minecraft.class_5134
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.movement;

import com.example.client.mixin.AccessorJesusServerboundMovePlayerPacket;
import com.example.client.mixin.AccessorServerboundMovePlayerPacket;
import java.util.Iterator;
import net.anubis.client.event.EventPriority;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.PacketSendEvent;
import net.anubis.client.event.impl.PlayerMoveEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.movement.FlyModule;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10255;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1941;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_259;
import net.minecraft.class_2596;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2769;
import net.minecraft.class_2828;
import net.minecraft.class_310;
import net.minecraft.class_3610;
import net.minecraft.class_3611;
import net.minecraft.class_3612;
import net.minecraft.class_4985;
import net.minecraft.class_5134;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class JesusModule
extends ClientModule {
    public static volatile JesusModule active;
    private static final String SOLID = "Solid";
    private static final String IGNORE = "Ignore";
    private static final double ASCEND_SPEED = 0.11;
    private static final double NCP_STRAFE_SPEED = 0.2873;
    private static final int NCP_BOB_INTERVAL = 15;
    private static final int OVER_WATER = 1;
    private static final int OVER_LAVA = 2;
    private final BooleanSetting powderSnow = this.addSetting("powder-snow", new BooleanSetting("Powder Snow", true));
    private final BooleanSetting ncpBypass = this.addSetting("ncp-bypass", new BooleanSetting("NCP Bypass", false));
    private final BooleanSetting slowDown = this.addSetting("slow-down", new BooleanSetting("Slow Down", false));
    private final ModeSetting waterMode = this.addSetting("water-mode", new ModeSetting("Water Mode", "Solid", "Solid", "Ignore"));
    private final BooleanSetting dipIfBurning = this.addSetting("dip-if-burning", new BooleanSetting("Dip If Burning", true));
    private final BooleanSetting dipOnSneakWater = this.addSetting("water-dip-on-sneak", new BooleanSetting("Water Dip On Sneak", true));
    private final BooleanSetting dipOnFallWater = this.addSetting("water-dip-on-fall", new BooleanSetting("Water Dip On Fall", true));
    private final NumberSetting dipFallHeightWater = this.addSetting("water-dip-fall-height", new NumberSetting("Water Dip Fall Height", 4.0f, 1.0f, 255.0f).step(1.0f));
    private final ModeSetting lavaMode = this.addSetting("lava-mode", new ModeSetting("Lava Mode", "Solid", "Solid", "Ignore"));
    private final BooleanSetting dipIfFireResistant = this.addSetting("dip-if-resistant", new BooleanSetting("Dip If Resistant", true));
    private final BooleanSetting dipOnSneakLava = this.addSetting("lava-dip-on-sneak", new BooleanSetting("Lava Dip On Sneak", true));
    private final BooleanSetting dipOnFallLava = this.addSetting("lava-dip-on-fall", new BooleanSetting("Lava Dip On Fall", true));
    private final NumberSetting dipFallHeightLava = this.addSetting("lava-dip-fall-height", new NumberSetting("Lava Dip Fall Height", 4.0f, 1.0f, 255.0f).step(1.0f));
    private int ascending = 10;
    private int swimmingTicks;
    private boolean inBubbleColumn;
    private boolean moveIsVelocity;

    public JesusModule() {
        super("jesus", "Jesus", "Walk on liquids and powder snow like Jesus.", ModuleCategory.MOVEMENT);
        this.slowDown.visibleWhen(this.ncpBypass::get);
        this.dipIfBurning.visibleWhen(() -> this.waterMode.is(SOLID));
        this.dipOnSneakWater.visibleWhen(() -> this.waterMode.is(SOLID));
        this.dipOnFallWater.visibleWhen(() -> this.waterMode.is(SOLID));
        this.dipFallHeightWater.visibleWhen(() -> this.waterMode.is(SOLID) && this.dipOnFallWater.get());
        this.dipIfFireResistant.visibleWhen(() -> this.lavaMode.is(SOLID));
        this.dipOnSneakLava.visibleWhen(() -> this.lavaMode.is(SOLID));
        this.dipOnFallLava.visibleWhen(() -> this.lavaMode.is(SOLID));
        this.dipFallHeightLava.visibleWhen(() -> this.lavaMode.is(SOLID) && this.dipOnFallLava.get());
    }

    @Override
    protected void onEnable() {
        this.swimmingTicks = 0;
        this.inBubbleColumn = false;
        this.moveIsVelocity = false;
        active = this;
        this.on(TickEvent.TYPE, Stage.POST, this::onPostTick);
        this.on(PlayerMoveEvent.TYPE, Stage.PRE, EventPriority.HIGHEST, this::captureMoveAliasing);
        this.on(PlayerMoveEvent.TYPE, this::onMove);
        this.on(PacketSendEvent.TYPE, this::onPacketSend);
    }

    @Override
    protected void onDisable() {
        active = null;
        this.inBubbleColumn = false;
        this.moveIsVelocity = false;
        this.swimmingTicks = 0;
    }

    private void onPostTick(TickEvent event) {
        class_746 moving;
        boolean bubbleColumn = this.inBubbleColumn;
        this.inBubbleColumn = false;
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null) {
            return;
        }
        if (player.method_20232()) {
            return;
        }
        if (player.method_5799() && !this.waterShouldBeSolid(player)) {
            return;
        }
        if (player.method_5771() && !this.lavaShouldBeSolid(player)) {
            return;
        }
        Object object = moving = player.method_5765() ? player.method_5854() : player;
        if (moving == null) {
            return;
        }
        if (bubbleColumn) {
            if (mc.field_1690.field_1903.method_1434() && moving.method_18798().field_1351 < 0.11) {
                JesusModule.setVelocityY((class_1297)moving, 0.11);
            }
            return;
        }
        if (moving.method_5799() || moving.method_5771()) {
            JesusModule.setVelocityY((class_1297)moving, 0.11);
            this.ascending = 0;
            return;
        }
        class_2680 below = mc.field_1687.method_8320(moving.method_24515().method_10074());
        boolean waterlogged = (Boolean)below.method_61767((class_2769)class_2741.field_12508, (Comparable)Boolean.valueOf(false));
        if (this.ascending == 0) {
            JesusModule.setVelocityY((class_1297)moving, 0.11);
        } else if (this.ascending == 1 && (below.method_27852(class_2246.field_10382) || below.method_27852(class_2246.field_10164) || waterlogged)) {
            JesusModule.setVelocityY((class_1297)moving, 0.0);
        }
        ++this.ascending;
    }

    private static void setVelocityY(class_1297 entity, double y) {
        class_243 v = entity.method_18798();
        entity.method_18800(v.field_1352, y, v.field_1350);
    }

    private void captureMoveAliasing(PlayerMoveEvent event) {
        this.moveIsVelocity = false;
        if (!this.ncpBypass.get()) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        class_243 v = player.method_18798();
        this.moveIsVelocity = v.field_1352 == event.x && v.field_1351 == event.y && v.field_1350 == event.z;
    }

    private void onMove(PlayerMoveEvent event) {
        boolean lava;
        if (!this.ncpBypass.get()) {
            return;
        }
        boolean aliased = this.moveIsVelocity;
        this.moveIsVelocity = false;
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null) {
            return;
        }
        int over = JesusModule.overLiquid(player);
        boolean water = (over & 1) != 0 && this.waterShouldBeSolid(player);
        boolean bl = lava = (over & 2) != 0 && this.lavaShouldBeSolid(player);
        if (!water && !lava) {
            this.swimmingTicks = 0;
            return;
        }
        if (++this.swimmingTicks < 15) {
            if (player.method_24828()) {
                JesusModule.applyStrafe(mc, player, event, JesusModule.isMoving(player) ? 0.2873 : 0.0);
                if (aliased) {
                    JesusModule.setVelocityXZ((class_1297)player, event.x, event.z);
                }
            }
            return;
        }
        this.swimmingTicks = 0;
        if (this.slowDown.get()) {
            event.x = 0.0;
            event.z = 0.0;
            if (aliased) {
                JesusModule.setVelocityXZ((class_1297)player, 0.0, 0.0);
            }
        }
        JesusModule.setVelocityY((class_1297)player, 0.08);
        if (aliased) {
            event.y = 0.08;
        }
    }

    private static void setVelocityXZ(class_1297 entity, double x, double z) {
        entity.method_18800(x, entity.method_18798().field_1351, z);
    }

    private void onPacketSend(PacketSendEvent event) {
        boolean shouldWork;
        class_2596<?> class_25962 = event.packet;
        if (!(class_25962 instanceof class_2828)) {
            return;
        }
        class_2828 packet = (class_2828)class_25962;
        if (!this.ncpBypass.get()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (!mc.method_18854()) {
            return;
        }
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null) {
            return;
        }
        if (player.method_5799() && !this.waterShouldBeSolid(player)) {
            return;
        }
        if (player.method_5771() && !this.lavaShouldBeSolid(player)) {
            return;
        }
        int over = JesusModule.overLiquid(player);
        boolean bl = shouldWork = (over & 1) != 0 && this.waterShouldBeSolid(player) || (over & 2) != 0 && this.lavaShouldBeSolid(player);
        if (player.method_5799() || player.method_5771() || player.field_6017 > 3.0 || !shouldWork) {
            return;
        }
        ((AccessorJesusServerboundMovePlayerPacket)packet).anubis$jesusSetOnGround(false);
        if (!player.method_24828() || !packet.method_36171()) {
            return;
        }
        ((AccessorServerboundMovePlayerPacket)packet).anubis$setY(packet.method_12268(0.0) - (0.02 + 1.0E-4 * (double)this.swimmingTicks));
    }

    private static boolean isMoving(class_746 player) {
        return player.field_6250 != 0.0f || player.field_6212 != 0.0f;
    }

    private static void applyStrafe(class_310 mc, class_746 player, PlayerMoveEvent event, double speed) {
        class_241 input = player.field_3913.method_3128();
        float forward = Math.signum(input.field_1342);
        float side = Math.signum(input.field_1343);
        float yaw = player.method_61415(mc.method_61966().method_60637(true));
        if (forward == 0.0f && side == 0.0f) {
            event.x = 0.0;
            event.z = 0.0;
            return;
        }
        float strafe = 90.0f * side;
        if (forward != 0.0f) {
            strafe *= forward * 0.5f;
        }
        yaw -= strafe;
        if (forward < 0.0f) {
            yaw -= 180.0f;
        }
        double yawRadians = Math.toRadians(yaw);
        event.x = -Math.sin(yawRadians) * speed;
        event.z = Math.cos(yawRadians) * speed;
    }

    private static int overLiquid(class_746 player) {
        class_1297 vehicle = player.method_5854();
        class_238 box = vehicle != null ? player.method_5829().method_991(vehicle.method_5829()) : player.method_5829();
        boolean water = false;
        boolean lava = false;
        Iterator states = player.method_73183().method_29546(box.method_989(0.0, -0.01, 0.0)).iterator();
        while (states.hasNext()) {
            class_2680 state = (class_2680)states.next();
            if (state.method_27852(class_2246.field_10382) || state.method_26227().method_15772() == class_3612.field_15910) {
                water = true;
                continue;
            }
            if (state.method_27852(class_2246.field_10164)) {
                lava = true;
                continue;
            }
            if (state.method_26215()) continue;
            return 0;
        }
        return (water ? 1 : 0) | (lava ? 2 : 0);
    }

    private boolean waterShouldBeSolid(class_746 player) {
        if (player.method_7325() || player.method_31549().field_7479) {
            return false;
        }
        if (player.method_5854() instanceof class_10255) {
            return false;
        }
        if (FlyModule.active != null) {
            return false;
        }
        if (this.dipIfBurning.get() && player.method_5809()) {
            return false;
        }
        if (this.dipOnSneakWater.get() && class_310.method_1551().field_1690.field_1832.method_1434()) {
            return false;
        }
        if (this.dipOnFallWater.get() && player.field_6017 > (double)this.dipFallHeightWater.getInt()) {
            return false;
        }
        return this.waterMode.is(SOLID);
    }

    private boolean lavaShouldBeSolid(class_746 player) {
        if (player.method_7325() || player.method_31549().field_7479) {
            return false;
        }
        if (player.method_5854() instanceof class_4985) {
            return false;
        }
        if (this.isLavaDangerous(player) && this.lavaMode.is(SOLID)) {
            return true;
        }
        if (this.dipOnSneakLava.get() && class_310.method_1551().field_1690.field_1832.method_1434()) {
            return false;
        }
        if (this.dipOnFallLava.get() && player.field_6017 > (double)this.dipFallHeightLava.getInt()) {
            return false;
        }
        return this.lavaMode.is(SOLID);
    }

    private boolean isLavaDangerous(class_746 player) {
        if (!this.dipIfFireResistant.get()) {
            return true;
        }
        class_1293 fireResistance = player.method_6112(class_1294.field_5918);
        if (fireResistance == null) {
            return true;
        }
        return !((double)fireResistance.method_5584() > 300.0 * player.method_45325(class_5134.field_51579));
    }

    public boolean canWalkOnFluid(class_3610 fluid) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null || player.method_5681()) {
            return false;
        }
        class_3611 type = fluid.method_15772();
        if ((type == class_3612.field_15910 || type == class_3612.field_15909) && this.waterShouldBeSolid(player)) {
            return true;
        }
        return (type == class_3612.field_15908 || type == class_3612.field_15907) && this.lavaShouldBeSolid(player);
    }

    public class_265 collisionShape(class_1941 level, class_2680 state, class_2338 pos, class_265 shape) {
        class_3610 fluid = state.method_26227();
        if (fluid.method_15769()) {
            return shape;
        }
        class_310 mc = class_310.method_1551();
        if (level != mc.field_1687 || !mc.method_18854()) {
            return shape;
        }
        class_746 player = mc.field_1724;
        if (player == null) {
            return shape;
        }
        if ((state.method_27852(class_2246.field_10382) || fluid.method_15772() == class_3612.field_15910) && !player.method_5799() && this.waterShouldBeSolid(player) && (double)pos.method_10264() <= player.method_23318() - 1.0) {
            return class_259.method_1077();
        }
        if (state.method_27852(class_2246.field_10164) && !player.method_5771() && this.lavaShouldBeSolid(player) && (this.isLavaDangerous(player) || (double)pos.method_10264() <= player.method_23318() - 1.0)) {
            return class_259.method_1077();
        }
        return shape;
    }

    public boolean canWalkOnPowderSnow() {
        return this.powderSnow.get();
    }

    public void markInBubbleColumn() {
        this.inBubbleColumn = true;
    }
}

