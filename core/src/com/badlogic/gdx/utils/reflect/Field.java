// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Modifier;

public final class Field
{
    private final java.lang.reflect.Field field;
    
    Field(final java.lang.reflect.Field field) {
        this.field = field;
    }
    
    public String getName() {
        return this.field.getName();
    }
    
    public Class getType() {
        return this.field.getType();
    }
    
    public Class getDeclaringClass() {
        return this.field.getDeclaringClass();
    }
    
    public boolean isAccessible() {
        return this.field.isAccessible();
    }
    
    public void setAccessible(final boolean accessible) {
        this.field.setAccessible(accessible);
    }
    
    public boolean isDefaultAccess() {
        return !this.isPrivate() && !this.isProtected() && !this.isPublic();
    }
    
    public boolean isFinal() {
        return Modifier.isFinal(this.field.getModifiers());
    }
    
    public boolean isPrivate() {
        return Modifier.isPrivate(this.field.getModifiers());
    }
    
    public boolean isProtected() {
        return Modifier.isProtected(this.field.getModifiers());
    }
    
    public boolean isPublic() {
        return Modifier.isPublic(this.field.getModifiers());
    }
    
    public boolean isStatic() {
        return Modifier.isStatic(this.field.getModifiers());
    }
    
    public boolean isTransient() {
        return Modifier.isTransient(this.field.getModifiers());
    }
    
    public boolean isVolatile() {
        return Modifier.isVolatile(this.field.getModifiers());
    }
    
    public boolean isSynthetic() {
        return this.field.isSynthetic();
    }
    
    public Class getElementType(final int index) {
        final Type genericType = this.field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            final Type[] actualTypes = ((ParameterizedType)genericType).getActualTypeArguments();
            if (actualTypes.length - 1 >= index) {
                final Type actualType = actualTypes[index];
                if (actualType instanceof Class) {
                    return (Class)actualType;
                }
                if (actualType instanceof ParameterizedType) {
                    return (Class)((ParameterizedType)actualType).getRawType();
                }
                if (actualType instanceof GenericArrayType) {
                    final Type componentType = ((GenericArrayType)actualType).getGenericComponentType();
                    if (componentType instanceof Class) {
                        return ArrayReflection.newInstance((Class)componentType, 0).getClass();
                    }
                }
            }
        }
        return null;
    }
    
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationType) {
        return this.field.isAnnotationPresent(annotationType);
    }
    
    public com.badlogic.gdx.utils.reflect.Annotation[] getDeclaredAnnotations() {
        final Annotation[] annotations = this.field.getDeclaredAnnotations();
        final com.badlogic.gdx.utils.reflect.Annotation[] result = new com.badlogic.gdx.utils.reflect.Annotation[annotations.length];
        for (int i = 0; i < annotations.length; ++i) {
            result[i] = new com.badlogic.gdx.utils.reflect.Annotation(annotations[i]);
        }
        return result;
    }
    
    public com.badlogic.gdx.utils.reflect.Annotation getDeclaredAnnotation(final Class<? extends Annotation> annotationType) {
        final Annotation[] annotations = this.field.getDeclaredAnnotations();
        if (annotations == null) {
            return null;
        }
        Annotation[] array;
        for (int length = (array = annotations).length, i = 0; i < length; ++i) {
            final Annotation annotation = array[i];
            if (annotation.annotationType().equals(annotationType)) {
                return new com.badlogic.gdx.utils.reflect.Annotation(annotation);
            }
        }
        return null;
    }
    
    public Object get(final Object obj) throws ReflectionException {
        try {
            return this.field.get(obj);
        }
        catch (IllegalArgumentException e) {
            throw new ReflectionException("Object is not an instance of " + this.getDeclaringClass(), e);
        }
        catch (IllegalAccessException e2) {
            throw new ReflectionException("Illegal access to field: " + this.getName(), e2);
        }
    }
    
    public void set(final Object obj, final Object value) throws ReflectionException {
        try {
            this.field.set(obj, value);
        }
        catch (IllegalArgumentException e) {
            throw new ReflectionException("Argument not valid for field: " + this.getName(), e);
        }
        catch (IllegalAccessException e2) {
            throw new ReflectionException("Illegal access to field: " + this.getName(), e2);
        }
    }
}
