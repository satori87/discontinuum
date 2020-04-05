// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Widget extends Actor implements Layout
{
    private boolean needsLayout;
    private boolean fillParent;
    private boolean layoutEnabled;
    
    public Widget() {
        this.needsLayout = true;
        this.layoutEnabled = true;
    }
    
    @Override
    public float getMinWidth() {
        return this.getPrefWidth();
    }
    
    @Override
    public float getMinHeight() {
        return this.getPrefHeight();
    }
    
    @Override
    public float getPrefWidth() {
        return 0.0f;
    }
    
    @Override
    public float getPrefHeight() {
        return 0.0f;
    }
    
    @Override
    public float getMaxWidth() {
        return 0.0f;
    }
    
    @Override
    public float getMaxHeight() {
        return 0.0f;
    }
    
    @Override
    public void setLayoutEnabled(final boolean enabled) {
        this.layoutEnabled = enabled;
        if (enabled) {
            this.invalidateHierarchy();
        }
    }
    
    @Override
    public void validate() {
        if (!this.layoutEnabled) {
            return;
        }
        final Group parent = this.getParent();
        if (this.fillParent && parent != null) {
            final Stage stage = this.getStage();
            float parentWidth;
            float parentHeight;
            if (stage != null && parent == stage.getRoot()) {
                parentWidth = stage.getWidth();
                parentHeight = stage.getHeight();
            }
            else {
                parentWidth = parent.getWidth();
                parentHeight = parent.getHeight();
            }
            this.setSize(parentWidth, parentHeight);
        }
        if (!this.needsLayout) {
            return;
        }
        this.needsLayout = false;
        this.layout();
    }
    
    public boolean needsLayout() {
        return this.needsLayout;
    }
    
    @Override
    public void invalidate() {
        this.needsLayout = true;
    }
    
    @Override
    public void invalidateHierarchy() {
        if (!this.layoutEnabled) {
            return;
        }
        this.invalidate();
        final Group parent = this.getParent();
        if (parent instanceof Layout) {
            ((Layout)parent).invalidateHierarchy();
        }
    }
    
    @Override
    protected void sizeChanged() {
        this.invalidate();
    }
    
    @Override
    public void pack() {
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
        this.validate();
    }
    
    @Override
    public void setFillParent(final boolean fillParent) {
        this.fillParent = fillParent;
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        this.validate();
    }
    
    @Override
    public void layout() {
    }
}
