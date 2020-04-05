// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.renderers;

import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;

public abstract class ParticleControllerRenderer<D extends ParticleControllerRenderData, T extends ParticleBatch<D>> extends ParticleControllerComponent
{
    protected T batch;
    protected D renderData;
    
    protected ParticleControllerRenderer() {
    }
    
    protected ParticleControllerRenderer(final D renderData) {
        this.renderData = renderData;
    }
    
    @Override
    public void update() {
        this.batch.draw(this.renderData);
    }
    
    public boolean setBatch(final ParticleBatch<?> batch) {
        if (this.isCompatible(batch)) {
            this.batch = (T)batch;
            return true;
        }
        return false;
    }
    
    public abstract boolean isCompatible(final ParticleBatch<?> p0);
    
    @Override
    public void set(final ParticleController particleController) {
        super.set(particleController);
        if (this.renderData != null) {
            this.renderData.controller = this.controller;
        }
    }
}
