// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

public abstract class Pool<T>
{
    public final int max;
    public int peak;
    private final Array<T> freeObjects;
    
    public Pool() {
        this(16, Integer.MAX_VALUE);
    }
    
    public Pool(final int initialCapacity) {
        this(initialCapacity, Integer.MAX_VALUE);
    }
    
    public Pool(final int initialCapacity, final int max) {
        this.freeObjects = new Array<T>(false, initialCapacity);
        this.max = max;
    }
    
    protected abstract T newObject();
    
    public T obtain() {
        return (this.freeObjects.size == 0) ? this.newObject() : this.freeObjects.pop();
    }
    
    public void free(final T object) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null.");
        }
        if (this.freeObjects.size < this.max) {
            this.freeObjects.add(object);
            this.peak = Math.max(this.peak, this.freeObjects.size);
        }
        this.reset(object);
    }
    
    protected void reset(final T object) {
        if (object instanceof Poolable) {
            ((Poolable)object).reset();
        }
    }
    
    public void freeAll(final Array<T> objects) {
        if (objects == null) {
            throw new IllegalArgumentException("objects cannot be null.");
        }
        final Array<T> freeObjects = this.freeObjects;
        final int max = this.max;
        for (int i = 0; i < objects.size; ++i) {
            final T object = objects.get(i);
            if (object != null) {
                if (freeObjects.size < max) {
                    freeObjects.add(object);
                }
                this.reset(object);
            }
        }
        this.peak = Math.max(this.peak, freeObjects.size);
    }
    
    public void clear() {
        this.freeObjects.clear();
    }
    
    public int getFree() {
        return this.freeObjects.size;
    }
    
    public interface Poolable
    {
        void reset();
    }
}
