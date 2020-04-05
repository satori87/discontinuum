// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.iface;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bbg.dc.AssetLoader;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.bbg.dc.DCGame;

public class Frame
{
    DCGame game;
    public boolean useBackground;
    public boolean useFrame;
    public boolean centered;
    public int x;
    public int y;
    public int width;
    public int height;
    public List<Frame> frames;
    public List<Button> buttons;
    public List<Label> labels;
    public List<TextBox> textboxes;
    public List<Dialog> dialogs;
    public boolean visible;
    
    public Frame(final DCGame game, final int x, final int y, final int width, final int height, final boolean useBackground, final boolean centered) {
        this.useBackground = true;
        this.useFrame = true;
        this.centered = false;
        this.x = 0;
        this.y = 0;
        this.width = 32;
        this.height = 32;
        this.visible = true;
        this.game = game;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.useBackground = useBackground;
        this.useFrame = true;
        this.centered = centered;
        this.frames = new LinkedList<Frame>();
        this.buttons = new LinkedList<Button>();
        this.labels = new LinkedList<Label>();
        this.textboxes = new LinkedList<TextBox>();
        this.dialogs = new ArrayList<Dialog>();
    }
    
    public void update(final long tick) {
        if (!this.visible) {
            return;
        }
        if (this.game.scene.inputLocked) {
            return;
        }
        final List<Dialog> drops = new LinkedList<Dialog>();
        for (final Dialog d : this.dialogs) {
            if (!d.done) {
                if (!d.active) {
                    continue;
                }
                d.update(tick);
            }
            else {
                drops.add(d);
            }
        }
        drops.clear();
        for (final Frame d2 : this.frames) {
            d2.update(tick);
        }
        for (final Button b : this.buttons) {
            b.update(tick);
        }
        for (final TextBox t : this.textboxes) {
            t.update(tick);
        }
    }
    
    public void render() {
        if (!this.visible) {
            return;
        }
        for (final Frame d : this.frames) {
            d.render();
        }
        for (final Button b : this.buttons) {
            b.render();
        }
        for (final Label l : this.labels) {
            l.render();
        }
        for (final TextBox t : this.textboxes) {
            t.render();
        }
        if (this.centered) {
            this.x -= this.width / 2;
            this.y -= this.height / 2;
        }
        final TextureRegion[] frame = AssetLoader.frame;
        if (this.useBackground) {
            for (int a = 0; a < this.height; ++a) {
                for (int b2 = 0; b2 < this.width; ++b2) {
                    this.game.drawRegion(frame[8], this.x + b2, this.y + a, false, 0.0f, 1.0f);
                }
            }
        }
        if (this.useFrame) {
            this.game.drawRegion(frame[0], this.x, this.y, false, 0.0f, 1.0f);
            this.game.drawRegion(frame[1], this.x + this.width - 32, this.y, false, 0.0f, 1.0f);
            this.game.drawRegion(frame[2], this.x, this.y + this.height - 32, false, 0.0f, 1.0f);
            this.game.drawRegion(frame[3], this.x + this.width - 32, this.y + this.height - 32, false, 0.0f, 1.0f);
            for (int b3 = 32; b3 <= this.height - 32; b3 += 32) {
                this.game.drawRegion(frame[4], this.x, this.y + b3, false, 0.0f, 1.0f);
            }
            for (int b3 = 32; b3 <= this.height - 32; b3 += 32) {
                this.game.drawRegion(frame[5], this.x + this.width - 32, this.y + b3, false, 0.0f, 1.0f);
            }
            for (int b3 = 32; b3 <= this.width - 32; b3 += 32) {
                this.game.drawRegion(frame[6], this.x + b3, this.y, false, 0.0f, 1.0f);
            }
            for (int b3 = 32; b3 <= this.width - 32; b3 += 32) {
                this.game.drawRegion(frame[7], this.x + b3, this.y + this.height - 32, false, 0.0f, 1.0f);
            }
        }
        if (this.centered) {
            this.x += this.width / 2;
            this.y += this.height / 2;
        }
    }
    
    public void clear(final long tick) {
        this.dialogs.clear();
        this.frames.clear();
        this.buttons.clear();
        this.labels.clear();
        this.textboxes.clear();
    }
}
