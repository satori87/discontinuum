// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.Action;

public class ParallelAction extends Action
{
    Array<Action> actions;
    private boolean complete;
    
    public ParallelAction() {
        this.actions = new Array<Action>(4);
    }
    
    public ParallelAction(final Action action1) {
        this.actions = new Array<Action>(4);
        this.addAction(action1);
    }
    
    public ParallelAction(final Action action1, final Action action2) {
        this.actions = new Array<Action>(4);
        this.addAction(action1);
        this.addAction(action2);
    }
    
    public ParallelAction(final Action action1, final Action action2, final Action action3) {
        this.actions = new Array<Action>(4);
        this.addAction(action1);
        this.addAction(action2);
        this.addAction(action3);
    }
    
    public ParallelAction(final Action action1, final Action action2, final Action action3, final Action action4) {
        this.actions = new Array<Action>(4);
        this.addAction(action1);
        this.addAction(action2);
        this.addAction(action3);
        this.addAction(action4);
    }
    
    public ParallelAction(final Action action1, final Action action2, final Action action3, final Action action4, final Action action5) {
        this.actions = new Array<Action>(4);
        this.addAction(action1);
        this.addAction(action2);
        this.addAction(action3);
        this.addAction(action4);
        this.addAction(action5);
    }
    
    @Override
    public boolean act(final float delta) {
        if (this.complete) {
            return true;
        }
        this.complete = true;
        final Pool pool = this.getPool();
        this.setPool(null);
        try {
            final Array<Action> actions = this.actions;
            for (int i = 0, n = actions.size; i < n && this.actor != null; ++i) {
                final Action currentAction = actions.get(i);
                if (currentAction.getActor() != null && !currentAction.act(delta)) {
                    this.complete = false;
                }
                if (this.actor == null) {
                    return true;
                }
            }
            return this.complete;
        }
        finally {
            this.setPool(pool);
        }
    }
    
    @Override
    public void restart() {
        this.complete = false;
        final Array<Action> actions = this.actions;
        for (int i = 0, n = actions.size; i < n; ++i) {
            actions.get(i).restart();
        }
    }
    
    @Override
    public void reset() {
        super.reset();
        this.actions.clear();
    }
    
    public void addAction(final Action action) {
        this.actions.add(action);
        if (this.actor != null) {
            action.setActor(this.actor);
        }
    }
    
    @Override
    public void setActor(final Actor actor) {
        final Array<Action> actions = this.actions;
        for (int i = 0, n = actions.size; i < n; ++i) {
            actions.get(i).setActor(actor);
        }
        super.setActor(actor);
    }
    
    public Array<Action> getActions() {
        return this.actions;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(64);
        buffer.append(super.toString());
        buffer.append('(');
        final Array<Action> actions = this.actions;
        for (int i = 0, n = actions.size; i < n; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(actions.get(i));
        }
        buffer.append(')');
        return buffer.toString();
    }
}
