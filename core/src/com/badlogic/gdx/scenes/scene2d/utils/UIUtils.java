// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.Gdx;

public class UIUtils
{
    public static boolean isMac;
    public static boolean isWindows;
    public static boolean isLinux;
    
    static {
        UIUtils.isMac = System.getProperty("os.name").contains("OS X");
        UIUtils.isWindows = System.getProperty("os.name").contains("Windows");
        UIUtils.isLinux = System.getProperty("os.name").contains("Linux");
    }
    
    public static boolean left() {
        return Gdx.input.isButtonPressed(0);
    }
    
    public static boolean left(final int button) {
        return button == 0;
    }
    
    public static boolean right() {
        return Gdx.input.isButtonPressed(1);
    }
    
    public static boolean right(final int button) {
        return button == 1;
    }
    
    public static boolean middle() {
        return Gdx.input.isButtonPressed(2);
    }
    
    public static boolean middle(final int button) {
        return button == 2;
    }
    
    public static boolean shift() {
        return Gdx.input.isKeyPressed(59) || Gdx.input.isKeyPressed(60);
    }
    
    public static boolean shift(final int keycode) {
        return keycode == 59 || keycode == 60;
    }
    
    public static boolean ctrl() {
        if (UIUtils.isMac) {
            return Gdx.input.isKeyPressed(63);
        }
        return Gdx.input.isKeyPressed(129) || Gdx.input.isKeyPressed(130);
    }
    
    public static boolean ctrl(final int keycode) {
        if (UIUtils.isMac) {
            return keycode == 63;
        }
        return keycode == 129 || keycode == 130;
    }
    
    public static boolean alt() {
        return Gdx.input.isKeyPressed(57) || Gdx.input.isKeyPressed(58);
    }
    
    public static boolean alt(final int keycode) {
        return keycode == 57 || keycode == 58;
    }
}
