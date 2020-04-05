// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.attributes;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class CubemapAttribute extends Attribute
{
    public static final String EnvironmentMapAlias = "environmentCubemap";
    public static final long EnvironmentMap;
    protected static long Mask;
    public final TextureDescriptor<Cubemap> textureDescription;
    
    static {
        EnvironmentMap = Attribute.register("environmentCubemap");
        CubemapAttribute.Mask = CubemapAttribute.EnvironmentMap;
    }
    
    public static final boolean is(final long mask) {
        return (mask & CubemapAttribute.Mask) != 0x0L;
    }
    
    public CubemapAttribute(final long type) {
        super(type);
        if (!is(type)) {
            throw new GdxRuntimeException("Invalid type specified");
        }
        this.textureDescription = new TextureDescriptor<Cubemap>();
    }
    
    public <T extends Cubemap> CubemapAttribute(final long type, final TextureDescriptor<T> textureDescription) {
        this(type);
        this.textureDescription.set(textureDescription);
    }
    
    public CubemapAttribute(final long type, final Cubemap texture) {
        this(type);
        this.textureDescription.texture = texture;
    }
    
    public CubemapAttribute(final CubemapAttribute copyFrom) {
        this(copyFrom.type, copyFrom.textureDescription);
    }
    
    @Override
    public Attribute copy() {
        return new CubemapAttribute(this);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 967 * result + this.textureDescription.hashCode();
        return result;
    }
    
    @Override
    public int compareTo(final Attribute o) {
        if (this.type != o.type) {
            return (int)(this.type - o.type);
        }
        return this.textureDescription.compareTo(((CubemapAttribute)o).textureDescription);
    }
}
