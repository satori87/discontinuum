// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe;
import com.badlogic.gdx.graphics.g3d.model.NodeAnimation;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import java.util.Iterator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Array;

public class ModelInstance implements RenderableProvider
{
    public static boolean defaultShareKeyframes;
    public final Array<Material> materials;
    public final Array<Node> nodes;
    public final Array<Animation> animations;
    public final Model model;
    public Matrix4 transform;
    public Object userData;
    
    static {
        ModelInstance.defaultShareKeyframes = true;
    }
    
    public ModelInstance(final Model model) {
        this(model, (String[])null);
    }
    
    public ModelInstance(final Model model, final String nodeId, final boolean mergeTransform) {
        this(model, null, nodeId, false, false, mergeTransform);
    }
    
    public ModelInstance(final Model model, final Matrix4 transform, final String nodeId, final boolean mergeTransform) {
        this(model, transform, nodeId, false, false, mergeTransform);
    }
    
    public ModelInstance(final Model model, final String nodeId, final boolean parentTransform, final boolean mergeTransform) {
        this(model, null, nodeId, true, parentTransform, mergeTransform);
    }
    
    public ModelInstance(final Model model, final Matrix4 transform, final String nodeId, final boolean parentTransform, final boolean mergeTransform) {
        this(model, transform, nodeId, true, parentTransform, mergeTransform);
    }
    
    public ModelInstance(final Model model, final String nodeId, final boolean recursive, final boolean parentTransform, final boolean mergeTransform) {
        this(model, null, nodeId, recursive, parentTransform, mergeTransform);
    }
    
    public ModelInstance(final Model model, final Matrix4 transform, final String nodeId, final boolean recursive, final boolean parentTransform, final boolean mergeTransform) {
        this(model, transform, nodeId, recursive, parentTransform, mergeTransform, ModelInstance.defaultShareKeyframes);
    }
    
    public ModelInstance(final Model model, final Matrix4 transform, final String nodeId, final boolean recursive, final boolean parentTransform, final boolean mergeTransform, final boolean shareKeyframes) {
        this.materials = new Array<Material>();
        this.nodes = new Array<Node>();
        this.animations = new Array<Animation>();
        this.model = model;
        this.transform = ((transform == null) ? new Matrix4() : transform);
        final Node node = model.getNode(nodeId, recursive);
        final Node copy;
        this.nodes.add(copy = node.copy());
        if (mergeTransform) {
            this.transform.mul(parentTransform ? node.globalTransform : node.localTransform);
            copy.translation.set(0.0f, 0.0f, 0.0f);
            copy.rotation.idt();
            copy.scale.set(1.0f, 1.0f, 1.0f);
        }
        else if (parentTransform && copy.hasParent()) {
            this.transform.mul(node.getParent().globalTransform);
        }
        this.invalidate();
        this.copyAnimations(model.animations, shareKeyframes);
        this.calculateTransforms();
    }
    
    public ModelInstance(final Model model, final String... rootNodeIds) {
        this(model, (Matrix4)null, rootNodeIds);
    }
    
    public ModelInstance(final Model model, final Matrix4 transform, final String... rootNodeIds) {
        this.materials = new Array<Material>();
        this.nodes = new Array<Node>();
        this.animations = new Array<Animation>();
        this.model = model;
        this.transform = ((transform == null) ? new Matrix4() : transform);
        if (rootNodeIds == null) {
            this.copyNodes(model.nodes);
        }
        else {
            this.copyNodes(model.nodes, rootNodeIds);
        }
        this.copyAnimations(model.animations, ModelInstance.defaultShareKeyframes);
        this.calculateTransforms();
    }
    
    public ModelInstance(final Model model, final Array<String> rootNodeIds) {
        this(model, null, rootNodeIds);
    }
    
    public ModelInstance(final Model model, final Matrix4 transform, final Array<String> rootNodeIds) {
        this(model, transform, rootNodeIds, ModelInstance.defaultShareKeyframes);
    }
    
    public ModelInstance(final Model model, final Matrix4 transform, final Array<String> rootNodeIds, final boolean shareKeyframes) {
        this.materials = new Array<Material>();
        this.nodes = new Array<Node>();
        this.animations = new Array<Animation>();
        this.model = model;
        this.transform = ((transform == null) ? new Matrix4() : transform);
        this.copyNodes(model.nodes, rootNodeIds);
        this.copyAnimations(model.animations, shareKeyframes);
        this.calculateTransforms();
    }
    
