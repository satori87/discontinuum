// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.scenes;

import java.io.IOException;
import java.io.InputStream;
import com.esotericsoftware.kryo.io.Input;
import java.io.FileInputStream;
import java.io.File;
import java.util.Iterator;
import com.badlogic.gdx.graphics.Color;
import com.bbg.dc.DCGame;
import com.bbg.dc.iface.TextBox;
import com.bbg.dc.iface.Label;
import com.bbg.dc.MonsterData;
import com.bbg.dc.iface.Button;
import com.bbg.dc.iface.Scene;

public class MonsterScene extends Scene
{
    Button btnSave;
    Button btnDiscard;
    public int fromScene;
    public MonsterData[] monster;
    public int curMon;
    Label[] lbl;
    Label lblEdit;
    TextBox name;
    Button btnHumanoid;
    Button btnCreature;
    Button btnFriendly;
    Button btnNoCombat;
    Button btnAids;
    MonsterData mon;
    
    public MonsterScene(final DCGame game) {
        this.fromScene = 0;
        this.curMon = 0;
        this.mon = new MonsterData(0);
        this.game = game;
        this.monster = new MonsterData[255];
        for (int i = 0; i < 255; ++i) {
            this.monster[i] = new MonsterData(i);
        }
    }
    
    @Override
    public void switchTo() {
        super.switchTo();
        if (this.fromScene == 0) {
            this.mon = this.monster[this.curMon].copy();
            this.name.text = this.mon.name;
            if (this.mon.doll.beast) {
                this.btnHumanoid.toggle = false;
                this.btnCreature.toggle = true;
            }
            else {
                this.btnHumanoid.toggle = true;
                this.btnCreature.toggle = false;
            }
            this.btnFriendly.toggle = this.mon.flags[0];
            this.btnNoCombat.toggle = this.mon.flags[1];
            this.btnAids.toggle = this.mon.flags[2];
        }
    }
    
    @Override
    public void start() {
        super.start();
        this.lblEdit = new Label(this.game, 140, 85, 2.0f, "Editing Monster: ", Color.WHITE, false);
        this.labels.add(this.lblEdit);
        this.lbl = new Label[40];
        this.labels.add(new Label(this.game, 140, 185, 2.0f, "Name:", Color.WHITE, true));
        this.name = new TextBox(this.game, 0, 20, true, 512, 140, 600);
        this.textboxes.add(this.name);
        this.labels.add(new Label(this.game, 140, 245, 2.0f, "Type:", Color.WHITE, true));
        this.btnHumanoid = new Button(this.game, 200, 0, 370, 245, 160, 32, "Humanoid");
        this.buttons.add(this.btnHumanoid);
        this.btnCreature = new Button(this.game, 201, 0, 570, 245, 160, 32, "Creature");
        this.buttons.add(this.btnCreature);
        this.btnFriendly = new Button(this.game, 300, 2, 370, 300, 160, 32, "Friendly");
        this.buttons.add(this.btnFriendly);
        this.btnNoCombat = new Button(this.game, 301, 2, 570, 300, 160, 32, "CantFight");
        this.buttons.add(this.btnNoCombat);
        this.btnAids = new Button(this.game, 302, 2, 370, 340, 160, 32, "Aid Ally");
        this.buttons.add(this.btnAids);
        for (int i = 0; i < 15; ++i) {
            this.buttons.add(new Button(this.game, 100 + i * 2, 1, 900, i * 40 + 24, 80, 32, "Decr"));
            this.buttons.add(new Button(this.game, 101 + i * 2, 1, 1200, i * 40 + 24, 80, 32, "Incr"));
            this.lbl[i] = new Label(this.game, 1050, i * 40 + 24, 1.0f, "HEY", Color.WHITE, true);
            this.labels.add(this.lbl[i]);
        }
        this.btnSave = new Button(this.game, 600, 1, 500, 736, 96, 32, "Save");
        this.btnDiscard = new Button(this.game, 601, 1, 395, 736, 96, 32, "Discard");
        this.buttons.add(this.btnSave);
        this.buttons.add(this.btnDiscard);
    }
    
