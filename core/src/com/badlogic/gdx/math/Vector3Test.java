// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import org.junit.Test;
import org.junit.Assert;

public class Vector3Test
{
    @Test
    public void testToString() {
        Assert.assertEquals((Object)"(-5.0,42.00055,44444.32)", (Object)new Vector3(-5.0f, 42.00055f, 44444.32f).toString());
    }
    
    @Test
    public void testFromString() {
        Assert.assertEquals((Object)new Vector3(-5.0f, 42.00055f, 44444.32f), (Object)new Vector3().fromString("(-5,42.00055,44444.32)"));
    }
}
