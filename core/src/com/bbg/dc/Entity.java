// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef;
import java.util.Iterator;
import com.bbg.dc.iface.Scene;
import com.bbg.dc.aistuff.Vector;
import com.bbg.mapeffects.DynamicLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;

public class Entity
{
    DCGame game;
    Body body;
    Body lBody;
    EntityAIManager aiMan;
    public Doll doll;
    public Map map;
    public float shadowSize;
    public float radius;
    public float mass;
    public int height;
    public float speed;
    public int hp;
    public int maxHP;
    public int bleeding;
    long bleedStamp;
    public boolean dead;
    int birthX;
    int birthY;
    public int x;
    public int y;
    public int dir;
    long dirStamp;
    boolean moving;
    public float walkStep;
    public long walkStamp;
    public int nextStep;
    public int rideStep;
    public long footAt;
    public int lastFoot;
    int curSteps;
    public boolean outline;
    public Color outlineCol;
    public boolean outlineUp;
    public float outlineAlpha;
    public long outlineStamp;
    int cycle;
    public long attackStamp;
    public long coolDownStamp;
    public int attackCoolDown;
    public float attackFrame;
    public boolean attacking;
    long attackFrameTimer;
    float attackSpeed;
    int curCycle;
    boolean attackedYet;
    public int wanderRange;
    public int combatRange;
    public float pursueMinRange;
    public float pursueMaxRange;
    public int flee;
    public boolean friendly;
    public boolean canFight;
    public boolean aids;
    public int sight;
    public float lightAlpha;
    public int lightRays;
    public float lightDistance;
    DynamicLight light;
    
    public Entity() {
        this.shadowSize = 0.05f;
        this.radius = 0.1f;
        this.mass = 1.0f;
        this.height = 64;
        this.speed = 1.0f;
        this.hp = 0;
        this.maxHP = 0;
        this.bleeding = 0;
        this.bleedStamp = 0L;
        this.dead = false;
        this.birthX = 0;
        this.birthY = 0;
        this.x = 400;
        this.y = 400;
        this.dir = 2;
        this.dirStamp = 0L;
        this.moving = false;
        this.walkStep = 0.0f;
        this.walkStamp = 0L;
        this.nextStep = 0;
        this.rideStep = 0;
        this.footAt = 0L;
        this.lastFoot = 0;
        this.curSteps = 0;
        this.outline = false;
        this.outlineCol = Color.WHITE;
        this.outlineUp = false;
        this.outlineAlpha = 0.0f;
        this.outlineStamp = 0L;
        this.cycle = 0;
        this.attackStamp = 0L;
        this.coolDownStamp = 0L;
        this.attackCoolDown = 200;
        this.attackFrame = 0.0f;
        this.attacking = false;
        this.attackFrameTimer = 0L;
        this.attackSpeed = 5.0f;
        this.curCycle = 0;
        this.attackedYet = false;
        this.wanderRange = 0;
        this.combatRange = 0;
        this.pursueMinRange = 30.0f;
        this.pursueMaxRange = 38.0f;
        this.flee = 0;
        this.friendly = false;
        this.canFight = true;
        this.aids = false;
        this.sight = 0;
        this.lightAlpha = 0.0f;
        this.lightRays = 32;
        this.lightDistance = 0.0f;
        this.light = null;
    }
    
