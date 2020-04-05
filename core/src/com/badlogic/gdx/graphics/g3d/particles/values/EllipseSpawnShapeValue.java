// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public final class EllipseSpawnShapeValue extends PrimitiveSpawnShapeValue
{
    SpawnSide side;
    
    public EllipseSpawnShapeValue(final EllipseSpawnShapeValue value) {
        super(value);
        this.side = SpawnSide.both;
        this.load(value);
    }
    
    public EllipseSpawnShapeValue() {
        this.side = SpawnSide.both;
    }
    
    @Override
    public void spawnAux(final Vector3 vector, final float percent) {
        final float width = this.spawnWidth + this.spawnWidthDiff * this.spawnWidthValue.getScale(percent);
        final float height = this.spawnHeight + this.spawnHeightDiff * this.spawnHeightValue.getScale(percent);
        final float depth = this.spawnDepth + this.spawnDepthDiff * this.spawnDepthValue.getScale(percent);
        final float minT = 0.0f;
        float maxT = 6.2831855f;
        if (this.side == SpawnSide.top) {
            maxT = 3.1415927f;
        }
        else if (this.side == SpawnSide.bottom) {
            maxT = -3.1415927f;
        }
        final float t = MathUtils.random(minT, maxT);
        float radiusX;
        float radiusY;
        float radiusZ;
        if (this.edges) {
            if (width == 0.0f) {
                vector.set(0.0f, height / 2.0f * MathUtils.sin(t), depth / 2.0f * MathUtils.cos(t));
                return;
            }
            if (height == 0.0f) {
                vector.set(width / 2.0f * MathUtils.cos(t), 0.0f, depth / 2.0f * MathUtils.sin(t));
                return;
            }
            if (depth == 0.0f) {
                vector.set(width / 2.0f * MathUtils.cos(t), height / 2.0f * MathUtils.sin(t), 0.0f);
                return;
            }
            radiusX = width / 2.0f;
            radiusY = height / 2.0f;
            radiusZ = depth / 2.0f;
        }
        else {
            radiusX = MathUtils.random(width / 2.0f);
            radiusY = MathUtils.random(height / 2.0f);
            radiusZ = MathUtils.random(depth / 2.0f);
        }
        final float z = MathUtils.random(-1.0f, 1.0f);
        final float r = (float)Math.sqrt(1.0f - z * z);
        vector.set(radiusX * r * MathUtils.cos(t), radiusY * r * MathUtils.sin(t), radiusZ * z);
    }
    
    public SpawnSide getSide() {
        return this.side;
    }
    
    public void setSide(final SpawnSide side) {
        this.side = side;
    }
    
    @Override
    public void load(final ParticleValue value) {
        super.load(value);
        final EllipseSpawnShapeValue shape = (EllipseSpawnShapeValue)value;
        this.side = shape.side;
    }
    
    @Override
    public SpawnShapeValue copy() {
        return new EllipseSpawnShapeValue(this);
    }
    
    @Override
    public void write(final Json json) {
        super.write(json);
        json.writeValue("side", this.side);
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
        super.read(json, jsonData);
        this.side = json.readValue("side", SpawnSide.class, jsonData);
    }
}
