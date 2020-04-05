// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Tooltip<T extends Actor> extends InputListener
{
    static Vector2 tmp;
    private final TooltipManager manager;
    final Container<T> container;
    boolean instant;
    boolean always;
    Actor targetActor;
    
    static {
        Tooltip.tmp = new Vector2();
    }
    
    public Tooltip(final T contents) {
        this(contents, TooltipManager.getInstance());
    }
    
    public Tooltip(final T contents, final TooltipManager manager) {
        this.manager = manager;
        (this.container = (Container<T>)new Container(contents) {
            @Override
            public void act(final float delta) {
                super.act(delta);
                if (Tooltip.this.targetActor != null && Tooltip.this.targetActor.getStage() == null) {
                    this.remove();
                }
            }
        }).setTouchable(Touchable.disabled);
    }
    
    public TooltipManager getManager() {
        return this.manager;
    }
    
    public Container<T> getContainer() {
        return this.container;
    }
    
    public void setActor(final T contents) {
        this.container.setActor(contents);
    }
    
    public T getActor() {
        return this.container.getActor();
    }
    
    public void setInstant(final boolean instant) {
        this.instant = instant;
    }
    
    public void setAlways(final boolean always) {
        this.always = always;
    }
    
    @Override
    public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
        if (this.instant) {
            this.container.toFront();
            return false;
        }
        this.manager.touchDown(this);
        return false;
    }
    
    @Override
    public boolean mouseMoved(final InputEvent event, final float x, final float y) {
        if (this.container.hasParent()) {
            return false;
        }
        this.setContainerPosition(event.getListenerActor(), x, y);
        return true;
    }
    
    private void setContainerPosition(final Actor actor, final float x, final float y) {
        this.targetActor = actor;
        final Stage stage = actor.getStage();
        if (stage == null) {
            return;
        }
        this.container.pack();
        final float offsetX = this.manager.offsetX;
        final float offsetY = this.manager.offsetY;
        final float dist = this.manager.edgeDistance;
        Vector2 point = actor.localToStageCoordinates(Tooltip.tmp.set(x + offsetX, y - offsetY - this.container.getHeight()));
        if (point.y < dist) {
            point = actor.localToStageCoordinates(Tooltip.tmp.set(x + offsetX, y + offsetY));
        }
        if (point.x < dist) {
            point.x = dist;
        }
        if (point.x + this.container.getWidth() > stage.getWidth() - dist) {
            point.x = stage.getWidth() - dist - this.container.getWidth();
        }
        if (point.y + this.container.getHeight() > stage.getHeight() - dist) {
            point.y = stage.getHeight() - dist - this.container.getHeight();
        }
        this.container.setPosition(point.x, point.y);
        point = actor.localToStageCoordinates(Tooltip.tmp.set(actor.getWidth() / 2.0f, actor.getHeight() / 2.0f));
        point.sub(this.container.getX(), this.container.getY());
        this.container.setOrigin(point.x, point.y);
    }
    
    @Override
    public void enter(final InputEvent event, final float x, final float y, final int pointer, final Actor fromActor) {
        if (pointer != -1) {
            return;
        }
        if (Gdx.input.isTouched()) {
            return;
        }
        final Actor actor = event.getListenerActor();
        if (fromActor != null && fromActor.isDescendantOf(actor)) {
            return;
        }
        this.setContainerPosition(actor, x, y);
        this.manager.enter(this);
    }
    
    @Override
    public void exit(final InputEvent event, final float x, final float y, final int pointer, final Actor toActor) {
        if (toActor != null && toActor.isDescendantOf(event.getListenerActor())) {
            return;
        }
        this.hide();
    }
    
    public void hide() {
        this.manager.hide(this);
    }
}
