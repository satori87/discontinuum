// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.influencers;

import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.utils.Array;

public abstract class RegionInfluencer extends Influencer
{
    public Array<AspectTextureRegion> regions;
    ParallelArray.FloatChannel regionChannel;
    
    public RegionInfluencer(final int regionsCount) {
        this.regions = new Array<AspectTextureRegion>(false, regionsCount, AspectTextureRegion.class);
    }
    
    public RegionInfluencer() {
        this(1);
        final AspectTextureRegion aspectTextureRegion2;
        final AspectTextureRegion aspectTextureRegion;
        final AspectTextureRegion aspectRegion = aspectTextureRegion = (aspectTextureRegion2 = new AspectTextureRegion());
        final float n = 0.0f;
        aspectTextureRegion.v = n;
        aspectTextureRegion2.u = n;
        final AspectTextureRegion aspectTextureRegion3 = aspectRegion;
        final AspectTextureRegion aspectTextureRegion4 = aspectRegion;
        final float n2 = 1.0f;
        aspectTextureRegion4.v2 = n2;
        aspectTextureRegion3.u2 = n2;
        aspectRegion.halfInvAspectRatio = 0.5f;
        this.regions.add(aspectRegion);
    }
    
    public RegionInfluencer(final TextureRegion... regions) {
        this.regions = new Array<AspectTextureRegion>(false, regions.length, AspectTextureRegion.class);
        this.add(regions);
    }
    
    public RegionInfluencer(final Texture texture) {
        this(new TextureRegion[] { new TextureRegion(texture) });
    }
    
    public RegionInfluencer(final RegionInfluencer regionInfluencer) {
        this(regionInfluencer.regions.size);
        this.regions.ensureCapacity(regionInfluencer.regions.size);
        for (int i = 0; i < regionInfluencer.regions.size; ++i) {
            this.regions.add(new AspectTextureRegion(regionInfluencer.regions.get(i)));
        }
    }
    
    public void add(final TextureRegion... regions) {
        this.regions.ensureCapacity(regions.length);
        for (final TextureRegion region : regions) {
            this.regions.add(new AspectTextureRegion(region));
        }
    }
    
    public void clear() {
        this.regions.clear();
    }
    
    @Override
    public void allocateChannels() {
        this.regionChannel = this.controller.particles.addChannel(ParticleChannels.TextureRegion);
    }
    
    @Override
    public void write(final Json json) {
        json.writeValue("regions", this.regions, Array.class, AspectTextureRegion.class);
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
        this.regions.clear();
        this.regions.addAll(json.readValue("regions", (Class<Array<? extends AspectTextureRegion>>)Array.class, AspectTextureRegion.class, jsonData));
    }
    
    public static class Animated extends RegionInfluencer
    {
        ParallelArray.FloatChannel lifeChannel;
        
        public Animated() {
        }
        
        public Animated(final Animated regionInfluencer) {
            super(regionInfluencer);
        }
        
        public Animated(final TextureRegion textureRegion) {
            super(new TextureRegion[] { textureRegion });
        }
        
        public Animated(final Texture texture) {
            super(texture);
        }
        
        @Override
        public void allocateChannels() {
            super.allocateChannels();
            this.lifeChannel = this.controller.particles.addChannel(ParticleChannels.Life);
        }
        
        @Override
        public void update() {
            for (int i = 0, l = 2, c = this.controller.particles.size * this.regionChannel.strideSize; i < c; i += this.regionChannel.strideSize, l += this.lifeChannel.strideSize) {
                final AspectTextureRegion region = this.regions.get((int)(this.lifeChannel.data[l] * (this.regions.size - 1)));
                this.regionChannel.data[i + 0] = region.u;
                this.regionChannel.data[i + 1] = region.v;
                this.regionChannel.data[i + 2] = region.u2;
                this.regionChannel.data[i + 3] = region.v2;
                this.regionChannel.data[i + 4] = 0.5f;
                this.regionChannel.data[i + 5] = region.halfInvAspectRatio;
            }
        }
        
        @Override
        public Animated copy() {
            return new Animated(this);
        }
    }
    
    public static class Random extends RegionInfluencer
    {
        public Random() {
        }
        
        public Random(final Random regionInfluencer) {
            super(regionInfluencer);
        }
        
        public Random(final TextureRegion textureRegion) {
            super(new TextureRegion[] { textureRegion });
        }
        
        public Random(final Texture texture) {
            super(texture);
        }
        
        @Override
        public void activateParticles(final int startIndex, final int count) {
            for (int i = startIndex * this.regionChannel.strideSize, c = i + count * this.regionChannel.strideSize; i < c; i += this.regionChannel.strideSize) {
                final AspectTextureRegion region = this.regions.random();
                this.regionChannel.data[i + 0] = region.u;
                this.regionChannel.data[i + 1] = region.v;
                this.regionChannel.data[i + 2] = region.u2;
                this.regionChannel.data[i + 3] = region.v2;
                this.regionChannel.data[i + 4] = 0.5f;
                this.regionChannel.data[i + 5] = region.halfInvAspectRatio;
            }
        }
        
        @Override
        public Random copy() {
            return new Random(this);
        }
    }
    
    public static class Single extends RegionInfluencer
    {
        public Single() {
        }
        
        public Single(final Single regionInfluencer) {
            super(regionInfluencer);
        }
        
        public Single(final TextureRegion textureRegion) {
            super(new TextureRegion[] { textureRegion });
        }
        
        public Single(final Texture texture) {
            super(texture);
        }
        
        @Override
        public void init() {
            final AspectTextureRegion region = this.regions.items[0];
            for (int i = 0, c = this.controller.emitter.maxParticleCount * this.regionChannel.strideSize; i < c; i += this.regionChannel.strideSize) {
                this.regionChannel.data[i + 0] = region.u;
                this.regionChannel.data[i + 1] = region.v;
                this.regionChannel.data[i + 2] = region.u2;
                this.regionChannel.data[i + 3] = region.v2;
                this.regionChannel.data[i + 4] = 0.5f;
                this.regionChannel.data[i + 5] = region.halfInvAspectRatio;
            }
        }
        
        @Override
        public Single copy() {
            return new Single(this);
        }
    }
    
    public static class AspectTextureRegion
    {
        public float u;
        public float v;
        public float u2;
        public float v2;
        public float halfInvAspectRatio;
        
        public AspectTextureRegion() {
        }
        
        public AspectTextureRegion(final AspectTextureRegion aspectTextureRegion) {
            this.set(aspectTextureRegion);
        }
        
        public AspectTextureRegion(final TextureRegion region) {
            this.set(region);
        }
        
        public void set(final TextureRegion region) {
            this.u = region.getU();
            this.v = region.getV();
            this.u2 = region.getU2();
            this.v2 = region.getV2();
            this.halfInvAspectRatio = 0.5f * (region.getRegionHeight() / (float)region.getRegionWidth());
        }
        
        public void set(final AspectTextureRegion aspectTextureRegion) {
            this.u = aspectTextureRegion.u;
            this.v = aspectTextureRegion.v;
            this.u2 = aspectTextureRegion.u2;
            this.v2 = aspectTextureRegion.v2;
            this.halfInvAspectRatio = aspectTextureRegion.halfInvAspectRatio;
        }
    }
}
