// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;

public interface MeshPartBuilder
{
    MeshPart getMeshPart();
    
    int getPrimitiveType();
    
    VertexAttributes getAttributes();
    
    void setColor(final Color p0);
    
    void setColor(final float p0, final float p1, final float p2, final float p3);
    
    void setUVRange(final float p0, final float p1, final float p2, final float p3);
    
    void setUVRange(final TextureRegion p0);
    
    Matrix4 getVertexTransform(final Matrix4 p0);
    
    void setVertexTransform(final Matrix4 p0);
    
    boolean isVertexTransformationEnabled();
    
    void setVertexTransformationEnabled(final boolean p0);
    
    void ensureVertices(final int p0);
    
    void ensureIndices(final int p0);
    
    void ensureCapacity(final int p0, final int p1);
    
    void ensureTriangleIndices(final int p0);
    
    void ensureRectangleIndices(final int p0);
    
    short vertex(final float... p0);
    
    short vertex(final Vector3 p0, final Vector3 p1, final Color p2, final Vector2 p3);
    
    short vertex(final VertexInfo p0);
    
    short lastIndex();
    
    void index(final short p0);
    
    void index(final short p0, final short p1);
    
    void index(final short p0, final short p1, final short p2);
    
    void index(final short p0, final short p1, final short p2, final short p3);
    
    void index(final short p0, final short p1, final short p2, final short p3, final short p4, final short p5);
    
    void index(final short p0, final short p1, final short p2, final short p3, final short p4, final short p5, final short p6, final short p7);
    
    void line(final short p0, final short p1);
    
    void line(final VertexInfo p0, final VertexInfo p1);
    
    void line(final Vector3 p0, final Vector3 p1);
    
    void line(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5);
    
    void line(final Vector3 p0, final Color p1, final Vector3 p2, final Color p3);
    
    void triangle(final short p0, final short p1, final short p2);
    
    void triangle(final VertexInfo p0, final VertexInfo p1, final VertexInfo p2);
    
    void triangle(final Vector3 p0, final Vector3 p1, final Vector3 p2);
    
    void triangle(final Vector3 p0, final Color p1, final Vector3 p2, final Color p3, final Vector3 p4, final Color p5);
    
    void rect(final short p0, final short p1, final short p2, final short p3);
    
    void rect(final VertexInfo p0, final VertexInfo p1, final VertexInfo p2, final VertexInfo p3);
    
    void rect(final Vector3 p0, final Vector3 p1, final Vector3 p2, final Vector3 p3, final Vector3 p4);
    
    void rect(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9, final float p10, final float p11, final float p12, final float p13, final float p14);
    
    void addMesh(final Mesh p0);
    
    void addMesh(final MeshPart p0);
    
    void addMesh(final Mesh p0, final int p1, final int p2);
    
    void addMesh(final float[] p0, final short[] p1);
    
    void addMesh(final float[] p0, final short[] p1, final int p2, final int p3);
    
    @Deprecated
    void patch(final VertexInfo p0, final VertexInfo p1, final VertexInfo p2, final VertexInfo p3, final int p4, final int p5);
    
    @Deprecated
    void patch(final Vector3 p0, final Vector3 p1, final Vector3 p2, final Vector3 p3, final Vector3 p4, final int p5, final int p6);
    
    @Deprecated
    void patch(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9, final float p10, final float p11, final float p12, final float p13, final float p14, final int p15, final int p16);
    
    @Deprecated
    void box(final VertexInfo p0, final VertexInfo p1, final VertexInfo p2, final VertexInfo p3, final VertexInfo p4, final VertexInfo p5, final VertexInfo p6, final VertexInfo p7);
    
    @Deprecated
    void box(final Vector3 p0, final Vector3 p1, final Vector3 p2, final Vector3 p3, final Vector3 p4, final Vector3 p5, final Vector3 p6, final Vector3 p7);
    
    @Deprecated
    void box(final Matrix4 p0);
    
    @Deprecated
    void box(final float p0, final float p1, final float p2);
    
