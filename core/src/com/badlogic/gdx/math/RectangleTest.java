// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import org.junit.Test;
import org.junit.Assert;

public class RectangleTest
{
    @Test
    public void testToString() {
        Assert.assertEquals((Object)"[5.0,-4.1,0.03,-0.02]", (Object)new Rectangle(5.0f, -4.1f, 0.03f, -0.02f).toString());
    }
    
    @Test
    public void testFromString() {
        Assert.assertEquals((Object)new Rectangle(5.0f, -4.1f, 0.03f, -0.02f), (Object)new Rectangle().fromString("[5.0,-4.1,0.03,-0.02]"));
    }
}