    @Override
    public void buttonPressed(final int id) {
        if (id == 100) {
            final MonsterData mon = this.mon;
            --mon.maxHP;
            if (this.mon.maxHP < 0) {
                this.mon.maxHP = 0;
            }
        }
        else if (id == 101) {
            final MonsterData mon2 = this.mon;
            ++mon2.maxHP;
            if (this.mon.maxHP > 20000) {
                this.mon.maxHP = 20000;
            }
        }
        else if (id == 102) {
            final MonsterData mon3 = this.mon;
            --mon3.walkSpeed;
            if (this.mon.walkSpeed < 0) {
                this.mon.walkSpeed = 0;
            }
        }
        else if (id == 103) {
            final MonsterData mon4 = this.mon;
            ++mon4.walkSpeed;
            if (this.mon.walkSpeed > 100) {
                this.mon.walkSpeed = 100;
            }
        }
        else if (id == 104) {
            final MonsterData mon5 = this.mon;
            --mon5.attackCoolDown;
            if (this.mon.attackCoolDown < 0) {
                this.mon.attackCoolDown = 0;
            }
        }
        else if (id == 105) {
            final MonsterData mon6 = this.mon;
            ++mon6.attackCoolDown;
            if (this.mon.attackCoolDown > 5000) {
                this.mon.attackCoolDown = 5000;
            }
        }
        else if (id == 106) {
            final MonsterData mon7 = this.mon;
            --mon7.attackSpeed;
            if (this.mon.attackSpeed < 0) {
                this.mon.attackSpeed = 0;
            }
        }
        else if (id == 107) {
            final MonsterData mon8 = this.mon;
            ++mon8.attackSpeed;
            if (this.mon.attackSpeed > 100) {
                this.mon.attackSpeed = 100;
            }
        }
        else if (id == 108) {
            final MonsterData mon9 = this.mon;
            --mon9.lightAlpha;
            if (this.mon.lightAlpha < 0) {
                this.mon.lightAlpha = 0;
            }
        }
        else if (id == 109) {
            final MonsterData mon10 = this.mon;
            ++mon10.lightAlpha;
            if (this.mon.lightAlpha > 255) {
                this.mon.lightAlpha = 255;
            }
        }
        else if (id == 110) {
            final MonsterData mon11 = this.mon;
            --mon11.lightDist;
            if (this.mon.lightDist < 0) {
                this.mon.lightDist = 0;
            }
        }
        else if (id == 111) {
            final MonsterData mon12 = this.mon;
            ++mon12.lightDist;
            if (this.mon.lightDist > 100) {
                this.mon.lightDist = 100;
            }
        }
        else if (id == 112) {
            final MonsterData mon13 = this.mon;
            --mon13.pursueMaxRange;
            if (this.mon.pursueMaxRange < 0) {
                this.mon.pursueMaxRange = 0;
            }
        }
        else if (id == 113) {
            final MonsterData mon14 = this.mon;
            ++mon14.pursueMaxRange;
            if (this.mon.pursueMaxRange > 100) {
                this.mon.pursueMaxRange = 100;
            }
        }
        else if (id == 114) {
            final MonsterData mon15 = this.mon;
            --mon15.pursueMinRange;
            if (this.mon.pursueMinRange < 0) {
                this.mon.pursueMinRange = 0;
            }
        }
        else if (id == 115) {
            final MonsterData mon16 = this.mon;
            ++mon16.pursueMinRange;
            if (this.mon.pursueMinRange > 100) {
                this.mon.pursueMinRange = 100;
            }
        }
        else if (id == 116) {
            final MonsterData mon17 = this.mon;
            --mon17.sight;
            if (this.mon.sight < 0) {
                this.mon.sight = 0;
            }
        }
        else if (id == 117) {
            final MonsterData mon18 = this.mon;
            ++mon18.sight;
            if (this.mon.sight > 100) {
                this.mon.sight = 100;
            }
        }
        else if (id == 118) {
            final MonsterData mon19 = this.mon;
            --mon19.flee;
            if (this.mon.flee < 0) {
                this.mon.flee = 0;
            }
        }
        else if (id == 119) {
            final MonsterData mon20 = this.mon;
            ++mon20.flee;
            if (this.mon.flee > 100) {
                this.mon.flee = 100;
            }
        }
        else if (id == 120) {
            final MonsterData mon21 = this.mon;
            --mon21.wanderRange;
            if (this.mon.wanderRange < 0) {
                this.mon.wanderRange = 0;
            }
        }
        else if (id == 121) {
            final MonsterData mon22 = this.mon;
            ++mon22.wanderRange;
            if (this.mon.wanderRange > 100) {
                this.mon.wanderRange = 100;
            }
        }
        else if (id == 122) {
            final MonsterData mon23 = this.mon;
            --mon23.combatRange;
            if (this.mon.combatRange < 0) {
                this.mon.combatRange = 0;
            }
        }
        else if (id == 123) {
            final MonsterData mon24 = this.mon;
            ++mon24.combatRange;
            if (this.mon.combatRange > 100) {
                this.mon.combatRange = 100;
            }
        }
        else if (id == 124) {
            final MonsterData mon25 = this.mon;
            --mon25.mass;
            if (this.mon.mass < 0) {
                this.mon.mass = 0;
            }
        }
        else if (id == 125) {
            final MonsterData mon26 = this.mon;
            ++mon26.mass;
            if (this.mon.mass > 1000) {
                this.mon.mass = 1000;
            }
        }
        else if (id == 126) {
            final MonsterData mon27 = this.mon;
            --mon27.shadowSize;
            if (this.mon.shadowSize < 0) {
                this.mon.shadowSize = 0;
            }
        }
        else if (id == 127) {
            final MonsterData mon28 = this.mon;
            ++mon28.shadowSize;
            if (this.mon.shadowSize > 50) {
                this.mon.shadowSize = 50;
            }
        }
        else if (id == 128) {
            final MonsterData mon29 = this.mon;
            --mon29.radius;
            if (this.mon.radius < 0) {
                this.mon.radius = 0;
            }
        }
        else if (id == 129) {
            final MonsterData mon30 = this.mon;
            ++mon30.radius;
            if (this.mon.radius > 32) {
                this.mon.radius = 32;
            }
        }
        if (id == 200) {
            this.btnHumanoid.toggle = true;
            this.btnCreature.toggle = false;
            this.mon.doll.beast = false;
            this.game.dollScene.fromScene = 1;
            this.game.changeScene(this.game.dollScene);
        }
        else if (id == 201) {
            this.btnHumanoid.toggle = false;
            this.btnCreature.toggle = true;
            this.mon.doll.beast = true;
            this.game.creatureScene.fromScene = 1;
            this.game.changeScene(this.game.creatureScene);
        }
        else if (id != 300) {
            if (id == 600) {
                this.mon.name = this.name.text;
                this.monster[this.curMon] = this.mon;
                this.mon.doll.beast = this.btnCreature.toggle;
                this.mon.save(this.game);
                this.game.changeScene(this.game.listScene);
            }
            else if (id == 601) {
                this.game.changeScene(this.game.listScene);
            }
        }
    }
    
