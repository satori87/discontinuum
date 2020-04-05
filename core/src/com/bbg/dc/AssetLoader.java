// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

import com.badlogic.gdx.graphics.TextureData;
import com.esotericsoftware.kryo.io.Output;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.io.FileOutputStream;
import com.esotericsoftware.kryo.io.Input;
import java.util.zip.InflaterInputStream;
import java.util.Iterator;
import java.util.List;
import com.badlogic.gdx.files.FileHandle;
import java.util.Arrays;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.File;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AssetLoader
{
    public static DCGame game;
    public static TextureAtlas atlas;
    public static TextureAtlas masks;
    private static Sound[] snd;
    public static Texture topmask;
    public static Texture topmask2;
    public static Texture[] iface;
    public static Texture texture;
    public static Texture frameTex;
    public static Texture tileSel;
    public static Texture mapSel;
    public static TextureRegion[] wheel;
    public static String[] namesf;
    public static int[] namesff;
    public static int numFemaleNames;
    public static String[] namesm;
    public static int[] namesmf;
    public static int numMaleNames;
    public static String[] snames;
    public static int[] snamesf;
    public static int numSurNames;
    public static final int numMSet = 10;
    public static TextureRegion[] frame;
    public static TextureRegion[][] button;
    public static TextureRegion[][] field;
    public static TextureRegion[][] font;
    public static TextureRegion[] att;
    public static Texture attTex;
    public static TextureRegion wall;
    public static TextureRegion shadow;
    private static Texture[][] hairMask;
    public static TextureRegion walls;
    public static Texture wallTex;
    public static int[] fontWidth;
    public static int[] fontX;
    public static Mobj[][] mobjdata;
    private static Texture[][][] sprite;
    private static Texture[][][] weapon;
    public static TextureRegion whitebox;
    public static Texture white;
    public static int[][] numSS;
    public static int[][] numWS;
    private static Texture[][][] ride;
    private static Texture[][][] rideacc;
    private static Cursor[] cursor;
    public static Texture[] cursorTex;
    public static boolean[][][] topMask;
    static Pixmap[] pmaps;
    public static AtlasMask atlasMask;
    
    static {
        AssetLoader.snd = new Sound[200];
        AssetLoader.numFemaleNames = 0;
        AssetLoader.numMaleNames = 0;
    }
    
    public static TextureRegion getMobj(final int s, final int i) {
        final TextureRegion tr = AssetLoader.atlas.findRegion("whole/" + s + "/" + i);
        return tr;
    }
    
    public static TextureRegion getBloodRegion(final int s) {
        final TextureRegion tr = AssetLoader.masks.findRegion("blood" + s);
        return tr;
    }
    
    public static TextureRegion getMonsterSheetRegion(final int a, final int b, final int c) {
        final TextureRegion tr = AssetLoader.atlas.findRegion(String.valueOf(a) + "/" + getMonsterString(b) + c);
        return tr;
    }
    
    public static TextureRegion getMonsterRegion(final int a, final int b, final int c, final int x, final int y, final int w, final int h) {
        final TextureRegion tr = getMonsterSheetRegion(a, b, c);
        if (tr != null) {
            final TextureRegion dr = new TextureRegion(tr.getTexture(), tr.getRegionX() + x, tr.getRegionY() + y, w, h);
            return dr;
        }
        return null;
    }
    
    public static Texture getRide(final int a, final int i, final int c) {
        if (AssetLoader.ride[a][i][c] == null) {
            if (a == 0 && c == 0) {
                loadInternalTexture("sprites/ride/" + i + "/sb");
                AssetLoader.ride[a][i][c] = AssetLoader.texture;
            }
            else if (a == 0 && c == 1) {
                loadInternalTexture("sprites/ride/" + i + "/sf");
                AssetLoader.ride[a][i][c] = AssetLoader.texture;
            }
            else if (a == 1 && c == 0) {
                loadInternalTexture("sprites/ride/" + i + "/wb");
                AssetLoader.ride[a][i][c] = AssetLoader.texture;
            }
            else if (a == 1 && c == 1) {
                loadInternalTexture("sprites/ride/" + i + "/wf");
                AssetLoader.ride[a][i][c] = AssetLoader.texture;
            }
            else if (a == 2 && c == 0) {
                loadInternalTexture("sprites/ride/" + i + "/rb");
                AssetLoader.ride[a][i][c] = AssetLoader.texture;
            }
            else if (a == 2 && c == 1) {
                loadInternalTexture("sprites/ride/" + i + "/rf");
                AssetLoader.ride[a][i][c] = AssetLoader.texture;
            }
            else if (a == 3 && c == 0) {
                loadInternalTexture("sprites/ride/" + i + "/eb");
                AssetLoader.ride[a][i][c] = AssetLoader.texture;
            }
            else if (a == 3 && c == 1) {
                loadInternalTexture("sprites/ride/" + i + "/ef");
                AssetLoader.ride[a][i][c] = AssetLoader.texture;
            }
        }
        return AssetLoader.ride[a][i][c];
    }
    
    public static String getAccString(final int i) {
        switch (i) {
            case 1: {
                return "blanket";
            }
            case 2: {
                return "saddle";
            }
            case 3: {
                return "horn";
            }
            case 4: {
                return "socks";
            }
            case 5: {
                return "stockings";
            }
            case 6:
            case 7:
            case 8:
            case 9: {
                return "face";
            }
            case 10:
            case 11:
            case 12:
            case 13:
            case 14: {
                return "mane";
            }
            default: {
                return "blanket";
            }
        }
    }
    
    public static Texture getRideAcc(final int a, final int i, final int c) {
        if (AssetLoader.rideacc[a][i][c] == null) {
            if (i == 3) {
                if (c == 0) {
                    if (a == 0) {
                        loadInternalTexture("sprites/ride/" + getAccString(i) + "/1s");
                        AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                    }
                    else if (a == 1) {
                        loadInternalTexture("sprites/ride/" + getAccString(i) + "/1w");
                        AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                    }
                    else if (a == 2) {
                        loadInternalTexture("sprites/ride/" + getAccString(i) + "/1r");
                        AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                    }
                    else if (a == 3) {
                        loadInternalTexture("sprites/ride/" + getAccString(i) + "/1s");
                        AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                    }
                }
                else if (a == 0) {
                    loadInternalTexture("sprites/ride/" + getAccString(i) + "/2s");
                    AssetLoader.rideacc[a][i][1] = AssetLoader.texture;
                }
                else if (a == 1) {
                    loadInternalTexture("sprites/ride/" + getAccString(i) + "/2w");
                    AssetLoader.rideacc[a][i][1] = AssetLoader.texture;
                }
                else if (a == 2) {
                    loadInternalTexture("sprites/ride/" + getAccString(i) + "/2r");
                    AssetLoader.rideacc[a][i][1] = AssetLoader.texture;
                }
                else if (a == 3) {
                    loadInternalTexture("sprites/ride/" + getAccString(i) + "/2s");
                    AssetLoader.rideacc[a][i][1] = AssetLoader.texture;
                }
            }
            else if (i == 1 || i == 2 || i == 4 || i == 5) {
                if (c == 0) {
                    if (a == 0) {
                        loadInternalTexture("sprites/ride/" + getAccString(i) + "/s");
                        AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                    }
                    else if (a == 1) {
                        loadInternalTexture("sprites/ride/" + getAccString(i) + "/w");
                        AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                    }
                    else if (a == 2) {
                        loadInternalTexture("sprites/ride/" + getAccString(i) + "/r");
                        AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                    }
                    else if (a == 3) {
                        loadInternalTexture("sprites/ride/" + getAccString(i) + "/s");
                        AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                    }
                }
            }
            else if (i >= 6 && i <= 9) {
                if (c == 0) {
                    if (a == 0) {
                        loadInternalTexture("sprites/ride/face/" + (i - 5) + "s");
                        AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                    }
                    else if (a == 1) {
                        loadInternalTexture("sprites/ride/face/" + (i - 5) + "w");
                        AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                    }
                    else if (a == 2) {
                        loadInternalTexture("sprites/ride/face/" + (i - 5) + "r");
                        AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                    }
                    else if (a == 3) {
                        loadInternalTexture("sprites/ride/face/" + (i - 5) + "s");
                        AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                    }
                }
            }
            else if (i >= 10 && i <= 14) {
                if (a == 0 && c == 0) {
                    loadInternalTexture("sprites/ride/" + getAccString(i) + "/s/trim" + (i - 9) + "b");
                    AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                }
                else if (a == 0 && c == 1) {
                    loadInternalTexture("sprites/ride/" + getAccString(i) + "/s/trim" + (i - 9) + "f");
                    AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                }
                else if (a == 1 && c == 0) {
                    loadInternalTexture("sprites/ride/" + getAccString(i) + "/w/trim" + (i - 9) + "b");
                    AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                }
                else if (a == 1 && c == 1) {
                    loadInternalTexture("sprites/ride/" + getAccString(i) + "/w/trim" + (i - 9) + "f");
                    AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                }
                else if (a == 2 && c == 0) {
                    loadInternalTexture("sprites/ride/" + getAccString(i) + "/r/trim" + (i - 9) + "b");
                    AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                }
                else if (a == 2 && c == 1) {
                    loadInternalTexture("sprites/ride/" + getAccString(i) + "/r/trim" + (i - 9) + "f");
                    AssetLoader.rideacc[a][i][c] = AssetLoader.texture;
                }
            }
        }
        final Texture texture = AssetLoader.rideacc[a][i][c];
        return AssetLoader.rideacc[a][i][c];
    }
    
    public static TextureRegion getHMRegion(final int h, final int ddir) {
        final int sY = ddir * 64;
        final TextureRegion tr = AssetLoader.masks.findRegion("hats/" + h);
        if (tr != null) {
            final TextureRegion dr = new TextureRegion(tr.getTexture(), tr.getRegionX(), tr.getRegionY() + sY, 64, 64);
            return dr;
        }
        System.out.println("woo");
        return null;
    }
    
    public static TextureRegion getSpriteSheetRegion(final int a, final int b, final int c, final int daction) {
        String s = "walks";
        if (daction == 0) {
            s = "w";
        }
        else if (daction == 1) {
            s = "a";
        }
        else if (daction == 2) {
            s = "s";
        }
        else if (daction == 3) {
            s = "t";
        }
        else {
            s = "c";
        }
        String name = "";
        if (b == 15 || b == 4 || b == 16) {
            name = String.valueOf(getSetString(a)) + "/" + getTypeString(b) + "/" + (c + 100) / 100 + "/" + c % 100 + s;
        }
        else {
            name = String.valueOf(getSetString(a)) + "/" + getTypeString(b) + "/" + c + s;
        }
        if (b == 4 || b == 6 || b == 7 || b == 9 || b == 11 || b == 15 || b == 19 || b == 12) {
            if (b == 15 || b == 4 || b == 16) {
                name = String.valueOf(getTypeString(b)) + "/" + (c + 100) / 100 + "/" + c % 100;
                System.out.println(name);
            }
            else {
                name = String.valueOf(getTypeString(b)) + "/" + c;
            }
        }
        final TextureRegion tr = AssetLoader.atlas.findRegion(name);
        return tr;
    }
    
    public static int getCranialX(final int action, final int dir, final int step) {
        Label_0390: {
            switch (action) {
                case 1: {
                    Label_0224: {
                        switch (dir) {
                            case 0: {
                                switch (step) {
                                    case 0: {
                                        return 0;
                                    }
                                    case 1: {
                                        return 0;
                                    }
                                    case 2: {
                                        return -1;
                                    }
                                    case 3: {
                                        return 1;
                                    }
                                    case 4: {
                                        return 2;
                                    }
                                    case 5: {
                                        return 2;
                                    }
                                    default: {
                                        break Label_0224;
                                    }
                                }
                                break;
                            }
                            case 1: {
                                switch (step) {
                                    case 0: {
                                        return 0;
                                    }
                                    case 1: {
                                        return -2;
                                    }
                                    case 2: {
                                        return -2;
                                    }
                                    case 3: {
                                        return -3;
                                    }
                                    case 4: {
                                        return -4;
                                    }
                                    case 5: {
                                        return -4;
                                    }
                                    default: {
                                        break Label_0224;
                                    }
                                }
                                break;
                            }
                            case 2: {
                                switch (step) {
                                    case 0: {
                                        return 0;
                                    }
                                    case 1: {
                                        return 0;
                                    }
                                    case 2: {
                                        return -1;
                                    }
                                    case 3: {
                                        return 2;
                                    }
                                    case 4: {
                                        return 3;
                                    }
                                    case 5: {
                                        return 3;
                                    }
                                    default: {
                                        break Label_0224;
                                    }
                                }
                                break;
                            }
                            case 3: {
                                switch (step) {
                                    case 0: {
                                        return 0;
                                    }
                                    case 1: {
                                        return 2;
                                    }
                                    case 2: {
                                        return 2;
                                    }
                                    case 3: {
                                        return 3;
                                    }
                                    case 4: {
                                        return 4;
                                    }
                                    case 5: {
                                        return 4;
                                    }
                                    default: {
                                        break Label_0390;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    switch (dir) {
                        case 0: {
                            switch (step) {
                                case 0: {
                                    return 0;
                                }
                                case 1: {
                                    return 0;
                                }
                                case 2: {
                                    return 0;
                                }
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                case 7:
                                case 8:
                                case 9:
                                case 10:
                                case 11:
                                case 12: {
                                    return -1;
                                }
                                default: {
                                    return 1;
                                }
                            }
                            break;
                        }
                        case 1: {
                            return 1;
                        }
                        case 2: {
                            return -1;
                        }
                        case 3: {
                            return -1;
                        }
                        default: {
                            break Label_0390;
                        }
                    }
                    break;
                }
                case 3: {
                    Label_0484: {
                        switch (dir) {
                            case 0: {
                                switch (step) {
                                    case 0: {
                                        return 0;
                                    }
                                    case 1: {
                                        return 0;
                                    }
                                    case 2: {
                                        return 1;
                                    }
                                    case 3: {
                                        return 1;
                                    }
                                    case 4: {
                                        return 2;
                                    }
                                    case 5: {
                                        return 2;
                                    }
                                    case 6: {
                                        return 2;
                                    }
                                    case 7: {
                                        return 1;
                                    }
                                    default: {
                                        break Label_0484;
                                    }
                                }
                                break;
                            }
                            case 1: {
                                switch (step) {
                                    case 0: {
                                        return 0;
                                    }
                                    case 1: {
                                        return 0;
                                    }
                                    case 2: {
                                        return 0;
                                    }
                                    case 3: {
                                        return 0;
                                    }
                                    case 4: {
                                        return -2;
                                    }
                                    case 5: {
                                        return -2;
                                    }
                                    case 6: {
                                        return -2;
                                    }
                                    case 7: {
                                        return 0;
                                    }
                                    default: {
                                        return 0;
                                    }
                                }
                                break;
                            }
                            case 2: {
                                return 0;
                            }
                            case 3: {
                                switch (step) {
                                    case 0: {
                                        return 0;
                                    }
                                    case 1: {
                                        return 0;
                                    }
                                    case 2: {
                                        return 0;
                                    }
                                    case 3: {
                                        return 0;
                                    }
                                    case 4: {
                                        return 2;
                                    }
                                    case 5: {
                                        return 2;
                                    }
                                    case 6: {
                                        return 2;
                                    }
                                    case 7: {
                                        return 0;
                                    }
                                    default: {
                                        return 0;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case 0: {
                    return 0;
                }
                default: {
                    return 0;
                }
            }
        }
    }
    
    public static int getCranialY(final int action, final int dir, final int step) {
        Label_0690: {
            switch (action) {
                case 0: {
                    Label_0270: {
                        switch (dir) {
                            case 0: {
                                switch (step) {
                                    case 0: {
                                        return -1;
                                    }
                                    case 1: {
                                        return -1;
                                    }
                                    case 2: {
                                        return -1;
                                    }
                                    case 3: {
                                        return 0;
                                    }
                                    case 4: {
                                        return -1;
                                    }
                                    case 5: {
                                        return -1;
                                    }
                                    case 6: {
                                        return -1;
                                    }
                                    case 7: {
                                        return 0;
                                    }
                                    case 8: {
                                        return -1;
                                    }
                                    default: {
                                        break Label_0270;
                                    }
                                }
                                break;
                            }
                            case 1: {
                                switch (step) {
                                    case 0: {
                                        return 0;
                                    }
                                    case 1: {
                                        return 1;
                                    }
                                    case 2: {
                                        return 0;
                                    }
                                    case 3: {
                                        return 0;
                                    }
                                    case 4: {
                                        return 0;
                                    }
                                    case 5: {
                                        return 1;
                                    }
                                    case 6: {
                                        return 0;
                                    }
                                    case 7: {
                                        return 0;
                                    }
                                    case 8: {
                                        return 0;
                                    }
                                    default: {
                                        break Label_0270;
                                    }
                                }
                                break;
                            }
                            case 2: {
                                switch (step) {
                                    case 0: {
                                        return 0;
                                    }
                                    case 1: {
                                        return 0;
                                    }
                                    case 2: {
                                        return 0;
                                    }
                                    case 3: {
                                        return 1;
                                    }
                                    case 4: {
                                        return 0;
                                    }
                                    case 5: {
                                        return 0;
                                    }
                                    case 6: {
                                        return 0;
                                    }
                                    case 7: {
                                        return 1;
                                    }
                                    case 8: {
                                        return 0;
                                    }
                                    default: {
                                        break Label_0270;
                                    }
                                }
                                break;
                            }
                            case 3: {
                                switch (step) {
                                    case 0: {
                                        return 0;
                                    }
                                    case 1: {
                                        return 1;
                                    }
                                    case 2: {
                                        return 0;
                                    }
                                    case 3: {
                                        return 0;
                                    }
                                    case 4: {
                                        return 0;
                                    }
                                    case 5: {
                                        return 1;
                                    }
                                    case 6: {
                                        return 0;
                                    }
                                    case 7: {
                                        return 0;
                                    }
                                    case 8: {
                                        return 0;
                                    }
                                    default: {
                                        break Label_0690;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case 1: {
                    Label_0524: {
                        switch (dir) {
                            case 0: {
                                switch (step) {
                                    case 0: {
                                        return 0;
                                    }
                                    case 1: {
                                        return 1;
                                    }
                                    case 2: {
                                        return 1;
                                    }
                                    case 3: {
                                        return 0;
                                    }
                                    case 4: {
                                        return 0;
                                    }
                                    case 5: {
                                        return 0;
                                    }
                                    default: {
                                        break Label_0524;
                                    }
                                }
                                break;
                            }
                            case 1: {
                                switch (step) {
                                    case 0: {
                                        return 0;
                                    }
                                    case 1: {
                                        return 2;
                                    }
                                    case 2: {
                                        return 0;
                                    }
                                    case 3: {
                                        return 0;
                                    }
                                    case 4: {
                                        return 0;
                                    }
                                    case 5: {
                                        return 0;
                                    }
                                    default: {
                                        break Label_0524;
                                    }
                                }
                                break;
                            }
                            case 2: {
                                switch (step) {
                                    case 0: {
                                        return 0;
                                    }
                                    case 1: {
                                        return 0;
                                    }
                                    case 2: {
                                        return 2;
                                    }
                                    case 3: {
                                        return 1;
                                    }
                                    case 4: {
                                        return 0;
                                    }
                                    case 5: {
                                        return 0;
                                    }
                                    default: {
                                        break Label_0524;
                                    }
                                }
                                break;
                            }
                            case 3: {
                                switch (step) {
                                    case 0: {
                                        return 0;
                                    }
                                    case 1: {
                                        return 2;
                                    }
                                    case 2: {
                                        return 0;
                                    }
                                    case 3: {
                                        return 0;
                                    }
                                    case 4: {
                                        return 0;
                                    }
                                    case 5: {
                                        return 0;
                                    }
                                    default: {
                                        break Label_0690;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    switch (dir) {
                        case 0: {
                            switch (step) {
                                case 0: {
                                    return 0;
                                }
                                case 1: {
                                    return 1;
                                }
                                case 2: {
                                    return 0;
                                }
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                case 7:
                                case 8:
                                case 9:
                                case 10:
                                case 11:
                                case 12: {
                                    return 0;
                                }
                                default: {
                                    return 0;
                                }
                            }
                            break;
                        }
                        case 1: {
                            return 0;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 0;
                        }
                        default: {
                            break Label_0690;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (dir) {
                        case 0: {
                            switch (step) {
                                case 0: {
                                    return 0;
                                }
                                case 1: {
                                    return 1;
                                }
                                case 2: {
                                    return 0;
                                }
                                case 3: {
                                    return 0;
                                }
                                case 4: {
                                    return 1;
                                }
                                case 5: {
                                    return 1;
                                }
                                case 6: {
                                    return 1;
                                }
                                case 7: {
                                    return 0;
                                }
                                default: {
                                    return 0;
                                }
                            }
                            break;
                        }
                        case 1: {
                            return 0;
                        }
                        case 2: {
                            switch (step) {
                                case 0: {
                                    return 0;
                                }
                                case 1: {
                                    return 0;
                                }
                                case 2: {
                                    return 0;
                                }
                                case 3: {
                                    return 0;
                                }
                                case 4: {
                                    return 1;
                                }
                                case 5: {
                                    return 1;
                                }
                                case 6: {
                                    return 1;
                                }
                                case 7: {
                                    return 0;
                                }
                                default: {
                                    return 0;
                                }
                            }
                            break;
                        }
                        case 3: {
                            return 0;
                        }
                        default: {
                            break Label_0690;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public static TextureRegion getSpriteRegion(final int a, final int b, final int c, final int daction, final int ddir, final int dstep) {
        final int sX = dstep * 64;
        final int sY = ddir * 64;
        final TextureRegion tr = getSpriteSheetRegion(a, b, c, daction);
        if (tr == null) {
            return null;
        }
        if (b == 4 || b == 6 || b == 7 || b == 9 || b == 11 || b == 15 || b == 19 || b == 12) {
            final TextureRegion dr = new TextureRegion(tr.getTexture(), tr.getRegionX(), tr.getRegionY() + sY, 64, 64);
            return dr;
        }
        final TextureRegion dr = new TextureRegion(tr.getTexture(), tr.getRegionX() + sX, tr.getRegionY() + sY, 64, 64);
        return dr;
    }
    
    public static int getInt(final int m) {
        return (int)(Math.random() * m);
    }
    
    public static TextureRegion getTileSetRegion(final int s) {
        final String f = Integer.toString(s / 100);
        final String f2 = Integer.toString(s % 100);
        final TextureRegion tr = AssetLoader.atlas.findRegion(String.valueOf(f) + "/" + f2);
        return tr;
    }
    
    public static TextureRegion getTileRegion(final int s, final int sx, final int sy) {
        final TextureRegion tr = getTileSetRegion(s);
        final TextureRegion dr = new TextureRegion(tr.getTexture(), tr.getRegionX() + sx, tr.getRegionY() + sy, 32, 32);
        return dr;
    }
    
    public static Texture getHairMask(final int a, final int b) {
        if (AssetLoader.hairMask[a][b] != null) {
            return AssetLoader.hairMask[a][b];
        }
        if (Gdx.files.local("assets/sprites/male/hair/" + a + "h/" + b + ".png").exists()) {
            loadInternalTexture("sprites/male/hair/" + a + "h/" + b);
            return AssetLoader.hairMask[a][b] = AssetLoader.texture;
        }
        return null;
    }
    
    public static Texture getWeapon(final int a, final int b, final int c) {
        if (AssetLoader.weapon[a][b][c] != null) {
            return AssetLoader.weapon[a][b][c];
        }
        if (Gdx.files.local("assets/sprites/" + getSetString(a) + "/weapons/" + getWeaponTypeString(b) + "/" + c + ".png").exists()) {
            loadInternalTexture("sprites/" + getSetString(a) + "/weapons/" + getWeaponTypeString(b) + "/" + c);
            return AssetLoader.weapon[a][b][c] = AssetLoader.texture;
        }
        return null;
    }
    
    public static String getMonsterString(final int i) {
        switch (i) {
            case 0: {
                return "a";
            }
            case 1: {
                return "ad";
            }
            case 2: {
                return "b";
            }
            case 3: {
                return "bd";
            }
            case 4: {
                return "h";
            }
            case 5: {
                return "hd";
            }
            case 6: {
                return "w";
            }
            case 7: {
                return "wd";
            }
            default: {
                return "womp";
            }
        }
    }
    
    public static void loadFirst(final DCGame thegame) {
        AssetLoader.game = thegame;
        loadFonts();
        (AssetLoader.cursorTex = new Texture[5])[0] = loadInternalTexture("iface/cur0");
        (AssetLoader.cursor = new Cursor[5])[0] = Gdx.graphics.newCursor(new Pixmap(Gdx.files.local("assets/iface/cur0.png")), 0, 0);
    }
    
    public static ParticleEffect getEffect(final int e) {
        final ParticleEffect fx = new ParticleEffect();
        fx.load(Gdx.files.local("assets/effects/" + e + ".p"), AssetLoader.masks);
        return fx;
    }
    
    public static Color getColorBar(final int a) {
        switch (a) {
            case 0: {
                return Color.WHITE;
            }
            case 1: {
                return Color.BLACK;
            }
            case 2: {
                return Color.RED;
            }
            case 3: {
                return Color.GREEN;
            }
            case 4: {
                return Color.BLUE;
            }
            case 5: {
                return new Color(1.0f, 1.0f, 0.0f, 1.0f);
            }
            case 6: {
                return new Color(1.0f, 0.0f, 1.0f, 1.0f);
            }
            case 7: {
                return new Color(0.0f, 1.0f, 1.0f, 1.0f);
            }
            default: {
                return Color.WHITE;
            }
        }
    }
    
    public static void loadAssets() {
        int i = 0;
        AssetLoader.atlas = AssetLoader.game.manager.get("assets/common.txt", TextureAtlas.class);
        AssetLoader.masks = AssetLoader.game.manager.get("assets/masks.txt", TextureAtlas.class);
        loadMobj();
        loadFrame();
        AssetLoader.cursor[1] = Gdx.graphics.newCursor(new Pixmap(Gdx.files.local("assets/iface/cur1.png")), 1, 1);
        AssetLoader.cursor[2] = Gdx.graphics.newCursor(new Pixmap(Gdx.files.local("assets/iface/cur2.png")), 1, 1);
        AssetLoader.cursor[3] = Gdx.graphics.newCursor(new Pixmap(Gdx.files.local("assets/iface/cur3.png")), 1, 1);
        AssetLoader.iface = new Texture[20];
        AssetLoader.cursorTex[1] = loadInternalTexture("iface/cur1");
        AssetLoader.cursorTex[2] = loadInternalTexture("iface/cur2");
        AssetLoader.cursorTex[3] = loadInternalTexture("iface/cur3");
        loadTexture("assets/iface/barb");
        AssetLoader.iface[0] = AssetLoader.texture;
        loadTexture("assets/iface/baricons");
        AssetLoader.iface[1] = AssetLoader.texture;
        loadTexture("assets/iface/baricons2");
        AssetLoader.iface[2] = AssetLoader.texture;
        loadTexture("assets/iface/bg1");
        AssetLoader.iface[3] = AssetLoader.texture;
        loadTexture("assets/iface/bg2");
        AssetLoader.iface[4] = AssetLoader.texture;
        loadTexture("assets/iface/bg3");
        AssetLoader.iface[5] = AssetLoader.texture;
        loadTexture("assets/iface/bg4");
        AssetLoader.iface[6] = AssetLoader.texture;
        loadTexture("assets/iface/bg5");
        AssetLoader.iface[7] = AssetLoader.texture;
        loadTexture("assets/iface/frame");
        AssetLoader.iface[8] = AssetLoader.texture;
        loadTexture("assets/iface/frametop");
        AssetLoader.iface[9] = AssetLoader.texture;
        for (int s = 0; s < getMaxS(); ++s) {
            for (int t = 0; t < getMaxT(); ++t) {
                if (t != 15 && t != 4 && t != 16 && (s == 0 || (t != 9 && t != 7 && t != 11 && t != 12))) {
                    for (i = 0; i < 50; ++i) {
                        if (Gdx.files.local("assets/sprites/" + getSetString(s) + "/" + getTypeString(t) + "/" + i + ".png").exists()) {}
                    }
                }
            }
            for (int t = 0; t < 6; ++t) {
                for (i = 0; i < 40; ++i) {
                    if (Gdx.files.local("assets/sprites/" + getSetString(s) + "/weapons/" + getWeaponTypeString(t) + "/" + i + ".png").exists()) {}
                }
            }
        }
        loadTexture("assets/iface/white");
        AssetLoader.white = AssetLoader.texture;
        AssetLoader.whitebox = newTR(AssetLoader.texture, 0, 0, AssetLoader.texture.getWidth(), AssetLoader.texture.getHeight());
        loadTexture("assets/iface/mapsel");
        AssetLoader.mapSel = AssetLoader.texture;
        loadTexture("assets/iface/tilesel");
        AssetLoader.tileSel = AssetLoader.texture;
        loadTexture("assets/iface/att");
        AssetLoader.att = new TextureRegion[21];
        AssetLoader.attTex = AssetLoader.texture;
        for (i = 0; i < 21; ++i) {
            final int sx = i % 7;
            final int sy = i / 7;
            AssetLoader.att[i] = newTR(AssetLoader.texture, sx * 32, sy * 32, 32, 32);
        }
        loadTexture("assets/iface/s");
        AssetLoader.shadow = newTR(AssetLoader.texture, 0, 0, 4, 4);
        loadTexture("assets/iface/w");
        AssetLoader.wall = newTR(AssetLoader.texture, 0, 0, 4, 4);
        loadTexture("assets/iface/wall");
        AssetLoader.walls = newTR(AssetLoader.texture, 0, 0, 256, 32);
        AssetLoader.wallTex = AssetLoader.texture;
        loadInternalTexture("iface/topmask");
        AssetLoader.topmask = AssetLoader.texture;
        AssetLoader.topMask = new boolean[2][274][45];
        if (!AssetLoader.texture.getTextureData().isPrepared()) {
            AssetLoader.texture.getTextureData().prepare();
        }
        Pixmap pixmap = AssetLoader.texture.getTextureData().consumePixmap();
        for (int x = 0; x < 274; ++x) {
            for (int y = 0; y < 45; ++y) {
                final int ii = pixmap.getPixel(x, y);
                final Color c = new Color(ii);
                if (c.a > 0.0f) {
                    AssetLoader.topMask[0][x][y] = true;
                }
                else {
                    AssetLoader.topMask[0][x][y] = false;
                }
            }
        }
        loadInternalTexture("iface/topmask2");
        AssetLoader.topmask2 = AssetLoader.texture;
        if (!AssetLoader.texture.getTextureData().isPrepared()) {
            AssetLoader.texture.getTextureData().prepare();
        }
        pixmap = AssetLoader.texture.getTextureData().consumePixmap();
        for (int x = 0; x < 274; ++x) {
            for (int y = 0; y < 45; ++y) {
                final Color c2 = new Color(pixmap.getPixel(x, y));
                if (c2.a > 0.0f) {
                    AssetLoader.topMask[1][x][y] = true;
                }
                else {
                    AssetLoader.topMask[1][x][y] = false;
                }
            }
        }
        int highest = 0;
        for (int s2 = 0; s2 < getMaxS(); ++s2) {
            for (int t2 = 0; t2 < getMaxT(); ++t2) {
                if (t2 != 15 && t2 != 4 && t2 != 16) {
                    highest = 0;
                    if (s2 == 0 || (t2 != 9 && t2 != 7 && t2 != 11 && t2 != 12)) {
                        for (i = 0; i < 50; ++i) {
                            if (t2 == 6 || t2 == 7 || t2 == 9 || t2 == 11 || t2 == 19 || t2 == 12) {
                                final TextureRegion tr = AssetLoader.atlas.findRegion(String.valueOf(getTypeString(t2)) + "/" + i);
                                if (tr != null) {
                                    highest = i;
                                }
                            }
                            else {
                                final TextureRegion tr = AssetLoader.atlas.findRegion(String.valueOf(getSetString(s2)) + "/" + getTypeString(t2) + "/" + i + "w");
                                if (tr != null) {
                                    highest = i;
                                }
                            }
                        }
                    }
                }
                if (s2 == 0) {
                    AssetLoader.numSS[0][t2] = highest;
                    AssetLoader.numSS[1][t2] = highest;
                }
            }
            AssetLoader.numSS[0][15] = 3;
            AssetLoader.numSS[0][4] = 3;
            AssetLoader.numSS[0][16] = 1;
            AssetLoader.numSS[1][15] = 3;
            AssetLoader.numSS[1][4] = 3;
            AssetLoader.numSS[1][16] = 1;
            for (int t2 = 0; t2 < 6; ++t2) {
                highest = 0;
                for (i = 0; i < 40; ++i) {
                    if (Gdx.files.local("assets/sprites/" + getSetString(s2) + "/weapons/" + getWeaponTypeString(t2) + "/" + i + ".png").exists()) {
                        highest = i;
                    }
                }
                AssetLoader.numWS[s2][t2] = highest;
            }
        }
        for (int a = 0; a < 2; ++a) {}
    }
    
    public static TextureRegion getRainRegion(final int type) {
        return AssetLoader.atlas.findRegion("whole/rain" + type);
    }
    
    public static void loadTex(final String st) {
        AssetLoader.game.manager.load(String.valueOf(st) + ".png", Texture.class);
    }
    
    public static void preloadAssets() {
        final int i = 0;
        AssetLoader.numSS = new int[5][30];
        AssetLoader.numWS = new int[5][6];
        AssetLoader.sprite = new Texture[5][30][400];
        AssetLoader.weapon = new Texture[5][6][40];
        AssetLoader.hairMask = new Texture[50][50];
        AssetLoader.mobjdata = new Mobj[10][100];
        AssetLoader.ride = new Texture[4][20][2];
        AssetLoader.rideacc = new Texture[4][20][2];
        AssetLoader.game.manager.load("assets/common.txt", TextureAtlas.class);
        AssetLoader.game.manager.load("assets/masks.txt", TextureAtlas.class);
        loadTex("assets/iface/barb");
        loadTex("assets/iface/baricons");
        loadTex("assets/iface/baricons2");
        loadTex("assets/iface/bg1");
        loadTex("assets/iface/bg2");
        loadTex("assets/iface/bg3");
        loadTex("assets/iface/bg4");
        loadTex("assets/iface/bg5");
        loadTex("assets/iface/frame");
        loadTex("assets/iface/frametop");
        loadTex("assets/iface/white");
        loadTex("assets/iface/mapsel");
        loadTex("assets/iface/tilesel");
        loadTex("assets/iface/att");
        loadTex("assets/iface/s");
        loadTex("assets/iface/w");
        loadTex("assets/iface/wall");
    }
    
    public static String getWeaponTypeString(final int i) {
        switch (i) {
            case 0: {
                return "slash";
            }
            case 1: {
                return "shoot";
            }
            case 2: {
                return "thrust";
            }
            case 3: {
                return "shield";
            }
            case 4: {
                return "bigslash";
            }
            case 5: {
                return "bigthrust";
            }
            default: {
                return "slash";
            }
        }
    }
    
    public static Sound getSnd(final int i) {
        if (AssetLoader.snd[i] == null || i < 90909) {
            switch (i) {
                case 0:
                case 1:
                case 2:
                case 3: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/footsteps/grass" + i + ".ogg"));
                    break;
                }
                case 4:
                case 5:
                case 6:
                case 7: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/footsteps/step" + (i - 4) + ".ogg"));
                    break;
                }
                case 8:
                case 9:
                case 10:
                case 11: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/footsteps/wood" + (i - 8) + ".ogg"));
                    break;
                }
                case 12:
                case 13:
                case 14:
                case 15: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/footsteps/stone" + (i - 12) + ".ogg"));
                    break;
                }
                case 16:
                case 17: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/footsteps/water" + (i - 16) + ".ogg"));
                    break;
                }
                case 18:
                case 19:
                case 20: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/fire" + (i - 18) + ".ogg"));
                    break;
                }
                case 21:
                case 22:
                case 23:
                case 24:
                case 25: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/weather/" + (i - 21) + ".ogg"));
                    break;
                }
                case 26: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/water.ogg"));
                    break;
                }
                case 27: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/door0.ogg"));
                    break;
                }
                case 28: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/flap.ogg"));
                    break;
                }
                case 29: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/lilworm.ogg"));
                    break;
                }
                case 30: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/bigworm.ogg"));
                    break;
                }
                case 31: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/snake.ogg"));
                    break;
                }
                case 32: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/pumpking.ogg"));
                    break;
                }
                case 33: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/shroom.ogg"));
                    break;
                }
                case 34: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/ghost.ogg"));
                    break;
                }
                case 35: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/creak.ogg"));
                    break;
                }
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/combat/hit" + (i - 36) + ".ogg"));
                    break;
                }
                case 48:
                case 49:
                case 50:
                case 51:
                case 52: {
                    AssetLoader.snd[i] = Gdx.audio.newSound(Gdx.files.local("assets/sfx/combat/death" + (i - 48) + ".ogg"));
                    break;
                }
            }
            return AssetLoader.snd[i];
        }
        return null;
    }
    
    public static int getTileFootsteps(final int s, final int t) {
        final int y = t / 32;
        final int x = t % 32;
        Label_0328: {
            switch (s) {
                case 5: {
                    if (y >= 5 && y <= 10) {
                        return 3;
                    }
                    if (y < 5) {
                        if (x >= 6) {
                            return 3;
                        }
                        break;
                    }
                    else {
                        if (y >= 11 && x >= 9) {
                            return 3;
                        }
                        break;
                    }
                    break;
                }
                case 6: {
                    if (y <= 3 || y >= 10) {
                        return 3;
                    }
                    break;
                }
                case 8:
                case 9: {
                    return 3;
                }
                case 200: {
                    if (y >= 13 && x <= 3) {
                        return 3;
                    }
                }
                case 300: {
                    switch (t) {
                        case 3:
                        case 4:
                        case 34:
                        case 64:
                        case 96:
                        case 97:
                        case 98:
                        case 99: {
                            return 2;
                        }
                        case 5:
                        case 6:
                        case 7:
                        case 33:
                        case 37:
                        case 38:
                        case 39:
                        case 40:
                        case 70:
                        case 71:
                        case 102:
                        case 103:
                        case 167:
                        case 199: {
                            return 3;
                        }
                        default: {
                            break Label_0328;
                        }
                    }
                    break;
                }
            }
        }
        return 1;
    }
    
    public static String getQuote(final int b) {
        final int a = b % 5;
        switch (a) {
            case 0: {
                return "\"Anything that, in happening, causes itself to happen again, happens again\" -Douglas Adams";
            }
            case 1: {
                return "Reticulating Splines";
            }
            case 2: {
                return "\"Any sufficiently advanced technology is indistinguishable from magic.\" -Arthur C Clarke.";
            }
            case 3: {
                return "Figuring out which way is up";
            }
            case 4: {
                return "Superintendent Heisenberg was uncertain which vice principal to promote.";
            }
            case 5: {
                return "Shaking snow globes";
            }
            case 6: {
                return "\"I am the wisest man alive, for I know one thing, and that is that I know nothing.\" - Plato";
            }
            case 7: {
                return "Misplacing important game assets";
            }
            case 8: {
                return "\"There are things known and there are things unknown, and in between are the doors of perception.\" - Aldous Huxley";
            }
            case 9: {
                return "Spilling Coffee";
            }
            case 10: {
                return "\"People like us, who believe in physics, know that the distinction between past, present and future is only a stubbornly persistent illusion.\" \u2013 Albert Einstein.";
            }
            case 11: {
                return "3/8\"";
            }
            case 12: {
                return "\"History will be kind to me for I intend to write it.\" - Winston Churchill";
            }
            default: {
                return "Sharpening Axe";
            }
        }
    }
    
    public static long getSoundDuration(final int s) {
        switch (s) {
            case 18: {
                return 1000L;
            }
            case 19: {
                return 4000L;
            }
            case 20: {
                return 2000L;
            }
            case 26: {
                return 6000L;
            }
            case 27: {
                return 1000L;
            }
            case 21: {
                return 27000L;
            }
            case 22: {
                return 26000L;
            }
            case 23: {
                return 37000L;
            }
            case 24: {
                return 45000L;
            }
            case 25: {
                return 54000L;
            }
            default: {
                return 500L;
            }
        }
    }
    
    public static void loadMobj() {
        for (int s = 0; s < 10; ++s) {
            for (int m = 0; m < numMobj(s); ++m) {
                AssetLoader.mobjdata[s][m] = new Mobj();
                try {
                    final FileInputStream f = new FileInputStream(new File("walls/" + s + "/" + m));
                    final ObjectInputStream o = new ObjectInputStream(f);
                    AssetLoader.mobjdata[s][m].wall = (boolean[][])o.readObject();
                    o.close();
                    f.close();
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
                catch (ClassNotFoundException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }
    
    public static String getCatName(final int i) {
        switch (i) {
            case 0: {
                return "Terrain";
            }
            case 1: {
                return "Vegetation";
            }
            case 2: {
                return "City-Out";
            }
            case 3: {
                return "City-In";
            }
            case 4: {
                return "Outside";
            }
            case 5: {
                return "Castle";
            }
            default: {
                return "Tileset";
            }
        }
    }
    
    public static int numMobj(final int s) {
        switch (s) {
            case 0: {
                return 27;
            }
            case 1: {
                return 13;
            }
            case 2: {
                return 13;
            }
            case 3: {
                return 34;
            }
            case 4: {
                return 29;
            }
            case 5: {
                return 57;
            }
            case 6: {
                return 25;
            }
            case 7: {
                return 22;
            }
            case 8: {
                return 23;
            }
            case 9: {
                return 13;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static int getEdgePiece(final int diff) {
        switch (diff) {
            case 1: {
                return 10;
            }
            case 2: {
                return 11;
            }
            case 33: {
                return 12;
            }
            case 34: {
                return 13;
            }
            case 64: {
                return 1;
            }
            case 65: {
                return 2;
            }
            case 66: {
                return 3;
            }
            case 96: {
                return 4;
            }
            case 98: {
                return 6;
            }
            case 128: {
                return 7;
            }
            case 129: {
                return 8;
            }
            case 130: {
                return 9;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static int getTileDiff(final int p) {
        switch (p) {
            case 1: {
                return 64;
            }
            case 2: {
                return 65;
            }
            case 3: {
                return 66;
            }
            case 4: {
                return 96;
            }
            case 6: {
                return 98;
            }
            case 7: {
                return 128;
            }
            case 8: {
                return 129;
            }
            case 9: {
                return 130;
            }
            case 10: {
                return 1;
            }
            case 11: {
                return 2;
            }
            case 12: {
                return 33;
            }
            case 13: {
                return 34;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static int getEdgePiece(final int prevp, final int fromd, final int nextd) {
        Label_1253: {
            switch (fromd) {
                case 0: {
                    switch (nextd) {
                        case 1: {
                            switch (prevp) {
                                case 1:
                                case 4:
                                case 11: {
                                    return 13;
                                }
                                case 3:
                                case 6:
                                case 10: {
                                    return 9;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                        case 2: {
                            switch (prevp) {
                                case 1:
                                case 4:
                                case 11: {
                                    return 4;
                                }
                                case 3:
                                case 6:
                                case 10: {
                                    return 6;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                        case 3: {
                            switch (prevp) {
                                case 1:
                                case 4:
                                case 11: {
                                    return 7;
                                }
                                case 3:
                                case 6:
                                case 10: {
                                    return 12;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                        default: {
                            switch (prevp) {
                                case 1:
                                case 4:
                                case 11: {
                                    return 4;
                                }
                                case 3:
                                case 6:
                                case 10: {
                                    return 6;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (nextd) {
                        case 0: {
                            switch (prevp) {
                                case 1:
                                case 2:
                                case 12: {
                                    return 13;
                                }
                                case 7:
                                case 8:
                                case 10: {
                                    return 9;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                        case 2: {
                            switch (prevp) {
                                case 1:
                                case 2:
                                case 12: {
                                    return 3;
                                }
                                case 7:
                                case 8:
                                case 10: {
                                    return 11;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                        case 3: {
                            switch (prevp) {
                                case 1:
                                case 2:
                                case 12: {
                                    return 2;
                                }
                                case 7:
                                case 8:
                                case 10: {
                                    return 8;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                        default: {
                            switch (prevp) {
                                case 1:
                                case 2:
                                case 12: {
                                    return 2;
                                }
                                case 7:
                                case 8:
                                case 10: {
                                    return 8;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (nextd) {
                        case 0: {
                            switch (prevp) {
                                case 4:
                                case 7:
                                case 13: {
                                    return 4;
                                }
                                case 6:
                                case 9:
                                case 12: {
                                    return 6;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                        case 1: {
                            switch (prevp) {
                                case 4:
                                case 7:
                                case 13: {
                                    return 11;
                                }
                                case 6:
                                case 9:
                                case 12: {
                                    return 3;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                        case 3: {
                            switch (prevp) {
                                case 4:
                                case 7:
                                case 13: {
                                    return 1;
                                }
                                case 6:
                                case 9:
                                case 12: {
                                    return 10;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                        default: {
                            switch (prevp) {
                                case 4:
                                case 7:
                                case 13: {
                                    return 4;
                                }
                                case 6:
                                case 9:
                                case 12: {
                                    return 6;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (nextd) {
                        case 0: {
                            switch (prevp) {
                                case 2:
                                case 3:
                                case 13: {
                                    return 12;
                                }
                                case 8:
                                case 9:
                                case 11: {
                                    return 7;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                        case 1: {
                            switch (prevp) {
                                case 2:
                                case 3:
                                case 13: {
                                    return 2;
                                }
                                case 8:
                                case 9:
                                case 11: {
                                    return 8;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                        case 2: {
                            switch (prevp) {
                                case 2:
                                case 3:
                                case 13: {
                                    return 1;
                                }
                                case 8:
                                case 9:
                                case 11: {
                                    return 10;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                        default: {
                            switch (prevp) {
                                case 2:
                                case 3:
                                case 13: {
                                    return 2;
                                }
                                case 8:
                                case 9:
                                case 11: {
                                    return 8;
                                }
                                default: {
                                    break Label_1253;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public static int numTileset(final int s) {
        switch (s) {
            case 0: {
                return 9;
            }
            case 1: {
                return 4;
            }
            case 2: {
                return 6;
            }
            case 3: {
                return 11;
            }
            case 4: {
                return 4;
            }
            case 5: {
                return 2;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static String getLayerName(final int i) {
        switch (i) {
            case 0: {
                return "BG1";
            }
            case 1: {
                return "BG2";
            }
            case 2: {
                return "BG3";
            }
            case 3: {
                return "BG4";
            }
            case 4: {
                return "Mid";
            }
            case 5: {
                return "FG1";
            }
            case 6: {
                return "FG2";
            }
            case 7: {
                return "FG3";
            }
            default: {
                return "NONONO";
            }
        }
    }
    
    public static boolean[][] getEdgeWall(final int piece) {
        final boolean[][] swall = new boolean[8][8];
        switch (piece) {
            case 10: {
                swall[7][7] = true;
                swall[7][6] = true;
                swall[6][7] = true;
                break;
            }
            case 11: {
                swall[0][7] = true;
                swall[0][6] = true;
                swall[1][7] = true;
                break;
            }
            case 12: {
                swall[7][0] = true;
                swall[7][1] = true;
                swall[6][0] = true;
                break;
            }
            case 13: {
                swall[0][0] = true;
                swall[0][1] = true;
                swall[1][0] = true;
                break;
            }
            case 1: {
                for (int w = 0; w < 8; ++w) {
                    for (int h = 0; h < 8; ++h) {
                        if (w + h < 10) {
                            swall[w][h] = true;
                        }
                    }
                }
                for (int w = 0; w < 8; ++w) {
                    for (int h = 0; h < 3; ++h) {
                        swall[w][h] = true;
                    }
                }
                for (int w = 0; w < 3; ++w) {
                    for (int h = 0; h < 8; ++h) {
                        swall[w][h] = true;
                    }
                }
                break;
            }
            case 2: {
                for (int w = 0; w < 8; ++w) {
                    for (int h = 0; h < 3; ++h) {
                        swall[w][h] = true;
                    }
                }
                break;
            }
            case 3: {
                for (int w = 0; w < 8; ++w) {
                    for (int h = 0; h < 8; ++h) {
                        if (h - w < 3) {
                            swall[w][h] = true;
                        }
                    }
                }
                for (int w = 0; w < 8; ++w) {
                    for (int h = 0; h < 3; ++h) {
                        swall[w][h] = true;
                    }
                }
                for (int w = 0; w < 3; ++w) {
                    for (int h = 0; h < 8; ++h) {
                        swall[7 - w][h] = true;
                    }
                }
                break;
            }
            case 4: {
                for (int w = 0; w < 3; ++w) {
                    for (int h = 0; h < 8; ++h) {
                        swall[w][h] = true;
                    }
                }
                break;
            }
            case 6: {
                for (int w = 0; w < 3; ++w) {
                    for (int h = 0; h < 8; ++h) {
                        swall[7 - w][h] = true;
                    }
                }
                break;
            }
            case 7: {
                for (int w = 0; w < 8; ++w) {
                    for (int h = 0; h < 8; ++h) {
                        if (w + h < 10) {
                            swall[w][7 - h] = true;
                        }
                    }
                }
                for (int w = 0; w < 8; ++w) {
                    for (int h = 0; h < 3; ++h) {
                        swall[w][7 - h] = true;
                    }
                }
                for (int w = 0; w < 3; ++w) {
                    for (int h = 0; h < 8; ++h) {
                        swall[w][7 - h] = true;
                    }
                }
                break;
            }
            case 8: {
                for (int w = 0; w < 8; ++w) {
                    for (int h = 0; h < 3; ++h) {
                        swall[w][7 - h] = true;
                    }
                }
                break;
            }
            case 9: {
                for (int w = 0; w < 8; ++w) {
                    for (int h = 0; h < 8; ++h) {
                        if (h - w < 3) {
                            swall[w][7 - h] = true;
                        }
                    }
                }
                for (int w = 0; w < 8; ++w) {
                    for (int h = 0; h < 3; ++h) {
                        swall[w][7 - h] = true;
                    }
                }
                for (int w = 0; w < 3; ++w) {
                    for (int h = 0; h < 8; ++h) {
                        swall[7 - w][7 - h] = true;
                    }
                }
                break;
            }
        }
        return swall;
    }
    
    public static int getTilesetWidth(final int i) {
        switch (i) {
            case 1: {
                return 96;
            }
            default: {
                return 640;
            }
        }
    }
    
    public static int getTilesetHeight(final int i) {
        switch (i) {
            case 1: {
                return 192;
            }
            default: {
                return 640;
            }
        }
    }
    
    public static String dirName(final int i) {
        switch (i) {
            case 0: {
                return "Up";
            }
            case 1: {
                return "Left";
            }
            case 2: {
                return "Down";
            }
            case 3: {
                return "Right";
            }
            default: {
                return "Dimensional";
            }
        }
    }
    
    static int getMaxS() {
        return 2;
    }
    
    static int getMaxT() {
        return 22;
    }
    
    public static String getTypeString(final int t) {
        switch (t) {
            case 0: {
                return "arms";
            }
            case 1: {
                return "back";
            }
            case 2: {
                return "belt";
            }
            case 3: {
                return "body";
            }
            case 4: {
                return "ears";
            }
            case 5: {
                return "eyes";
            }
            case 6: {
                return "face";
            }
            case 7: {
                return "facial";
            }
            case 8: {
                return "feet";
            }
            case 9: {
                return "hair";
            }
            case 10: {
                return "hands";
            }
            case 11: {
                return "head";
            }
            case 12: {
                return "fins";
            }
            case 13: {
                return "legs";
            }
            case 14: {
                return "neck";
            }
            case 15: {
                return "nose";
            }
            case 16: {
                return "tail";
            }
            case 17: {
                return "torso0";
            }
            case 18: {
                return "torso1";
            }
            case 19: {
                return "heads";
            }
            case 20: {
                return "wings";
            }
            case 21: {
                return "shoulders";
            }
            default: {
                return "boof";
            }
        }
    }
    
    public static String getSetString(final int s) {
        switch (s) {
            case 0: {
                return "male";
            }
            case 1: {
                return "female";
            }
            default: {
                return "male";
            }
        }
    }
    
    public int findNextLetter(final int start) {
        if (start < 0 || start >= 1024) {
            return -1;
        }
        if (!AssetLoader.texture.getTextureData().isPrepared()) {
            AssetLoader.texture.getTextureData().prepare();
        }
        final Pixmap pixmap = AssetLoader.texture.getTextureData().consumePixmap();
        for (int x = start; x < start + 16; ++x) {
            if (x >= 0 && x < AssetLoader.texture.getTextureData().getWidth()) {
                final Color c = new Color(pixmap.getPixel(x, 1));
                if (c.a == 1.0 && c.b == 1.0 && c.g == 0.0f) {
                    return x;
                }
            }
        }
        return -1;
    }
    
    public void outputBreaks() {
        int a = 32;
        int curX = 0;
        boolean checkAgain;
        do {
            checkAgain = false;
            final int startX = this.findNextLetter(curX);
            if (startX >= 0) {
                final int endX = this.findNextLetter(startX + 2);
                if (endX < 0) {
                    continue;
                }
                final int width = endX - startX - 2;
                AssetLoader.fontWidth[a] = width;
                AssetLoader.fontX[a] = startX + 1;
                checkAgain = true;
                curX = endX;
                ++a;
            }
        } while (checkAgain);
        for (int i = 0; i < 256; ++i) {}
    }
    
    public static TextureRegion newTR(final Texture tex, final int x, final int y, final int w, final int h) {
        final TextureRegion t = new TextureRegion(tex, x, y, w, h);
        fix(t, false, true);
        return t;
    }
    
    public static void fixBleeding(final TextureRegion region, final int x, final int y, final int width, final int height) {
        final float fix = 0.01f;
        final float invTexWidth = 1.0f / region.getTexture().getWidth();
        final float invTexHeight = 1.0f / region.getTexture().getHeight();
        region.setRegion((x + fix) * invTexWidth, (y + fix) * invTexHeight, (x + width - fix) * invTexWidth, (y + height - fix) * invTexHeight);
    }
    
    public static void loadFrame() {
        int i = 0;
        loadTexture("assets/iface/frame");
        AssetLoader.frameTex = AssetLoader.texture;
        AssetLoader.frame = new TextureRegion[19];
        for (i = 0; i < 8; ++i) {
            AssetLoader.frame[i] = newTR(AssetLoader.texture, i * 32, 0, 32, 32);
        }
        AssetLoader.frame[8] = newTR(AssetLoader.texture, 0, 56, 1, 1);
        AssetLoader.frame[9] = newTR(AssetLoader.texture, 200, 42, 2, 22);
        AssetLoader.frame[10] = newTR(AssetLoader.texture, 214, 42, 2, 22);
        AssetLoader.frame[11] = newTR(AssetLoader.texture, 78, 56, 16, 16);
        AssetLoader.frame[12] = newTR(AssetLoader.texture, 78, 72, 16, 16);
        AssetLoader.frame[13] = newTR(AssetLoader.texture, 62, 56, 16, 16);
        AssetLoader.frame[14] = newTR(AssetLoader.texture, 62, 72, 16, 16);
        AssetLoader.frame[15] = newTR(AssetLoader.texture, 94, 72, 12, 16);
        AssetLoader.frame[16] = newTR(AssetLoader.texture, 94, 56, 12, 16);
        AssetLoader.frame[17] = newTR(AssetLoader.texture, 106, 56, 13, 13);
        AssetLoader.frame[18] = newTR(AssetLoader.texture, 106, 69, 13, 13);
        AssetLoader.button = new TextureRegion[2][9];
        for (int b = 0; b < 2; ++b) {
            for (i = 0; i < 8; ++i) {
                fixBleeding(AssetLoader.button[b][i] = new TextureRegion(AssetLoader.texture, 119 + i * 8, 56 + b * 8, 8, 8), 119 + i * 8, 56 + b * 8, 8, 8);
                fix(AssetLoader.button[b][i], false, true);
            }
            fixBleeding(AssetLoader.button[b][8] = new TextureRegion(AssetLoader.texture, 183, 56 + b * 8, 8, 8), 183, 56 + b * 8, 8, 8);
            fix(AssetLoader.button[b][8], false, true);
        }
        AssetLoader.field = new TextureRegion[2][3];
        for (int b = 0; b < 2; ++b) {
            fixBleeding(AssetLoader.field[b][0] = new TextureRegion(AssetLoader.texture, 0, 88 + b * 26, 42, 26), 0, 88 + b * 26, 42, 26);
            fix(AssetLoader.field[b][0], false, true);
            fixBleeding(AssetLoader.field[b][1] = new TextureRegion(AssetLoader.texture, 42, 88 + b * 26, 32, 26), 42, 88 + b * 26, 32, 26);
            fix(AssetLoader.field[b][1], false, true);
            fixBleeding(AssetLoader.field[b][2] = new TextureRegion(AssetLoader.texture, 74, 88 + b * 26, 41, 26), 74, 88 + b * 26, 41, 26);
            fix(AssetLoader.field[b][2], false, true);
        }
        AssetLoader.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }
    
    public static void loadFonts() {
        int i = 0;
        loadInternalTexture("font");
        AssetLoader.font = new TextureRegion[2][256];
        AssetLoader.fontWidth = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 5, 6, 9, 9, 10, 10, 3, 5, 5, 9, 7, 3, 8, 3, 7, 7, 5, 7, 7, 8, 7, 7, 7, 7, 7, 3, 3, 8, 6, 8, 7, 9, 7, 7, 7, 8, 7, 7, 7, 7, 5, 8, 7, 7, 9, 8, 7, 7, 8, 8, 7, 7, 7, 7, 9, 8, 7, 7, 5, 7, 5, 10, 7, 5, 7, 7, 7, 7, 7, 6, 7, 7, 5, 5, 7, 4, 9, 7, 7, 7, 8, 7, 7, 7, 7, 7, 9, 7, 7, 7, 6, 3, 6, 8, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        AssetLoader.fontX = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 17, 25, 36, 47, 59, 71, 76, 83, 90, 101, 110, 115, 125, 130, 139, 148, 155, 164, 173, 183, 192, 201, 210, 219, 228, 233, 238, 248, 256, 266, 275, 286, 295, 304, 313, 323, 332, 341, 350, 359, 366, 376, 385, 394, 405, 415, 424, 433, 443, 453, 462, 471, 480, 489, 500, 510, 519, 528, 535, 544, 551, 563, 572, 579, 588, 597, 606, 615, 624, 632, 641, 650, 657, 664, 673, 679, 690, 699, 708, 717, 727, 736, 745, 754, 763, 772, 783, 792, 801, 810, 818, 823, 831, 841, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        for (i = 0; i < 256; ++i) {
            if (AssetLoader.fontWidth[i] > 0) {
                final int[] fontWidth = AssetLoader.fontWidth;
                final int n = i;
                ++fontWidth[n];
                for (int t = 0; t < 2; ++t) {
                    fixBleeding(AssetLoader.font[t][i] = new TextureRegion(AssetLoader.texture, AssetLoader.fontX[i], t * 16, AssetLoader.fontWidth[i], 16), AssetLoader.fontX[i], t * 16, AssetLoader.fontWidth[i], 16);
                    fix(AssetLoader.font[t][i], false, true);
                    AssetLoader.font[t][i].getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                }
            }
        }
    }
    
    public static String randomSurName() {
        final int r = rndInt(90483);
        for (int a = 0; a < AssetLoader.numSurNames; ++a) {
            if (AssetLoader.snamesf[a] > r) {
                return AssetLoader.snames[a];
            }
        }
        return "Borgington";
    }
    
    public static String randomFemaleName() {
        final int r = rndInt(90024);
        for (int a = 0; a < AssetLoader.numFemaleNames; ++a) {
            if (AssetLoader.namesff[a] > r) {
                return AssetLoader.namesf[a];
            }
        }
        return "Borga";
    }
    
    public static String randomMaleName() {
        final int r = rndInt(90040);
        for (int a = 0; a < AssetLoader.numMaleNames; ++a) {
            if (AssetLoader.namesmf[a] > r) {
                return AssetLoader.namesm[a];
            }
        }
        return "Borg";
    }
    
    public static void loadFemaleNames() {
        AssetLoader.namesf = new String[5000];
        AssetLoader.namesff = new int[5000];
        final FileHandle nameFile = Gdx.files.local("assets/firstnamesf");
        final String text = nameFile.readString();
        final List<String> lines = Arrays.asList(text.split("\\r?\\n"));
        String name = "BOBA";
        String fname = "Boba";
        String n = "5.555";
        int f = 0;
        int c = 0;
        for (final String curLine : lines) {
            n = curLine.substring(21, 27);
            f = (int)(Float.parseFloat(n) * 1000.0f);
            name = curLine.substring(0, 14);
            name = name.trim();
            fname = name.substring(0, 1);
            name = name.substring(1);
            char[] charArray;
            for (int length = (charArray = name.toCharArray()).length, i = 0; i < length; ++i) {
                final char ch = charArray[i];
                fname = String.valueOf(fname) + Character.toLowerCase(ch);
            }
            AssetLoader.namesf[c] = fname;
            AssetLoader.namesff[c] = f;
            ++c;
        }
        AssetLoader.numFemaleNames = c;
    }
    
    public static void loadMaleNames() {
        AssetLoader.namesm = new String[5000];
        AssetLoader.namesmf = new int[5000];
        final FileHandle nameFile = Gdx.files.local("assets/firstnamesm");
        final String text = nameFile.readString();
        final List<String> lines = Arrays.asList(text.split("\\r?\\n"));
        String name = "BOBA";
        String fname = "Boba";
        String n = "5.555";
        int f = 0;
        int c = 0;
        for (final String curLine : lines) {
            n = curLine.substring(21, 27);
            f = (int)(Float.parseFloat(n) * 1000.0f);
            name = curLine.substring(0, 14);
            name = name.trim();
            fname = name.substring(0, 1);
            name = name.substring(1);
            char[] charArray;
            for (int length = (charArray = name.toCharArray()).length, i = 0; i < length; ++i) {
                final char ch = charArray[i];
                fname = String.valueOf(fname) + Character.toLowerCase(ch);
            }
            AssetLoader.namesm[c] = fname;
            AssetLoader.namesmf[c] = f;
            ++c;
        }
        AssetLoader.numMaleNames = c;
    }
    
    public static void loadSurNames() {
        AssetLoader.snames = new String[90000];
        AssetLoader.snamesf = new int[90000];
        final FileHandle nameFile = Gdx.files.local("assets/surnames");
        final String text = nameFile.readString();
        final List<String> lines = Arrays.asList(text.split("\\r?\\n"));
        String name = "BOBA";
        String fname = "Boba";
        String n = "5.555";
        int f = 0;
        int c = 0;
        for (final String curLine : lines) {
            n = curLine.substring(21, 27);
            f = (int)(Float.parseFloat(n) * 1000.0f);
            name = curLine.substring(0, 14);
            name = name.trim();
            fname = name.substring(0, 1);
            name = name.substring(1);
            char[] charArray;
            for (int length = (charArray = name.toCharArray()).length, i = 0; i < length; ++i) {
                final char ch = charArray[i];
                fname = String.valueOf(fname) + Character.toLowerCase(ch);
            }
            AssetLoader.snames[c] = fname;
            AssetLoader.snamesf[c] = f;
            ++c;
        }
        AssetLoader.numSurNames = c;
    }
    
    public static void loadTexture(final String name) {
        fixTexture(AssetLoader.texture = AssetLoader.game.manager.get(String.valueOf(name) + ".png", Texture.class));
    }
    
    public static Texture loadInternalTexture(final String name) {
        AssetLoader.texture = null;
        final FileHandle fh = Gdx.files.local("assets/" + name + ".png");
        if (fh.exists()) {
            fixTexture(AssetLoader.texture = new Texture(fh));
        }
        return AssetLoader.texture;
    }
    
    public static void fix(final TextureRegion tex, final boolean flipX, final boolean flipY) {
        fixTexture(tex.getTexture());
        tex.flip(flipX, flipY);
    }
    
    public static void fixTexture(final Texture tex) {
    }
    
    public static void loadMasks(final DCGame game) {
        final String[] s = { "c", "t", "w", "a", "s" };
        try {
            final FileInputStream f = new FileInputStream(new File("assets/amask"));
            final InputStream inputStream = new InflaterInputStream(f);
            final Input input = new Input(inputStream);
            AssetLoader.atlasMask = (AtlasMask)game.kryo.readObject(input, (Class)AtlasMask.class);
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
    
    public static void createMasks(final DCGame game) {
        AssetLoader.atlasMask = new AtlasMask();
        AssetLoader.pmaps = new Pixmap[15];
        for (int i = 0; i < 15; ++i) {
            final Texture tex = game.batcher.uTex[i];
            if (tex != null) {
                final TextureData td = tex.getTextureData();
                if (!td.isPrepared()) {
                    td.prepare();
                }
                AssetLoader.pmaps[i] = td.consumePixmap();
                for (int x = 0; x < 4096; ++x) {
                    for (int y = 0; y < 4096; ++y) {
                        final Color c = new Color(AssetLoader.pmaps[i].getPixel(x, y));
                        AssetLoader.atlasMask.mask[i][x][y] = (c.a > 0.0f);
                    }
                }
            }
        }
        try {
            final FileOutputStream f = new FileOutputStream(new File("assets/amask"));
            final OutputStream outputStream = new DeflaterOutputStream(f);
            final Output output = new Output(outputStream);
            game.kryo.writeObject(output, (Object)AssetLoader.atlasMask);
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
    
    public void dispose() {
        if (AssetLoader.texture != null) {
            AssetLoader.texture.dispose();
        }
        AssetLoader.texture = null;
        AssetLoader.button = null;
        AssetLoader.field = null;
        AssetLoader.font = null;
        AssetLoader.frame = null;
    }
    
    public static int getMtoFhelmX(final int a, final int d, final int f) {
        if (a == 3 && d == 0 && f == 1) {
            return -1;
        }
        if (a == 1) {
            if (d == 0) {
                if (f == 1 || f == 2) {
                    return -1;
                }
            }
            else if (d == 1) {
                if (f >= 1 && f <= 3) {
                    return 1;
                }
                if (f >= 4 && f <= 5) {
                    return 2;
                }
            }
            else if (d == 2) {
                if (f >= 3 && f <= 5) {
                    return -1;
                }
            }
            else if (d == 3) {
                if (f == 1 || f == 3) {
                    return -1;
                }
                if (f == 4 || f == 5) {
                    return -2;
                }
            }
        }
        else if (a == 2) {
            if (d == 0 && f == 0) {
                return 1;
            }
            if (d == 1) {
                return -1;
            }
            if (d == 2 && f == 0) {
                return 1;
            }
        }
        return 0;
    }
    
    public static int getMtoFhelmY(final int a, final int d, final int f) {
        if (a == 3 && d == 0 && f == 0) {
            return 1;
        }
        if (a == 0) {
            if (d == 0) {
                return 1;
            }
            if ((d == 1 || d == 3) && (f == 2 || f == 6)) {
                return 1;
            }
        }
        else if (a == 1) {
            if (d == 0 && f >= 1) {
                return -1;
            }
            if (d == 1 && f == 2) {
                return 1;
            }
            if (d == 2) {
                if (f == 1) {
                    return 2;
                }
                if (f == 2 || f == 3) {
                    return -1;
                }
            }
            else if (d == 3 && f == 2) {
                return 1;
            }
        }
        else if (a == 2) {
            if (d == 0 && f == 1) {
                return -1;
            }
            if (d == 1 && f == 3) {
                return 2;
            }
            if (d == 3 && f == 3) {
                return 2;
            }
        }
        return 0;
    }
    
    public static boolean doesHelmetDraw(final int[] a) {
        for (int c = 0; c < 2; ++c) {
            if ((a[c] >= 25 && a[c] <= 28) || a[c] == 12 || a[c] == 38) {
                return true;
            }
        }
        return false;
    }
    
    public static Color getRandomColor() {
        return new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1.0f);
    }
    
    public static float getStringWidth(final String s, final float scale, final float padding, final float spacing) {
        float total = 0.0f;
        char[] charArray;
        for (int length = (charArray = s.toCharArray()).length, i = 0; i < length; ++i) {
            final int ascii;
            final char c = (char)(ascii = charArray[i]);
            total += AssetLoader.fontWidth[ascii] * scale + padding * 2.0f + spacing;
        }
        return total;
    }
    
    public static int rndInt(final int m) {
        return (int)(Math.random() * m);
    }
    
    public static int[] randomColor() {
        final int[] c = new int[4];
        for (int i = 0; i < 3; ++i) {
            c[i] = (int)(Math.random() * 255.0);
        }
        c[3] = 255;
        return c;
    }
    
    public static class Mobj
    {
        public boolean[][] wall;
        
        public Mobj() {
            this.wall = new boolean[40][40];
        }
    }
}
