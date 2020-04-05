// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.attributes;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class FloatAttribute extends Attribute
{
    public static final String ShininessAlias = "shininess";
    public static final long Shininess;
    public static final String AlphaTestAlias = "alphaTest";
    public static final long AlphaTest;
    public float value;
    
    static {
        Shininess = Attribute.register("shininess");
        AlphaTest = Attribute.register("alphaTest");
    }
    
    public static FloatAttribute createShininess(final float value) {
        return new FloatAttribute(FloatAttribute.Shininess, value);
    }
    
    public static FloatAttribute createAlphaTest(final float value) {
        return new FloatAttribute(FloatAttribute.AlphaTest, value);
    }
    
    public FloatAttribute(final long type) {
        super(type);
    }
    
    public FloatAttribute(final long type, final float value) {
        super(type);
        this.value = value;
    }
    
    @Override
    public Attribute copy() {
        return new FloatAttribute(this.type, this.value);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 977 * result + NumberUtils.floatToRawIntBits(this.value);
        return result;
    }
    
    @Override
    public int compareTo(final Attribute o) {
        if (this.type != o.type) {
            return (int)(this.type - o.type);
        }
        final float v = ((FloatAttribute)o).value;
        return MathUtils.isEqual(this.value, v) ? 0 : ((this.value < v) ? -1 : 1);
    }
}
