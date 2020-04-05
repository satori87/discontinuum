// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public final class Method
{
    private final java.lang.reflect.Method method;
    
    Method(final java.lang.reflect.Method method) {
        this.method = method;
    }
    
    public String getName() {
        return this.method.getName();
    }
    
    public Class getReturnType() {
        return this.method.getReturnType();
    }
    
    public Class[] getParameterTypes() {
        return this.method.getParameterTypes();
    }
    
    public Class getDeclaringClass() {
        return this.method.getDeclaringClass();
    }
    
    public boolean isAccessible() {
        return this.method.isAccessible();
    }
    
    public void setAccessible(final boolean accessible) {
        this.method.setAccessible(accessible);
    }
    
    public boolean isAbstract() {
        return Modifier.isAbstract(this.method.getModifiers());
    }
    
    public boolean isDefaultAccess() {
        return !this.isPrivate() && !this.isProtected() && !this.isPublic();
    }
    
    public boolean isFinal() {
        return Modifier.isFinal(this.method.getModifiers());
    }
    
    public boolean isPrivate() {
        return Modifier.isPrivate(this.method.getModifiers());
    }
    
    public boolean isProtected() {
        return Modifier.isProtected(this.method.getModifiers());
    }
    
    public boolean isPublic() {
        return Modifier.isPublic(this.method.getModifiers());
    }
    
    public boolean isNative() {
        return Modifier.isNative(this.method.getModifiers());
    }
    
    public boolean isStatic() {
        return Modifier.isStatic(this.method.getModifiers());
    }
    
    public boolean isVarArgs() {
        return this.method.isVarArgs();
    }
    
    public Object invoke(final Object obj, final Object... args) throws ReflectionException {
        try {
            return this.method.invoke(obj, args);
        }
        catch (IllegalArgumentException e) {
            throw new ReflectionException("Illegal argument(s) supplied to method: " + this.getName(), e);
        }
        catch (IllegalAccessException e2) {
            throw new ReflectionException("Illegal access to method: " + this.getName(), e2);
        }
        catch (InvocationTargetException e3) {
            throw new ReflectionException("Exception occurred in method: " + this.getName(), e3);
        }
    }
    
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationType) {
        return this.method.isAnnotationPresent(annotationType);
    }
    
    public com.badlogic.gdx.utils.reflect.Annotation[] getDeclaredAnnotations() {
        final Annotation[] annotations = this.method.getDeclaredAnnotations();
        final com.badlogic.gdx.utils.reflect.Annotation[] result = new com.badlogic.gdx.utils.reflect.Annotation[annotations.length];
        for (int i = 0; i < annotations.length; ++i) {
            result[i] = new com.badlogic.gdx.utils.reflect.Annotation(annotations[i]);
        }
        return result;
    }
    
    public com.badlogic.gdx.utils.reflect.Annotation getDeclaredAnnotation(final Class<? extends Annotation> annotationType) {
        final Annotation[] annotations = this.method.getDeclaredAnnotations();
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
}
