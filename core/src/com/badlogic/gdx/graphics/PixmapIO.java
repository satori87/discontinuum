// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import java.util.zip.Checksum;
import java.util.zip.CheckedOutputStream;
import java.util.zip.CRC32;
import java.io.ByteArrayOutputStream;
import com.badlogic.gdx.utils.ByteArray;
import java.util.zip.Deflater;
import com.badlogic.gdx.utils.Disposable;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import java.io.BufferedInputStream;
import java.nio.ByteBuffer;
import java.io.Closeable;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.io.IOException;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.files.FileHandle;

public class PixmapIO
{
    public static void writeCIM(final FileHandle file, final Pixmap pixmap) {
        CIM.write(file, pixmap);
    }
    
    public static Pixmap readCIM(final FileHandle file) {
        return CIM.read(file);
    }
    
    public static void writePNG(final FileHandle file, final Pixmap pixmap) {
        try {
            final PNG writer = new PNG((int)(pixmap.getWidth() * pixmap.getHeight() * 1.5f));
            try {
                writer.setFlipY(false);
                writer.write(file, pixmap);
            }
            finally {
                writer.dispose();
            }
            writer.dispose();
        }
        catch (IOException ex) {
            throw new GdxRuntimeException("Error writing PNG: " + file, ex);
        }
    }
    
    private static class CIM
    {
        private static final int BUFFER_SIZE = 32000;
        private static final byte[] writeBuffer;
        private static final byte[] readBuffer;
        
        static {
            writeBuffer = new byte[32000];
            readBuffer = new byte[32000];
        }
        
        public static void write(final FileHandle file, final Pixmap pixmap) {
            DataOutputStream out = null;
            try {
                final DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(file.write(false));
                out = new DataOutputStream(deflaterOutputStream);
                out.writeInt(pixmap.getWidth());
                out.writeInt(pixmap.getHeight());
                out.writeInt(Pixmap.Format.toGdx2DPixmapFormat(pixmap.getFormat()));
                final ByteBuffer pixelBuf = pixmap.getPixels();
                pixelBuf.position(0);
                pixelBuf.limit(pixelBuf.capacity());
                final int remainingBytes = pixelBuf.capacity() % 32000;
                final int iterations = pixelBuf.capacity() / 32000;
                synchronized (CIM.writeBuffer) {
                    for (int i = 0; i < iterations; ++i) {
                        pixelBuf.get(CIM.writeBuffer);
                        out.write(CIM.writeBuffer);
                    }
                    pixelBuf.get(CIM.writeBuffer, 0, remainingBytes);
                    out.write(CIM.writeBuffer, 0, remainingBytes);
                }
                // monitorexit(CIM.writeBuffer)
                pixelBuf.position(0);
                pixelBuf.limit(pixelBuf.capacity());
            }
            catch (Exception e) {
                throw new GdxRuntimeException("Couldn't write Pixmap to file '" + file + "'", e);
            }
            finally {
                StreamUtils.closeQuietly(out);
            }
            StreamUtils.closeQuietly(out);
        }
        
        public static Pixmap read(final FileHandle file) {
            DataInputStream in = null;
            try {
                in = new DataInputStream(new InflaterInputStream(new BufferedInputStream(file.read())));
                final int width = in.readInt();
                final int height = in.readInt();
                final Pixmap.Format format = Pixmap.Format.fromGdx2DPixmapFormat(in.readInt());
                final Pixmap pixmap = new Pixmap(width, height, format);
                final ByteBuffer pixelBuf = pixmap.getPixels();
                pixelBuf.position(0);
                pixelBuf.limit(pixelBuf.capacity());
                synchronized (CIM.readBuffer) {
                    int readBytes = 0;
                    while ((readBytes = in.read(CIM.readBuffer)) > 0) {
                        pixelBuf.put(CIM.readBuffer, 0, readBytes);
                    }
                }
                // monitorexit(CIM.readBuffer)
                pixelBuf.position(0);
                pixelBuf.limit(pixelBuf.capacity());
                return pixmap;
            }
            catch (Exception e) {
                throw new GdxRuntimeException("Couldn't read Pixmap from file '" + file + "'", e);
            }
            finally {
                StreamUtils.closeQuietly(in);
            }
        }
    }
    
    public static class PNG implements Disposable
    {
        private static final byte[] SIGNATURE;
        private static final int IHDR = 1229472850;
        private static final int IDAT = 1229209940;
        private static final int IEND = 1229278788;
        private static final byte COLOR_ARGB = 6;
        private static final byte COMPRESSION_DEFLATE = 0;
        private static final byte FILTER_NONE = 0;
        private static final byte INTERLACE_NONE = 0;
        private static final byte PAETH = 4;
        private final ChunkBuffer buffer;
        private final Deflater deflater;
        private ByteArray lineOutBytes;
        private ByteArray curLineBytes;
        private ByteArray prevLineBytes;
        private boolean flipY;
        private int lastLineLen;
        
        static {
            SIGNATURE = new byte[] { -119, 80, 78, 71, 13, 10, 26, 10 };
        }
        
        public PNG() {
            this(16384);
        }
        
        public PNG(final int initialBufferSize) {
            this.flipY = true;
            this.buffer = new ChunkBuffer(initialBufferSize);
            this.deflater = new Deflater();
        }
        
        public void setFlipY(final boolean flipY) {
            this.flipY = flipY;
        }
        
        public void setCompression(final int level) {
            this.deflater.setLevel(level);
        }
        
