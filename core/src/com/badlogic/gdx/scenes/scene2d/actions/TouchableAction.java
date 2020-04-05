// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.Action;

public class TouchableAction extends Action
{
    private Touchable touchable;
    
    @Override
    public boolean act(final float delta) {
        this.target.setTouchable(this.touchable);
        return true;
    }
    
    public Touchable getTouchable() {
        return this.touchable;
    }
    
    public void setTouchable(final Touchable touchable) {
        this.touchable = touchable;
    }
}
