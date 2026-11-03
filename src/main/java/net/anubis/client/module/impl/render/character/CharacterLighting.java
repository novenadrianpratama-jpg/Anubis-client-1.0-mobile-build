/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2874$class_12325
 *  net.minecraft.class_308$class_11274
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.anubis.client.module.impl.render.character;

import net.anubis.client.module.impl.render.character.kit.Lights;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2874;
import net.minecraft.class_308;
import net.minecraft.class_310;
import net.minecraft.class_638;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public final class CharacterLighting {
    private static final Vector3f LIGHT_0 = new Vector3f(0.2f, 1.0f, -0.7f).normalize();
    private static final Vector3f LIGHT_1 = new Vector3f(-0.2f, 1.0f, 0.7f).normalize();
    private static final Lights NETHER = new Lights(0.2f, 1.0f, -0.7f, -0.2f, -1.0f, 0.7f);
    private static final Lights INVENTORY = new Lights(0.2f, -1.0f, 1.0f, -0.2f, -1.0f, 0.0f);
    private static final Lights ITEMS_FLAT = CharacterLighting.transformed(new Matrix4f().rotationY(-0.3926991f).rotateX(2.3561945f));
    private static final Lights ITEMS_3D = CharacterLighting.transformed(new Matrix4f().scaling(1.0f, -1.0f, 1.0f).rotateYXZ(1.0821041f, 3.2375858f, 0.0f).rotateYXZ(-0.3926991f, 2.3561945f, 0.0f));
    private static class_308.class_11274 entry = class_308.class_11274.field_60025;

    private CharacterLighting() {
    }

    public static void setup(class_308.class_11274 current) {
        entry = current;
    }

    static Lights current() {
        return switch (entry) {
            default -> throw new MatchException(null, null);
            case class_308.class_11274.field_60025 -> {
                if (CharacterLighting.nether()) {
                    yield NETHER;
                }
                yield Lights.WORLD;
            }
            case class_308.class_11274.field_60026 -> ITEMS_FLAT;
            case class_308.class_11274.field_60027 -> ITEMS_3D;
            case class_308.class_11274.field_60028, class_308.class_11274.field_60029 -> INVENTORY;
        };
    }

    private static boolean nether() {
        class_638 level = class_310.method_1551().field_1687;
        return level != null && level.method_8597().comp_5221() == class_2874.class_12325.field_64381;
    }

    private static Lights transformed(Matrix4f matrix) {
        Vector3f a = matrix.transformDirection((Vector3fc)LIGHT_0, new Vector3f());
        Vector3f b = matrix.transformDirection((Vector3fc)LIGHT_1, new Vector3f());
        return new Lights(a.x, a.y, a.z, b.x, b.y, b.z);
    }
}

