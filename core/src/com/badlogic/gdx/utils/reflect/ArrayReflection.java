// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.reflect;

import java.lang.reflect.Array;

public final class ArrayReflection
{
    public static Object newInstance(final Class c, final int size) {
        return Array.newInstance(c, size);
    }
    
    public static int getLength(final Object array) {
        return Array.getLength(array);
    }
    
    public static Object get(final Object array, final int index) {
        return Array.get(array, index);
    }
    
    public static void set(final Object array, final int index, final Object value) {
        Array.set(array, index, value);
    }
}
