// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.attributes;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class BlendingAttribute extends Attribute
{
    public static final String Alias = "blended";
    public static final long Type;
    public boolean blended;
    public int sourceFunction;
    public int destFunction;
    public float opacity;
    
    static {
        Type = Attribute.register("blended");
    }
    
    public static final boolean is(final long mask) {
        return (mask & BlendingAttribute.Type) == mask;
    }
    
    public BlendingAttribute() {
        this(null);
    }
    
    public BlendingAttribute(final boolean blended, final int sourceFunc, final int destFunc, final float opacity) {
        super(BlendingAttribute.Type);
        this.opacity = 1.0f;
        this.blended = blended;
        this.sourceFunction = sourceFunc;
        this.destFunction = destFunc;
        this.opacity = opacity;
    }
    
    public BlendingAttribute(final int sourceFunc, final int destFunc, final float opacity) {
        this(true, sourceFunc, destFunc, opacity);
    }
    
    public BlendingAttribute(final int sourceFunc, final int destFunc) {
        this(sourceFunc, destFunc, 1.0f);
    }
    
    public BlendingAttribute(final boolean blended, final float opacity) {
        this(blended, 770, 771, opacity);
    }
    
    public BlendingAttribute(final float opacity) {
        this(true, opacity);
    }
    
    public BlendingAttribute(final BlendingAttribute copyFrom) {
        this(copyFrom == null || copyFrom.blended, (copyFrom == null) ? 770 : copyFrom.sourceFunction, (copyFrom == null) ? 771 : copyFrom.destFunction, (copyFrom == null) ? 1.0f : copyFrom.opacity);
    }
    
    @Override
    public BlendingAttribute copy() {
        return new BlendingAttribute(this);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 947 * result + (this.blended ? 1 : 0);
        result = 947 * result + this.sourceFunction;
        result = 947 * result + this.destFunction;
        result = 947 * result + NumberUtils.floatToRawIntBits(this.opacity);
        return result;
    }
    
    @Override
    public int compareTo(final Attribute o) {
        if (this.type != o.type) {
            return (int)(this.type - o.type);
        }
        final BlendingAttribute other = (BlendingAttribute)o;
        if (this.blended != other.blended) {
            return this.blended ? 1 : -1;
        }
        if (this.sourceFunction != other.sourceFunction) {
            return this.sourceFunction - other.sourceFunction;
        }
        if (this.destFunction != other.destFunction) {
            return this.destFunction - other.destFunction;
        }
        return MathUtils.isEqual(this.opacity, other.opacity) ? 0 : ((this.opacity < other.opacity) ? 1 : -1);
    }
}
