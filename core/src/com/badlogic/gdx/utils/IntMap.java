// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.NoSuchElementException;
import java.util.Iterator;
import com.badlogic.gdx.math.MathUtils;

public class IntMap<V> implements Iterable<Entry<V>>
{
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    private static final int EMPTY = 0;
    public int size;
    int[] keyTable;
    V[] valueTable;
    int capacity;
    int stashSize;
    V zeroValue;
    boolean hasZeroValue;
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
    
    public IntMap() {
        this(51, 0.8f);
    }
    
    public IntMap(final int initialCapacity) {
        this(initialCapacity, 0.8f);
    }
    
    public IntMap(int initialCapacity, final float loadFactor) {
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
        this.keyTable = new int[this.capacity + this.stashCapacity];
        this.valueTable = (V[])new Object[this.keyTable.length];
    }
    
    public IntMap(final IntMap<? extends V> map) {
        this((int)Math.floor(map.capacity * map.loadFactor), map.loadFactor);
        this.stashSize = map.stashSize;
        System.arraycopy(map.keyTable, 0, this.keyTable, 0, map.keyTable.length);
        System.arraycopy(map.valueTable, 0, this.valueTable, 0, map.valueTable.length);
        this.size = map.size;
        this.zeroValue = (V)map.zeroValue;
        this.hasZeroValue = map.hasZeroValue;
    }
    
