// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;

public class DrawTask
{
    DCGame game;
    public boolean centered;
    public boolean flipX;
    public boolean flipY;
    public Texture texture;
    public TextureRegion region;
    public int type;
    public int font;
    public int x;
    public int y;
    public int srcX;
    public int srcY;
    public int width;
    public int height;
    public int srcWidth;
    public int srcHeight;
    public float rotation;
    public float scale;
    public Color col;
    public String text;
    
    public DrawTask(final DCGame game, final Texture texture, final int x, final int y, final int width, final int height, final int srcX, final int srcY, final int srcWidth, final int srcHeight, final boolean flipX, final boolean flipY, final Color col) {
        this.col = Color.WHITE;
        this.game = game;
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.srcX = srcX;
        this.srcY = srcY;
        this.srcWidth = srcWidth;
        this.srcHeight = srcHeight;
        this.flipX = flipX;
        this.flipY = flipY;
        this.col = col;
        this.type = 1;
    }
    
    public DrawTask(final DCGame game, final TextureRegion region, final int x, final int y, final boolean centered, final float rotation, final float scale, final Color col) {
        this.col = Color.WHITE;
        this.game = game;
        this.region = region;
        this.x = x;
        this.y = y;
        this.centered = centered;
        this.rotation = rotation;
        this.scale = scale;
        this.col = col;
        this.type = 0;
    }
    
    public DrawTask(final DCGame game, final int font, final int x, final int y, final String text, final boolean centered, final float scale, final Color col) {
        this.col = Color.WHITE;
        this.game = game;
        this.font = font;
        this.x = x;
        this.y = y;
        this.text = text;
        this.centered = centered;
        this.scale = scale;
        this.col = col;
        this.type = 2;
    }
    
    public void setShaderDatas(final int s, final float d) {
    }
    
    public void render() {
        switch (this.type) {
            case 0: {
                this.game.drawRegion(this.region, this.x, this.y, this.centered, this.rotation, this.scale);
                break;
            }
            case 1: {
                this.game.draw(this.texture, this.x, this.y, this.width, this.height, this.srcX, this.srcY, this.flipX, this.flipY);
                break;
            }
            case 2: {
                this.game.drawFont(this.font, this.x, this.y, this.text, this.centered, this.scale);
                break;
            }
        }
    }
}
