// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.utils.Array;

public class ButtonGroup<T extends Button>
{
    private final Array<T> buttons;
    private Array<T> checkedButtons;
    private int minCheckCount;
    private int maxCheckCount;
    private boolean uncheckLast;
    private T lastChecked;
    
    public ButtonGroup() {
        this.buttons = new Array<T>();
        this.checkedButtons = new Array<T>(1);
        this.maxCheckCount = 1;
        this.uncheckLast = true;
        this.minCheckCount = 1;
    }
    
    public ButtonGroup(final T... buttons) {
        this.buttons = new Array<T>();
        this.checkedButtons = new Array<T>(1);
        this.maxCheckCount = 1;
        this.uncheckLast = true;
        this.minCheckCount = 0;
        this.add(buttons);
        this.minCheckCount = 1;
    }
    
    public void add(final T button) {
        if (button == null) {
            throw new IllegalArgumentException("button cannot be null.");
        }
        button.buttonGroup = null;
        final boolean shouldCheck = button.isChecked() || this.buttons.size < this.minCheckCount;
        button.setChecked(false);
        button.buttonGroup = this;
        this.buttons.add(button);
        button.setChecked(shouldCheck);
    }
    
    public void add(final T... buttons) {
        if (buttons == null) {
            throw new IllegalArgumentException("buttons cannot be null.");
        }
        for (int i = 0, n = buttons.length; i < n; ++i) {
            this.add(buttons[i]);
        }
    }
    
    public void remove(final T button) {
        if (button == null) {
            throw new IllegalArgumentException("button cannot be null.");
        }
        button.buttonGroup = null;
        this.buttons.removeValue(button, true);
        this.checkedButtons.removeValue(button, true);
    }
    
    public void remove(final T... buttons) {
        if (buttons == null) {
            throw new IllegalArgumentException("buttons cannot be null.");
        }
        for (int i = 0, n = buttons.length; i < n; ++i) {
            this.remove(buttons[i]);
        }
    }
    
    public void clear() {
        this.buttons.clear();
        this.checkedButtons.clear();
    }
    
    public void setChecked(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null.");
        }
        for (int i = 0, n = this.buttons.size; i < n; ++i) {
            final T button = this.buttons.get(i);
            if (button instanceof TextButton && text.contentEquals(((TextButton)button).getText())) {
                button.setChecked(true);
                return;
            }
        }
    }
    
    protected boolean canCheck(final T button, final boolean newState) {
        if (button.isChecked == newState) {
            return false;
        }
        if (!newState) {
            if (this.checkedButtons.size <= this.minCheckCount) {
                return false;
            }
            this.checkedButtons.removeValue(button, true);
        }
        else {
            if (this.maxCheckCount != -1 && this.checkedButtons.size >= this.maxCheckCount) {
                if (!this.uncheckLast) {
                    return false;
                }
                final int old = this.minCheckCount;
                this.minCheckCount = 0;
                this.lastChecked.setChecked(false);
                this.minCheckCount = old;
            }
            this.checkedButtons.add(button);
            this.lastChecked = button;
        }
        return true;
    }
    
    public void uncheckAll() {
        final int old = this.minCheckCount;
        this.minCheckCount = 0;
        for (int i = 0, n = this.buttons.size; i < n; ++i) {
            final T button = this.buttons.get(i);
            button.setChecked(false);
        }
        this.minCheckCount = old;
    }
    
    public T getChecked() {
        if (this.checkedButtons.size > 0) {
            return this.checkedButtons.get(0);
        }
        return null;
    }
    
    public int getCheckedIndex() {
        if (this.checkedButtons.size > 0) {
            return this.buttons.indexOf(this.checkedButtons.get(0), true);
        }
        return -1;
    }
    
    public Array<T> getAllChecked() {
        return this.checkedButtons;
    }
    
    public Array<T> getButtons() {
        return this.buttons;
    }
    
    public void setMinCheckCount(final int minCheckCount) {
        this.minCheckCount = minCheckCount;
    }
    
    public void setMaxCheckCount(int maxCheckCount) {
        if (maxCheckCount == 0) {
            maxCheckCount = -1;
        }
        this.maxCheckCount = maxCheckCount;
    }
    
    public void setUncheckLast(final boolean uncheckLast) {
        this.uncheckLast = uncheckLast;
    }
}
