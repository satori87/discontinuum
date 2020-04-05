// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.NoSuchElementException;
import java.util.Iterator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.reflect.ArrayReflection;

public class ArrayMap<K, V> implements Iterable<ObjectMap.Entry<K, V>>
{
    public K[] keys;
    public V[] values;
    public int size;
    public boolean ordered;
    private Entries entries1;
    private Entries entries2;
    private Values valuesIter1;
    private Values valuesIter2;
    private Keys keysIter1;
    private Keys keysIter2;
    
    public ArrayMap() {
        this(true, 16);
    }
    
    public ArrayMap(final int capacity) {
        this(true, capacity);
    }
    
    public ArrayMap(final boolean ordered, final int capacity) {
        this.ordered = ordered;
        this.keys = (K[])new Object[capacity];
        this.values = (V[])new Object[capacity];
    }
    
    public ArrayMap(final boolean ordered, final int capacity, final Class keyArrayType, final Class valueArrayType) {
        this.ordered = ordered;
        this.keys = (K[])ArrayReflection.newInstance(keyArrayType, capacity);
        this.values = (V[])ArrayReflection.newInstance(valueArrayType, capacity);
    }
    
    public ArrayMap(final Class keyArrayType, final Class valueArrayType) {
        this(false, 16, keyArrayType, valueArrayType);
    }
    
    public ArrayMap(final ArrayMap array) {
        this(array.ordered, array.size, array.keys.getClass().getComponentType(), array.values.getClass().getComponentType());
        this.size = array.size;
        System.arraycopy(array.keys, 0, this.keys, 0, this.size);
        System.arraycopy(array.values, 0, this.values, 0, this.size);
    }
    
    public int put(final K key, final V value) {
        int index = this.indexOfKey(key);
        if (index == -1) {
            if (this.size == this.keys.length) {
                this.resize(Math.max(8, (int)(this.size * 1.75f)));
            }
            index = this.size++;
        }
        this.keys[index] = key;
        this.values[index] = value;
        return index;
    }
    
    public int put(final K key, final V value, final int index) {
        final int existingIndex = this.indexOfKey(key);
        if (existingIndex != -1) {
            this.removeIndex(existingIndex);
        }
        else if (this.size == this.keys.length) {
            this.resize(Math.max(8, (int)(this.size * 1.75f)));
        }
        System.arraycopy(this.keys, index, this.keys, index + 1, this.size - index);
        System.arraycopy(this.values, index, this.values, index + 1, this.size - index);
        this.keys[index] = key;
        this.values[index] = value;
        ++this.size;
        return index;
    }
    
    public void putAll(final ArrayMap<? extends K, ? extends V> map) {
        this.putAll(map, 0, map.size);
    }
    
