// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.profiling;

import com.badlogic.gdx.math.FloatCounter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.Graphics;

public class GLProfiler
{
    private Graphics graphics;
    private GLInterceptor glInterceptor;
    private GLErrorListener listener;
    private boolean enabled;
    
    public GLProfiler(final Graphics graphics) {
        this.enabled = false;
        this.graphics = graphics;
        final GL30 gl30 = graphics.getGL30();
        if (gl30 != null) {
            this.glInterceptor = new GL30Interceptor(this, graphics.getGL30());
        }
        else {
            this.glInterceptor = new GL20Interceptor(this, graphics.getGL20());
        }
        this.listener = GLErrorListener.LOGGING_LISTENER;
    }
    
    public void enable() {
        if (this.enabled) {
            return;
        }
        final GL30 gl30 = this.graphics.getGL30();
        if (gl30 != null) {
            this.graphics.setGL30((GL30)this.glInterceptor);
        }
        else {
            this.graphics.setGL20(this.glInterceptor);
        }
        this.enabled = true;
    }
    
    public void disable() {
        if (!this.enabled) {
            return;
        }
        final GL30 gl30 = this.graphics.getGL30();
        if (gl30 != null) {
            this.graphics.setGL30(((GL30Interceptor)this.graphics.getGL30()).gl30);
        }
        else {
            this.graphics.setGL20(((GL20Interceptor)this.graphics.getGL20()).gl20);
        }
        this.enabled = false;
    }
    
    public void setListener(final GLErrorListener errorListener) {
        this.listener = errorListener;
    }
    
    public GLErrorListener getListener() {
        return this.listener;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public int getCalls() {
        return this.glInterceptor.getCalls();
    }
    
    public int getTextureBindings() {
        return this.glInterceptor.getTextureBindings();
    }
    
    public int getDrawCalls() {
        return this.glInterceptor.getDrawCalls();
    }
    
    public int getShaderSwitches() {
        return this.glInterceptor.getShaderSwitches();
    }
    
    public FloatCounter getVertexCount() {
        return this.glInterceptor.getVertexCount();
    }
    
    public void reset() {
        this.glInterceptor.reset();
    }
}
