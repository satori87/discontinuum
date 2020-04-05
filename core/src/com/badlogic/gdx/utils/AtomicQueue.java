// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicQueue<T>
{
    private final AtomicInteger writeIndex;
    private final AtomicInteger readIndex;
    private final AtomicReferenceArray<T> queue;
    
    public AtomicQueue(final int capacity) {
        this.writeIndex = new AtomicInteger();
        this.readIndex = new AtomicInteger();
        this.queue = new AtomicReferenceArray<T>(capacity);
    }
    
    private int next(final int idx) {
        return (idx + 1) % this.queue.length();
    }
    
    public boolean put(final T value) {
        final int write = this.writeIndex.get();
        final int read = this.readIndex.get();
        final int next = this.next(write);
        if (next == read) {
            return false;
        }
        this.queue.set(write, value);
        this.writeIndex.set(next);
        return true;
    }
    
    public T poll() {
        final int read = this.readIndex.get();
        final int write = this.writeIndex.get();
        if (read == write) {
            return null;
        }
        final T value = this.queue.get(read);
        this.readIndex.set(this.next(read));
        return value;
    }
}