    public Entity(final DCGame game) {
        this.shadowSize = 0.05f;
        this.radius = 0.1f;
        this.mass = 1.0f;
        this.height = 64;
        this.speed = 1.0f;
        this.hp = 0;
        this.maxHP = 0;
        this.bleeding = 0;
        this.bleedStamp = 0L;
        this.dead = false;
        this.birthX = 0;
        this.birthY = 0;
        this.x = 400;
        this.y = 400;
        this.dir = 2;
        this.dirStamp = 0L;
        this.moving = false;
        this.walkStep = 0.0f;
        this.walkStamp = 0L;
        this.nextStep = 0;
        this.rideStep = 0;
        this.footAt = 0L;
        this.lastFoot = 0;
        this.curSteps = 0;
        this.outline = false;
        this.outlineCol = Color.WHITE;
        this.outlineUp = false;
        this.outlineAlpha = 0.0f;
        this.outlineStamp = 0L;
        this.cycle = 0;
        this.attackStamp = 0L;
        this.coolDownStamp = 0L;
        this.attackCoolDown = 200;
        this.attackFrame = 0.0f;
        this.attacking = false;
        this.attackFrameTimer = 0L;
        this.attackSpeed = 5.0f;
        this.curCycle = 0;
        this.attackedYet = false;
        this.wanderRange = 0;
        this.combatRange = 0;
        this.pursueMinRange = 30.0f;
        this.pursueMaxRange = 38.0f;
        this.flee = 0;
        this.friendly = false;
        this.canFight = true;
        this.aids = false;
        this.sight = 0;
        this.lightAlpha = 0.0f;
        this.lightRays = 32;
        this.lightDistance = 0.0f;
        this.light = null;
        this.game = game;
    }
    
    public float getAngle() {
        return 0.0f;
    }
    
    public void render(final int px, final int py, final float scalhe, final boolean showname, final int namex, final int namey) {
        int w = 0;
        int r = 0;
        final int ww = (int)(this.walkStep / 32.0f);
        int action = 0;
        if (this.moving) {
            r = this.rideStep / 16;
            w = (int)(this.walkStep / 32.0f);
        }
        this.cycle = 6;
        if (this.doll.beast) {
            if (this.attacking) {
                w = (int)(this.attackFrame / 32.0f);
                action = 1;
                this.cycle = ((this.curCycle == 3) ? 6 : this.curCycle);
            }
            if (this.curCycle == 3 && this.aiMan.pursuit != null) {
                Vector v = Vector.byChange((float)(this.aiMan.pursuit.x - this.x), (float)(this.aiMan.pursuit.y - this.y));
                float perc = this.attackFrame / (this.curSteps * 32.0f);
                if (perc >= 0.5f) {
                    perc = 1.0f - perc;
                }
                v = new Vector(v.direction, v.intensity * perc);
                this.doll.renderBeast(this.game, px + (int)v.xChange, py + (int)v.yChange, ww, 6, this.dir);
            }
            else {
                this.doll.renderBeast(this.game, px, py, w, this.cycle, this.dir);
            }
        }
        else {
            if (this.attacking) {
                w = (int)(this.attackFrame / 32.0f);
                action = 1;
            }
            if (this.doll.steed[0] > 0) {
                this.doll.renderHumanoid(this.game, px, py, r, 0, 0, this.dir);
            }
            else {
                this.doll.renderHumanoid(this.game, px, py, 0, w, action, this.dir);
            }
        }
        if (!this.doll.outline) {
            this.game.setColor(Color.WHITE);
        }
    }
    
    public int getDrawX() {
        return this.doll.renderX;
    }
    
    public int getDrawY() {
        return this.doll.renderY;
    }
    
    public int getDrawW() {
        return this.doll.renderW;
    }
    
    public int getDrawH() {
        return this.doll.renderH;
    }
    
    public void move(final int nx, final int ny) {
        this.x = nx;
        this.y = ny;
        this.body.setTransform((this.x + this.doll.getBodyX()) / 100.0f, (this.y + this.doll.getBodyY()) / 100.0f, 0.0f);
    }
    
