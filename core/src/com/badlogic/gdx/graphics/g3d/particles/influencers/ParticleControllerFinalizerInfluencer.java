// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.influencers;

import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;

public class ParticleControllerFinalizerInfluencer extends Influencer
{
    ParallelArray.FloatChannel positionChannel;
    ParallelArray.FloatChannel scaleChannel;
    ParallelArray.FloatChannel rotationChannel;
    ParallelArray.ObjectChannel<ParticleController> controllerChannel;
    boolean hasScale;
    boolean hasRotation;
    
    @Override
    public void init() {
        this.controllerChannel = this.controller.particles.getChannel(ParticleChannels.ParticleController);
        if (this.controllerChannel == null) {
            throw new GdxRuntimeException("ParticleController channel not found, specify an influencer which will allocate it please.");
        }
        this.scaleChannel = this.controller.particles.getChannel(ParticleChannels.Scale);
        this.rotationChannel = this.controller.particles.getChannel(ParticleChannels.Rotation3D);
        this.hasScale = (this.scaleChannel != null);
        this.hasRotation = (this.rotationChannel != null);
    }
    
    @Override
    public void allocateChannels() {
        this.positionChannel = this.controller.particles.addChannel(ParticleChannels.Position);
    }
    
    @Override
    public void update() {
        for (int i = 0, positionOffset = 0, c = this.controller.particles.size; i < c; ++i, positionOffset += this.positionChannel.strideSize) {
            final ParticleController particleController = this.controllerChannel.data[i];
            final float scale = this.hasScale ? this.scaleChannel.data[i] : 1.0f;
            float qx = 0.0f;
            float qy = 0.0f;
            float qz = 0.0f;
            float qw = 1.0f;
            if (this.hasRotation) {
                final int rotationOffset = i * this.rotationChannel.strideSize;
                qx = this.rotationChannel.data[rotationOffset + 0];
                qy = this.rotationChannel.data[rotationOffset + 1];
                qz = this.rotationChannel.data[rotationOffset + 2];
                qw = this.rotationChannel.data[rotationOffset + 3];
            }
            particleController.setTransform(this.positionChannel.data[positionOffset + 0], this.positionChannel.data[positionOffset + 1], this.positionChannel.data[positionOffset + 2], qx, qy, qz, qw, scale);
            particleController.update();
        }
    }
    
    @Override
    public ParticleControllerFinalizerInfluencer copy() {
        return new ParticleControllerFinalizerInfluencer();
    }
}
