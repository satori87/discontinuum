// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.NoSuchElementException;
import java.util.Iterator;
import com.badlogic.gdx.math.MathUtils;

public class ObjectMap<K, V> implements Iterable<Entry<K, V>>
{
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    public int size;
    K[] keyTable;
    V[] valueTable;
    int capacity;
    int stashSize;
    private float loadFactor;
    private int hashShift;
    private int mask;
    private int threshold;
    private int stashCapacity;
    private int pushIterations;
    private Entries entries1;
    private Entries entries2;
    private Values values1;
    private Values values2;
    private Keys keys1;
    private Keys keys2;
    
    public ObjectMap() {
        this(51, 0.8f);
    }
    
    public ObjectMap(final int initialCapacity) {
        this(initialCapacity, 0.8f);
    }
    
    public ObjectMap(int initialCapacity, final float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
        }
        initialCapacity = MathUtils.nextPowerOfTwo((int)Math.ceil(initialCapacity / loadFactor));
        if (initialCapacity > 1073741824) {
            throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity);
        }
        this.capacity = initialCapacity;
        if (loadFactor <= 0.0f) {
            throw new IllegalArgumentException("loadFactor must be > 0: " + loadFactor);
        }
        this.loadFactor = loadFactor;
        this.threshold = (int)(this.capacity * loadFactor);
        this.mask = this.capacity - 1;
        this.hashShift = 31 - Integer.numberOfTrailingZeros(this.capacity);
        this.stashCapacity = Math.max(3, (int)Math.ceil(Math.log(this.capacity)) * 2);
        this.pushIterations = Math.max(Math.min(this.capacity, 8), (int)Math.sqrt(this.capacity) / 8);
        this.keyTable = (K[])new Object[this.capacity + this.stashCapacity];
        this.valueTable = (V[])new Object[this.keyTable.length];
    }
    
    public ObjectMap(final ObjectMap<? extends K, ? extends V> map) {
        this((int)Math.floor(map.capacity * map.loadFactor), map.loadFactor);
        this.stashSize = map.stashSize;
        System.arraycopy(map.keyTable, 0, this.keyTable, 0, map.keyTable.length);
        System.arraycopy(map.valueTable, 0, this.valueTable, 0, map.valueTable.length);
        this.size = map.size;
    }
    
    public V put(final K key, final V value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null.");
        }
        final Object[] keyTable = this.keyTable;
        final int hashCode = key.hashCode();
        final int index1 = hashCode & this.mask;
        final K key2 = (K)keyTable[index1];
        if (key.equals(key2)) {
            final V oldValue = this.valueTable[index1];
            this.valueTable[index1] = value;
            return oldValue;
        }
        final int index2 = this.hash2(hashCode);
        final K key3 = (K)keyTable[index2];
        if (key.equals(key3)) {
            final V oldValue2 = this.valueTable[index2];
            this.valueTable[index2] = value;
            return oldValue2;
        }
        final int index3 = this.hash3(hashCode);
        final K key4 = (K)keyTable[index3];
        if (key.equals(key4)) {
            final V oldValue3 = this.valueTable[index3];
            this.valueTable[index3] = value;
            return oldValue3;
        }
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (key.equals(keyTable[i])) {
                final V oldValue4 = this.valueTable[i];
                this.valueTable[i] = value;
                return oldValue4;
            }
        }
        if (key2 == null) {
            keyTable[index1] = key;
            this.valueTable[index1] = value;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return null;
        }
        if (key3 == null) {
            keyTable[index2] = key;
            this.valueTable[index2] = value;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return null;
        }
        if (key4 == null) {
            keyTable[index3] = key;
            this.valueTable[index3] = value;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return null;
        }
        this.push(key, value, index1, key2, index2, key3, index3, key4);
        return null;
    }
    
    public void putAll(final ObjectMap<? extends K, ? extends V> map) {
        this.ensureCapacity(map.size);
        for (final Entry<? extends K, ? extends V> entry : map) {
            this.put(entry.key, entry.value);
        }
    }
    
    private void putResize(final K key, final V value) {
        final int hashCode = key.hashCode();
        final int index1 = hashCode & this.mask;
        final K key2 = this.keyTable[index1];
        if (key2 == null) {
            this.keyTable[index1] = key;
            this.valueTable[index1] = value;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return;
        }
        final int index2 = this.hash2(hashCode);
        final K key3 = this.keyTable[index2];
        if (key3 == null) {
            this.keyTable[index2] = key;
            this.valueTable[index2] = value;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return;
        }
        final int index3 = this.hash3(hashCode);
        final K key4 = this.keyTable[index3];
        if (key4 == null) {
            this.keyTable[index3] = key;
            this.valueTable[index3] = value;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return;
        }
        this.push(key, value, index1, key2, index2, key3, index3, key4);
    }
    
    private void push(K insertKey, V insertValue, int index1, K key1, int index2, K key2, int index3, K key3) {
        final Object[] keyTable = this.keyTable;
        final Object[] valueTable = this.valueTable;
        final int mask = this.mask;
        int i = 0;
        final int pushIterations = this.pushIterations;
        while (true) {
            K evictedKey = null;
            V evictedValue = null;
            switch (MathUtils.random(2)) {
                case 0: {
                    evictedKey = key1;
                    evictedValue = (V)valueTable[index1];
                    keyTable[index1] = insertKey;
                    valueTable[index1] = insertValue;
                    break;
                }
                case 1: {
                    evictedKey = key2;
                    evictedValue = (V)valueTable[index2];
                    keyTable[index2] = insertKey;
                    valueTable[index2] = insertValue;
                    break;
                }
                default: {
                    evictedKey = key3;
                    evictedValue = (V)valueTable[index3];
                    keyTable[index3] = insertKey;
                    valueTable[index3] = insertValue;
                    break;
                }
            }
            final int hashCode = evictedKey.hashCode();
            index1 = (hashCode & mask);
            key1 = (K)keyTable[index1];
            if (key1 == null) {
                keyTable[index1] = evictedKey;
                valueTable[index1] = evictedValue;
                if (this.size++ >= this.threshold) {
                    this.resize(this.capacity << 1);
                }
                return;
            }
            index2 = this.hash2(hashCode);
            key2 = (K)keyTable[index2];
            if (key2 == null) {
                keyTable[index2] = evictedKey;
                valueTable[index2] = evictedValue;
                if (this.size++ >= this.threshold) {
                    this.resize(this.capacity << 1);
                }
                return;
            }
            index3 = this.hash3(hashCode);
            key3 = (K)keyTable[index3];
            if (key3 == null) {
                keyTable[index3] = evictedKey;
                valueTable[index3] = evictedValue;
                if (this.size++ >= this.threshold) {
                    this.resize(this.capacity << 1);
                }
                return;
            }
            if (++i == pushIterations) {
                this.putStash(evictedKey, evictedValue);
                return;
            }
            insertKey = evictedKey;
            insertValue = evictedValue;
        }
    }
    
    private void putStash(final K key, final V value) {
        if (this.stashSize == this.stashCapacity) {
            this.resize(this.capacity << 1);
            this.putResize(key, value);
            return;
        }
        final int index = this.capacity + this.stashSize;
        this.keyTable[index] = key;
        this.valueTable[index] = value;
        ++this.stashSize;
        ++this.size;
    }
    
    public V get(final K key) {
        final int hashCode = key.hashCode();
        int index = hashCode & this.mask;
        if (!key.equals(this.keyTable[index])) {
            index = this.hash2(hashCode);
            if (!key.equals(this.keyTable[index])) {
                index = this.hash3(hashCode);
                if (!key.equals(this.keyTable[index])) {
                    return this.getStash(key, null);
                }
            }
        }
        return this.valueTable[index];
    }
    
    public V get(final K key, final V defaultValue) {
        final int hashCode = key.hashCode();
        int index = hashCode & this.mask;
        if (!key.equals(this.keyTable[index])) {
            index = this.hash2(hashCode);
            if (!key.equals(this.keyTable[index])) {
                index = this.hash3(hashCode);
                if (!key.equals(this.keyTable[index])) {
                    return this.getStash(key, defaultValue);
                }
            }
        }
        return this.valueTable[index];
    }
    
    private V getStash(final K key, final V defaultValue) {
        final Object[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (key.equals(keyTable[i])) {
                return this.valueTable[i];
            }
        }
        return defaultValue;
    }
    
    public V remove(final K key) {
        final int hashCode = key.hashCode();
        int index = hashCode & this.mask;
        if (key.equals(this.keyTable[index])) {
            this.keyTable[index] = null;
            final V oldValue = this.valueTable[index];
            this.valueTable[index] = null;
            --this.size;
            return oldValue;
        }
        index = this.hash2(hashCode);
        if (key.equals(this.keyTable[index])) {
            this.keyTable[index] = null;
            final V oldValue = this.valueTable[index];
            this.valueTable[index] = null;
            --this.size;
            return oldValue;
        }
        index = this.hash3(hashCode);
        if (key.equals(this.keyTable[index])) {
            this.keyTable[index] = null;
            final V oldValue = this.valueTable[index];
            this.valueTable[index] = null;
            --this.size;
            return oldValue;
        }
        return this.removeStash(key);
    }
    
    V removeStash(final K key) {
        final Object[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (key.equals(keyTable[i])) {
                final V oldValue = this.valueTable[i];
                this.removeStashIndex(i);
                --this.size;
                return oldValue;
            }
        }
        return null;
    }
    
    void removeStashIndex(final int index) {
        --this.stashSize;
        final int lastIndex = this.capacity + this.stashSize;
        if (index < lastIndex) {
            this.keyTable[index] = this.keyTable[lastIndex];
            this.valueTable[index] = this.valueTable[lastIndex];
            this.keyTable[lastIndex] = null;
            this.valueTable[lastIndex] = null;
        }
        else {
            this.keyTable[index] = null;
            this.valueTable[index] = null;
        }
    }
    
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    public void shrink(int maximumCapacity) {
        if (maximumCapacity < 0) {
            throw new IllegalArgumentException("maximumCapacity must be >= 0: " + maximumCapacity);
        }
        if (this.size > maximumCapacity) {
            maximumCapacity = this.size;
        }
        if (this.capacity <= maximumCapacity) {
            return;
        }
        maximumCapacity = MathUtils.nextPowerOfTwo(maximumCapacity);
        this.resize(maximumCapacity);
    }
    
    public void clear(final int maximumCapacity) {
        if (this.capacity <= maximumCapacity) {
            this.clear();
            return;
        }
        this.size = 0;
        this.resize(maximumCapacity);
    }
    
    public void clear() {
        if (this.size == 0) {
            return;
        }
        final Object[] keyTable = this.keyTable;
        final Object[] valueTable = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (i-- > 0) {
            valueTable[i] = (keyTable[i] = null);
        }
        this.size = 0;
        this.stashSize = 0;
    }
    
    public boolean containsValue(final Object value, final boolean identity) {
        final Object[] valueTable = this.valueTable;
        if (value == null) {
            final Object[] keyTable = this.keyTable;
            int i = this.capacity + this.stashSize;
            while (i-- > 0) {
                if (keyTable[i] != null && valueTable[i] == null) {
                    return true;
                }
            }
        }
        else if (identity) {
            int j = this.capacity + this.stashSize;
            while (j-- > 0) {
                if (valueTable[j] == value) {
                    return true;
                }
            }
        }
        else {
            int j = this.capacity + this.stashSize;
            while (j-- > 0) {
                if (value.equals(valueTable[j])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean containsKey(final K key) {
        final int hashCode = key.hashCode();
        int index = hashCode & this.mask;
        if (!key.equals(this.keyTable[index])) {
            index = this.hash2(hashCode);
            if (!key.equals(this.keyTable[index])) {
                index = this.hash3(hashCode);
                if (!key.equals(this.keyTable[index])) {
                    return this.containsKeyStash(key);
                }
            }
        }
        return true;
    }
    
    private boolean containsKeyStash(final K key) {
        final Object[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (key.equals(keyTable[i])) {
                return true;
            }
        }
        return false;
    }
    
    public K findKey(final Object value, final boolean identity) {
        final Object[] valueTable = this.valueTable;
        if (value == null) {
            final Object[] keyTable = this.keyTable;
            int i = this.capacity + this.stashSize;
            while (i-- > 0) {
                if (keyTable[i] != null && valueTable[i] == null) {
                    return (K)keyTable[i];
                }
            }
        }
        else if (identity) {
            int j = this.capacity + this.stashSize;
            while (j-- > 0) {
                if (valueTable[j] == value) {
                    return this.keyTable[j];
                }
            }
        }
        else {
            int j = this.capacity + this.stashSize;
            while (j-- > 0) {
                if (value.equals(valueTable[j])) {
                    return this.keyTable[j];
                }
            }
        }
        return null;
    }
    
    public void ensureCapacity(final int additionalCapacity) {
        if (additionalCapacity < 0) {
            throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
        }
        final int sizeNeeded = this.size + additionalCapacity;
        if (sizeNeeded >= this.threshold) {
            this.resize(MathUtils.nextPowerOfTwo((int)Math.ceil(sizeNeeded / this.loadFactor)));
        }
    }
    
    private void resize(final int newSize) {
        final int oldEndIndex = this.capacity + this.stashSize;
        this.capacity = newSize;
        this.threshold = (int)(newSize * this.loadFactor);
        this.mask = newSize - 1;
        this.hashShift = 31 - Integer.numberOfTrailingZeros(newSize);
        this.stashCapacity = Math.max(3, (int)Math.ceil(Math.log(newSize)) * 2);
        this.pushIterations = Math.max(Math.min(newSize, 8), (int)Math.sqrt(newSize) / 8);
        final Object[] oldKeyTable = this.keyTable;
        final Object[] oldValueTable = this.valueTable;
        this.keyTable = (K[])new Object[newSize + this.stashCapacity];
        this.valueTable = (V[])new Object[newSize + this.stashCapacity];
        final int oldSize = this.size;
        this.size = 0;
        this.stashSize = 0;
        if (oldSize > 0) {
            for (int i = 0; i < oldEndIndex; ++i) {
                final K key = (K)oldKeyTable[i];
                if (key != null) {
                    this.putResize(key, oldValueTable[i]);
                }
            }
        }
    }
    
    private int hash2(int h) {
        h *= -1262997959;
        return (h ^ h >>> this.hashShift) & this.mask;
    }
    
    private int hash3(int h) {
        h *= -825114047;
        return (h ^ h >>> this.hashShift) & this.mask;
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        final Object[] keyTable = this.keyTable;
        final Object[] valueTable = this.valueTable;
        for (int i = 0, n = this.capacity + this.stashSize; i < n; ++i) {
            final K key = (K)keyTable[i];
            if (key != null) {
                h += key.hashCode() * 31;
                final V value = (V)valueTable[i];
                if (value != null) {
                    h += value.hashCode();
                }
            }
        }
        return h;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ObjectMap)) {
            return false;
        }
        final ObjectMap<K, V> other = (ObjectMap<K, V>)obj;
        if (other.size != this.size) {
            return false;
        }
        final Object[] keyTable = this.keyTable;
        final Object[] valueTable = this.valueTable;
        for (int i = 0, n = this.capacity + this.stashSize; i < n; ++i) {
            final K key = (K)keyTable[i];
            if (key != null) {
                final V value = (V)valueTable[i];
                if (value == null) {
                    if (!other.containsKey(key) || other.get(key) != null) {
                        return false;
                    }
                }
                else if (!value.equals(other.get(key))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public String toString(final String separator) {
        return this.toString(separator, false);
    }
    
    @Override
    public String toString() {
        return this.toString(", ", true);
    }
    
    private String toString(final String separator, final boolean braces) {
        if (this.size == 0) {
            return braces ? "{}" : "";
        }
        final com.badlogic.gdx.utils.StringBuilder buffer = new com.badlogic.gdx.utils.StringBuilder(32);
        if (braces) {
            buffer.append('{');
        }
        final Object[] keyTable = this.keyTable;
        final Object[] valueTable = this.valueTable;
        int i = keyTable.length;
        while (true) {
            while (i-- > 0) {
                K key = (K)keyTable[i];
                if (key == null) {
                    continue;
                }
                buffer.append(key);
                buffer.append('=');
                buffer.append(valueTable[i]);
                while (i-- > 0) {
                    key = (K)keyTable[i];
                    if (key == null) {
                        continue;
                    }
                    buffer.append(separator);
                    buffer.append(key);
                    buffer.append('=');
                    buffer.append(valueTable[i]);
                }
                if (braces) {
                    buffer.append('}');
                }
                return buffer.toString();
            }
            continue;
        }
    }
    
    @Override
    public Entries<K, V> iterator() {
        return this.entries();
    }
    
    public Entries<K, V> entries() {
        if (this.entries1 == null) {
            this.entries1 = new Entries((ObjectMap<K, V>)this);
            this.entries2 = new Entries((ObjectMap<K, V>)this);
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
    
    public Values<V> values() {
        if (this.values1 == null) {
            this.values1 = new Values((ObjectMap<?, V>)this);
            this.values2 = new Values((ObjectMap<?, V>)this);
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
    
    public Keys<K> keys() {
        if (this.keys1 == null) {
            this.keys1 = new Keys((ObjectMap<K, ?>)this);
            this.keys2 = new Keys((ObjectMap<K, ?>)this);
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
    
    public static class Entries<K, V> extends MapIterator<K, V, Entry<K, V>>
    {
        Entry<K, V> entry;
        
        public Entries(final ObjectMap<K, V> map) {
            super(map);
            this.entry = new Entry<K, V>();
        }
        
        @Override
        public Entry<K, V> next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            final Object[] keyTable = this.map.keyTable;
            this.entry.key = (K)keyTable[this.nextIndex];
            this.entry.value = (V)this.map.valueTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            this.findNextIndex();
            return this.entry;
        }
        
        @Override
        public boolean hasNext() {
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.hasNext;
        }
        
        @Override
        public Entries<K, V> iterator() {
            return this;
        }
    }
    
    public static class Keys<K> extends MapIterator<K, Object, K>
    {
        public Keys(final ObjectMap<K, ?> map) {
            super(map);
        }
        
        @Override
        public boolean hasNext() {
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.hasNext;
        }
        
        @Override
        public K next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            final K key = (K)this.map.keyTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            this.findNextIndex();
            return key;
        }
        
        @Override
        public Keys<K> iterator() {
            return this;
        }
        
        public Array<K> toArray() {
            return this.toArray(new Array<K>(true, this.map.size));
        }
        
        public Array<K> toArray(final Array<K> array) {
            while (this.hasNext) {
                array.add(this.next());
            }
            return array;
        }
    }
    
    public static class Values<V> extends MapIterator<Object, V, V>
    {
        public Values(final ObjectMap<?, V> map) {
            super(map);
        }
        
        @Override
        public boolean hasNext() {
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.hasNext;
        }
        
        @Override
        public V next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            final V value = (V)this.map.valueTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            this.findNextIndex();
            return value;
        }
        
        @Override
        public Values<V> iterator() {
            return this;
        }
        
        public Array<V> toArray() {
            return this.toArray(new Array<V>(true, this.map.size));
        }
        
        public Array<V> toArray(final Array<V> array) {
            while (this.hasNext) {
                array.add(this.next());
            }
            return array;
        }
    }
    
    public static class Entry<K, V>
    {
        public K key;
        public V value;
        
        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
    
    private abstract static class MapIterator<K, V, I> implements Iterable<I>, Iterator<I>
    {
        public boolean hasNext;
        final ObjectMap<K, V> map;
        int nextIndex;
        int currentIndex;
        boolean valid;
        
        public MapIterator(final ObjectMap<K, V> map) {
            this.valid = true;
            this.map = map;
            this.reset();
        }
        
        public void reset() {
            this.currentIndex = -1;
            this.nextIndex = -1;
            this.findNextIndex();
        }
        
        void findNextIndex() {
            this.hasNext = false;
            final Object[] keyTable = this.map.keyTable;
            final int n = this.map.capacity + this.map.stashSize;
            while (++this.nextIndex < n) {
                if (keyTable[this.nextIndex] != null) {
                    this.hasNext = true;
                    break;
                }
            }
        }
        
        @Override
        public void remove() {
            if (this.currentIndex < 0) {
                throw new IllegalStateException("next must be called before remove.");
            }
            if (this.currentIndex >= this.map.capacity) {
                this.map.removeStashIndex(this.currentIndex);
                this.nextIndex = this.currentIndex - 1;
                this.findNextIndex();
            }
            else {
                this.map.keyTable[this.currentIndex] = null;
                this.map.valueTable[this.currentIndex] = null;
            }
            this.currentIndex = -1;
            final ObjectMap<K, V> map = this.map;
            --map.size;
        }
    }
}
