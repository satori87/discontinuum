// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.scenes.scene2d.Action;

public class RunnableAction extends Action
{
    private Runnable runnable;
    private boolean ran;
    
    @Override
    public boolean act(final float delta) {
        if (!this.ran) {
            this.ran = true;
            this.run();
        }
        return true;
    }
    
    public void run() {
        final Pool pool = this.getPool();
        this.setPool(null);
        try {
            this.runnable.run();
        }
        finally {
            this.setPool(pool);
        }
        this.setPool(pool);
    }
    
    @Override
    public void restart() {
        this.ran = false;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.runnable = null;
    }
    
    public Runnable getRunnable() {
        return this.runnable;
    }
    
    public void setRunnable(final Runnable runnable) {
        this.runnable = runnable;
    }
}