    void incrementStep() {
        float mod = 0.0f;
        mod = this.speed * 1.5f;
        if (!this.doll.beast) {
            if (this.game.gameScene.shifting()) {
                if (this.doll.steed[0] > 0) {
                    this.doll.steedCycle = 2;
                    this.rideStep += 4;
                }
                else {
                    this.walkStep += mod;
                }
            }
            else if (this.doll.steed[0] > 0) {
                this.doll.steedCycle = 1;
                this.rideStep += 2;
            }
            else {
                this.walkStep += mod * 2.0f;
            }
            if (this.walkStep >= 288.0f) {
                this.walkStep = 32.0f;
            }
            if (this.rideStep > 95) {
                this.rideStep = 0;
            }
        }
        else {
            final int steps = this.doll.getMonsterWSteps();
            mod = 1.5f * this.speed * (steps / 9.0f);
            if (this.game.gameScene.shifting()) {
                if (this.doll.steed[0] > 0) {
                    this.doll.steedCycle = 2;
                    this.rideStep += 4;
                }
                else {
                    this.walkStep += mod;
                }
            }
            else if (this.doll.steed[0] > 0) {
                this.doll.steedCycle = 1;
                this.rideStep += 2;
            }
            else {
                this.walkStep += mod * 2.0f;
            }
            if (this.walkStep >= (steps - 1) * 32) {
                this.walkStep = 0.0f;
                if (this.doll.doesMonsterStand()) {
                    this.walkStep = 32.0f;
                }
            }
            if (this.rideStep > 95) {
                this.rideStep = 0;
            }
        }
        float outlineD = 0.05f;
        if (this.map.player.aiMan.pursuit == this) {
            this.outlineCol = Color.RED;
            outlineD /= 2.0f;
        }
        if ((this.outline || this.map.player.aiMan.pursuit == this) && this.game.tick > this.outlineStamp) {
            if (this.outlineUp) {
                this.outlineAlpha += outlineD;
                if (this.outlineAlpha >= 0.8f) {
                    this.outlineUp = false;
                    this.outlineAlpha = 1.0f;
                }
            }
            else {
                this.outlineAlpha -= outlineD;
                if (this.outlineAlpha <= 0.2f) {
                    this.outlineUp = true;
                    this.outlineAlpha = 0.0f;
                }
            }
        }
    }
    
    public void pre(final long tick) {
        if (this.light != null) {
            this.light.update();
        }
        this.incrementStep();
        this.aiMan.update();
        this.combat();
        this.checkBleed();
    }
    
    float getAcceleration() {
        return this.speed * this.mass * 100.0f;
    }
    
    void checkBleed() {
        this.bleeding = this.calcBleeding();
        if (this.game.tick > this.bleedStamp && this.bleeding > Math.random() * 40.0) {
            this.bleedStamp = this.game.tick + 200L;
            this.bleed(1, 0, 1.5f);
            if (this.bleeding > 7) {
                this.bloodstream(0 + Random.getInt(2), 4);
            }
        }
    }
    
    public void die(final Entity attacker) {
        if (this instanceof Monster) {
            Scene.play3D(Random.getInt(5) + 48, (float)this.map.player.x, (float)this.map.player.y, (float)this.x, (float)this.y, 5, 1.0f, 1.0f, false);
            final float spreadFactor = 1.0f;
            this.dead = true;
            this.destroyBody();
            this.bleed(30, 0, 2.5f * spreadFactor);
            this.bleed(1 + Random.getInt(2), 1, 2.5f * spreadFactor);
            this.bloodstream(3 + Random.getInt(9), 15);
        }
        else {
            final float spreadFactor = 2.8f;
            this.bleed(30, 0, 2.5f * spreadFactor);
            this.bleed(5 + Random.getInt(5), 1, 2.5f * spreadFactor);
            this.bloodstream(5 + Random.getInt(15), 15);
            this.hp = this.maxHP;
            System.out.println("Debug death avoidance!");
        }
    }
    
