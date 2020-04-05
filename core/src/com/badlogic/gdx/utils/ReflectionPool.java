// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;

public class ReflectionPool<T> extends Pool<T>
{
    private final Constructor constructor;
    
    public ReflectionPool(final Class<T> type) {
        this(type, 16, Integer.MAX_VALUE);
    }
    
    public ReflectionPool(final Class<T> type, final int initialCapacity) {
        this(type, initialCapacity, Integer.MAX_VALUE);
    }
    
    public ReflectionPool(final Class<T> type, final int initialCapacity, final int max) {
        super(initialCapacity, max);
        this.constructor = this.findConstructor(type);
        if (this.constructor == null) {
            throw new RuntimeException("Class cannot be created (missing no-arg constructor): " + type.getName());
        }
    }
    
    private Constructor findConstructor(final Class<T> type) {
        try {
            return ClassReflection.getConstructor(type, (Class[])null);
        }
        catch (Exception ex1) {
            try {
                final Constructor constructor = ClassReflection.getDeclaredConstructor(type, (Class[])null);
                constructor.setAccessible(true);
                return constructor;
            }
            catch (ReflectionException ex2) {
                return null;
            }
        }
    }
    
    @Override
    protected T newObject() {
        try {
            return (T)this.constructor.newInstance((Object[])null);
        }
        catch (Exception ex) {
            throw new GdxRuntimeException("Unable to create new instance: " + this.constructor.getDeclaringClass().getName(), ex);
        }
    }
}
