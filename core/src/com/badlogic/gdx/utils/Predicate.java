// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.util.Iterator;

public interface Predicate<T>
{
    boolean evaluate(final T p0);
    
    public static class PredicateIterator<T> implements Iterator<T>
    {
        public Iterator<T> iterator;
        public Predicate<T> predicate;
        public boolean end;
        public boolean peeked;
        public T next;
        
        public PredicateIterator(final Iterable<T> iterable, final Predicate<T> predicate) {
            this(iterable.iterator(), predicate);
        }
        
        public PredicateIterator(final Iterator<T> iterator, final Predicate<T> predicate) {
            this.end = false;
            this.peeked = false;
            this.next = null;
            this.set(iterator, predicate);
        }
        
        public void set(final Iterable<T> iterable, final Predicate<T> predicate) {
            this.set(iterable.iterator(), predicate);
        }
        
        public void set(final Iterator<T> iterator, final Predicate<T> predicate) {
            this.iterator = iterator;
            this.predicate = predicate;
            final boolean b = false;
            this.peeked = b;
            this.end = b;
            this.next = null;
        }
        
        @Override
        public boolean hasNext() {
            if (this.end) {
                return false;
            }
            if (this.next != null) {
                return true;
            }
            this.peeked = true;
            while (this.iterator.hasNext()) {
                final T n = this.iterator.next();
                if (this.predicate.evaluate(n)) {
                    this.next = n;
                    return true;
                }
            }
            this.end = true;
            return false;
        }
        
        @Override
        public T next() {
            if (this.next == null && !this.hasNext()) {
                return null;
            }
            final T result = this.next;
            this.next = null;
            this.peeked = false;
            return result;
        }
        
        @Override
        public void remove() {
            if (this.peeked) {
                throw new GdxRuntimeException("Cannot remove between a call to hasNext() and next().");
            }
            this.iterator.remove();
        }
    }
    
    public static class PredicateIterable<T> implements Iterable<T>
    {
        public Iterable<T> iterable;
        public Predicate<T> predicate;
        public PredicateIterator<T> iterator;
        
        public PredicateIterable(final Iterable<T> iterable, final Predicate<T> predicate) {
            this.iterator = null;
            this.set(iterable, predicate);
        }
        
        public void set(final Iterable<T> iterable, final Predicate<T> predicate) {
            this.iterable = iterable;
            this.predicate = predicate;
        }
        
        @Override
        public Iterator<T> iterator() {
            if (this.iterator == null) {
                this.iterator = new PredicateIterator<T>(this.iterable.iterator(), this.predicate);
            }
            else {
                this.iterator.set(this.iterable.iterator(), this.predicate);
            }
            return this.iterator;
        }
    }
}
