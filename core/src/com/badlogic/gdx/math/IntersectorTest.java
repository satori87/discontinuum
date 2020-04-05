// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import org.junit.Test;
import org.junit.Assert;

public class IntersectorTest
{
    private static boolean triangleEquals(final float[] base, final int baseOffset, final int stride, final float[] comp) {
        Assert.assertTrue(stride >= 3);
        Assert.assertTrue(base.length - baseOffset >= 9);
        Assert.assertTrue(comp.length == 9);
        int offset = -1;
        for (int i = 0; i < 3; ++i) {
            final int b = baseOffset + i * stride;
            if (MathUtils.isEqual(base[b], comp[0]) && MathUtils.isEqual(base[b + 1], comp[1]) && MathUtils.isEqual(base[b + 2], comp[2])) {
                offset = i;
                break;
            }
        }
        Assert.assertTrue("Triangles do not have common first vertex.", offset != -1);
        for (int i = 0; i < 3; ++i) {
            final int b = baseOffset + (offset + i) * stride % (3 * stride);
            final int c = i * stride;
            if (!MathUtils.isEqual(base[b], comp[c]) || !MathUtils.isEqual(base[b + 1], comp[c + 1]) || !MathUtils.isEqual(base[b + 2], comp[c + 2])) {
                return false;
            }
        }
        return true;
    }
    
    @Test
    public void testSplitTriangle() {
        final Plane plane = new Plane(new Vector3(1.0f, 0.0f, 0.0f), 0.0f);
        final Intersector.SplitTriangle split = new Intersector.SplitTriangle(3);
        float[] fTriangle = { -10.0f, 0.0f, 10.0f, -1.0f, 0.0f, 0.0f, -12.0f, 0.0f, 10.0f };
        Intersector.splitTriangle(fTriangle, plane, split);
        Assert.assertTrue(split.numBack == 1);
        Assert.assertTrue(split.numFront == 0);
        Assert.assertTrue(split.total == 1);
        Assert.assertTrue(triangleEquals(split.back, 0, 3, fTriangle));
        fTriangle[4] = 5.0f;
        Assert.assertFalse("Test is broken", triangleEquals(split.back, 0, 3, fTriangle));
        fTriangle = new float[] { 10.0f, 0.0f, 10.0f, 1.0f, 0.0f, 0.0f, 12.0f, 0.0f, 10.0f };
        Intersector.splitTriangle(fTriangle, plane, split);
        Assert.assertTrue(split.numBack == 0);
        Assert.assertTrue(split.numFront == 1);
        Assert.assertTrue(split.total == 1);
        Assert.assertTrue(triangleEquals(split.front, 0, 3, fTriangle));
        float[] triangle = { -10.0f, 0.0f, 10.0f, 10.0f, 0.0f, 0.0f, -10.0f, 0.0f, -10.0f };
        Intersector.splitTriangle(triangle, plane, split);
        Assert.assertTrue(split.numBack == 2);
        Assert.assertTrue(split.numFront == 1);
        Assert.assertTrue(split.total == 3);
        Assert.assertTrue(triangleEquals(split.front, 0, 3, new float[] { 0.0f, 0.0f, 5.0f, 10.0f, 0.0f, 0.0f, 0.0f, 0.0f, -5.0f }));
        float[][] firstWay = { { -10.0f, 0.0f, 10.0f, 0.0f, 0.0f, 5.0f, 0.0f, 0.0f, -5.0f }, { -10.0f, 0.0f, 10.0f, 0.0f, 0.0f, -5.0f, -10.0f, 0.0f, -10.0f } };
        float[][] secondWay = { { -10.0f, 0.0f, 10.0f, 0.0f, 0.0f, 5.0f, -10.0f, 0.0f, -10.0f }, { 0.0f, 0.0f, 5.0f, 0.0f, 0.0f, -5.0f, -10.0f, 0.0f, -10.0f } };
        float[] base = split.back;
        boolean first = (triangleEquals(base, 0, 3, firstWay[0]) && triangleEquals(base, 9, 3, firstWay[1])) || (triangleEquals(base, 0, 3, firstWay[1]) && triangleEquals(base, 9, 3, firstWay[0]));
        boolean second = (triangleEquals(base, 0, 3, secondWay[0]) && triangleEquals(base, 9, 3, secondWay[1])) || (triangleEquals(base, 0, 3, secondWay[1]) && triangleEquals(base, 9, 3, secondWay[0]));
        Assert.assertTrue("Either first or second way must be right (first: " + first + ", second: " + second + ")", first ^ second);
        triangle = new float[] { 10.0f, 0.0f, 10.0f, -10.0f, 0.0f, 0.0f, 10.0f, 0.0f, -10.0f };
        Intersector.splitTriangle(triangle, plane, split);
        Assert.assertTrue(split.numBack == 1);
        Assert.assertTrue(split.numFront == 2);
        Assert.assertTrue(split.total == 3);
        Assert.assertTrue(triangleEquals(split.back, 0, 3, new float[] { 0.0f, 0.0f, 5.0f, -10.0f, 0.0f, 0.0f, 0.0f, 0.0f, -5.0f }));
        firstWay = new float[][] { { 10.0f, 0.0f, 10.0f, 0.0f, 0.0f, 5.0f, 0.0f, 0.0f, -5.0f }, { 10.0f, 0.0f, 10.0f, 0.0f, 0.0f, -5.0f, 10.0f, 0.0f, -10.0f } };
        secondWay = new float[][] { { 10.0f, 0.0f, 10.0f, 0.0f, 0.0f, 5.0f, 10.0f, 0.0f, -10.0f }, { 0.0f, 0.0f, 5.0f, 0.0f, 0.0f, -5.0f, 10.0f, 0.0f, -10.0f } };
        base = split.front;
        first = ((triangleEquals(base, 0, 3, firstWay[0]) && triangleEquals(base, 9, 3, firstWay[1])) || (triangleEquals(base, 0, 3, firstWay[1]) && triangleEquals(base, 9, 3, firstWay[0])));
        second = ((triangleEquals(base, 0, 3, secondWay[0]) && triangleEquals(base, 9, 3, secondWay[1])) || (triangleEquals(base, 0, 3, secondWay[1]) && triangleEquals(base, 9, 3, secondWay[0])));
        Assert.assertTrue("Either first or second way must be right (first: " + first + ", second: " + second + ")", first ^ second);
    }
}
