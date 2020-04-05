// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles;

import com.badlogic.gdx.utils.reflect.ArrayReflection;
import java.util.Iterator;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Array;

public class ParallelArray
{
    Array<Channel> arrays;
    public int capacity;
    public int size;
    
    public ParallelArray(final int capacity) {
        this.arrays = new Array<Channel>(false, 2, Channel.class);
        this.capacity = capacity;
        this.size = 0;
    }
    
    public <T extends Channel> T addChannel(final ChannelDescriptor channelDescriptor) {
        return this.addChannel(channelDescriptor, (ChannelInitializer<T>)null);
    }
    
    public <T extends Channel> T addChannel(final ChannelDescriptor channelDescriptor, final ChannelInitializer<T> initializer) {
        T channel = this.getChannel(channelDescriptor);
        if (channel == null) {
            channel = this.allocateChannel(channelDescriptor);
            if (initializer != null) {
                initializer.init(channel);
            }
            this.arrays.add(channel);
        }
        return channel;
    }
    
    private <T extends Channel> T allocateChannel(final ChannelDescriptor channelDescriptor) {
        if (channelDescriptor.type == Float.TYPE) {
            return (T)new FloatChannel(channelDescriptor.id, channelDescriptor.count, this.capacity);
        }
        if (channelDescriptor.type == Integer.TYPE) {
            return (T)new IntChannel(channelDescriptor.id, channelDescriptor.count, this.capacity);
        }
        return (T)new ObjectChannel(channelDescriptor.id, channelDescriptor.count, this.capacity, (Class<T>)channelDescriptor.type);
    }
    
    public <T> void removeArray(final int id) {
        this.arrays.removeIndex(this.findIndex(id));
    }
    
    private int findIndex(final int id) {
        for (int i = 0; i < this.arrays.size; ++i) {
            final Channel array = this.arrays.items[i];
            if (array.id == id) {
                return i;
            }
        }
        return -1;
    }
    
    public void addElement(final Object... values) {
        if (this.size == this.capacity) {
            throw new GdxRuntimeException("Capacity reached, cannot add other elements");
        }
        int k = 0;
        for (final Channel strideArray : this.arrays) {
            strideArray.add(k, values);
            k += strideArray.strideSize;
        }
        ++this.size;
    }
    
    public void removeElement(final int index) {
        final int last = this.size - 1;
        for (final Channel strideArray : this.arrays) {
            strideArray.swap(index, last);
        }
        this.size = last;
    }
    
    public <T extends Channel> T getChannel(final ChannelDescriptor descriptor) {
        for (final Channel array : this.arrays) {
            if (array.id == descriptor.id) {
                return (T)array;
            }
        }
        return null;
    }
    
    public void clear() {
        this.arrays.clear();
        this.size = 0;
    }
    
    public void setCapacity(final int requiredCapacity) {
        if (this.capacity != requiredCapacity) {
            for (final Channel channel : this.arrays) {
                channel.setCapacity(requiredCapacity);
            }
            this.capacity = requiredCapacity;
        }
    }
    
    public static class ChannelDescriptor
    {
        public int id;
        public Class<?> type;
        public int count;
        
        public ChannelDescriptor(final int id, final Class<?> type, final int count) {
            this.id = id;
            this.type = type;
            this.count = count;
        }
    }
    
    public abstract class Channel
    {
        public int id;
        public Object data;
        public int strideSize;
        
        public Channel(final int id, final Object data, final int strideSize) {
            this.id = id;
            this.strideSize = strideSize;
            this.data = data;
        }
        
        public abstract void add(final int p0, final Object... p1);
        
        public abstract void swap(final int p0, final int p1);
        
        protected abstract void setCapacity(final int p0);
    }
    
    public class FloatChannel extends Channel
    {
        public float[] data;
        
        public FloatChannel(final int id, final int strideSize, final int size) {
            super(id, new float[size * strideSize], strideSize);
            this.data = (float[])super.data;
        }
        
        @Override
        public void add(final int index, final Object... objects) {
            for (int i = this.strideSize * ParallelArray.this.size, c = i + this.strideSize, k = 0; i < c; ++i, ++k) {
                this.data[i] = (float)objects[k];
            }
        }
        
        @Override
        public void swap(int i, int k) {
            i *= this.strideSize;
            k *= this.strideSize;
            for (int c = i + this.strideSize; i < c; ++i, ++k) {
                final float t = this.data[i];
                this.data[i] = this.data[k];
                this.data[k] = t;
            }
        }
        
        public void setCapacity(final int requiredCapacity) {
            final float[] newData = new float[this.strideSize * requiredCapacity];
            System.arraycopy(this.data, 0, newData, 0, Math.min(this.data.length, newData.length));
            final float[] array = newData;
            this.data = array;
            super.data = array;
        }
    }
    
    public class IntChannel extends Channel
    {
        public int[] data;
        
        public IntChannel(final int id, final int strideSize, final int size) {
            super(id, new int[size * strideSize], strideSize);
            this.data = (int[])super.data;
        }
        
        @Override
        public void add(final int index, final Object... objects) {
            for (int i = this.strideSize * ParallelArray.this.size, c = i + this.strideSize, k = 0; i < c; ++i, ++k) {
                this.data[i] = (int)objects[k];
            }
        }
        
        @Override
        public void swap(int i, int k) {
            i *= this.strideSize;
            k *= this.strideSize;
            for (int c = i + this.strideSize; i < c; ++i, ++k) {
                final int t = this.data[i];
                this.data[i] = this.data[k];
                this.data[k] = t;
            }
        }
        
        public void setCapacity(final int requiredCapacity) {
            final int[] newData = new int[this.strideSize * requiredCapacity];
            System.arraycopy(this.data, 0, newData, 0, Math.min(this.data.length, newData.length));
            final int[] array = newData;
            this.data = array;
            super.data = array;
        }
    }
    
    public class ObjectChannel<T> extends Channel
    {
        Class<T> componentType;
        public T[] data;
        
        public ObjectChannel(final int id, final int strideSize, final int size, final Class<T> type) {
            super(id, ArrayReflection.newInstance(type, size * strideSize), strideSize);
            this.componentType = type;
            this.data = (T[])super.data;
        }
        
        @Override
        public void add(final int index, final Object... objects) {
            for (int i = this.strideSize * ParallelArray.this.size, c = i + this.strideSize, k = 0; i < c; ++i, ++k) {
                this.data[i] = (T)objects[k];
            }
        }
        
        @Override
        public void swap(int i, int k) {
            i *= this.strideSize;
            k *= this.strideSize;
            for (int c = i + this.strideSize; i < c; ++i, ++k) {
                final T t = this.data[i];
                this.data[i] = this.data[k];
                this.data[k] = t;
            }
        }
        
        public void setCapacity(final int requiredCapacity) {
            final Object[] newData = (Object[])ArrayReflection.newInstance(this.componentType, this.strideSize * requiredCapacity);
            System.arraycopy(this.data, 0, newData, 0, Math.min(this.data.length, newData.length));
            final Object[] array = newData;
            this.data = (T[])array;
            super.data = array;
        }
    }
    
    public interface ChannelInitializer<T extends Channel>
    {
        void init(final T p0);
    }
}
