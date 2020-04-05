// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;

public class TiledMapImageLayer extends MapLayer
{
    private TextureRegion region;
    private float x;
    private float y;
    
    public TiledMapImageLayer(final TextureRegion region, final float x, final float y) {
        this.region = region;
        this.x = x;
        this.y = y;
    }
    
    public TextureRegion getTextureRegion() {
        return this.region;
    }
    
    public void setTextureRegion(final TextureRegion region) {
        this.region = region;
    }
    
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
}
