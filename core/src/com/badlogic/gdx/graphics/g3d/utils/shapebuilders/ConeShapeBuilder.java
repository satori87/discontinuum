// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;

public class ConeShapeBuilder extends BaseShapeBuilder
{
    public static void build(final MeshPartBuilder builder, final float width, final float height, final float depth, final int divisions) {
        build(builder, width, height, depth, divisions, 0.0f, 360.0f);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final float depth, final int divisions, final float angleFrom, final float angleTo) {
        build(builder, width, height, depth, divisions, angleFrom, angleTo, true);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final float depth, final int divisions, final float angleFrom, final float angleTo, final boolean close) {
        builder.ensureVertices(divisions + 2);
        builder.ensureTriangleIndices(divisions);
        final float hw = width * 0.5f;
        final float hh = height * 0.5f;
        final float hd = depth * 0.5f;
        final float ao = 0.017453292f * angleFrom;
        final float step = 0.017453292f * (angleTo - angleFrom) / divisions;
        final float us = 1.0f / divisions;
        float u = 0.0f;
        float angle = 0.0f;
        final MeshPartBuilder.VertexInfo set;
        final MeshPartBuilder.VertexInfo vertexInfo2;
        final MeshPartBuilder.VertexInfo vertexInfo;
        final MeshPartBuilder.VertexInfo curr1 = vertexInfo = (vertexInfo2 = (set = ConeShapeBuilder.vertTmp3.set(null, null, null, null)));
        final boolean hasUV = true;
        vertexInfo.hasNormal = hasUV;
        vertexInfo2.hasPosition = hasUV;
        set.hasUV = hasUV;
        final MeshPartBuilder.VertexInfo curr2 = ConeShapeBuilder.vertTmp4.set(null, null, null, null).setPos(0.0f, hh, 0.0f).setNor(0.0f, 1.0f, 0.0f).setUV(0.5f, 0.0f);
        final short base = builder.vertex(curr2);
        short i2 = 0;
        for (int j = 0; j <= divisions; ++j) {
            angle = ao + step * j;
            u = 1.0f - us * j;
            curr1.position.set(MathUtils.cos(angle) * hw, 0.0f, MathUtils.sin(angle) * hd);
            curr1.normal.set(curr1.position).nor();
            curr1.position.y = -hh;
            curr1.uv.set(u, 1.0f);
            final short i3 = builder.vertex(curr1);
            if (j != 0) {
                builder.triangle(base, i3, i2);
            }
            i2 = i3;
        }
        if (close) {
            EllipseShapeBuilder.build(builder, width, depth, 0.0f, 0.0f, divisions, 0.0f, -hh, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 180.0f - angleTo, 180.0f - angleFrom);
        }
    }
}
