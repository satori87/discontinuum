// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.graphics.Color;

public class ColorAction extends TemporalAction
{
    private float startR;
    private float startG;
    private float startB;
    private float startA;
    private Color color;
    private final Color end;
    
    public ColorAction() {
        this.end = new Color();
    }
    
    @Override
    protected void begin() {
        if (this.color == null) {
            this.color = this.target.getColor();
        }
        this.startR = this.color.r;
        this.startG = this.color.g;
        this.startB = this.color.b;
        this.startA = this.color.a;
    }
    
    @Override
    protected void update(final float percent) {
        final float r = this.startR + (this.end.r - this.startR) * percent;
        final float g = this.startG + (this.end.g - this.startG) * percent;
        final float b = this.startB + (this.end.b - this.startB) * percent;
        final float a = this.startA + (this.end.a - this.startA) * percent;
        this.color.set(r, g, b, a);
    }
    
    @Override
    public void reset() {
        super.reset();
        this.color = null;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setColor(final Color color) {
        this.color = color;
    }
    
    public Color getEndColor() {
        return this.end;
    }
    
    public void setEndColor(final Color color) {
        this.end.set(color);
    }
}
