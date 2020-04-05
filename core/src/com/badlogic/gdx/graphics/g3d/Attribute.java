// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.utils.Array;

public abstract class Attribute implements Comparable<Attribute>
{
    private static final Array<String> types;
    public final long type;
    private final int typeBit;
    
    static {
        types = new Array<String>();
    }
    
    public static final long getAttributeType(final String alias) {
        for (int i = 0; i < Attribute.types.size; ++i) {
            if (Attribute.types.get(i).compareTo(alias) == 0) {
                return 1L << i;
            }
        }
        return 0L;
    }
    
    public static final String getAttributeAlias(final long type) {
        int idx = -1;
        while (type != 0L && ++idx < 63 && (type >> idx & 0x1L) == 0x0L) {}
        return (idx >= 0 && idx < Attribute.types.size) ? Attribute.types.get(idx) : null;
    }
    
    protected static final long register(final String alias) {
        final long result = getAttributeType(alias);
        if (result > 0L) {
            return result;
        }
        Attribute.types.add(alias);
        return 1L << Attribute.types.size - 1;
    }
    
    protected Attribute(final long type) {
        this.type = type;
        this.typeBit = Long.numberOfTrailingZeros(type);
    }
    
    public abstract Attribute copy();
    
    protected boolean equals(final Attribute other) {
        return other.hashCode() == this.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Attribute)) {
            return false;
        }
        final Attribute other = (Attribute)obj;
        return this.type == other.type && this.equals(other);
    }
    
    @Override
    public String toString() {
        return getAttributeAlias(this.type);
    }
    
    @Override
    public int hashCode() {
        return 7489 * this.typeBit;
    }
}
