/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10707
 *  net.minecraft.class_1267
 *  net.minecraft.class_1280
 *  net.minecraft.class_1282
 *  net.minecraft.class_1293
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1799
 *  net.minecraft.class_1887
 *  net.minecraft.class_1890
 *  net.minecraft.class_1893
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2378
 *  net.minecraft.class_238
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_3532
 *  net.minecraft.class_3726
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_5134
 *  net.minecraft.class_5321
 *  net.minecraft.class_6880
 *  net.minecraft.class_7924
 *  net.minecraft.class_9334
 */
package net.anubis.client.module.impl.combat.crystal;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10707;
import net.minecraft.class_1267;
import net.minecraft.class_1280;
import net.minecraft.class_1282;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1887;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2378;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_3726;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_5134;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_7924;
import net.minecraft.class_9334;

@Environment(value=EnvType.CLIENT)
public final class CrystalDamage {
    private static final double REACH = 12.0;
    private static final float SELF_SCALE = 1.15f;
    private static final float SELF_MARGIN = 2.0f;
    private static final class_1304[] ARMOR = new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166};

    private CrystalDamage() {
    }

    public static float estimate(class_243 center, class_1309 entity) {
        return new Victim(entity, class_243.field_1353).damage(center);
    }

    public static float estimate(class_243 center, class_1309 entity, float power, class_2338 ignored) {
        return new Victim(entity, class_243.field_1353).damage(center, power, ignored);
    }

    public static float health(class_1309 entity) {
        return entity.method_6032() + entity.method_6067();
    }

    public static boolean endangers(class_1657 self, float damage) {
        return !self.method_68878() && damage * 1.15f + 2.0f >= CrystalDamage.health((class_1309)self);
    }

    private static float falloff(double exposure) {
        return CrystalDamage.falloff(exposure, 12.0);
    }

    private static float falloff(double exposure, double diameter) {
        return (float)((exposure * exposure + exposure) / 2.0 * 7.0 * diameter + 1.0);
    }

    private static float protection(class_1309 entity) {
        class_2378 enchantments = entity.method_56673().method_30530(class_7924.field_41265);
        int total = 0;
        for (class_1304 slot : ARMOR) {
            class_1799 stack = entity.method_6118(slot);
            if (stack.method_7960()) continue;
            total += CrystalDamage.level((class_2378<class_1887>)enchantments, (class_5321<class_1887>)class_1893.field_9111, stack) + 2 * CrystalDamage.level((class_2378<class_1887>)enchantments, (class_5321<class_1887>)class_1893.field_9107, stack);
        }
        return total;
    }

    private static int level(class_2378<class_1887> enchantments, class_5321<class_1887> key, class_1799 stack) {
        return enchantments.method_46746(key).map(holder -> class_1890.method_8225((class_6880)holder, (class_1799)stack)).orElse(0);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Victim {
        private final class_1309 entity;
        private final class_243 position;
        private final class_238 box;
        private final class_1282 source;
        private final float armor;
        private final float toughness;
        private final float protection;
        private final float resistance;
        private final class_10707 shield;
        private final Map<class_243, Float> exposures = new HashMap<class_243, Float>();
        private final Map<Blast, Float> blastExposures = new HashMap<Blast, Float>();

        public Victim(class_1309 entity, class_243 offset) {
            this.entity = entity;
            this.position = entity.method_73189().method_1019(offset);
            this.box = entity.method_5829().method_997(offset);
            this.source = entity.method_48923().method_48819(null, null);
            this.armor = entity.method_6096();
            this.toughness = (float)entity.method_45325(class_5134.field_23725);
            this.protection = CrystalDamage.protection(entity);
            class_1293 effect = entity.method_6112(class_1294.field_5907);
            this.resistance = effect == null ? 1.0f : (float)Math.max(25 - (effect.method_5578() + 1) * 5, 0) / 25.0f;
            class_1799 blocking = entity.method_62821();
            this.shield = blocking == null ? null : (class_10707)blocking.method_58694(class_9334.field_56396);
        }

        public class_1309 entity() {
            return this.entity;
        }

        public float damage(class_243 center) {
            return this.damage(center, false);
        }

        public float damage(class_243 center, boolean baseToCome) {
            return this.reduce(this.raw(center, baseToCome));
        }

        public float raw(class_243 center, boolean baseToCome) {
            double distance = Math.sqrt(this.position.method_1025(center)) / 12.0;
            if (distance > 1.0) {
                return 0.0f;
            }
            float damage = this.scaled(CrystalDamage.falloff((1.0 - distance) * (double)this.exposure(center, baseToCome)));
            return damage - this.blocked(center, damage);
        }

        public float reduce(float raw) {
            float damage = class_1280.method_5496((class_1309)this.entity, (float)raw, (class_1282)this.source, (float)this.armor, (float)this.toughness);
            return class_1280.method_5497((float)(damage * this.resistance), (float)this.protection);
        }

        public float damage(class_243 center, float power, class_2338 ignored) {
            return this.reduce(this.raw(center, power, ignored));
        }

        public float raw(class_243 center, float power, class_2338 ignored) {
            double diameter = (double)power * 2.0;
            double distance = Math.sqrt(this.position.method_1025(center)) / diameter;
            if (distance > 1.0) {
                return 0.0f;
            }
            float damage = this.scaled(CrystalDamage.falloff((1.0 - distance) * (double)this.blastExposure(center, ignored), diameter));
            return damage - this.blocked(center, damage);
        }

        private float blocked(class_243 center, float damage) {
            if (this.shield == null || damage <= 0.0f) {
                return 0.0f;
            }
            class_243 facing = this.entity.method_5631(0.0f, this.entity.method_5791());
            class_243 toward = center.method_1020(this.position);
            class_243 flat = new class_243(toward.field_1352, 0.0, toward.field_1350).method_1029();
            double angle = Math.acos(class_3532.method_15350((double)flat.method_1026(facing), (double)-1.0, (double)1.0));
            return this.shield.method_67202(this.source, damage, angle);
        }

        public float upperBound(class_243 center) {
            double distance = Math.sqrt(this.position.method_1025(center)) / 12.0;
            return distance > 1.0 ? 0.0f : this.reduce(this.scaled(CrystalDamage.falloff(1.0 - distance)));
        }

        public float upperBound(class_243 center, float power) {
            double diameter = (double)power * 2.0;
            double distance = Math.sqrt(this.position.method_1025(center)) / diameter;
            return distance > 1.0 ? 0.0f : this.reduce(this.scaled(CrystalDamage.falloff(1.0 - distance, diameter)));
        }

        private float exposure(class_243 center, boolean baseToCome) {
            Float known = this.exposures.get(center);
            if (known != null) {
                return known.floatValue();
            }
            class_238 base = baseToCome ? new class_238(class_2338.method_49637((double)center.field_1352, (double)(center.field_1351 - 0.5), (double)center.field_1350)) : null;
            float seen = this.openAir(center, null) && (base == null || !this.hull(center).method_994(base)) ? 1.0f : this.seenPercent(center, base, null);
            this.exposures.put(center, Float.valueOf(seen));
            return seen;
        }

        private float blastExposure(class_243 center, class_2338 ignored) {
            Blast key = new Blast(center, ignored);
            Float known = this.blastExposures.get(key);
            if (known != null) {
                return known.floatValue();
            }
            float seen = this.openAir(center, ignored) ? 1.0f : this.seenPercent(center, null, ignored);
            this.blastExposures.put(key, Float.valueOf(seen));
            return seen;
        }

        private class_238 hull(class_243 center) {
            return this.box.method_991(new class_238(center, center));
        }

        private boolean openAir(class_243 center, class_2338 ignored) {
            class_1937 level = this.entity.method_73183();
            class_3726 context = class_3726.method_16195((class_1297)this.entity);
            int minX = class_3532.method_15357((double)Math.min(this.box.field_1323, center.field_1352));
            int minY = class_3532.method_15357((double)Math.min(this.box.field_1322, center.field_1351));
            int minZ = class_3532.method_15357((double)Math.min(this.box.field_1321, center.field_1350));
            int maxX = class_3532.method_15357((double)Math.max(this.box.field_1320, center.field_1352));
            int maxY = class_3532.method_15357((double)Math.max(this.box.field_1325, center.field_1351));
            int maxZ = class_3532.method_15357((double)Math.max(this.box.field_1324, center.field_1350));
            class_2338.class_2339 pos = new class_2338.class_2339();
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        pos.method_10103(x, y, z);
                        if (pos.equals((Object)ignored) || level.method_8320((class_2338)pos).method_26194((class_1922)level, (class_2338)pos, context).method_1110()) continue;
                        return false;
                    }
                }
            }
            return true;
        }

        private float seenPercent(class_243 center, class_238 base, class_2338 ignored) {
            double stepX = 1.0 / ((this.box.field_1320 - this.box.field_1323) * 2.0 + 1.0);
            double stepY = 1.0 / ((this.box.field_1325 - this.box.field_1322) * 2.0 + 1.0);
            double stepZ = 1.0 / ((this.box.field_1324 - this.box.field_1321) * 2.0 + 1.0);
            double offsetX = (1.0 - Math.floor(1.0 / stepX) * stepX) / 2.0;
            double offsetZ = (1.0 - Math.floor(1.0 / stepZ) * stepZ) / 2.0;
            if (stepX < 0.0 || stepY < 0.0 || stepZ < 0.0) {
                return 0.0f;
            }
            class_1937 level = this.entity.method_73183();
            int seen = 0;
            int total = 0;
            for (double fx = 0.0; fx <= 1.0; fx += stepX) {
                for (double fy = 0.0; fy <= 1.0; fy += stepY) {
                    for (double fz = 0.0; fz <= 1.0; fz += stepZ) {
                        class_243 from = new class_243(class_3532.method_16436((double)fx, (double)this.box.field_1323, (double)this.box.field_1320) + offsetX, class_3532.method_16436((double)fy, (double)this.box.field_1322, (double)this.box.field_1325), class_3532.method_16436((double)fz, (double)this.box.field_1321, (double)this.box.field_1324) + offsetZ);
                        class_3965 hit = level.method_17742(new class_3959(from, center, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)this.entity));
                        if (!(hit.method_17783() != class_239.class_240.field_1333 && !hit.method_17777().equals((Object)ignored) || base != null && Victim.throughBlock(base, from, center))) {
                            ++seen;
                        }
                        ++total;
                    }
                }
            }
            return (float)seen / (float)total;
        }

        private static boolean throughBlock(class_238 block, class_243 from, class_243 to) {
            class_243 end = new class_243(class_3532.method_16436((double)1.0E-7, (double)to.field_1352, (double)from.field_1352), class_3532.method_16436((double)1.0E-7, (double)to.field_1351, (double)from.field_1351), class_3532.method_16436((double)1.0E-7, (double)to.field_1350, (double)from.field_1350));
            return block.method_1006(from) || block.method_992(from, end).isPresent();
        }

        private float scaled(float damage) {
            if (!(this.entity instanceof class_1657)) {
                return damage;
            }
            return switch (this.entity.method_73183().method_8407()) {
                default -> throw new MatchException(null, null);
                case class_1267.field_5801 -> 0.0f;
                case class_1267.field_5805 -> Math.min(damage / 2.0f + 1.0f, damage);
                case class_1267.field_5802 -> damage;
                case class_1267.field_5807 -> damage * 1.5f;
            };
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Blast(class_243 center, class_2338 ignored) {
    }
}

