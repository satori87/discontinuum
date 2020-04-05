// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.NoSuchElementException;
import java.util.Iterator;
import com.badlogic.gdx.math.MathUtils;

public class ObjectSet<T> implements Iterable<T>
{
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    public int size;
    T[] keyTable;
    int capacity;
    int stashSize;
    private float loadFactor;
    private int hashShift;
    private int mask;
    private int threshold;
    private int stashCapacity;
    private int pushIterations;
    private ObjectSetIterator iterator1;
    private ObjectSetIterator iterator2;
    
    public ObjectSet() {
        this(51, 0.8f);
    }
    
    public ObjectSet(final int initialCapacity) {
        this(initialCapacity, 0.8f);
    }
    
    public ObjectSet(int initialCapacity, final float loadFactor) {
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
        this.keyTable = (T[])new Object[this.capacity + this.stashCapacity];
    }
    
    public ObjectSet(final ObjectSet set) {
        this((int)Math.floor(set.capacity * set.loadFactor), set.loadFactor);
        this.stashSize = set.stashSize;
        System.arraycopy(set.keyTable, 0, this.keyTable, 0, set.keyTable.length);
        this.size = set.size;
    }
    
    public boolean add(final T key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null.");
        }
        final Object[] keyTable = this.keyTable;
        final int hashCode = key.hashCode();
        final int index1 = hashCode & this.mask;
        final T key2 = (T)keyTable[index1];
        if (key.equals(key2)) {
            return false;
        }
        final int index2 = this.hash2(hashCode);
        final T key3 = (T)keyTable[index2];
        if (key.equals(key3)) {
            return false;
        }
        final int index3 = this.hash3(hashCode);
        final T key4 = (T)keyTable[index3];
        if (key.equals(key4)) {
            return false;
        }
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (key.equals(keyTable[i])) {
                return false;
            }
        }
        if (key2 == null) {
            keyTable[index1] = key;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return true;
        }
        if (key3 == null) {
            keyTable[index2] = key;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return true;
        }
        if (key4 == null) {
            keyTable[index3] = key;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return true;
        }
        this.push(key, index1, key2, index2, key3, index3, key4);
        return true;
    }
    
    public void addAll(final Array<? extends T> array) {
        this.addAll(array, 0, array.size);
    }
    
    public void addAll(final Array<? extends T> array, final int offset, final int length) {
        if (offset + length > array.size) {
            throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
        }
        this.addAll(array.items, offset, length);
    }
    
    public void addAll(final T... array) {
        this.addAll(array, 0, array.length);
    }
    
    public void addAll(final T[] array, final int offset, final int length) {
        this.ensureCapacity(length);
        for (int i = offset, n = i + length; i < n; ++i) {
            this.add(array[i]);
        }
    }
    
    public void addAll(final ObjectSet<T> set) {
        this.ensureCapacity(set.size);
        for (final T key : set) {
            this.add(key);
        }
    }
    
    private void addResize(final T key) {
        final int hashCode = key.hashCode();
        final int index1 = hashCode & this.mask;
        final T key2 = this.keyTable[index1];
        if (key2 == null) {
            this.keyTable[index1] = key;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return;
        }
        final int index2 = this.hash2(hashCode);
        final T key3 = this.keyTable[index2];
        if (key3 == null) {
            this.keyTable[index2] = key;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return;
        }
        final int index3 = this.hash3(hashCode);
        final T key4 = this.keyTable[index3];
        if (key4 == null) {
            this.keyTable[index3] = key;
            if (this.size++ >= this.threshold) {
                this.resize(this.capacity << 1);
            }
            return;
        }
        this.push(key, index1, key2, index2, key3, index3, key4);
    }
    
    private void push(T insertKey, int index1, T key1, int index2, T key2, int index3, T key3) {
        final Object[] keyTable = this.keyTable;
        final int mask = this.mask;
        int i = 0;
        final int pushIterations = this.pushIterations;
        while (true) {
            T evictedKey = null;
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
            final int hashCode = evictedKey.hashCode();
            index1 = (hashCode & mask);
            key1 = (T)keyTable[index1];
            if (key1 == null) {
                keyTable[index1] = evictedKey;
                if (this.size++ >= this.threshold) {
                    this.resize(this.capacity << 1);
                }
                return;
            }
            index2 = this.hash2(hashCode);
            key2 = (T)keyTable[index2];
            if (key2 == null) {
                keyTable[index2] = evictedKey;
                if (this.size++ >= this.threshold) {
                    this.resize(this.capacity << 1);
                }
                return;
            }
            index3 = this.hash3(hashCode);
            key3 = (T)keyTable[index3];
            if (key3 == null) {
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
    
    private void addStash(final T key) {
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
    
    public boolean remove(final T key) {
        final int hashCode = key.hashCode();
        int index = hashCode & this.mask;
        if (key.equals(this.keyTable[index])) {
            this.keyTable[index] = null;
            --this.size;
            return true;
        }
        index = this.hash2(hashCode);
        if (key.equals(this.keyTable[index])) {
            this.keyTable[index] = null;
            --this.size;
            return true;
        }
        index = this.hash3(hashCode);
        if (key.equals(this.keyTable[index])) {
            this.keyTable[index] = null;
            --this.size;
            return true;
        }
        return this.removeStash(key);
    }
    
    boolean removeStash(final T key) {
        final Object[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (key.equals(keyTable[i])) {
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
            this.keyTable[lastIndex] = null;
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
        int i = this.capacity + this.stashSize;
        while (i-- > 0) {
            keyTable[i] = null;
        }
        this.size = 0;
        this.stashSize = 0;
    }
    
    public boolean contains(final T key) {
        final int hashCode = key.hashCode();
        int index = hashCode & this.mask;
        if (!key.equals(this.keyTable[index])) {
            index = this.hash2(hashCode);
            if (!key.equals(this.keyTable[index])) {
                index = this.hash3(hashCode);
                if (!key.equals(this.keyTable[index])) {
                    return this.getKeyStash(key) != null;
                }
            }
        }
        return true;
    }
    
    public T get(final T key) {
        final int hashCode = key.hashCode();
        int index = hashCode & this.mask;
        T found = this.keyTable[index];
        if (!key.equals(found)) {
            index = this.hash2(hashCode);
            found = this.keyTable[index];
            if (!key.equals(found)) {
                index = this.hash3(hashCode);
                found = this.keyTable[index];
                if (!key.equals(found)) {
                    return this.getKeyStash(key);
                }
            }
        }
        return found;
    }
    
    private T getKeyStash(final T key) {
        final Object[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; ++i) {
            if (key.equals(keyTable[i])) {
                return (T)keyTable[i];
            }
        }
        return null;
    }
    
    public T first() {
        final Object[] keyTable = this.keyTable;
        for (int i = 0, n = this.capacity + this.stashSize; i < n; ++i) {
            if (keyTable[i] != null) {
                return (T)keyTable[i];
            }
        }
        throw new IllegalStateException("ObjectSet is empty.");
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
        this.keyTable = (T[])new Object[newSize + this.stashCapacity];
        final int oldSize = this.size;
        this.size = 0;
        this.stashSize = 0;
        if (oldSize > 0) {
            for (final T key : oldKeyTable) {
                if (key != null) {
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
            if (this.keyTable[i] != null) {
                h += this.keyTable[i].hashCode();
            }
        }
        return h;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ObjectSet)) {
            return false;
        }
        final ObjectSet other = (ObjectSet)obj;
        if (other.size != this.size) {
            return false;
        }
        final Object[] keyTable = this.keyTable;
        for (int i = 0, n = this.capacity + this.stashSize; i < n; ++i) {
            if (keyTable[i] != null && !other.contains(keyTable[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return String.valueOf('{') + this.toString(", ") + '}';
    }
    
    public String toString(final String separator) {
        if (this.size == 0) {
            return "";
        }
        final com.badlogic.gdx.utils.StringBuilder buffer = new com.badlogic.gdx.utils.StringBuilder(32);
        final Object[] keyTable = this.keyTable;
        int i = keyTable.length;
        while (true) {
            while (i-- > 0) {
                T key = (T)keyTable[i];
                if (key == null) {
                    continue;
                }
                buffer.append(key);
                while (i-- > 0) {
                    key = (T)keyTable[i];
                    if (key == null) {
                        continue;
                    }
                    buffer.append(separator);
                    buffer.append(key);
                }
                return buffer.toString();
            }
            continue;
        }
    }
    
    @Override
    public ObjectSetIterator<T> iterator() {
        if (this.iterator1 == null) {
            this.iterator1 = new ObjectSetIterator((ObjectSet<K>)this);
            this.iterator2 = new ObjectSetIterator((ObjectSet<K>)this);
        }
        if (!this.iterator1.valid) {
            this.iterator1.reset();
            this.iterator1.valid = true;
            this.iterator2.valid = false;
            return (ObjectSetIterator<T>)this.iterator1;
        }
        this.iterator2.reset();
        this.iterator2.valid = true;
        this.iterator1.valid = false;
        return (ObjectSetIterator<T>)this.iterator2;
    }
    
    public static <T> ObjectSet<T> with(final T... array) {
        final ObjectSet set = new ObjectSet();
        set.addAll(array);
        return (ObjectSet<T>)set;
    }
    
    public static class ObjectSetIterator<K> implements Iterable<K>, Iterator<K>
    {
        public boolean hasNext;
        final ObjectSet<K> set;
        int nextIndex;
        int currentIndex;
        boolean valid;
        
        public ObjectSetIterator(final ObjectSet<K> set) {
            this.valid = true;
            this.set = set;
            this.reset();
        }
        
        public void reset() {
            this.currentIndex = -1;
            this.nextIndex = -1;
            this.findNextIndex();
        }
        
        private void findNextIndex() {
            this.hasNext = false;
            final Object[] keyTable = this.set.keyTable;
            final int n = this.set.capacity + this.set.stashSize;
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
            if (this.currentIndex >= this.set.capacity) {
                this.set.removeStashIndex(this.currentIndex);
                this.nextIndex = this.currentIndex - 1;
                this.findNextIndex();
            }
            else {
                this.set.keyTable[this.currentIndex] = null;
            }
            this.currentIndex = -1;
            final ObjectSet<K> set = this.set;
            --set.size;
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
            final K key = this.set.keyTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            this.findNextIndex();
            return key;
        }
        
        @Override
        public ObjectSetIterator<K> iterator() {
            return this;
        }
        
        public Array<K> toArray(final Array<K> array) {
            while (this.hasNext) {
                array.add(this.next());
            }
            return array;
        }
        
        public Array<K> toArray() {
            return this.toArray(new Array<K>(true, this.set.size));
        }
    }
}
