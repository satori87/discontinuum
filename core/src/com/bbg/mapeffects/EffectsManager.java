// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.mapeffects;

import com.bbg.dc.Entity;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Color;
import java.util.Iterator;
import com.bbg.dc.Att;
import com.bbg.dc.Tile;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.bbg.dc.AssetLoader;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import java.util.List;
import java.util.LinkedList;
import com.bbg.dc.Map;
import com.bbg.dc.DCGame;

public class EffectsManager
{
    public DCGame game;
    public Map map;
    public LinkedList<PFX> effects;
    public List<Blood> blood;
    List<Floater> floaters;
    private ParticleEffect leftFOW;
    private ParticleEffect rightFOW;
    private ParticleEffect topFOW;
    private ParticleEffect bottomFOW;
    long stamp;
    
    public EffectsManager(final DCGame game) {
        this.effects = new LinkedList<PFX>();
        this.blood = new ArrayList<Blood>();
        this.floaters = new LinkedList<Floater>();
        this.stamp = 0L;
        this.game = game;
    }
    
    public void start(final Map map) {
        this.map = map;
        this.effects.clear();
        this.blood.clear();
        this.floaters.clear();
        (this.leftFOW = AssetLoader.getEffect(4)).start();
        this.leftFOW.setPosition(-96.0f, 1024.0f);
        this.leftFOW.scaleEffect(2.5f);
        this.leftFOW.getEmitters().get(0).setCleansUpBlendFunction(false);
        (this.topFOW = AssetLoader.getEffect(5)).start();
        this.topFOW.setPosition(1024.0f, -96.0f);
        this.topFOW.scaleEffect(2.5f);
        this.topFOW.getEmitters().get(0).setCleansUpBlendFunction(false);
        (this.rightFOW = AssetLoader.getEffect(4)).start();
        this.rightFOW.setPosition(2144.0f, 1024.0f);
        this.rightFOW.scaleEffect(2.5f);
        this.rightFOW.getEmitters().get(0).setCleansUpBlendFunction(false);
        (this.bottomFOW = AssetLoader.getEffect(5)).start();
        this.bottomFOW.setPosition(1024.0f, 2144.0f);
        this.bottomFOW.scaleEffect(2.5f);
        this.bottomFOW.getEmitters().get(0).setCleansUpBlendFunction(false);
        for (int i = 0; i < 20; ++i) {
            this.leftFOW.update(1.0f);
            this.topFOW.update(1.0f);
            this.rightFOW.update(1.0f);
            this.bottomFOW.update(1.0f);
        }
        for (int x = 0; x < 64; ++x) {
            for (int y = 0; y < 64; ++y) {
                final Tile t = map.data.tile[x][y];
                for (int b = 0; b < 2; ++b) {
                    final Att a = t.att[b];
                    if (a.data[0] == 9) {
                        final ParticleEffect pe = AssetLoader.getEffect(a.data[1]);
                        pe.start();
                        pe.flipY();
                        pe.setPosition((float)(x * 32 + 16 + a.data[3]), (float)(y * 32 + a.data[4] + 16));
                        final float scl = a.data[5] / 100.0f;
                        pe.scaleEffect(scl);
                        final PFX pfx = new PFX();
                        pfx.layer = a.data[2];
                        pfx.type = a.data[1];
                        pfx.p = pe;
                        for (final ParticleEmitter p : pfx.p.getEmitters()) {
                            p.setCleansUpBlendFunction(false);
                        }
                        this.effects.add(pfx);
                        for (int j = 0; j < 100; ++j) {
                            pfx.p.update(0.1f);
                        }
                    }
                }
            }
        }
    }
    
