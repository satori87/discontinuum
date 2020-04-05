// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public final class RectangleSpawnShapeValue extends PrimitiveSpawnShapeValue
{
    public RectangleSpawnShapeValue(final RectangleSpawnShapeValue value) {
        super(value);
        this.load(value);
    }
    
    public RectangleSpawnShapeValue() {
    }
    
    @Override
    public void spawnAux(final Vector3 vector, final float percent) {
        final float width = this.spawnWidth + this.spawnWidthDiff * this.spawnWidthValue.getScale(percent);
        final float height = this.spawnHeight + this.spawnHeightDiff * this.spawnHeightValue.getScale(percent);
        final float depth = this.spawnDepth + this.spawnDepthDiff * this.spawnDepthValue.getScale(percent);
        if (this.edges) {
            final int a = MathUtils.random(-1, 1);
            float tx = 0.0f;
            float ty = 0.0f;
            float tz = 0.0f;
            if (a == -1) {
                tx = ((MathUtils.random(1) == 0) ? (-width / 2.0f) : (width / 2.0f));
                if (tx == 0.0f) {
                    ty = ((MathUtils.random(1) == 0) ? (-height / 2.0f) : (height / 2.0f));
                    tz = ((MathUtils.random(1) == 0) ? (-depth / 2.0f) : (depth / 2.0f));
                }
                else {
                    ty = MathUtils.random(height) - height / 2.0f;
                    tz = MathUtils.random(depth) - depth / 2.0f;
                }
            }
            else if (a == 0) {
                tz = ((MathUtils.random(1) == 0) ? (-depth / 2.0f) : (depth / 2.0f));
                if (tz == 0.0f) {
                    ty = ((MathUtils.random(1) == 0) ? (-height / 2.0f) : (height / 2.0f));
                    tx = ((MathUtils.random(1) == 0) ? (-width / 2.0f) : (width / 2.0f));
                }
                else {
                    ty = MathUtils.random(height) - height / 2.0f;
                    tx = MathUtils.random(width) - width / 2.0f;
                }
            }
            else {
                ty = ((MathUtils.random(1) == 0) ? (-height / 2.0f) : (height / 2.0f));
                if (ty == 0.0f) {
                    tx = ((MathUtils.random(1) == 0) ? (-width / 2.0f) : (width / 2.0f));
                    tz = ((MathUtils.random(1) == 0) ? (-depth / 2.0f) : (depth / 2.0f));
                }
                else {
                    tx = MathUtils.random(width) - width / 2.0f;
                    tz = MathUtils.random(depth) - depth / 2.0f;
                }
            }
            vector.x = tx;
            vector.y = ty;
            vector.z = tz;
        }
        else {
            vector.x = MathUtils.random(width) - width / 2.0f;
            vector.y = MathUtils.random(height) - height / 2.0f;
            vector.z = MathUtils.random(depth) - depth / 2.0f;
        }
    }
    
    @Override
    public SpawnShapeValue copy() {
        return new RectangleSpawnShapeValue(this);
    }
}
