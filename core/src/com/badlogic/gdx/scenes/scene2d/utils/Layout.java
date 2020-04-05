// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

public interface Layout
{
    void layout();
    
    void invalidate();
    
    void invalidateHierarchy();
    
    void validate();
    
    void pack();
    
    void setFillParent(final boolean p0);
    
    void setLayoutEnabled(final boolean p0);
    
    float getMinWidth();
    
    float getMinHeight();
    
    float getPrefWidth();
    
    float getPrefHeight();
    
    float getMaxWidth();
    
    float getMaxHeight();
}
