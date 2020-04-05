// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets;

public class RefCountedContainer
{
    Object object;
    int refCount;
    
    public RefCountedContainer(final Object object) {
        this.refCount = 1;
        if (object == null) {
            throw new IllegalArgumentException("Object must not be null");
        }
        this.object = object;
    }
    
    public void incRefCount() {
        ++this.refCount;
    }
    
    public void decRefCount() {
        --this.refCount;
    }
    
    public int getRefCount() {
        return this.refCount;
    }
    
    public void setRefCount(final int refCount) {
        this.refCount = refCount;
    }
    
    public <T> T getObject(final Class<T> type) {
        return (T)this.object;
    }
    
    public void setObject(final Object asset) {
        this.object = asset;
    }
}
