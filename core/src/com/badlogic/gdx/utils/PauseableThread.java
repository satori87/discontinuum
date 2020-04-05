// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

public class PauseableThread extends Thread
{
    final Runnable runnable;
    boolean paused;
    boolean exit;
    
    public PauseableThread(final Runnable runnable) {
        this.paused = false;
        this.exit = false;
        this.runnable = runnable;
    }
    
    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                try {
                    while (this.paused) {
                        this.wait();
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (this.exit) {
                break;
            }
            this.runnable.run();
        }
    }
    
    public void onPause() {
        this.paused = true;
    }
    
    public void onResume() {
        synchronized (this) {
            this.paused = false;
            this.notifyAll();
        }
    }
    
    public boolean isPaused() {
        return this.paused;
    }
    
    public void stopThread() {
        this.exit = true;
        if (this.paused) {
            this.onResume();
        }
    }
}
