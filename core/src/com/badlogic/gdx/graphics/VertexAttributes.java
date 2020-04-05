// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import java.util.NoSuchElementException;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Iterator;

public final class VertexAttributes implements Iterable<VertexAttribute>, Comparable<VertexAttributes>
{
    private final VertexAttribute[] attributes;
    public final int vertexSize;
    private long mask;
    private ReadonlyIterable<VertexAttribute> iterable;
    
    public VertexAttributes(final VertexAttribute... attributes) {
        this.mask = -1L;
        if (attributes.length == 0) {
            throw new IllegalArgumentException("attributes must be >= 1");
        }
        final VertexAttribute[] list = new VertexAttribute[attributes.length];
        for (int i = 0; i < attributes.length; ++i) {
            list[i] = attributes[i];
        }
        this.attributes = list;
        this.vertexSize = this.calculateOffsets();
    }
    
    public int getOffset(final int usage, final int defaultIfNotFound) {
        final VertexAttribute vertexAttribute = this.findByUsage(usage);
        if (vertexAttribute == null) {
            return defaultIfNotFound;
        }
        return vertexAttribute.offset / 4;
    }
    
    public int getOffset(final int usage) {
        return this.getOffset(usage, 0);
    }
    
    public VertexAttribute findByUsage(final int usage) {
        for (int len = this.size(), i = 0; i < len; ++i) {
            if (this.get(i).usage == usage) {
                return this.get(i);
            }
        }
        return null;
    }
    
    private int calculateOffsets() {
        int count = 0;
        for (int i = 0; i < this.attributes.length; ++i) {
            final VertexAttribute attribute = this.attributes[i];
            attribute.offset = count;
            count += attribute.getSizeInBytes();
        }
        return count;
    }
    
    public int size() {
        return this.attributes.length;
    }
    
    public VertexAttribute get(final int index) {
        return this.attributes[index];
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < this.attributes.length; ++i) {
            builder.append("(");
            builder.append(this.attributes[i].alias);
            builder.append(", ");
            builder.append(this.attributes[i].usage);
            builder.append(", ");
            builder.append(this.attributes[i].numComponents);
            builder.append(", ");
            builder.append(this.attributes[i].offset);
            builder.append(")");
            builder.append("\n");
        }
        builder.append("]");
        return builder.toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof VertexAttributes)) {
            return false;
        }
        final VertexAttributes other = (VertexAttributes)obj;
        if (this.attributes.length != other.attributes.length) {
            return false;
        }
        for (int i = 0; i < this.attributes.length; ++i) {
            if (!this.attributes[i].equals(other.attributes[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        long result = 61 * this.attributes.length;
        for (int i = 0; i < this.attributes.length; ++i) {
            result = result * 61L + this.attributes[i].hashCode();
        }
        return (int)(result ^ result >> 32);
    }
    
    public long getMask() {
        if (this.mask == -1L) {
            long result = 0L;
            for (int i = 0; i < this.attributes.length; ++i) {
                result |= this.attributes[i].usage;
            }
            this.mask = result;
        }
        return this.mask;
    }
    
    public long getMaskWithSizePacked() {
        return this.getMask() | (long)this.attributes.length << 32;
    }
    
    @Override
    public int compareTo(final VertexAttributes o) {
        if (this.attributes.length != o.attributes.length) {
            return this.attributes.length - o.attributes.length;
        }
        final long m1 = this.getMask();
        final long m2 = o.getMask();
        if (m1 != m2) {
            return (m1 < m2) ? -1 : 1;
        }
        for (int i = this.attributes.length - 1; i >= 0; --i) {
            final VertexAttribute va0 = this.attributes[i];
            final VertexAttribute va2 = o.attributes[i];
            if (va0.usage != va2.usage) {
                return va0.usage - va2.usage;
            }
            if (va0.unit != va2.unit) {
                return va0.unit - va2.unit;
            }
            if (va0.numComponents != va2.numComponents) {
                return va0.numComponents - va2.numComponents;
            }
            if (va0.normalized != va2.normalized) {
                return va0.normalized ? 1 : -1;
            }
            if (va0.type != va2.type) {
                return va0.type - va2.type;
            }
        }
        return 0;
    }
    
    @Override
    public Iterator<VertexAttribute> iterator() {
        if (this.iterable == null) {
            this.iterable = new ReadonlyIterable<VertexAttribute>(this.attributes);
        }
        return this.iterable.iterator();
    }
    
    public static final class Usage
    {
        public static final int Position = 1;
        public static final int ColorUnpacked = 2;
        public static final int ColorPacked = 4;
        public static final int Normal = 8;
        public static final int TextureCoordinates = 16;
        public static final int Generic = 32;
        public static final int BoneWeight = 64;
        public static final int Tangent = 128;
        public static final int BiNormal = 256;
    }
    
    private static class ReadonlyIterator<T> implements Iterator<T>, Iterable<T>
    {
        private final T[] array;
        int index;
        boolean valid;
        
        public ReadonlyIterator(final T[] array) {
            this.valid = true;
            this.array = array;
        }
        
        @Override
        public boolean hasNext() {
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.index < this.array.length;
        }
        
        @Override
        public T next() {
            if (this.index >= this.array.length) {
                throw new NoSuchElementException(String.valueOf(this.index));
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.array[this.index++];
        }
        
        @Override
        public void remove() {
            throw new GdxRuntimeException("Remove not allowed.");
        }
        
        public void reset() {
            this.index = 0;
        }
        
        @Override
        public Iterator<T> iterator() {
            return this;
        }
    }
    
    private static class ReadonlyIterable<T> implements Iterable<T>
    {
        private final T[] array;
        private ReadonlyIterator iterator1;
        private ReadonlyIterator iterator2;
        
        public ReadonlyIterable(final T[] array) {
            this.array = array;
        }
        
        @Override
        public Iterator<T> iterator() {
            if (this.iterator1 == null) {
                this.iterator1 = new ReadonlyIterator((T[])this.array);
                this.iterator2 = new ReadonlyIterator((T[])this.array);
            }
            if (!this.iterator1.valid) {
                this.iterator1.index = 0;
                this.iterator1.valid = true;
                this.iterator2.valid = false;
                return (Iterator<T>)this.iterator1;
            }
            this.iterator2.index = 0;
            this.iterator2.valid = true;
            this.iterator1.valid = false;
            return (Iterator<T>)this.iterator2;
        }
    }
}
