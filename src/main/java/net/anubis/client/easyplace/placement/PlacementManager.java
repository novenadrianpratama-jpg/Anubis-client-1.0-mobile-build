/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  fi.dy.masa.litematica.world.WorldSchematic
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1269
 *  net.minecraft.class_1657
 *  net.minecraft.class_1792
 *  net.minecraft.class_1937
 *  net.minecraft.class_2281
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3965
 *  net.minecraft.class_746
 *  net.minecraft.class_7923
 */
package net.anubis.client.easyplace.placement;

import fi.dy.masa.litematica.world.WorldSchematic;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.anubis.client.easyplace.EasyPlace;
import net.anubis.client.easyplace.compat.BaritoneCompat;
import net.anubis.client.easyplace.inventory.InventoryManager;
import net.anubis.client.easyplace.inventory.InventoryPickResult;
import net.anubis.client.easyplace.mixin.AccessorMouseHandler;
import net.anubis.client.easyplace.placement.BuildScope;
import net.anubis.client.easyplace.placement.Clicks;
import net.anubis.client.easyplace.placement.DependencyManager;
import net.anubis.client.easyplace.placement.PlacementConfig;
import net.anubis.client.easyplace.placement.PlacementListener;
import net.anubis.client.easyplace.placement.PlacementState;
import net.anubis.client.easyplace.placement.PlacementTarget;
import net.anubis.client.easyplace.schematic.SchematicBlockState;
import net.anubis.client.easyplace.schematic.SchematicManager;
import net.anubis.client.easyplace.schematic.SchematicTarget;
import net.anubis.client.easyplace.schematic.TargetStatus;
import net.anubis.client.easyplace.sync.BlockAcks;
import net.anubis.client.easyplace.sync.ServerAckManager;
import net.anubis.client.easyplace.util.BlockStateUtil;
import net.anubis.client.easyplace.util.EasyPlaceDebug;
import net.anubis.client.easyplace.util.PlacementUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1937;
import net.minecraft.class_2281;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_746;
import net.minecraft.class_7923;

@Environment(value=EnvType.CLIENT)
public final class PlacementManager {
    private static final int ACK_TIMEOUT_TICKS = 40;
    private static final int MAX_SLOWDOWN = 20;
    private static final int MAX_RETRIES = 2;
    private static final int FAIL_BLOCK_TICKS = 20;
    private static final int FAILS_BEFORE_HOLD = 3;
    private static final int MAX_HOLD_TICKS = 600;
    private static final float TURN_STEP = 20.0f;
    private static final float TURN_EASE = 0.42f;
    private static final float TURN_SNAP = 1.0f;
    private static final int NO_CANDIDATE_COOLDOWN = 0;
    private static final int SNEAK_WAIT_TICKS = 20;
    private static final int SHIFT_STEADY_TICKS = 1;
    private final class_310 mc;
    private final SchematicManager schematicManager;
    private final InventoryManager inventoryManager;
    private final DependencyManager dependencyManager;
    private final ServerAckManager ackManager;
    private final PlacementListener listener;
    private final Map<Long, Long> blockedUntil = new HashMap<Long, Long>();
    private final Map<Long, Long> recentlyPlaced = new HashMap<Long, Long>();
    private final Map<Long, Integer> failsAt = new HashMap<Long, Integer>();
    private boolean reaimed;
    private boolean turning;
    private float turnYaw;
    private float turnPitch;
    private final Random hand = new Random();
    private PlacementState state = PlacementState.IDLE;
    private SchematicTarget target;
    private InventoryPickResult inventoryPick;
    private PlacementTarget placementTarget;
    private ServerAckManager.Result ackResult = ServerAckManager.Result.IDLE;
    private int slowdown;
    private int goodInRow;
    private int refusedCount;
    private long tick;
    private int retry;
    private class_2338 lastNavTarget;
    private long noCandidateUntil;
    private long lastPlacementTick = -1L;
    private float lastYawDelta = 0.0f;
    private int rotationTicksRemaining = 0;
    private float lastSentPitch = Float.NaN;
    private BuildScope scope;
    private Outcome outcome;
    private class_1268 placeHand = class_1268.field_5808;
    private long placedCount;
    private long lastTargetTick = Long.MIN_VALUE;
    private int sneakWait;
    private boolean sneakPressed;
    private boolean lastShift;
    private int shiftSteady;
    private long lastPreTick;

