// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.math.MathUtils;

public class ScaledNumericValue extends RangedNumericValue
{
    private float[] scaling;
    public float[] timeline;
    private float highMin;
    private float highMax;
    private boolean relative;
    
    public ScaledNumericValue() {
        this.scaling = new float[] { 1.0f };
        this.timeline = new float[] { 0.0f };
        this.relative = false;
    }
    
    public float newHighValue() {
        return this.highMin + (this.highMax - this.highMin) * MathUtils.random();
    }
    
    public void setHigh(final float value) {
        this.highMin = value;
        this.highMax = value;
    }
    
    public void setHigh(final float min, final float max) {
        this.highMin = min;
        this.highMax = max;
    }
    
    public float getHighMin() {
        return this.highMin;
    }
    
    public void setHighMin(final float highMin) {
        this.highMin = highMin;
    }
    
    public float getHighMax() {
        return this.highMax;
    }
    
    public void setHighMax(final float highMax) {
        this.highMax = highMax;
    }
    
    public float[] getScaling() {
        return this.scaling;
    }
    
    public void setScaling(final float[] values) {
        this.scaling = values;
    }
    
    public float[] getTimeline() {
        return this.timeline;
    }
    
    public void setTimeline(final float[] timeline) {
        this.timeline = timeline;
    }
    
    public boolean isRelative() {
        return this.relative;
    }
    
    public void setRelative(final boolean relative) {
        this.relative = relative;
    }
    
    public float getScale(final float percent) {
        int endIndex = -1;
        final int n = this.timeline.length;
        for (int i = 1; i < n; ++i) {
            final float t = this.timeline[i];
            if (t > percent) {
                endIndex = i;
                break;
            }
        }
        if (endIndex == -1) {
            return this.scaling[n - 1];
        }
        final int startIndex = endIndex - 1;
        final float startValue = this.scaling[startIndex];
        final float startTime = this.timeline[startIndex];
        return startValue + (this.scaling[endIndex] - startValue) * ((percent - startTime) / (this.timeline[endIndex] - startTime));
    }
    
    public void load(final ScaledNumericValue value) {
        super.load(value);
        this.highMax = value.highMax;
        this.highMin = value.highMin;
        this.scaling = new float[value.scaling.length];
        System.arraycopy(value.scaling, 0, this.scaling, 0, this.scaling.length);
        this.timeline = new float[value.timeline.length];
        System.arraycopy(value.timeline, 0, this.timeline, 0, this.timeline.length);
        this.relative = value.relative;
    }
    
    @Override
    public void write(final Json json) {
        super.write(json);
        json.writeValue("highMin", this.highMin);
        json.writeValue("highMax", this.highMax);
        json.writeValue("relative", this.relative);
        json.writeValue("scaling", this.scaling);
        json.writeValue("timeline", this.timeline);
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
        super.read(json, jsonData);
        this.highMin = json.readValue("highMin", Float.TYPE, jsonData);
        this.highMax = json.readValue("highMax", Float.TYPE, jsonData);
        this.relative = json.readValue("relative", Boolean.TYPE, jsonData);
        this.scaling = json.readValue("scaling", float[].class, jsonData);
        this.timeline = json.readValue("timeline", float[].class, jsonData);
    }
}
