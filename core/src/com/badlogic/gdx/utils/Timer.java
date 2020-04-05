// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class Timer
{
    static final Object threadLock;
    static TimerThread thread;
    final Array<Task> tasks;
    
    static {
        threadLock = new Object();
    }
    
    public static Timer instance() {
        synchronized (Timer.threadLock) {
            final TimerThread thread = thread();
            if (thread.instance == null) {
                thread.instance = new Timer();
            }
            // monitorexit(Timer.threadLock)
            return thread.instance;
        }
    }
    
    private static TimerThread thread() {
        synchronized (Timer.threadLock) {
            if (Timer.thread == null || Timer.thread.files != Gdx.files) {
                if (Timer.thread != null) {
                    Timer.thread.dispose();
                }
                Timer.thread = new TimerThread();
            }
            // monitorexit(Timer.threadLock)
            return Timer.thread;
        }
    }
    
    public Timer() {
        this.tasks = new Array<Task>(false, 8);
        this.start();
    }
    
    public Task postTask(final Task task) {
        return this.scheduleTask(task, 0.0f, 0.0f, 0);
    }
    
    public Task scheduleTask(final Task task, final float delaySeconds) {
        return this.scheduleTask(task, delaySeconds, 0.0f, 0);
    }
    
    public Task scheduleTask(final Task task, final float delaySeconds, final float intervalSeconds) {
        return this.scheduleTask(task, delaySeconds, intervalSeconds, -1);
    }
    
    public Task scheduleTask(final Task task, final float delaySeconds, final float intervalSeconds, final int repeatCount) {
        synchronized (this) {
            // monitorenter(task)
            try {
                if (task.timer != null) {
                    new IllegalArgumentException("The same task may not be scheduled twice.");
                }
                task.timer = this;
                task.executeTimeMillis = System.nanoTime() / 1000000L + (long)(delaySeconds * 1000.0f);
                task.intervalMillis = (long)(intervalSeconds * 1000.0f);
                task.repeatCount = repeatCount;
                this.tasks.add(task);
            }
            // monitorexit(task)
            finally {}
        }
        synchronized (Timer.threadLock) {
            Timer.threadLock.notifyAll();
        }
        // monitorexit(Timer.threadLock)
        return task;
    }
    
    public void stop() {
        synchronized (Timer.threadLock) {
            thread().instances.removeValue(this, true);
        }
        // monitorexit(Timer.threadLock)
    }
    
    public void start() {
        synchronized (Timer.threadLock) {
            final TimerThread thread = thread();
            final Array<Timer> instances = thread.instances;
            if (instances.contains(this, true)) {
                // monitorexit(Timer.threadLock)
                return;
            }
            instances.add(this);
            Timer.threadLock.notifyAll();
        }
        // monitorexit(Timer.threadLock)
    }
    
    public synchronized void clear() {
        for (int i = 0, n = this.tasks.size; i < n; ++i) {
            final Task task = this.tasks.get(i);
            synchronized (task) {
                task.executeTimeMillis = 0L;
                task.timer = null;
            }
            // monitorexit(task)
        }
        this.tasks.clear();
    }
    
    public synchronized boolean isEmpty() {
        return this.tasks.size == 0;
    }
    
    synchronized long update(final long timeMillis, long waitMillis) {
        for (int i = 0, n = this.tasks.size; i < n; ++i) {
            final Task task = this.tasks.get(i);
            synchronized (task) {
                if (task.executeTimeMillis > timeMillis) {
                    waitMillis = Math.min(waitMillis, task.executeTimeMillis - timeMillis);
                }
                // monitorexit(task)
                else {
                    if (task.repeatCount == 0) {
                        task.timer = null;
                        this.tasks.removeIndex(i);
                        --i;
                        --n;
                    }
                    else {
                        task.executeTimeMillis = timeMillis + task.intervalMillis;
                        waitMillis = Math.min(waitMillis, task.intervalMillis);
                        if (task.repeatCount > 0) {
                            final Task task2 = task;
                            --task2.repeatCount;
                        }
                    }
                    task.app.postRunnable(task);
                }
                // monitorexit(task)
            }
        }
        return waitMillis;
    }
    
    public synchronized void delay(final long delayMillis) {
        for (int i = 0, n = this.tasks.size; i < n; ++i) {
            final Task task = this.tasks.get(i);
            synchronized (task) {
                final Task task2 = task;
                task2.executeTimeMillis += delayMillis;
            }
            // monitorexit(task)
        }
    }
    
    public static Task post(final Task task) {
        return instance().postTask(task);
    }
    
    public static Task schedule(final Task task, final float delaySeconds) {
        return instance().scheduleTask(task, delaySeconds);
    }
    
    public static Task schedule(final Task task, final float delaySeconds, final float intervalSeconds) {
        return instance().scheduleTask(task, delaySeconds, intervalSeconds);
    }
    
    public static Task schedule(final Task task, final float delaySeconds, final float intervalSeconds, final int repeatCount) {
        return instance().scheduleTask(task, delaySeconds, intervalSeconds, repeatCount);
    }
    
    public abstract static class Task implements Runnable
    {
        final Application app;
        long executeTimeMillis;
        long intervalMillis;
        int repeatCount;
        volatile Timer timer;
        
        public Task() {
            this.app = Gdx.app;
            if (this.app == null) {
                throw new IllegalStateException("Gdx.app not available.");
            }
        }
        
        @Override
        public abstract void run();
        
        public void cancel() {
            final Timer timer = this.timer;
            if (timer != null) {
                synchronized (timer) {
                    // monitorenter(this)
                    try {
                        this.executeTimeMillis = 0L;
                        this.timer = null;
                        timer.tasks.removeValue(this, true);
                    }
                    // monitorexit(this)
                    finally {}
                    // monitorexit(timer)
                    return;
                }
            }
            synchronized (this) {
                this.executeTimeMillis = 0L;
                this.timer = null;
            }
        }
        
        public boolean isScheduled() {
            return this.timer != null;
        }
        
        public synchronized long getExecuteTimeMillis() {
            return this.executeTimeMillis;
        }
    }
    
    static class TimerThread implements Runnable, LifecycleListener
    {
        final Files files;
        final Array<Timer> instances;
        Timer instance;
        private long pauseMillis;
        
        public TimerThread() {
            this.instances = new Array<Timer>(1);
            this.files = Gdx.files;
            Gdx.app.addLifecycleListener(this);
            this.resume();
            final Thread thread = new Thread(this, "Timer");
            thread.setDaemon(true);
            thread.start();
        }
        
        @Override
        public void run() {
            while (true) {
                synchronized (Timer.threadLock) {
                    if (Timer.thread == this && this.files == Gdx.files) {
                        long waitMillis = 5000L;
                        if (this.pauseMillis == 0L) {
                            final long timeMillis = System.nanoTime() / 1000000L;
                            for (int i = 0, n = this.instances.size; i < n; ++i) {
                                try {
                                    waitMillis = this.instances.get(i).update(timeMillis, waitMillis);
                                }
                                catch (Throwable ex) {
                                    throw new GdxRuntimeException("Task failed: " + this.instances.get(i).getClass().getName(), ex);
                                }
                            }
                        }
                        if (Timer.thread == this && this.files == Gdx.files) {
                            try {
                                if (waitMillis > 0L) {
                                    Timer.threadLock.wait(waitMillis);
                                }
                            }
                            catch (InterruptedException ex2) {}
                            // monitorexit(Timer.threadLock)
                            continue;
                        }
                    }
                    // monitorexit(Timer.threadLock)
                }
                break;
            }
            this.dispose();
        }
        
        @Override
        public void resume() {
            synchronized (Timer.threadLock) {
                final long delayMillis = System.nanoTime() / 1000000L - this.pauseMillis;
                for (int i = 0, n = this.instances.size; i < n; ++i) {
                    this.instances.get(i).delay(delayMillis);
                }
                this.pauseMillis = 0L;
                Timer.threadLock.notifyAll();
            }
            // monitorexit(Timer.threadLock)
        }
        
        @Override
        public void pause() {
            synchronized (Timer.threadLock) {
                this.pauseMillis = System.nanoTime() / 1000000L;
                Timer.threadLock.notifyAll();
            }
            // monitorexit(Timer.threadLock)
        }
        
        @Override
        public void dispose() {
            synchronized (Timer.threadLock) {
                if (Timer.thread == this) {
                    Timer.thread = null;
                }
                this.instances.clear();
                Timer.threadLock.notifyAll();
            }
            // monitorexit(Timer.threadLock)
            Gdx.app.removeLifecycleListener(this);
        }
    }
}
