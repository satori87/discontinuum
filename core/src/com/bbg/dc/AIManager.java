// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

import com.bbg.dc.iface.Vector;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.bbg.dc.aistuff.MapNode;
import com.badlogic.gdx.utils.Array;
import com.bbg.dc.aistuff.Wall;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.bbg.dc.aistuff.MapGraph;
import com.bbg.dc.aistuff.FlatGraph;
import com.bbg.dc.aistuff.FlatNode;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;

public class AIManager
{
    public DCGame game;
    public Map map;
    public IndexedAStarPathFinder<FlatNode> pathFinder;
    public FlatGraph nodeGraph;
    public MapGraph graph;
    public boolean foundCollision;
    public int generalPathCount;
    public int calculatedPathCount;
    RayCastCallback raycastJustWalls;
    
    public AIManager(final DCGame game, final Map map) {
        this.foundCollision = false;
        this.generalPathCount = 0;
        this.calculatedPathCount = 0;
        this.raycastJustWalls = (RayCastCallback)new RayCastCallback() {
            public float reportRayFixture(final Fixture fixture, final Vector2 point, final Vector2 normal, final float fraction) {
                if (fixture.getBody().getType() == BodyDef.BodyType.StaticBody) {
                    if (fixture.getBody().getUserData() instanceof Wall) {
                        final Wall w = (Wall)fixture.getBody().getUserData();
                        AIManager.this.foundCollision = !w.boundary;
                    }
                    else {
                        AIManager.this.foundCollision = true;
                    }
                }
                return 1.0f;
            }
        };
        this.game = game;
        this.map = map;
        this.nodeGraph = new FlatGraph();
        this.graph = game.mapScene.mapGraph[map.id];
    }
    
    public void start() {
        this.nodeGraph = new FlatGraph();
        final Array<MapNode> nodes = new Array<MapNode>(4096);
        for (int x = 0; x < 64; ++x) {
            for (int y = 0; y < 64; ++y) {
                nodes.add(this.graph.mapNodes.get(x * 64 + y));
            }
        }
        this.nodeGraph.init(nodes);
        this.pathFinder = (IndexedAStarPathFinder<FlatNode>)new IndexedAStarPathFinder((IndexedGraph)this.nodeGraph, false);
    }
    
    public void update() {
        GdxAI.getTimepiece().update(this.game.deltaT);
        this.generalPathCount = 0;
        this.calculatedPathCount = 0;
    }
    
    public boolean canCalculateNodePath() {
        return this.map.aiMan.calculatedPathCount < 2;
    }
    
    public boolean canCalculatePath() {
        return this.map.aiMan.generalPathCount < 6;
    }
    
    public boolean raycast(final float x1, final float y1, final float x2, final float y2, final float w) {
        this.foundCollision = false;
        final Vector v = Vector.byChange(x2 - x1, y2 - y1);
        this.map.world.rayCast(this.raycastJustWalls, x1 / 100.0f, y1 / 100.0f, x2 / 100.0f, y2 / 100.0f);
        if (!this.foundCollision) {
            Vector v2 = new Vector(v.direction + 1.5707964f, w);
            this.map.world.rayCast(this.raycastJustWalls, (x1 + v2.xChange) / 100.0f, (y1 + v2.yChange) / 100.0f, (x2 + v2.xChange) / 100.0f, (y2 + v2.yChange) / 100.0f);
            if (!this.foundCollision) {
                v2 = new Vector(v.direction - 1.5707964f, w);
                this.map.world.rayCast(this.raycastJustWalls, (x1 + v2.xChange) / 100.0f, (y1 + v2.yChange) / 100.0f, (x2 + v2.xChange) / 100.0f, (y2 + v2.yChange) / 100.0f);
            }
        }
        return this.foundCollision;
    }
}
