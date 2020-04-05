// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import org.junit.Test;
import org.junit.Assert;

public class Shape2DTest
{
    @Test
    public void testCircle() {
        final Circle c1 = new Circle(0.0f, 0.0f, 1.0f);
        final Circle c2 = new Circle(0.0f, 0.0f, 1.0f);
        final Circle c3 = new Circle(2.0f, 0.0f, 1.0f);
        final Circle c4 = new Circle(0.0f, 0.0f, 2.0f);
        Assert.assertTrue(c1.overlaps(c1));
        Assert.assertTrue(c1.overlaps(c2));
        Assert.assertFalse(c1.overlaps(c3));
        Assert.assertTrue(c1.overlaps(c4));
        Assert.assertTrue(c4.overlaps(c1));
        Assert.assertTrue(c1.contains(0.0f, 1.0f));
        Assert.assertFalse(c1.contains(0.0f, 2.0f));
        Assert.assertTrue(c1.contains(c1));
        Assert.assertFalse(c1.contains(c4));
        Assert.assertTrue(c4.contains(c1));
    }
    
    @Test
    public void testRectangle() {
        final Rectangle r1 = new Rectangle(0.0f, 0.0f, 1.0f, 1.0f);
        final Rectangle r2 = new Rectangle(1.0f, 0.0f, 2.0f, 1.0f);
        Assert.assertTrue(r1.overlaps(r1));
        Assert.assertFalse(r1.overlaps(r2));
        Assert.assertTrue(r1.contains(0.0f, 0.0f));
    }
}
