// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

import com.bbg.dc.aistuff.Box2dSteeringUtils;
import com.bbg.dc.aistuff.Box2dLocation;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.bbg.dc.aistuff.FlatGraph;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.bbg.dc.aistuff.MapNode;
import java.util.Iterator;
import com.bbg.dc.aistuff.Vector;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.bbg.dc.aistuff.Box2dRadiusProximity;
import com.badlogic.gdx.ai.steer.utils.RayConfiguration;
import com.bbg.dc.aistuff.Box2dRaycastCollisionDetector;
import com.badlogic.gdx.ai.steer.utils.rays.CentralRayWithWhiskersConfiguration;
import com.badlogic.gdx.ai.steer.utils.rays.ParallelSideRayConfiguration;
import com.badlogic.gdx.ai.steer.utils.rays.SingleRayConfiguration;
import com.bbg.dc.aistuff.FlatNode;
import com.bbg.dc.aistuff.GraphPath;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.RaycastObstacleAvoidance;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;
import com.badlogic.gdx.ai.steer.utils.rays.RayConfigurationBase;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.Hide;
import com.badlogic.gdx.ai.steer.behaviors.Flee;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.ai.steer.Steerable;

public class EntityAIManager implements Steerable<Vector2>
{
    DCGame game;
    Map map;
    AIManager aiMan;
    Entity e;
    public Entity pursuit;
    public Target target;
    public long pathTick;
    public int pathX;
    public int pathY;
    public boolean pathing;
    public boolean wandering;
    public boolean pursuing;
    boolean tagged;
    public Entity fleeFrom;
    public boolean hasFleePath;
    public boolean inPursuitRange;
    Seek<Vector2> seekSB;
    Pursue<Vector2> pursueSB;
    Flee<Vector2> fleeSB;
    Hide<Vector2> hideSB;
    BlendedSteering<Vector2> blendedSeek;
    BlendedSteering<Vector2> blendedPursue;
    public long pathAt;
    public RayConfigurationBase<Vector2>[] rayConfig;
    public RaycastCollisionDetector<Vector2> raycastCollisionDetector;
    public RaycastObstacleAvoidance<Vector2> raycastTight;
    public RaycastObstacleAvoidance<Vector2> raycastLoose;
    int targetX;
    int targetY;
    int x500;
    int y500;
    long t500;
    public boolean pathCast;
    int lastFirst;
    int lastLast;
    int lastPX;
    int lastPY;
    public boolean recalculatePath;
    public boolean fleeing;
    SteeringBehavior<Vector2> steeringBehavior;
    GraphPath<FlatNode> path;
    float lastDir;
    float curDir;
    int nextDir;
    public static final int castW = 8;
    
    public EntityAIManager(final DCGame game, final Map map, final AIManager aiMan, final Entity e) {
        this.pathTick = 0L;
        this.pathX = 2040;
        this.pathY = 2040;
        this.pathing = false;
        this.wandering = false;
        this.pursuing = false;
        this.tagged = false;
        this.fleeFrom = null;
        this.hasFleePath = false;
        this.inPursuitRange = false;
        this.pathAt = 0L;
        this.targetX = -1;
        this.targetY = -1;
        this.x500 = 0;
        this.y500 = 0;
        this.t500 = 0L;
        this.pathCast = false;
        this.lastFirst = -100;
        this.lastLast = -100;
        this.lastPX = -1;
        this.lastPY = -1;
        this.recalculatePath = false;
        this.fleeing = false;
        this.path = new GraphPath<FlatNode>();
        this.lastDir = 0.0f;
        this.curDir = 0.0f;
        this.nextDir = 0;
        this.game = game;
        this.map = map;
        this.aiMan = aiMan;
        this.e = e;
        this.pathing = false;
        this.pursuing = false;
        e.body.setUserData((Object)this);
        if (!(e instanceof Target)) {
            this.rayConfig = (RayConfigurationBase<Vector2>[])new RayConfigurationBase[] { (RayConfigurationBase)new SingleRayConfiguration((Steerable)this, 4.0f), (RayConfigurationBase)new ParallelSideRayConfiguration((Steerable)this, 0.32f, 0.16f), (RayConfigurationBase)new CentralRayWithWhiskersConfiguration((Steerable)this, 0.25f, 0.1f, 0.61086524f) };
            this.raycastCollisionDetector = (RaycastCollisionDetector<Vector2>)new Box2dRaycastCollisionDetector(e.body.getWorld(), e);
            this.raycastLoose = (RaycastObstacleAvoidance<Vector2>)new RaycastObstacleAvoidance((Steerable)this, (RayConfiguration)this.rayConfig[1], (RaycastCollisionDetector)this.raycastCollisionDetector, 0.16f);
            this.raycastTight = (RaycastObstacleAvoidance<Vector2>)new RaycastObstacleAvoidance((Steerable)this, (RayConfiguration)this.rayConfig[1], (RaycastCollisionDetector)this.raycastCollisionDetector, 0.16f);
            final Box2dRadiusProximity proximity = new Box2dRadiusProximity((Steerable<Vector2>)this, map.world, 0.2f);
            final CollisionAvoidance<Vector2> colSB = (CollisionAvoidance<Vector2>)new CollisionAvoidance((Steerable)this, (Proximity)proximity);
            this.target = new Target(game, map, aiMan, 0, 0);
            this.seekSB = (Seek<Vector2>)new Seek((Steerable)this, (Location)this.target.aiMan);
            this.pursueSB = (Pursue<Vector2>)new Pursue((Steerable)this, (Steerable)this.target.aiMan);
            this.blendedSeek = (BlendedSteering<Vector2>)new BlendedSteering((Steerable)this).add((SteeringBehavior)this.seekSB, 1.0f).add((SteeringBehavior)this.raycastLoose, 0.3f);
            this.blendedPursue = (BlendedSteering<Vector2>)new BlendedSteering((Steerable)this).add((SteeringBehavior)this.pursueSB, 1.0f).add((SteeringBehavior)colSB, 1.0f);
            this.steeringBehavior = (SteeringBehavior<Vector2>)this.blendedSeek;
        }
    }
    
