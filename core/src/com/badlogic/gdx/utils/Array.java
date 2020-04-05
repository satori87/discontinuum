// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.NoSuchElementException;
import java.util.Iterator;
import com.badlogic.gdx.math.MathUtils;
import java.util.Comparator;
import com.badlogic.gdx.utils.reflect.ArrayReflection;

public class Array<T> implements Iterable<T>
{
    public T[] items;
    public int size;
    public boolean ordered;
    private ArrayIterable iterable;
    private Predicate.PredicateIterable<T> predicateIterable;
    
    public Array() {
        this(true, 16);
    }
    
    public Array(final int capacity) {
        this(true, capacity);
    }
    
    public Array(final boolean ordered, final int capacity) {
        this.ordered = ordered;
        this.items = (T[])new Object[capacity];
    }
    
    public Array(final boolean ordered, final int capacity, final Class arrayType) {
        this.ordered = ordered;
        this.items = (T[])ArrayReflection.newInstance(arrayType, capacity);
    }
    
    public Array(final Class arrayType) {
        this(true, 16, arrayType);
    }
    
    public Array(final Array<? extends T> array) {
        this(array.ordered, array.size, array.items.getClass().getComponentType());
        this.size = array.size;
        System.arraycopy(array.items, 0, this.items, 0, this.size);
    }
    
    public Array(final T[] array) {
        this(true, array, 0, array.length);
    }
    
    public Array(final boolean ordered, final T[] array, final int start, final int count) {
        this(ordered, count, array.getClass().getComponentType());
        this.size = count;
        System.arraycopy(array, start, this.items, 0, this.size);
    }
    
    public void add(final T value) {
        Object[] items = this.items;
        if (this.size == items.length) {
            items = this.resize(Math.max(8, (int)(this.size * 1.75f)));
        }
        items[this.size++] = value;
    }
    
    public void add(final T value1, final T value2) {
        Object[] items = this.items;
        if (this.size + 1 >= items.length) {
            items = this.resize(Math.max(8, (int)(this.size * 1.75f)));
        }
        items[this.size] = value1;
        items[this.size + 1] = value2;
        this.size += 2;
    }
    
    public void add(final T value1, final T value2, final T value3) {
        Object[] items = this.items;
        if (this.size + 2 >= items.length) {
            items = this.resize(Math.max(8, (int)(this.size * 1.75f)));
        }
        items[this.size] = value1;
        items[this.size + 1] = value2;
        items[this.size + 2] = value3;
        this.size += 3;
    }
    
    public void add(final T value1, final T value2, final T value3, final T value4) {
        Object[] items = this.items;
        if (this.size + 3 >= items.length) {
            items = this.resize(Math.max(8, (int)(this.size * 1.8f)));
        }
        items[this.size] = value1;
        items[this.size + 1] = value2;
        items[this.size + 2] = value3;
        items[this.size + 3] = value4;
        this.size += 4;
    }
    
    public void addAll(final Array<? extends T> array) {
        this.addAll(array, 0, array.size);
    }
    
    public void addAll(final Array<? extends T> array, final int start, final int count) {
        if (start + count > array.size) {
            throw new IllegalArgumentException("start + count must be <= size: " + start + " + " + count + " <= " + array.size);
        }
        this.addAll(array.items, start, count);
    }
    
    public void addAll(final T... array) {
        this.addAll(array, 0, array.length);
    }
    
    public void addAll(final T[] array, final int start, final int count) {
        Object[] items = this.items;
        final int sizeNeeded = this.size + count;
        if (sizeNeeded > items.length) {
            items = this.resize(Math.max(8, (int)(sizeNeeded * 1.75f)));
        }
        System.arraycopy(array, start, items, this.size, count);
        this.size += count;
    }
    
    public T get(final int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
        return this.items[index];
    }
    
    public void set(final int index, final T value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
        this.items[index] = value;
    }
    
    public void insert(final int index, final T value) {
        if (index > this.size) {
            throw new IndexOutOfBoundsException("index can't be > size: " + index + " > " + this.size);
        }
        Object[] items = this.items;
        if (this.size == items.length) {
            items = this.resize(Math.max(8, (int)(this.size * 1.75f)));
        }
        if (this.ordered) {
            System.arraycopy(items, index, items, index + 1, this.size - index);
        }
        else {
            items[this.size] = items[index];
        }
        ++this.size;
        items[index] = value;
    }
    
