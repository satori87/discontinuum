// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.scenes;

import com.badlogic.gdx.physics.box2d.Shape;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bbg.dc.Map;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bbg.dc.Att;
import com.bbg.dc.Tile;
import com.badlogic.gdx.Gdx;
import com.bbg.dc.AssetLoader;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.bbg.dc.iface.Vector;
import com.bbg.dc.aistuff.MapNode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.bbg.dc.aistuff.Wall;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import java.util.Iterator;
import com.badlogic.gdx.graphics.Color;
import java.io.IOException;
import java.io.FileNotFoundException;
import com.esotericsoftware.kryo.io.Input;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import java.io.FileInputStream;
import java.io.File;
import com.bbg.dc.DCGame;
import com.bbg.dc.DrawTask;
import java.util.LinkedList;
import com.bbg.dc.iface.ListBox;
import com.bbg.dc.iface.Frame;
import com.bbg.dc.iface.Label;
import com.bbg.dc.aistuff.MapGraph;
import com.bbg.dc.MapData;
import com.bbg.dc.iface.Button;
import java.util.List;
import com.bbg.dc.iface.Scene;

public class MapScene extends Scene
{
    List<Button> layerBtns;
    List<Button> visBtns;
    public MapData[] map;
    public MapGraph[] mapGraph;
    int layer;
    boolean cursor;
    boolean[] vis;
    int curSet;
    int curSetCat;
    int curTile;
    int curTileSet;
    int curWall;
    int[] curTiles;
    int[] curTileSets;
    Label set;
    Label cat;
    Frame editFX;
    Label lblFXScl;
    Label lblFX;
    Label lblFXX;
    Label lblFXY;
    Label lblFXL;
    int curFXScl;
    int curFXX;
    int curFXY;
    int curFX;
    int curFXL;
    Frame editSpawn;
    Label spawnRange;
    int curSpawnRange;
    Label spawnMon;
    int curSpawnMon;
    Button spawnStand;
    Label spawnProb;
    int curSpawnProb;
    Frame editWindow;
    Label windist;
    int curWindist;
    int curWindir;
    Label windir;
    Frame editEdge;
    Button edgeWall;
    Button edgeSeed;
    Button edgeInvert;
    int curEdgeL;
    Label lblEdgeL;
    Frame editWarp;
    Label warpM;
    Label warpX;
    Label warpY;
    Label warpS;
    int curWarpM;
    int curWarpX;
    int curWarpY;
    int curWarpS;
    Frame editWall;
    Button shadowToo;
    Frame editMobj;
    Label lblMobj;
    Label lblMSet;
    Label lblML;
    Label lblMY;
    Button btnIgnoreWall;
    int dragging;
    int dragX;
    int dragY;
    int oDragX;
    int oDragY;
    int mapX;
    int mapY;
    int fbset;
    int fbtile;
    int flayer;
    int fset;
    int ftile;
    public int curMap;
    public long moveStamp;
    public Label lblSE;
    public Label lblSEVol;
    public Label lblSERange;
    public Label lblSEFreq;
    public Frame editSE;
    public Button btnSE2D;
    public Button btnSEConstant;
    public int curSE;
    public int curSERange;
    public int curSEVol;
    public int curSEFreq;
    public Frame mapFrame;
    public Frame editLight;
    public Button listEdit;
    public Button listBack;
    public ListBox mapList;
    public Label lblTile;
    public Label lblHover;
    public Button btnGrid;
    public int lamp;
    public long lampAt;
    public boolean lampup;
    public int curAtt;
    public int curMobj;
    public int curMSet;
    public int curYO;
    public int curML;
    int oldlayer;
    int dX;
    int dY;
    int mX;
    int mY;
    int wx;
    int wy;
    int curR;
    int curG;
    int curB;
    int curA;
    int curDist;
    int curX;
    int curY;
    Label r;
    Label g;
    Label b;
    Label a;
    Label d;
    Label x;
    Label y;
    Label lblRays;
    Label lblSoftness;
    Button f;
    Button s;
    Button xr;
    int softness;
    int rays;
    public boolean noding;
    public int curNode;
    public LinkedList<DrawTask>[] drawList;
    Button btnNode;
    boolean raycast;
    
    public MapScene(final DCGame game) {
        this.layerBtns = new LinkedList<Button>();
        this.visBtns = new LinkedList<Button>();
        this.layer = 0;
        this.cursor = true;
        this.vis = new boolean[12];
        this.curSet = 0;
        this.curSetCat = 0;
        this.curTile = 0;
        this.curTileSet = 0;
        this.curWall = 0;
        this.curTiles = new int[12];
        this.curTileSets = new int[12];
        this.curSpawnRange = 0;
        this.curSpawnMon = 0;
        this.curEdgeL = 0;
        this.curWarpM = 0;
        this.curWarpX = 50;
        this.curWarpY = 50;
        this.curWarpS = 0;
        this.dragging = 0;
        this.dragX = 0;
        this.dragY = 0;
        this.oDragX = 0;
        this.oDragY = 0;
        this.mapX = 0;
        this.mapY = 0;
        this.fbset = 0;
        this.fbtile = 0;
        this.flayer = 0;
        this.fset = 0;
        this.ftile = 0;
        this.curMap = -1;
        this.moveStamp = 0L;
        this.lamp = 0;
        this.lampAt = 0L;
        this.lampup = true;
        this.curAtt = 0;
        this.curMobj = 0;
        this.curMSet = 0;
        this.curYO = 0;
        this.curML = 0;
        this.oldlayer = 0;
        this.dX = 0;
        this.dY = 0;
        this.mX = 0;
        this.mY = 0;
        this.wx = 0;
        this.wy = 0;
        this.curR = 0;
        this.curG = 0;
        this.curB = 0;
        this.curA = 1;
        this.curDist = 0;
        this.curX = 0;
        this.curY = 0;
        this.softness = 0;
        this.rays = 32;
        this.noding = false;
        this.curNode = 0;
        this.raycast = false;
        this.drawList = (LinkedList<DrawTask>[])new LinkedList[1100];
        this.game = game;
        for (int y = 0; y < 1088; ++y) {
            this.drawList[y] = new LinkedList<DrawTask>();
        }
        for (int v = 0; v < 12; ++v) {
            this.vis[v] = true;
            this.curTiles[v] = 0;
            this.curTileSets[v] = 0;
        }
        this.map = new MapData[100];
        new File("maps").mkdirs();
        this.mapGraph = new MapGraph[100];
    }
    
    public void loadMaps() {
        for (int m = 0; m < 100; ++m) {
            this.map[m] = new MapData(this.game, m);
            if (new File("maps/map" + m).exists()) {
                this.map[m].load(this.game);
            }
        }
    }
    
