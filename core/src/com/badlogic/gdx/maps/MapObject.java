// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps;

import com.badlogic.gdx.graphics.Color;

public class MapObject
{
    private String name;
    private float opacity;
    private boolean visible;
    private MapProperties properties;
    private Color color;
    
    public MapObject() {
        this.name = "";
        this.opacity = 1.0f;
        this.visible = true;
        this.properties = new MapProperties();
        this.color = Color.WHITE.cpy();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setColor(final Color color) {
        this.color = color;
    }
    
    public float getOpacity() {
        return this.opacity;
    }
    
    public void setOpacity(final float opacity) {
        this.opacity = opacity;
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
}
