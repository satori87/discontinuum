// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.aistuff;

import java.util.ArrayList;
import java.util.List;

public class MapNode
{
    public int index;
    public int x;
    public int y;
    public boolean active;
    public List<Integer> connections;
    
    public MapNode() {
        this.index = 0;
        this.x = 0;
        this.y = 0;
        this.active = false;
        this.connections = new ArrayList<Integer>();
    }
    
    public MapNode(final int index, final int x, final int y) {
        this();
        this.x = x;
        this.y = y;
        this.index = index;
    }
    
    public int getTrueX() {
        return this.x * 32 + 16;
    }
    
    public int getTrueY() {
        return this.y * 32 + 16;
    }
}
