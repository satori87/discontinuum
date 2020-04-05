// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

public class DataInput extends DataInputStream
{
    private char[] chars;
    
    public DataInput(final InputStream in) {
        super(in);
        this.chars = new char[32];
    }
    
    public int readInt(final boolean optimizePositive) throws IOException {
        int b = this.read();
        int result = b & 0x7F;
        if ((b & 0x80) != 0x0) {
            b = this.read();
            result |= (b & 0x7F) << 7;
            if ((b & 0x80) != 0x0) {
                b = this.read();
                result |= (b & 0x7F) << 14;
                if ((b & 0x80) != 0x0) {
                    b = this.read();
                    result |= (b & 0x7F) << 21;
                    if ((b & 0x80) != 0x0) {
                        b = this.read();
                        result |= (b & 0x7F) << 28;
                    }
                }
            }
        }
        return optimizePositive ? result : (result >>> 1 ^ -(result & 0x1));
    }
    
    public String readString() throws IOException {
        int charCount = this.readInt(true);
        switch (charCount) {
            case 0: {
                return null;
            }
            case 1: {
                return "";
            }
            default: {
                --charCount;
                if (this.chars.length < charCount) {
                    this.chars = new char[charCount];
                }
                char[] chars;
                int charIndex;
                int b;
                for (chars = this.chars, charIndex = 0, b = 0; charIndex < charCount; chars[charIndex++] = (char)b) {
                    b = this.read();
                    if (b > 127) {
                        break;
                    }
                }
                if (charIndex < charCount) {
                    this.readUtf8_slow(charCount, charIndex, b);
                }
                return new String(chars, 0, charCount);
            }
        }
    }
    
    private void readUtf8_slow(final int charCount, int charIndex, int b) throws IOException {
        final char[] chars = this.chars;
        while (true) {
            switch (b >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7: {
                    chars[charIndex] = (char)b;
                    break;
                }
                case 12:
                case 13: {
                    chars[charIndex] = (char)((b & 0x1F) << 6 | (this.read() & 0x3F));
                    break;
                }
                case 14: {
                    chars[charIndex] = (char)((b & 0xF) << 12 | (this.read() & 0x3F) << 6 | (this.read() & 0x3F));
                    break;
                }
            }
            if (++charIndex >= charCount) {
                break;
            }
            b = (this.read() & 0xFF);
        }
    }
}
