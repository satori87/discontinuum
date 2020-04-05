// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.bbg.dc.DCGame;
import com.bbg.dc.iface.Label;
import com.bbg.dc.windows.InvWin;
import com.bbg.dc.Player;
import com.bbg.dc.Map;
import com.bbg.dc.iface.Scene;

public class GameScene extends Scene
{
    public Map[] maps;
    public Player player;
    public Map map;
    InvWin[] invWin;
    public long hideStamp;
    public String hideText;
    Label sceneText;
    public Label fps;
    boolean firstRun;
    
    public GameScene(final DCGame game) {
        this.hideStamp = 0L;
        this.hideText = "";
        this.firstRun = true;
        this.game = game;
    }
    
    @Override
    public void start() {
        super.start();
        this.fps = new Label(this.game, 10, 10, 1.0f, "FPS:", Color.WHITE, false);
        this.fps.abs = true;
        this.sceneText = new Label(this.game, this.game.width / 2, this.game.height / 2, 2.0f, "", Color.WHITE, true);
        this.sceneText.abs = true;
        this.invWin = new InvWin[50];
        for (int i = 0; i < 5; ++i) {
            this.invWin[i] = new InvWin(this.game, 100 + i * 100, 100 + i * 100, 4, true);
        }
        this.labels.add(this.fps);
        this.maps = new Map[100];
        this.player = new Player(this.game);
        for (int i = 0; i < 100; ++i) {
            this.maps[i] = new Map(this.game, i);
        }
        this.map = this.maps[0];
    }
    
    public void switchMap(final int mapNum) {
        if (mapNum < 0) {
            return;
        }
        this.firstRun = false;
        final Map oldMap = this.map;
        this.map.part();
        (this.map = this.maps[mapNum]).join(oldMap);
        this.hideStamp = this.game.tick - 2000L;
        this.hideText = this.map.data.name;
    }
    
    @Override
    public void switchFrom() {
        super.switchFrom();
        if (this.map != null) {
            this.map.part();
        }
    }
    
    @Override
    public void buttonPressed(final int id) {
    }
    
    public void buttonReleased(final int id) {
    }
    
    @Override
    public void update(final long tick) {
        if (this.game.tick > this.hideStamp) {
            super.update(tick);
            if (!this.inWindow && !this.dragging) {
                this.processInput(tick);
            }
            this.map.update(tick);
            if (this.dragging) {
                this.game.cursor = 3;
            }
            else if (this.canDrag) {
                this.game.cursor = 2;
            }
        }
    }
    
    @Override
    public void render() {
        int cx = this.game.getCamX();
        int cy = this.game.getCamY();
        if (this.player.x > cx + 48) {
            cx = this.player.x - 48;
        }
        else if (this.player.x < cx - 48) {
            cx = this.player.x + 48;
        }
        if (this.player.y > cy + 48) {
            cy = this.player.y - 48;
        }
        else if (this.player.y < cy - 48) {
            cy = this.player.y + 48;
        }
        final int left = this.map.data.canExit(1) ? -64 : 0;
        final int right = this.map.data.canExit(3) ? 64 : 0;
        final int top = this.map.data.canExit(0) ? -64 : 0;
        final int bottom = this.map.data.canExit(2) ? 64 : 0;
        if (cx < this.game.width / 2 + left) {
            cx = this.game.width / 2 + left;
        }
        if (cy < this.game.height / 2 + top) {
            cy = this.game.height / 2 + top;
        }
        if (cx > 2048 - this.game.width / 2 + right) {
            cx = 2048 - this.game.width / 2 + right;
        }
        if (cy > 2048 - this.game.height / 2 + bottom) {
            cy = 2048 - this.game.height / 2 + bottom;
        }
        this.game.moveCameraTo(cx, cy);
        this.map.render();
        this.overlay();
        if (this.hideStamp < this.game.tick) {
            super.render();
        }
    }
    
    public void processInput(final long tick) {
        final boolean[] keyDown = this.game.input.keyDown;
        if (keyDown[247]) {
            keyDown[247] = false;
            this.game.mapScene.curMap = this.map.id;
            this.game.changeScene(this.game.mapScene);
            this.game.moveCameraTo(0, 0);
            this.game.mapScene.mapX = this.player.x / 32 - 11;
            this.game.mapScene.mapY = this.player.y / 32 - 11;
        }
    }
    
    public void overlay() {
        this.fps.text = "FPS: " + Gdx.graphics.getFramesPerSecond() + " / " + this.player.x / 32 + "," + this.player.y / 32;
        if (this.hideStamp > this.game.tick) {
            this.sceneText.text = this.hideText;
            this.sceneText.x = this.game.width / 2;
            this.sceneText.y = this.game.height / 2;
            this.sceneText.render();
        }
    }
}
