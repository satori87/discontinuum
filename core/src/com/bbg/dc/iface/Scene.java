// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.iface;

import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import java.util.Iterator;
import java.util.ArrayList;
import org.lwjgl.input.Cursor;
import java.util.LinkedList;
import java.util.List;
import com.bbg.dc.DCGame;

public abstract class Scene
{
    public DCGame game;
    public List<Frame> frames;
    public List<Button> buttons;
    public List<Label> labels;
    public List<TextBox> textboxes;
    public List<Dialog> dialogs;
    public LinkedList<Window> windows;
    public boolean inWindow;
    public boolean dragging;
    public boolean canDrag;
    public boolean inputLocked;
    public boolean started;
    public Label lblStatus;
    public long statusStamp;
    public long startStamp;
    public int dialogX;
    public int dialogY;
    public Window bringToTop;
    public Dialog activeDialog;
    static Cursor emptyCursor;
    
    public abstract void buttonPressed(final int p0);
    
    public Scene() {
        this.windows = new LinkedList<Window>();
        this.inWindow = false;
        this.dragging = false;
        this.canDrag = false;
        this.inputLocked = false;
        this.started = false;
        this.statusStamp = 0L;
        this.startStamp = 0L;
        this.dialogX = 540;
        this.dialogY = 360;
        this.bringToTop = null;
        this.frames = new LinkedList<Frame>();
        this.buttons = new LinkedList<Button>();
        this.labels = new LinkedList<Label>();
        this.textboxes = new LinkedList<TextBox>();
        this.dialogs = new ArrayList<Dialog>();
        this.windows = new LinkedList<Window>();
    }
    
    public Scene(final DCGame game) {
        this();
        this.game = game;
    }
    
    public void render() {
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
        if (this.activeDialog != null) {
            this.activeDialog.render();
        }
        for (final Window w : this.windows) {
            w.render();
        }
        if (this.game.tick > this.statusStamp && this.lblStatus != null) {
            this.lblStatus.text = "";
        }
    }
    
    public static void setHWCursorVisible(final boolean visible) throws LWJGLException {
        if (Gdx.app.getType() != Application.ApplicationType.Desktop && Gdx.app instanceof LwjglApplication) {
            return;
        }
        if (Scene.emptyCursor == null) {
            if (!Mouse.isCreated()) {
                throw new LWJGLException("Could not create empty cursor before Mouse object is created");
            }
            final int min = Cursor.getMinCursorSize();
            final IntBuffer tmp = BufferUtils.createIntBuffer(min * min);
            Scene.emptyCursor = new Cursor(min, min, min / 2, min / 2, 1, tmp, (IntBuffer)null);
        }
        if (Mouse.isInsideWindow()) {
            Mouse.setNativeCursor(visible ? null : Scene.emptyCursor);
        }
    }
    
    public boolean shifting() {
        return this.game.input.keyDown[59] || this.game.input.keyDown[60];
    }
    
    public boolean slashing() {
        return this.game.input.keyDown[76];
    }
    
    public boolean controlling() {
        return this.game.input.keyDown[129] || this.game.input.keyDown[130];
    }
    
    public boolean alting() {
        return this.game.input.keyDown[57] || this.game.input.keyDown[58];
    }
    
    public static long playSound(final int s, final float vol, final float pitchF) {
        return -1L;
    }
    
    public static long playSound(final int s, final float vol, final float pitchF, final boolean loop) {
        return -1L;
    }
    
    public static long play3D(final int s, final float x1, final float y1, final float x, final float y, final int range, final float vol, final float pitchF, final boolean loop) {
        return -1L;
    }
    
    public static void updateSound(final long id, final int s, final float x, final float y, final float x1, final float y1, final int range, final float vol, final float pitchF, final boolean loop) {
    }
    
    public void bringToTop(final Window i) {
        this.bringToTop = i;
    }
    
    public void switchTo() {
        if (!this.started) {
            this.start();
        }
    }
    
    public void switchFrom() {
    }
    
    public void update(final long tick) {
        if (!this.started) {
            this.start();
        }
        try {
            setHWCursorVisible(false);
        }
        catch (LWJGLException e) {
            e.printStackTrace();
        }
        this.game.cursor = 0;
        this.inWindow = false;
        this.dragging = false;
        this.canDrag = false;
        for (final Window w : this.windows) {
            w.update(tick);
        }
        if (this.bringToTop != null) {
            this.windows.remove(this.bringToTop);
            this.windows.addLast(this.bringToTop);
            this.bringToTop = null;
        }
        if (this.inWindow) {
            this.game.input.wasMouseJustClicked[0] = false;
        }
        final List<Dialog> drops = new LinkedList<Dialog>();
        for (final Dialog d : this.dialogs) {
            if (!d.done) {
                if (d.active) {
                    d.update(tick);
                }
                else {
                    if (this.activeDialog != null || !d.timed || tick <= d.timeStamp) {
                        continue;
                    }
                    d.start(tick);
                }
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
        this.game.input.keyTyped.clear();
    }
    
    public void clear() {
        this.dialogs.clear();
        this.frames.clear();
        this.buttons.clear();
        this.labels.clear();
        this.textboxes.clear();
        this.windows.clear();
        this.activeDialog = null;
    }
    
    public void start() {
        this.clear();
        this.startStamp = this.game.tick;
        this.activeDialog = null;
        for (final Dialog d : this.dialogs) {
            if (d.timed) {
                d.timeStamp = this.startStamp + d.time;
            }
        }
        this.started = true;
    }
    
    public Dialog addDialog(final int id, final int nextType, final int next, final int pic, final int nextTime, final String text) {
        final Dialog d = new Dialog(this.game, id, nextType, next, nextTime, pic, 600, text);
        d.x = this.dialogX;
        d.y = this.dialogY;
        this.dialogs.add(id, d);
        return d;
    }
    
    public List<Button> addButtons(final boolean centered, final boolean updown, final int type, final int x, final int y, final int width, final int height, final int padding, final String[] text, final int[] ids) {
        final List<Button> bl = new LinkedList<Button>();
        final int n = text.length;
        int bX = 0;
        int bY = 0;
        if (centered) {
            if (updown) {
                bX = x;
                bY = y - padding / 2 - height / 2 - (n - 1) / 2 * (padding + height);
            }
            else {
                bX = x - padding / 2 - width / 2 - (n - 1) / 2 * (padding + width);
                bY = y;
            }
        }
        else {
            bX = x;
            bY = y;
        }
        if (n % 2 != 0) {
            if (updown) {
                bY += (padding + height) / 2;
            }
            else {
                bX += (padding + width) / 2;
            }
        }
        for (int c = 0; c < n; ++c) {
            final Button b = new Button(this.game, ids[c], type, bX, bY, width, height, text[c]);
            bl.add(b);
            this.buttons.add(b);
            if (updown) {
                bY += height + padding;
            }
            else {
                bX += width + padding;
            }
        }
        return bl;
    }
}
