// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;

public class NumericValue extends ParticleValue
{
    private float value;
    
    public float getValue() {
        return this.value;
    }
    
    public void setValue(final float value) {
        this.value = value;
    }
    
    public void load(final NumericValue value) {
        super.load(value);
        this.value = value.value;
    }
    
    @Override
    public void write(final Json json) {
        super.write(json);
        json.writeValue("value", this.value);
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
        super.read(json, jsonData);
        this.value = json.readValue("value", Float.TYPE, jsonData);
    }
}
