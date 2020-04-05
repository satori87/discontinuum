// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.iface;

import com.badlogic.gdx.graphics.Color;
import com.bbg.dc.AssetLoader;
import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.bbg.dc.DCGame;

public class TextBox
{
    DCGame game;
    public int id;
    public int x;
    public int y;
    public long tick;
    public boolean focus;
    public boolean blink;
    public long blinkStamp;
    public int max;
    public int width;
    public String text;
    
    public TextBox(final DCGame game, final int id, final int max, final boolean focus, final int x, final int y, final int width) {
        this.id = 0;
        this.x = 0;
        this.y = 0;
        this.tick = 0L;
        this.focus = false;
        this.blink = false;
        this.blinkStamp = 0L;
        this.max = 10;
        this.width = 32;
        this.text = "";
        this.game = game;
        this.max = max;
        this.focus = false;
        this.id = id;
        this.x = x - width / 2;
        this.y = y + 16;
        this.width = width;
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
        this.tick = tick;
        if (tick > this.blinkStamp) {
            this.blink = !this.blink;
            this.blinkStamp = tick + 400L;
        }
        if (this.game.scene.inputLocked) {
            return;
        }
        final int mX = this.game.input.getMouseX();
        final int mY = this.game.input.getMouseY();
        if (this.game.input.wasMouseJustClicked[0] && inBox(mX, mY, this.x, this.x + this.width, this.y - 7, this.y + 10 + 36)) {
            this.game.input.wasMouseJustClicked[0] = false;
            for (final TextBox t : this.game.scene.textboxes) {
                t.focus = false;
            }
            Gdx.input.setOnscreenKeyboardVisible(true);
            this.focus = true;
        }
        if (this.focus) {
            this.processKeyPress(this.max);
        }
    }
    
    public void processKeyPress(final int max) {
        int a = 0;
        for (final Character c : this.game.input.keyTyped) {
            a = c;
            switch (a) {
                case 32: {
                    if (this.text.length() >= max) {
                        return;
                    }
                    this.text = String.valueOf(this.text) + " ";
                    continue;
                }
                case 8: {
                    if (this.text.length() > 0) {
                        this.text = this.text.substring(0, this.text.length() - 1);
                        continue;
                    }
                    continue;
                }
                case 10: {
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    continue;
                }
                case 13: {
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    continue;
                }
                default: {
                    if (this.text.length() >= max) {
                        return;
                    }
                    if (a >= 33 && a <= 126) {
                        this.text = String.valueOf(this.text) + c;
                        continue;
                    }
                    continue;
                }
            }
        }
        this.game.input.keyTyped.clear();
    }
    
    public void render() {
        final int l = 0;
        this.game.drawRegion(AssetLoader.field[l][0], this.x, this.y, false, 0.0f, 2.0f);
        for (int b = 42; b < this.width - 42; b += 32) {
            this.game.drawRegion(AssetLoader.field[l][1], this.x + b, this.y, false, 0.0f, 2.0f);
        }
        this.game.drawRegion(AssetLoader.field[l][2], this.x + this.width - 42, this.y, false, 0.0f, 2.0f);
        this.game.drawFont(0, this.x + this.width / 2, this.y + 28, this.text, true, 2.0f, Color.WHITE);
        if (this.focus && this.blink) {
            this.game.drawFont(0, (int)(this.x + this.width / 2 + AssetLoader.getStringWidth(this.text, 1.0f, 0.0f, 1.0f)), this.y + 28, "|", true, 2.0f, Color.WHITE);
        }
    }
}
