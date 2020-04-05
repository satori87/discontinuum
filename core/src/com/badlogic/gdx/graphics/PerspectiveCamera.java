// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class PerspectiveCamera extends Camera
{
    public float fieldOfView;
    final Vector3 tmp;
    
    public PerspectiveCamera() {
        this.fieldOfView = 67.0f;
        this.tmp = new Vector3();
    }
    
    public PerspectiveCamera(final float fieldOfViewY, final float viewportWidth, final float viewportHeight) {
        this.fieldOfView = 67.0f;
        this.tmp = new Vector3();
        this.fieldOfView = fieldOfViewY;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.update();
    }
    
    @Override
    public void update() {
        this.update(true);
    }
    
    @Override
    public void update(final boolean updateFrustum) {
        final float aspect = this.viewportWidth / this.viewportHeight;
        this.projection.setToProjection(Math.abs(this.near), Math.abs(this.far), this.fieldOfView, aspect);
        this.view.setToLookAt(this.position, this.tmp.set(this.position).add(this.direction), this.up);
        this.combined.set(this.projection);
        Matrix4.mul(this.combined.val, this.view.val);
        if (updateFrustum) {
            this.invProjectionView.set(this.combined);
            Matrix4.inv(this.invProjectionView.val);
            this.frustum.update(this.invProjectionView);
        }
    }
}