    @Override
    public void update(final long tick) {
        super.update(tick);
        this.updateLabels(tick);
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
    
    public void loadMonsters() {
        for (int m = 0; m < 255; ++m) {
            this.monster[m] = new MonsterData(m);
            if (new File("monsters/mon" + m).exists()) {
                try {
                    final FileInputStream f = new FileInputStream(new File("monsters/mon" + m));
                    final Input input = new Input((InputStream)f);
                    final MonsterData md = (MonsterData)this.game.kryo.readObject(input, (Class)MonsterData.class);
                    this.monster[m] = md.copy();
                    input.close();
                    f.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void updateLabels(final long tick) {
        this.lbl[0].text = "maxHP: " + this.mon.maxHP;
        this.lbl[1].text = "walkSpeed: " + this.mon.walkSpeed;
        this.lbl[2].text = "attackCoolDown: " + this.mon.attackCoolDown;
        this.lbl[3].text = "attackSpeed: " + this.mon.attackSpeed;
        this.lbl[4].text = "Light Alpha: " + this.mon.lightAlpha;
        this.lbl[5].text = "Light Dist: " + this.mon.lightDist;
        this.lbl[6].text = "maxAttackRange: " + this.mon.pursueMaxRange;
        this.lbl[7].text = "minAttackRange: " + this.mon.pursueMinRange;
        this.lbl[8].text = "sight: " + this.mon.sight;
        this.lbl[9].text = "flee: " + this.mon.flee;
        this.lbl[10].text = "wanderRange: " + this.mon.wanderRange;
        this.lbl[11].text = "combatRange: " + this.mon.combatRange;
        this.lbl[12].text = "mass: " + this.mon.mass;
        this.lbl[13].text = "shadowSize: " + this.mon.shadowSize;
        this.lbl[14].text = "bodySize: " + this.mon.radius;
        this.mon.flags[0] = this.btnFriendly.toggle;
        this.mon.flags[1] = this.btnNoCombat.toggle;
        this.mon.flags[2] = this.btnAids.toggle;
        this.lblEdit.text = "Editing Monster: " + this.curMon;
    }
}
