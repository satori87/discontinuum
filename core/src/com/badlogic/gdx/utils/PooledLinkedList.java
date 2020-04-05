// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

public class PooledLinkedList<T>
{
    private Item<T> head;
    private Item<T> tail;
    private Item<T> iter;
    private Item<T> curr;
    private int size;
    private final Pool<Item<T>> pool;
    
    public PooledLinkedList(final int maxPoolSize) {
        this.size = 0;
        this.pool = new Pool<Item<T>>(16, maxPoolSize) {
            @Override
            protected Item<T> newObject() {
                return new Item<T>();
            }
        };
    }
    
    public void add(final T object) {
        final Item<T> item = this.pool.obtain();
        item.payload = object;
        item.next = null;
        item.prev = null;
        if (this.head == null) {
            this.head = item;
            this.tail = item;
            ++this.size;
            return;
        }
        item.prev = this.tail;
        this.tail.next = item;
        this.tail = item;
        ++this.size;
    }
    
    public int size() {
        return this.size;
    }
    
    public void iter() {
        this.iter = this.head;
    }
    
    public void iterReverse() {
        this.iter = this.tail;
    }
    
    public T next() {
        if (this.iter == null) {
            return null;
        }
        final T payload = this.iter.payload;
        this.curr = this.iter;
        this.iter = this.iter.next;
        return payload;
    }
    
    public T previous() {
        if (this.iter == null) {
            return null;
        }
        final T payload = this.iter.payload;
        this.curr = this.iter;
        this.iter = this.iter.prev;
        return payload;
    }
    
    public void remove() {
        if (this.curr == null) {
            return;
        }
        --this.size;
        this.pool.free(this.curr);
        final Item<T> c = this.curr;
        final Item<T> n = this.curr.next;
        final Item<T> p = this.curr.prev;
        this.curr = null;
        if (this.size == 0) {
            this.head = null;
            this.tail = null;
            return;
        }
        if (c == this.head) {
            n.prev = null;
            this.head = n;
            return;
        }
        if (c == this.tail) {
            p.next = null;
            this.tail = p;
            return;
        }
        p.next = n;
        n.prev = p;
    }
    
    public void clear() {
        this.iter();
        T v = null;
        while ((v = this.next()) != null) {
            this.remove();
        }
    }
    
    static final class Item<T>
    {
        public T payload;
        public Item<T> next;
        public Item<T> prev;
    }
}
