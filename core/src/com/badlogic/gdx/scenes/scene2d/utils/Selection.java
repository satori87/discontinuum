// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import java.util.Iterator;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Selection<T> implements Disableable, Iterable<T>
{
    private Actor actor;
    final OrderedSet<T> selected;
    private final OrderedSet<T> old;
    boolean isDisabled;
    private boolean toggle;
    boolean multiple;
    boolean required;
    private boolean programmaticChangeEvents;
    T lastSelected;
    
    public Selection() {
        this.selected = new OrderedSet<T>();
        this.old = new OrderedSet<T>();
        this.programmaticChangeEvents = true;
    }
    
    public void setActor(final Actor actor) {
        this.actor = actor;
    }
    
    public void choose(final T item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null.");
        }
        if (this.isDisabled) {
            return;
        }
        this.snapshot();
        try {
            if ((this.toggle || (!this.required && this.selected.size == 1) || UIUtils.ctrl()) && this.selected.contains(item)) {
                if (this.required && this.selected.size == 1) {
                    return;
                }
                this.selected.remove(item);
                this.lastSelected = null;
            }
            else {
                boolean modified = false;
                if (!this.multiple || (!this.toggle && !UIUtils.ctrl())) {
                    if (this.selected.size == 1 && this.selected.contains(item)) {
                        return;
                    }
                    modified = (this.selected.size > 0);
                    this.selected.clear();
                }
                if (!this.selected.add(item) && !modified) {
                    return;
                }
                this.lastSelected = item;
            }
            if (this.fireChangeEvent()) {
                this.revert();
            }
            else {
                this.changed();
            }
        }
        finally {
            this.cleanup();
        }
        this.cleanup();
    }
    
    public boolean hasItems() {
        return this.selected.size > 0;
    }
    
    public boolean isEmpty() {
        return this.selected.size == 0;
    }
    
    public int size() {
        return this.selected.size;
    }
    
    public OrderedSet<T> items() {
        return this.selected;
    }
    
    public T first() {
        return (this.selected.size == 0) ? null : this.selected.first();
    }
    
    void snapshot() {
        this.old.clear();
        this.old.addAll(this.selected);
    }
    
    void revert() {
        this.selected.clear();
        this.selected.addAll(this.old);
    }
    
    void cleanup() {
        this.old.clear(32);
    }
    
    public void set(final T item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null.");
        }
        if (this.selected.size == 1 && this.selected.first() == item) {
            return;
        }
        this.snapshot();
        this.selected.clear();
        this.selected.add(item);
        if (this.programmaticChangeEvents && this.fireChangeEvent()) {
            this.revert();
        }
        else {
            this.lastSelected = item;
            this.changed();
        }
        this.cleanup();
    }
    
    public void setAll(final Array<T> items) {
        boolean added = false;
        this.snapshot();
        this.lastSelected = null;
        this.selected.clear();
        for (int i = 0, n = items.size; i < n; ++i) {
            final T item = items.get(i);
            if (item == null) {
                throw new IllegalArgumentException("item cannot be null.");
            }
            if (this.selected.add(item)) {
                added = true;
            }
        }
        if (added) {
            if (this.programmaticChangeEvents && this.fireChangeEvent()) {
                this.revert();
            }
            else if (items.size > 0) {
                this.lastSelected = items.peek();
                this.changed();
            }
        }
        this.cleanup();
    }
    
    public void add(final T item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null.");
        }
        if (!this.selected.add(item)) {
            return;
        }
        if (this.programmaticChangeEvents && this.fireChangeEvent()) {
            this.selected.remove(item);
        }
        else {
            this.lastSelected = item;
            this.changed();
        }
    }
    
    public void addAll(final Array<T> items) {
        boolean added = false;
        this.snapshot();
        for (int i = 0, n = items.size; i < n; ++i) {
            final T item = items.get(i);
            if (item == null) {
                throw new IllegalArgumentException("item cannot be null.");
            }
            if (this.selected.add(item)) {
                added = true;
            }
        }
        if (added) {
            if (this.programmaticChangeEvents && this.fireChangeEvent()) {
                this.revert();
            }
            else {
                this.lastSelected = items.peek();
                this.changed();
            }
        }
        this.cleanup();
    }
    
    public void remove(final T item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null.");
        }
        if (!this.selected.remove(item)) {
            return;
        }
        if (this.programmaticChangeEvents && this.fireChangeEvent()) {
            this.selected.add(item);
        }
        else {
            this.lastSelected = null;
            this.changed();
        }
    }
    
    public void removeAll(final Array<T> items) {
        boolean removed = false;
        this.snapshot();
        for (int i = 0, n = items.size; i < n; ++i) {
            final T item = items.get(i);
            if (item == null) {
                throw new IllegalArgumentException("item cannot be null.");
            }
            if (this.selected.remove(item)) {
                removed = true;
            }
        }
        if (removed) {
            if (this.programmaticChangeEvents && this.fireChangeEvent()) {
                this.revert();
            }
            else {
                this.lastSelected = null;
                this.changed();
            }
        }
        this.cleanup();
    }
    
    public void clear() {
        if (this.selected.size == 0) {
            return;
        }
        this.snapshot();
        this.selected.clear();
        if (this.programmaticChangeEvents && this.fireChangeEvent()) {
            this.revert();
        }
        else {
            this.lastSelected = null;
            this.changed();
        }
        this.cleanup();
    }
    
    protected void changed() {
    }
    
    public boolean fireChangeEvent() {
        if (this.actor == null) {
            return false;
        }
        final ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
        try {
            return this.actor.fire(changeEvent);
        }
        finally {
            Pools.free(changeEvent);
        }
    }
    
    public boolean contains(final T item) {
        return item != null && this.selected.contains(item);
    }
    
    public T getLastSelected() {
        if (this.lastSelected != null) {
            return this.lastSelected;
        }
        if (this.selected.size > 0) {
            return this.selected.first();
        }
        return null;
    }
    
    @Override
    public Iterator<T> iterator() {
        return this.selected.iterator();
    }
    
    public Array<T> toArray() {
        return this.selected.iterator().toArray();
    }
    
    public Array<T> toArray(final Array<T> array) {
        return this.selected.iterator().toArray(array);
    }
    
    @Override
    public void setDisabled(final boolean isDisabled) {
        this.isDisabled = isDisabled;
    }
    
    @Override
    public boolean isDisabled() {
        return this.isDisabled;
    }
    
    public boolean getToggle() {
        return this.toggle;
    }
    
    public void setToggle(final boolean toggle) {
        this.toggle = toggle;
    }
    
    public boolean getMultiple() {
        return this.multiple;
    }
    
    public void setMultiple(final boolean multiple) {
        this.multiple = multiple;
    }
    
    public boolean getRequired() {
        return this.required;
    }
    
    public void setRequired(final boolean required) {
        this.required = required;
    }
    
    public void setProgrammaticChangeEvents(final boolean programmaticChangeEvents) {
        this.programmaticChangeEvents = programmaticChangeEvents;
    }
    
    @Override
    public String toString() {
        return this.selected.toString();
    }
}