    public void swap(final int first, final int second) {
        if (first >= this.size) {
            throw new IndexOutOfBoundsException("first can't be >= size: " + first + " >= " + this.size);
        }
        if (second >= this.size) {
            throw new IndexOutOfBoundsException("second can't be >= size: " + second + " >= " + this.size);
        }
        final Object[] items = this.items;
        final T firstValue = (T)items[first];
        items[first] = items[second];
        items[second] = firstValue;
    }
    
    public boolean contains(final T value, final boolean identity) {
        final Object[] items = this.items;
        int i = this.size - 1;
        if (!identity) {
            if (value != null) {
                while (i >= 0) {
                    if (value.equals(items[i--])) {
                        return true;
                    }
                }
                return false;
            }
        }
        while (i >= 0) {
            if (items[i--] == value) {
                return true;
            }
        }
        return false;
    }
    
    public int indexOf(final T value, final boolean identity) {
        final Object[] items = this.items;
        if (identity || value == null) {
            for (int i = 0, n = this.size; i < n; ++i) {
                if (items[i] == value) {
                    return i;
                }
            }
        }
        else {
            for (int i = 0, n = this.size; i < n; ++i) {
                if (value.equals(items[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int lastIndexOf(final T value, final boolean identity) {
        final Object[] items = this.items;
        if (identity || value == null) {
            for (int i = this.size - 1; i >= 0; --i) {
                if (items[i] == value) {
                    return i;
                }
            }
        }
        else {
            for (int i = this.size - 1; i >= 0; --i) {
                if (value.equals(items[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public boolean removeValue(final T value, final boolean identity) {
        final Object[] items = this.items;
        if (identity || value == null) {
            for (int i = 0, n = this.size; i < n; ++i) {
                if (items[i] == value) {
                    this.removeIndex(i);
                    return true;
                }
            }
        }
        else {
            for (int i = 0, n = this.size; i < n; ++i) {
                if (value.equals(items[i])) {
                    this.removeIndex(i);
                    return true;
                }
            }
        }
        return false;
    }
    
    public T removeIndex(final int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
        final Object[] items = this.items;
        final T value = (T)items[index];
        --this.size;
        if (this.ordered) {
            System.arraycopy(items, index + 1, items, index, this.size - index);
        }
        else {
            items[index] = items[this.size];
        }
        items[this.size] = null;
        return value;
    }
    
    public void removeRange(final int start, final int end) {
        if (end >= this.size) {
            throw new IndexOutOfBoundsException("end can't be >= size: " + end + " >= " + this.size);
        }
        if (start > end) {
            throw new IndexOutOfBoundsException("start can't be > end: " + start + " > " + end);
        }
        final Object[] items = this.items;
        final int count = end - start + 1;
        if (this.ordered) {
            System.arraycopy(items, start + count, items, start, this.size - (start + count));
        }
        else {
            final int lastIndex = this.size - 1;
            for (int i = 0; i < count; ++i) {
                items[start + i] = items[lastIndex - i];
            }
        }
        this.size -= count;
    }
    
    public boolean removeAll(final Array<? extends T> array, final boolean identity) {
        final int startSize;
        int size = startSize = this.size;
        final Object[] items = this.items;
        if (identity) {
            for (int i = 0, n = array.size; i < n; ++i) {
                final T item = (T)array.get(i);
                for (int ii = 0; ii < size; ++ii) {
                    if (item == items[ii]) {
                        this.removeIndex(ii);
                        --size;
                        break;
                    }
                }
            }
        }
        else {
            for (int i = 0, n = array.size; i < n; ++i) {
                final T item = (T)array.get(i);
                for (int ii = 0; ii < size; ++ii) {
                    if (item.equals(items[ii])) {
                        this.removeIndex(ii);
                        --size;
                        break;
                    }
                }
            }
        }
        return size != startSize;
    }
    
    public T pop() {
        if (this.size == 0) {
            throw new IllegalStateException("Array is empty.");
        }
        --this.size;
        final T item = this.items[this.size];
        this.items[this.size] = null;
        return item;
    }
    
    public T peek() {
        if (this.size == 0) {
            throw new IllegalStateException("Array is empty.");
        }
        return this.items[this.size - 1];
    }
    
    public T first() {
        if (this.size == 0) {
            throw new IllegalStateException("Array is empty.");
        }
        return this.items[0];
    }
    
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    public void clear() {
        final Object[] items = this.items;
        for (int i = 0, n = this.size; i < n; ++i) {
            items[i] = null;
        }
        this.size = 0;
    }
    
    public T[] shrink() {
        if (this.items.length != this.size) {
            this.resize(this.size);
        }
        return this.items;
    }
    
    public T[] ensureCapacity(final int additionalCapacity) {
        if (additionalCapacity < 0) {
            throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
        }
        final int sizeNeeded = this.size + additionalCapacity;
        if (sizeNeeded > this.items.length) {
            this.resize(Math.max(8, sizeNeeded));
        }
        return this.items;
    }
    
    public T[] setSize(final int newSize) {
        this.truncate(newSize);
        if (newSize > this.items.length) {
            this.resize(Math.max(8, newSize));
        }
        this.size = newSize;
        return this.items;
    }
    
    protected T[] resize(final int newSize) {
        final Object[] items = this.items;
        final Object[] newItems = (Object[])ArrayReflection.newInstance(items.getClass().getComponentType(), newSize);
        System.arraycopy(items, 0, newItems, 0, Math.min(this.size, newItems.length));
        return this.items = (T[])newItems;
    }
    
    public void sort() {
        Sort.instance().sort(this.items, 0, this.size);
    }
    
    public void sort(final Comparator<? super T> comparator) {
        Sort.instance().sort(this.items, comparator, 0, this.size);
    }
    
    public T selectRanked(final Comparator<T> comparator, final int kthLowest) {
        if (kthLowest < 1) {
            throw new GdxRuntimeException("nth_lowest must be greater than 0, 1 = first, 2 = second...");
        }
        return Select.instance().select(this.items, comparator, kthLowest, this.size);
    }
    
    public int selectRankedIndex(final Comparator<T> comparator, final int kthLowest) {
        if (kthLowest < 1) {
            throw new GdxRuntimeException("nth_lowest must be greater than 0, 1 = first, 2 = second...");
        }
        return Select.instance().selectIndex(this.items, comparator, kthLowest, this.size);
    }
    
    public void reverse() {
        final Object[] items = this.items;
        int i = 0;
        final int lastIndex = this.size - 1;
        for (int n = this.size / 2; i < n; ++i) {
            final int ii = lastIndex - i;
            final T temp = (T)items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }
    
    public void shuffle() {
        final Object[] items = this.items;
        for (int i = this.size - 1; i >= 0; --i) {
            final int ii = MathUtils.random(i);
            final T temp = (T)items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }
    
    @Override
    public Iterator<T> iterator() {
        if (this.iterable == null) {
            this.iterable = new ArrayIterable((Array<T>)this);
        }
        return this.iterable.iterator();
    }
    
    public Iterable<T> select(final Predicate<T> predicate) {
        if (this.predicateIterable == null) {
            this.predicateIterable = new Predicate.PredicateIterable<T>(this, predicate);
        }
        else {
            this.predicateIterable.set(this, predicate);
        }
        return this.predicateIterable;
    }
    
    public void truncate(final int newSize) {
        if (newSize < 0) {
            throw new IllegalArgumentException("newSize must be >= 0: " + newSize);
        }
        if (this.size <= newSize) {
            return;
        }
        for (int i = newSize; i < this.size; ++i) {
            this.items[i] = null;
        }
        this.size = newSize;
    }
    
    public T random() {
        if (this.size == 0) {
            return null;
        }
        return this.items[MathUtils.random(0, this.size - 1)];
    }
    
    public T[] toArray() {
        return this.toArray(this.items.getClass().getComponentType());
    }
    
    public <V> V[] toArray(final Class type) {
        final Object[] result = (Object[])ArrayReflection.newInstance(type, this.size);
        System.arraycopy(this.items, 0, result, 0, this.size);
        return (V[])result;
    }
    
    @Override
    public int hashCode() {
        if (!this.ordered) {
            return super.hashCode();
        }
        final Object[] items = this.items;
        int h = 1;
        for (int i = 0, n = this.size; i < n; ++i) {
            h *= 31;
            final Object item = items[i];
            if (item != null) {
                h += item.hashCode();
            }
        }
        return h;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!this.ordered) {
            return false;
        }
        if (!(object instanceof Array)) {
            return false;
        }
        final Array array = (Array)object;
        if (!array.ordered) {
            return false;
        }
        final int n = this.size;
        if (n != array.size) {
            return false;
        }
        final Object[] items1 = this.items;
        final Object[] items2 = array.items;
        for (int i = 0; i < n; ++i) {
            final Object o1 = items1[i];
            final Object o2 = items2[i];
            if (o1 == null) {
                if (o2 != null) {
                    return false;
                }
            }
            else if (!o1.equals(o2)) {
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
        final Object[] items = this.items;
        final com.badlogic.gdx.utils.StringBuilder buffer = new com.badlogic.gdx.utils.StringBuilder(32);
        buffer.append('[');
        buffer.append(items[0]);
        for (int i = 1; i < this.size; ++i) {
            buffer.append(", ");
            buffer.append(items[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }
    
    public String toString(final String separator) {
        if (this.size == 0) {
            return "";
        }
        final Object[] items = this.items;
        final com.badlogic.gdx.utils.StringBuilder buffer = new com.badlogic.gdx.utils.StringBuilder(32);
        buffer.append(items[0]);
        for (int i = 1; i < this.size; ++i) {
            buffer.append(separator);
            buffer.append(items[i]);
        }
        return buffer.toString();
    }
    
    public static <T> Array<T> of(final Class<T> arrayType) {
        return new Array<T>(arrayType);
    }
    
    public static <T> Array<T> of(final boolean ordered, final int capacity, final Class<T> arrayType) {
        return new Array<T>(ordered, capacity, arrayType);
    }
    
    public static <T> Array<T> with(final T... array) {
        return new Array<T>(array);
    }
    
    public static class ArrayIterator<T> implements Iterator<T>, Iterable<T>
    {
        private final Array<T> array;
        private final boolean allowRemove;
        int index;
        boolean valid;
        
        public ArrayIterator(final Array<T> array) {
            this(array, true);
        }
        
        public ArrayIterator(final Array<T> array, final boolean allowRemove) {
            this.valid = true;
            this.array = array;
            this.allowRemove = allowRemove;
        }
        
        @Override
        public boolean hasNext() {
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.index < this.array.size;
        }
        
        @Override
        public T next() {
            if (this.index >= this.array.size) {
                throw new NoSuchElementException(String.valueOf(this.index));
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.array.items[this.index++];
        }
        
        @Override
        public void remove() {
            if (!this.allowRemove) {
                throw new GdxRuntimeException("Remove not allowed.");
            }
            --this.index;
            this.array.removeIndex(this.index);
        }
        
        public void reset() {
            this.index = 0;
        }
        
        @Override
        public Iterator<T> iterator() {
            return this;
        }
    }
    
    public static class ArrayIterable<T> implements Iterable<T>
    {
        private final Array<T> array;
        private final boolean allowRemove;
        private ArrayIterator iterator1;
        private ArrayIterator iterator2;
        
        public ArrayIterable(final Array<T> array) {
            this(array, true);
        }
        
        public ArrayIterable(final Array<T> array, final boolean allowRemove) {
            this.array = array;
            this.allowRemove = allowRemove;
        }
        
        @Override
        public Iterator<T> iterator() {
            if (this.iterator1 == null) {
                this.iterator1 = new ArrayIterator((Array<T>)this.array, this.allowRemove);
                this.iterator2 = new ArrayIterator((Array<T>)this.array, this.allowRemove);
            }
            if (!this.iterator1.valid) {
                this.iterator1.index = 0;
                this.iterator1.valid = true;
                this.iterator2.valid = false;
                return (Iterator<T>)this.iterator1;
            }
            this.iterator2.index = 0;
            this.iterator2.valid = true;
            this.iterator1.valid = false;
            return (Iterator<T>)this.iterator2;
        }
    }
}