    public PlacementManager(class_310 mc, PlacementListener listener) {
        this.mc = mc;
        this.listener = listener;
        this.schematicManager = new SchematicManager(mc);
        this.inventoryManager = new InventoryManager(mc);
        this.dependencyManager = new DependencyManager(mc);
        this.ackManager = new ServerAckManager();
    }

    public void start() {
        this.reset();
        this.state = PlacementState.FIND_TARGET;
    }

    public void setOutcome(Outcome outcome) {
        this.outcome = outcome;
    }

    public void setScope(BuildScope scope) {
        if (this.scope != null && scope == null || this.scope == null && scope != null) {
            this.schematicManager.invalidate();
        }
        this.scope = scope;
    }

    public long placedCount() {
        return this.placedCount;
    }

    public int refusedCount() {
        return this.refusedCount;
    }

    public boolean placeableFrom(class_2680 expected, class_2338 pos, class_243 eye) {
        return this.dependencyManager.placeableFrom(expected, pos, eye, true);
    }

    public String debugState(class_2338 pos) {
        return "state=" + String.valueOf((Object)this.state) + " scopeAllows=" + String.valueOf(this.scope == null ? "noscope" : Boolean.valueOf(this.scope.allows(pos))) + " blocked=" + this.blockedUntil.containsKey(pos.method_10063()) + " recent=" + this.recentlyPlaced.containsKey(pos.method_10063()) + " noCand=" + (this.noCandidateUntil - this.tick) + " lastTarget=" + this.ticksWithoutTarget() + " target=" + (this.target == null ? "-" : this.target.pos().method_23854()) + " sincePre=" + (this.tick - this.lastPreTick) + " searchNow=" + PlacementManager.describe(this.schematicManager.getSchematicWorld() == null ? null : this.dependencyManager.searchForPlaceables(this.schematicManager.getSchematicWorld(), true, false, 360.0f, 180.0, 0.0f, this.scope, this.blockedUntil::containsKey));
    }

    private static String describe(PlacementTarget found) {
        return found == null ? "none" : found.targetPos().method_23854();
    }

    public long ticksWithoutTarget() {
        return this.lastTargetTick == Long.MIN_VALUE ? Long.MAX_VALUE : this.tick - this.lastTargetTick;
    }

    public void abort() {
        this.failCurrent();
    }

    public boolean busy() {
        return this.state == PlacementState.CALCULATE_ROTATION || this.state == PlacementState.WAIT_ROTATION || this.state == PlacementState.EXECUTE_PLACE || this.state == PlacementState.WAIT_ACK;
    }

    public void tickPre(PlacementConfig config) {
        if (this.mc.field_1724 == null || this.mc.field_1687 == null || this.mc.field_1761 == null) {
            return;
        }
        this.lastPreTick = this.tick;
        if (this.state == PlacementState.IDLE || this.state == PlacementState.FIND_TARGET) {
            if (this.lastPlacementTick >= 0L && this.tick - this.lastPlacementTick <= (long)(config.tickDelay() + this.slowdown)) {
                this.state = PlacementState.FIND_TARGET;
                return;
            }
            this.resolve(config);
        }
        if (this.state == PlacementState.CALCULATE_ROTATION) {
            this.prepareRotation(config);
        }
        if (this.state == PlacementState.EXECUTE_PLACE) {
            this.lastPlacementTick = this.tick;
            this.executePlace(config);
        }
    }

    public void reset() {
        this.releaseSneak();
        this.state = PlacementState.IDLE;
        this.target = null;
        this.inventoryPick = null;
        this.placementTarget = null;
        this.ackResult = ServerAckManager.Result.IDLE;
        this.retry = 0;
        this.recentlyPlaced.clear();
        this.blockedUntil.clear();
        this.failsAt.clear();
        this.reaimed = false;
        this.turning = false;
        this.ackManager.clear();
        this.inventoryManager.reset();
        this.schematicManager.clear();
        this.lastNavTarget = null;
        this.noCandidateUntil = 0L;
        this.lastYawDelta = 0.0f;
        this.rotationTicksRemaining = 0;
        this.lastSentPitch = Float.NaN;
        this.slowdown = 0;
        this.goodInRow = 0;
        this.refusedCount = 0;
    }

