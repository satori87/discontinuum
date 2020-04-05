// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.scenes;

import java.util.Iterator;
import com.bbg.dc.AssetLoader;
import com.bbg.dc.DCGame;
import com.bbg.dc.iface.Button;
import com.bbg.dc.iface.Frame;
import com.badlogic.gdx.graphics.Color;
import com.bbg.dc.Doll;
import com.bbg.dc.iface.Label;
import com.bbg.dc.iface.Scene;

public class DollScene extends Scene
{
    Label[] lbl;
    Doll doll;
    public int fromScene;
    Color col;
    Frame frameCP;
    Button cpRdown;
    Button cpRup;
    Button cpGdown;
    Button cpGup;
    Button cpBdown;
    Button cpBup;
    Button cpAdown;
    Button cpAup;
    Label R;
    Label G;
    Label B;
    Label A;
    int curR;
    int curG;
    int curB;
    int curA;
    Button btnSave;
    Button btnDiscard;
    long walkStamp;
    public int walkStep;
    public int shootStep;
    public int slashStep;
    public int thrustStep;
    public int castStep;
    public int fishStep;
    public int fish2Step;
    public long clickAt;
    public int[] d;
    public int ride4Step;
    public int ride6Step;
    int rideStep;
    public int rdir;
    public int rx;
    public int ry;
    int playerStep;
    int pa;
    
    public DollScene(final DCGame game) {
        this.fromScene = 0;
        this.curR = 255;
        this.curG = 255;
        this.curB = 255;
        this.curA = 255;
        this.walkStamp = 0L;
        this.walkStep = 0;
        this.shootStep = 0;
        this.slashStep = 0;
        this.thrustStep = 0;
        this.castStep = 0;
        this.fishStep = 0;
        this.fish2Step = 0;
        this.clickAt = 0L;
        this.d = new int[5];
        this.ride4Step = 0;
        this.ride6Step = 0;
        this.rideStep = 0;
        this.rdir = 0;
        this.rx = 0;
        this.ry = 0;
        this.playerStep = 0;
        this.pa = 0;
        this.game = game;
        this.doll = new Doll();
    }
    
    @Override
    public void switchTo() {
        super.switchTo();
        switch (this.fromScene) {
            case 0: {
                this.doll = new Doll();
                this.btnSave.visible = false;
                break;
            }
            case 1: {
                this.btnSave.visible = true;
                this.doll = this.game.monsterScene.mon.doll.copy();
                break;
            }
        }
    }
    
