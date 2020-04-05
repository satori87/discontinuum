// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;
import com.bbg.dc.aistuff.Vector;
import java.util.Iterator;
import com.bbg.mapeffects.SoundManager;
import com.bbg.mapeffects.EffectsManager;
import com.bbg.mapeffects.LightManager;
import com.bbg.mapeffects.WeatherManager;
import java.util.LinkedList;
import java.util.ArrayList;
import com.badlogic.gdx.physics.box2d.World;

public class Map
{
    public int id;
    DCGame game;
    public Player player;
    public MapData data;
    World world;
    ArrayList<Entity> entities;
    LinkedList<DrawTask>[] drawList;
    public static WeatherManager weatherMan;
    LightManager lightMan;
    EffectsManager fxMan;
    AIManager aiMan;
    public SoundManager soundMan;
    
    public Map(final DCGame game, final int id) {
        this.id = 0;
        this.game = game;
        this.id = id;
        this.entities = new ArrayList<Entity>();
        this.drawList = (LinkedList<DrawTask>[])new LinkedList[2176];
        for (int y = 0; y < 2176; ++y) {
            this.drawList[y] = new LinkedList<DrawTask>();
        }
        Map.weatherMan = new WeatherManager(game);
        this.lightMan = new LightManager(game);
        this.fxMan = new EffectsManager(game);
        this.aiMan = new AIManager(game, this);
        this.soundMan = new SoundManager(game);
        this.player = game.gameScene.player;
        this.data = game.mapScene.map[id];
        World.setVelocityThreshold(0.0f);
    }
    
    public void update(final long tick) {
        Iterator<Entity> entityItr = this.entities.iterator();
        while (entityItr.hasNext()) {
            final Entity e = entityItr.next();
            if (!e.dead) {
                e.pre(tick);
            }
            else {
                entityItr.remove();
            }
        }
        this.lightMan.update();
        Map.weatherMan.update();
        this.fxMan.update();
        this.aiMan.update();
        this.world.step(this.game.deltaT, 1, 1);
        this.world.clearForces();
        entityItr = this.entities.iterator();
        while (entityItr.hasNext()) {
            final Entity e = entityItr.next();
            if (!e.dead) {
                e.post(tick);
            }
            else {
                entityItr.remove();
            }
        }
        this.soundMan.play();
        this.player.endTick();
    }
    
    public void render() {
        this.game.clip(this.game.getCamX() - this.game.width / 2, this.game.getCamY() - this.game.height / 2, this.game.width, this.game.height);
        for (int i = 0; i < 8; ++i) {
            this.drawLayer(i);
            this.drawSorted(i);
        }
        Map.weatherMan.render();
        this.game.resetBatcher();
        this.lightMan.render();
        this.game.resetBatcher();
        this.fxMan.overlay();
        this.game.endClip();
        this.player.overlay();
    }
    
    public Vector.Pos getMouse() {
        final int mX = this.game.input.getMouseX();
        final int mY = this.game.input.getMouseY();
        final int x = mX + this.player.x - this.game.width / 2 + (this.game.getCamX() - this.player.x);
        final int y = mY + this.player.y - this.game.height / 2 + (this.game.getCamY() - this.player.y);
        return new Vector.Pos(x, y);
    }
    
    public Vector.Pos getMouseDown(final int layer) {
        final int mX = this.game.input.mouseDownX[0];
        final int mY = this.game.input.mouseDownY[0];
        final int x = mX + this.player.x - this.game.width / 2 + (this.game.getCamX() - this.player.x);
        final int y = mY + this.player.y - this.game.height / 2 + (this.game.getCamY() - this.player.y);
        return new Vector.Pos(x, y);
    }
    
    public void spawnMonster(final int mon, final int x, final int y) {
        final Monster m = new Monster(this.game, mon, x, y);
        this.entities.add(m);
        m.join(this);
    }
    
