// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.attributes;

import java.util.Iterator;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class PointLightsAttribute extends Attribute
{
    public static final String Alias = "pointLights";
    public static final long Type;
    public final Array<PointLight> lights;
    
    static {
        Type = Attribute.register("pointLights");
    }
    
    public static final boolean is(final long mask) {
        return (mask & PointLightsAttribute.Type) == mask;
    }
    
    public PointLightsAttribute() {
        super(PointLightsAttribute.Type);
        this.lights = new Array<PointLight>(1);
    }
    
    public PointLightsAttribute(final PointLightsAttribute copyFrom) {
        this();
        this.lights.addAll(copyFrom.lights);
    }
    
    @Override
    public PointLightsAttribute copy() {
        return new PointLightsAttribute(this);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        for (final PointLight light : this.lights) {
            result = 1231 * result + ((light == null) ? 0 : light.hashCode());
        }
        return result;
    }
    
    @Override
    public int compareTo(final Attribute o) {
        if (this.type != o.type) {
            return (this.type < o.type) ? -1 : 1;
        }
        return 0;
    }
}