    @Override
    public void start() {
        super.start();
        this.lbl = new Label[40];
        this.btnSave = new Button(this.game, 600, 1, 500, 736, 96, 32, "Save");
        this.btnDiscard = new Button(this.game, 601, 1, 395, 736, 96, 32, "Discard");
        this.buttons.add(this.btnSave);
        this.buttons.add(this.btnDiscard);
        this.frameCP = new Frame(this.game, 0, 0, 100, 100, false, false);
        this.frameCP.visible = false;
        this.frames.add(this.frameCP);
        this.cpRdown = new Button(this.game, 300, 1, 100, 560, 80, 32, "Down");
        this.cpRup = new Button(this.game, 400, 1, 300, 560, 80, 32, "Up");
        this.R = new Label(this.game, 200, 560, 1.0f, "HEY", Color.WHITE, true);
        this.cpGdown = new Button(this.game, 301, 1, 100, 600, 80, 32, "Down");
        this.cpGup = new Button(this.game, 401, 1, 300, 600, 80, 32, "Up");
        this.G = new Label(this.game, 200, 600, 1.0f, "HEY", Color.WHITE, true);
        this.cpBdown = new Button(this.game, 302, 1, 100, 640, 80, 32, "Down");
        this.cpBup = new Button(this.game, 402, 1, 300, 640, 80, 32, "Up");
        this.B = new Label(this.game, 200, 640, 1.0f, "HEY", Color.WHITE, true);
        this.cpAdown = new Button(this.game, 303, 1, 100, 680, 80, 32, "Down");
        this.cpAup = new Button(this.game, 403, 1, 300, 680, 80, 32, "Up");
        this.A = new Label(this.game, 200, 680, 1.0f, "HEY", Color.WHITE, true);
        this.buttons.add(this.cpRdown);
        this.buttons.add(this.cpRup);
        this.labels.add(this.R);
        this.buttons.add(this.cpGdown);
        this.buttons.add(this.cpGup);
        this.labels.add(this.G);
        this.buttons.add(this.cpBdown);
        this.buttons.add(this.cpBup);
        this.labels.add(this.B);
        this.buttons.add(this.cpAdown);
        this.buttons.add(this.cpAup);
        this.labels.add(this.A);
        for (int i = 0; i < 8; ++i) {
            final Button b = new Button(this.game, 500 + i, 1, 70 + i * 32 + 16, 719, 32, 32, "");
            b.visible = false;
            this.buttons.add(b);
        }
        for (int i = 0; i < 18; ++i) {
            this.buttons.add(new Button(this.game, 100 + i * 2, 1, 1000, i * 40 + 24, 80, 32, "Prev"));
            this.buttons.add(new Button(this.game, 100 + i * 2 + 1, 1, 1175, i * 40 + 24, 80, 32, "Next"));
            this.buttons.add(new Button(this.game, 200 + i, 1, 1265, i * 40 + 24, 80, 32, "Color"));
            this.lbl[i] = new Label(this.game, 1090, i * 40 + 24, 1.0f, "HEY", Color.WHITE, true);
            this.labels.add(this.lbl[i]);
        }
        for (int i = 0; i < 10; ++i) {
            this.buttons.add(new Button(this.game, 100 + i * 2 + 36, 1, 600, i * 40 + 24, 80, 32, "Prev"));
            this.buttons.add(new Button(this.game, 100 + i * 2 + 37, 1, 800, i * 40 + 24, 80, 32, "Next"));
            this.buttons.add(new Button(this.game, 200 + i + 18, 1, 900, i * 40 + 24, 80, 32, "Color"));
            this.lbl[i + 18] = new Label(this.game, 700, i * 40 + 24, 1.0f, "HEY", Color.WHITE, true);
            this.labels.add(this.lbl[i + 18]);
        }
        for (int i = 10; i < 11; ++i) {
            this.buttons.add(new Button(this.game, 100 + i * 2 + 36, 1, 600, i * 40 + 24, 80, 32, "Male"));
            this.buttons.add(new Button(this.game, 100 + i * 2 + 37, 1, 800, i * 40 + 24, 80, 32, "Female"));
            this.lbl[i + 18] = new Label(this.game, 700, i * 40 + 24, 1.0f, "HEY", Color.WHITE, true);
            this.labels.add(this.lbl[i + 18]);
        }
        for (int i = 11; i < 19; ++i) {
            this.buttons.add(new Button(this.game, 100 + i * 2 + 36, 1, 600, i * 40 + 24, 80, 32, "Prev"));
            this.buttons.add(new Button(this.game, 100 + i * 2 + 37, 1, 800, i * 40 + 24, 80, 32, "Next"));
            if (i != 12) {
                this.buttons.add(new Button(this.game, 200 + i + 18, 1, 900, i * 40 + 24, 80, 32, "Color"));
            }
            this.lbl[i + 18] = new Label(this.game, 700, i * 40 + 24, 1.0f, "HEY", Color.WHITE, true);
            this.labels.add(this.lbl[i + 18]);
        }
    }
    
