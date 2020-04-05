// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;

public class BoxShapeBuilder extends BaseShapeBuilder
{
    public static void build(final MeshPartBuilder builder, final BoundingBox box) {
        builder.box(box.getCorner000(BaseShapeBuilder.obtainV3()), box.getCorner010(BaseShapeBuilder.obtainV3()), box.getCorner100(BaseShapeBuilder.obtainV3()), box.getCorner110(BaseShapeBuilder.obtainV3()), box.getCorner001(BaseShapeBuilder.obtainV3()), box.getCorner011(BaseShapeBuilder.obtainV3()), box.getCorner101(BaseShapeBuilder.obtainV3()), box.getCorner111(BaseShapeBuilder.obtainV3()));
        freeAll();
    }
    
    public static void build(final MeshPartBuilder builder, final MeshPartBuilder.VertexInfo corner000, final MeshPartBuilder.VertexInfo corner010, final MeshPartBuilder.VertexInfo corner100, final MeshPartBuilder.VertexInfo corner110, final MeshPartBuilder.VertexInfo corner001, final MeshPartBuilder.VertexInfo corner011, final MeshPartBuilder.VertexInfo corner101, final MeshPartBuilder.VertexInfo corner111) {
        builder.ensureVertices(8);
        final short i000 = builder.vertex(corner000);
        final short i2 = builder.vertex(corner100);
        final short i3 = builder.vertex(corner110);
        final short i4 = builder.vertex(corner010);
        final short i5 = builder.vertex(corner001);
        final short i6 = builder.vertex(corner101);
        final short i7 = builder.vertex(corner111);
        final short i8 = builder.vertex(corner011);
        final int primitiveType = builder.getPrimitiveType();
        if (primitiveType == 1) {
            builder.ensureIndices(24);
            builder.rect(i000, i2, i3, i4);
            builder.rect(i6, i5, i8, i7);
            builder.index(i000, i5, i4, i8, i3, i7, i2, i6);
        }
        else if (primitiveType == 0) {
            builder.ensureRectangleIndices(2);
            builder.rect(i000, i2, i3, i4);
            builder.rect(i6, i5, i8, i7);
        }
        else {
            builder.ensureRectangleIndices(6);
            builder.rect(i000, i2, i3, i4);
            builder.rect(i6, i5, i8, i7);
            builder.rect(i000, i4, i8, i5);
            builder.rect(i6, i7, i3, i2);
            builder.rect(i6, i2, i000, i5);
            builder.rect(i3, i7, i8, i4);
        }
    }
    
    public static void build(final MeshPartBuilder builder, final Vector3 corner000, final Vector3 corner010, final Vector3 corner100, final Vector3 corner110, final Vector3 corner001, final Vector3 corner011, final Vector3 corner101, final Vector3 corner111) {
        if ((builder.getAttributes().getMask() & 0x198L) == 0x0L) {
            build(builder, BoxShapeBuilder.vertTmp1.set(corner000, null, null, null), BoxShapeBuilder.vertTmp2.set(corner010, null, null, null), BoxShapeBuilder.vertTmp3.set(corner100, null, null, null), BoxShapeBuilder.vertTmp4.set(corner110, null, null, null), BoxShapeBuilder.vertTmp5.set(corner001, null, null, null), BoxShapeBuilder.vertTmp6.set(corner011, null, null, null), BoxShapeBuilder.vertTmp7.set(corner101, null, null, null), BoxShapeBuilder.vertTmp8.set(corner111, null, null, null));
        }
        else {
            builder.ensureVertices(24);
            builder.ensureRectangleIndices(6);
            Vector3 nor = BoxShapeBuilder.tmpV1.set(corner000).lerp(corner110, 0.5f).sub(BoxShapeBuilder.tmpV2.set(corner001).lerp(corner111, 0.5f)).nor();
            builder.rect(corner000, corner010, corner110, corner100, nor);
            builder.rect(corner011, corner001, corner101, corner111, nor.scl(-1.0f));
            nor = BoxShapeBuilder.tmpV1.set(corner000).lerp(corner101, 0.5f).sub(BoxShapeBuilder.tmpV2.set(corner010).lerp(corner111, 0.5f)).nor();
            builder.rect(corner001, corner000, corner100, corner101, nor);
            builder.rect(corner010, corner011, corner111, corner110, nor.scl(-1.0f));
            nor = BoxShapeBuilder.tmpV1.set(corner000).lerp(corner011, 0.5f).sub(BoxShapeBuilder.tmpV2.set(corner100).lerp(corner111, 0.5f)).nor();
            builder.rect(corner001, corner011, corner010, corner000, nor);
            builder.rect(corner100, corner110, corner111, corner101, nor.scl(-1.0f));
        }
    }
    
    public static void build(final MeshPartBuilder builder, final Matrix4 transform) {
        build(builder, BaseShapeBuilder.obtainV3().set(-0.5f, -0.5f, -0.5f).mul(transform), BaseShapeBuilder.obtainV3().set(-0.5f, 0.5f, -0.5f).mul(transform), BaseShapeBuilder.obtainV3().set(0.5f, -0.5f, -0.5f).mul(transform), BaseShapeBuilder.obtainV3().set(0.5f, 0.5f, -0.5f).mul(transform), BaseShapeBuilder.obtainV3().set(-0.5f, -0.5f, 0.5f).mul(transform), BaseShapeBuilder.obtainV3().set(-0.5f, 0.5f, 0.5f).mul(transform), BaseShapeBuilder.obtainV3().set(0.5f, -0.5f, 0.5f).mul(transform), BaseShapeBuilder.obtainV3().set(0.5f, 0.5f, 0.5f).mul(transform));
        freeAll();
    }
    
    public static void build(final MeshPartBuilder builder, final float width, final float height, final float depth) {
        build(builder, 0.0f, 0.0f, 0.0f, width, height, depth);
    }
    
    public static void build(final MeshPartBuilder builder, final float x, final float y, final float z, final float width, final float height, final float depth) {
        final float hw = width * 0.5f;
        final float hh = height * 0.5f;
        final float hd = depth * 0.5f;
        final float x2 = x - hw;
        final float y2 = y - hh;
        final float z2 = z - hd;
        final float x3 = x + hw;
        final float y3 = y + hh;
        final float z3 = z + hd;
        build(builder, BaseShapeBuilder.obtainV3().set(x2, y2, z2), BaseShapeBuilder.obtainV3().set(x2, y3, z2), BaseShapeBuilder.obtainV3().set(x3, y2, z2), BaseShapeBuilder.obtainV3().set(x3, y3, z2), BaseShapeBuilder.obtainV3().set(x2, y2, z3), BaseShapeBuilder.obtainV3().set(x2, y3, z3), BaseShapeBuilder.obtainV3().set(x3, y2, z3), BaseShapeBuilder.obtainV3().set(x3, y3, z3));
        freeAll();
    }
}
