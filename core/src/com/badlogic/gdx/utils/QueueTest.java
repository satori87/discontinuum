// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.Iterator;
import org.junit.Test;
import org.junit.Assert;

public class QueueTest
{
    @Test
    public void resizableQueueTest() {
        final Queue<Integer> q = new Queue<Integer>(8);
        Assert.assertTrue("New queue is not empty!", q.size == 0);
        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < i; ++j) {
                try {
                    q.addLast(j);
                }
                catch (IllegalStateException e) {
                    Assert.fail("Failed to add element " + j + " (" + i + ")");
                }
                final Integer peeked = q.last();
                Assert.assertTrue("peekLast shows " + peeked + ", should be " + j + " (" + i + ")", peeked.equals(j));
                final int size = q.size;
                Assert.assertTrue("Size should be " + (j + 1) + " but is " + size + " (" + i + ")", size == j + 1);
            }
            if (i != 0) {
                final Integer peek = q.first();
                Assert.assertTrue("First thing is not zero but " + peek + " (" + i + ")", peek == 0);
            }
            for (int j = 0; j < i; ++j) {
                final Integer pop = q.removeFirst();
                Assert.assertTrue("Popped should be " + j + " but is " + pop + " (" + i + ")", pop == j);
                final int size = q.size;
                Assert.assertTrue("Size should be " + (i - 1 - j) + " but is " + size + " (" + i + ")", size == i - 1 - j);
            }
            Assert.assertTrue("Not empty after cycle " + i, q.size == 0);
        }
        for (int i = 0; i < 56; ++i) {
            q.addLast(42);
        }
        q.clear();
        Assert.assertTrue("Clear did not clear properly", q.size == 0);
    }
    
    @Test
    public void resizableDequeTest() {
        final Queue<Integer> q = new Queue<Integer>(8);
        Assert.assertTrue("New deque is not empty!", q.size == 0);
        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < i; ++j) {
                try {
                    q.addFirst(j);
                }
                catch (IllegalStateException e) {
                    Assert.fail("Failed to add element " + j + " (" + i + ")");
                }
                final Integer peeked = q.first();
                Assert.assertTrue("peek shows " + peeked + ", should be " + j + " (" + i + ")", peeked.equals(j));
                final int size = q.size;
                Assert.assertTrue("Size should be " + (j + 1) + " but is " + size + " (" + i + ")", size == j + 1);
            }
            if (i != 0) {
                final Integer peek = q.last();
                Assert.assertTrue("Last thing is not zero but " + peek + " (" + i + ")", peek == 0);
            }
            for (int j = 0; j < i; ++j) {
                final Integer pop = q.removeLast();
                Assert.assertTrue("Popped should be " + j + " but is " + pop + " (" + i + ")", pop == j);
                final int size = q.size;
                Assert.assertTrue("Size should be " + (i - 1 - j) + " but is " + size + " (" + i + ")", size == i - 1 - j);
            }
            Assert.assertTrue("Not empty after cycle " + i, q.size == 0);
        }
        for (int i = 0; i < 56; ++i) {
            q.addFirst(42);
        }
        q.clear();
        Assert.assertTrue("Clear did not clear properly", q.size == 0);
    }
    
    @Test
    public void getTest() {
        final Queue<Integer> q = new Queue<Integer>(7);
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 4; ++j) {
                q.addLast(j);
            }
            Assert.assertEquals("get(0) is not equal to peek (" + i + ")", (Object)q.get(0), (Object)q.first());
            Assert.assertEquals("get(size-1) is not equal to peekLast (" + i + ")", (Object)q.get(q.size - 1), (Object)q.last());
            for (int j = 0; j < 4; ++j) {
                Assert.assertTrue(q.get(j) == j);
            }
            for (int j = 0; j < 3; ++j) {
                q.removeFirst();
                Assert.assertEquals("get(0) is not equal to peek (" + i + ")", (Object)q.get(0), (Object)q.first());
            }
            q.removeFirst();
            assert q.size == 0;
            try {
                q.get(0);
                Assert.fail("get() on empty queue did not throw");
            }
            catch (IndexOutOfBoundsException ex) {}
        }
    }
    
    @Test
    public void removeTest() {
        final Queue<Integer> q = new Queue<Integer>();
        for (int j = 0; j <= 6; ++j) {
            q.addLast(j);
        }
        this.assertValues(q, 0, 1, 2, 3, 4, 5, 6);
        q.removeIndex(0);
        this.assertValues(q, 1, 2, 3, 4, 5, 6);
        q.removeIndex(1);
        this.assertValues(q, 1, 3, 4, 5, 6);
        q.removeIndex(4);
        this.assertValues(q, 1, 3, 4, 5);
        q.removeIndex(2);
        this.assertValues(q, 1, 3, 5);
        q.clear();
        for (int j = 2; j >= 0; --j) {
            q.addFirst(j);
        }
        for (int j = 3; j <= 6; ++j) {
            q.addLast(j);
        }
        this.assertValues(q, 0, 1, 2, 3, 4, 5, 6);
        q.removeIndex(1);
        this.assertValues(q, 0, 2, 3, 4, 5, 6);
        q.removeIndex(0);
        this.assertValues(q, 2, 3, 4, 5, 6);
        q.clear();
        for (int j = 2; j >= 0; --j) {
            q.addFirst(j);
        }
        for (int j = 3; j <= 6; ++j) {
            q.addLast(j);
        }
        this.assertValues(q, 0, 1, 2, 3, 4, 5, 6);
        q.removeIndex(5);
        this.assertValues(q, 0, 1, 2, 3, 4, 6);
        q.removeIndex(5);
        this.assertValues(q, 0, 1, 2, 3, 4);
    }
    
    @Test
    public void indexOfTest() {
        final Queue<Integer> q = new Queue<Integer>();
        for (int j = 0; j <= 6; ++j) {
            q.addLast(j);
        }
        for (int j = 0; j <= 6; ++j) {
            Assert.assertEquals((long)q.indexOf(j, false), (long)j);
        }
        q.clear();
        for (int j = 2; j >= 0; --j) {
            q.addFirst(j);
        }
        for (int j = 3; j <= 6; ++j) {
            q.addLast(j);
        }
        for (int j = 0; j <= 6; ++j) {
            Assert.assertEquals((long)q.indexOf(j, false), (long)j);
        }
    }
    
    @Test
    public void iteratorTest() {
        final Queue<Integer> q = new Queue<Integer>();
        for (int j = 0; j <= 6; ++j) {
            q.addLast(j);
        }
        Iterator<Integer> iter = q.iterator();
        for (int i = 0; i <= 6; ++i) {
            Assert.assertEquals((long)iter.next(), (long)i);
        }
        iter = q.iterator();
        iter.next();
        iter.remove();
        this.assertValues(q, 1, 2, 3, 4, 5, 6);
        iter.next();
        iter.remove();
        this.assertValues(q, 2, 3, 4, 5, 6);
        iter.next();
        iter.next();
        iter.remove();
        this.assertValues(q, 2, 4, 5, 6);
        iter.next();
        iter.next();
        iter.next();
        iter.remove();
        this.assertValues(q, 2, 4, 5);
        q.clear();
        for (int i = 2; i >= 0; --i) {
            q.addFirst(i);
        }
        for (int i = 3; i <= 6; ++i) {
            q.addLast(i);
        }
        iter = q.iterator();
        for (int i = 0; i <= 6; ++i) {
            Assert.assertEquals((long)iter.next(), (long)i);
        }
        iter = q.iterator();
        iter.next();
        iter.remove();
        this.assertValues(q, 1, 2, 3, 4, 5, 6);
        iter.next();
        iter.remove();
        this.assertValues(q, 2, 3, 4, 5, 6);
        iter.next();
        iter.next();
        iter.remove();
        this.assertValues(q, 2, 4, 5, 6);
        iter.next();
        iter.next();
        iter.next();
        iter.remove();
        this.assertValues(q, 2, 4, 5);
    }
    
    @Test
    public void iteratorRemoveEdgeCaseTest() {
        final Queue<Integer> queue = new Queue<Integer>();
        for (int i = 0; i < 100; ++i) {
            queue.addLast(i);
            if (i > 50) {
                queue.removeFirst();
            }
        }
        final Iterator<Integer> it = queue.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        queue.addLast(1337);
        final Integer j = queue.first();
        Assert.assertEquals(1337L, (long)j);
    }
    
    @Test
    public void toStringTest() {
        final Queue<Integer> q = new Queue<Integer>(1);
        Assert.assertTrue(q.toString().equals("[]"));
        q.addLast(4);
        Assert.assertTrue(q.toString().equals("[4]"));
        q.addLast(5);
        q.addLast(6);
        q.addLast(7);
        Assert.assertTrue(q.toString().equals("[4, 5, 6, 7]"));
    }
    
    @Test
    public void hashEqualsTest() {
        final Queue<Integer> q1 = new Queue<Integer>();
        final Queue<Integer> q2 = new Queue<Integer>();
        this.assertEqualsAndHash(q1, q2);
        q1.addFirst(1);
        Assert.assertNotEquals((Object)q1, (Object)q2);
        q2.addFirst(1);
        this.assertEqualsAndHash(q1, q2);
        q1.clear();
        q1.addLast(1);
        q1.addLast(2);
        q2.addLast(2);
        this.assertEqualsAndHash(q1, q2);
        for (int i = 0; i < 100; ++i) {
            q1.addLast(i);
            q1.addLast(i);
            q1.removeFirst();
            Assert.assertNotEquals((Object)q1, (Object)q2);
            q2.addLast(i);
            q2.addLast(i);
            q2.removeFirst();
            this.assertEqualsAndHash(q1, q2);
        }
    }
    
    private void assertEqualsAndHash(final Queue<?> q1, final Queue<?> q2) {
        Assert.assertEquals((Object)q1, (Object)q2);
        Assert.assertEquals("Hash codes are not equal", (long)q1.hashCode(), (long)q2.hashCode());
    }
    
    private void assertValues(final Queue<Integer> q, final Integer... values) {
        for (int i = 0, n = values.length; i < n; ++i) {
            Assert.assertEquals((Object)values[i], (Object)q.get(i));
        }
    }
}
