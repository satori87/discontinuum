// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.Comparator;

public class Select
{
    private static Select instance;
    private QuickSelect quickSelect;
    
    public static Select instance() {
        if (Select.instance == null) {
            Select.instance = new Select();
        }
        return Select.instance;
    }
    
    public <T> T select(final T[] items, final Comparator<T> comp, final int kthLowest, final int size) {
        final int idx = this.selectIndex(items, comp, kthLowest, size);
        return items[idx];
    }
    
    public <T> int selectIndex(final T[] items, final Comparator<T> comp, final int kthLowest, final int size) {
        if (size < 1) {
            throw new GdxRuntimeException("cannot select from empty array (size < 1)");
        }
        if (kthLowest > size) {
            throw new GdxRuntimeException("Kth rank is larger than size. k: " + kthLowest + ", size: " + size);
        }
        int idx;
        if (kthLowest == 1) {
            idx = this.fastMin(items, comp, size);
        }
        else if (kthLowest == size) {
            idx = this.fastMax(items, comp, size);
        }
        else {
            if (this.quickSelect == null) {
                this.quickSelect = new QuickSelect();
            }
            idx = this.quickSelect.select(items, comp, kthLowest, size);
        }
        return idx;
    }
    
    private <T> int fastMin(final T[] items, final Comparator<T> comp, final int size) {
        int lowestIdx = 0;
        for (int i = 1; i < size; ++i) {
            final int comparison = comp.compare(items[i], items[lowestIdx]);
            if (comparison < 0) {
                lowestIdx = i;
            }
        }
        return lowestIdx;
    }
    
    private <T> int fastMax(final T[] items, final Comparator<T> comp, final int size) {
        int highestIdx = 0;
        for (int i = 1; i < size; ++i) {
            final int comparison = comp.compare(items[i], items[highestIdx]);
            if (comparison > 0) {
                highestIdx = i;
            }
        }
        return highestIdx;
    }
}
