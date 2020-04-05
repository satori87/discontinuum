// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.model;

import com.badlogic.gdx.utils.Array;

public class Animation
{
    public String id;
    public float duration;
    public Array<NodeAnimation> nodeAnimations;
    
    public Animation() {
        this.nodeAnimations = new Array<NodeAnimation>();
    }
}
