// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

public class BinaryHeap<T extends Node>
{
    public int size;
    private Node[] nodes;
    private final boolean isMaxHeap;
    
    public BinaryHeap() {
        this(16, false);
    }
    
    public BinaryHeap(final int capacity, final boolean isMaxHeap) {
        this.isMaxHeap = isMaxHeap;
        this.nodes = new Node[capacity];
    }
    
    public T add(final T node) {
        if (this.size == this.nodes.length) {
            final Node[] newNodes = new Node[this.size << 1];
            System.arraycopy(this.nodes, 0, newNodes, 0, this.size);
            this.nodes = newNodes;
        }
        node.index = this.size;
        this.nodes[this.size] = node;
        this.up(this.size++);
        return node;
    }
    
    public T add(final T node, final float value) {
        node.value = value;
        return this.add(node);
    }
    
    public boolean contains(final T node, final boolean identity) {
        if (identity || node == null) {
            Node[] nodes;
            for (int length = (nodes = this.nodes).length, i = 0; i < length; ++i) {
                final Node n = nodes[i];
                if (n == node) {
                    return true;
                }
            }
        }
        else {
            Node[] nodes2;
            for (int length2 = (nodes2 = this.nodes).length, j = 0; j < length2; ++j) {
                final Node n = nodes2[j];
                if (n.equals(node)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public T peek() {
        if (this.size == 0) {
            throw new IllegalStateException("The heap is empty.");
        }
        return (T)this.nodes[0];
    }
    
    public T pop() {
        return this.remove(0);
    }
    
    public T remove(final T node) {
        return this.remove(node.index);
    }
    
    private T remove(final int index) {
        final Node[] nodes = this.nodes;
        final Node removed = nodes[index];
        final Node[] array = nodes;
        final Node[] array2 = nodes;
        final int size = this.size - 1;
        this.size = size;
        array[index] = array2[size];
        nodes[this.size] = null;
        if (this.size > 0 && index < this.size) {
            this.down(index);
        }
        return (T)removed;
    }
    
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    public void clear() {
        final Node[] nodes = this.nodes;
        for (int i = 0, n = this.size; i < n; ++i) {
            nodes[i] = null;
        }
        this.size = 0;
    }
    
    public void setValue(final T node, final float value) {
        final float oldValue = node.value;
        node.value = value;
        if (value < oldValue ^ this.isMaxHeap) {
            this.up(node.index);
        }
        else {
            this.down(node.index);
        }
    }
    
    private void up(int index) {
        final Node[] nodes = this.nodes;
        final Node node = nodes[index];
        final float value = node.value;
        while (index > 0) {
            final int parentIndex = index - 1 >> 1;
            final Node parent = nodes[parentIndex];
            if (!(value < parent.value ^ this.isMaxHeap)) {
                break;
            }
            nodes[index] = parent;
            parent.index = index;
            index = parentIndex;
        }
        nodes[index] = node;
        node.index = index;
    }
    
    private void down(int index) {
        final Node[] nodes = this.nodes;
        final int size = this.size;
        final Node node = nodes[index];
        final float value = node.value;
        while (true) {
            final int leftIndex = 1 + (index << 1);
            if (leftIndex >= size) {
                break;
            }
            final int rightIndex = leftIndex + 1;
            final Node leftNode = nodes[leftIndex];
            final float leftValue = leftNode.value;
            Node rightNode;
            float rightValue;
            if (rightIndex >= size) {
                rightNode = null;
                rightValue = (this.isMaxHeap ? Float.MIN_VALUE : Float.MAX_VALUE);
            }
            else {
                rightNode = nodes[rightIndex];
                rightValue = rightNode.value;
            }
            if (leftValue < rightValue ^ this.isMaxHeap) {
                if (leftValue == value) {
                    break;
                }
                if (leftValue > value ^ this.isMaxHeap) {
                    break;
                }
                nodes[index] = leftNode;
                leftNode.index = index;
                index = leftIndex;
            }
            else {
                if (rightValue == value) {
                    break;
                }
                if (rightValue > value ^ this.isMaxHeap) {
                    break;
                }
                nodes[index] = rightNode;
                rightNode.index = index;
                index = rightIndex;
            }
        }
        nodes[index] = node;
        node.index = index;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof BinaryHeap)) {
            return false;
        }
        final BinaryHeap other = (BinaryHeap)obj;
        if (other.size != this.size) {
            return false;
        }
        for (int i = 0, n = this.size; i < n; ++i) {
            if (other.nodes[i].value != this.nodes[i].value) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        for (int i = 0, n = this.size; i < n; ++i) {
            h = h * 31 + Float.floatToIntBits(this.nodes[i].value);
        }
        return h;
    }
    
    @Override
    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        final Node[] nodes = this.nodes;
        final StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        buffer.append(nodes[0].value);
        for (int i = 1; i < this.size; ++i) {
            buffer.append(", ");
            buffer.append(nodes[i].value);
        }
        buffer.append(']');
        return buffer.toString();
    }
    
    public static class Node
    {
        float value;
        int index;
        
        public Node(final float value) {
            this.value = value;
        }
        
        public float getValue() {
            return this.value;
        }
        
        @Override
        public String toString() {
            return Float.toString(this.value);
        }
    }
}