    private void spawnMonsters() {
        for (int x = 0; x < 64; ++x) {
            for (int y = 0; y < 64; ++y) {
                for (int b = 0; b < 2; ++b) {
                    final Att a = this.data.tile[x][y].att[b];
                    if (a.data[0] == 10) {
                        final int prob = a.data[3];
                        final int mon = a.data[2];
                        if ((int)(Math.random() * 100.0) <= prob) {
                            this.spawnMonster(mon, x * 32 + 16, y * 32 + 32);
                        }
                    }
                }
            }
        }
    }
    
    public void join(final Map oldMap) {
        Map.weatherMan.start(this, oldMap);
        this.lightMan.start(this);
        this.fxMan.start(this);
        if (!this.data.setup) {
            this.setupMap(oldMap);
        }
        this.entities.clear();
        this.entities.add(this.player);
        this.drawMap();
        this.spawnMonsters();
        this.aiMan.start();
        this.player.join(this);
    }
    
    private void drawSortedTile(final int i, final MapData data, final int x, final int y, final int oX, final int oY) {
        final Tile t = data.tile[x][y];
        for (int e = 0; e < 2; ++e) {
            final Att a = t.att[e];
            if (a.data[0] == 5 && a.data[4] == i) {
                final int cx = oX * 32 - 16 + a.data[5];
                final int cy = oY * 32 - 16 + a.data[6];
                final int ts = a.data[2];
                final int sx = a.data[1] % 32;
                final int sy = (a.data[1] - sx) / 32;
                final TextureRegion tr = AssetLoader.getTileRegion(ts, sx * 32, sy * 32);
                tr.flip(false, true);
                final DrawTask dt = new DrawTask(this.game, tr, cx, cy, false, 0.0f, 1.0f, Color.WHITE);
                final int ry = cy + 16 + 32;
                if (ry >= 0 && ry < 2176) {
                    this.drawList[ry].add(dt);
                }
            }
            if (a.data[0] == 1 && a.data[2] == i) {
                final TextureRegion tr2 = AssetLoader.getMobj(a.data[1] / 100, a.data[1] % 100);
                final int cx2 = x * 32 - tr2.getRegionWidth() / 2 + 16 + a.data[3];
                final int cy2 = y * 32 - tr2.getRegionHeight() + 16 + a.data[4];
                final DrawTask dt2 = new DrawTask(this.game, tr2, cx2, cy2, false, 0.0f, 1.0f, Color.WHITE);
                final int ry2 = cy2 + tr2.getRegionHeight() + a.data[5];
                if (ry2 >= 0 && ry2 < 2176) {
                    this.drawList[ry2].add(dt2);
                }
            }
        }
    }
    
    private void drawNeighborTile(final int i, final MapData data, final int x, final int y, final boolean sorted) {
        MapData md = data;
        int dx = 0;
        int dy = 0;
        if (x < 0) {
            if (y < 0) {
                if (data.getExit(1) > 0) {
                    md = this.game.gameScene.maps[data.getExit(1)].data;
                    if (md.getExit(0) > 0) {
                        md = this.game.gameScene.maps[data.getExit(0)].data;
                        dx = x + 64;
                        dy = y + 64;
                    }
                }
            }
            else if (y >= 64) {
                if (data.getExit(1) > 0) {
                    md = this.game.gameScene.maps[data.getExit(1)].data;
                    if (md.getExit(2) > 0) {
                        md = this.game.gameScene.maps[md.getExit(2)].data;
                        dx = x + 64;
                        dy = y - 64;
                    }
                }
            }
            else if (data.getExit(1) > 0) {
                md = this.game.gameScene.maps[data.getExit(1)].data;
                dx = x + 64;
                dy = y;
            }
        }
        else if (x >= 64) {
            if (y < 0) {
                if (data.getExit(3) > 0) {
                    md = this.game.gameScene.maps[data.getExit(3)].data;
                    if (md.getExit(0) > 0) {
                        md = this.game.gameScene.maps[md.getExit(0)].data;
                        dx = x - 64;
                        dy = y + 64;
                    }
                }
            }
            else if (y >= 64) {
                if (data.getExit(3) > 0) {
                    md = this.game.gameScene.maps[data.getExit(3)].data;
                    if (md.getExit(2) > 0) {
                        md = this.game.gameScene.maps[md.getExit(2)].data;
                        dx = x - 64;
                        dy = y - 64;
                    }
                }
            }
            else if (data.getExit(3) > 0) {
                md = this.game.gameScene.maps[data.getExit(3)].data;
                dx = x - 64;
                dy = y;
            }
        }
        else if (y < 0) {
            if (data.getExit(0) > 0) {
                md = this.game.gameScene.maps[data.getExit(0)].data;
                dx = x;
                dy = y + 64;
            }
        }
        else if (y >= 64 && data.getExit(2) > 0) {
            md = this.game.gameScene.maps[data.getExit(2)].data;
            dx = x;
            dy = y - 64;
        }
        if (sorted) {
            this.drawSortedTile(i, md, dx, dy, x, y);
        }
        else {
            this.drawTile(i, md, dx, dy, x, y);
        }
    }
    
