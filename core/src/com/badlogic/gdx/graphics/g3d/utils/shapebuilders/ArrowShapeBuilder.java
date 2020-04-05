// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;

public class ArrowShapeBuilder extends BaseShapeBuilder
{
    public static void build(final MeshPartBuilder builder, final float x1, final float y1, final float z1, final float x2, final float y2, final float z2, final float capLength, final float stemThickness, final int divisions) {
        final Vector3 begin = BaseShapeBuilder.obtainV3().set(x1, y1, z1);
        final Vector3 end = BaseShapeBuilder.obtainV3().set(x2, y2, z2);
        final float length = begin.dst(end);
        final float coneHeight = length * capLength;
        final float coneDiameter = 2.0f * (float)(coneHeight * Math.sqrt(0.3333333432674408));
        final float stemLength = length - coneHeight;
        final float stemDiameter = coneDiameter * stemThickness;
        final Vector3 up = BaseShapeBuilder.obtainV3().set(end).sub(begin).nor();
        final Vector3 forward = BaseShapeBuilder.obtainV3().set(up).crs(Vector3.Z);
        if (forward.isZero()) {
            forward.set(Vector3.X);
        }
        forward.crs(up).nor();
        final Vector3 left = BaseShapeBuilder.obtainV3().set(up).crs(forward).nor();
        final Vector3 direction = BaseShapeBuilder.obtainV3().set(end).sub(begin).nor();
        final Matrix4 userTransform = builder.getVertexTransform(BaseShapeBuilder.obtainM4());
        final Matrix4 transform = BaseShapeBuilder.obtainM4();
        final float[] val = transform.val;
        val[0] = left.x;
        val[4] = up.x;
        val[8] = forward.x;
        val[1] = left.y;
        val[5] = up.y;
        val[9] = forward.y;
        val[2] = left.z;
        val[6] = up.z;
        val[10] = forward.z;
        final Matrix4 temp = BaseShapeBuilder.obtainM4();
        transform.setTranslation(BaseShapeBuilder.obtainV3().set(direction).scl(stemLength / 2.0f).add(x1, y1, z1));
        builder.setVertexTransform(temp.set(transform).mul(userTransform));
        CylinderShapeBuilder.build(builder, stemDiameter, stemLength, stemDiameter, divisions);
        transform.setTranslation(BaseShapeBuilder.obtainV3().set(direction).scl(stemLength).add(x1, y1, z1));
        builder.setVertexTransform(temp.set(transform).mul(userTransform));
        ConeShapeBuilder.build(builder, coneDiameter, coneHeight, coneDiameter, divisions);
        builder.setVertexTransform(userTransform);
        freeAll();
    }
}
