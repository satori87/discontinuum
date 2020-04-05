// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.influencers;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.graphics.g3d.particles.values.ScaledNumericValue;

public abstract class SimpleInfluencer extends Influencer
{
    public ScaledNumericValue value;
    ParallelArray.FloatChannel valueChannel;
    ParallelArray.FloatChannel interpolationChannel;
    ParallelArray.FloatChannel lifeChannel;
    ParallelArray.ChannelDescriptor valueChannelDescriptor;
    
    public SimpleInfluencer() {
        (this.value = new ScaledNumericValue()).setHigh(1.0f);
    }
    
    public SimpleInfluencer(final SimpleInfluencer billboardScaleinfluencer) {
        this();
        this.set(billboardScaleinfluencer);
    }
    
    private void set(final SimpleInfluencer scaleInfluencer) {
        this.value.load(scaleInfluencer.value);
        this.valueChannelDescriptor = scaleInfluencer.valueChannelDescriptor;
    }
    
    @Override
    public void allocateChannels() {
        this.valueChannel = this.controller.particles.addChannel(this.valueChannelDescriptor);
        ParticleChannels.Interpolation.id = this.controller.particleChannels.newId();
        this.interpolationChannel = this.controller.particles.addChannel(ParticleChannels.Interpolation);
        this.lifeChannel = this.controller.particles.addChannel(ParticleChannels.Life);
    }
    
    @Override
    public void activateParticles(final int startIndex, final int count) {
        if (!this.value.isRelative()) {
            for (int i = startIndex * this.valueChannel.strideSize, a = startIndex * this.interpolationChannel.strideSize, c = i + count * this.valueChannel.strideSize; i < c; i += this.valueChannel.strideSize, a += this.interpolationChannel.strideSize) {
                final float start = this.value.newLowValue();
                final float diff = this.value.newHighValue() - start;
                this.interpolationChannel.data[a + 0] = start;
                this.interpolationChannel.data[a + 1] = diff;
                this.valueChannel.data[i] = start + diff * this.value.getScale(0.0f);
            }
        }
        else {
            for (int i = startIndex * this.valueChannel.strideSize, a = startIndex * this.interpolationChannel.strideSize, c = i + count * this.valueChannel.strideSize; i < c; i += this.valueChannel.strideSize, a += this.interpolationChannel.strideSize) {
                final float start = this.value.newLowValue();
                final float diff = this.value.newHighValue();
                this.interpolationChannel.data[a + 0] = start;
                this.interpolationChannel.data[a + 1] = diff;
                this.valueChannel.data[i] = start + diff * this.value.getScale(0.0f);
            }
        }
    }
    
    @Override
    public void update() {
        for (int i = 0, a = 0, l = 2, c = i + this.controller.particles.size * this.valueChannel.strideSize; i < c; i += this.valueChannel.strideSize, a += this.interpolationChannel.strideSize, l += this.lifeChannel.strideSize) {
            this.valueChannel.data[i] = this.interpolationChannel.data[a + 0] + this.interpolationChannel.data[a + 1] * this.value.getScale(this.lifeChannel.data[l]);
        }
    }
    
    @Override
    public void write(final Json json) {
        json.writeValue("value", this.value);
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
        this.value = json.readValue("value", ScaledNumericValue.class, jsonData);
    }
}