    public ModelInstance(final Model model, final Vector3 position) {
        this(model);
        this.transform.setToTranslation(position);
    }
    
    public ModelInstance(final Model model, final float x, final float y, final float z) {
        this(model);
        this.transform.setToTranslation(x, y, z);
    }
    
    public ModelInstance(final Model model, final Matrix4 transform) {
        this(model, transform, (String[])null);
    }
    
    public ModelInstance(final ModelInstance copyFrom) {
        this(copyFrom, copyFrom.transform.cpy());
    }
    
    public ModelInstance(final ModelInstance copyFrom, final Matrix4 transform) {
        this(copyFrom, transform, ModelInstance.defaultShareKeyframes);
    }
    
    public ModelInstance(final ModelInstance copyFrom, final Matrix4 transform, final boolean shareKeyframes) {
        this.materials = new Array<Material>();
        this.nodes = new Array<Node>();
        this.animations = new Array<Animation>();
        this.model = copyFrom.model;
        this.transform = ((transform == null) ? new Matrix4() : transform);
        this.copyNodes(copyFrom.nodes);
        this.copyAnimations(copyFrom.animations, shareKeyframes);
        this.calculateTransforms();
    }
    
    public ModelInstance copy() {
        return new ModelInstance(this);
    }
    
    private void copyNodes(final Array<Node> nodes) {
        for (int i = 0, n = nodes.size; i < n; ++i) {
            final Node node = nodes.get(i);
            this.nodes.add(node.copy());
        }
        this.invalidate();
    }
    
    private void copyNodes(final Array<Node> nodes, final String... nodeIds) {
        for (int i = 0, n = nodes.size; i < n; ++i) {
            final Node node = nodes.get(i);
            for (final String nodeId : nodeIds) {
                if (nodeId.equals(node.id)) {
                    this.nodes.add(node.copy());
                    break;
                }
            }
        }
        this.invalidate();
    }
    
    private void copyNodes(final Array<Node> nodes, final Array<String> nodeIds) {
        for (int i = 0, n = nodes.size; i < n; ++i) {
            final Node node = nodes.get(i);
            for (final String nodeId : nodeIds) {
                if (nodeId.equals(node.id)) {
                    this.nodes.add(node.copy());
                    break;
                }
            }
        }
        this.invalidate();
    }
    
    private void invalidate(final Node node) {
        for (int i = 0, n = node.parts.size; i < n; ++i) {
            final NodePart part = node.parts.get(i);
            final ArrayMap<Node, Matrix4> bindPose = part.invBoneBindTransforms;
            if (bindPose != null) {
                for (int j = 0; j < bindPose.size; ++j) {
                    bindPose.keys[j] = this.getNode(bindPose.keys[j].id);
                }
            }
            if (!this.materials.contains(part.material, true)) {
                final int midx = this.materials.indexOf(part.material, false);
                if (midx < 0) {
                    this.materials.add(part.material = part.material.copy());
                }
                else {
                    part.material = this.materials.get(midx);
                }
            }
        }
        for (int i = 0, n = node.getChildCount(); i < n; ++i) {
            this.invalidate(node.getChild(i));
        }
    }
    
    private void invalidate() {
        for (int i = 0, n = this.nodes.size; i < n; ++i) {
            this.invalidate(this.nodes.get(i));
        }
    }
    
    private void copyAnimations(final Iterable<Animation> source, final boolean shareKeyframes) {
        for (final Animation anim : source) {
            final Animation animation = new Animation();
            animation.id = anim.id;
            animation.duration = anim.duration;
            for (final NodeAnimation nanim : anim.nodeAnimations) {
                final Node node = this.getNode(nanim.node.id);
                if (node == null) {
                    continue;
                }
                final NodeAnimation nodeAnim = new NodeAnimation();
                nodeAnim.node = node;
                if (shareKeyframes) {
                    nodeAnim.translation = nanim.translation;
                    nodeAnim.rotation = nanim.rotation;
                    nodeAnim.scaling = nanim.scaling;
                }
                else {
                    if (nanim.translation != null) {
                        nodeAnim.translation = new Array<NodeKeyframe<Vector3>>();
                        for (final NodeKeyframe<Vector3> kf : nanim.translation) {
                            nodeAnim.translation.add(new NodeKeyframe<Vector3>(kf.keytime, kf.value));
                        }
                    }
                    if (nanim.rotation != null) {
                        nodeAnim.rotation = new Array<NodeKeyframe<Quaternion>>();
                        for (final NodeKeyframe<Quaternion> kf2 : nanim.rotation) {
                            nodeAnim.rotation.add(new NodeKeyframe<Quaternion>(kf2.keytime, kf2.value));
                        }
                    }
                    if (nanim.scaling != null) {
                        nodeAnim.scaling = new Array<NodeKeyframe<Vector3>>();
                        for (final NodeKeyframe<Vector3> kf : nanim.scaling) {
                            nodeAnim.scaling.add(new NodeKeyframe<Vector3>(kf.keytime, kf.value));
                        }
                    }
                }
                if (nodeAnim.translation == null && nodeAnim.rotation == null && nodeAnim.scaling == null) {
                    continue;
                }
                animation.nodeAnimations.add(nodeAnim);
            }
            if (animation.nodeAnimations.size > 0) {
                this.animations.add(animation);
            }
        }
    }
    
