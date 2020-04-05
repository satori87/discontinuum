// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.scenes;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.bbg.dc.iface.ScreenshotFactory;
import com.bbg.dc.AssetLoader;
import java.io.File;
import com.bbg.dc.DCGame;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.bbg.dc.iface.Scene;

public class ExportScene extends Scene
{
    FrameBuffer[] fbo;
    boolean firstrun;
    
    public ExportScene(final DCGame game) {
        super(game);
        this.firstrun = true;
    }
    
    @Override
    public void start() {
        super.start();
    }
    
    @Override
    public void buttonPressed(final int id) {
    }
    
    public void buttonReleased(final int id) {
    }
    
    @Override
    public void update(final long tick) {
    }
    
    public void oorender() {
        if (!this.firstrun) {
            return;
        }
        this.firstrun = false;
    }
    
    public void nooorender() {
        if (!this.firstrun) {
            return;
        }
        this.firstrun = false;
        for (int a = 1; a < 4; ++a) {
            for (int b = 0; b < 50; ++b) {
                final File f = new File("assets/ears/" + a + "/" + b + "c.png");
                if (f.exists()) {
                    this.ftf("ears/" + a + "/" + b + "c", "atlas/ears/" + a + "/" + b, 0, 0, 64, 256);
                }
                else {
                    System.out.println("uh uh");
                }
            }
        }
    }
    
    public void someitemthingiforget() {
        if (!this.firstrun) {
            return;
        }
        this.firstrun = false;
        boolean out = false;
        for (int i = 0; i < 247; ++i) {
            final File file = new File("assets/atlas/items/" + i + ".png");
            if (!file.exists()) {
                System.out.println(String.valueOf(i) + " doesn't exist");
                out = false;
                for (int x = i + 1; x < 249 && !out; ++x) {
                    final File file2 = new File("assets/atlas/items/" + x + ".png");
                    if (!out && file2.exists()) {
                        file2.renameTo(file);
                        out = true;
                    }
                }
            }
        }
    }
    
    public void didit() {
        if (this.firstrun) {
            this.firstrun = false;
            for (int i = 0; i < 15; ++i) {}
        }
    }
    
    public void ftf(final String load, final String save, final int x, final int y, final int w, final int h) {
        final Texture tex = AssetLoader.loadInternalTexture(load);
        ScreenshotFactory.saveTexture("assets/" + save, x, y, w, h, tex);
    }
    
    public void getItemsFromSheet() {
        if (this.firstrun) {
            this.firstrun = false;
            for (int t = 13; t < 22; ++t) {
                for (int c = 0; c < 50; ++c) {
                    final TextureRegion tr = AssetLoader.getSpriteSheetRegion(0, t, c, 0);
                    if (tr != null) {
                        ScreenshotFactory.saveTexture("assets/items/" + AssetLoader.getTypeString(t) + "/" + c, tr.getRegionX(), tr.getRegionY() + 128, 64, 64, tr.getTexture());
                    }
                }
            }
        }
    }
    
    public void pierender() {
        if (this.firstrun) {
            this.firstrun = false;
            final int[] w = { 448, 512, 576, 384, 832, 384 };
            final int[] h = { 256, 256, 256, 256, 256, 64 };
            final int[] y = { 0, 256, 512, 768, 1024, 1280 };
            final String[] st = { "c", "t", "w", "a", "s", "h" };
            for (int t = 0; t < 40; ++t) {
                final Texture tex = AssetLoader.loadInternalTexture("hands/" + t);
                if (tex != null) {
                    final int i = 2;
                    ScreenshotFactory.saveTexture("assets/atlas/walks/male/hands/" + t + st[i], 0, y[i], w[i], h[i], tex);
                }
                else {
                    System.out.println("nope");
                }
            }
        }
    }
}
