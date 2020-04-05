// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.InputAdapter;

public class FirstPersonCameraController extends InputAdapter
{
    private final Camera camera;
    private final IntIntMap keys;
    private int STRAFE_LEFT;
    private int STRAFE_RIGHT;
    private int FORWARD;
    private int BACKWARD;
    private int UP;
    private int DOWN;
    private float velocity;
    private float degreesPerPixel;
    private final Vector3 tmp;
    
    public FirstPersonCameraController(final Camera camera) {
        this.keys = new IntIntMap();
        this.STRAFE_LEFT = 29;
        this.STRAFE_RIGHT = 32;
        this.FORWARD = 51;
        this.BACKWARD = 47;
        this.UP = 45;
        this.DOWN = 33;
        this.velocity = 5.0f;
        this.degreesPerPixel = 0.5f;
        this.tmp = new Vector3();
        this.camera = camera;
    }
    
    @Override
    public boolean keyDown(final int keycode) {
        this.keys.put(keycode, keycode);
        return true;
    }
    
    @Override
    public boolean keyUp(final int keycode) {
        this.keys.remove(keycode, 0);
        return true;
    }
    
    public void setVelocity(final float velocity) {
        this.velocity = velocity;
    }
    
    public void setDegreesPerPixel(final float degreesPerPixel) {
        this.degreesPerPixel = degreesPerPixel;
    }
    
    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        final float deltaX = -Gdx.input.getDeltaX() * this.degreesPerPixel;
        final float deltaY = -Gdx.input.getDeltaY() * this.degreesPerPixel;
        this.camera.direction.rotate(this.camera.up, deltaX);
        this.tmp.set(this.camera.direction).crs(this.camera.up).nor();
        this.camera.direction.rotate(this.tmp, deltaY);
        return true;
    }
    
    public void update() {
        this.update(Gdx.graphics.getDeltaTime());
    }
    
    public void update(final float deltaTime) {
        if (this.keys.containsKey(this.FORWARD)) {
            this.tmp.set(this.camera.direction).nor().scl(deltaTime * this.velocity);
            this.camera.position.add(this.tmp);
        }
        if (this.keys.containsKey(this.BACKWARD)) {
            this.tmp.set(this.camera.direction).nor().scl(-deltaTime * this.velocity);
            this.camera.position.add(this.tmp);
        }
        if (this.keys.containsKey(this.STRAFE_LEFT)) {
            this.tmp.set(this.camera.direction).crs(this.camera.up).nor().scl(-deltaTime * this.velocity);
            this.camera.position.add(this.tmp);
        }
        if (this.keys.containsKey(this.STRAFE_RIGHT)) {
            this.tmp.set(this.camera.direction).crs(this.camera.up).nor().scl(deltaTime * this.velocity);
            this.camera.position.add(this.tmp);
        }
        if (this.keys.containsKey(this.UP)) {
            this.tmp.set(this.camera.up).nor().scl(deltaTime * this.velocity);
            this.camera.position.add(this.tmp);
        }
        if (this.keys.containsKey(this.DOWN)) {
            this.tmp.set(this.camera.up).nor().scl(-deltaTime * this.velocity);
            this.camera.position.add(this.tmp);
        }
        this.camera.update(true);
    }
}
