// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

public class MoveToAction extends TemporalAction
{
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private int alignment;
    
    public MoveToAction() {
        this.alignment = 12;
    }
    
    @Override
    protected void begin() {
        this.startX = this.target.getX(this.alignment);
        this.startY = this.target.getY(this.alignment);
    }
    
    @Override
    protected void update(final float percent) {
        this.target.setPosition(this.startX + (this.endX - this.startX) * percent, this.startY + (this.endY - this.startY) * percent, this.alignment);
    }
    
    @Override
    public void reset() {
        super.reset();
        this.alignment = 12;
    }
    
    public void setStartPosition(final float x, final float y) {
        this.startX = x;
        this.startY = y;
    }
    
    public void setPosition(final float x, final float y) {
        this.endX = x;
        this.endY = y;
    }
    
    public void setPosition(final float x, final float y, final int alignment) {
        this.endX = x;
        this.endY = y;
        this.alignment = alignment;
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
    
    public int getAlignment() {
        return this.alignment;
    }
    
    public void setAlignment(final int alignment) {
        this.alignment = alignment;
    }
}
