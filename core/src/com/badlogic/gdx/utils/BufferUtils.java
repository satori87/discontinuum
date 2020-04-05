// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.LongBuffer;
import java.nio.IntBuffer;
import java.nio.CharBuffer;
import java.nio.ShortBuffer;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import java.nio.FloatBuffer;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public final class BufferUtils
{
    static Array<ByteBuffer> unsafeBuffers;
    static int allocatedUnsafe;
    
    static {
        BufferUtils.unsafeBuffers = new Array<ByteBuffer>();
        BufferUtils.allocatedUnsafe = 0;
    }
    
    public static void copy(final float[] src, final Buffer dst, final int numFloats, final int offset) {
        if (dst instanceof ByteBuffer) {
            dst.limit(numFloats << 2);
        }
        else if (dst instanceof FloatBuffer) {
            dst.limit(numFloats);
        }
        copyJni(src, dst, numFloats, offset);
        dst.position(0);
    }
    
    public static void copy(final byte[] src, final int srcOffset, final Buffer dst, final int numElements) {
        dst.limit(dst.position() + bytesToElements(dst, numElements));
        copyJni(src, srcOffset, dst, positionInBytes(dst), numElements);
    }
    
    public static void copy(final short[] src, final int srcOffset, final Buffer dst, final int numElements) {
        dst.limit(dst.position() + bytesToElements(dst, numElements << 1));
        copyJni(src, srcOffset, dst, positionInBytes(dst), numElements << 1);
    }
    
    public static void copy(final char[] src, final int srcOffset, final int numElements, final Buffer dst) {
        copyJni(src, srcOffset, dst, positionInBytes(dst), numElements << 1);
    }
    
    public static void copy(final int[] src, final int srcOffset, final int numElements, final Buffer dst) {
        copyJni(src, srcOffset, dst, positionInBytes(dst), numElements << 2);
    }
    
    public static void copy(final long[] src, final int srcOffset, final int numElements, final Buffer dst) {
        copyJni(src, srcOffset, dst, positionInBytes(dst), numElements << 3);
    }
    
    public static void copy(final float[] src, final int srcOffset, final int numElements, final Buffer dst) {
        copyJni(src, srcOffset, dst, positionInBytes(dst), numElements << 2);
    }
    
    public static void copy(final double[] src, final int srcOffset, final int numElements, final Buffer dst) {
        copyJni(src, srcOffset, dst, positionInBytes(dst), numElements << 3);
    }
    
    public static void copy(final char[] src, final int srcOffset, final Buffer dst, final int numElements) {
        dst.limit(dst.position() + bytesToElements(dst, numElements << 1));
        copyJni(src, srcOffset, dst, positionInBytes(dst), numElements << 1);
    }
    
    public static void copy(final int[] src, final int srcOffset, final Buffer dst, final int numElements) {
        dst.limit(dst.position() + bytesToElements(dst, numElements << 2));
        copyJni(src, srcOffset, dst, positionInBytes(dst), numElements << 2);
    }
    
    public static void copy(final long[] src, final int srcOffset, final Buffer dst, final int numElements) {
        dst.limit(dst.position() + bytesToElements(dst, numElements << 3));
        copyJni(src, srcOffset, dst, positionInBytes(dst), numElements << 3);
    }
    
    public static void copy(final float[] src, final int srcOffset, final Buffer dst, final int numElements) {
        dst.limit(dst.position() + bytesToElements(dst, numElements << 2));
        copyJni(src, srcOffset, dst, positionInBytes(dst), numElements << 2);
    }
    
    public static void copy(final double[] src, final int srcOffset, final Buffer dst, final int numElements) {
        dst.limit(dst.position() + bytesToElements(dst, numElements << 3));
        copyJni(src, srcOffset, dst, positionInBytes(dst), numElements << 3);
    }
    
    public static void copy(final Buffer src, final Buffer dst, final int numElements) {
        final int numBytes = elementsToBytes(src, numElements);
        dst.limit(dst.position() + bytesToElements(dst, numBytes));
        copyJni(src, positionInBytes(src), dst, positionInBytes(dst), numBytes);
    }
    
    public static void transform(final Buffer data, final int dimensions, final int strideInBytes, final int count, final Matrix4 matrix) {
        transform(data, dimensions, strideInBytes, count, matrix, 0);
    }
    
    public static void transform(final float[] data, final int dimensions, final int strideInBytes, final int count, final Matrix4 matrix) {
        transform(data, dimensions, strideInBytes, count, matrix, 0);
    }
    
    public static void transform(final Buffer data, final int dimensions, final int strideInBytes, final int count, final Matrix4 matrix, final int offset) {
        switch (dimensions) {
            case 4: {
                transformV4M4Jni(data, strideInBytes, count, matrix.val, positionInBytes(data) + offset);
                break;
            }
            case 3: {
                transformV3M4Jni(data, strideInBytes, count, matrix.val, positionInBytes(data) + offset);
                break;
            }
            case 2: {
                transformV2M4Jni(data, strideInBytes, count, matrix.val, positionInBytes(data) + offset);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public static void transform(final float[] data, final int dimensions, final int strideInBytes, final int count, final Matrix4 matrix, final int offset) {
        switch (dimensions) {
            case 4: {
                transformV4M4Jni(data, strideInBytes, count, matrix.val, offset);
                break;
            }
            case 3: {
                transformV3M4Jni(data, strideInBytes, count, matrix.val, offset);
                break;
            }
            case 2: {
                transformV2M4Jni(data, strideInBytes, count, matrix.val, offset);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public static void transform(final Buffer data, final int dimensions, final int strideInBytes, final int count, final Matrix3 matrix) {
        transform(data, dimensions, strideInBytes, count, matrix, 0);
    }
    
    public static void transform(final float[] data, final int dimensions, final int strideInBytes, final int count, final Matrix3 matrix) {
        transform(data, dimensions, strideInBytes, count, matrix, 0);
    }
    
    public static void transform(final Buffer data, final int dimensions, final int strideInBytes, final int count, final Matrix3 matrix, final int offset) {
        switch (dimensions) {
            case 3: {
                transformV3M3Jni(data, strideInBytes, count, matrix.val, positionInBytes(data) + offset);
                break;
            }
            case 2: {
                transformV2M3Jni(data, strideInBytes, count, matrix.val, positionInBytes(data) + offset);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public static void transform(final float[] data, final int dimensions, final int strideInBytes, final int count, final Matrix3 matrix, final int offset) {
        switch (dimensions) {
            case 3: {
                transformV3M3Jni(data, strideInBytes, count, matrix.val, offset);
                break;
            }
            case 2: {
                transformV2M3Jni(data, strideInBytes, count, matrix.val, offset);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public static long findFloats(final Buffer vertex, final int strideInBytes, final Buffer vertices, final int numVertices) {
        return find(vertex, positionInBytes(vertex), strideInBytes, vertices, positionInBytes(vertices), numVertices);
    }
    
    public static long findFloats(final float[] vertex, final int strideInBytes, final Buffer vertices, final int numVertices) {
        return find(vertex, 0, strideInBytes, vertices, positionInBytes(vertices), numVertices);
    }
    
    public static long findFloats(final Buffer vertex, final int strideInBytes, final float[] vertices, final int numVertices) {
        return find(vertex, positionInBytes(vertex), strideInBytes, vertices, 0, numVertices);
    }
    
    public static long findFloats(final float[] vertex, final int strideInBytes, final float[] vertices, final int numVertices) {
        return find(vertex, 0, strideInBytes, vertices, 0, numVertices);
    }
    
    public static long findFloats(final Buffer vertex, final int strideInBytes, final Buffer vertices, final int numVertices, final float epsilon) {
        return find(vertex, positionInBytes(vertex), strideInBytes, vertices, positionInBytes(vertices), numVertices, epsilon);
    }
    
    public static long findFloats(final float[] vertex, final int strideInBytes, final Buffer vertices, final int numVertices, final float epsilon) {
        return find(vertex, 0, strideInBytes, vertices, positionInBytes(vertices), numVertices, epsilon);
    }
    
    public static long findFloats(final Buffer vertex, final int strideInBytes, final float[] vertices, final int numVertices, final float epsilon) {
        return find(vertex, positionInBytes(vertex), strideInBytes, vertices, 0, numVertices, epsilon);
    }
    
    public static long findFloats(final float[] vertex, final int strideInBytes, final float[] vertices, final int numVertices, final float epsilon) {
        return find(vertex, 0, strideInBytes, vertices, 0, numVertices, epsilon);
    }
    
    private static int positionInBytes(final Buffer dst) {
        if (dst instanceof ByteBuffer) {
            return dst.position();
        }
        if (dst instanceof ShortBuffer) {
            return dst.position() << 1;
        }
        if (dst instanceof CharBuffer) {
            return dst.position() << 1;
        }
        if (dst instanceof IntBuffer) {
            return dst.position() << 2;
        }
        if (dst instanceof LongBuffer) {
            return dst.position() << 3;
        }
        if (dst instanceof FloatBuffer) {
            return dst.position() << 2;
        }
        if (dst instanceof DoubleBuffer) {
            return dst.position() << 3;
        }
        throw new GdxRuntimeException("Can't copy to a " + dst.getClass().getName() + " instance");
    }
    
    private static int bytesToElements(final Buffer dst, final int bytes) {
        if (dst instanceof ByteBuffer) {
            return bytes;
        }
        if (dst instanceof ShortBuffer) {
            return bytes >>> 1;
        }
        if (dst instanceof CharBuffer) {
            return bytes >>> 1;
        }
        if (dst instanceof IntBuffer) {
            return bytes >>> 2;
        }
        if (dst instanceof LongBuffer) {
            return bytes >>> 3;
        }
        if (dst instanceof FloatBuffer) {
            return bytes >>> 2;
        }
        if (dst instanceof DoubleBuffer) {
            return bytes >>> 3;
        }
        throw new GdxRuntimeException("Can't copy to a " + dst.getClass().getName() + " instance");
    }
    
    private static int elementsToBytes(final Buffer dst, final int elements) {
        if (dst instanceof ByteBuffer) {
            return elements;
        }
        if (dst instanceof ShortBuffer) {
            return elements << 1;
        }
        if (dst instanceof CharBuffer) {
            return elements << 1;
        }
        if (dst instanceof IntBuffer) {
            return elements << 2;
        }
        if (dst instanceof LongBuffer) {
            return elements << 3;
        }
        if (dst instanceof FloatBuffer) {
            return elements << 2;
        }
        if (dst instanceof DoubleBuffer) {
            return elements << 3;
        }
        throw new GdxRuntimeException("Can't copy to a " + dst.getClass().getName() + " instance");
    }
    
    public static FloatBuffer newFloatBuffer(final int numFloats) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(numFloats * 4);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asFloatBuffer();
    }
    
    public static DoubleBuffer newDoubleBuffer(final int numDoubles) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(numDoubles * 8);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asDoubleBuffer();
    }
    
    public static ByteBuffer newByteBuffer(final int numBytes) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(numBytes);
        buffer.order(ByteOrder.nativeOrder());
        return buffer;
    }
    
    public static ShortBuffer newShortBuffer(final int numShorts) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(numShorts * 2);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asShortBuffer();
    }
    
    public static CharBuffer newCharBuffer(final int numChars) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(numChars * 2);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asCharBuffer();
    }
    
    public static IntBuffer newIntBuffer(final int numInts) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(numInts * 4);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asIntBuffer();
    }
    
    public static LongBuffer newLongBuffer(final int numLongs) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(numLongs * 8);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asLongBuffer();
    }
    
    public static void disposeUnsafeByteBuffer(final ByteBuffer buffer) {
        final int size = buffer.capacity();
        synchronized (BufferUtils.unsafeBuffers) {
            if (!BufferUtils.unsafeBuffers.removeValue(buffer, true)) {
                throw new IllegalArgumentException("buffer not allocated with newUnsafeByteBuffer or already disposed");
            }
        }
        // monitorexit(BufferUtils.unsafeBuffers)
        BufferUtils.allocatedUnsafe -= size;
        freeMemory(buffer);
    }
    
    public static boolean isUnsafeByteBuffer(final ByteBuffer buffer) {
        synchronized (BufferUtils.unsafeBuffers) {
            // monitorexit(BufferUtils.unsafeBuffers)
            return BufferUtils.unsafeBuffers.contains(buffer, true);
        }
    }
    
    public static ByteBuffer newUnsafeByteBuffer(final int numBytes) {
        final ByteBuffer buffer = newDisposableByteBuffer(numBytes);
        buffer.order(ByteOrder.nativeOrder());
        BufferUtils.allocatedUnsafe += numBytes;
        synchronized (BufferUtils.unsafeBuffers) {
            BufferUtils.unsafeBuffers.add(buffer);
        }
        // monitorexit(BufferUtils.unsafeBuffers)
        return buffer;
    }
    
    public static long getUnsafeBufferAddress(final Buffer buffer) {
        return getBufferAddress(buffer) + buffer.position();
    }
    
    public static ByteBuffer newUnsafeByteBuffer(final ByteBuffer buffer) {
        BufferUtils.allocatedUnsafe += buffer.capacity();
        synchronized (BufferUtils.unsafeBuffers) {
            BufferUtils.unsafeBuffers.add(buffer);
        }
        // monitorexit(BufferUtils.unsafeBuffers)
        return buffer;
    }
    
    public static int getAllocatedBytesUnsafe() {
        return BufferUtils.allocatedUnsafe;
    }
    
    private static native void freeMemory(final ByteBuffer p0);
    
    private static native ByteBuffer newDisposableByteBuffer(final int p0);
    
    private static native long getBufferAddress(final Buffer p0);
    
    public static native void clear(final ByteBuffer p0, final int p1);
    
    private static native void copyJni(final float[] p0, final Buffer p1, final int p2, final int p3);
    
    private static native void copyJni(final byte[] p0, final int p1, final Buffer p2, final int p3, final int p4);
    
    private static native void copyJni(final char[] p0, final int p1, final Buffer p2, final int p3, final int p4);
    
    private static native void copyJni(final short[] p0, final int p1, final Buffer p2, final int p3, final int p4);
    
    private static native void copyJni(final int[] p0, final int p1, final Buffer p2, final int p3, final int p4);
    
    private static native void copyJni(final long[] p0, final int p1, final Buffer p2, final int p3, final int p4);
    
    private static native void copyJni(final float[] p0, final int p1, final Buffer p2, final int p3, final int p4);
    
    private static native void copyJni(final double[] p0, final int p1, final Buffer p2, final int p3, final int p4);
    
    private static native void copyJni(final Buffer p0, final int p1, final Buffer p2, final int p3, final int p4);
    
    private static native void transformV4M4Jni(final Buffer p0, final int p1, final int p2, final float[] p3, final int p4);
    
    private static native void transformV4M4Jni(final float[] p0, final int p1, final int p2, final float[] p3, final int p4);
    
    private static native void transformV3M4Jni(final Buffer p0, final int p1, final int p2, final float[] p3, final int p4);
    
    private static native void transformV3M4Jni(final float[] p0, final int p1, final int p2, final float[] p3, final int p4);
    
    private static native void transformV2M4Jni(final Buffer p0, final int p1, final int p2, final float[] p3, final int p4);
    
    private static native void transformV2M4Jni(final float[] p0, final int p1, final int p2, final float[] p3, final int p4);
    
    private static native void transformV3M3Jni(final Buffer p0, final int p1, final int p2, final float[] p3, final int p4);
    
    private static native void transformV3M3Jni(final float[] p0, final int p1, final int p2, final float[] p3, final int p4);
    
    private static native void transformV2M3Jni(final Buffer p0, final int p1, final int p2, final float[] p3, final int p4);
    
    private static native void transformV2M3Jni(final float[] p0, final int p1, final int p2, final float[] p3, final int p4);
    
    private static native long find(final Buffer p0, final int p1, final int p2, final Buffer p3, final int p4, final int p5);
    
    private static native long find(final float[] p0, final int p1, final int p2, final Buffer p3, final int p4, final int p5);
    
    private static native long find(final Buffer p0, final int p1, final int p2, final float[] p3, final int p4, final int p5);
    
    private static native long find(final float[] p0, final int p1, final int p2, final float[] p3, final int p4, final int p5);
    
    private static native long find(final Buffer p0, final int p1, final int p2, final Buffer p3, final int p4, final int p5, final float p6);
    
    private static native long find(final float[] p0, final int p1, final int p2, final Buffer p3, final int p4, final int p5, final float p6);
    
    private static native long find(final Buffer p0, final int p1, final int p2, final float[] p3, final int p4, final int p5, final float p6);
    
    private static native long find(final float[] p0, final int p1, final int p2, final float[] p3, final int p4, final int p5, final float p6);
}
