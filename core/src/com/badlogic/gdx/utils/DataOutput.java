// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;

public class DataOutput extends DataOutputStream
{
    public DataOutput(final OutputStream out) {
        super(out);
    }
    
    public int writeInt(int value, final boolean optimizePositive) throws IOException {
        if (!optimizePositive) {
            value = (value << 1 ^ value >> 31);
        }
        if (value >>> 7 == 0) {
            this.write((byte)value);
            return 1;
        }
        this.write((byte)((value & 0x7F) | 0x80));
        if (value >>> 14 == 0) {
            this.write((byte)(value >>> 7));
            return 2;
        }
        this.write((byte)(value >>> 7 | 0x80));
        if (value >>> 21 == 0) {
            this.write((byte)(value >>> 14));
            return 3;
        }
        this.write((byte)(value >>> 14 | 0x80));
        if (value >>> 28 == 0) {
            this.write((byte)(value >>> 21));
            return 4;
        }
        this.write((byte)(value >>> 21 | 0x80));
        this.write((byte)(value >>> 28));
        return 5;
    }
    
    public void writeString(final String value) throws IOException {
        if (value == null) {
            this.write(0);
            return;
        }
        final int charCount = value.length();
        if (charCount == 0) {
            this.writeByte(1);
            return;
        }
        this.writeInt(charCount + 1, true);
        int charIndex;
        for (charIndex = 0; charIndex < charCount; ++charIndex) {
            final int c = value.charAt(charIndex);
            if (c > 127) {
                break;
            }
            this.write((byte)c);
        }
        if (charIndex < charCount) {
            this.writeString_slow(value, charCount, charIndex);
        }
    }
    
    private void writeString_slow(final String value, final int charCount, int charIndex) throws IOException {
        while (charIndex < charCount) {
            final int c = value.charAt(charIndex);
            if (c <= 127) {
                this.write((byte)c);
            }
            else if (c > 2047) {
                this.write((byte)(0xE0 | (c >> 12 & 0xF)));
                this.write((byte)(0x80 | (c >> 6 & 0x3F)));
                this.write((byte)(0x80 | (c & 0x3F)));
            }
            else {
                this.write((byte)(0xC0 | (c >> 6 & 0x1F)));
                this.write((byte)(0x80 | (c & 0x3F)));
            }
            ++charIndex;
        }
    }
}
