// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;

public class CylinderShapeBuilder extends BaseShapeBuilder
{
    public static void build(final MeshPartBuilder builder, final float width, final float height, final float depth, final int divisions) {
        build(builder, width, height, depth, divisions, 0.0f, 360.0f);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final float depth, final int divisions, final float angleFrom, final float angleTo) {
        build(builder, width, height, depth, divisions, angleFrom, angleTo, true);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final float depth, final int divisions, final float angleFrom, final float angleTo, final boolean close) {
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
        final MeshPartBuilder.VertexInfo curr1 = vertexInfo = (vertexInfo2 = (set = CylinderShapeBuilder.vertTmp3.set(null, null, null, null)));
        final boolean hasUV = true;
        vertexInfo.hasNormal = hasUV;
        vertexInfo2.hasPosition = hasUV;
        set.hasUV = hasUV;
        final MeshPartBuilder.VertexInfo set2;
        final MeshPartBuilder.VertexInfo vertexInfo4;
        final MeshPartBuilder.VertexInfo vertexInfo3;
        final MeshPartBuilder.VertexInfo curr2 = vertexInfo3 = (vertexInfo4 = (set2 = CylinderShapeBuilder.vertTmp4.set(null, null, null, null)));
        final boolean hasUV2 = true;
        vertexInfo3.hasNormal = hasUV2;
        vertexInfo4.hasPosition = hasUV2;
        set2.hasUV = hasUV2;
        short i3 = 0;
        short i4 = 0;
        builder.ensureVertices(2 * (divisions + 1));
        builder.ensureRectangleIndices(divisions);
        for (int j = 0; j <= divisions; ++j) {
            angle = ao + step * j;
            u = 1.0f - us * j;
            curr1.position.set(MathUtils.cos(angle) * hw, 0.0f, MathUtils.sin(angle) * hd);
            curr1.normal.set(curr1.position).nor();
            curr1.position.y = -hh;
            curr1.uv.set(u, 1.0f);
            curr2.position.set(curr1.position);
            curr2.normal.set(curr1.normal);
            curr2.position.y = hh;
            curr2.uv.set(u, 0.0f);
            final short i5 = builder.vertex(curr1);
            final short i6 = builder.vertex(curr2);
            if (j != 0) {
                builder.rect(i3, i6, i5, i4);
            }
            i4 = i5;
            i3 = i6;
        }
        if (close) {
            EllipseShapeBuilder.build(builder, width, depth, 0.0f, 0.0f, divisions, 0.0f, hh, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, angleFrom, angleTo);
            EllipseShapeBuilder.build(builder, width, depth, 0.0f, 0.0f, divisions, 0.0f, -hh, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 180.0f - angleTo, 180.0f - angleFrom);
        }
    }
}
