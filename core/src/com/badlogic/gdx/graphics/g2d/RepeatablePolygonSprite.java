// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.utils.ShortArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class RepeatablePolygonSprite
{
    private TextureRegion region;
    private float density;
    private boolean dirty;
    private Array<float[]> parts;
    private Array<float[]> vertices;
    private Array<short[]> indices;
    private int cols;
    private int rows;
    private float gridWidth;
    private float gridHeight;
    public float x;
    public float y;
    private Color color;
    private Vector2 offset;
    
    public RepeatablePolygonSprite() {
        this.dirty = true;
        this.parts = new Array<float[]>();
        this.vertices = new Array<float[]>();
        this.indices = new Array<short[]>();
        this.x = 0.0f;
        this.y = 0.0f;
        this.color = Color.WHITE;
        this.offset = new Vector2();
    }
    
    public void setPolygon(final TextureRegion region, final float[] vertices) {
        this.setPolygon(region, vertices, -1.0f);
    }
    
    public void setPolygon(final TextureRegion region, float[] vertices, float density) {
        this.region = region;
        vertices = this.offset(vertices);
        final Polygon polygon = new Polygon(vertices);
        final Polygon tmpPoly = new Polygon();
        final Polygon intersectionPoly = new Polygon();
        final EarClippingTriangulator triangulator = new EarClippingTriangulator();
        final Rectangle boundRect = polygon.getBoundingRectangle();
        if (density == -1.0f) {
            density = boundRect.getWidth() / region.getRegionWidth();
        }
        final float regionAspectRatio = region.getRegionHeight() / (float)region.getRegionWidth();
        this.cols = (int)Math.ceil(density);
        this.gridWidth = boundRect.getWidth() / density;
        this.gridHeight = regionAspectRatio * this.gridWidth;
        this.rows = (int)Math.ceil(boundRect.getHeight() / this.gridHeight);
        for (int col = 0; col < this.cols; ++col) {
            for (int row = 0; row < this.rows; ++row) {
                float[] verts = new float[8];
                int idx = 0;
                verts[idx++] = col * this.gridWidth;
                verts[idx++] = row * this.gridHeight;
                verts[idx++] = col * this.gridWidth;
                verts[idx++] = (row + 1) * this.gridHeight;
                verts[idx++] = (col + 1) * this.gridWidth;
                verts[idx++] = (row + 1) * this.gridHeight;
                verts[idx++] = (col + 1) * this.gridWidth;
                verts[idx] = row * this.gridHeight;
                tmpPoly.setVertices(verts);
                Intersector.intersectPolygons(polygon, tmpPoly, intersectionPoly);
                verts = intersectionPoly.getVertices();
                if (verts.length > 0) {
                    this.parts.add(this.snapToGrid(verts));
                    final ShortArray arr = triangulator.computeTriangles(verts);
                    this.indices.add(arr.toArray());
                }
                else {
                    this.parts.add(null);
                }
            }
        }
        this.buildVertices();
    }
    
    private float[] snapToGrid(final float[] vertices) {
        for (int i = 0; i < vertices.length; i += 2) {
            final float numX = vertices[i] / this.gridWidth % 1.0f;
            final float numY = vertices[i + 1] / this.gridHeight % 1.0f;
            if (numX > 0.99f || numX < 0.01f) {
                vertices[i] = this.gridWidth * Math.round(vertices[i] / this.gridWidth);
            }
            if (numY > 0.99f || numY < 0.01f) {
                vertices[i + 1] = this.gridHeight * Math.round(vertices[i + 1] / this.gridHeight);
            }
        }
        return vertices;
    }
    
    private float[] offset(final float[] vertices) {
        this.offset.set(vertices[0], vertices[1]);
        for (int i = 0; i < vertices.length - 1; i += 2) {
            if (this.offset.x > vertices[i]) {
                this.offset.x = vertices[i];
            }
            if (this.offset.y > vertices[i + 1]) {
                this.offset.y = vertices[i + 1];
            }
        }
        for (int i = 0; i < vertices.length; i += 2) {
            final int n = i;
            vertices[n] -= this.offset.x;
            final int n2 = i + 1;
            vertices[n2] -= this.offset.y;
        }
        return vertices;
    }
    
    private void buildVertices() {
        this.vertices.clear();
        for (int i = 0; i < this.parts.size; ++i) {
            final float[] verts = this.parts.get(i);
            if (verts != null) {
                final float[] fullVerts = new float[5 * verts.length / 2];
                int idx = 0;
                final int col = i / this.rows;
                final int row = i % this.rows;
                for (int j = 0; j < verts.length; j += 2) {
                    fullVerts[idx++] = verts[j] + this.offset.x + this.x;
                    fullVerts[idx++] = verts[j + 1] + this.offset.y + this.y;
                    fullVerts[idx++] = this.color.toFloatBits();
                    float u = verts[j] % this.gridWidth / this.gridWidth;
                    float v = verts[j + 1] % this.gridHeight / this.gridHeight;
                    if (verts[j] == col * this.gridWidth) {
                        u = 0.0f;
                    }
                    if (verts[j] == (col + 1) * this.gridWidth) {
                        u = 1.0f;
                    }
                    if (verts[j + 1] == row * this.gridHeight) {
                        v = 0.0f;
                    }
                    if (verts[j + 1] == (row + 1) * this.gridHeight) {
                        v = 1.0f;
                    }
                    u = this.region.getU() + (this.region.getU2() - this.region.getU()) * u;
                    v = this.region.getV() + (this.region.getV2() - this.region.getV()) * v;
                    fullVerts[idx++] = u;
                    fullVerts[idx++] = v;
                }
                this.vertices.add(fullVerts);
            }
        }
        this.dirty = false;
    }
    
    public void draw(final PolygonSpriteBatch batch) {
        if (this.dirty) {
            this.buildVertices();
        }
        for (int i = 0; i < this.vertices.size; ++i) {
            batch.draw(this.region.getTexture(), this.vertices.get(i), 0, this.vertices.get(i).length, this.indices.get(i), 0, this.indices.get(i).length);
        }
    }
    
    public void setColor(final Color color) {
        this.color = color;
        this.dirty = true;
    }
    
    public void setPosition(final float x, final float y) {
        this.x = x;
        this.y = y;
        this.dirty = true;
    }
}
