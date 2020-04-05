// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class MapLayer
{
    private String name;
    private float opacity;
    private boolean visible;
    private float offsetX;
    private float offsetY;
    private float renderOffsetX;
    private float renderOffsetY;
    private boolean renderOffsetDirty;
    private MapLayer parent;
    private MapObjects objects;
    private MapProperties properties;
    
    public MapLayer() {
        this.name = "";
        this.opacity = 1.0f;
        this.visible = true;
        this.renderOffsetDirty = true;
        this.objects = new MapObjects();
        this.properties = new MapProperties();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public float getOpacity() {
        return this.opacity;
    }
    
    public void setOpacity(final float opacity) {
        this.opacity = opacity;
    }
    
    public float getOffsetX() {
        return this.offsetX;
    }
    
    public void setOffsetX(final float offsetX) {
        this.offsetX = offsetX;
        this.invalidateRenderOffset();
    }
    
    public float getOffsetY() {
        return this.offsetY;
    }
    
    public void setOffsetY(final float offsetY) {
        this.offsetY = offsetY;
        this.invalidateRenderOffset();
    }
    
    public float getRenderOffsetX() {
        if (this.renderOffsetDirty) {
            this.calculateRenderOffsets();
        }
        return this.renderOffsetX;
    }
    
    public float getRenderOffsetY() {
        if (this.renderOffsetDirty) {
            this.calculateRenderOffsets();
        }
        return this.renderOffsetY;
    }
    
    public void invalidateRenderOffset() {
        this.renderOffsetDirty = true;
    }
    
    public MapLayer getParent() {
        return this.parent;
    }
    
    public void setParent(final MapLayer parent) {
        if (parent == this) {
            throw new GdxRuntimeException("Can't set self as the parent");
        }
        this.parent = parent;
    }
    
    public MapObjects getObjects() {
        return this.objects;
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
    
    public MapProperties getProperties() {
        return this.properties;
    }
    
    protected void calculateRenderOffsets() {
        if (this.parent != null) {
            this.parent.calculateRenderOffsets();
            this.renderOffsetX = this.parent.getRenderOffsetX() + this.offsetX;
            this.renderOffsetY = this.parent.getRenderOffsetY() + this.offsetY;
        }
        else {
            this.renderOffsetX = this.offsetX;
            this.renderOffsetY = this.offsetY;
        }
        this.renderOffsetDirty = false;
    }
}
