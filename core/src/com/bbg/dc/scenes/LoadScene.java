// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.scenes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.Iterator;
import java.util.List;
import com.badlogic.gdx.files.FileHandle;
import java.util.Arrays;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.bbg.dc.AssetLoader;
import com.bbg.dc.DCGame;
import com.bbg.dc.iface.Label;
import com.bbg.dc.iface.Scene;

public class LoadScene extends Scene
{
    Label lblQuote;
    Label lblProgress;
    public boolean loadedAssets;
    public float progress;
    int quote;
    public long quoteStamp;
    boolean run;
    
    public LoadScene(final DCGame game) {
        super(game);
        this.loadedAssets = false;
        this.progress = 0.0f;
        this.quote = AssetLoader.rndInt(12);
        this.quoteStamp = 0L;
        this.run = true;
    }
    
    @Override
    public void start() {
        super.start();
        this.lblQuote = new Label(this.game, this.game.width / 2, this.game.height / 2, 1.0f, "", Color.WHITE, true);
        this.lblProgress = new Label(this.game, this.game.width / 2, this.game.height / 2 + 100, 1.0f, "", Color.WHITE, true);
        this.labels.add(this.lblQuote);
        this.labels.add(this.lblProgress);
        AssetLoader.preloadAssets();
    }
    
    @Override
    public void buttonPressed(final int id) {
    }
    
    public void buttonReleased(final int id) {
    }
    
    public String[] findTextures() {
        final String[] strs = { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" };
        if (!this.run) {
            return strs;
        }
        System.out.println("cool");
        this.run = false;
        final FileHandle artFile = Gdx.files.local("assets/common.txt");
        final String text = artFile.readString();
        final List<String> lines = Arrays.asList(text.split("\\r?\\n"));
        String st = "";
        int c = 0;
        int d = 0;
        final boolean thisline = false;
        for (final String s : lines) {
            if (!thisline && s.length() > 6) {
                st = s.substring(s.length() - 4);
                if (st.equals(".png")) {
                    st = s.substring(7);
                    if (st.length() == 5) {
                        st = st.substring(0, 1);
                    }
                    else if (st.length() == 6) {
                        st = st.substring(0, 2);
                    }
                    d = Integer.parseInt(st);
                    strs[d] = lines.get(c + 5);
                }
            }
            ++c;
        }
        return strs;
    }
    
    @Override
    public void update(final long tick) {
        this.loadedAssets = this.game.manager.update();
        if (this.loadedAssets) {
            this.game.monsterScene.loadMonsters();
            this.game.mapScene.loadMaps();
            this.game.mapScene.loadGraphs();
            AssetLoader.loadAssets();
            final int c = 0;
            final String[] strs = this.findTextures();
            final String st = "";
            for (int i = 0; i < 14; ++i) {
                if (strs[i].length() > 0) {
                    this.game.batcher.setTexture(i, AssetLoader.atlas.findRegion(strs[i]).getTexture());
                }
            }
            final TextureRegion particleRegion = AssetLoader.masks.findRegion("particle");
            this.game.batcher.setTexture(14, particleRegion.getTexture());
            this.game.aregion = new TextureRegion(this.game.batcher.uTex[0], 0, 0, 2, 2);
            SpriteBatch.particleX = (float)particleRegion.getRegionX();
            SpriteBatch.particleY = (float)particleRegion.getRegionY();
            AssetLoader.loadMasks(this.game);
            this.game.changeScene(this.game.mainMenu);
        }
        else {
            this.progress = this.game.manager.getProgress();
            this.lblProgress.text = String.valueOf(Math.round(this.progress * 100.0f)) + "%";
        }
        if (tick > this.quoteStamp) {
            this.quoteStamp = tick + 3000L;
            ++this.quote;
            this.lblQuote.text = AssetLoader.getQuote(this.quote);
        }
        super.update(tick);
    }
    
    @Override
    public void render() {
        super.render();
    }
}
