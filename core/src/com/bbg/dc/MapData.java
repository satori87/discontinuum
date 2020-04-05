// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

import com.badlogic.gdx.physics.box2d.Shape;
import com.esotericsoftware.kryo.io.Output;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.io.FileOutputStream;
import com.bbg.mapeffects.LightManager;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.bbg.dc.aistuff.Wall;
import com.badlogic.gdx.physics.box2d.Body;
import java.io.IOException;
import java.io.FileNotFoundException;
import com.esotericsoftware.kryo.io.Input;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import java.io.FileInputStream;
import java.io.File;

public class MapData
{
    public boolean setup;
    public int id;
    public String name;
    public String desc;
    public Tile[][] tile;
    public boolean[][] wall;
    public boolean[][] shadow;
    public boolean[] flags;
    public int[] data;
    
    public MapData() {
        this.setup = false;
        this.id = 0;
        this.name = "Unnamed Map";
        this.desc = "";
    }
    
    public MapData(final DCGame game, final int id) {
        this.setup = false;
        this.id = 0;
        this.name = "Unnamed Map";
        this.desc = "";
        this.id = id;
        this.reset(game);
    }
    
    public boolean canExit(final int dir) {
        return this.data[dir + 1] > 0;
    }
    
    public int getExit(final int dir) {
        return this.data[dir + 1];
    }
    
    public void reset(final DCGame game) {
        this.tile = new Tile[64][64];
        for (int x = 0; x < 64; ++x) {
            for (int y = 0; y < 64; ++y) {
                this.tile[x][y] = new Tile();
            }
        }
        this.wall = new boolean[512][512];
        this.shadow = new boolean[512][512];
        this.flags = new boolean[32];
        this.data = new int[32];
        for (int i = 0; i < 32; ++i) {
            this.flags[i] = false;
            this.data[i] = 0;
        }
        for (int x = 0; x < 512; ++x) {
            for (int y = 0; y < 512; ++y) {
                this.wall[x][y] = false;
                this.shadow[x][y] = false;
            }
        }
    }
    
