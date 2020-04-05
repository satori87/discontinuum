// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

public class SerializationException extends RuntimeException
{
    private StringBuilder trace;
    
    public SerializationException() {
    }
    
    public SerializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public SerializationException(final String message) {
        super(message);
    }
    
    public SerializationException(final Throwable cause) {
        super("", cause);
    }
    
    public boolean causedBy(final Class type) {
        return this.causedBy(this, type);
    }
    
    private boolean causedBy(final Throwable ex, final Class type) {
        final Throwable cause = ex.getCause();
        return cause != null && cause != ex && (type.isAssignableFrom(cause.getClass()) || this.causedBy(cause, type));
    }
    
    @Override
    public String getMessage() {
        if (this.trace == null) {
            return super.getMessage();
        }
        final StringBuilder sb = new StringBuilder(512);
        sb.append(super.getMessage());
        if (sb.length() > 0) {
            sb.append('\n');
        }
        sb.append("Serialization trace:");
        sb.append(this.trace);
        return sb.toString();
    }
    
    public void addTrace(final String info) {
        if (info == null) {
            throw new IllegalArgumentException("info cannot be null.");
        }
        if (this.trace == null) {
            this.trace = new StringBuilder(512);
        }
        this.trace.append('\n');
        this.trace.append(info);
    }
}
