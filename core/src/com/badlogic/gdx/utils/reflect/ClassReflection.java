// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

public final class ClassReflection
{
    public static Class forName(final String name) throws ReflectionException {
        try {
            return Class.forName(name);
        }
        catch (ClassNotFoundException e) {
            throw new ReflectionException("Class not found: " + name, e);
        }
    }
    
    public static String getSimpleName(final Class c) {
        return c.getSimpleName();
    }
    
    public static boolean isInstance(final Class c, final Object obj) {
        return c.isInstance(obj);
    }
    
    public static boolean isAssignableFrom(final Class c1, final Class c2) {
        return c1.isAssignableFrom(c2);
    }
    
    public static boolean isMemberClass(final Class c) {
        return c.isMemberClass();
    }
    
    public static boolean isStaticClass(final Class c) {
        return Modifier.isStatic(c.getModifiers());
    }
    
    public static boolean isArray(final Class c) {
        return c.isArray();
    }
    
    public static boolean isPrimitive(final Class c) {
        return c.isPrimitive();
    }
    
    public static boolean isEnum(final Class c) {
        return c.isEnum();
    }
    
    public static boolean isAnnotation(final Class c) {
        return c.isAnnotation();
    }
    
    public static boolean isInterface(final Class c) {
        return c.isInterface();
    }
    
    public static boolean isAbstract(final Class c) {
        return Modifier.isAbstract(c.getModifiers());
    }
    
    public static <T> T newInstance(final Class<T> c) throws ReflectionException {
        try {
            return c.newInstance();
        }
        catch (InstantiationException e) {
            throw new ReflectionException("Could not instantiate instance of class: " + c.getName(), e);
        }
        catch (IllegalAccessException e2) {
            throw new ReflectionException("Could not instantiate instance of class: " + c.getName(), e2);
        }
    }
    
    public static Class getComponentType(final Class c) {
        return c.getComponentType();
    }
    
    public static Constructor[] getConstructors(final Class c) {
        final java.lang.reflect.Constructor[] constructors = c.getConstructors();
        final Constructor[] result = new Constructor[constructors.length];
        for (int i = 0, j = constructors.length; i < j; ++i) {
            result[i] = new Constructor(constructors[i]);
        }
        return result;
    }
    
    public static Constructor getConstructor(final Class c, final Class... parameterTypes) throws ReflectionException {
        try {
            return new Constructor(c.getConstructor((Class[])parameterTypes));
        }
        catch (SecurityException e) {
            throw new ReflectionException("Security violation occurred while getting constructor for class: '" + c.getName() + "'.", e);
        }
        catch (NoSuchMethodException e2) {
            throw new ReflectionException("Constructor not found for class: " + c.getName(), e2);
        }
    }
    
    public static Constructor getDeclaredConstructor(final Class c, final Class... parameterTypes) throws ReflectionException {
        try {
            return new Constructor(c.getDeclaredConstructor((Class[])parameterTypes));
        }
        catch (SecurityException e) {
            throw new ReflectionException("Security violation while getting constructor for class: " + c.getName(), e);
        }
        catch (NoSuchMethodException e2) {
            throw new ReflectionException("Constructor not found for class: " + c.getName(), e2);
        }
    }
    
    public static Object[] getEnumConstants(final Class c) {
        return c.getEnumConstants();
    }
    
    public static Method[] getMethods(final Class c) {
        final java.lang.reflect.Method[] methods = c.getMethods();
        final Method[] result = new Method[methods.length];
        for (int i = 0, j = methods.length; i < j; ++i) {
            result[i] = new Method(methods[i]);
        }
        return result;
    }
    
    public static Method getMethod(final Class c, final String name, final Class... parameterTypes) throws ReflectionException {
        try {
            return new Method(c.getMethod(name, (Class[])parameterTypes));
        }
        catch (SecurityException e) {
            throw new ReflectionException("Security violation while getting method: " + name + ", for class: " + c.getName(), e);
        }
        catch (NoSuchMethodException e2) {
            throw new ReflectionException("Method not found: " + name + ", for class: " + c.getName(), e2);
        }
    }
    
    public static Method[] getDeclaredMethods(final Class c) {
        final java.lang.reflect.Method[] methods = c.getDeclaredMethods();
        final Method[] result = new Method[methods.length];
        for (int i = 0, j = methods.length; i < j; ++i) {
            result[i] = new Method(methods[i]);
        }
        return result;
    }
    
