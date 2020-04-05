// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math.collision;

import com.badlogic.gdx.math.Vector3;
import java.io.Serializable;

public class Segment implements Serializable
{
    private static final long serialVersionUID = 2739667069736519602L;
    public final Vector3 a;
    public final Vector3 b;
    
    public Segment(final Vector3 a, final Vector3 b) {
        this.a = new Vector3();
        this.b = new Vector3();
        this.a.set(a);
        this.b.set(b);
    }
    
    public Segment(final float aX, final float aY, final float aZ, final float bX, final float bY, final float bZ) {
        this.a = new Vector3();
        this.b = new Vector3();
        this.a.set(aX, aY, aZ);
        this.b.set(bX, bY, bZ);
    }
    
    public float len() {
        return this.a.dst(this.b);
    }
    
    public float len2() {
        return this.a.dst2(this.b);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        final Segment s = (Segment)o;
        return this.a.equals(s.a) && this.b.equals(s.b);
    }
    
    @Override
    public int hashCode() {
        final int prime = 71;
        int result = 1;
        result = 71 * result + this.a.hashCode();
        result = 71 * result + this.b.hashCode();
        return result;
    }
}