    public void renderBlood(final boolean foreground) {
        if (foreground) {
            for (final Blood z : this.blood) {
                if (z.foreground) {
                    final int zx = (int)z.X;
                    final int zy = (int)z.Y;
                    this.game.setColor(z.col);
                    if (Math.abs(zx - this.game.getCamX()) > this.game.width / 2 || Math.abs(zy - this.game.getCamY()) > this.game.height / 2) {
                        continue;
                    }
                    this.game.drawRegion(AssetLoader.getBloodRegion(z.bloodType), zx, zy, true, z.rotation, 1.0f);
                }
            }
        }
        else {
            for (final Blood z : this.blood) {
                if (!z.foreground) {
                    final int zx = (int)z.X;
                    final int zy = (int)z.Y;
                    this.game.setColor(z.col);
                    if (Math.abs(zx - this.game.getCamX()) > this.game.width / 2 || Math.abs(zy - this.game.getCamY()) > this.game.height / 2) {
                        continue;
                    }
                    this.game.drawRegion(AssetLoader.getBloodRegion(z.bloodType), zx, zy, true, z.rotation, 1.0f);
                }
            }
        }
        this.game.setColor(Color.WHITE);
    }
    
    public void update() {
        final Iterator<Blood> bloodItr = this.blood.iterator();
        this.leftFOW.update(0.008333334f);
        this.topFOW.update(0.008333334f);
        this.rightFOW.update(0.008333334f);
        this.bottomFOW.update(0.008333334f);
        while (bloodItr.hasNext()) {
            final Blood p = bloodItr.next();
            p.update(0.016666668f);
            if (p.remove) {
                bloodItr.remove();
            }
        }
        final float windX = Map.weatherMan.getWindX();
        final float windY = Map.weatherMan.getWindY();
        for (final PFX fx : this.effects) {
            if (fx.type == 2) {
                final ParticleEmitter em = fx.p.getEmitters().get(0);
                em.getWind().setLow(0.0f, 0.0f);
                em.getWind().setHigh(windX, windX);
                float wy = windY * 3.0f;
                if (Math.abs(wy) < 40.0f) {
                    wy = 40.0f * (windY / Math.abs(windY));
                }
                em.getVelocity().setHigh(wy, wy);
            }
        }
        final List<Floater> drops = new LinkedList<Floater>();
        for (final Floater f : this.floaters) {
            if (f.active) {
                f.update(this.game.tick);
            }
            else {
                drops.add(f);
            }
        }
        for (final Floater f : drops) {
            this.floaters.remove(f);
        }
        if (this.game.tick > this.stamp) {
            this.stamp = this.game.tick + 67L;
            for (final PFX pfx : this.effects) {
                for (final ParticleEmitter pe : pfx.p.getEmitters()) {
                    if (Math.abs(pe.getX() - this.game.getCamX()) < this.game.width / 2 + 64 && Math.abs(pe.getY() - this.game.getCamY()) < this.game.height / 2 + 64) {
                        pfx.p.update(0.06666667f);
                    }
                }
            }
        }
    }
    
    public void overlay() {
        for (final Floater f : this.floaters) {
            f.render(this.game);
        }
        this.game.batcher.flush();
    }
    
    public void render(final int i, final int y) {
        for (final PFX pfx : this.effects) {
            if (pfx.layer == i) {
                for (final ParticleEmitter pe : pfx.p.getEmitters()) {
                    if (y - 16 == pe.getY() && Math.abs(pe.getX() - this.game.getCamX()) < this.game.width / 2 + 64 && Math.abs(pe.getY() - this.game.getCamY()) < this.game.height / 2 + 64) {
                        pfx.p.draw(this.game.batcher);
                        if (!pe.isAdditive()) {
                            continue;
                        }
                        this.game.batcher.setBlendFunction(770, 771);
                    }
                }
            }
        }
    }
    
    public void addFloater(final Entity e, final int x, final int y, final String text, final Color col, final long aliveAt) {
        final Floater f = new Floater(e, x, y, text, col, aliveAt);
        this.floaters.add(f);
    }
    
    public void addBlood(final int type, final Color bloodCol, final float X, final float Y, final float delay, final float TTL, final float XVelocity, final float YVelocity, final float spread, final float fallDistance) {
        final Blood b = new Blood(this.game, type, bloodCol, X, Y - 16.0f, (int)delay, (int)TTL, XVelocity, YVelocity, spread, fallDistance);
        this.blood.add(b);
    }
}
