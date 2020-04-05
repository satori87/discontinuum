// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher
{
    public static void main(final String[] arg) {
        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.resizable = false;
        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
        config.vSyncEnabled = false;
        config.foregroundFPS = 60;
        config.backgroundFPS = 60;
        new LwjglApplication((ApplicationListener)new DCGame(), config);
    }
}
