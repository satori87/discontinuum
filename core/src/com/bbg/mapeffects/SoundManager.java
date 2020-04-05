// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.mapeffects;

import com.bbg.dc.iface.Vector;
import com.bbg.dc.Random;
import com.bbg.dc.iface.Scene;
import com.badlogic.gdx.audio.Sound;
import com.bbg.dc.Tile;
import java.util.Iterator;
import com.bbg.dc.AssetLoader;
import java.util.LinkedList;
import com.bbg.dc.Map;
import com.bbg.dc.DCGame;

public class SoundManager
{
    private DCGame game;
    private Map map;
    public LinkedList<MapSound> sounds;
    
    public SoundManager(final DCGame game) {
        this.sounds = new LinkedList<MapSound>();
        this.game = game;
    }
    
    public void start(final Map map, final Map oldMap) {
        this.map = map;
        if (oldMap != null) {
            for (final MapSound ms : oldMap.soundMan.sounds) {
                if (ms.playing) {
                    AssetLoader.getSnd(ms.s).stop(ms.id);
                }
            }
        }
        this.sounds.clear();
        for (int x = 0; x < 64; ++x) {
            for (int y = 0; y < 64; ++y) {
                final Tile t = map.data.tile[x][y];
                for (int a = 0; a < 2; ++a) {
                    if (t.att[a].data[0] == 6) {
                        final int s = 24;
                        final float v = 0.2f;
                        final int r = 5;
                        final MapSound ms2 = new MapSound(this.game, x, y, r, s, v, true, true, 100);
                        ms2.fromRain = true;
                        this.sounds.add(ms2);
                    }
                    else if (t.att[a].data[0] == 7) {
                        final int s = t.att[a].data[1];
                        final float v = t.att[a].data[2] / 100.0f;
                        final int r = t.att[a].data[3];
                        final int f = t.att[a].data[4];
                        final MapSound ms3 = new MapSound(this.game, x, y, r, s, v, t.att[a].flags[0], t.att[a].flags[1], f);
                        this.sounds.add(ms3);
                    }
                }
            }
        }
    }
    
    public Sound getSnd(final int id) {
        return AssetLoader.getSnd(id);
    }
    
    private void playFirst(final MapSound ms) {
        if (ms.loop) {
            if (ms.is2D) {
                if ((ms.fromRain && Map.weatherMan.isRaining()) || !ms.fromRain) {
                    ms.id = -1L;
                    ms.id = Scene.play3D(ms.s, (float)this.map.player.x, (float)this.map.player.y, (float)(ms.x * 32 + 16), (float)(ms.y * 32 + 16), ms.range, ms.vol, 1.0f, true);
                    if (ms.id >= 0L) {
                        ms.inRange = true;
                        ms.cool = this.game.tick + AssetLoader.getSoundDuration(ms.s);
                    }
                }
            }
            else {
                ms.id = -1L;
                ms.id = Scene.playSound(ms.s, ms.vol, 1.0f, true);
                if (ms.id >= 0L) {
                    ms.inRange = true;
                    ms.cool = this.game.tick + AssetLoader.getSoundDuration(ms.s);
                }
            }
        }
        else if (this.game.tick > ms.freqStamp) {
            ms.freqStamp = this.game.tick + 900L + Random.getInt(200);
            final int r = Random.getInt(ms.freq);
            if (r == ms.freq - 1) {
                if (ms.is2D) {
                    ms.id = -1L;
                    ms.id = Scene.play3D(ms.s, (float)this.map.player.x, (float)this.map.player.y, (float)(ms.x * 32 + 16), (float)(ms.y * 32 + 16), ms.range, ms.vol, 1.0f, false);
                    if (ms.id >= 0L) {
                        ms.inRange = true;
                        ms.cool = this.game.tick + AssetLoader.getSoundDuration(ms.s);
                    }
                }
            }
            else if ((ms.fromRain && Map.weatherMan.isRaining()) || !ms.fromRain) {
                ms.id = -1L;
                ms.id = Scene.playSound(ms.s, ms.vol, 1.0f);
            }
        }
    }
    
    void updateSound(final MapSound ms) {
        ms.update(this.game.tick);
        if (ms.is2D && this.game.tick > ms.updateAt) {
            Scene.updateSound(ms.id, ms.s, (float)(ms.x * 32 + 16), (float)(ms.y * 32 + 16), (float)this.map.player.x, (float)this.map.player.y, ms.range, ms.vol, 1.0f, ms.loop);
            ms.updateAt = this.game.tick + 200L;
        }
    }
    
    public void kill() {
        for (final MapSound ms : this.sounds) {
            if (ms.id >= 0L) {
                this.getSnd(ms.s).stop(ms.id);
            }
        }
    }
    
    public void play() {
        for (final MapSound ms : this.sounds) {
            if (Vector.distance((float)(ms.x * 32), (float)(ms.y * 32), (float)this.map.player.x, (float)this.map.player.y) <= ms.range * 32) {
                if (ms.inRange) {
                    this.updateSound(ms);
                }
                else {
                    this.playFirst(ms);
                }
            }
            else {
                if (ms.id >= 0L) {
                    AssetLoader.getSnd(ms.s).stop(ms.id);
                    ms.id = -1L;
                }
                ms.inRange = false;
            }
        }
    }
}
