// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.attributes;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class TextureAttribute extends Attribute
{
    public static final String DiffuseAlias = "diffuseTexture";
    public static final long Diffuse;
    public static final String SpecularAlias = "specularTexture";
    public static final long Specular;
    public static final String BumpAlias = "bumpTexture";
    public static final long Bump;
    public static final String NormalAlias = "normalTexture";
    public static final long Normal;
    public static final String AmbientAlias = "ambientTexture";
    public static final long Ambient;
    public static final String EmissiveAlias = "emissiveTexture";
    public static final long Emissive;
    public static final String ReflectionAlias = "reflectionTexture";
    public static final long Reflection;
    protected static long Mask;
    public final TextureDescriptor<Texture> textureDescription;
    public float offsetU;
    public float offsetV;
    public float scaleU;
    public float scaleV;
    public int uvIndex;
    
    static {
        Diffuse = Attribute.register("diffuseTexture");
        Specular = Attribute.register("specularTexture");
        Bump = Attribute.register("bumpTexture");
        Normal = Attribute.register("normalTexture");
        Ambient = Attribute.register("ambientTexture");
        Emissive = Attribute.register("emissiveTexture");
        Reflection = Attribute.register("reflectionTexture");
        TextureAttribute.Mask = (TextureAttribute.Diffuse | TextureAttribute.Specular | TextureAttribute.Bump | TextureAttribute.Normal | TextureAttribute.Ambient | TextureAttribute.Emissive | TextureAttribute.Reflection);
    }
    
    public static final boolean is(final long mask) {
        return (mask & TextureAttribute.Mask) != 0x0L;
    }
    
    public static TextureAttribute createDiffuse(final Texture texture) {
        return new TextureAttribute(TextureAttribute.Diffuse, texture);
    }
    
    public static TextureAttribute createDiffuse(final TextureRegion region) {
        return new TextureAttribute(TextureAttribute.Diffuse, region);
    }
    
    public static TextureAttribute createSpecular(final Texture texture) {
        return new TextureAttribute(TextureAttribute.Specular, texture);
    }
    
    public static TextureAttribute createSpecular(final TextureRegion region) {
        return new TextureAttribute(TextureAttribute.Specular, region);
    }
    
    public static TextureAttribute createNormal(final Texture texture) {
        return new TextureAttribute(TextureAttribute.Normal, texture);
    }
    
    public static TextureAttribute createNormal(final TextureRegion region) {
        return new TextureAttribute(TextureAttribute.Normal, region);
    }
    
    public static TextureAttribute createBump(final Texture texture) {
        return new TextureAttribute(TextureAttribute.Bump, texture);
    }
    
    public static TextureAttribute createBump(final TextureRegion region) {
        return new TextureAttribute(TextureAttribute.Bump, region);
    }
    
    public static TextureAttribute createAmbient(final Texture texture) {
        return new TextureAttribute(TextureAttribute.Ambient, texture);
    }
    
    public static TextureAttribute createAmbient(final TextureRegion region) {
        return new TextureAttribute(TextureAttribute.Ambient, region);
    }
    
    public static TextureAttribute createEmissive(final Texture texture) {
        return new TextureAttribute(TextureAttribute.Emissive, texture);
    }
    
    public static TextureAttribute createEmissive(final TextureRegion region) {
        return new TextureAttribute(TextureAttribute.Emissive, region);
    }
    
    public static TextureAttribute createReflection(final Texture texture) {
        return new TextureAttribute(TextureAttribute.Reflection, texture);
    }
    
    public static TextureAttribute createReflection(final TextureRegion region) {
        return new TextureAttribute(TextureAttribute.Reflection, region);
    }
    
    public TextureAttribute(final long type) {
        super(type);
        this.offsetU = 0.0f;
        this.offsetV = 0.0f;
        this.scaleU = 1.0f;
        this.scaleV = 1.0f;
        this.uvIndex = 0;
        if (!is(type)) {
            throw new GdxRuntimeException("Invalid type specified");
        }
        this.textureDescription = new TextureDescriptor<Texture>();
    }
    
    public <T extends Texture> TextureAttribute(final long type, final TextureDescriptor<T> textureDescription) {
        this(type);
        this.textureDescription.set(textureDescription);
    }
    
    public <T extends Texture> TextureAttribute(final long type, final TextureDescriptor<T> textureDescription, final float offsetU, final float offsetV, final float scaleU, final float scaleV, final int uvIndex) {
        this(type, textureDescription);
        this.offsetU = offsetU;
        this.offsetV = offsetV;
        this.scaleU = scaleU;
        this.scaleV = scaleV;
        this.uvIndex = uvIndex;
    }
    
    public <T extends Texture> TextureAttribute(final long type, final TextureDescriptor<T> textureDescription, final float offsetU, final float offsetV, final float scaleU, final float scaleV) {
        this(type, textureDescription, offsetU, offsetV, scaleU, scaleV, 0);
    }
    
    public TextureAttribute(final long type, final Texture texture) {
        this(type);
        this.textureDescription.texture = texture;
    }
    
    public TextureAttribute(final long type, final TextureRegion region) {
        this(type);
        this.set(region);
    }
    
    public TextureAttribute(final TextureAttribute copyFrom) {
        this(copyFrom.type, copyFrom.textureDescription, copyFrom.offsetU, copyFrom.offsetV, copyFrom.scaleU, copyFrom.scaleV, copyFrom.uvIndex);
    }
    
    public void set(final TextureRegion region) {
        this.textureDescription.texture = region.getTexture();
        this.offsetU = region.getU();
        this.offsetV = region.getV();
        this.scaleU = region.getU2() - this.offsetU;
        this.scaleV = region.getV2() - this.offsetV;
    }
    
    @Override
    public Attribute copy() {
        return new TextureAttribute(this);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 991 * result + this.textureDescription.hashCode();
        result = 991 * result + NumberUtils.floatToRawIntBits(this.offsetU);
        result = 991 * result + NumberUtils.floatToRawIntBits(this.offsetV);
        result = 991 * result + NumberUtils.floatToRawIntBits(this.scaleU);
        result = 991 * result + NumberUtils.floatToRawIntBits(this.scaleV);
        result = 991 * result + this.uvIndex;
        return result;
    }
    
    @Override
    public int compareTo(final Attribute o) {
        if (this.type != o.type) {
            return (this.type < o.type) ? -1 : 1;
        }
        final TextureAttribute other = (TextureAttribute)o;
        final int c = this.textureDescription.compareTo(other.textureDescription);
        if (c != 0) {
            return c;
        }
        if (this.uvIndex != other.uvIndex) {
            return this.uvIndex - other.uvIndex;
        }
        if (!MathUtils.isEqual(this.scaleU, other.scaleU)) {
            return (this.scaleU > other.scaleU) ? 1 : -1;
        }
        if (!MathUtils.isEqual(this.scaleV, other.scaleV)) {
            return (this.scaleV > other.scaleV) ? 1 : -1;
        }
        if (!MathUtils.isEqual(this.offsetU, other.offsetU)) {
            return (this.offsetU > other.offsetU) ? 1 : -1;
        }
        if (!MathUtils.isEqual(this.offsetV, other.offsetV)) {
            return (this.offsetV > other.offsetV) ? 1 : -1;
        }
        return 0;
    }
}
