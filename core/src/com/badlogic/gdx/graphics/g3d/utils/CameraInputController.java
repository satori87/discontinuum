// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.input.GestureDetector;

public class CameraInputController extends GestureDetector
{
    public int rotateButton;
    public float rotateAngle;
    public int translateButton;
    public float translateUnits;
    public int forwardButton;
    public int activateKey;
    protected boolean activatePressed;
    public boolean alwaysScroll;
    public float scrollFactor;
    public float pinchZoomFactor;
    public boolean autoUpdate;
    public Vector3 target;
    public boolean translateTarget;
    public boolean forwardTarget;
    public boolean scrollTarget;
    public int forwardKey;
    protected boolean forwardPressed;
    public int backwardKey;
    protected boolean backwardPressed;
    public int rotateRightKey;
    protected boolean rotateRightPressed;
    public int rotateLeftKey;
    protected boolean rotateLeftPressed;
    public Camera camera;
    protected int button;
    private float startX;
    private float startY;
    private final Vector3 tmpV1;
    private final Vector3 tmpV2;
    protected final CameraGestureListener gestureListener;
    private int touched;
    private boolean multiTouch;
    
    protected CameraInputController(final CameraGestureListener gestureListener, final Camera camera) {
        super(gestureListener);
        this.rotateButton = 0;
        this.rotateAngle = 360.0f;
        this.translateButton = 1;
        this.translateUnits = 10.0f;
        this.forwardButton = 2;
        this.activateKey = 0;
        this.alwaysScroll = true;
        this.scrollFactor = -0.1f;
        this.pinchZoomFactor = 10.0f;
        this.autoUpdate = true;
        this.target = new Vector3();
        this.translateTarget = true;
        this.forwardTarget = true;
        this.scrollTarget = false;
        this.forwardKey = 51;
        this.backwardKey = 47;
        this.rotateRightKey = 29;
        this.rotateLeftKey = 32;
        this.button = -1;
        this.tmpV1 = new Vector3();
        this.tmpV2 = new Vector3();
        this.gestureListener = gestureListener;
        this.gestureListener.controller = this;
        this.camera = camera;
    }
    
    public CameraInputController(final Camera camera) {
        this(new CameraGestureListener(), camera);
    }
    
