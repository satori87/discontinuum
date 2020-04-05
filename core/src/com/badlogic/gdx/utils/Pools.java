// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

public class Pools
{
    private static final ObjectMap<Class, Pool> typePools;
    
    static {
        typePools = new ObjectMap<Class, Pool>();
    }
    
    public static <T> Pool<T> get(final Class<T> type, final int max) {
        Pool pool = Pools.typePools.get(type);
        if (pool == null) {
            pool = new ReflectionPool(type, 4, max);
            Pools.typePools.put(type, pool);
        }
        return (Pool<T>)pool;
    }
    
    public static <T> Pool<T> get(final Class<T> type) {
        return get(type, 100);
    }
    
    public static <T> void set(final Class<T> type, final Pool<T> pool) {
        Pools.typePools.put(type, pool);
    }
    
    public static <T> T obtain(final Class<T> type) {
        return get(type).obtain();
    }
    
    public static void free(final Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null.");
        }
        final Pool pool = Pools.typePools.get(object.getClass());
        if (pool == null) {
            return;
        }
        pool.free(object);
    }
    
    public static void freeAll(final Array objects) {
        freeAll(objects, false);
    }
    
    public static void freeAll(final Array objects, final boolean samePool) {
        if (objects == null) {
            throw new IllegalArgumentException("Objects cannot be null.");
        }
        Pool pool = null;
        for (int i = 0, n = objects.size; i < n; ++i) {
            final Object object = objects.get(i);
            if (object != null) {
                if (pool == null) {
                    pool = Pools.typePools.get(object.getClass());
                    if (pool == null) {
                        continue;
                    }
                }
                pool.free(object);
                if (!samePool) {
                    pool = null;
                }
            }
        }
    }
    
    private Pools() {
    }
}