    public void tick(PlacementConfig config) {
        ++this.tick;
        if (this.mc.field_1724 != null) {
            boolean shift = this.mc.field_1724.method_5715();
            this.shiftSteady = shift == this.lastShift ? this.shiftSteady + 1 : 0;
            this.lastShift = shift;
        }
        this.blockedUntil.entrySet().removeIf(entry -> (Long)entry.getValue() <= this.tick);
        this.recentlyPlaced.entrySet().removeIf(entry -> (Long)entry.getValue() <= this.tick);
        if (this.mc.field_1724 == null || this.mc.field_1687 == null || this.mc.field_1761 == null) {
            this.softReset();
            return;
        }
        if (this.state == PlacementState.WAIT_ROTATION && this.turning) {
            if (this.mc.field_1724 != null && this.turnTowards(this.mc.field_1724)) {
                this.turning = false;
                this.rotationTicksRemaining = 1;
            }
        } else if (this.state == PlacementState.WAIT_ROTATION) {
            if (this.rotationTicksRemaining > 0) {
                --this.rotationTicksRemaining;
            } else {
                this.state = PlacementState.EXECUTE_PLACE;
            }
        }
        if (this.mc.field_1755 != null && this.state != PlacementState.WAIT_ACK) {
            return;
        }
        if (this.state == PlacementState.WAIT_ACK) {
            this.waitForAck(config);
        }
    }

    private void resolve(PlacementConfig config) {
        if (!this.schematicManager.hasActiveSchematic()) {
            if (this.state != PlacementState.FIND_TARGET) {
                this.state = PlacementState.FIND_TARGET;
                this.listener.feedback("no-schematic", "No active Litematica schematic", 100);
            }
            return;
        }
        if (this.tick < this.noCandidateUntil) {
            return;
        }
        WorldSchematic schematicWorld = this.schematicManager.getSchematicWorld();
        if (schematicWorld == null) {
            this.state = PlacementState.FIND_TARGET;
            return;
        }
        this.placementTarget = this.dependencyManager.searchForPlaceables(schematicWorld, config.hopperForceFacing(), config.fovFilter(), (float)config.fovDegrees(), config.maxYawChange(), this.lastYawDelta, this.scope, this.blockedUntil::containsKey);
        if (this.placementTarget == null) {
            boolean hasWrong;
            if (!config.schematicOnly() && (hasWrong = this.schematicManager.getTargets(this.tick, config.auraRange(), config.schematicAura(), config.allBlocks()).stream().anyMatch(c -> c.status() == TargetStatus.WRONG))) {
                this.listener.feedback("wrong-safe-skip", "Wrong blocks detected; automatic breaking is disabled for safety", 100);
            }
            this.noCandidateUntil = this.tick + 0L;
            if (this.scope == null && config.baritoneWalk() && BaritoneCompat.isAvailable()) {
                SchematicTarget navTarget = this.schematicManager.nearestMissingForNavigation(this.tick, config.allBlocks());
                if (navTarget != null) {
                    class_2338 navPos = navTarget.pos();
                    if (!navPos.equals((Object)this.lastNavTarget)) {
                        this.lastNavTarget = navPos;
                        int walkRange = Math.max(1, (int)config.auraRange() - 1);
                        BaritoneCompat.goNear(navPos, walkRange);
                        this.listener.feedback("baritone-nav", "Walking to " + navPos.method_23854(), 40);
                    }
                } else if (this.lastNavTarget != null) {
                    BaritoneCompat.cancel();
                    this.lastNavTarget = null;
                }
            }
            this.state = PlacementState.FIND_TARGET;
            return;
        }
        this.lastTargetTick = this.tick;
        if (this.lastNavTarget != null) {
            BaritoneCompat.cancel();
            this.lastNavTarget = null;
        }
        class_2338 targetPos = this.placementTarget.targetPos();
        class_2680 expected = schematicWorld.method_8320(targetPos);
        class_2680 actual = this.mc.field_1687.method_8320(targetPos);
        TargetStatus status = BlockStateUtil.classify(expected, actual);
        this.target = new SchematicTarget(targetPos, SchematicBlockState.of(expected), actual, status, targetPos.method_46558().method_1025(this.mc.field_1724.method_33571()));
        class_1792 required = BlockStateUtil.requiredItem(this.target.expected().state());
        if (required == null) {
            this.listener.feedback("unsupported-" + String.valueOf(this.target.expected().blockId()), "No placeable item for: " + String.valueOf(this.target.expected().blockId()), 100);
            this.blockedUntil.put(this.target.pos().method_10063(), this.tick + 1L);
            this.schematicManager.invalidate();
            this.target = null;
            this.placementTarget = null;
            this.state = PlacementState.FIND_TARGET;
            return;
        }
        this.inventoryPick = this.inventoryManager.prepare(config.inventoryPickSource(), required, this.tick, this.scope != null);
        if (this.inventoryPick.status() == InventoryPickResult.Status.WAITING) {
            return;
        }
        if (this.inventoryPick.status() == InventoryPickResult.Status.MISSING) {
            this.listener.feedback("missing-item-" + String.valueOf(class_7923.field_41178.method_10221((Object)required)), "Missing item: " + String.valueOf(class_7923.field_41178.method_10221((Object)required)), 100);
            this.blockedUntil.put(this.target.pos().method_10063(), this.tick + 1L);
            this.schematicManager.invalidate();
            this.target = null;
            this.inventoryPick = null;
            this.placementTarget = null;
            this.state = PlacementState.FIND_TARGET;
            return;
        }
        this.placeHand = class_1268.field_5808;
        if (this.scope != null) {
            if (this.inventoryPick.hand() == class_1268.field_5810) {
                this.placeHand = class_1268.field_5810;
            } else if (this.inventoryPick.slot() >= 0 && this.inventoryPick.slot() < 9) {
                this.placementTarget = PlacementManager.withSlot(this.placementTarget, this.inventoryPick.slot());
            } else {
                this.state = PlacementState.FIND_TARGET;
                return;
            }
        }
        this.debug(config, "target-selected");
        this.state = PlacementState.CALCULATE_ROTATION;
    }

