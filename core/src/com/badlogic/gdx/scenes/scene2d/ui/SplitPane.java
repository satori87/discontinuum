// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class SplitPane extends WidgetGroup
{
    SplitPaneStyle style;
    private Actor firstWidget;
    private Actor secondWidget;
    boolean vertical;
    float splitAmount;
    float minAmount;
    float maxAmount;
    private Rectangle firstWidgetBounds;
    private Rectangle secondWidgetBounds;
    Rectangle handleBounds;
    boolean cursorOverHandle;
    private Rectangle tempScissors;
    Vector2 lastPoint;
    Vector2 handlePosition;
    
    public SplitPane(final Actor firstWidget, final Actor secondWidget, final boolean vertical, final Skin skin) {
        this(firstWidget, secondWidget, vertical, skin, "default-" + (vertical ? "vertical" : "horizontal"));
    }
    
    public SplitPane(final Actor firstWidget, final Actor secondWidget, final boolean vertical, final Skin skin, final String styleName) {
        this(firstWidget, secondWidget, vertical, skin.get(styleName, SplitPaneStyle.class));
    }
    
    public SplitPane(final Actor firstWidget, final Actor secondWidget, final boolean vertical, final SplitPaneStyle style) {
        this.splitAmount = 0.5f;
        this.maxAmount = 1.0f;
        this.firstWidgetBounds = new Rectangle();
        this.secondWidgetBounds = new Rectangle();
        this.handleBounds = new Rectangle();
        this.tempScissors = new Rectangle();
        this.lastPoint = new Vector2();
        this.handlePosition = new Vector2();
        this.vertical = vertical;
        this.setStyle(style);
        this.setFirstWidget(firstWidget);
        this.setSecondWidget(secondWidget);
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
        this.initialize();
    }
    
    private void initialize() {
        this.addListener(new InputListener() {
            int draggingPointer = -1;
            
            @Override
            public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                if (this.draggingPointer != -1) {
                    return false;
                }
                if (pointer == 0 && button != 0) {
                    return false;
                }
                if (SplitPane.this.handleBounds.contains(x, y)) {
                    this.draggingPointer = pointer;
                    SplitPane.this.lastPoint.set(x, y);
                    SplitPane.this.handlePosition.set(SplitPane.this.handleBounds.x, SplitPane.this.handleBounds.y);
                    return true;
                }
                return false;
            }
            
            @Override
            public void touchUp(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                if (pointer == this.draggingPointer) {
                    this.draggingPointer = -1;
                }
            }
            
            @Override
            public void touchDragged(final InputEvent event, final float x, final float y, final int pointer) {
                if (pointer != this.draggingPointer) {
                    return;
                }
                final Drawable handle = SplitPane.this.style.handle;
                if (!SplitPane.this.vertical) {
                    final float delta = x - SplitPane.this.lastPoint.x;
                    final float availWidth = SplitPane.this.getWidth() - handle.getMinWidth();
                    float dragX = SplitPane.this.handlePosition.x + delta;
                    SplitPane.this.handlePosition.x = dragX;
                    dragX = Math.max(0.0f, dragX);
                    dragX = Math.min(availWidth, dragX);
                    SplitPane.this.splitAmount = dragX / availWidth;
                    SplitPane.this.lastPoint.set(x, y);
                }
                else {
                    final float delta = y - SplitPane.this.lastPoint.y;
                    final float availHeight = SplitPane.this.getHeight() - handle.getMinHeight();
                    float dragY = SplitPane.this.handlePosition.y + delta;
                    SplitPane.this.handlePosition.y = dragY;
                    dragY = Math.max(0.0f, dragY);
                    dragY = Math.min(availHeight, dragY);
                    SplitPane.this.splitAmount = 1.0f - dragY / availHeight;
                    SplitPane.this.lastPoint.set(x, y);
                }
                SplitPane.this.invalidate();
            }
            
            @Override
            public boolean mouseMoved(final InputEvent event, final float x, final float y) {
                SplitPane.this.cursorOverHandle = SplitPane.this.handleBounds.contains(x, y);
                return false;
            }
        });
    }
    
    public void setStyle(final SplitPaneStyle style) {
        this.style = style;
        this.invalidateHierarchy();
    }
    
    public SplitPaneStyle getStyle() {
        return this.style;
    }
    
    @Override
    public void layout() {
        this.clampSplitAmount();
        if (!this.vertical) {
            this.calculateHorizBoundsAndPositions();
        }
        else {
            this.calculateVertBoundsAndPositions();
        }
        final Actor firstWidget = this.firstWidget;
        if (firstWidget != null) {
            final Rectangle firstWidgetBounds = this.firstWidgetBounds;
            firstWidget.setBounds(firstWidgetBounds.x, firstWidgetBounds.y, firstWidgetBounds.width, firstWidgetBounds.height);
            if (firstWidget instanceof Layout) {
                ((Layout)firstWidget).validate();
            }
        }
        final Actor secondWidget = this.secondWidget;
        if (secondWidget != null) {
            final Rectangle secondWidgetBounds = this.secondWidgetBounds;
            secondWidget.setBounds(secondWidgetBounds.x, secondWidgetBounds.y, secondWidgetBounds.width, secondWidgetBounds.height);
            if (secondWidget instanceof Layout) {
                ((Layout)secondWidget).validate();
            }
        }
    }
    
    @Override
    public float getPrefWidth() {
        final float first = (this.firstWidget == null) ? 0.0f : ((this.firstWidget instanceof Layout) ? ((Layout)this.firstWidget).getPrefWidth() : this.firstWidget.getWidth());
        final float second = (this.secondWidget == null) ? 0.0f : ((this.secondWidget instanceof Layout) ? ((Layout)this.secondWidget).getPrefWidth() : this.secondWidget.getWidth());
        if (this.vertical) {
            return Math.max(first, second);
        }
        return first + this.style.handle.getMinWidth() + second;
    }
    
    @Override
    public float getPrefHeight() {
        final float first = (this.firstWidget == null) ? 0.0f : ((this.firstWidget instanceof Layout) ? ((Layout)this.firstWidget).getPrefHeight() : this.firstWidget.getHeight());
        final float second = (this.secondWidget == null) ? 0.0f : ((this.secondWidget instanceof Layout) ? ((Layout)this.secondWidget).getPrefHeight() : this.secondWidget.getHeight());
        if (!this.vertical) {
            return Math.max(first, second);
        }
        return first + this.style.handle.getMinHeight() + second;
    }
    
    @Override
    public float getMinWidth() {
        final float first = (this.firstWidget instanceof Layout) ? ((Layout)this.firstWidget).getMinWidth() : 0.0f;
        final float second = (this.secondWidget instanceof Layout) ? ((Layout)this.secondWidget).getMinWidth() : 0.0f;
        if (this.vertical) {
            return Math.max(first, second);
        }
        return first + this.style.handle.getMinWidth() + second;
    }
    
    @Override
    public float getMinHeight() {
        final float first = (this.firstWidget instanceof Layout) ? ((Layout)this.firstWidget).getMinHeight() : 0.0f;
        final float second = (this.secondWidget instanceof Layout) ? ((Layout)this.secondWidget).getMinHeight() : 0.0f;
        if (!this.vertical) {
            return Math.max(first, second);
        }
        return first + this.style.handle.getMinHeight() + second;
    }
    
    public void setVertical(final boolean vertical) {
        if (this.vertical == vertical) {
            return;
        }
        this.vertical = vertical;
        this.invalidateHierarchy();
    }
    
    public boolean isVertical() {
        return this.vertical;
    }
    
    private void calculateHorizBoundsAndPositions() {
        final Drawable handle = this.style.handle;
        final float height = this.getHeight();
        final float availWidth = this.getWidth() - handle.getMinWidth();
        final float leftAreaWidth = (float)(int)(availWidth * this.splitAmount);
        final float rightAreaWidth = availWidth - leftAreaWidth;
        final float handleWidth = handle.getMinWidth();
        this.firstWidgetBounds.set(0.0f, 0.0f, leftAreaWidth, height);
        this.secondWidgetBounds.set(leftAreaWidth + handleWidth, 0.0f, rightAreaWidth, height);
        this.handleBounds.set(leftAreaWidth, 0.0f, handleWidth, height);
    }
    
    private void calculateVertBoundsAndPositions() {
        final Drawable handle = this.style.handle;
        final float width = this.getWidth();
        final float height = this.getHeight();
        final float availHeight = height - handle.getMinHeight();
        final float topAreaHeight = (float)(int)(availHeight * this.splitAmount);
        final float bottomAreaHeight = availHeight - topAreaHeight;
        final float handleHeight = handle.getMinHeight();
        this.firstWidgetBounds.set(0.0f, height - topAreaHeight, width, topAreaHeight);
        this.secondWidgetBounds.set(0.0f, 0.0f, width, bottomAreaHeight);
        this.handleBounds.set(0.0f, bottomAreaHeight, width, handleHeight);
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        this.validate();
        final Color color = this.getColor();
        final float alpha = color.a * parentAlpha;
        this.applyTransform(batch, this.computeTransform());
        if (this.firstWidget != null && this.firstWidget.isVisible()) {
            batch.flush();
            this.getStage().calculateScissors(this.firstWidgetBounds, this.tempScissors);
            if (ScissorStack.pushScissors(this.tempScissors)) {
                this.firstWidget.draw(batch, alpha);
                batch.flush();
                ScissorStack.popScissors();
            }
        }
        if (this.secondWidget != null && this.secondWidget.isVisible()) {
            batch.flush();
            this.getStage().calculateScissors(this.secondWidgetBounds, this.tempScissors);
            if (ScissorStack.pushScissors(this.tempScissors)) {
                this.secondWidget.draw(batch, alpha);
                batch.flush();
                ScissorStack.popScissors();
            }
        }
        batch.setColor(color.r, color.g, color.b, alpha);
        this.style.handle.draw(batch, this.handleBounds.x, this.handleBounds.y, this.handleBounds.width, this.handleBounds.height);
        this.resetTransform(batch);
    }
    
    public void setSplitAmount(final float splitAmount) {
        this.splitAmount = splitAmount;
        this.invalidate();
    }
    
    public float getSplitAmount() {
        return this.splitAmount;
    }
    
    protected void clampSplitAmount() {
        float effectiveMinAmount = this.minAmount;
        float effectiveMaxAmount = this.maxAmount;
        if (this.vertical) {
            final float availableHeight = this.getHeight() - this.style.handle.getMinHeight();
            if (this.firstWidget instanceof Layout) {
                effectiveMinAmount = Math.max(effectiveMinAmount, Math.min(((Layout)this.firstWidget).getMinHeight() / availableHeight, 1.0f));
            }
            if (this.secondWidget instanceof Layout) {
                effectiveMaxAmount = Math.min(effectiveMaxAmount, 1.0f - Math.min(((Layout)this.secondWidget).getMinHeight() / availableHeight, 1.0f));
            }
        }
        else {
            final float availableWidth = this.getWidth() - this.style.handle.getMinWidth();
            if (this.firstWidget instanceof Layout) {
                effectiveMinAmount = Math.max(effectiveMinAmount, Math.min(((Layout)this.firstWidget).getMinWidth() / availableWidth, 1.0f));
            }
            if (this.secondWidget instanceof Layout) {
                effectiveMaxAmount = Math.min(effectiveMaxAmount, 1.0f - Math.min(((Layout)this.secondWidget).getMinWidth() / availableWidth, 1.0f));
            }
        }
        if (effectiveMinAmount > effectiveMaxAmount) {
            this.splitAmount = 0.5f * (effectiveMinAmount + effectiveMaxAmount);
        }
        else {
            this.splitAmount = Math.max(Math.min(this.splitAmount, effectiveMaxAmount), effectiveMinAmount);
        }
    }
    
    public float getMinSplitAmount() {
        return this.minAmount;
    }
    
    public void setMinSplitAmount(final float minAmount) {
        if (minAmount < 0.0f || minAmount > 1.0f) {
            throw new GdxRuntimeException("minAmount has to be >= 0 and <= 1");
        }
        this.minAmount = minAmount;
    }
    
    public float getMaxSplitAmount() {
        return this.maxAmount;
    }
    
    public void setMaxSplitAmount(final float maxAmount) {
        if (maxAmount < 0.0f || maxAmount > 1.0f) {
            throw new GdxRuntimeException("maxAmount has to be >= 0 and <= 1");
        }
        this.maxAmount = maxAmount;
    }
    
    public void setFirstWidget(final Actor widget) {
        if (this.firstWidget != null) {
            super.removeActor(this.firstWidget);
        }
        if ((this.firstWidget = widget) != null) {
            super.addActor(widget);
        }
        this.invalidate();
    }
    
    public void setSecondWidget(final Actor widget) {
        if (this.secondWidget != null) {
            super.removeActor(this.secondWidget);
        }
        if ((this.secondWidget = widget) != null) {
            super.addActor(widget);
        }
        this.invalidate();
    }
    
    @Override
    public void addActor(final Actor actor) {
        throw new UnsupportedOperationException("Use SplitPane#setWidget.");
    }
    
    @Override
    public void addActorAt(final int index, final Actor actor) {
        throw new UnsupportedOperationException("Use SplitPane#setWidget.");
    }
    
    @Override
    public void addActorBefore(final Actor actorBefore, final Actor actor) {
        throw new UnsupportedOperationException("Use SplitPane#setWidget.");
    }
    
    @Override
    public boolean removeActor(final Actor actor) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        }
        if (actor == this.firstWidget) {
            this.setFirstWidget(null);
            return true;
        }
        if (actor == this.secondWidget) {
            this.setSecondWidget(null);
            return true;
        }
        return true;
    }
    
    @Override
    public boolean removeActor(final Actor actor, final boolean unfocus) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        }
        if (actor == this.firstWidget) {
            super.removeActor(actor, unfocus);
            this.firstWidget = null;
            this.invalidate();
            return true;
        }
        if (actor == this.secondWidget) {
            super.removeActor(actor, unfocus);
            this.secondWidget = null;
            this.invalidate();
            return true;
        }
        return false;
    }
    
    public boolean isCursorOverHandle() {
        return this.cursorOverHandle;
    }
    
    public static class SplitPaneStyle
    {
        public Drawable handle;
        
        public SplitPaneStyle() {
        }
        
        public SplitPaneStyle(final Drawable handle) {
            this.handle = handle;
        }
        
        public SplitPaneStyle(final SplitPaneStyle style) {
            this.handle = style.handle;
        }
    }
}
