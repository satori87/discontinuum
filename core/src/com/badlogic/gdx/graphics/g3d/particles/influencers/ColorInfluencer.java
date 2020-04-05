// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.influencers;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.graphics.g3d.particles.values.GradientColorValue;
import com.badlogic.gdx.graphics.g3d.particles.values.ScaledNumericValue;
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;

public abstract class ColorInfluencer extends Influencer
{
    ParallelArray.FloatChannel colorChannel;
    
    @Override
    public void allocateChannels() {
        this.colorChannel = this.controller.particles.addChannel(ParticleChannels.Color);
    }
    
    public static class Random extends ColorInfluencer
    {
        ParallelArray.FloatChannel colorChannel;
        
        @Override
        public void allocateChannels() {
            this.colorChannel = this.controller.particles.addChannel(ParticleChannels.Color);
        }
        
        @Override
        public void activateParticles(final int startIndex, final int count) {
            for (int i = startIndex * this.colorChannel.strideSize, c = i + count * this.colorChannel.strideSize; i < c; i += this.colorChannel.strideSize) {
                this.colorChannel.data[i + 0] = MathUtils.random();
                this.colorChannel.data[i + 1] = MathUtils.random();
                this.colorChannel.data[i + 2] = MathUtils.random();
                this.colorChannel.data[i + 3] = MathUtils.random();
            }
        }
        
        @Override
        public Random copy() {
            return new Random();
        }
    }
    
    public static class Single extends ColorInfluencer
    {
        ParallelArray.FloatChannel alphaInterpolationChannel;
        ParallelArray.FloatChannel lifeChannel;
        public ScaledNumericValue alphaValue;
        public GradientColorValue colorValue;
        
        public Single() {
            this.colorValue = new GradientColorValue();
            (this.alphaValue = new ScaledNumericValue()).setHigh(1.0f);
        }
        
        public Single(final Single billboardColorInfluencer) {
            this();
            this.set(billboardColorInfluencer);
        }
        
        public void set(final Single colorInfluencer) {
            this.colorValue.load(colorInfluencer.colorValue);
            this.alphaValue.load(colorInfluencer.alphaValue);
        }
        
        @Override
        public void allocateChannels() {
            super.allocateChannels();
            ParticleChannels.Interpolation.id = this.controller.particleChannels.newId();
            this.alphaInterpolationChannel = this.controller.particles.addChannel(ParticleChannels.Interpolation);
            this.lifeChannel = this.controller.particles.addChannel(ParticleChannels.Life);
        }
        
        @Override
        public void activateParticles(final int startIndex, final int count) {
            for (int i = startIndex * this.colorChannel.strideSize, a = startIndex * this.alphaInterpolationChannel.strideSize, l = startIndex * this.lifeChannel.strideSize + 2, c = i + count * this.colorChannel.strideSize; i < c; i += this.colorChannel.strideSize, a += this.alphaInterpolationChannel.strideSize, l += this.lifeChannel.strideSize) {
                final float alphaStart = this.alphaValue.newLowValue();
                final float alphaDiff = this.alphaValue.newHighValue() - alphaStart;
                this.colorValue.getColor(0.0f, this.colorChannel.data, i);
                this.colorChannel.data[i + 3] = alphaStart + alphaDiff * this.alphaValue.getScale(this.lifeChannel.data[l]);
                this.alphaInterpolationChannel.data[a + 0] = alphaStart;
                this.alphaInterpolationChannel.data[a + 1] = alphaDiff;
            }
        }
        
        @Override
        public void update() {
            for (int i = 0, a = 0, l = 2, c = i + this.controller.particles.size * this.colorChannel.strideSize; i < c; i += this.colorChannel.strideSize, a += this.alphaInterpolationChannel.strideSize, l += this.lifeChannel.strideSize) {
                final float lifePercent = this.lifeChannel.data[l];
                this.colorValue.getColor(lifePercent, this.colorChannel.data, i);
                this.colorChannel.data[i + 3] = this.alphaInterpolationChannel.data[a + 0] + this.alphaInterpolationChannel.data[a + 1] * this.alphaValue.getScale(lifePercent);
            }
        }
        
        @Override
        public Single copy() {
            return new Single(this);
        }
        
        @Override
        public void write(final Json json) {
            json.writeValue("alpha", this.alphaValue);
            json.writeValue("color", this.colorValue);
        }
        
        @Override
        public void read(final Json json, final JsonValue jsonData) {
            this.alphaValue = json.readValue("alpha", ScaledNumericValue.class, jsonData);
            this.colorValue = json.readValue("color", GradientColorValue.class, jsonData);
        }
    }
}
