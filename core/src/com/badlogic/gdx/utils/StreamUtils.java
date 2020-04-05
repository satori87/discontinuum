// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.io.Closeable;
import java.io.StringWriter;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public final class StreamUtils
{
    public static final int DEFAULT_BUFFER_SIZE = 4096;
    public static final byte[] EMPTY_BYTES;
    
    static {
        EMPTY_BYTES = new byte[0];
    }
    
    public static void copyStream(final InputStream input, final OutputStream output) throws IOException {
        copyStream(input, output, new byte[4096]);
    }
    
    public static void copyStream(final InputStream input, final OutputStream output, final int bufferSize) throws IOException {
        copyStream(input, output, new byte[bufferSize]);
    }
    
    public static void copyStream(final InputStream input, final OutputStream output, final byte[] buffer) throws IOException {
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }
    
    public static void copyStream(final InputStream input, final ByteBuffer output) throws IOException {
        copyStream(input, output, new byte[4096]);
    }
    
    public static void copyStream(final InputStream input, final ByteBuffer output, final int bufferSize) throws IOException {
        copyStream(input, output, new byte[bufferSize]);
    }
    
    public static int copyStream(final InputStream input, final ByteBuffer output, final byte[] buffer) throws IOException {
        final int startPosition = output.position();
        int total = 0;
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            BufferUtils.copy(buffer, 0, output, bytesRead);
            total += bytesRead;
            output.position(startPosition + total);
        }
        output.position(startPosition);
        return total;
    }
    
    public static byte[] copyStreamToByteArray(final InputStream input) throws IOException {
        return copyStreamToByteArray(input, input.available());
    }
    
    public static byte[] copyStreamToByteArray(final InputStream input, final int estimatedSize) throws IOException {
        final ByteArrayOutputStream baos = new OptimizedByteArrayOutputStream(Math.max(0, estimatedSize));
        copyStream(input, baos);
        return baos.toByteArray();
    }
    
    public static String copyStreamToString(final InputStream input) throws IOException {
        return copyStreamToString(input, input.available(), null);
    }
    
    public static String copyStreamToString(final InputStream input, final int estimatedSize) throws IOException {
        return copyStreamToString(input, estimatedSize, null);
    }
    
    public static String copyStreamToString(final InputStream input, final int estimatedSize, final String charset) throws IOException {
        final InputStreamReader reader = (charset == null) ? new InputStreamReader(input) : new InputStreamReader(input, charset);
        final StringWriter writer = new StringWriter(Math.max(0, estimatedSize));
        final char[] buffer = new char[4096];
        int charsRead;
        while ((charsRead = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, charsRead);
        }
        return writer.toString();
    }
    
    public static void closeQuietly(final Closeable c) {
        if (c != null) {
            try {
                c.close();
            }
            catch (Throwable t) {}
        }
    }
    
    public static class OptimizedByteArrayOutputStream extends ByteArrayOutputStream
    {
        public OptimizedByteArrayOutputStream(final int initialSize) {
            super(initialSize);
        }
        
        @Override
        public synchronized byte[] toByteArray() {
            if (this.count == this.buf.length) {
                return this.buf;
            }
            return super.toByteArray();
        }
        
        public byte[] getBuffer() {
            return this.buf;
        }
    }
}
