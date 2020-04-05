// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

public class PolygonRegion
{
    final float[] textureCoords;
    final float[] vertices;
    final short[] triangles;
    final TextureRegion region;
    
    public PolygonRegion(final TextureRegion region, final float[] vertices, final short[] triangles) {
        this.region = region;
        this.vertices = vertices;
        this.triangles = triangles;
        final float[] textureCoords2 = new float[vertices.length];
        this.textureCoords = textureCoords2;
        final float[] textureCoords = textureCoords2;
        final float u = region.u;
        final float v = region.v;
        final float uvWidth = region.u2 - u;
        final float uvHeight = region.v2 - v;
        final int width = region.regionWidth;
        final int height = region.regionHeight;
        for (int i = 0, n = vertices.length; i < n; i += 2) {
            textureCoords[i] = u + uvWidth * (vertices[i] / width);
            textureCoords[i + 1] = v + uvHeight * (1.0f - vertices[i + 1] / height);
        }
    }
    
    public float[] getVertices() {
        return this.vertices;
    }
    
    public short[] getTriangles() {
        return this.triangles;
    }
    
    public float[] getTextureCoords() {
        return this.textureCoords;
    }
    
    public TextureRegion getRegion() {
        return this.region;
    }
}
