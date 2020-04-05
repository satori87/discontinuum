// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.mapeffects;

import com.bbg.dc.DCGame;
import com.bbg.dc.Entity;
import com.badlogic.gdx.graphics.Color;

public class Floater
{
    public float x;
    public float y;
    public float initY;
    public float speed;
    public String text;
    public Color col;
    public Color fadeCol;
    public boolean active;
    public float alpha;
    public int dist;
    public int fadedist;
    public long aliveAt;
    public Entity e;
    
    public Floater(final Entity e, final int x, final int y, final String text, final Color col, final long aliveAt) {
        this.x = 0.0f;
        this.y = 0.0f;
        this.initY = 0.0f;
        this.speed = 0.5f;
        this.text = "";
        this.col = Color.WHITE;
        this.fadeCol = Color.WHITE;
        this.active = true;
        this.alpha = 1.0f;
        this.dist = 32;
        this.fadedist = 18;
        this.aliveAt = 0L;
        this.e = null;
        this.x = (float)x;
        this.y = (float)y;
        this.initY = (float)y;
        this.text = text;
        this.col = col;
        this.aliveAt = aliveAt;
        this.e = e;
    }
    
    public void update(final long tick) {
        if (tick < this.aliveAt) {
            return;
        }
        this.y -= this.speed;
        final float diff = this.initY - this.y;
        if (diff >= this.dist) {
            this.active = false;
        }
        else if (diff >= this.fadedist) {
            this.alpha -= this.speed / diff;
        }
    }
    
    public void render(final DCGame game) {
        if (game.tick < this.aliveAt || !this.active) {
            return;
        }
        this.col.a = this.alpha;
        game.drawFont(0, this.e.x + Math.round(this.x), this.e.y + Math.round(this.y), this.text, true, 1.0f, this.col);
    }
}