    @Override
    public void buttonPressed(final int id) {
        if (id >= 100 && id <= 155) {
            final int d = (id - 100) / 2;
            if (id % 2 == 0) {
                final int[] array = this.doll.data[d];
                final int n = 0;
                --array[n];
                if (this.doll.data[d][0] < 0) {
                    if (d < 22) {
                        this.doll.data[d][0] = AssetLoader.numSS[this.doll.set][d];
                    }
                    else {
                        this.doll.data[d][0] = AssetLoader.numWS[this.doll.set][d - 22];
                    }
                }
            }
            else {
                final int[] array2 = this.doll.data[d];
                final int n2 = 0;
                ++array2[n2];
                if (d < 22) {
                    if (this.doll.data[d][0] > AssetLoader.numSS[this.doll.set][d]) {
                        this.doll.data[d][0] = 0;
                    }
                }
                else if (this.doll.data[d][0] > AssetLoader.numWS[this.doll.set][d - 22]) {
                    this.doll.data[d][0] = 0;
                }
            }
        }
        else if (id >= 200 && id <= 227) {
            final int d = id - 200;
            this.doll.col[d][0] = this.curR;
            this.doll.col[d][1] = this.curG;
            this.doll.col[d][2] = this.curB;
            this.doll.col[d][3] = this.curA;
        }
        else if (id == 229) {
            this.doll.steedCol[0][0] = this.curR;
            this.doll.steedCol[0][1] = this.curG;
            this.doll.steedCol[0][2] = this.curB;
            this.doll.steedCol[0][3] = this.curA;
        }
        else if (id != 230) {
            if (id == 231) {
                this.doll.steedCol[2][0] = this.curR;
                this.doll.steedCol[2][1] = this.curG;
                this.doll.steedCol[2][2] = this.curB;
                this.doll.steedCol[2][3] = this.curA;
            }
            else if (id == 232) {
                this.doll.steedCol[1][0] = this.curR;
                this.doll.steedCol[1][1] = this.curG;
                this.doll.steedCol[1][2] = this.curB;
                this.doll.steedCol[1][3] = this.curA;
            }
            else if (id == 233) {
                this.doll.steedCol[3][0] = this.curR;
                this.doll.steedCol[3][1] = this.curG;
                this.doll.steedCol[3][2] = this.curB;
                this.doll.steedCol[3][3] = this.curA;
            }
            else if (id == 234) {
                this.doll.steedCol[4][0] = this.curR;
                this.doll.steedCol[4][1] = this.curG;
                this.doll.steedCol[4][2] = this.curB;
                this.doll.steedCol[4][3] = this.curA;
            }
            else if (id == 235) {
                if (this.doll.steed[5] == 1) {
                    this.doll.steedCol[5][0] = this.curR;
                    this.doll.steedCol[5][1] = this.curG;
                    this.doll.steedCol[5][2] = this.curB;
                    this.doll.steedCol[5][3] = this.curA;
                }
                else {
                    this.doll.steedCol[6][0] = this.curR;
                    this.doll.steedCol[6][1] = this.curG;
                    this.doll.steedCol[6][2] = this.curB;
                    this.doll.steedCol[6][3] = this.curA;
                }
            }
            else if (id == 236) {
                this.doll.steedCol[7][0] = this.curR;
                this.doll.steedCol[7][1] = this.curG;
                this.doll.steedCol[7][2] = this.curB;
                this.doll.steedCol[7][3] = this.curA;
            }
        }
        if (id == 156) {
            this.doll.set = 0;
        }
        else if (id == 157) {
            this.doll.set = 1;
        }
        else if (id == 158) {
            final int[] steed = this.doll.steed;
            final int n3 = 0;
            --steed[n3];
            if (this.doll.steed[0] < 0) {
                this.doll.steed[0] = 10;
            }
        }
        else if (id == 159) {
            final int[] steed2 = this.doll.steed;
            final int n4 = 0;
            ++steed2[n4];
            if (this.doll.steed[0] > 10) {
                this.doll.steed[0] = 0;
            }
        }
        else if (id == 160) {
            final Doll doll = this.doll;
            --doll.steedCycle;
            if (this.doll.steedCycle < 0) {
                this.doll.steedCycle = 2;
            }
        }
        else if (id == 161) {
            final Doll doll2 = this.doll;
            ++doll2.steedCycle;
            if (this.doll.steedCycle > 2) {
                this.doll.steedCycle = 0;
            }
        }
        else if (id == 162) {
            final int[] steed3 = this.doll.steed;
            final int n5 = 2;
            --steed3[n5];
            if (this.doll.steed[2] < 0) {
                this.doll.steed[2] = 1;
            }
        }
        else if (id == 163) {
            final int[] steed4 = this.doll.steed;
            final int n6 = 2;
            ++steed4[n6];
            if (this.doll.steed[2] > 1) {
                this.doll.steed[2] = 0;
            }
        }
        else if (id == 164) {
            final int[] steed5 = this.doll.steed;
            final int n7 = 1;
            --steed5[n7];
            if (this.doll.steed[1] < 0) {
                this.doll.steed[1] = 1;
            }
        }
        else if (id == 165) {
            final int[] steed6 = this.doll.steed;
            final int n8 = 1;
            ++steed6[n8];
            if (this.doll.steed[1] > 1) {
                this.doll.steed[1] = 0;
            }
        }
        else if (id == 166) {
            final int[] steed7 = this.doll.steed;
            final int n9 = 3;
            --steed7[n9];
            if (this.doll.steed[3] < 0) {
                this.doll.steed[3] = 5;
            }
        }
        else if (id == 167) {
            final int[] steed8 = this.doll.steed;
            final int n10 = 3;
            ++steed8[n10];
            if (this.doll.steed[3] > 5) {
                this.doll.steed[3] = 0;
            }
        }
        else if (id == 168) {
            final int[] steed9 = this.doll.steed;
            final int n11 = 4;
            --steed9[n11];
            if (this.doll.steed[4] < 0) {
                this.doll.steed[4] = 1;
            }
        }
        else if (id == 169) {
            final int[] steed10 = this.doll.steed;
            final int n12 = 4;
            ++steed10[n12];
            if (this.doll.steed[4] > 4) {
                this.doll.steed[4] = 0;
            }
        }
        else if (id == 170) {
            final int[] steed11 = this.doll.steed;
            final int n13 = 5;
            --steed11[n13];
            if (this.doll.steed[5] < 0) {
                this.doll.steed[5] = 2;
            }
        }
        else if (id == 171) {
            final int[] steed12 = this.doll.steed;
            final int n14 = 5;
            ++steed12[n14];
            if (this.doll.steed[5] > 2) {
                this.doll.steed[5] = 0;
            }
        }
        else if (id == 172) {
            final int[] steed13 = this.doll.steed;
            final int n15 = 7;
            --steed13[n15];
            if (this.doll.steed[7] < 0) {
                this.doll.steed[7] = 2;
            }
        }
        else if (id == 173) {
            final int[] steed14 = this.doll.steed;
            final int n16 = 7;
            ++steed14[n16];
            if (this.doll.steed[7] > 2) {
                this.doll.steed[7] = 0;
            }
        }
        else if (id == 300) {
            --this.curR;
            if (this.curR < 0) {
                this.curR = 0;
            }
        }
        else if (id == 400) {
            ++this.curR;
            if (this.curR > 255) {
                this.curR = 255;
            }
        }
        else if (id == 301) {
            --this.curG;
            if (this.curG < 0) {
                this.curG = 0;
            }
        }
        else if (id == 401) {
            ++this.curG;
            if (this.curG > 255) {
                this.curG = 255;
            }
        }
        else if (id == 302) {
            --this.curB;
            if (this.curB < 0) {
                this.curB = 0;
            }
        }
        else if (id == 402) {
            ++this.curB;
            if (this.curB > 255) {
                this.curB = 255;
            }
        }
        else if (id == 303) {
            --this.curA;
            if (this.curA < 0) {
                this.curA = 0;
            }
        }
        else if (id == 403) {
            ++this.curA;
            if (this.curA > 255) {
                this.curA = 255;
            }
        }
        else if (id >= 500 && id < 600) {
            final Color c = AssetLoader.getColorBar(id - 500);
            this.curA = (int)(c.a * 255.0f);
            this.curR = (int)(c.r * 255.0f);
            this.curG = (int)(c.g * 255.0f);
            this.curB = (int)(c.b * 255.0f);
        }
        else if (id == 600) {
            switch (this.fromScene) {
                case 1: {
                    this.game.monsterScene.mon.doll = this.doll;
                    this.game.monsterScene.fromScene = 1;
                    this.game.changeScene(this.game.monsterScene);
                    break;
                }
            }
        }
        else if (id == 601) {
            switch (this.fromScene) {
                case 0: {
                    this.game.changeScene(this.game.mainMenu);
                    break;
                }
                case 1: {
                    this.game.monsterScene.fromScene = 1;
                    this.game.changeScene(this.game.monsterScene);
                    break;
                }
            }
        }
    }
    
