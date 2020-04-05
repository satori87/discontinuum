// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.aistuff;

import com.badlogic.gdx.ai.pfa.DefaultConnection;

public class FlatConnection extends DefaultConnection<FlatNode>
{
    public FlatConnection(final FlatGraph worldMap, final FlatNode fromNode, final FlatNode toNode) {
        super((Object)fromNode, (Object)toNode);
    }
    
    public float getCost() {
        final int x1 = ((FlatNode)this.getToNode()).x;
        final int x2 = ((FlatNode)this.getFromNode()).x;
        final int y1 = ((FlatNode)this.getToNode()).y;
        final int y2 = ((FlatNode)this.getFromNode()).y;
        return Vector.distance((float)x1, (float)y1, (float)x2, (float)y2);
    }
}
