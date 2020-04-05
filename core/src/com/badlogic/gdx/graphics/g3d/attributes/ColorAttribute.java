// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.attributes;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class ColorAttribute extends Attribute
{
    public static final String DiffuseAlias = "diffuseColor";
    public static final long Diffuse;
    public static final String SpecularAlias = "specularColor";
    public static final long Specular;
    public static final String AmbientAlias = "ambientColor";
    public static final long Ambient;
    public static final String EmissiveAlias = "emissiveColor";
    public static final long Emissive;
    public static final String ReflectionAlias = "reflectionColor";
    public static final long Reflection;
    public static final String AmbientLightAlias = "ambientLightColor";
    public static final long AmbientLight;
    public static final String FogAlias = "fogColor";
    public static final long Fog;
    protected static long Mask;
    public final Color color;
    
    static {
        Diffuse = Attribute.register("diffuseColor");
        Specular = Attribute.register("specularColor");
        Ambient = Attribute.register("ambientColor");
        Emissive = Attribute.register("emissiveColor");
        Reflection = Attribute.register("reflectionColor");
        AmbientLight = Attribute.register("ambientLightColor");
        Fog = Attribute.register("fogColor");
        ColorAttribute.Mask = (ColorAttribute.Ambient | ColorAttribute.Diffuse | ColorAttribute.Specular | ColorAttribute.Emissive | ColorAttribute.Reflection | ColorAttribute.AmbientLight | ColorAttribute.Fog);
    }
    
    public static final boolean is(final long mask) {
        return (mask & ColorAttribute.Mask) != 0x0L;
    }
    
    public static final ColorAttribute createAmbient(final Color color) {
        return new ColorAttribute(ColorAttribute.Ambient, color);
    }
    
    public static final ColorAttribute createAmbient(final float r, final float g, final float b, final float a) {
        return new ColorAttribute(ColorAttribute.Ambient, r, g, b, a);
    }
    
    public static final ColorAttribute createDiffuse(final Color color) {
        return new ColorAttribute(ColorAttribute.Diffuse, color);
    }
    
    public static final ColorAttribute createDiffuse(final float r, final float g, final float b, final float a) {
        return new ColorAttribute(ColorAttribute.Diffuse, r, g, b, a);
    }
    
    public static final ColorAttribute createSpecular(final Color color) {
        return new ColorAttribute(ColorAttribute.Specular, color);
    }
    
    public static final ColorAttribute createSpecular(final float r, final float g, final float b, final float a) {
        return new ColorAttribute(ColorAttribute.Specular, r, g, b, a);
    }
    
    public static final ColorAttribute createReflection(final Color color) {
        return new ColorAttribute(ColorAttribute.Reflection, color);
    }
    
    public static final ColorAttribute createReflection(final float r, final float g, final float b, final float a) {
        return new ColorAttribute(ColorAttribute.Reflection, r, g, b, a);
    }
    
    public ColorAttribute(final long type) {
        super(type);
        this.color = new Color();
        if (!is(type)) {
            throw new GdxRuntimeException("Invalid type specified");
        }
    }
    
    public ColorAttribute(final long type, final Color color) {
        this(type);
        if (color != null) {
            this.color.set(color);
        }
    }
    
    public ColorAttribute(final long type, final float r, final float g, final float b, final float a) {
        this(type);
        this.color.set(r, g, b, a);
    }
    
    public ColorAttribute(final ColorAttribute copyFrom) {
        this(copyFrom.type, copyFrom.color);
    }
    
    @Override
    public Attribute copy() {
        return new ColorAttribute(this);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 953 * result + this.color.toIntBits();
        return result;
    }
    
    @Override
    public int compareTo(final Attribute o) {
        if (this.type != o.type) {
            return (int)(this.type - o.type);
        }
        return ((ColorAttribute)o).color.toIntBits() - this.color.toIntBits();
    }
}
