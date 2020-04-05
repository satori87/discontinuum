// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.iface;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;
import com.bbg.dc.AssetLoader;
import com.bbg.dc.DCGame;

public class Button
{
    DCGame game;
    public int id;
    public boolean dialog;
    public long selDown;
    public boolean sel;
    public boolean toggle;
    public boolean disabled;
    public boolean visible;
    public int x;
    public int y;
    public int type;
    public int width;
    public int height;
    public String text;
    
    public Button(final DCGame game, final int id, final int type, final int x, final int y, final int width, final int height, final String text) {
        this.id = 0;
        this.dialog = false;
        this.selDown = 0L;
        this.sel = false;
        this.toggle = false;
        this.disabled = false;
        this.visible = true;
        this.x = 0;
        this.y = 0;
        this.type = 0;
        this.width = 32;
        this.height = 32;
        this.text = "button";
        this.game = game;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.type = type;
    }
    
    public static boolean inCentered(final int x, final int y, final int centerX, final int centerY, final int width, final int height) {
        final int topY = centerY - height / 2;
        final int bottomY = centerY + height / 2;
        final int leftX = centerX - width / 2;
        final int rightX = centerX + width / 2;
        return x > leftX && x < rightX && y > topY && y < bottomY;
    }
    
    public static boolean inBox(final int x, final int y, final int lowerX, final int upperX, final int lowerY, final int upperY) {
        return x >= lowerX && x <= upperX && y >= lowerY && y <= upperY;
    }
    
    public void update(final long tick) {
        if (this.disabled || this.game.scene.inputLocked) {
            return;
        }
        final int mX = this.game.input.getMouseX();
        final int mY = this.game.input.getMouseY();
        if (inCentered(mX, mY, this.x, this.y, this.width, this.height)) {
            if (this.game.input.mouseDown[0]) {
                if (this.game.input.wasMouseJustClicked[0]) {
                    if (this.type == 1) {
                        if (inCentered(this.game.input.mouseDownX[0], this.game.input.mouseDownY[0], this.x, this.y, this.width, this.height)) {
                            this.sel = true;
                            this.selDown = tick;
                            this.game.input.wasMouseJustClicked[0] = false;
                        }
                    }
                    else if (this.type == 0) {
                        this.sel = true;
                        this.game.input.wasMouseJustReleased[0] = false;
                        if (this.dialog) {
                            if (this.game.scene.activeDialog != null) {
                                this.game.scene.activeDialog.choose(this);
                            }
                        }
                        else {
                            this.game.scene.buttonPressed(this.id);
                        }
                    }
                    else if (this.type == 2) {
                        this.game.input.wasMouseJustClicked[0] = false;
                        this.toggle = !this.toggle;
                    }
                }
            }
            else if (this.game.input.wasMouseJustReleased[0]) {
                this.game.input.wasMouseJustReleased[0] = false;
                if (inCentered(this.game.input.mouseDownX[0], this.game.input.mouseDownY[0], this.x, this.y, this.width, this.height)) {
                    if (this.dialog) {
                        if (this.game.scene.activeDialog != null) {
                            this.game.scene.activeDialog.choose(this);
                        }
                    }
                    else {
                        this.game.scene.buttonPressed(this.id);
                    }
                }
            }
            else {
                this.sel = false;
            }
        }
        else {
            this.sel = false;
        }
    }
    
    public void render() {
        if (!this.visible) {
            return;
        }
        this.x -= this.width / 2;
        this.y -= this.height / 2;
        final TextureRegion[][] button = AssetLoader.button;
        int p;
        if (this.sel || this.disabled || this.toggle) {
            p = 1;
        }
        else {
            p = 0;
        }
        for (int a = 8; a < this.height - 8; a += 8) {
            for (int b = 8; b < this.width - 8; b += 8) {
                this.game.drawRegion(button[p][8], this.x + b, this.y + a, false, 0.0f, 1.0f);
            }
        }
        this.game.drawRegion(button[p][0], this.x, this.y, false, 0.0f, 1.0f);
        this.game.drawRegion(button[p][1], this.x + this.width - 8, this.y, false, 0.0f, 1.0f);
        this.game.drawRegion(button[p][2], this.x, this.y + this.height - 8, false, 0.0f, 1.0f);
        this.game.drawRegion(button[p][3], this.x + this.width - 8, this.y + this.height - 8, false, 0.0f, 1.0f);
        for (int b2 = 8; b2 < this.height - 8; b2 += 8) {
            this.game.drawRegion(button[p][4], this.x, this.y + b2, false, 0.0f, 1.0f);
        }
        for (int b2 = 8; b2 < this.height - 8; b2 += 8) {
            this.game.drawRegion(button[p][5], this.x + this.width - 8, this.y + b2, false, 0.0f, 1.0f);
        }
        for (int b2 = 8; b2 < this.width - 8; b2 += 8) {
            this.game.drawRegion(button[p][6], this.x + b2, this.y, false, 0.0f, 1.0f);
        }
        for (int b2 = 8; b2 < this.width - 8; b2 += 8) {
            this.game.drawRegion(button[p][7], this.x + b2, this.y + this.height - 8, false, 0.0f, 1.0f);
        }
        this.x += this.width / 2;
        this.y += this.height / 2;
        Color c = Color.WHITE;
        if (this.disabled) {
            c = Color.GRAY;
        }
        this.game.drawFont(0, this.x, this.y, this.text, true, this.height / 24.0f, c);
    }
}