    public void loadGraphs() {
        for (int m = 0; m < 100; ++m) {
            this.mapGraph[m] = new MapGraph();
            if (new File("maps/mapgraph" + m).exists()) {
                try {
                    final FileInputStream f = new FileInputStream(new File("maps/mapgraph" + m));
                    final InputStream inputStream = new InflaterInputStream(f);
                    final Input input = new Input(inputStream);
                    this.mapGraph[m] = (MapGraph)this.game.kryo.readObject(input, (Class)MapGraph.class);
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
        }
    }
    
    @Override
    public void clear() {
        super.clear();
    }
    
    @Override
    public void start() {
        super.start();
        if (this.layer == 11) {
            this.layer = 9;
        }
        this.addButtons(true, true, 1, 820, 24, 64, 28, 0, new String[] { "Prev" }, new int[1]);
        this.addButtons(true, true, 1, 960, 24, 64, 28, 0, new String[] { "Next" }, new int[] { 1 });
        this.addButtons(true, true, 1, 1040, 24, 64, 28, 0, new String[] { "Prev" }, new int[] { 46 });
        this.addButtons(true, true, 1, 1180, 24, 64, 28, 0, new String[] { "Next" }, new int[] { 47 });
        this.set = new Label(this.game, 890, 24, 1.0f, "Set: 0", Color.WHITE, true);
        this.cat = new Label(this.game, 1115, 24, 1.0f, "Terrain", Color.WHITE, true);
        this.labels.add(this.set);
        this.labels.add(this.cat);
        this.frames.add(new Frame(this.game, 779, 48, 522, 522, false, false));
        this.frames.add(new Frame(this.game, 779, 582, 42, 42, false, false));
        this.frames.add(new Frame(this.game, 843, 582, 170, 42, false, false));
        this.layerBtns = this.addButtons(false, false, 2, 780, 646, 44, 24, 4, new String[] { "BG1", "BG2", "BG3", "BG4", "Mid", "FG1", "FG2", "FG3", "Wall", "Att", "Shdw" }, new int[] { 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110 });
        this.layerBtns.get(this.layer).toggle = true;
        this.visBtns = this.addButtons(false, false, 2, 780, 678, 44, 24, 4, new String[] { "Vis", "Vis", "Vis", "Vis", "Vis", "Vis", "Vis", "Vis", "Vis", "Vis", "Vis" }, new int[] { 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210 });
        for (final Button b : this.visBtns) {
            b.toggle = true;
        }
        this.addButtons(false, false, 1, 900, 744, 48, 24, 8, new String[] { "Load", "Save", "Opts", "Test", "Back" }, new int[] { 12, 13, 14, 21, 15 });
        this.mapFrame = new Frame(this.game, 512, 384, 600, 400, true, true);
        final String[] names = new String[100];
        final int[] data = new int[100];
        for (int i = 0; i < 100; ++i) {
            names[i] = String.valueOf(i) + ": " + this.map[i].name;
            data[i] = i;
        }
        (this.mapList = new ListBox(this.game, 1, 0, 212, 184, 600, 200, names, data)).start();
        this.listEdit = new Button(this.game, 17, 1, 750, 556, 80, 36, "Edit");
        this.listBack = new Button(this.game, 16, 1, 270, 556, 80, 36, "Back");
        this.lblTile = new Label(this.game, 1250, 24, 1.0f, String.valueOf(this.curTileSet) + ":" + this.curTile, Color.WHITE, true);
        this.labels.add(this.lblTile);
        this.lblHover = new Label(this.game, 1250, 38, 1.0f, "", Color.WHITE, true);
        this.labels.add(this.lblHover);
        this.editFX = new Frame(this.game, 0, 0, 100, 100, false, false);
        this.editFX.buttons.add(new Button(this.game, 72, 1, 950, 420, 80, 32, "Prev"));
        this.editFX.buttons.add(new Button(this.game, 73, 1, 1150, 420, 80, 32, "Next"));
        this.editFX.buttons.add(new Button(this.game, 74, 1, 950, 460, 80, 32, "Prev"));
        this.editFX.buttons.add(new Button(this.game, 75, 1, 1150, 460, 80, 32, "Next"));
        this.editFX.buttons.add(new Button(this.game, 76, 1, 950, 500, 80, 32, "Prev"));
        this.editFX.buttons.add(new Button(this.game, 77, 1, 1150, 500, 80, 32, "Next"));
        this.editFX.buttons.add(new Button(this.game, 78, 1, 950, 540, 80, 32, "Prev"));
        this.editFX.buttons.add(new Button(this.game, 79, 1, 1150, 540, 80, 32, "Next"));
        this.editFX.buttons.add(new Button(this.game, 80, 1, 950, 380, 80, 32, "Prev"));
        this.editFX.buttons.add(new Button(this.game, 81, 1, 1150, 380, 80, 32, "Next"));
        this.lblFX = new Label(this.game, 1050, 420, 1.0f, "", Color.WHITE, true);
        this.lblFXL = new Label(this.game, 1050, 460, 1.0f, "", Color.WHITE, true);
        this.lblFXX = new Label(this.game, 1050, 500, 1.0f, "", Color.WHITE, true);
        this.lblFXY = new Label(this.game, 1050, 540, 1.0f, "", Color.WHITE, true);
        this.lblFXScl = new Label(this.game, 1050, 380, 1.0f, "", Color.WHITE, true);
        this.editFX.labels.add(this.lblFX);
        this.editFX.labels.add(this.lblFXL);
        this.editFX.labels.add(this.lblFXX);
        this.editFX.labels.add(this.lblFXY);
        this.editFX.labels.add(this.lblFXScl);
        this.editFX.useFrame = false;
        this.editFX.visible = false;
        this.frames.add(this.editFX);
        this.editEdge = new Frame(this.game, 0, 0, 100, 100, false, false);
        this.editEdge.buttons.add(new Button(this.game, 69, 1, 950, 500, 80, 32, "Prev"));
        this.editEdge.buttons.add(new Button(this.game, 70, 1, 1150, 500, 80, 32, "Next"));
        this.edgeWall = new Button(this.game, 67, 2, 950, 460, 80, 32, "Wall");
        this.edgeSeed = new Button(this.game, 68, 2, 950, 420, 80, 32, "Seed");
        this.edgeInvert = new Button(this.game, 71, 2, 1150, 420, 80, 32, "Invert");
        this.editEdge.buttons.add(this.edgeWall);
        this.editEdge.buttons.add(this.edgeSeed);
        this.editEdge.buttons.add(this.edgeInvert);
        this.lblEdgeL = new Label(this.game, 1050, 500, 1.0f, "", Color.WHITE, true);
        this.editEdge.labels.add(this.lblEdgeL);
        this.editEdge.useFrame = false;
        this.editEdge.visible = false;
        this.frames.add(this.editEdge);
        this.editSE = new Frame(this.game, 0, 0, 100, 100, false, false);
        this.editSE.buttons.add(new Button(this.game, 59, 1, 950, 460, 80, 32, "Prev"));
        this.editSE.buttons.add(new Button(this.game, 60, 1, 1150, 460, 80, 32, "Next"));
        this.editSE.buttons.add(new Button(this.game, 61, 1, 950, 500, 80, 32, "Prev"));
        this.editSE.buttons.add(new Button(this.game, 62, 1, 1150, 500, 80, 32, "Next"));
        this.editSE.buttons.add(new Button(this.game, 63, 1, 950, 540, 80, 32, "Prev"));
        this.editSE.buttons.add(new Button(this.game, 64, 1, 1150, 540, 80, 32, "Next"));
        this.editSE.buttons.add(new Button(this.game, 88, 1, 950, 380, 80, 32, "Prev"));
        this.editSE.buttons.add(new Button(this.game, 89, 1, 1150, 380, 80, 32, "Next"));
        this.btnSE2D = new Button(this.game, 65, 2, 1100, 420, 180, 32, "2D");
        this.btnSEConstant = new Button(this.game, 66, 2, 950, 420, 80, 32, "Loop");
        this.editSE.useFrame = false;
        this.editSE.visible = false;
        this.frames.add(this.editSE);
        this.editSE.buttons.add(this.btnSE2D);
        this.editSE.buttons.add(this.btnSEConstant);
        this.lblSE = new Label(this.game, 1050, 460, 1.0f, "", Color.WHITE, true);
        this.lblSEVol = new Label(this.game, 1050, 500, 1.0f, "", Color.WHITE, true);
        this.lblSERange = new Label(this.game, 1050, 540, 1.0f, "", Color.WHITE, true);
        this.lblSEFreq = new Label(this.game, 1050, 380, 1.0f, "", Color.WHITE, true);
        this.editSE.labels.add(this.lblSE);
        this.editSE.labels.add(this.lblSEVol);
        this.editSE.labels.add(this.lblSERange);
        this.editSE.labels.add(this.lblSEFreq);
        this.editSpawn = new Frame(this.game, 0, 0, 100, 100, false, false);
        this.editSpawn.buttons.add(new Button(this.game, 82, 1, 950, 460, 80, 32, "Prev"));
        this.editSpawn.buttons.add(new Button(this.game, 83, 1, 1150, 460, 80, 32, "Next"));
        this.editSpawn.buttons.add(new Button(this.game, 84, 1, 950, 500, 80, 32, "Prev"));
        this.editSpawn.buttons.add(new Button(this.game, 85, 1, 1150, 500, 80, 32, "Next"));
        this.editSpawn.buttons.add(new Button(this.game, 86, 1, 950, 540, 80, 32, "Prev"));
        this.editSpawn.buttons.add(new Button(this.game, 87, 1, 1150, 540, 80, 32, "Next"));
        this.spawnRange = new Label(this.game, 1050, 460, 1.0f, "", Color.WHITE, true);
        this.spawnMon = new Label(this.game, 1050, 500, 1.0f, "", Color.WHITE, true);
        this.spawnProb = new Label(this.game, 1050, 540, 1.0f, "", Color.WHITE, true);
        this.editSpawn.labels.add(this.spawnRange);
        this.editSpawn.labels.add(this.spawnMon);
        this.editSpawn.labels.add(this.spawnProb);
        this.editSpawn.visible = false;
        this.editSpawn.useFrame = false;
        this.frames.add(this.editSpawn);
        this.spawnStand = new Button(this.game, 1010, 2, 1050, 420, 160, 32, "Stands");
        this.editSpawn.buttons.add(this.spawnStand);
        this.editWindow = new Frame(this.game, 0, 0, 100, 100, false, false);
        this.editWindow.buttons.add(new Button(this.game, 55, 1, 950, 460, 80, 32, "Prev"));
        this.editWindow.buttons.add(new Button(this.game, 56, 1, 1150, 460, 80, 32, "Next"));
        this.editWindow.buttons.add(new Button(this.game, 57, 1, 950, 500, 80, 32, "Prev"));
        this.editWindow.buttons.add(new Button(this.game, 58, 1, 1150, 500, 80, 32, "Next"));
        this.windist = new Label(this.game, 1050, 460, 1.0f, "", Color.WHITE, true);
        this.windir = new Label(this.game, 1050, 500, 1.0f, "", Color.WHITE, true);
        this.editWindow.labels.add(this.windist);
        this.editWindow.labels.add(this.windir);
        this.editWindow.visible = false;
        this.editWindow.useFrame = false;
        this.frames.add(this.editWindow);
        this.editWarp = new Frame(this.game, 0, 0, 100, 100, false, false);
        this.editWarp.buttons.add(new Button(this.game, 65, 1, 950, 420, 80, 32, "Prev"));
        this.editWarp.buttons.add(new Button(this.game, 66, 1, 1150, 420, 80, 32, "Next"));
        this.editWarp.buttons.add(new Button(this.game, 49, 1, 950, 460, 80, 32, "Prev"));
        this.editWarp.buttons.add(new Button(this.game, 50, 1, 1150, 460, 80, 32, "Next"));
        this.editWarp.buttons.add(new Button(this.game, 51, 1, 950, 500, 80, 32, "Prev"));
        this.editWarp.buttons.add(new Button(this.game, 52, 1, 1150, 500, 80, 32, "Next"));
        this.editWarp.buttons.add(new Button(this.game, 53, 1, 950, 540, 80, 32, "Prev"));
        this.editWarp.buttons.add(new Button(this.game, 54, 1, 1150, 540, 80, 32, "Next"));
        this.warpM = new Label(this.game, 1050, 460, 1.0f, "Map", Color.WHITE, true);
        this.warpX = new Label(this.game, 1050, 500, 1.0f, "X", Color.WHITE, true);
        this.warpY = new Label(this.game, 1050, 540, 1.0f, "Y", Color.WHITE, true);
        this.warpS = new Label(this.game, 1050, 420, 1.0f, "Sound:", Color.WHITE, true);
        this.editWarp.labels.add(this.warpS);
        this.editWarp.labels.add(this.warpM);
        this.editWarp.labels.add(this.warpX);
        this.editWarp.labels.add(this.warpY);
        this.editWarp.visible = false;
        this.editWarp.useFrame = false;
        this.frames.add(this.editWarp);
        this.editWall = new Frame(this.game, 0, 0, 100, 100, false, false);
        this.shadowToo = new Button(this.game, 48, 2, 1050, 420, 160, 32, "Lightwall");
        this.editWall.buttons.add(this.shadowToo);
        this.editWall.visible = false;
        this.editWall.useFrame = false;
        this.frames.add(this.editWall);
        this.editMobj = new Frame(this.game, 0, 0, 100, 100, false, false);
        this.editMobj.buttons.add(new Button(this.game, 18, 1, 950, 500, 80, 32, "Prev"));
        this.editMobj.buttons.add(new Button(this.game, 19, 1, 1150, 500, 80, 32, "Next"));
        this.editMobj.buttons.add(new Button(this.game, 22, 1, 950, 540, 80, 32, "Prev"));
        this.editMobj.buttons.add(new Button(this.game, 23, 1, 1150, 540, 80, 32, "Next"));
        this.editMobj.buttons.add(new Button(this.game, 24, 1, 950, 460, 80, 32, "Prev"));
        this.editMobj.buttons.add(new Button(this.game, 25, 1, 1150, 460, 80, 32, "Next"));
        this.editMobj.buttons.add(new Button(this.game, 44, 1, 950, 420, 80, 32, "Prev"));
        this.editMobj.buttons.add(new Button(this.game, 45, 1, 1150, 420, 80, 32, "Next"));
        this.btnIgnoreWall = new Button(this.game, 43, 2, 1050, 380, 160, 32, "Ignore Wall");
        this.editMobj.buttons.add(this.btnIgnoreWall);
        this.lblML = new Label(this.game, 1050, 460, 1.0f, "0", Color.WHITE, true);
        this.lblMobj = new Label(this.game, 1050, 500, 1.0f, "0", Color.WHITE, true);
        this.lblMSet = new Label(this.game, 1050, 540, 1.0f, "0", Color.WHITE, true);
        this.lblMY = new Label(this.game, 1050, 420, 1.0f, "0", Color.WHITE, true);
        this.editMobj.labels.add(this.lblMobj);
        this.editMobj.labels.add(this.lblMSet);
        this.editMobj.labels.add(this.lblML);
        this.editMobj.labels.add(this.lblMY);
        this.editMobj.visible = false;
        this.editMobj.useFrame = false;
        this.frames.add(this.editMobj);
        this.editLight = new Frame(this.game, 0, 0, 100, 100, false, false);
        this.editLight.buttons.add(new Button(this.game, 26, 1, 950, 300, 80, 32, "-"));
        this.editLight.buttons.add(new Button(this.game, 27, 1, 1150, 300, 80, 32, "+"));
        this.editLight.buttons.add(new Button(this.game, 28, 1, 950, 340, 80, 32, "-"));
        this.editLight.buttons.add(new Button(this.game, 29, 1, 1150, 340, 80, 32, "+"));
        this.editLight.buttons.add(new Button(this.game, 30, 1, 950, 380, 80, 32, "-"));
        this.editLight.buttons.add(new Button(this.game, 31, 1, 1150, 380, 80, 32, "+"));
        this.editLight.buttons.add(new Button(this.game, 32, 1, 950, 420, 80, 32, "-"));
        this.editLight.buttons.add(new Button(this.game, 33, 1, 1150, 420, 80, 32, "+"));
        this.editLight.buttons.add(new Button(this.game, 34, 1, 950, 460, 80, 32, "-"));
        this.editLight.buttons.add(new Button(this.game, 35, 1, 1150, 460, 80, 32, "+"));
        this.editLight.buttons.add(new Button(this.game, 36, 1, 950, 500, 80, 32, "-"));
        this.editLight.buttons.add(new Button(this.game, 37, 1, 1150, 500, 80, 32, "+"));
        this.editLight.buttons.add(new Button(this.game, 38, 1, 950, 540, 80, 32, "-"));
        this.editLight.buttons.add(new Button(this.game, 39, 1, 1150, 540, 80, 32, "+"));
        this.editLight.buttons.add(new Button(this.game, 700, 1, 950, 260, 80, 32, "-"));
        this.editLight.buttons.add(new Button(this.game, 701, 1, 1150, 260, 80, 32, "+"));
        this.editLight.buttons.add(new Button(this.game, 702, 1, 950, 220, 80, 32, "-"));
        this.editLight.buttons.add(new Button(this.game, 703, 1, 1150, 220, 80, 32, "+"));
        this.r = new Label(this.game, 1050, 300, 1.0f, "r", Color.WHITE, true);
        this.g = new Label(this.game, 1050, 340, 1.0f, "g", Color.WHITE, true);
        this.b = new Label(this.game, 1050, 380, 1.0f, "b", Color.WHITE, true);
        this.a = new Label(this.game, 1050, 420, 1.0f, "a", Color.WHITE, true);
        this.d = new Label(this.game, 1050, 460, 1.0f, "d", Color.WHITE, true);
        this.x = new Label(this.game, 1050, 500, 1.0f, "x", Color.WHITE, true);
        this.y = new Label(this.game, 1050, 540, 1.0f, "y", Color.WHITE, true);
        this.f = new Button(this.game, 40, 2, 950, 180, 80, 32, "Flicker");
        this.s = new Button(this.game, 41, 2, 1050, 180, 80, 32, "Soft");
        this.xr = new Button(this.game, 42, 2, 1150, 180, 80, 32, "Xray");
        this.lblRays = new Label(this.game, 1050, 260, 1.0f, "rays", Color.WHITE, true);
        this.lblSoftness = new Label(this.game, 1050, 220, 1.0f, "soft", Color.WHITE, true);
        this.editLight.buttons.add(this.f);
        this.editLight.buttons.add(this.s);
        this.editLight.buttons.add(this.xr);
        this.editLight.labels.add(this.r);
        this.editLight.labels.add(this.g);
        this.editLight.labels.add(this.b);
        this.editLight.labels.add(this.a);
        this.editLight.labels.add(this.d);
        this.editLight.labels.add(this.x);
        this.editLight.labels.add(this.y);
        this.editLight.labels.add(this.lblRays);
        this.editLight.labels.add(this.lblSoftness);
        this.editLight.visible = false;
        this.editLight.useFrame = false;
        this.frames.add(this.editLight);
        this.btnGrid = new Button(this.game, 20, 2, 1220, 744, 80, 24, "Grid");
        this.buttons.add(this.btnGrid);
        this.btnNode = new Button(this.game, 300, 1, 1284, 614, 44, 24, "Node");
        this.buttons.add(this.btnNode);
    }
    
    public Body startBody(final World ww, final int m, final float x, final float y) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x / 100.0f, y / 100.0f);
        final Body b = ww.createBody(bodyDef);
        return b;
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
    
    void calcNodes() {
        final World world = new World(new Vector2(0.0f, 0.0f), true);
        for (int x = 0; x < 64; ++x) {
            for (int y = 0; y < 64; ++y) {
                final Body bo = this.startBody(world, this.curMap, (float)(x * 32 + 16), (float)(y * 32 + 16));
                for (int w = 0; w < 8; ++w) {
                    for (int h = 0; h < 8; ++h) {
                        final int wx = x * 8 + w;
                        final int wy = y * 8 + h;
                        if (wx >= 0 && wx < 512 && wy >= 0 && wy < 512) {
                            if (x >= 0 && y >= 0 && x < 64 && y < 64) {
                                if (this.map[this.curMap].wall[wx][wy]) {
                                    this.addWallBody(bo, 4.0f, 4.0f, (float)(w * 4 - 16), (float)(h * 4 - 16));
                                }
                            }
                            else {
                                this.addWallBody(bo, 4.0f, 4.0f, (float)(w * 4 - 16), (float)(h * 4 - 16));
                            }
                        }
                    }
                }
            }
        }
        for (int x = 0; x < 64; ++x) {
            for (int y = 0; y < 64; ++y) {
                final MapNode mn = new MapNode(x * 64 + y, x, y);
                mn.active = false;
                if (!this.hasWall(x * 32 + 16, y * 32 + 16)) {
                    mn.active = true;
                }
                this.mapGraph[this.curMap].mapNodes.set(x * 64 + y, mn);
            }
        }
        for (int x = 0; x < 64; ++x) {
            for (int y = 0; y < 64; ++y) {
                final MapNode mn = this.mapGraph[this.curMap].mapNodes.get(x * 64 + y);
                mn.connections.clear();
                if (mn.active) {
                    for (final MapNode mm : this.mapGraph[this.curMap].mapNodes) {
                        if (mm.active && mn.index != mm.index && (mn.x != mm.x || mn.y != mm.y) && Vector.distance((float)mn.x, (float)mn.y, (float)mm.x, (float)mm.y) <= 1.9f && !this.raycast(world, (float)mn.getTrueX(), (float)mn.getTrueY(), (float)mm.getTrueX(), (float)mm.getTrueY(), 8.0f)) {
                            mn.connections.add(mm.index);
                        }
                    }
                }
            }
        }
        world.dispose();
    }
    
    public boolean raycast(final World world, final float x1, final float y1, final float x2, final float y2, final float w) {
        this.raycast = false;
        final RayCastCallback raycastJustWalls = (RayCastCallback)new RayCastCallback() {
            public float reportRayFixture(final Fixture fixture, final Vector2 point, final Vector2 normal, final float fraction) {
                if (fixture.getBody().getType() == BodyDef.BodyType.StaticBody) {
                    MapScene.this.raycast = true;
                }
                return 1.0f;
            }
        };
        final Vector v = Vector.byChange(x2 - x1, y2 - y1);
        world.rayCast(raycastJustWalls, x1 / 100.0f, y1 / 100.0f, x2 / 100.0f, y2 / 100.0f);
        if (!this.raycast) {
            Vector v2 = new Vector(v.direction + 1.5707964f, w);
            world.rayCast(raycastJustWalls, (x1 + v2.xChange) / 100.0f, (y1 + v2.yChange) / 100.0f, (x2 + v2.xChange) / 100.0f, (y2 + v2.yChange) / 100.0f);
            if (!this.raycast) {
                v2 = new Vector(v.direction - 1.5707964f, w);
                world.rayCast(raycastJustWalls, (x1 + v2.xChange) / 100.0f, (y1 + v2.yChange) / 100.0f, (x2 + v2.xChange) / 100.0f, (y2 + v2.yChange) / 100.0f);
            }
        }
        return this.raycast;
    }
    
    public boolean hasWall(final int pX, final int pY) {
        int h = 0;
        if (pX >= 0 && pY >= 0 && pX < 2048 && pY < 2048) {
            for (int w = pX / 4 - 4; w <= pX / 4 + 4; ++w) {
                for (int hh = pY / 4 - 4; hh <= pY / 4 + 4; ++hh) {
                    h = hh;
                    if (w >= 0 && h >= 0 && w < 512 && h < 512 && this.map[this.curMap].wall[w][h]) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }
    
    @Override
    public void buttonPressed(final int id) {
        if (id >= 100 && id <= 110) {
            this.btnNode.toggle = false;
            this.oldlayer = this.layer;
            this.layer = id - 100;
            if (this.oldlayer != this.layer && this.oldlayer == 11) {
                this.editMobj.visible = false;
            }
            for (final Button b : this.layerBtns) {
                b.toggle = false;
            }
            this.layerBtns.get(this.layer).toggle = true;
        }
        else if (id == 700) {
            --this.rays;
            if (this.rays < 32) {
                this.rays = 32;
            }
        }
        else if (id == 701) {
            ++this.rays;
            if (this.rays > 256) {
                this.rays = 256;
            }
        }
        else if (id == 702) {
            --this.softness;
            if (this.softness < 0) {
                this.softness = 0;
            }
        }
        else if (id == 703) {
            ++this.softness;
            if (this.softness > 20) {
                this.softness = 20;
            }
        }
        else if (id == 300) {
            this.calcNodes();
        }
        else if (id >= 200 && id <= 210) {
            this.vis[id - 200] = !this.vis[id - 200];
            this.visBtns.get(id - 200).toggle = this.vis[id - 200];
        }
        else {
            switch (id) {
                case 46: {
                    --this.curSetCat;
                    if (this.curSetCat < 0) {
                        this.curSetCat = 5;
                    }
                    if (this.curSet > AssetLoader.numTileset(this.curSetCat) - 1) {
                        this.curSet = 0;
                        break;
                    }
                    break;
                }
                case 47: {
                    ++this.curSetCat;
                    if (this.curSetCat > 5) {
                        this.curSetCat = 0;
                    }
                    if (this.curSet > AssetLoader.numTileset(this.curSetCat) - 1) {
                        this.curSet = 0;
                        break;
                    }
                    break;
                }
                case 0: {
                    --this.curSet;
                    if (this.curSet < 0) {
                        this.curSet = AssetLoader.numTileset(this.curSetCat) - 1;
                        break;
                    }
                    break;
                }
                case 1: {
                    ++this.curSet;
                    if (this.curSet > AssetLoader.numTileset(this.curSetCat) - 1) {
                        this.curSet = 0;
                        break;
                    }
                    break;
                }
                case 12: {
                    if (this.curMap >= 0 && new File("maps/map" + this.curMap).exists()) {
                        this.map[this.curMap].load(this.game);
                        break;
                    }
                    break;
                }
                case 13: {
                    this.map[this.curMap].save(this.game);
                    this.mapGraph[this.curMap].save(this.game, this.curMap);
                    break;
                }
                case 14: {
                    this.game.mapOptions.curMap = this.curMap;
                    this.game.changeScene(this.game.mapOptions);
                    break;
                }
                case 15: {
                    if (this.curMap >= 0 && new File("maps/map" + this.curMap).exists()) {
                        this.map[this.curMap].load(this.game);
                    }
                    this.curMap = -1;
                    this.switchTo();
                    Gdx.graphics.setTitle("DC - Editing Map " + this.curMap);
                    break;
                }
                case 16: {
                    this.game.changeScene(this.game.mainMenu);
                    break;
                }
                case 17: {
                    this.curMap = this.mapList.sel;
                    Gdx.graphics.setTitle("DC - Editing Map " + this.curMap);
                    break;
                }
                case 18: {
                    --this.curMobj;
                    if (this.curMobj < 0) {
                        this.curMobj = AssetLoader.numMobj(this.curMSet) - 1;
                        break;
                    }
                    break;
                }
                case 19: {
                    ++this.curMobj;
                    if (this.curMobj >= AssetLoader.numMobj(this.curMSet)) {
                        this.curMobj = 0;
                        break;
                    }
                    break;
                }
                case 21: {
                    this.doEdges();
                    this.game.changeScene(this.game.gameScene);
                    this.game.gameScene.player.warp(this.curMap, this.mapX + 11, this.mapY + 11, -1);
                    break;
                }
                case 22: {
                    --this.curMSet;
                    if (this.curMSet < 0) {
                        this.curMSet = 9;
                        break;
                    }
                    break;
                }
                case 23: {
                    ++this.curMSet;
                    if (this.curMSet >= 10) {
                        this.curMSet = 0;
                        break;
                    }
                    break;
                }
                case 24: {
                    --this.curML;
                    if (this.curML < 0) {
                        this.curML = 7;
                        break;
                    }
                    break;
                }
                case 25: {
                    ++this.curML;
                    if (this.curML > 7) {
                        this.curML = 0;
                        break;
                    }
                    break;
                }
                case 26: {
                    --this.curR;
                    if (this.curR < 0) {
                        this.curR = 0;
                        break;
                    }
                    break;
                }
                case 27: {
                    ++this.curR;
                    if (this.curR > 255) {
                        this.curR = 255;
                        break;
                    }
                    break;
                }
                case 28: {
                    --this.curG;
                    if (this.curG < 0) {
                        this.curG = 0;
                        break;
                    }
                    break;
                }
                case 29: {
                    ++this.curG;
                    if (this.curG > 255) {
                        this.curG = 255;
                        break;
                    }
                    break;
                }
                case 30: {
                    --this.curB;
                    if (this.curB < 0) {
                        this.curB = 0;
                        break;
                    }
                    break;
                }
                case 31: {
                    ++this.curB;
                    if (this.curB > 255) {
                        this.curB = 255;
                        break;
                    }
                    break;
                }
                case 32: {
                    --this.curA;
                    if (this.curA < 0) {
                        this.curA = 0;
                        break;
                    }
                    break;
                }
                case 33: {
                    ++this.curA;
                    if (this.curA > 255) {
                        this.curA = 255;
                        break;
                    }
                    break;
                }
                case 34: {
                    --this.curDist;
                    if (this.curDist < 0) {
                        this.curDist = 0;
                        break;
                    }
                    break;
                }
                case 35: {
                    ++this.curDist;
                    if (this.curDist > 100) {
                        this.curDist = 100;
                        break;
                    }
                    break;
                }
                case 36: {
                    --this.curX;
                    if (this.curX < -100) {
                        this.curX = -100;
                        break;
                    }
                    break;
                }
                case 37: {
                    ++this.curX;
                    if (this.curX > 100) {
                        this.curX = 100;
                        break;
                    }
                    break;
                }
                case 38: {
                    --this.curY;
                    if (this.curY < -100) {
                        this.curY = -100;
                        break;
                    }
                    break;
                }
                case 39: {
                    ++this.curY;
                    if (this.curY > 100) {
                        this.curY = 100;
                        break;
                    }
                    break;
                }
                case 44: {
                    --this.curYO;
                    if (this.curYO < -100) {
                        this.curYO = -100;
                        break;
                    }
                    break;
                }
                case 45: {
                    ++this.curYO;
                    if (this.curYO > 100) {
                        this.curYO = 100;
                        break;
                    }
                    break;
                }
                case 49: {
                    --this.curWarpM;
                    if (this.curWarpM < 0) {
                        this.curWarpM = 0;
                        break;
                    }
                    break;
                }
                case 50: {
                    ++this.curWarpM;
                    if (this.curWarpM > 63) {
                        this.curWarpM = 63;
                        break;
                    }
                    break;
                }
                case 51: {
                    --this.curWarpX;
                    if (this.curWarpX < 0) {
                        this.curWarpX = 0;
                        break;
                    }
                    break;
                }
                case 52: {
                    ++this.curWarpX;
                    if (this.curWarpX > 63) {
                        this.curWarpX = 63;
                        break;
                    }
                    break;
                }
                case 53: {
                    --this.curWarpY;
                    if (this.curWarpY < 0) {
                        this.curWarpY = 0;
                        break;
                    }
                    break;
                }
                case 54: {
                    ++this.curWarpY;
                    if (this.curWarpY > 63) {
                        this.curWarpY = 63;
                        break;
                    }
                    break;
                }
                case 55: {
                    --this.curWindist;
                    if (this.curWindist < 0) {
                        this.curWindist = 0;
                        break;
                    }
                    break;
                }
                case 56: {
                    ++this.curWindist;
                    if (this.curWindist > 50) {
                        this.curWindist = 50;
                        break;
                    }
                    break;
                }
                case 57: {
                    --this.curWindir;
                    if (this.curWindir < 0) {
                        this.curWindir = 3;
                        break;
                    }
                    break;
                }
                case 58: {
                    ++this.curWindir;
                    if (this.curWindir > 3) {
                        this.curWindir = 0;
                        break;
                    }
                    break;
                }
                case 59: {
                    --this.curSE;
                    if (this.curSE < 0) {
                        this.curSE = 0;
                        break;
                    }
                    break;
                }
                case 60: {
                    ++this.curSE;
                    if (this.curSE > 99) {
                        this.curSE = 99;
                        break;
                    }
                    break;
                }
                case 61: {
                    --this.curSEVol;
                    if (this.curSEVol < 0) {
                        this.curSEVol = 0;
                        break;
                    }
                    break;
                }
                case 62: {
                    ++this.curSEVol;
                    if (this.curSEVol > 100) {
                        this.curSEVol = 100;
                        break;
                    }
                    break;
                }
                case 63: {
                    --this.curSERange;
                    if (this.curSERange < 0) {
                        this.curSERange = 0;
                        break;
                    }
                    break;
                }
                case 64: {
                    ++this.curSERange;
                    if (this.curSERange > 64) {
                        this.curSERange = 64;
                        break;
                    }
                    break;
                }
                case 65: {
                    --this.curWarpS;
                    if (this.curWarpS < 0) {
                        this.curWarpS = 0;
                        break;
                    }
                    break;
                }
                case 66: {
                    ++this.curWarpS;
                    if (this.curWarpS > 100) {
                        this.curWarpS = 100;
                        break;
                    }
                    break;
                }
                case 69: {
                    --this.curEdgeL;
                    if (this.curEdgeL < 0) {
                        this.curEdgeL = 7;
                        break;
                    }
                    break;
                }
                case 70: {
                    ++this.curEdgeL;
                    if (this.curEdgeL > 7) {
                        this.curEdgeL = 0;
                        break;
                    }
                    break;
                }
                case 72: {
                    --this.curFX;
                    if (this.curFX < 0) {
                        this.curFX = 0;
                        break;
                    }
                    break;
                }
                case 73: {
                    ++this.curFX;
                    if (this.curFX > 100) {
                        this.curFX = 100;
                        break;
                    }
                    break;
                }
                case 74: {
                    --this.curFXL;
                    if (this.curFXL < 0) {
                        this.curFXL = 7;
                        break;
                    }
                    break;
                }
                case 75: {
                    ++this.curFXL;
                    if (this.curFXL > 7) {
                        this.curFXL = 0;
                        break;
                    }
                    break;
                }
                case 76: {
                    --this.curFXX;
                    if (this.curFXX < 0) {
                        this.curFXX = 0;
                        break;
                    }
                    break;
                }
                case 77: {
                    ++this.curFXX;
                    if (this.curFXX > 64) {
                        this.curFXX = 64;
                        break;
                    }
                    break;
                }
                case 78: {
                    --this.curFXY;
                    if (this.curFXY < -64) {
                        this.curFXY = -64;
                        break;
                    }
                    break;
                }
                case 79: {
                    ++this.curFXY;
                    if (this.curFXY > 100) {
                        this.curFXY = 100;
                        break;
                    }
                    break;
                }
                case 80: {
                    --this.curFXScl;
                    if (this.curFXScl < 0) {
                        this.curFXScl = 0;
                        break;
                    }
                    break;
                }
                case 81: {
                    ++this.curFXScl;
                    if (this.curFXScl > 1000) {
                        this.curFXScl = 1000;
                        break;
                    }
                    break;
                }
                case 82: {
                    --this.curSpawnRange;
                    if (this.curSpawnRange < 0) {
                        this.curSpawnRange = 0;
                        break;
                    }
                    break;
                }
                case 83: {
                    ++this.curSpawnRange;
                    if (this.curSpawnRange > 70) {
                        this.curSpawnRange = 70;
                        break;
                    }
                    break;
                }
                case 84: {
                    --this.curSpawnMon;
                    if (this.curSpawnMon < 0) {
                        this.curSpawnMon = 0;
                        break;
                    }
                    break;
                }
                case 85: {
                    ++this.curSpawnMon;
                    if (this.curSpawnMon > 255) {
                        this.curSpawnMon = 255;
                        break;
                    }
                    break;
                }
                case 86: {
                    --this.curSpawnProb;
                    if (this.curSpawnProb < 0) {
                        this.curSpawnProb = 0;
                        break;
                    }
                    break;
                }
                case 87: {
                    ++this.curSpawnProb;
                    if (this.curSpawnProb > 100) {
                        this.curSpawnProb = 100;
                        break;
                    }
                    break;
                }
                case 88: {
                    --this.curSEFreq;
                    if (this.curSEFreq < 0) {
                        this.curSEFreq = 0;
                        break;
                    }
                    break;
                }
                case 89: {
                    ++this.curSEFreq;
                    if (this.curSEFreq > 1000) {
                        this.curSEFreq = 0;
                        break;
                    }
                    break;
                }
            }
        }
        this.set.text = "Set: " + this.curSet;
        this.cat.text = AssetLoader.getCatName(this.curSetCat);
    }
    
    public void buttonReleased(final int id) {
    }
    
    void doEdges() {
        Tile t = null;
        for (int x = 0; x < 64; ++x) {
            for (int y = 0; y < 64; ++y) {
                t = this.map[this.curMap].tile[x][y];
                for (int a = 0; a < 2; ++a) {
                    if (t.att[a].data[0] == 8 && t.att[a].flags[1]) {
                        this.map[this.curMap].wall = this.placeEdge(this.map[this.curMap].wall, this.map[this.curMap].tile, t.att[a], x, y, a);
                    }
                }
            }
        }
    }
    
    boolean[][] placeEdge(final boolean[][] walls, final Tile[][] tiles, final Att a, final int ix, final int iy, final int b) {
        final boolean wall = a.flags[0];
        final int set = a.data[1];
        final int tile = a.data[2];
        final int i = a.data[3];
        final List<Edge> edges = new LinkedList<Edge>();
        final int diff = tiles[ix][iy].sprite[i] - a.data[2];
        int p = AssetLoader.getEdgePiece(diff);
        if (p <= 0) {
            return walls;
        }
        Edge e = new Edge(p, ix, iy);
        int curx = ix;
        int cury = iy;
        int nextd = 0;
        edges.add(e);
        int dir = 0;
        int fromd = 0;
        int oldp = p;
        boolean out = false;
        dir = -1;
        if (this.checkEdge(tiles, curx, cury, set, tile, 0, b)) {
            dir = 0;
            fromd = 2;
        }
        else if (this.checkEdge(tiles, curx, cury, set, tile, 1, b)) {
            dir = 1;
            fromd = 3;
        }
        else if (this.checkEdge(tiles, curx, cury, set, tile, 2, b)) {
            dir = 2;
            fromd = 0;
        }
        else if (this.checkEdge(tiles, curx, cury, set, tile, 3, b)) {
            dir = 3;
            fromd = 1;
        }
        if (dir >= 0) {
            switch (dir) {
                case 0: {
                    --cury;
                    break;
                }
                case 1: {
                    --curx;
                    break;
                }
                case 2: {
                    ++cury;
                    break;
                }
                case 3: {
                    ++curx;
                    break;
                }
            }
            do {
                nextd = -1;
                for (int d = 0; d < 4; ++d) {
                    if (d != fromd && nextd < 0 && this.checkEdge(tiles, curx, cury, set, tile, d, b)) {
                        nextd = d;
                    }
                }
                oldp = p;
                p = AssetLoader.getEdgePiece(oldp, fromd, nextd);
                e = new Edge(p, curx, cury);
                edges.add(e);
                switch (nextd) {
                    case 0: {
                        fromd = 2;
                        --cury;
                        continue;
                    }
                    case 1: {
                        fromd = 3;
                        --curx;
                        continue;
                    }
                    case 2: {
                        fromd = 0;
                        ++cury;
                        continue;
                    }
                    case 3: {
                        fromd = 1;
                        ++curx;
                        continue;
                    }
                    default: {
                        out = true;
                        continue;
                    }
                }
            } while (!out);
            for (final Edge g : edges) {
                tiles[g.x][g.y].spriteSet[i] = set;
                tiles[g.x][g.y].sprite[i] = tile + AssetLoader.getTileDiff(g.p);
                if (wall) {
                    final boolean[][] swall = AssetLoader.getEdgeWall(g.p);
                    for (int wx = 0; wx < 8; ++wx) {
                        for (int wy = 0; wy < 8; ++wy) {
                            swall[wx][wy] = !swall[wx][wy];
                            if (swall[wx][wy]) {
                                walls[g.x * 8 + wx][g.y * 8 + wy] = true;
                            }
                        }
                    }
                }
            }
            edges.clear();
        }
        return walls;
    }
    
    void drawSorted(final int i) {
        int dx = 0;
        int dy = 0;
        for (int y = 0; y < 1088; ++y) {
            this.drawList[y].clear();
        }
        for (int y = -5; y < 29; ++y) {
            for (int x = -5; x < 29; ++x) {
                dx = x + this.mapX;
                dy = y + this.mapY;
                if (dx >= 0 && dy >= 0 && dx < 64 && dy < 64) {
                    final Tile t = this.map[this.curMap].tile[dx][dy];
                    for (int e = 0; e < 2; ++e) {
                        final Att a = t.att[e];
                        if (a.data[0] == 5 && a.data[4] == i) {
                            final int cx = x * 32 - 16 + a.data[5];
                            final int cy = y * 32 - 16 + a.data[6];
                            final int ts = a.data[2];
                            final int sx = a.data[1] % 32;
                            final int sy = (a.data[1] - sx) / 32;
                            final TextureRegion tr = AssetLoader.getTileRegion(ts, sx * 32, sy * 32);
                            if (!tr.isFlipY()) {
                                tr.flip(false, true);
                            }
                            final DrawTask dt = new DrawTask(this.game, tr, cx, cy, false, 0.0f, 1.0f, Color.WHITE);
                            final int ry = cy + 16;
                            if (ry >= 0 && ry < 1088) {
                                this.drawList[ry].add(dt);
                            }
                        }
                        if (a.data[0] == 1 && a.data[2] == i) {
                            final TextureRegion tr2 = AssetLoader.getMobj(a.data[1] / 100, a.data[1] % 100);
                            final int cx2 = x * 32 - tr2.getRegionWidth() / 2 + 16 + a.data[3];
                            final int cy2 = y * 32 - tr2.getRegionHeight() + 16 + a.data[4];
                            if (!tr2.isFlipY()) {
                                tr2.flip(false, true);
                            }
                            final DrawTask dt2 = new DrawTask(this.game, tr2, cx2, cy2, false, 0.0f, 1.0f, Color.WHITE);
                            final int ry2 = cy2 + tr2.getRegionHeight();
                            if (ry2 >= 0 && ry2 < 1088) {
                                this.drawList[ry2].add(dt2);
                            }
                        }
                    }
                }
            }
        }
        for (int y = 0; y < 1088; ++y) {
            for (final DrawTask dt3 : this.drawList[y]) {
                dt3.render();
            }
        }
    }
    
    boolean checkEdge(final Tile[][] tiles, final int ox, final int oy, final int set, final int tile, final int dir, final int b) {
        int x = ox;
        int y = oy;
        switch (dir) {
            case 0: {
                --y;
                break;
            }
            case 1: {
                --x;
                break;
            }
            case 2: {
                ++y;
                break;
            }
            case 3: {
                ++x;
                break;
            }
        }
        if (Map.validPos(x, y)) {
            final Att a = tiles[x][y].att[b];
            final int d = a.data[0];
            if (d == 8 && a.data[1] == set && a.data[2] == tile) {
                return true;
            }
        }
        return false;
    }
    
    void drawLayer(final int i) {
        int dx = 0;
        int dy = 0;
        for (int x = 0; x < 24; ++x) {
            for (int y = 0; y < 24; ++y) {
                dx = x + this.mapX;
                dy = y + this.mapY;
                if (dx >= 0 && dy >= 0 && dx < 64 && dy < 64) {
                    final Tile t = this.map[this.curMap].tile[dx][dy];
                    if (t.spriteSet[i] >= 0) {
                        final int sx = t.sprite[i] % 32;
                        final int sy = (t.sprite[i] - sx) / 32;
                        final TextureRegion tr = AssetLoader.getTileRegion(t.spriteSet[i], sx * 32, sy * 32);
                        tr.flip(false, true);
                        this.game.drawRegion(tr, x * 32, y * 32, false, 0.0f, 1.0f);
                    }
                }
            }
        }
    }
    
    void drawAtt() {
        for (int y = 0; y < this.portHeight(); ++y) {
            for (int x = 0; x < this.portWidth(); ++x) {
                final int dx = x + this.mapX;
                final int dy = y + this.mapY;
                if (this.curMap >= 0 && dx >= 0 && dx < 64 && dy >= 0 && dy < 64) {
                    if (this.vis[9] || this.map[this.curMap].tile[dx][dy].att[1].data[0] == 11) {
                        if (this.map[this.curMap].tile[dx][dy].att[0].data[0] > 0) {
                            this.game.drawRegion(AssetLoader.att[this.map[this.curMap].tile[dx][dy].att[0].data[0] - 1], x * 32 - 4, y * 32 - 4, false, 0.0f, 1.0f);
                        }
                        if (this.map[this.curMap].tile[dx][dy].att[1].data[0] > 0) {
                            this.game.drawRegion(AssetLoader.att[this.map[this.curMap].tile[dx][dy].att[1].data[0] - 1], x * 32 + 4, y * 32 + 4, false, 0.0f, 1.0f);
                        }
                    }
                    for (int wx = 0; wx < 8; ++wx) {
                        for (int wy = 0; wy < 8; ++wy) {
                            if (this.map[this.curMap].wall[dx * 8 + wx][dy * 8 + wy] && this.vis[8]) {
                                this.game.drawRegion(AssetLoader.wall, x * 32 + wx * 4, y * 32 + wy * 4, false, 0.0f, 1.0f);
                            }
                            if (this.map[this.curMap].shadow[dx * 8 + wx][dy * 8 + wy] && this.vis[10]) {
                                this.game.drawRegion(AssetLoader.shadow, x * 32 + wx * 4 + 2, y * 32 + wy * 4 + 2, false, 0.0f, 1.0f);
                            }
                        }
                    }
                    if (this.btnGrid.toggle) {
                        this.game.draw(AssetLoader.mapSel, x * 32, y * 32, 0, 0, 32, 1);
                        this.game.draw(AssetLoader.mapSel, x * 32, y * 32 + 31, 0, 0, 32, 1);
                        this.game.draw(AssetLoader.mapSel, x * 32, y * 32, 0, 0, 1, 32);
                        this.game.draw(AssetLoader.mapSel, x * 32 + 31, y * 32, 0, 0, 1, 32);
                    }
                }
            }
        }
        this.game.batcher.end();
        this.game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int x2 = 0; x2 < 64; ++x2) {
            for (int y2 = 0; y2 < 64; ++y2) {
                final Att a = this.map[this.curMap].tile[x2][y2].att[1];
                if (a.data[0] == 11) {
                    for (int i = 1; i < 10; ++i) {
                        if (a.data[i] > 0) {
                            this.game.shapeRenderer.line((float)((x2 - this.mapX) * 32 + 16), (float)((y2 - this.mapY) * 32 + 16), (float)(((a.data[i] - 1) / 64 - this.mapX) * 32 + 16), (float)(((a.data[i] - 1) % 64 - this.mapY) * 32 + 16));
                        }
                    }
                }
            }
        }
        this.game.shapeRenderer.end();
        this.game.batcher.begin();
    }
    
    void drawHover() {
        if (this.dragging == 0) {
            final int srcX = this.curTile % 32 * 32;
            final int srcY = this.curTile / 32 * 32;
            if (this.layer < 8) {
                final TextureRegion tr = AssetLoader.getTileRegion(this.curTileSet, srcX, srcY);
                if (!tr.isFlipY()) {
                    tr.flip(false, true);
                }
                if (this.slashing()) {
                    if (this.game.input.keyDown[56]) {
                        this.game.drawRegion(tr, this.mX / 4 * 4 - 16, this.mY / 4 * 4 - 16, false, 0.0f, 1.0f);
                    }
                    else {
                        this.game.drawRegion(tr, this.mX / 4 * 4 - 16, this.mY / 4 * 4 - 16, false, 0.0f, 1.0f);
                    }
                }
                else {
                    this.game.drawRegion(tr, (this.dX - this.mapX) * 32, (this.dY - this.mapY) * 32, false, 0.0f, 1.0f);
                    this.game.draw(AssetLoader.mapSel, (this.dX - this.mapX) * 32, (this.dY - this.mapY) * 32, 0, 0, 32, 1);
                    this.game.draw(AssetLoader.mapSel, (this.dX - this.mapX) * 32, (this.dY - this.mapY) * 32 + 31, 0, 0, 32, 1);
                    this.game.draw(AssetLoader.mapSel, (this.dX - this.mapX) * 32, (this.dY - this.mapY) * 32, 0, 0, 1, 32);
                    this.game.draw(AssetLoader.mapSel, (this.dX - this.mapX) * 32 + 31, (this.dY - this.mapY) * 32, 0, 0, 1, 32);
                }
            }
            else if (this.layer != 10) {
                if (this.layer == 11) {
                    final TextureRegion tr = AssetLoader.getMobj(this.curMSet, this.curMobj);
                    if (tr != null) {
                        final int cx = this.mX - tr.getRegionWidth() / 2 + 16;
                        final int cy = this.mY - tr.getRegionHeight() + 16;
                        this.game.drawRegion(tr, cx, cy, false, 0.0f, 1.0f);
                    }
                }
                else if (this.layer == 12) {
                    final int tx = this.mX / 32 + this.mapX;
                    final int ty = this.mY / 32 + this.mapY;
                    if (tx >= 0 && ty >= 0 && tx < 64 && ty < 64 && this.map[this.curMap].tile[tx][ty].att[1].data[0] == 11) {
                        this.game.draw(AssetLoader.white, this.mX / 32 * 32, this.mY / 32 * 32, 0, 0, 32, 32);
                    }
                }
            }
        }
    }
    
    void drawTile(final int i, final int x, final int y, final int dx, final int dy) {
        int sx = 0;
        int sy = 0;
        Tile t = new Tile();
        if (this.vis[i]) {
            t = this.map[this.curMap].tile[dx][dy];
            if (t.spriteSet[i] >= 0) {
                sx = t.sprite[i] % 32;
                sy = (t.sprite[i] - sx) / 32;
                final TextureRegion tr = AssetLoader.getTileRegion(this.curTileSet, sx * 32, sy * 32);
                this.game.drawRegion(tr, x * 32, y * 32, false, 0.0f, 1.0f);
            }
        }
    }
    
    void drawTiles() {
        try {
            final int mX = this.game.input.getMouseX();
            final int mY = this.game.input.getMouseY();
            if (this.layer < 8) {
                TextureRegion tr = AssetLoader.getTileSetRegion(this.curSetCat * 100 + this.curSet);
                if (!tr.isFlipY()) {
                    tr.flip(false, true);
                }
                this.game.drawRegion(tr, 784, 53, false, 0.0f, 1.0f);
                if (tr.isFlipY()) {
                    tr.flip(false, true);
                }
                if (this.curSetCat * 100 + this.curSet == this.curTileSet) {
                    final int hX = this.curTile % 32 * 32 + 784;
                    final int hY = this.curTile / 32 * 32 + 53;
                    this.game.batcher.flush();
                    final Rectangle scissors = new Rectangle();
                    final Rectangle clipBounds = new Rectangle(784.0f, 53.0f, 512.0f, 512.0f);
                    ScissorStack.calculateScissors(this.game.cam, this.game.batcher.getTransformMatrix(), clipBounds, scissors);
                    ScissorStack.pushScissors(scissors);
                    this.game.batcher.draw(AssetLoader.tileSel, (float)(hX - 1), (float)(hY - 1), 34.0f, 34.0f, 34, 0, 34, 34, false, true);
                    this.game.batcher.flush();
                    ScissorStack.popScissors();
                }
                int srcX = this.curTile % 32 * 32;
                int srcY = this.curTile / 32 * 32;
                tr = AssetLoader.getTileRegion(this.curTileSet, srcX, srcY);
                if (!tr.isFlipY()) {
                    tr.flip(false, true);
                }
                this.game.drawRegion(tr, 784, 587, false, 0.0f, 1.0f);
                for (int i = 0; i < 5; ++i) {
                    srcX = this.curTiles[i] % 32 * 32;
                    srcY = this.curTiles[i] / 32 * 32;
                    tr = AssetLoader.getTileRegion(this.curTileSet, srcX, srcY);
                    if (!tr.isFlipY()) {
                        tr.flip(false, true);
                    }
                    this.game.drawRegion(tr, 848 + i * 32, 587, false, 0.0f, 1.0f);
                }
                if (Button.inBox(mX, mY, 784, 1296, 53, 565)) {
                    final int hX2 = (mX - 784) / 32 * 32 + 784;
                    final int hY2 = (mY - 53) / 32 * 32 + 53;
                    this.game.batcher.draw(AssetLoader.tileSel, (float)(hX2 - 1), (float)(hY2 - 1), 34.0f, 34.0f, 0, 0, 34, 34, false, true);
                }
            }
            else if (this.layer == 8) {
                this.game.drawRegion(AssetLoader.walls, 784, 53, false, 0.0f, 1.0f);
                this.game.draw(AssetLoader.wallTex, 785, 587, this.curWall * 32, 0, 32, 32);
            }
            else if (this.layer == 9) {
                this.game.draw(AssetLoader.attTex, 784, 53, 0, 0, 224, 96);
                this.game.drawRegion(AssetLoader.att[this.curAtt], 785, 587, false, 0.0f, 1.0f);
            }
            else if (this.layer == 10) {
                this.game.batcher.setColor(Color.CYAN);
                this.game.drawRegion(AssetLoader.walls, 784, 53, false, 0.0f, 1.0f);
                this.game.draw(AssetLoader.wallTex, 785, 587, this.curWall * 32, 0, 32, 32);
                this.game.batcher.setColor(Color.WHITE);
            }
            else if (this.layer == 11) {
                this.game.drawRegion(AssetLoader.getMobj(this.curMSet, this.curMobj), 1000, 300, true, 0.0f, 1.0f);
            }
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    
    public int portWidth() {
        return 24;
    }
    
    public int portHeight() {
        return 24;
    }
    
    void fill(final int x, final int y) {
        final int layer = this.flayer;
        final int set = this.fset;
        final int tile = this.ftile;
        final int bset = this.fbset;
        final int btile = this.fbtile;
        if (x < 0 || x >= 64 || y < 0 || y >= 64) {
            return;
        }
        Tile t = this.map[this.curMap].tile[x][y];
        int dx = x;
        int dy = y;
        if (t.spriteSet[layer] == bset && t.sprite[layer] == btile) {
            t.spriteSet[layer] = set;
            t.sprite[layer] = tile;
            dx = x;
            dy = y - 1;
            t = this.map[this.curMap].tile[dx][dy];
            if (t.spriteSet[layer] == bset && t.sprite[layer] == btile) {
                this.fill(dx, dy);
            }
            dx = x;
            dy = y + 1;
            t = this.map[this.curMap].tile[dx][dy];
            if (t.spriteSet[layer] == bset && t.sprite[layer] == btile) {
                this.fill(dx, dy);
            }
            dx = x - 1;
            dy = y;
            t = this.map[this.curMap].tile[dx][dy];
            if (t.spriteSet[layer] == bset && t.sprite[layer] == btile) {
                this.fill(dx, dy);
            }
            dx = x + 1;
            dy = y;
            t = this.map[this.curMap].tile[dx][dy];
            if (t.spriteSet[layer] == bset && t.sprite[layer] == btile) {
                this.fill(dx, dy);
            }
        }
    }
    
    @Override
    public void switchTo() {
        super.switchTo();
        if (this.curMap < 0) {
            final String[] names = new String[100];
            final int[] data = new int[100];
            for (int i = 0; i < 100; ++i) {
                names[i] = String.valueOf(i) + ": " + this.map[i].name;
                data[i] = i;
            }
            (this.mapList = new ListBox(this.game, 1, 0, 212, 184, 600, 200, names, data)).start();
        }
    }
    
    void specialjawn() {
        if (this.game.input.keyDown[249]) {
            for (int x = 0; x < 64; ++x) {
                for (int y = 0; y < 64; ++y) {
                    final Tile t = this.map[this.curMap].tile[x][y];
                    if (t.att[0].data[0] > 0) {
                        final int m = t.att[0].data[1] % 100;
                        final int s = t.att[0].data[1] / 100;
                        final boolean[][] swall = new boolean[40][40];
                        for (int w = 0; w < 40; ++w) {
                            for (int h = 0; h < 40; ++h) {
                                final int ww = w + x * 8 - 16;
                                final int hh = h + y * 8 - 16;
                                if (ww >= 0 && ww < 512 && hh >= 0 && hh < 512) {
                                    swall[w][h] = this.map[this.curMap].wall[ww][hh];
                                    final boolean b = swall[w][h];
                                }
                            }
                        }
                        try {
                            final File file = new File("walls/" + s + "/" + m);
                            final FileOutputStream f = new FileOutputStream(file);
                            final ObjectOutputStream o = new ObjectOutputStream(f);
                            o.writeObject(swall);
                            o.close();
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
            }
        }
    }
    
    void moveMap(final long tick) {
        this.game.moveCameraTo(this.game.width / 2, this.game.height / 2);
        if (tick > this.moveStamp) {
            this.moveStamp = tick + 100L;
            int s = 1;
            if (this.shifting()) {
                s *= 2;
            }
            if (this.controlling()) {
                s *= 2;
            }
            if (this.game.input.keyDown[19]) {
                this.mapY -= s;
            }
            else if (this.game.input.keyDown[20]) {
                this.mapY += s;
            }
            if (this.game.input.keyDown[21]) {
                this.mapX -= s;
            }
            else if (this.game.input.keyDown[22]) {
                this.mapX += s;
            }
            if (this.mapX < -this.portWidth() + 3) {
                this.mapX = -this.portWidth() + 3;
            }
            if (this.mapX > 61) {
                this.mapX = 61;
            }
            if (this.mapY < -this.portHeight() + 3) {
                this.mapY = -this.portHeight() + 3;
            }
            if (this.mapY > 61) {
                this.mapY = 61;
            }
        }
    }
    
    void updateLabels() {
        this.spawnRange.text = "Range: " + this.curSpawnRange;
        this.spawnMon.text = "Monster: " + this.curSpawnMon;
        this.spawnProb.text = "Odds: " + this.curSpawnProb + "%";
        this.lblFX.text = "FX: " + this.curFX;
        this.lblFXL.text = AssetLoader.getLayerName(this.curFXL);
        this.lblFXX.text = "XMod: " + this.curFXX;
        this.lblFXY.text = "YMod: " + this.curFXY;
        this.lblFXScl.text = "Scale: " + this.curFXScl;
        this.lblEdgeL.text = AssetLoader.getLayerName(this.curEdgeL);
        this.lblMobj.text = "Obj " + this.curMobj;
        this.lblMSet.text = "Set " + this.curMSet;
        this.lblMY.text = "YOff " + this.curYO;
        this.lblML.text = AssetLoader.getLayerName(this.curML);
        this.lblTile.text = String.valueOf(this.curTileSet) + ":" + this.curTile + ",  " + this.dX + "," + this.dY;
        this.lblSE.text = "Sound: " + this.curSE;
        this.lblSEVol.text = "Volume: " + this.curSEVol;
        this.lblSERange.text = "Range: " + this.curSERange;
        this.lblSEFreq.text = "Frequency: " + this.curSEFreq;
        this.warpM.text = "Map:" + this.curWarpM;
        this.warpX.text = "X: " + this.curWarpX;
        this.warpY.text = "Y: " + this.curWarpY;
        this.warpS.text = "Sound: " + this.curWarpS;
        this.windist.text = "Dist:" + this.curWindist;
        this.windir.text = AssetLoader.dirName(this.curWindir);
        this.editFX.visible = false;
        this.editLight.visible = false;
        this.editMobj.visible = false;
        this.editWall.visible = false;
        this.editWarp.visible = false;
        this.editWindow.visible = false;
        this.editSpawn.visible = false;
        this.editSE.visible = false;
        this.editEdge.visible = false;
        if (this.layer == 9) {
            if (this.curAtt == 1) {
                this.editWarp.visible = true;
            }
            else if (this.curAtt == 3) {
                this.editLight.visible = true;
            }
            else if (this.curAtt == 2) {
                this.editWall.visible = true;
            }
            else if (this.curAtt == 5) {
                this.editWindow.visible = true;
            }
            else if (this.curAtt == 6) {
                this.editSE.visible = true;
            }
            else if (this.curAtt == 7) {
                this.editEdge.visible = true;
            }
            else if (this.curAtt == 8) {
                this.editFX.visible = true;
            }
            else if (this.curAtt == 9) {
                this.editSpawn.visible = true;
            }
        }
        else if (this.layer == 11) {
            this.editMobj.visible = true;
        }
        this.r.text = "Red: " + this.curR;
        this.g.text = "Green: " + this.curG;
        this.b.text = "Blue: " + this.curB;
        this.a.text = "Alpha: " + this.curA;
        this.d.text = "Dist: " + this.curDist;
        this.x.text = "XMod: " + this.curX;
        this.y.text = "YMod: " + this.curY;
        this.lblRays.text = "Rays: " + this.rays;
        this.lblSoftness.text = "Softness: " + this.softness;
    }
    
    void checkDrag() {
        if (this.dragging == 1) {
            this.dragX = this.dX;
            this.dragY = this.dY;
            int fX = 0;
            int fY = 0;
            if (this.dragX < this.oDragX) {
                fX = this.dragX;
            }
            else {
                fX = this.oDragX;
            }
            if (this.dragY < this.oDragY) {
                fY = this.dragY;
            }
            else {
                fY = this.oDragY;
            }
            final int distX = Math.abs(this.dragX - this.oDragX);
            final int distY = Math.abs(this.dragY - this.oDragY);
            if (this.game.input.mouseDown[0] && Button.inBox(this.game.input.mouseDownX[0], this.game.input.mouseDownY[0], 0, 767, 0, 767)) {
                for (int x = fX; x <= fX + distX; ++x) {
                    for (int y = fY; y <= fY + distY; ++y) {
                        final int srcX = this.curTile % 32 * 32;
                        final int srcY = this.curTile / 32 * 32;
                        if (this.layer < 8) {
                            final TextureRegion tr = AssetLoader.getTileRegion(this.curTileSet, srcX, srcY);
                            this.game.drawRegion(tr, (x - this.mapX) * 32, (y - this.mapY) * 32, false, 0.0f, 1.0f);
                        }
                    }
                }
                for (int x = fX; x <= fX + distX; ++x) {
                    this.game.draw(AssetLoader.mapSel, (x - this.mapX) * 32, (fY - this.mapY) * 32, 0, 0, 32, 1);
                    this.game.draw(AssetLoader.mapSel, (x - this.mapX) * 32, (fY - this.mapY) * 32 + distY * 32 + 31, 0, 0, 32, 1);
                }
                for (int y2 = fY; y2 <= fY + distY; ++y2) {
                    this.game.draw(AssetLoader.mapSel, (fX - this.mapX) * 32, (y2 - this.mapY) * 32, 0, 0, 1, 32);
                    this.game.draw(AssetLoader.mapSel, (fX - this.mapX) * 32 + distX * 32 + 31, (y2 - this.mapY) * 32, 0, 0, 1, 32);
                }
            }
            else {
                this.dragging = 0;
                for (int x = fX; x <= fX + distX; ++x) {
                    for (int y = fY; y <= fY + distY; ++y) {
                        if (x >= 0 && y >= 0 && x < 64 && y < 64) {
                            if (this.layer < 8) {
                                this.map[this.curMap].tile[x][y].sprite[this.layer] = this.curTile;
                                this.map[this.curMap].tile[x][y].spriteSet[this.layer] = this.curTileSet;
                            }
                            else if (this.layer == 8) {
                                for (int w = 0; w < 8; ++w) {
                                    for (int h = 0; h < 8; ++h) {
                                        final int ww = x * 8 + w;
                                        final int hh = y * 8 + h;
                                        if ((ww >= 0 & ww < 512) && hh >= 0 && hh < 512) {
                                            this.map[this.curMap].wall[ww][hh] = true;
                                        }
                                    }
                                }
                            }
                            else if (this.layer == 9) {
                                this.placeAtt(x, y);
                            }
                            else if (this.layer == 10) {
                                for (int w = 0; w < 8; ++w) {
                                    for (int h = 0; h < 8; ++h) {
                                        final int ww = x * 8 + w;
                                        final int hh = y * 8 + h;
                                        if ((ww >= 0 & ww < 512) && hh >= 0 && hh < 512) {
                                            this.map[this.curMap].shadow[ww][hh] = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (this.dragging == 2) {
            this.dragX = this.dX;
            this.dragY = this.dY;
            int fX = 0;
            int fY = 0;
            if (this.dragX < this.oDragX) {
                fX = this.dragX;
            }
            else {
                fX = this.oDragX;
            }
            if (this.dragY < this.oDragY) {
                fY = this.dragY;
            }
            else {
                fY = this.oDragY;
            }
            final int distX = Math.abs(this.dragX - this.oDragX);
            final int distY = Math.abs(this.dragY - this.oDragY);
            if (this.game.input.mouseDown[1] && Button.inBox(this.game.input.mouseDownX[1], this.game.input.mouseDownY[1], 0, 767, 0, 767)) {
                for (int x = fX; x <= fX + distX; ++x) {
                    this.game.draw(AssetLoader.mapSel, (x - this.mapX) * 32, (fY - this.mapY) * 32, 0, 0, 32, 1);
                    this.game.draw(AssetLoader.mapSel, (x - this.mapX) * 32, (fY - this.mapY) * 32 + distY * 32 + 31, 0, 0, 32, 1);
                }
                for (int y2 = fY; y2 <= fY + distY; ++y2) {
                    this.game.draw(AssetLoader.mapSel, (fX - this.mapX) * 32, (y2 - this.mapY) * 32, 0, 0, 1, 32);
                    this.game.draw(AssetLoader.mapSel, (fX - this.mapX) * 32 + distX * 32 + 31, (y2 - this.mapY) * 32, 0, 0, 1, 32);
                }
            }
            else {
                this.dragging = 0;
                for (int x = fX; x <= fX + distX; ++x) {
                    for (int y = fY; y <= fY + distY; ++y) {
                        if (x >= 0 && y >= 0 && x < 64 && y < 64) {
                            if (this.layer < 8) {
                                this.map[this.curMap].tile[x][y].sprite[this.layer] = 0;
                                this.map[this.curMap].tile[x][y].spriteSet[this.layer] = -1;
                            }
                            else if (this.layer == 8) {
                                for (int w = 0; w < 8; ++w) {
                                    for (int h = 0; h < 8; ++h) {
                                        final int ww = x * 8 + w;
                                        final int hh = y * 8 + h;
                                        if ((ww >= 0 & ww < 512) && hh >= 0 && hh < 512) {
                                            this.map[this.curMap].wall[ww][hh] = false;
                                        }
                                    }
                                }
                            }
                            else if (this.layer == 9) {
                                if (this.shifting()) {
                                    this.map[this.curMap].tile[x][y].att[1] = new Att();
                                }
                                else {
                                    this.map[this.curMap].tile[x][y].att[0] = new Att();
                                }
                            }
                            else if (this.layer == 10) {
                                for (int w = 0; w < 8; ++w) {
                                    for (int h = 0; h < 8; ++h) {
                                        final int ww = x * 8 + w;
                                        final int hh = y * 8 + h;
                                        if ((ww >= 0 & ww < 512) && hh >= 0 && hh < 512) {
                                            this.map[this.curMap].shadow[ww][hh] = false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    void LclickMap() {
        if (this.alting()) {
            if (this.game.input.wasMouseJustClicked[0]) {
                this.game.input.wasMouseJustClicked[0] = false;
                if (this.layer < 8) {
                    if (this.map[this.curMap].tile[this.dX][this.dY].spriteSet[this.layer] >= 0) {
                        this.shiftTiles();
                        this.curTile = this.map[this.curMap].tile[this.dX][this.dY].sprite[this.layer];
                        this.curTileSet = this.map[this.curMap].tile[this.dX][this.dY].spriteSet[this.layer];
                    }
                }
                else if (this.layer == 9 || this.layer == 10) {
                    final Att a = this.map[this.curMap].tile[this.dX][this.dY].att[0];
                    if (a.data[0] >= 0) {
                        this.copyAtt(this.dX, this.dY);
                    }
                }
            }
        }
        else if (this.controlling() && this.game.input.wasMouseJustClicked[0]) {
            if (this.layer <= 10) {
                this.game.input.wasMouseJustClicked[0] = false;
                this.dragging = 1;
                this.dragX = this.dX;
                this.dragY = this.dY;
                this.oDragX = this.dX;
                this.oDragY = this.dY;
            }
        }
        else if (this.slashing() && this.game.input.wasMouseJustClicked[0]) {
            if (this.layer < 8) {
                this.game.input.wasMouseJustClicked[0] = false;
                Att a = new Att();
                int b = 0;
                if (this.shifting()) {
                    b = 1;
                    a = this.map[this.curMap].tile[this.dX][this.dY].att[1];
                }
                else {
                    a = this.map[this.curMap].tile[this.dX][this.dY].att[0];
                }
                int ex = 0;
                int ey = 0;
                if (!this.game.input.keyDown[56]) {
                    ex = this.mX - (this.dX - this.mapX) * 32;
                    ey = this.mY - (this.dY - this.mapY) * 32;
                }
                else {
                    ex = this.mX / 4 * 4 - (this.dX - this.mapX) * 32;
                    ey = this.mY / 4 * 4 - (this.dY - this.mapY) * 32;
                }
                if (a.data[0] == 0) {
                    this.map[this.curMap].tile[this.dX][this.dY].att[b] = new Att();
                    a = this.map[this.curMap].tile[this.dX][this.dY].att[b];
                }
                else {
                    boolean found = false;
                    for (int x = -1; x < 2; ++x) {
                        for (int y = -1; y < 2; ++y) {
                            final int nx = this.dX + x;
                            final int ny = this.dY + y;
                            if (!found && nx >= 0 && nx < 64 && ny >= 0 && ny < 64 && this.map[this.curMap].tile[nx][ny].att[b].data[0] == 0) {
                                found = true;
                                this.map[this.curMap].tile[nx][ny].att[b] = new Att();
                                a = this.map[this.curMap].tile[nx][ny].att[b];
                                if (!this.game.input.keyDown[56]) {
                                    ex = this.mX - (nx - this.mapX) * 32;
                                    ey = this.mY - (ny - this.mapY) * 32;
                                }
                                else {
                                    ex = this.mX / 4 * 4 - (nx - this.mapX) * 32;
                                    ey = this.mY / 4 * 4 - (ny - this.mapY) * 32;
                                }
                            }
                        }
                    }
                }
                a.data[0] = 5;
                a.data[1] = this.curTile;
                a.data[2] = this.curTileSet;
                a.data[4] = this.layer;
                a.data[5] = ex;
                a.data[6] = ey;
            }
        }
        else if (!this.slashing()) {
            if (this.layer < 8) {
                this.map[this.curMap].tile[this.dX][this.dY].sprite[this.layer] = this.curTile;
                this.map[this.curMap].tile[this.dX][this.dY].spriteSet[this.layer] = this.curTileSet;
            }
            else if (this.layer == 8) {
                if (this.curWall > 0) {
                    for (int wxx = -(this.curWall + 1) / 2; wxx < (this.curWall + 1) / 2; ++wxx) {
                        for (int wyy = -(this.curWall + 1) / 2; wyy < (this.curWall + 1) / 2; ++wyy) {
                            if (wxx + this.wx >= 0 && wyy + this.wy >= 0 && wxx + this.wx < 512 && wyy + this.wy < 512) {
                                this.map[this.curMap].wall[this.wx + wxx][this.wy + wyy] = true;
                            }
                        }
                    }
                }
                else {
                    this.map[this.curMap].wall[this.wx][this.wy] = true;
                }
            }
            else if (this.layer == 9) {
                this.placeAtt(this.dX, this.dY);
            }
            else if (this.layer == 10) {
                if (this.curWall > 0) {
                    for (int wxx = -(this.curWall + 1) / 2; wxx < (this.curWall + 1) / 2; ++wxx) {
                        for (int wyy = -(this.curWall + 1) / 2; wyy < (this.curWall + 1) / 2; ++wyy) {
                            if (wxx + this.wx >= 0 && wyy + this.wy >= 0 && wxx + this.wx < 512 && wyy + this.wy < 512) {
                                this.map[this.curMap].shadow[this.wx + wxx][this.wy + wyy] = true;
                            }
                        }
                    }
                }
                else {
                    this.map[this.curMap].shadow[this.wx][this.wy] = true;
                }
            }
            else if (this.layer == 11) {
                int b2 = 0;
                if (this.shifting()) {
                    b2 = 1;
                }
                this.map[this.curMap].tile[this.dX][this.dY].att[b2] = new Att();
                final Att a2 = this.map[this.curMap].tile[this.dX][this.dY].att[b2];
                a2.data[0] = 1;
                a2.data[2] = this.curML;
                a2.data[1] = this.curMSet * 100 + this.curMobj;
                final int ex = this.mX - (this.dX - this.mapX) * 32;
                final int ey = this.mY - (this.dY - this.mapY) * 32;
                a2.data[3] = ex;
                a2.data[4] = ey;
                a2.data[3] = 0;
                a2.data[4] = 0;
                a2.flags[0] = this.btnIgnoreWall.toggle;
                a2.data[5] = this.curYO;
            }
            else if (this.layer == 12 && this.game.input.wasMouseJustClicked[0]) {
                final int tx = this.mX / 32 + this.mapX;
                final int ty = this.mY / 32 + this.mapY;
                if (tx >= 0 && ty >= 0 && tx < 64 && ty < 64) {
                    final Att a3 = this.map[this.curMap].tile[tx][ty].att[1];
                    if (this.noding) {
                        if (this.curNode == tx * 64 + ty) {
                            this.noding = false;
                            this.game.input.wasMouseJustClicked[0] = false;
                        }
                        else if (a3.data[0] == 11) {
                            for (int i = 1; i < 10; ++i) {
                                if (a3.data[i] == this.curNode + 1) {
                                    this.game.input.wasMouseJustClicked[0] = false;
                                    return;
                                }
                            }
                            final Att n1 = this.map[this.curMap].tile[this.curNode / 64][this.curNode % 64].att[1];
                            boolean out = false;
                            for (int j = 1; j < 10; ++j) {
                                if (!out && n1.data[j] == 0) {
                                    out = true;
                                    n1.data[j] = tx * 64 + ty + 1;
                                }
                            }
                            out = false;
                            for (int j = 1; j < 10; ++j) {
                                if (!out && a3.data[j] == 0) {
                                    out = true;
                                    a3.data[j] = this.curNode + 1;
                                }
                            }
                            this.game.input.wasMouseJustClicked[0] = false;
                            this.noding = false;
                        }
                    }
                    else if (a3.data[0] == 11) {
                        this.game.input.wasMouseJustClicked[0] = false;
                        this.noding = true;
                        this.curNode = tx * 64 + ty;
                    }
                }
            }
        }
    }
    
    void copyAtt(final int dX, final int dY) {
        int b = 0;
        if (this.shifting()) {
            b = 1;
        }
        final Att a = this.map[this.curMap].tile[dX][dY].att[b];
        this.curAtt = a.data[0] - 1;
        if (this.curAtt == 0) {
            this.curML = a.data[2];
            this.curMSet = a.data[1] / 100;
            this.curMobj = a.data[1] % 100;
            this.btnIgnoreWall.toggle = a.flags[0];
            this.curYO = a.data[5];
            this.layer = 11;
        }
        if (this.curAtt == 1) {
            this.curWarpM = a.data[1];
            this.curWarpX = a.data[2];
            this.curWarpY = a.data[3];
            this.curWarpS = a.data[4];
        }
        else if (this.curAtt == 2) {
            this.shadowToo.toggle = a.flags[0];
        }
        else if (this.curAtt == 3) {
            this.curA = a.data[1];
            this.curR = a.data[5];
            this.curG = a.data[6];
            this.curB = a.data[7];
            this.curDist = a.data[2];
            this.rays = a.data[8];
            this.softness = a.data[9];
            this.curX = a.data[3];
            this.curY = a.data[4];
            this.f.toggle = a.flags[0];
            this.s.toggle = a.flags[1];
            this.xr.toggle = a.flags[2];
        }
        else if (this.curAtt == 5) {
            this.curWindist = a.data[1];
            this.curWindir = a.data[2];
        }
        else if (this.curAtt == 6) {
            this.curSE = a.data[1];
            this.curSEVol = a.data[2];
            this.curSERange = a.data[3];
            this.curSEFreq = a.data[4];
            this.btnSEConstant.toggle = a.flags[0];
            this.btnSE2D.toggle = a.flags[1];
        }
        else if (this.curAtt == 7) {
            final int[] data = a.data;
            final int n = 1;
            final int curTileSet = this.curTileSet;
            data[n] = curTileSet;
            this.curTileSet = curTileSet;
            final int[] data2 = a.data;
            final int n2 = 2;
            final int curTile = this.curTile;
            data2[n2] = curTile;
            this.curTile = curTile;
            final int[] data3 = a.data;
            final int n3 = 3;
            final int curEdgeL = this.curEdgeL;
            data3[n3] = curEdgeL;
            this.curEdgeL = curEdgeL;
            this.edgeWall.toggle = a.flags[0];
            this.edgeSeed.toggle = a.flags[1];
            this.edgeInvert.toggle = a.flags[2];
        }
        else if (this.curAtt == 8) {
            this.curFX = a.data[1];
            this.curFXL = a.data[2];
            this.curFXX = a.data[3];
            this.curFXY = a.data[4];
            this.curFXScl = a.data[5];
        }
        else if (this.curAtt == 9) {
            this.curSpawnRange = a.data[1];
            this.spawnStand.toggle = a.flags[0];
            this.curSpawnMon = a.data[2];
            this.curSpawnProb = a.data[3];
        }
    }
    
    void placeAtt(final int dX, final int dY) {
        int b = 0;
        if (this.shifting()) {
            b = 1;
        }
        Att a = this.map[this.curMap].tile[dX][dY].att[b];
        if (this.curAtt == 1) {
            this.map[this.curMap].tile[dX][dY].att[b] = new Att();
            a = this.map[this.curMap].tile[dX][dY].att[b];
            a.data[0] = this.curAtt + 1;
            a.data[1] = this.curWarpM;
            a.data[2] = this.curWarpX;
            a.data[3] = this.curWarpY;
            a.data[4] = this.curWarpS;
        }
        else if (this.curAtt == 2) {
            this.map[this.curMap].tile[dX][dY].att[b] = new Att();
            a = this.map[this.curMap].tile[dX][dY].att[b];
            a.data[0] = this.curAtt + 1;
            a.flags[0] = this.shadowToo.toggle;
        }
        else if (this.curAtt == 3) {
            this.map[this.curMap].tile[dX][dY].att[b] = new Att();
            a = this.map[this.curMap].tile[dX][dY].att[b];
            a.data[0] = this.curAtt + 1;
            a.data[1] = this.curA;
            a.data[5] = this.curR;
            a.data[6] = this.curG;
            a.data[7] = this.curB;
            a.data[2] = this.curDist;
            a.data[3] = this.curX;
            a.data[4] = this.curY;
            a.data[8] = this.rays;
            a.data[9] = this.softness;
            a.flags[0] = this.f.toggle;
            a.flags[1] = this.s.toggle;
            a.flags[2] = this.xr.toggle;
        }
        else if (this.curAtt == 5) {
            this.map[this.curMap].tile[dX][dY].att[b] = new Att();
            a = this.map[this.curMap].tile[dX][dY].att[b];
            a.data[0] = this.curAtt + 1;
            a.data[1] = this.curWindist;
            a.data[2] = this.curWindir;
        }
        else if (this.curAtt == 6) {
            this.map[this.curMap].tile[dX][dY].att[b] = new Att();
            a = this.map[this.curMap].tile[dX][dY].att[b];
            a.data[0] = this.curAtt + 1;
            a.data[1] = this.curSE;
            a.data[2] = this.curSEVol;
            a.data[3] = this.curSERange;
            a.data[4] = this.curSEFreq;
            a.flags[0] = this.btnSEConstant.toggle;
            a.flags[1] = this.btnSE2D.toggle;
        }
        else if (this.curAtt == 7) {
            this.map[this.curMap].tile[dX][dY].att[b] = new Att();
            a = this.map[this.curMap].tile[dX][dY].att[b];
            a.data[0] = this.curAtt + 1;
            a.data[1] = this.curTileSet;
            a.data[2] = this.curTile;
            a.data[3] = this.curEdgeL;
            a.flags[0] = this.edgeWall.toggle;
            a.flags[1] = this.edgeSeed.toggle;
            a.flags[2] = this.edgeInvert.toggle;
        }
        else if (this.curAtt == 8) {
            this.map[this.curMap].tile[dX][dY].att[b] = new Att();
            a = this.map[this.curMap].tile[dX][dY].att[b];
            a.data[0] = this.curAtt + 1;
            a.data[1] = this.curFX;
            a.data[2] = this.curFXL;
            a.data[3] = this.curFXX;
            a.data[4] = this.curFXY;
            a.data[5] = this.curFXScl;
        }
        else if (this.curAtt == 9) {
            this.map[this.curMap].tile[dX][dY].att[b] = new Att();
            a = this.map[this.curMap].tile[dX][dY].att[b];
            a.data[0] = this.curAtt + 1;
            a.data[1] = this.curSpawnRange;
            a.flags[0] = this.spawnStand.toggle;
            a.data[2] = this.curSpawnMon;
            a.data[3] = this.curSpawnProb;
        }
        else if (this.curAtt == 10) {
            b = 1;
            this.map[this.curMap].tile[dX][dY].att[b] = new Att();
            a = this.map[this.curMap].tile[dX][dY].att[b];
            a.data[0] = this.curAtt + 1;
        }
    }
    
    void RclickMap() {
        this.noding = false;
        if (this.alting()) {
            if (this.game.input.wasMouseJustClicked[1] && this.dX >= 0 && this.dY >= 0 && this.dX < 64 && this.dY < 64) {
                this.game.input.wasMouseJustClicked[1] = false;
                if (this.layer < 8) {
                    this.curSet = this.map[this.curMap].tile[this.dX][this.dY].spriteSet[this.layer] % 100;
                    this.curSetCat = this.map[this.curMap].tile[this.dX][this.dY].spriteSet[this.layer] / 100;
                    this.curTile = this.map[this.curMap].tile[this.dX][this.dY].sprite[this.layer];
                    if (this.curSetCat > 5) {
                        this.curSetCat = 5;
                    }
                    if (this.curSetCat < 0) {
                        this.curSetCat = 0;
                    }
                    if (this.curSet > AssetLoader.numTileset(this.curSetCat) - 1) {
                        this.curSet = 0;
                    }
                    if (this.curSet < 0) {
                        this.curSet = AssetLoader.numTileset(this.curSetCat) - 1;
                    }
                }
            }
        }
        else if (this.controlling() && this.game.input.wasMouseJustClicked[1]) {
            if (this.layer <= 10) {
                this.game.input.wasMouseJustClicked[1] = false;
                this.dragging = 2;
                this.dragX = this.dX;
                this.dragY = this.dY;
                this.oDragX = this.dX;
                this.oDragY = this.dY;
            }
        }
        else if (this.dX >= 0 && this.dY >= 0 && this.dX < 64 && this.dY < 64) {
            if (this.layer < 8) {
                this.map[this.curMap].tile[this.dX][this.dY].sprite[this.layer] = 0;
                this.map[this.curMap].tile[this.dX][this.dY].spriteSet[this.layer] = -1;
            }
            else if (this.layer == 8) {
                if (this.curWall > 0) {
                    for (int wxx = -(this.curWall + 1) / 2; wxx < (this.curWall + 1) / 2; ++wxx) {
                        for (int wyy = -(this.curWall + 1) / 2; wyy < (this.curWall + 1) / 2; ++wyy) {
                            if (wxx + this.wx >= 0 && this.wy + wyy >= 0 && wxx + this.wx < 512 && wyy + this.wy < 512) {
                                this.map[this.curMap].wall[this.wx + wxx][this.wy + wyy] = false;
                            }
                        }
                    }
                }
                else {
                    this.map[this.curMap].wall[this.wx][this.wy] = false;
                }
            }
            else if (this.layer == 10) {
                if (this.curWall > 0) {
                    for (int wxx = -(this.curWall + 1) / 2; wxx < (this.curWall + 1) / 2; ++wxx) {
                        for (int wyy = -(this.curWall + 1) / 2; wyy < (this.curWall + 1) / 2; ++wyy) {
                            if (wxx + this.wx >= 0 && wyy + this.wy >= 0 && wxx + this.wx < 512 && wyy + this.wy < 512) {
                                this.map[this.curMap].shadow[this.wx + wxx][this.wy + wyy] = false;
                            }
                        }
                    }
                }
                else {
                    this.map[this.curMap].shadow[this.wx][this.wy] = false;
                }
            }
            else if (this.layer == 9 || this.layer == 11) {
                if (this.shifting()) {
                    this.map[this.curMap].tile[this.dX][this.dY].att[1] = new Att();
                }
                else {
                    this.map[this.curMap].tile[this.dX][this.dY].att[0] = new Att();
                }
            }
        }
    }
    
    void clickTiles() {
        if ((!this.game.input.mouseDown[1] || !Button.inBox(this.game.input.mouseDownX[1], this.game.input.mouseDownY[1], 784, 1296, 53, 565)) && this.game.input.mouseDown[0] && this.game.input.wasMouseJustClicked[0] && Button.inBox(this.game.input.mouseDownX[0], this.game.input.mouseDownY[0], 784, 1296, 53, 565)) {
            this.game.input.wasMouseJustClicked[0] = false;
            if (this.layer < 8) {
                this.shiftTiles();
                this.curTile = (this.mY - 53) / 32 * 32 + (this.mX - 784) / 32;
                this.curTileSet = this.curSetCat * 100 + this.curSet;
                if (this.curTileSet < 0) {
                    this.curTileSet = 0;
                }
            }
            else if (this.layer == 8) {
                final int c = (this.mY - 53) / 32 * 32 + (this.mX - 784) / 32;
                if (c >= 0 && c < 8) {
                    this.curWall = c;
                }
            }
            else if (this.layer == 9) {
                final int c = (this.mY - 53) / 32 * 7 + (this.mX - 784) / 32;
                if (c >= 0 && c < 21) {
                    this.changeAtt(c);
                }
            }
            else if (this.layer == 10) {
                final int c = (this.mY - 53) / 32 * 32 + (this.mX - 784) / 32;
                if (c >= 0 && c < 8) {
                    this.curWall = c;
                }
            }
        }
    }
    
    void clickCurTile() {
        if (((this.game.input.mouseDown[0] && this.game.input.wasMouseJustClicked[0] && Button.inBox(this.game.input.mouseDownX[0], this.game.input.mouseDownY[0], 784, 816, 587, 619)) || (this.game.input.mouseDown[1] && this.game.input.wasMouseJustClicked[1])) && this.layer < 8) {
            if (this.game.input.wasMouseJustClicked[0]) {
                this.game.input.wasMouseJustClicked[0] = false;
            }
            if (this.game.input.wasMouseJustClicked[1]) {
                this.game.input.wasMouseJustClicked[1] = false;
            }
            this.curSet = this.curTileSet % 100;
            this.curSetCat = this.curTileSet / 100;
            if (this.curSet > AssetLoader.numTileset(this.curSetCat) - 1) {
                this.curSet = 0;
            }
            if (this.curSet < 0) {
                this.curSet = AssetLoader.numTileset(this.curSetCat) - 1;
            }
        }
    }
    
    void clickRecentTiles() {
        if (this.game.input.mouseDown[0] && this.game.input.wasMouseJustClicked[0] && Button.inBox(this.game.input.mouseDownX[0], this.game.input.mouseDownY[0], 848, 1008, 587, 619)) {
            if (this.layer < 8) {
                this.game.input.wasMouseJustClicked[0] = false;
                final int slot = (this.mX - 848) / 32;
                int bubble = this.curTile;
                this.curTile = this.curTiles[slot];
                this.curTiles[slot] = bubble;
                bubble = this.curTileSet;
                this.curTileSet = this.curTileSets[slot];
                if (this.curTileSet < 0) {
                    this.curTileSet = 0;
                }
                this.curTileSets[slot] = bubble;
            }
        }
        else if (this.game.input.mouseDown[1] && this.game.input.wasMouseJustClicked[1] && Button.inBox(this.game.input.mouseDownX[1], this.game.input.mouseDownY[1], 848, 1008, 587, 619) && this.layer < 8) {
            this.game.input.wasMouseJustClicked[1] = false;
            final int slot = (this.mX - 848) / 32;
            this.curSet = this.curTileSets[slot] % 100;
            this.curSetCat = this.curTileSets[slot] / 100;
            if (this.curSet > AssetLoader.numTileset(this.curSetCat) - 1) {
                this.curSet = 0;
            }
            if (this.curSet < 0) {
                this.curSet = AssetLoader.numTileset(this.curSetCat) - 1;
            }
        }
    }
    
    void updateTimers() {
        if (this.game.tick > this.lampAt) {
            this.lampAt = this.game.tick + 50L;
            if (this.lampup) {
                ++this.lamp;
                if (this.lamp > 3) {
                    this.lampup = false;
                }
            }
            else {
                --this.lamp;
                if (this.lamp == 0) {
                    this.lampup = true;
                }
            }
            for (final Button b : this.editFX.buttons) {
                if (b.sel && this.game.tick - b.selDown > 300L) {
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                }
            }
            for (final Button b : this.editLight.buttons) {
                if (b.sel && this.game.tick - b.selDown > 300L) {
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                }
            }
            for (final Button b : this.editWarp.buttons) {
                if (b.sel && this.game.tick - b.selDown > 300L) {
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                }
            }
            for (final Button b : this.editWindow.buttons) {
                if (b.sel && this.game.tick - b.selDown > 300L) {
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                }
            }
            for (final Button b : this.editSpawn.buttons) {
                if (b.sel && this.game.tick - b.selDown > 300L) {
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                }
            }
            for (final Button b : this.editSE.buttons) {
                if (b.sel && this.game.tick - b.selDown > 300L) {
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                }
            }
            for (final Button b : this.editEdge.buttons) {
                if (b.sel && this.game.tick - b.selDown > 300L) {
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                }
            }
        }
    }
    
    void checkNodes() {
        int nx = 0;
        int ny = 0;
        for (int x = 0; x < 64; ++x) {
            for (int y = 0; y < 64; ++y) {
                final Att a = this.map[this.curMap].tile[x][y].att[1];
                if (a.data[0] == 11) {
                    for (int i = 1; i < 10; ++i) {
                        if (a.data[i] > 0) {
                            nx = (a.data[i] - 1) / 64;
                            ny = (a.data[i] - 1) % 64;
                            if (this.map[this.curMap].tile[nx][ny].att[1].data[0] != 11) {
                                a.data[i] = 0;
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void render() {
        final long tick = this.game.tick;
        if (!this.started) {
            this.start();
        }
        if (this.curMap < 0) {
            this.mapList.update(tick);
            this.listEdit.update(tick);
            this.listBack.update(tick);
            this.mapFrame.render();
            this.mapList.render();
            this.listEdit.render();
            this.listBack.render();
            return;
        }
        this.drawTiles();
        this.checkNodes();
        this.moveMap(tick);
        this.dX = this.mX / 32 + this.mapX;
        this.dY = this.mY / 32 + this.mapY;
        this.mX = this.game.input.getMouseX();
        this.mY = this.game.input.getMouseY();
        this.wx = this.mX / 4 + this.mapX * 8;
        this.wy = this.mY / 4 + this.mapY * 8;
        this.updateLabels();
        this.updateTimers();
        this.drawWorld();
        if (Button.inBox(this.mX, this.mY, 0, 767, 0, 767) && this.dX >= 0 && this.dY >= 0 && this.dX < 64 && this.dY < 64) {
            if (this.dragging > 0) {
                this.checkDrag();
            }
            else if (this.game.input.mouseDown[0] && Button.inBox(this.game.input.mouseDownX[0], this.game.input.mouseDownY[0], 0, 767, 0, 767)) {
                this.LclickMap();
            }
            else if (this.game.input.mouseDown[1] && Button.inBox(this.game.input.mouseDownX[1], this.game.input.mouseDownY[1], 0, 767, 0, 767)) {
                this.RclickMap();
            }
        }
        else if (Button.inBox(this.mX, this.mY, 784, 1296, 53, 565)) {
            this.clickTiles();
        }
        else if (Button.inBox(this.mX, this.mY, 784, 816, 587, 619)) {
            this.clickCurTile();
        }
        else if (Button.inBox(this.mX, this.mY, 848, 1008, 587, 619)) {
            this.clickRecentTiles();
        }
        this.game.input.keyPress.clear();
        super.update(tick);
        super.render();
    }
    
    void drawWorld() {
        this.game.batcher.flush();
        final Rectangle scissors = new Rectangle();
        final Rectangle clipBounds = new Rectangle(0.0f, 0.0f, 768.0f, 768.0f);
        ScissorStack.calculateScissors(this.game.cam, this.game.batcher.getTransformMatrix(), clipBounds, scissors);
        ScissorStack.pushScissors(scissors);
        for (int i = 0; i < 8; ++i) {
            if (this.vis[i]) {
                this.drawLayer(i);
                this.drawSorted(i);
            }
        }
        this.drawHover();
        this.drawAtt();
        this.game.batcher.flush();
        ScissorStack.popScissors();
    }
    
    void changeAtt(final int a) {
        switch (this.curAtt = a) {
            case 0: {
                this.layer = 11;
                this.editMobj.visible = true;
                break;
            }
        }
    }
    
    void shiftTiles() {
        for (int i = 9; i > 0; --i) {
            this.curTiles[i] = this.curTiles[i - 1];
            this.curTileSets[i] = this.curTileSets[i];
        }
        this.curTiles[0] = this.curTile;
        this.curTileSets[0] = this.curTileSet;
    }
    
    public class Edge
    {
        public int p;
        public int x;
        public int y;
        
        public Edge(final int p, final int x, final int y) {
            this.p = 0;
            this.x = 0;
            this.y = 0;
            this.p = p;
            this.x = x;
            this.y = y;
        }
    }
}
