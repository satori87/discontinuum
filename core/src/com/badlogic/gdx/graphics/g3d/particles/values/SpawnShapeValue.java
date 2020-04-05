// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData;

public abstract class SpawnShapeValue extends ParticleValue implements ResourceData.Configurable, Json.Serializable
{
    public RangedNumericValue xOffsetValue;
    public RangedNumericValue yOffsetValue;
    public RangedNumericValue zOffsetValue;
    
    public SpawnShapeValue() {
        this.xOffsetValue = new RangedNumericValue();
        this.yOffsetValue = new RangedNumericValue();
        this.zOffsetValue = new RangedNumericValue();
    }
    
    public SpawnShapeValue(final SpawnShapeValue spawnShapeValue) {
        this();
    }
    
    public abstract void spawnAux(final Vector3 p0, final float p1);
    
    public final Vector3 spawn(final Vector3 vector, final float percent) {
        this.spawnAux(vector, percent);
        if (this.xOffsetValue.active) {
            vector.x += this.xOffsetValue.newLowValue();
        }
        if (this.yOffsetValue.active) {
            vector.y += this.yOffsetValue.newLowValue();
        }
        if (this.zOffsetValue.active) {
            vector.z += this.zOffsetValue.newLowValue();
        }
        return vector;
    }
    
    public void init() {
    }
    
    public void start() {
    }
    
    @Override
    public void load(final ParticleValue value) {
        super.load(value);
        final SpawnShapeValue shape = (SpawnShapeValue)value;
        this.xOffsetValue.load(shape.xOffsetValue);
        this.yOffsetValue.load(shape.yOffsetValue);
        this.zOffsetValue.load(shape.zOffsetValue);
    }
    
    public abstract SpawnShapeValue copy();
    
    @Override
    public void write(final Json json) {
        super.write(json);
        json.writeValue("xOffsetValue", this.xOffsetValue);
        json.writeValue("yOffsetValue", this.yOffsetValue);
        json.writeValue("zOffsetValue", this.zOffsetValue);
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
        super.read(json, jsonData);
        this.xOffsetValue = json.readValue("xOffsetValue", RangedNumericValue.class, jsonData);
        this.yOffsetValue = json.readValue("yOffsetValue", RangedNumericValue.class, jsonData);
        this.zOffsetValue = json.readValue("zOffsetValue", RangedNumericValue.class, jsonData);
    }
    
    @Override
    public void save(final AssetManager manager, final ResourceData data) {
    }
    
    @Override
    public void load(final AssetManager manager, final ResourceData data) {
    }
}
