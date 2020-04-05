// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.scenes;

import java.util.Iterator;
import com.bbg.dc.AssetLoader;
import com.badlogic.gdx.graphics.Color;
import com.bbg.dc.DCGame;
import com.bbg.dc.iface.Button;
import com.bbg.dc.Doll;
import com.bbg.dc.iface.Label;
import com.bbg.dc.iface.Scene;

public class CreatureScene extends Scene
{
    Label[] lbl;
    Doll doll;
    public int fromScene;
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
    long clickAt;
    Button btnSave;
    Button btnDiscard;
    long tickAt;
    int walkStep;
    int aStep;
    int bStep;
    int hStep;
    
    public CreatureScene(final DCGame game) {
        this.fromScene = 0;
        this.curR = 255;
        this.curG = 255;
        this.curB = 255;
        this.curA = 255;
        this.clickAt = 0L;
        this.tickAt = 0L;
        this.walkStep = 0;
        this.aStep = 0;
        this.bStep = 0;
        this.hStep = 0;
        this.game = game;
        this.doll = new Doll(true);
    }
    
    @Override
    public void switchTo() {
        super.switchTo();
        switch (this.fromScene) {
            case 0: {
                this.btnSave.visible = false;
                this.doll = new Doll();
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
        this.cpRdown = new Button(this.game, 300, 1, 600, 560, 80, 32, "Down");
        this.cpRup = new Button(this.game, 400, 1, 800, 560, 80, 32, "Up");
        this.R = new Label(this.game, 700, 560, 1.0f, "HEY", Color.WHITE, true);
        this.cpGdown = new Button(this.game, 301, 1, 600, 600, 80, 32, "Down");
        this.cpGup = new Button(this.game, 401, 1, 800, 600, 80, 32, "Up");
        this.G = new Label(this.game, 700, 600, 1.0f, "HEY", Color.WHITE, true);
        this.cpBdown = new Button(this.game, 302, 1, 600, 640, 80, 32, "Down");
        this.cpBup = new Button(this.game, 402, 1, 800, 640, 80, 32, "Up");
        this.B = new Label(this.game, 700, 640, 1.0f, "HEY", Color.WHITE, true);
        this.cpAdown = new Button(this.game, 303, 1, 600, 680, 80, 32, "Down");
        this.cpAup = new Button(this.game, 403, 1, 800, 680, 80, 32, "Up");
        this.A = new Label(this.game, 700, 680, 1.0f, "HEY", Color.WHITE, true);
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
            final Button b = new Button(this.game, 500 + i, 1, 570 + i * 32 + 16, 719, 32, 32, "");
            b.visible = false;
            this.buttons.add(b);
        }
        for (int i = 0; i < 2; ++i) {
            this.buttons.add(new Button(this.game, 100 + i * 2, 1, 1000, i * 40 + 24, 80, 32, "Prev"));
            this.buttons.add(new Button(this.game, 101 + i * 2, 1, 1175, i * 40 + 24, 80, 32, "Next"));
            this.buttons.add(new Button(this.game, 200 + i, 1, 1265, i * 40 + 24, 80, 32, "Color"));
            this.lbl[i] = new Label(this.game, 1090, i * 40 + 24, 1.0f, "HEY", Color.WHITE, true);
            this.labels.add(this.lbl[i]);
        }
        for (int i = 2; i < 12; ++i) {
            this.buttons.add(new Button(this.game, 102 + i, 1, 1000, i * 40 + 24, 80, 32, "Toggle"));
            this.buttons.add(new Button(this.game, 200 + i, 1, 1265, i * 40 + 24, 80, 32, "Color"));
            this.lbl[i] = new Label(this.game, 1090, i * 40 + 24, 1.0f, "HEY", Color.WHITE, true);
            this.labels.add(this.lbl[i]);
        }
    }
    
    @Override
    public void buttonPressed(final int id) {
        if (id == 100) {
            final Doll doll = this.doll;
            --doll.monster;
            if (this.doll.monster < 0) {
                this.doll.monster = 21;
            }
            this.walkStep = 0;
            this.hStep = 0;
            this.aStep = 0;
            this.bStep = 0;
        }
        else if (id == 101) {
            final Doll doll2 = this.doll;
            ++doll2.monster;
            if (this.doll.monster > 20) {
                this.doll.monster = 0;
                this.doll.i = 0;
            }
            this.walkStep = 0;
            this.hStep = 0;
            this.aStep = 0;
            this.bStep = 0;
        }
        else if (id == 102) {
            final Doll doll3 = this.doll;
            --doll3.i;
            if (this.doll.i < 0) {
                this.doll.i = 0;
            }
        }
        else if (id == 103) {
            final Doll doll4 = this.doll;
            ++doll4.i;
            if (AssetLoader.getMonsterSheetRegion(this.doll.monster, 6, this.doll.i) == null) {
                this.doll.i = 0;
            }
        }
        else if (id >= 104 && id <= 115) {
            this.doll.d[id - 104] = !this.doll.d[id - 104];
        }
        else if (id == 200) {
            this.doll.mcol[0] = this.curR;
            this.doll.mcol[1] = this.curG;
            this.doll.mcol[2] = this.curB;
            this.doll.mcol[3] = this.curA;
        }
        else if (id >= 202 && id <= 213) {
            final int d = id - 202;
            this.doll.dcol[d][0] = this.curR;
            this.doll.dcol[d][1] = this.curG;
            this.doll.dcol[d][2] = this.curB;
            this.doll.dcol[d][3] = this.curA;
        }
        if (id == 300) {
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
                    this.game.monsterScene.fromScene = 2;
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
                    this.game.monsterScene.fromScene = 2;
                    this.game.changeScene(this.game.monsterScene);
                    break;
                }
            }
        }
    }
    
    public void buttonReleased(final int id) {
    }
    
    public void updateLabels(final long tick) {
        this.lbl[0].text = "Monster:" + this.doll.monster;
        this.lbl[1].text = "Body:" + this.doll.i;
        for (int i = 0; i < 10; ++i) {
            String st = "Off";
            if (this.doll.d[i]) {
                st = "On";
            }
            this.lbl[i + 2].text = "d" + i + ": " + st;
        }
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
        if (this.game.tick > this.tickAt) {
            this.tickAt = this.game.tick + 200L;
            ++this.walkStep;
            if (this.walkStep >= this.doll.getMonsterWSteps()) {
                this.walkStep = 0;
                if (this.doll.doesMonsterStand()) {
                    this.walkStep = 1;
                }
            }
            ++this.aStep;
            if (this.aStep >= this.doll.getMonsterASteps()) {
                this.aStep = 0;
                this.doll.doesMonsterStand();
            }
            ++this.bStep;
            if (this.bStep >= Doll.getMonsterBSteps(this.doll.monster)) {
                this.bStep = 0;
                this.doll.doesMonsterStand();
            }
            ++this.hStep;
            if (this.hStep >= this.doll.getMonsterHSteps()) {
                this.hStep = 0;
                this.doll.doesMonsterStand();
            }
        }
    }
    
    @Override
    public void render() {
        this.doll.renderBeast(this.game, 100, 100, this.walkStep, 6, 0);
        this.doll.renderBeast(this.game, 100, 300, this.walkStep, 6, 1);
        this.doll.renderBeast(this.game, 100, 500, this.walkStep, 6, 2);
        this.doll.renderBeast(this.game, 100, 700, this.walkStep, 6, 3);
        this.doll.renderBeast(this.game, 300, 100, this.aStep, 0, 0);
        this.doll.renderBeast(this.game, 300, 300, this.aStep, 0, 1);
        this.doll.renderBeast(this.game, 300, 500, this.aStep, 0, 2);
        this.doll.renderBeast(this.game, 300, 700, this.aStep, 0, 3);
        this.doll.renderBeast(this.game, 500, 100, this.bStep, 2, 0);
        this.doll.renderBeast(this.game, 500, 300, this.bStep, 2, 1);
        this.doll.renderBeast(this.game, 500, 500, this.bStep, 2, 2);
        this.doll.renderBeast(this.game, 500, 700, this.bStep, 2, 3);
        this.doll.renderBeast(this.game, 700, 100, this.hStep, 4, 0);
        Color col = new Color(this.curR / 255.0f, this.curG / 255.0f, this.curB / 255.0f, this.curA / 255.0f);
        this.game.batcher.setColor(col);
        this.game.drawRegion(AssetLoader.whitebox, 880, 600, false, 0.0f, 1.0f);
        this.game.batcher.setColor(Color.WHITE);
        for (int i = 0; i < 8; ++i) {
            col = AssetLoader.getColorBar(i);
            this.game.batcher.setColor(col);
            this.game.drawRegion(AssetLoader.whitebox, 570 + i * 32, 703, false, 0.0f, 0.5f);
            this.game.batcher.setColor(Color.WHITE);
        }
        super.render();
    }
}
