// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.model;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.math.collision.BoundingBox;
import java.util.Iterator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Node
{
    public String id;
    public boolean inheritTransform;
    public boolean isAnimated;
    public final Vector3 translation;
    public final Quaternion rotation;
    public final Vector3 scale;
    public final Matrix4 localTransform;
    public final Matrix4 globalTransform;
    public Array<NodePart> parts;
    protected Node parent;
    private final Array<Node> children;
    
    public Node() {
        this.inheritTransform = true;
        this.translation = new Vector3();
        this.rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
        this.scale = new Vector3(1.0f, 1.0f, 1.0f);
        this.localTransform = new Matrix4();
        this.globalTransform = new Matrix4();
        this.parts = new Array<NodePart>(2);
        this.children = new Array<Node>(2);
    }
    
    public Matrix4 calculateLocalTransform() {
        if (!this.isAnimated) {
            this.localTransform.set(this.translation, this.rotation, this.scale);
        }
        return this.localTransform;
    }
    
    public Matrix4 calculateWorldTransform() {
        if (this.inheritTransform && this.parent != null) {
            this.globalTransform.set(this.parent.globalTransform).mul(this.localTransform);
        }
        else {
            this.globalTransform.set(this.localTransform);
        }
        return this.globalTransform;
    }
    
    public void calculateTransforms(final boolean recursive) {
        this.calculateLocalTransform();
        this.calculateWorldTransform();
        if (recursive) {
            for (final Node child : this.children) {
                child.calculateTransforms(true);
            }
        }
    }
    
    public void calculateBoneTransforms(final boolean recursive) {
        for (final NodePart part : this.parts) {
            if (part.invBoneBindTransforms != null && part.bones != null) {
                if (part.invBoneBindTransforms.size != part.bones.length) {
                    continue;
                }
                for (int n = part.invBoneBindTransforms.size, i = 0; i < n; ++i) {
                    part.bones[i].set(part.invBoneBindTransforms.keys[i].globalTransform).mul(part.invBoneBindTransforms.values[i]);
                }
            }
        }
        if (recursive) {
            for (final Node child : this.children) {
                child.calculateBoneTransforms(true);
            }
        }
    }
    
    public BoundingBox calculateBoundingBox(final BoundingBox out) {
        out.inf();
        return this.extendBoundingBox(out);
    }
    
    public BoundingBox calculateBoundingBox(final BoundingBox out, final boolean transform) {
        out.inf();
        return this.extendBoundingBox(out, transform);
    }
    
    public BoundingBox extendBoundingBox(final BoundingBox out) {
        return this.extendBoundingBox(out, true);
    }
    
    public BoundingBox extendBoundingBox(final BoundingBox out, final boolean transform) {
        for (int partCount = this.parts.size, i = 0; i < partCount; ++i) {
            final NodePart part = this.parts.get(i);
            if (part.enabled) {
                final MeshPart meshPart = part.meshPart;
                if (transform) {
                    meshPart.mesh.extendBoundingBox(out, meshPart.offset, meshPart.size, this.globalTransform);
                }
                else {
                    meshPart.mesh.extendBoundingBox(out, meshPart.offset, meshPart.size);
                }
            }
        }
        for (int childCount = this.children.size, j = 0; j < childCount; ++j) {
            this.children.get(j).extendBoundingBox(out);
        }
        return out;
    }
    
    public <T extends Node> void attachTo(final T parent) {
        parent.addChild(this);
    }
    
    public void detach() {
        if (this.parent != null) {
            this.parent.removeChild(this);
            this.parent = null;
        }
    }
    
    public boolean hasChildren() {
        return this.children != null && this.children.size > 0;
    }
    
    public int getChildCount() {
        return this.children.size;
    }
    
    public Node getChild(final int index) {
        return this.children.get(index);
    }
    
    public Node getChild(final String id, final boolean recursive, final boolean ignoreCase) {
        return getNode(this.children, id, recursive, ignoreCase);
    }
    
    public <T extends Node> int addChild(final T child) {
        return this.insertChild(-1, child);
    }
    
    public <T extends Node> int addChildren(final Iterable<T> nodes) {
        return this.insertChildren(-1, nodes);
    }
    
    public <T extends Node> int insertChild(int index, final T child) {
        for (Node p = this; p != null; p = p.getParent()) {
            if (p == child) {
                throw new GdxRuntimeException("Cannot add a parent as a child");
            }
        }
        Node p = child.getParent();
        if (p != null && !p.removeChild(child)) {
            throw new GdxRuntimeException("Could not remove child from its current parent");
        }
        if (index < 0 || index >= this.children.size) {
            index = this.children.size;
            this.children.add(child);
        }
        else {
            this.children.insert(index, child);
        }
        child.parent = this;
        return index;
    }
    
    public <T extends Node> int insertChildren(int index, final Iterable<T> nodes) {
        if (index < 0 || index > this.children.size) {
            index = this.children.size;
        }
        int i = index;
        for (final T child : nodes) {
            this.insertChild(i++, child);
        }
        return index;
    }
    
    public <T extends Node> boolean removeChild(final T child) {
        if (!this.children.removeValue(child, true)) {
            return false;
        }
        child.parent = null;
        return true;
    }
    
    public Iterable<Node> getChildren() {
        return this.children;
    }
    
    public Node getParent() {
        return this.parent;
    }
    
    public boolean hasParent() {
        return this.parent != null;
    }
    
    public Node copy() {
        return new Node().set(this);
    }
    
    protected Node set(final Node other) {
        this.detach();
        this.id = other.id;
        this.isAnimated = other.isAnimated;
        this.inheritTransform = other.inheritTransform;
        this.translation.set(other.translation);
        this.rotation.set(other.rotation);
        this.scale.set(other.scale);
        this.localTransform.set(other.localTransform);
        this.globalTransform.set(other.globalTransform);
        this.parts.clear();
        for (final NodePart nodePart : other.parts) {
            this.parts.add(nodePart.copy());
        }
        this.children.clear();
        for (final Node child : other.getChildren()) {
            this.addChild(child.copy());
        }
        return this;
    }
    
    public static Node getNode(final Array<Node> nodes, final String id, final boolean recursive, final boolean ignoreCase) {
        final int n = nodes.size;
        if (ignoreCase) {
            for (int i = 0; i < n; ++i) {
                final Node node;
                if ((node = nodes.get(i)).id.equalsIgnoreCase(id)) {
                    return node;
                }
            }
        }
        else {
            for (int i = 0; i < n; ++i) {
                final Node node;
                if ((node = nodes.get(i)).id.equals(id)) {
                    return node;
                }
            }
        }
        if (recursive) {
            for (int i = 0; i < n; ++i) {
                final Node node;
                if ((node = getNode(nodes.get(i).children, id, true, ignoreCase)) != null) {
                    return node;
                }
            }
        }
        return null;
    }
}
