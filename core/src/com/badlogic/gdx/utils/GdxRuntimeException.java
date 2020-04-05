// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

public class GdxRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 6735854402467673117L;
    
    public GdxRuntimeException(final String message) {
        super(message);
    }
    
    public GdxRuntimeException(final Throwable t) {
        super(t);
    }
    
    public GdxRuntimeException(final String message, final Throwable t) {
        super(message, t);
    }
}
