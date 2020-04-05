// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.mapeffects;

import com.badlogic.gdx.graphics.g2d.Batch;
import java.util.Iterator;
import com.bbg.dc.AssetLoader;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import java.util.LinkedList;
import com.bbg.dc.Map;
import com.bbg.dc.DCGame;

public class WeatherManager
{
    DCGame game;
    Map map;
    LinkedList<Rain> drops;
    private static boolean raining;
    private LinkedList<Rain> rain;
    private static float windX;
    private static float windY;
    private static int rainType;
    private static boolean storm;
    private static long rainid;
    private static long stormid;
    private static long rainStopAt;
    private static float rainStr;
    private ParticleEffect fog;
    long stamp;
    public boolean started;
    private static long rainStamp;
    private static long windStamp;
    
    static {
        WeatherManager.raining = true;
        WeatherManager.windX = 0.0f;
        WeatherManager.windY = 0.0f;
        WeatherManager.rainType = 1;
        WeatherManager.storm = false;
        WeatherManager.rainid = 0L;
        WeatherManager.stormid = 0L;
        WeatherManager.rainStopAt = 0L;
        WeatherManager.rainStr = 1.0f;
        WeatherManager.rainStamp = 0L;
        WeatherManager.windStamp = 0L;
    }
    
    public WeatherManager(final DCGame game) {
        this.drops = new LinkedList<Rain>();
        this.stamp = 0L;
        this.started = false;
        this.game = game;
        this.rain = new LinkedList<Rain>();
    }
    
    public boolean isRaining() {
        return WeatherManager.raining;
    }
    
    public float getWindX() {
        return WeatherManager.windX;
    }
    
    public float getWindY() {
        return WeatherManager.windY;
    }
    
    public void update() {
        if (this.map.data.indoors()) {
            return;
        }
        if (this.game.tick > this.stamp) {
            this.stamp = this.game.tick + 17L;
            this.rain();
            this.wind();
            if (this.map.data.foggy()) {
                this.fog.update(0.0015f);
            }
        }
    }
    
    private void wind() {
        if (this.game.tick > WeatherManager.windStamp) {
            WeatherManager.windStamp = this.game.tick + 200L;
            final float w = (float)(Math.random() * 2.0) - 1.0f;
            final float h = (float)(Math.random() * 2.0) - 0.9f;
            WeatherManager.windX += w;
            WeatherManager.windY += h;
            if (WeatherManager.windX <= -10.0f) {
                WeatherManager.windX = -10.0f;
            }
            if (WeatherManager.windY >= 10.0f) {
                WeatherManager.windY = 10.0f;
            }
            if (WeatherManager.windY <= -10.0f) {
                WeatherManager.windY = -10.0f;
            }
            if (WeatherManager.windY >= 10.0f) {
                WeatherManager.windY = 10.0f;
            }
        }
    }
    
    public void start(final Map map, final Map oldMap) {
        this.map = map;
        if (WeatherManager.raining && oldMap != null) {
            if (map.data.indoors() != oldMap.data.indoors()) {
                if (map.data.flags[0]) {
                    AssetLoader.getSnd(21).stop(WeatherManager.rainid);
                    if (WeatherManager.storm) {
                        AssetLoader.getSnd(21).stop(WeatherManager.stormid);
                    }
                    this.rain.clear();
                }
                else {
                    this.startRain();
                }
            }
        }
        else {
            this.rain.clear();
        }
        if (map.data.indoors() || this.started) {
            return;
        }
        this.started = true;
        (this.fog = AssetLoader.getEffect(3)).start();
        this.fog.flipY();
        this.fog.setPosition(2048.0f, 1024.0f);
        this.fog.scaleEffect(8.0f);
        for (int i = 0; i < 200; ++i) {
            this.fog.update(1.0f);
        }
    }
    
    private void rain() {
        if (WeatherManager.raining) {
            if (this.game.tick > WeatherManager.rainStopAt) {
                WeatherManager.raining = false;
                AssetLoader.getSnd(21 + WeatherManager.rainType).stop(WeatherManager.rainid);
                if (WeatherManager.storm) {
                    AssetLoader.getSnd(25).stop(WeatherManager.stormid);
                }
                this.rain.clear();
            }
            if (!this.map.data.flags[0]) {
                for (int a = AssetLoader.rndInt((int)(25.0f * WeatherManager.rainStr)) + (int)(25.0f * WeatherManager.rainStr), i = 0; i < a; ++i) {
                    this.rain.add(new Rain(this.game, this));
                }
                for (final Rain r : this.rain) {
                    r.update(this.game.tick);
                    if (!r.active) {
                        this.drops.add(r);
                    }
                }
                for (final Rain r : this.drops) {
                    this.rain.remove(r);
                }
                this.drops.clear();
            }
        }
        else if (this.game.tick > WeatherManager.rainStamp) {
            WeatherManager.rainStamp = this.game.tick + 60L;
            if (AssetLoader.rndInt(100) < 3) {
                WeatherManager.raining = true;
                WeatherManager.rainStopAt = this.game.tick + 300000L + AssetLoader.rndInt(600000);
                WeatherManager.rainStr = (float)(Math.random() * 2.0) + 0.1f;
                final float ratio = this.game.width / 1024.0f;
                WeatherManager.rainStr *= ratio;
                this.startRain();
            }
        }
    }
    
    public void render() {
        if (this.map.data.indoors()) {
            return;
        }
        for (final Rain r : this.rain) {
            r.render(this.game);
        }
        if (this.map.data.foggy()) {
            this.fog.draw(this.game.batcher);
        }
        this.game.batcher.flush();
    }
    
    private void startRain() {
        if (WeatherManager.rainStr > 1.7) {
            WeatherManager.rainType = 1;
            WeatherManager.storm = true;
            final boolean b = this.map.data.flags[0];
        }
        else {
            WeatherManager.rainType = 0;
        }
        final boolean b2 = this.map.data.flags[0];
    }
}
