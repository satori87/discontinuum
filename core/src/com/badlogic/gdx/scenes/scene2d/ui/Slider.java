// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.math.Interpolation;

public class Slider extends ProgressBar
{
    int draggingPointer;
    boolean mouseOver;
    private Interpolation visualInterpolationInverse;
    private float[] snapValues;
    private float threshold;
    
    public Slider(final float min, final float max, final float stepSize, final boolean vertical, final Skin skin) {
        this(min, max, stepSize, vertical, skin.get("default-" + (vertical ? "vertical" : "horizontal"), SliderStyle.class));
    }
    
    public Slider(final float min, final float max, final float stepSize, final boolean vertical, final Skin skin, final String styleName) {
        this(min, max, stepSize, vertical, skin.get(styleName, SliderStyle.class));
    }
    
    public Slider(final float min, final float max, final float stepSize, final boolean vertical, final SliderStyle style) {
        super(min, max, stepSize, vertical, style);
        this.draggingPointer = -1;
        this.visualInterpolationInverse = Interpolation.linear;
        this.addListener(new InputListener() {
            @Override
            public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                if (Slider.this.disabled) {
                    return false;
                }
                if (Slider.this.draggingPointer != -1) {
                    return false;
                }
                Slider.this.draggingPointer = pointer;
                Slider.this.calculatePositionAndValue(x, y);
                return true;
            }
            
            @Override
            public void touchUp(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                if (pointer != Slider.this.draggingPointer) {
                    return;
                }
                Slider.this.draggingPointer = -1;
                if (event.isTouchFocusCancel() || !Slider.this.calculatePositionAndValue(x, y)) {
                    final ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
                    Slider.this.fire(changeEvent);
                    Pools.free(changeEvent);
                }
            }
            
            @Override
            public void touchDragged(final InputEvent event, final float x, final float y, final int pointer) {
                Slider.this.calculatePositionAndValue(x, y);
            }
            
            @Override
            public void enter(final InputEvent event, final float x, final float y, final int pointer, final Actor fromActor) {
                if (pointer == -1) {
                    Slider.this.mouseOver = true;
                }
            }
            
            @Override
            public void exit(final InputEvent event, final float x, final float y, final int pointer, final Actor toActor) {
                if (pointer == -1) {
                    Slider.this.mouseOver = false;
                }
            }
        });
    }
    
    public void setStyle(final SliderStyle style) {
        if (style == null) {
            throw new NullPointerException("style cannot be null");
        }
        if (!(style instanceof SliderStyle)) {
            throw new IllegalArgumentException("style must be a SliderStyle.");
        }
        super.setStyle(style);
    }
    
    @Override
    public SliderStyle getStyle() {
        return (SliderStyle)super.getStyle();
    }
    
    @Override
    protected Drawable getKnobDrawable() {
        final SliderStyle style = this.getStyle();
        return (this.disabled && style.disabledKnob != null) ? style.disabledKnob : ((this.isDragging() && style.knobDown != null) ? style.knobDown : ((this.mouseOver && style.knobOver != null) ? style.knobOver : style.knob));
    }
    
    boolean calculatePositionAndValue(final float x, final float y) {
        final SliderStyle style = this.getStyle();
        final Drawable knob = this.getKnobDrawable();
        final Drawable bg = (this.disabled && style.disabledBackground != null) ? style.disabledBackground : style.background;
        final float oldPosition = this.position;
        final float min = this.getMinValue();
        final float max = this.getMaxValue();
        float value;
        if (this.vertical) {
            final float height = this.getHeight() - bg.getTopHeight() - bg.getBottomHeight();
            final float knobHeight = (knob == null) ? 0.0f : knob.getMinHeight();
            this.position = y - bg.getBottomHeight() - knobHeight * 0.5f;
            value = min + (max - min) * this.visualInterpolationInverse.apply(this.position / (height - knobHeight));
            this.position = Math.max(0.0f, this.position);
            this.position = Math.min(height - knobHeight, this.position);
        }
        else {
            final float width = this.getWidth() - bg.getLeftWidth() - bg.getRightWidth();
            final float knobWidth = (knob == null) ? 0.0f : knob.getMinWidth();
            this.position = x - bg.getLeftWidth() - knobWidth * 0.5f;
            value = min + (max - min) * this.visualInterpolationInverse.apply(this.position / (width - knobWidth));
            this.position = Math.max(0.0f, this.position);
            this.position = Math.min(width - knobWidth, this.position);
        }
        final float oldValue = value;
        if (!Gdx.input.isKeyPressed(59) && !Gdx.input.isKeyPressed(60)) {
            value = this.snap(value);
        }
        final boolean valueSet = this.setValue(value);
        if (value == oldValue) {
            this.position = oldPosition;
        }
        return valueSet;
    }
    
    protected float snap(final float value) {
        if (this.snapValues == null || this.snapValues.length == 0) {
            return value;
        }
        float bestDiff = -1.0f;
        float bestValue = 0.0f;
        for (int i = 0; i < this.snapValues.length; ++i) {
            final float snapValue = this.snapValues[i];
            final float diff = Math.abs(value - snapValue);
            if (diff <= this.threshold && (bestDiff == -1.0f || diff < bestDiff)) {
                bestDiff = diff;
                bestValue = snapValue;
            }
        }
        return (bestDiff == -1.0f) ? value : bestValue;
    }
    
    public void setSnapToValues(final float[] values, final float threshold) {
        this.snapValues = values;
        this.threshold = threshold;
    }
    
    public boolean isDragging() {
        return this.draggingPointer != -1;
    }
    
    public void setVisualInterpolationInverse(final Interpolation interpolation) {
        this.visualInterpolationInverse = interpolation;
    }
    
    public static class SliderStyle extends ProgressBarStyle
    {
        public Drawable knobOver;
        public Drawable knobDown;
        
        public SliderStyle() {
        }
        
        public SliderStyle(final Drawable background, final Drawable knob) {
            super(background, knob);
        }
        
        public SliderStyle(final SliderStyle style) {
            super(style);
            this.knobOver = style.knobOver;
            this.knobDown = style.knobDown;
        }
    }
}
