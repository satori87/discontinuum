// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.ObjectMap;

public class Dialog extends Window
{
    Table contentTable;
    Table buttonTable;
    private Skin skin;
    ObjectMap<Actor, Object> values;
    boolean cancelHide;
    Actor previousKeyboardFocus;
    Actor previousScrollFocus;
    FocusListener focusListener;
    protected InputListener ignoreTouchDown;
    
    public Dialog(final String title, final Skin skin) {
        super(title, skin.get(WindowStyle.class));
        this.values = new ObjectMap<Actor, Object>();
        this.ignoreTouchDown = new InputListener() {
            @Override
            public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                event.cancel();
                return false;
            }
        };
        this.setSkin(skin);
        this.skin = skin;
        this.initialize();
    }
    
    public Dialog(final String title, final Skin skin, final String windowStyleName) {
        super(title, skin.get(windowStyleName, WindowStyle.class));
        this.values = new ObjectMap<Actor, Object>();
        this.ignoreTouchDown = new InputListener() {
            @Override
            public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                event.cancel();
                return false;
            }
        };
        this.setSkin(skin);
        this.skin = skin;
        this.initialize();
    }
    
    public Dialog(final String title, final WindowStyle windowStyle) {
        super(title, windowStyle);
        this.values = new ObjectMap<Actor, Object>();
        this.ignoreTouchDown = new InputListener() {
            @Override
            public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
                event.cancel();
                return false;
            }
        };
        this.initialize();
    }
    
    private void initialize() {
        this.setModal(true);
        this.defaults().space(6.0f);
        final Table table = new Table(this.skin);
        this.contentTable = table;
        this.add(table).expand().fill();
        this.row();
        final Table table2 = new Table(this.skin);
        this.buttonTable = table2;
        this.add(table2).fillX();
        this.contentTable.defaults().space(6.0f);
        this.buttonTable.defaults().space(6.0f);
        this.buttonTable.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, Actor actor) {
                if (!Dialog.this.values.containsKey(actor)) {
                    return;
                }
                while (actor.getParent() != Dialog.this.buttonTable) {
                    actor = actor.getParent();
                }
                Dialog.this.result(Dialog.this.values.get(actor));
                if (!Dialog.this.cancelHide) {
                    Dialog.this.hide();
                }
                Dialog.this.cancelHide = false;
            }
        });
        this.focusListener = new FocusListener() {
            @Override
            public void keyboardFocusChanged(final FocusEvent event, final Actor actor, final boolean focused) {
                if (!focused) {
                    this.focusChanged(event);
                }
            }
            
            @Override
            public void scrollFocusChanged(final FocusEvent event, final Actor actor, final boolean focused) {
                if (!focused) {
                    this.focusChanged(event);
                }
            }
            
            private void focusChanged(final FocusEvent event) {
                final Stage stage = Dialog.this.getStage();
                if (Dialog.this.isModal && stage != null && stage.getRoot().getChildren().size > 0 && stage.getRoot().getChildren().peek() == Dialog.this) {
                    final Actor newFocusedActor = event.getRelatedActor();
                    if (newFocusedActor != null && !newFocusedActor.isDescendantOf(Dialog.this) && !newFocusedActor.equals(Dialog.this.previousKeyboardFocus) && !newFocusedActor.equals(Dialog.this.previousScrollFocus)) {
                        event.cancel();
                    }
                }
            }
        };
    }
    
    @Override
    protected void setStage(final Stage stage) {
        if (stage == null) {
            this.addListener(this.focusListener);
        }
        else {
            this.removeListener(this.focusListener);
        }
        super.setStage(stage);
    }
    
    public Table getContentTable() {
        return this.contentTable;
    }
    
    public Table getButtonTable() {
        return this.buttonTable;
    }
    
    public Dialog text(final String text) {
        if (this.skin == null) {
            throw new IllegalStateException("This method may only be used if the dialog was constructed with a Skin.");
        }
        return this.text(text, this.skin.get(Label.LabelStyle.class));
    }
    
    public Dialog text(final String text, final Label.LabelStyle labelStyle) {
        return this.text(new Label(text, labelStyle));
    }
    
    public Dialog text(final Label label) {
        this.contentTable.add(label);
        return this;
    }
    
    public Dialog button(final String text) {
        return this.button(text, null);
    }
    
    public Dialog button(final String text, final Object object) {
        if (this.skin == null) {
            throw new IllegalStateException("This method may only be used if the dialog was constructed with a Skin.");
        }
        return this.button(text, object, this.skin.get(TextButton.TextButtonStyle.class));
    }
    
    public Dialog button(final String text, final Object object, final TextButton.TextButtonStyle buttonStyle) {
        return this.button(new TextButton(text, buttonStyle), object);
    }
    
    public Dialog button(final Button button) {
        return this.button(button, null);
    }
    
    public Dialog button(final Button button, final Object object) {
        this.buttonTable.add(button);
        this.setObject(button, object);
        return this;
    }
    
    public Dialog show(final Stage stage, final Action action) {
        this.clearActions();
        this.removeCaptureListener(this.ignoreTouchDown);
        this.previousKeyboardFocus = null;
        Actor actor = stage.getKeyboardFocus();
        if (actor != null && !actor.isDescendantOf(this)) {
            this.previousKeyboardFocus = actor;
        }
        this.previousScrollFocus = null;
        actor = stage.getScrollFocus();
        if (actor != null && !actor.isDescendantOf(this)) {
            this.previousScrollFocus = actor;
        }
        this.pack();
        stage.addActor(this);
        stage.cancelTouchFocus();
        stage.setKeyboardFocus(this);
        stage.setScrollFocus(this);
        if (action != null) {
            this.addAction(action);
        }
        return this;
    }
    
    public Dialog show(final Stage stage) {
        this.show(stage, Actions.sequence(Actions.alpha(0.0f), Actions.fadeIn(0.4f, Interpolation.fade)));
        this.setPosition((float)Math.round((stage.getWidth() - this.getWidth()) / 2.0f), (float)Math.round((stage.getHeight() - this.getHeight()) / 2.0f));
        return this;
    }
    
    public void hide(final Action action) {
        final Stage stage = this.getStage();
        if (stage != null) {
            this.removeListener(this.focusListener);
            if (this.previousKeyboardFocus != null && this.previousKeyboardFocus.getStage() == null) {
                this.previousKeyboardFocus = null;
            }
            Actor actor = stage.getKeyboardFocus();
            if (actor == null || actor.isDescendantOf(this)) {
                stage.setKeyboardFocus(this.previousKeyboardFocus);
            }
            if (this.previousScrollFocus != null && this.previousScrollFocus.getStage() == null) {
                this.previousScrollFocus = null;
            }
            actor = stage.getScrollFocus();
            if (actor == null || actor.isDescendantOf(this)) {
                stage.setScrollFocus(this.previousScrollFocus);
            }
        }
        if (action != null) {
            this.addCaptureListener(this.ignoreTouchDown);
            this.addAction(Actions.sequence(action, Actions.removeListener(this.ignoreTouchDown, true), Actions.removeActor()));
        }
        else {
            this.remove();
        }
    }
    
    public void hide() {
        this.hide(Actions.fadeOut(0.4f, Interpolation.fade));
    }
    
    public void setObject(final Actor actor, final Object object) {
        this.values.put(actor, object);
    }
    
    public Dialog key(final int keycode, final Object object) {
        this.addListener(new InputListener() {
            @Override
            public boolean keyDown(final InputEvent event, final int keycode2) {
                if (keycode == keycode2) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            Dialog.this.result(object);
                            if (!Dialog.this.cancelHide) {
                                Dialog.this.hide();
                            }
                            Dialog.this.cancelHide = false;
                        }
                    });
                }
                return false;
            }
        });
        return this;
    }
    
    protected void result(final Object object) {
    }
    
    public void cancel() {
        this.cancelHide = true;
    }
}
