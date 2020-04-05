// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.math.Matrix4;

public class Renderable
{
    public final Matrix4 worldTransform;
    public final MeshPart meshPart;
    public Material material;
    public Environment environment;
    public Matrix4[] bones;
    public Shader shader;
    public Object userData;
    
    public Renderable() {
        this.worldTransform = new Matrix4();
        this.meshPart = new MeshPart();
    }
    
    public Renderable set(final Renderable renderable) {
        this.worldTransform.set(renderable.worldTransform);
        this.material = renderable.material;
        this.meshPart.set(renderable.meshPart);
        this.bones = renderable.bones;
        this.environment = renderable.environment;
        this.shader = renderable.shader;
        this.userData = renderable.userData;
        return this;
    }
}
