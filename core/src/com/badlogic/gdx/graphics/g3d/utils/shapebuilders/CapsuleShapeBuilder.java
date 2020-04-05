// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;

public class CapsuleShapeBuilder extends BaseShapeBuilder
{
    public static void build(final MeshPartBuilder builder, final float radius, final float height, final int divisions) {
        if (height < 2.0f * radius) {
            throw new GdxRuntimeException("Height must be at least twice the radius");
        }
        final float d = 2.0f * radius;
        CylinderShapeBuilder.build(builder, d, height - d, d, divisions, 0.0f, 360.0f, false);
        SphereShapeBuilder.build(builder, CapsuleShapeBuilder.matTmp1.setToTranslation(0.0f, 0.5f * (height - d), 0.0f), d, d, d, divisions, divisions, 0.0f, 360.0f, 0.0f, 90.0f);
        SphereShapeBuilder.build(builder, CapsuleShapeBuilder.matTmp1.setToTranslation(0.0f, -0.5f * (height - d), 0.0f), d, d, d, divisions, divisions, 0.0f, 360.0f, 90.0f, 180.0f);
    }
}
