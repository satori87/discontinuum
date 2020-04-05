// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.model.NodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

public class BaseAnimationController
{
    private final Pool<Transform> transformPool;
    private static final ObjectMap<Node, Transform> transforms;
    private boolean applying;
    public final ModelInstance target;
    private static final Transform tmpT;
    
    static {
        transforms = new ObjectMap<Node, Transform>();
        tmpT = new Transform();
    }
    
    public BaseAnimationController(final ModelInstance target) {
        this.transformPool = new Pool<Transform>() {
            @Override
            protected Transform newObject() {
                return new Transform();
            }
        };
        this.applying = false;
        this.target = target;
    }
    
    protected void begin() {
        if (this.applying) {
            throw new GdxRuntimeException("You must call end() after each call to being()");
        }
        this.applying = true;
    }
    
    protected void apply(final Animation animation, final float time, final float weight) {
        if (!this.applying) {
            throw new GdxRuntimeException("You must call begin() before adding an animation");
        }
        applyAnimation(BaseAnimationController.transforms, this.transformPool, weight, animation, time);
    }
    
    protected void end() {
        if (!this.applying) {
            throw new GdxRuntimeException("You must call begin() first");
        }
        for (final ObjectMap.Entry<Node, Transform> entry : BaseAnimationController.transforms.entries()) {
            entry.value.toMatrix4(entry.key.localTransform);
            this.transformPool.free(entry.value);
        }
        BaseAnimationController.transforms.clear();
        this.target.calculateTransforms();
        this.applying = false;
    }
    
    protected void applyAnimation(final Animation animation, final float time) {
        if (this.applying) {
            throw new GdxRuntimeException("Call end() first");
        }
        applyAnimation(null, null, 1.0f, animation, time);
        this.target.calculateTransforms();
    }
    
    protected void applyAnimations(final Animation anim1, final float time1, final Animation anim2, final float time2, final float weight) {
        if (anim2 == null || weight == 0.0f) {
            this.applyAnimation(anim1, time1);
        }
        else if (anim1 == null || weight == 1.0f) {
            this.applyAnimation(anim2, time2);
        }
        else {
            if (this.applying) {
                throw new GdxRuntimeException("Call end() first");
            }
            this.begin();
            this.apply(anim1, time1, 1.0f);
            this.apply(anim2, time2, weight);
            this.end();
        }
    }
    
    private static final <T> int getFirstKeyframeIndexAtTime(final Array<NodeKeyframe<T>> arr, final float time) {
        for (int n = arr.size - 1, i = 0; i < n; ++i) {
            if (time >= arr.get(i).keytime && time <= arr.get(i + 1).keytime) {
                return i;
            }
        }
        return 0;
    }
    
    private static final Vector3 getTranslationAtTime(final NodeAnimation nodeAnim, final float time, final Vector3 out) {
        if (nodeAnim.translation == null) {
            return out.set(nodeAnim.node.translation);
        }
        if (nodeAnim.translation.size == 1) {
            return out.set((Vector3)nodeAnim.translation.get(0).value);
        }
        int index = getFirstKeyframeIndexAtTime(nodeAnim.translation, time);
        final NodeKeyframe firstKeyframe = nodeAnim.translation.get(index);
        out.set((Vector3)firstKeyframe.value);
        if (++index < nodeAnim.translation.size) {
            final NodeKeyframe<Vector3> secondKeyframe = nodeAnim.translation.get(index);
            final float t = (time - firstKeyframe.keytime) / (secondKeyframe.keytime - firstKeyframe.keytime);
            out.lerp((Vector3)secondKeyframe.value, t);
        }
        return out;
    }
    
    private static final Quaternion getRotationAtTime(final NodeAnimation nodeAnim, final float time, final Quaternion out) {
        if (nodeAnim.rotation == null) {
            return out.set(nodeAnim.node.rotation);
        }
        if (nodeAnim.rotation.size == 1) {
            return out.set(nodeAnim.rotation.get(0).value);
        }
        int index = getFirstKeyframeIndexAtTime(nodeAnim.rotation, time);
        final NodeKeyframe firstKeyframe = nodeAnim.rotation.get(index);
        out.set((Quaternion)firstKeyframe.value);
        if (++index < nodeAnim.rotation.size) {
            final NodeKeyframe<Quaternion> secondKeyframe = nodeAnim.rotation.get(index);
            final float t = (time - firstKeyframe.keytime) / (secondKeyframe.keytime - firstKeyframe.keytime);
            out.slerp(secondKeyframe.value, t);
        }
        return out;
    }
    
