// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;

public class EllipseShapeBuilder extends BaseShapeBuilder
{
    public static void build(final MeshPartBuilder builder, final float radius, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ) {
        build(builder, radius, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, 0.0f, 360.0f);
    }
    
    public static void build(final MeshPartBuilder builder, final float radius, final int divisions, final Vector3 center, final Vector3 normal) {
        build(builder, radius, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z);
    }
    
    public static void build(final MeshPartBuilder builder, final float radius, final int divisions, final Vector3 center, final Vector3 normal, final Vector3 tangent, final Vector3 binormal) {
        build(builder, radius, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z, tangent.x, tangent.y, tangent.z, binormal.x, binormal.y, binormal.z);
    }
    
    public static void build(final MeshPartBuilder builder, final float radius, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float tangentX, final float tangentY, final float tangentZ, final float binormalX, final float binormalY, final float binormalZ) {
        build(builder, radius, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, 0.0f, 360.0f);
    }
    
    public static void build(final MeshPartBuilder builder, final float radius, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float angleFrom, final float angleTo) {
        build(builder, radius * 2.0f, radius * 2.0f, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, angleFrom, angleTo);
    }
    
    public static void build(final MeshPartBuilder builder, final float radius, final int divisions, final Vector3 center, final Vector3 normal, final float angleFrom, final float angleTo) {
        build(builder, radius, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z, angleFrom, angleTo);
    }
    
    public static void build(final MeshPartBuilder builder, final float radius, final int divisions, final Vector3 center, final Vector3 normal, final Vector3 tangent, final Vector3 binormal, final float angleFrom, final float angleTo) {
        build(builder, radius, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z, tangent.x, tangent.y, tangent.z, binormal.x, binormal.y, binormal.z, angleFrom, angleTo);
    }
    