    public void putAll(final ArrayMap<? extends K, ? extends V> map, final int offset, final int length) {
        if (offset + length > map.size) {
            throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + map.size);
        }
        final int sizeNeeded = this.size + length - offset;
        if (sizeNeeded >= this.keys.length) {
            this.resize(Math.max(8, (int)(sizeNeeded * 1.75f)));
        }
        System.arraycopy(map.keys, offset, this.keys, this.size, length);
        System.arraycopy(map.values, offset, this.values, this.size, length);
        this.size += length;
    }
    
    public V get(final K key) {
        final Object[] keys = this.keys;
        int i = this.size - 1;
        if (key == null) {
            while (i >= 0) {
                if (keys[i] == key) {
                    return this.values[i];
                }
                --i;
            }
        }
        else {
            while (i >= 0) {
                if (key.equals(keys[i])) {
                    return this.values[i];
                }
                --i;
            }
        }
        return null;
    }
    
    public K getKey(final V value, final boolean identity) {
        final Object[] values = this.values;
        int i = this.size - 1;
        if (!identity) {
            if (value != null) {
                while (i >= 0) {
                    if (value.equals(values[i])) {
                        return this.keys[i];
                    }
                    --i;
                }
                return null;
            }
        }
        while (i >= 0) {
            if (values[i] == value) {
                return this.keys[i];
            }
            --i;
        }
        return null;
    }
    
    public K getKeyAt(final int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        return this.keys[index];
    }
    
    public V getValueAt(final int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        return this.values[index];
    }
    
    public K firstKey() {
        if (this.size == 0) {
            throw new IllegalStateException("Map is empty.");
        }
        return this.keys[0];
    }
    
    public V firstValue() {
        if (this.size == 0) {
            throw new IllegalStateException("Map is empty.");
        }
        return this.values[0];
    }
    
    public void setKey(final int index, final K key) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        this.keys[index] = key;
    }
    
    public void setValue(final int index, final V value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        this.values[index] = value;
    }
    
    public void insert(final int index, final K key, final V value) {
        if (index > this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        if (this.size == this.keys.length) {
            this.resize(Math.max(8, (int)(this.size * 1.75f)));
        }
        if (this.ordered) {
            System.arraycopy(this.keys, index, this.keys, index + 1, this.size - index);
            System.arraycopy(this.values, index, this.values, index + 1, this.size - index);
        }
        else {
            this.keys[this.size] = this.keys[index];
            this.values[this.size] = this.values[index];
        }
        ++this.size;
        this.keys[index] = key;
        this.values[index] = value;
    }
    
    public boolean containsKey(final K key) {
        final Object[] keys = this.keys;
        int i = this.size - 1;
        if (key == null) {
            while (i >= 0) {
                if (keys[i--] == key) {
                    return true;
                }
            }
        }
        else {
            while (i >= 0) {
                if (key.equals(keys[i--])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean containsValue(final V value, final boolean identity) {
        final Object[] values = this.values;
        int i = this.size - 1;
        if (!identity) {
            if (value != null) {
                while (i >= 0) {
                    if (value.equals(values[i--])) {
                        return true;
                    }
                }
                return false;
            }
        }
        while (i >= 0) {
            if (values[i--] == value) {
                return true;
            }
        }
        return false;
    }
    
    public int indexOfKey(final K key) {
        final Object[] keys = this.keys;
        if (key == null) {
            for (int i = 0, n = this.size; i < n; ++i) {
                if (keys[i] == key) {
                    return i;
                }
            }
        }
        else {
            for (int i = 0, n = this.size; i < n; ++i) {
                if (key.equals(keys[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int indexOfValue(final V value, final boolean identity) {
        final Object[] values = this.values;
        if (identity || value == null) {
            for (int i = 0, n = this.size; i < n; ++i) {
                if (values[i] == value) {
                    return i;
                }
            }
        }
        else {
            for (int i = 0, n = this.size; i < n; ++i) {
                if (value.equals(values[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public V removeKey(final K key) {
        final Object[] keys = this.keys;
        if (key == null) {
            for (int i = 0, n = this.size; i < n; ++i) {
                if (keys[i] == key) {
                    final V value = this.values[i];
                    this.removeIndex(i);
                    return value;
                }
            }
        }
        else {
            for (int i = 0, n = this.size; i < n; ++i) {
                if (key.equals(keys[i])) {
                    final V value = this.values[i];
                    this.removeIndex(i);
                    return value;
                }
            }
        }
        return null;
    }
    
    public boolean removeValue(final V value, final boolean identity) {
        final Object[] values = this.values;
        if (identity || value == null) {
            for (int i = 0, n = this.size; i < n; ++i) {
                if (values[i] == value) {
                    this.removeIndex(i);
                    return true;
                }
            }
        }
        else {
            for (int i = 0, n = this.size; i < n; ++i) {
                if (value.equals(values[i])) {
                    this.removeIndex(i);
                    return true;
                }
            }
        }
        return false;
    }
    
    public void removeIndex(final int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        final Object[] keys = this.keys;
        --this.size;
        if (this.ordered) {
            System.arraycopy(keys, index + 1, keys, index, this.size - index);
            System.arraycopy(this.values, index + 1, this.values, index, this.size - index);
        }
        else {
            keys[index] = keys[this.size];
            this.values[index] = this.values[this.size];
        }
        keys[this.size] = null;
        this.values[this.size] = null;
    }
    
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    public K peekKey() {
        return this.keys[this.size - 1];
    }
    
    public V peekValue() {
        return this.values[this.size - 1];
    }
    
    public void clear(final int maximumCapacity) {
        if (this.keys.length <= maximumCapacity) {
            this.clear();
            return;
        }
        this.size = 0;
        this.resize(maximumCapacity);
    }
    
    public void clear() {
        final Object[] keys = this.keys;
        final Object[] values = this.values;
        for (int i = 0, n = this.size; i < n; ++i) {
            values[i] = (keys[i] = null);
        }
        this.size = 0;
    }
    
    public void shrink() {
        if (this.keys.length == this.size) {
            return;
        }
        this.resize(this.size);
    }
    
    public void ensureCapacity(final int additionalCapacity) {
        if (additionalCapacity < 0) {
            throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
        }
        final int sizeNeeded = this.size + additionalCapacity;
        if (sizeNeeded >= this.keys.length) {
            this.resize(Math.max(8, sizeNeeded));
        }
    }
    
    protected void resize(final int newSize) {
        final Object[] newKeys = (Object[])ArrayReflection.newInstance(this.keys.getClass().getComponentType(), newSize);
        System.arraycopy(this.keys, 0, newKeys, 0, Math.min(this.size, newKeys.length));
        this.keys = (K[])newKeys;
        final Object[] newValues = (Object[])ArrayReflection.newInstance(this.values.getClass().getComponentType(), newSize);
        System.arraycopy(this.values, 0, newValues, 0, Math.min(this.size, newValues.length));
        this.values = (V[])newValues;
    }
    
    public void reverse() {
        int i = 0;
        final int lastIndex = this.size - 1;
        for (int n = this.size / 2; i < n; ++i) {
            final int ii = lastIndex - i;
            final K tempKey = this.keys[i];
            this.keys[i] = this.keys[ii];
            this.keys[ii] = tempKey;
            final V tempValue = this.values[i];
            this.values[i] = this.values[ii];
            this.values[ii] = tempValue;
        }
    }
    
    public void shuffle() {
        for (int i = this.size - 1; i >= 0; --i) {
            final int ii = MathUtils.random(i);
            final K tempKey = this.keys[i];
            this.keys[i] = this.keys[ii];
            this.keys[ii] = tempKey;
            final V tempValue = this.values[i];
            this.values[i] = this.values[ii];
            this.values[ii] = tempValue;
        }
    }
    
    public void truncate(final int newSize) {
        if (this.size <= newSize) {
            return;
        }
        for (int i = newSize; i < this.size; ++i) {
            this.keys[i] = null;
            this.values[i] = null;
        }
        this.size = newSize;
    }
    
    @Override
    public int hashCode() {
        final Object[] keys = this.keys;
        final Object[] values = this.values;
        int h = 0;
        for (int i = 0, n = this.size; i < n; ++i) {
            final K key = (K)keys[i];
            final V value = (V)values[i];
            if (key != null) {
                h += key.hashCode() * 31;
            }
            if (value != null) {
                h += value.hashCode();
            }
        }
        return h;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ArrayMap)) {
            return false;
        }
        final ArrayMap<K, V> other = (ArrayMap<K, V>)obj;
        if (other.size != this.size) {
            return false;
        }
        final Object[] keys = this.keys;
        final Object[] values = this.values;
        for (int i = 0, n = this.size; i < n; ++i) {
            final K key = (K)keys[i];
            final V value = (V)values[i];
            if (value == null) {
                if (!other.containsKey(key) || other.get(key) != null) {
                    return false;
                }
            }
            else if (!value.equals(other.get(key))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        if (this.size == 0) {
            return "{}";
        }
        final Object[] keys = this.keys;
        final Object[] values = this.values;
        final com.badlogic.gdx.utils.StringBuilder buffer = new com.badlogic.gdx.utils.StringBuilder(32);
        buffer.append('{');
        buffer.append(keys[0]);
        buffer.append('=');
        buffer.append(values[0]);
        for (int i = 1; i < this.size; ++i) {
            buffer.append(", ");
            buffer.append(keys[i]);
            buffer.append('=');
            buffer.append(values[i]);
        }
        buffer.append('}');
        return buffer.toString();
    }
    
    @Override
    public Iterator<ObjectMap.Entry<K, V>> iterator() {
        return this.entries();
    }
    
    public Entries<K, V> entries() {
        if (this.entries1 == null) {
            this.entries1 = new Entries((ArrayMap<K, V>)this);
            this.entries2 = new Entries((ArrayMap<K, V>)this);
        }
        if (!this.entries1.valid) {
            this.entries1.index = 0;
            this.entries1.valid = true;
            this.entries2.valid = false;
            return (Entries<K, V>)this.entries1;
        }
        this.entries2.index = 0;
        this.entries2.valid = true;
        this.entries1.valid = false;
        return (Entries<K, V>)this.entries2;
    }
    
    public Values<V> values() {
        if (this.valuesIter1 == null) {
            this.valuesIter1 = new Values((ArrayMap<Object, V>)this);
            this.valuesIter2 = new Values((ArrayMap<Object, V>)this);
        }
        if (!this.valuesIter1.valid) {
            this.valuesIter1.index = 0;
            this.valuesIter1.valid = true;
            this.valuesIter2.valid = false;
            return (Values<V>)this.valuesIter1;
        }
        this.valuesIter2.index = 0;
        this.valuesIter2.valid = true;
        this.valuesIter1.valid = false;
        return (Values<V>)this.valuesIter2;
    }
    
    public Keys<K> keys() {
        if (this.keysIter1 == null) {
            this.keysIter1 = new Keys((ArrayMap<K, Object>)this);
            this.keysIter2 = new Keys((ArrayMap<K, Object>)this);
        }
        if (!this.keysIter1.valid) {
            this.keysIter1.index = 0;
            this.keysIter1.valid = true;
            this.keysIter2.valid = false;
            return (Keys<K>)this.keysIter1;
        }
        this.keysIter2.index = 0;
        this.keysIter2.valid = true;
        this.keysIter1.valid = false;
        return (Keys<K>)this.keysIter2;
    }
    
    public static class Entries<K, V> implements Iterable<ObjectMap.Entry<K, V>>, Iterator<ObjectMap.Entry<K, V>>
    {
        private final ArrayMap<K, V> map;
        ObjectMap.Entry<K, V> entry;
        int index;
        boolean valid;
        
        public Entries(final ArrayMap<K, V> map) {
            this.entry = new ObjectMap.Entry<K, V>();
            this.valid = true;
            this.map = map;
        }
        
        @Override
        public boolean hasNext() {
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.index < this.map.size;
        }
        
        @Override
        public Iterator<ObjectMap.Entry<K, V>> iterator() {
            return this;
        }
        
        @Override
        public ObjectMap.Entry<K, V> next() {
            if (this.index >= this.map.size) {
                throw new NoSuchElementException(String.valueOf(this.index));
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            this.entry.key = this.map.keys[this.index];
            this.entry.value = this.map.values[this.index++];
            return this.entry;
        }
        
        @Override
        public void remove() {
            --this.index;
            this.map.removeIndex(this.index);
        }
        
        public void reset() {
            this.index = 0;
        }
    }
    
    public static class Values<V> implements Iterable<V>, Iterator<V>
    {
        private final ArrayMap<Object, V> map;
        int index;
        boolean valid;
        
        public Values(final ArrayMap<Object, V> map) {
            this.valid = true;
            this.map = map;
        }
        
        @Override
        public boolean hasNext() {
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.index < this.map.size;
        }
        
        @Override
        public Iterator<V> iterator() {
            return this;
        }
        
        @Override
        public V next() {
            if (this.index >= this.map.size) {
                throw new NoSuchElementException(String.valueOf(this.index));
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.map.values[this.index++];
        }
        
        @Override
        public void remove() {
            --this.index;
            this.map.removeIndex(this.index);
        }
        
        public void reset() {
            this.index = 0;
        }
        
        public Array<V> toArray() {
            return new Array<V>(true, this.map.values, this.index, this.map.size - this.index);
        }
        
        public Array<V> toArray(final Array array) {
            array.addAll(this.map.values, this.index, this.map.size - this.index);
            return (Array<V>)array;
        }
    }
    
    public static class Keys<K> implements Iterable<K>, Iterator<K>
    {
        private final ArrayMap<K, Object> map;
        int index;
        boolean valid;
        
        public Keys(final ArrayMap<K, Object> map) {
            this.valid = true;
            this.map = map;
        }
        
        @Override
        public boolean hasNext() {
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.index < this.map.size;
        }
        
        @Override
        public Iterator<K> iterator() {
            return this;
        }
        
        @Override
        public K next() {
            if (this.index >= this.map.size) {
                throw new NoSuchElementException(String.valueOf(this.index));
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.map.keys[this.index++];
        }
        
        @Override
        public void remove() {
            --this.index;
            this.map.removeIndex(this.index);
        }
        
        public void reset() {
            this.index = 0;
        }
        
        public Array<K> toArray() {
            return new Array<K>(true, this.map.keys, this.index, this.map.size - this.index);
        }
        
        public Array<K> toArray(final Array array) {
            array.addAll(this.map.keys, this.index, this.map.size - this.index);
            return (Array<K>)array;
        }
    }
}