    public void buttonReleased(final int id) {
    }
    
    public void updateLabels(final long tick) {
        for (int i = 0; i < 22; ++i) {
            this.lbl[i].text = String.valueOf(AssetLoader.getTypeString(i)) + ": " + this.doll.data[i][0];
        }
        for (int i = 22; i < 28; ++i) {
            this.lbl[i].text = String.valueOf(AssetLoader.getWeaponTypeString(i - 22)) + ": " + this.doll.data[i][0];
        }
        if (this.doll.set == 0) {
            this.lbl[28].text = "Male";
        }
        else {
            this.lbl[28].text = "Female";
        }
        this.lbl[29].text = "Steed: " + this.doll.steed[0];
        this.lbl[30].text = "SteedSp:" + this.doll.steedCycle;
        this.lbl[31].text = "Blanket:" + this.doll.steed[2];
        this.lbl[32].text = "Saddle:" + this.doll.steed[1];
        this.lbl[33].text = "Mane:" + this.doll.steed[3];
        this.lbl[34].text = "Face:" + this.doll.steed[4];
        this.lbl[35].text = "Horn:" + this.doll.steed[5];
        this.lbl[36].text = "Socks:" + this.doll.steed[7];
        this.R.text = "Red: " + this.curR;
        this.G.text = "Green: " + this.curG;
        this.B.text = "Blue: " + this.curB;
        this.A.text = "Alpha: " + this.curA;
    }
    
