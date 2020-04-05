// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import com.badlogic.gdx.utils.reflect.ArrayReflection;

public class Queue<T> implements Iterable<T>
{
    protected T[] values;
    protected int head;
    protected int tail;
    public int size;
    private QueueIterable iterable;
    
    public Queue() {
        this(16);
    }
    
    public Queue(final int initialSize) {
        this.head = 0;
        this.tail = 0;
        this.size = 0;
        this.values = (T[])new Object[initialSize];
    }
    
    public Queue(final int initialSize, final Class<T> type) {
        this.head = 0;
        this.tail = 0;
        this.size = 0;
        this.values = (T[])ArrayReflection.newInstance(type, initialSize);
    }
    
    public void addLast(final T object) {
        Object[] values = this.values;
        if (this.size == values.length) {
            this.resize(values.length << 1);
            values = this.values;
        }
        values[this.tail++] = object;
        if (this.tail == values.length) {
            this.tail = 0;
        }
        ++this.size;
    }
    
    public void addFirst(final T object) {
        Object[] values = this.values;
        if (this.size == values.length) {
            this.resize(values.length << 1);
            values = this.values;
        }
        int head = this.head;
        if (--head == -1) {
            head = values.length - 1;
        }
        values[head] = object;
        this.head = head;
        ++this.size;
    }
    
    public void ensureCapacity(final int additional) {
        final int needed = this.size + additional;
        if (this.values.length < needed) {
            this.resize(needed);
        }
    }
    
    protected void resize(final int newSize) {
        final Object[] values = this.values;
        final int head = this.head;
        final int tail = this.tail;
        final Object[] newArray = (Object[])ArrayReflection.newInstance(values.getClass().getComponentType(), newSize);
        if (head < tail) {
            System.arraycopy(values, head, newArray, 0, tail - head);
        }
        else if (this.size > 0) {
            final int rest = values.length - head;
            System.arraycopy(values, head, newArray, 0, rest);
            System.arraycopy(values, 0, newArray, rest, tail);
        }
        this.values = (T[])newArray;
        this.head = 0;
        this.tail = this.size;
    }
    
    public T removeFirst() {
        if (this.size == 0) {
            throw new NoSuchElementException("Queue is empty.");
        }
        final Object[] values = this.values;
        final T result = (T)values[this.head];
        values[this.head] = null;
        ++this.head;
        if (this.head == values.length) {
            this.head = 0;
        }
        --this.size;
        return result;
    }
    
    public T removeLast() {
        if (this.size == 0) {
            throw new NoSuchElementException("Queue is empty.");
        }
        final Object[] values = this.values;
        int tail = this.tail;
        if (--tail == -1) {
            tail = values.length - 1;
        }
        final T result = (T)values[tail];
        values[tail] = null;
        this.tail = tail;
        --this.size;
        return result;
    }
    
    public int indexOf(final T value, final boolean identity) {
        if (this.size == 0) {
            return -1;
        }
        final Object[] values = this.values;
        final int head = this.head;
        final int tail = this.tail;
        if (identity || value == null) {
            if (head < tail) {
                for (int i = head; i < tail; ++i) {
                    if (values[i] == value) {
                        return i;
                    }
                }
            }
            else {
                for (int i = head, n = values.length; i < n; ++i) {
                    if (values[i] == value) {
                        return i - head;
                    }
                }
                for (int i = 0; i < tail; ++i) {
                    if (values[i] == value) {
                        return i + values.length - head;
                    }
                }
            }
        }
        else if (head < tail) {
            for (int i = head; i < tail; ++i) {
                if (value.equals(values[i])) {
                    return i;
                }
            }
        }
        else {
            for (int i = head, n = values.length; i < n; ++i) {
                if (value.equals(values[i])) {
                    return i - head;
                }
            }
            for (int i = 0; i < tail; ++i) {
                if (value.equals(values[i])) {
                    return i + values.length - head;
                }
            }
        }
        return -1;
    }
    
    public boolean removeValue(final T value, final boolean identity) {
        final int index = this.indexOf(value, identity);
        if (index == -1) {
            return false;
        }
        this.removeIndex(index);
        return true;
    }
    
