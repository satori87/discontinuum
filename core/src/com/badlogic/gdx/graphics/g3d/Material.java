// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d;

import java.util.Iterator;
import com.badlogic.gdx.utils.Array;

public class Material extends Attributes
{
    private static int counter;
    public String id;
    
    static {
        Material.counter = 0;
    }
    
    public Material() {
        this("mtl" + ++Material.counter);
    }
    
    public Material(final String id) {
        this.id = id;
    }
    
    public Material(final Attribute... attributes) {
        this();
        this.set(attributes);
    }
    
    public Material(final String id, final Attribute... attributes) {
        this(id);
        this.set(attributes);
    }
    
    public Material(final Array<Attribute> attributes) {
        this();
        this.set(attributes);
    }
    
    public Material(final String id, final Array<Attribute> attributes) {
        this(id);
        this.set(attributes);
    }
    
    public Material(final Material copyFrom) {
        this(copyFrom.id, copyFrom);
    }
    
    public Material(final String id, final Material copyFrom) {
        this(id);
        for (final Attribute attr : copyFrom) {
            this.set(attr.copy());
        }
    }
    
    public Material copy() {
        return new Material(this);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + 3 * this.id.hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof Material && (other == this || (((Material)other).id.equals(this.id) && super.equals(other)));
    }
}
