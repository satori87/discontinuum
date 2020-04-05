// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled.tiles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;

public class StaticTiledMapTile implements TiledMapTile
{
    private int id;
    private BlendMode blendMode;
    private MapProperties properties;
    private MapObjects objects;
    private TextureRegion textureRegion;
    private float offsetX;
    private float offsetY;
    
    @Override
    public int getId() {
        return this.id;
    }
    
    @Override
    public void setId(final int id) {
        this.id = id;
    }
    
    @Override
    public BlendMode getBlendMode() {
        return this.blendMode;
    }
    
    @Override
    public void setBlendMode(final BlendMode blendMode) {
        this.blendMode = blendMode;
    }
    
    @Override
    public MapProperties getProperties() {
        if (this.properties == null) {
            this.properties = new MapProperties();
        }
        return this.properties;
    }
    
    @Override
    public MapObjects getObjects() {
        if (this.objects == null) {
            this.objects = new MapObjects();
        }
        return this.objects;
    }
    
    @Override
    public TextureRegion getTextureRegion() {
        return this.textureRegion;
    }
    
    @Override
    public void setTextureRegion(final TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }
    
    @Override
    public float getOffsetX() {
        return this.offsetX;
    }
    
    @Override
    public void setOffsetX(final float offsetX) {
        this.offsetX = offsetX;
    }
    
    @Override
    public float getOffsetY() {
        return this.offsetY;
    }
    
    @Override
    public void setOffsetY(final float offsetY) {
        this.offsetY = offsetY;
    }
    
    public StaticTiledMapTile(final TextureRegion textureRegion) {
        this.blendMode = BlendMode.ALPHA;
        this.textureRegion = textureRegion;
    }
    
    public StaticTiledMapTile(final StaticTiledMapTile copy) {
        this.blendMode = BlendMode.ALPHA;
        if (copy.properties != null) {
            this.getProperties().putAll(copy.properties);
        }
        this.objects = copy.objects;
        this.textureRegion = copy.textureRegion;
        this.id = copy.id;
    }
}
