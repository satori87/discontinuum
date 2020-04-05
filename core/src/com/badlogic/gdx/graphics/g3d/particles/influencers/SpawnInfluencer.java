// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.influencers;

import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.values.PointSpawnShapeValue;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.graphics.g3d.particles.values.SpawnShapeValue;

public class SpawnInfluencer extends Influencer
{
    public SpawnShapeValue spawnShapeValue;
    ParallelArray.FloatChannel positionChannel;
    
    public SpawnInfluencer() {
        this.spawnShapeValue = new PointSpawnShapeValue();
    }
    
    public SpawnInfluencer(final SpawnShapeValue spawnShapeValue) {
        this.spawnShapeValue = spawnShapeValue;
    }
    
    public SpawnInfluencer(final SpawnInfluencer source) {
        this.spawnShapeValue = source.spawnShapeValue.copy();
    }
    
    @Override
    public void init() {
        this.spawnShapeValue.init();
    }
    
    @Override
    public void allocateChannels() {
        this.positionChannel = this.controller.particles.addChannel(ParticleChannels.Position);
    }
    
    @Override
    public void start() {
        this.spawnShapeValue.start();
    }
    
    @Override
    public void activateParticles(final int startIndex, final int count) {
        for (int i = startIndex * this.positionChannel.strideSize, c = i + count * this.positionChannel.strideSize; i < c; i += this.positionChannel.strideSize) {
            this.spawnShapeValue.spawn(SpawnInfluencer.TMP_V1, this.controller.emitter.percent);
            SpawnInfluencer.TMP_V1.mul(this.controller.transform);
            this.positionChannel.data[i + 0] = SpawnInfluencer.TMP_V1.x;
            this.positionChannel.data[i + 1] = SpawnInfluencer.TMP_V1.y;
            this.positionChannel.data[i + 2] = SpawnInfluencer.TMP_V1.z;
        }
    }
    
    @Override
    public SpawnInfluencer copy() {
        return new SpawnInfluencer(this);
    }
    
    @Override
    public void write(final Json json) {
        json.writeValue("spawnShape", this.spawnShapeValue, SpawnShapeValue.class);
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
        this.spawnShapeValue = json.readValue("spawnShape", SpawnShapeValue.class, jsonData);
    }
    
    @Override
    public void save(final AssetManager manager, final ResourceData data) {
        this.spawnShapeValue.save(manager, data);
    }
    
    @Override
    public void load(final AssetManager manager, final ResourceData data) {
        this.spawnShapeValue.load(manager, data);
    }
}
