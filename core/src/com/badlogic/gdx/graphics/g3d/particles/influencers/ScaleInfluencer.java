// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.influencers;

import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;

public class ScaleInfluencer extends SimpleInfluencer
{
    public ScaleInfluencer() {
        this.valueChannelDescriptor = ParticleChannels.Scale;
    }
    
    @Override
    public void activateParticles(final int startIndex, final int count) {
        if (this.value.isRelative()) {
            for (int i = startIndex * this.valueChannel.strideSize, a = startIndex * this.interpolationChannel.strideSize, c = i + count * this.valueChannel.strideSize; i < c; i += this.valueChannel.strideSize, a += this.interpolationChannel.strideSize) {
                final float start = this.value.newLowValue() * this.controller.scale.x;
                final float diff = this.value.newHighValue() * this.controller.scale.x;
                this.interpolationChannel.data[a + 0] = start;
                this.interpolationChannel.data[a + 1] = diff;
                this.valueChannel.data[i] = start + diff * this.value.getScale(0.0f);
            }
        }
        else {
            for (int i = startIndex * this.valueChannel.strideSize, a = startIndex * this.interpolationChannel.strideSize, c = i + count * this.valueChannel.strideSize; i < c; i += this.valueChannel.strideSize, a += this.interpolationChannel.strideSize) {
                final float start = this.value.newLowValue() * this.controller.scale.x;
                final float diff = this.value.newHighValue() * this.controller.scale.x - start;
                this.interpolationChannel.data[a + 0] = start;
                this.interpolationChannel.data[a + 1] = diff;
                this.valueChannel.data[i] = start + diff * this.value.getScale(0.0f);
            }
        }
    }
    
    public ScaleInfluencer(final ScaleInfluencer scaleInfluencer) {
        super(scaleInfluencer);
    }
    
    @Override
    public ParticleControllerComponent copy() {
        return new ScaleInfluencer(this);
    }
}