    public void takeDamage(final Entity attacker, final int dam) {
        if (attacker == null) {
            return;
        }
        this.bleed(dam * 2, 0, 2.0f);
        this.bloodstream(3 + Random.getInt(dam), 7);
        Scene.play3D(Random.getInt(12) + 36, (float)this.map.player.x, (float)this.map.player.y, (float)this.x, (float)this.y, 5, 1.0f, 1.0f, false);
        if (this instanceof Monster) {
            this.aiMan.pursue(attacker);
            if (this.aids) {
                for (final Entity e : this.map.entities) {
                    if (e instanceof Monster && e != attacker && e != this) {
                        final Monster mon = (Monster)e;
                        final Monster me = (Monster)this;
                        if (mon.mon != me.mon || Vector.distance((float)this.getTrueX(), (float)this.getTrueY(), (float)mon.getTrueX(), (float)mon.getTrueY()) >= mon.sight) {
                            continue;
                        }
                        mon.aiMan.pursue(attacker);
                    }
                }
            }
        }
        this.map.fxMan.addFloater(this, 0, -48, Integer.toString(dam), Color.RED, 0L);
        this.hp -= dam;
        if (this.hp <= 0) {
            this.die(attacker);
        }
        else if (this instanceof Monster) {
            final float hper = this.hp / (float)this.maxHP;
            final int per = (int)(hper * 100.0f);
            if (per <= this.flee) {
                this.aiMan.flee(attacker);
            }
        }
    }
    
    public void physicalAttack(final Entity e) {
        if (e == null) {
            return;
        }
        final int a = this.getPhysAtk();
        final int d = e.getPhysDef();
        final int dam = a - d;
        e.takeDamage(this, dam);
    }
    
    public int getPhysDef() {
        return 3;
    }
    
    public int getPhysAtk() {
        return 10 + AssetLoader.rndInt(5);
    }
    
    void combat() {
        float mod = 0.0f;
        mod = this.attackSpeed;
        int steps = 0;
        if (this.aiMan.canAttack()) {
            if (!this.aiMan.validPursuit()) {
                return;
            }
            if (!this.attacking && this.game.tick > this.coolDownStamp) {
                this.attacking = true;
                this.attackedYet = false;
                this.curCycle = 0;
                if (this.doll.beast && this.doll.monster == 1 && this.doll.d[1] && AssetLoader.rndInt(10) > 5) {
                    this.curCycle = 2;
                }
                this.attackFrame = 0.0f;
            }
        }
        if (this.attacking) {
            if (this.curCycle == 2) {
                steps = Doll.getMonsterBSteps(1);
            }
            else {
                steps = this.doll.getASteps();
            }
            if (steps == 0) {
                this.curCycle = 3;
                steps = 6;
            }
            this.curSteps = steps;
            mod = this.attackSpeed * (steps / 3.0f);
            this.attackFrame += mod;
            if (this.attackFrame >= steps * 32.0f) {
                this.walkStep = 0.0f;
                this.coolDownStamp = this.game.tick + this.attackCoolDown;
                this.attacking = false;
                if (this.curCycle < 3) {
                    this.physicalAttack(this.aiMan.pursuit);
                }
            }
            else if (this.attackFrame >= steps * 32.0f / 2.0f && this.curCycle == 3 && !this.attackedYet) {
                this.attackedYet = true;
                this.physicalAttack(this.aiMan.pursuit);
            }
        }
    }
    
    public void join(final Map map) {
        this.hp = this.maxHP;
        this.map = map;
        this.createBody(false);
        this.createLBody();
        this.light = map.lightMan.createLight(this.lightRays, this.lightDistance, this.lightAlpha, true, true, 1.5f, this.lBody);
        this.attacking = false;
        this.walkStep = 0.0f;
        this.aiMan = new EntityAIManager(this.game, map, map.aiMan, this);
    }
    
    public void part(final Map m) {
        this.destroyBody();
    }
    
    public int getTrueX() {
        return this.x + this.doll.getBodyX();
    }
    
    public int getTrueY() {
        return this.y + this.doll.getBodyY();
    }
    
