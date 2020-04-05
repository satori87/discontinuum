// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;

public class GradientColorValue extends ParticleValue
{
    private static float[] temp;
    private float[] colors;
    public float[] timeline;
    
    static {
        GradientColorValue.temp = new float[3];
    }
    
    public GradientColorValue() {
        this.colors = new float[] { 1.0f, 1.0f, 1.0f };
        this.timeline = new float[] { 0.0f };
    }
    
    public float[] getTimeline() {
        return this.timeline;
    }
    
    public void setTimeline(final float[] timeline) {
        this.timeline = timeline;
    }
    
    public float[] getColors() {
        return this.colors;
    }
    
    public void setColors(final float[] colors) {
        this.colors = colors;
    }
    
    public float[] getColor(final float percent) {
        this.getColor(percent, GradientColorValue.temp, 0);
        return GradientColorValue.temp;
    }
    
    public void getColor(final float percent, final float[] out, final int index) {
        int startIndex = 0;
        int endIndex = -1;
        final float[] timeline = this.timeline;
        for (int n = timeline.length, i = 1; i < n; ++i) {
            final float t = timeline[i];
            if (t > percent) {
                endIndex = i;
                break;
            }
            startIndex = i;
        }
        final float startTime = timeline[startIndex];
        startIndex *= 3;
        final float r1 = this.colors[startIndex];
        final float g1 = this.colors[startIndex + 1];
        final float b1 = this.colors[startIndex + 2];
        if (endIndex == -1) {
            out[index] = r1;
            out[index + 1] = g1;
            out[index + 2] = b1;
            return;
        }
        final float factor = (percent - startTime) / (timeline[endIndex] - startTime);
        endIndex *= 3;
        out[index] = r1 + (this.colors[endIndex] - r1) * factor;
        out[index + 1] = g1 + (this.colors[endIndex + 1] - g1) * factor;
        out[index + 2] = b1 + (this.colors[endIndex + 2] - b1) * factor;
    }
    
    @Override
    public void write(final Json json) {
        super.write(json);
        json.writeValue("colors", this.colors);
        json.writeValue("timeline", this.timeline);
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
        super.read(json, jsonData);
        this.colors = json.readValue("colors", float[].class, jsonData);
        this.timeline = json.readValue("timeline", float[].class, jsonData);
    }
    
    public void load(final GradientColorValue value) {
        super.load(value);
        this.colors = new float[value.colors.length];
        System.arraycopy(value.colors, 0, this.colors, 0, this.colors.length);
        this.timeline = new float[value.timeline.length];
        System.arraycopy(value.timeline, 0, this.timeline, 0, this.timeline.length);
    }
}