    public void load(final DCGame game) {
        try {
            final FileInputStream f = new FileInputStream(new File("maps/map" + this.id));
            final InputStream inputStream = new InflaterInputStream(f);
            final Input input = new Input(inputStream);
            final MapData a = (MapData)game.kryo.readObject(input, (Class)MapData.class);
            this.tile = a.tile;
            this.flags = a.flags;
            this.data = a.data;
            this.desc = a.desc;
            this.name = a.name;
            this.wall = a.wall;
            this.shadow = a.shadow;
            this.setup = false;
            input.close();
            f.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }
    
    public void addWalls() {
        boolean[][] owall = new boolean[40][40];
        for (int x = 0; x < 64; ++x) {
            for (int y = 0; y < 64; ++y) {
                final Tile t = this.tile[x][y];
                for (int a = 0; a < 2; ++a) {
                    if (t.att[a].data[0] == 1 && !t.att[a].flags[0]) {
                        final int s = t.att[a].data[1] / 100;
                        final int mm = t.att[a].data[1] % 100;
                        owall = AssetLoader.mobjdata[s][mm].wall;
                        for (int w = -16; w < 24; ++w) {
                            for (int h = -16; h < 24; ++h) {
                                if (owall[w + 16][h + 16]) {
                                    final int wx = x * 8 + w + t.att[a].data[3] / 4;
                                    final int wy = y * 8 + h + t.att[a].data[4] / 4;
                                    if (wx >= 0 && wx < 512 && wy >= 0 && wy < 512) {
                                        this.wall[wx][wy] = true;
                                        this.shadow[wx][wy] = true;
                                    }
                                }
                            }
                        }
                    }
                    else if (t.att[a].data[0] == 3) {
                        for (int w2 = 0; w2 < 8; ++w2) {
                            for (int h2 = 0; h2 < 8; ++h2) {
                                final int wx2 = x * 8 + w2;
                                final int wy2 = y * 8 + h2;
                                this.wall[wx2][wy2] = true;
                                if (t.att[a].flags[0]) {
                                    this.shadow[wx2][wy2] = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void addWallBody(final Body b, final float w, final float h, final float x, final float y) {
        final Wall wall = new Wall(b, false);
        b.setUserData((Object)wall);
        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.5f;
        fixtureDef.filter.categoryBits = 8;
        fixtureDef.filter.maskBits = 3;
        final PolygonShape p = new PolygonShape();
        p.setAsBox(w / 100.0f / 2.0f, h / 100.0f / 2.0f, new Vector2(x / 100.0f, y / 100.0f), 0.0f);
        fixtureDef.shape = (Shape)p;
        b.createFixture(fixtureDef);
    }
    
    public void addBoundary(final World world, final float w, final float h, final float x, final float y, final boolean isExit) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x / 100.0f, y / 100.0f);
        final Body b = world.createBody(bodyDef);
        final Wall wall = new Wall(b, isExit);
        b.setUserData((Object)wall);
        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 100.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 1.0f;
        fixtureDef.filter.categoryBits = (short)(isExit ? 4 : 8);
        fixtureDef.filter.maskBits = (short)(isExit ? 2 : 3);
        final PolygonShape p = new PolygonShape();
        p.setAsBox(w / 100.0f, h / 100.0f, new Vector2(0.0f, 0.0f), 0.0f);
        fixtureDef.shape = (Shape)p;
        b.createFixture(fixtureDef);
    }
    
    public boolean indoors() {
        return this.flags[0];
    }
    
    public boolean foggy() {
        return this.flags[1];
    }
    
    public void createWalls(final World world, final LightManager lightMan) {
        this.addBoundary(world, 2200.0f, 32.0f, -32.0f, -32.0f, this.canExit(0));
        this.addBoundary(world, 32.0f, 2200.0f, -32.0f, -32.0f, this.canExit(1));
        this.addBoundary(world, 32.0f, 2200.0f, 2080.0f, -32.0f, this.canExit(3));
        this.addBoundary(world, 2200.0f, 32.0f, -32.0f, 2080.0f, this.canExit(2));
        for (int x = 0; x < 64; ++x) {
            for (int y = 0; y < 64; ++y) {
                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set((x * 32 + 16) / 100.0f, (y * 32 + 16) / 100.0f);
                Body bo = world.createBody(bodyDef);
                for (int w = 0; w < 8; ++w) {
                    for (int h = 0; h < 8; ++h) {
                        final int wx = x * 8 + w;
                        final int wy = y * 8 + h;
                        if (wx >= 0 && wx < 512 && wy >= 0 && wy < 512) {
                            if (x >= 0 && y >= 0 && x < 64 && y < 64) {
                                if (this.wall[wx][wy]) {
                                    this.addWallBody(bo, 4.0f, 4.0f, (float)(w * 4 - 16), (float)(h * 4 - 16));
                                }
                            }
                            else {
                                this.addWallBody(bo, 4.0f, 4.0f, (float)(w * 4 - 16), (float)(h * 4 - 16));
                            }
                        }
                    }
                }
                bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set((x * 32 + 16) / 100.0f, (y * 32 + 16) / 100.0f);
                bo = lightMan.getWorld().createBody(bodyDef);
                for (int w = 0; w < 8; ++w) {
                    for (int h = 0; h < 8; ++h) {
                        final int wx = x * 8 + w;
                        final int wy = y * 8 + h;
                        if (wx >= 0 && wx < 512 && wy >= 0 && wy < 512 && x >= 0 && y >= 0 && x < 64 && y < 64 && this.shadow[wx][wy]) {
                            this.addWallBody(bo, 4.0f, 4.0f, (float)(w * 4 - 16), (float)(h * 4 - 16));
                        }
                    }
                }
            }
        }
    }
    
    public void save(final DCGame game) {
        try {
            final FileOutputStream f = new FileOutputStream(new File("maps/map" + this.id));
            final OutputStream outputStream = new DeflaterOutputStream(f);
            final Output output = new Output(outputStream);
            game.kryo.writeObject(output, (Object)this);
            output.close();
            f.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}
