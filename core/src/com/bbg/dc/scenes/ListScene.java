// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.scenes;

import com.bbg.dc.DCGame;
import com.bbg.dc.iface.Button;
import com.bbg.dc.iface.ListBox;
import com.bbg.dc.iface.Frame;
import com.bbg.dc.iface.Scene;

public class ListScene extends Scene
{
    public int type;
    Frame mapFrame;
    ListBox mapList;
    Button listEdit;
    Button listBack;
    
    public ListScene(final DCGame game) {
        this.type = 0;
        this.game = game;
    }
    
    @Override
    public void start() {
        super.start();
        this.mapFrame = new Frame(this.game, 512, 384, 600, 700, true, true);
        this.listEdit = new Button(this.game, 2, 1, 750, 730, 80, 36, "Edit");
        this.listBack = new Button(this.game, 3, 1, 270, 730, 80, 36, "Back");
        this.buttons.add(this.listEdit);
        this.buttons.add(this.listBack);
    }
    
    @Override
    public void switchTo() {
        super.switchTo();
        int m = 0;
        switch (this.type) {
            case 0: {
                m = 100;
                break;
            }
            case 1: {
                m = 255;
                break;
            }
        }
        final String[] names = new String[m];
        final int[] data = new int[m];
        for (int i = 0; i < 100; ++i) {
            names[i] = String.valueOf(i) + ": ";
            switch (this.type) {
                case 0: {
                    names[i] = String.valueOf(names[i]) + this.game.mapScene.map[i].name;
                    break;
                }
                case 1: {
                    names[i] = String.valueOf(names[i]) + this.game.monsterScene.monster[i].name;
                    break;
                }
            }
            data[i] = i;
        }
        (this.mapList = new ListBox(this.game, 1, 0, 212, 20, 600, 500, names, data)).start();
    }
    
    @Override
    public void buttonPressed(final int id) {
        if (id == 2) {
            this.game.monsterScene.curMon = this.mapList.sel;
            this.game.monsterScene.fromScene = 0;
            this.game.changeScene(this.game.monsterScene);
        }
        else if (id == 3) {
            this.game.changeScene(this.game.mainMenu);
        }
    }
    
    @Override
    public void update(final long tick) {
        super.update(tick);
        this.mapList.update(this.game.tick);
    }
    
    @Override
    public void render() {
        super.render();
        this.mapList.render();
    }
}