    public void update() {
        if (this.rotateRightPressed || this.rotateLeftPressed || this.forwardPressed || this.backwardPressed) {
            final float delta = Gdx.graphics.getDeltaTime();
            if (this.rotateRightPressed) {
                this.camera.rotate(this.camera.up, -delta * this.rotateAngle);
            }
            if (this.rotateLeftPressed) {
                this.camera.rotate(this.camera.up, delta * this.rotateAngle);
            }
            if (this.forwardPressed) {
                this.camera.translate(this.tmpV1.set(this.camera.direction).scl(delta * this.translateUnits));
                if (this.forwardTarget) {
                    this.target.add(this.tmpV1);
                }
            }
            if (this.backwardPressed) {
                this.camera.translate(this.tmpV1.set(this.camera.direction).scl(-delta * this.translateUnits));
                if (this.forwardTarget) {
                    this.target.add(this.tmpV1);
                }
            }
            if (this.autoUpdate) {
                this.camera.update();
            }
        }
    }
    
    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        this.touched |= 1 << pointer;
        this.multiTouch = !MathUtils.isPowerOfTwo(this.touched);
        if (this.multiTouch) {
            this.button = -1;
        }
        else if (this.button < 0 && (this.activateKey == 0 || this.activatePressed)) {
            this.startX = (float)screenX;
            this.startY = (float)screenY;
            this.button = button;
        }
        return super.touchDown(screenX, screenY, pointer, button) || this.activateKey == 0 || this.activatePressed;
    }
    
    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        this.touched &= (-1 ^ 1 << pointer);
        this.multiTouch = !MathUtils.isPowerOfTwo(this.touched);
        if (button == this.button) {
            this.button = -1;
        }
        return super.touchUp(screenX, screenY, pointer, button) || this.activatePressed;
    }
    
    protected boolean process(final float deltaX, final float deltaY, final int button) {
        if (button == this.rotateButton) {
            this.tmpV1.set(this.camera.direction).crs(this.camera.up).y = 0.0f;
            this.camera.rotateAround(this.target, this.tmpV1.nor(), deltaY * this.rotateAngle);
            this.camera.rotateAround(this.target, Vector3.Y, deltaX * -this.rotateAngle);
        }
        else if (button == this.translateButton) {
            this.camera.translate(this.tmpV1.set(this.camera.direction).crs(this.camera.up).nor().scl(-deltaX * this.translateUnits));
            this.camera.translate(this.tmpV2.set(this.camera.up).scl(-deltaY * this.translateUnits));
            if (this.translateTarget) {
                this.target.add(this.tmpV1).add(this.tmpV2);
            }
        }
        else if (button == this.forwardButton) {
            this.camera.translate(this.tmpV1.set(this.camera.direction).scl(deltaY * this.translateUnits));
            if (this.forwardTarget) {
                this.target.add(this.tmpV1);
            }
        }
        if (this.autoUpdate) {
            this.camera.update();
        }
        return true;
    }
    
    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        final boolean result = super.touchDragged(screenX, screenY, pointer);
        if (result || this.button < 0) {
            return result;
        }
        final float deltaX = (screenX - this.startX) / Gdx.graphics.getWidth();
        final float deltaY = (this.startY - screenY) / Gdx.graphics.getHeight();
        this.startX = (float)screenX;
        this.startY = (float)screenY;
        return this.process(deltaX, deltaY, this.button);
    }
    
    @Override
    public boolean scrolled(final int amount) {
        return this.zoom(amount * this.scrollFactor * this.translateUnits);
    }
    
    public boolean zoom(final float amount) {
        if (!this.alwaysScroll && this.activateKey != 0 && !this.activatePressed) {
            return false;
        }
        this.camera.translate(this.tmpV1.set(this.camera.direction).scl(amount));
        if (this.scrollTarget) {
            this.target.add(this.tmpV1);
        }
        if (this.autoUpdate) {
            this.camera.update();
        }
        return true;
    }
    
    protected boolean pinchZoom(final float amount) {
        return this.zoom(this.pinchZoomFactor * amount);
    }
    
    @Override
    public boolean keyDown(final int keycode) {
        if (keycode == this.activateKey) {
            this.activatePressed = true;
        }
        if (keycode == this.forwardKey) {
            this.forwardPressed = true;
        }
        else if (keycode == this.backwardKey) {
            this.backwardPressed = true;
        }
        else if (keycode == this.rotateRightKey) {
            this.rotateRightPressed = true;
        }
        else if (keycode == this.rotateLeftKey) {
            this.rotateLeftPressed = true;
        }
        return false;
    }
    
    @Override
    public boolean keyUp(final int keycode) {
        if (keycode == this.activateKey) {
            this.activatePressed = false;
            this.button = -1;
        }
        if (keycode == this.forwardKey) {
            this.forwardPressed = false;
        }
        else if (keycode == this.backwardKey) {
            this.backwardPressed = false;
        }
        else if (keycode == this.rotateRightKey) {
            this.rotateRightPressed = false;
        }
        else if (keycode == this.rotateLeftKey) {
            this.rotateLeftPressed = false;
        }
        return false;
    }
    
    protected static class CameraGestureListener extends GestureAdapter
    {
        public CameraInputController controller;
        private float previousZoom;
        
        @Override
        public boolean touchDown(final float x, final float y, final int pointer, final int button) {
            this.previousZoom = 0.0f;
            return false;
        }
        
        @Override
        public boolean tap(final float x, final float y, final int count, final int button) {
            return false;
        }
        
        @Override
        public boolean longPress(final float x, final float y) {
            return false;
        }
        
        @Override
        public boolean fling(final float velocityX, final float velocityY, final int button) {
            return false;
        }
        
        @Override
        public boolean pan(final float x, final float y, final float deltaX, final float deltaY) {
            return false;
        }
        
        @Override
        public boolean zoom(final float initialDistance, final float distance) {
            final float newZoom = distance - initialDistance;
            final float amount = newZoom - this.previousZoom;
            this.previousZoom = newZoom;
            final float w = (float)Gdx.graphics.getWidth();
            final float h = (float)Gdx.graphics.getHeight();
            return this.controller.pinchZoom(amount / ((w > h) ? h : w));
        }
        
        @Override
        public boolean pinch(final Vector2 initialPointer1, final Vector2 initialPointer2, final Vector2 pointer1, final Vector2 pointer2) {
            return false;
        }
    }
}
