// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.utils.ShortArray;

public class SphereShapeBuilder extends BaseShapeBuilder
{
    private static final ShortArray tmpIndices;
    private static final Matrix3 normalTransform;
    
    static {
        tmpIndices = new ShortArray();
        normalTransform = new Matrix3();
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final float depth, final int divisionsU, final int divisionsV) {
        build(builder, width, height, depth, divisionsU, divisionsV, 0.0f, 360.0f, 0.0f, 180.0f);
    }
    
    @Deprecated
    public static void build(final MeshPartBuilder builder, final Matrix4 transform, final float width, final float height, final float depth, final int divisionsU, final int divisionsV) {
        build(builder, transform, width, height, depth, divisionsU, divisionsV, 0.0f, 360.0f, 0.0f, 180.0f);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final float depth, final int divisionsU, final int divisionsV, final float angleUFrom, final float angleUTo, final float angleVFrom, final float angleVTo) {
        build(builder, SphereShapeBuilder.matTmp1.idt(), width, height, depth, divisionsU, divisionsV, angleUFrom, angleUTo, angleVFrom, angleVTo);
    }
    
    @Deprecated
    public static void build(final MeshPartBuilder builder, final Matrix4 transform, final float width, final float height, final float depth, final int divisionsU, final int divisionsV, final float angleUFrom, final float angleUTo, final float angleVFrom, final float angleVTo) {
        final float hw = width * 0.5f;
        final float hh = height * 0.5f;
        final float hd = depth * 0.5f;
        final float auo = 0.017453292f * angleUFrom;
        final float stepU = 0.017453292f * (angleUTo - angleUFrom) / divisionsU;
        final float avo = 0.017453292f * angleVFrom;
        final float stepV = 0.017453292f * (angleVTo - angleVFrom) / divisionsV;
        final float us = 1.0f / divisionsU;
        final float vs = 1.0f / divisionsV;
        float u = 0.0f;
        float v = 0.0f;
        float angleU = 0.0f;
        float angleV = 0.0f;
        final MeshPartBuilder.VertexInfo set;
        final MeshPartBuilder.VertexInfo vertexInfo2;
        final MeshPartBuilder.VertexInfo vertexInfo;
        final MeshPartBuilder.VertexInfo curr1 = vertexInfo = (vertexInfo2 = (set = SphereShapeBuilder.vertTmp3.set(null, null, null, null)));
        final boolean hasUV = true;
        vertexInfo.hasNormal = hasUV;
        vertexInfo2.hasPosition = hasUV;
        set.hasUV = hasUV;
        SphereShapeBuilder.normalTransform.set(transform);
        final int s = divisionsU + 3;
        SphereShapeBuilder.tmpIndices.clear();
        SphereShapeBuilder.tmpIndices.ensureCapacity(divisionsU * 2);
        SphereShapeBuilder.tmpIndices.size = s;
        int tempOffset = 0;
        builder.ensureVertices((divisionsV + 1) * (divisionsU + 1));
        builder.ensureRectangleIndices(divisionsU);
        for (int iv = 0; iv <= divisionsV; ++iv) {
            angleV = avo + stepV * iv;
            v = vs * iv;
            final float t = MathUtils.sin(angleV);
            final float h = MathUtils.cos(angleV) * hh;
            for (int iu = 0; iu <= divisionsU; ++iu) {
                angleU = auo + stepU * iu;
                u = 1.0f - us * iu;
                curr1.position.set(MathUtils.cos(angleU) * hw * t, h, MathUtils.sin(angleU) * hd * t);
                curr1.normal.set(curr1.position).mul(SphereShapeBuilder.normalTransform).nor();
                curr1.position.mul(transform);
                curr1.uv.set(u, v);
                SphereShapeBuilder.tmpIndices.set(tempOffset, builder.vertex(curr1));
                final int o = tempOffset + s;
                if (iv > 0 && iu > 0) {
                    builder.rect(SphereShapeBuilder.tmpIndices.get(tempOffset), SphereShapeBuilder.tmpIndices.get((o - 1) % s), SphereShapeBuilder.tmpIndices.get((o - (divisionsU + 2)) % s), SphereShapeBuilder.tmpIndices.get((o - (divisionsU + 1)) % s));
                }
                tempOffset = (tempOffset + 1) % SphereShapeBuilder.tmpIndices.size;
            }
        }
    }
}