    public void post(final long tick) {
        this.x = Math.round(this.body.getPosition().x * 100.0f) - this.doll.getBodyX();
        this.y = Math.round(this.body.getPosition().y * 100.0f) - this.doll.getBodyY();
        if (this.shadowSize > 0.0f) {
            this.lBody.setTransform((this.x + this.doll.getLBodyX()) / 100.0f, (this.y + this.doll.getLBodyY()) / 100.0f, this.getAngle());
        }
        this.checkIfMoving();
        if (!this.doll.beast) {
            if (this.moving) {
                if (this.doll.steed[0] == 0 && (Math.abs(this.walkStep - 64.0f) < 4.0f || Math.abs(this.walkStep - 192.0f) < 4.0f)) {
                    this.footStep();
                }
                else if (this.doll.steed[0] > 0 && (this.rideStep == 32 || this.rideStep == 96 || this.rideStep == 160)) {
                    this.footStep(2, true);
                }
                else if (this.doll.steedCycle == 2 && this.doll.steed[0] > 0 && this.rideStep == 66) {
                    this.footStep(2, true);
                }
            }
        }
        else if (this.moving) {
            final int l = (int)(this.walkStep / 32.0f);
            if (this.doll.steed[0] == 0 && this.doll.doesMonsterStep((int)(this.walkStep / 32.0f))) {
                if (l != this.lastFoot) {
                    this.lastFoot = l;
                    if (Doll.getMonsterSound(this.doll.monster, this.doll.i) >= 0) {
                        this.footStep(Doll.getMonsterSound(this.doll.monster, this.doll.i), false);
                    }
                    else if (Doll.getMonsterSound(this.doll.monster, this.doll.i) == -1) {
                        this.footStep();
                    }
                }
            }
            else if (l != this.lastFoot) {
                this.lastFoot = -1;
            }
        }
    }
    
    public void destroyBody() {
        if (this.body != null) {
            this.body.getWorld().destroyBody(this.body);
        }
        if (this.lBody != null) {
            this.lBody.getWorld().destroyBody(this.lBody);
        }
    }
    
    void checkIfMoving() {
        if (this.doll.flies()) {
            this.moving = true;
        }
        else {
            this.moving = false;
        }
        final float v = (float)Math.hypot(this.body.getLinearVelocity().x, this.body.getLinearVelocity().y);
        if (v > 0.0f) {
            this.moving = true;
            if (this.aiMan.pathing && this.game.tick > this.dirStamp) {
                if (this.aiMan.nextDir != this.dir) {
                    this.dirStamp = this.game.tick + 400L;
                }
                this.dir = this.aiMan.nextDir;
            }
        }
    }
    
    void footStep(final int snd, final boolean cycle) {
        float v = 1.0f;
        float pitch = 1.0f;
        if (snd == 1) {
            v = 1.0f;
        }
        else if (snd == 3) {
            v = 0.15f;
            pitch = 1.5f;
        }
        else if (snd == 2) {
            v = 0.3f;
        }
        if (cycle) {
            if (this instanceof Player) {
                Scene.playSound(snd * 4 + this.nextStep, v, pitch);
            }
            else {
                Scene.play3D(snd * 4 + this.nextStep, (float)this.map.player.x, (float)this.map.player.y, (float)this.x, (float)this.y, 5, v, pitch, false);
            }
        }
        else {
            Scene.play3D(snd, (float)this.map.player.x, (float)this.map.player.y, (float)this.x, (float)this.y, 5, 1.0f, 1.0f, false);
        }
        ++this.nextStep;
        if (this.nextStep > 3) {
            this.nextStep = 0;
        }
    }
    
    void footStep() {
        if (this.x < 0 || this.y < 0 || this.x >= 2048 || this.y >= 2048) {
            return;
        }
        final Tile ti = this.map.data.tile[this.x / 32][this.y / 32];
        int snd = 1;
        for (int i = 0; i < 4; ++i) {
            final int set = ti.spriteSet[i];
            if (set >= 0) {
                final int t = ti.sprite[i];
                final int s = AssetLoader.getTileFootsteps(set, t);
                if (s > snd) {
                    snd = s;
                }
            }
        }
        this.footStep(snd, true);
    }
    
