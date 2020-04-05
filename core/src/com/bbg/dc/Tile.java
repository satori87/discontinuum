// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

import java.io.Serializable;

public class Tile implements Serializable
{
    private static final long serialVersionUID = 1L;
    public int[] sprite;
    public int[] spriteSet;
    public Att[] att;
    
    public Tile() {
        this.sprite = new int[8];
        this.spriteSet = new int[8];
        (this.att = new Att[2])[0] = new Att();
        this.att[1] = new Att();
        for (int i = 0; i < 8; ++i) {
            this.sprite[i] = 0;
            this.spriteSet[i] = -1;
        }
    }
}
