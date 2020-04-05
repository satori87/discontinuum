// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.aistuff;

import java.util.Iterator;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;

public class FlatGraph implements IndexedGraph<FlatNode>
{
    public static final int sizeX = 64;
    public static final int sizeY = 64;
    public Array<FlatNode> nodes;
    public FlatNode startNode;
    public static TiledManhattanDistance<FlatNode> heuristic;
    
    static {
        FlatGraph.heuristic = new TiledManhattanDistance<FlatNode>();
    }
    
    public FlatGraph() {
        this.nodes = new Array<FlatNode>(4096);
        for (int i = 0; i < 4096; ++i) {
            this.nodes.add(null);
        }
        this.startNode = null;
    }
    
    public void init(final Array<MapNode> mapNodes) {
        for (final MapNode m : mapNodes) {
            if (m != null && m.active) {
                final FlatNode fn = new FlatNode(m.index, m.x, m.y);
                this.nodes.set(m.index, fn);
            }
        }
        for (final MapNode m : mapNodes) {
            if (m != null && m.active) {
                for (final Integer c : m.connections) {
                    final FlatNode fn2 = this.getNode(m.index);
                    final FlatNode cn = this.getNode(c);
                    if (fn2.connections == null) {
                        fn2.connections = new Array<Connection<FlatNode>>();
                    }
                    fn2.getConnections().add((Connection<FlatNode>)new FlatConnection(this, fn2, cn));
                }
            }
        }
    }
    
    public FlatNode getNode(final int index) {
        return this.nodes.get(index);
    }
    
    public int getIndex(final FlatNode node) {
        if (node != null) {
            return node.getIndex();
        }
        return 0;
    }
    
    public int getNodeCount() {
        return this.nodes.size;
    }
    
    public Array<Connection<FlatNode>> getConnections(final FlatNode fromNode) {
        return fromNode.getConnections();
    }
}
