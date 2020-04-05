// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d;

import java.util.Iterator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;

public class Group extends Actor implements Cullable
{
    private static final Vector2 tmp;
    final SnapshotArray<Actor> children;
    private final Affine2 worldTransform;
    private final Matrix4 computedTransform;
    private final Matrix4 oldTransform;
    boolean transform;
    private Rectangle cullingArea;
    
    static {
        tmp = new Vector2();
    }
    
    public Group() {
        this.children = new SnapshotArray<Actor>(true, 4, Actor.class);
        this.worldTransform = new Affine2();
        this.computedTransform = new Matrix4();
        this.oldTransform = new Matrix4();
        this.transform = true;
    }
    
    @Override
    public void act(final float delta) {
        super.act(delta);
        final Actor[] actors = this.children.begin();
        for (int i = 0, n = this.children.size; i < n; ++i) {
            actors[i].act(delta);
        }
        this.children.end();
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        if (this.transform) {
            this.applyTransform(batch, this.computeTransform());
        }
        this.drawChildren(batch, parentAlpha);
        if (this.transform) {
            this.resetTransform(batch);
        }
    }
    
    protected void drawChildren(final Batch batch, float parentAlpha) {
        parentAlpha *= this.color.a;
        final SnapshotArray<Actor> children = this.children;
        final Actor[] actors = children.begin();
        final Rectangle cullingArea = this.cullingArea;
        if (cullingArea != null) {
            final float cullLeft = cullingArea.x;
            final float cullRight = cullLeft + cullingArea.width;
            final float cullBottom = cullingArea.y;
            final float cullTop = cullBottom + cullingArea.height;
            if (this.transform) {
                for (int i = 0, n = children.size; i < n; ++i) {
                    final Actor child = actors[i];
                    if (child.isVisible()) {
                        final float cx = child.x;
                        final float cy = child.y;
                        if (cx <= cullRight && cy <= cullTop && cx + child.width >= cullLeft && cy + child.height >= cullBottom) {
                            child.draw(batch, parentAlpha);
                        }
                    }
                }
            }
            else {
                final float offsetX = this.x;
                final float offsetY = this.y;
                this.x = 0.0f;
                this.y = 0.0f;
                for (int j = 0, n2 = children.size; j < n2; ++j) {
                    final Actor child2 = actors[j];
                    if (child2.isVisible()) {
                        final float cx2 = child2.x;
                        final float cy2 = child2.y;
                        if (cx2 <= cullRight && cy2 <= cullTop && cx2 + child2.width >= cullLeft && cy2 + child2.height >= cullBottom) {
                            child2.x = cx2 + offsetX;
                            child2.y = cy2 + offsetY;
                            child2.draw(batch, parentAlpha);
                            child2.x = cx2;
                            child2.y = cy2;
                        }
                    }
                }
                this.x = offsetX;
                this.y = offsetY;
            }
        }
        else if (this.transform) {
            for (int k = 0, n3 = children.size; k < n3; ++k) {
                final Actor child3 = actors[k];
                if (child3.isVisible()) {
                    child3.draw(batch, parentAlpha);
                }
            }
        }
        else {
            final float offsetX2 = this.x;
            final float offsetY2 = this.y;
            this.x = 0.0f;
            this.y = 0.0f;
            for (int l = 0, n4 = children.size; l < n4; ++l) {
                final Actor child4 = actors[l];
                if (child4.isVisible()) {
                    final float cx3 = child4.x;
                    final float cy3 = child4.y;
                    child4.x = cx3 + offsetX2;
                    child4.y = cy3 + offsetY2;
                    child4.draw(batch, parentAlpha);
                    child4.x = cx3;
                    child4.y = cy3;
                }
            }
            this.x = offsetX2;
            this.y = offsetY2;
        }
        children.end();
    }
    
    @Override
    public void drawDebug(final ShapeRenderer shapes) {
        this.drawDebugBounds(shapes);
        if (this.transform) {
            this.applyTransform(shapes, this.computeTransform());
        }
        this.drawDebugChildren(shapes);
        if (this.transform) {
            this.resetTransform(shapes);
        }
    }
    