    private static final Vector3 getScalingAtTime(final NodeAnimation nodeAnim, final float time, final Vector3 out) {
        if (nodeAnim.scaling == null) {
            return out.set(nodeAnim.node.scale);
        }
        if (nodeAnim.scaling.size == 1) {
            return out.set((Vector3)nodeAnim.scaling.get(0).value);
        }
        int index = getFirstKeyframeIndexAtTime(nodeAnim.scaling, time);
        final NodeKeyframe firstKeyframe = nodeAnim.scaling.get(index);
        out.set((Vector3)firstKeyframe.value);
        if (++index < nodeAnim.scaling.size) {
            final NodeKeyframe<Vector3> secondKeyframe = nodeAnim.scaling.get(index);
            final float t = (time - firstKeyframe.keytime) / (secondKeyframe.keytime - firstKeyframe.keytime);
            out.lerp((Vector3)secondKeyframe.value, t);
        }
        return out;
    }
    
    private static final Transform getNodeAnimationTransform(final NodeAnimation nodeAnim, final float time) {
        final Transform transform = BaseAnimationController.tmpT;
        getTranslationAtTime(nodeAnim, time, transform.translation);
        getRotationAtTime(nodeAnim, time, transform.rotation);
        getScalingAtTime(nodeAnim, time, transform.scale);
        return transform;
    }
    
    private static final void applyNodeAnimationDirectly(final NodeAnimation nodeAnim, final float time) {
        final Node node = nodeAnim.node;
        node.isAnimated = true;
        final Transform transform = getNodeAnimationTransform(nodeAnim, time);
        transform.toMatrix4(node.localTransform);
    }
    
    private static final void applyNodeAnimationBlending(final NodeAnimation nodeAnim, final ObjectMap<Node, Transform> out, final Pool<Transform> pool, final float alpha, final float time) {
        final Node node = nodeAnim.node;
        node.isAnimated = true;
        final Transform transform = getNodeAnimationTransform(nodeAnim, time);
        final Transform t = out.get(node, null);
        if (t != null) {
            if (alpha > 0.999999f) {
                t.set(transform);
            }
            else {
                t.lerp(transform, alpha);
            }
        }
        else if (alpha > 0.999999f) {
            out.put(node, pool.obtain().set(transform));
        }
        else {
            out.put(node, pool.obtain().set(node.translation, node.rotation, node.scale).lerp(transform, alpha));
        }
    }
    
    protected static void applyAnimation(final ObjectMap<Node, Transform> out, final Pool<Transform> pool, final float alpha, final Animation animation, final float time) {
        if (out == null) {
            for (final NodeAnimation nodeAnim : animation.nodeAnimations) {
                applyNodeAnimationDirectly(nodeAnim, time);
            }
        }
        else {
            for (final Node node : out.keys()) {
                node.isAnimated = false;
            }
            for (final NodeAnimation nodeAnim : animation.nodeAnimations) {
                applyNodeAnimationBlending(nodeAnim, out, pool, alpha, time);
            }
            for (final ObjectMap.Entry<Node, Transform> e : out.entries()) {
                if (!e.key.isAnimated) {
                    e.key.isAnimated = true;
                    e.value.lerp(e.key.translation, e.key.rotation, e.key.scale, alpha);
                }
            }
        }
    }
    
    protected void removeAnimation(final Animation animation) {
        for (final NodeAnimation nodeAnim : animation.nodeAnimations) {
            nodeAnim.node.isAnimated = false;
        }
    }
    
    public static final class Transform implements Pool.Poolable
    {
        public final Vector3 translation;
        public final Quaternion rotation;
        public final Vector3 scale;
        
        public Transform() {
            this.translation = new Vector3();
            this.rotation = new Quaternion();
            this.scale = new Vector3(1.0f, 1.0f, 1.0f);
        }
        
        public Transform idt() {
            this.translation.set(0.0f, 0.0f, 0.0f);
            this.rotation.idt();
            this.scale.set(1.0f, 1.0f, 1.0f);
            return this;
        }
        
        public Transform set(final Vector3 t, final Quaternion r, final Vector3 s) {
            this.translation.set(t);
            this.rotation.set(r);
            this.scale.set(s);
            return this;
        }
        
        public Transform set(final Transform other) {
            return this.set(other.translation, other.rotation, other.scale);
        }
        
        public Transform lerp(final Transform target, final float alpha) {
            return this.lerp(target.translation, target.rotation, target.scale, alpha);
        }
        
        public Transform lerp(final Vector3 targetT, final Quaternion targetR, final Vector3 targetS, final float alpha) {
            this.translation.lerp(targetT, alpha);
            this.rotation.slerp(targetR, alpha);
            this.scale.lerp(targetS, alpha);
            return this;
        }
        
        public Matrix4 toMatrix4(final Matrix4 out) {
            return out.set(this.translation, this.rotation, this.scale);
        }
        
        @Override
        public void reset() {
            this.idt();
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.translation.toString()) + " - " + this.rotation.toString() + " - " + this.scale.toString();
        }
    }
}
