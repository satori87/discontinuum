// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public abstract class Camera
{
    public final Vector3 position;
    public final Vector3 direction;
    public final Vector3 up;
    public final Matrix4 projection;
    public final Matrix4 view;
    public final Matrix4 combined;
    public final Matrix4 invProjectionView;
    public float near;
    public float far;
    public float viewportWidth;
    public float viewportHeight;
    public final Frustum frustum;
    private final Vector3 tmpVec;
    private final Ray ray;
    
    public Camera() {
        this.position = new Vector3();
        this.direction = new Vector3(0.0f, 0.0f, -1.0f);
        this.up = new Vector3(0.0f, 1.0f, 0.0f);
        this.projection = new Matrix4();
        this.view = new Matrix4();
        this.combined = new Matrix4();
        this.invProjectionView = new Matrix4();
        this.near = 1.0f;
        this.far = 100.0f;
        this.viewportWidth = 0.0f;
        this.viewportHeight = 0.0f;
        this.frustum = new Frustum();
        this.tmpVec = new Vector3();
        this.ray = new Ray(new Vector3(), new Vector3());
    }
    
    public abstract void update();
    
    public abstract void update(final boolean p0);
    
    public void lookAt(final float x, final float y, final float z) {
        this.tmpVec.set(x, y, z).sub(this.position).nor();
        if (!this.tmpVec.isZero()) {
            final float dot = this.tmpVec.dot(this.up);
            if (Math.abs(dot - 1.0f) < 1.0E-9f) {
                this.up.set(this.direction).scl(-1.0f);
            }
            else if (Math.abs(dot + 1.0f) < 1.0E-9f) {
                this.up.set(this.direction);
            }
            this.direction.set(this.tmpVec);
            this.normalizeUp();
        }
    }
    
    public void lookAt(final Vector3 target) {
        this.lookAt(target.x, target.y, target.z);
    }
    
    public void normalizeUp() {
        this.tmpVec.set(this.direction).crs(this.up).nor();
        this.up.set(this.tmpVec).crs(this.direction).nor();
    }
    
    public void rotate(final float angle, final float axisX, final float axisY, final float axisZ) {
        this.direction.rotate(angle, axisX, axisY, axisZ);
        this.up.rotate(angle, axisX, axisY, axisZ);
    }
    
    public void rotate(final Vector3 axis, final float angle) {
        this.direction.rotate(axis, angle);
        this.up.rotate(axis, angle);
    }
    
    public void rotate(final Matrix4 transform) {
        this.direction.rot(transform);
        this.up.rot(transform);
    }
    
    public void rotate(final Quaternion quat) {
        quat.transform(this.direction);
        quat.transform(this.up);
    }
    
    public void rotateAround(final Vector3 point, final Vector3 axis, final float angle) {
        this.tmpVec.set(point);
        this.tmpVec.sub(this.position);
        this.translate(this.tmpVec);
        this.rotate(axis, angle);
        this.tmpVec.rotate(axis, angle);
        this.translate(-this.tmpVec.x, -this.tmpVec.y, -this.tmpVec.z);
    }
    
    public void transform(final Matrix4 transform) {
        this.position.mul(transform);
        this.rotate(transform);
    }
    
    public void translate(final float x, final float y, final float z) {
        this.position.add(x, y, z);
    }
    
    public void translate(final Vector3 vec) {
        this.position.add(vec);
    }
    
    public Vector3 unproject(final Vector3 screenCoords, final float viewportX, final float viewportY, final float viewportWidth, final float viewportHeight) {
        float x = screenCoords.x;
        float y = screenCoords.y;
        x -= viewportX;
        y = Gdx.graphics.getHeight() - y - 1.0f;
        y -= viewportY;
        screenCoords.x = 2.0f * x / viewportWidth - 1.0f;
        screenCoords.y = 2.0f * y / viewportHeight - 1.0f;
        screenCoords.z = 2.0f * screenCoords.z - 1.0f;
        screenCoords.prj(this.invProjectionView);
        return screenCoords;
    }
    
    public Vector3 unproject(final Vector3 screenCoords) {
        this.unproject(screenCoords, 0.0f, 0.0f, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
        return screenCoords;
    }
    
    public Vector3 project(final Vector3 worldCoords) {
        this.project(worldCoords, 0.0f, 0.0f, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
        return worldCoords;
    }
    
    public Vector3 project(final Vector3 worldCoords, final float viewportX, final float viewportY, final float viewportWidth, final float viewportHeight) {
        worldCoords.prj(this.combined);
        worldCoords.x = viewportWidth * (worldCoords.x + 1.0f) / 2.0f + viewportX;
        worldCoords.y = viewportHeight * (worldCoords.y + 1.0f) / 2.0f + viewportY;
        worldCoords.z = (worldCoords.z + 1.0f) / 2.0f;
        return worldCoords;
    }
    
    public Ray getPickRay(final float screenX, final float screenY, final float viewportX, final float viewportY, final float viewportWidth, final float viewportHeight) {
        this.unproject(this.ray.origin.set(screenX, screenY, 0.0f), viewportX, viewportY, viewportWidth, viewportHeight);
        this.unproject(this.ray.direction.set(screenX, screenY, 1.0f), viewportX, viewportY, viewportWidth, viewportHeight);
        this.ray.direction.sub(this.ray.origin).nor();
        return this.ray;
    }
    
    public Ray getPickRay(final float screenX, final float screenY) {
        return this.getPickRay(screenX, screenY, 0.0f, 0.0f, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
    }
}
