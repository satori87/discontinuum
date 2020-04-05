// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface TiledMapTile
{
    int getId();
    
    void setId(final int p0);
    
    BlendMode getBlendMode();
    
    void setBlendMode(final BlendMode p0);
    
    TextureRegion getTextureRegion();
    
    void setTextureRegion(final TextureRegion p0);
    
    float getOffsetX();
    
    void setOffsetX(final float p0);
    
    float getOffsetY();
    
    void setOffsetY(final float p0);
    
    MapProperties getProperties();
    
    MapObjects getObjects();
    
    public enum BlendMode
    {
        NONE("NONE", 0), 
        ALPHA("ALPHA", 1);
        
        private BlendMode(final String name, final int ordinal) {
        }
    }
}
