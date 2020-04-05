// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;

public class FrustumShapeBuilder extends BaseShapeBuilder
{
    public static void build(final MeshPartBuilder builder, final Camera camera) {
        build(builder, camera, FrustumShapeBuilder.tmpColor0.set(1.0f, 0.66f, 0.0f, 1.0f), FrustumShapeBuilder.tmpColor1.set(1.0f, 0.0f, 0.0f, 1.0f), FrustumShapeBuilder.tmpColor2.set(0.0f, 0.66f, 1.0f, 1.0f), FrustumShapeBuilder.tmpColor3.set(1.0f, 1.0f, 1.0f, 1.0f), FrustumShapeBuilder.tmpColor4.set(0.2f, 0.2f, 0.2f, 1.0f));
    }
    
    public static void build(final MeshPartBuilder builder, final Camera camera, final Color frustumColor, final Color coneColor, final Color upColor, final Color targetColor, final Color crossColor) {
        final Vector3[] planePoints = camera.frustum.planePoints;
        build(builder, camera.frustum, frustumColor, crossColor);
        builder.line(planePoints[0], coneColor, camera.position, coneColor);
        builder.line(planePoints[1], coneColor, camera.position, coneColor);
        builder.line(planePoints[2], coneColor, camera.position, coneColor);
        builder.line(planePoints[3], coneColor, camera.position, coneColor);
        builder.line(camera.position, targetColor, centerPoint(planePoints[4], planePoints[5], planePoints[6]), targetColor);
        final float halfNearSize = FrustumShapeBuilder.tmpV0.set(planePoints[1]).sub(planePoints[0]).scl(0.5f).len();
        final Vector3 centerNear = centerPoint(planePoints[0], planePoints[1], planePoints[2]);
        FrustumShapeBuilder.tmpV0.set(camera.up).scl(halfNearSize * 2.0f);
        centerNear.add(FrustumShapeBuilder.tmpV0);
        builder.line(centerNear, upColor, planePoints[2], upColor);
        builder.line(planePoints[2], upColor, planePoints[3], upColor);
        builder.line(planePoints[3], upColor, centerNear, upColor);
    }
    
    public static void build(final MeshPartBuilder builder, final Frustum frustum, final Color frustumColor, final Color crossColor) {
        final Vector3[] planePoints = frustum.planePoints;
        builder.line(planePoints[0], frustumColor, planePoints[1], frustumColor);
        builder.line(planePoints[1], frustumColor, planePoints[2], frustumColor);
        builder.line(planePoints[2], frustumColor, planePoints[3], frustumColor);
        builder.line(planePoints[3], frustumColor, planePoints[0], frustumColor);
        builder.line(planePoints[4], frustumColor, planePoints[5], frustumColor);
        builder.line(planePoints[5], frustumColor, planePoints[6], frustumColor);
        builder.line(planePoints[6], frustumColor, planePoints[7], frustumColor);
        builder.line(planePoints[7], frustumColor, planePoints[4], frustumColor);
        builder.line(planePoints[0], frustumColor, planePoints[4], frustumColor);
        builder.line(planePoints[1], frustumColor, planePoints[5], frustumColor);
        builder.line(planePoints[2], frustumColor, planePoints[6], frustumColor);
        builder.line(planePoints[3], frustumColor, planePoints[7], frustumColor);
        builder.line(middlePoint(planePoints[1], planePoints[0]), crossColor, middlePoint(planePoints[3], planePoints[2]), crossColor);
        builder.line(middlePoint(planePoints[2], planePoints[1]), crossColor, middlePoint(planePoints[3], planePoints[0]), crossColor);
        builder.line(middlePoint(planePoints[5], planePoints[4]), crossColor, middlePoint(planePoints[7], planePoints[6]), crossColor);
        builder.line(middlePoint(planePoints[6], planePoints[5]), crossColor, middlePoint(planePoints[7], planePoints[4]), crossColor);
    }
    
    private static Vector3 middlePoint(final Vector3 point0, final Vector3 point1) {
        FrustumShapeBuilder.tmpV0.set(point1).sub(point0).scl(0.5f);
        return FrustumShapeBuilder.tmpV1.set(point0).add(FrustumShapeBuilder.tmpV0);
    }
    
    private static Vector3 centerPoint(final Vector3 point0, final Vector3 point1, final Vector3 point2) {
        FrustumShapeBuilder.tmpV0.set(point1).sub(point0).scl(0.5f);
        FrustumShapeBuilder.tmpV1.set(point0).add(FrustumShapeBuilder.tmpV0);
        FrustumShapeBuilder.tmpV0.set(point2).sub(point1).scl(0.5f);
        return FrustumShapeBuilder.tmpV1.add(FrustumShapeBuilder.tmpV0);
    }
}
