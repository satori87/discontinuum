// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;

public class ParticleValue implements Json.Serializable
{
    public boolean active;
    
    public ParticleValue() {
    }
    
    public ParticleValue(final ParticleValue value) {
        this.active = value.active;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
    
    public void load(final ParticleValue value) {
        this.active = value.active;
    }
    
    @Override
    public void write(final Json json) {
        json.writeValue("active", this.active);
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
        this.active = json.readValue("active", Boolean.class, jsonData);
    }
}
