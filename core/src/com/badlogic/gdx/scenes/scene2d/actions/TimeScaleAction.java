// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

public class TimeScaleAction extends DelegateAction
{
    private float scale;
    
    @Override
    protected boolean delegate(final float delta) {
        return this.action == null || this.action.act(delta * this.scale);
    }
    
    public float getScale() {
        return this.scale;
    }
    
    public void setScale(final float scale) {
        this.scale = scale;
    }
}
