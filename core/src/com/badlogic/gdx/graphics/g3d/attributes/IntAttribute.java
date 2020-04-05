// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.attributes;

import com.badlogic.gdx.graphics.g3d.Attribute;

public class IntAttribute extends Attribute
{
    public static final String CullFaceAlias = "cullface";
    public static final long CullFace;
    public int value;
    
    static {
        CullFace = Attribute.register("cullface");
    }
    
    public static IntAttribute createCullFace(final int value) {
        return new IntAttribute(IntAttribute.CullFace, value);
    }
    
    public IntAttribute(final long type) {
        super(type);
    }
    
    public IntAttribute(final long type, final int value) {
        super(type);
        this.value = value;
    }
    
    @Override
    public Attribute copy() {
        return new IntAttribute(this.type, this.value);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 983 * result + this.value;
        return result;
    }
    
    @Override
    public int compareTo(final Attribute o) {
        if (this.type != o.type) {
            return (int)(this.type - o.type);
        }
        return this.value - ((IntAttribute)o).value;
    }
}