        public void write(final FileHandle file, final Pixmap pixmap) throws IOException {
            final OutputStream output = file.write(false);
            try {
                this.write(output, pixmap);
            }
            finally {
                StreamUtils.closeQuietly(output);
            }
            StreamUtils.closeQuietly(output);
        }
        
        public void write(final OutputStream output, final Pixmap pixmap) throws IOException {
            final DeflaterOutputStream deflaterOutput = new DeflaterOutputStream(this.buffer, this.deflater);
            final DataOutputStream dataOutput = new DataOutputStream(output);
            dataOutput.write(PNG.SIGNATURE);
            this.buffer.writeInt(1229472850);
            this.buffer.writeInt(pixmap.getWidth());
            this.buffer.writeInt(pixmap.getHeight());
            this.buffer.writeByte(8);
            this.buffer.writeByte(6);
            this.buffer.writeByte(0);
            this.buffer.writeByte(0);
            this.buffer.writeByte(0);
            this.buffer.endChunk(dataOutput);
            this.buffer.writeInt(1229209940);
            this.deflater.reset();
            final int lineLen = pixmap.getWidth() * 4;
            byte[] lineOut;
            byte[] curLine;
            byte[] prevLine;
            if (this.lineOutBytes == null) {
                final ByteArray lineOutBytes = new ByteArray(lineLen);
                this.lineOutBytes = lineOutBytes;
                lineOut = lineOutBytes.items;
                final ByteArray curLineBytes = new ByteArray(lineLen);
                this.curLineBytes = curLineBytes;
                curLine = curLineBytes.items;
                final ByteArray prevLineBytes = new ByteArray(lineLen);
                this.prevLineBytes = prevLineBytes;
                prevLine = prevLineBytes.items;
            }
            else {
                lineOut = this.lineOutBytes.ensureCapacity(lineLen);
                curLine = this.curLineBytes.ensureCapacity(lineLen);
                prevLine = this.prevLineBytes.ensureCapacity(lineLen);
                for (int i = 0, n = this.lastLineLen; i < n; ++i) {
                    prevLine[i] = 0;
                }
            }
            this.lastLineLen = lineLen;
            final ByteBuffer pixels = pixmap.getPixels();
            final int oldPosition = pixels.position();
            final boolean rgba8888 = pixmap.getFormat() == Pixmap.Format.RGBA8888;
            for (int y = 0, h = pixmap.getHeight(); y < h; ++y) {
                final int py = this.flipY ? (h - y - 1) : y;
                if (rgba8888) {
                    pixels.position(py * lineLen);
                    pixels.get(curLine, 0, lineLen);
                }
                else {
                    int px = 0;
                    int x = 0;
                    while (px < pixmap.getWidth()) {
                        final int pixel = pixmap.getPixel(px, py);
                        curLine[x++] = (byte)(pixel >> 24 & 0xFF);
                        curLine[x++] = (byte)(pixel >> 16 & 0xFF);
                        curLine[x++] = (byte)(pixel >> 8 & 0xFF);
                        curLine[x++] = (byte)(pixel & 0xFF);
                        ++px;
                    }
                }
                lineOut[0] = (byte)(curLine[0] - prevLine[0]);
                lineOut[1] = (byte)(curLine[1] - prevLine[1]);
                lineOut[2] = (byte)(curLine[2] - prevLine[2]);
                lineOut[3] = (byte)(curLine[3] - prevLine[3]);
                for (int x2 = 4; x2 < lineLen; ++x2) {
                    final int a = curLine[x2 - 4] & 0xFF;
                    final int b = prevLine[x2] & 0xFF;
                    int c = prevLine[x2 - 4] & 0xFF;
                    final int p = a + b - c;
                    int pa = p - a;
                    if (pa < 0) {
                        pa = -pa;
                    }
                    int pb = p - b;
                    if (pb < 0) {
                        pb = -pb;
                    }
                    int pc = p - c;
                    if (pc < 0) {
                        pc = -pc;
                    }
                    if (pa <= pb && pa <= pc) {
                        c = a;
                    }
                    else if (pb <= pc) {
                        c = b;
                    }
                    lineOut[x2] = (byte)(curLine[x2] - c);
                }
                deflaterOutput.write(4);
                deflaterOutput.write(lineOut, 0, lineLen);
                final byte[] temp = curLine;
                curLine = prevLine;
                prevLine = temp;
            }
            pixels.position(oldPosition);
            deflaterOutput.finish();
            this.buffer.endChunk(dataOutput);
            this.buffer.writeInt(1229278788);
            this.buffer.endChunk(dataOutput);
            output.flush();
        }
        
        @Override
        public void dispose() {
            this.deflater.end();
        }
        
        static class ChunkBuffer extends DataOutputStream
        {
            final ByteArrayOutputStream buffer;
            final CRC32 crc;
            
            ChunkBuffer(final int initialSize) {
                this(new ByteArrayOutputStream(initialSize), new CRC32());
            }
            
            private ChunkBuffer(final ByteArrayOutputStream buffer, final CRC32 crc) {
                super(new CheckedOutputStream(buffer, crc));
                this.buffer = buffer;
                this.crc = crc;
            }
            
            public void endChunk(final DataOutputStream target) throws IOException {
                this.flush();
                target.writeInt(this.buffer.size() - 4);
                this.buffer.writeTo(target);
                target.writeInt((int)this.crc.getValue());
                this.buffer.reset();
                this.crc.reset();
            }
        }
    }
}
