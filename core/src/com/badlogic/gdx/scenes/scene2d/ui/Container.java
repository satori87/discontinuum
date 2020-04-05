// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Container<T extends Actor> extends WidgetGroup
{
    private T actor;
    private Value minWidth;
    private Value minHeight;
    private Value prefWidth;
    private Value prefHeight;
    private Value maxWidth;
    private Value maxHeight;
    private Value padTop;
    private Value padLeft;
    private Value padBottom;
    private Value padRight;
    private float fillX;
    private float fillY;
    private int align;
    private Drawable background;
    private boolean clip;
    private boolean round;
    
    public Container() {
        this.minWidth = Value.minWidth;
        this.minHeight = Value.minHeight;
        this.prefWidth = Value.prefWidth;
        this.prefHeight = Value.prefHeight;
        this.maxWidth = Value.zero;
        this.maxHeight = Value.zero;
        this.padTop = Value.zero;
        this.padLeft = Value.zero;
        this.padBottom = Value.zero;
        this.padRight = Value.zero;
        this.round = true;
        this.setTouchable(Touchable.childrenOnly);
        this.setTransform(false);
    }
    
    public Container(final T actor) {
        this();
        this.setActor(actor);
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        this.validate();
        if (this.isTransform()) {
            this.applyTransform(batch, this.computeTransform());
            this.drawBackground(batch, parentAlpha, 0.0f, 0.0f);
            if (this.clip) {
                batch.flush();
                final float padLeft = this.padLeft.get(this);
                final float padBottom = this.padBottom.get(this);
                if (this.clipBegin(padLeft, padBottom, this.getWidth() - padLeft - this.padRight.get(this), this.getHeight() - padBottom - this.padTop.get(this))) {
                    this.drawChildren(batch, parentAlpha);
                    batch.flush();
                    this.clipEnd();
                }
            }
            else {
                this.drawChildren(batch, parentAlpha);
            }
            this.resetTransform(batch);
        }
        else {
            this.drawBackground(batch, parentAlpha, this.getX(), this.getY());
            super.draw(batch, parentAlpha);
        }
    }
    
    protected void drawBackground(final Batch batch, final float parentAlpha, final float x, final float y) {
        if (this.background == null) {
            return;
        }
        final Color color = this.getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        this.background.draw(batch, x, y, this.getWidth(), this.getHeight());
    }
    
    public void setBackground(final Drawable background) {
        this.setBackground(background, true);
    }
    
    public void setBackground(final Drawable background, final boolean adjustPadding) {
        if (this.background == background) {
            return;
        }
        this.background = background;
        if (adjustPadding) {
            if (background == null) {
                this.pad(Value.zero);
            }
            else {
                this.pad(background.getTopHeight(), background.getLeftWidth(), background.getBottomHeight(), background.getRightWidth());
            }
            this.invalidate();
        }
    }
    
    public Container<T> background(final Drawable background) {
        this.setBackground(background);
        return this;
    }
    
    public Drawable getBackground() {
        return this.background;
    }
    
    @Override
    public void layout() {
        if (this.actor == null) {
            return;
        }
        final float padLeft = this.padLeft.get(this);
        final float padBottom = this.padBottom.get(this);
        final float containerWidth = this.getWidth() - padLeft - this.padRight.get(this);
        final float containerHeight = this.getHeight() - padBottom - this.padTop.get(this);
        final float minWidth = this.minWidth.get(this.actor);
        final float minHeight = this.minHeight.get(this.actor);
        final float prefWidth = this.prefWidth.get(this.actor);
        final float prefHeight = this.prefHeight.get(this.actor);
        final float maxWidth = this.maxWidth.get(this.actor);
        final float maxHeight = this.maxHeight.get(this.actor);
        float width;
        if (this.fillX > 0.0f) {
            width = containerWidth * this.fillX;
        }
        else {
            width = Math.min(prefWidth, containerWidth);
        }
        if (width < minWidth) {
            width = minWidth;
        }
        if (maxWidth > 0.0f && width > maxWidth) {
            width = maxWidth;
        }
        float height;
        if (this.fillY > 0.0f) {
            height = containerHeight * this.fillY;
        }
        else {
            height = Math.min(prefHeight, containerHeight);
        }
        if (height < minHeight) {
            height = minHeight;
        }
        if (maxHeight > 0.0f && height > maxHeight) {
            height = maxHeight;
        }
        float x = padLeft;
        if ((this.align & 0x10) != 0x0) {
            x += containerWidth - width;
        }
        else if ((this.align & 0x8) == 0x0) {
            x += (containerWidth - width) / 2.0f;
        }
        float y = padBottom;
        if ((this.align & 0x2) != 0x0) {
            y += containerHeight - height;
        }
        else if ((this.align & 0x4) == 0x0) {
            y += (containerHeight - height) / 2.0f;
        }
        if (this.round) {
            x = (float)Math.round(x);
            y = (float)Math.round(y);
            width = (float)Math.round(width);
            height = (float)Math.round(height);
        }
        this.actor.setBounds(x, y, width, height);
        if (this.actor instanceof Layout) {
            ((Layout)this.actor).validate();
        }
    }
    
    public void setActor(final T actor) {
        if (actor == this) {
            throw new IllegalArgumentException("actor cannot be the Container.");
        }
        if (actor == this.actor) {
            return;
        }
        if (this.actor != null) {
            super.removeActor(this.actor);
        }
        if ((this.actor = actor) != null) {
            super.addActor(actor);
        }
    }
    
    public T getActor() {
        return this.actor;
    }
    
    @Override
    @Deprecated
    public void addActor(final Actor actor) {
        throw new UnsupportedOperationException("Use Container#setActor.");
    }
    
    @Override
    @Deprecated
    public void addActorAt(final int index, final Actor actor) {
        throw new UnsupportedOperationException("Use Container#setActor.");
    }
    
    @Override
    @Deprecated
    public void addActorBefore(final Actor actorBefore, final Actor actor) {
        throw new UnsupportedOperationException("Use Container#setActor.");
    }
    
    @Override
    @Deprecated
    public void addActorAfter(final Actor actorAfter, final Actor actor) {
        throw new UnsupportedOperationException("Use Container#setActor.");
    }
    
    @Override
    public boolean removeActor(final Actor actor) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        }
        if (actor != this.actor) {
            return false;
        }
        this.setActor(null);
        return true;
    }
    
    @Override
    public boolean removeActor(final Actor actor, final boolean unfocus) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        }
        if (actor != this.actor) {
            return false;
        }
        this.actor = null;
        return super.removeActor(actor, unfocus);
    }
    
    public Container<T> size(final Value size) {
        if (size == null) {
            throw new IllegalArgumentException("size cannot be null.");
        }
        this.minWidth = size;
        this.minHeight = size;
        this.prefWidth = size;
        this.prefHeight = size;
        this.maxWidth = size;
        this.maxHeight = size;
        return this;
    }
    
    public Container<T> size(final Value width, final Value height) {
        if (width == null) {
            throw new IllegalArgumentException("width cannot be null.");
        }
        if (height == null) {
            throw new IllegalArgumentException("height cannot be null.");
        }
        this.minWidth = width;
        this.minHeight = height;
        this.prefWidth = width;
        this.prefHeight = height;
        this.maxWidth = width;
        this.maxHeight = height;
        return this;
    }
    
    public Container<T> size(final float size) {
        this.size(new Value.Fixed(size));
        return this;
    }
    
    public Container<T> size(final float width, final float height) {
        this.size(new Value.Fixed(width), new Value.Fixed(height));
        return this;
    }
    
    public Container<T> width(final Value width) {
        if (width == null) {
            throw new IllegalArgumentException("width cannot be null.");
        }
        this.minWidth = width;
        this.prefWidth = width;
        this.maxWidth = width;
        return this;
    }
    
    public Container<T> width(final float width) {
        this.width(new Value.Fixed(width));
        return this;
    }
    
    public Container<T> height(final Value height) {
        if (height == null) {
            throw new IllegalArgumentException("height cannot be null.");
        }
        this.minHeight = height;
        this.prefHeight = height;
        this.maxHeight = height;
        return this;
    }
    
    public Container<T> height(final float height) {
        this.height(new Value.Fixed(height));
        return this;
    }
    
    public Container<T> minSize(final Value size) {
        if (size == null) {
            throw new IllegalArgumentException("size cannot be null.");
        }
        this.minWidth = size;
        this.minHeight = size;
        return this;
    }
    
    public Container<T> minSize(final Value width, final Value height) {
        if (width == null) {
            throw new IllegalArgumentException("width cannot be null.");
        }
        if (height == null) {
            throw new IllegalArgumentException("height cannot be null.");
        }
        this.minWidth = width;
        this.minHeight = height;
        return this;
    }
    
    public Container<T> minWidth(final Value minWidth) {
        if (minWidth == null) {
            throw new IllegalArgumentException("minWidth cannot be null.");
        }
        this.minWidth = minWidth;
        return this;
    }
    
    public Container<T> minHeight(final Value minHeight) {
        if (minHeight == null) {
            throw new IllegalArgumentException("minHeight cannot be null.");
        }
        this.minHeight = minHeight;
        return this;
    }
    
    public Container<T> minSize(final float size) {
        this.minSize(new Value.Fixed(size));
        return this;
    }
    
    public Container<T> minSize(final float width, final float height) {
        this.minSize(new Value.Fixed(width), new Value.Fixed(height));
        return this;
    }
    
    public Container<T> minWidth(final float minWidth) {
        this.minWidth = new Value.Fixed(minWidth);
        return this;
    }
    
    public Container<T> minHeight(final float minHeight) {
        this.minHeight = new Value.Fixed(minHeight);
        return this;
    }
    
    public Container<T> prefSize(final Value size) {
        if (size == null) {
            throw new IllegalArgumentException("size cannot be null.");
        }
        this.prefWidth = size;
        this.prefHeight = size;
        return this;
    }
    
    public Container<T> prefSize(final Value width, final Value height) {
        if (width == null) {
            throw new IllegalArgumentException("width cannot be null.");
        }
        if (height == null) {
            throw new IllegalArgumentException("height cannot be null.");
        }
        this.prefWidth = width;
        this.prefHeight = height;
        return this;
    }
    
    public Container<T> prefWidth(final Value prefWidth) {
        if (prefWidth == null) {
            throw new IllegalArgumentException("prefWidth cannot be null.");
        }
        this.prefWidth = prefWidth;
        return this;
    }
    
    public Container<T> prefHeight(final Value prefHeight) {
        if (prefHeight == null) {
            throw new IllegalArgumentException("prefHeight cannot be null.");
        }
        this.prefHeight = prefHeight;
        return this;
    }
    
    public Container<T> prefSize(final float width, final float height) {
        this.prefSize(new Value.Fixed(width), new Value.Fixed(height));
        return this;
    }
    
    public Container<T> prefSize(final float size) {
        this.prefSize(new Value.Fixed(size));
        return this;
    }
    
    public Container<T> prefWidth(final float prefWidth) {
        this.prefWidth = new Value.Fixed(prefWidth);
        return this;
    }
    
    public Container<T> prefHeight(final float prefHeight) {
        this.prefHeight = new Value.Fixed(prefHeight);
        return this;
    }
    
    public Container<T> maxSize(final Value size) {
        if (size == null) {
            throw new IllegalArgumentException("size cannot be null.");
        }
        this.maxWidth = size;
        this.maxHeight = size;
        return this;
    }
    
    public Container<T> maxSize(final Value width, final Value height) {
        if (width == null) {
            throw new IllegalArgumentException("width cannot be null.");
        }
        if (height == null) {
            throw new IllegalArgumentException("height cannot be null.");
        }
        this.maxWidth = width;
        this.maxHeight = height;
        return this;
    }
    
    public Container<T> maxWidth(final Value maxWidth) {
        if (maxWidth == null) {
            throw new IllegalArgumentException("maxWidth cannot be null.");
        }
        this.maxWidth = maxWidth;
        return this;
    }
    
    public Container<T> maxHeight(final Value maxHeight) {
        if (maxHeight == null) {
            throw new IllegalArgumentException("maxHeight cannot be null.");
        }
        this.maxHeight = maxHeight;
        return this;
    }
    
    public Container<T> maxSize(final float size) {
        this.maxSize(new Value.Fixed(size));
        return this;
    }
    
    public Container<T> maxSize(final float width, final float height) {
        this.maxSize(new Value.Fixed(width), new Value.Fixed(height));
        return this;
    }
    
    public Container<T> maxWidth(final float maxWidth) {
        this.maxWidth = new Value.Fixed(maxWidth);
        return this;
    }
    
    public Container<T> maxHeight(final float maxHeight) {
        this.maxHeight = new Value.Fixed(maxHeight);
        return this;
    }
    
    public Container<T> pad(final Value pad) {
        if (pad == null) {
            throw new IllegalArgumentException("pad cannot be null.");
        }
        this.padTop = pad;
        this.padLeft = pad;
        this.padBottom = pad;
        this.padRight = pad;
        return this;
    }
    
    public Container<T> pad(final Value top, final Value left, final Value bottom, final Value right) {
        if (top == null) {
            throw new IllegalArgumentException("top cannot be null.");
        }
        if (left == null) {
            throw new IllegalArgumentException("left cannot be null.");
        }
        if (bottom == null) {
            throw new IllegalArgumentException("bottom cannot be null.");
        }
        if (right == null) {
            throw new IllegalArgumentException("right cannot be null.");
        }
        this.padTop = top;
        this.padLeft = left;
        this.padBottom = bottom;
        this.padRight = right;
        return this;
    }
    
    public Container<T> padTop(final Value padTop) {
        if (padTop == null) {
            throw new IllegalArgumentException("padTop cannot be null.");
        }
        this.padTop = padTop;
        return this;
    }
    
    public Container<T> padLeft(final Value padLeft) {
        if (padLeft == null) {
            throw new IllegalArgumentException("padLeft cannot be null.");
        }
        this.padLeft = padLeft;
        return this;
    }
    
    public Container<T> padBottom(final Value padBottom) {
        if (padBottom == null) {
            throw new IllegalArgumentException("padBottom cannot be null.");
        }
        this.padBottom = padBottom;
        return this;
    }
    
    public Container<T> padRight(final Value padRight) {
        if (padRight == null) {
            throw new IllegalArgumentException("padRight cannot be null.");
        }
        this.padRight = padRight;
        return this;
    }
    
    public Container<T> pad(final float pad) {
        final Value value = new Value.Fixed(pad);
        this.padTop = value;
        this.padLeft = value;
        this.padBottom = value;
        this.padRight = value;
        return this;
    }
    
    public Container<T> pad(final float top, final float left, final float bottom, final float right) {
        this.padTop = new Value.Fixed(top);
        this.padLeft = new Value.Fixed(left);
        this.padBottom = new Value.Fixed(bottom);
        this.padRight = new Value.Fixed(right);
        return this;
    }
    
    public Container<T> padTop(final float padTop) {
        this.padTop = new Value.Fixed(padTop);
        return this;
    }
    
    public Container<T> padLeft(final float padLeft) {
        this.padLeft = new Value.Fixed(padLeft);
        return this;
    }
    
    public Container<T> padBottom(final float padBottom) {
        this.padBottom = new Value.Fixed(padBottom);
        return this;
    }
    
    public Container<T> padRight(final float padRight) {
        this.padRight = new Value.Fixed(padRight);
        return this;
    }
    
    public Container<T> fill() {
        this.fillX = 1.0f;
        this.fillY = 1.0f;
        return this;
    }
    
    public Container<T> fillX() {
        this.fillX = 1.0f;
        return this;
    }
    
    public Container<T> fillY() {
        this.fillY = 1.0f;
        return this;
    }
    
    public Container<T> fill(final float x, final float y) {
        this.fillX = x;
        this.fillY = y;
        return this;
    }
    
    public Container<T> fill(final boolean x, final boolean y) {
        this.fillX = (x ? 1.0f : 0.0f);
        this.fillY = (y ? 1.0f : 0.0f);
        return this;
    }
    
    public Container<T> fill(final boolean fill) {
        this.fillX = (fill ? 1.0f : 0.0f);
        this.fillY = (fill ? 1.0f : 0.0f);
        return this;
    }
    
    public Container<T> align(final int align) {
        this.align = align;
        return this;
    }
    
    public Container<T> center() {
        this.align = 1;
        return this;
    }
    
    public Container<T> top() {
        this.align |= 0x2;
        this.align &= 0xFFFFFFFB;
        return this;
    }
    
    public Container<T> left() {
        this.align |= 0x8;
        this.align &= 0xFFFFFFEF;
        return this;
    }
    
    public Container<T> bottom() {
        this.align |= 0x4;
        this.align &= 0xFFFFFFFD;
        return this;
    }
    
    public Container<T> right() {
        this.align |= 0x10;
        this.align &= 0xFFFFFFF7;
        return this;
    }
    
    @Override
    public float getMinWidth() {
        return this.minWidth.get(this.actor) + this.padLeft.get(this) + this.padRight.get(this);
    }
    
    public Value getMinHeightValue() {
        return this.minHeight;
    }
    
    @Override
    public float getMinHeight() {
        return this.minHeight.get(this.actor) + this.padTop.get(this) + this.padBottom.get(this);
    }
    
    public Value getPrefWidthValue() {
        return this.prefWidth;
    }
    
    @Override
    public float getPrefWidth() {
        float v = this.prefWidth.get(this.actor);
        if (this.background != null) {
            v = Math.max(v, this.background.getMinWidth());
        }
        return Math.max(this.getMinWidth(), v + this.padLeft.get(this) + this.padRight.get(this));
    }
    
    public Value getPrefHeightValue() {
        return this.prefHeight;
    }
    
    @Override
    public float getPrefHeight() {
        float v = this.prefHeight.get(this.actor);
        if (this.background != null) {
            v = Math.max(v, this.background.getMinHeight());
        }
        return Math.max(this.getMinHeight(), v + this.padTop.get(this) + this.padBottom.get(this));
    }
    
    public Value getMaxWidthValue() {
        return this.maxWidth;
    }
    
    @Override
    public float getMaxWidth() {
        float v = this.maxWidth.get(this.actor);
        if (v > 0.0f) {
            v += this.padLeft.get(this) + this.padRight.get(this);
        }
        return v;
    }
    
    public Value getMaxHeightValue() {
        return this.maxHeight;
    }
    
    @Override
    public float getMaxHeight() {
        float v = this.maxHeight.get(this.actor);
        if (v > 0.0f) {
            v += this.padTop.get(this) + this.padBottom.get(this);
        }
        return v;
    }
    
    public Value getPadTopValue() {
        return this.padTop;
    }
    
    public float getPadTop() {
        return this.padTop.get(this);
    }
    
    public Value getPadLeftValue() {
        return this.padLeft;
    }
    
    public float getPadLeft() {
        return this.padLeft.get(this);
    }
    
    public Value getPadBottomValue() {
        return this.padBottom;
    }
    
    public float getPadBottom() {
        return this.padBottom.get(this);
    }
    
    public Value getPadRightValue() {
        return this.padRight;
    }
    
    public float getPadRight() {
        return this.padRight.get(this);
    }
    
    public float getPadX() {
        return this.padLeft.get(this) + this.padRight.get(this);
    }
    
    public float getPadY() {
        return this.padTop.get(this) + this.padBottom.get(this);
    }
    
    public float getFillX() {
        return this.fillX;
    }
    
    public float getFillY() {
        return this.fillY;
    }
    
    public int getAlign() {
        return this.align;
    }
    
    public void setRound(final boolean round) {
        this.round = round;
    }
    
    public void setClip(final boolean enabled) {
        this.setTransform(this.clip = enabled);
        this.invalidate();
    }
    
    public boolean getClip() {
        return this.clip;
    }
    
    @Override
    public Actor hit(final float x, final float y, final boolean touchable) {
        if (this.clip) {
            if (touchable && this.getTouchable() == Touchable.disabled) {
                return null;
            }
            if (x < 0.0f || x >= this.getWidth() || y < 0.0f || y >= this.getHeight()) {
                return null;
            }
        }
        return super.hit(x, y, touchable);
    }
    
    @Override
    public void drawDebug(final ShapeRenderer shapes) {
        this.validate();
        if (this.isTransform()) {
            this.applyTransform(shapes, this.computeTransform());
            if (this.clip) {
                shapes.flush();
                final float padLeft = this.padLeft.get(this);
                final float padBottom = this.padBottom.get(this);
                final boolean draw = (this.background == null) ? this.clipBegin(0.0f, 0.0f, this.getWidth(), this.getHeight()) : this.clipBegin(padLeft, padBottom, this.getWidth() - padLeft - this.padRight.get(this), this.getHeight() - padBottom - this.padTop.get(this));
                if (draw) {
                    this.drawDebugChildren(shapes);
                    this.clipEnd();
                }
            }
            else {
                this.drawDebugChildren(shapes);
            }
            this.resetTransform(shapes);
        }
        else {
            super.drawDebug(shapes);
        }
    }
}
