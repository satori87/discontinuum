// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import java.io.OutputStream;
import java.io.DataOutputStream;
import java.util.zip.GZIPOutputStream;
import com.badlogic.gdx.math.MathUtils;
import java.io.Closeable;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.util.zip.GZIPInputStream;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import java.nio.ByteBuffer;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.Pixmap;

public class ETC1
{
    public static int PKM_HEADER_SIZE;
    public static int ETC1_RGB8_OES;
    
    static {
        ETC1.PKM_HEADER_SIZE = 16;
        ETC1.ETC1_RGB8_OES = 36196;
    }
    
    private static int getPixelSize(final Pixmap.Format format) {
        if (format == Pixmap.Format.RGB565) {
            return 2;
        }
        if (format == Pixmap.Format.RGB888) {
            return 3;
        }
        throw new GdxRuntimeException("Can only handle RGB565 or RGB888 images");
    }
    
    public static ETC1Data encodeImage(final Pixmap pixmap) {
        final int pixelSize = getPixelSize(pixmap.getFormat());
        final ByteBuffer compressedData = encodeImage(pixmap.getPixels(), 0, pixmap.getWidth(), pixmap.getHeight(), pixelSize);
        BufferUtils.newUnsafeByteBuffer(compressedData);
        return new ETC1Data(pixmap.getWidth(), pixmap.getHeight(), compressedData, 0);
    }
    
    public static ETC1Data encodeImagePKM(final Pixmap pixmap) {
        final int pixelSize = getPixelSize(pixmap.getFormat());
        final ByteBuffer compressedData = encodeImagePKM(pixmap.getPixels(), 0, pixmap.getWidth(), pixmap.getHeight(), pixelSize);
        BufferUtils.newUnsafeByteBuffer(compressedData);
        return new ETC1Data(pixmap.getWidth(), pixmap.getHeight(), compressedData, 16);
    }
    
    public static Pixmap decodeImage(final ETC1Data etc1Data, final Pixmap.Format format) {
        int dataOffset = 0;
        int width = 0;
        int height = 0;
        if (etc1Data.hasPKMHeader()) {
            dataOffset = 16;
            width = getWidthPKM(etc1Data.compressedData, 0);
            height = getHeightPKM(etc1Data.compressedData, 0);
        }
        else {
            dataOffset = 0;
            width = etc1Data.width;
            height = etc1Data.height;
        }
        final int pixelSize = getPixelSize(format);
        final Pixmap pixmap = new Pixmap(width, height, format);
        decodeImage(etc1Data.compressedData, dataOffset, pixmap.getPixels(), 0, width, height, pixelSize);
        return pixmap;
    }
    
    public static native int getCompressedDataSize(final int p0, final int p1);
    
    public static native void formatHeader(final ByteBuffer p0, final int p1, final int p2, final int p3);
    
    static native int getWidthPKM(final ByteBuffer p0, final int p1);
    
    static native int getHeightPKM(final ByteBuffer p0, final int p1);
    
    static native boolean isValidPKM(final ByteBuffer p0, final int p1);
    
    private static native void decodeImage(final ByteBuffer p0, final int p1, final ByteBuffer p2, final int p3, final int p4, final int p5, final int p6);
    
    private static native ByteBuffer encodeImage(final ByteBuffer p0, final int p1, final int p2, final int p3, final int p4);
    
    private static native ByteBuffer encodeImagePKM(final ByteBuffer p0, final int p1, final int p2, final int p3, final int p4);
    
    public static final class ETC1Data implements Disposable
    {
        public final int width;
        public final int height;
        public final ByteBuffer compressedData;
        public final int dataOffset;
        
        public ETC1Data(final int width, final int height, final ByteBuffer compressedData, final int dataOffset) {
            this.width = width;
            this.height = height;
            this.compressedData = compressedData;
            this.dataOffset = dataOffset;
            this.checkNPOT();
        }
        
        public ETC1Data(final FileHandle pkmFile) {
            final byte[] buffer = new byte[10240];
            DataInputStream in = null;
            try {
                in = new DataInputStream(new BufferedInputStream(new GZIPInputStream(pkmFile.read())));
                final int fileSize = in.readInt();
                this.compressedData = BufferUtils.newUnsafeByteBuffer(fileSize);
                int readBytes = 0;
                while ((readBytes = in.read(buffer)) != -1) {
                    this.compressedData.put(buffer, 0, readBytes);
                }
                this.compressedData.position(0);
                this.compressedData.limit(this.compressedData.capacity());
            }
            catch (Exception e) {
                throw new GdxRuntimeException("Couldn't load pkm file '" + pkmFile + "'", e);
            }
            finally {
                StreamUtils.closeQuietly(in);
            }
            StreamUtils.closeQuietly(in);
            this.width = ETC1.getWidthPKM(this.compressedData, 0);
            this.height = ETC1.getHeightPKM(this.compressedData, 0);
            this.dataOffset = ETC1.PKM_HEADER_SIZE;
            this.compressedData.position(this.dataOffset);
            this.checkNPOT();
        }
        
        private void checkNPOT() {
            if (!MathUtils.isPowerOfTwo(this.width) || !MathUtils.isPowerOfTwo(this.height)) {
                System.out.println("ETC1Data warning: non-power-of-two ETC1 textures may crash the driver of PowerVR GPUs");
            }
        }
        
        public boolean hasPKMHeader() {
            return this.dataOffset == 16;
        }
        
        public void write(final FileHandle file) {
            DataOutputStream write = null;
            final byte[] buffer = new byte[10240];
            int writtenBytes = 0;
            this.compressedData.position(0);
            this.compressedData.limit(this.compressedData.capacity());
            try {
                write = new DataOutputStream(new GZIPOutputStream(file.write(false)));
                write.writeInt(this.compressedData.capacity());
                while (writtenBytes != this.compressedData.capacity()) {
                    final int bytesToWrite = Math.min(this.compressedData.remaining(), buffer.length);
                    this.compressedData.get(buffer, 0, bytesToWrite);
                    write.write(buffer, 0, bytesToWrite);
                    writtenBytes += bytesToWrite;
                }
            }
            catch (Exception e) {
                throw new GdxRuntimeException("Couldn't write PKM file to '" + file + "'", e);
            }
            finally {
                StreamUtils.closeQuietly(write);
            }
            StreamUtils.closeQuietly(write);
            this.compressedData.position(this.dataOffset);
            this.compressedData.limit(this.compressedData.capacity());
        }
        
        @Override
        public void dispose() {
            BufferUtils.disposeUnsafeByteBuffer(this.compressedData);
        }
        
        @Override
        public String toString() {
            if (this.hasPKMHeader()) {
                return String.valueOf(ETC1.isValidPKM(this.compressedData, 0) ? "valid" : "invalid") + " pkm [" + ETC1.getWidthPKM(this.compressedData, 0) + "x" + ETC1.getHeightPKM(this.compressedData, 0) + "], compressed: " + (this.compressedData.capacity() - ETC1.PKM_HEADER_SIZE);
            }
            return "raw [" + this.width + "x" + this.height + "], compressed: " + (this.compressedData.capacity() - ETC1.PKM_HEADER_SIZE);
        }
    }
}
