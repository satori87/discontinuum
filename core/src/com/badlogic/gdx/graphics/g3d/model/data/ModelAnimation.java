// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.model.data;

import com.badlogic.gdx.utils.Array;

public class ModelAnimation
{
    public String id;
    public Array<ModelNodeAnimation> nodeAnimations;
    
    public ModelAnimation() {
        this.nodeAnimations = new Array<ModelNodeAnimation>();
    }
}
