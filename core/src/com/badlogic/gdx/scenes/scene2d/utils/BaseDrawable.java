// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.graphics.g2d.Batch;

public class BaseDrawable implements Drawable
{
    private String name;
    private float leftWidth;
    private float rightWidth;
    private float topHeight;
    private float bottomHeight;
    private float minWidth;
    private float minHeight;
    
    public BaseDrawable() {
    }
    
    public BaseDrawable(final Drawable drawable) {
        if (drawable instanceof BaseDrawable) {
            this.name = ((BaseDrawable)drawable).getName();
        }
        this.leftWidth = drawable.getLeftWidth();
        this.rightWidth = drawable.getRightWidth();
        this.topHeight = drawable.getTopHeight();
        this.bottomHeight = drawable.getBottomHeight();
        this.minWidth = drawable.getMinWidth();
        this.minHeight = drawable.getMinHeight();
    }
    
    @Override
    public void draw(final Batch batch, final float x, final float y, final float width, final float height) {
    }
    
    @Override
    public float getLeftWidth() {
        return this.leftWidth;
    }
    
    @Override
    public void setLeftWidth(final float leftWidth) {
        this.leftWidth = leftWidth;
    }
    
    @Override
    public float getRightWidth() {
        return this.rightWidth;
    }
    
    @Override
    public void setRightWidth(final float rightWidth) {
        this.rightWidth = rightWidth;
    }
    
    @Override
    public float getTopHeight() {
        return this.topHeight;
    }
    
    @Override
    public void setTopHeight(final float topHeight) {
        this.topHeight = topHeight;
    }
    
    @Override
    public float getBottomHeight() {
        return this.bottomHeight;
    }
    
    @Override
    public void setBottomHeight(final float bottomHeight) {
        this.bottomHeight = bottomHeight;
    }
    
    @Override
    public float getMinWidth() {
        return this.minWidth;
    }
    
    @Override
    public void setMinWidth(final float minWidth) {
        this.minWidth = minWidth;
    }
    
    @Override
    public float getMinHeight() {
        return this.minHeight;
    }
    
    @Override
    public void setMinHeight(final float minHeight) {
        this.minHeight = minHeight;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        if (this.name == null) {
            return ClassReflection.getSimpleName(this.getClass());
        }
        return this.name;
    }
}