    public boolean canAttack() {
        return this.pursuing && this.inPursuitRange;
    }
    
    public boolean validPursuit() {
        if (this.pursuit == null) {
            this.pursuing = false;
            return this.pathing = false;
        }
        return true;
    }
    
    public void flee(final Entity attacker) {
        this.fleeing = true;
        this.hasFleePath = false;
        this.pursuing = false;
        this.pathing = true;
        this.fleeFrom = attacker;
    }
    
    void arrive() {
        if (this.wandering) {
            this.wander();
        }
        else {
            this.pathing = false;
            this.e.moving = false;
            this.e.move(this.pathX - this.e.doll.getBodyX(), this.pathY - this.e.doll.getBodyY());
            this.e.body.setLinearVelocity(new Vector2(0.0f, 0.0f));
            this.path = new GraphPath<FlatNode>();
        }
    }
    
    void tryToFindSafeNode() {
        if (this.hasFleePath && Vector.distance((float)this.fleeFrom.getTrueX(), (float)this.fleeFrom.getTrueY(), (float)this.pathX, (float)this.pathY) < 128.0f) {
            this.hasFleePath = false;
        }
        if (!this.hasFleePath) {
            final Vector2 v = this.randomSpotAwayFrom(this.fleeFrom);
            this.pathX = (int)v.x;
            this.pathY = (int)v.y;
            this.pathing = true;
            this.recalculatePath = true;
            this.hasFleePath = true;
        }
    }
    
