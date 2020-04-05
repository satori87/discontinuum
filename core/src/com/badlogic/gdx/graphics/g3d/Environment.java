// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import java.util.Iterator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.ShadowMap;

public class Environment extends Attributes
{
    @Deprecated
    public ShadowMap shadowMap;
    
    public Environment add(final BaseLight... lights) {
        for (final BaseLight light : lights) {
            this.add(light);
        }
        return this;
    }
    
    public Environment add(final Array<BaseLight> lights) {
        for (final BaseLight light : lights) {
            this.add(light);
        }
        return this;
    }
    
    public Environment add(final BaseLight light) {
        if (light instanceof DirectionalLight) {
            this.add((DirectionalLight)light);
        }
        else if (light instanceof PointLight) {
            this.add((PointLight)light);
        }
        else {
            if (!(light instanceof SpotLight)) {
                throw new GdxRuntimeException("Unknown light type");
            }
            this.add((SpotLight)light);
        }
        return this;
    }
    
    public Environment add(final DirectionalLight light) {
        DirectionalLightsAttribute dirLights = (DirectionalLightsAttribute)this.get(DirectionalLightsAttribute.Type);
        if (dirLights == null) {
            this.set(dirLights = new DirectionalLightsAttribute());
        }
        dirLights.lights.add(light);
        return this;
    }
    
    public Environment add(final PointLight light) {
        PointLightsAttribute pointLights = (PointLightsAttribute)this.get(PointLightsAttribute.Type);
        if (pointLights == null) {
            this.set(pointLights = new PointLightsAttribute());
        }
        pointLights.lights.add(light);
        return this;
    }
    
    public Environment add(final SpotLight light) {
        SpotLightsAttribute spotLights = (SpotLightsAttribute)this.get(SpotLightsAttribute.Type);
        if (spotLights == null) {
            this.set(spotLights = new SpotLightsAttribute());
        }
        spotLights.lights.add(light);
        return this;
    }
    
    public Environment remove(final BaseLight... lights) {
        for (final BaseLight light : lights) {
            this.remove(light);
        }
        return this;
    }
    
    public Environment remove(final Array<BaseLight> lights) {
        for (final BaseLight light : lights) {
            this.remove(light);
        }
        return this;
    }
    
    public Environment remove(final BaseLight light) {
        if (light instanceof DirectionalLight) {
            this.remove((DirectionalLight)light);
        }
        else if (light instanceof PointLight) {
            this.remove((PointLight)light);
        }
        else {
            if (!(light instanceof SpotLight)) {
                throw new GdxRuntimeException("Unknown light type");
            }
            this.remove((SpotLight)light);
        }
        return this;
    }
    
    public Environment remove(final DirectionalLight light) {
        if (this.has(DirectionalLightsAttribute.Type)) {
            final DirectionalLightsAttribute dirLights = (DirectionalLightsAttribute)this.get(DirectionalLightsAttribute.Type);
            dirLights.lights.removeValue(light, false);
            if (dirLights.lights.size == 0) {
                this.remove(DirectionalLightsAttribute.Type);
            }
        }
        return this;
    }
    
    public Environment remove(final PointLight light) {
        if (this.has(PointLightsAttribute.Type)) {
            final PointLightsAttribute pointLights = (PointLightsAttribute)this.get(PointLightsAttribute.Type);
            pointLights.lights.removeValue(light, false);
            if (pointLights.lights.size == 0) {
                this.remove(PointLightsAttribute.Type);
            }
        }
        return this;
    }
    
    public Environment remove(final SpotLight light) {
        if (this.has(SpotLightsAttribute.Type)) {
            final SpotLightsAttribute spotLights = (SpotLightsAttribute)this.get(SpotLightsAttribute.Type);
            spotLights.lights.removeValue(light, false);
            if (spotLights.lights.size == 0) {
                this.remove(SpotLightsAttribute.Type);
            }
        }
        return this;
    }
}
