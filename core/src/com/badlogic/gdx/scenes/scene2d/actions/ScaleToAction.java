// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

public class ScaleToAction extends TemporalAction
{
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    
    @Override
    protected void begin() {
        this.startX = this.target.getScaleX();
        this.startY = this.target.getScaleY();
    }
    
    @Override
    protected void update(final float percent) {
        this.target.setScale(this.startX + (this.endX - this.startX) * percent, this.startY + (this.endY - this.startY) * percent);
    }
    
    public void setScale(final float x, final float y) {
        this.endX = x;
        this.endY = y;
    }
    
    public void setScale(final float scale) {
        this.endX = scale;
        this.endY = scale;
    }
    
    public float getX() {
        return this.endX;
    }
    
    public void setX(final float x) {
        this.endX = x;
    }
    
    public float getY() {
        return this.endY;
    }
    
    public void setY(final float y) {
        this.endY = y;
    }
}
