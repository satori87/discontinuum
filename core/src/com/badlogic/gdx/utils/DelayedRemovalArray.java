// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.Comparator;

public class DelayedRemovalArray<T> extends Array<T>
{
    private int iterating;
    private IntArray remove;
    private int clear;
    
    public DelayedRemovalArray() {
        this.remove = new IntArray(0);
    }
    
    public DelayedRemovalArray(final Array array) {
        super(array);
        this.remove = new IntArray(0);
    }
    
    public DelayedRemovalArray(final boolean ordered, final int capacity, final Class arrayType) {
        super(ordered, capacity, arrayType);
        this.remove = new IntArray(0);
    }
    
    public DelayedRemovalArray(final boolean ordered, final int capacity) {
        super(ordered, capacity);
        this.remove = new IntArray(0);
    }
    
    public DelayedRemovalArray(final boolean ordered, final T[] array, final int startIndex, final int count) {
        super(ordered, array, startIndex, count);
        this.remove = new IntArray(0);
    }
    
    public DelayedRemovalArray(final Class arrayType) {
        super(arrayType);
        this.remove = new IntArray(0);
    }
    
    public DelayedRemovalArray(final int capacity) {
        super(capacity);
        this.remove = new IntArray(0);
    }
    
    public DelayedRemovalArray(final T[] array) {
        super(array);
        this.remove = new IntArray(0);
    }
    
    public void begin() {
        ++this.iterating;
    }
    
    public void end() {
        if (this.iterating == 0) {
            throw new IllegalStateException("begin must be called before end.");
        }
        --this.iterating;
        if (this.iterating == 0) {
            if (this.clear > 0 && this.clear == this.size) {
                this.remove.clear();
                this.clear();
            }
            else {
                for (int i = 0, n = this.remove.size; i < n; ++i) {
                    final int index = this.remove.pop();
                    if (index >= this.clear) {
                        this.removeIndex(index);
                    }
                }
                for (int i = this.clear - 1; i >= 0; --i) {
                    this.removeIndex(i);
                }
            }
            this.clear = 0;
        }
    }
    
    private void remove(final int index) {
        if (index < this.clear) {
            return;
        }
        for (int i = 0, n = this.remove.size; i < n; ++i) {
            final int removeIndex = this.remove.get(i);
            if (index == removeIndex) {
                return;
            }
            if (index < removeIndex) {
                this.remove.insert(i, index);
                return;
            }
        }
        this.remove.add(index);
    }
    
    @Override
    public boolean removeValue(final T value, final boolean identity) {
        if (this.iterating <= 0) {
            return super.removeValue(value, identity);
        }
        final int index = this.indexOf(value, identity);
        if (index == -1) {
            return false;
        }
        this.remove(index);
        return true;
    }
    
    @Override
    public T removeIndex(final int index) {
        if (this.iterating > 0) {
            this.remove(index);
            return this.get(index);
        }
        return super.removeIndex(index);
    }
    
    @Override
    public void removeRange(final int start, final int end) {
        if (this.iterating > 0) {
            for (int i = end; i >= start; --i) {
                this.remove(i);
            }
        }
        else {
            super.removeRange(start, end);
        }
    }
    
    @Override
    public void clear() {
        if (this.iterating > 0) {
            this.clear = this.size;
            return;
        }
        super.clear();
    }
    
    @Override
    public void set(final int index, final T value) {
        if (this.iterating > 0) {
            throw new IllegalStateException("Invalid between begin/end.");
        }
        super.set(index, value);
    }
    
    @Override
    public void insert(final int index, final T value) {
        if (this.iterating > 0) {
            throw new IllegalStateException("Invalid between begin/end.");
        }
        super.insert(index, value);
    }
    
    @Override
    public void swap(final int first, final int second) {
        if (this.iterating > 0) {
            throw new IllegalStateException("Invalid between begin/end.");
        }
        super.swap(first, second);
    }
    
    @Override
    public T pop() {
        if (this.iterating > 0) {
            throw new IllegalStateException("Invalid between begin/end.");
        }
        return super.pop();
    }
    
    @Override
    public void sort() {
        if (this.iterating > 0) {
            throw new IllegalStateException("Invalid between begin/end.");
        }
        super.sort();
    }
    
    @Override
    public void sort(final Comparator<? super T> comparator) {
        if (this.iterating > 0) {
            throw new IllegalStateException("Invalid between begin/end.");
        }
        super.sort(comparator);
    }
    
    @Override
    public void reverse() {
        if (this.iterating > 0) {
            throw new IllegalStateException("Invalid between begin/end.");
        }
        super.reverse();
    }
    
    @Override
    public void shuffle() {
        if (this.iterating > 0) {
            throw new IllegalStateException("Invalid between begin/end.");
        }
        super.shuffle();
    }
    
    @Override
    public void truncate(final int newSize) {
        if (this.iterating > 0) {
            throw new IllegalStateException("Invalid between begin/end.");
        }
        super.truncate(newSize);
    }
    
    @Override
    public T[] setSize(final int newSize) {
        if (this.iterating > 0) {
            throw new IllegalStateException("Invalid between begin/end.");
        }
        return super.setSize(newSize);
    }
    
    public static <T> DelayedRemovalArray<T> with(final T... array) {
        return new DelayedRemovalArray<T>(array);
    }
}
