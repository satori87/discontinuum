// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

public class Monster extends Entity
{
    public int mon;
    public MonsterData data;
    
    public Monster(final DCGame game, final int m, final int x, final int y) {
        super(game);
        this.mon = 0;
        this.game = game;
        this.mon = m;
        this.x = x;
        this.y = y;
        this.birthX = x;
        this.birthY = y;
        this.lightAlpha = 0.4f;
        this.lightDistance = 2.2f;
        this.data = game.monsterScene.monster[m];
        this.doll = this.data.doll.copy();
        this.speed = this.data.walkSpeed / 10.0f;
        this.attackCoolDown = this.data.attackCoolDown;
        this.attackSpeed = this.data.attackSpeed / 20.0f;
        this.combatRange = this.data.combatRange * 32;
        this.flee = this.data.flee;
        this.lightAlpha = this.data.lightAlpha / 10.0f;
        this.lightDistance = this.data.lightDist / 10.0f;
        this.shadowSize = this.data.shadowSize / 100.0f;
        this.mass = this.data.mass / 10.0f;
        this.maxHP = this.data.maxHP;
        this.radius = this.data.radius / 100.0f;
        this.pursueMaxRange = (float)this.data.pursueMaxRange;
        this.pursueMinRange = (float)this.data.pursueMinRange;
        this.sight = this.data.sight * 32;
        this.wanderRange = this.data.wanderRange * 32;
        this.friendly = this.data.flags[0];
        this.canFight = !this.data.flags[1];
        this.aids = this.data.flags[2];
    }
    
    @Override
    public void pre(final long tick) {
        super.pre(tick);
    }
    
    @Override
    public void post(final long tick) {
        super.post(tick);
    }
    
    @Override
    public void join(final Map m) {
        super.join(m);
    }
    
    @Override
    public void part(final Map m) {
        super.part(m);
    }
}
