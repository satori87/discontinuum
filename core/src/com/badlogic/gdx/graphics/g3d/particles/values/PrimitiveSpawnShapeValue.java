// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.math.Vector3;

public abstract class PrimitiveSpawnShapeValue extends SpawnShapeValue
{
    protected static final Vector3 TMP_V1;
    public ScaledNumericValue spawnWidthValue;
    public ScaledNumericValue spawnHeightValue;
    public ScaledNumericValue spawnDepthValue;
    protected float spawnWidth;
    protected float spawnWidthDiff;
    protected float spawnHeight;
    protected float spawnHeightDiff;
    protected float spawnDepth;
    protected float spawnDepthDiff;
    boolean edges;
    
    static {
        TMP_V1 = new Vector3();
    }
    
    public PrimitiveSpawnShapeValue() {
        this.edges = false;
        this.spawnWidthValue = new ScaledNumericValue();
        this.spawnHeightValue = new ScaledNumericValue();
        this.spawnDepthValue = new ScaledNumericValue();
    }
    
    public PrimitiveSpawnShapeValue(final PrimitiveSpawnShapeValue value) {
        super(value);
        this.edges = false;
        this.spawnWidthValue = new ScaledNumericValue();
        this.spawnHeightValue = new ScaledNumericValue();
        this.spawnDepthValue = new ScaledNumericValue();
    }
    
    @Override
    public void setActive(final boolean active) {
        super.setActive(active);
        this.spawnWidthValue.setActive(true);
        this.spawnHeightValue.setActive(true);
        this.spawnDepthValue.setActive(true);
    }
    
    public boolean isEdges() {
        return this.edges;
    }
    
    public void setEdges(final boolean edges) {
        this.edges = edges;
    }
    
    public ScaledNumericValue getSpawnWidth() {
        return this.spawnWidthValue;
    }
    
    public ScaledNumericValue getSpawnHeight() {
        return this.spawnHeightValue;
    }
    
    public ScaledNumericValue getSpawnDepth() {
        return this.spawnDepthValue;
    }
    
    public void setDimensions(final float width, final float height, final float depth) {
        this.spawnWidthValue.setHigh(width);
        this.spawnHeightValue.setHigh(height);
        this.spawnDepthValue.setHigh(depth);
    }
    
    @Override
    public void start() {
        this.spawnWidth = this.spawnWidthValue.newLowValue();
        this.spawnWidthDiff = this.spawnWidthValue.newHighValue();
        if (!this.spawnWidthValue.isRelative()) {
            this.spawnWidthDiff -= this.spawnWidth;
        }
        this.spawnHeight = this.spawnHeightValue.newLowValue();
        this.spawnHeightDiff = this.spawnHeightValue.newHighValue();
        if (!this.spawnHeightValue.isRelative()) {
            this.spawnHeightDiff -= this.spawnHeight;
        }
        this.spawnDepth = this.spawnDepthValue.newLowValue();
        this.spawnDepthDiff = this.spawnDepthValue.newHighValue();
        if (!this.spawnDepthValue.isRelative()) {
            this.spawnDepthDiff -= this.spawnDepth;
        }
    }
    
    @Override
    public void load(final ParticleValue value) {
        super.load(value);
        final PrimitiveSpawnShapeValue shape = (PrimitiveSpawnShapeValue)value;
        this.edges = shape.edges;
        this.spawnWidthValue.load(shape.spawnWidthValue);
        this.spawnHeightValue.load(shape.spawnHeightValue);
        this.spawnDepthValue.load(shape.spawnDepthValue);
    }
    
    @Override
    public void write(final Json json) {
        super.write(json);
        json.writeValue("spawnWidthValue", this.spawnWidthValue);
        json.writeValue("spawnHeightValue", this.spawnHeightValue);
        json.writeValue("spawnDepthValue", this.spawnDepthValue);
        json.writeValue("edges", this.edges);
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
        super.read(json, jsonData);
        this.spawnWidthValue = json.readValue("spawnWidthValue", ScaledNumericValue.class, jsonData);
        this.spawnHeightValue = json.readValue("spawnHeightValue", ScaledNumericValue.class, jsonData);
        this.spawnDepthValue = json.readValue("spawnDepthValue", ScaledNumericValue.class, jsonData);
        this.edges = json.readValue("edges", Boolean.TYPE, jsonData);
    }
    
    public enum SpawnSide
    {
        both("both", 0), 
        top("top", 1), 
        bottom("bottom", 2);
        
        private SpawnSide(final String name, final int ordinal) {
        }
    }
}
