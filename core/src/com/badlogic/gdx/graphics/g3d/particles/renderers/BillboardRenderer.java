// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.renderers;

import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;

public class BillboardRenderer extends ParticleControllerRenderer<BillboardControllerRenderData, BillboardParticleBatch>
{
    public BillboardRenderer() {
        super(new BillboardControllerRenderData());
    }
    
    public BillboardRenderer(final BillboardParticleBatch batch) {
        this();
        this.setBatch(batch);
    }
    
    @Override
    public void allocateChannels() {
        ((BillboardControllerRenderData)this.renderData).positionChannel = this.controller.particles.addChannel(ParticleChannels.Position);
        ((BillboardControllerRenderData)this.renderData).regionChannel = this.controller.particles.addChannel(ParticleChannels.TextureRegion, (ParallelArray.ChannelInitializer<ParallelArray.FloatChannel>)ParticleChannels.TextureRegionInitializer.get());
        ((BillboardControllerRenderData)this.renderData).colorChannel = this.controller.particles.addChannel(ParticleChannels.Color, (ParallelArray.ChannelInitializer<ParallelArray.FloatChannel>)ParticleChannels.ColorInitializer.get());
        ((BillboardControllerRenderData)this.renderData).scaleChannel = this.controller.particles.addChannel(ParticleChannels.Scale, (ParallelArray.ChannelInitializer<ParallelArray.FloatChannel>)ParticleChannels.ScaleInitializer.get());
        ((BillboardControllerRenderData)this.renderData).rotationChannel = this.controller.particles.addChannel(ParticleChannels.Rotation2D, (ParallelArray.ChannelInitializer<ParallelArray.FloatChannel>)ParticleChannels.Rotation2dInitializer.get());
    }
    
    @Override
    public ParticleControllerComponent copy() {
        return new BillboardRenderer((BillboardParticleBatch)this.batch);
    }
    
    @Override
    public boolean isCompatible(final ParticleBatch<?> batch) {
        return batch instanceof BillboardParticleBatch;
    }
}
