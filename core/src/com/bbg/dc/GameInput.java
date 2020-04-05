// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.InputProcessor;

public class GameInput implements InputProcessor
{
    DCGame game;
    public List<Integer> keyPress;
    public List<Character> keyTyped;
    public boolean[] keyDown;
    public long[] keyDownAt;
    public boolean[] mouseDown;
    public boolean[] wasMouseJustClicked;
    public boolean[] wasMouseJustReleased;
    public boolean wasMouseMoved;
    public int[] mouseDownX;
    public int[] mouseDownY;
    private int mouseX;
    private int mouseY;
    private int lastmx;
    private int lastmy;
    public int scroll;
    
    public GameInput(final DCGame game) {
        this.wasMouseMoved = false;
        this.mouseX = 0;
        this.mouseY = 0;
        this.lastmx = 0;
        this.lastmy = 0;
        this.scroll = 0;
        this.game = game;
        this.mouseDown = new boolean[40];
        this.wasMouseJustClicked = new boolean[40];
        this.wasMouseJustReleased = new boolean[40];
        this.mouseDownX = new int[40];
        this.mouseDownY = new int[40];
        for (int i = 0; i < 40; ++i) {
            this.mouseDown[i] = false;
            this.wasMouseJustClicked[i] = false;
            this.wasMouseJustReleased[i] = false;
            this.mouseDownX[i] = 0;
            this.mouseDownY[i] = 0;
        }
        this.keyPress = new ArrayList<Integer>();
        this.keyTyped = new ArrayList<Character>();
        this.keyDown = new boolean[256];
        this.keyDownAt = new long[256];
        for (int i = 0; i < 256; ++i) {
            this.keyDown[i] = false;
            this.keyDownAt[i] = 0L;
        }
    }
    
    public synchronized int getMouseX() {
        return this.mouseX;
    }
    
    public synchronized int getMouseY() {
        return this.mouseY;
    }
    
    @Override
    public boolean keyDown(final int keycode) {
        this.keyDown[keycode] = true;
        this.keyPress.add(keycode);
        this.keyDownAt[keycode] = this.game.tick;
        return false;
    }
    
    @Override
    public boolean keyUp(final int keycode) {
        return this.keyDown[keycode] = false;
    }
    
    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        this.mouseX = (int)(screenX / (float)Gdx.graphics.getWidth() * this.game.width);
        this.mouseY = (int)(screenY / (float)Gdx.graphics.getHeight() * this.game.height);
        this.mouseDown[button] = true;
        this.mouseDownX[button] = this.mouseX;
        this.mouseDownY[button] = this.mouseY;
        this.wasMouseJustClicked[button] = true;
        this.wasMouseJustReleased[button] = false;
        return true;
    }
    
    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        this.mouseX = (int)(screenX / (float)Gdx.graphics.getWidth() * this.game.width);
        this.mouseY = (int)(screenY / (float)Gdx.graphics.getHeight() * this.game.height);
        this.mouseDown[button] = false;
        this.wasMouseJustReleased[button] = true;
        return false;
    }
    
    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        this.mouseX = (int)(screenX / (float)Gdx.graphics.getWidth() * this.game.width);
        this.mouseY = (int)(screenY / (float)Gdx.graphics.getHeight() * this.game.height);
        return false;
    }
    
    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        this.mouseX = (int)(screenX / (float)Gdx.graphics.getWidth() * this.game.width);
        this.mouseY = (int)(screenY / (float)Gdx.graphics.getHeight() * this.game.height);
        if (this.mouseX != this.lastmx || this.mouseY != this.lastmy) {
            this.wasMouseMoved = true;
        }
        this.lastmx = this.mouseX;
        this.lastmy = this.mouseY;
        return true;
    }
    
    @Override
    public boolean scrolled(final int amount) {
        this.scroll = amount;
        return false;
    }
    
    @Override
    public boolean keyTyped(final char character) {
        this.keyTyped.add(character);
        return true;
    }
}