    @Override
    public void getRenderables(final Array<Renderable> renderables, final Pool<Renderable> pool) {
        for (final Node node : this.nodes) {
            this.getRenderables(node, renderables, pool);
        }
    }
    
    public Renderable getRenderable(final Renderable out) {
        return this.getRenderable(out, this.nodes.get(0));
    }
    
    public Renderable getRenderable(final Renderable out, final Node node) {
        return this.getRenderable(out, node, node.parts.get(0));
    }
    
    public Renderable getRenderable(final Renderable out, final Node node, final NodePart nodePart) {
        nodePart.setRenderable(out);
        if (nodePart.bones == null && this.transform != null) {
            out.worldTransform.set(this.transform).mul(node.globalTransform);
        }
        else if (this.transform != null) {
            out.worldTransform.set(this.transform);
        }
        else {
            out.worldTransform.idt();
        }
        out.userData = this.userData;
        return out;
    }
    
    protected void getRenderables(final Node node, final Array<Renderable> renderables, final Pool<Renderable> pool) {
        if (node.parts.size > 0) {
            for (final NodePart nodePart : node.parts) {
                if (nodePart.enabled) {
                    renderables.add(this.getRenderable(pool.obtain(), node, nodePart));
                }
            }
        }
        for (final Node child : node.getChildren()) {
            this.getRenderables(child, renderables, pool);
        }
    }
    
    public void calculateTransforms() {
        final int n = this.nodes.size;
        for (int i = 0; i < n; ++i) {
            this.nodes.get(i).calculateTransforms(true);
        }
        for (int i = 0; i < n; ++i) {
            this.nodes.get(i).calculateBoneTransforms(true);
        }
    }
    
    public BoundingBox calculateBoundingBox(final BoundingBox out) {
        out.inf();
        return this.extendBoundingBox(out);
    }
    
    public BoundingBox extendBoundingBox(final BoundingBox out) {
        for (int n = this.nodes.size, i = 0; i < n; ++i) {
            this.nodes.get(i).extendBoundingBox(out);
        }
        return out;
    }
    
    public Animation getAnimation(final String id) {
        return this.getAnimation(id, false);
    }
    
    public Animation getAnimation(final String id, final boolean ignoreCase) {
        final int n = this.animations.size;
        if (ignoreCase) {
            for (int i = 0; i < n; ++i) {
                final Animation animation;
                if ((animation = this.animations.get(i)).id.equalsIgnoreCase(id)) {
                    return animation;
                }
            }
        }
        else {
            for (int i = 0; i < n; ++i) {
                final Animation animation;
                if ((animation = this.animations.get(i)).id.equals(id)) {
                    return animation;
                }
            }
        }
        return null;
    }
    
    public Material getMaterial(final String id) {
        return this.getMaterial(id, true);
    }
    
    public Material getMaterial(final String id, final boolean ignoreCase) {
        final int n = this.materials.size;
        if (ignoreCase) {
            for (int i = 0; i < n; ++i) {
                final Material material;
                if ((material = this.materials.get(i)).id.equalsIgnoreCase(id)) {
                    return material;
                }
            }
        }
        else {
            for (int i = 0; i < n; ++i) {
                final Material material;
                if ((material = this.materials.get(i)).id.equals(id)) {
                    return material;
                }
            }
        }
        return null;
    }
    
    public Node getNode(final String id) {
        return this.getNode(id, true);
    }
    
    public Node getNode(final String id, final boolean recursive) {
        return this.getNode(id, recursive, false);
    }
    
    public Node getNode(final String id, final boolean recursive, final boolean ignoreCase) {
        return Node.getNode(this.nodes, id, recursive, ignoreCase);
    }
}
