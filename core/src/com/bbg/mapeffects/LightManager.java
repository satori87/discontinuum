// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.mapeffects;

import com.badlogic.gdx.physics.box2d.Body;
import com.bbg.dc.Att;
import com.bbg.dc.Tile;
import com.badlogic.gdx.graphics.Color;
import java.util.Calendar;
import java.util.Iterator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Matrix4;
import java.util.LinkedList;
import box2dLight.ConeLight;
import java.util.List;
import com.badlogic.gdx.physics.box2d.World;
import box2dLight.RayHandler;
import com.bbg.dc.Map;
import com.bbg.dc.DCGame;

public class LightManager
{
    private DCGame game;
    private Map map;
    private RayHandler rayHandler;
    public World world;
    private float[] dark;
    private List<ConeLight> coneLights;
    private List<DynamicLight> lights;
    private long lightStamp;
    
    public LightManager(final DCGame game) {
        this.coneLights = new LinkedList<ConeLight>();
        this.lights = new LinkedList<DynamicLight>();
        this.lightStamp = 0L;
        this.game = game;
        this.dark = new float[3600];
    }
    
    public void render() {
        final Matrix4 m4 = this.game.cam.combined;
        m4.scl(100.0f);
        this.rayHandler.setCombinedMatrix(m4, 0.0f, 0.0f, (float)this.game.width, (float)this.game.height);
        this.rayHandler.updateAndRender();
    }
    
    public void start(final Map map) {
        this.map = map;
        for (int i = 0; i < 1800; ++i) {
            this.dark[i] = 1.0f - i * 5.58E-4f;
            if (this.dark[i] <= 0.0f) {
                this.dark[i] = 0.0f;
            }
            if (this.dark[i] > 1.0f) {
                this.dark[i] = 1.0f;
            }
        }
        int c = 1799;
        for (int j = 1800; j < 3600; ++j) {
            this.dark[j] = this.dark[c];
            --c;
        }
        this.world = new World(new Vector2(0.0f, 0.0f), true);
        if (this.rayHandler != null) {
            this.rayHandler.dispose();
        }
        (this.rayHandler = new RayHandler(this.world)).setAmbientLight(0.0f, 0.0f, 0.0f, 1.0f);
        this.rayHandler.setShadows(true);
        this.addMapLights();
    }
    
    public World getWorld() {
        return this.world;
    }
    
    public void update() {
        this.setAmbient();
        for (final DynamicLight pl : this.lights) {
            if (!pl.isStaticLight() && Math.abs(pl.getX() * 100.0f - this.game.getCamX()) < this.game.width / 2 + pl.distance * 100.0f && Math.abs(pl.getY() * 100.0f - this.game.getCamY()) < this.game.height / 2 + pl.distance * 100.0f) {
                pl.update();
            }
        }
        for (final ConeLight cl : this.coneLights) {
            cl.update();
        }
    }
    
    void setAmbient() {
        if (this.map.data.flags[0]) {
            this.rayHandler.setAmbientLight(0.0f, 0.0f, 0.0f, this.map.data.data[0] / 255.0f);
        }
        else {
            final Calendar rightNow = Calendar.getInstance();
            final int m = rightNow.get(12);
            final int s = rightNow.get(13);
            this.rayHandler.setAmbientLight(0.0f, 0.0f, 0.0f, this.dark[m * 60 + s]);
        }
    }
    
    public void addMapLights() {
        if (this.game.tick > this.lightStamp) {
            this.lightStamp = this.game.tick + 200L;
            this.lights.clear();
            this.coneLights.clear();
            for (int x = this.game.getCamX() / 32 - 35; x < this.game.getCamX() / 32 + 35; ++x) {
                for (int y = this.game.getCamY() / 32 - 35; y < this.game.getCamY() / 32 + 35; ++y) {
                    if (x >= 0 && y >= 0 && x < 64 && y < 64) {
                        final Tile t = this.map.data.tile[x][y];
                        for (int b = 0; b < 2; ++b) {
                            final Att a = t.att[b];
                            if (a.data[0] == 4) {
                                final float dist = a.data[2] / 10.0f;
                                final int modX = a.data[3];
                                final int modY = a.data[4];
                                final Color c = new Color(a.data[5] / 255.0f, a.data[6] / 255.0f, a.data[7] / 255.0f, a.data[1] / 255.0f);
                                final int cx = x * 32 + 16 + modX;
                                final int cy = y * 32 + 16 + modY;
                                final float dx = cx / 100.0f;
                                final float dy = cy / 100.0f;
                                int r = a.data[8];
                                if (r < 128) {
                                    r = 128;
                                }
                                final DynamicLight pl = new DynamicLight(this.game, this, a.flags[0], this.rayHandler, r, c, dist, dx, dy);
                                pl.flickers = a.flags[0];
                                pl.setSoft(a.flags[1]);
                                pl.setSoftnessLength(a.data[9] / 10.0f);
                                if (a.flags[2]) {
                                    pl.setXray(true);
                                    pl.setStaticLight(false);
                                }
                                this.lights.add(pl);
                            }
                            else if (a.data[0] == 6) {
                                final Calendar rightNow = Calendar.getInstance();
                                final int m = rightNow.get(12);
                                final int s = rightNow.get(13);
                                final float d = this.dark[m * 60 + s];
                                if (d > 0.0f) {
                                    final int dir = a.data[2];
                                    int fx = 0;
                                    final int fy = 0;
                                    int angle = 0;
                                    if (dir == 0) {
                                        angle = 270;
                                    }
                                    else if (dir == 1) {
                                        fx += 8;
                                        angle = 180;
                                    }
                                    else if (dir == 2) {
                                        angle = 90;
                                    }
                                    else if (dir == 3) {
                                        fx -= 8;
                                        angle = 0;
                                    }
                                    final ConeLight cl = new ConeLight(this.rayHandler, 60, new Color(0.0f, 0.0f, 0.0f, d), a.data[1] / 10.0f, (x * 32 + 16 + fx) / 100.0f, (y * 32 + 16 + fy) / 100.0f, (float)angle, 50.0f);
                                    cl.setSoft(true);
                                    cl.setStaticLight(false);
                                    cl.setSoftnessLength(2.5f);
                                    this.coneLights.add(cl);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public DynamicLight createLight(final int lightRays, final float lightDistance, final float lightAlpha, final boolean flickers, final boolean soft, final float softLength, final Body lBody) {
        if (lightDistance == 0.0f || lightAlpha == 0.0f) {
            return null;
        }
        final DynamicLight light = new DynamicLight(this.game, this, flickers, this.rayHandler, lightRays, new Color(0.0f, 0.0f, 0.0f, lightAlpha), lightDistance, lBody.getPosition().x, lBody.getPosition().y);
        light.setSoft(true);
        light.setSoftnessLength(1.5f);
        light.attachToBody(lBody);
        light.setIgnoreAttachedBody(true);
        return light;
    }
}
