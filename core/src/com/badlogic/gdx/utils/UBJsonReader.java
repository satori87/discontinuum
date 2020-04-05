// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import java.io.Closeable;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.InputStream;

public class UBJsonReader implements BaseJsonReader
{
    public boolean oldFormat;
    
    public UBJsonReader() {
        this.oldFormat = true;
    }
    
    @Override
    public JsonValue parse(final InputStream input) {
        DataInputStream din = null;
        try {
            din = new DataInputStream(input);
            return this.parse(din);
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
        finally {
            StreamUtils.closeQuietly(din);
        }
    }
    
    @Override
    public JsonValue parse(final FileHandle file) {
        try {
            return this.parse(file.read(8192));
        }
        catch (Exception ex) {
            throw new SerializationException("Error parsing file: " + file, ex);
        }
    }
    
    public JsonValue parse(final DataInputStream din) throws IOException {
        try {
            return this.parse(din, din.readByte());
        }
        finally {
            StreamUtils.closeQuietly(din);
        }
    }
    
    protected JsonValue parse(final DataInputStream din, final byte type) throws IOException {
        if (type == 91) {
            return this.parseArray(din);
        }
        if (type == 123) {
            return this.parseObject(din);
        }
        if (type == 90) {
            return new JsonValue(JsonValue.ValueType.nullValue);
        }
        if (type == 84) {
            return new JsonValue(true);
        }
        if (type == 70) {
            return new JsonValue(false);
        }
        if (type == 66) {
            return new JsonValue(this.readUChar(din));
        }
        if (type == 85) {
            return new JsonValue(this.readUChar(din));
        }
        if (type == 105) {
            return new JsonValue(this.oldFormat ? din.readShort() : ((long)din.readByte()));
        }
        if (type == 73) {
            return new JsonValue(this.oldFormat ? din.readInt() : ((long)din.readShort()));
        }
        if (type == 108) {
            return new JsonValue(din.readInt());
        }
        if (type == 76) {
            return new JsonValue(din.readLong());
        }
        if (type == 100) {
            return new JsonValue(din.readFloat());
        }
        if (type == 68) {
            return new JsonValue(din.readDouble());
        }
        if (type == 115 || type == 83) {
            return new JsonValue(this.parseString(din, type));
        }
        if (type == 97 || type == 65) {
            return this.parseData(din, type);
        }
        if (type == 67) {
            return new JsonValue(din.readChar());
        }
        throw new GdxRuntimeException("Unrecognized data type");
    }
    
    protected JsonValue parseArray(final DataInputStream din) throws IOException {
        final JsonValue result = new JsonValue(JsonValue.ValueType.array);
        byte type = din.readByte();
        byte valueType = 0;
        if (type == 36) {
            valueType = din.readByte();
            type = din.readByte();
        }
        long size = -1L;
        if (type == 35) {
            size = this.parseSize(din, false, -1L);
            if (size < 0L) {
                throw new GdxRuntimeException("Unrecognized data type");
            }
            if (size == 0L) {
                return result;
            }
            type = ((valueType == 0) ? din.readByte() : valueType);
        }
        JsonValue prev = null;
        long c = 0L;
        while (din.available() > 0 && type != 93) {
            final JsonValue val = this.parse(din, type);
            val.parent = result;
            if (prev != null) {
                val.prev = prev;
                prev.next = val;
                final JsonValue jsonValue = result;
                ++jsonValue.size;
            }
            else {
                result.child = val;
                result.size = 1;
            }
            prev = val;
            if (size > 0L && ++c >= size) {
                break;
            }
            type = ((valueType == 0) ? din.readByte() : valueType);
        }
        return result;
    }
    
    protected JsonValue parseObject(final DataInputStream din) throws IOException {
        final JsonValue result = new JsonValue(JsonValue.ValueType.object);
        byte type = din.readByte();
        byte valueType = 0;
        if (type == 36) {
            valueType = din.readByte();
            type = din.readByte();
        }
        long size = -1L;
        if (type == 35) {
            size = this.parseSize(din, false, -1L);
            if (size < 0L) {
                throw new GdxRuntimeException("Unrecognized data type");
            }
            if (size == 0L) {
                return result;
            }
            type = din.readByte();
        }
        JsonValue prev = null;
        long c = 0L;
        while (din.available() > 0 && type != 125) {
            final String key = this.parseString(din, true, type);
            final JsonValue child = this.parse(din, (valueType == 0) ? din.readByte() : valueType);
            child.setName(key);
            child.parent = result;
            if (prev != null) {
                child.prev = prev;
                prev.next = child;
                final JsonValue jsonValue = result;
                ++jsonValue.size;
            }
            else {
                result.child = child;
                result.size = 1;
            }
            prev = child;
            if (size > 0L && ++c >= size) {
                break;
            }
            type = din.readByte();
        }
        return result;
    }
    
    protected JsonValue parseData(final DataInputStream din, final byte blockType) throws IOException {
        final byte dataType = din.readByte();
        final long size = (blockType == 65) ? this.readUInt(din) : this.readUChar(din);
        final JsonValue result = new JsonValue(JsonValue.ValueType.array);
        JsonValue prev = null;
        for (long i = 0L; i < size; ++i) {
            final JsonValue val = this.parse(din, dataType);
            val.parent = result;
            if (prev != null) {
                prev.next = val;
                final JsonValue jsonValue = result;
                ++jsonValue.size;
            }
            else {
                result.child = val;
                result.size = 1;
            }
            prev = val;
        }
        return result;
    }
    
    protected String parseString(final DataInputStream din, final byte type) throws IOException {
        return this.parseString(din, false, type);
    }
    
    protected String parseString(final DataInputStream din, final boolean sOptional, final byte type) throws IOException {
        long size = -1L;
        if (type == 83) {
            size = this.parseSize(din, true, -1L);
        }
        else if (type == 115) {
            size = this.readUChar(din);
        }
        else if (sOptional) {
            size = this.parseSize(din, type, false, -1L);
        }
        if (size < 0L) {
            throw new GdxRuntimeException("Unrecognized data type, string expected");
        }
        return (size > 0L) ? this.readString(din, size) : "";
    }
    
    protected long parseSize(final DataInputStream din, final boolean useIntOnError, final long defaultValue) throws IOException {
        return this.parseSize(din, din.readByte(), useIntOnError, defaultValue);
    }
    
    protected long parseSize(final DataInputStream din, final byte type, final boolean useIntOnError, final long defaultValue) throws IOException {
        if (type == 105) {
            return this.readUChar(din);
        }
        if (type == 73) {
            return this.readUShort(din);
        }
        if (type == 108) {
            return this.readUInt(din);
        }
        if (type == 76) {
            return din.readLong();
        }
        if (useIntOnError) {
            long result = (long)(type & 0xFF) << 24;
            result |= (long)(din.readByte() & 0xFF) << 16;
            result |= (long)(din.readByte() & 0xFF) << 8;
            result |= (din.readByte() & 0xFF);
            return result;
        }
        return defaultValue;
    }
    
    protected short readUChar(final DataInputStream din) throws IOException {
        return (short)(din.readByte() & 0xFF);
    }
    
    protected int readUShort(final DataInputStream din) throws IOException {
        return din.readShort() & 0xFFFF;
    }
    
    protected long readUInt(final DataInputStream din) throws IOException {
        return (long)din.readInt() & -1L;
    }
    
    protected String readString(final DataInputStream din, final long size) throws IOException {
        final byte[] data = new byte[(int)size];
        din.readFully(data);
        return new String(data, "UTF-8");
    }
}
