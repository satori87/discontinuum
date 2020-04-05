// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math.collision;

import org.junit.Test;
import org.junit.Assert;
import com.badlogic.gdx.math.Vector3;

public class CollisionTest
{
    @Test
    public void testBoundingBox() {
        final BoundingBox b1 = new BoundingBox(Vector3.Zero, new Vector3(1.0f, 1.0f, 1.0f));
        final BoundingBox b2 = new BoundingBox(new Vector3(1.0f, 1.0f, 1.0f), new Vector3(2.0f, 2.0f, 2.0f));
        Assert.assertTrue(b1.contains(Vector3.Zero));
        Assert.assertTrue(b1.contains(b1));
        Assert.assertFalse(b1.contains(b2));
    }
}
