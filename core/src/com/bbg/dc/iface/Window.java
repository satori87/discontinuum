// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.iface;

import java.util.Iterator;
import com.bbg.dc.GameInput;
import com.bbg.dc.AssetLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.bbg.dc.DCGame;

public class Window
{
    public DCGame game;
    public int x;
    public int y;
    public boolean top;
    public String title;
    public int bg;
    public boolean useTop;
    public boolean visible;
    public int style;
    public boolean movable;
    public List<Frame> frames;
    public List<Button> buttons;
    public List<Label> labels;
    public List<TextBox> textboxes;
    public List<Dialog> dialogs;
    public boolean dragging;
    public int dragX;
    public int dragY;
    
    public Window(final DCGame game, final int x, final int y, final int bg, final boolean useTop) {
        this.x = 0;
        this.y = 0;
        this.top = true;
        this.title = "Window";
        this.bg = 0;
        this.useTop = true;
        this.visible = true;
        this.style = 0;
        this.movable = false;
        this.dragging = false;
        this.dragX = 0;
        this.dragY = 0;
        this.game = game;
        this.x = x;
        this.y = y;
        this.bg = bg;
        this.useTop = useTop;
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
        final GameInput in = this.game.input;
        int tx = in.getMouseX() - (this.x - 8) - 1;
        int ty = in.getMouseY() - (this.y - 50) - 1;
        if (Button.inBox(in.getMouseX(), in.getMouseY(), this.x, this.x + 264, this.y, this.y + 363)) {
            this.game.scene.inWindow = true;
        }
        else if (Button.inBox(in.getMouseX(), in.getMouseY(), this.x - 8, this.x - 8 + 274, this.y - 5, this.y - 5 + 379)) {
            this.game.scene.inWindow = true;
        }
        else if (Button.inBox(in.getMouseX(), in.getMouseY(), this.x - 8, this.x - 8 + 274, this.y - 50, this.y) && tx >= 0 && ty >= 0) {
            if (AssetLoader.topMask[1][tx][ty]) {
                this.game.scene.inWindow = true;
            }
            else if (AssetLoader.topMask[0][tx][ty]) {
                this.game.scene.canDrag = true;
                this.game.scene.inWindow = true;
            }
        }
        if (this.dragging || !this.game.scene.inputLocked) {
            if (in.mouseDown[0]) {
                if (Button.inBox(in.mouseDownX[0], in.mouseDownY[0], this.x, this.x + 264, this.y, this.y + 363)) {
                    System.out.println("insideframe");
                    this.game.scene.bringToTop(this);
                    if (this.dragging) {
                        System.out.println("keep draggin");
                        this.x = this.dragX + (in.getMouseX() - in.mouseDownX[0]);
                        this.y = this.dragY + (in.getMouseY() - in.mouseDownY[0]);
                    }
                }
                else if (Button.inBox(in.mouseDownX[0], in.mouseDownY[0], this.x - 8, this.x - 8 + 274, this.y - 5, this.y - 5 + 379)) {
                    this.game.scene.bringToTop(this);
                    if (this.dragging) {
                        System.out.println("keep draggin");
                        this.x = this.dragX + (in.getMouseX() - in.mouseDownX[0]);
                        this.y = this.dragY + (in.getMouseY() - in.mouseDownY[0]);
                    }
                    System.out.println("frameitself");
                }
                else if (Button.inBox(in.mouseDownX[0], in.mouseDownY[0], this.x - 8, this.x - 8 + 274, this.y - 50, this.y)) {
                    tx = in.mouseDownX[0] - (this.x - 8) - 1;
                    ty = in.mouseDownY[0] - (this.y - 50) - 1;
                    if (tx >= 0 && ty >= 0) {
                        if (AssetLoader.topMask[0][tx][ty]) {
                            this.game.scene.bringToTop(this);
                            if (in.wasMouseJustClicked[0]) {
                                in.wasMouseJustClicked[0] = false;
                                if (this.dragging) {
                                    System.out.println("You've done something impossible in Window.java");
                                }
                                else {
                                    System.out.println("lets drag");
                                    this.dragging = true;
                                    this.game.scene.inputLocked = true;
                                    this.dragX = this.x;
                                    this.dragY = this.y;
                                }
                            }
                            else if (this.dragging) {
                                System.out.println("keep draggin");
                                this.x = this.dragX + (in.getMouseX() - in.mouseDownX[0]);
                                this.y = this.dragY + (in.getMouseY() - in.mouseDownY[0]);
                            }
                        }
                        else if (AssetLoader.topMask[1][tx][ty]) {
                            this.visible = false;
                            return;
                        }
                    }
                }
                else if (this.dragging) {
                    System.out.println("keep draggin");
                    this.x = this.dragX + (in.getMouseX() - in.mouseDownX[0]);
                    this.y = this.dragY + (in.getMouseY() - in.mouseDownY[0]);
                }
            }
            else if (this.dragging) {
                System.out.println("beaniofin");
                this.dragging = false;
                this.game.scene.inputLocked = false;
                this.x = this.dragX + (in.getMouseX() - in.mouseDownX[0]);
                this.y = this.dragY + (in.getMouseY() - in.mouseDownY[0]);
            }
        }
        if (this.dragging) {
            this.game.scene.dragging = true;
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
        this.game.drawAbs(AssetLoader.iface[8], this.x - 8, this.y - 5, 0, 0, 274, 379);
        this.game.drawAbs(AssetLoader.iface[9], this.x - 8, this.y - 50, 0, 0, 274, 45);
        this.game.drawAbs(AssetLoader.iface[7], this.x, this.y, 0, 0, 264, 363);
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
    }
    
    public void clear(final long tick) {
        this.dialogs.clear();
        this.frames.clear();
        this.buttons.clear();
        this.labels.clear();
        this.textboxes.clear();
    }
}
