// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.scenes;

import com.badlogic.gdx.Gdx;
import com.bbg.dc.DCGame;
import com.badlogic.gdx.Graphics;
import com.bbg.dc.iface.Button;
import com.bbg.dc.iface.ListBox;
import com.bbg.dc.iface.Frame;
import com.bbg.dc.iface.Scene;

public class GraphicsScene extends Scene
{
    Frame frame;
    ListBox list;
    Button listEdit;
    Button listBack;
    String[] names;
    int[] data;
    Graphics.Monitor mon;
    Graphics.DisplayMode[] modes;
    String[] modNames;
    int[] modData;
    boolean hideWrongModes;
    int validModeCount;
    Button hideModes;
    
    public GraphicsScene(final DCGame game) {
        super(game);
        this.hideWrongModes = true;
        this.validModeCount = 0;
    }
    
    @Override
    public void start() {
        super.start();
        this.buttons.add(new Button(this.game, 101, 1, 400, 565, 200, 36, "Confirm"));
        this.checkModes();
    }
    
    private void checkModes() {
        this.mon = Gdx.graphics.getMonitor();
        this.modes = Gdx.graphics.getDisplayModes(this.mon);
        final float sr = 0.0f;
        float r = 0.0f;
        this.names = new String[this.modes.length];
        this.data = new int[this.modes.length];
        this.validModeCount = 0;
        for (int i = 0; i < this.modes.length; ++i) {
            this.names[i] = "";
            r = this.modes[i].width / (float)this.modes[i].height;
            if (sr == r || !this.hideWrongModes) {
                this.names[i] = String.valueOf(Integer.toString(this.modes[i].width)) + "x" + Integer.toString(this.modes[i].height) + " " + Integer.toString(this.modes[i].refreshRate) + "hz " + Integer.toString(this.modes[i].bitsPerPixel) + "pp";
                this.data[i] = i;
                ++this.validModeCount;
            }
            else {
                this.data[i] = -1;
            }
        }
        this.modNames = new String[this.validModeCount];
        this.modData = new int[this.validModeCount];
        int d = 0;
        for (int j = 0; j < this.modes.length; ++j) {
            if (this.data[j] >= 0) {
                this.modNames[d] = "hey";
                this.modNames[d] = this.names[j];
                this.modData[d] = this.data[j];
                ++d;
            }
        }
        (this.list = new ListBox(this.game, 1, 0, 100, 20, 600, 500, this.modNames, this.modData)).start();
    }
    
    @Override
    public void update(final long tick) {
        this.list.update(tick);
        super.update(tick);
    }
    
    @Override
    public void render() {
        this.list.render();
        super.render();
    }
    
    @Override
    public void buttonPressed(final int id) {
        if (id == 100) {
            this.hideWrongModes = !this.hideWrongModes;
            this.checkModes();
        }
    }
}
