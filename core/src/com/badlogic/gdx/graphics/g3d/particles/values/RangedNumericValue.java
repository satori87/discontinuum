// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.math.MathUtils;

public class RangedNumericValue extends ParticleValue
{
    private float lowMin;
    private float lowMax;
    
    public float newLowValue() {
        return this.lowMin + (this.lowMax - this.lowMin) * MathUtils.random();
    }
    
    public void setLow(final float value) {
        this.lowMin = value;
        this.lowMax = value;
    }
    
    public void setLow(final float min, final float max) {
        this.lowMin = min;
        this.lowMax = max;
    }
    
    public float getLowMin() {
        return this.lowMin;
    }
    
    public void setLowMin(final float lowMin) {
        this.lowMin = lowMin;
    }
    
    public float getLowMax() {
        return this.lowMax;
    }
    
    public void setLowMax(final float lowMax) {
        this.lowMax = lowMax;
    }
    
    public void load(final RangedNumericValue value) {
        super.load(value);
        this.lowMax = value.lowMax;
        this.lowMin = value.lowMin;
    }
    
    @Override
    public void write(final Json json) {
        super.write(json);
        json.writeValue("lowMin", this.lowMin);
        json.writeValue("lowMax", this.lowMax);
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
        super.read(json, jsonData);
        this.lowMin = json.readValue("lowMin", Float.TYPE, jsonData);
        this.lowMax = json.readValue("lowMax", Float.TYPE, jsonData);
    }
}
