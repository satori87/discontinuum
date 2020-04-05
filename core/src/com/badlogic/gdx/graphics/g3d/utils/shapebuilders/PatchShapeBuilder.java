// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;

public class PatchShapeBuilder extends BaseShapeBuilder
{
    public static void build(final MeshPartBuilder builder, final MeshPartBuilder.VertexInfo corner00, final MeshPartBuilder.VertexInfo corner10, final MeshPartBuilder.VertexInfo corner11, final MeshPartBuilder.VertexInfo corner01, final int divisionsU, final int divisionsV) {
        if (divisionsU < 1 || divisionsV < 1) {
            throw new GdxRuntimeException("divisionsU and divisionV must be > 0, u,v: " + divisionsU + ", " + divisionsV);
        }
        builder.ensureVertices((divisionsV + 1) * (divisionsU + 1));
        builder.ensureRectangleIndices(divisionsV * divisionsU);
        for (int u = 0; u <= divisionsU; ++u) {
            final float alphaU = u / (float)divisionsU;
            PatchShapeBuilder.vertTmp5.set(corner00).lerp(corner10, alphaU);
            PatchShapeBuilder.vertTmp6.set(corner01).lerp(corner11, alphaU);
            for (int v = 0; v <= divisionsV; ++v) {
                final short idx = builder.vertex(PatchShapeBuilder.vertTmp7.set(PatchShapeBuilder.vertTmp5).lerp(PatchShapeBuilder.vertTmp6, v / (float)divisionsV));
                if (u > 0 && v > 0) {
                    builder.rect((short)(idx - divisionsV - 2), (short)(idx - 1), idx, (short)(idx - divisionsV - 1));
                }
            }
        }
    }
    
    public static void build(final MeshPartBuilder builder, final Vector3 corner00, final Vector3 corner10, final Vector3 corner11, final Vector3 corner01, final Vector3 normal, final int divisionsU, final int divisionsV) {
        build(builder, PatchShapeBuilder.vertTmp1.set(corner00, normal, null, null).setUV(0.0f, 1.0f), PatchShapeBuilder.vertTmp2.set(corner10, normal, null, null).setUV(1.0f, 1.0f), PatchShapeBuilder.vertTmp3.set(corner11, normal, null, null).setUV(1.0f, 0.0f), PatchShapeBuilder.vertTmp4.set(corner01, normal, null, null).setUV(0.0f, 0.0f), divisionsU, divisionsV);
    }
    
    public static void build(final MeshPartBuilder builder, final float x00, final float y00, final float z00, final float x10, final float y10, final float z10, final float x11, final float y11, final float z11, final float x01, final float y01, final float z01, final float normalX, final float normalY, final float normalZ, final int divisionsU, final int divisionsV) {
        build(builder, PatchShapeBuilder.vertTmp1.set(null).setPos(x00, y00, z00).setNor(normalX, normalY, normalZ).setUV(0.0f, 1.0f), PatchShapeBuilder.vertTmp2.set(null).setPos(x10, y10, z10).setNor(normalX, normalY, normalZ).setUV(1.0f, 1.0f), PatchShapeBuilder.vertTmp3.set(null).setPos(x11, y11, z11).setNor(normalX, normalY, normalZ).setUV(1.0f, 0.0f), PatchShapeBuilder.vertTmp4.set(null).setPos(x01, y01, z01).setNor(normalX, normalY, normalZ).setUV(0.0f, 0.0f), divisionsU, divisionsV);
    }
}
