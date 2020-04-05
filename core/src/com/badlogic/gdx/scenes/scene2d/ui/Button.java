// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;

public class Button extends Table implements Disableable
{
    private ButtonStyle style;
    boolean isChecked;
    boolean isDisabled;
    boolean focused;
    ButtonGroup buttonGroup;
    private ClickListener clickListener;
    private boolean programmaticChangeEvents;
    
    public Button(final Skin skin) {
        super(skin);
        this.programmaticChangeEvents = true;
        this.initialize();
        this.setStyle(skin.get(ButtonStyle.class));
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
    }
    
    public Button(final Skin skin, final String styleName) {
        super(skin);
        this.programmaticChangeEvents = true;
        this.initialize();
        this.setStyle(skin.get(styleName, ButtonStyle.class));
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
    }
    
    public Button(final Actor child, final Skin skin, final String styleName) {
        this(child, skin.get(styleName, ButtonStyle.class));
        this.setSkin(skin);
    }
    
    public Button(final Actor child, final ButtonStyle style) {
        this.programmaticChangeEvents = true;
        this.initialize();
        this.add(child);
        this.setStyle(style);
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
    }
    
    public Button(final ButtonStyle style) {
        this.programmaticChangeEvents = true;
        this.initialize();
        this.setStyle(style);
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
    }
    
    public Button() {
        this.programmaticChangeEvents = true;
        this.initialize();
    }
    