    private void prepareRotation(PlacementConfig config) {
        boolean needsSneak;
        if (this.mc.field_1724 == null || this.placementTarget == null || this.mc.field_1761 == null) {
            this.failCurrent();
            return;
        }
        class_746 player = this.mc.field_1724;
        boolean bl = needsSneak = Clicks.usesClick((class_1937)this.mc.field_1687, this.placementTarget.neighborPos(), this.scope != null) || this.placementTarget.sneak();
        if (this.scope != null) {
            this.mc.field_1690.field_1832.method_23481(needsSneak);
            this.sneakPressed = needsSneak;
            if (player.method_5715() != needsSneak || this.shiftSteady < 1) {
                if (++this.sneakWait > 20) {
                    if (Boolean.getBoolean("easyplace.builder.debug")) {
                        EasyPlace.LOG.info("[builder] sneak never settled: want {} shift {} key {} steady {} target {}", new Object[]{needsSneak, player.method_5715(), this.mc.field_1690.field_1832.method_1434(), this.shiftSteady, this.placementTarget.targetPos().method_23854()});
                    }
                    this.failCurrent();
                }
                return;
            }
        } else if (needsSneak && !player.method_5715()) {
            return;
        }
        this.sneakWait = 0;
        if (this.placeHand == class_1268.field_5808 && player.method_31548().method_67532() != this.placementTarget.hotbarSlot()) {
            player.method_31548().method_61496(this.placementTarget.hotbarSlot());
        }
        if (config.rotationSpoof()) {
            this.zeroMouseDeltas();
            float targetYaw = this.placementTarget.yaw();
            float targetPitch = this.placementTarget.pitch();
            float currentYaw = player.method_36454();
            float deltaYaw = class_3532.method_15393((float)(targetYaw - currentYaw));
            float newYaw = currentYaw + deltaYaw;
            float pitchDelta = Math.abs(targetPitch - (Float.isNaN(this.lastSentPitch) ? player.method_36455() : this.lastSentPitch));
            float absDeltaYaw = Math.abs(deltaYaw);
            this.rotationTicksRemaining = pitchDelta > 15.0f || absDeltaYaw > 45.0f ? 1 : 0;
            this.lastYawDelta = deltaYaw;
            if (this.scope != null) {
                this.startTurn(targetYaw, targetPitch);
                return;
            }
            this.lastSentPitch = targetPitch;
            player.method_36456(newYaw);
            player.method_36457(targetPitch);
            this.state = PlacementState.WAIT_ROTATION;
        } else {
            this.state = PlacementState.EXECUTE_PLACE;
        }
        this.debug(config, "rotation-prepared");
    }