    public static Method getDeclaredMethod(final Class c, final String name, final Class... parameterTypes) throws ReflectionException {
        try {
            return new Method(c.getDeclaredMethod(name, (Class[])parameterTypes));
        }
        catch (SecurityException e) {
            throw new ReflectionException("Security violation while getting method: " + name + ", for class: " + c.getName(), e);
        }
        catch (NoSuchMethodException e2) {
            throw new ReflectionException("Method not found: " + name + ", for class: " + c.getName(), e2);
        }
    }
    
    public static Field[] getFields(final Class c) {
        final java.lang.reflect.Field[] fields = c.getFields();
        final Field[] result = new Field[fields.length];
        for (int i = 0, j = fields.length; i < j; ++i) {
            result[i] = new Field(fields[i]);
        }
        return result;
    }
    
    public static Field getField(final Class c, final String name) throws ReflectionException {
        try {
            return new Field(c.getField(name));
        }
        catch (SecurityException e) {
            throw new ReflectionException("Security violation while getting field: " + name + ", for class: " + c.getName(), e);
        }
        catch (NoSuchFieldException e2) {
            throw new ReflectionException("Field not found: " + name + ", for class: " + c.getName(), e2);
        }
    }
    
    public static Field[] getDeclaredFields(final Class c) {
        final java.lang.reflect.Field[] fields = c.getDeclaredFields();
        final Field[] result = new Field[fields.length];
        for (int i = 0, j = fields.length; i < j; ++i) {
            result[i] = new Field(fields[i]);
        }
        return result;
    }
    
    public static Field getDeclaredField(final Class c, final String name) throws ReflectionException {
        try {
            return new Field(c.getDeclaredField(name));
        }
        catch (SecurityException e) {
            throw new ReflectionException("Security violation while getting field: " + name + ", for class: " + c.getName(), e);
        }
        catch (NoSuchFieldException e2) {
            throw new ReflectionException("Field not found: " + name + ", for class: " + c.getName(), e2);
        }
    }
    
    public static boolean isAnnotationPresent(final Class c, final Class<? extends Annotation> annotationType) {
        return c.isAnnotationPresent(annotationType);
    }
    
    public static com.badlogic.gdx.utils.reflect.Annotation[] getAnnotations(final Class c) {
        final Annotation[] annotations = c.getAnnotations();
        final com.badlogic.gdx.utils.reflect.Annotation[] result = new com.badlogic.gdx.utils.reflect.Annotation[annotations.length];
        for (int i = 0; i < annotations.length; ++i) {
            result[i] = new com.badlogic.gdx.utils.reflect.Annotation(annotations[i]);
        }
        return result;
    }
    
    public static com.badlogic.gdx.utils.reflect.Annotation getAnnotation(final Class c, final Class<? extends Annotation> annotationType) {
        final Annotation annotation = c.getAnnotation(annotationType);
        if (annotation != null) {
            return new com.badlogic.gdx.utils.reflect.Annotation(annotation);
        }
        return null;
    }
    
    public static com.badlogic.gdx.utils.reflect.Annotation[] getDeclaredAnnotations(final Class c) {
        final Annotation[] annotations = c.getDeclaredAnnotations();
        final com.badlogic.gdx.utils.reflect.Annotation[] result = new com.badlogic.gdx.utils.reflect.Annotation[annotations.length];
        for (int i = 0; i < annotations.length; ++i) {
            result[i] = new com.badlogic.gdx.utils.reflect.Annotation(annotations[i]);
        }
        return result;
    }
    
    public static com.badlogic.gdx.utils.reflect.Annotation getDeclaredAnnotation(final Class c, final Class<? extends Annotation> annotationType) {
        final Annotation[] annotations = c.getDeclaredAnnotations();
        Annotation[] array;
        for (int length = (array = annotations).length, i = 0; i < length; ++i) {
            final Annotation annotation = array[i];
            if (annotation.annotationType().equals(annotationType)) {
                return new com.badlogic.gdx.utils.reflect.Annotation(annotation);
            }
        }
        return null;
    }
    
    public static Class[] getInterfaces(final Class c) {
        return c.getInterfaces();
    }
}
