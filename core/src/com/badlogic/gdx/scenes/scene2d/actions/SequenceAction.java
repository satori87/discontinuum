// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.scenes.scene2d.Action;

public class SequenceAction extends ParallelAction
{
    private int index;
    
    public SequenceAction() {
    }
    
    public SequenceAction(final Action action1) {
        this.addAction(action1);
    }
    
    public SequenceAction(final Action action1, final Action action2) {
        this.addAction(action1);
        this.addAction(action2);
    }
    
    public SequenceAction(final Action action1, final Action action2, final Action action3) {
        this.addAction(action1);
        this.addAction(action2);
        this.addAction(action3);
    }
    
    public SequenceAction(final Action action1, final Action action2, final Action action3, final Action action4) {
        this.addAction(action1);
        this.addAction(action2);
        this.addAction(action3);
        this.addAction(action4);
    }
    
    public SequenceAction(final Action action1, final Action action2, final Action action3, final Action action4, final Action action5) {
        this.addAction(action1);
        this.addAction(action2);
        this.addAction(action3);
        this.addAction(action4);
        this.addAction(action5);
    }
    
    @Override
    public boolean act(final float delta) {
        if (this.index >= this.actions.size) {
            return true;
        }
        final Pool pool = this.getPool();
        this.setPool(null);
        try {
            if (this.actions.get(this.index).act(delta)) {
                if (this.actor == null) {
                    return true;
                }
                ++this.index;
                if (this.index >= this.actions.size) {
                    return true;
                }
            }
            return false;
        }
        finally {
            this.setPool(pool);
        }
    }
    
    @Override
    public void restart() {
        super.restart();
        this.index = 0;
    }
}
