// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.renderers;

import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;

public class ParticleControllerControllerRenderer extends ParticleControllerRenderer
{
    ParallelArray.ObjectChannel<ParticleController> controllerChannel;
    
    @Override
    public void init() {
        this.controllerChannel = this.controller.particles.getChannel(ParticleChannels.ParticleController);
        if (this.controllerChannel == null) {
            throw new GdxRuntimeException("ParticleController channel not found, specify an influencer which will allocate it please.");
        }
    }
    
    @Override
    public void update() {
        for (int i = 0, c = this.controller.particles.size; i < c; ++i) {
            this.controllerChannel.data[i].draw();
        }
    }
    
    @Override
    public ParticleControllerComponent copy() {
        return new ParticleControllerControllerRenderer();
    }
    
    @Override
    public boolean isCompatible(final ParticleBatch batch) {
        return false;
    }
}
