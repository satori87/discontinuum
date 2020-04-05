// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.scenes;

import java.util.Iterator;
import com.bbg.dc.iface.TextBox;
import com.bbg.dc.iface.Frame;
import com.badlogic.gdx.graphics.Color;
import com.bbg.dc.DCGame;
import com.bbg.dc.iface.Label;
import com.bbg.dc.iface.Button;
import com.bbg.dc.iface.Scene;

public class MapOptions extends Scene
{
    public int curMap;
    public Button indoors;
    public Button fog;
    Label ambient;
    int amb;
    
    public MapOptions(final DCGame game) {
        this.curMap = 0;
        this.amb = 0;
        this.game = game;
    }
    
    @Override
    public void start() {
        super.start();
        this.labels.add(new Label(this.game, 512, 120, 2.0f, "Map Name", Color.WHITE, true));
        this.frames.add(new Frame(this.game, 512, 384, 800, 600, true, true));
        this.buttons.add(new Button(this.game, 1, 1, 830, 640, 128, 36, "Ok"));
        this.buttons.add(new Button(this.game, 2, 1, 180, 640, 128, 36, "Cancel"));
        this.labels.add(new Label(this.game, 330, 260, 2.0f, "Exit Up", Color.WHITE, true));
        this.labels.add(new Label(this.game, 330, 320, 2.0f, "Exit Left", Color.WHITE, true));
        this.labels.add(new Label(this.game, 330, 380, 2.0f, "Exit Down", Color.WHITE, true));
        this.labels.add(new Label(this.game, 330, 440, 2.0f, "Exit Right", Color.WHITE, true));
        this.textboxes.add(new TextBox(this.game, 0, 20, true, 512, 140, 600));
        this.textboxes.add(new TextBox(this.game, 1, 3, false, 512, 220, 200));
        this.textboxes.add(new TextBox(this.game, 2, 3, false, 512, 280, 200));
        this.textboxes.add(new TextBox(this.game, 3, 3, false, 512, 340, 200));
        this.textboxes.add(new TextBox(this.game, 4, 3, false, 512, 400, 200));
        this.textboxes.get(1).text = new StringBuilder().append(this.game.mapScene.map[this.curMap].data[1]).toString();
        this.textboxes.get(2).text = new StringBuilder().append(this.game.mapScene.map[this.curMap].data[2]).toString();
        this.textboxes.get(3).text = new StringBuilder().append(this.game.mapScene.map[this.curMap].data[3]).toString();
        this.textboxes.get(4).text = new StringBuilder().append(this.game.mapScene.map[this.curMap].data[4]).toString();
        this.indoors = new Button(this.game, 3, 2, 200, 550, 128, 36, "Indoors");
        this.indoors.toggle = this.game.mapScene.map[this.curMap].flags[0];
        this.fog = new Button(this.game, 10, 2, 200, 590, 128, 36, "Fog");
        this.fog.toggle = this.game.mapScene.map[this.curMap].flags[1];
        this.amb = this.game.mapScene.map[this.curMap].data[0];
        this.buttons.add(this.indoors);
        this.buttons.add(this.fog);
        this.ambient = new Label(this.game, 450, 550, 1.0f, "Alpha", Color.WHITE, true);
        this.buttons.add(new Button(this.game, 4, 1, 350, 550, 80, 32, "Prev"));
        this.buttons.add(new Button(this.game, 5, 1, 550, 550, 80, 32, "Next"));
        this.labels.add(this.ambient);
        this.amb = this.game.mapScene.map[this.curMap].data[0];
    }
    
    @Override
    public void switchTo() {
        super.switchTo();
        this.indoors.toggle = this.game.mapScene.map[this.curMap].flags[0];
        this.fog.toggle = this.game.mapScene.map[this.curMap].flags[1];
        this.textboxes.get(0).text = this.game.mapScene.map[this.curMap].name;
        this.amb = this.game.mapScene.map[this.curMap].data[0];
        this.textboxes.get(1).text = new StringBuilder().append(this.game.mapScene.map[this.curMap].data[1]).toString();
        this.textboxes.get(2).text = new StringBuilder().append(this.game.mapScene.map[this.curMap].data[2]).toString();
        this.textboxes.get(3).text = new StringBuilder().append(this.game.mapScene.map[this.curMap].data[3]).toString();
        this.textboxes.get(4).text = new StringBuilder().append(this.game.mapScene.map[this.curMap].data[4]).toString();
    }
    
    @Override
    public void buttonPressed(final int id) {
        switch (id) {
            case 1: {
                this.game.mapScene.map[this.curMap].name = this.textboxes.get(0).text;
                this.game.mapScene.map[this.curMap].flags[0] = this.indoors.toggle;
                this.game.mapScene.map[this.curMap].flags[1] = this.fog.toggle;
                this.game.mapScene.map[this.curMap].data[0] = this.amb;
                for (int i = 0; i < 4; ++i) {
                    final int a = Integer.parseInt(this.textboxes.get(i + 1).text);
                    if (a >= 0 && a < 2000) {
                        this.game.mapScene.map[this.curMap].data[i + 1] = a;
                    }
                    else {
                        this.game.mapScene.map[this.curMap].data[i + 1] = 0;
                    }
                }
                this.game.changeScene(this.game.mapScene);
                break;
            }
            case 2: {
                this.game.changeScene(this.game.mapScene);
            }
            case 4: {
                --this.amb;
                if (this.amb < 0) {
                    this.amb = 0;
                    break;
                }
                break;
            }
            case 5: {
                ++this.amb;
                if (this.amb > 255) {
                    this.amb = 255;
                    break;
                }
                break;
            }
        }
    }
    
    public void buttonReleased(final int id) {
    }
    
    @Override
    public void update(final long tick) {
        super.update(tick);
        this.ambient.text = "Alpha: " + this.amb;
        for (final Button b : this.buttons) {
            final boolean sel = b.sel;
            if (b.sel && tick - b.selDown > 300L) {
                System.out.println(b.id);
                this.buttonPressed(b.id);
                this.buttonPressed(b.id);
                this.buttonPressed(b.id);
                this.buttonPressed(b.id);
                this.buttonPressed(b.id);
            }
        }
    }
    
    @Override
    public void render() {
        super.render();
    }
}
