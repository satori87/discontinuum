// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.async;

import java.util.concurrent.ExecutionException;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.concurrent.Future;

public class AsyncResult<T>
{
    private final Future<T> future;
    
    AsyncResult(final Future<T> future) {
        this.future = future;
    }
    
    public boolean isDone() {
        return this.future.isDone();
    }
    
    public T get() {
        try {
            return this.future.get();
        }
        catch (InterruptedException ex2) {
            return null;
        }
        catch (ExecutionException ex) {
            throw new GdxRuntimeException(ex.getCause());
        }
    }
}
