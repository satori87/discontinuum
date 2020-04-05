// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.Comparator;

public class QuickSelect<T>
{
    private T[] array;
    private Comparator<? super T> comp;
    
    public int select(final T[] items, final Comparator<T> comp, final int n, final int size) {
        this.array = items;
        this.comp = comp;
        return this.recursiveSelect(0, size - 1, n);
    }
    
    private int partition(final int left, final int right, final int pivot) {
        final T pivotValue = this.array[pivot];
        this.swap(right, pivot);
        int storage = left;
        for (int i = left; i < right; ++i) {
            if (this.comp.compare((Object)this.array[i], (Object)pivotValue) < 0) {
                this.swap(storage, i);
                ++storage;
            }
        }
        this.swap(right, storage);
        return storage;
    }
    
    private int recursiveSelect(final int left, final int right, final int k) {
        if (left == right) {
            return left;
        }
        final int pivotIndex = this.medianOfThreePivot(left, right);
        final int pivotNewIndex = this.partition(left, right, pivotIndex);
        final int pivotDist = pivotNewIndex - left + 1;
        int result;
        if (pivotDist == k) {
            result = pivotNewIndex;
        }
        else if (k < pivotDist) {
            result = this.recursiveSelect(left, pivotNewIndex - 1, k);
        }
        else {
            result = this.recursiveSelect(pivotNewIndex + 1, right, k - pivotDist);
        }
        return result;
    }
    
    private int medianOfThreePivot(final int leftIdx, final int rightIdx) {
        final T left = this.array[leftIdx];
        final int midIdx = (leftIdx + rightIdx) / 2;
        final T mid = this.array[midIdx];
        final T right = this.array[rightIdx];
        if (this.comp.compare((Object)left, (Object)mid) > 0) {
            if (this.comp.compare((Object)mid, (Object)right) > 0) {
                return midIdx;
            }
            if (this.comp.compare((Object)left, (Object)right) > 0) {
                return rightIdx;
            }
            return leftIdx;
        }
        else {
            if (this.comp.compare((Object)left, (Object)right) > 0) {
                return leftIdx;
            }
            if (this.comp.compare((Object)mid, (Object)right) > 0) {
                return rightIdx;
            }
            return midIdx;
        }
    }
    
    private void swap(final int left, final int right) {
        final T tmp = this.array[left];
        this.array[left] = this.array[right];
        this.array[right] = tmp;
    }
}
