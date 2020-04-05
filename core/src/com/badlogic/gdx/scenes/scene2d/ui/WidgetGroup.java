// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.Group;

public class WidgetGroup extends Group implements Layout
{
    private boolean needsLayout;
    private boolean fillParent;
    private boolean layoutEnabled;
    
    public WidgetGroup() {
        this.needsLayout = true;
        this.layoutEnabled = true;
    }
    
    public WidgetGroup(final Actor... actors) {
        this.needsLayout = true;
        this.layoutEnabled = true;
        for (final Actor actor : actors) {
            this.addActor(actor);
        }
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
        if (this.layoutEnabled == enabled) {
            return;
        }
        this.setLayoutEnabled(this, this.layoutEnabled = enabled);
    }
    
    private void setLayoutEnabled(final Group parent, final boolean enabled) {
        final SnapshotArray<Actor> children = parent.getChildren();
        for (int i = 0, n = children.size; i < n; ++i) {
            final Actor actor = children.get(i);
            if (actor instanceof Layout) {
                ((Layout)actor).setLayoutEnabled(enabled);
            }
            else if (actor instanceof Group) {
                this.setLayoutEnabled((Group)actor, enabled);
            }
        }
    }
    
    @Override
    public void validate() {
        if (!this.layoutEnabled) {
            return;
        }
        Group parent = this.getParent();
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
            if (this.getWidth() != parentWidth || this.getHeight() != parentHeight) {
                this.setWidth(parentWidth);
                this.setHeight(parentHeight);
                this.invalidate();
            }
        }
        if (!this.needsLayout) {
            return;
        }
        this.needsLayout = false;
        this.layout();
        if (this.needsLayout) {
            while (parent != null) {
                if (parent instanceof WidgetGroup) {
                    return;
                }
                parent = parent.getParent();
            }
            for (int i = 0; i < 5; ++i) {
                this.needsLayout = false;
                this.layout();
                if (!this.needsLayout) {
                    break;
                }
            }
        }
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
        this.invalidate();
        final Group parent = this.getParent();
        if (parent instanceof Layout) {
            ((Layout)parent).invalidateHierarchy();
        }
    }
    
    @Override
    protected void childrenChanged() {
        this.invalidateHierarchy();
    }
    
    @Override
    protected void sizeChanged() {
        this.invalidate();
    }
    
    @Override
    public void pack() {
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
        this.validate();
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
        this.validate();
    }
    
    @Override
    public void setFillParent(final boolean fillParent) {
        this.fillParent = fillParent;
    }
    
    @Override
    public void layout() {
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        this.validate();
        super.draw(batch, parentAlpha);
    }
}
