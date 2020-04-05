// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.renderers;

import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;

public class PointSpriteRenderer extends ParticleControllerRenderer<PointSpriteControllerRenderData, PointSpriteParticleBatch>
{
    public PointSpriteRenderer() {
        super(new PointSpriteControllerRenderData());
    }
    
    public PointSpriteRenderer(final PointSpriteParticleBatch batch) {
        this();
        this.setBatch(batch);
    }
    
    @Override
    public void allocateChannels() {
        ((PointSpriteControllerRenderData)this.renderData).positionChannel = this.controller.particles.addChannel(ParticleChannels.Position);
        ((PointSpriteControllerRenderData)this.renderData).regionChannel = this.controller.particles.addChannel(ParticleChannels.TextureRegion, (ParallelArray.ChannelInitializer<ParallelArray.FloatChannel>)ParticleChannels.TextureRegionInitializer.get());
        ((PointSpriteControllerRenderData)this.renderData).colorChannel = this.controller.particles.addChannel(ParticleChannels.Color, (ParallelArray.ChannelInitializer<ParallelArray.FloatChannel>)ParticleChannels.ColorInitializer.get());
        ((PointSpriteControllerRenderData)this.renderData).scaleChannel = this.controller.particles.addChannel(ParticleChannels.Scale, (ParallelArray.ChannelInitializer<ParallelArray.FloatChannel>)ParticleChannels.ScaleInitializer.get());
        ((PointSpriteControllerRenderData)this.renderData).rotationChannel = this.controller.particles.addChannel(ParticleChannels.Rotation2D, (ParallelArray.ChannelInitializer<ParallelArray.FloatChannel>)ParticleChannels.Rotation2dInitializer.get());
    }
    
    @Override
    public boolean isCompatible(final ParticleBatch<?> batch) {
        return batch instanceof PointSpriteParticleBatch;
    }
    
    @Override
    public ParticleControllerComponent copy() {
        return new PointSpriteRenderer((PointSpriteParticleBatch)this.batch);
    }
}