    @Deprecated
    void box(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5);
    
    @Deprecated
    void circle(final float p0, final int p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7);
    
    @Deprecated
    void circle(final float p0, final int p1, final Vector3 p2, final Vector3 p3);
    
    @Deprecated
    void circle(final float p0, final int p1, final Vector3 p2, final Vector3 p3, final Vector3 p4, final Vector3 p5);
    
    @Deprecated
    void circle(final float p0, final int p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9, final float p10, final float p11, final float p12, final float p13);
    
    @Deprecated
    void circle(final float p0, final int p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9);
    
    @Deprecated
    void circle(final float p0, final int p1, final Vector3 p2, final Vector3 p3, final float p4, final float p5);
    
    @Deprecated
    void circle(final float p0, final int p1, final Vector3 p2, final Vector3 p3, final Vector3 p4, final Vector3 p5, final float p6, final float p7);
    
    @Deprecated
    void circle(final float p0, final int p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9, final float p10, final float p11, final float p12, final float p13, final float p14, final float p15);
    
    @Deprecated
    void ellipse(final float p0, final float p1, final int p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8);
    
    @Deprecated
    void ellipse(final float p0, final float p1, final int p2, final Vector3 p3, final Vector3 p4);
    
    @Deprecated
    void ellipse(final float p0, final float p1, final int p2, final Vector3 p3, final Vector3 p4, final Vector3 p5, final Vector3 p6);
    
    @Deprecated
    void ellipse(final float p0, final float p1, final int p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9, final float p10, final float p11, final float p12, final float p13, final float p14);
    
    @Deprecated
    void ellipse(final float p0, final float p1, final int p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9, final float p10);
    
    @Deprecated
    void ellipse(final float p0, final float p1, final int p2, final Vector3 p3, final Vector3 p4, final float p5, final float p6);
    
    @Deprecated
    void ellipse(final float p0, final float p1, final int p2, final Vector3 p3, final Vector3 p4, final Vector3 p5, final Vector3 p6, final float p7, final float p8);
    
    @Deprecated
    void ellipse(final float p0, final float p1, final int p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9, final float p10, final float p11, final float p12, final float p13, final float p14, final float p15, final float p16);
    
    @Deprecated
    void ellipse(final float p0, final float p1, final float p2, final float p3, final int p4, final float p5, final float p6, final float p7, final float p8, final float p9, final float p10, final float p11, final float p12, final float p13, final float p14, final float p15, final float p16, final float p17, final float p18);
    
    @Deprecated
    void ellipse(final float p0, final float p1, final float p2, final float p3, final int p4, final float p5, final float p6, final float p7, final float p8, final float p9, final float p10, final float p11, final float p12);
    
    @Deprecated
    void ellipse(final float p0, final float p1, final float p2, final float p3, final int p4, final float p5, final float p6, final float p7, final float p8, final float p9, final float p10);
    
    @Deprecated
    void ellipse(final float p0, final float p1, final float p2, final float p3, final int p4, final Vector3 p5, final Vector3 p6);
    
    @Deprecated
    void cylinder(final float p0, final float p1, final float p2, final int p3);
    
    @Deprecated
    void cylinder(final float p0, final float p1, final float p2, final int p3, final float p4, final float p5);
    
    @Deprecated
    void cylinder(final float p0, final float p1, final float p2, final int p3, final float p4, final float p5, final boolean p6);
    
    @Deprecated
    void cone(final float p0, final float p1, final float p2, final int p3);
    
    @Deprecated
    void cone(final float p0, final float p1, final float p2, final int p3, final float p4, final float p5);
    
    @Deprecated
    void sphere(final float p0, final float p1, final float p2, final int p3, final int p4);
    
    @Deprecated
    void sphere(final Matrix4 p0, final float p1, final float p2, final float p3, final int p4, final int p5);
    
    @Deprecated
    void sphere(final float p0, final float p1, final float p2, final int p3, final int p4, final float p5, final float p6, final float p7, final float p8);
    