    @Override
    public void update(final long tick) {
        this.updateLabels(tick);
        super.update(tick);
        if (this.game.tick > this.clickAt) {
            this.clickAt = this.game.tick + 50L;
            for (final Button b : this.buttons) {
                if (b.sel && this.game.tick - b.selDown > 300L) {
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                    this.buttonPressed(b.id);
                }
            }
        }
        if (this.game.tick > this.walkStamp) {
            this.walkStamp = this.game.tick + 140L;
            ++this.walkStep;
            ++this.shootStep;
            ++this.slashStep;
            ++this.thrustStep;
            ++this.castStep;
            ++this.fishStep;
            ++this.ride4Step;
            ++this.ride6Step;
            if (this.ride4Step > 3) {
                this.ride4Step = 0;
            }
            if (this.ride6Step > 5) {
                this.ride6Step = 0;
            }
            if (this.walkStep > 8) {
                this.walkStep = 1;
                final int[] d = this.d;
                final int n = 0;
                ++d[n];
            }
            if (this.thrustStep > 7) {
                this.thrustStep = 0;
                final int[] d2 = this.d;
                final int n2 = 3;
                ++d2[n2];
            }
            if (this.castStep > 6) {
                this.castStep = 0;
                final int[] d3 = this.d;
                final int n3 = 4;
                ++d3[n3];
            }
            if (this.slashStep > 5) {
                this.slashStep = 0;
                final int[] d4 = this.d;
                final int n4 = 1;
                ++d4[n4];
            }
            if (this.shootStep > 12) {
                this.shootStep = 4;
                final int[] d5 = this.d;
                final int n5 = 2;
                ++d5[n5];
            }
            if (this.fishStep > 39) {
                ++this.fish2Step;
                if (this.fish2Step > 10) {
                    this.fishStep = 0;
                    this.fish2Step = 0;
                }
                else {
                    this.fishStep = 20;
                }
            }
        }
        for (int dd = 0; dd < 5; ++dd) {
            if (this.d[dd] > 3) {
                this.d[dd] = 0;
            }
        }
        if (this.doll.steed[0] < 6) {
            this.rideStep = this.ride4Step;
        }
        else {
            this.rideStep = this.ride6Step;
        }
    }
    
