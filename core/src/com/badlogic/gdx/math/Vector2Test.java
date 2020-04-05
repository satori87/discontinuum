// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import org.junit.Test;
import org.junit.Assert;

public class Vector2Test
{
    @Test
    public void testToString() {
        Assert.assertEquals((Object)"(-5.0,42.00055)", (Object)new Vector2(-5.0f, 42.00055f).toString());
    }
    
    @Test
    public void testFromString() {
        Assert.assertEquals((Object)new Vector2(-5.0f, 42.00055f), (Object)new Vector2().fromString("(-5,42.00055)"));
    }
}
