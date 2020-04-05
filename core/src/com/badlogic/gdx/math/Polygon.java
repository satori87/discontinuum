// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

public class Polygon implements Shape2D
{
    private float[] localVertices;
    private float[] worldVertices;
    private float x;
    private float y;
    private float originX;
    private float originY;
    private float rotation;
    private float scaleX;
    private float scaleY;
    private boolean dirty;
    private Rectangle bounds;
    
    public Polygon() {
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        this.localVertices = new float[0];
    }
    
    public Polygon(final float[] vertices) {
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        if (vertices.length < 6) {
            throw new IllegalArgumentException("polygons must contain at least 3 points.");
        }
        this.localVertices = vertices;
    }
    
    public float[] getVertices() {
        return this.localVertices;
    }
    
    public float[] getTransformedVertices() {
        if (!this.dirty) {
            return this.worldVertices;
        }
        this.dirty = false;
        final float[] localVertices = this.localVertices;
        if (this.worldVertices == null || this.worldVertices.length != localVertices.length) {
            this.worldVertices = new float[localVertices.length];
        }
        final float[] worldVertices = this.worldVertices;
        final float positionX = this.x;
        final float positionY = this.y;
        final float originX = this.originX;
        final float originY = this.originY;
        final float scaleX = this.scaleX;
        final float scaleY = this.scaleY;
        final boolean scale = scaleX != 1.0f || scaleY != 1.0f;
        final float rotation = this.rotation;
        final float cos = MathUtils.cosDeg(rotation);
        final float sin = MathUtils.sinDeg(rotation);
        for (int i = 0, n = localVertices.length; i < n; i += 2) {
            float x = localVertices[i] - originX;
            float y = localVertices[i + 1] - originY;
            if (scale) {
                x *= scaleX;
                y *= scaleY;
            }
            if (rotation != 0.0f) {
                final float oldX = x;
                x = cos * x - sin * y;
                y = sin * oldX + cos * y;
            }
            worldVertices[i] = positionX + x + originX;
            worldVertices[i + 1] = positionY + y + originY;
        }
        return worldVertices;
    }
    
    public void setOrigin(final float originX, final float originY) {
        this.originX = originX;
        this.originY = originY;
        this.dirty = true;
    }
    
    public void setPosition(final float x, final float y) {
        this.x = x;
        this.y = y;
        this.dirty = true;
    }
    
    public void setVertices(final float[] vertices) {
        if (vertices.length < 6) {
            throw new IllegalArgumentException("polygons must contain at least 3 points.");
        }
        this.localVertices = vertices;
        this.dirty = true;
    }
    
    public void translate(final float x, final float y) {
        this.x += x;
        this.y += y;
        this.dirty = true;
    }
    
    public void setRotation(final float degrees) {
        this.rotation = degrees;
        this.dirty = true;
    }
    
    public void rotate(final float degrees) {
        this.rotation += degrees;
        this.dirty = true;
    }
    
    public void setScale(final float scaleX, final float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.dirty = true;
    }
    
    public void scale(final float amount) {
        this.scaleX += amount;
        this.scaleY += amount;
        this.dirty = true;
    }
    
    public void dirty() {
        this.dirty = true;
    }
    
    public float area() {
        final float[] vertices = this.getTransformedVertices();
        return GeometryUtils.polygonArea(vertices, 0, vertices.length);
    }
    
    public Rectangle getBoundingRectangle() {
        final float[] vertices = this.getTransformedVertices();
        float minX = vertices[0];
        float minY = vertices[1];
        float maxX = vertices[0];
        float maxY = vertices[1];
        for (int numFloats = vertices.length, i = 2; i < numFloats; i += 2) {
            minX = ((minX > vertices[i]) ? vertices[i] : minX);
            minY = ((minY > vertices[i + 1]) ? vertices[i + 1] : minY);
            maxX = ((maxX < vertices[i]) ? vertices[i] : maxX);
            maxY = ((maxY < vertices[i + 1]) ? vertices[i + 1] : maxY);
        }
        if (this.bounds == null) {
            this.bounds = new Rectangle();
        }
        this.bounds.x = minX;
        this.bounds.y = minY;
        this.bounds.width = maxX - minX;
        this.bounds.height = maxY - minY;
        return this.bounds;
    }
    
    @Override
    public boolean contains(final float x, final float y) {
        final float[] vertices = this.getTransformedVertices();
        final int numFloats = vertices.length;
        int intersects = 0;
        for (int i = 0; i < numFloats; i += 2) {
            final float x2 = vertices[i];
            final float y2 = vertices[i + 1];
            final float x3 = vertices[(i + 2) % numFloats];
            final float y3 = vertices[(i + 3) % numFloats];
            if (((y2 <= y && y < y3) || (y3 <= y && y < y2)) && x < (x3 - x2) / (y3 - y2) * (y - y2) + x2) {
                ++intersects;
            }
        }
        return (intersects & 0x1) == 0x1;
    }
    
    @Override
    public boolean contains(final Vector2 point) {
        return this.contains(point.x, point.y);
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getOriginX() {
        return this.originX;
    }
    
    public float getOriginY() {
        return this.originY;
    }
    
    public float getRotation() {
        return this.rotation;
    }
    
    public float getScaleX() {
        return this.scaleX;
    }
    
    public float getScaleY() {
        return this.scaleY;
    }
}
