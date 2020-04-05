// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.profiling;

import com.badlogic.gdx.math.FloatCounter;
import com.badlogic.gdx.graphics.GL20;

public abstract class GLInterceptor implements GL20
{
    protected int calls;
    protected int textureBindings;
    protected int drawCalls;
    protected int shaderSwitches;
    protected final FloatCounter vertexCount;
    protected GLProfiler glProfiler;
    
    protected GLInterceptor(final GLProfiler profiler) {
        this.vertexCount = new FloatCounter(0);
        this.glProfiler = profiler;
    }
    
    public static String resolveErrorNumber(final int error) {
        switch (error) {
            case 1281: {
                return "GL_INVALID_VALUE";
            }
            case 1282: {
                return "GL_INVALID_OPERATION";
            }
            case 1286: {
                return "GL_INVALID_FRAMEBUFFER_OPERATION";
            }
            case 1280: {
                return "GL_INVALID_ENUM";
            }
            case 1285: {
                return "GL_OUT_OF_MEMORY";
            }
            default: {
                return "number " + error;
            }
        }
    }
    
    public int getCalls() {
        return this.calls;
    }
    
    public int getTextureBindings() {
        return this.textureBindings;
    }
    
    public int getDrawCalls() {
        return this.drawCalls;
    }
    
    public int getShaderSwitches() {
        return this.shaderSwitches;
    }
    
    public FloatCounter getVertexCount() {
        return this.vertexCount;
    }
    
    public void reset() {
        this.calls = 0;
        this.textureBindings = 0;
        this.drawCalls = 0;
        this.shaderSwitches = 0;
        this.vertexCount.reset();
    }
}
