// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Arrays;

public class ShortArray
{
    public short[] items;
    public int size;
    public boolean ordered;
    
    public ShortArray() {
        this(true, 16);
    }
    
    public ShortArray(final int capacity) {
        this(true, capacity);
    }
    
    public ShortArray(final boolean ordered, final int capacity) {
        this.ordered = ordered;
        this.items = new short[capacity];
    }
    
    public ShortArray(final ShortArray array) {
        this.ordered = array.ordered;
        this.size = array.size;
        this.items = new short[this.size];
        System.arraycopy(array.items, 0, this.items, 0, this.size);
    }
    
    public ShortArray(final short[] array) {
        this(true, array, 0, array.length);
    }
    
    public ShortArray(final boolean ordered, final short[] array, final int startIndex, final int count) {
        this(ordered, count);
        this.size = count;
        System.arraycopy(array, startIndex, this.items, 0, count);
    }
    
    public void add(final int value) {
        short[] items = this.items;
        if (this.size == items.length) {
            items = this.resize(Math.max(8, (int)(this.size * 1.75f)));
        }
        items[this.size++] = (short)value;
    }
    
    public void add(final short value) {
        short[] items = this.items;
        if (this.size == items.length) {
            items = this.resize(Math.max(8, (int)(this.size * 1.75f)));
        }
        items[this.size++] = value;
    }
    
    public void add(final short value1, final short value2) {
        short[] items = this.items;
        if (this.size + 1 >= items.length) {
            items = this.resize(Math.max(8, (int)(this.size * 1.75f)));
        }
        items[this.size] = value1;
        items[this.size + 1] = value2;
        this.size += 2;
    }
    
    public void add(final short value1, final short value2, final short value3) {
        short[] items = this.items;
        if (this.size + 2 >= items.length) {
            items = this.resize(Math.max(8, (int)(this.size * 1.75f)));
        }
        items[this.size] = value1;
        items[this.size + 1] = value2;
        items[this.size + 2] = value3;
        this.size += 3;
    }
    
    public void add(final short value1, final short value2, final short value3, final short value4) {
        short[] items = this.items;
        if (this.size + 3 >= items.length) {
            items = this.resize(Math.max(8, (int)(this.size * 1.8f)));
        }
        items[this.size] = value1;
        items[this.size + 1] = value2;
        items[this.size + 2] = value3;
        items[this.size + 3] = value4;
        this.size += 4;
    }
    
    public void addAll(final ShortArray array) {
        this.addAll(array, 0, array.size);
    }
    
    public void addAll(final ShortArray array, final int offset, final int length) {
        if (offset + length > array.size) {
            throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
        }
        this.addAll(array.items, offset, length);
    }
    
    public void addAll(final short... array) {
        this.addAll(array, 0, array.length);
    }
    
    public void addAll(final short[] array, final int offset, final int length) {
        short[] items = this.items;
        final int sizeNeeded = this.size + length;
        if (sizeNeeded > items.length) {
            items = this.resize(Math.max(8, (int)(sizeNeeded * 1.75f)));
        }
        System.arraycopy(array, offset, items, this.size, length);
        this.size += length;
    }
    
    public short get(final int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
        return this.items[index];
    }
    
    public void set(final int index, final short value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
        this.items[index] = value;
    }
    
    public void incr(final int index, final short value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
        final short[] items = this.items;
        items[index] += value;
    }
    
    public void mul(final int index, final short value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
        final short[] items = this.items;
        items[index] *= value;
    }
    
    public void insert(final int index, final short value) {
        if (index > this.size) {
            throw new IndexOutOfBoundsException("index can't be > size: " + index + " > " + this.size);
        }
        short[] items = this.items;
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
        final short[] items = this.items;
        final short firstValue = items[first];
        items[first] = items[second];
        items[second] = firstValue;
    }
    
    public boolean contains(final short value) {
        int i = this.size - 1;
        final short[] items = this.items;
        while (i >= 0) {
            if (items[i--] == value) {
                return true;
            }
        }
        return false;
    }
    
    public int indexOf(final short value) {
        final short[] items = this.items;
        for (int i = 0, n = this.size; i < n; ++i) {
            if (items[i] == value) {
                return i;
            }
        }
        return -1;
    }
    
    public int lastIndexOf(final char value) {
        final short[] items = this.items;
        for (int i = this.size - 1; i >= 0; --i) {
            if (items[i] == value) {
                return i;
            }
        }
        return -1;
    }
    
    public boolean removeValue(final short value) {
        final short[] items = this.items;
        for (int i = 0, n = this.size; i < n; ++i) {
            if (items[i] == value) {
                this.removeIndex(i);
                return true;
            }
        }
        return false;
    }
    
