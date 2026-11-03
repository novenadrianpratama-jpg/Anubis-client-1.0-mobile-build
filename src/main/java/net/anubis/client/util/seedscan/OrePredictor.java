/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap$Entry
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.shorts.ShortArrayList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338
 */
package net.anubis.client.util.seedscan;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import java.util.Iterator;
import net.anubis.client.util.seedscan.ChunkDecoration;
import net.anubis.client.util.seedscan.GateHeights;
import net.anubis.client.util.seedscan.OreSim;
import net.anubis.client.util.seedscan.OreSpec;
import net.anubis.client.util.seedscan.SeedWorldgen;
import net.anubis.client.util.seedscan.TerrainChunk;
import net.anubis.client.util.seedscan.VBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;

@Environment(value=EnvType.CLIENT)
public final class OrePredictor {
    private static final ThreadLocal<Answers> ANSWERS = ThreadLocal.withInitial(Answers::new);

    private OrePredictor() {
    }

    static boolean baseish(byte kind) {
        return VBlock.isBase(kind) || kind == 11;
    }

    static boolean disagreesOnBase(Inputs inputs, byte model, int x, int y, int z) {
        byte shown = inputs.shown(x, y, z);
        if (shown == 15) {
            return true;
        }
        boolean modelBase = OrePredictor.baseish(model);
        if (shown == 11) {
            return !modelBase || model != 11 && !inputs.predictedOre(x, y, z);
        }
        return modelBase != VBlock.isBase(shown);
    }

    static boolean disagreesOnAir(Inputs inputs, byte model, int x, int y, int z) {
        byte shown = inputs.shown(x, y, z);
        if (shown == 15) {
            return true;
        }
        return model == 0 != (shown == 0) || OrePredictor.disagreesOnBase(inputs, model, x, y, z);
    }

