// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.mapeffects;

import com.bbg.dc.AssetLoader;
import com.badlogic.gdx.graphics.Color;
import com.bbg.dc.DCGame;

public class Blood
{
    DCGame game;
    public final float ACCELERATION = 400.0f;
    public int bloodType;
    public float destinationX;
    public float destinationY;
    public float moveX;
    public float moveY;
    public float speed;
    public int dir;
    public long diesAt;
    public float X;
    public float Y;
    public float rotation;
    public boolean willDieAt;
    public boolean aliveYet;
    public long aliveAt;
    public float XVelocity;
    public float YVelocity;
    public float xChange;
    public float yChange;
    public int width;
    public int height;
    public boolean foreground;
    public float initialY;
    public float fallDistance;
    public boolean remove;
    public Color col;
    public boolean moveLocked;
    public long lockStamp;
    public int bleeding;
    public long bleedStamp;
    
    public Blood(final DCGame game, final int type, final Color bloodCol, final float X, final float Y, final int delay, final int TTL, final float initialXVelocity, final float initialYVelocity, final float spread, float fallDistance) {
        this.bloodType = 0;
        this.destinationX = 0.0f;
        this.destinationY = 0.0f;
        this.moveX = 0.0f;
        this.moveY = 0.0f;
        this.speed = 0.0f;
        this.dir = 0;
        this.diesAt = 0L;
        this.X = 0.0f;
        this.Y = 0.0f;
        this.rotation = 0.0f;
        this.willDieAt = true;
        this.aliveYet = false;
        this.aliveAt = 0L;
        this.XVelocity = 0.0f;
        this.YVelocity = 0.0f;
        this.xChange = 0.0f;
        this.yChange = 0.0f;
        this.width = 0;
        this.height = 0;
        this.foreground = true;
        this.initialY = 0.0f;
        this.fallDistance = 0.0f;
        this.remove = false;
        this.col = Color.WHITE;
        this.moveLocked = false;
        this.lockStamp = 0L;
        this.bleeding = 0;
        this.bleedStamp = 0L;
        this.X = X;
        this.Y = Y;
        this.col = bloodCol;
        this.initialY = Y;
        this.rotation = (float)Math.random() * 360.0f;
        this.game = game;
        this.diesAt = game.tick + TTL;
        this.willDieAt = true;
        this.XVelocity = initialXVelocity;
        this.YVelocity = initialYVelocity * 1.5f;
        if (this.YVelocity == 0.0f) {
            this.YVelocity = 1.0f;
        }
        this.fallDistance = fallDistance;
        if (initialYVelocity < 0.0f) {
            fallDistance = -fallDistance;
        }
        fallDistance += 18.0f;
        if (delay > 0) {
            this.aliveAt = game.tick + delay;
            this.aliveYet = false;
        }
        this.bloodType = type;
        this.width = AssetLoader.getBloodRegion(type).getRegionWidth();
        this.height = AssetLoader.getBloodRegion(type).getRegionHeight();
    }
    
    public void calcMove(final float delta) {
        this.YVelocity += 400.0f * delta;
        this.xChange = this.XVelocity * delta;
        this.yChange = this.YVelocity * delta;
    }
    
    public void checkMove() {
    }
    
    public void move() {
        if (this.foreground) {
            this.X += this.xChange;
            this.Y += this.yChange;
        }
    }
    
    public void remove() {
        this.remove = true;
    }
    
    public void update(final float delta) {
        if (this.willDieAt && this.game.tick >= this.diesAt) {
            this.remove();
        }
        else if (this.aliveYet) {
            this.calcMove(delta);
            this.checkMove();
            if (!this.moveLocked) {
                this.move();
            }
            else if (this.game.tick > this.lockStamp) {
                this.moveLocked = false;
            }
            this.validatePosition();
        }
        else if (this.game.tick >= this.aliveAt) {
            this.aliveYet = true;
        }
    }
    
    public void setNewDestination(final float X, final float Y) {
        this.destinationX = X;
        this.destinationY = Y;
    }
    
    public void moveLock(final int duration) {
        this.moveLocked = true;
        this.lockStamp = this.game.tick + 100L;
    }
    
    public static int getDir(final float X, final float Y, final float xChange, final float yChange) {
        int dir = -1;
        if (Math.abs(xChange) > Math.abs(yChange)) {
            if (xChange > 0.0f) {
                dir = 3;
            }
            else if (xChange < 0.0f) {
                dir = 2;
            }
        }
        else if (yChange < 0.0f) {
            dir = 0;
        }
        else if (yChange > 0.0f) {
            dir = 1;
        }
        return dir;
    }
    
    public void validatePosition() {
        if (this.YVelocity > 0.0f && this.Y - this.initialY > this.fallDistance) {
            this.Y = this.initialY + this.fallDistance;
            this.foreground = false;
            this.XVelocity = 0.0f;
            this.YVelocity = 0.0f;
        }
    }
}
