// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import java.util.Comparator;

public class DefaultRenderableSorter implements RenderableSorter, Comparator<Renderable>
{
    private Camera camera;
    private final Vector3 tmpV1;
    private final Vector3 tmpV2;
    
    public DefaultRenderableSorter() {
        this.tmpV1 = new Vector3();
        this.tmpV2 = new Vector3();
    }
    
    @Override
    public void sort(final Camera camera, final Array<Renderable> renderables) {
        this.camera = camera;
        renderables.sort(this);
    }
    
    private Vector3 getTranslation(final Matrix4 worldTransform, final Vector3 center, final Vector3 output) {
        if (center.isZero()) {
            worldTransform.getTranslation(output);
        }
        else if (!worldTransform.hasRotationOrScaling()) {
            worldTransform.getTranslation(output).add(center);
        }
        else {
            output.set(center).mul(worldTransform);
        }
        return output;
    }
    
    @Override
    public int compare(final Renderable o1, final Renderable o2) {
        final boolean b1 = o1.material.has(BlendingAttribute.Type) && ((BlendingAttribute)o1.material.get(BlendingAttribute.Type)).blended;
        final boolean b2 = o2.material.has(BlendingAttribute.Type) && ((BlendingAttribute)o2.material.get(BlendingAttribute.Type)).blended;
        if (b1 != b2) {
            return b1 ? 1 : -1;
        }
        this.getTranslation(o1.worldTransform, o1.meshPart.center, this.tmpV1);
        this.getTranslation(o2.worldTransform, o2.meshPart.center, this.tmpV2);
        final float dst = (float)((int)(1000.0f * this.camera.position.dst2(this.tmpV1)) - (int)(1000.0f * this.camera.position.dst2(this.tmpV2)));
        final int result = (dst < 0.0f) ? -1 : ((dst > 0.0f) ? 1 : 0);
        return b1 ? (-result) : result;
    }
}