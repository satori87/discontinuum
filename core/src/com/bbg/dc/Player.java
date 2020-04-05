// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

import com.bbg.dc.iface.Scene;
import com.badlogic.gdx.graphics.Color;
import java.util.Iterator;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bbg.dc.aistuff.Vector;

public class Player extends Entity
{
    int onExit;
    Entity hoverTarget;
    public boolean running;
    Entity foundE;
    
    public Player(final DCGame game) {
        super(game);
        this.onExit = 0;
        this.running = false;
        this.foundE = null;
        this.game = game;
        this.maxHP = 200;
        this.doll = new Doll(false);
        this.doll.monster = 18;
        this.doll.i = 0;
        this.speed = 2.2f;
        this.doll.data[3][0] = 3;
        this.doll.steedCycle = 0;
        this.doll.data[13][0] = 1;
        this.doll.data[17][0] = 3;
        this.doll.data[8][0] = 1;
        this.doll.data[22][0] = 2;
        this.lightAlpha = 0.6f;
        this.lightDistance = 1.8f;
        this.lightRays = 128;
        this.mass = 1.6f;
        this.attackSpeed = 3.0f;
    }
    
    boolean onMonster(final int pX, final int pY, final Entity e) {
        final int edX = e.doll.renderX;
        final int edY = e.doll.renderY;
        final int edW = e.doll.dw;
        final int edH = e.doll.dh;
        if (Vector.distance((float)pX, (float)pY, (float)(edX + edW / 2), (float)(edY + edH / 2)) < edW) {
            TextureRegion tr = null;
            if (e.doll.beast) {
                tr = AssetLoader.getMonsterRegion(e.doll.monster, e.doll.dcycle, e.doll.i, e.doll.sourceX, e.doll.sourceY, e.doll.dw, e.doll.dh);
            }
            else {
                tr = AssetLoader.getSpriteRegion(e.doll.set, 3, 1, e.doll.daction, e.doll.ddir, e.doll.dstep);
            }
            final int t = this.game.getTextureNumber(tr.getTexture());
            for (int r = 8, fx = pX - r; fx < pX + r; ++fx) {
                for (int fy = pY - r; fy < pY + r; ++fy) {
                    final int tx = tr.getRegionX() + (fx - edX);
                    final int ty = tr.getRegionY() + (fy - edY);
                    if (tx >= tr.getRegionX() && ty >= tr.getRegionY() && tx < tr.getRegionX() + edW - 1 && ty < tr.getRegionY() + edH - 1 && AssetLoader.atlasMask.mask[t][tx][ty]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public int checkPlayerClickWalk(final int pX, final int pY) {
        int h = 0;
        this.foundE = null;
        for (final Entity e : this.map.entities) {
            if (e instanceof Monster && this.onMonster(pX, pY, e)) {
                this.foundE = e;
                return 3;
            }
        }
        for (int w = pX / 4 - 4; w <= pX / 4 + 4; ++w) {
            for (int hh = pY / 4 - 4; hh <= pY / 4 + 4; ++hh) {
                h = hh;
                if (w >= 0 && h >= 0 && w < 512 && h < 512 && this.map.data.wall[w][h]) {
                    return 2;
                }
            }
        }
        return 0;
    }
    
    public void processInput() {
        final int mX = this.game.input.getMouseX();
        final int mY = this.game.input.getMouseY();
        this.hoverTarget = null;
        this.onExit = -1;
        if (this.game.scene.inWindow) {
            return;
        }
        if (mX >= 0 && mY >= 0 && mX < this.game.width && mY < this.game.height) {
            final Vector.Pos mouse = this.map.getMouse();
            int space = 0;
            int vX = mouse.x;
            int vY = mouse.y;
            this.game.setCursor(0);
            if (vX < 0 && this.map.data.canExit(1)) {
                this.onExit = 1;
            }
            else if (vX >= 2048 && this.map.data.canExit(3)) {
                this.onExit = 3;
            }
            else if (vY < 0 && this.map.data.canExit(0)) {
                this.onExit = 0;
            }
            else if (vY >= 2048 && this.map.data.canExit(2)) {
                this.onExit = 2;
            }
            for (final Entity e : this.map.entities) {
                e.outline = false;
            }
            if (Map.validPos(mouse.x, mouse.y)) {
                space = this.checkPlayerClickWalk(vX, vY);
                if (space == 3) {
                    this.hoverTarget = null;
                    if (this.foundE != null) {
                        if (this.foundE.canFight) {
                            this.game.setCursor(1);
                        }
                        this.hoverTarget = this.foundE;
                        this.foundE.outline = true;
                        this.foundE.outlineCol = Color.RED;
                        this.onExit = -1;
                    }
                }
            }
            if (this.game.input.mouseDown[0] && this.game.input.wasMouseJustClicked[0]) {
                this.game.input.wasMouseJustClicked[0] = false;
                boolean out = false;
                float r = 0.0f;
                float d = 4.0f;
                Vector v = null;
                int help = 0;
                do {
                    ++help;
                    space = this.checkPlayerClickWalk(vX, vY);
                    if (space == 0) {
                        this.aiMan.setPath(vX, vY);
                        out = true;
                    }
                    else if (space == 1) {
                        out = true;
                    }
                    else if (space == 2) {
                        r += (float)0.7853981633974483;
                        if (r >= 6.283185307179586) {
                            r = 0.0f;
                            d += 8.0f;
                        }
                        v = new Vector(r, d);
                        vX += (int)v.xChange;
                        vY += (int)v.yChange;
                    }
                    else {
                        if (space != 3) {
                            continue;
                        }
                        float dist = 0.0f;
                        float highest = 9999.0f;
                        Entity best = null;
                        for (final Entity e2 : this.map.entities) {
                            if (e2 instanceof Monster && !out && e2.canFight) {
                                dist = Vector.distance((float)vX, (float)vY, (float)e2.x, (float)(e2.y - 32));
                                if (dist >= 32.0f || dist >= highest) {
                                    continue;
                                }
                                highest = dist;
                                best = e2;
                            }
                        }
                        if (best != null) {
                            this.aiMan.pursue(best);
                        }
                        out = true;
                    }
                } while (!out && help < 10000);
                if (help >= 800) {
                    System.out.println("processinput help:" + help);
                }
            }
        }
        final boolean[] keyDown = this.game.input.keyDown;
        final float s = this.speed * 500.0f;
        if (keyDown[19]) {
            this.body.applyForce(0.0f, -s, 0.0f, 0.0f, true);
        }
        if (keyDown[20]) {
            this.body.applyForce(0.0f, s, 0.0f, 0.0f, true);
        }
        if (keyDown[21]) {
            this.body.applyForce(-s, 0.0f, 0.0f, 0.0f, true);
        }
        if (keyDown[22]) {
            this.body.applyForce(s, 0.0f, 0.0f, 0.0f, true);
        }
    }
    
    @Override
    public void pre(final long tick) {
        this.processInput();
        super.pre(tick);
    }
    
    public void endTick() {
        if (!this.checkTransition()) {
            this.checkTile(this.x, this.y);
        }
    }
    
    boolean checkTransition() {
        final MapData m = this.map.data;
        if (this.x < 0) {
            this.trans(m.data[2], 2047, this.y, 0);
        }
        else if (this.x >= 2048) {
            this.trans(m.data[4], 0, this.y, 0);
        }
        else if (this.y < 0) {
            this.trans(m.data[1], this.x, 2047, 0);
        }
        else {
            if (this.y < 2048) {
                return false;
            }
            this.trans(m.data[3], this.x, 0, 0);
        }
        return true;
    }
    
    @Override
    public void render(final int px, final int py, final float scale, final boolean showname, final int namex, final int namey) {
        int w = 0;
        int r = 0;
        int r2 = 0;
        int action = 0;
        if (this.moving) {
            r = this.rideStep / 16;
            w = (int)(this.walkStep / 32.0f);
        }
        if (this.attacking) {
            w = (int)(this.attackFrame / 32.0f);
            r2 = (int)(this.attackFrame / 32.0f);
            action = 1;
        }
        if (this.doll.steed[0] > 0) {
            this.doll.renderHumanoid(this.game, px, py, r, r2, 0, this.dir);
        }
        else {
            this.doll.renderHumanoid(this.game, px, py, 0, w, action, this.dir);
        }
    }
    
    void checkTile(final int x, final int y) {
        final int dx = x / 32;
        final int dy = y / 32;
        if (dx >= 0 && dx < 64 && dy >= 0 && dy < 64) {
            for (int b = 0; b < 2; ++b) {
                final Att a = this.map.data.tile[dx][dy].att[b];
                if (a.data[0] == 2) {
                    final int m = a.data[1];
                    final int wx = a.data[2];
                    final int wy = a.data[3];
                    final int s = a.data[4];
                    this.warp(m, wx, wy, s);
                }
            }
        }
    }
    
    @Override
    public void join(final Map m) {
        super.join(m);
    }
    
    @Override
    public void part(final Map m) {
        super.part(m);
    }
    
    public void warp(final int m, final int x, final int y, final int s) {
        this.game.gameScene.switchMap(m);
        final int ex = this.x % 32;
        final int ey = this.y % 32;
        this.move(x * 32 + ex, y * 32 + ey);
        if (s > 0) {
            Scene.playSound(s, 1.0f, 0.8f);
        }
    }
    
    void trans(final int m, final int x, final int y, final int s) {
        if (m < 0) {
            return;
        }
        this.game.gameScene.switchMap(m);
        this.move(x, y);
        if (s > 0) {
            Scene.playSound(s, 1.0f, 0.8f);
        }
    }
    
    void overlay() {
        String text = "";
        final int mX = this.game.input.getMouseX();
        final int mY = this.game.input.getMouseY();
        Color c = Color.YELLOW;
        if (this.onExit >= 0) {
            text = "Go " + Prefs.getCardinalDirectionName(this.onExit);
            c = Color.YELLOW;
        }
        else if (this.hoverTarget != null && this.hoverTarget instanceof Monster) {
            final Monster mon = (Monster)this.hoverTarget;
            if (mon.canFight) {
                text = "Attack " + mon.data.name;
                c = Color.RED;
            }
            else {
                text = mon.data.name;
                c = Color.CYAN;
            }
        }
        if (text.length() > 0) {
            c.a = 1.0f;
            this.game.drawFontAbs(0, mX + 16, mY + 42, text, true, 1.0f, c);
        }
    }
}
