// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.math.Interpolation;

public class IntAction extends TemporalAction
{
    private int start;
    private int end;
    private int value;
    
    public IntAction() {
        this.start = 0;
        this.end = 1;
    }
    
    public IntAction(final int start, final int end) {
        this.start = start;
        this.end = end;
    }
    
    public IntAction(final int start, final int end, final float duration) {
        super(duration);
        this.start = start;
        this.end = end;
    }
    
    public IntAction(final int start, final int end, final float duration, final Interpolation interpolation) {
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
        this.value = (int)(this.start + (this.end - this.start) * percent);
    }
    
    public int getValue() {
        return this.value;
    }
    
    public void setValue(final int value) {
        this.value = value;
    }
    
    public int getStart() {
        return this.start;
    }
    
    public void setStart(final int start) {
        this.start = start;
    }
    
    public int getEnd() {
        return this.end;
    }
    
    public void setEnd(final int end) {
        this.end = end;
    }
}
