// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

public class Att
{
    public int[] data;
    public boolean[] flags;
    
    public Att() {
        this.data = new int[10];
        this.flags = new boolean[16];
        for (int i = 0; i < 16; ++i) {
            if (i < 10) {
                this.data[i] = 0;
            }
            this.flags[i] = false;
        }
    }
}
