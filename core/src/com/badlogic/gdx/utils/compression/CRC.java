// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.compression;

public class CRC
{
    public static int[] Table;
    int _value;
    
    static {
        CRC.Table = new int[256];
        for (int i = 0; i < 256; ++i) {
            int r = i;
            for (int j = 0; j < 8; ++j) {
                if ((r & 0x1) != 0x0) {
                    r = (r >>> 1 ^ 0xEDB88320);
                }
                else {
                    r >>>= 1;
                }
            }
            CRC.Table[i] = r;
        }
    }
    
    public CRC() {
        this._value = -1;
    }
    
    public void Init() {
        this._value = -1;
    }
    
    public void Update(final byte[] data, final int offset, final int size) {
        for (int i = 0; i < size; ++i) {
            this._value = (CRC.Table[(this._value ^ data[offset + i]) & 0xFF] ^ this._value >>> 8);
        }
    }
    
    public void Update(final byte[] data) {
        for (int size = data.length, i = 0; i < size; ++i) {
            this._value = (CRC.Table[(this._value ^ data[i]) & 0xFF] ^ this._value >>> 8);
        }
    }
    
    public void UpdateByte(final int b) {
        this._value = (CRC.Table[(this._value ^ b) & 0xFF] ^ this._value >>> 8);
    }
    
    public int GetDigest() {
        return ~this._value;
    }
}
