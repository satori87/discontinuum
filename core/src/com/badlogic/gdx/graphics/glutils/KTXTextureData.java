// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import java.nio.IntBuffer;
import java.nio.Buffer;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.Gdx;
import java.nio.ByteOrder;
import java.io.Closeable;
import com.badlogic.gdx.utils.StreamUtils;
import com.badlogic.gdx.utils.BufferUtils;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.util.zip.GZIPInputStream;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.ByteBuffer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.CubemapData;
import com.badlogic.gdx.graphics.TextureData;

public class KTXTextureData implements TextureData, CubemapData
{
    private FileHandle file;
    private int glType;
    private int glTypeSize;
    private int glFormat;
    private int glInternalFormat;
    private int glBaseInternalFormat;
    private int pixelWidth;
    private int pixelHeight;
    private int pixelDepth;
    private int numberOfArrayElements;
    private int numberOfFaces;
    private int numberOfMipmapLevels;
    private int imagePos;
    private ByteBuffer compressedData;
    private boolean useMipMaps;
    private static final int GL_TEXTURE_1D = 4660;
    private static final int GL_TEXTURE_3D = 4660;
    private static final int GL_TEXTURE_1D_ARRAY_EXT = 4660;
    private static final int GL_TEXTURE_2D_ARRAY_EXT = 4660;
    
    public KTXTextureData(final FileHandle file, final boolean genMipMaps) {
        this.pixelWidth = -1;
        this.pixelHeight = -1;
        this.pixelDepth = -1;
        this.file = file;
        this.useMipMaps = genMipMaps;
    }
    
    @Override
    public TextureDataType getType() {
        return TextureDataType.Custom;
    }
    
    @Override
    public boolean isPrepared() {
        return this.compressedData != null;
    }
    
