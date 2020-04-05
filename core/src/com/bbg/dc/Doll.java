// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Doll
{
    int dx;
    int dy;
    public int dstep;
    public int daction;
    public int dw;
    public int dh;
    public int ddir;
    public boolean flipX;
    public boolean flipY;
    public int[][] data;
    public int[][] col;
    public int[] steed;
    public int[][] steedCol;
    public int steedCycle;
    public boolean ignoreSpear;
    public int set;
    public int y1;
    public int y2;
    public int x1;
    public int x2;
    public boolean outline;
    public int i;
    public int monster;
    public boolean[] d;
    public int[] mcol;
    public int[][] dcol;
    public boolean beast;
    int renderX;
    int renderY;
    int renderW;
    int renderH;
    int sourceX;
    int sourceY;
    int dcycle;
    
    public Doll copy() {
        final Doll d = new Doll(this.beast);
        d.dx = this.dx;
        d.dy = this.dy;
        d.dstep = this.dstep;
        d.daction = this.daction;
        d.ddir = this.ddir;
        d.flipX = this.flipX;
        d.flipY = this.flipY;
        d.data = this.data;
        d.col = this.col;
        d.steed = this.steed;
        d.steedCol = this.steedCol;
        d.steedCycle = this.steedCycle;
        d.ignoreSpear = this.ignoreSpear;
        d.set = this.set;
        d.y1 = this.y1;
        d.y2 = this.y2;
        d.x1 = this.x1;
        d.x2 = this.x2;
        d.i = this.i;
        d.monster = this.monster;
        d.d = this.d;
        d.mcol = this.mcol;
        d.dcol = this.dcol;
        d.beast = this.beast;
        return d;
    }
    
    public Doll() {
        this.dx = 0;
        this.dy = 0;
        this.dstep = 0;
        this.daction = 0;
        this.dw = 0;
        this.dh = 0;
        this.ddir = 0;
        this.flipX = false;
        this.flipY = true;
        this.steedCycle = 0;
        this.ignoreSpear = false;
        this.set = 0;
        this.y1 = 0;
        this.y2 = 64;
        this.x1 = 0;
        this.x2 = 64;
        this.outline = false;
        this.i = 0;
        this.monster = 0;
        this.beast = false;
        this.renderX = 0;
        this.renderY = 0;
        this.renderW = 0;
        this.renderH = 0;
        this.sourceX = 0;
        this.sourceY = 0;
        this.dcycle = 0;
        this.data = new int[30][3];
        this.col = new int[30][4];
        this.steedCol = new int[30][4];
        this.steed = new int[30];
        for (int i = 0; i < 30; ++i) {
            this.data[i][0] = 0;
            this.data[i][1] = 0;
            this.data[i][2] = 0;
            this.steed[i] = 0;
            this.col[i][0] = 255;
            this.col[i][1] = 255;
            this.col[i][2] = 255;
            this.col[i][3] = 255;
            this.steedCol[i][0] = 255;
            this.steedCol[i][1] = 255;
            this.steedCol[i][2] = 255;
            this.steedCol[i][3] = 255;
        }
        this.d = new boolean[10];
        this.mcol = new int[4];
        this.dcol = new int[10][4];
        for (int i = 0; i < 10; ++i) {
            this.d[i] = false;
            this.dcol[i][0] = 255;
            this.dcol[i][1] = 255;
            this.dcol[i][2] = 255;
            this.dcol[i][3] = 255;
        }
        this.mcol[0] = 255;
        this.mcol[1] = 255;
        this.mcol[2] = 255;
        this.mcol[3] = 255;
        this.beast = false;
    }
    
    public Doll(final boolean isBeast) {
        this.dx = 0;
        this.dy = 0;
        this.dstep = 0;
        this.daction = 0;
        this.dw = 0;
        this.dh = 0;
        this.ddir = 0;
        this.flipX = false;
        this.flipY = true;
        this.steedCycle = 0;
        this.ignoreSpear = false;
        this.set = 0;
        this.y1 = 0;
        this.y2 = 64;
        this.x1 = 0;
        this.x2 = 64;
        this.outline = false;
        this.i = 0;
        this.monster = 0;
        this.beast = false;
        this.renderX = 0;
        this.renderY = 0;
        this.renderW = 0;
        this.renderH = 0;
        this.sourceX = 0;
        this.sourceY = 0;
        this.dcycle = 0;
        this.data = new int[30][3];
        this.col = new int[30][4];
        this.steedCol = new int[30][4];
        this.steed = new int[30];
        for (int i = 0; i < 30; ++i) {
            this.data[i][0] = 0;
            this.data[i][1] = 0;
            this.data[i][2] = 0;
            this.steed[i] = 0;
            this.col[i][0] = 255;
            this.col[i][1] = 255;
            this.col[i][2] = 255;
            this.col[i][3] = 255;
            this.steedCol[i][0] = 255;
            this.steedCol[i][1] = 255;
            this.steedCol[i][2] = 255;
            this.steedCol[i][3] = 255;
        }
        this.d = new boolean[10];
        this.mcol = new int[4];
        this.dcol = new int[10][4];
        for (int i = 0; i < 10; ++i) {
            this.d[i] = false;
            this.dcol[i][0] = 255;
            this.dcol[i][1] = 255;
            this.dcol[i][2] = 255;
            this.dcol[i][3] = 255;
        }
        this.mcol[0] = 255;
        this.mcol[1] = 255;
        this.mcol[2] = 255;
        this.mcol[3] = 255;
        this.beast = isBeast;
    }
    
    public void renderHumanoid(final DCGame game, int px, int py, int rideStep, final int playerStep, final int action, final int dir) {
        px -= 32;
        py -= 32;
        this.ignoreSpear = false;
        if (this.steed[0] == 0) {
            this.y1 = 0;
            this.y2 = 64;
            this.x1 = 0;
            this.x2 = 64;
            this.renderHumanoid(game, px, py, playerStep, action, dir);
            return;
        }
        Doll d = new Doll();
        if (this.steedCycle == 0) {
            rideStep = 0;
        }
        final int oldset = this.set;
        final int modX = getRideX(this.steed[0], rideStep, dir, this.steedCycle);
        int modY = getRideY(this.steed[0], rideStep, dir, this.steedCycle);
        if (dir == 0) {
            if (action == 4 || (oldset == 0 && action == 0)) {
                ++modY;
            }
            else if (oldset == 1 && action == 1) {
                --modY;
            }
        }
        int topModx = 0;
        if (this.steed[0] >= 6 && dir == 0) {
            py -= 14;
        }
        switch (dir) {
            case 0: {
                game.setColor(this.steedCol[0]);
                game.draw(AssetLoader.getRide(this.steedCycle, this.steed[0], 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                game.resetColor();
                if (this.steed[0] >= 6 && this.steed[3] > 0) {
                    game.setColor(this.steedCol[3]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, this.steed[3] + 9, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                }
                if (this.steed[0] >= 6 && this.steed[5] > 0 && dir == 0) {
                    game.setColor(this.steedCol[5]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 3, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.setColor(this.steedCol[6]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 3, 1), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                }
                if (this.steed[2] > 0 && this.steed[0] >= 6) {
                    game.setColor(this.steedCol[2]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 1, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                }
                if (this.steed[1] > 0 && this.steed[0] >= 6) {
                    game.setColor(this.steedCol[1]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 2, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                }
                if (action == 2) {
                    topModx = 1;
                }
                this.renderHumanoid(game, px + modX, py + modY + 48, 5, 4, this.set = 0, 0, 32, 48, 64);
                this.renderHumanoid(game, px + modX + 32, py + modY + 48, 5, 4, 0, 31, 64, 48, 64);
                this.set = oldset;
                this.renderHumanoid(game, px + modX + topModx, py + modY, playerStep, action, dir, 0, 64, 0, 52);
                break;
            }
            case 1: {
                this.renderHumanoid(game, px + modX, py + modY, playerStep, action, dir, 0, 64, 0, 50);
                this.ignoreSpear = true;
                this.renderHumanoid(game, px + modX, py + modY + 50, 2, 3, dir, this.set = 0, 64, 50, 64);
                this.set = oldset;
                game.setColor(this.steedCol[0]);
                game.draw(AssetLoader.getRide(this.steedCycle, this.steed[0], 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                game.resetColor();
                if (this.steed[0] >= 6 && this.steed[3] > 0) {
                    game.setColor(this.steedCol[3]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, this.steed[3] + 9, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                }
                if (this.steed[0] >= 6 && this.steed[5] > 0 && dir == 0) {
                    game.setColor(this.steedCol[5]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 3, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.setColor(this.steedCol[6]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 3, 1), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                }
                if (this.steed[2] > 0 && this.steed[0] >= 6) {
                    game.setColor(this.steedCol[2]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 1, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                }
                if (this.steed[1] > 0 && this.steed[0] >= 6) {
                    game.setColor(this.steedCol[1]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 2, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                    break;
                }
                break;
            }
            case 2: {
                if (action == 2) {
                    topModx = 1;
                }
                game.setColor(this.steedCol[0]);
                game.draw(AssetLoader.getRide(this.steedCycle, this.steed[0], 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                game.resetColor();
                if (this.steed[0] >= 6 && this.steed[3] > 0) {
                    game.setColor(this.steedCol[3]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, this.steed[3] + 9, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                }
                if (this.steed[0] >= 6 && this.steed[5] > 0 && dir == 0) {
                    game.setColor(this.steedCol[5]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 3, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.setColor(this.steedCol[6]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 3, 1), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                }
                if (this.steed[2] > 0 && this.steed[0] >= 6) {
                    game.setColor(this.steedCol[2]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 1, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                }
                if (this.steed[1] > 0 && this.steed[0] >= 6) {
                    game.setColor(this.steedCol[1]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 2, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                }
                this.ignoreSpear = true;
                this.renderHumanoid(game, px + modX, py + modY + 47, 5, 4, this.set = 0, 0, 64, 47, 64);
                this.set = oldset;
                this.ignoreSpear = false;
                this.renderHumanoid(game, px + modX + topModx, py + modY, playerStep, action, dir, 0, 64, 0, 50);
                break;
            }
            case 3: {
                this.renderHumanoid(game, px + modX, py + modY, playerStep, action, dir, 0, 64, 0, 50);
                this.ignoreSpear = true;
                this.renderHumanoid(game, px + modX, py + modY + 50, 2, 3, dir, this.set = 0, 64, 50, 64);
                this.set = oldset;
                game.setColor(this.steedCol[0]);
                game.draw(AssetLoader.getRide(this.steedCycle, this.steed[0], 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                game.resetColor();
                if (this.steed[0] >= 6 && this.steed[3] > 0) {
                    game.setColor(this.steedCol[3]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, this.steed[3] + 9, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                }
                if (this.steed[0] >= 6 && this.steed[5] > 0 && dir == 0) {
                    game.setColor(this.steedCol[5]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 3, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.setColor(this.steedCol[6]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 3, 1), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                }
                if (this.steed[2] > 0 && this.steed[0] >= 6) {
                    game.setColor(this.steedCol[2]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 1, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                }
                if (this.steed[1] > 0 && this.steed[0] >= 6) {
                    game.setColor(this.steedCol[1]);
                    game.draw(AssetLoader.getRideAcc(this.steedCycle, 2, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
                    game.resetColor();
                    break;
                }
                break;
            }
        }
        this.ignoreSpear = false;
        if (dir == 2) {
            d = new Doll();
            d.data[25][0] = this.data[25][0];
            d.col[25] = this.col[25];
            d.renderHumanoid(game, px + modX + topModx, py + modY, playerStep, action, dir, 0, 64, 0, 64);
        }
        game.setColor(this.steedCol[0]);
        game.draw(AssetLoader.getRide(this.steedCycle, this.steed[0], 1), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
        game.resetColor();
        if (this.steed[0] >= 6 && this.steed[3] > 0 && dir > 0) {
            game.setColor(this.steedCol[3]);
            game.draw(AssetLoader.getRideAcc(this.steedCycle, this.steed[3] + 9, 1), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
            game.resetColor();
        }
        if (this.steed[0] >= 6 && this.steed[5] > 0 && dir > 0) {
            game.setColor(this.steedCol[5]);
            game.draw(AssetLoader.getRideAcc(this.steedCycle, 3, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
            game.setColor(this.steedCol[6]);
            game.draw(AssetLoader.getRideAcc(this.steedCycle, 3, 1), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
            game.resetColor();
        }
        if (this.steed[0] >= 6 && this.steed[4] > 0 && dir > 0) {
            game.setColor(this.steedCol[4]);
            game.draw(AssetLoader.getRideAcc(this.steedCycle, 5 + this.steed[4], 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
            game.resetColor();
        }
        if (this.steed[0] >= 6 && this.steed[7] > 0) {
            game.setColor(this.steedCol[7]);
            game.draw(AssetLoader.getRideAcc(this.steedCycle, this.steed[7] + 3, 0), px - 32, py - 32, rideStep * 128, dir * 128, 128, 128);
            game.resetColor();
        }
        if (dir == 1) {
            d = new Doll();
            d.data[8][0] = this.data[8][0];
            d.col[8] = this.col[8];
            d.renderHumanoid(game, px + modX + 32, py + modY, 2, 3, dir, 32, 64, 0, 58);
            d.renderHumanoid(game, px + modX + 31, py + modY + 58, 2, 3, dir, 31, 64, 58, 59);
            d.renderHumanoid(game, px + modX + 30, py + modY + 59, 2, 3, dir, 30, 64, 59, 64);
        }
        else if (dir == 3) {
            d = new Doll();
            d.data[8][0] = this.data[8][0];
            d.col[8] = this.col[8];
            d.renderHumanoid(game, px + modX, py + modY, 2, 3, dir, 0, 32, 0, 57);
            d.renderHumanoid(game, px + modX, py + modY + 58, 2, 3, dir, 0, 33, 58, 58);
            d.renderHumanoid(game, px + modX, py + modY + 59, 2, 3, dir, 0, 34, 59, 64);
        }
        d = new Doll();
        d.set = oldset;
        if (dir == 0 || dir == 2) {
            d.data[24][0] = this.data[24][0];
            d.data[27][0] = this.data[27][0];
            d.col[24] = this.col[24];
            d.col[27] = this.col[27];
            d.data[22][0] = this.data[22][0];
            d.data[26][0] = this.data[26][0];
            d.col[22] = this.col[22];
            d.col[26] = this.col[26];
            d.renderHumanoid(game, px + modX + topModx, py + modY, playerStep, action, dir, 0, 64, 0, 64);
        }
        if (action != 2) {
            d = new Doll();
            d.set = oldset;
            d.data[11][0] = this.data[11][0];
            d.col[11] = this.col[11];
            d.renderHumanoid(game, px + modX, py + modY, playerStep, action, dir, 0, 64, 0, 24);
        }
        if (dir == 1 || dir == 3) {
            d = new Doll();
            d.set = oldset;
            d.data[25][0] = this.data[25][0];
            d.col[25] = this.col[25];
            d.renderHumanoid(game, px + modX, py + modY, playerStep, action, dir, 0, 64, 0, 64);
        }
    }
    
    public void renderHumanoid(final DCGame game, final int x, final int y, final int step, final int action, final int dir, int x1, int x2, int y1, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.renderHumanoid(game, x, y, step, action, dir);
        x1 = 0;
        x2 = 64;
        y1 = 0;
        y2 = 64;
    }
    
    public void renderHumanoid(final DCGame game, final int x, final int y, final int step, final int action, final int dir) {
        this.dx = x;
        this.dy = y;
        this.dstep = step;
        this.daction = action;
        this.ddir = dir;
        if (action == 1 && this.ddir != 2) {
            for (int c = 0; c < 2; ++c) {
                switch (this.data[26][c]) {
                    case 36:
                    case 37:
                    case 38: {
                        this.dstep = 5 - this.dstep;
                        break;
                    }
                }
            }
        }
        this.renderX = x;
        this.renderY = y;
        this.renderW = 64;
        this.renderH = 64;
        this.sourceX = this.dstep * 64;
        this.sourceY = this.ddir * 64;
        this.dw = 64;
        this.dh = 64;
        switch (dir) {
            case 0: {
                if (action == 1) {
                    this.renderHumanoidLayer(game, 22);
                    this.renderHumanoidLayer(game, 26);
                }
                else if (action == 2) {
                    this.renderHumanoidLayer(game, 23);
                }
                else if (action == 3) {
                    this.renderHumanoidLayer(game, 24);
                    this.renderHumanoidLayer(game, 27);
                }
                this.renderHumanoidLayer(game, 7);
                this.renderHumanoidLayer(game, 3);
                this.renderHumanoidLayer(game, 12);
                this.renderHumanoidLayer(game, 9);
                this.renderHumanoidLayer(game, 4);
                this.renderHumanoidLayer(game, 6);
                if (this.data[13][0] == 4 || this.data[13][0] == 4 || this.data[13][0] == 4) {
                    this.renderHumanoidLayer(game, 8);
                    this.renderHumanoidLayer(game, 16);
                    this.renderHumanoidLayer(game, 13);
                }
                else {
                    this.renderHumanoidLayer(game, 13);
                    this.renderHumanoidLayer(game, 8);
                    this.renderHumanoidLayer(game, 16);
                }
                this.renderHumanoidLayer(game, 17);
                this.renderHumanoidLayer(game, 18);
                this.renderHumanoidLayer(game, 2);
                this.renderHumanoidLayer(game, 19);
                this.renderHumanoidLayer(game, 21);
                this.renderHumanoidLayer(game, 0);
                this.renderHumanoidLayer(game, 10);
                this.renderHumanoidLayer(game, 11);
                this.renderHumanoidLayer(game, 14);
                this.renderHumanoidLayer(game, 25);
                this.renderHumanoidLayer(game, 20);
                this.renderHumanoidLayer(game, 1);
                break;
            }
            case 1: {
                this.renderHumanoidLayer(game, 1);
                this.renderHumanoidLayer(game, 20);
                this.renderHumanoidLayer(game, 16);
                this.renderHumanoidLayer(game, 3);
                this.renderHumanoidLayer(game, 5);
                this.renderHumanoidLayer(game, 15);
                this.renderHumanoidLayer(game, 7);
                this.renderHumanoidLayer(game, 12);
                this.renderHumanoidLayer(game, 9);
                this.renderHumanoidLayer(game, 4);
                this.renderHumanoidLayer(game, 6);
                if (this.data[13][0] == 4 || this.data[13][0] == 4 || this.data[13][0] == 4) {
                    this.renderHumanoidLayer(game, 8);
                    this.renderHumanoidLayer(game, 13);
                }
                else {
                    this.renderHumanoidLayer(game, 13);
                    this.renderHumanoidLayer(game, 8);
                }
                this.renderHumanoidLayer(game, 17);
                this.renderHumanoidLayer(game, 18);
                this.renderHumanoidLayer(game, 2);
                this.renderHumanoidLayer(game, 19);
                this.renderHumanoidLayer(game, 21);
                this.renderHumanoidLayer(game, 0);
                this.renderHumanoidLayer(game, 11);
                this.renderHumanoidLayer(game, 14);
                this.renderHumanoidLayer(game, 10);
                if (action == 3) {
                    this.renderHumanoidLayer(game, 24);
                    this.renderHumanoidLayer(game, 27);
                }
                this.renderHumanoidLayer(game, 25);
                if (action == 1) {
                    this.renderHumanoidLayer(game, 22);
                    this.renderHumanoidLayer(game, 26);
                }
                if (action == 2) {
                    this.renderHumanoidLayer(game, 23);
                    break;
                }
                break;
            }
            case 2: {
                if (action == 3) {
                    this.renderHumanoidSpear(game, 24);
                    this.renderHumanoidSpear(game, 27);
                }
                this.renderHumanoidLayer(game, 1);
                this.renderHumanoidLayer(game, 20);
                this.renderHumanoidLayer(game, 16);
                this.renderHumanoidLayer(game, 3);
                this.renderHumanoidLayer(game, 5);
                this.renderHumanoidLayer(game, 15);
                this.renderHumanoidLayer(game, 7);
                this.renderHumanoidLayer(game, 12);
                this.renderHumanoidLayer(game, 9);
                this.renderHumanoidLayer(game, 4);
                this.renderHumanoidLayer(game, 6);
                if (this.data[13][0] == 4 || this.data[13][0] == 4 || this.data[13][0] == 4) {
                    this.renderHumanoidLayer(game, 8);
                    this.renderHumanoidLayer(game, 13);
                }
                else {
                    this.renderHumanoidLayer(game, 13);
                    this.renderHumanoidLayer(game, 8);
                }
                this.renderHumanoidLayer(game, 17);
                this.renderHumanoidLayer(game, 18);
                this.renderHumanoidLayer(game, 19);
                this.renderHumanoidLayer(game, 2);
                this.renderHumanoidLayer(game, 21);
                this.renderHumanoidLayer(game, 0);
                this.renderHumanoidLayer(game, 14);
                this.renderHumanoidLayer(game, 10);
                this.renderHumanoidLayer(game, 19);
                if (action == 3) {
                    this.renderHumanoidSpear(game, 24);
                    this.renderHumanoidSpear(game, 27);
                }
                this.renderHumanoidLayer(game, 11);
                this.renderHumanoidLayer(game, 25);
                if (action == 1) {
                    this.renderHumanoidLayer(game, 22);
                    this.renderHumanoidLayer(game, 26);
                    break;
                }
                if (action == 2) {
                    this.renderHumanoidLayer(game, 23);
                    break;
                }
                break;
            }
            case 3: {
                if (action == 3) {
                    this.renderHumanoidSpear(game, 24);
                    this.renderHumanoidSpear(game, 27);
                }
                this.renderHumanoidLayer(game, 1);
                this.renderHumanoidLayer(game, 20);
                this.renderHumanoidLayer(game, 16);
                this.renderHumanoidLayer(game, 3);
                this.renderHumanoidLayer(game, 5);
                this.renderHumanoidLayer(game, 15);
                this.renderHumanoidLayer(game, 7);
                this.renderHumanoidLayer(game, 12);
                this.renderHumanoidLayer(game, 9);
                this.renderHumanoidLayer(game, 4);
                this.renderHumanoidLayer(game, 6);
                if (this.data[13][0] == 4 || this.data[13][0] == 4 || this.data[13][0] == 4) {
                    this.renderHumanoidLayer(game, 8);
                    this.renderHumanoidLayer(game, 13);
                }
                else {
                    this.renderHumanoidLayer(game, 13);
                    this.renderHumanoidLayer(game, 8);
                }
                this.renderHumanoidLayer(game, 17);
                this.renderHumanoidLayer(game, 18);
                this.renderHumanoidLayer(game, 19);
                this.renderHumanoidLayer(game, 2);
                this.renderHumanoidLayer(game, 21);
                this.renderHumanoidLayer(game, 0);
                this.renderHumanoidLayer(game, 11);
                this.renderHumanoidLayer(game, 14);
                this.renderHumanoidLayer(game, 10);
                this.renderHumanoidLayer(game, 19);
                if (action == 3) {
                    this.renderHumanoidSpear(game, 24);
                    this.renderHumanoidSpear(game, 27);
                }
                this.renderHumanoidLayer(game, 25);
                if (action == 1) {
                    this.renderHumanoidLayer(game, 22);
                    this.renderHumanoidLayer(game, 26);
                }
                if (action == 2) {
                    this.renderHumanoidLayer(game, 23);
                    break;
                }
                break;
            }
        }
    }
    
    public void renderHumanoidLayer(final DCGame game, final int i) {
        int sX = this.dstep * 64 + this.x1;
        int sY = this.ddir * 64 + this.y1;
        final int h = this.y2 - this.y1;
        final int w = this.x2 - this.x1;
        final int oldset = this.set;
        final int olddx = this.dx;
        final int olddy = this.dy;
        if (this.daction == 4 && this.ddir == 0) {
            --this.dy;
        }
        if (this.set == 1 && (i == 6 || i == 9 || i == 7 || i == 11 || i == 12 || i == 4 || i == 15 || i == 19)) {
            this.dx += AssetLoader.getMtoFhelmX(this.daction, this.ddir, this.dstep);
            this.dy += AssetLoader.getMtoFhelmY(this.daction, this.ddir, this.dstep);
            this.set = 0;
        }
        if (i == 6 || i == 9 || i == 7 || i == 11 || i == 12 || i == 4 || i == 15 || i == 19) {
            this.dx += AssetLoader.getCranialX(this.daction, this.ddir, this.dstep);
            this.dy += AssetLoader.getCranialY(this.daction, this.ddir, this.dstep);
        }
        if (this.set == 0 && i == 11 && this.daction == 1 && this.ddir == 0) {
            ++this.dy;
        }
        if (i >= 22 && this.ignoreSpear) {
            return;
        }
        for (int c = 0; c < 2; ++c) {
            if (this.data[i][c] > 0) {
                if (!this.outline) {
                    game.setColor(this.col[i]);
                }
                if (i < 22) {
                    if (i == 15 || i == 4 || i == 16) {
                        final TextureRegion tr = AssetLoader.getSpriteRegion(this.set, i, this.data[3][0] + 100 * (this.data[i][c] - 1), this.daction, this.ddir, this.dstep);
                        if (tr != null) {
                            game.drawRegion(tr, this.dx, this.dy, false, 0.0f, 1.0f);
                        }
                    }
                    else if (i == 9) {
                        if (this.data[11][c] > 0) {
                            if (!AssetLoader.doesHelmetDraw(this.data[11])) {
                                final TextureRegion tr = AssetLoader.getSpriteRegion(this.set, i, this.data[i][c], this.daction, this.ddir, this.dstep);
                                final TextureRegion mr = AssetLoader.getHMRegion(this.data[11][c], this.ddir);
                                if (tr != null && mr != null) {
                                    System.out.println(String.valueOf(mr.getRegionX()) + "," + mr.getRegionY());
                                    final float oldData = game.shaderData[0];
                                    game.shaderData[0] = 2.0f;
                                    game.aregion = new TextureRegion(mr.getTexture(), mr.getRegionX(), mr.getRegionY(), 64, 64);
                                    game.drawRegion(tr, this.dx, this.dy, false, 0.0f, 1.0f);
                                    game.shaderData[0] = oldData;
                                }
                            }
                            else {
                                final TextureRegion tr = AssetLoader.getSpriteRegion(this.set, i, this.data[i][c], this.daction, this.ddir, this.dstep);
                                if (tr != null) {
                                    game.drawRegion(tr, this.dx, this.dy, false, 0.0f, 1.0f);
                                }
                            }
                        }
                        else {
                            final TextureRegion tr = AssetLoader.getSpriteRegion(this.set, i, this.data[i][c], this.daction, this.ddir, this.dstep);
                            if (tr != null) {
                                game.drawRegion(tr, this.dx, this.dy, false, 0.0f, 1.0f);
                            }
                        }
                    }
                    else {
                        final TextureRegion tr = AssetLoader.getSpriteRegion(this.set, i, this.data[i][c], this.daction, this.ddir, this.dstep);
                        if (tr != null) {
                            game.drawRegion(tr, this.dx, this.dy, false, 0.0f, 1.0f);
                        }
                    }
                }
                else if (i == 25) {
                    final Texture tex = AssetLoader.getWeapon(this.set, i - 22, this.data[i][c]);
                    if (tex != null) {
                        if (this.ddir == 0) {
                            game.draw(tex, this.dx, this.dy, sX, sY, w, h, this.flipX, this.flipY);
                        }
                        else {
                            game.draw(tex, this.dx, this.dy, sX, sY, w, h, this.flipX, this.flipY);
                        }
                    }
                }
                else if (i == 23) {
                    Texture tex = AssetLoader.getWeapon(this.set, i - 22, this.data[i][c]);
                    if (tex != null) {
                        sY = this.ddir * 64;
                        game.draw(tex, this.dx, this.dy, sX, sY, w, h, this.flipX, this.flipY);
                    }
                    tex = AssetLoader.getWeapon(this.set, 1, 5);
                    if (tex != null) {
                        sY = this.ddir * 64;
                        game.draw(tex, this.dx, this.dy, sX, sY, w, h, this.flipX, this.flipY);
                    }
                }
                else if (i < 26) {
                    final Texture tex = AssetLoader.getWeapon(this.set, i - 22, this.data[i][c]);
                    if (tex != null) {
                        sY = this.ddir * 64;
                        game.draw(tex, this.dx, this.dy, sX, sY, w, h, this.flipX, this.flipY);
                    }
                }
                else {
                    final Texture tex = AssetLoader.getWeapon(this.set, i - 22, this.data[i][c]);
                    if (tex != null) {
                        sX = this.dstep * 192;
                        sY = this.ddir * 192;
                        game.draw(tex, this.dx - 64, this.dy - 64, sX, sY, 192, 192, this.flipX, this.flipY);
                    }
                }
                if (!this.outline) {
                    game.resetColor();
                }
            }
        }
        this.dx = olddx;
        this.dy = olddy;
        this.set = oldset;
    }
    
    public void renderHumanoidSpear(final DCGame game, final int i) {
        if (this.ignoreSpear) {
            return;
        }
        if (!this.outline) {
            game.setColor(this.col[i]);
        }
        int sX = this.dstep * 64;
        int sY = this.ddir * 64;
        for (int c = 0; c < 2; ++c) {
            if (i == 24) {
                if (this.data[i][c] > 0) {
                    final Texture tex = AssetLoader.getWeapon(this.set, i - 22, this.data[i][c]);
                    if (tex != null) {
                        sY = this.ddir * 64;
                        game.draw(tex, this.dx, this.dy, sX, sY, 64, 64, this.flipX, this.flipY);
                    }
                }
            }
            else if (i == 27 && this.data[i][c] > 0) {
                final Texture tex = AssetLoader.getWeapon(this.set, i - 22, this.data[i][c]);
                if (tex != null) {
                    sX = this.dstep * 192;
                    sY = this.ddir * 192;
                    game.draw(tex, this.dx - 64, this.dy - 64, sX, sY, 192, 192, this.flipX, this.flipY);
                }
            }
        }
        if (!this.outline) {
            game.resetColor();
        }
    }
    
    public void renderBeast(final DCGame game, int x, int y, int step, int cycle, int dir) {
        int w = 64;
        int h = 64;
        if (this.monster == 2 && cycle == 0) {
            h = 96;
            y -= 16;
        }
        if (this.monster == 20) {
            w = 128;
            h = 128;
        }
        if (this.monster == 9 && this.i == 19) {
            y += 16;
        }
        if (this.monster == 13) {
            w = 192;
            h = 192;
        }
        if (this.monster == 19) {
            w = 32;
            h = 32;
            if (this.i == 12) {
                w = 64;
                h = 64;
            }
        }
        else if (this.monster == 20) {
            w = 128;
            h = 128;
        }
        if (this.monster == 10) {
            y += 12;
        }
        if (this.monster == 19 && this.i != 12) {
            y += 24;
        }
        if (this.monster == 15) {
            y += 4;
        }
        if (this.monster >= 3 && this.monster <= 7) {
            if (cycle == 6) {
                if (dir == 1) {
                    y += 7;
                    x -= 8;
                }
                else if (dir == 2) {
                    y += 13;
                }
                else if (dir == 3) {
                    y += 7;
                    x += 8;
                }
            }
            else if (cycle == 0) {
                if (dir == 1) {
                    y += 7;
                    x -= 8;
                }
                else if (dir == 2) {
                    y += 13;
                }
                else if (dir == 3) {
                    y += 7;
                    x += 8;
                }
            }
        }
        if (this.monster < 8 && dir == 1) {
            x -= 8;
        }
        else if (this.monster < 8 & dir == 3) {
            x += 8;
        }
        if (this.monster == 14) {
            w = 96;
            h = 96;
        }
        if (this.monster == 11) {
            w = 16;
            h = 16;
        }
        if (this.monster == 16) {
            w = 64;
            h = 64;
        }
        if (this.monster == 17) {
            w = 32;
            h = 32;
        }
        if (this.monster == 18) {
            w = 144;
            h = 128;
        }
        x -= w / 2;
        y -= h / 2;
        if (this.monster == 11) {
            dir = 0;
        }
        if (this.monster == 16) {
            dir = 0;
        }
        if (this.monster == 17) {
            dir = 0;
        }
        if (this.monster == 10) {
            if (cycle == 6) {
                step += 4;
            }
            else if (cycle == 4) {
                dir = 4;
            }
            else if (cycle == 2) {
                return;
            }
            cycle = 6;
        }
        this.renderX = x;
        this.renderY = y;
        this.renderW = w;
        this.renderH = h;
        this.sourceX = step * w;
        this.sourceY = dir * h;
        this.dcycle = cycle;
        this.dstep = step;
        this.dw = w;
        this.dh = h;
        TextureRegion tr = AssetLoader.getMonsterRegion(this.monster, cycle, this.i, step * w, dir * h, w, h);
        if (!this.outline) {
            game.setColor(this.mcol);
        }
        game.drawRegion(tr, x, y, false, 0.0f, 1.0f);
        for (int a = 11; a < 10; ++a) {
            if (this.d[a]) {
                if (!this.outline) {
                    game.setColor(this.dcol[a]);
                }
                tr = AssetLoader.getMonsterRegion(this.monster, cycle + 1, a, step * w, dir * h, w, h);
                game.drawRegion(tr, x, y, false, 0.0f, 1.0f);
                if (!this.outline) {
                    game.setColor(Color.WHITE);
                }
            }
        }
        if (!this.outline) {
            game.setColor(Color.WHITE);
        }
    }
    
    public static int getRide2WalkY(final int steed, final int rideStep, final int rideDir) {
        Label_0324: {
            switch (rideStep) {
                case 0: {
                    switch (rideDir) {
                        case 0: {
                            return -13;
                        }
                        case 1: {
                            return -28;
                        }
                        case 2: {
                            return -24;
                        }
                        case 3: {
                            return -28;
                        }
                        default: {
                            break Label_0324;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (rideDir) {
                        case 0: {
                            return -13;
                        }
                        case 1: {
                            return -28;
                        }
                        case 2: {
                            return -24;
                        }
                        case 3: {
                            return -28;
                        }
                        default: {
                            break Label_0324;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (rideDir) {
                        case 0: {
                            return -13;
                        }
                        case 1: {
                            return -28;
                        }
                        case 2: {
                            return -24;
                        }
                        case 3: {
                            return -28;
                        }
                        default: {
                            break Label_0324;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rideDir) {
                        case 0: {
                            return -13;
                        }
                        case 1: {
                            return -28;
                        }
                        case 2: {
                            return -23;
                        }
                        case 3: {
                            return -28;
                        }
                        default: {
                            break Label_0324;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rideDir) {
                        case 0: {
                            return -13;
                        }
                        case 1: {
                            return -28;
                        }
                        case 2: {
                            return -23;
                        }
                        case 3: {
                            return -28;
                        }
                        default: {
                            break Label_0324;
                        }
                    }
                    break;
                }
                case 5: {
                    switch (rideDir) {
                        case 0: {
                            return -13;
                        }
                        case 1: {
                            return -28;
                        }
                        case 2: {
                            return -23;
                        }
                        case 3: {
                            return -28;
                        }
                        default: {
                            break Label_0324;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public static int getRide2WalkX(final int steed, final int rideStep, final int rideDir) {
        Label_0301: {
            switch (rideStep) {
                case 0: {
                    switch (rideDir) {
                        case 0: {
                            return 1;
                        }
                        case 1: {
                            return -2;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 2;
                        }
                        default: {
                            break Label_0301;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -2;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 2;
                        }
                        default: {
                            break Label_0301;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -2;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 2;
                        }
                        default: {
                            break Label_0301;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rideDir) {
                        case 0: {
                            return -1;
                        }
                        case 1: {
                            return -2;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 2;
                        }
                        default: {
                            break Label_0301;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rideDir) {
                        case 0: {
                            return -1;
                        }
                        case 1: {
                            return -2;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 2;
                        }
                        default: {
                            break Label_0301;
                        }
                    }
                    break;
                }
                case 5: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -2;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 2;
                        }
                        default: {
                            break Label_0301;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public static int getRide2GallopY(final int steed, final int rideStep, final int rideDir) {
        Label_0324: {
            switch (rideStep) {
                case 0: {
                    switch (rideDir) {
                        case 0: {
                            return -13;
                        }
                        case 1: {
                            return -29;
                        }
                        case 2: {
                            return -22;
                        }
                        case 3: {
                            return -29;
                        }
                        default: {
                            break Label_0324;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (rideDir) {
                        case 0: {
                            return -12;
                        }
                        case 1: {
                            return -28;
                        }
                        case 2: {
                            return -21;
                        }
                        case 3: {
                            return -28;
                        }
                        default: {
                            break Label_0324;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (rideDir) {
                        case 0: {
                            return -12;
                        }
                        case 1: {
                            return -27;
                        }
                        case 2: {
                            return -21;
                        }
                        case 3: {
                            return -27;
                        }
                        default: {
                            break Label_0324;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rideDir) {
                        case 0: {
                            return -13;
                        }
                        case 1: {
                            return -25;
                        }
                        case 2: {
                            return -20;
                        }
                        case 3: {
                            return -25;
                        }
                        default: {
                            break Label_0324;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rideDir) {
                        case 0: {
                            return -13;
                        }
                        case 1: {
                            return -25;
                        }
                        case 2: {
                            return -20;
                        }
                        case 3: {
                            return -25;
                        }
                        default: {
                            break Label_0324;
                        }
                    }
                    break;
                }
                case 5: {
                    switch (rideDir) {
                        case 0: {
                            return -14;
                        }
                        case 1: {
                            return -27;
                        }
                        case 2: {
                            return -22;
                        }
                        case 3: {
                            return -27;
                        }
                        default: {
                            break Label_0324;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public static int getRide2GallopX(final int steed, final int rideStep, final int rideDir) {
        Label_0301: {
            switch (rideStep) {
                case 0: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -2;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 2;
                        }
                        default: {
                            break Label_0301;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -2;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 2;
                        }
                        default: {
                            break Label_0301;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -2;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 2;
                        }
                        default: {
                            break Label_0301;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -5;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 5;
                        }
                        default: {
                            break Label_0301;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -5;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 5;
                        }
                        default: {
                            break Label_0301;
                        }
                    }
                    break;
                }
                case 5: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -2;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 2;
                        }
                        default: {
                            break Label_0301;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public static int getRide2StandY(final int steed, final int rideDir) {
        switch (rideDir) {
            case 0: {
                return -12;
            }
            case 1: {
                return -28;
            }
            case 2: {
                return -24;
            }
            case 3: {
                return -28;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static int getRide2StandX(final int steed, final int rideDir) {
        switch (rideDir) {
            case 0: {
                return 0;
            }
            case 1: {
                return -2;
            }
            case 2: {
                return 0;
            }
            case 3: {
                return 2;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static int getRideStandY(final int steed, final int rideDir) {
        return getRideEatY(steed, 0, rideDir);
    }
    
    public static int getRideStandX(final int steed, final int rideDir) {
        return getRideEatX(steed, 0, rideDir);
    }
    
    public static int getRideWalkY(final int steed, final int rideStep, final int rideDir) {
        Label_0220: {
            switch (rideStep) {
                case 0: {
                    switch (rideDir) {
                        case 0: {
                            return -23;
                        }
                        case 1: {
                            return -28;
                        }
                        case 2: {
                            return -21;
                        }
                        case 3: {
                            return -28;
                        }
                        default: {
                            break Label_0220;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (rideDir) {
                        case 0: {
                            return -23;
                        }
                        case 1: {
                            return -28;
                        }
                        case 2: {
                            return -21;
                        }
                        case 3: {
                            return -28;
                        }
                        default: {
                            break Label_0220;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (rideDir) {
                        case 0: {
                            return -23;
                        }
                        case 1: {
                            return -28;
                        }
                        case 2: {
                            return -20;
                        }
                        case 3: {
                            return -28;
                        }
                        default: {
                            break Label_0220;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rideDir) {
                        case 0: {
                            return -23;
                        }
                        case 1: {
                            return -28;
                        }
                        case 2: {
                            return -20;
                        }
                        case 3: {
                            return -28;
                        }
                        default: {
                            break Label_0220;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public static int getRideWalkX(final int steed, final int rideStep, final int rideDir) {
        Label_0205: {
            switch (rideStep) {
                case 0: {
                    switch (rideDir) {
                        case 0: {
                            return 1;
                        }
                        case 1: {
                            return -3;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 3;
                        }
                        default: {
                            break Label_0205;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -3;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 3;
                        }
                        default: {
                            break Label_0205;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (rideDir) {
                        case 0: {
                            return -1;
                        }
                        case 1: {
                            return -4;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 4;
                        }
                        default: {
                            break Label_0205;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -4;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 4;
                        }
                        default: {
                            break Label_0205;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public boolean doesMonsterStep(final int step) {
        switch (this.monster) {
            case 1:
            case 2: {
                return step == 2 || step == 5;
            }
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 13:
            case 20: {
                return step == 1 || step == 3;
            }
            case 9:
            case 11:
            case 12:
            case 14:
            case 15:
            case 17:
            case 19: {
                return step == 1;
            }
            case 10: {
                return step % 2 == 1;
            }
            case 18: {
                return step == 2;
            }
            case 16: {
                return step == 1;
            }
            default: {
                return false;
            }
        }
    }
    
    public int getBodyX() {
        return 0;
    }
    
    public int getBodyY() {
        if (this.monster == 11) {
            return -32;
        }
        if (this.monster == 13) {
            return 8;
        }
        return -8;
    }
    
    public int getLBodyX() {
        return 0;
    }
    
    public int getLBodyY() {
        if (this.monster == 10) {
            return -16;
        }
        if (this.monster == 11) {
            return -32;
        }
        return -8;
    }
    
    public static int getMonsterSound(final int monster, final int i) {
        Label_0189: {
            switch (monster) {
                case 8: {
                    return 28;
                }
                case 9: {
                    switch (i) {
                        case 0: {
                            return 29;
                        }
                        case 1: {
                            return 30;
                        }
                        case 2: {
                            return 31;
                        }
                        case 3: {
                            return 32;
                        }
                        case 5: {
                            return 33;
                        }
                        case 6:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14: {
                            return 28;
                        }
                        case 7:
                        case 15:
                        case 16:
                        case 17:
                        case 18: {
                            return -2;
                        }
                        default: {
                            break Label_0189;
                        }
                    }
                    break;
                }
                case 11:
                case 12:
                case 14:
                case 15:
                case 17: {
                    return 33;
                }
                case 16: {
                    return 35;
                }
                case 18: {
                    return 28;
                }
            }
        }
        return -1;
    }
    
    public static int getRideGallopY(final int steed, final int rideStep, final int rideDir) {
        Label_0220: {
            switch (rideStep) {
                case 0: {
                    switch (rideDir) {
                        case 0: {
                            return -22;
                        }
                        case 1: {
                            return -32;
                        }
                        case 2: {
                            return -21;
                        }
                        case 3: {
                            return -32;
                        }
                        default: {
                            break Label_0220;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (rideDir) {
                        case 0: {
                            return -23;
                        }
                        case 1: {
                            return -28;
                        }
                        case 2: {
                            return -22;
                        }
                        case 3: {
                            return -28;
                        }
                        default: {
                            break Label_0220;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (rideDir) {
                        case 0: {
                            return -26;
                        }
                        case 1: {
                            return -24;
                        }
                        case 2: {
                            return -25;
                        }
                        case 3: {
                            return -24;
                        }
                        default: {
                            break Label_0220;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rideDir) {
                        case 0: {
                            return -24;
                        }
                        case 1: {
                            return -26;
                        }
                        case 2: {
                            return -23;
                        }
                        case 3: {
                            return -26;
                        }
                        default: {
                            break Label_0220;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public static int getRideGallopX(final int steed, final int rideStep, final int rideDir) {
        Label_0205: {
            switch (rideStep) {
                case 0: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -2;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 2;
                        }
                        default: {
                            break Label_0205;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -2;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 2;
                        }
                        default: {
                            break Label_0205;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -7;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 7;
                        }
                        default: {
                            break Label_0205;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -5;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 5;
                        }
                        default: {
                            break Label_0205;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public static int getRideEatY(final int steed, final int rideStep, final int rideDir) {
        Label_0220: {
            switch (rideStep) {
                case 0: {
                    switch (rideDir) {
                        case 0: {
                            return -23;
                        }
                        case 1: {
                            return -28;
                        }
                        case 2: {
                            return -21;
                        }
                        case 3: {
                            return -28;
                        }
                        default: {
                            break Label_0220;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (rideDir) {
                        case 0: {
                            return -23;
                        }
                        case 1: {
                            return -28;
                        }
                        case 2: {
                            return -21;
                        }
                        case 3: {
                            return -28;
                        }
                        default: {
                            break Label_0220;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (rideDir) {
                        case 0: {
                            return -23;
                        }
                        case 1: {
                            return -27;
                        }
                        case 2: {
                            return -20;
                        }
                        case 3: {
                            return -27;
                        }
                        default: {
                            break Label_0220;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rideDir) {
                        case 0: {
                            return -23;
                        }
                        case 1: {
                            return -27;
                        }
                        case 2: {
                            return -20;
                        }
                        case 3: {
                            return -27;
                        }
                        default: {
                            break Label_0220;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public static int getRideEatX(final int steed, final int rideStep, final int rideDir) {
        Label_0205: {
            switch (rideStep) {
                case 0: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -3;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 3;
                        }
                        default: {
                            break Label_0205;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -3;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 3;
                        }
                        default: {
                            break Label_0205;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -4;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 4;
                        }
                        default: {
                            break Label_0205;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rideDir) {
                        case 0: {
                            return 0;
                        }
                        case 1: {
                            return -4;
                        }
                        case 2: {
                            return 0;
                        }
                        case 3: {
                            return 4;
                        }
                        default: {
                            break Label_0205;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public static int getRideY(final int steed, final int rideStep, final int rideDir, final int speed) {
        if (steed < 6) {
            if (speed == 0) {
                return getRideStandY(steed, rideDir);
            }
            if (speed == 1) {
                return getRideWalkY(steed, rideStep, rideDir);
            }
            if (speed == 2) {
                return getRideGallopY(steed, rideStep, rideDir);
            }
            if (speed == 3) {
                return getRideEatY(steed, rideStep, rideDir);
            }
        }
        else {
            if (speed == 0) {
                return getRide2StandY(steed, rideDir);
            }
            if (speed == 1) {
                return getRide2WalkY(steed, rideStep, rideDir);
            }
            if (speed == 2) {
                return getRide2GallopY(steed, rideStep, rideDir);
            }
        }
        return 0;
    }
    
    public static int getRideX(final int steed, final int rideStep, final int rideDir, final int speed) {
        if (steed < 6) {
            if (speed == 0) {
                return getRideStandX(steed, rideDir);
            }
            if (speed == 1) {
                return getRideWalkX(steed, rideStep, rideDir);
            }
            if (speed == 2) {
                return getRideGallopX(steed, rideStep, rideDir);
            }
            if (speed == 3) {
                return getRideEatX(steed, rideStep, rideDir);
            }
        }
        else {
            if (speed == 0) {
                return getRide2StandX(steed, rideDir);
            }
            if (speed == 1) {
                return getRide2WalkX(steed, rideStep, rideDir);
            }
            if (speed == 2) {
                return getRide2GallopX(steed, rideStep, rideDir);
            }
        }
        return 0;
    }
    
    public boolean doesMonsterStand() {
        switch (this.monster) {
            case 1:
            case 2:
            case 19: {
                if ((this.monster == 19 && this.i == 12) || this.monster != 19) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    public static int getMonsterBSteps(final int monster) {
        switch (monster) {
            case 1: {
                return 7;
            }
            default: {
                return 0;
            }
        }
    }
    
    public int getBottomY() {
        if (this.monster == 11) {
            return -16;
        }
        if (this.monster == 13) {
            return 24;
        }
        if (this.monster == 19) {}
        return 8;
    }
    
    public int getASteps() {
        if (this.beast) {
            return this.getMonsterASteps();
        }
        return 6;
    }
    
    public int getMonsterASteps() {
        Label_0188: {
            switch (this.monster) {
                case 1: {
                    return 5;
                }
                case 2: {
                    return 7;
                }
                case 3: {
                    return 4;
                }
                case 4: {
                    return 4;
                }
                case 5: {
                    return 4;
                }
                case 6: {
                    return 4;
                }
                case 7: {
                    return 4;
                }
                case 10: {
                    return 4;
                }
                case 13: {
                    return 5;
                }
                case 15: {
                    return 2;
                }
                case 19: {
                    switch (this.i) {
                        case 2:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11: {
                            return 4;
                        }
                        case 12: {
                            return 3;
                        }
                        default: {
                            break Label_0188;
                        }
                    }
                    break;
                }
                case 20: {
                    switch (this.i) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6: {
                            return 4;
                        }
                        default: {
                            break Label_0188;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public int getMonsterHSteps() {
        switch (this.monster) {
            case 1: {
                return 5;
            }
            case 2: {
                return 7;
            }
            case 3: {
                return 7;
            }
            case 4: {
                return 7;
            }
            case 5: {
                return 7;
            }
            case 6: {
                return 7;
            }
            case 7: {
                return 7;
            }
            case 10: {
                return 4;
            }
            case 13: {
                return 5;
            }
            case 16: {
                return 6;
            }
            default: {
                return 0;
            }
        }
    }
    
    public int getMonsterWSteps() {
        Label_0216: {
            switch (this.monster) {
                case 1: {
                    return 7;
                }
                case 2: {
                    return 7;
                }
                case 3: {
                    return 4;
                }
                case 4: {
                    return 4;
                }
                case 5: {
                    return 4;
                }
                case 6: {
                    return 4;
                }
                case 7: {
                    return 4;
                }
                case 8: {
                    return 4;
                }
                case 9: {
                    return 3;
                }
                case 10: {
                    return 6;
                }
                case 11: {
                    return 3;
                }
                case 12: {
                    return 3;
                }
                case 13: {
                    return 4;
                }
                case 14: {
                    return 3;
                }
                case 15: {
                    return 3;
                }
                case 16: {
                    return 8;
                }
                case 17: {
                    return 3;
                }
                case 18: {
                    return 3;
                }
                case 19: {
                    switch (this.i) {
                        case 0:
                        case 1:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10: {
                            return 3;
                        }
                        case 2:
                        case 12: {
                            return 4;
                        }
                        default: {
                            break Label_0216;
                        }
                    }
                    break;
                }
                case 20: {
                    switch (this.i) {
                        case 0: {
                            return 3;
                        }
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6: {
                            return 4;
                        }
                        default: {
                            break Label_0216;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public boolean flies() {
        if (this.beast) {
            if (this.monster == 8) {
                return true;
            }
            if (this.monster == 9) {
                if (this.i == 6 || this.i == 7 || (this.i >= 10 && this.i <= 18)) {
                    return true;
                }
            }
            else if (this.monster == 18) {
                return true;
            }
        }
        return false;
    }
}
