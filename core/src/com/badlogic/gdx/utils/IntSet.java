// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.NoSuchElementException;
import com.badlogic.gdx.math.MathUtils;

public class IntSet
{
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    private static final int EMPTY = 0;
    public int size;
    int[] keyTable;
    int capacity;
    int stashSize;
    boolean hasZeroValue;
    private float loadFactor;
    private int hashShift;
    private int mask;
    private int threshold;
    private int stashCapacity;
    private int pushIterations;
    private IntSetIterator iterator1;
    private IntSetIterator iterator2;
    
    public IntSet() {
        this(51, 0.8f);
    }
    
    public IntSet(final int initialCapacity) {
        this(initialCapacity, 0.8f);
    }
    
    public IntSet(int initialCapacity, final float loadFactor) {
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
    }
    
    public IntSet(final IntSet set) {
        this((int)Math.floor(set.capacity * set.loadFactor), set.loadFactor);
        this.stashSize = set.stashSize;
        System.arraycopy(set.keyTable, 0, this.keyTable, 0, set.keyTable.length);
        this.size = set.size;
        this.hasZeroValue = set.hasZeroValue;
    }
    
    public boolean add(final int key) {
        if (key == 0) {
            if (this.hasZeroValue) {
                return false;
            }
            this.hasZeroValue = true;
            ++this.size;
            return true;
        }
        else {
            final int[] keyTable = this.keyTable;
            final int index1 = key & this.mask;
            final int key2 = keyTable[index1];
            if (key2 == key) {
                return false;
            }
            final int index2 = this.hash2(key);
            final int key3 = keyTable[index2];
            if (key3 == key) {
                return false;
            }
            final int index3 = this.hash3(key);
            final int key4 = keyTable[index3];
            if (key4 == key) {
                return false;
            }
            for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
                if (keyTable[i] == key) {
                    return false;
                }
            }
            if (key2 == 0) {
                keyTable[index1] = key;
                if (this.size++ >= this.threshold) {
                    this.resize(this.capacity << 1);
                }
                return true;
            }
            if (key3 == 0) {
                keyTable[index2] = key;
                if (this.size++ >= this.threshold) {
                    this.resize(this.capacity << 1);
                }
                return true;
            }
            if (key4 == 0) {
                keyTable[index3] = key;
                if (this.size++ >= this.threshold) {
                    this.resize(this.capacity << 1);
                }
                return true;
            }
            this.push(key, index1, key2, index2, key3, index3, key4);
            return true;
        }
    }
    
    public void addAll(final IntArray array) {
        this.addAll(array, 0, array.size);
    }
    
    public void addAll(final IntArray array, final int offset, final int length) {
        if (offset + length > array.size) {
            throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
        }
        this.addAll(array.items, offset, length);
    }
    
    public void addAll(final int... array) {
        this.addAll(array, 0, array.length);
    }
    
    public void addAll(final int[] array, final int offset, final int length) {
        this.ensureCapacity(length);
        for (int i = offset, n = i + length; i < n; ++i) {
            this.add(array[i]);
        }
    }
    
    public void addAll(final IntSet set) {
        this.ensureCapacity(set.size);
        final IntSetIterator iterator = set.iterator();
        while (iterator.hasNext) {
            this.add(iterator.next());
        }
    }
    
    private void addResize(final int key) {
        if (key == 0) {
            this.hasZeroValue = true;
            return;
        }
        final int index1 = key & this.mask;
        final int key2 = this.keyTable[index1];
        if (key2 == 0) {
            this.keyTable[index1] = key;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return;
        }
        final int index2 = this.hash2(key);
        final int key3 = this.keyTable[index2];
        if (key3 == 0) {
            this.keyTable[index2] = key;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return;
        }
        final int index3 = this.hash3(key);
        final int key4 = this.keyTable[index3];
        if (key4 == 0) {
            this.keyTable[index3] = key;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return;
        }
        this.push(key, index1, key2, index2, key3, index3, key4);
    }
    
    private void push(int insertKey, int index1, int key1, int index2, int key2, int index3, int key3) {
        final int[] keyTable = this.keyTable;
        final int mask = this.mask;
        int i = 0;
        final int pushIterations = this.pushIterations;
        while (true) {
            int evictedKey = 0;
            switch (MathUtils.random(2)) {
                case 0: {
                    evictedKey = key1;
                    keyTable[index1] = insertKey;
                    break;
                }
                case 1: {
                    evictedKey = key2;
                    keyTable[index2] = insertKey;
                    break;
                }
                default: {
                    evictedKey = key3;
                    keyTable[index3] = insertKey;
                    break;
                }
            }
            index1 = (evictedKey & mask);
            key1 = keyTable[index1];
            if (key1 == 0) {
                keyTable[index1] = evictedKey;
                if (this.size++ >= this.threshold) {
                    this.resize(this.capacity << 1);
                }
                return;
            }
            index2 = this.hash2(evictedKey);
            key2 = keyTable[index2];
            if (key2 == 0) {
                keyTable[index2] = evictedKey;
                if (this.size++ >= this.threshold) {
                    this.resize(this.capacity << 1);
                }
                return;
            }
            index3 = this.hash3(evictedKey);
            key3 = keyTable[index3];
            if (key3 == 0) {
                keyTable[index3] = evictedKey;
                if (this.size++ >= this.threshold) {
                    this.resize(this.capacity << 1);
                }
                return;
            }
            if (++i == pushIterations) {
                this.addStash(evictedKey);
                return;
            }
            insertKey = evictedKey;
        }
    }
    
    private void addStash(final int key) {
        if (this.stashSize == this.stashCapacity) {
            this.resize(this.capacity << 1);
            this.addResize(key);
            return;
        }
        final int index = this.capacity + this.stashSize;
        this.keyTable[index] = key;
        ++this.stashSize;
        ++this.size;
    }
    
    public boolean remove(final int key) {
        if (key == 0) {
            if (!this.hasZeroValue) {
                return false;
            }
            this.hasZeroValue = false;
            --this.size;
            return true;
        }
        else {
            int index = key & this.mask;
            if (this.keyTable[index] == key) {
                this.keyTable[index] = 0;
                --this.size;
                return true;
            }
            index = this.hash2(key);
            if (this.keyTable[index] == key) {
                this.keyTable[index] = 0;
                --this.size;
                return true;
            }
            index = this.hash3(key);
            if (this.keyTable[index] == key) {
                this.keyTable[index] = 0;
                --this.size;
                return true;
            }
            return this.removeStash(key);
        }
    }
    
    boolean removeStash(final int key) {
        final int[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (keyTable[i] == key) {
                this.removeStashIndex(i);
                --this.size;
                return true;
            }
        }
        return false;
    }
    
    void removeStashIndex(final int index) {
        --this.stashSize;
        final int lastIndex = this.capacity + this.stashSize;
        if (index < lastIndex) {
            this.keyTable[index] = this.keyTable[lastIndex];
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
    
    public boolean contains(final int key) {
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
    
    public int first() {
        if (this.hasZeroValue) {
            return 0;
        }
        final int[] keyTable = this.keyTable;
        for (int i = 0, n = this.capacity + this.stashSize; i < n; ++i) {
            if (keyTable[i] != 0) {
                return keyTable[i];
            }
        }
        throw new IllegalStateException("IntSet is empty.");
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
        this.keyTable = new int[newSize + this.stashCapacity];
        final int oldSize = this.size;
        this.size = (this.hasZeroValue ? 1 : 0);
        this.stashSize = 0;
        if (oldSize > 0) {
            for (final int key : oldKeyTable) {
                if (key != 0) {
                    this.addResize(key);
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
        for (int i = 0, n = this.capacity + this.stashSize; i < n; ++i) {
            if (this.keyTable[i] != 0) {
                h += this.keyTable[i];
            }
        }
        return h;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof IntSet)) {
            return false;
        }
        final IntSet other = (IntSet)obj;
        if (other.size != this.size) {
            return false;
        }
        if (other.hasZeroValue != this.hasZeroValue) {
            return false;
        }
        for (int i = 0, n = this.capacity + this.stashSize; i < n; ++i) {
            if (this.keyTable[i] != 0 && !other.contains(this.keyTable[i])) {
                return false;
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
        int i = keyTable.length;
        if (this.hasZeroValue) {
            buffer.append("0");
        }
        else {
            while (i-- > 0) {
                final int key = keyTable[i];
                if (key == 0) {
                    continue;
                }
                buffer.append(key);
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
        }
        buffer.append(']');
        return buffer.toString();
    }
    
    public IntSetIterator iterator() {
        if (this.iterator1 == null) {
            this.iterator1 = new IntSetIterator(this);
            this.iterator2 = new IntSetIterator(this);
        }
        if (!this.iterator1.valid) {
            this.iterator1.reset();
            this.iterator1.valid = true;
            this.iterator2.valid = false;
            return this.iterator1;
        }
        this.iterator2.reset();
        this.iterator2.valid = true;
        this.iterator1.valid = false;
        return this.iterator2;
    }
    
    public static IntSet with(final int... array) {
        final IntSet set = new IntSet();
        set.addAll(array);
        return set;
    }
    
    public static class IntSetIterator
    {
        static final int INDEX_ILLEGAL = -2;
        static final int INDEX_ZERO = -1;
        public boolean hasNext;
        final IntSet set;
        int nextIndex;
        int currentIndex;
        boolean valid;
        
        public IntSetIterator(final IntSet set) {
            this.valid = true;
            this.set = set;
            this.reset();
        }
        
        public void reset() {
            this.currentIndex = -2;
            this.nextIndex = -1;
            if (this.set.hasZeroValue) {
                this.hasNext = true;
            }
            else {
                this.findNextIndex();
            }
        }
        
        void findNextIndex() {
            this.hasNext = false;
            final int[] keyTable = this.set.keyTable;
            final int n = this.set.capacity + this.set.stashSize;
            while (++this.nextIndex < n) {
                if (keyTable[this.nextIndex] != 0) {
                    this.hasNext = true;
                    break;
                }
            }
        }
        
        public void remove() {
            if (this.currentIndex == -1 && this.set.hasZeroValue) {
                this.set.hasZeroValue = false;
            }
            else {
                if (this.currentIndex < 0) {
                    throw new IllegalStateException("next must be called before remove.");
                }
                if (this.currentIndex >= this.set.capacity) {
                    this.set.removeStashIndex(this.currentIndex);
                    this.nextIndex = this.currentIndex - 1;
                    this.findNextIndex();
                }
                else {
                    this.set.keyTable[this.currentIndex] = 0;
                }
            }
            this.currentIndex = -2;
            final IntSet set = this.set;
            --set.size;
        }
        
        public int next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            final int key = (this.nextIndex == -1) ? 0 : this.set.keyTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            this.findNextIndex();
            return key;
        }
        
        public IntArray toArray() {
            final IntArray array = new IntArray(true, this.set.size);
            while (this.hasNext) {
                array.add(this.next());
            }
            return array;
        }
    }
}