    protected void drawDebugChildren(final ShapeRenderer shapes) {
        final SnapshotArray<Actor> children = this.children;
        final Actor[] actors = children.begin();
        if (this.transform) {
            for (int i = 0, n = children.size; i < n; ++i) {
                final Actor child = actors[i];
                if (child.isVisible()) {
                    if (child.getDebug() || child instanceof Group) {
                        child.drawDebug(shapes);
                    }
                }
            }
            shapes.flush();
        }
        else {
            final float offsetX = this.x;
            final float offsetY = this.y;
            this.x = 0.0f;
            this.y = 0.0f;
            for (int j = 0, n2 = children.size; j < n2; ++j) {
                final Actor child2 = actors[j];
                if (child2.isVisible()) {
                    if (child2.getDebug() || child2 instanceof Group) {
                        final float cx = child2.x;
                        final float cy = child2.y;
                        child2.x = cx + offsetX;
                        child2.y = cy + offsetY;
                        child2.drawDebug(shapes);
                        child2.x = cx;
                        child2.y = cy;
                    }
                }
            }
            this.x = offsetX;
            this.y = offsetY;
        }
        children.end();
    }
    
    protected Matrix4 computeTransform() {
        final Affine2 worldTransform = this.worldTransform;
        final float originX = this.originX;
        final float originY = this.originY;
        worldTransform.setToTrnRotScl(this.x + originX, this.y + originY, this.rotation, this.scaleX, this.scaleY);
        if (originX != 0.0f || originY != 0.0f) {
            worldTransform.translate(-originX, -originY);
        }
        Group parentGroup;
        for (parentGroup = this.parent; parentGroup != null && !parentGroup.transform; parentGroup = parentGroup.parent) {}
        if (parentGroup != null) {
            worldTransform.preMul(parentGroup.worldTransform);
        }
        this.computedTransform.set(worldTransform);
        return this.computedTransform;
    }
    
    protected void applyTransform(final Batch batch, final Matrix4 transform) {
        this.oldTransform.set(batch.getTransformMatrix());
        batch.setTransformMatrix(transform);
    }
    
    protected void resetTransform(final Batch batch) {
        batch.setTransformMatrix(this.oldTransform);
    }
    
    protected void applyTransform(final ShapeRenderer shapes, final Matrix4 transform) {
        this.oldTransform.set(shapes.getTransformMatrix());
        shapes.setTransformMatrix(transform);
    }
    
    protected void resetTransform(final ShapeRenderer shapes) {
        shapes.setTransformMatrix(this.oldTransform);
    }
    
    @Override
    public void setCullingArea(final Rectangle cullingArea) {
        this.cullingArea = cullingArea;
    }
    
    public Rectangle getCullingArea() {
        return this.cullingArea;
    }
    
    @Override
    public Actor hit(final float x, final float y, final boolean touchable) {
        if (touchable && this.getTouchable() == Touchable.disabled) {
            return null;
        }
        if (!this.isVisible()) {
            return null;
        }
        final Vector2 point = Group.tmp;
        final Actor[] childrenArray = this.children.items;
        for (int i = this.children.size - 1; i >= 0; --i) {
            final Actor child = childrenArray[i];
            child.parentToLocalCoordinates(point.set(x, y));
            final Actor hit = child.hit(point.x, point.y, touchable);
            if (hit != null) {
                return hit;
            }
        }
        return super.hit(x, y, touchable);
    }
    
    protected void childrenChanged() {
    }
    
    public void addActor(final Actor actor) {
        if (actor.parent != null) {
            if (actor.parent == this) {
                return;
            }
            actor.parent.removeActor(actor, false);
        }
        this.children.add(actor);
        actor.setParent(this);
        actor.setStage(this.getStage());
        this.childrenChanged();
    }
    
    public void addActorAt(final int index, final Actor actor) {
        if (actor.parent != null) {
            if (actor.parent == this) {
                return;
            }
            actor.parent.removeActor(actor, false);
        }
        if (index >= this.children.size) {
            this.children.add(actor);
        }
        else {
            this.children.insert(index, actor);
        }
        actor.setParent(this);
        actor.setStage(this.getStage());
        this.childrenChanged();
    }
    
    public void addActorBefore(final Actor actorBefore, final Actor actor) {
        if (actor.parent != null) {
            if (actor.parent == this) {
                return;
            }
            actor.parent.removeActor(actor, false);
        }
        final int index = this.children.indexOf(actorBefore, true);
        this.children.insert(index, actor);
        actor.setParent(this);
        actor.setStage(this.getStage());
        this.childrenChanged();
    }
    
