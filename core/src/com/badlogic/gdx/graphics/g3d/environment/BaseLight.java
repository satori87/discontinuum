// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.environment;

import com.badlogic.gdx.graphics.Color;

public abstract class BaseLight<T extends BaseLight<T>>
{
    public final Color color;
    
    public BaseLight() {
        this.color = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    }
    
    public T setColor(final float r, final float g, final float b, final float a) {
        this.color.set(r, g, b, a);
        return (T)this;
    }
    
    public T setColor(final Color color) {
        this.color.set(color);
        return (T)this;
    }
}
