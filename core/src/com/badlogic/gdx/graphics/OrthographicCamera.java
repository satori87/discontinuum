// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class OrthographicCamera extends Camera
{
    public float zoom;
    private final Vector3 tmp;
    
    public OrthographicCamera() {
        this.zoom = 1.0f;
        this.tmp = new Vector3();
        this.near = 0.0f;
    }
    
    public OrthographicCamera(final float viewportWidth, final float viewportHeight) {
        this.zoom = 1.0f;
        this.tmp = new Vector3();
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.near = 0.0f;
        this.update();
    }
    
    @Override
    public void update() {
        this.update(true);
    }
    
    @Override
    public void update(final boolean updateFrustum) {
        this.projection.setToOrtho(this.zoom * -this.viewportWidth / 2.0f, this.zoom * (this.viewportWidth / 2.0f), this.zoom * -(this.viewportHeight / 2.0f), this.zoom * this.viewportHeight / 2.0f, this.near, this.far);
        this.view.setToLookAt(this.position, this.tmp.set(this.position).add(this.direction), this.up);
        this.combined.set(this.projection);
        Matrix4.mul(this.combined.val, this.view.val);
        if (updateFrustum) {
            this.invProjectionView.set(this.combined);
            Matrix4.inv(this.invProjectionView.val);
            this.frustum.update(this.invProjectionView);
        }
    }
    
    public void setToOrtho(final boolean yDown) {
        this.setToOrtho(yDown, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
    }
    
    public void setToOrtho(final boolean yDown, final float viewportWidth, final float viewportHeight) {
        if (yDown) {
            this.up.set(0.0f, -1.0f, 0.0f);
            this.direction.set(0.0f, 0.0f, 1.0f);
        }
        else {
            this.up.set(0.0f, 1.0f, 0.0f);
            this.direction.set(0.0f, 0.0f, -1.0f);
        }
        this.position.set(this.zoom * viewportWidth / 2.0f, this.zoom * viewportHeight / 2.0f, 0.0f);
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.update();
    }
    
    public void rotate(final float angle) {
        this.rotate(this.direction, angle);
    }
    
    public void translate(final float x, final float y) {
        this.translate(x, y, 0.0f);
    }
    
    public void translate(final Vector2 vec) {
        this.translate(vec.x, vec.y, 0.0f);
    }
}
