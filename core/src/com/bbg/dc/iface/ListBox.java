// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.iface;

import java.util.Iterator;
import com.bbg.dc.AssetLoader;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;
import com.bbg.dc.DCGame;

public class ListBox
{
    DCGame game;
    public int width;
    public int height;
    public int y;
    public int x;
    public int id;
    public int type;
    public int sel;
    public int c;
    public String[] text;
    public int[] data;
    public Frame frame;
    public List<Label> lbl;
    
    public ListBox(final DCGame game, final int type, final int id, final int x, final int y, final int width, final int height, final String[] text, final int[] data) {
        this.width = 0;
        this.height = 0;
        this.y = 0;
        this.x = 0;
        this.id = 0;
        this.type = 0;
        this.sel = 0;
        this.c = 0;
        this.game = game;
        this.x = x;
        this.y = y;
        this.type = type;
        this.id = id;
        this.width = width;
        this.height = height / 20 * 20 + 10;
        this.data = data;
        this.text = text;
        this.c = text.length;
    }
    
    public void start() {
        this.frame = new Frame(this.game, this.x, this.y, this.width, this.height, false, false);
        this.lbl = new ArrayList<Label>();
        int lowest = this.c;
        if (this.height / 20 < lowest) {
            lowest = this.height / 20;
        }
        for (int i = 0; i < lowest; ++i) {
            this.lbl.add(new Label(this.game, this.x + 14, this.y + 6 + i * 20, 1.0f, this.text[i], Color.WHITE, false));
        }
    }
    
    public void update(final long tick) {
        if (this.game.scene.inputLocked) {
            return;
        }
        int ns = 0;
        final int mX = this.game.input.getMouseX();
        final int mY = this.game.input.getMouseY();
        if (Button.inBox(mX, mY, this.x, this.x + this.width, this.y + 5, this.y + this.height - 5) && this.game.input.mouseDown[0]) {
            ns = (mY - this.y - 6) / 20;
            if (ns < this.c) {
                this.sel = ns;
            }
        }
    }
    
    public void render() {
        this.frame.render();
        for (int i = 0; i < this.height / 20; ++i) {
            if (i == this.sel) {
                for (int dx = 0; dx < this.width - 10; dx += 2) {
                    this.game.batcher.draw(AssetLoader.frameTex, (float)(dx + this.x + 5), (float)(this.y + 5 + i * 20), 2.0f, 20.0f, 214, 42, 2, 20, false, true);
                }
            }
        }
        for (final Label l : this.lbl) {
            l.render();
        }
    }
}
