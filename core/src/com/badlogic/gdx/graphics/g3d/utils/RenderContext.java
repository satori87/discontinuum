// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Gdx;

public class RenderContext
{
    public final TextureBinder textureBinder;
    private boolean blending;
    private int blendSFactor;
    private int blendDFactor;
    private int depthFunc;
    private float depthRangeNear;
    private float depthRangeFar;
    private boolean depthMask;
    private int cullFace;
    
    public RenderContext(final TextureBinder textures) {
        this.textureBinder = textures;
    }
    
    public void begin() {
        Gdx.gl.glDisable(2929);
        this.depthFunc = 0;
        Gdx.gl.glDepthMask(true);
        this.depthMask = true;
        Gdx.gl.glDisable(3042);
        this.blending = false;
        Gdx.gl.glDisable(2884);
        final int cullFace = 0;
        this.blendDFactor = cullFace;
        this.blendSFactor = cullFace;
        this.cullFace = cullFace;
        this.textureBinder.begin();
    }
    
    public void end() {
        if (this.depthFunc != 0) {
            Gdx.gl.glDisable(2929);
        }
        if (!this.depthMask) {
            Gdx.gl.glDepthMask(true);
        }
        if (this.blending) {
            Gdx.gl.glDisable(3042);
        }
        if (this.cullFace > 0) {
            Gdx.gl.glDisable(2884);
        }
        this.textureBinder.end();
    }
    
    public void setDepthMask(final boolean depthMask) {
        if (this.depthMask != depthMask) {
            Gdx.gl.glDepthMask(this.depthMask = depthMask);
        }
    }
    
    public void setDepthTest(final int depthFunction) {
        this.setDepthTest(depthFunction, 0.0f, 1.0f);
    }
    
    public void setDepthTest(final int depthFunction, final float depthRangeNear, final float depthRangeFar) {
        final boolean wasEnabled = this.depthFunc != 0;
        final boolean enabled = depthFunction != 0;
        if (this.depthFunc != depthFunction) {
            this.depthFunc = depthFunction;
            if (enabled) {
                Gdx.gl.glEnable(2929);
                Gdx.gl.glDepthFunc(depthFunction);
            }
            else {
                Gdx.gl.glDisable(2929);
            }
        }
        if (enabled) {
            if (!wasEnabled || this.depthFunc != depthFunction) {
                Gdx.gl.glDepthFunc(this.depthFunc = depthFunction);
            }
            if (!wasEnabled || this.depthRangeNear != depthRangeNear || this.depthRangeFar != depthRangeFar) {
                Gdx.gl.glDepthRangef(this.depthRangeNear = depthRangeNear, this.depthRangeFar = depthRangeFar);
            }
        }
    }
    
    public void setBlending(final boolean enabled, final int sFactor, final int dFactor) {
        if (enabled != this.blending) {
            this.blending = enabled;
            if (enabled) {
                Gdx.gl.glEnable(3042);
            }
            else {
                Gdx.gl.glDisable(3042);
            }
        }
        if (enabled && (this.blendSFactor != sFactor || this.blendDFactor != dFactor)) {
            Gdx.gl.glBlendFunc(sFactor, dFactor);
            this.blendSFactor = sFactor;
            this.blendDFactor = dFactor;
        }
    }
    
    public void setCullFace(final int face) {
        if (face != this.cullFace) {
            this.cullFace = face;
            if (face == 1028 || face == 1029 || face == 1032) {
                Gdx.gl.glEnable(2884);
                Gdx.gl.glCullFace(face);
            }
            else {
                Gdx.gl.glDisable(2884);
            }
        }
    }
}