    void calculateSteering() {
        final Player p = this.map.player;
        if (!(this.e instanceof Player) && this.fleeing) {
            if (this.fleeFrom.dead) {
                this.fleeing = false;
                this.pathing = false;
            }
            else if (Vector.distance((float)p.getTrueX(), (float)p.getTrueY(), (float)this.e.getTrueX(), (float)this.e.getTrueY()) < 160.0f) {
                this.tryToFindSafeNode();
            }
            else {
                this.pathing = false;
            }
        }
        if (!this.fleeing && this.e instanceof Monster && !this.e.friendly && this.e.canFight && Vector.distance((float)this.e.getTrueX(), (float)this.e.getTrueY(), (float)p.getTrueX(), (float)p.getTrueY()) < this.e.sight) {
            this.pursue(p);
        }
        if (this.pathing && this.game.tick > this.pathTick) {
            if (this.pursuing) {
                if (this.pursuit != null && this.pursuit.dead) {
                    this.pursuit = null;
                    this.pursuing = false;
                    this.pathing = false;
                    this.e.attacking = false;
                    return;
                }
                this.pathX = this.pursuit.x + this.pursuit.doll.getBodyX();
                this.pathY = this.pursuit.y + this.pursuit.doll.getBodyY();
            }
            float pathD = 0.0f;
            if (this.wandering && this.game.tick > this.t500) {
                this.t500 = this.game.tick + 500L;
                if (Math.abs(Math.hypot(this.x500 - this.e.x, this.y500 - this.e.y)) < this.e.speed * 5.0f) {
                    this.wander();
                    this.pathTick = this.game.tick - 10L;
                }
                this.x500 = this.e.x;
                this.y500 = this.e.y;
            }
            if (this.game.tick > this.pathAt && (this.aiMan.generalPathCount < 5 || this.e instanceof Player)) {
                if (!(this.e instanceof Player)) {
                    final AIManager aiMan = this.aiMan;
                    ++aiMan.generalPathCount;
                    this.pathAt = this.game.tick + 200L;
                }
                else {
                    this.pathAt = this.game.tick + 100L;
                }
                if (this.e.getTrueX() == this.pathX && this.e.getTrueY() == this.pathY) {
                    this.arrive();
                    return;
                }
                pathD = Vector.distance((float)this.e.getTrueX(), (float)this.e.getTrueY(), (float)this.pathX, (float)this.pathY);
                if (this.pursuing) {
                    final float homeDist = Vector.distance((float)this.pursuit.x, (float)this.pursuit.y, (float)this.e.birthX, (float)this.e.birthY);
                    if ((pathD > this.e.sight || homeDist >= this.e.combatRange) && this.e instanceof Monster) {
                        this.pursuing = false;
                        this.pathing = false;
                        return;
                    }
                    final float attackD = pathD - this.pursuit.radius * 50.0f;
                    if (attackD <= this.e.pursueMaxRange) {
                        this.inPursuitRange = true;
                        this.e.body.setLinearVelocity(new Vector2(0.0f, 0.0f));
                        if (attackD <= this.e.pursueMinRange) {
                            return;
                        }
                    }
                    else if (this.inPursuitRange && attackD >= this.e.pursueMaxRange) {
                        this.inPursuitRange = false;
                    }
                }
                if (!this.pursuing && (pathD <= this.e.speed * 1.5f || ((this.wandering || this.fleeing) && pathD <= 32.0f))) {
                    if (this.fleeing) {
                        this.hasFleePath = false;
                    }
                    else {
                        this.arrive();
                    }
                    return;
                }
                this.targetX = this.pathX - this.e.doll.getBodyX();
                this.targetY = this.pathY - this.e.doll.getBodyY();
                this.calculatePath();
                boolean found = false;
                if (this.path.getCount() > 0) {
                    FlatNode next = (FlatNode)this.path.get(0);
                    if (this.pathCast) {
                        int c = 0;
                        float nextD = 0.0f;
                        for (final FlatNode fn : this.path) {
                            if (c >= 0) {
                                nextD = Vector.distance((float)fn.getTrueX(), (float)fn.getTrueY(), (float)this.e.getTrueX(), (float)this.e.getTrueY());
                                if (nextD > 0.0f && nextD <= 128.0f && !this.aiMan.raycast((float)fn.getTrueX(), (float)fn.getTrueY(), (float)this.e.getTrueX(), (float)this.e.getTrueY(), 8.0f)) {
                                    next = fn;
                                    found = true;
                                }
                            }
                            ++c;
                        }
                        if (!found) {
                            this.recalculatePath = true;
                            this.calculatePath();
                        }
                        this.targetX = next.getTrueX();
                        this.targetY = next.getTrueY();
                    }
                }
                this.target.move(this.targetX, this.targetY);
                if (this.pursuing && !this.pathCast) {
                    this.steeringBehavior = (SteeringBehavior<Vector2>)this.blendedPursue;
                }
                else {
                    this.steeringBehavior = (SteeringBehavior<Vector2>)this.blendedSeek;
                }
            }
        }
        else if (!this.wandering && !(this.e instanceof Player)) {
            this.wander();
        }
    }
    
    void wander() {
        this.wandering = true;
        final Vector2 wander = this.getRandomWander();
        if (wander.x >= 0.0f && wander.y >= 0.0f) {
            this.pathX = (int)wander.x;
            this.pathY = (int)wander.y;
            this.pathing = true;
            if (!this.e.doll.flies()) {
                this.pathTick = this.game.tick + 700L + AssetLoader.rndInt(2500);
            }
            else {
                this.pathTick = this.game.tick - 10L;
            }
            this.e.body.setLinearVelocity(new Vector2(0.0f, 0.0f));
        }
    }
    
    MapNode closestNode(final int x, final int y) {
        float d = 9999.0f;
        float newd = 0.0f;
        MapNode c = null;
        for (int nx = x / 32 - 4; nx <= x / 32 + 4; ++nx) {
            for (int ny = y / 32 - 4; ny <= y / 32 + 4; ++ny) {
                if (nx >= 0 && ny >= 0 && nx < 64 && ny < 64) {
                    final MapNode m = this.aiMan.graph.mapNodes.get(nx * 64 + ny);
                    if (m != null && m.active && m.connections.size() > 0) {
                        newd = Vector.distance((float)x, (float)y, (float)m.getTrueX(), (float)m.getTrueY());
                        if (newd < d && newd > 0.0f && !this.aiMan.raycast((float)x, (float)y, (float)m.getTrueX(), (float)m.getTrueY(), 8.0f)) {
                            c = m;
                            d = newd;
                        }
                    }
                }
            }
        }
        if (c == null) {
            System.out.println("closestnode failure");
        }
        return c;
    }
    
