// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.Iterator;

public class SortedIntList<E> implements Iterable<Node<E>>
{
    private NodePool<E> nodePool;
    private Iterator iterator;
    int size;
    Node<E> first;
    
    public SortedIntList() {
        this.nodePool = new NodePool<E>();
        this.size = 0;
    }
    
    public E insert(final int index, final E value) {
        if (this.first != null) {
            Node<E> c;
            for (c = this.first; c.n != null && c.n.index <= index; c = c.n) {}
            if (index > c.index) {
                c.n = this.nodePool.obtain(c, c.n, value, index);
                if (c.n.n != null) {
                    c.n.n.p = c.n;
                }
                ++this.size;
            }
            else if (index < c.index) {
                final Node<E> newFirst = this.nodePool.obtain(null, this.first, value, index);
                this.first.p = newFirst;
                this.first = newFirst;
                ++this.size;
            }
            else {
                c.value = value;
            }
        }
        else {
            this.first = this.nodePool.obtain(null, null, value, index);
            ++this.size;
        }
        return null;
    }
    
    public E get(final int index) {
        E match = null;
        if (this.first != null) {
            Node<E> c;
            for (c = this.first; c.n != null && c.index < index; c = c.n) {}
            if (c.index == index) {
                match = c.value;
            }
        }
        return match;
    }
    
    public void clear() {
        while (this.first != null) {
            this.nodePool.free((Node<E>)this.first);
            this.first = this.first.n;
        }
        this.size = 0;
    }
    
    public int size() {
        return this.size;
    }
    
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Override
    public java.util.Iterator<Node<E>> iterator() {
        if (this.iterator == null) {
            this.iterator = new Iterator();
        }
        return this.iterator.reset();
    }
    
    class Iterator implements java.util.Iterator<Node<E>>
    {
        private Node<E> position;
        private Node<E> previousPosition;
        
        @Override
        public boolean hasNext() {
            return this.position != null;
        }
        
        @Override
        public Node<E> next() {
            this.previousPosition = this.position;
            this.position = this.position.n;
            return this.previousPosition;
        }
        
        @Override
        public void remove() {
            if (this.previousPosition != null) {
                if (this.previousPosition == SortedIntList.this.first) {
                    SortedIntList.this.first = this.position;
                }
                else {
                    this.previousPosition.p.n = this.position;
                    if (this.position != null) {
                        this.position.p = this.previousPosition.p;
                    }
                }
                final SortedIntList this$0 = SortedIntList.this;
                --this$0.size;
            }
        }
        
        public Iterator reset() {
            this.position = SortedIntList.this.first;
            this.previousPosition = null;
            return this;
        }
    }
    
    static class NodePool<E> extends Pool<Node<E>>
    {
        @Override
        protected Node<E> newObject() {
            return new Node<E>();
        }
        
        public Node<E> obtain(final Node<E> p, final Node<E> n, final E value, final int index) {
            final Node<E> newNode = super.obtain();
            newNode.p = p;
            newNode.n = n;
            newNode.value = value;
            newNode.index = index;
            return newNode;
        }
    }
    
    public static class Node<E>
    {
        protected Node<E> p;
        protected Node<E> n;
        public E value;
        public int index;
    }
}
