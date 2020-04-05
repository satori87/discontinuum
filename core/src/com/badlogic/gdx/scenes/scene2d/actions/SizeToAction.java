// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

public class SizeToAction extends TemporalAction
{
    private float startWidth;
    private float startHeight;
    private float endWidth;
    private float endHeight;
    
    @Override
    protected void begin() {
        this.startWidth = this.target.getWidth();
        this.startHeight = this.target.getHeight();
    }
    
    @Override
    protected void update(final float percent) {
        this.target.setSize(this.startWidth + (this.endWidth - this.startWidth) * percent, this.startHeight + (this.endHeight - this.startHeight) * percent);
    }
    
    public void setSize(final float width, final float height) {
        this.endWidth = width;
        this.endHeight = height;
    }
    
    public float getWidth() {
        return this.endWidth;
    }
    
    public void setWidth(final float width) {
        this.endWidth = width;
    }
    
    public float getHeight() {
        return this.endHeight;
    }
    
    public void setHeight(final float height) {
        this.endHeight = height;
    }
}
