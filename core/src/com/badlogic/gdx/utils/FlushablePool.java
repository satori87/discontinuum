// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

public abstract class FlushablePool<T> extends Pool<T>
{
    protected Array<T> obtained;
    
    public FlushablePool() {
        this.obtained = new Array<T>();
    }
    
    public FlushablePool(final int initialCapacity) {
        super(initialCapacity);
        this.obtained = new Array<T>();
    }
    
    public FlushablePool(final int initialCapacity, final int max) {
        super(initialCapacity, max);
        this.obtained = new Array<T>();
    }
    
    @Override
    public T obtain() {
        final T result = super.obtain();
        this.obtained.add(result);
        return result;
    }
    
    public void flush() {
        super.freeAll(this.obtained);
        this.obtained.clear();
    }
    
    @Override
    public void free(final T object) {
        this.obtained.removeValue(object, true);
        super.free(object);
    }
    
    @Override
    public void freeAll(final Array<T> objects) {
        this.obtained.removeAll((Array<? extends T>)objects, true);
        super.freeAll(objects);
    }
}
