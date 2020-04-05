// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

public class PooledLinkedListTest
{
    private PooledLinkedList<Integer> list;
    
    @Before
    public void setUp() {
        (this.list = new PooledLinkedList<Integer>(10)).add(1);
        this.list.add(2);
        this.list.add(3);
    }
    
    @Test
    public void size() {
        Assert.assertEquals(3L, (long)this.list.size());
        this.list.iter();
        this.list.next();
        this.list.remove();
        Assert.assertEquals(2L, (long)this.list.size());
    }
    
    @Test
    public void iteration() {
        this.list.iter();
        Assert.assertEquals((Object)1, (Object)this.list.next());
        Assert.assertEquals((Object)2, (Object)this.list.next());
        Assert.assertEquals((Object)3, (Object)this.list.next());
        Assert.assertNull((Object)this.list.next());
    }
    
    @Test
    public void reverseIteration() {
        this.list.iterReverse();
        Assert.assertEquals((Object)3, (Object)this.list.previous());
        Assert.assertEquals((Object)2, (Object)this.list.previous());
        Assert.assertEquals((Object)1, (Object)this.list.previous());
        Assert.assertNull((Object)this.list.previous());
    }
    
    @Test
    public void remove() {
        this.list.iter();
        this.list.next();
        this.list.remove();
        this.list.next();
        this.list.next();
        this.list.remove();
        this.list.iter();
        Assert.assertEquals((Object)2, (Object)this.list.next());
        Assert.assertNull((Object)this.list.next());
    }
    
    @Test
    public void clear() {
        this.list.clear();
        Assert.assertEquals(0L, (long)this.list.size());
        this.list.iter();
        Assert.assertNull((Object)this.list.next());
    }
}