    private void initialize() {
        this.setTouchable(Touchable.enabled);
        this.addListener(this.clickListener = new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                if (Button.this.isDisabled()) {
                    return;
                }
                Button.this.setChecked(!Button.this.isChecked, true);
            }
        });
        this.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(final FocusEvent event, final Actor actor, final boolean focused) {
                Button.this.focused = focused;
            }
        });
    }
    
    public Button(final Drawable up) {
        this(new ButtonStyle(up, null, null));
    }
    
    public Button(final Drawable up, final Drawable down) {
        this(new ButtonStyle(up, down, null));
    }
    
    public Button(final Drawable up, final Drawable down, final Drawable checked) {
        this(new ButtonStyle(up, down, checked));
    }
    
    public Button(final Actor child, final Skin skin) {
        this(child, skin.get(ButtonStyle.class));
    }
    
    public void setChecked(final boolean isChecked) {
        this.setChecked(isChecked, this.programmaticChangeEvents);
    }
    
    void setChecked(final boolean isChecked, final boolean fireEvent) {
        if (this.isChecked == isChecked) {
            return;
        }
        if (this.buttonGroup != null && !this.buttonGroup.canCheck(this, isChecked)) {
            return;
        }
        this.isChecked = isChecked;
        if (fireEvent) {
            final ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
            if (this.fire(changeEvent)) {
                this.isChecked = !isChecked;
            }
            Pools.free(changeEvent);
        }
    }
    
    public void toggle() {
        this.setChecked(!this.isChecked);
    }
    
    public boolean isChecked() {
        return this.isChecked;
    }
    
    public boolean isPressed() {
        return this.clickListener.isVisualPressed();
    }
    
    public boolean isOver() {
        return this.clickListener.isOver();
    }
    
    public ClickListener getClickListener() {
        return this.clickListener;
    }
    
    @Override
    public boolean isDisabled() {
        return this.isDisabled;
    }
    
    @Override
    public void setDisabled(final boolean isDisabled) {
        this.isDisabled = isDisabled;
    }
    
    public void setProgrammaticChangeEvents(final boolean programmaticChangeEvents) {
        this.programmaticChangeEvents = programmaticChangeEvents;
    }
    
    public void setStyle(final ButtonStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style;
        Drawable background = null;
        if (this.isPressed() && !this.isDisabled()) {
            background = ((style.down == null) ? style.up : style.down);
        }
        else if (this.isDisabled() && style.disabled != null) {
            background = style.disabled;
        }
        else if (this.isChecked && style.checked != null) {
            if (this.isOver() && style.checkedOver != null) {
                background = style.checkedOver;
            }
            else if (this.focused && style.checkedFocused != null) {
                background = style.checkedFocused;
            }
            else {
                background = style.checked;
            }
        }
        else if (this.isOver() && style.over != null) {
            background = style.over;
        }
        else if (this.focused && style.focused != null) {
            background = style.focused;
        }
        else {
            background = style.up;
        }
        this.setBackground(background);
    }
    
    public ButtonStyle getStyle() {
        return this.style;
    }
    
    public ButtonGroup getButtonGroup() {
        return this.buttonGroup;
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        this.validate();
        final boolean isDisabled = this.isDisabled();
        final boolean isPressed = this.isPressed();
        final boolean isChecked = this.isChecked();
        final boolean isOver = this.isOver();
        Drawable background = null;
        if (isDisabled && this.style.disabled != null) {
            background = this.style.disabled;
        }
        else if (isPressed && this.style.down != null) {
            background = this.style.down;
        }
        else if (isChecked && this.style.checked != null) {
            if (this.style.checkedOver != null && isOver) {
                background = this.style.checkedOver;
            }
            else if (this.style.checkedFocused != null && this.focused) {
                background = this.style.checkedFocused;
            }
            else {
                background = this.style.checked;
            }
        }
        else if (isOver && this.style.over != null) {
            background = this.style.over;
        }
        else if (this.focused && this.style.focused != null) {
            background = this.style.focused;
        }
        else if (this.style.up != null) {
            background = this.style.up;
        }
        this.setBackground(background);
        float offsetX = 0.0f;
        float offsetY = 0.0f;
        if (isPressed && !isDisabled) {
            offsetX = this.style.pressedOffsetX;
            offsetY = this.style.pressedOffsetY;
        }
        else if (isChecked && !isDisabled) {
            offsetX = this.style.checkedOffsetX;
            offsetY = this.style.checkedOffsetY;
        }
        else {
            offsetX = this.style.unpressedOffsetX;
            offsetY = this.style.unpressedOffsetY;
        }
        final Array<Actor> children = this.getChildren();
        for (int i = 0; i < children.size; ++i) {
            children.get(i).moveBy(offsetX, offsetY);
        }
        super.draw(batch, parentAlpha);
        for (int i = 0; i < children.size; ++i) {
            children.get(i).moveBy(-offsetX, -offsetY);
        }
        final Stage stage = this.getStage();
        if (stage != null && stage.getActionsRequestRendering() && isPressed != this.clickListener.isPressed()) {
            Gdx.graphics.requestRendering();
        }
    }
    
    @Override
    public float getPrefWidth() {
        float width = super.getPrefWidth();
        if (this.style.up != null) {
            width = Math.max(width, this.style.up.getMinWidth());
        }
        if (this.style.down != null) {
            width = Math.max(width, this.style.down.getMinWidth());
        }
        if (this.style.checked != null) {
            width = Math.max(width, this.style.checked.getMinWidth());
        }
        return width;
    }
    
    @Override
    public float getPrefHeight() {
        float height = super.getPrefHeight();
        if (this.style.up != null) {
            height = Math.max(height, this.style.up.getMinHeight());
        }
        if (this.style.down != null) {
            height = Math.max(height, this.style.down.getMinHeight());
        }
        if (this.style.checked != null) {
            height = Math.max(height, this.style.checked.getMinHeight());
        }
        return height;
    }
    
    @Override
    public float getMinWidth() {
        return this.getPrefWidth();
    }
    
    @Override
    public float getMinHeight() {
        return this.getPrefHeight();
    }
    
    public static class ButtonStyle
    {
        public Drawable up;
        public Drawable down;
        public Drawable over;
        public Drawable focused;
        public Drawable checked;
        public Drawable checkedOver;
        public Drawable checkedFocused;
        public Drawable disabled;
        public float pressedOffsetX;
        public float pressedOffsetY;
        public float unpressedOffsetX;
        public float unpressedOffsetY;
        public float checkedOffsetX;
        public float checkedOffsetY;
        
        public ButtonStyle() {
        }
        
        public ButtonStyle(final Drawable up, final Drawable down, final Drawable checked) {
            this.up = up;
            this.down = down;
            this.checked = checked;
        }
        
        public ButtonStyle(final ButtonStyle style) {
            this.up = style.up;
            this.down = style.down;
            this.over = style.over;
            this.focused = style.focused;
            this.checked = style.checked;
            this.checkedOver = style.checkedOver;
            this.checkedFocused = style.checkedFocused;
            this.disabled = style.disabled;
            this.pressedOffsetX = style.pressedOffsetX;
            this.pressedOffsetY = style.pressedOffsetY;
            this.unpressedOffsetX = style.unpressedOffsetX;
            this.unpressedOffsetY = style.unpressedOffsetY;
            this.checkedOffsetX = style.checkedOffsetX;
            this.checkedOffsetY = style.checkedOffsetY;
        }
    }
}
