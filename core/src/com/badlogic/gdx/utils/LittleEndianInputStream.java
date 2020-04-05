// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.DataInput;
import java.io.FilterInputStream;

public class LittleEndianInputStream extends FilterInputStream implements DataInput
{
    private DataInputStream din;
    
    public LittleEndianInputStream(final InputStream in) {
        super(in);
        this.din = new DataInputStream(in);
    }
    
    @Override
    public void readFully(final byte[] b) throws IOException {
        this.din.readFully(b);
    }
    
    @Override
    public void readFully(final byte[] b, final int off, final int len) throws IOException {
        this.din.readFully(b, off, len);
    }
    
    @Override
    public int skipBytes(final int n) throws IOException {
        return this.din.skipBytes(n);
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        return this.din.readBoolean();
    }
    
    @Override
    public byte readByte() throws IOException {
        return this.din.readByte();
    }
    
    @Override
    public int readUnsignedByte() throws IOException {
        return this.din.readUnsignedByte();
    }
    
    @Override
    public short readShort() throws IOException {
        final int low = this.din.read();
        final int high = this.din.read();
        return (short)(high << 8 | (low & 0xFF));
    }
    
    @Override
    public int readUnsignedShort() throws IOException {
        final int low = this.din.read();
        final int high = this.din.read();
        return (high & 0xFF) << 8 | (low & 0xFF);
    }
    
    @Override
    public char readChar() throws IOException {
        return this.din.readChar();
    }
    
    @Override
    public int readInt() throws IOException {
        final int[] res = new int[4];
        for (int i = 3; i >= 0; --i) {
            res[i] = this.din.read();
        }
        return (res[0] & 0xFF) << 24 | (res[1] & 0xFF) << 16 | (res[2] & 0xFF) << 8 | (res[3] & 0xFF);
    }
    
    @Override
    public long readLong() throws IOException {
        final int[] res = new int[8];
        for (int i = 7; i >= 0; --i) {
            res[i] = this.din.read();
        }
        return (long)(res[0] & 0xFF) << 56 | (long)(res[1] & 0xFF) << 48 | (long)(res[2] & 0xFF) << 40 | (long)(res[3] & 0xFF) << 32 | (long)(res[4] & 0xFF) << 24 | (long)(res[5] & 0xFF) << 16 | (long)(res[6] & 0xFF) << 8 | (long)(res[7] & 0xFF);
    }
    
    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }
    
    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }
    
    @Override
    public final String readLine() throws IOException {
        return this.din.readLine();
    }
    
    @Override
    public String readUTF() throws IOException {
        return this.din.readUTF();
    }
}
