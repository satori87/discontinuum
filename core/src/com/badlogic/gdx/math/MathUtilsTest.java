// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import org.junit.Test;
import org.junit.Assert;

public class MathUtilsTest
{
    @Test
    public void lerpAngleDeg() {
        Assert.assertEquals(10.0f, MathUtils.lerpAngleDeg(10.0f, 30.0f, 0.0f), 0.01f);
        Assert.assertEquals(20.0f, MathUtils.lerpAngleDeg(10.0f, 30.0f, 0.5f), 0.01f);
        Assert.assertEquals(30.0f, MathUtils.lerpAngleDeg(10.0f, 30.0f, 1.0f), 0.01f);
    }
    
    @Test
    public void lerpAngleDegCrossingZero() {
        Assert.assertEquals(350.0f, MathUtils.lerpAngleDeg(350.0f, 10.0f, 0.0f), 0.01f);
        Assert.assertEquals(0.0f, MathUtils.lerpAngleDeg(350.0f, 10.0f, 0.5f), 0.01f);
        Assert.assertEquals(10.0f, MathUtils.lerpAngleDeg(350.0f, 10.0f, 1.0f), 0.01f);
    }
    
    @Test
    public void lerpAngleDegCrossingZeroBackwards() {
        Assert.assertEquals(10.0f, MathUtils.lerpAngleDeg(10.0f, 350.0f, 0.0f), 0.01f);
        Assert.assertEquals(0.0f, MathUtils.lerpAngleDeg(10.0f, 350.0f, 0.5f), 0.01f);
        Assert.assertEquals(350.0f, MathUtils.lerpAngleDeg(10.0f, 350.0f, 1.0f), 0.01f);
    }
}