    public void addActorAfter(final Actor actorAfter, final Actor actor) {
        if (actor.parent != null) {
            if (actor.parent == this) {
                return;
            }
            actor.parent.removeActor(actor, false);
        }
        final int index = this.children.indexOf(actorAfter, true);
        if (index == this.children.size) {
            this.children.add(actor);
        }
        else {
            this.children.insert(index + 1, actor);
        }
        actor.setParent(this);
        actor.setStage(this.getStage());
        this.childrenChanged();
    }
    
    public boolean removeActor(final Actor actor) {
        return this.removeActor(actor, true);
    }
    
    public boolean removeActor(final Actor actor, final boolean unfocus) {
        if (!this.children.removeValue(actor, true)) {
            return false;
        }
        if (unfocus) {
            final Stage stage = this.getStage();
            if (stage != null) {
                stage.unfocus(actor);
            }
        }
        actor.setParent(null);
        actor.setStage(null);
        this.childrenChanged();
        return true;
    }
    
    public void clearChildren() {
        final Actor[] actors = this.children.begin();
        for (int i = 0, n = this.children.size; i < n; ++i) {
            final Actor child = actors[i];
            child.setStage(null);
            child.setParent(null);
        }
        this.children.end();
        this.children.clear();
        this.childrenChanged();
    }
    
    @Override
    public void clear() {
        super.clear();
        this.clearChildren();
    }
    
    public <T extends Actor> T findActor(final String name) {
        final Array<Actor> children = this.children;
        for (int i = 0, n = children.size; i < n; ++i) {
            if (name.equals(children.get(i).getName())) {
                return (T)children.get(i);
            }
        }
        for (int i = 0, n = children.size; i < n; ++i) {
            final Actor child = children.get(i);
            if (child instanceof Group) {
                final Actor actor = ((Group)child).findActor(name);
                if (actor != null) {
                    return (T)actor;
                }
            }
        }
        return null;
    }
    
    @Override
    protected void setStage(final Stage stage) {
        super.setStage(stage);
        final Actor[] childrenArray = this.children.items;
        for (int i = 0, n = this.children.size; i < n; ++i) {
            childrenArray[i].setStage(stage);
        }
    }
    
    public boolean swapActor(final int first, final int second) {
        final int maxIndex = this.children.size;
        if (first < 0 || first >= maxIndex) {
            return false;
        }
        if (second < 0 || second >= maxIndex) {
            return false;
        }
        this.children.swap(first, second);
        return true;
    }
    
    public boolean swapActor(final Actor first, final Actor second) {
        final int firstIndex = this.children.indexOf(first, true);
        final int secondIndex = this.children.indexOf(second, true);
        if (firstIndex == -1 || secondIndex == -1) {
            return false;
        }
        this.children.swap(firstIndex, secondIndex);
        return true;
    }
    
    public SnapshotArray<Actor> getChildren() {
        return this.children;
    }
    
    public boolean hasChildren() {
        return this.children.size > 0;
    }
    
    public void setTransform(final boolean transform) {
        this.transform = transform;
    }
    
    public boolean isTransform() {
        return this.transform;
    }
    
    public Vector2 localToDescendantCoordinates(final Actor descendant, final Vector2 localCoords) {
        final Group parent = descendant.parent;
        if (parent == null) {
            throw new IllegalArgumentException("Child is not a descendant: " + descendant);
        }
        if (parent != this) {
            this.localToDescendantCoordinates(parent, localCoords);
        }
        descendant.parentToLocalCoordinates(localCoords);
        return localCoords;
    }
    
    public void setDebug(final boolean enabled, final boolean recursively) {
        this.setDebug(enabled);
        if (recursively) {
            for (final Actor child : this.children) {
                if (child instanceof Group) {
                    ((Group)child).setDebug(enabled, recursively);
                }
                else {
                    child.setDebug(enabled);
                }
            }
        }
    }
    
    public Group debugAll() {
        this.setDebug(true, true);
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(128);
        this.toString(buffer, 1);
        buffer.setLength(buffer.length() - 1);
        return buffer.toString();
    }
    
    void toString(final StringBuilder buffer, final int indent) {
        buffer.append(super.toString());
        buffer.append('\n');
        final Actor[] actors = this.children.begin();
        for (int i = 0, n = this.children.size; i < n; ++i) {
            for (int ii = 0; ii < indent; ++ii) {
                buffer.append("|  ");
            }
            final Actor actor = actors[i];
            if (actor instanceof Group) {
                ((Group)actor).toString(buffer, indent + 1);
            }
            else {
                buffer.append(actor);
                buffer.append('\n');
            }
        }
        this.children.end();
    }
}