    public static ChunkDecoration predict(SeedWorldgen world, int chunkX, int chunkZ, Inputs inputs) {
        View view = new View(world, chunkX, chunkZ, inputs);
        Iterator<OreSpec> iterator = world.specs().iterator();
        while (iterator.hasNext()) {
            OreSpec spec;
            view.spec = spec = iterator.next();
            OreSim.Chain chain = OreSim.run(spec, chunkX, chunkZ, view);
            ObjectIterator it = chain.writes.long2ByteEntrySet().fastIterator();
            while (it.hasNext()) {
                Long2ByteMap.Entry entry = (Long2ByteMap.Entry)it.next();
                view.builder.add(entry.getLongKey(), ChunkDecoration.pack(spec.order(), spec.kind(), entry.getByteValue() == 2));
            }
            if (!chain.fogged()) continue;
            int[] box = chain.fogBox();
            view.builder.fog(new ChunkDecoration.Fog(spec.order(), spec.kind(), box[0], box[1], box[2], box[3], box[4], box[5]));
        }
        return view.builder.build();
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Inputs {
        public TerrainChunk terrain(int var1, int var2);

        public GateHeights gateHeights(int var1, int var2);

        public boolean othersMayWrite(int var1, int var2, int var3);

        public byte shown(int var1, int var2, int var3);

        public boolean predictedOre(int var1, int var2, int var3);
    }

    @Environment(value=EnvType.CLIENT)
    private static final class View
    implements OreSim.World {
        final SeedWorldgen world;
        final int chunkX;
        final int chunkZ;
        final Inputs inputs;
        final TerrainChunk[] terrains = new TerrainChunk[9];
        final GateHeights[] heights = new GateHeights[9];
        final ChunkDecoration.Builder builder;
        final Answers answers = ANSWERS.get();
        final int generation;
        final int minX;
        final int minZ;
        OreSpec spec;

        View(SeedWorldgen world, int chunkX, int chunkZ, Inputs inputs) {
            this.world = world;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.inputs = inputs;
            this.builder = new ChunkDecoration.Builder(chunkX, chunkZ);
            for (int dx = -1; dx <= 1; ++dx) {
                for (int dz = -1; dz <= 1; ++dz) {
                    this.terrains[(dx + 1) * 3 + dz + 1] = inputs.terrain(chunkX + dx, chunkZ + dz);
                    this.heights[(dx + 1) * 3 + dz + 1] = inputs.gateHeights(chunkX + dx, chunkZ + dz);
                }
            }
            this.generation = ++this.answers.generation;
            this.minX = chunkX - 1 << 4;
            this.minZ = chunkZ - 1 << 4;
        }

        private int around(int x, int z) {
            int dx = (x >> 4) - this.chunkX;
            int dz = (z >> 4) - this.chunkZ;
            if (dx < -1 || dx > 1 || dz < -1 || dz > 1) {
                throw new IllegalStateException("read outside the 3x3 chunks at " + x + ", " + z);
            }
            return (dx + 1) * 3 + dz + 1;
        }

        private TerrainChunk terrain(int x, int z) {
            return this.terrains[this.around(x, z)];
        }

        private byte answers(int x, int y, int z) {
            int lz = z - this.minZ;
            int lx = x - this.minX;
            int index = ((y - -64) * 48 + lz) * 48 + lx;
            if (this.answers.stamp[index] == this.generation) {
                return this.answers.answer[index];
            }
            byte model = this.terrain(x, z).kind(x, y, z);
            int base = VBlock.isBase(model) ? 1 : 0;
            boolean disagrees = OrePredictor.disagreesOnBase(this.inputs, model, x, y, z);
            int forAir = disagrees ? 2 : base;
            int forDraws = forAir;
            if (forDraws == 1 && this.inputs.othersMayWrite(x, y, z)) {
                forDraws = 2;
            }
            int air = OrePredictor.disagreesOnAir(this.inputs, model, x, y, z) ? 2 : (model == 0 ? 1 : 0);
            byte packed = (byte)(forDraws | forAir << 2 | air << 4);
            this.answers.stamp[index] = this.generation;
            this.answers.answer[index] = packed;
            return packed;
        }

        @Override
        public long seed() {
            return this.world.seed;
        }

        @Override
        public int gateLow(int x, int z) {
            return this.heights[this.around(x, z)].low(x, z);
        }

        @Override
        public int gateHigh(int x, int z) {
            return this.heights[this.around(x, z)].high(x, z);
        }

        @Override
        public byte base(int x, int y, int z) {
            ShortArrayList own;
            int base;
            if (this.spec.drawsOnTerrain()) {
                base = this.answers(x, y, z) & 3;
                if (base == 1 && this.ownFogCovers(x, y, z)) {
                    base = 2;
                }
            } else if (this.spec.discard() > 0.0f) {
                base = (byte)(this.answers(x, y, z) >> 2 & 3);
            } else {
                int n = base = VBlock.isBase(this.terrain(x, z).kind(x, y, z)) ? 1 : 0;
            }
            if ((own = this.builder.at(class_2338.method_10064((int)x, (int)y, (int)z))) != null) {
                for (int n = 0; n < own.size(); ++n) {
                    short write = own.getShort(n);
                    if (VBlock.isBase(ChunkDecoration.kind(write))) continue;
                    base = ChunkDecoration.possible(write) && base != 0 ? 2 : 0;
                }
            }
            return (byte)base;
        }

        private boolean ownFogCovers(int x, int y, int z) {
            for (ChunkDecoration.Fog fog : this.builder.fogList()) {
                if (VBlock.isBase(fog.kind()) || !fog.contains(x, y, z)) continue;
                return true;
            }
            return false;
        }

        @Override
        public byte air(int x, int y, int z) {
            if (y < -64 || y > 319) {
                return 1;
            }
            if (this.spec.discard() > 0.0f) {
                return (byte)(this.answers(x, y, z) >> 4 & 3);
            }
            return this.terrain(x, z).kind(x, y, z) == 0 ? (byte)1 : 0;
        }

        @Override
        public boolean inBiome(OreSpec spec, int x, int y, int z) {
            return spec.biomes() == null || spec.inBiome(this.world.biomeAt(x, y, z));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Answers {
        static final int SIZE = 884736;
        final int[] stamp = new int[884736];
        final byte[] answer = new byte[884736];
        int generation;

        private Answers() {
        }
    }
}

