// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import java.util.Iterator;
import com.badlogic.gdx.utils.Array;

public class CumulativeDistribution<T>
{
    private Array<CumulativeValue> values;
    
    public CumulativeDistribution() {
        this.values = new Array<CumulativeValue>(false, 10, CumulativeValue.class);
    }
    
    public void add(final T value, final float intervalSize) {
        this.values.add(new CumulativeValue(value, 0.0f, intervalSize));
    }
    
    public void add(final T value) {
        this.values.add(new CumulativeValue(value, 0.0f, 0.0f));
    }
    
    public void generate() {
        float sum = 0.0f;
        for (int i = 0; i < this.values.size; ++i) {
            sum += this.values.items[i].interval;
            this.values.items[i].frequency = sum;
        }
    }
    
    public void generateNormalized() {
        float sum = 0.0f;
        for (int i = 0; i < this.values.size; ++i) {
            sum += this.values.items[i].interval;
        }
        float intervalSum = 0.0f;
        for (int j = 0; j < this.values.size; ++j) {
            intervalSum += this.values.items[j].interval / sum;
            this.values.items[j].frequency = intervalSum;
        }
    }
    
    public void generateUniform() {
        final float freq = 1.0f / this.values.size;
        for (int i = 0; i < this.values.size; ++i) {
            this.values.items[i].interval = freq;
            this.values.items[i].frequency = (i + 1) * freq;
        }
    }
    
    public T value(final float probability) {
        CumulativeValue value = null;
        int imax = this.values.size - 1;
        int imin = 0;
        while (imin <= imax) {
            final int imid = imin + (imax - imin) / 2;
            value = this.values.items[imid];
            if (probability < value.frequency) {
                imax = imid - 1;
            }
            else {
                if (probability <= value.frequency) {
                    break;
                }
                imin = imid + 1;
            }
        }
        return this.values.items[imin].value;
    }
    
    public T value() {
        return this.value(MathUtils.random());
    }
    
    public int size() {
        return this.values.size;
    }
    
    public float getInterval(final int index) {
        return this.values.items[index].interval;
    }
    
    public T getValue(final int index) {
        return this.values.items[index].value;
    }
    
    public void setInterval(final T obj, final float intervalSize) {
        for (final CumulativeValue value : this.values) {
            if (value.value == obj) {
                value.interval = intervalSize;
            }
        }
    }
    
    public void setInterval(final int index, final float intervalSize) {
        this.values.items[index].interval = intervalSize;
    }
    
    public void clear() {
        this.values.clear();
    }
    
    public class CumulativeValue
    {
        public T value;
        public float frequency;
        public float interval;
        
        public CumulativeValue(final T value, final float frequency, final float interval) {
            this.value = value;
            this.frequency = frequency;
            this.interval = interval;
        }
    }
}
