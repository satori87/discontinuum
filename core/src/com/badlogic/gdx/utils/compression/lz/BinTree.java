// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.compression.lz;

import java.io.IOException;

public class BinTree extends InWindow
{
    int _cyclicBufferPos;
    int _cyclicBufferSize;
    int _matchMaxLen;
    int[] _son;
    int[] _hash;
    int _cutValue;
    int _hashMask;
    int _hashSizeSum;
    boolean HASH_ARRAY;
    static final int kHash2Size = 1024;
    static final int kHash3Size = 65536;
    static final int kBT2HashSize = 65536;
    static final int kStartMaxLen = 1;
    static final int kHash3Offset = 1024;
    static final int kEmptyHashValue = 0;
    static final int kMaxValForNormalize = 1073741823;
    int kNumHashDirectBytes;
    int kMinMatchCheck;
    int kFixHashSize;
    private static final int[] CrcTable;
    
    static {
        CrcTable = new int[256];
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
            BinTree.CrcTable[i] = r;
        }
    }
    
    public BinTree() {
        this._cyclicBufferSize = 0;
        this._cutValue = 255;
        this._hashSizeSum = 0;
        this.HASH_ARRAY = true;
        this.kNumHashDirectBytes = 0;
        this.kMinMatchCheck = 4;
        this.kFixHashSize = 66560;
    }
    
    public void SetType(final int numHashBytes) {
        this.HASH_ARRAY = (numHashBytes > 2);
        if (this.HASH_ARRAY) {
            this.kNumHashDirectBytes = 0;
            this.kMinMatchCheck = 4;
            this.kFixHashSize = 66560;
        }
        else {
            this.kNumHashDirectBytes = 2;
            this.kMinMatchCheck = 3;
            this.kFixHashSize = 0;
        }
    }
    
    @Override
    public void Init() throws IOException {
        super.Init();
        for (int i = 0; i < this._hashSizeSum; ++i) {
            this._hash[i] = 0;
        }
        this._cyclicBufferPos = 0;
        this.ReduceOffsets(-1);
    }
    
    @Override
    public void MovePos() throws IOException {
        if (++this._cyclicBufferPos >= this._cyclicBufferSize) {
            this._cyclicBufferPos = 0;
        }
        super.MovePos();
        if (this._pos == 1073741823) {
            this.Normalize();
        }
    }
    
    public boolean Create(final int historySize, final int keepAddBufferBefore, final int matchMaxLen, final int keepAddBufferAfter) {
        if (historySize > 1073741567) {
            return false;
        }
        this._cutValue = 16 + (matchMaxLen >> 1);
        final int windowReservSize = (historySize + keepAddBufferBefore + matchMaxLen + keepAddBufferAfter) / 2 + 256;
        super.Create(historySize + keepAddBufferBefore, matchMaxLen + keepAddBufferAfter, windowReservSize);
        this._matchMaxLen = matchMaxLen;
        final int cyclicBufferSize = historySize + 1;
        if (this._cyclicBufferSize != cyclicBufferSize) {
            final int cyclicBufferSize2 = cyclicBufferSize;
            this._cyclicBufferSize = cyclicBufferSize2;
            this._son = new int[cyclicBufferSize2 * 2];
        }
        int hs = 65536;
        if (this.HASH_ARRAY) {
            hs = historySize - 1;
            hs |= hs >> 1;
            hs |= hs >> 2;
            hs |= hs >> 4;
            hs |= hs >> 8;
            hs >>= 1;
            hs |= 0xFFFF;
            if (hs > 16777216) {
                hs >>= 1;
            }
            this._hashMask = hs;
            hs = ++hs + this.kFixHashSize;
        }
        if (hs != this._hashSizeSum) {
            final int hashSizeSum = hs;
            this._hashSizeSum = hashSizeSum;
            this._hash = new int[hashSizeSum];
        }
        return true;
    }
    
    public int GetMatches(final int[] distances) throws IOException {
        int lenLimit;
        if (this._pos + this._matchMaxLen <= this._streamPos) {
            lenLimit = this._matchMaxLen;
        }
        else {
            lenLimit = this._streamPos - this._pos;
            if (lenLimit < this.kMinMatchCheck) {
                this.MovePos();
                return 0;
            }
        }
        int offset = 0;
        final int matchMinPos = (this._pos > this._cyclicBufferSize) ? (this._pos - this._cyclicBufferSize) : 0;
        final int cur = this._bufferOffset + this._pos;
        int maxLen = 1;
        int hash2Value = 0;
        int hash3Value = 0;
        int hashValue;
        if (this.HASH_ARRAY) {
            int temp = BinTree.CrcTable[this._bufferBase[cur] & 0xFF] ^ (this._bufferBase[cur + 1] & 0xFF);
            hash2Value = (temp & 0x3FF);
            temp ^= (this._bufferBase[cur + 2] & 0xFF) << 8;
            hash3Value = (temp & 0xFFFF);
            hashValue = ((temp ^ BinTree.CrcTable[this._bufferBase[cur + 3] & 0xFF] << 5) & this._hashMask);
        }
        else {
            hashValue = ((this._bufferBase[cur] & 0xFF) ^ (this._bufferBase[cur + 1] & 0xFF) << 8);
        }
        int curMatch = this._hash[this.kFixHashSize + hashValue];
        if (this.HASH_ARRAY) {
            int curMatch2 = this._hash[hash2Value];
            final int curMatch3 = this._hash[1024 + hash3Value];
            this._hash[hash2Value] = this._pos;
            this._hash[1024 + hash3Value] = this._pos;
            if (curMatch2 > matchMinPos && this._bufferBase[this._bufferOffset + curMatch2] == this._bufferBase[cur]) {
                maxLen = (distances[offset++] = 2);
                distances[offset++] = this._pos - curMatch2 - 1;
            }
            if (curMatch3 > matchMinPos && this._bufferBase[this._bufferOffset + curMatch3] == this._bufferBase[cur]) {
                if (curMatch3 == curMatch2) {
                    offset -= 2;
                }
                maxLen = (distances[offset++] = 3);
                distances[offset++] = this._pos - curMatch3 - 1;
                curMatch2 = curMatch3;
            }
            if (offset != 0 && curMatch2 == curMatch) {
                offset -= 2;
                maxLen = 1;
            }
        }
        this._hash[this.kFixHashSize + hashValue] = this._pos;
        int ptr0 = (this._cyclicBufferPos << 1) + 1;
        int ptr2 = this._cyclicBufferPos << 1;
        int len2;
        int len1 = len2 = this.kNumHashDirectBytes;
        if (this.kNumHashDirectBytes != 0 && curMatch > matchMinPos && this._bufferBase[this._bufferOffset + curMatch + this.kNumHashDirectBytes] != this._bufferBase[cur + this.kNumHashDirectBytes]) {
            maxLen = (distances[offset++] = this.kNumHashDirectBytes);
            distances[offset++] = this._pos - curMatch - 1;
        }
        int count = this._cutValue;
        while (true) {
            while (curMatch > matchMinPos && count-- != 0) {
                final int delta = this._pos - curMatch;
                final int cyclicPos = ((delta <= this._cyclicBufferPos) ? (this._cyclicBufferPos - delta) : (this._cyclicBufferPos - delta + this._cyclicBufferSize)) << 1;
                final int pby1 = this._bufferOffset + curMatch;
                int len3 = Math.min(len2, len1);
                if (this._bufferBase[pby1 + len3] == this._bufferBase[cur + len3]) {
                    while (++len3 != lenLimit && this._bufferBase[pby1 + len3] == this._bufferBase[cur + len3]) {}
                    if (maxLen < len3) {
                        maxLen = (distances[offset++] = len3);
                        distances[offset++] = delta - 1;
                        if (len3 == lenLimit) {
                            this._son[ptr2] = this._son[cyclicPos];
                            this._son[ptr0] = this._son[cyclicPos + 1];
                            this.MovePos();
                            return offset;
                        }
                    }
                }
                if ((this._bufferBase[pby1 + len3] & 0xFF) < (this._bufferBase[cur + len3] & 0xFF)) {
                    this._son[ptr2] = curMatch;
                    ptr2 = cyclicPos + 1;
                    curMatch = this._son[ptr2];
                    len1 = len3;
                }
                else {
                    this._son[ptr0] = curMatch;
                    ptr0 = cyclicPos;
                    curMatch = this._son[ptr0];
                    len2 = len3;
                }
            }
            this._son[ptr0] = (this._son[ptr2] = 0);
            continue;
        }
    }
    
    public void Skip(int num) throws IOException {
    Label_0596_Outer:
        do {
            int lenLimit;
            if (this._pos + this._matchMaxLen <= this._streamPos) {
                lenLimit = this._matchMaxLen;
            }
            else {
                lenLimit = this._streamPos - this._pos;
                if (lenLimit < this.kMinMatchCheck) {
                    this.MovePos();
                    continue Label_0596_Outer;
                }
            }
            final int matchMinPos = (this._pos > this._cyclicBufferSize) ? (this._pos - this._cyclicBufferSize) : 0;
            final int cur = this._bufferOffset + this._pos;
            int hashValue;
            if (this.HASH_ARRAY) {
                int temp = BinTree.CrcTable[this._bufferBase[cur] & 0xFF] ^ (this._bufferBase[cur + 1] & 0xFF);
                final int hash2Value = temp & 0x3FF;
                this._hash[hash2Value] = this._pos;
                temp ^= (this._bufferBase[cur + 2] & 0xFF) << 8;
                final int hash3Value = temp & 0xFFFF;
                this._hash[1024 + hash3Value] = this._pos;
                hashValue = ((temp ^ BinTree.CrcTable[this._bufferBase[cur + 3] & 0xFF] << 5) & this._hashMask);
            }
            else {
                hashValue = ((this._bufferBase[cur] & 0xFF) ^ (this._bufferBase[cur + 1] & 0xFF) << 8);
            }
            int curMatch = this._hash[this.kFixHashSize + hashValue];
            this._hash[this.kFixHashSize + hashValue] = this._pos;
            int ptr0 = (this._cyclicBufferPos << 1) + 1;
            int ptr2 = this._cyclicBufferPos << 1;
            int len2;
            int len1 = len2 = this.kNumHashDirectBytes;
            int count = this._cutValue;
            while (true) {
                while (curMatch > matchMinPos && count-- != 0) {
                    final int delta = this._pos - curMatch;
                    final int cyclicPos = ((delta <= this._cyclicBufferPos) ? (this._cyclicBufferPos - delta) : (this._cyclicBufferPos - delta + this._cyclicBufferSize)) << 1;
                    final int pby1 = this._bufferOffset + curMatch;
                    int len3 = Math.min(len2, len1);
                    if (this._bufferBase[pby1 + len3] == this._bufferBase[cur + len3]) {
                        while (++len3 != lenLimit && this._bufferBase[pby1 + len3] == this._bufferBase[cur + len3]) {}
                        if (len3 == lenLimit) {
                            this._son[ptr2] = this._son[cyclicPos];
                            this._son[ptr0] = this._son[cyclicPos + 1];
                            this.MovePos();
                            continue Label_0596_Outer;
                        }
                    }
                    if ((this._bufferBase[pby1 + len3] & 0xFF) < (this._bufferBase[cur + len3] & 0xFF)) {
                        this._son[ptr2] = curMatch;
                        ptr2 = cyclicPos + 1;
                        curMatch = this._son[ptr2];
                        len1 = len3;
                    }
                    else {
                        this._son[ptr0] = curMatch;
                        ptr0 = cyclicPos;
                        curMatch = this._son[ptr0];
                        len2 = len3;
                    }
                }
                this._son[ptr0] = (this._son[ptr2] = 0);
                continue;
            }
        } while (--num != 0);
    }
    
    void NormalizeLinks(final int[] items, final int numItems, final int subValue) {
        for (int i = 0; i < numItems; ++i) {
            int value = items[i];
            if (value <= subValue) {
                value = 0;
            }
            else {
                value -= subValue;
            }
            items[i] = value;
        }
    }
    
    void Normalize() {
        final int subValue = this._pos - this._cyclicBufferSize;
        this.NormalizeLinks(this._son, this._cyclicBufferSize * 2, subValue);
        this.NormalizeLinks(this._hash, this._hashSizeSum, subValue);
        this.ReduceOffsets(subValue);
    }
    
    public void SetCutValue(final int cutValue) {
        this._cutValue = cutValue;
    }
}