    @Override
    public void prepare() {
        if (this.compressedData != null) {
            throw new GdxRuntimeException("Already prepared");
        }
        if (this.file == null) {
            throw new GdxRuntimeException("Need a file to load from");
        }
        if (this.file.name().endsWith(".zktx")) {
            final byte[] buffer = new byte[10240];
            DataInputStream in = null;
            try {
                in = new DataInputStream(new BufferedInputStream(new GZIPInputStream(this.file.read())));
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
                throw new GdxRuntimeException("Couldn't load zktx file '" + this.file + "'", e);
            }
            finally {
                StreamUtils.closeQuietly(in);
            }
            StreamUtils.closeQuietly(in);
        }
        else {
            this.compressedData = ByteBuffer.wrap(this.file.readBytes());
        }
        if (this.compressedData.get() != -85) {
            throw new GdxRuntimeException("Invalid KTX Header");
        }
        if (this.compressedData.get() != 75) {
            throw new GdxRuntimeException("Invalid KTX Header");
        }
        if (this.compressedData.get() != 84) {
            throw new GdxRuntimeException("Invalid KTX Header");
        }
        if (this.compressedData.get() != 88) {
            throw new GdxRuntimeException("Invalid KTX Header");
        }
        if (this.compressedData.get() != 32) {
            throw new GdxRuntimeException("Invalid KTX Header");
        }
        if (this.compressedData.get() != 49) {
            throw new GdxRuntimeException("Invalid KTX Header");
        }
        if (this.compressedData.get() != 49) {
            throw new GdxRuntimeException("Invalid KTX Header");
        }
        if (this.compressedData.get() != -69) {
            throw new GdxRuntimeException("Invalid KTX Header");
        }
        if (this.compressedData.get() != 13) {
            throw new GdxRuntimeException("Invalid KTX Header");
        }
        if (this.compressedData.get() != 10) {
            throw new GdxRuntimeException("Invalid KTX Header");
        }
        if (this.compressedData.get() != 26) {
            throw new GdxRuntimeException("Invalid KTX Header");
        }
        if (this.compressedData.get() != 10) {
            throw new GdxRuntimeException("Invalid KTX Header");
        }
        final int endianTag = this.compressedData.getInt();
        if (endianTag != 67305985 && endianTag != 16909060) {
            throw new GdxRuntimeException("Invalid KTX Header");
        }
        if (endianTag != 67305985) {
            this.compressedData.order((this.compressedData.order() == ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
        }
        this.glType = this.compressedData.getInt();
        this.glTypeSize = this.compressedData.getInt();
        this.glFormat = this.compressedData.getInt();
        this.glInternalFormat = this.compressedData.getInt();
        this.glBaseInternalFormat = this.compressedData.getInt();
        this.pixelWidth = this.compressedData.getInt();
        this.pixelHeight = this.compressedData.getInt();
        this.pixelDepth = this.compressedData.getInt();
        this.numberOfArrayElements = this.compressedData.getInt();
        this.numberOfFaces = this.compressedData.getInt();
        this.numberOfMipmapLevels = this.compressedData.getInt();
        if (this.numberOfMipmapLevels == 0) {
            this.numberOfMipmapLevels = 1;
            this.useMipMaps = true;
        }
        final int bytesOfKeyValueData = this.compressedData.getInt();
        this.imagePos = this.compressedData.position() + bytesOfKeyValueData;
        if (!this.compressedData.isDirect()) {
            int pos = this.imagePos;
            for (int level = 0; level < this.numberOfMipmapLevels; ++level) {
                final int faceLodSize = this.compressedData.getInt(pos);
                final int faceLodSizeRounded = faceLodSize + 3 & 0xFFFFFFFC;
                pos += faceLodSizeRounded * this.numberOfFaces + 4;
            }
            this.compressedData.limit(pos);
            this.compressedData.position(0);
            final ByteBuffer directBuffer = BufferUtils.newUnsafeByteBuffer(pos);
            directBuffer.order(this.compressedData.order());
            directBuffer.put(this.compressedData);
            this.compressedData = directBuffer;
        }
    }
    
    @Override
    public void consumeCubemapData() {
        this.consumeCustomData(34067);
    }
    
    @Override
    public void consumeCustomData(int target) {
        if (this.compressedData == null) {
            throw new GdxRuntimeException("Call prepare() before calling consumeCompressedData()");
        }
        final IntBuffer buffer = BufferUtils.newIntBuffer(16);
        boolean compressed = false;
        if (this.glType == 0 || this.glFormat == 0) {
            if (this.glType + this.glFormat != 0) {
                throw new GdxRuntimeException("either both or none of glType, glFormat must be zero");
            }
            compressed = true;
        }
        int textureDimensions = 1;
        int glTarget = 4660;
        if (this.pixelHeight > 0) {
            textureDimensions = 2;
            glTarget = 3553;
        }
        if (this.pixelDepth > 0) {
            textureDimensions = 3;
            glTarget = 4660;
        }
        if (this.numberOfFaces == 6) {
            if (textureDimensions != 2) {
                throw new GdxRuntimeException("cube map needs 2D faces");
            }
            glTarget = 34067;
        }
        else if (this.numberOfFaces != 1) {
            throw new GdxRuntimeException("numberOfFaces must be either 1 or 6");
        }
        if (this.numberOfArrayElements > 0) {
            if (glTarget == 4660) {
                glTarget = 4660;
            }
            else {
                if (glTarget != 3553) {
                    throw new GdxRuntimeException("No API for 3D and cube arrays yet");
                }
                glTarget = 4660;
            }
            ++textureDimensions;
        }
        if (glTarget == 4660) {
            throw new GdxRuntimeException("Unsupported texture format (only 2D texture are supported in LibGdx for the time being)");
        }
        int singleFace = -1;
        if (this.numberOfFaces == 6 && target != 34067) {
            if (34069 > target || target > 34074) {
                throw new GdxRuntimeException("You must specify either GL_TEXTURE_CUBE_MAP to bind all 6 faces of the cube or the requested face GL_TEXTURE_CUBE_MAP_POSITIVE_X and followings.");
            }
            singleFace = target - 34069;
            target = 34069;
        }
        else if (this.numberOfFaces == 6 && target == 34067) {
            target = 34069;
        }
        else if (target != glTarget && (34069 > target || target > 34074 || target != 3553)) {
            throw new GdxRuntimeException("Invalid target requested : 0x" + Integer.toHexString(target) + ", expecting : 0x" + Integer.toHexString(glTarget));
        }
        Gdx.gl.glGetIntegerv(3317, buffer);
        final int previousUnpackAlignment = buffer.get(0);
        if (previousUnpackAlignment != 4) {
            Gdx.gl.glPixelStorei(3317, 4);
        }
        final int glInternalFormat = this.glInternalFormat;
        final int glFormat = this.glFormat;
        int pos = this.imagePos;
        for (int level = 0; level < this.numberOfMipmapLevels; ++level) {
            final int pixelWidth = Math.max(1, this.pixelWidth >> level);
            int pixelHeight = Math.max(1, this.pixelHeight >> level);
            int pixelDepth = Math.max(1, this.pixelDepth >> level);
            this.compressedData.position(pos);
            final int faceLodSize = this.compressedData.getInt();
            final int faceLodSizeRounded = faceLodSize + 3 & 0xFFFFFFFC;
            pos += 4;
            for (int face = 0; face < this.numberOfFaces; ++face) {
                this.compressedData.position(pos);
                pos += faceLodSizeRounded;
                if (singleFace == -1 || singleFace == face) {
                    final ByteBuffer data = this.compressedData.slice();
                    data.limit(faceLodSizeRounded);
                    if (textureDimensions != 1) {
                        if (textureDimensions == 2) {
                            if (this.numberOfArrayElements > 0) {
                                pixelHeight = this.numberOfArrayElements;
                            }
                            if (compressed) {
                                if (glInternalFormat == ETC1.ETC1_RGB8_OES) {
                                    if (!Gdx.graphics.supportsExtension("GL_OES_compressed_ETC1_RGB8_texture")) {
                                        final ETC1.ETC1Data etcData = new ETC1.ETC1Data(pixelWidth, pixelHeight, data, 0);
                                        final Pixmap pixmap = ETC1.decodeImage(etcData, Pixmap.Format.RGB888);
                                        Gdx.gl.glTexImage2D(target + face, level, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
                                        pixmap.dispose();
                                    }
                                    else {
                                        Gdx.gl.glCompressedTexImage2D(target + face, level, glInternalFormat, pixelWidth, pixelHeight, 0, faceLodSize, data);
                                    }
                                }
                                else {
                                    Gdx.gl.glCompressedTexImage2D(target + face, level, glInternalFormat, pixelWidth, pixelHeight, 0, faceLodSize, data);
                                }
                            }
                            else {
                                Gdx.gl.glTexImage2D(target + face, level, glInternalFormat, pixelWidth, pixelHeight, 0, glFormat, this.glType, data);
                            }
                        }
                        else if (textureDimensions == 3 && this.numberOfArrayElements > 0) {
                            pixelDepth = this.numberOfArrayElements;
                        }
                    }
                }
            }
        }
        if (previousUnpackAlignment != 4) {
            Gdx.gl.glPixelStorei(3317, previousUnpackAlignment);
        }
        if (this.useMipMaps()) {
            Gdx.gl.glGenerateMipmap(target);
        }
        this.disposePreparedData();
    }
    
    public void disposePreparedData() {
        if (this.compressedData != null) {
            BufferUtils.disposeUnsafeByteBuffer(this.compressedData);
        }
        this.compressedData = null;
    }
    
    @Override
    public Pixmap consumePixmap() {
        throw new GdxRuntimeException("This TextureData implementation does not return a Pixmap");
    }
    
    @Override
    public boolean disposePixmap() {
        throw new GdxRuntimeException("This TextureData implementation does not return a Pixmap");
    }
    
    @Override
    public int getWidth() {
        return this.pixelWidth;
    }
    
    @Override
    public int getHeight() {
        return this.pixelHeight;
    }
    
    public int getNumberOfMipMapLevels() {
        return this.numberOfMipmapLevels;
    }
    
    public int getNumberOfFaces() {
        return this.numberOfFaces;
    }
    
    public int getGlInternalFormat() {
        return this.glInternalFormat;
    }
    
    public ByteBuffer getData(final int requestedLevel, final int requestedFace) {
        int pos = this.imagePos;
        for (int level = 0; level < this.numberOfMipmapLevels; ++level) {
            final int faceLodSize = this.compressedData.getInt(pos);
            final int faceLodSizeRounded = faceLodSize + 3 & 0xFFFFFFFC;
            pos += 4;
            if (level == requestedLevel) {
                for (int face = 0; face < this.numberOfFaces; ++face) {
                    if (face == requestedFace) {
                        this.compressedData.position(pos);
                        final ByteBuffer data = this.compressedData.slice();
                        data.limit(faceLodSizeRounded);
                        return data;
                    }
                    pos += faceLodSizeRounded;
                }
            }
            else {
                pos += faceLodSizeRounded * this.numberOfFaces;
            }
        }
        return null;
    }
    
    @Override
    public Pixmap.Format getFormat() {
        throw new GdxRuntimeException("This TextureData implementation directly handles texture formats.");
    }
    
    @Override
    public boolean useMipMaps() {
        return this.useMipMaps;
    }
    
    @Override
    public boolean isManaged() {
        return true;
    }
}