    public V put(final int key, final V value) {
        if (key == 0) {
            final V oldValue = this.zeroValue;
            this.zeroValue = value;
            if (!this.hasZeroValue) {
                this.hasZeroValue = true;
                ++this.size;
            }
            return oldValue;
        }
        final int[] keyTable = this.keyTable;
        final int index1 = key & this.mask;
        final int key2 = keyTable[index1];
        if (key2 == key) {
            final V oldValue2 = this.valueTable[index1];
            this.valueTable[index1] = value;
            return oldValue2;
        }
        final int index2 = this.hash2(key);
        final int key3 = keyTable[index2];
        if (key3 == key) {
            final V oldValue3 = this.valueTable[index2];
            this.valueTable[index2] = value;
            return oldValue3;
        }
        final int index3 = this.hash3(key);
        final int key4 = keyTable[index3];
        if (key4 == key) {
            final V oldValue4 = this.valueTable[index3];
            this.valueTable[index3] = value;
            return oldValue4;
        }
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (keyTable[i] == key) {
                final V oldValue5 = this.valueTable[i];
                this.valueTable[i] = value;
                return oldValue5;
            }
        }
        if (key2 == 0) {
            keyTable[index1] = key;
            this.valueTable[index1] = value;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return null;
        }
        if (key3 == 0) {
            keyTable[index2] = key;
            this.valueTable[index2] = value;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return null;
        }
        if (key4 == 0) {
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
    
    public void putAll(final IntMap<? extends V> map) {
        for (final Entry<? extends V> entry : map.entries()) {
            this.put(entry.key, entry.value);
        }
    }
    
    private void putResize(final int key, final V value) {
        if (key == 0) {
            this.zeroValue = value;
            this.hasZeroValue = true;
            return;
        }
        final int index1 = key & this.mask;
        final int key2 = this.keyTable[index1];
        if (key2 == 0) {
            this.keyTable[index1] = key;
            this.valueTable[index1] = value;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return;
        }
        final int index2 = this.hash2(key);
        final int key3 = this.keyTable[index2];
        if (key3 == 0) {
            this.keyTable[index2] = key;
            this.valueTable[index2] = value;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return;
        }
        final int index3 = this.hash3(key);
        final int key4 = this.keyTable[index3];
        if (key4 == 0) {
            this.keyTable[index3] = key;
            this.valueTable[index3] = value;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return;
        }
        this.push(key, value, index1, key2, index2, key3, index3, key4);
    }
    
    private void push(int insertKey, V insertValue, int index1, int key1, int index2, int key2, int index3, int key3) {
        final int[] keyTable = this.keyTable;
        final Object[] valueTable = this.valueTable;
        final int mask = this.mask;
        int i = 0;
        final int pushIterations = this.pushIterations;
        while (true) {
            int evictedKey = 0;
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
            index1 = (evictedKey & mask);
            key1 = keyTable[index1];
            if (key1 == 0) {
                keyTable[index1] = evictedKey;
                valueTable[index1] = evictedValue;
                if (this.size++ >= this.threshold) {
                    this.resize(this.capacity << 1);
                }
                return;
            }
            index2 = this.hash2(evictedKey);
            key2 = keyTable[index2];
            if (key2 == 0) {
                keyTable[index2] = evictedKey;
                valueTable[index2] = evictedValue;
                if (this.size++ >= this.threshold) {
                    this.resize(this.capacity << 1);
                }
                return;
            }
            index3 = this.hash3(evictedKey);
            key3 = keyTable[index3];
            if (key3 == 0) {
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
    
    private void putStash(final int key, final V value) {
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
    
    public V get(final int key) {
        if (key != 0) {
            int index = key & this.mask;
            if (this.keyTable[index] != key) {
                index = this.hash2(key);
                if (this.keyTable[index] != key) {
                    index = this.hash3(key);
                    if (this.keyTable[index] != key) {
                        return this.getStash(key, null);
                    }
                }
            }
            return this.valueTable[index];
        }
        if (!this.hasZeroValue) {
            return null;
        }
        return this.zeroValue;
    }
    
    public V get(final int key, final V defaultValue) {
        if (key != 0) {
            int index = key & this.mask;
            if (this.keyTable[index] != key) {
                index = this.hash2(key);
                if (this.keyTable[index] != key) {
                    index = this.hash3(key);
                    if (this.keyTable[index] != key) {
                        return this.getStash(key, defaultValue);
                    }
                }
            }
            return this.valueTable[index];
        }
        if (!this.hasZeroValue) {
            return defaultValue;
        }
        return this.zeroValue;
    }
    
    private V getStash(final int key, final V defaultValue) {
        final int[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (keyTable[i] == key) {
                return this.valueTable[i];
            }
        }
        return defaultValue;
    }
    
    public V remove(final int key) {
        if (key == 0) {
            if (!this.hasZeroValue) {
                return null;
            }
            final V oldValue = this.zeroValue;
            this.zeroValue = null;
            this.hasZeroValue = false;
            --this.size;
            return oldValue;
        }
        else {
            int index = key & this.mask;
            if (this.keyTable[index] == key) {
                this.keyTable[index] = 0;
                final V oldValue2 = this.valueTable[index];
                this.valueTable[index] = null;
                --this.size;
                return oldValue2;
            }
            index = this.hash2(key);
            if (this.keyTable[index] == key) {
                this.keyTable[index] = 0;
                final V oldValue2 = this.valueTable[index];
                this.valueTable[index] = null;
                --this.size;
                return oldValue2;
            }
            index = this.hash3(key);
            if (this.keyTable[index] == key) {
                this.keyTable[index] = 0;
                final V oldValue2 = this.valueTable[index];
                this.valueTable[index] = null;
                --this.size;
                return oldValue2;
            }
            return this.removeStash(key);
        }
    }
    
    V removeStash(final int key) {
        final int[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (keyTable[i] == key) {
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
            this.valueTable[lastIndex] = null;
        }
        else {
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
        this.zeroValue = null;
        this.hasZeroValue = false;
        this.size = 0;
        this.resize(maximumCapacity);
    }
    
    public void clear() {
        if (this.size == 0) {
            return;
        }
        final int[] keyTable = this.keyTable;
        final Object[] valueTable = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (i-- > 0) {
            keyTable[i] = 0;
            valueTable[i] = null;
        }
        this.size = 0;
        this.stashSize = 0;
        this.zeroValue = null;
        this.hasZeroValue = false;
    }
    
    public boolean containsValue(final Object value, final boolean identity) {
        final Object[] valueTable = this.valueTable;
        if (value == null) {
            if (this.hasZeroValue && this.zeroValue == null) {
                return true;
            }
            final int[] keyTable = this.keyTable;
            int i = this.capacity + this.stashSize;
            while (i-- > 0) {
                if (keyTable[i] != 0 && valueTable[i] == null) {
                    return true;
                }
            }
        }
        else if (identity) {
            if (value == this.zeroValue) {
                return true;
            }
            int j = this.capacity + this.stashSize;
            while (j-- > 0) {
                if (valueTable[j] == value) {
                    return true;
                }
            }
        }
        else {
            if (this.hasZeroValue && value.equals(this.zeroValue)) {
                return true;
            }
            int j = this.capacity + this.stashSize;
            while (j-- > 0) {
                if (value.equals(valueTable[j])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean containsKey(final int key) {
        if (key == 0) {
            return this.hasZeroValue;
        }
        int index = key & this.mask;
        if (this.keyTable[index] != key) {
            index = this.hash2(key);
            if (this.keyTable[index] != key) {
                index = this.hash3(key);
                if (this.keyTable[index] != key) {
                    return this.containsKeyStash(key);
                }
            }
        }
        return true;
    }
    
    private boolean containsKeyStash(final int key) {
        final int[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (keyTable[i] == key) {
                return true;
            }
        }
        return false;
    }
    
    public int findKey(final Object value, final boolean identity, final int notFound) {
        final Object[] valueTable = this.valueTable;
        if (value == null) {
            if (this.hasZeroValue && this.zeroValue == null) {
                return 0;
            }
            final int[] keyTable = this.keyTable;
            int i = this.capacity + this.stashSize;
            while (i-- > 0) {
                if (keyTable[i] != 0 && valueTable[i] == null) {
                    return keyTable[i];
                }
            }
        }
        else if (identity) {
            if (value == this.zeroValue) {
                return 0;
            }
            int j = this.capacity + this.stashSize;
            while (j-- > 0) {
                if (valueTable[j] == value) {
                    return this.keyTable[j];
                }
            }
        }
        else {
            if (this.hasZeroValue && value.equals(this.zeroValue)) {
                return 0;
            }
            int j = this.capacity + this.stashSize;
            while (j-- > 0) {
                if (value.equals(valueTable[j])) {
                    return this.keyTable[j];
                }
            }
        }
        return notFound;
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
        final int[] oldKeyTable = this.keyTable;
        final Object[] oldValueTable = this.valueTable;
        this.keyTable = new int[newSize + this.stashCapacity];
        this.valueTable = (V[])new Object[newSize + this.stashCapacity];
        final int oldSize = this.size;
        this.size = (this.hasZeroValue ? 1 : 0);
        this.stashSize = 0;
        if (oldSize > 0) {
            for (int i = 0; i < oldEndIndex; ++i) {
                final int key = oldKeyTable[i];
                if (key != 0) {
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
        if (this.hasZeroValue && this.zeroValue != null) {
            h += this.zeroValue.hashCode();
        }
        final int[] keyTable = this.keyTable;
        final Object[] valueTable = this.valueTable;
        for (int i = 0, n = this.capacity + this.stashSize; i < n; ++i) {
            final int key = keyTable[i];
            if (key != 0) {
                h += key * 31;
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
        if (!(obj instanceof IntMap)) {
            return false;
        }
        final IntMap<V> other = (IntMap<V>)obj;
        if (other.size != this.size) {
            return false;
        }
        if (other.hasZeroValue != this.hasZeroValue) {
            return false;
        }
        if (this.hasZeroValue) {
            if (other.zeroValue == null) {
                if (this.zeroValue != null) {
                    return false;
                }
            }
            else if (!other.zeroValue.equals(this.zeroValue)) {
                return false;
            }
        }
        final int[] keyTable = this.keyTable;
        final Object[] valueTable = this.valueTable;
        for (int i = 0, n = this.capacity + this.stashSize; i < n; ++i) {
            final int key = keyTable[i];
            if (key != 0) {
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
    
    @Override
    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        final com.badlogic.gdx.utils.StringBuilder buffer = new com.badlogic.gdx.utils.StringBuilder(32);
        buffer.append('[');
        final int[] keyTable = this.keyTable;
        final Object[] valueTable = this.valueTable;
        int i = keyTable.length;
        if (this.hasZeroValue) {
            buffer.append("0=");
            buffer.append(this.zeroValue);
        }
        else {
            while (i-- > 0) {
                final int key = keyTable[i];
                if (key == 0) {
                    continue;
                }
                buffer.append(key);
                buffer.append('=');
                buffer.append(valueTable[i]);
                break;
            }
        }
        while (i-- > 0) {
            final int key = keyTable[i];
            if (key == 0) {
                continue;
            }
            buffer.append(", ");
            buffer.append(key);
            buffer.append('=');
            buffer.append(valueTable[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }
    
    @Override
    public Iterator<Entry<V>> iterator() {
        return this.entries();
    }
    
    public Entries<V> entries() {
        if (this.entries1 == null) {
            this.entries1 = new Entries(this);
            this.entries2 = new Entries(this);
        }
        if (!this.entries1.valid) {
            this.entries1.reset();
            this.entries1.valid = true;
            this.entries2.valid = false;
            return (Entries<V>)this.entries1;
        }
        this.entries2.reset();
        this.entries2.valid = true;
        this.entries1.valid = false;
        return (Entries<V>)this.entries2;
    }
    
    public Values<V> values() {
        if (this.values1 == null) {
            this.values1 = new Values((IntMap<V>)this);
            this.values2 = new Values((IntMap<V>)this);
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
    
    public Keys keys() {
        if (this.keys1 == null) {
            this.keys1 = new Keys(this);
            this.keys2 = new Keys(this);
        }
        if (!this.keys1.valid) {
            this.keys1.reset();
            this.keys1.valid = true;
            this.keys2.valid = false;
            return this.keys1;
        }
        this.keys2.reset();
        this.keys2.valid = true;
        this.keys1.valid = false;
        return this.keys2;
    }
    
    public static class Entries<V> extends MapIterator<V> implements Iterable<Entry<V>>, Iterator<Entry<V>>
    {
        private Entry<V> entry;
        
        public Entries(final IntMap map) {
            super(map);
            this.entry = new Entry<V>();
        }
        
        @Override
        public Entry<V> next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            final int[] keyTable = this.map.keyTable;
            if (this.nextIndex == -1) {
                this.entry.key = 0;
                this.entry.value = this.map.zeroValue;
            }
            else {
                this.entry.key = keyTable[this.nextIndex];
                this.entry.value = this.map.valueTable[this.nextIndex];
            }
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
        public Iterator<Entry<V>> iterator() {
            return this;
        }
        
        @Override
        public void remove() {
            super.remove();
        }
    }
    
    public static class Keys extends MapIterator
    {
        public Keys(final IntMap map) {
            super(map);
        }
        
        public int next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            final int key = (this.nextIndex == -1) ? 0 : this.map.keyTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            this.findNextIndex();
            return key;
        }
        
        public IntArray toArray() {
            final IntArray array = new IntArray(true, this.map.size);
            while (this.hasNext) {
                array.add(this.next());
            }
            return array;
        }
    }
    
    public static class Values<V> extends MapIterator<V> implements Iterable<V>, Iterator<V>
    {
        public Values(final IntMap<V> map) {
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
            V value;
            if (this.nextIndex == -1) {
                value = this.map.zeroValue;
            }
            else {
                value = this.map.valueTable[this.nextIndex];
            }
            this.currentIndex = this.nextIndex;
            this.findNextIndex();
            return value;
        }
        
        @Override
        public Iterator<V> iterator() {
            return this;
        }
        
        public Array<V> toArray() {
            final Array array = new Array(true, this.map.size);
            while (this.hasNext) {
                array.add(this.next());
            }
            return (Array<V>)array;
        }
        
        @Override
        public void remove() {
            super.remove();
        }
    }
    
    public static class Entry<V>
    {
        public int key;
        public V value;
        
        @Override
        public String toString() {
            return String.valueOf(this.key) + "=" + this.value;
        }
    }
    
    private static class MapIterator<V>
    {
        static final int INDEX_ILLEGAL = -2;
        static final int INDEX_ZERO = -1;
        public boolean hasNext;
        final IntMap<V> map;
        int nextIndex;
        int currentIndex;
        boolean valid;
        
        public MapIterator(final IntMap<V> map) {
            this.valid = true;
            this.map = map;
            this.reset();
        }
        
        public void reset() {
            this.currentIndex = -2;
            this.nextIndex = -1;
            if (this.map.hasZeroValue) {
                this.hasNext = true;
            }
            else {
                this.findNextIndex();
            }
        }
        
        void findNextIndex() {
            this.hasNext = false;
            final int[] keyTable = this.map.keyTable;
            final int n = this.map.capacity + this.map.stashSize;
            while (++this.nextIndex < n) {
                if (keyTable[this.nextIndex] != 0) {
                    this.hasNext = true;
                    break;
                }
            }
        }
        
        public void remove() {
            if (this.currentIndex == -1 && this.map.hasZeroValue) {
                this.map.zeroValue = null;
                this.map.hasZeroValue = false;
            }
            else {
                if (this.currentIndex < 0) {
                    throw new IllegalStateException("next must be called before remove.");
                }
                if (this.currentIndex >= this.map.capacity) {
                    this.map.removeStashIndex(this.currentIndex);
                    this.nextIndex = this.currentIndex - 1;
                    this.findNextIndex();
                }
                else {
                    this.map.keyTable[this.currentIndex] = 0;
                    this.map.valueTable[this.currentIndex] = null;
                }
            }
            this.currentIndex = -2;
            final IntMap<V> map = this.map;
            --map.size;
        }
    }
}
