// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.attributes;

import java.util.Iterator;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class SpotLightsAttribute extends Attribute
{
    public static final String Alias = "spotLights";
    public static final long Type;
    public final Array<SpotLight> lights;
    
    static {
        Type = Attribute.register("spotLights");
    }
    
    public static final boolean is(final long mask) {
        return (mask & SpotLightsAttribute.Type) == mask;
    }
    
    public SpotLightsAttribute() {
        super(SpotLightsAttribute.Type);
        this.lights = new Array<SpotLight>(1);
    }
    
    public SpotLightsAttribute(final SpotLightsAttribute copyFrom) {
        this();
        this.lights.addAll(copyFrom.lights);
    }
    
    @Override
    public SpotLightsAttribute copy() {
        return new SpotLightsAttribute(this);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        for (final SpotLight light : this.lights) {
            result = 1237 * result + ((light == null) ? 0 : light.hashCode());
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
