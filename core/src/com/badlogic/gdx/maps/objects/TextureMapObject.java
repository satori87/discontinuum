// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;

public class TextureMapObject extends MapObject
{
    private float x;
    private float y;
    private float originX;
    private float originY;
    private float scaleX;
    private float scaleY;
    private float rotation;
    private TextureRegion textureRegion;
    
    public float getX() {
        return this.x;
    }
    
    public void setX(final float x) {
        this.x = x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public void setY(final float y) {
        this.y = y;
    }
    
    public float getOriginX() {
        return this.originX;
    }
    
    public void setOriginX(final float x) {
        this.originX = x;
    }
    
    public float getOriginY() {
        return this.originY;
    }
    
    public void setOriginY(final float y) {
        this.originY = y;
    }
    
    public float getScaleX() {
        return this.scaleX;
    }
    
    public void setScaleX(final float x) {
        this.scaleX = x;
    }
    
    public float getScaleY() {
        return this.scaleY;
    }
    
    public void setScaleY(final float y) {
        this.scaleY = y;
    }
    
    public float getRotation() {
        return this.rotation;
    }
    
    public void setRotation(final float rotation) {
        this.rotation = rotation;
    }
    
    public TextureRegion getTextureRegion() {
        return this.textureRegion;
    }
    
    public void setTextureRegion(final TextureRegion region) {
        this.textureRegion = region;
    }
    
    public TextureMapObject() {
        this(null);
    }
    
    public TextureMapObject(final TextureRegion textureRegion) {
        this.x = 0.0f;
        this.y = 0.0f;
        this.originX = 0.0f;
        this.originY = 0.0f;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.rotation = 0.0f;
        this.textureRegion = null;
        this.textureRegion = textureRegion;
    }
}
