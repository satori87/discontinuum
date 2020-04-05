// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;

public class ProgressBar extends Widget implements Disableable
{
    private ProgressBarStyle style;
    private float min;
    private float max;
    private float stepSize;
    private float value;
    private float animateFromValue;
    float position;
    final boolean vertical;
    private float animateDuration;
    private float animateTime;
    private Interpolation animateInterpolation;
    boolean disabled;
    private Interpolation visualInterpolation;
    private boolean round;
    
    public ProgressBar(final float min, final float max, final float stepSize, final boolean vertical, final Skin skin) {
        this(min, max, stepSize, vertical, skin.get("default-" + (vertical ? "vertical" : "horizontal"), ProgressBarStyle.class));
    }
    
    public ProgressBar(final float min, final float max, final float stepSize, final boolean vertical, final Skin skin, final String styleName) {
        this(min, max, stepSize, vertical, skin.get(styleName, ProgressBarStyle.class));
    }
    
    public ProgressBar(final float min, final float max, final float stepSize, final boolean vertical, final ProgressBarStyle style) {
        this.animateInterpolation = Interpolation.linear;
        this.visualInterpolation = Interpolation.linear;
        this.round = true;
        if (min > max) {
            throw new IllegalArgumentException("max must be > min. min,max: " + min + ", " + max);
        }
        if (stepSize <= 0.0f) {
            throw new IllegalArgumentException("stepSize must be > 0: " + stepSize);
        }
        this.setStyle(style);
        this.min = min;
        this.max = max;
        this.stepSize = stepSize;
        this.vertical = vertical;
        this.value = min;
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
    }
    
    public void setStyle(final ProgressBarStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style;
        this.invalidateHierarchy();
    }
    
    public ProgressBarStyle getStyle() {
        return this.style;
    }
    
