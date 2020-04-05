// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public final class CylinderSpawnShapeValue extends PrimitiveSpawnShapeValue
{
    public CylinderSpawnShapeValue(final CylinderSpawnShapeValue cylinderSpawnShapeValue) {
        super(cylinderSpawnShapeValue);
        this.load(cylinderSpawnShapeValue);
    }
    
    public CylinderSpawnShapeValue() {
    }
    
    @Override
    public void spawnAux(final Vector3 vector, final float percent) {
        final float width = this.spawnWidth + this.spawnWidthDiff * this.spawnWidthValue.getScale(percent);
        final float height = this.spawnHeight + this.spawnHeightDiff * this.spawnHeightValue.getScale(percent);
        final float depth = this.spawnDepth + this.spawnDepthDiff * this.spawnDepthValue.getScale(percent);
        final float hf = height / 2.0f;
        final float ty = MathUtils.random(height) - hf;
        float radiusX;
        float radiusZ;
        if (this.edges) {
            radiusX = width / 2.0f;
            radiusZ = depth / 2.0f;
        }
        else {
            radiusX = MathUtils.random(width) / 2.0f;
            radiusZ = MathUtils.random(depth) / 2.0f;
        }
        float spawnTheta = 0.0f;
        final boolean isRadiusXZero = radiusX == 0.0f;
        final boolean isRadiusZZero = radiusZ == 0.0f;
        if (!isRadiusXZero && !isRadiusZZero) {
            spawnTheta = MathUtils.random(360.0f);
        }
        else if (isRadiusXZero) {
            spawnTheta = (float)((MathUtils.random(1) == 0) ? -90 : 90);
        }
        else if (isRadiusZZero) {
            spawnTheta = (float)((MathUtils.random(1) == 0) ? 0 : 180);
        }
        vector.set(radiusX * MathUtils.cosDeg(spawnTheta), ty, radiusZ * MathUtils.sinDeg(spawnTheta));
    }
    
    @Override
    public SpawnShapeValue copy() {
        return new CylinderSpawnShapeValue(this);
    }
}
