// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Texture;

public class TextureRegion
{
    Texture texture;
    float u;
    float v;
    float u2;
    float v2;
    int regionWidth;
    int regionHeight;
    
    public TextureRegion() {
    }
    
    public TextureRegion(final Texture texture) {
        if (texture == null) {
            throw new IllegalArgumentException("texture cannot be null.");
        }
        this.texture = texture;
        this.setRegion(0, 0, texture.getWidth(), texture.getHeight());
    }
    
    public TextureRegion(final Texture texture, final int width, final int height) {
        this.texture = texture;
        this.setRegion(0, 0, width, height);
    }
    
    public TextureRegion(final Texture texture, final int x, final int y, final int width, final int height) {
        this.texture = texture;
        this.setRegion(x, y, width, height);
    }
    
    public TextureRegion(final Texture texture, final float u, final float v, final float u2, final float v2) {
        this.texture = texture;
        this.setRegion(u, v, u2, v2);
    }
    
    public TextureRegion(final TextureRegion region) {
        this.setRegion(region);
    }
    
    public TextureRegion(final TextureRegion region, final int x, final int y, final int width, final int height) {
        this.setRegion(region, x, y, width, height);
    }
    
    public void setRegion(final Texture texture) {
        this.texture = texture;
        this.setRegion(0, 0, texture.getWidth(), texture.getHeight());
    }
    
    public void setRegion(final int x, final int y, final int width, final int height) {
        final float invTexWidth = 1.0f / this.texture.getWidth();
        final float invTexHeight = 1.0f / this.texture.getHeight();
        this.setRegion(x * invTexWidth, y * invTexHeight, (x + width) * invTexWidth, (y + height) * invTexHeight);
        this.regionWidth = Math.abs(width);
        this.regionHeight = Math.abs(height);
    }
    
    public void setRegion(float u, float v, float u2, float v2) {
        final int texWidth = this.texture.getWidth();
        final int texHeight = this.texture.getHeight();
        this.regionWidth = Math.round(Math.abs(u2 - u) * texWidth);
        this.regionHeight = Math.round(Math.abs(v2 - v) * texHeight);
        if (this.regionWidth == 1 && this.regionHeight == 1) {
            final float adjustX = 0.25f / texWidth;
            u += adjustX;
            u2 -= adjustX;
            final float adjustY = 0.25f / texHeight;
            v += adjustY;
            v2 -= adjustY;
        }
        this.u = u;
        this.v = v;
        this.u2 = u2;
        this.v2 = v2;
    }
    
    public void setRegion(final TextureRegion region) {
        this.texture = region.texture;
        this.setRegion(region.u, region.v, region.u2, region.v2);
    }
    
    public void setRegion(final TextureRegion region, final int x, final int y, final int width, final int height) {
        this.texture = region.texture;
        this.setRegion(region.getRegionX() + x, region.getRegionY() + y, width, height);
    }
    
    public Texture getTexture() {
        return this.texture;
    }
    
    public void setTexture(final Texture texture) {
        this.texture = texture;
    }
    
    public float getU() {
        return this.u;
    }
    
    public void setU(final float u) {
        this.u = u;
        this.regionWidth = Math.round(Math.abs(this.u2 - u) * this.texture.getWidth());
    }
    
    public float getV() {
        return this.v;
    }
    
    public void setV(final float v) {
        this.v = v;
        this.regionHeight = Math.round(Math.abs(this.v2 - v) * this.texture.getHeight());
    }
    
    public float getU2() {
        return this.u2;
    }
    
    public void setU2(final float u2) {
        this.u2 = u2;
        this.regionWidth = Math.round(Math.abs(u2 - this.u) * this.texture.getWidth());
    }
    
    public float getV2() {
        return this.v2;
    }
    
    public void setV2(final float v2) {
        this.v2 = v2;
        this.regionHeight = Math.round(Math.abs(v2 - this.v) * this.texture.getHeight());
    }
    
    public int getRegionX() {
        return Math.round(this.u * this.texture.getWidth());
    }
    
    public void setRegionX(final int x) {
        this.setU(x / (float)this.texture.getWidth());
    }
    
    public int getRegionY() {
        return Math.round(this.v * this.texture.getHeight());
    }
    
    public void setRegionY(final int y) {
        this.setV(y / (float)this.texture.getHeight());
    }
    
    public int getRegionWidth() {
        return this.regionWidth;
    }
    
    public void setRegionWidth(final int width) {
        if (this.isFlipX()) {
            this.setU(this.u2 + width / (float)this.texture.getWidth());
        }
        else {
            this.setU2(this.u + width / (float)this.texture.getWidth());
        }
    }
    
    public int getRegionHeight() {
        return this.regionHeight;
    }
    
    public void setRegionHeight(final int height) {
        if (this.isFlipY()) {
            this.setV(this.v2 + height / (float)this.texture.getHeight());
        }
        else {
            this.setV2(this.v + height / (float)this.texture.getHeight());
        }
    }
    
    public void flip(final boolean x, final boolean y) {
        if (x) {
            final float temp = this.u;
            this.u = this.u2;
            this.u2 = temp;
        }
        if (y) {
            final float temp = this.v;
            this.v = this.v2;
            this.v2 = temp;
        }
    }
    
    public boolean isFlipX() {
        return this.u > this.u2;
    }
    
    public boolean isFlipY() {
        return this.v > this.v2;
    }
    
    public void scroll(final float xAmount, final float yAmount) {
        if (xAmount != 0.0f) {
            final float width = (this.u2 - this.u) * this.texture.getWidth();
            this.u = (this.u + xAmount) % 1.0f;
            this.u2 = this.u + width / this.texture.getWidth();
        }
        if (yAmount != 0.0f) {
            final float height = (this.v2 - this.v) * this.texture.getHeight();
            this.v = (this.v + yAmount) % 1.0f;
            this.v2 = this.v + height / this.texture.getHeight();
        }
    }
    
    public TextureRegion[][] split(final int tileWidth, final int tileHeight) {
        int x = this.getRegionX();
        int y = this.getRegionY();
        final int width = this.regionWidth;
        final int height = this.regionHeight;
        final int rows = height / tileHeight;
        final int cols = width / tileWidth;
        final int startX = x;
        final TextureRegion[][] tiles = new TextureRegion[rows][cols];
        for (int row = 0; row < rows; ++row, y += tileHeight) {
            x = startX;
            for (int col = 0; col < cols; ++col, x += tileWidth) {
                tiles[row][col] = new TextureRegion(this.texture, x, y, tileWidth, tileHeight);
            }
        }
        return tiles;
    }
    
    public static TextureRegion[][] split(final Texture texture, final int tileWidth, final int tileHeight) {
        final TextureRegion region = new TextureRegion(texture);
        return region.split(tileWidth, tileHeight);
    }
}
