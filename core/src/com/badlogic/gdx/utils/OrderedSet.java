// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.NoSuchElementException;
import java.util.Iterator;

public class OrderedSet<T> extends ObjectSet<T>
{
    final Array<T> items;
    OrderedSetIterator iterator1;
    OrderedSetIterator iterator2;
    
    public OrderedSet() {
        this.items = new Array<T>();
    }
    
    public OrderedSet(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
        this.items = new Array<T>(this.capacity);
    }
    
    public OrderedSet(final int initialCapacity) {
        super(initialCapacity);
        this.items = new Array<T>(this.capacity);
    }
    
    public OrderedSet(final OrderedSet set) {
        super(set);
        (this.items = new Array<T>(this.capacity)).addAll((Array<? extends T>)set.items);
    }
    
    @Override
    public boolean add(final T key) {
        if (!super.add(key)) {
            return false;
        }
        this.items.add(key);
        return true;
    }
    
    public boolean add(final T key, final int index) {
        if (!super.add(key)) {
            this.items.removeValue(key, true);
            this.items.insert(index, key);
            return false;
        }
        this.items.insert(index, key);
        return true;
    }
    
    @Override
    public boolean remove(final T key) {
        if (!super.remove(key)) {
            return false;
        }
        this.items.removeValue(key, false);
        return true;
    }
    
    @Override
    public void clear(final int maximumCapacity) {
        this.items.clear();
        super.clear(maximumCapacity);
    }
    
    @Override
    public void clear() {
        this.items.clear();
        super.clear();
    }
    
    public Array<T> orderedItems() {
        return this.items;
    }
    
    @Override
    public OrderedSetIterator<T> iterator() {
        if (this.iterator1 == null) {
            this.iterator1 = new OrderedSetIterator((OrderedSet<T>)this);
            this.iterator2 = new OrderedSetIterator((OrderedSet<T>)this);
        }
        if (!this.iterator1.valid) {
            this.iterator1.reset();
            this.iterator1.valid = true;
            this.iterator2.valid = false;
            return (OrderedSetIterator<T>)this.iterator1;
        }
        this.iterator2.reset();
        this.iterator2.valid = true;
        this.iterator1.valid = false;
        return (OrderedSetIterator<T>)this.iterator2;
    }
    
    @Override
    public String toString() {
        if (this.size == 0) {
            return "{}";
        }
        final Object[] items = this.items.items;
        final StringBuilder buffer = new StringBuilder(32);
        buffer.append('{');
        buffer.append(items[0]);
        for (int i = 1; i < this.size; ++i) {
            buffer.append(", ");
            buffer.append(items[i]);
        }
        buffer.append('}');
        return buffer.toString();
    }
    
    @Override
    public String toString(final String separator) {
        return this.items.toString(separator);
    }
    
    public static class OrderedSetIterator<T> extends ObjectSetIterator<T>
    {
        private Array<T> items;
        
        public OrderedSetIterator(final OrderedSet<T> set) {
            super(set);
            this.items = set.items;
        }
        
        @Override
        public void reset() {
            this.nextIndex = 0;
            this.hasNext = (this.set.size > 0);
        }
        
        @Override
        public T next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            final T key = this.items.get(this.nextIndex);
            ++this.nextIndex;
            this.hasNext = (this.nextIndex < this.set.size);
            return key;
        }
        
        @Override
        public void remove() {
            if (this.nextIndex < 0) {
                throw new IllegalStateException("next must be called before remove.");
            }
            --this.nextIndex;
            this.set.remove(this.items.get(this.nextIndex));
        }
    }
}
