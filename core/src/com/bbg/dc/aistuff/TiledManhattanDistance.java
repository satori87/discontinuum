// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.aistuff;

import com.badlogic.gdx.ai.pfa.Heuristic;

public class TiledManhattanDistance<N extends FlatNode> implements Heuristic<N>
{
    public float estimate(final N node, final N endNode) {
        return (float)(Math.abs(endNode.x - node.x) + Math.abs(endNode.y - node.y));
    }
}