    @Override
    public void act(final float delta) {
        super.act(delta);
        if (this.animateTime > 0.0f) {
            this.animateTime -= delta;
            final Stage stage = this.getStage();
            if (stage != null && stage.getActionsRequestRendering()) {
                Gdx.graphics.requestRendering();
            }
        }
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        final ProgressBarStyle style = this.style;
        final boolean disabled = this.disabled;
        final Drawable knob = this.getKnobDrawable();
        final Drawable bg = (disabled && style.disabledBackground != null) ? style.disabledBackground : style.background;
        final Drawable knobBefore = (disabled && style.disabledKnobBefore != null) ? style.disabledKnobBefore : style.knobBefore;
        final Drawable knobAfter = (disabled && style.disabledKnobAfter != null) ? style.disabledKnobAfter : style.knobAfter;
        final Color color = this.getColor();
        final float x = this.getX();
        final float y = this.getY();
        final float width = this.getWidth();
        final float height = this.getHeight();
        final float knobHeight = (knob == null) ? 0.0f : knob.getMinHeight();
        final float knobWidth = (knob == null) ? 0.0f : knob.getMinWidth();
        final float percent = this.getVisualPercent();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        if (this.vertical) {
            float positionHeight = height;
            float bgTopHeight = 0.0f;
            if (bg != null) {
                if (this.round) {
                    bg.draw(batch, (float)Math.round(x + (width - bg.getMinWidth()) * 0.5f), y, (float)Math.round(bg.getMinWidth()), height);
                }
                else {
                    bg.draw(batch, x + width - bg.getMinWidth() * 0.5f, y, bg.getMinWidth(), height);
                }
                bgTopHeight = bg.getTopHeight();
                positionHeight -= bgTopHeight + bg.getBottomHeight();
            }
            float knobHeightHalf = 0.0f;
            if (knob == null) {
                knobHeightHalf = ((knobBefore == null) ? 0.0f : (knobBefore.getMinHeight() * 0.5f));
                this.position = (positionHeight - knobHeightHalf) * percent;
                this.position = Math.min(positionHeight - knobHeightHalf, this.position);
            }
            else {
                knobHeightHalf = knobHeight * 0.5f;
                this.position = (positionHeight - knobHeight) * percent;
                this.position = Math.min(positionHeight - knobHeight, this.position) + bg.getBottomHeight();
            }
            this.position = Math.max(0.0f, this.position);
            if (knobBefore != null) {
                if (this.round) {
                    knobBefore.draw(batch, (float)Math.round(x + (width - knobBefore.getMinWidth()) * 0.5f), (float)Math.round(y + bgTopHeight), (float)Math.round(knobBefore.getMinWidth()), (float)Math.round(this.position + knobHeightHalf));
                }
                else {
                    knobBefore.draw(batch, x + (width - knobBefore.getMinWidth()) * 0.5f, y + bgTopHeight, knobBefore.getMinWidth(), this.position + knobHeightHalf);
                }
            }
            if (knobAfter != null) {
                if (this.round) {
                    knobAfter.draw(batch, (float)Math.round(x + (width - knobAfter.getMinWidth()) * 0.5f), (float)Math.round(y + this.position + knobHeightHalf), (float)Math.round(knobAfter.getMinWidth()), (float)Math.round(height - this.position - knobHeightHalf));
                }
                else {
                    knobAfter.draw(batch, x + (width - knobAfter.getMinWidth()) * 0.5f, y + this.position + knobHeightHalf, knobAfter.getMinWidth(), height - this.position - knobHeightHalf);
                }
            }
            if (knob != null) {
                if (this.round) {
                    knob.draw(batch, (float)Math.round(x + (width - knobWidth) * 0.5f), (float)Math.round(y + this.position), (float)Math.round(knobWidth), (float)Math.round(knobHeight));
                }
                else {
                    knob.draw(batch, x + (width - knobWidth) * 0.5f, y + this.position, knobWidth, knobHeight);
                }
            }
        }
        else {
            float positionWidth = width;
            float bgLeftWidth = 0.0f;
            if (bg != null) {
                if (this.round) {
                    bg.draw(batch, x, (float)Math.round(y + (height - bg.getMinHeight()) * 0.5f), width, (float)Math.round(bg.getMinHeight()));
                }
                else {
                    bg.draw(batch, x, y + (height - bg.getMinHeight()) * 0.5f, width, bg.getMinHeight());
                }
                bgLeftWidth = bg.getLeftWidth();
                positionWidth -= bgLeftWidth + bg.getRightWidth();
            }
            float knobWidthHalf = 0.0f;
            if (knob == null) {
                knobWidthHalf = ((knobBefore == null) ? 0.0f : (knobBefore.getMinWidth() * 0.5f));
                this.position = (positionWidth - knobWidthHalf) * percent;
                this.position = Math.min(positionWidth - knobWidthHalf, this.position);
            }
            else {
                knobWidthHalf = knobWidth * 0.5f;
                this.position = (positionWidth - knobWidth) * percent;
                this.position = Math.min(positionWidth - knobWidth, this.position) + bgLeftWidth;
            }
            this.position = Math.max(0.0f, this.position);
            if (knobBefore != null) {
                if (this.round) {
                    knobBefore.draw(batch, (float)Math.round(x + bgLeftWidth), (float)Math.round(y + (height - knobBefore.getMinHeight()) * 0.5f), (float)Math.round(this.position + knobWidthHalf), (float)Math.round(knobBefore.getMinHeight()));
                }
                else {
                    knobBefore.draw(batch, x + bgLeftWidth, y + (height - knobBefore.getMinHeight()) * 0.5f, this.position + knobWidthHalf, knobBefore.getMinHeight());
                }
            }
            if (knobAfter != null) {
                if (this.round) {
                    knobAfter.draw(batch, (float)Math.round(x + this.position + knobWidthHalf), (float)Math.round(y + (height - knobAfter.getMinHeight()) * 0.5f), (float)Math.round(width - this.position - knobWidthHalf), (float)Math.round(knobAfter.getMinHeight()));
                }
                else {
                    knobAfter.draw(batch, x + this.position + knobWidthHalf, y + (height - knobAfter.getMinHeight()) * 0.5f, width - this.position - knobWidthHalf, knobAfter.getMinHeight());
                }
            }
            if (knob != null) {
                if (this.round) {
                    knob.draw(batch, (float)Math.round(x + this.position), (float)Math.round(y + (height - knobHeight) * 0.5f), (float)Math.round(knobWidth), (float)Math.round(knobHeight));
                }
                else {
                    knob.draw(batch, x + this.position, y + (height - knobHeight) * 0.5f, knobWidth, knobHeight);
                }
            }
        }
    }
    
    public float getValue() {
        return this.value;
    }
    
    public float getVisualValue() {
        if (this.animateTime > 0.0f) {
            return this.animateInterpolation.apply(this.animateFromValue, this.value, 1.0f - this.animateTime / this.animateDuration);
        }
        return this.value;
    }
    
    public float getPercent() {
        if (this.min == this.max) {
            return 0.0f;
        }
        return (this.value - this.min) / (this.max - this.min);
    }
    
