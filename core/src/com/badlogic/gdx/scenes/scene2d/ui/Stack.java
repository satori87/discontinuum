// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class Stack extends WidgetGroup
{
    private float prefWidth;
    private float prefHeight;
    private float minWidth;
    private float minHeight;
    private float maxWidth;
    private float maxHeight;
    private boolean sizeInvalid;
    
    public Stack() {
        this.sizeInvalid = true;
        this.setTransform(false);
        this.setWidth(150.0f);
        this.setHeight(150.0f);
        this.setTouchable(Touchable.childrenOnly);
    }
    
    public Stack(final Actor... actors) {
        this();
        for (final Actor actor : actors) {
            this.addActor(actor);
        }
    }
    
    @Override
    public void invalidate() {
        super.invalidate();
        this.sizeInvalid = true;
    }
    
    private void computeSize() {
        this.sizeInvalid = false;
        this.prefWidth = 0.0f;
        this.prefHeight = 0.0f;
        this.minWidth = 0.0f;
        this.minHeight = 0.0f;
        this.maxWidth = 0.0f;
        this.maxHeight = 0.0f;
        final SnapshotArray<Actor> children = this.getChildren();
        for (int i = 0, n = children.size; i < n; ++i) {
            final Actor child = children.get(i);
            float childMaxWidth;
            float childMaxHeight;
            if (child instanceof Layout) {
                final Layout layout = (Layout)child;
                this.prefWidth = Math.max(this.prefWidth, layout.getPrefWidth());
                this.prefHeight = Math.max(this.prefHeight, layout.getPrefHeight());
                this.minWidth = Math.max(this.minWidth, layout.getMinWidth());
                this.minHeight = Math.max(this.minHeight, layout.getMinHeight());
                childMaxWidth = layout.getMaxWidth();
                childMaxHeight = layout.getMaxHeight();
            }
            else {
                this.prefWidth = Math.max(this.prefWidth, child.getWidth());
                this.prefHeight = Math.max(this.prefHeight, child.getHeight());
                this.minWidth = Math.max(this.minWidth, child.getWidth());
                this.minHeight = Math.max(this.minHeight, child.getHeight());
                childMaxWidth = 0.0f;
                childMaxHeight = 0.0f;
            }
            if (childMaxWidth > 0.0f) {
                this.maxWidth = ((this.maxWidth == 0.0f) ? childMaxWidth : Math.min(this.maxWidth, childMaxWidth));
            }
            if (childMaxHeight > 0.0f) {
                this.maxHeight = ((this.maxHeight == 0.0f) ? childMaxHeight : Math.min(this.maxHeight, childMaxHeight));
            }
        }
    }
    
    public void add(final Actor actor) {
        this.addActor(actor);
    }
    
    @Override
    public void layout() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        final float width = this.getWidth();
        final float height = this.getHeight();
        final Array<Actor> children = this.getChildren();
        for (int i = 0, n = children.size; i < n; ++i) {
            final Actor child = children.get(i);
            child.setBounds(0.0f, 0.0f, width, height);
            if (child instanceof Layout) {
                ((Layout)child).validate();
            }
        }
    }
    
    @Override
    public float getPrefWidth() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.prefWidth;
    }
    
    @Override
    public float getPrefHeight() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.prefHeight;
    }
    
    @Override
    public float getMinWidth() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.minWidth;
    }
    
    @Override
    public float getMinHeight() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.minHeight;
    }
    
    @Override
    public float getMaxWidth() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.maxWidth;
    }
    
    @Override
    public float getMaxHeight() {
        if (this.sizeInvalid) {
            this.computeSize();
        }
        return this.maxHeight;
    }
}
