// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;

public class HdpiUtils
{
    public static void glScissor(final int x, final int y, final int width, final int height) {
        if (Gdx.graphics.getWidth() != Gdx.graphics.getBackBufferWidth() || Gdx.graphics.getHeight() != Gdx.graphics.getBackBufferHeight()) {
            Gdx.gl.glScissor(toBackBufferX(x), toBackBufferY(y), toBackBufferX(width), toBackBufferY(height));
        }
        else {
            Gdx.gl.glScissor(x, y, width, height);
        }
    }
    
    public static void glViewport(final int x, final int y, final int width, final int height) {
        if (Gdx.graphics.getWidth() != Gdx.graphics.getBackBufferWidth() || Gdx.graphics.getHeight() != Gdx.graphics.getBackBufferHeight()) {
            Gdx.gl.glViewport(toBackBufferX(x), toBackBufferY(y), toBackBufferX(width), toBackBufferY(height));
        }
        else {
            Gdx.gl.glViewport(x, y, width, height);
        }
    }
    
    public static int toLogicalX(final int backBufferX) {
        return (int)(backBufferX * Gdx.graphics.getWidth() / (float)Gdx.graphics.getBackBufferWidth());
    }
    
    public static int toLogicalY(final int backBufferY) {
        return (int)(backBufferY * Gdx.graphics.getHeight() / (float)Gdx.graphics.getBackBufferHeight());
    }
    
    public static int toBackBufferX(final int logicalX) {
        return (int)(logicalX * Gdx.graphics.getBackBufferWidth() / (float)Gdx.graphics.getWidth());
    }
    
    public static int toBackBufferY(final int logicalY) {
        return (int)(logicalY * Gdx.graphics.getBackBufferHeight() / (float)Gdx.graphics.getHeight());
    }
}