    public short removeIndex(final int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
        final short[] items = this.items;
        final short value = items[index];
        --this.size;
        if (this.ordered) {
            System.arraycopy(items, index + 1, items, index, this.size - index);
        }
        else {
            items[index] = items[this.size];
        }
        return value;
    }
    
    public void removeRange(final int start, final int end) {
        if (end >= this.size) {
            throw new IndexOutOfBoundsException("end can't be >= size: " + end + " >= " + this.size);
        }
        if (start > end) {
            throw new IndexOutOfBoundsException("start can't be > end: " + start + " > " + end);
        }
        final short[] items = this.items;
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
    
    public boolean removeAll(final ShortArray array) {
        final int startSize;
        int size = startSize = this.size;
        final short[] items = this.items;
        for (int i = 0, n = array.size; i < n; ++i) {
            final short item = array.get(i);
            for (int ii = 0; ii < size; ++ii) {
                if (item == items[ii]) {
                    this.removeIndex(ii);
                    --size;
                    break;
                }
            }
        }
        return size != startSize;
    }
    
    public short pop() {
        final short[] items = this.items;
        final int size = this.size - 1;
        this.size = size;
        return items[size];
    }
    
    public short peek() {
        return this.items[this.size - 1];
    }
    
    public short first() {
        if (this.size == 0) {
            throw new IllegalStateException("Array is empty.");
        }
        return this.items[0];
    }
    
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    public void clear() {
        this.size = 0;
    }
    
    public short[] shrink() {
        if (this.items.length != this.size) {
            this.resize(this.size);
        }
        return this.items;
    }
    
    public short[] ensureCapacity(final int additionalCapacity) {
        if (additionalCapacity < 0) {
            throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
        }
        final int sizeNeeded = this.size + additionalCapacity;
        if (sizeNeeded > this.items.length) {
            this.resize(Math.max(8, sizeNeeded));
        }
        return this.items;
    }
    
    public short[] setSize(final int newSize) {
        if (newSize < 0) {
            throw new IllegalArgumentException("newSize must be >= 0: " + newSize);
        }
        if (newSize > this.items.length) {
            this.resize(Math.max(8, newSize));
        }
        this.size = newSize;
        return this.items;
    }
    
    protected short[] resize(final int newSize) {
        final short[] newItems = new short[newSize];
        final short[] items = this.items;
        System.arraycopy(items, 0, newItems, 0, Math.min(this.size, newItems.length));
        return this.items = newItems;
    }
    
    public void sort() {
        Arrays.sort(this.items, 0, this.size);
    }
    
    public void reverse() {
        final short[] items = this.items;
        int i = 0;
        final int lastIndex = this.size - 1;
        for (int n = this.size / 2; i < n; ++i) {
            final int ii = lastIndex - i;
            final short temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }
    
    public void shuffle() {
        final short[] items = this.items;
        for (int i = this.size - 1; i >= 0; --i) {
            final int ii = MathUtils.random(i);
            final short temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }
    
    public void truncate(final int newSize) {
        if (this.size > newSize) {
            this.size = newSize;
        }
    }
    
    public short random() {
        if (this.size == 0) {
            return 0;
        }
        return this.items[MathUtils.random(0, this.size - 1)];
    }
    
    public short[] toArray() {
        final short[] array = new short[this.size];
        System.arraycopy(this.items, 0, array, 0, this.size);
        return array;
    }
    
    @Override
    public int hashCode() {
        if (!this.ordered) {
            return super.hashCode();
        }
        final short[] items = this.items;
        int h = 1;
        for (int i = 0, n = this.size; i < n; ++i) {
            h = h * 31 + items[i];
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
        if (!(object instanceof ShortArray)) {
            return false;
        }
        final ShortArray array = (ShortArray)object;
        if (!array.ordered) {
            return false;
        }
        final int n = this.size;
        if (n != array.size) {
            return false;
        }
        final short[] items1 = this.items;
        final short[] items2 = array.items;
        for (int i = 0; i < n; ++i) {
            if (this.items[i] != array.items[i]) {
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
        final short[] items = this.items;
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
        final short[] items = this.items;
        final com.badlogic.gdx.utils.StringBuilder buffer = new com.badlogic.gdx.utils.StringBuilder(32);
        buffer.append(items[0]);
        for (int i = 1; i < this.size; ++i) {
            buffer.append(separator);
            buffer.append(items[i]);
        }
        return buffer.toString();
    }
    
    public static ShortArray with(final short... array) {
        return new ShortArray(array);
    }
}
