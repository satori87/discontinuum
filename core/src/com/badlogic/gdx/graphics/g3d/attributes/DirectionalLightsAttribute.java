// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.attributes;

import java.util.Iterator;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class DirectionalLightsAttribute extends Attribute
{
    public static final String Alias = "directionalLights";
    public static final long Type;
    public final Array<DirectionalLight> lights;
    
    static {
        Type = Attribute.register("directionalLights");
    }
    
    public static final boolean is(final long mask) {
        return (mask & DirectionalLightsAttribute.Type) == mask;
    }
    
    public DirectionalLightsAttribute() {
        super(DirectionalLightsAttribute.Type);
        this.lights = new Array<DirectionalLight>(1);
    }
    
    public DirectionalLightsAttribute(final DirectionalLightsAttribute copyFrom) {
        this();
        this.lights.addAll(copyFrom.lights);
    }
    
    @Override
    public DirectionalLightsAttribute copy() {
        return new DirectionalLightsAttribute(this);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        for (final DirectionalLight light : this.lights) {
            result = 1229 * result + ((light == null) ? 0 : light.hashCode());
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
