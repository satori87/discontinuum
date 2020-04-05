// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.NoSuchElementException;
import java.util.Iterator;
import com.badlogic.gdx.math.MathUtils;

public class IntIntMap implements Iterable<Entry>
{
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    private static final int EMPTY = 0;
    public int size;
    int[] keyTable;
    int[] valueTable;
    int capacity;
    int stashSize;
    int zeroValue;
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
    
    public IntIntMap() {
        this(51, 0.8f);
    }
    
    public IntIntMap(final int initialCapacity) {
        this(initialCapacity, 0.8f);
    }
    
    public IntIntMap(int initialCapacity, final float loadFactor) {
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
        this.valueTable = new int[this.keyTable.length];
    }
    
    public IntIntMap(final IntIntMap map) {
        this((int)Math.floor(map.capacity * map.loadFactor), map.loadFactor);
        this.stashSize = map.stashSize;
        System.arraycopy(map.keyTable, 0, this.keyTable, 0, map.keyTable.length);
        System.arraycopy(map.valueTable, 0, this.valueTable, 0, map.valueTable.length);
        this.size = map.size;
        this.zeroValue = map.zeroValue;
        this.hasZeroValue = map.hasZeroValue;
    }
    
    public void put(final int key, final int value) {
        if (key == 0) {
            this.zeroValue = value;
            if (!this.hasZeroValue) {
                this.hasZeroValue = true;
                ++this.size;
            }
            return;
        }
        final int[] keyTable = this.keyTable;
        final int index1 = key & this.mask;
        final int key2 = keyTable[index1];
        if (key == key2) {
            this.valueTable[index1] = value;
            return;
        }
        final int index2 = this.hash2(key);
        final int key3 = keyTable[index2];
        if (key == key3) {
            this.valueTable[index2] = value;
            return;
        }
        final int index3 = this.hash3(key);
        final int key4 = keyTable[index3];
        if (key == key4) {
            this.valueTable[index3] = value;
            return;
        }
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (key == keyTable[i]) {
                this.valueTable[i] = value;
                return;
            }
        }
        if (key2 == 0) {
            keyTable[index1] = key;
            this.valueTable[index1] = value;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return;
        }
        if (key3 == 0) {
            keyTable[index2] = key;
            this.valueTable[index2] = value;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return;
        }
        if (key4 == 0) {
            keyTable[index3] = key;
            this.valueTable[index3] = value;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return;
        }
        this.push(key, value, index1, key2, index2, key3, index3, key4);
    }
    
    public void putAll(final IntIntMap map) {
        for (final Entry entry : map.entries()) {
            this.put(entry.key, entry.value);
        }
    }
    
    private void putResize(final int key, final int value) {
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
    
    private void push(int insertKey, int insertValue, int index1, int key1, int index2, int key2, int index3, int key3) {
        final int[] keyTable = this.keyTable;
        final int[] valueTable = this.valueTable;
        final int mask = this.mask;
        int i = 0;
        final int pushIterations = this.pushIterations;
        while (true) {
            int evictedKey = 0;
            int evictedValue = 0;
            switch (MathUtils.random(2)) {
                case 0: {
                    evictedKey = key1;
                    evictedValue = valueTable[index1];
                    keyTable[index1] = insertKey;
                    valueTable[index1] = insertValue;
                    break;
                }
                case 1: {
                    evictedKey = key2;
                    evictedValue = valueTable[index2];
                    keyTable[index2] = insertKey;
                    valueTable[index2] = insertValue;
                    break;
                }
                default: {
                    evictedKey = key3;
                    evictedValue = valueTable[index3];
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
    
    private void putStash(final int key, final int value) {
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
    
    public int get(final int key, final int defaultValue) {
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
    
    private int getStash(final int key, final int defaultValue) {
        final int[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (key == keyTable[i]) {
                return this.valueTable[i];
            }
        }
        return defaultValue;
    }
    
    public int getAndIncrement(final int key, final int defaultValue, final int increment) {
        if (key != 0) {
            int index = key & this.mask;
            if (key != this.keyTable[index]) {
                index = this.hash2(key);
                if (key != this.keyTable[index]) {
                    index = this.hash3(key);
                    if (key != this.keyTable[index]) {
                        return this.getAndIncrementStash(key, defaultValue, increment);
                    }
                }
            }
            final int value = this.valueTable[index];
            this.valueTable[index] = value + increment;
            return value;
        }
        if (this.hasZeroValue) {
            final int value2 = this.zeroValue;
            this.zeroValue += increment;
            return value2;
        }
        this.hasZeroValue = true;
        this.zeroValue = defaultValue + increment;
        ++this.size;
        return defaultValue;
    }
    
    private int getAndIncrementStash(final int key, final int defaultValue, final int increment) {
        final int[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (key == keyTable[i]) {
                final int value = this.valueTable[i];
                this.valueTable[i] = value + increment;
                return value;
            }
        }
        this.put(key, defaultValue + increment);
        return defaultValue;
    }
    
    public int remove(final int key, final int defaultValue) {
        if (key == 0) {
            if (!this.hasZeroValue) {
                return defaultValue;
            }
            this.hasZeroValue = false;
            --this.size;
            return this.zeroValue;
        }
        else {
            int index = key & this.mask;
            if (key == this.keyTable[index]) {
                this.keyTable[index] = 0;
                final int oldValue = this.valueTable[index];
                --this.size;
                return oldValue;
            }
            index = this.hash2(key);
            if (key == this.keyTable[index]) {
                this.keyTable[index] = 0;
                final int oldValue = this.valueTable[index];
                --this.size;
                return oldValue;
            }
            index = this.hash3(key);
            if (key == this.keyTable[index]) {
                this.keyTable[index] = 0;
                final int oldValue = this.valueTable[index];
                --this.size;
                return oldValue;
            }
            return this.removeStash(key, defaultValue);
        }
    }
    
    int removeStash(final int key, final int defaultValue) {
        final int[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (key == keyTable[i]) {
                final int oldValue = this.valueTable[i];
                this.removeStashIndex(i);
                --this.size;
                return oldValue;
            }
        }
        return defaultValue;
    }
    
    void removeStashIndex(final int index) {
        --this.stashSize;
        final int lastIndex = this.capacity + this.stashSize;
        if (index < lastIndex) {
            this.keyTable[index] = this.keyTable[lastIndex];
            this.valueTable[index] = this.valueTable[lastIndex];
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
        this.hasZeroValue = false;
        this.size = 0;
        this.resize(maximumCapacity);
    }
    
    public void clear() {
        if (this.size == 0) {
            return;
        }
        final int[] keyTable = this.keyTable;
        int i = this.capacity + this.stashSize;
        while (i-- > 0) {
            keyTable[i] = 0;
        }
        this.size = 0;
        this.stashSize = 0;
        this.hasZeroValue = false;
    }
    
    public boolean containsValue(final int value) {
        if (this.hasZeroValue && this.zeroValue == value) {
            return true;
        }
        final int[] keyTable = this.keyTable;
        final int[] valueTable = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (i-- > 0) {
            if (keyTable[i] != 0 && valueTable[i] == value) {
                return true;
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
            if (key == keyTable[i]) {
                return true;
            }
        }
        return false;
    }
    
    public int findKey(final int value, final int notFound) {
        if (this.hasZeroValue && this.zeroValue == value) {
            return 0;
        }
        final int[] keyTable = this.keyTable;
        final int[] valueTable = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (i-- > 0) {
            if (keyTable[i] != 0 && valueTable[i] == value) {
                return keyTable[i];
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
        final int[] oldValueTable = this.valueTable;
        this.keyTable = new int[newSize + this.stashCapacity];
        this.valueTable = new int[newSize + this.stashCapacity];
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
        if (this.hasZeroValue) {
            h += Float.floatToIntBits((float)this.zeroValue);
        }
        final int[] keyTable = this.keyTable;
        final int[] valueTable = this.valueTable;
        for (int i = 0, n = this.capacity + this.stashSize; i < n; ++i) {
            final int key = keyTable[i];
            if (key != 0) {
                h += key * 31;
                final int value = valueTable[i];
                h += value;
            }
        }
        return h;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof IntIntMap)) {
            return false;
        }
        final IntIntMap other = (IntIntMap)obj;
        if (other.size != this.size) {
            return false;
        }
        if (other.hasZeroValue != this.hasZeroValue) {
            return false;
        }
        if (this.hasZeroValue && other.zeroValue != this.zeroValue) {
            return false;
        }
        final int[] keyTable = this.keyTable;
        final int[] valueTable = this.valueTable;
        for (int i = 0, n = this.capacity + this.stashSize; i < n; ++i) {
            final int key = keyTable[i];
            if (key != 0) {
                final int otherValue = other.get(key, 0);
                if (otherValue == 0 && !other.containsKey(key)) {
                    return false;
                }
                final int value = valueTable[i];
                if (otherValue != value) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        if (this.size == 0) {
            return "{}";
        }
        final com.badlogic.gdx.utils.StringBuilder buffer = new com.badlogic.gdx.utils.StringBuilder(32);
        buffer.append('{');
        final int[] keyTable = this.keyTable;
        final int[] valueTable = this.valueTable;
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
        buffer.append('}');
        return buffer.toString();
    }
    
    @Override
    public Iterator<Entry> iterator() {
        return this.entries();
    }
    
    public Entries entries() {
        if (this.entries1 == null) {
            this.entries1 = new Entries(this);
            this.entries2 = new Entries(this);
        }
        if (!this.entries1.valid) {
            this.entries1.reset();
            this.entries1.valid = true;
            this.entries2.valid = false;
            return this.entries1;
        }
        this.entries2.reset();
        this.entries2.valid = true;
        this.entries1.valid = false;
        return this.entries2;
    }
    
    public Values values() {
        if (this.values1 == null) {
            this.values1 = new Values(this);
            this.values2 = new Values(this);
        }
        if (!this.values1.valid) {
            this.values1.reset();
            this.values1.valid = true;
            this.values2.valid = false;
            return this.values1;
        }
        this.values2.reset();
        this.values2.valid = true;
        this.values1.valid = false;
        return this.values2;
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
    
    public static class Entries extends MapIterator implements Iterable<Entry>, Iterator<Entry>
    {
        private Entry entry;
        
        public Entries(final IntIntMap map) {
            super(map);
            this.entry = new Entry();
        }
        
        @Override
        public Entry next() {
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
        public Iterator<Entry> iterator() {
            return this;
        }
        
        @Override
        public void remove() {
            super.remove();
        }
    }
    
    public static class Keys extends MapIterator
    {
        public Keys(final IntIntMap map) {
            super(map);
        }
        
        public boolean hasNext() {
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.hasNext;
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
    
    public static class Values extends MapIterator
    {
        public Values(final IntIntMap map) {
            super(map);
        }
        
        public boolean hasNext() {
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.hasNext;
        }
        
        public int next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            int value;
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
        
        public IntArray toArray() {
            final IntArray array = new IntArray(true, this.map.size);
            while (this.hasNext) {
                array.add(this.next());
            }
            return array;
        }
    }
    
    public static class Entry
    {
        public int key;
        public int value;
        
        @Override
        public String toString() {
            return String.valueOf(this.key) + "=" + this.value;
        }
    }
    
    private static class MapIterator
    {
        static final int INDEX_ILLEGAL = -2;
        static final int INDEX_ZERO = -1;
        public boolean hasNext;
        final IntIntMap map;
        int nextIndex;
        int currentIndex;
        boolean valid;
        
        public MapIterator(final IntIntMap map) {
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
                }
            }
            this.currentIndex = -2;
            final IntIntMap map = this.map;
            --map.size;
        }
    }
}
