// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.iface;

import java.util.Iterator;
import com.badlogic.gdx.graphics.Color;
import com.bbg.dc.DCGame;

public class Label
{
    DCGame game;
    public int x;
    public int y;
    public boolean visible;
    public boolean blinking;
    public Color blinkCol;
    public boolean blink;
    public int blinkterval;
    public long blinkStamp;
    public boolean centered;
    public boolean wrap;
    public int wrapw;
    public String text;
    public float scale;
    public Color color;
    public boolean abs;
    
    public Label(final DCGame game, final int x, final int y, final float scale, final String text, final Color color, final boolean centered) {
        this.x = 0;
        this.y = 0;
        this.visible = true;
        this.blinking = false;
        this.blinkCol = Color.RED;
        this.blink = false;
        this.blinkterval = 300;
        this.blinkStamp = 0L;
        this.centered = false;
        this.wrap = false;
        this.wrapw = 0;
        this.text = "bunbun";
        this.scale = 1.0f;
        this.color = Color.WHITE;
        this.abs = false;
        this.game = game;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.color = color;
        this.text = text;
        this.centered = centered;
    }
    
    public void blink(final Color c, final int b) {
        this.blinkCol = c;
        this.blinking = true;
        this.blinkterval = b;
    }
    
    public void render() {
        if (!this.visible) {
            return;
        }
        Color c = this.color;
        if (this.blinking && this.blink) {
            c = this.blinkCol;
        }
        final int modX = this.game.getCamX() - this.game.width / 2;
        final int modY = this.game.getCamY() - this.game.height / 2;
        if (this.wrap) {
            int u = 0;
            for (final String b : Dialog.wrapText(2.0f, this.wrapw, this.text)) {
                this.game.drawFont(0, this.x + modX, modY + u * 30, b, false, this.scale, c);
                ++u;
            }
        }
        else {
            this.game.drawFont(0, this.x + modX, this.y + modY, this.text, this.centered, this.scale, c);
        }
    }
    
    public void update(final long tick) {
        if (tick > this.blinkStamp) {
            this.blink = !this.blink;
            this.blinkStamp = tick + this.blinkterval;
        }
    }
}
