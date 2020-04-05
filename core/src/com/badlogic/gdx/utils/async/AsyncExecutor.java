// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.async;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ExecutorService;
import com.badlogic.gdx.utils.Disposable;

public class AsyncExecutor implements Disposable
{
    private final ExecutorService executor;
    
    public AsyncExecutor(final int maxConcurrent) {
        this.executor = Executors.newFixedThreadPool(maxConcurrent, new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                final Thread thread = new Thread(r, "AsynchExecutor-Thread");
                thread.setDaemon(true);
                return thread;
            }
        });
    }
    
    public <T> AsyncResult<T> submit(final AsyncTask<T> task) {
        if (this.executor.isShutdown()) {
            throw new GdxRuntimeException("Cannot run tasks on an executor that has been shutdown (disposed)");
        }
        return new AsyncResult<T>(this.executor.submit((Callable<T>)new Callable<T>() {
            @Override
            public T call() throws Exception {
                return task.call();
            }
        }));
    }
    
    @Override
    public void dispose() {
        this.executor.shutdown();
        try {
            this.executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            throw new GdxRuntimeException("Couldn't shutdown loading thread", e);
        }
    }
}