    private void drawSorted(final int i) {
        for (int y = 0; y < 2176; ++y) {
            this.drawList[y].clear();
        }
        for (int y = this.game.getCamY() / 32 - 24; y < this.game.getCamY() / 32 + 24; ++y) {
            for (int x = this.game.getCamX() / 32 - 25; x < this.game.getCamX() / 32 + 25; ++x) {
                if (x >= 0 && y >= 0 && x < 64 && y < 64) {
                    this.drawSortedTile(i, this.data, x, y, x, y);
                }
                else {
                    this.drawNeighborTile(i, this.data, x, y, true);
                }
            }
        }
        final int outline = 0;
        for (int y2 = 0; y2 < 2176; ++y2) {
            if (i == 4) {
                for (final Entity e : this.entities) {
                    if (Math.abs(e.x - this.game.getCamX()) <= this.game.width / 2 && Math.abs(e.y - this.game.getCamY()) <= this.game.height / 2 && y2 == e.y + e.doll.getBottomY()) {
                        if (e.outline || this.player.aiMan.pursuit == e) {
                            this.game.shaderData[0] = 1.0f;
                            e.outlineCol.a = e.outlineAlpha;
                            this.game.forceColor(e.outlineCol);
                            e.doll.outline = true;
                            e.render(e.x - 2, e.y - e.height / 2, 1.0f, false, 0, 0);
                            e.render(e.x + 2, e.y - e.height / 2, 1.0f, false, 0, 0);
                            e.render(e.x, e.y - 2 - e.height / 2, 1.0f, false, 0, 0);
                            e.render(e.x, e.y + 2 - e.height / 2, 1.0f, false, 0, 0);
                            e.render(e.x - 1, e.y - 1 - e.height / 2, 1.0f, false, 0, 0);
                            e.render(e.x - 1, e.y + 1 - e.height / 2, 1.0f, false, 0, 0);
                            e.render(e.x + 1, e.y - 1 - e.height / 2, 1.0f, false, 0, 0);
                            e.render(e.x + 1, e.y + 1 - e.height / 2, 1.0f, false, 0, 0);
                            this.game.endForce();
                            this.game.shaderData[0] = 0.0f;
                            e.doll.outline = false;
                        }
                        e.render(e.x, e.y - e.height / 2, 1.0f, false, 0, 0);
                    }
                }
            }
            this.fxMan.render(i, y2);
            for (final DrawTask dt : this.drawList[y2]) {
                dt.render();
            }
        }
    }
    
    private void drawTile(final int i, final MapData md, final int x, final int y, final int oX, final int oY) {
        final Tile t = md.tile[x][y];
        if (t.spriteSet[i] >= 0) {
            final int sx = t.sprite[i] % 32;
            final int sy = (t.sprite[i] - sx) / 32;
            final TextureRegion tr = AssetLoader.getTileRegion(t.spriteSet[i], sx * 32, sy * 32);
            this.game.drawRegion(tr, oX * 32, oY * 32, false, 0.0f, 1.0f);
        }
    }
    