    private Vector2 randomSpotAwayFrom(final Entity e) {
        Vector2 v = new Vector2(-1.0f, -1.0f);
        int x = e.x + 200 - AssetLoader.rndInt(400);
        int y = e.y + 200 - AssetLoader.rndInt(400);
        final boolean out = false;
        int help = 0;
        do {
            ++help;
            v = this.map.getValidSpot(x, y);
            if (v.x >= 0.0f && v.y >= 0.0f) {
                if (Vector.distance(v.x, v.y, (float)e.x, (float)e.y) > 128.0f) {
                    return v;
                }
                x = e.x + 200 - AssetLoader.rndInt(400);
                y = e.y + 200 - AssetLoader.rndInt(400);
            }
            else {
                x = e.x + 200 - AssetLoader.rndInt(400);
                y = e.y + 200 - AssetLoader.rndInt(400);
            }
        } while (!out && help < 1000);
        if (help >= 80) {
            System.out.println("randomspotawayfrom help:" + help);
        }
        return v;
    }
    
    private void applySteering() {
        if (this.pathing && this.game.tick > this.pathTick && this.steeringBehavior != null) {
            Vector vd = Vector.byChange((float)(this.targetX - this.e.x), (float)(this.targetY - this.e.y));
            if (this.pursuing) {
                vd = Vector.byChange((float)(this.pursuit.getTrueX() - this.e.getTrueX()), (float)(this.pursuit.getTrueY() - this.e.getTrueY()));
            }
            if (vd.direction >= 5.497787f || vd.direction <= 0.7853982f) {
                this.nextDir = 0;
            }
            else if (vd.direction >= 3.9269907f) {
                this.nextDir = 1;
            }
            else if (vd.direction >= 2.3561945f) {
                this.nextDir = 2;
            }
            else {
                this.nextDir = 3;
            }
            this.lastDir = this.curDir;
            this.curDir = vd.direction;
            if (this.e.attacking) {
                this.e.dir = this.nextDir;
                this.e.dirStamp = this.game.tick + 400L;
                return;
            }
            if (this.pursuing && this.inPursuitRange) {
                return;
            }
            final SteeringAcceleration<Vector2> steeringOutput = (SteeringAcceleration<Vector2>)new SteeringAcceleration((com.badlogic.gdx.math.Vector)new Vector2());
            this.steeringBehavior.calculateSteering((SteeringAcceleration)steeringOutput);
            if (steeringOutput != null && !((Vector2)steeringOutput.linear).isZero()) {
                this.e.body.applyForceToCenter((Vector2)steeringOutput.linear, true);
            }
        }
    }
    
    public void pursue(final Entity e) {
        if (this.pursuing && e == this.pursuit) {
            return;
        }
        if (this.fleeing) {
            return;
        }
        if (e instanceof Monster) {
            final float homedist = Vector.distance((float)e.x, (float)e.y, (float)e.birthX, (float)e.birthY);
            if (homedist >= e.combatRange) {
                return;
            }
        }
        if (e instanceof Monster && !e.canFight) {
            return;
        }
        this.pursuit = e;
        this.wandering = false;
        this.pathTick = 0L;
        this.pathAt = 0L;
        this.pathing = true;
        this.pursuing = true;
        this.target.move(e.x, e.y);
    }
    
