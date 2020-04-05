// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d;

import java.util.Iterator;
import com.badlogic.gdx.utils.Array;
import java.util.Comparator;

public class Attributes implements Iterable<Attribute>, Comparator<Attribute>, Comparable<Attributes>
{
    protected long mask;
    protected final Array<Attribute> attributes;
    protected boolean sorted;
    
    public Attributes() {
        this.attributes = new Array<Attribute>();
        this.sorted = true;
    }
    
    public final void sort() {
        if (!this.sorted) {
            this.attributes.sort(this);
            this.sorted = true;
        }
    }
    
    public final long getMask() {
        return this.mask;
    }
    
    public final Attribute get(final long type) {
        if (this.has(type)) {
            for (int i = 0; i < this.attributes.size; ++i) {
                if (this.attributes.get(i).type == type) {
                    return this.attributes.get(i);
                }
            }
        }
        return null;
    }
    
    public final <T extends Attribute> T get(final Class<T> clazz, final long type) {
        return (T)this.get(type);
    }
    
    public final Array<Attribute> get(final Array<Attribute> out, final long type) {
        for (int i = 0; i < this.attributes.size; ++i) {
            if ((this.attributes.get(i).type & type) != 0x0L) {
                out.add(this.attributes.get(i));
            }
        }
        return out;
    }
    
    public void clear() {
        this.mask = 0L;
        this.attributes.clear();
    }
    
    public int size() {
        return this.attributes.size;
    }
    
    private final void enable(final long mask) {
        this.mask |= mask;
    }
    
    private final void disable(final long mask) {
        this.mask &= ~mask;
    }
    
    public final void set(final Attribute attribute) {
        final int idx = this.indexOf(attribute.type);
        if (idx < 0) {
            this.enable(attribute.type);
            this.attributes.add(attribute);
            this.sorted = false;
        }
        else {
            this.attributes.set(idx, attribute);
        }
        this.sort();
    }
    
    public final void set(final Attribute attribute1, final Attribute attribute2) {
        this.set(attribute1);
        this.set(attribute2);
    }
    
    public final void set(final Attribute attribute1, final Attribute attribute2, final Attribute attribute3) {
        this.set(attribute1);
        this.set(attribute2);
        this.set(attribute3);
    }
    
    public final void set(final Attribute attribute1, final Attribute attribute2, final Attribute attribute3, final Attribute attribute4) {
        this.set(attribute1);
        this.set(attribute2);
        this.set(attribute3);
        this.set(attribute4);
    }
    
    public final void set(final Attribute... attributes) {
        for (final Attribute attr : attributes) {
            this.set(attr);
        }
    }
    
    public final void set(final Iterable<Attribute> attributes) {
        for (final Attribute attr : attributes) {
            this.set(attr);
        }
    }
    
    public final void remove(final long mask) {
        for (int i = this.attributes.size - 1; i >= 0; --i) {
            final long type = this.attributes.get(i).type;
            if ((mask & type) == type) {
                this.attributes.removeIndex(i);
                this.disable(type);
                this.sorted = false;
            }
        }
        this.sort();
    }
    
    public final boolean has(final long type) {
        return type != 0L && (this.mask & type) == type;
    }
    
    protected int indexOf(final long type) {
        if (this.has(type)) {
            for (int i = 0; i < this.attributes.size; ++i) {
                if (this.attributes.get(i).type == type) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public final boolean same(final Attributes other, final boolean compareValues) {
        if (other == this) {
            return true;
        }
        if (other == null || this.mask != other.mask) {
            return false;
        }
        if (!compareValues) {
            return true;
        }
        this.sort();
        other.sort();
        for (int i = 0; i < this.attributes.size; ++i) {
            if (!this.attributes.get(i).equals(other.attributes.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    public final boolean same(final Attributes other) {
        return this.same(other, false);
    }
    
    @Override
    public final int compare(final Attribute arg0, final Attribute arg1) {
        return (int)(arg0.type - arg1.type);
    }
    
    @Override
    public final Iterator<Attribute> iterator() {
        return this.attributes.iterator();
    }
    
    public int attributesHash() {
        this.sort();
        final int n = this.attributes.size;
        long result = 71L + this.mask;
        int m = 1;
        for (int i = 0; i < n; ++i) {
            result += this.mask * this.attributes.get(i).hashCode() * (m = (m * 7 & 0xFFFF));
        }
        return (int)(result ^ result >> 32);
    }
    
    @Override
    public int hashCode() {
        return this.attributesHash();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof Attributes && (other == this || this.same((Attributes)other, true));
    }
    
    @Override
    public int compareTo(final Attributes other) {
        if (other == this) {
            return 0;
        }
        if (this.mask != other.mask) {
            return (this.mask < other.mask) ? -1 : 1;
        }
        this.sort();
        other.sort();
        for (int i = 0; i < this.attributes.size; ++i) {
            final int c = this.attributes.get(i).compareTo(other.attributes.get(i));
            if (c != 0) {
                return (c < 0) ? -1 : ((c > 0) ? 1 : 0);
            }
        }
        return 0;
    }
}