    private void drawMap() {
        final SpriteBatch oldBatch = this.game.batcher;
        for (int i = 0; i < 8; ++i) {
            SpriteBatch batch = new SpriteBatch();
            this.game.fbo[i].begin();
            Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            Gdx.gl.glClear(16384);
            final OrthographicCamera cam = new OrthographicCamera();
            cam.setToOrtho(false, (float)this.game.fbo[i].getWidth(), (float)this.game.fbo[i].getHeight());
            batch = new SpriteBatch();
            batch.setShader(this.game.shader);
            batch.setProjectionMatrix(cam.combined);
            batch.begin();
            this.game.setBatcher(batch);
            for (int x = 0; x < 64; ++x) {
                for (int y = 0; y < 64; ++y) {
                    this.drawTile(i, this.data, x, y, x, y);
                }
            }
            batch.end();
            this.game.fbo[i].end();
        }
        this.game.setBatcher(oldBatch);
    }
    
    private void drawLayer(final int i) {
        for (int x = this.game.getCamX() / 32 - 24; x < this.game.getCamX() / 32 + 24; ++x) {
            for (int y = this.game.getCamY() / 32 - 24; y < this.game.getCamY() / 32 + 24; ++y) {
                if (x < 0 || y < 0 || x >= 64 || y >= 64) {
                    this.drawNeighborTile(i, this.data, x, y, false);
                }
            }
        }
        final TextureRegion tr = new TextureRegion(this.game.fbo[i].getColorBufferTexture());
        tr.flip(false, true);
        this.game.drawRegion(tr, 0, 0, false, 0.0f, 1.0f);
        if (i == 4) {
            this.fxMan.renderBlood(false);
        }
        else if (i == 5) {
            this.fxMan.renderBlood(true);
        }
    }
    
    public void part() {
        this.soundMan.kill();
        this.player.part(this);
    }
    
    private void setupMap(final Map oldMap) {
        this.soundMan.start(this, oldMap);
        this.createWorld();
        this.data.addWalls();
        this.data.createWalls(this.world, this.lightMan);
    }
    
    private void createWorld() {
        (this.world = new World(new Vector2(0.0f, 0.0f), true)).setContinuousPhysics(true);
        this.world.setAutoClearForces(true);
        World.setVelocityThreshold(World.getVelocityThreshold() * 1.0E-7f);
    }
    
    public static boolean validPos(final int x, final int y) {
        return x >= 0 && y >= 0 && x < 2048 && y < 2048;
    }
    
    public Vector2 getValidSpot(int vX, int vY) {
        final boolean out = false;
        float r = 0.0f;
        float d = 16.0f;
        com.bbg.dc.iface.Vector v = null;
        int help = 0;
        if (vX >= 0 && vY >= 0 && vX < 2048 && vY < 2048) {
            do {
                ++help;
                if (this.checkSpace(vX, vY)) {
                    return new Vector2((float)vX, (float)vY);
                }
                r += (float)0.7853981633974483;
                if (r >= 6.283185307179586) {
                    r = 0.0f;
                    d += 16.0f;
                }
                v = new com.bbg.dc.iface.Vector(r, d);
                vX += (int)v.xChange;
                vY += (int)v.yChange;
            } while (!out && help < 1000);
            if (help >= 80) {
                System.out.println("getvalidspot help: " + help);
            }
        }
        return new Vector2(-1.0f, -1.0f);
    }
    
    public boolean checkSpace(final int pX, final int pY) {
        if (pX >= 0 && pY >= 0 && pX < 2048 && pY < 2048) {
            for (int w = pX / 4 - 4; w <= pX / 4 + 4; ++w) {
                for (int h = pY / 4 - 4; h <= pY / 4 + 4; ++h) {
                    if (w >= 0 && h >= 0 && w < 512 && h < 512 && this.data.wall[w][h]) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
}
