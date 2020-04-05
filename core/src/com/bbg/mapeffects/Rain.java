// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.mapeffects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bbg.dc.AssetLoader;
import com.bbg.dc.DCGame;

public class Rain
{
    public int x;
    public int y;
    public int dy;
    public int layer;
    public int type;
    public long stamp;
    public boolean active;
    public boolean flip;
    
    public Rain(final DCGame game, final WeatherManager weatherMan) {
        this.x = 0;
        this.y = 0;
        this.dy = 0;
        this.layer = 0;
        this.type = 0;
        this.stamp = 0L;
        this.active = true;
        this.flip = true;
        this.x = game.getCamX() - game.width / 2 + AssetLoader.rndInt(game.width);
        this.y = game.getCamY() - game.height / 2 + AssetLoader.rndInt(game.height + 80 + 80) - 80;
        final int be = game.gameScene.player.y + game.height / 2 + 80 + 80;
        this.dy = this.y + AssetLoader.rndInt(be - this.y);
        this.type = AssetLoader.rndInt(2);
        int a = AssetLoader.rndInt(3);
        if (a == 0) {
            this.layer = 3;
        }
        else if (a == 1) {
            this.layer = 4;
        }
        else if (a == 2) {
            this.layer = 7;
        }
        a = AssetLoader.rndInt(2);
        if (a == 0) {
            this.flip = true;
        }
        else {
            this.flip = false;
        }
    }
    
    public void update(final long tick) {
        if (tick > this.stamp) {
            this.stamp = tick + 1L;
            if (this.type < 3) {
                this.y += 5;
                if (this.y >= this.dy || this.y >= 6144) {
                    this.type = 3;
                    this.stamp = tick + 300L;
                }
            }
            else {
                this.active = false;
            }
        }
    }
    
    public void render(final DCGame game) {
        if (!this.active) {
            return;
        }
        final TextureRegion tr = AssetLoader.getRainRegion(this.type);
        game.drawRegion(tr, this.x, this.y, false, 0.0f, 1.0f);
    }
}