    @Deprecated
    void sphere(final Matrix4 p0, final float p1, final float p2, final float p3, final int p4, final int p5, final float p6, final float p7, final float p8, final float p9);
    
    @Deprecated
    void capsule(final float p0, final float p1, final int p2);
    
    @Deprecated
    void arrow(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final int p8);
    
    public static class VertexInfo implements Pool.Poolable
    {
        public final Vector3 position;
        public boolean hasPosition;
        public final Vector3 normal;
        public boolean hasNormal;
        public final Color color;
        public boolean hasColor;
        public final Vector2 uv;
        public boolean hasUV;
        
        public VertexInfo() {
            this.position = new Vector3();
            this.normal = new Vector3(0.0f, 1.0f, 0.0f);
            this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
            this.uv = new Vector2();
        }
        
        @Override
        public void reset() {
            this.position.set(0.0f, 0.0f, 0.0f);
            this.normal.set(0.0f, 1.0f, 0.0f);
            this.color.set(1.0f, 1.0f, 1.0f, 1.0f);
            this.uv.set(0.0f, 0.0f);
        }
        
        public VertexInfo set(final Vector3 pos, final Vector3 nor, final Color col, final Vector2 uv) {
            this.reset();
            this.hasPosition = (pos != null);
            if (this.hasPosition) {
                this.position.set(pos);
            }
            this.hasNormal = (nor != null);
            if (this.hasNormal) {
                this.normal.set(nor);
            }
            this.hasColor = (col != null);
            if (this.hasColor) {
                this.color.set(col);
            }
            this.hasUV = (uv != null);
            if (this.hasUV) {
                this.uv.set(uv);
            }
            return this;
        }
        
        public VertexInfo set(final VertexInfo other) {
            if (other == null) {
                return this.set(null, null, null, null);
            }
            this.hasPosition = other.hasPosition;
            this.position.set(other.position);
            this.hasNormal = other.hasNormal;
            this.normal.set(other.normal);
            this.hasColor = other.hasColor;
            this.color.set(other.color);
            this.hasUV = other.hasUV;
            this.uv.set(other.uv);
            return this;
        }
        
        public VertexInfo setPos(final float x, final float y, final float z) {
            this.position.set(x, y, z);
            this.hasPosition = true;
            return this;
        }
        
        public VertexInfo setPos(final Vector3 pos) {
            this.hasPosition = (pos != null);
            if (this.hasPosition) {
                this.position.set(pos);
            }
            return this;
        }
        
        public VertexInfo setNor(final float x, final float y, final float z) {
            this.normal.set(x, y, z);
            this.hasNormal = true;
            return this;
        }
        
        public VertexInfo setNor(final Vector3 nor) {
            this.hasNormal = (nor != null);
            if (this.hasNormal) {
                this.normal.set(nor);
            }
            return this;
        }
        
        public VertexInfo setCol(final float r, final float g, final float b, final float a) {
            this.color.set(r, g, b, a);
            this.hasColor = true;
            return this;
        }
        
        public VertexInfo setCol(final Color col) {
            this.hasColor = (col != null);
            if (this.hasColor) {
                this.color.set(col);
            }
            return this;
        }
        
        public VertexInfo setUV(final float u, final float v) {
            this.uv.set(u, v);
            this.hasUV = true;
            return this;
        }
        
        public VertexInfo setUV(final Vector2 uv) {
            this.hasUV = (uv != null);
            if (this.hasUV) {
                this.uv.set(uv);
            }
            return this;
        }
        
        public VertexInfo lerp(final VertexInfo target, final float alpha) {
            if (this.hasPosition && target.hasPosition) {
                this.position.lerp(target.position, alpha);
            }
            if (this.hasNormal && target.hasNormal) {
                this.normal.lerp(target.normal, alpha);
            }
            if (this.hasColor && target.hasColor) {
                this.color.lerp(target.color, alpha);
            }
            if (this.hasUV && target.hasUV) {
                this.uv.lerp(target.uv, alpha);
            }
            return this;
        }
    }
}
