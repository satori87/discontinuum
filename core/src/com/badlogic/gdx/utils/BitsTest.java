// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import org.junit.Test;
import org.junit.Assert;

public class BitsTest
{
    @Test
    public void testHashcodeAndEquals() {
        final Bits b1 = new Bits();
        final Bits b2 = new Bits();
        b1.set(1);
        b2.set(1);
        Assert.assertEquals((long)b1.hashCode(), (long)b2.hashCode());
        Assert.assertTrue(b1.equals(b2));
        b2.set(420);
        b2.clear(420);
        Assert.assertEquals((long)b1.hashCode(), (long)b2.hashCode());
        Assert.assertTrue(b1.equals(b2));
        b1.set(810);
        b1.clear(810);
        Assert.assertEquals((long)b1.hashCode(), (long)b2.hashCode());
        Assert.assertTrue(b1.equals(b2));
    }
    
    @Test
    public void testXor() {
        final Bits b1 = new Bits();
        final Bits b2 = new Bits();
        b2.set(200);
        b1.xor(b2);
        Assert.assertTrue(b1.get(200));
        b1.set(1024);
        b2.xor(b1);
        Assert.assertTrue(b2.get(1024));
    }
    
    @Test
    public void testOr() {
        final Bits b1 = new Bits();
        final Bits b2 = new Bits();
        b2.set(200);
        b1.or(b2);
        Assert.assertTrue(b1.get(200));
        b1.set(1024);
        b2.or(b1);
        Assert.assertTrue(b2.get(1024));
    }
    
    @Test
    public void testAnd() {
        final Bits b1 = new Bits();
        final Bits b2 = new Bits();
        b2.set(200);
        b2.and(b1);
        Assert.assertFalse(b2.get(200));
        b1.set(400);
        b1.and(b2);
        Assert.assertFalse(b1.get(400));
    }
}
