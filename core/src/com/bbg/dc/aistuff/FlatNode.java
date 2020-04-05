// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.aistuff;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

public class FlatNode
{
    public int x;
    public int y;
    public int index;
    public Array<Connection<FlatNode>> connections;
    
    public FlatNode(final int index, final int x, final int y) {
        this.x = 0;
        this.y = 0;
        this.index = 0;
        this.x = x;
        this.y = y;
        this.index = index;
        this.connections = new Array<Connection<FlatNode>>();
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public Array<Connection<FlatNode>> getConnections() {
        return this.connections;
    }
    
    public int getTrueX() {
        return this.x * 32 + 16;
    }
    
    public int getTrueY() {
        return this.y * 32 + 16;
    }
}