    public void createBody(final boolean sensor) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(this.getTrueX() / 100.0f, this.getTrueY() / 100.0f);
        bodyDef.angularVelocity = 0.0f;
        (this.body = this.game.gameScene.map.world.createBody(bodyDef)).setUserData((Object)this.aiMan);
        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = false;
        final CircleShape circle = new CircleShape();
        circle.setRadius(this.radius);
        fixtureDef.shape = (Shape)circle;
        fixtureDef.density = 5.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 1.0f;
        if (this instanceof Player) {
            fixtureDef.filter.categoryBits = 1;
            fixtureDef.filter.maskBits = 10;
        }
        else if (this instanceof Monster) {
            fixtureDef.filter.categoryBits = 2;
            fixtureDef.filter.maskBits = 15;
        }
        fixtureDef.isSensor = sensor;
        this.body.createFixture(fixtureDef);
        this.body.setLinearDamping(400.0f);
        final MassData md = new MassData();
        md.mass = this.mass;
        this.body.setMassData(md);
        circle.dispose();
    }
    
    public void bloodstream(final int t, final int l) {
        final float spread = 60.0f;
        int bloodType = 0;
        Color bloodCol = Color.WHITE;
        if (this instanceof Monster) {
            bloodType = MonsterData.getBloodType(this.doll.monster, this.doll.i);
            if (this.doll.monster == 11 || this.doll.monster == 12) {
                bloodCol = MonsterData.getSlimeColor(this.doll.monster, this.doll.i);
            }
        }
        for (int n = 0; n < t; ++n) {
            final float XVelocity = (float)(-spread + Math.random() * spread * 2.0);
            final float YVelocity = (float)(-(spread / 2.0f * 3.0f) + Math.random() * (spread / 2.0f) * 6.0);
            final float p = (float)(Math.random() * 16.0) - 8.0f;
            final float q = (float)(Math.random() * 8.0) - 4.0f;
            final float fallDistance = (float)(4.0 + Math.random() * 24.0);
            for (int i = 0; i < l; ++i) {
                final float c = (float)(10 * i);
                this.map.fxMan.addBlood(bloodType, bloodCol, this.x + p, this.y + q, c, 100.0f + c + (float)Math.random() * 2000.0f, XVelocity, YVelocity, spread, fallDistance - 3.0f + Random.getInt(3));
            }
        }
    }
    
    public int calcBleeding() {
        if (this.hp / this.maxHP < 0.5) {
            return this.bleeding = (int)(10.0f - this.hp / this.maxHP * 5.0f);
        }
        return 0;
    }
    
    public void bleed(final int t, final int type, final float spread) {
        int bloodType = 0;
        Color bloodCol = Color.WHITE;
        if (this instanceof Monster) {
            bloodType = MonsterData.getBloodType(this.doll.monster, this.doll.i);
            if (this.doll.monster == 11 || this.doll.monster == 12) {
                bloodCol = MonsterData.getSlimeColor(this.doll.monster, this.doll.i);
            }
        }
        for (int i = 0; i < t; ++i) {
            final float p = (float)(Math.random() * 16.0) - 8.0f;
            final float q = (float)(Math.random() * 8.0) - 4.0f;
            final float c = (float)Math.random() * 100.0f;
            final float XVelocity = (float)(spread * -40.0f + Math.random() * spread * 80.0);
            final float YVelocity = (float)(spread / 2.0f * -10.0f + Math.random() * (spread / 2.0f) * 20.0);
            this.map.fxMan.addBlood(bloodType + type, bloodCol, this.x + p, this.y + q, c, 100.0f + c + (float)Math.random() * 2000.0f, XVelocity, YVelocity, spread * 100.0f, (float)Math.random() * (spread / 10.0f));
        }
    }
    
    public void createLBody() {
        final World world = this.map.lightMan.getWorld();
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((this.x + this.doll.getLBodyX()) / 100.0f, (this.y + this.doll.getLBodyY()) / 100.0f);
        this.lBody = world.createBody(bodyDef);
        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 100.0f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.filter.categoryBits = 1;
        fixtureDef.filter.maskBits = 1;
        final CircleShape circle = new CircleShape();
        if (this.shadowSize == 0.0f) {
            circle.setRadius(10.0f);
            fixtureDef.isSensor = true;
        }
        else {
            circle.setRadius(this.shadowSize);
        }
        fixtureDef.shape = (Shape)circle;
        this.lBody.createFixture(fixtureDef);
        circle.dispose();
    }
}
