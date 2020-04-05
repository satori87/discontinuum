// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.mapeffects;

import com.bbg.dc.DCGame;

public class MapSound
{
    DCGame game;
    public int x;
    public int y;
    public int s;
    public boolean loop;
    public boolean is2D;
    public float vol;
    public int range;
    public long updateAt;
    public long freqStamp;
    public int freq;
    public boolean fromRain;
    public long last;
    public long cool;
    public long id;
    public boolean inRange;
    public boolean playing;
    
    public MapSound(final DCGame game, final int x, final int y, final int range, final int s, final float vol, final boolean loop, final boolean is2D, final int freq) {
        this.x = 0;
        this.y = 0;
        this.s = 0;
        this.loop = false;
        this.is2D = false;
        this.vol = 1.0f;
        this.range = 0;
        this.updateAt = 0L;
        this.freqStamp = 0L;
        this.freq = 0;
        this.fromRain = false;
        this.last = 0L;
        this.cool = 0L;
        this.id = -1L;
        this.inRange = false;
        this.playing = false;
        this.game = game;
        this.x = x;
        this.y = y;
        this.range = range;
        this.s = s;
        this.vol = vol;
        this.loop = loop;
        this.is2D = is2D;
        this.freq = freq;
    }
    
    public void update(final long tick) {
        if (this.inRange && tick > this.cool && !this.loop && !this.fromRain) {
            this.inRange = false;
        }
    }
}
