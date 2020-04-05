// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.aistuff;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.ai.pfa.SmoothableGraphPath;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;

public class GraphPath<N extends FlatNode> extends DefaultGraphPath<N> implements SmoothableGraphPath<N, Vector2>
{
    private Vector2 tmpPosition;
    
    public GraphPath() {
        this.tmpPosition = new Vector2();
    }
    
    public Vector2 getNodePosition(final int index) {
        final N node = this.nodes.get(index);
        return this.tmpPosition.set((float)node.x, (float)node.y);
    }
    
    public void swapNodes(final int index1, final int index2) {
        this.nodes.set(index1, this.nodes.get(index2));
    }
    
    public void truncatePath(final int newLength) {
        this.nodes.truncate(newLength);
    }
}