    @Override
    public void render() {
        this.doll.renderHumanoid(this.game, 100, 50, this.rideStep, 0, 0, 0);
        this.doll.renderHumanoid(this.game, 200, 50, this.rideStep, 0, 0, 1);
        this.doll.renderHumanoid(this.game, 300, 50, this.rideStep, 0, 0, 2);
        this.doll.renderHumanoid(this.game, 400, 50, this.rideStep, 0, 0, 3);
        this.changeStep(1);
        this.doll.renderHumanoid(this.game, 100, 150, this.rideStep, this.playerStep, this.pa, 0);
        this.doll.renderHumanoid(this.game, 200, 150, this.rideStep, this.playerStep, this.pa, 1);
        this.doll.renderHumanoid(this.game, 300, 150, this.rideStep, this.playerStep, this.pa, 2);
        this.doll.renderHumanoid(this.game, 400, 150, this.rideStep, this.playerStep, this.pa, 3);
        this.changeStep(2);
        this.doll.renderHumanoid(this.game, 100, 250, this.rideStep, this.playerStep, this.pa, 0);
        this.doll.renderHumanoid(this.game, 200, 250, this.rideStep, this.playerStep, this.pa, 1);
        this.doll.renderHumanoid(this.game, 300, 250, this.rideStep, this.playerStep, this.pa, 2);
        this.doll.renderHumanoid(this.game, 400, 250, this.rideStep, this.playerStep, this.pa, 3);
        this.changeStep(3);
        this.doll.renderHumanoid(this.game, 100, 350, this.rideStep, this.playerStep, this.pa, 0);
        this.doll.renderHumanoid(this.game, 200, 350, this.rideStep, this.playerStep, this.pa, 1);
        this.doll.renderHumanoid(this.game, 300, 350, this.rideStep, this.playerStep, this.pa, 2);
        this.doll.renderHumanoid(this.game, 400, 350, this.rideStep, this.playerStep, this.pa, 3);
        this.changeStep(4);
        this.doll.renderHumanoid(this.game, 100, 450, this.rideStep, this.playerStep, this.pa, 0);
        this.doll.renderHumanoid(this.game, 200, 450, this.rideStep, this.playerStep, this.pa, 1);
        this.doll.renderHumanoid(this.game, 300, 450, this.rideStep, this.playerStep, this.pa, 2);
        this.doll.renderHumanoid(this.game, 400, 450, this.rideStep, this.playerStep, this.pa, 3);
        this.col = new Color(this.curR / 255.0f, this.curG / 255.0f, this.curB / 255.0f, this.curA / 255.0f);
        this.game.batcher.setColor(this.col);
        this.game.drawRegion(AssetLoader.whitebox, 380, 600, false, 0.0f, 1.0f);
        this.game.batcher.setColor(Color.WHITE);
        for (int i = 0; i < 8; ++i) {
            this.col = AssetLoader.getColorBar(i);
            this.game.batcher.setColor(this.col);
            this.game.drawRegion(AssetLoader.whitebox, 70 + i * 32, 703, false, 0.0f, 0.5f);
            this.game.batcher.setColor(Color.WHITE);
        }
        super.render();
    }
    
    public void changeStep(final int a) {
        switch (this.pa = a) {
            case 0: {
                this.playerStep = this.walkStep;
                break;
            }
            case 1: {
                this.playerStep = this.slashStep;
                break;
            }
            case 2: {
                this.playerStep = this.shootStep;
                break;
            }
            case 3: {
                this.playerStep = this.thrustStep;
                break;
            }
            case 4: {
                this.playerStep = this.castStep;
                break;
            }
        }
    }
}
