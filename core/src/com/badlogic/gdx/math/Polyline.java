// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

public class Polyline implements Shape2D
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
    private float length;
    private float scaledLength;
    private boolean calculateScaledLength;
    private boolean calculateLength;
    private boolean dirty;
    
    public Polyline() {
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.calculateScaledLength = true;
        this.calculateLength = true;
        this.dirty = true;
        this.localVertices = new float[0];
    }
    
    public Polyline(final float[] vertices) {
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.calculateScaledLength = true;
        this.calculateLength = true;
        this.dirty = true;
        if (vertices.length < 4) {
            throw new IllegalArgumentException("polylines must contain at least 2 points.");
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
        if (this.worldVertices == null || this.worldVertices.length < localVertices.length) {
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
    
    public float getLength() {
        if (!this.calculateLength) {
            return this.length;
        }
        this.calculateLength = false;
        this.length = 0.0f;
        for (int i = 0, n = this.localVertices.length - 2; i < n; i += 2) {
            final float x = this.localVertices[i + 2] - this.localVertices[i];
            final float y = this.localVertices[i + 1] - this.localVertices[i + 3];
            this.length += (float)Math.sqrt(x * x + y * y);
        }
        return this.length;
    }
    
    public float getScaledLength() {
        if (!this.calculateScaledLength) {
            return this.scaledLength;
        }
        this.calculateScaledLength = false;
        this.scaledLength = 0.0f;
        for (int i = 0, n = this.localVertices.length - 2; i < n; i += 2) {
            final float x = this.localVertices[i + 2] * this.scaleX - this.localVertices[i] * this.scaleX;
            final float y = this.localVertices[i + 1] * this.scaleY - this.localVertices[i + 3] * this.scaleY;
            this.scaledLength += (float)Math.sqrt(x * x + y * y);
        }
        return this.scaledLength;
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
        if (vertices.length < 4) {
            throw new IllegalArgumentException("polylines must contain at least 2 points.");
        }
        this.localVertices = vertices;
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
        this.calculateScaledLength = true;
    }
    
    public void scale(final float amount) {
        this.scaleX += amount;
        this.scaleY += amount;
        this.dirty = true;
        this.calculateScaledLength = true;
    }
    
    public void calculateLength() {
        this.calculateLength = true;
    }
    
    public void calculateScaledLength() {
        this.calculateScaledLength = true;
    }
    
    public void dirty() {
        this.dirty = true;
    }
    
    public void translate(final float x, final float y) {
        this.x += x;
        this.y += y;
        this.dirty = true;
    }
    
    @Override
    public boolean contains(final Vector2 point) {
        return false;
    }
    
    @Override
    public boolean contains(final float x, final float y) {
        return false;
    }
}
