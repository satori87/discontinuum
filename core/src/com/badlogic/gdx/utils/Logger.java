// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import com.badlogic.gdx.Gdx;

public class Logger
{
    public static final int NONE = 0;
    public static final int ERROR = 1;
    public static final int INFO = 2;
    public static final int DEBUG = 3;
    private final String tag;
    private int level;
    
    public Logger(final String tag) {
        this(tag, 1);
    }
    
    public Logger(final String tag, final int level) {
        this.tag = tag;
        this.level = level;
    }
    
    public void debug(final String message) {
        if (this.level >= 3) {
            Gdx.app.debug(this.tag, message);
        }
    }
    
    public void debug(final String message, final Exception exception) {
        if (this.level >= 3) {
            Gdx.app.debug(this.tag, message, exception);
        }
    }
    
    public void info(final String message) {
        if (this.level >= 2) {
            Gdx.app.log(this.tag, message);
        }
    }
    
    public void info(final String message, final Exception exception) {
        if (this.level >= 2) {
            Gdx.app.log(this.tag, message, exception);
        }
    }
    
    public void error(final String message) {
        if (this.level >= 1) {
            Gdx.app.error(this.tag, message);
        }
    }
    
    public void error(final String message, final Throwable exception) {
        if (this.level >= 1) {
            Gdx.app.error(this.tag, message, exception);
        }
    }
    
    public void setLevel(final int level) {
        this.level = level;
    }
    
    public int getLevel() {
        return this.level;
    }
}
