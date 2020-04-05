// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Drawable
{
    void draw(final Batch p0, final float p1, final float p2, final float p3, final float p4);
    
    float getLeftWidth();
    
    void setLeftWidth(final float p0);
    
    float getRightWidth();
    
    void setRightWidth(final float p0);
    
    float getTopHeight();
    
    void setTopHeight(final float p0);
    
    float getBottomHeight();
    
    void setBottomHeight(final float p0);
    
    float getMinWidth();
    
    void setMinWidth(final float p0);
    
    float getMinHeight();
    
    void setMinHeight(final float p0);
}
