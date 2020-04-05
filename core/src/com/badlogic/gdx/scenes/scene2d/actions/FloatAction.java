// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.math.Interpolation;

public class FloatAction extends TemporalAction
{
    private float start;
    private float end;
    private float value;
    
    public FloatAction() {
        this.start = 0.0f;
        this.end = 1.0f;
    }
    
    public FloatAction(final float start, final float end) {
        this.start = start;
        this.end = end;
    }
    
    public FloatAction(final float start, final float end, final float duration) {
        super(duration);
        this.start = start;
        this.end = end;
    }
    
    public FloatAction(final float start, final float end, final float duration, final Interpolation interpolation) {
        super(duration, interpolation);
        this.start = start;
        this.end = end;
    }
    
    @Override
    protected void begin() {
        this.value = this.start;
    }
    
    @Override
    protected void update(final float percent) {
        this.value = this.start + (this.end - this.start) * percent;
    }
    
    public float getValue() {
        return this.value;
    }
    
    public void setValue(final float value) {
        this.value = value;
    }
    
    public float getStart() {
        return this.start;
    }
    
    public void setStart(final float start) {
        this.start = start;
    }
    
    public float getEnd() {
        return this.end;
    }
    
    public void setEnd(final float end) {
        this.end = end;
    }
}