    public T removeIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("index can't be < 0: " + index);
        }
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
        final Object[] values = this.values;
        final int head = this.head;
        final int tail = this.tail;
        index += head;
        T value;
        if (head < tail) {
            value = (T)values[index];
            System.arraycopy(values, index + 1, values, index, tail - index);
            values[tail] = null;
            --this.tail;
        }
        else if (index >= values.length) {
            index -= values.length;
            value = (T)values[index];
            System.arraycopy(values, index + 1, values, index, tail - index);
            --this.tail;
        }
        else {
            value = (T)values[index];
            System.arraycopy(values, head, values, head + 1, index - head);
            values[head] = null;
            ++this.head;
            if (this.head == values.length) {
                this.head = 0;
            }
        }
        --this.size;
        return value;
    }
    
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    public T first() {
        if (this.size == 0) {
            throw new NoSuchElementException("Queue is empty.");
        }
        return this.values[this.head];
    }
    
    public T last() {
        if (this.size == 0) {
            throw new NoSuchElementException("Queue is empty.");
        }
        final Object[] values = this.values;
        int tail = this.tail;
        if (--tail == -1) {
            tail = values.length - 1;
        }
        return (T)values[tail];
    }
    
    public T get(final int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("index can't be < 0: " + index);
        }
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + this.size);
        }
        final Object[] values = this.values;
        int i = this.head + index;
        if (i >= values.length) {
            i -= values.length;
        }
        return (T)values[i];
    }
    
    public void clear() {
        if (this.size == 0) {
            return;
        }
        final Object[] values = this.values;
        final int head = this.head;
        final int tail = this.tail;
        if (head < tail) {
            for (int i = head; i < tail; ++i) {
                values[i] = null;
            }
        }
        else {
            for (int i = head; i < values.length; ++i) {
                values[i] = null;
            }
            for (int i = 0; i < tail; ++i) {
                values[i] = null;
            }
        }
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }
    
    @Override
    public Iterator<T> iterator() {
        if (this.iterable == null) {
            this.iterable = new QueueIterable((Queue<T>)this);
        }
        return this.iterable.iterator();
    }
    
    @Override
    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        final Object[] values = this.values;
        final int head = this.head;
        final int tail = this.tail;
        final com.badlogic.gdx.utils.StringBuilder sb = new com.badlogic.gdx.utils.StringBuilder(64);
        sb.append('[');
        sb.append(values[head]);
        for (int i = (head + 1) % values.length; i != tail; i = (i + 1) % values.length) {
            sb.append(", ").append(values[i]);
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        final int size = this.size;
        final Object[] values = this.values;
        final int backingLength = values.length;
        int index = this.head;
        int hash = size + 1;
        for (int s = 0; s < size; ++s) {
            final T value = (T)values[index];
            hash *= 31;
            if (value != null) {
                hash += value.hashCode();
            }
            if (++index == backingLength) {
                index = 0;
            }
        }
        return hash;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Queue)) {
            return false;
        }
        final Queue<?> q = (Queue<?>)o;
        final int size = this.size;
        if (q.size != size) {
            return false;
        }
        final Object[] myValues = this.values;
        final int myBackingLength = myValues.length;
        final Object[] itsValues = (Object[])q.values;
        final int itsBackingLength = itsValues.length;
        int myIndex = this.head;
        int itsIndex = q.head;
        for (int s = 0; s < size; ++s) {
            final T myValue = (T)myValues[myIndex];
            final Object itsValue = itsValues[itsIndex];
            if (myValue == null) {
                if (itsValue != null) {
                    return false;
                }
            }
            else if (!myValue.equals(itsValue)) {
                return false;
            }
            ++myIndex;
            ++itsIndex;
            if (myIndex == myBackingLength) {
                myIndex = 0;
            }
            if (itsIndex == itsBackingLength) {
                itsIndex = 0;
            }
        }
        return true;
    }
    
    public static class QueueIterator<T> implements Iterator<T>, Iterable<T>
    {
        private final Queue<T> queue;
        private final boolean allowRemove;
        int index;
        boolean valid;
        
        public QueueIterator(final Queue<T> queue) {
            this(queue, true);
        }
        
        public QueueIterator(final Queue<T> queue, final boolean allowRemove) {
            this.valid = true;
            this.queue = queue;
            this.allowRemove = allowRemove;
        }
        
        @Override
        public boolean hasNext() {
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.index < this.queue.size;
        }
        
        @Override
        public T next() {
            if (this.index >= this.queue.size) {
                throw new NoSuchElementException(String.valueOf(this.index));
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.queue.get(this.index++);
        }
        
        @Override
        public void remove() {
            if (!this.allowRemove) {
                throw new GdxRuntimeException("Remove not allowed.");
            }
            --this.index;
            this.queue.removeIndex(this.index);
        }
        
        public void reset() {
            this.index = 0;
        }
        
        @Override
        public Iterator<T> iterator() {
            return this;
        }
    }
    
    public static class QueueIterable<T> implements Iterable<T>
    {
        private final Queue<T> queue;
        private final boolean allowRemove;
        private QueueIterator iterator1;
        private QueueIterator iterator2;
        
        public QueueIterable(final Queue<T> queue) {
            this(queue, true);
        }
        
        public QueueIterable(final Queue<T> queue, final boolean allowRemove) {
            this.queue = queue;
            this.allowRemove = allowRemove;
        }
        
        @Override
        public Iterator<T> iterator() {
            if (this.iterator1 == null) {
                this.iterator1 = new QueueIterator((Queue<T>)this.queue, this.allowRemove);
                this.iterator2 = new QueueIterator((Queue<T>)this.queue, this.allowRemove);
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