    public float getVisualPercent() {
        if (this.min == this.max) {
            return 0.0f;
        }
        return this.visualInterpolation.apply((this.getVisualValue() - this.min) / (this.max - this.min));
    }
    
    protected Drawable getKnobDrawable() {
        return (this.disabled && this.style.disabledKnob != null) ? this.style.disabledKnob : this.style.knob;
    }
    
    protected float getKnobPosition() {
        return this.position;
    }
    
    public boolean setValue(float value) {
        value = this.clamp(Math.round(value / this.stepSize) * this.stepSize);
        final float oldValue = this.value;
        if (value == oldValue) {
            return false;
        }
        final float oldVisualValue = this.getVisualValue();
        this.value = value;
        final ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
        final boolean cancelled = this.fire(changeEvent);
        if (cancelled) {
            this.value = oldValue;
        }
        else if (this.animateDuration > 0.0f) {
            this.animateFromValue = oldVisualValue;
            this.animateTime = this.animateDuration;
        }
        Pools.free(changeEvent);
        return !cancelled;
    }
    
    protected float clamp(final float value) {
        return MathUtils.clamp(value, this.min, this.max);
    }
    
    public void setRange(final float min, final float max) {
        if (min > max) {
            throw new IllegalArgumentException("min must be <= max: " + min + " <= " + max);
        }
        this.min = min;
        this.max = max;
        if (this.value < min) {
            this.setValue(min);
        }
        else if (this.value > max) {
            this.setValue(max);
        }
    }
    
    public void setStepSize(final float stepSize) {
        if (stepSize <= 0.0f) {
            throw new IllegalArgumentException("steps must be > 0: " + stepSize);
        }
        this.stepSize = stepSize;
    }
    
    @Override
    public float getPrefWidth() {
        if (this.vertical) {
            final Drawable knob = this.getKnobDrawable();
            final Drawable bg = (this.disabled && this.style.disabledBackground != null) ? this.style.disabledBackground : this.style.background;
            return Math.max((knob == null) ? 0.0f : knob.getMinWidth(), bg.getMinWidth());
        }
        return 140.0f;
    }
    
    @Override
    public float getPrefHeight() {
        if (this.vertical) {
            return 140.0f;
        }
        final Drawable knob = this.getKnobDrawable();
        final Drawable bg = (this.disabled && this.style.disabledBackground != null) ? this.style.disabledBackground : this.style.background;
        return Math.max((knob == null) ? 0.0f : knob.getMinHeight(), (bg == null) ? 0.0f : bg.getMinHeight());
    }
    
    public float getMinValue() {
        return this.min;
    }
    
    public float getMaxValue() {
        return this.max;
    }
    
    public float getStepSize() {
        return this.stepSize;
    }
    
    public void setAnimateDuration(final float duration) {
        this.animateDuration = duration;
    }
    
    public void setAnimateInterpolation(final Interpolation animateInterpolation) {
        if (animateInterpolation == null) {
            throw new IllegalArgumentException("animateInterpolation cannot be null.");
        }
        this.animateInterpolation = animateInterpolation;
    }
    
    public void setVisualInterpolation(final Interpolation interpolation) {
        this.visualInterpolation = interpolation;
    }
    
    public void setRound(final boolean round) {
        this.round = round;
    }
    
    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }
    
    @Override
    public boolean isDisabled() {
        return this.disabled;
    }
    
    public boolean isVertical() {
        return this.vertical;
    }
    
    public static class ProgressBarStyle
    {
        public Drawable background;
        public Drawable disabledBackground;
        public Drawable knob;
        public Drawable disabledKnob;
        public Drawable knobBefore;
        public Drawable knobAfter;
        public Drawable disabledKnobBefore;
        public Drawable disabledKnobAfter;
        
        public ProgressBarStyle() {
        }
        
        public ProgressBarStyle(final Drawable background, final Drawable knob) {
            this.background = background;
            this.knob = knob;
        }
        
        public ProgressBarStyle(final ProgressBarStyle style) {
            this.background = style.background;
            this.disabledBackground = style.disabledBackground;
            this.knob = style.knob;
            this.disabledKnob = style.disabledKnob;
            this.knobBefore = style.knobBefore;
            this.knobAfter = style.knobAfter;
            this.disabledKnobBefore = style.disabledKnobBefore;
            this.disabledKnobAfter = style.disabledKnobAfter;
        }
    }
}
