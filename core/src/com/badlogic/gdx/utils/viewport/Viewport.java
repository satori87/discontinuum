// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.viewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Camera;

public abstract class Viewport
{
    private Camera camera;
    private float worldWidth;
    private float worldHeight;
    private int screenX;
    private int screenY;
    private int screenWidth;
    private int screenHeight;
    private final Vector3 tmp;
    
    public Viewport() {
        this.tmp = new Vector3();
    }
    
    public void apply() {
        this.apply(false);
    }
    
    public void apply(final boolean centerCamera) {
        HdpiUtils.glViewport(this.screenX, this.screenY, this.screenWidth, this.screenHeight);
        this.camera.viewportWidth = this.worldWidth;
        this.camera.viewportHeight = this.worldHeight;
        if (centerCamera) {
            this.camera.position.set(this.worldWidth / 2.0f, this.worldHeight / 2.0f, 0.0f);
        }
        this.camera.update();
    }
    
    public final void update(final int screenWidth, final int screenHeight) {
        this.update(screenWidth, screenHeight, false);
    }
    
    public void update(final int screenWidth, final int screenHeight, final boolean centerCamera) {
        this.apply(centerCamera);
    }
    
    public Vector2 unproject(final Vector2 screenCoords) {
        this.tmp.set(screenCoords.x, screenCoords.y, 1.0f);
        this.camera.unproject(this.tmp, (float)this.screenX, (float)this.screenY, (float)this.screenWidth, (float)this.screenHeight);
        screenCoords.set(this.tmp.x, this.tmp.y);
        return screenCoords;
    }
    
    public Vector2 project(final Vector2 worldCoords) {
        this.tmp.set(worldCoords.x, worldCoords.y, 1.0f);
        this.camera.project(this.tmp, (float)this.screenX, (float)this.screenY, (float)this.screenWidth, (float)this.screenHeight);
        worldCoords.set(this.tmp.x, this.tmp.y);
        return worldCoords;
    }
    
    public Vector3 unproject(final Vector3 screenCoords) {
        this.camera.unproject(screenCoords, (float)this.screenX, (float)this.screenY, (float)this.screenWidth, (float)this.screenHeight);
        return screenCoords;
    }
    
    public Vector3 project(final Vector3 worldCoords) {
        this.camera.project(worldCoords, (float)this.screenX, (float)this.screenY, (float)this.screenWidth, (float)this.screenHeight);
        return worldCoords;
    }
    
    public Ray getPickRay(final float screenX, final float screenY) {
        return this.camera.getPickRay(screenX, screenY, (float)this.screenX, (float)this.screenY, (float)this.screenWidth, (float)this.screenHeight);
    }
    
    public void calculateScissors(final Matrix4 batchTransform, final Rectangle area, final Rectangle scissor) {
        ScissorStack.calculateScissors(this.camera, (float)this.screenX, (float)this.screenY, (float)this.screenWidth, (float)this.screenHeight, batchTransform, area, scissor);
    }
    
    public Vector2 toScreenCoordinates(final Vector2 worldCoords, final Matrix4 transformMatrix) {
        this.tmp.set(worldCoords.x, worldCoords.y, 0.0f);
        this.tmp.mul(transformMatrix);
        this.camera.project(this.tmp);
        this.tmp.y = Gdx.graphics.getHeight() - this.tmp.y;
        worldCoords.x = this.tmp.x;
        worldCoords.y = this.tmp.y;
        return worldCoords;
    }
    
    public Camera getCamera() {
        return this.camera;
    }
    
    public void setCamera(final Camera camera) {
        this.camera = camera;
    }
    
    public float getWorldWidth() {
        return this.worldWidth;
    }
    
    public void setWorldWidth(final float worldWidth) {
        this.worldWidth = worldWidth;
    }
    
    public float getWorldHeight() {
        return this.worldHeight;
    }
    
    public void setWorldHeight(final float worldHeight) {
        this.worldHeight = worldHeight;
    }
    
    public void setWorldSize(final float worldWidth, final float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }
    
    public int getScreenX() {
        return this.screenX;
    }
    
    public void setScreenX(final int screenX) {
        this.screenX = screenX;
    }
    
    public int getScreenY() {
        return this.screenY;
    }
    
    public void setScreenY(final int screenY) {
        this.screenY = screenY;
    }
    
    public int getScreenWidth() {
        return this.screenWidth;
    }
    
    public void setScreenWidth(final int screenWidth) {
        this.screenWidth = screenWidth;
    }
    
    public int getScreenHeight() {
        return this.screenHeight;
    }
    
    public void setScreenHeight(final int screenHeight) {
        this.screenHeight = screenHeight;
    }
    
    public void setScreenPosition(final int screenX, final int screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
    }
    
    public void setScreenSize(final int screenWidth, final int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }
    
    public void setScreenBounds(final int screenX, final int screenY, final int screenWidth, final int screenHeight) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }
    
    public int getLeftGutterWidth() {
        return this.screenX;
    }
    
    public int getRightGutterX() {
        return this.screenX + this.screenWidth;
    }
    
    public int getRightGutterWidth() {
        return Gdx.graphics.getWidth() - (this.screenX + this.screenWidth);
    }
    
    public int getBottomGutterHeight() {
        return this.screenY;
    }
    
    public int getTopGutterY() {
        return this.screenY + this.screenHeight;
    }
    
    public int getTopGutterHeight() {
        return Gdx.graphics.getHeight() - (this.screenY + this.screenHeight);
    }
}