    private void startTurn(float yaw, float pitch) {
        this.turnYaw = yaw;
        this.turnPitch = pitch;
        this.turning = true;
        this.state = PlacementState.WAIT_ROTATION;
    }

    private boolean turnTowards(class_746 player) {
        this.zeroMouseDeltas();
        float yawLeft = class_3532.method_15393((float)(this.turnYaw - player.method_36454()));
        float pitchLeft = this.turnPitch - player.method_36455();
        if (Math.abs(yawLeft) < 1.0f && Math.abs(pitchLeft) < 1.0f) {
            player.method_36456(player.method_36454() + yawLeft);
            player.method_36457(this.turnPitch);
            this.lastSentPitch = this.turnPitch;
            return true;
        }
        float ease = 0.42f * (0.85f + this.hand.nextFloat() * 0.3f);
        float yawStep = Math.abs(yawLeft) < 1.0f ? yawLeft : class_3532.method_15363((float)(yawLeft * ease), (float)-20.0f, (float)20.0f);
        float pitchStep = Math.abs(pitchLeft) < 1.0f ? pitchLeft : class_3532.method_15363((float)(pitchLeft * ease), (float)-20.0f, (float)20.0f);
        player.method_36456(player.method_36454() + yawStep);
        player.method_36457(class_3532.method_15363((float)(player.method_36455() + pitchStep), (float)-90.0f, (float)90.0f));
        return false;
    }

    private void zeroMouseDeltas() {
        if (this.mc.field_1729 == null) {
            return;
        }
        AccessorMouseHandler mouse = (AccessorMouseHandler)this.mc.field_1729;
        mouse.easyplace$setAccumulatedDX(0.0);
        mouse.easyplace$setAccumulatedDY(0.0);
    }

