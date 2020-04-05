// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.scenes;

import com.badlogic.gdx.Gdx;
import com.bbg.dc.iface.Frame;
import com.bbg.dc.iface.Label;
import com.badlogic.gdx.graphics.Color;
import com.bbg.dc.DCGame;
import com.bbg.dc.iface.Scene;

public class MainMenu extends Scene
{
    public MainMenu(final DCGame game) {
        this.game = game;
    }
    
    @Override
    public void start() {
        super.start();
        this.labels.add(new Label(this.game, 540, 210, 4.0f, "Discontinuum", Color.WHITE, true));
        this.frames.add(new Frame(this.game, 540, 360, 512, 560, true, true));
        this.addButtons(true, true, 1, 540, 444, 448, 48, 12, new String[] { "Map Editor", "Map Test", "Humanoid Editor", "Creature Editor", "Monster Editor", "Quit" }, new int[] { 0, 1, 2, 3, 4, 5 });
    }
    
    @Override
    public void buttonPressed(final int id) {
        switch (id) {
            case 0: {
                this.game.changeScene(this.game.mapScene);
                break;
            }
            case 1: {
                this.game.changeScene(this.game.gameScene);
                this.game.gameScene.switchMap(0);
                break;
            }
            case 2: {
                this.game.dollScene.fromScene = 0;
                this.game.changeScene(this.game.dollScene);
                break;
            }
            case 3: {
                this.game.creatureScene.fromScene = 0;
                this.game.changeScene(this.game.creatureScene);
                break;
            }
            case 4: {
                this.game.listScene.type = 1;
                this.game.changeScene(this.game.listScene);
                break;
            }
            case 5: {
                Gdx.app.exit();
                break;
            }
        }
    }
    
    public void buttonReleased(final int id) {
    }
    
    @Override
    public void update(final long tick) {
        super.update(tick);
    }
    
    @Override
    public void render() {
        super.render();
    }
}
