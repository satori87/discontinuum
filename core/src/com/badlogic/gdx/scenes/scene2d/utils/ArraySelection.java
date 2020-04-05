// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import java.util.Iterator;
import com.badlogic.gdx.utils.Array;

public class ArraySelection<T> extends Selection<T>
{
    private Array<T> array;
    private boolean rangeSelect;
    private int rangeStart;
    
    public ArraySelection(final Array<T> array) {
        this.rangeSelect = true;
        this.array = array;
    }
    
    @Override
    public void choose(final T item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null.");
        }
        if (this.isDisabled) {
            return;
        }
        final int index = this.array.indexOf(item, false);
        if (this.selected.size > 0 && this.rangeSelect && this.multiple && UIUtils.shift()) {
            final int oldRangeState = this.rangeStart;
            this.snapshot();
            int start = this.rangeStart;
            int end = index;
            if (start > end) {
                final int temp = end;
                end = start;
                start = temp;
            }
            if (!UIUtils.ctrl()) {
                this.selected.clear();
            }
            for (int i = start; i <= end; ++i) {
                this.selected.add(this.array.get(i));
            }
            if (this.fireChangeEvent()) {
                this.rangeStart = oldRangeState;
                this.revert();
            }
            this.cleanup();
            return;
        }
        this.rangeStart = index;
        super.choose(item);
    }
    
    public boolean getRangeSelect() {
        return this.rangeSelect;
    }
    
    public void setRangeSelect(final boolean rangeSelect) {
        this.rangeSelect = rangeSelect;
    }
    
    public void validate() {
        final Array<T> array = this.array;
        if (array.size == 0) {
            this.clear();
            return;
        }
        final Iterator<T> iter = this.items().iterator();
        while (iter.hasNext()) {
            final T selected = iter.next();
            if (!array.contains(selected, false)) {
                iter.remove();
            }
        }
        if (this.required && this.selected.size == 0) {
            this.set(array.first());
        }
    }
}