    private void calculatePath() {
        final int x = this.e.getTrueX();
        final int y = this.e.getTrueY();
        if (Vector.distance((float)x, (float)y, (float)this.pathX, (float)this.pathY) <= 0.0f) {
            this.arrive();
            return;
        }
        this.pathCast = this.aiMan.raycast((float)x, (float)y, (float)this.pathX, (float)this.pathY, 8.0f);
        if ((x != this.pathX || y != this.pathY) && !this.pathCast) {
            return;
        }
        if (this.pathX != this.lastPX || this.pathY != this.lastPY || this.e instanceof Player) {}
        if (this.e instanceof Player || this.aiMan.canCalculateNodePath()) {
            this.recalculatePath = false;
            this.lastPX = this.pathX;
            this.lastPY = this.pathY;
            FlatNode first = null;
            final MapNode close = this.closestNode(x, y);
            if (close != null) {
                first = this.aiMan.nodeGraph.getNode(close.index);
            }
            else {
                System.out.println("firstnode is null");
            }
            FlatNode last = null;
            final MapNode lastNode = this.closestNode(this.pathX, this.pathY);
            if (lastNode != null) {
                last = this.aiMan.nodeGraph.getNode(lastNode.index);
            }
            else {
                System.out.println("lastnode is null");
            }
            if (first != null && last != null) {
                if (last.index != this.lastLast) {
                    final AIManager aiMan = this.map.aiMan;
                    ++aiMan.calculatedPathCount;
                    this.path.clear();
                    this.aiMan.pathFinder.searchNodePath((Object)first, (Object)last, (Heuristic)FlatGraph.heuristic, (com.badlogic.gdx.ai.pfa.GraphPath)this.path);
                }
                this.lastFirst = first.index;
                this.lastLast = last.index;
            }
            else {
                if (this.wandering) {
                    this.wander();
                }
                else {
                    System.out.println("player calculate path problem");
                }
                System.out.println("calculate path problems");
            }
        }
        else {
            this.recalculatePath = true;
        }
    }
    
    private Vector2 getRandomWander() {
        final boolean out = false;
        Vector v = null;
        float r = 0.0f;
        float d = 16.0f;
        final int pX = this.e.x + AssetLoader.rndInt(448) - 224;
        final int pY = this.e.y + AssetLoader.rndInt(448) - 224;
        int vX = pX;
        int vY = pY;
        int help = 0;
        do {
            ++help;
            if (vX >= 0 && vY >= 0 && vX < 2048 && vY < 2048 && this.map.checkSpace(vX, vY) && (Vector.distance((float)this.e.birthX, (float)this.e.birthY, (float)vX, (float)vY) < this.e.wanderRange || this.fleeing)) {
                return new Vector2((float)vX, (float)vY);
            }
            r += (float)0.7853981633974483;
            if (r >= 6.283185307179586) {
                r = 0.0f;
                d += 4.0f;
            }
            v = new Vector(r, d);
            vX = (int)(pX + v.xChange);
            vY = (int)(pY + v.yChange);
        } while (!out && help < 1000);
        if (help >= 200) {
            System.out.println("randomwander help: " + help);
        }
        return new Vector2(-1.0f, -1.0f);
    }
    
    public void setPath(final int vX, final int vY) {
        this.pathX = vX;
        this.pathY = vY;
        this.pathing = true;
        this.pursuing = false;
    }
    
    public void update() {
        if (!this.pursuing) {
            this.pursuit = null;
        }
        this.calculateSteering();
        this.applySteering();
    }
    
    public Vector2 angleToVector(final Vector2 outVector, final float angle) {
        outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
    }
    
    public float getOrientation() {
        return this.e.body.getAngle();
    }
    
    public Vector2 getPosition() {
        return this.e.body.getPosition();
    }
    
    public Location<Vector2> newLocation() {
        return (Location<Vector2>)new Box2dLocation();
    }
    
    public void setOrientation(final float orientation) {
        this.e.body.setTransform(this.getPosition(), orientation);
    }
    
    public Vector2 getLinearVelocity() {
        return this.e.body.getLinearVelocity();
    }
    
    public float getAngularVelocity() {
        return this.e.body.getAngularVelocity();
    }
    
    public float getBoundingRadius() {
        return this.e.radius;
    }
    
    public boolean isTagged() {
        return this.tagged;
    }
    
    public void setTagged(final boolean tagged) {
        this.tagged = tagged;
    }
    
    public float getMaxLinearSpeed() {
        return 1.0f;
    }
    
    public void setMaxLinearSpeed(final float maxLinearSpeed) {
    }
    
    public float getMaxLinearAcceleration() {
        return this.e.getAcceleration();
    }
    
    public void setMaxLinearAcceleration(final float maxLinearAcceleration) {
    }
    
    public float getMaxAngularSpeed() {
        return 1.0f;
    }
    
    public void setMaxAngularSpeed(final float maxAngularSpeed) {
    }
    
    public float getMaxAngularAcceleration() {
        return 1.0f;
    }
    
    public void setMaxAngularAcceleration(final float maxAngularAcceleration) {
    }
    
    public float getZeroLinearSpeedThreshold() {
        return 1.0E-7f;
    }
    
    public void setZeroLinearSpeedThreshold(final float value) {
        throw new UnsupportedOperationException();
    }
    
    public float vectorToAngle(final Vector2 vector) {
        return Box2dSteeringUtils.vectorToAngle(vector);
    }
}
