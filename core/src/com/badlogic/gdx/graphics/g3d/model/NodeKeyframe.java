// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.model;

public class NodeKeyframe<T>
{
    public float keytime;
    public final T value;
    
    public NodeKeyframe(final float t, final T v) {
        this.keytime = t;
        this.value = v;
    }
}