    private void executePlace(PlacementConfig config) {
        if (this.mc.field_1724 == null || this.placementTarget == null || this.mc.field_1761 == null) {
            this.failCurrent();
            return;
        }
        class_746 player = this.mc.field_1724;
        if (player.method_5829().method_994(new class_238(this.placementTarget.targetPos()))) {
            this.traceFail("body in the cell");
            this.failCurrent();
            this.noCandidateUntil = this.tick + 2L;
            return;
        }
        double reach = player.method_55754();
        class_3965 hit = PlacementUtil.rayTraceTowards((class_1657)player, player.method_36454(), player.method_36455(), reach);
        if (hit == null || hit.method_17783() != class_239.class_240.field_1332 || !hit.method_17777().equals((Object)this.placementTarget.neighborPos()) || hit.method_17780() != this.placementTarget.clickedFace()) {
            class_243 eyePos = PlacementUtil.getEyePos((class_1657)player);
            if (!PlacementUtil.isFaceVisibleToEye(this.placementTarget.neighborPos(), this.placementTarget.clickedFace(), eyePos)) {
                this.traceFail("face not visible any more");
                this.failCurrent();
                this.noCandidateUntil = this.tick + 1L;
                return;
            }
            class_243 targetCursor = this.placementTarget.hitCursor() != null ? this.placementTarget.hitCursor() : PlacementUtil.faceCenterCursor(this.placementTarget.neighborPos(), this.placementTarget.clickedFace());
            float[] newRot = PlacementUtil.calcRotation(eyePos, targetCursor);
            class_3965 recheck = PlacementUtil.rayTraceTowards((class_1657)player, newRot[0], newRot[1], reach);
            if (recheck != null && recheck.method_17783() == class_239.class_240.field_1332 && recheck.method_17777().equals((Object)this.placementTarget.neighborPos()) && recheck.method_17780() == this.placementTarget.clickedFace()) {
                if (this.scope != null) {
                    this.startTurn(newRot[0], newRot[1]);
                    return;
                }
                this.zeroMouseDeltas();
                float deltaYaw = class_3532.method_15393((float)(newRot[0] - player.method_36454()));
                player.method_36456(player.method_36454() + deltaYaw);
                player.method_36457(newRot[1]);
                this.lastSentPitch = newRot[1];
                this.rotationTicksRemaining = 0;
                this.state = PlacementState.WAIT_ROTATION;
                return;
            }
            this.traceFail("aim hits " + (String)(recheck == null ? "nothing" : recheck.method_17777().method_23854() + " " + String.valueOf(recheck.method_17780())));
            this.failCurrent();
            this.noCandidateUntil = this.tick + 1L;
            return;
        }
        class_3965 finalHit = hit;
        if (this.scope != null && this.placeHand == class_1268.field_5808 && this.target != null && !this.dependencyManager.verify(this.target.expected().state(), this.placementTarget.targetPos(), finalHit, player.method_36454(), player.method_36455(), player.method_31548().method_67532(), player.method_5715())) {
            if (!this.reaimed && this.placementTarget.hitCursor() != null) {
                this.reaimed = true;
                float[] rot = PlacementUtil.calcRotation(PlacementUtil.getEyePos((class_1657)player), this.placementTarget.hitCursor());
                if (this.scope != null) {
                    this.startTurn(rot[0], rot[1]);
                    return;
                }
                this.zeroMouseDeltas();
                player.method_36456(player.method_36454() + class_3532.method_15393((float)(rot[0] - player.method_36454())));
                player.method_36457(rot[1]);
                this.lastSentPitch = rot[1];
                this.rotationTicksRemaining = 0;
                this.state = PlacementState.WAIT_ROTATION;
                return;
            }
            this.traceFail("would not come out right, holding " + String.valueOf(player.method_31548().method_5438(player.method_31548().method_67532())) + " shift " + player.method_5715() + " face " + String.valueOf(finalHit.method_17780()) + " hit " + String.valueOf(finalHit.method_17784()) + " planned " + String.valueOf(this.placementTarget.hitCursor()) + " eye " + String.valueOf(PlacementUtil.getEyePos((class_1657)player)) + " yaw " + player.method_36454() + " pitch " + player.method_36455() + " got " + this.dependencyManager.wouldBe(this.placementTarget.targetPos(), finalHit, player.method_36454(), player.method_36455(), player.method_31548().method_67532(), player.method_5715()));
            this.failCurrent();
            this.noCandidateUntil = this.tick + 1L;
            return;
        }
        class_2680 before = this.mc.field_1687.method_8320(this.placementTarget.targetPos());
        class_1269 placementResult = this.mc.field_1761.method_2896(player, this.placeHand, finalHit);
        if (Boolean.getBoolean("easyplace.builder.debug") && this.target != null && this.target.expected().state().method_26204() instanceof class_2281) {
            EasyPlace.LOG.info("[builder] chest at {} want {} got {} (was {}) shift={} sneakFlag={} against {} face {}", new Object[]{this.placementTarget.targetPos().method_23854(), this.target.expected().state(), this.mc.field_1687.method_8320(this.placementTarget.targetPos()), before, player.method_5715(), this.placementTarget.sneak(), this.placementTarget.neighborPos().method_23854(), finalHit.method_17780()});
        }
        this.releaseSneak();
        if (placementResult.method_23665()) {
            player.method_6104(this.placeHand);
            this.ackManager.begin(this.target, this.tick, 40, this.retry, BlockAcks.lastSent(this.mc.field_1687), this.mc.field_1687.method_8320(this.placementTarget.targetPos()));
            this.ackResult = ServerAckManager.Result.WAITING;
            this.state = PlacementState.WAIT_ACK;
            this.debug(config, "placement-sent");
        } else {
            this.traceFail("use rejected: " + String.valueOf(placementResult));
            this.debug(config, "placement-rejected");
            this.failCurrent();
            this.noCandidateUntil = this.tick + 2L;
        }
    }