    public static void build(final MeshPartBuilder builder, final float radius, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float tangentX, final float tangentY, final float tangentZ, final float binormalX, final float binormalY, final float binormalZ, final float angleFrom, final float angleTo) {
        build(builder, radius * 2.0f, radius * 2.0f, 0.0f, 0.0f, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, angleFrom, angleTo);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ) {
        build(builder, width, height, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, 0.0f, 360.0f);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final int divisions, final Vector3 center, final Vector3 normal) {
        build(builder, width, height, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final int divisions, final Vector3 center, final Vector3 normal, final Vector3 tangent, final Vector3 binormal) {
        build(builder, width, height, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z, tangent.x, tangent.y, tangent.z, binormal.x, binormal.y, binormal.z);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float tangentX, final float tangentY, final float tangentZ, final float binormalX, final float binormalY, final float binormalZ) {
        build(builder, width, height, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, 0.0f, 360.0f);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float angleFrom, final float angleTo) {
        build(builder, width, height, 0.0f, 0.0f, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, angleFrom, angleTo);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final int divisions, final Vector3 center, final Vector3 normal, final float angleFrom, final float angleTo) {
        build(builder, width, height, 0.0f, 0.0f, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z, angleFrom, angleTo);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final int divisions, final Vector3 center, final Vector3 normal, final Vector3 tangent, final Vector3 binormal, final float angleFrom, final float angleTo) {
        build(builder, width, height, 0.0f, 0.0f, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z, tangent.x, tangent.y, tangent.z, binormal.x, binormal.y, binormal.z, angleFrom, angleTo);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float tangentX, final float tangentY, final float tangentZ, final float binormalX, final float binormalY, final float binormalZ, final float angleFrom, final float angleTo) {
        build(builder, width, height, 0.0f, 0.0f, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, tangentX, tangentY, tangentZ, binormalX, binormalY, binormalZ, angleFrom, angleTo);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final float innerWidth, final float innerHeight, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float angleFrom, final float angleTo) {
        EllipseShapeBuilder.tmpV1.set(normalX, normalY, normalZ).crs(0.0f, 0.0f, 1.0f);
        EllipseShapeBuilder.tmpV2.set(normalX, normalY, normalZ).crs(0.0f, 1.0f, 0.0f);
        if (EllipseShapeBuilder.tmpV2.len2() > EllipseShapeBuilder.tmpV1.len2()) {
            EllipseShapeBuilder.tmpV1.set(EllipseShapeBuilder.tmpV2);
        }
        EllipseShapeBuilder.tmpV2.set(EllipseShapeBuilder.tmpV1.nor()).crs(normalX, normalY, normalZ).nor();
        build(builder, width, height, innerWidth, innerHeight, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, EllipseShapeBuilder.tmpV1.x, EllipseShapeBuilder.tmpV1.y, EllipseShapeBuilder.tmpV1.z, EllipseShapeBuilder.tmpV2.x, EllipseShapeBuilder.tmpV2.y, EllipseShapeBuilder.tmpV2.z, angleFrom, angleTo);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final float innerWidth, final float innerHeight, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ) {
        build(builder, width, height, innerWidth, innerHeight, divisions, centerX, centerY, centerZ, normalX, normalY, normalZ, 0.0f, 360.0f);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final float innerWidth, final float innerHeight, final int divisions, final Vector3 center, final Vector3 normal) {
        build(builder, width, height, innerWidth, innerHeight, divisions, center.x, center.y, center.z, normal.x, normal.y, normal.z, 0.0f, 360.0f);
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final float innerWidth, final float innerHeight, final int divisions, final float centerX, final float centerY, final float centerZ, final float normalX, final float normalY, final float normalZ, final float tangentX, final float tangentY, final float tangentZ, final float binormalX, final float binormalY, final float binormalZ, final float angleFrom, final float angleTo) {
        if (innerWidth <= 0.0f || innerHeight <= 0.0f) {
            builder.ensureVertices(divisions + 2);
            builder.ensureTriangleIndices(divisions);
        }
        else if (innerWidth == width && innerHeight == height) {
            builder.ensureVertices(divisions + 1);
            builder.ensureIndices(divisions + 1);
            if (builder.getPrimitiveType() != 1) {
                throw new GdxRuntimeException("Incorrect primitive type : expect GL_LINES because innerWidth == width && innerHeight == height");
            }
        }
        else {
            builder.ensureVertices((divisions + 1) * 2);
            builder.ensureRectangleIndices(divisions + 1);
        }
        final float ao = 0.017453292f * angleFrom;
        final float step = 0.017453292f * (angleTo - angleFrom) / divisions;
        final Vector3 sxEx = EllipseShapeBuilder.tmpV1.set(tangentX, tangentY, tangentZ).scl(width * 0.5f);
        final Vector3 syEx = EllipseShapeBuilder.tmpV2.set(binormalX, binormalY, binormalZ).scl(height * 0.5f);
        final Vector3 sxIn = EllipseShapeBuilder.tmpV3.set(tangentX, tangentY, tangentZ).scl(innerWidth * 0.5f);
        final Vector3 syIn = EllipseShapeBuilder.tmpV4.set(binormalX, binormalY, binormalZ).scl(innerHeight * 0.5f);
        final MeshPartBuilder.VertexInfo set;
        final MeshPartBuilder.VertexInfo vertexInfo2;
        final MeshPartBuilder.VertexInfo vertexInfo;
        final MeshPartBuilder.VertexInfo currIn = vertexInfo = (vertexInfo2 = (set = EllipseShapeBuilder.vertTmp3.set(null, null, null, null)));
        final boolean hasUV = true;
        vertexInfo.hasNormal = hasUV;
        vertexInfo2.hasPosition = hasUV;
        set.hasUV = hasUV;
        currIn.uv.set(0.5f, 0.5f);
        currIn.position.set(centerX, centerY, centerZ);
        currIn.normal.set(normalX, normalY, normalZ);
        final MeshPartBuilder.VertexInfo set2;
        final MeshPartBuilder.VertexInfo vertexInfo4;
        final MeshPartBuilder.VertexInfo vertexInfo3;
        final MeshPartBuilder.VertexInfo currEx = vertexInfo3 = (vertexInfo4 = (set2 = EllipseShapeBuilder.vertTmp4.set(null, null, null, null)));
        final boolean hasUV2 = true;
        vertexInfo3.hasNormal = hasUV2;
        vertexInfo4.hasPosition = hasUV2;
        set2.hasUV = hasUV2;
        currEx.uv.set(0.5f, 0.5f);
        currEx.position.set(centerX, centerY, centerZ);
        currEx.normal.set(normalX, normalY, normalZ);
        final short center = builder.vertex(currEx);
        float angle = 0.0f;
        final float us = 0.5f * (innerWidth / width);
        final float vs = 0.5f * (innerHeight / height);
        short i2 = 0;
        short i3 = 0;
        short i4 = 0;
        for (int j = 0; j <= divisions; ++j) {
            angle = ao + step * j;
            final float x = MathUtils.cos(angle);
            final float y = MathUtils.sin(angle);
            currEx.position.set(centerX, centerY, centerZ).add(sxEx.x * x + syEx.x * y, sxEx.y * x + syEx.y * y, sxEx.z * x + syEx.z * y);
            currEx.uv.set(0.5f + 0.5f * x, 0.5f + 0.5f * y);
            short i5 = builder.vertex(currEx);
            if (innerWidth <= 0.0f || innerHeight <= 0.0f) {
                if (j != 0) {
                    builder.triangle(i5, i2, center);
                }
                i2 = i5;
            }
            else if (innerWidth == width && innerHeight == height) {
                if (j != 0) {
                    builder.line(i5, i2);
                }
                i2 = i5;
            }
            else {
                currIn.position.set(centerX, centerY, centerZ).add(sxIn.x * x + syIn.x * y, sxIn.y * x + syIn.y * y, sxIn.z * x + syIn.z * y);
                currIn.uv.set(0.5f + us * x, 0.5f + vs * y);
                i2 = i5;
                i5 = builder.vertex(currIn);
                if (j != 0) {
                    builder.rect(i5, i2, i4, i3);
                }
                i4 = i2;
                i3 = i5;
            }
        }
    }
}
