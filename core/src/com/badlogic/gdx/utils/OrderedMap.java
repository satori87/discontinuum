// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.NoSuchElementException;
import java.util.Iterator;

public class OrderedMap<K, V> extends ObjectMap<K, V>
{
    final Array<K> keys;
    private Entries entries1;
    private Entries entries2;
    private Values values1;
    private Values values2;
    private Keys keys1;
    private Keys keys2;
    
    public OrderedMap() {
        this.keys = new Array<K>();
    }
    
    public OrderedMap(final int initialCapacity) {
        super(initialCapacity);
        this.keys = new Array<K>(this.capacity);
    }
    
    public OrderedMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
        this.keys = new Array<K>(this.capacity);
    }
    
    public OrderedMap(final OrderedMap<? extends K, ? extends V> map) {
        super(map);
        this.keys = new Array<K>(map.keys);
    }
    
    @Override
    public V put(final K key, final V value) {
        if (!this.containsKey(key)) {
            this.keys.add(key);
        }
        return super.put(key, value);
    }
    
    @Override
    public V remove(final K key) {
        this.keys.removeValue(key, false);
        return super.remove(key);
    }
    
    @Override
    public void clear(final int maximumCapacity) {
        this.keys.clear();
        super.clear(maximumCapacity);
    }
    
    @Override
    public void clear() {
        this.keys.clear();
        super.clear();
    }
    
    public Array<K> orderedKeys() {
        return this.keys;
    }
    
    @Override
    public Entries<K, V> iterator() {
        return this.entries();
    }
    
    @Override
    public Entries<K, V> entries() {
        if (this.entries1 == null) {
            this.entries1 = new OrderedMapEntries(this);
            this.entries2 = new OrderedMapEntries(this);
        }
        if (!this.entries1.valid) {
            this.entries1.reset();
            this.entries1.valid = true;
            this.entries2.valid = false;
            return (Entries<K, V>)this.entries1;
        }
        this.entries2.reset();
        this.entries2.valid = true;
        this.entries1.valid = false;
        return (Entries<K, V>)this.entries2;
    }
    
    @Override
    public Values<V> values() {
        if (this.values1 == null) {
            this.values1 = new OrderedMapValues(this);
            this.values2 = new OrderedMapValues(this);
        }
        if (!this.values1.valid) {
            this.values1.reset();
            this.values1.valid = true;
            this.values2.valid = false;
            return (Values<V>)this.values1;
        }
        this.values2.reset();
        this.values2.valid = true;
        this.values1.valid = false;
        return (Values<V>)this.values2;
    }
    
    @Override
    public Keys<K> keys() {
        if (this.keys1 == null) {
            this.keys1 = new OrderedMapKeys(this);
            this.keys2 = new OrderedMapKeys(this);
        }
        if (!this.keys1.valid) {
            this.keys1.reset();
            this.keys1.valid = true;
            this.keys2.valid = false;
            return (Keys<K>)this.keys1;
        }
        this.keys2.reset();
        this.keys2.valid = true;
        this.keys1.valid = false;
        return (Keys<K>)this.keys2;
    }
    
    @Override
    public String toString() {
        if (this.size == 0) {
            return "{}";
        }
        final StringBuilder buffer = new StringBuilder(32);
        buffer.append('{');
        final Array<K> keys = this.keys;
        for (int i = 0, n = keys.size; i < n; ++i) {
            final K key = keys.get(i);
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(key);
            buffer.append('=');
            buffer.append(this.get(key));
        }
        buffer.append('}');
        return buffer.toString();
    }
    
    public static class OrderedMapEntries<K, V> extends Entries<K, V>
    {
        private Array<K> keys;
        
        public OrderedMapEntries(final OrderedMap<K, V> map) {
            super(map);
            this.keys = map.keys;
        }
        
        @Override
        public void reset() {
            this.nextIndex = 0;
            this.hasNext = (this.map.size > 0);
        }
        
        @Override
        public Entry next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            this.entry.key = this.keys.get(this.nextIndex);
            this.entry.value = (V)this.map.get((K)this.entry.key);
            ++this.nextIndex;
            this.hasNext = (this.nextIndex < this.map.size);
            return this.entry;
        }
        
        @Override
        public void remove() {
            if (this.currentIndex < 0) {
                throw new IllegalStateException("next must be called before remove.");
            }
            this.map.remove((K)this.entry.key);
            --this.nextIndex;
        }
    }
    
    public static class OrderedMapKeys<K> extends Keys<K>
    {
        private Array<K> keys;
        
        public OrderedMapKeys(final OrderedMap<K, ?> map) {
            super(map);
            this.keys = map.keys;
        }
        
        @Override
        public void reset() {
            this.nextIndex = 0;
            this.hasNext = (this.map.size > 0);
        }
        
        @Override
        public K next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            final K key = this.keys.get(this.nextIndex);
            this.currentIndex = this.nextIndex;
            ++this.nextIndex;
            this.hasNext = (this.nextIndex < this.map.size);
            return key;
        }
        
        @Override
        public void remove() {
            if (this.currentIndex < 0) {
                throw new IllegalStateException("next must be called before remove.");
            }
            this.map.remove((K)this.keys.get(this.nextIndex - 1));
            this.nextIndex = this.currentIndex;
            this.currentIndex = -1;
        }
    }
    
    public static class OrderedMapValues<V> extends Values<V>
    {
        private Array keys;
        
        public OrderedMapValues(final OrderedMap<?, V> map) {
            super(map);
            this.keys = map.keys;
        }
        
        @Override
        public void reset() {
            this.nextIndex = 0;
            this.hasNext = (this.map.size > 0);
        }
        
        @Override
        public V next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            final V value = (V)this.map.get(this.keys.get(this.nextIndex));
            this.currentIndex = this.nextIndex;
            ++this.nextIndex;
            this.hasNext = (this.nextIndex < this.map.size);
            return value;
        }
        
        @Override
        public void remove() {
            if (this.currentIndex < 0) {
                throw new IllegalStateException("next must be called before remove.");
            }
            this.map.remove(this.keys.get(this.currentIndex));
            this.nextIndex = this.currentIndex;
            this.currentIndex = -1;
        }
    }
}
