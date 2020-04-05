// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class PolygonSprite
{
    PolygonRegion region;
    private float x;
    private float y;
    private float width;
    private float height;
    private float scaleX;
    private float scaleY;
    private float rotation;
    private float originX;
    private float originY;
    private float[] vertices;
    private boolean dirty;
    private Rectangle bounds;
    private final Color color;
    
    public PolygonSprite(final PolygonRegion region) {
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.bounds = new Rectangle();
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.setRegion(region);
        this.setSize((float)region.region.regionWidth, (float)region.region.regionHeight);
        this.setOrigin(this.width / 2.0f, this.height / 2.0f);
    }
    
    public PolygonSprite(final PolygonSprite sprite) {
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.bounds = new Rectangle();
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.set(sprite);
    }
    
    public void set(final PolygonSprite sprite) {
        if (sprite == null) {
            throw new IllegalArgumentException("sprite cannot be null.");
        }
        this.setRegion(sprite.region);
        this.x = sprite.x;
        this.y = sprite.y;
        this.width = sprite.width;
        this.height = sprite.height;
        this.originX = sprite.originX;
        this.originY = sprite.originY;
        this.rotation = sprite.rotation;
        this.scaleX = sprite.scaleX;
        this.scaleY = sprite.scaleY;
        this.color.set(sprite.color);
    }
    
    public void setBounds(final float x, final float y, final float width, final float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dirty = true;
    }
    
    public void setSize(final float width, final float height) {
        this.width = width;
        this.height = height;
        this.dirty = true;
    }
    
    public void setPosition(final float x, final float y) {
        this.translate(x - this.x, y - this.y);
    }
    
    public void setX(final float x) {
        this.translateX(x - this.x);
    }
    
    public void setY(final float y) {
        this.translateY(y - this.y);
    }
    
    public void translateX(final float xAmount) {
        this.x += xAmount;
        if (this.dirty) {
            return;
        }
        final float[] vertices = this.vertices;
        for (int i = 0; i < vertices.length; i += 9) {
            final float[] array = vertices;
            final int n = i;
            array[n] += xAmount;
        }
    }
    
    public void translateY(final float yAmount) {
        this.y += yAmount;
        if (this.dirty) {
            return;
        }
        final float[] vertices = this.vertices;
        for (int i = 1; i < vertices.length; i += 9) {
            final float[] array = vertices;
            final int n = i;
            array[n] += yAmount;
        }
    }
    
    public void translate(final float xAmount, final float yAmount) {
        this.x += xAmount;
        this.y += yAmount;
        if (this.dirty) {
            return;
        }
        final float[] vertices = this.vertices;
        for (int i = 0; i < vertices.length; i += 9) {
            final float[] array = vertices;
            final int n = i;
            array[n] += xAmount;
            final float[] array2 = vertices;
            final int n2 = i + 1;
            array2[n2] += yAmount;
        }
    }
    
    public void setColor(final Color tint) {
        this.color.set(tint);
        final float color = tint.toFloatBits();
        final float[] vertices = this.vertices;
        for (int i = 2; i < vertices.length; i += 9) {
            vertices[i] = color;
        }
    }
    
    public void setColor(final float r, final float g, final float b, final float a) {
        this.color.set(r, g, b, a);
        final float packedColor = this.color.toFloatBits();
        final float[] vertices = this.vertices;
        for (int i = 2; i < vertices.length; i += 9) {
            vertices[i] = packedColor;
        }
    }
    
    public void setOrigin(final float originX, final float originY) {
        this.originX = originX;
        this.originY = originY;
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
    
    public void setScale(final float scaleXY) {
        this.scaleX = scaleXY;
        this.scaleY = scaleXY;
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
    
    public float[] getVertices() {
        if (!this.dirty) {
            return this.vertices;
        }
        this.dirty = false;
        final float originX = this.originX;
        final float originY = this.originY;
        final float scaleX = this.scaleX;
        final float scaleY = this.scaleY;
        final PolygonRegion region = this.region;
        final float[] vertices = this.vertices;
        final float[] regionVertices = region.vertices;
        final float worldOriginX = this.x + originX;
        final float worldOriginY = this.y + originY;
        final float sX = this.width / region.region.getRegionWidth();
        final float sY = this.height / region.region.getRegionHeight();
        final float cos = MathUtils.cosDeg(this.rotation);
        final float sin = MathUtils.sinDeg(this.rotation);
        for (int i = 0, v = 0, n = regionVertices.length; i < n; i += 2, v += 5) {
            final float fx = (regionVertices[i] * sX - originX) * scaleX;
            final float fy = (regionVertices[i + 1] * sY - originY) * scaleY;
            vertices[v] = cos * fx - sin * fy + worldOriginX;
            vertices[v + 1] = sin * fx + cos * fy + worldOriginY;
        }
        return vertices;
    }
    
    public Rectangle getBoundingRectangle() {
        final float[] vertices = this.getVertices();
        float minx = vertices[0];
        float miny = vertices[1];
        float maxx = vertices[0];
        float maxy = vertices[1];
        for (int i = 5; i < vertices.length; i += 5) {
            final float x = vertices[i];
            final float y = vertices[i + 1];
            minx = ((minx > x) ? x : minx);
            maxx = ((maxx < x) ? x : maxx);
            miny = ((miny > y) ? y : miny);
            maxy = ((maxy < y) ? y : maxy);
        }
        this.bounds.x = minx;
        this.bounds.y = miny;
        this.bounds.width = maxx - minx;
        this.bounds.height = maxy - miny;
        return this.bounds;
    }
    
    public void draw(final PolygonSpriteBatch spriteBatch) {
        final PolygonRegion region = this.region;
        spriteBatch.draw(region.region.texture, this.getVertices(), 0, this.vertices.length, region.triangles, 0, region.triangles.length);
    }
    
    public void draw(final PolygonSpriteBatch spriteBatch, final float alphaModulation) {
        final Color color = this.getColor();
        final float oldAlpha = color.a;
        final Color color2 = color;
        color2.a *= alphaModulation;
        this.setColor(color);
        this.draw(spriteBatch);
        color.a = oldAlpha;
        this.setColor(color);
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public float getHeight() {
        return this.height;
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
    
    public Color getColor() {
        return this.color;
    }
    
    public Color getPackedColor() {
        Color.abgr8888ToColor(this.color, this.vertices[2]);
        return this.color;
    }
    
    public void setRegion(final PolygonRegion region) {
        this.region = region;
        final float[] regionVertices = region.vertices;
        final float[] textureCoords = region.textureCoords;
        final int verticesLength = regionVertices.length / 2 * 5;
        if (this.vertices == null || this.vertices.length != verticesLength) {
            this.vertices = new float[verticesLength];
        }
        final float floatColor = this.color.toFloatBits();
        final float[] vertices = this.vertices;
        int i = 0;
        for (int v = 2; v < verticesLength; v += 5) {
            vertices[v] = floatColor;
            vertices[v + 1] = textureCoords[i];
            vertices[v + 2] = textureCoords[i + 1];
            i += 2;
        }
        this.dirty = true;
    }
    
    public PolygonRegion getRegion() {
        return this.region;
    }
}