    private void waitForAck(PlacementConfig config) {
        this.ackResult = this.ackManager.poll(this.mc.field_1687, this.tick);
        if (this.ackResult == ServerAckManager.Result.WAITING) {
            return;
        }
        if (this.ackResult == ServerAckManager.Result.CONFIRMED) {
            if (this.slowdown > 0 && ++this.goodInRow >= 10) {
                --this.slowdown;
                this.goodInRow = 0;
            }
            this.listener.feedback("confirmed-" + this.target.pos().method_10063(), "Placement confirmed: " + this.target.pos().method_23854(), 1);
            this.debug(config, "confirmed");
            this.recentlyPlaced.put(this.target.pos().method_10063(), this.tick + 1L);
            this.failsAt.remove(this.target.pos().method_10063());
            this.reaimed = false;
            ++this.placedCount;
            if (this.outcome != null) {
                this.outcome.placed(this.target.pos());
            }
            this.schematicManager.invalidate();
            this.target = null;
            this.inventoryPick = null;
            this.placementTarget = null;
            this.ackResult = ServerAckManager.Result.IDLE;
            this.retry = 0;
            this.ackManager.clear();
            this.inventoryManager.reset();
            this.state = PlacementState.FIND_TARGET;
            return;
        }
        this.goodInRow = 0;
        this.slowdown = Math.min(20, this.slowdown + 3);
        ++this.refusedCount;
        if (Boolean.getBoolean("easyplace.builder.debug")) {
            EasyPlace.LOG.info("[builder] server {} the block at {}, slowing to {} extra ticks", new Object[]{this.ackResult == ServerAckManager.Result.REFUSED ? "refused" : "never answered", this.target.pos().method_23854(), this.slowdown});
        }
        if (this.retry < 2) {
            ++this.retry;
            this.ackManager.clear();
            this.placementTarget = null;
            this.state = PlacementState.CALCULATE_ROTATION;
            this.listener.feedback("retry-" + this.target.pos().method_10063(), "Placement ACK timeout; retry " + this.retry + "/2", 10);
            this.debug(config, "ack-timeout-retry");
        } else {
            this.failCurrent();
        }
    }

    private static PlacementTarget withSlot(PlacementTarget target, int slot) {
        if (target.hotbarSlot() == slot) {
            return target;
        }
        return new PlacementTarget(target.targetPos(), target.neighborPos(), target.placeSide(), target.clickedFace(), slot, target.yaw(), target.pitch(), target.score(), target.hitCursor(), target.sneak());
    }

    private void traceFail(String why) {
        if (!Boolean.getBoolean("easyplace.builder.debug") || this.placementTarget == null) {
            return;
        }
        EasyPlace.LOG.info("[builder] place fail {} {}: {}", new Object[]{this.placementTarget.targetPos().method_23854(), this.target == null ? "?" : this.target.expected().state(), why});
    }

    private void releaseSneak() {
        if (!this.sneakPressed) {
            return;
        }
        this.sneakPressed = false;
        this.sneakWait = 0;
        this.mc.field_1690.field_1832.method_23481(false);
    }

    private void failCurrent() {
        this.releaseSneak();
        if (this.target != null) {
            long key = this.target.pos().method_10063();
            int fails = this.failsAt.merge(key, 1, Integer::sum);
            if (fails >= 3) {
                this.blockedUntil.put(key, this.tick + (long)Math.min(600, 20 << Math.min(5, fails - 3)));
            }
            if (this.outcome != null) {
                this.outcome.failed(this.target.pos());
            }
        }
        this.reaimed = false;
        this.turning = false;
        this.schematicManager.invalidate();
        this.target = null;
        this.inventoryPick = null;
        this.placementTarget = null;
        this.ackResult = ServerAckManager.Result.IDLE;
        this.retry = 0;
        this.ackManager.clear();
        this.inventoryManager.reset();
        this.state = PlacementState.FIND_TARGET;
    }

    private void softReset() {
        this.releaseSneak();
        this.reaimed = false;
        this.turning = false;
        this.state = PlacementState.FIND_TARGET;
        this.target = null;
        this.ackManager.clear();
        this.inventoryManager.reset();
        this.schematicManager.clear();
    }

    private void debug(PlacementConfig config, String event) {
        if (!config.debug()) {
            return;
        }
        float yaw = this.placementTarget != null ? this.placementTarget.yaw() : Float.NaN;
        float pitch = this.placementTarget != null ? this.placementTarget.pitch() : Float.NaN;
        EasyPlaceDebug.log(event, this.state, this.placementTarget, new float[]{yaw, pitch}, this.ackResult, this.retry);
    }

    public PlacementState getState() {
        return this.state;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Outcome {
        public void placed(class_2338 var1);

        public void failed(class_2338 var1);
    }
}

