// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

public class Target extends Entity
{
    public Target(final DCGame game, final Map map, final AIManager aiMan, final int x, final int y) {
        super(game);
        this.game = game;
        this.x = x;
        this.y = y;
        this.doll = new Doll();
        this.createBody(true);
        this.aiMan = new EntityAIManager(game, map, aiMan, this);
        this.body.setUserData((Object)this.aiMan);
    }
}
