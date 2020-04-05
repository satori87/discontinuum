// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public final class LineSpawnShapeValue extends PrimitiveSpawnShapeValue
{
    public LineSpawnShapeValue(final LineSpawnShapeValue value) {
        super(value);
        this.load(value);
    }
    
    public LineSpawnShapeValue() {
    }
    
    @Override
    public void spawnAux(final Vector3 vector, final float percent) {
        final float width = this.spawnWidth + this.spawnWidthDiff * this.spawnWidthValue.getScale(percent);
        final float height = this.spawnHeight + this.spawnHeightDiff * this.spawnHeightValue.getScale(percent);
        final float depth = this.spawnDepth + this.spawnDepthDiff * this.spawnDepthValue.getScale(percent);
        final float a = MathUtils.random();
        vector.x = a * width;
        vector.y = a * height;
        vector.z = a * depth;
    }
    
    @Override
    public SpawnShapeValue copy() {
        return new LineSpawnShapeValue(this);
    }
}
