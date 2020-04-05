// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

public abstract class Game implements ApplicationListener
{
    protected Screen screen;
    
    @Override
    public void dispose() {
        if (this.screen != null) {
            this.screen.hide();
        }
    }
    
    @Override
    public void pause() {
        if (this.screen != null) {
            this.screen.pause();
        }
    }
    
    @Override
    public void resume() {
        if (this.screen != null) {
            this.screen.resume();
        }
    }
    
    @Override
    public void render() {
        if (this.screen != null) {
            this.screen.render(Gdx.graphics.getDeltaTime());
        }
    }
    
    @Override
    public void resize(final int width, final int height) {
        if (this.screen != null) {
            this.screen.resize(width, height);
        }
    }
    
    public void setScreen(final Screen screen) {
        if (this.screen != null) {
            this.screen.hide();
        }
        this.screen = screen;
        if (this.screen != null) {
            this.screen.show();
            this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }
    
    public Screen getScreen() {
        return this.screen;
    }
}
