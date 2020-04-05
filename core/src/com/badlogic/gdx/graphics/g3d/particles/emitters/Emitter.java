// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.emitters;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;

public abstract class Emitter extends ParticleControllerComponent implements Json.Serializable
{
    public int minParticleCount;
    public int maxParticleCount;
    public float percent;
    
    public Emitter(final Emitter regularEmitter) {
        this.maxParticleCount = 4;
        this.set(regularEmitter);
    }
    
    public Emitter() {
        this.maxParticleCount = 4;
    }
    
    @Override
    public void init() {
        this.controller.particles.size = 0;
    }
    
    @Override
    public void end() {
        this.controller.particles.size = 0;
    }
    
    public boolean isComplete() {
        return this.percent >= 1.0f;
    }
    
    public int getMinParticleCount() {
        return this.minParticleCount;
    }
    
    public void setMinParticleCount(final int minParticleCount) {
        this.minParticleCount = minParticleCount;
    }
    
    public int getMaxParticleCount() {
        return this.maxParticleCount;
    }
    
    public void setMaxParticleCount(final int maxParticleCount) {
        this.maxParticleCount = maxParticleCount;
    }
    
    public void setParticleCount(final int aMin, final int aMax) {
        this.setMinParticleCount(aMin);
        this.setMaxParticleCount(aMax);
    }
    
    public void set(final Emitter emitter) {
        this.minParticleCount = emitter.minParticleCount;
        this.maxParticleCount = emitter.maxParticleCount;
    }
    
    @Override
    public void write(final Json json) {
        json.writeValue("minParticleCount", this.minParticleCount);
        json.writeValue("maxParticleCount", this.maxParticleCount);
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
        this.minParticleCount = json.readValue("minParticleCount", Integer.TYPE, jsonData);
        this.maxParticleCount = json.readValue("maxParticleCount", Integer.TYPE, jsonData);
    }
}
